package com.alohaclass.jdbc.dao;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alohaclass.jdbc.dto.Page;
import com.alohaclass.jdbc.dto.PageInfo;

/**
 * BaseDAO
 * - DAO 
 */
public interface BaseDAO<T> {
	
	// 테이블명
	String table();
	// PK (no or id)
	String pk();
	
	// 매핑
	T map(ResultSet rs) throws Exception;
	
	// 검색옵션 조건 
//	String getSearchOptions(List<String > searchOptions) throws Exception;
	default String getSearchOptions(List<String> searchOptions) throws Exception {
		if( searchOptions == null || searchOptions.size() == 0 ) {
			return "1=1";
		}
		String str = "";
		for (Iterator iterator = searchOptions.iterator(); iterator.hasNext();) {
			String column = (String) iterator.next();
			str += (column + " LIKE CONCAT('%', ?, '%') ");
			if( iterator.hasNext() ) 
				str += " OR ";
		}
		return str;
	}
	
	// 필터옵션 정렬
//	String getFilterOptions(Map<String, String> filterOptions) throws Exception;
	default String getFilterOptions(Map<String, String> filterOptions) throws Exception {
		String str = " ORDER BY ";
		Set<String> keys = filterOptions.keySet();
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = filterOptions.get(key);
			str += (key + " " + value);		// title ASC, writer DESC ...
			if( iterator.hasNext() )
				str += ", ";
		}
		return str;
	}
	
	
	// 목록
	List<T> list() throws Exception;
	
	// 페이징
	PageInfo<T> page() throws Exception;
	PageInfo<T> page(PageInfo<T> pageInfo) throws Exception;
	PageInfo<T> page(Page page) throws Exception;
	PageInfo<T> page(Page page, String keyword, List<String> searchOptions) throws Exception;
	PageInfo<T> page(Page page, String keyword, List<String> searchOptions, Map<String, String> filterOptions) throws Exception;
	
	/**
	 * select 
	 * - pk 기준으로 조회
	 * @param pk
	 * @return
	 * @throws Exception
	 */
	T select(Object pk) throws Exception;
	
	/**
	 * selectBy 
	 * - 1개 이상의 필드를 기준으로 조회
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	T selectBy(Map<Object, Object> fields) throws Exception;
	
	/**
	 * where == selectBy
	 * - 이름만 쉽게
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	T where(Map<Object, Object> fields) throws Exception;
	
	/**
	 * insert
	 * - null 이 아닌 필드만 insert
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	int insert(T entity) throws Exception;
	
	/**
	 * insert
	 * - 지정한 필드만 insert
	 * @param entity
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	int insert(T entity, String... fields) throws Exception;
	
	/**
	 * update
	 * - pk 조건으로 update
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	int update(T entity) throws Exception;
	
	/**
	 * update
	 * - 지정한 필드만 update
	 * @param entity
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	int update(T entity, String... fields) throws Exception;
	
	/**
	 * delete
	 * - pk 를 기준으로 delete
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	int delete(Object pk) throws Exception;
	
	/**
	 * delete
	 * - 1개 이상의 필드를 기준으로 삭제
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	int deleteBy(Map<Object, Object> fields) throws Exception;

	/**
	 * 개수
	 * - 전체
	 * @return
	 * @throws Exception
	 */
	int count() throws Exception;
	
	/**
	 * 개수
	 * - 페이징(검색) 조건
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	int count(PageInfo<T> pageInfo) throws Exception;
	
	/**
	 * 개수
	 * - 검색 조건
	 * @param keyword
	 * @param searchOptions
	 * @return
	 * @throws Exception
	 */
	int count(String keyword, List<String> searchOptions) throws Exception;
	

}





