<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Set context path -->
    <c:set var="context" value="${pageContext.request.contextPath}" />


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
    <title>Student Makeup</title>
</head>

<body>
<c:if test="${!isStudent}">
    <a id="backToAttendanceSummary" href="${context}/attendanceSummary/${sectionId}">Back to Attendance Summary</a>
</c:if>

<c:if test="${student.deleted}">
    <div class="alert alert-warning">
        <p>This student has dropped the course.</p>
    </div>
</c:if>

<style>
    th, td {
        padding: 10px;
    }
    table {
        table-layout:fixed;
    }
</style>

<br/>
<form:form id="summaryForm" modelAttribute="summaryForm">

    <form:input type="hidden" id="sectionId" path="sectionId" />
    <form:input type="hidden" id="studentId" path="studentId" />

    <h3>Student Attendance Summary</h3>

    <table class="table table-bordered">

        <thead>
        <tr>
            <th class="col-md-3">Name</th>
            <th class="col-md-3">WID</th>
            <th class="col-md-4">Total Classes Present</th>
            <th class="col-md-4">Total Classes Absent</th>
            <th class="col-md-4">Total Classes Excused</th>
            <th class="col-md-4">Total Classes Tardy</th>

        </tr>
        </thead>
        <tr>
            <td>${student.name}</td>
            <td>${student.sisUserId}</td>
            <td class="text-center">${totalPresent}</td>
            <td class="text-center">${totalMissed}</td>
            <td class="text-center">${totalExcused}</td>
            <td class="text-center">${totalTardy}</td>
        </tr>
    </table>
    <br>
    <h3>Attendance Logs</h3>

    <div class="section-dropdown row col-sm-4 form-group ">
        <label for="sectionId">Section</label>
        <form:select class="form-control" id="sectionId" path="sectionId" items="${dropDownList}"
                     itemValue="canvasSectionId" itemLabel="name"
                     onchange="toggleSection(value, '${context}');"/>
    </div>
    <br>
    <br>
    <br>

    <c:forEach items="${sectionList}" var="section" varStatus="sectionLoop">
        <table class="table table-bordered sectionTable" style="display:none" id="${section.canvasSectionId}">
            <thead>
            <tr>
                <th class="col-md-1">Class Date</th>
                <th class="col-md-1">Status</th>
                <th class="col-md-2">Notes</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach items="${studentList}" var="current" varStatus="studentLoop">
                <c:forEach items="${current.attendances}" var="attendance" varStatus="attendanceLoop">
                    <c:if test="${(current.canvasSectionId == section.canvasSectionId)}">
                        <tr>
                            <td><fmt:formatDate pattern="MM/dd/yyyy" value="${attendance.dateOfClass}"/></td>
                            <td>${attendance.status}</td>
                            <td>${attendance.notes}</td>
                        </tr>
                    </c:if>
                </c:forEach>
            </c:forEach>
            </tbody>
        </table>
    </c:forEach>


</form:form>
<script type="text/javascript">
    $(function () {
        // Update the selected section information
        val = ${dropDownList.get(0).canvasSectionId};
        contextPath = '${context}';
        console.log('value in body - ' + val);
        toggleSection(val, contextPath);
    });
</script>

<script src="${context}/js/moment.js"></script>
<!-- Load Bootstrap JS -->
<script src="${context}/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${context}/js/makeUpTableHandler.js"></script>
</body>
</html>