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

    <title>Aviation Reporting Class Roster</title>
</head>
<body onload="val = $('#sectionId option:first').val() ; toggleSection(val);">
<td class="container-fluid">
    <form:form id="sectionSelect" modelAttribute="rosterForm" class="sectionDropdown" method="POST" action="${context}/save">
    <label>
        <form:select class="form-control" path="sectionId" items="${sectionList}" itemValue="id" itemLabel="name"
                     onchange="toggleSection(value)"/>
    </label>
    <c:if test="${not empty error}">
    <div class="alert alert-info">
        <p>${error}</p>
    </div>
    </c:if>
    <br/>

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
            <input value="Save Class Minutes" name="saveClassMinutes" class="hovering-purple-button pull-right" type="submit">
        </div>
    </div>
    <br><br><br>

    <div class="container">
        <div class="row">
            <div class="col-sm-4">
                <div class="form-group">
                    <div class="input-group date" id="datePicker">
                        <form:input id="currentDate" path="currentDate" cssClass="form-control"/>
                        <fmt:formatDate value="${rosterForm.currentDate}" pattern="MM/dd/yyyy" var="currentDateCompare"/>
                        <span class="input-group-addon">
                            <span class="glyphicon glyphicon-calendar"></span>
                        </span>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="form-group">
                    <label for="dayWorth">Class Day Minutes</label>
                    <input type="text" id="dayWorth" class="form-control" />
                </div>
            </div>
            <script type="text/javascript">
                $(function () {
                    var datePicker = $('#datePicker');
                    datePicker.datepicker({
                        defaultDate: Date.now(),
                        autoclose: true
                    });
                    $('#currentDate').on("change", function(){
                        //todo
                    });
                });
            </script>
        </div>
    </div>

<div class="container">

    <c:forEach items="${rosterForm.sectionInfoList}" var="sectionInfo" varStatus="sectionLoop">
        <c:if test="${not empty sectionInfo.students}">
            <table class="table table-bordered sectionTable" style="display:none" id="${sectionInfo.sectionId}">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>WID</th>
                        <th>Current Date</th>
                        <th>Minutes Missed</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach items="${sectionInfo.students}" var="aviationStudent" varStatus="studentLoop">
                        <tr>
                            <td>
                                ${aviationStudent.name}
                            </td>
                            <td>
                                ${aviationStudent.sisUserId}
                            </td>
                            <c:forEach var="attendance" items="${aviationStudent.attendances}" varStatus="attendanceLoop">
                                <fmt:formatDate value="${attendance.dateOfClass}" pattern="MM/dd/yyyy" var="attendanceDate"/>
                                <c:if test="${aviationStudent.studentId == attendance.aviationStudent.studentId && currentDateCompare eq attendanceDate}">
                                    <td>
                                        <label>
                                            <form:select path="sectionInfoList[${sectionLoop.index}].students[${studentLoop.index}].attendances[${attendanceLoop.index}].status"
                                                         cssClass="form-control no-padding no-width">
                                                <form:option value="<%=Status.PRESENT%>">Present</form:option>
                                                <form:option value="<%=Status.TARDY%>">Tardy</form:option>
                                                <form:option value="<%=Status.ABSENT%>">Absent</form:option>
                                            </form:select>
                                        </label>
                                    </td>

                                    <td>
                                        <form:input path="sectionInfoList[${sectionLoop.index}].students[${studentLoop.index}].attendances[${attendanceLoop.index}].minutesMissed"
                                                    cssClass="form-control" size="5"/>
                                    </td>
                                </c:if>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </tbody>

            </table>
        </c:if>
    </c:forEach>

    <div>
        <input class="hovering-purple-button" type="submit" name="saveAttendance" value="Save Attendance"/>
    </div>
    </div>
    </form:form>

</
>
</div>
<script src="${context}/js/moment.js"></script>
<script src="${context}/bootstrap/js/bootstrap-datepicker.min.js"></script>
<!-- Load Bootstrap JS -->
<script src="${context}/bootstrap/js/bootstrap.min.js"></script>

</body>
</html>