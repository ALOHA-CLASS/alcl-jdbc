package service;

import dao.BoardDAO;
import dto.Board;

public class BoardServiceImpl extends BaseServiceImpl<BoardDAO, Board> implements BoardService {

	public BoardServiceImpl(BoardDAO dao) {
		super(dao);
	}

}
