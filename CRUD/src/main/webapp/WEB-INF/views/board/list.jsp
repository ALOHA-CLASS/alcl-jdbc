<%-- JSTL --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>project💻 - ALOHA CLASS🌴</title>
</head>
<body>
	<%-- [Contents] ######################################################### --%>
	<table border="1">
		<tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>일정</th>
            <th>등록일자</th>
            <th>수정일자</th>
        </tr>
        <c:if test="${ boardList != null && boardList.size() == 0 }">
            <tr>
                <td colspan="6">게시글이 없습니다.</td>
            </tr>
        </c:if>
        <c:forEach var="board" items="${ boardList }">
            <tr>
                <td>${board.no}</td>
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
			
	
	
	<%-- [Contents] ######################################################### --%>
</body>
</html>



