<%@ page language="java" import="java.util.*"
	contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>登录</title>

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

	<form action="checkLogin" method="post">
		<table align="center">
			<tr>
				<td><input type="radio" name="mode" value="stu"
					checked="checked">学生 <input type="radio" name="mode"
					value="tch">教师</td>
			</tr>
			<tr>
				<td>学/工号：<input type="text" name="id">
				</td>
			</tr>
			<tr>
				<td>密码：<input type="password" name="pass">
				</td>
			</tr>
			<tr>
				<td align="center"><input type="submit" value="提交"></td>
			</tr>
		</table>
	</form>
</body>
</html>
