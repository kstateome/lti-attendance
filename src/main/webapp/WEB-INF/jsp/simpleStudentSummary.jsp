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
            <th class="col-md-4">Total Classes Tardy</th>
            <th class="col-md-4">Total Classes Absent</th>
            <th class="col-md-4">Total Classes Excused</th>

        </tr>
        </thead>
        <tr>
            <td>${student.name}</td>
            <td>${student.sisUserId}</td>
            <td class="text-center">${totalPresentDays}</td>
            <td class="text-center">${totalAbsentDays}</td>
            <td class="text-center">${totalExcusedDays}</td>
            <td class="text-center">${totalTardyDays}</td>

        </tr>
    </table>

    <c:if test="${presentWeight != null}">
        <div class="panel-group form-div" id="accordion" role="tablist" aria-multiselectable="true">
            <div class="panel panel-default">
                <div class="panel-heading" role="tab" id="headingOne">
                    <h4 class="panel-title">
                        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne"
                           aria-expanded="false" aria-controls="collapseOne">
                            Click here to see the calculation behind your attendance grade
                        </a>
                    </h4>
                </div>
                <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
                    <div class="panel-body">
                        <hr>
                        <p>Your instructor has created an assignment based on your attendance in this class. Below is an
                            explanation of how your attendance grade is calculated. This page is adaptive and will always reflect
                            your current attendance grade in this course. If there is a discrepancy between the grade in the
                            gradebook and what is on this page, please inform your instructor.</p>
                        <br>
                        <p>(${totalPresentDays} day(s) Present) * (${presentWeight}% of ${assignmentPoints} possible points)
                            = ${totalPresentDays} * (${presentMultiplier} * ${assignmentPoints}) = ${presentDaysTimesMultiplier} * ${assignmentPoints} =
                                ${totalPresentPoints}</p>

                        <p>(${totalTardyDays} day(s) Tardy) * (${tardyWeight}% of ${assignmentPoints} possible points)
                            = ${totalTardyDays} * (${tardyMultiplier} * ${assignmentPoints}) = ${tardyDaysTimesMultiplier} * ${assignmentPoints} =
                                ${totalTardyPoints}</p>

                        <p>(${totalAbsentDays} day(s) Absent) * (${absentWeight}% of ${assignmentPoints} possible points)
                            = ${totalAbsentDays} * (${absentMultiplier} * ${assignmentPoints}) = ${absentDaysTimesMultiplier} * ${assignmentPoints} =
                                ${totalAbsentPoints}</p>

                        <p>(${totalExcusedDays} day(s) Excused) * (${excusedWeight}% of ${assignmentPoints} possible points)
                            = ${totalExcusedDays} * (${excusedMultiplier} * ${assignmentPoints}) = ${excusedDaysTimesMultiplier} * ${assignmentPoints} =
                                ${totalExcusedPoints}</p>

                        <br>
                        <p>${totalPresentPoints} + ${totalTardyPoints} + ${totalAbsentPoints} + ${totalExcusedPoints} =
                            ${sumStudentsPoints} total points earned over the course of ${totalDays} total day(s) </p>

                        <p>${sumStudentsPoints} / ${totalDays} = <em> ${studentFinalGrade} </em></p>
                        <br>
                        <p>Your final attendance grade is <strong> ${studentFinalGrade} / ${assignmentPoints} </strong></p>
                        <br>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <h3>Attendance Logs</h3>

    <table class="table table-bordered">
        <thead>
        <tr>
            <th class="col-md-1">Class Date</th>
            <th class="col-md-1">Status</th>
            <th class="col-md-1">Section</th>
            <th class="col-md-2">Notes</th>
        </tr>
        </thead>

        <tbody id="summaryTableBody">
        <c:forEach items="${studentList}" var="student" varStatus="studentLoop">
            <c:forEach items="${student.attendances}" var="attendance" varStatus="attendanceLoop">
              <c:if test="${attendance.status != 'NA'}">  
              <tr>
                    <td><fmt:formatDate pattern="MM/dd/yyyy" value="${attendance.dateOfClass}"/></td>
                    <td>${attendance.status}</td>
                    <c:forEach items="${sectionList}" var="section" varStatus="sectionLoop">
                        <c:if test="${section.canvasSectionId == student.canvasSectionId}">
                            <td>${section.name}</td>
                        </c:if>
                    </c:forEach>
                    <td>${attendance.notes}</td>
                </tr>
                </c:if>
            </c:forEach>

        </c:forEach>

        </tbody>

    </table>

</form:form>
<script src="${context}/js/moment.js"></script>
<!-- Load Bootstrap JS -->
<script src="${context}/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${context}/js/makeUpTableHandler.js"></script>
</body>
</html>