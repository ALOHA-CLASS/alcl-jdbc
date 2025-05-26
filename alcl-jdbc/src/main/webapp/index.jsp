<%-- JSTL --%>
<%@page import="java.util.UUID"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
	
	// list
	BoardService boardService = new BoardServiceImpl();
    List<Board> list = boardService.list();
    pageContext.setAttribute("list", list);
    
    // board
// 	board = boardService.selectById("55432865-3174-11f0-83a9-a8a1596f255e");
// 	if( board == null ) board = new Board();

	// insertKey
	Board board = Board.builder()
			            .id(UUID.randomUUID().toString())
			            .title("제목입니다.")
			            .writer("작성자")
			            .content("내용입니다.")
			            .date(new java.util.Date())
			            .build();
	Board newBoard = boardService.insertKey(board);
	pageContext.setAttribute("newBoard", newBoard);
	
	
	
	
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
	
	<hr>
	<p>
	    <%= newBoard.toString() %>
	</p>	
	
	<hr>
	<table border="1">
		<tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>일정</th>
            <th>등록일자</th>
            <th>수정일자</th>
        </tr>
        <c:if test="${ list != null && list.size() == 0 }">
            <tr>
                <td colspan="6">게시글이 없습니다.</td>
            </tr>
        </c:if>
        <c:forEach var="board" items="${ list }">
            <tr>
                <td>${board.boardNo}</td>
                <td><a href="${pageContext.request.contextPath}/board/${board.id}">${board.title}</a></td>
                <td>${board.writer}</td>
                <td>
                	<fmt:formatDate value="${board.date}" pattern="yyyy-MM-dd HH:mm:ss" />
                </td>
                <td>${board.createdAt}</td>
                <td>${board.updatedAt}</td>
            </tr>
        </c:forEach>
	</table>
</body>
</html>
