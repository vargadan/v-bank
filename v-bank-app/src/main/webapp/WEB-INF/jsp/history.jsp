<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="Transaction history of account ${param.accountNo}"/>
</jsp:include>
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
<%@ include file="footer.jsp" %>
</body>
</html>