package com.alohaclass.test.Service;

import java.util.List;

import com.alohaclass.jdbc.dto.PageInfo;
import com.alohaclass.test.DTO.Board;

public interface BoardService {
	
	// C.R.U.D
	public List<Board> list();
	public PageInfo<Board> page();
	public Board select(int no);
	public Board selectById(String id);
	public int insert(Board Board);
	public Board insertKey(Board Board);
	public int update(Board Board);
	public int delete(int no);

}
