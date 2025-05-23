package com.alohaclass.test.Service;

import java.util.List;

import com.alohaclass.jdbc.dto.PageInfo;
import com.alohaclass.test.DTO.Test;

public interface TestService {
	
	// C.R.U.D
	public List<Test> list();
	public PageInfo<Test> page();
	public Test select(int no);
	public int insert(Test test);
	public Test insertKey(Test test);
	public int update(Test test);
	public int delete(int no);

}










