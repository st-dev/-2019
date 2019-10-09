package servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import other.DB;

/**
 * Servlet implementation class gradeServlet
 */
@WebServlet("/gradeServlet")
public class gradeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public gradeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Object classNo = request.getSession().getAttribute("classNo");
  		Object term = request.getSession().getAttribute("term");
  		Object course1 = request.getSession().getAttribute("course");
  		String course = new String(course1.toString().getBytes("ISO-8859-1"),"UTF-8");
  		String[] grades = request.getParameterValues("grade");
  		
  		DB getstu = new DB();
  		DB write = new DB();
  		String sql = "select stu from class where no="+classNo;
  		try{
	  		ResultSet rs = getstu.query(sql);
	  		int i = 0;
	  		while(rs.next())
	  		{
	  			String stu = String.valueOf(rs.getInt("stu"));
	  			String updatesql = "insert into t1 (id,term,course,grade) values("+stu+","+term+",'"+course+"',"+grades[i]+")";
	  			i++;
	  			write.update(updatesql);
	  		}
  		}catch(SQLException e){e.printStackTrace();}
  		request.setAttribute("msg", "Â¼Èë³É¹¦");
		request.getRequestDispatcher("/teacher/index.jsp").forward(request, response);
	}

}
