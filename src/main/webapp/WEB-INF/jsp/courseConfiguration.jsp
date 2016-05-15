<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="edu.ksu.canvas.aviation.enums.Status" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Set context path -->
    <c:set var="context" value="${pageContext.request.contextPath}"/>


    <!-- LOAD BOOTSTRAP -->
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap-theme.css"/>
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap-datepicker.min.css"/>
    <link rel="stylesheet" href="${context}/stylesheets/jquery-ui.min.css"/>
    <link rel="stylesheet" href="${context}/stylesheets/style.css"/>
    <link rel="stylesheet" href="${context}/css/buttonOverrides.css"/>

    <%--This needs to be here..--%>
    <script src="${context}/js/jquery.2.1.3.min.js"></script>
    <script src="${context}/js/jquery-ui.min.js"></script>
    <script src="${context}/js/scripts.js"></script>

    <title>Aviation Reporting Class Setup</title>
</head>
<body>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">Aviation Attendance</a>
        </div>
        <ul class="nav navbar-nav">
            <li class="active"><a id="classSetupLink" href="${context}/classSetup/${selectedSectionId}">Configuration</a></li>
            <li><a id="attendanceSummaryLink" href="${context}/attendanceSummary/${selectedSectionId}">Attendance Summary</a></li>
            <li><a id="rosterLink" href="${context}/showRoster/${selectedSectionId}">Class Roster</a></li>
        </ul>
    </div>
</nav>
    <form:form id="sectionSelect" modelAttribute="classSetupForm" class="sectionDropdown" method="POST" action="${context}/classSetup/save">
    <c:if test="${not empty error}">
    <div class="alert alert-info">
        <p>${error}</p>
    </div>
    </c:if>

    <div class="form-group">
        <div class="col-md-3">
            <label for="courseWorth">Total Class Minutes</label>
            <form:input path="totalClassMinutes" type="text" id="courseWorth" cssClass="form-control"
                        placeholder="Total Class Minutes"/>
            <form:errors cssClass="error" path="totalClassMinutes">
                Invalid/empty input
            </form:errors>
        </div>
        <div class="col-md-3">
            <label for="defaultMinutesPerSession">Normal Class Length</label>
            <form:input path="defaultMinutesPerSession" type="text" id="defaultMinutesPerSession"
                        cssClass="form-control" placeholder="Normal Class Length"/>
            <form:errors cssClass="error" path="defaultMinutesPerSession">
                Invalid/empty input
            </form:errors>
        </div>
        <div class="col-md-2">
            <label style="color:white;" for="saveClassMinutes">Save Class Minutes</label>
            <input value="Save Class Minutes" id="saveClassMinutes" name="saveClassMinutes" class="hovering-purple-button pull-right" type="submit"/>
        </div>
    </div>
    </form:form>

</body>
</html>