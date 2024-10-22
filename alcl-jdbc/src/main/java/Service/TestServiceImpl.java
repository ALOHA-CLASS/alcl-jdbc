package Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alohaclass.jdbc.dto.Page;
import com.alohaclass.jdbc.dto.PageInfo;

import DAO.TestDAO;
import DTO.Test;

public class TestServiceImpl implements TestService {
	
	TestDAO testDAO = new TestDAO();

	@Override
	public List<Test> list() {
		List<Test> list = null;
		try {
			list = testDAO.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public PageInfo<Test> page() {
		PageInfo<Test> pageInfo = null;
		try {
			// 방법1
			// pageInfo = testDAO.page();
			// 방법2
			 Page page = new Page();
			 page.setPage(1);
			 page.setSize(10);
			// pageInfo = testDAO.page(page);
			// 방법3
			String keyword = "검색어";
			List<String> searchOptions = new ArrayList<String>();
			searchOptions.add("name");
			searchOptions.add("age");
			// pageInfo = testDAO.page(page, keyword, searchOptions);
			// 방법4
			Map<String, String> filterOptions = new HashMap<String, String>() {{
	            put("name", "ASC");
	            put("age", "DESC");
	        }};
			testDAO.page(page, keyword, searchOptions, filterOptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public Test select(int no) {
		Test test = null;
		try {
			test = testDAO.select(no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return test;
	}

	@Override
	public int insert(Test test) {
		int result = 0; 
		try {
			result = testDAO.insert(test);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int update(Test test) {
		int result = 0;
		try {
			result = testDAO.update(test);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int delete(int no) {
		int result = 0;
		try {
			result = testDAO.delete(no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	

}








