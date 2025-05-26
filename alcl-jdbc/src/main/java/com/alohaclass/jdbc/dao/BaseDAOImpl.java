package com.alohaclass.jdbc.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alohaclass.jdbc.annotation.Column;
import com.alohaclass.jdbc.annotation.Pk;
import com.alohaclass.jdbc.annotation.Table;
import com.alohaclass.jdbc.config.Config;
import com.alohaclass.jdbc.dto.Page;
import com.alohaclass.jdbc.dto.PageInfo;
import com.alohaclass.jdbc.utils.StringUtil;

public abstract class BaseDAOImpl<T> extends JDBConnection implements BaseDAO<T> {

	public String table() {
		// 제네릭 타입 T의 실제 클래스 얻기
		Class<T> clazz = getGenericType();

		// 어노테이션에서 테이블 이름 추출
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = clazz.getAnnotation(Table.class);
			return table.value();
		}

		throw new IllegalStateException("클래스 " + clazz.getSimpleName() + "에 @Table 어노테이션이 없습니다.");
	}

	public String pk() {
		Class<T> clazz = getGenericType();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Pk.class)) {
				String fieldName = field.getName();
				if (Config.mapCamelCaseToUnderscore) {
					fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
				}
				return fieldName;
			}
		}
		throw new IllegalStateException("클래스 " + clazz.getSimpleName() + "에 @Pk 필드가 없습니다.");
	}

	@SuppressWarnings("unchecked")
	private Class<T> getGenericType() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public T map(ResultSet rs) throws Exception {
		ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
		Class<T> clazz = (Class<T>) superclass.getActualTypeArguments()[0];
		T entity = clazz.getDeclaredConstructor().newInstance();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			// @Column(exist = false)이면 skip
			Column annotation = field.getAnnotation(Column.class);
			if (annotation != null && !annotation.exist()) {
				continue;
			}
			String fieldName = field.getName();
			if (Config.mapCamelCaseToUnderscore) {
				fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
			}
			if (field.getType().equals(String.class)) {
				field.set(entity, rs.getString(fieldName));
			} else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
				field.set(entity, rs.getBoolean(fieldName));
			} else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
				field.set(entity, rs.getLong(fieldName));
			} else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
				field.set(entity, rs.getInt(fieldName));
			} else if (field.getType().equals(Date.class)) {
				field.set(entity, rs.getTimestamp(fieldName));
			} else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
				field.set(entity, rs.getDouble(fieldName));
			} else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
				field.set(entity, rs.getFloat(fieldName));
			}
		}
		return entity;
	}

	@Override
	public List<T> list() throws Exception {
		String sql = " SELECT * FROM " + table();
		List<T> list = new ArrayList<T>();
		try {
			stmt = con.createStatement();
			log(sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
		} catch (Exception e) {
			System.err.println(table() + " - list() 조회 중 에러");
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<T> listBy(Map<String, Object> fields) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM " + table() + " WHERE ");
		boolean first = true;

		for (Map.Entry<String, Object> entry : fields.entrySet()) {
			String fieldName = entry.getKey();
			if (Config.mapCamelCaseToUnderscore) {
				fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
			}
			if (!first) {
				sql.append(" AND ");
			}
			sql.append(fieldName).append(" = ?");
			first = false;
		}

		List<T> list = new ArrayList<>();
		try {
			psmt = con.prepareStatement(sql.toString());

			int index = 1;
			StringBuilder paramLog = new StringBuilder("param (?) : ");
			for (Object value : fields.values()) {
				paramLog.append("(").append(index).append(")").append(value).append(" ");
				setPreparedStatementValue(psmt, index++, value);
			}

			log(sql, paramLog);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			return list;
		} catch (Exception e) {
			System.err.println(table() + " - listBy(Map<String, Object> fields) 조회 중 에러");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PageInfo<T> page() throws Exception {
		int total = count();
		Page page = new Page(total);

		String sql = " SELECT * FROM " + table() + " LIMIT ?, ? ";

		PageInfo<T> pageInfo = new PageInfo<>();
		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			pageInfo.setPage(page);
			pageInfo.setList(list);
		} catch (Exception e) {
			System.err.println(table() + " - page() 조회 중 에러");
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public PageInfo<T> page(PageInfo<T> pageInfo) throws Exception {
		Page page = pageInfo.getPage();
		if (page == null || page.getTotal() == 0) {
			int total = count(pageInfo);
			if (page == null)
				page = new Page();
			page.setTotal(total);
		}
		String searchCondition = getSearchOptions(pageInfo.getSearchOptions());
		int searchCounditionCount = pageInfo.getSearchOptions().size();
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1" + "   AND ( " + searchCondition + "       )"
				+ " LIMIT ?, ? ";

		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			for (int i = 0; i < searchCounditionCount; i++) {
				psmt.setString(index++, pageInfo.getKeyword());
			}
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			pageInfo.setPage(page);
			pageInfo.setList(list);
		} catch (Exception e) {
			System.err.println(table() + " - page() 조회 중 에러");
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public PageInfo<T> page(Page page) throws Exception {
		if (page == null || page.getTotal() == 0) {
			int total = count();
			if (page == null)
				page = new Page();
			page.setTotal(total);
		}
		String sql = " SELECT * " + " FROM " + table() + " LIMIT ?, ? ";
		PageInfo<T> pageInfo = new PageInfo<>();
		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			pageInfo.setPage(page);
			pageInfo.setList(list);
		} catch (Exception e) {
			System.err.println(table() + " - page() 조회 중 에러");
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public PageInfo<T> page(Page page, Map<String, String> filterOptions) throws Exception {
		if (page == null || page.getTotal() == 0) {
			int total = count();
			if (page == null)
				page = new Page();
			page.setTotal(total);
		}
		String orderBy = getFilterOptions(filterOptions);
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1 " + orderBy + " LIMIT ?, ? ";

		PageInfo<T> pageInfo = new PageInfo<>();
		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			pageInfo.setPage(page);
			pageInfo.setList(list);
		} catch (Exception e) {
			System.err.println(table() + " - page(page, filterOptions) 조회 중 에러");
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public PageInfo<T> page(Page page, String keyword, List<String> searchOptions) throws Exception {
		if (page == null || page.getTotal() == 0) {
			int total = count(keyword, searchOptions);
			if (page == null)
				page = new Page();
			page.setTotal(total);
		}
		String searchCondition = getSearchOptions(searchOptions);
		int searchCounditionCount = searchOptions == null ? 0 : searchOptions.size();
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1" + "   AND ( " + searchCondition + "       )"
				+ " LIMIT ?, ? ";

		PageInfo<T> pageInfo = new PageInfo<>();
		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			for (int i = 0; i < searchCounditionCount; i++) {
				psmt.setString(index++, keyword);
			}
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			pageInfo.setPage(page);
			pageInfo.setList(list);
		} catch (Exception e) {
			System.err.println(table() + " - page(page, keyword, searchOptions) 조회 중 에러");
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public PageInfo<T> page(Page page, String keyword, List<String> searchOptions, Map<String, String> filterOptions)
			throws Exception {
		if (page == null || page.getTotal() == 0) {
			int total = count(keyword, searchOptions);
			if (page == null)
				page = new Page();
			page.setTotal(total);
		}

		String searchCondition = getSearchOptions(searchOptions);
		String AND = " AND ( " + searchCondition + " )";

		if ((keyword == null || keyword.equals("")) || (searchOptions == null || searchOptions.size() == 0)) {
			AND = "";
		}
		int searchCounditionCount = searchOptions == null ? 0 : searchOptions.size();
		String orderBy = getFilterOptions(filterOptions);
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1 " + AND + orderBy + " LIMIT ?, ? ";

		PageInfo<T> pageInfo = new PageInfo<>();
		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);

			int index = 1;
			for (int i = 0; i < searchCounditionCount; i++) {
				psmt.setString(index++, keyword);
			}
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			pageInfo.setPage(page);
			pageInfo.setList(list);
		} catch (Exception e) {
			System.err.println(table() + " - page(page, keyword, searchOptions, filterOptions) 조회 중 에러");
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public T select(Object pk) throws Exception {
		String sql = "SELECT * FROM " + table() + " WHERE " + pk() + " = ?";
		StringBuilder param = new StringBuilder("param (?) : (1)").append(pk.toString()).append(" ");

		try {
			psmt = con.prepareStatement(sql);
			setPreparedStatementValue(psmt, 1, pk); // 중복 제거
			log(new StringBuilder(sql), param);

			rs = psmt.executeQuery();
			if (rs.next()) {
				return map(rs); // 조회된 row -> entity로 변환
			}
		} catch (Exception e) {
			System.err.println(table() + " - select(pk) 조회 중 에러");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public T where(Map<String, Object> fields) throws Exception {
		return selectBy(fields);
	}

	@Override
	public T selectBy(Map<String, Object> fields) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM " + table() + " WHERE ");
		StringBuilder param = new StringBuilder("param (?) : ");
		boolean first = true;

		for (Map.Entry<String, Object> entry : fields.entrySet()) {
			if (!first) {
				sql.append(" AND ");
			}
			sql.append(entry.getKey()).append(" = ?");
			first = false;
		}

		try {
			psmt = con.prepareStatement(sql.toString());

			int index = 1;
			for (Map.Entry<String, Object> entry : fields.entrySet()) {
				Object value = entry.getValue();
				param.append("(").append(index).append(")").append(value).append(" ");
				setPreparedStatementValue(psmt, index++, value); // 중복 제거
			}

			log(sql, param);

			rs = psmt.executeQuery();
			if (rs.next()) {
				return map(rs);
			}
		} catch (Exception e) {
			System.err.println(table() + " - selectBy(Map<String, Object>) 조회 중 에러");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * IN 조건 값들을 '' 로 묶어서 반환
	 * 
	 * @param col
	 * @param fields
	 * @return
	 */
	private String getInCondition(String col, String fields) {
		if (fields == null || fields.isEmpty()) {
			return "";
		}
		if (!fields.contains(",")) {
			return "AND " + col + " IN ( '" + fields + "' ) ";
		}
		String[] fieldArray = fields.split(",");
		StringBuilder formattedFields = new StringBuilder();
		for (String field : fieldArray) {
			if (formattedFields.length() > 0) {
				formattedFields.append(", ");
			}
			formattedFields.append("'").append(field.trim()).append("'");
		}
		return "AND " + col + " IN ( " + formattedFields.toString() + " ) ";
	}

	private String getInConditions(String col, String... fields) {
		if (fields == null || fields.length == 0) {
			return "";
		}
		StringBuilder formattedFields = new StringBuilder();
		for (String field : fields) {
			if (formattedFields.length() > 0) {
				formattedFields.append(", ");
			}
			formattedFields.append("'").append(field.trim()).append("'");
		}
		return "AND " + col + " IN ( " + formattedFields.toString() + " ) ";
	}

	private String getInConditions(String col, List<String> fieldList) {
		if (fieldList == null || fieldList.isEmpty()) {
			return "";
		}
		StringBuilder formattedFields = new StringBuilder();
		for (String field : fieldList) {
			if (formattedFields.length() > 0) {
				formattedFields.append(", ");
			}
			formattedFields.append("'").append(field.trim()).append("'");
		}
		return "AND " + col + " IN ( " + formattedFields.toString() + " ) ";
	}

	@Override
	public List<T> in(String col, String fields) throws Exception {
		String IN = getInCondition(col, fields);
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1 " + IN;

		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
		} catch (Exception e) {
			System.err.println(table() + " - inAndPage(PageInfo<T> pageInfo, String col, String fields) 조회 중 에러");
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<T> in(String col, String... field) throws Exception {
		String IN = getInConditions(col, field);
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1 " + IN;

		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
		} catch (Exception e) {
			System.err.println(table() + " - inAndPage(PageInfo<T> pageInfo, String col, String fields) 조회 중 에러");
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<T> in(String col, List<String> fieldList) throws Exception {
		String IN = getInConditions(col, fieldList);
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1 " + IN;

		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
		} catch (Exception e) {
			System.err.println(table() + " - inAndPage(PageInfo<T> pageInfo, String col, String fields) 조회 중 에러");
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public PageInfo<T> inAndPage(PageInfo<T> pageInfo, String col, String fields) throws Exception {
		Page page = pageInfo.getPage();
		if (page == null || page.getTotal() == 0) {
			int total = count(pageInfo);
			if (page == null)
				page = new Page();
			page.setTotal(total);
		}
		String searchCondition = getSearchOptions(pageInfo.getSearchOptions());
		int searchCounditionCount = pageInfo.getSearchOptions().size();

		String IN = getInCondition(col, fields);
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1 " + IN + " AND   ( " + searchCondition + "       )"
				+ " LIMIT ?, ? ";

		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			for (int i = 0; i < searchCounditionCount; i++) {
				psmt.setString(index++, pageInfo.getKeyword());
			}
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			pageInfo.setPage(page);
			pageInfo.setList(list);
		} catch (Exception e) {
			System.err.println(table() + " - inAndPage(PageInfo<T> pageInfo, String col, String fields) 조회 중 에러");
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public PageInfo<T> inAndPage(PageInfo<T> pageInfo, String col, String... field) throws Exception {
		Page page = pageInfo.getPage();
		if (page == null || page.getTotal() == 0) {
			int total = count(pageInfo);
			if (page == null)
				page = new Page();
			page.setTotal(total);
		}
		String searchCondition = getSearchOptions(pageInfo.getSearchOptions());
		int searchCounditionCount = pageInfo.getSearchOptions().size();

		String IN = getInConditions(col, field);
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1 " + IN + " AND   ( " + searchCondition + "       )"
				+ " LIMIT ?, ? ";

		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			for (int i = 0; i < searchCounditionCount; i++) {
				psmt.setString(index++, pageInfo.getKeyword());
			}
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			pageInfo.setPage(page);
			pageInfo.setList(list);
		} catch (Exception e) {
			System.err.println(table() + " - inAndPage(PageInfo<T> pageInfo, String col, String fields) 조회 중 에러");
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public PageInfo<T> inAndPage(PageInfo<T> pageInfo, String col, List<String> fieldList) throws Exception {
		Page page = pageInfo.getPage();
		if (page == null || page.getTotal() == 0) {
			int total = count(pageInfo);
			if (page == null)
				page = new Page();
			page.setTotal(total);
		}
		String searchCondition = getSearchOptions(pageInfo.getSearchOptions());
		int searchCounditionCount = pageInfo.getSearchOptions().size();

		String IN = getInConditions(col, fieldList);
		String sql = " SELECT * " + " FROM " + table() + " WHERE 1=1 " + IN + " AND   ( " + searchCondition + "       )"
				+ " LIMIT ?, ? ";

		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			for (int i = 0; i < searchCounditionCount; i++) {
				psmt.setString(index++, pageInfo.getKeyword());
			}
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			log(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				T entity = map(rs);
				list.add(entity);
			}
			pageInfo.setPage(page);
			pageInfo.setList(list);
		} catch (Exception e) {
			System.err.println(table() + " - inAndPage(PageInfo<T> pageInfo, String col, String fields) 조회 중 에러");
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public int insert(T entity) throws Exception {
		int result = 0;
		StringBuilder sql = new StringBuilder("INSERT INTO " + table() + " (");
		StringBuilder placeholders = new StringBuilder(" VALUES (");

		Field[] fields = entity.getClass().getDeclaredFields();
		boolean first = true;

		for (Field field : fields) {
			field.setAccessible(true);

			Column tableField = field.getAnnotation(Column.class);
			if (tableField != null && !tableField.exist()) {
				continue;
			}

			Object value = field.get(entity);

			if (value != null && !isDefaultValue(value)) {
				if (!first) {
					sql.append(", ");
					placeholders.append(", ");
				}
				String fieldName = field.getName();
				if (Config.mapCamelCaseToUnderscore) {
					fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
				}
				sql.append(fieldName);
				placeholders.append("?");
				first = false;
			}
		}

		sql.append(") ");
		placeholders.append(")");
		sql.append(placeholders.toString());

		try {
			psmt = con.prepareStatement(sql.toString());
			int index = 1;

			for (Field field : fields) {
				field.setAccessible(true);

				Column tableField = field.getAnnotation(Column.class);
				if (tableField != null && !tableField.exist()) {
					continue;
				}

				Object value = field.get(entity);

				if (value != null && !isDefaultValue(value)) {
					setPreparedStatementValue(psmt, index++, value); // 중복 제거
				}
			}
			log(sql);
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - insert(entity) 도중 에러");
			e.printStackTrace();
		}
		return result;
	}

	private boolean isDefaultValue(Object value) {
		if (value instanceof Long) {
			return (Long) value == 0;
		} else if (value instanceof Boolean) {
			return !(Boolean) value;
		} else if (value instanceof Integer) {
			return (Integer) value == 0;
		} else if (value instanceof Double) {
			return (Double) value == 0.0;
		} else if (value instanceof Float) {
			return (Float) value == 0.0f;
		}
		return false;
	}

	@Override
	public int insert(T entity, String... fieldNames) throws Exception {
		int result = 0;
		StringBuilder sql = new StringBuilder("INSERT INTO " + table() + " (");
		StringBuilder placeholders = new StringBuilder(" VALUES (");

		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : entity.getClass().getDeclaredFields()) {
			fieldMap.put(field.getName(), field);
		}

		boolean first = true;
		for (String fieldName : fieldNames) {
			Field field = fieldMap.get(fieldName);
			if (field != null) {
				Column tableField = field.getAnnotation(Column.class);
				if (tableField != null && !tableField.exist()) {
					continue; // DB에 존재하지 않는 필드는 건너뜀
				}

				if (!first) {
					sql.append(", ");
					placeholders.append(", ");
				}

				if (Config.mapCamelCaseToUnderscore) {
					fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
				}

				sql.append(fieldName);
				placeholders.append("?");
				first = false;
			}
		}

		sql.append(") ");
		placeholders.append(")");
		sql.append(placeholders.toString());

		try {
			psmt = con.prepareStatement(sql.toString());
			int index = 1;

			for (String fieldName : fieldNames) {
				Field field = fieldMap.get(fieldName);
				if (field != null) {
					Column tableField = field.getAnnotation(Column.class);
					if (tableField != null && !tableField.exist()) {
						continue;
					}

					field.setAccessible(true);
					Object value = field.get(entity);

					setPreparedStatementValue(psmt, index++, value); // 중복 제거!
				}
			}
			log(sql);
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - insert(entity, String...) 도중 에러");
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public T insertKey(T entity) throws Exception {
		int result = 0;
		StringBuilder sql = new StringBuilder("INSERT INTO " + table() + " (");
		StringBuilder placeholders = new StringBuilder(" VALUES (");

		Field[] fields = entity.getClass().getDeclaredFields();
		boolean first = true;

		for (Field field : fields) {
			field.setAccessible(true);

			// 🔹 TableField(exist = false) 필드 건너뛰기
			Column tf = field.getAnnotation(Column.class);
			if (tf != null && !tf.exist())
				continue;

			Object value = field.get(entity);
			if (value != null && !isDefaultValue(value)) {
				if (!first) {
					sql.append(", ");
					placeholders.append(", ");
				}
				String fieldName = field.getName();
				if (Config.mapCamelCaseToUnderscore) {
					fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
				}
				sql.append(fieldName);
				placeholders.append("?");
				first = false;
			}
		}

		sql.append(") ");
		placeholders.append(")");
		sql.append(placeholders.toString());

		try {
			psmt = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			int index = 1;

			for (Field field : fields) {
				field.setAccessible(true);

				// 🔹 TableField(exist = false) 필드 건너뛰기
				Column tf = field.getAnnotation(Column.class);
				if (tf != null && !tf.exist())
					continue;

				Object value = field.get(entity);
				if (value != null && !isDefaultValue(value)) {
					setPreparedStatementValue(psmt, index++, value); // 중복 제거!
				}
			}

			log(sql);
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - insert(entity) 도중 에러");
			e.printStackTrace();
		}

		// 🔹 AUTO_INCREMENT key 설정
		Long genKey = 0L;
		try (ResultSet generatedKeys = psmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				genKey = generatedKeys.getLong(1);
				Field pkField = entity.getClass().getDeclaredField(pk());
				pkField.setAccessible(true);
				if (pkField.getType().equals(Long.class) || pkField.getType().equals(long.class)) {
					pkField.set(entity, genKey);
				} else if (pkField.getType().equals(Integer.class) || pkField.getType().equals(int.class)) {
					pkField.set(entity, genKey.intValue());
				} else if (pkField.getType().equals(String.class)) {
					pkField.set(entity, genKey.toString());
				} else {
					pkField.set(entity, genKey);
				}
				System.out.println("genKey : " + genKey);
			}
		} catch (Exception e) {
			System.err.println(table() + " - insertKey(entity) 도중 에러");
			e.printStackTrace();
		}

		return entity;
	}

	@Override
	public T insertKey(T entity, String... fieldNames) throws Exception {
		int result = 0;
		StringBuilder sql = new StringBuilder("INSERT INTO " + table() + " (");
		StringBuilder placeholders = new StringBuilder(" VALUES (");

		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : entity.getClass().getDeclaredFields()) {
			fieldMap.put(field.getName(), field);
		}

		boolean first = true;
		for (String fieldName : fieldNames) {
			Field field = fieldMap.get(fieldName);
			if (field == null)
				continue;

			// 🔹 TableField(exist = false) 필드 제외
			Column tf = field.getAnnotation(Column.class);
			if (tf != null && !tf.exist())
				continue;

			if (!first) {
				sql.append(", ");
				placeholders.append(", ");
			}

			if (Config.mapCamelCaseToUnderscore) {
				fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
			}

			sql.append(fieldName);
			placeholders.append("?");
			first = false;
		}

		sql.append(") ");
		placeholders.append(")");
		sql.append(placeholders.toString());

		try {
			psmt = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			int index = 1;

			for (String fieldName : fieldNames) {
				Field field = fieldMap.get(fieldName);
				if (field == null)
					continue;

				// 🔹 TableField(exist = false) 필드 제외
				Column tf = field.getAnnotation(Column.class);
				if (tf != null && !tf.exist())
					continue;

				field.setAccessible(true);
				Object value = field.get(entity);
				if (value != null && !isDefaultValue(value)) {
					setPreparedStatementValue(psmt, index++, value); // 중복 제거!
				}
			}

			log(sql);
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - insert(entity, String...) 도중 에러");
			e.printStackTrace();
		}

		// AUTO_INCREMENT key 처리
		Long genKey = 0L;
		try (ResultSet generatedKeys = psmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				genKey = generatedKeys.getLong(1);
				Field pkField = entity.getClass().getDeclaredField(pk());
				pkField.setAccessible(true);
				if (pkField.getType().equals(Long.class) || pkField.getType().equals(long.class)) {
					pkField.set(entity, genKey);
				} else if (pkField.getType().equals(Integer.class) || pkField.getType().equals(int.class)) {
					pkField.set(entity, genKey.intValue());
				} else if (pkField.getType().equals(String.class)) {
					pkField.set(entity, genKey.toString());
				} else {
					pkField.set(entity, genKey);
				}
				System.out.println("genKey : " + genKey);
			}
		} catch (Exception e) {
			System.err.println(table() + " - insertKey(entity) 도중 에러");
			e.printStackTrace();
		}

		return entity;
	}

	@Override
	public int update(T entity) throws Exception {
		int result = 0;
		StringBuilder sql = new StringBuilder("UPDATE " + table() + " SET ");
		StringBuilder whereClause = new StringBuilder(" WHERE " + pk() + " = ?");

		Field[] fields = entity.getClass().getDeclaredFields();
		boolean first = true;
		Object pkValue = null;

		for (Field field : fields) {
			field.setAccessible(true);

			// 🔹 존재하지 않는 필드 제외
			Column tf = field.getAnnotation(Column.class);
			if (tf != null && !tf.exist())
				continue;

			Object value = field.get(entity);
			String fieldName = field.getName();
			if (Config.mapCamelCaseToUnderscore) {
				fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
			}

			if (fieldName.equals(pk())) {
				pkValue = value;
				continue;
			}

			if (value != null) {
				if (!first) {
					sql.append(", ");
				}
				sql.append(fieldName).append(" = ?");
				first = false;
			}
		}

		sql.append(whereClause);

		try {
			psmt = con.prepareStatement(sql.toString());
			int index = 1;
			StringBuilder param = new StringBuilder("param (?) : ");

			for (Field field : fields) {
				field.setAccessible(true);

				// 🔹 존재하지 않는 필드 제외
				Column tf = field.getAnnotation(Column.class);
				if (tf != null && !tf.exist())
					continue;

				Object value = field.get(entity);
				String fieldName = field.getName();
				if (Config.mapCamelCaseToUnderscore) {
					fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
				}

				if (fieldName.equals(pk())) {
					continue;
				}

				if (value != null) {
					param.append("(").append(index).append(")").append(value.toString()).append(" ");
					setPreparedStatementValue(psmt, index++, value); // 중복 제거!
				}
			}

			// WHERE 절의 pk 파라미터 세팅
			param.append("(").append(index).append(")").append(pkValue.toString()).append(" ");
			setPreparedStatementValue(psmt, index, pkValue);

			log(sql, param, pkValue.toString());
			result = psmt.executeUpdate();

		} catch (Exception e) {
			System.err.println(table() + " - update(entity) 도중 에러");
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int update(T entity, String... fields) throws Exception {
		int result = 0;
		StringBuilder sql = new StringBuilder("UPDATE " + table() + " SET ");
		StringBuilder whereClause = new StringBuilder(" WHERE " + pk() + " = ?");

		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : entity.getClass().getDeclaredFields()) {
			fieldMap.put(field.getName(), field);
		}

		boolean first = true;

		for (String fieldName : fields) {
			if (fieldMap.containsKey(fieldName)) {
				Field field = fieldMap.get(fieldName);

				// 🔹 존재하지 않는 필드 제외
				Column tf = field.getAnnotation(Column.class);
				if (tf != null && !tf.exist())
					continue;

				field.setAccessible(true);
				Object value = field.get(entity);

				if (Config.mapCamelCaseToUnderscore) {
					fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
				}

				if (value != null) {
					if (!first)
						sql.append(", ");
					sql.append(fieldName).append(" = ?");
					first = false;
				}
			}
		}

		sql.append(whereClause);

		Field pkField = fieldMap.get(pk());
		pkField.setAccessible(true);
		Object pkValue = pkField.get(entity);

		try {
			psmt = con.prepareStatement(sql.toString());
			int index = 1;
			StringBuilder param = new StringBuilder("param (?) : ");

			for (String fieldName : fields) {
				if (fieldMap.containsKey(fieldName)) {
					Field field = fieldMap.get(fieldName);

					// 🔹 존재하지 않는 필드 제외
					Column tf = field.getAnnotation(Column.class);
					if (tf != null && !tf.exist())
						continue;

					field.setAccessible(true);
					Object value = field.get(entity);

					if (field.getName().equals(pk())) {
						continue;
					}

					if (Config.mapCamelCaseToUnderscore) {
						fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
					}

					if (value != null) {
						param.append("(").append(index).append(")").append(value.toString()).append(" ");
						setPreparedStatementValue(psmt, index++, value); // 중복 제거!
					}
				}
			}

			// 🔸 WHERE 절 pk 바인딩
			param.append("(").append(index).append(")").append(pkValue.toString()).append(" ");
			if (pkValue instanceof String) {
				psmt.setString(index, (String) pkValue);
			} else if (pkValue instanceof Boolean) {
				psmt.setBoolean(index, (Boolean) pkValue);
			} else if (pkValue instanceof Long) {
				psmt.setLong(index, (Long) pkValue);
			} else if (pkValue instanceof Integer) {
				psmt.setInt(index, (Integer) pkValue);
			} else if (pkValue instanceof Double) {
				psmt.setDouble(index, (Double) pkValue);
			} else if (pkValue instanceof Float) {
				psmt.setFloat(index, (Float) pkValue);
			} else if (pkValue instanceof java.util.Date) {
				psmt.setDate(index, new java.sql.Date(((java.util.Date) pkValue).getTime()));
			} else {
				psmt.setObject(index, pkValue);
			}

			log(sql, param, pkValue.toString());
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - update(entity, String...) 도중 에러");
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public int updateBy(T entity, Map<String, Object> map) throws Exception {
		int result = 0;
		StringBuilder sql = new StringBuilder("UPDATE " + table() + " SET ");
		Field[] fields = entity.getClass().getDeclaredFields();
		boolean first = true;

		// SET절 구성 (PK 제외, null 제외)
		for (Field field : fields) {
			field.setAccessible(true);
			Column tf = field.getAnnotation(Column.class);
			if (tf != null && !tf.exist())
				continue;
			String fieldName = field.getName();
			if (Config.mapCamelCaseToUnderscore) {
				fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
			}
			if (fieldName.equals(pk()))
				continue;
			Object value = field.get(entity);
			if (value != null) {
				if (!first)
					sql.append(", ");
				sql.append(fieldName).append(" = ?");
				first = false;
			}
		}

		// WHERE절 구성
		sql.append(" WHERE ");
		boolean firstCond = true;
		for (String key : map.keySet()) {
			String fieldName = key;
			if (Config.mapCamelCaseToUnderscore) {
				fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
			}
			if (!firstCond)
				sql.append(" AND ");
			sql.append(fieldName).append(" = ?");
			firstCond = false;
		}

		try {
			psmt = con.prepareStatement(sql.toString());
			int index = 1;
			StringBuilder param = new StringBuilder("param (?) : ");
			// SET절 파라미터 바인딩
			for (Field field : fields) {
				field.setAccessible(true);
				Column tf = field.getAnnotation(Column.class);
				if (tf != null && !tf.exist())
					continue;
				String fieldName = field.getName();
				if (Config.mapCamelCaseToUnderscore) {
					fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
				}
				if (fieldName.equals(pk()))
					continue;
				Object value = field.get(entity);
				if (value != null) {
					param.append("(").append(index).append(")").append(value).append(" ");
					setPreparedStatementValue(psmt, index++, value);
				}
			}
			// WHERE절 파라미터 바인딩
			for (Object value : map.values()) {
				param.append("(").append(index).append(")").append(value).append(" ");
				setPreparedStatementValue(psmt, index++, value);
			}
			log(sql, param);
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - updateBy(entity, map) 도중 에러");
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int delete(Object pk) throws Exception {
		int result = 0;
		String sql = "DELETE FROM " + table() + " WHERE " + pk() + " = ?";

		try {
			psmt = con.prepareStatement(sql);
			setPreparedStatementValue(psmt, 1, pk); // 🔹 타입별 분기 공통 메서드로 분리

			log(sql);
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - delete(pk) 도중 에러");
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int deleteBy(Map<String, Object> fields) throws Exception {
		int result = 0;
		StringBuilder sql = new StringBuilder("DELETE FROM " + table() + " WHERE ");
		boolean first = true;

		for (Map.Entry<String, Object> entry : fields.entrySet()) {
			String fieldName = entry.getKey();
			if (Config.mapCamelCaseToUnderscore) {
				fieldName = StringUtil.convertCamelCaseToUnderscore(fieldName);
			}
			if (!first) {
				sql.append(" AND ");
			}
			sql.append(fieldName).append(" = ?");
			first = false;
		}

		try {
			psmt = con.prepareStatement(sql.toString());
			int index = 1;
			StringBuilder paramLog = new StringBuilder("param (?) : ");

			for (Object value : fields.values()) {
				paramLog.append("(").append(index).append(")").append(value).append(" ");
				setPreparedStatementValue(psmt, index++, value);
			}

			log(sql, paramLog);
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - deleteBy(Map<String, Object> fields) 도중 에러");
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int count() throws Exception {
		int total = 0;
		String sql = "SELECT COUNT(*) FROM " + table();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				total = rs.getInt(1);
			}
		} catch (Exception e) {
			System.err.println(table() + " - count() 조회 중 에러");
			e.printStackTrace();
		}
		return total;
	}

	@Override
	public int count(PageInfo<T> pageInfo) throws Exception {
		int total = 0;
		String searchCondition = getSearchOptions(pageInfo.getSearchOptions());
		int searchConditionCount = pageInfo.getSearchOptions().size();
		String sql = "SELECT COUNT(*) FROM " + table() + " WHERE 1=1 AND (" + searchCondition + ")";

		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			for (int i = 0; i < searchConditionCount; i++) {
				psmt.setString(index++, pageInfo.getKeyword());
			}
			rs = psmt.executeQuery();
			if (rs.next()) {
				total = rs.getInt(1);
			}
		} catch (Exception e) {
			System.err.println(table() + " - count(PageInfo<T> pageInfo) 조회 중 에러");
			e.printStackTrace();
		}
		return total;
	}

	@Override
	public int count(String keyword, List<String> searchOptions) throws Exception {
		int total = 0;
		String searchCondition = getSearchOptions(searchOptions);
		int searchConditionCount = searchOptions == null ? 0 : searchOptions.size();
		String sql = "SELECT COUNT(*) FROM " + table() + " WHERE 1=1 AND (" + searchCondition + ")";

		try {
			psmt = con.prepareStatement(sql);
			int index = 1;
			for (int i = 0; i < searchConditionCount; i++) {
				psmt.setString(index++, keyword);
			}
			rs = psmt.executeQuery();
			if (rs.next()) {
				total = rs.getInt(1);
			}
		} catch (Exception e) {
			System.err.println(table() + " - count(keyword, searchOptions) 조회 중 에러");
			e.printStackTrace();
		}
		return total;

	}

	/**
	 * SQL 로그
	 * 
	 * @param sql
	 */
	public void log(String sql) {
		if (Config.sqlLog) {
			System.out.println("[SQL] - alcl.jdbc");
			System.out.println("==================================================");
			System.out.println(sql);
			System.out.println("==================================================");
		}
	}

	public void log(StringBuilder sql) {
		if (Config.sqlLog) {
			System.out.println("[SQL] - alcl.jdbc");
			System.out.println("==================================================");
			System.out.println(sql.toString());
			System.out.println("==================================================");
		}
	}

	public void log(StringBuilder sql, StringBuilder param) {
		if (Config.sqlLog) {
			System.out.println("[SQL] - alcl.jdbc");
			System.out.println("==================================================");
			System.out.println(sql);
			System.out.println(param.toString());
			System.out.println("==================================================");
		}
	}

	public void log(StringBuilder sql, StringBuilder param, String pk) {
		if (Config.sqlLog) {
			System.out.println("[SQL] - alcl.jdbc");
			System.out.println("==================================================");
			System.out.println(sql);
			System.out.println(param.toString());
			System.out.println("pk - " + pk() + " : " + pk);
			System.out.println("==================================================");
		}
	}

	// 🔸 타입 분기 공통화
	private void setPreparedStatementValue(PreparedStatement ps, int index, Object value) throws SQLException {
		if (value instanceof String) {
			ps.setString(index, (String) value);
		} else if (value instanceof Boolean) {
			ps.setBoolean(index, (Boolean) value);
		} else if (value instanceof Long) {
			ps.setLong(index, (Long) value);
		} else if (value instanceof Integer) {
			ps.setInt(index, (Integer) value);
		} else if (value instanceof Double) {
			ps.setDouble(index, (Double) value);
		} else if (value instanceof Float) {
			ps.setFloat(index, (Float) value);
		} else if (value instanceof Date) {
			Date dateValue = (Date) value;
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateValue);
			Timestamp timestampValue = new Timestamp(cal.getTimeInMillis());
			ps.setTimestamp(index, timestampValue);
		} else {
			ps.setObject(index, value);
		}
	}

}






