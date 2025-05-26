package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alohaclass.jdbc.dao.BaseDAOImpl;
import com.alohaclass.jdbc.dto.Page;
import com.alohaclass.jdbc.dto.PageInfo;

public class BaseServiceImpl<D extends BaseDAOImpl<T>, T> implements BaseService<T> {
	
	protected D dao;

    // 생성자 주입 방식
    public BaseServiceImpl(D dao) {
        this.dao = dao;
    }

	@Override
	public List<T> list() {
		List<T> list = null;
		try {
			list = dao.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public PageInfo<T> page(int page, int pageSize) {
		PageInfo<T> pageInfo = null;
		try {
			Page pageObj = new Page();
			pageObj.setPage(1);
			pageObj.setSize(10);
			pageInfo = dao.page(pageObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageInfo;
	}
	
	

	@Override
	public T select(Long no) {
		T t = null;
		try {
			t = dao.select(no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public T selectById(String id) {
		T t = null;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("id", id);
			t = dao.selectBy(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public boolean insert(T entity) {
		int result = 0;
		try {
			result = dao.insert(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result > 0;
	}

	@Override
	public boolean update(T entity) {
		int result = 0;
		try {
			result = dao.update(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result > 0;
	}

	@Override
	public boolean updateById(T entity, String id) {
		int result = 0;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("id", id);
			result = dao.updateBy(entity, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result > 0;
	}

	@Override
	public boolean delete(Long no) {
		int result = 0;
		try {
			result = dao.delete(no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result > 0;
	}

	@Override
	public boolean deleteById(String id) {
		int result = 0;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("id", id);
			result = dao.deleteBy(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result > 0;
	}

	@Override
	public PageInfo<T> page(Page page, String keyword, List<String> columns) {
		PageInfo<T> pageInfo = null;
		try {
			pageInfo = dao.page(page, keyword, columns);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageInfo;
	}
	

	
	
}













