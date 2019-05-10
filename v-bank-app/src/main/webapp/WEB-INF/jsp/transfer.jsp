<!doctype html>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<t:page title="Please make a transfer from ${param.fromAccountNo}">
    <div class="container">
        <form action="/doTransfer" method="post">
            <input id="fromAccountNo" name="fromAccountNo" type="hidden" value="${param.fromAccountNo}">
            <fieldset>
                <p>
                    <t:alertMessage message="${toAccountNoMsg}" />
                    <label for="toAccountNo">To Account:</label>
                    <input id="toAccountNo" name="toAccountNo" type="text" value="${param.toAccountNo}">
                </p>
                <p>
                    <t:alertMessage message="${amountMsg}" />
                    <label for="amount">Amount:</label>
                    <input id="amount" name="amount" type="text" value="${param.amount}">
                </p>
                <p>
                    <t:alertMessage message="${currencyMsg}" />
                    <label for="currency">Currency:</label>
                    <input id="currency" name="currency" type="text" value="${param.currency != null ? param.currency : 'CHF'}">
                </p>
                <p>
                    <t:alertMessage message="${noteMsg}" />
                    <label for="note">Note:</label>
                    <input id="note" name="note" type="text" value="${param.note}">
                </p>
                <p>
                    <input type="submit" value="send" class="button"/>
                </p>
                <sec:csrfInput />
            </fieldset>
        </form>
    </div>
    <div class="container">
        <a href="/">back</a>
    </div>
</t:page>