<!doctype html>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<<<<<<< HEAD
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<t:page>
=======
<t:page title="V-Bank Web Login">
>>>>>>> 5678176... titles
    <div class="container">
        <div class="row">
            <c:if test="${param.logout != null}">
                <div class="alert alert-info" role="alert">You have been logged out.</div>
            </c:if>
        </div>
        <div class="w-100"></div>
        <div class="row">
            <form id="loginform" action="/login" method="post">
                <sec:csrfInput />
                <div class="wrap-input">
                    <input class="input-login" id="username" type="text" name="username" placeholder="Username"/>
                    <span class="focus-input" />
                </div>
                <div class="wrap-input">
                    <input class="input-login" id="password" type="password" name="password" placeholder="Password"/>
                    <span class="focus-input" />
                </div>
                <div class="button-holder">
                    <button id="login-button" class="button" onclick="document.getElementById('loginform').submit()">Sign In</button>
                </div>
            </form>
        </div>
    </div>
</t:page>