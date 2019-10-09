package servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import other.DB;

/**
 * Servlet implementation class termSubmit
 */
@WebServlet("/termSubmit")
public class termSubmit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public termSubmit() {
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
		Object id = request.getSession().getAttribute("id");
		String term = request.getParameter("term");
		DB conn = new DB();
		String sql = "select course, grade from t1 where id="+id+" and term="+term;
		ResultSet rs;
		try {
			rs = conn.query(sql);
			if(!rs.next())
			{
				request.setAttribute("msg", "ÎÞ¼ÇÂ¼");
				request.getRequestDispatcher("/stu/query.jsp").forward(request, response);
			}
			else
			{
		        response.setContentType("application/vnd.ms-pdf;charset=UTF-8");            
		        response.setHeader("Content-Disposition","attachment;filename=test.pdf");
				PdfDocument pdf = new PdfDocument(new PdfWriter(response.getOutputStream()));
		        Document document = new Document(pdf);
		        Table table = new Table(2);
		        try {
					do
					{
						String course = rs.getNString("course");
						String grade = Integer.toString(rs.getInt("grade"));
						PdfFont f = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true);
						Cell cell = new Cell().add(new Paragraph(course).setFont(f));
						table.addCell(cell);
						cell = new Cell().add(new Paragraph(grade).setFont(f));
						table.addCell(cell);
					}while(rs.next());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        document.add(table);
		        document.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
