<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang='en'>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="Please log in."/>
</jsp:include>
<c:if test="${param.error != null}">
    <div>Invalid username and password.</div>
</c:if>
<c:if test="${param.logout != null}">
    <div>You have been logged out.</div>
</c:if>
<form action="/login" method="post">
    <div><label for="username">User Name : </label><input id="username" type="text" name="username"/></div>
    <div><label for="password">Password: </label><input id="password" type="password" name="password"/></div>
    <div><input type="submit" value="Sign In"/></div>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
<%@ include file="footer.jsp" %>
</body>
</html>