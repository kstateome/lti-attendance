<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>LTI Session Error</title>
</head>
<body>
<h2>LTI Session Error</h2>
<p>You do not have an active session. If it has been a while since you opened this page, it may have timed out.
    Re-launching this LTI tool from Canvas should fix it. This can be done by clicking the <em>refresh</em> button in your browser.
</p>
<p>If this problem persists after a refresh, please ensure that your browser
    is not blocking any cookies from <em>lti.canvas.k-state.edu</em>.
    If your browser is set to block third party cookies, you will have to make an exception for this tool to work.
</p>
<c:if test="${not empty errorMessage}">
    <p>Detailed error message:</p>
    <p>${errorMessage}</p>
</c:if>
</body>
</html>