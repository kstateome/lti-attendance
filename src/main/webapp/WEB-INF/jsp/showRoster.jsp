<?xml version="1.0" encoding="UTF-8" ?>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="html" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <!-- Set context path -->
    <c:set var="context" value="${pageContext.request.contextPath}" />

    <!-- Load Bootstrap JS -->
    <script src="${context}/bootstrap/js/bootstrap.js"></script>
    <!-- LOAD BOOTSTRAP -->
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap-theme.min.css"/>
    <title>Test Page</title>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <c:forEach items="${rosterForm.enrollments}" var="enrollment">
                <div class="row">
                    <div class="col-md-3">
                        Key = ${enrollment.key.courseId}
                    </div>
                </div>
                <div class="row">
                    <c:forEach items="${enrollment.value}" var="student" varStatus="loop">
                        <div class="col-md-4">
                            ${student.user.sortableName} ${!loop.last ? ', ' : ''}
                        </div>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>
    </div>
</body>
</html>