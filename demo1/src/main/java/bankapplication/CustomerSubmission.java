package bankapplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bankapplication.account.Account;
import com.bankapplication.customer.Customer;
import com.bankapplication.logiclayer.LogicLayer;


@WebServlet("/customersubmission")
public class CustomerSubmission extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.println("success");
		String name = request.getParameter("userName");
		long mobileNo = Long.parseLong(request.getParameter("phone"));
		String branch = request.getParameter("branch");
		double amount =Double.parseDouble( request.getParameter("balance"));
		Account account = new Account();
		account.setBalance(amount);
		account.setBranch(branch);
		Customer customer = new Customer();
		customer.setMobileNo(mobileNo);
		customer.setName(name);
		List<Customer> customerList = new ArrayList<>();
		List<Account> accountList = new ArrayList<>();
		customerList.add(customer);
		accountList.add(account);
		LogicLayer logicLayer = new LogicLayer();
		Map<Integer,List<List>>tempMap = logicLayer.insertUsers(customerList, accountList);
		List<Customer>successCustomerList = logicLayer.getCustomerList(tempMap,1);
        List<Account> successAccountList = logicLayer.getAccountList(tempMap,1);
        writer.print("hi");
        writer.println(successCustomerList.toString());
        writer.println(successAccountList.toString());

	}
	
}
