import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.sql.*;

@MultipartConfig
public class EditBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Edit Book</title>");
        out.println("<script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script>");
        out.println("</head><body>");

        try {
            int id = Integer.parseInt(request.getParameter("bookId"));
            Part filePart = request.getPart("bookImage");
            String fileName = filePart.getSubmittedFileName();
            String uploadPath = getServletContext().getRealPath("") + "images/books/" + fileName;

            String name = request.getParameter("bookName");
            String author = request.getParameter("bookAuthor");
            double price = Double.parseDouble(request.getParameter("bookPrice"));
            String category = request.getParameter("bookCategory");
            int stock = Integer.parseInt(request.getParameter("bookStock"));
            String description = request.getParameter("bookDescription");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "");
            
            String query;
            if (fileName.isEmpty()) {
                query = "UPDATE books SET name=?, author=?, price=?, category=?, stock=?, description=? WHERE id=?";
            } else {
                filePart.write(uploadPath);
                query = "UPDATE books SET image=?, name=?, author=?, price=?, category=?, stock=?, description=? WHERE id=?";
            }
            
            PreparedStatement ps = con.prepareStatement(query);
            
            if (!fileName.isEmpty()) {
                ps.setString(1, fileName);
                ps.setString(2, name);
                ps.setString(3, author);
                ps.setDouble(4, price);
                ps.setString(5, category);
                ps.setInt(6, stock);
                ps.setString(7, description);
                ps.setInt(8, id);
            } else {
                ps.setString(1, name);
                ps.setString(2, author);
                ps.setDouble(3, price);
                ps.setString(4, category);
                ps.setInt(5, stock);
                ps.setString(6, description);
                ps.setInt(7, id);
            }

            ps.executeUpdate();
            con.close();

            out.println("<script>");
            out.println("Swal.fire({ title: 'Updated!', text: 'Book Updated Successfully!', icon: 'success' })");
            out.println(".then(() => { window.location='publisher/manage-books.jsp'; });");
            out.println("</script>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script>");
            out.println("Swal.fire({ title: 'Error!', text: 'Failed to update book!', icon: 'error' })");
            out.println(".then(() => { window.location='publisher/manage-books.jsp'; });");
            out.println("</script>");
        }

        out.println("</body></html>");
    }
}
