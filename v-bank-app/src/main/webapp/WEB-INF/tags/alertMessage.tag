<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@tag description="Overall Page template" pageEncoding="UTF-8" %>
<%@ attribute name="message" required="true" %>
<c:if test="${not empty message}">
    <div class="container">
        <div class="row alert alert-danger" role="alert"><c:out value="${message}"/></div>
    </div>
</c:if>