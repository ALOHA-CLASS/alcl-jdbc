import java.util.ArrayList;
import java.util.List;

import com.alohaclass.jdbc.dto.Page;
import com.alohaclass.jdbc.dto.PageInfo;

import dao.BoardDAO;
import dto.Board;
import service.BoardService;
import service.BoardServiceImpl;

public class Test {
	
	public static void main(String[] args) {
		BoardDAO dao = new BoardDAO();
		BoardService service = new BoardServiceImpl(dao);
		List<Board> list = service.list();
		for (Board board : list) {
			System.out.println(board);
		}
		Board board = Board.builder()
							.title("수정")
							.build();
		String id = "42abca32-3220-11f0-ac9a-a8a1596f255e";
		boolean result = service.updateById(board, id);
		System.out.println("수정 결과 : " + result);
		
		// 페이징 검색
		Page pageObj = new Page();
		List<String> columns = new ArrayList<String>();
		columns.add("title");
		columns.add("writer");
		columns.add("content");
		
		PageInfo<Board> pageInfo = service.page(pageObj, "제목", columns);
		System.out.println("pageInfo : " + pageInfo);
		List<Board> boardList = pageInfo.getList();
		for (Board b : boardList) {
			System.out.println(b);
		}
		
		pageObj = pageInfo.getPage();
		System.out.println("pageObj : " + pageObj);
		
		
	}

}











