<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="constants.AttributeConst" %>
<%@ page import="constants.ForwardConst" %>

<c:set var="action" value="${ForwardConst.ACT_AUTH.getValue()}" />
<c:set var="command" value="${ForwardConst.CMD_LOGIN.getValue()}" />

<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
        <c:if test="${loginError}">
            <div id="flush_error">
                IDかパスワードが間違っています。
            </div>
        </c:if>
        <c:if test="${flush != null}">
            <div id="flush_success">
                <c:out value="${flush}"></c:out>
            </div>
        </c:if>
        <h2>ログイン</h2>
        <form method="POST" action="<c:url value='/?action=${action}&command=${command}' />">
            <label for="${AttributeConst.STU_CODE.getValue()}">ID</label><br />
            <input type="text" name="${AttributeConst.STU_CODE.getValue()}" value="${code}" />
            <br /><br />

            <label for="${AttributeConst.STU_PASS.getValue()}">パスワード</label><br />
            <input type="password" name="${AttributeConst.STU_PASS.getValue()}" />
            <br /><br />

            <input type="hidden" name="${AttributeConst.TOKEN.getValue()}" value="${_token}" />
            <button type="submit">ログイン</button>
        </form>
    </c:param>
</c:import>
