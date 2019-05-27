<!doctype html>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:page title="Transaction history of ${param.accountNo}">
<table class="table table-sm">
    <thead>
    <tr>
        <th scope="col">From Account</th>
        <th scope="col">To Account</th>
        <th scope="col">Amount</th>
        <th scope="col">Currency</th>
        <th scope="col">Executed</th>
        <th scope="col">Note</th>
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
            <td><c:out value="${transaction.fromAccountNo}" escapeXml="true"/></td>
            <td><c:out value="${transaction.toAccountNo}" escapeXml="true"/></td>
            <td><c:out value="${transaction.amount}" escapeXml="true"/></td>
            <td><c:out value="${transaction.currency}" escapeXml="true"/></td>
            <td><c:out value="${transaction.executed}" escapeXml="true"/></td>
            <td><c:out value="${transaction.note}" escapeXml="true"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<a href="/">back</a>
</t:page>