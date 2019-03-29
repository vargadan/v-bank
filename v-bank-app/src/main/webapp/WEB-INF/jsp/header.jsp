<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<head>
    <title>Logged in as ${username}</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <link rel="stylesheet" href="/css/styles.css">
</head>
<body>
<div id="header">
    <span class="left">
        V-Bank Internet Banking
    </span>
    <span class="right" style="width: 49%; text-align: right; display: block; float: right">
        <c:if test="${not empty username}">
            <span>Logged in as ${username}</span>
            <a href="javascript:document.getElementById('logout').submit()">LOGOUT</a>
            <form method="post" action="/logout" id="logout">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </c:if>
    </span>
</div>
<h2 id="title">${param.title}</h2>
<c:if test="{not empty message}"><h3>${message}</h3></c:if>
<c:if test="{not empty param.message}"><h3>${param.message}</h3></c:if>