<%@page import="java.sql.ResultSet"%>
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
    
    <title>成绩录入</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

	<%
		Object classNo = session.getAttribute("classNo");
  		Object term = session.getAttribute("term");
  		Object course1 = session.getAttribute("course");
  		String course = new String(course1.toString().getBytes("ISO-8859-1"),"UTF-8");
  	%>
  </head>
  
  <body>  	
  	<form action="gradeServlet" method="post">
		<table align="center">
			<tr>
				<td>班号：<%=classNo %></td>
				<td>学期：<%=term %></td>
				<td>科目：<%=course %></td>
			</tr>
	<%
	DB conn = new DB();
	String sql = "select stu from class where no="+classNo;
	ResultSet rs = conn.query(sql);
	if(!rs.next())
	{
	%>
			<tr><td>无学生</td></tr>
	<%
	}
	else
	{
	%>
			<tr>
				<td><%=rs.getInt("stu") %></td>
				<td><input type="text" name="grade"></td>				
			</tr>
	<%
		while(rs.next())
		{
	%>
			<tr>
				<td><%=rs.getInt("stu") %></td>
				<td><input type="text" name="grade"></td>				
			</tr>
	<%
		}
	%>
			<tr>
				<td align="center"><input type="submit" value="提交"></td>
			</tr>
	<%
	}
	%>
		</table>
	</form>
  </body>
</html>
