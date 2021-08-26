<%@ page language="java" errorPage="error.jsp" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%@ page import="com.bankapplication.account.*" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Account</title>
</head>
<body>
<h2>Account</h2>

<table>
            <thead>
                <tr>
                    <th>Id</th>
                    <th>AccountNo</th>
                    <th>Branch</th>
                    <th>Balance</th>
                </tr>
            </thead>
            
            <tbody>
                <c:forEach items="${accountList}" var="val">
                <tr>
                    <td>${val.customerId}</td>
                    <td>${val.accountNo}</td>
                    <td>${val.branch}</td>
                    <td>${val.balance}</td>
                </tr>
                </c:forEach>   
            </tbody>
        </table>

</body>
</html>