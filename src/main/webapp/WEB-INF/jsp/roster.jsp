<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="edu.ksu.canvas.attendance.enums.Status" %>
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


    <title>Attendance Class Roster</title>
</head>
<body>
<nav class="navbar navbar-default hidden-print">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="${context}/roster/${selectedSectionId}">K-State Attendance</a>
        </div>
        <ul class="nav navbar-nav">
            <li><a id="classSetupLink" href="${context}/classSetup/${selectedSectionId}">Setup</a></li>
            <li><a id="attendanceSummaryLink" href="${context}/attendanceSummary/${selectedSectionId}">Attendance Summary</a></li>
            <li class="active"><a id="rosterLink" href="#">Class Roster</a></li>
            <li><a id="helpLink" href="${context}/help/${selectedSectionId}">Help</a></li>
        </ul>
    </div>
</nav>
<div class="container container-adjustment">
    <form:form id="sectionSelect" modelAttribute="rosterForm" class="sectionDropdown" method="POST"
               action="${context}/roster/${selectedSectionId}/save">

        <c:if test="${not empty error}">
            <div class="alert alert-info">
                <p>${error}</p>
            </div>
        </c:if>

        <c:if test="${not empty saveSuccess}">
            <div class="alert alert-success" id="saveSuccessMessage" role="alert">Attendance successfully saved.</div>
        </c:if>

        <c:if test="${not empty deleteSuccess}">
            <div class="alert alert-success" id= deleteSuccessMessage" role="alert">Attendance successfully deleted.</div>
        </c:if>

        <c:if test="${not empty noAttendanceToDelete}">
            <div class="alert alert-warning" id= deleteErrorMessage" role="alert">No Attendance to delete.</div>
        </c:if>

        <div class="container-fluid ">
            <div class="row">
                <div class='col-sm-4'>
                    <div id="sectionSelectFormGroup" class="form-group">
                        <label for="sectionId">Section</label>
                        <form:select id="sectionId" class="form-control" path="sectionId" items="${sectionList}"
                                     itemValue="canvasSectionId" itemLabel="name"/>
                    </div>
                </div>
            </div>

            <br/>

            <div class="alert alert-warning collapse" id="futureDateWarning" role="alert">You have selected a date in
                the future.
            </div>

            <table id="dateTable">
                <tr>
                    <th><label>Day of Attendance</label></th>
                </tr>
                <tr>
                    <td>
                        <div class="row">

                            <div class='col-md-3 keep-element-above'>

                                <div class="form-group">
                                    <div class="input-group date" id="datePicker">
                                        <form:input id="currentDate" path="currentDate" cssClass="form-control"/>
                                        <fmt:formatDate value="${rosterForm.currentDate}" pattern="MM/dd/yyyy"
                                                        var="currentDateCompare"/>
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-calendar"></span>
                                    </span>
                                    </div>
                                </div>
                            </div>
                            <script type="text/javascript">
                                $(function () {
                                    var datePicker = $('#datePicker');
                                    datePicker.datepicker({
                                        autoclose: true
                                    });
                                    $('#currentDate').on("change", function () {
                                        var dateChange = $("<input>").attr("type", "hidden").attr("name", "changeDate");
                                        $(".sectionTable").hide();
                                        $("#waitLoading").show();
                                        $("#sectionSelect").append($(dateChange));
                                        $("#sectionSelect").submit();
                                    });
                                });
                            </script>
                            <div class="col-md-3 saveAttendanceButton">
                                <button id="saveAttendanceOnTop" class="hovering-purple-button" type="button" name="saveAttendance" onclick="submitRoster()">
                                    Save Attendance</button>
                            </div>
                            <div class="col-md-3 deleteAttendanceButton">
                                <a id="deleteAttendance" href="${context}/roster/${selectedSectionId}/delete" name="deleteAttendance" style="text-decoration: none" >
                                    <button  class="hovering-purple-button" type="button">Delete Attendance</button>
                                </a>
                            </div>
                            <div class="col-md-3 saveAttendanceButton">
                                <input id="saveUnassignedAsPresent" type="button" class="hovering-purple-button" onclick="saveAsPresent()"
                                        name="saveUnassignedAsPresent" value="Set Unassigned to Present"/>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </div>



        <div class="container">
            <div id="waitLoading" class="text-center" style="display: none">
                <img id="loading-image" src="${context}/img/ajax-loader.gif"
                     alt="Please wait for content to finish loading"/>
            </div>
            <c:forEach items="${rosterForm.sectionModels}" var="sectionModel" varStatus="sectionLoop">
                <c:if test="${sectionModel.canvasSectionId == selectedSectionId}">
                    <table class="table table-bordered sectionTable" id="${sectionModel.canvasSectionId}">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>WID</th>
                            <th>Status</th>
                            <c:choose>
                                <c:when test="${rosterForm.simpleAttendance}">
                                    <th>Notes</th>
                                </c:when>
                                <c:otherwise>
                                    <th>Minutes Missed</th>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach items="${sectionModel.attendances}" var="attendance" varStatus="attendanceLoop">
                            <tr>
                                <td class="studentName ${attendance.dropped ? 'dropped' : ''}">
                                    <form:input type="hidden" id="attendanceId-${attendance.attendanceStudentId}"
                                                path="sectionModels[${sectionLoop.index}].attendances[${attendanceLoop.index}].attendanceId"/>
                                    <form:input type="hidden" id="attendanceStudentId-${attendance.attendanceStudentId}"
                                                path="sectionModels[${sectionLoop.index}].attendances[${attendanceLoop.index}].attendanceStudentId"/>
                                        ${attendance.attendanceStudentName}
                                </td>
                                <td class="studentSisUserId">
                                        ${attendance.attendanceStudentSisUserId}
                                </td>
                                <td class="studentStatus">
                                    <fmt:formatDate value="${attendance.dateOfClass}" pattern="MM/dd/yyyy"
                                                    var="attendanceDate"/>
                                    <label>
                                        <form:select id="attendanceStatus-${attendance.attendanceStudentId}"
                                                     path="sectionModels[${sectionLoop.index}].attendances[${attendanceLoop.index}].status"
                                                     cssClass="attendanceStatus form-control no-padding changing-width">
                                            <form:option id="default-${attendance.attendanceStudentId}"
                                                         value="<%=Status.NA%>" title="default" label="---"/>
                                            <form:option id="present-${attendance.attendanceStudentId}"
                                                         value="<%=Status.PRESENT%>" label="Present"/>
                                            <form:option id="tardy-${attendance.attendanceStudentId}"
                                                         value="<%=Status.TARDY%>" label="Tardy"/>
                                            <form:option id="absent-${attendance.attendanceStudentId}"
                                                         value="<%=Status.ABSENT%>" label="Absent"/>
                                            <c:if test="${rosterForm.simpleAttendance}">
                                                <form:option id="absentExcused-${attendance.attendanceStudentId}"
                                                             value="<%=Status.EXCUSED%>" label="Excused"/>
                                            </c:if>
                                        </form:select>
                                    </label>
                                    <form:errors cssClass="error"
                                                 path="sectionModels[${sectionLoop.index}].attendances[${attendanceLoop.index}].status"/>
                                </td>
                                <td class="studentNotes">
                                <c:choose>
                                    <c:when test="${rosterForm.simpleAttendance}">
                                        <form:input id="notes${attendance.attendanceStudentId}" cssClass="form-control" path="sectionModels[${sectionLoop.index}].attendances[${attendanceLoop.index}].notes"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:choose>
                                            <c:when test="${attendance.status != 'PRESENT'}">
                                                <form:input id="minutesMissed${attendance.attendanceStudentId}"
                                                            path="sectionModels[${sectionLoop.index}].attendances[${attendanceLoop.index}].minutesMissed"
                                                            cssClass="form-control" size="5"/>
                                            </c:when>
                                            <c:otherwise>
                                                <form:input id="minutesMissed${attendance.attendanceStudentId}"
                                                            path="sectionModels[${sectionLoop.index}].attendances[${attendanceLoop.index}].minutesMissed"
                                                            cssClass="form-control" size="5" disabled="true"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <form:errors cssClass="error"
                                                     path="sectionModels[${sectionLoop.index}].attendances[${attendanceLoop.index}].minutesMissed"/>
                                    </c:otherwise>
                                </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>

                    </table>
                </c:if>
            </c:forEach>

            <div>
                <button id="saveAttendanceOnBottom" class="hovering-purple-button" type="button" onclick="submitRoster()" name="saveAttendance">Save Attendance</button>
            </div>
        </div>
    </form:form>
