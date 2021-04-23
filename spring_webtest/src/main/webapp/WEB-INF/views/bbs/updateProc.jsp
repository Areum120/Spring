<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/ssi/ssi_bbs.jsp"%>
<%
boolean flag = (Boolean) request.getAttribute("flag");
boolean pflag = (Boolean) request.getAttribute("pflag");
%>
<!DOCTYPE html>
<html>
<head>
<title>게시판수정</title>
<meta charset="utf-8">
</head>
<body>
	<div class="container">
		<div class="well well-lg">
			<%
			if (!pflag) {
				out.print("잘못된 비밀번호 입니다.");
			} else if (flag) {
				out.print("글 수정을 성공했습니다.");
			} else {
				out.print("글 수정을 실패했습니다.");
			}
			%>


		</div>
		<%
		if (!pflag) {
		%>
		<button class="btn" onclick="history.back()">다시시도</button>
		<%
		}
		%>
		<button class="btn" onclick="location.href='create.do'">다시등록</button>
		<button type="button" class="btn" onclick="location.href='list.do'">목록</button>

	</div>

</body>
</html>
