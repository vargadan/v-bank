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
            <c:choose>
                <c:when test="${not empty _csrf}">
                    <a href="javascript:document.getElementById('logout').submit()">LOGOUT</a>
                    <form method="post" action="/logout" id="logout">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </c:when>
                <c:otherwise>
                    <a href="/logout">LOGOUT</a>
                </c:otherwise>
            </c:choose>
        </c:if>
    </span>
</div>
<h2 id="title">${title}</h2>
<c:if test="{not empty message}"><h3>${message}</h3></c:if>
<c:if test="{not empty param.message}"><h3>${param.message}</h3></c:if>