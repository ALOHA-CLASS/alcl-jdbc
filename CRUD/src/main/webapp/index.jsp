
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>project💻 - ALOHA CLASS🌴</title>
</head>
<body>
	<%-- [Contents] ######################################################### --%>
	
	
	<a href="<%= request.getContextPath() %>/board">목록</a>
	<form action="<%= request.getContextPath() %>/board" method="post">
		<div>
			<label for="title">제목</label>
			<input type="text" id="title" name="title" required>
		</div>
		<div>
			<label for="writer">작성자</label>
			<input type="text" id="writer" name="writer" required>
		</div>
		<div>
            <label for="content">내용</label>
            <textarea id="content" name="content" required></textarea>
         </div>
         <div>
            <label for="date">날짜</label>
            <input type="datetime-local" id="date" name="date" required>
         </div>	
		 <!-- 등록 -->
		 <div>
            <button type="submit">등록</button>
         </div>
	</form>	
	<%-- [Contents] ######################################################### --%>
</body>
</html>



