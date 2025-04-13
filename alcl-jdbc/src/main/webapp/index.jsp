<%@page import="DTO.Board"%>
<%@page import="java.util.List"%>
<%@page import="Service.BoardServiceImpl"%>
<%@page import="Service.BoardService"%>
<%@page import="Service.TestServiceImpl"%>
<%@page import="Service.TestService"%>
<%@page import="DTO.Test"%>
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
</body>
</html>
