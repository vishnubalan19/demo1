package bankapplication;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bankapplication.exception.CustomException;
import com.bankapplication.logiclayer.LogicLayer;

@WebServlet("/delete")

public class DeleteCustomer extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		int customerId = Integer.parseInt(request.getParameter("customerId"));
		LogicLayer logicLayer = new LogicLayer();
		try {
			logicLayer.removeCustomer(customerId);
			PrintWriter writer = response.getWriter();
			writer.println("success");
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
