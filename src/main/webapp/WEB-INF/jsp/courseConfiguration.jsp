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
    <script src="${context}/bootstrap/js/bootstrap.min.js"></script>
    <script src="${context}/js/jquery.confirm.js"></script>
    <script src="${context}/js/scripts.js"></script>

    <title>Class Setup</title>
</head>
<body>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="${context}/roster/${selectedSectionId}">K-State Attendance</a>
        </div>
        <ul class="nav navbar-nav">
            <li class="active"><a id="classSetupLink"
                                  href="${context}/courseConfiguration/${selectedSectionId}">Setup</a></li>
            <li><a id="attendanceSummaryLink" href="${context}/attendanceSummary/${selectedSectionId}">Attendance
                Summary</a></li>
            <li><a id="rosterLink" href="${context}/roster/${selectedSectionId}">Class Roster</a></li>
        </ul>
    </div>
</nav>
<form:form id="sectionSelect" modelAttribute="courseConfigurationForm" class="sectionDropdown" method="POST"
           action="${context}/courseConfiguration/${selectedSectionId}/save">
    <c:forEach items="${error}" var="oneError">
        <div class="alert alert-danger">
            <p>${oneError}</p>
        </div>
    </c:forEach>
    <c:if test="${pushingSuccessful}">
        <div class="alert alert-success" id="pushingSuccessful">
            <p>Pushing attendance grades to Canvas successful.</p>
        </div>

        <div class="confirmation-modal modal fade" id = "pushConfirmation">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="hidePushingAlert()">&times;</button>
                        <h4 class="modal-title">Push Confirmation</h4>
                    </div>
                    <div class="modal-body">
                        Please allow a few minutes for Canvas to update the gradebook.
                    </div>
                    <div class="modal-footer">
                        <button class="confirm btn btn-primary" type="button" onclick="hidePushingAlert()">
                            OK
                        </button>
                    </div>
                </div>
            </div>
        </div>

    </c:if>
