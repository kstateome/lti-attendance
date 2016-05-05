<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="${context}/stylesheets/jquery-ui.min.css"/>
    <link rel="stylesheet" href="${context}/stylesheets/style.css"/>
    <link rel="stylesheet" href="${context}/css/buttonOverrides.css"/>

    <%--This needs to be here..--%>
    <script src="${context}/js/jquery.2.1.3.min.js"></script>
    <script src="${context}/js/jquery-ui.min.js"></script>
    <script src="${context}/js/scripts.js"></script>

    <title>Aviation Reporting Class Roster</title>
</head>
<body onload="val = $('#sectionId option:first').val() ; toggleSection(val);">
<form:form id="sectionSelect" modelAttribute="rosterForm" method="POST" action="${context}/selectSectionDropdown">
    <label>
        <form:select path="sectionId" items="${sectionList}" itemValue="id" itemLabel="name" onchange="toggleSection(value)"/>
    </label>
</form:form>
<form:form action="${context}/saveAttendance" method="POST">

    <div class="container">
        <div class="row">
            <div class='col-sm-6'>
                <div class="form-group">
                    <div class='input-group date' id='datetimepicker5'>
                        <input type='text' class="form-control" />
                    <span class="input-group-addon">
                        <span class="glyphicon glyphicon-calendar"></span>
                    </span>
                    </div>
                </div>
            </div>
            <script type="text/javascript">
                $(function () {
                    $('#datetimepicker5').datetimepicker({
                        defaultDate: "11/1/2013"

                    });
                });
            </script>
        </div>
    </div>

    <div class="container">

            <c:forEach items="${rosterForm.sectionInfoList}" var="sectionInfo" varStatus="loop">
                <c:if test="${not empty sectionInfo.students}">
                    <%--<c:set var="currentDate" value="${sectionInfo.days[0].date}"/>--%>
                <table class="table table-bordered sectionTable" style="display:none" id="${sectionInfo.sectionId}">
                    <%--<div style="visibility:hidden" id="${sectionInfo.sectionId}">--%>
                        <tr>
                            <th>Name</th>
                            <th>WID</th>
                            <th>Current Date</th>
                            <th>Minutes Missed</th>
                            <th>Date Made Up</th>
                            <th>% of Course Missed</th>
                        </tr>

                        <c:forEach items="${sectionInfo.students}" var="student" varStatus="loop">
                            <tr >
                                <td>
                                        ${student.name}
                                </td>
                                <td>
                                        ${student.studentId}
                                </td>
                                <td>
                                    <label>
                                        <select class="form-control no-padding no-width">
                                            <option value="Present">Present</option>
                                            <option value="Tardy">Tardy</option>
                                            <option value="Absent">Absent</option>
                                        </select>
                                    </label>
                                </td>
                                    <%--<div class="col-md-2" contenteditable="true">
                                        <c:forEach items="${sectionInfo.days}" var="day">
                                            <c:forEach items="${day.attendances}" var="attendance">
                                                <c:if test="${student.id == attendance.id && day.date == currentDate}">
                                                    ${attendance.minutesMissed}
                                                </c:if>
                                            </c:forEach>
                                        </c:forEach>
                                    </div>
                                    <div class='col-sm-2'>
                                        <c:forEach items="${sectionInfo.days}" var="day">
                                            <c:forEach items="${day.attendances}" var="attendance">
                                                <c:if test="${student.id == attendance.id && day.date == currentDate}">
                                                    <c:if test="${attendance.dateOfClass != null } ">
                                                        <div class='col-sm-2' contenteditable="true" for="datetimepicker4" class="form-control datetimepicker4" id='datetimepicker4'>
                                                            ${attendance.dateOfClass}
                                                        </div>
                                                    </c:if>
                                                    <c:if test="${attendance.dateOfClass == null } ">
                                                        <label for="datetimepicker4"></label><input type='text' class="form-control datetimepicker4" id='datetimepicker4' value=""/>
                                                    </c:if>
                                                </c:if>
                                            </c:forEach>
                                        </c:forEach>
                                    </div>
                                    <div class="col-md-2" contenteditable="true">
                                        <c:forEach items="${sectionInfo.days}" var="day">
                                            <c:forEach items="${day.attendances}" var="attendance">
                                                <c:if test="${student.id == attendance.id && day.date == currentDate}">
                                                    ${attendance.percentageMissed}
                                                </c:if>
                                            </c:forEach>
                                        </c:forEach>
                                    </div>--%>
                                <script type="text/javascript">
                                    $(function() {
                                        $('input.datetimepicker4').datetimepicker();
                                    });
                                </script>
                            </tr>
                        </c:forEach>
                </table>
                </c:if>
            </c:forEach>

        <div>
            <input class="hovering-purple-button" type="submit" value="Save Attendance"/>
        </div>
    </div>

</form:form>

<script src="${context}/js/moment.js"></script>
<script src="${context}/bootstrap/js/bootstrap-datetimepicker.js"></script>
<!-- Load Bootstrap JS -->
<script src="${context}/bootstrap/js/bootstrap.min.js"></script>

</body>
</html>