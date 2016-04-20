<?xml version="1.0" encoding="UTF-8" ?>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="html" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<html>
<head>
    <title>Test Page</title>
</head>
<body>
    <div>
        <table>
            <c:forEach items="${rosterForm.enrollments}" var="enrollment">
                Key = ${enrollment.key}, values =
                <c:forEach items="${enrollment.value}" var="student" varStatus="loop">
                    ${student} ${!loop.last ? ', ' : ''}
                </c:forEach><br>
            </c:forEach>
        </table>
    </div>
</body>
</html>