</div>

<script src="${context}/js/moment.js"></script>
<script src="${context}/bootstrap/js/bootstrap-datepicker.min.js"></script>
<!-- Load Bootstrap JS -->
<script src="${context}/bootstrap/js/bootstrap.min.js"></script>


<script type="text/javascript">
    $(function () {

        // Deal with selected section information
        val = ${selectedSectionId};
        contextPath = '${context}';
        toggleSection(val, contextPath);

        // Handle changing things with attendance statuses
        $('.attendanceStatus').change(function () {
            var attendanceId = $(this).attr('id').split('-')[1];
            var minutesMissed = $('#minutesMissed' + attendanceId);
            var notes = $('#notes' + attendanceId);
            if ($(this).val() === '<%=Status.TARDY%>') {
                minutesMissed.removeAttr('disabled');
                minutesMissed.focus();
                notes.focus();

            }
            else if ($(this).val() === '<%=Status.ABSENT%>') {
                minutesMissed.removeAttr('disabled');
                minutesMissed.focus();
                notes.focus();
                minutesMissed.val(${rosterForm.defaultMinutesPerSession});
                attendanceId.val(${rosterForm.defaultMinutesPerSession});
            }
            else {
                minutesMissed.attr('disabled', 'disabled');
                minutesMissed.val('');
                notes.focus();
            }
        });

        $(".attendanceStatus").change(function () {
            selectedIdSplit = $(this).find(':selected').attr('id').split("-");
            status = selectedIdSplit[0];
            studentId = selectedIdSplit[1];
            $('#minutesMissed' + studentId).val('');
            if (status == 'tardy' || status == 'absent') {
                $('#minutesMissed' + studentId).removeAttr('disabled');
            } else {
                $('#minutesMissed' + studentId).attr('disabled', 'true');
            }
        });

        <!-- Used to show a warning if the selected date is in the future -->
        if (moment($("#currentDate").val()).isAfter(moment())) {
            $("#futureDateWarning").show();
        }
    });

    $('#deleteAttendance').click(function () {
        return confirm('Do you want to delete this Attendance?');
    });


    $('#sectionId').change(function () {
        window.location = '${context}/roster/' + this.value;
    });

    function saveAsPresent() {
        var statuses = $("option[title='default']");
        for (var i = 0; i < statuses.length; i++) {
            statuses[i].value = "<%=Status.PRESENT%>";
            statuses[i].label = "Present";
        }
        $("<input />")
                .attr("type", "hidden")
                .attr("name", "saveAttendance")
                .attr("value", "saveAttendance")
                .appendTo("#sectionSelect");
        $('#sectionSelect').submit();
        $('#saveUnassignedAsPresent').attr("disabled", "disabled");
    }

    function submitRoster(){
        $("<input />")
                .attr("type", "hidden")
                .attr("name", "saveAttendance")
                .attr("value", "saveAttendance")
                .appendTo("#sectionSelect");
        $('#sectionSelect').submit();
        $('#saveAttendanceOnTop').attr("disabled", "disabled");
        $('#saveAttendanceOnBottom').attr("disabled", "disabled");
    }
</script>
</body>
</html>
