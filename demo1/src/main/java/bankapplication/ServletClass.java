package bankapplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bankapplication.customer.Customer;
import com.bankapplication.logiclayer.LogicLayer;
import com.bankapplication.mapdata.MapData;


@WebServlet("/servlet")
public class ServletClass extends HttpServlet {
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		//out.println("hi");
		//request.getName();
		String value = request.getParameter("name");
		RequestDispatcher requestDispatcher;
		LogicLayer logicLayer = new LogicLayer();
		List<Customer>customerList = null;
		if(value.equalsIgnoreCase("customer")) {
			
			try {
				customerList = logicLayer.getCustomerList();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request.setAttribute("customerList",customerList);
			requestDispatcher = request.getRequestDispatcher("customer.jsp");
			requestDispatcher.forward(request, response);
		}
		else if(value.equalsIgnoreCase("account")) {
			try {
				logicLayer.retrieveUsers();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request.setAttribute("accountList", MapData.mapData.getAccountList());
			requestDispatcher = request.getRequestDispatcher("account.jsp");
			requestDispatcher.forward(request, response);
		}
		else if(value.equalsIgnoreCase("transaction")) {
			
			requestDispatcher = request.getRequestDispatcher("transaction.jsp");
			requestDispatcher.forward(request, response);
		}
		else if(value.equalsIgnoreCase("addCustomer")) {
			requestDispatcher = request.getRequestDispatcher("addCustomer.jsp");
			requestDispatcher.forward(request, response);
		}
		else {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/"));
		}
	}
}
