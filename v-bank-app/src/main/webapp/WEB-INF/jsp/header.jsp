<div id="header" >
    <span class="left">
        V-Bank Internet Banking
    </span>
    <span class="right" style="width: 49%; text-align: right; display: block; float: right">
        <c:if test="${not empty username}">
            <span>Logged in as ${username}</span>
            <a href="/logout">LOGOUT</a>
        </c:if>
    </span>
</div>
<h2>${title}</h2>
<c:if test="{not empty message}"><h3>${message}</h3></c:if>
<c:if test="{not empty param.message}"><h3>${param.message}</h3></c:if>