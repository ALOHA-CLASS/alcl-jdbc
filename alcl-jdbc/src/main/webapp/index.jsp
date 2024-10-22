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
	test.setMainTitle("11111");
	
	TestService testService = new TestServiceImpl();
	int result = testService.update(test);
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
	<h3>result : <%= result %></h3>	
</body>
</html>