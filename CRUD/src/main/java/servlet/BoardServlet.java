package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

import dao.BoardDAO;
import dto.Board;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BoardService;
import service.BoardServiceImpl;

@WebServlet("/board")
public class BoardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardDAO boardDAO;
	private BoardService boardService;
	
       
    public BoardServlet() {
        super();
        boardDAO = new BoardDAO();
        boardService = new BoardServiceImpl(boardDAO);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String title = request.getParameter("title");
		String writer = request.getParameter("writer");
		String content = request.getParameter("content");
		String dateString = request.getParameter("date");
		// 2025-05-26T11:36 --> Date로 변환 필요 SimpleDateFormat
		Date date = null;
		try {
			date = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(dateString);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println("title : " + title);
		System.out.println("writer : " + writer);
		System.out.println("content : " + content);
		System.out.println("dateString : " + dateString);
		System.out.println("date : " + date);
		
		Board board = Board.builder()
							.id(UUID.randomUUID().toString())
							.title(title)
							.writer(writer)
							.content(content)
							.date(date)
							.build();
		System.out.println("board : " + board);

		boardService.insert(board);
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("" + "<html>"
				+ "<head><title>게시판</title></head>"
				+ "<body>"
				+ "<h1>게시판</h1>"
				+ "<p>제목: " + title + "</p>"
				+ "<p>작성자: " + writer + "</p>"
				+ "<p>내용: " + content + "</p>"
				+ "<p>날짜: " + dateString + "</p>"
				+ "<p>변환된 날짜: " + (date != null ? date.toString() : "날짜 변환 실패") + "</p>"
				+ "<p>게시글 정보: " + board.toString() + "</p>"
				+ "</body>"
				+ "</html>");
		
		
	}

}

















