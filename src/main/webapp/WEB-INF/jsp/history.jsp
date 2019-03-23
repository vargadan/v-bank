<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
    <title>Logged in as ${username}</title>
    <style>
        table, th, td {
            border: 1px solid black;
        }
    </style>
</head>
<body>
<h1 id="header">Transaction history of account ${param.accountNo}</h1>
<table>
    <thead>
    <tr>
        <th>From Account</th>
        <th>To Account</th>
        <th>Amount</th>
        <th>Currency</th>
        <th>Pending</th>
        <th>Note</th>
    </tr>
    </thead>
    <tbody>
    <c:if test="${empty transactions}">
    <tr>
        <td colspan="2">No Transactions Available</td>
    </tr>
    </c:if>
    <c:forEach var="transaction" items="${transactions}">
        <tr>
            <td>${transaction.fromAccount}</td>
            <td>${transaction.toAccount}</td>
            <td>${transaction.amount}</td>
            <td>${transaction.currency}</td>
            <td>${not transaction.executed}</td>
            <td>${transaction.note}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<a href="/">back home</a>
</body>
</html>