<%@ page language="java" errorPage="error.jsp" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="com.bankapplication.customer.*" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h2>Customer Details</h2>
<!-- <form action="customersubmission" method="post">
<b><label for="userName">UserName</label></b><br>
<input type="text" id="userName" name="userName"><br>
<br>
<b><label for="phone">Phone number</label></b><br>
<input type="text" id="phone" name="phone"><br>
<br>
<b><label for="branch">Branch</label></b><br>
<input type="text" id="branch" name="branch"><br>
<br>
<b><label for="balance">Initial Deposit</label></b><br>
<input type="text" id="balance" name="balance"><br>
<br>
<input type="submit" value="Submit">
<input type="reset">
</form> -->
<form action="delete" method="post">
<table>
            <thead>
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>MobileNo</th>
                </tr>
            </thead>
            
            <tbody>
			
            	<c:forEach items="${customerList}" var="val">
                <tr>
                <td><input type="checkbox" name="customerId" value="${val.customerId}">
                    <td>${val.customerId}</td>
                    <td>${val.name}</td>
                    <td>${val.mobileNo}</td>
                </tr>
                </c:forEach>
                
                
            </tbody>
        </table>
        <input type="submit" value="Submit">   
 </form>
        
</body>
</html>