<!--There needs to be a message that returns a list of sections that did not successfully push grades to Canvas. It should be grouped with the following success messages. -->
    <c:if test="${updateSuccessful}">
        <div class="alert alert-success">
            <p>Course Setup successfully updated.</p>
        </div>
    </c:if>

    <c:if test="${synchronizationSuccessful}">
        <div class="alert alert-success" id="synchronizationSuccessful">
            <p>Synchronization with canvas was successful.</p>
        </div>
    </c:if>

    <c:if test="${deleteSuccessful}">
        <div class="alert alert-success" id="deleteSuccessful">
            <p>Assignment has been deleted from canvas.</p>
        </div>
    </c:if>


    <h3>Synchronization</h3>

    <p>
        For performance reasons, this application does not automatically synchronize with Canvas. If you notice missing
        students, sections,
        or other problems, please click the button below to rectify the problem. It may take several seconds for this
        operation to complete.
    </p>

    <input value="Synchronize with Canvas" id="synchronizeWithCanvas" name="synchronizeWithCanvas"
           class="hovering-purple-button" type="submit"/>
    <br/>

    <h3>Setup</h3>
    <br/>
    <div class="col-lg-3">
        <label for="simpleAttendance">
            <form:radiobutton path="simpleAttendance" id="simpleAttendance" value="true"/> Use Simple Attendance (non-minute based)
        </label>
        <br/>
        <label for="aviationAttendance">
            <form:radiobutton path="simpleAttendance" id="aviationAttendance" value="false"/> Use Minute-Based Attendance Accounting (Aviation Maintenance Management)
        </label>
    </div>

    <div class="container-fluid">
        <div id="aviationTimeConfig" class="row ${courseConfigurationForm.simpleAttendance? 'hidden' : '' }">
            <fieldset class="form-inline">
                <div class="col-md-3 col-md-offset-1">
                    <label for="courseWorth">Total Class Minutes</label>
                    <form:input path="totalClassMinutes" type="text" id="courseWorth" cssClass="form-control"
                                placeholder="Total Class Minutes" size="6"/>
                    <form:errors cssClass="error center-block" path="totalClassMinutes"/>
                </div>
                <div class="col-md-3">
                    <label for="defaultMinutesPerSession">Normal Class Length</label>
                    <form:input path="defaultMinutesPerSession" type="text" id="defaultMinutesPerSession"
                                cssClass="form-control" placeholder="Normal Class Length" size="5"/>
                    <form:errors cssClass="error center-block" path="defaultMinutesPerSession"/>
                </div>
            </fieldset>
        </div>
        <br/>
        <label for="showNotesToStudents">
            <form:checkbox path="showNotesToStudents" id="showNotesToStudents"/> Show Notes entered on Class Roster page to students
        </label>
        <br/>
            <label>
                <form:checkbox  path ="gradingOn" id="conversionConfirm"/> Convert Attendance to Assignment
            </label>
        <br/>

        <div class = "container-fluid ${courseConfigurationForm.gradingOn? '' : 'hidden'}" id="conversionConfig" >
            <br/>
            <label> NOTE: When this assignment is pushed to the gradebook, it will immediately be published. Please do not alter the assignment in the gradebook, but instead use this application to update the assignment as needed.
            </label>
            <br/>
            <div class="col-md-2 col-md-offset-0">
                <label for="assignmentName">
                    <h5><i>Assignment Name: </i></h5>
                    <form:input type = "text" path ="assignmentName" id = "assignmentName" size = "15"/>
                </label>
                <br/>
                <label for="assignmentPoints">
                    <h5><i>Total Points: </i></h5>
                    <form:input type = "text" path ="assignmentPoints" id = "assignmentPoints" size = "5"/>
                </label>
                <br/>
            </div>
            <div class="col-md-7 col-md-offset-0">
                <h5><i>Attendance Weights: </i></h5>
                <p>"Present", "Tardy", and "Excused" are possible options for attendance status.
                    Please enter the percentage of the "Total Points" that each type of status should receive.</p>

                <div class="col-md-2 col-md-offset-0">
                    <label>Present: </label>
                    <br/>
                    <label for="presentPoints">
                        <form:input type = "text" path ="presentPoints" id = "presentPoints" placeholder="100" size="7"/>
                    </label>
                </div>
                <div class="col-md-2 col-md-offset-0">
                    <label>Tardy: </label>
                    <br/>
                    <label for="tardyPoints">
                        <form:input type = "text" path ="tardyPoints" id = "tardyPoints" placeholder="0" size="7"/>
                    </label>
                </div>
                <div class="col-md-2 col-md-offset-0">
                    <label>Absent: </label>
                    <br/>
                    <label for="absentPoints">
                        <form:input type = "text" path ="absentPoints" id = "absentPoints" placeholder="0" size="7"/>
                    </label>
                </div>
                <div class="col-md-2 col-md-offset-0">
                    <label>Excused: </label>
                    <br/>
                    <label for="excusedPoints">
                        <form:input type = "text" path ="excusedPoints" id = "excusedPoints" placeholder="0" size="7"/>
                    </label>
                </div>

            </div>

        </div>

        <input value="Save Setup" id="saveCourseConfiguration" name="saveCourseConfiguration"
               class="hovering-purple-button pull-left buffer-top" type="submit"/>
        <input value="Push Assignment to Canvas" id="pushGradesToCanvas" name="pushGradesToCanvas"
               class="hovering-purple-button pull-right buffer-top ${courseConfigurationForm.gradingOn? '' : 'hidden'}" type="submit"/>
    </div>
    <hr/>
    <br/><br/>
    <script>

        $(document).ready(function(){
            $('#pushConfirmation').modal('show');
        });

        var errorMessage = "There was an error communicating with the server.";

        $('#simpleAttendance').change(function(){
            if (this.checked) {
                $('#aviationTimeConfig').addClass('hidden');
            }
        });
        $('#aviationAttendance').change(function(){
            if (this.checked) {
                $('#aviationTimeConfig').removeClass('hidden');
            }
        });

        $('#conversionConfirm').change(function(){
            if (this.checked) {
                $('#pushGradesToCanvas').removeClass('hidden');
                $('#conversionConfig').removeClass('hidden');
            } else {
                $('#pushGradesToCanvas').addClass('hidden');
                $('#conversionConfig').addClass('hidden');
                if(hasAssignmentConfiguration() == true) {
                    confirmChoice('Turning off the grading feature will delete the Attendance Assignment from Canvas. Do you want to continue?', 'Delete Assignment Confirmation');
                }
            }
        });

        function hidePushingAlert (){
            $('#pushConfirmation').modal('hide');
        }

        function confirmChoice(msg, title) {
            $.confirm({
                text: msg,
                title: title,
                cancelButton: "No",
                confirm: function() {
                    var form = $('sectionSelect');
                    form.action = "<c:url value="/courseConfiguration/${selectedSectionId}/delete"/>";
                    form.submit();
                },
                cancel: function(){
                   location.reload();
                }
            });
        }

        function hasAssignmentConfiguration() {
            if($('#assignmentName').length == 0 || $('#assignmentPoints').length == 0 ) {
                return false;
            } else {
                return true;
            }
        }


    </script>

</form:form>

</body>
</html>
