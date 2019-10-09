<%@ page import="java.sql.ResultSet"%>
<%@ page language="java" import="java.util.*" import="other.DB"
	contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>开始查询</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

</head>

<body>
<%
  	Object msg = request.getAttribute("msg");
  	if(msg != null)
  	{
%>
	<script type="text/javascript">alert("<%=msg%>");</script>
<%	}%>

	<form action="termSubmit" method="post">
		<table align="center">
			<tr>
				<td>学号：<%=session.getAttribute("id") %></td>
			</tr>
			<tr>
				<td>学期：<select name="term">
						<option selected value="1">1</option>
						<option value="2">2</option>
						<option value="3">3</option>
				</select></td>
				<td><input type="submit" value="确定"></td>
			</tr>
		</table>
	</form>
</body>
</html>
