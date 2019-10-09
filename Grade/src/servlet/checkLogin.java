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
 * Servlet implementation class checkLogin
 */
@WebServlet("/checkLogin")
public class checkLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

    DB conn = new DB();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public checkLogin() {
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
		String mode = request.getParameter("mode");
		String id = request.getParameter("id");
		String pass = request.getParameter("pass");
		String sql = null;
		String dest = null;
		if(mode.equals("tch"))
		{
			sql = "select pass from teacher where id = " + id;
			dest = "/Grade/teacher/index.jsp";
		}
		else
		{
			sql = "select pass from stu where id = " + id;
			dest = "/Grade/stu/query.jsp";
		}
		boolean ok = false;
		if(!id.isEmpty() && !pass.isEmpty())
		{
			try {
				ResultSet rs = conn.query(sql);
				if(rs.next())
					if(rs.getString("pass").trim().equals(pass))
						ok = true;
			} catch (SQLException e) {e.printStackTrace();}
		}
		if(!ok)
		{
			request.setAttribute("msg", "µÇÂ½Ê§°Ü");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
		else
		{
			request.getSession().setAttribute("id", id);
			response.sendRedirect(dest);
		}
	}

}
