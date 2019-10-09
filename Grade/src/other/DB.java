package other;

import java.sql.*;

public class DB 
{	
	Connection conn;
	Statement stmt;
	ResultSet rs;
	String url = "jdbc:sqlserver://localhost:1433;DatabaseName=gradeDB";
	String username = "olly";
	String password = "199811";
	
	public DB() 
	{
		try 
		{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.createStatement();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		if (conn != null)
			conn.close();
		super.finalize();
	}
	
	public ResultSet query(String sql) throws SQLException 
	{
		rs = stmt.executeQuery(sql);
		return rs;
	}

	public void update(String sql) throws SQLException 
	{
		stmt.executeUpdate(sql);
	}
}
