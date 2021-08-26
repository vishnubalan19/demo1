<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<form action="customersubmission" method="post">
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
</form>

</body>
</html>