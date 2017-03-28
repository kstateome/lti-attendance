<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title></title>
</head>
<body>
<h1><spring:message code="institution.name" /> Attendance</h1>

<br/>

<p>There is a student without a recorded student ID number in Canvas. Please contact your instructor or the <a target="_blank" href="<spring:message code="institution.helpDesk.website"/>"><spring:message code="institution.name" /> IT Help Desk</a> at
    <a href="<spring:message code="institution.helpDesk.contactInfo.email" />"><spring:message code="institution.helpDesk.contactInfo.email" /></a> or <spring:message code="institution.helpDesk.contactInfo.phone" />.</p>
</body>
</html>