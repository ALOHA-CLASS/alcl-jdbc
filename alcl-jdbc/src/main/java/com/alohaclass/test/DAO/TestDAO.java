package com.alohaclass.test.DAO;

import java.sql.ResultSet;

import com.alohaclass.jdbc.dao.BaseDAOImpl;
import com.alohaclass.test.DTO.Test;

public class TestDAO extends BaseDAOImpl<Test> {

	@Override
	public Test map(ResultSet rs) throws Exception {
		Test test = new Test();
		test.setName( rs.getString("name") );
		test.setAge( rs.getInt("age") );
		return test;
	}

	@Override
	public String pk() {
		return "no";
	}

	@Override
	public String table() {
		return "test";
	}

}
