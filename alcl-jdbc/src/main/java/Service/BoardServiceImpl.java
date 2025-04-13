package Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alohaclass.jdbc.dto.Page;
import com.alohaclass.jdbc.dto.PageInfo;

import DAO.BoardDAO;
import DTO.Board;

public class BoardServiceImpl implements BoardService {
	
	BoardDAO boardDAO = new BoardDAO();

	@Override
	public List<Board> list() {
		List<Board> list = null;
		try {
			list = boardDAO.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	

	@Override
	public PageInfo<Board> page() {
		PageInfo<Board> pageInfo = null;
		try {
			Page page = new Page();
			page.setPage(1);
			page.setSize(10);
			// 방법1
			// pageInfo = boardDAO.page();
			// 방법2
			//pageInfo = testDAO.page(page);
			// 방법3
			String keyword = "검색어";
			List<String> searchOptions = List.of("title", "content");
			// pageInfo = boardDAO.page(page, keyword, searchOptions);
			// 방법4
			Map<String, String> filterOptions = new HashMap<String, String>() {{
	            put("title", "ASC");
	            put("writer", "DESC");
	        }};
	        boardDAO.page(page, keyword, searchOptions, filterOptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageInfo;
	}

	@Override
	public Board select(int no) {
		Board board = null;
		try {
			board = boardDAO.select(no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return board;
	}

	@Override
	public int insert(Board Board) {
		int result = 0;
		try {
			result = boardDAO.insert(Board);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Board insertKey(Board Board) {
		Board result = null;
		try {
			result = boardDAO.insertKey(Board);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int update(Board Board) {
		int result = 0;
		try {
			result = boardDAO.update(Board);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int delete(int no) {
		int result = 0;
		try {
			result = boardDAO.delete(no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	} 

}
