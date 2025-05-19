import java.util.List;

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
//		Board board = Board.builder()
//							.title("수정")
//							.build();
//		String id = "42abca32-3220-11f0-ac9a-a8a1596f255e";
//		boolean result = service.updateById(board, id);
//		System.out.println("수정 결과 : " + result);
	}

}
