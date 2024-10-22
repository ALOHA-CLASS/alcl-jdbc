package com.alohaclass.jdbc.dao;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alohaclass.jdbc.dto.Page;
import com.alohaclass.jdbc.dto.PageInfo;

public abstract class BaseDAOImpl<T> extends JDBConnection implements BaseDAO<T> {

	public abstract String table();
    public abstract String pk();
    public abstract T map(ResultSet rs) throws Exception;

    
	@Override
	public List<T> list() throws Exception {
		String sql = " SELECT * FROM " + table();
		List<T> list = new ArrayList<T>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while( rs.next() ) {
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
	public PageInfo<T> page() throws Exception {
		int total = count();
		Page page = new Page(total);
		
		String sql = " SELECT * FROM " + table()
				   + " LIMIT ?, ? ";
		
		PageInfo<T> pageInfo = new PageInfo<>();
		List<T> list = new ArrayList<T>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while( rs.next() ) {
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
		if( page == null || page.getTotal() == 0 ) {
			int total = count(pageInfo);
			page = new Page(total);
		}
		String searchCondition = getSearchOptions(pageInfo.getSearchOptions());
		int searchCounditionCount = pageInfo.getSearchOptions().size();
		String sql = " SELECT * "
				   + " FROM " + table()
				   + " WHERE 1=1"
				   + "   AND ( "
				   + searchCondition
				   + "       )"
				   + " LIMIT ?, ? ";
		
		List<T> list = new ArrayList<T>();
		try {
			psmt = con.prepareStatement(sql);
			rs = psmt.executeQuery();
			int index = 1;
			for (int i = 0; i < searchCounditionCount; i++) {
				psmt.setString(index++, pageInfo.getKeyword());
			}
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			while( rs.next() ) {
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
		if( page == null || page.getTotal() == 0 ) {
			int total = count();
			page = new Page(total);
		}
		String sql = " SELECT * "
				   + " FROM " + table()
				   + " LIMIT ?, ? ";
		PageInfo<T> pageInfo = new PageInfo<>();
		List<T> list = new ArrayList<T>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			int index = 1;
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			while( rs.next() ) {
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
	public PageInfo<T> page(Page page, String keyword, List<String> searchOptions) throws Exception {
		if( page == null || page.getTotal() == 0 ) {
			int total = count(keyword, searchOptions);
			page = new Page(total);
		}
		String searchCondition = getSearchOptions(searchOptions);
		int searchCounditionCount = searchOptions.size();
		String sql = " SELECT * "
				   + " FROM " + table()
				   + " WHERE 1=1"
				   + "   AND ( "
				   + searchCondition
				   + "       )"
				   + " LIMIT ?, ? ";
		
		PageInfo<T> pageInfo = new PageInfo<>();
		List<T> list = new ArrayList<T>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			int index = 1;
			for (int i = 0; i < searchCounditionCount; i++) {
				psmt.setString(index++, keyword);
			}
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			while( rs.next() ) {
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
		if( page == null || page.getTotal() == 0 ) {
			int total = count(keyword, searchOptions);
			page = new Page(total);
		}
		String searchCondition = getSearchOptions(searchOptions);
		int searchCounditionCount = searchOptions.size();
		String orderBy = getFilterOptions(filterOptions);
		String sql = " SELECT * "
				   + " FROM " + table()
				   + " WHERE 1=1 "
				   + "   AND ( "
				   + searchCondition
				   + "       )"
				   + orderBy
				   + " LIMIT ?, ? ";
		
		PageInfo<T> pageInfo = new PageInfo<>();
		List<T> list = new ArrayList<T>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			int index = 1;
			for (int i = 0; i < searchCounditionCount; i++) {
				psmt.setString(index++, keyword);
			}
			psmt.setInt(index++, page.getIndex());
			psmt.setInt(index++, page.getSize());
			while( rs.next() ) {
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
		String sql = " SELECT * "
				   + " FROM users "
				   + " WHERE " + pk() + " = ? "
				   ;
		try {
			psmt = con.prepareStatement(sql);
			System.out.println("pk : " + pk);
			if (pk instanceof String) {
				psmt.setString(1, (String) pk);
			} else if (pk instanceof Boolean ) {
				psmt.setBoolean(1, (Boolean) pk);
			} else if (pk instanceof Long) {
				psmt.setLong(1, (Long) pk);
			} else if (pk instanceof Integer) {
				psmt.setInt(1, (Integer) pk);
			} else if (pk instanceof Double) {
				psmt.setDouble(1, (Double) pk);
			} else if (pk instanceof Float) {
				psmt.setFloat(1, (Float) pk);
			} else {
				psmt.setObject(1, pk);
			}
			
			rs = psmt.executeQuery();
			if( rs.next() ) {
				T entity = map(rs);
				return entity;
			}
		} catch (Exception e) {
			System.err.println(table() + " - select(pk 조회 중 에러");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public T where(Map<Object, Object> fields) throws Exception {
		 return selectBy(fields);
	}
	
	@Override
    public T selectBy(Map<Object, Object> fields) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + table() + " WHERE ");
        boolean first = true;

        for (Map.Entry<Object, Object> entry : fields.entrySet()) {
            if (!first) {
                sql.append(" AND ");
            }
            sql.append(entry.getKey()).append(" = ?");
            first = false;
        }

        try {
            psmt = con.prepareStatement(sql.toString());

            int index = 1;
            for (Map.Entry<Object, Object> entry : fields.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof String) {
                    psmt.setString(index++, (String) value);
                } else if (value instanceof Boolean) {
                    psmt.setBoolean(index++, (Boolean) value);
                } else if (value instanceof Long) {
                    psmt.setLong(index++, (Long) value);
                } else if (value instanceof Integer) {
                    psmt.setInt(index++, (Integer) value);
                } else if (value instanceof Double) {
                    psmt.setDouble(index++, (Double) value);
                } else if (value instanceof Float) {
                    psmt.setFloat(index++, (Float) value);
                } else if (value instanceof Date) {
                    psmt.setDate(index++, new java.sql.Date(((Date) value).getTime()));
                } else {
                    psmt.setObject(index++, value);
                }
            }
            rs = psmt.executeQuery();
            if (rs.next()) {
                T entity = map(rs);
                return entity;
            }
        } catch (Exception e) {
            System.err.println(table() + " - selectBy(Map<Object, Object> fields) 조회 중 에러");
            e.printStackTrace();
        }
        return null;
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
            Object value = field.get(entity);

            if (value != null && !isDefaultValue(value)) {
                if (!first) {
                    sql.append(", ");
                    placeholders.append(", ");
                }
                sql.append(field.getName());
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
                Object value = field.get(entity);

                if (value != null && !isDefaultValue(value)) {
                    if (value instanceof String) {
                        psmt.setString(index++, (String) value);
                    } else if (value instanceof Boolean) {
                        psmt.setBoolean(index++, (Boolean) value);
                    } else if (value instanceof Long) {
                        psmt.setLong(index++, (Long) value);
                    } else if (value instanceof Integer) {
                        psmt.setInt(index++, (Integer) value);
                    } else if (value instanceof Date) {
                        psmt.setDate(index++, new java.sql.Date(((Date) value).getTime()));
                    } else {
                        psmt.setObject(index++, value);
                    }
                }
            }
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
            if (fieldMap.containsKey(fieldName)) {
                if (!first) {
                    sql.append(", ");
                    placeholders.append(", ");
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
                if (fieldMap.containsKey(fieldName)) {
                    Field field = fieldMap.get(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(entity);

                    if (value instanceof String) {
                        psmt.setString(index++, (String) value);
                    } else if (value instanceof Boolean) {
                        psmt.setBoolean(index++, (Boolean) value);
                    } else if (value instanceof Long) {
                        psmt.setLong(index++, (Long) value);
                    } else if (value instanceof Date) {
                        psmt.setDate(index++, new java.sql.Date(((Date) value).getTime()));
                    } else {
                        psmt.setObject(index++, value);
                    }
                }
            }

            result = psmt.executeUpdate();
        } catch (Exception e) {
            System.err.println(table() + " - insert(entity, String...) 도중 에러");
            e.printStackTrace();
        }
        return result;
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
            Object value = field.get(entity);
            if (field.getName().equals(pk())) {
                pkValue = value;
                continue;
            }
            if (value != null) {
                if (!first) {
                    sql.append(", ");
                }
                sql.append(field.getName()).append(" = ?");
                first = false;
            }
        }

        sql.append(whereClause);

        try {
            psmt = con.prepareStatement(sql.toString());
            int index = 1;

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (field.getName().equals(pk())) {
                    continue;
                }
                if (value != null) {
                    if (value instanceof String) {
                        psmt.setString(index++, (String) value);
                    } else if (value instanceof Boolean) {
                        psmt.setBoolean(index++, (Boolean) value);
                    } else if (value instanceof Long) {
                        psmt.setLong(index++, (Long) value);
                    } else if (value instanceof Integer) {
                        psmt.setInt(index++, (Integer) value);
                    } else if (value instanceof Double) {
                        psmt.setDouble(index++, (Double) value);
                    } else if (value instanceof Float) {
                        psmt.setFloat(index++, (Float) value);
                    } else if (value instanceof java.util.Date) {
                        psmt.setDate(index++, new java.sql.Date(((java.util.Date) value).getTime()));
                    } else {
                        psmt.setObject(index++, value);
                    }
                }
            }

            // WHERE pk = ? <-- 조건 값 지정
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
		StringBuilder whereClause = new StringBuilder(" WHERE " + pk() + " = ? ");

		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : entity.getClass().getDeclaredFields()) {
			fieldMap.put(field.getName(), field);
		}
		boolean first = true;

		for (String fieldName : fields) {
			if (fieldMap.containsKey(fieldName)) {
				Field field = fieldMap.get(fieldName);
				field.setAccessible(true);
				Object value = field.get(entity);
				if (value != null) {
					if (!first) {
						sql.append(", ");
					}
					sql.append(field.getName()).append(" = ?");
					first = false;
				}
			}
		}

		sql.append(whereClause);
		
		Field map = fieldMap.get(pk());
		map.setAccessible(true);
		Object val = map.get(entity);

		try {
			psmt = con.prepareStatement(sql.toString());
			int index = 1;

			for (String fieldName : fields) {
				if (fieldMap.containsKey(fieldName)) {
					Field field = fieldMap.get(fieldName);
					field.setAccessible(true);
					Object value = field.get(entity);
					if (field.getName().equals(pk())) {
						continue;
					}
					if (value != null) {
						if (value instanceof String) {
							psmt.setString(index++, (String) value);
						} else if (value instanceof Boolean) {
							psmt.setBoolean(index++, (Boolean) value);
						} else if (value instanceof Long) {
							psmt.setLong(index++, (Long) value);
						} else if (value instanceof Integer) {
							psmt.setInt(index++, (Integer) value);
						} else if (value instanceof Double) {
							psmt.setDouble(index++, (Double) value);
						} else if (value instanceof Float) {
							psmt.setFloat(index++, (Float) value);
						} else if (value instanceof java.util.Date) {
							psmt.setDate(index++, new java.sql.Date(((java.util.Date) value).getTime()));
						} else {
							psmt.setObject(index++, value);
						}
					}
				}
			}
			
			Field pkField = fieldMap.get( pk() );
			Object pkValue = pkField.get(entity);;

			// WHERE pk = ? <-- 조건 값 지정
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
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - update(entity, String...) 도중 에러");
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
			if (pk instanceof String) {
				psmt.setString(1, (String) pk);
			} else if (pk instanceof Boolean) {
				psmt.setBoolean(1, (Boolean) pk);
			} else if (pk instanceof Long) {
				psmt.setLong(1, (Long) pk);
			} else if (pk instanceof Integer) {
				psmt.setInt(1, (Integer) pk);
			} else if (pk instanceof Double) {
				psmt.setDouble(1, (Double) pk);
			} else if (pk instanceof Float) {
				psmt.setFloat(1, (Float) pk);
			} else {
				psmt.setObject(1, pk);
			}
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - delete(pk) 도중 에러");
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int deleteBy(Map<Object, Object> fields) throws Exception {
		int result = 0;
		StringBuilder sql = new StringBuilder("DELETE FROM " + table() + " WHERE ");
		boolean first = true;

		for (Map.Entry<Object, Object> entry : fields.entrySet()) {
			if (!first) {
				sql.append(" AND ");
			}
			sql.append(entry.getKey()).append(" = ?");
			first = false;
		}
		System.out.println(sql);

		try {
			psmt = con.prepareStatement(sql.toString());

			int index = 1;
			for (Map.Entry<Object, Object> entry : fields.entrySet()) {
				Object value = entry.getValue();
				System.out.println("value : " + value);
				if (value instanceof String) {
					psmt.setString(index++, (String) value);
				} else if (value instanceof Boolean) {
					psmt.setBoolean(index++, (Boolean) value);
				} else if (value instanceof Long) {
					psmt.setLong(index++, (Long) value);
				} else if (value instanceof Integer) {
					psmt.setInt(index++, (Integer) value);
				} else if (value instanceof Double) {
					psmt.setDouble(index++, (Double) value);
				} else if (value instanceof Float) {
					psmt.setFloat(index++, (Float) value);
				} else if (value instanceof Date) {
					psmt.setDate(index++, new java.sql.Date(((Date) value).getTime()));
				} else {
					psmt.setObject(index++, value);
				}
			}
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.err.println(table() + " - deleteBy(Map<Object, Object> fields) 도중 에러");
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
		int searchConditionCount = searchOptions.size();
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

}
