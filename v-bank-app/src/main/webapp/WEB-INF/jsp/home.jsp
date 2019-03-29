<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<c:set var="title" value="Here are you accounts"/>
<%@ include file="header.jsp" %>
<script type="text/javascript" src="/js/jquery-3.3.1.js"></script>
<script type="text/javascript">
    function addAccountRow(accountId) {
        console.log('accountId : ' + accountId);
        $(document).ready(function () {
            $.ajax({
                url: "/api/v1/account/" + accountId
            }).then(function (data) {
                var accountRow = getAccountRowTable(data);
                $('#accountsTable  > tbody:last-child').append(accountRow);
            });
        });
    };

    function getAccountRowTable(data) {
        var accountRow = '<tr><td><a href="/history?accountNo='
            + data.accountNo + '">'
            + data.accountNo + '</a></td><td>'
            + data.balance + '</td><td>'
            + data.currency + '</td>'
            + '<td><a href="/transfer?fromAccount='
            + data.accountNo + '">TANSFER</a></td></tr>';
        return accountRow;
    }
</script>
<c:forEach var="accountId" items="${accountIds}">
    <script type="text/javascript">addAccountRow('${accountId}')</script>
</c:forEach>
</div>
<table id="accountsTable">
    <tbody>
    </tbody>
</table>
<%@ include file="footer.jsp" %>
</body>
</html>