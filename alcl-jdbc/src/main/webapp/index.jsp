<%@page import="java.util.ArrayList"%>
<%@page import="com.alohaclass.jdbc.dto.PageInfo"%>
<%@page import="com.alohaclass.jdbc.dto.Page"%>
<%@page import="com.alohaclass.test.DAO.BoardDAO"%>
<%@page import="com.alohaclass.test.DTO.Board"%>
<%@page import="java.util.List"%>
<%@page import="com.alohaclass.test.Service.BoardServiceImpl"%>
<%@page import="com.alohaclass.test.Service.BoardService"%>
<%@page import="com.alohaclass.test.Service.TestServiceImpl"%>
<%@page import="com.alohaclass.test.Service.TestService"%>
<%@page import="com.alohaclass.test.DTO.Test"%>
<%@page import="com.alohaclass.jdbc.config.Config"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	Test test= new Test();	
	test.setNo(1);
	test.setName("aloha");
	test.setAge(20);
	test.setMainTitle("12321312321321수정....");
	
	TestService testService = new TestServiceImpl();
	int result = testService.update(test);
	
	//
	Test test2= new Test();	
	test2.setName("aloha");
	test2.setAge(20);
	test2.setMainTitle("제목 입니다....");
	
	
	test2 = testService.insertKey(test2);
	
	// Board
	BoardService boardService = new BoardServiceImpl();
    List<Board> list = boardService.list();
    Board board = boardService.select(1);
    
	board = boardService.selectById("55432865-3174-11f0-83a9-a8a1596f255e");
	if( board == null ) board = new Board();
	
	
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title></title>
</head>
<body>
	<h1>alcl-jdbcl 라이브러리</h1>
	<h3>mapUnderscoreToCamelCase : <%= Config.mapUnderscoreToCamelCase %></h3>
	<h3>mapCamelCaseToUnderscore : <%= Config.mapCamelCaseToUnderscore %></h3>
	<h3>Auto Commit : <%= Config.autoCommit %></h3>
	<h3>result : <%= result %></h3>	
	<p>
		<%= test2.toString() %>
	</p>
	<p>
		<%= board.toString() %>
	</p>
</body>
</html>
