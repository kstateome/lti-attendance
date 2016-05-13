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

  <a id="showRoster" href="${context}/attendanceSummary/${sectionId}">Back to Attendance Summary</a>

  <br/><br/>
  <style>
    th, td {
      padding: 10px;
    }
  </style>
  <table>
    <tr><td align="right">Name:</td><td>${student.name}</td></tr>
    <tr><td align="right">WID:</td><td>${student.sisUserId}</td></tr>
  </table>
  
  <br/>
  <form:form id="makeupTrackerForm" modelAttribute="makeupTrackerForm" method="POST" action="${context}/save">
    <c:if test="${not empty error}">
    <div class="alert alert-info">
        <p>${error}</p>
    </div>
    </c:if>
  
        <form:input type="hidden" id="sectionId" path="sectionId" />
        <form:input type="hidden" id="studentId" path="studentId" />

		<table class="table table-bordered">
			<thead>
				<tr>
					<th>Class Date</th>
					<th>Made-up Date</th>
					<th>Minutes Made-up</th>
					<th><input class="hovering-purple-button" type="submit" name="addMakeup" value="Add Makeup" /></th>
				</tr>
			</thead>

			<tbody>
				<c:forEach items="${makeupTrackerForm.entries}" var="makeupTracker" varStatus="makeupTrackerLoop">
					<tr>
						<td>
						    <form:input type="hidden" id="id${makeupTrackerLoop.index}" path="entries[${makeupTrackerLoop.index}].makeupTrackerId" />
                            <div class="form-group">
                                <div class="input-group date" id="datePickerDateOfClass${makeupTrackerLoop.index}">
                                    <form:input id="classDate${makeupTrackerLoop.index}" path="entries[${makeupTrackerLoop.index}].dateOfClass" cssClass="form-control" />
                                    <span class="input-group-addon"> <span class="glyphicon glyphicon-calendar"></span>
                                    </span>
                                </div>
						</td>
						<td>
							<div class="form-group">
								<div class="input-group date" id="datePickerMadeup${makeupTrackerLoop.index}">
									<form:input id="dateMadeup${makeupTrackerLoop.index}" path="entries[${makeupTrackerLoop.index}].dateMadeUp" cssClass="form-control" />
									<span class="input-group-addon"> <span class="glyphicon glyphicon-calendar"></span>
									</span>
								</div>
							</div>
						</td>
						<td><form:input path="entries[${makeupTrackerLoop.index}].minutesMadeUp" cssClass="form-control" size="5" /></td>
						<td><a href="${context}/deleteMakeup/${makeupTrackerForm.sectionId}/${makeupTrackerForm.studentId}/${makeupTrackerForm.entries[makeupTrackerLoop.index].makeupTrackerId}">Delete</a></td>
					</tr>
				</c:forEach>
			</tbody>

		</table>

		<div>
			<input class="hovering-purple-button" type="submit" name="saveMakeup" value="Save Makeups" />
		</div>

	</form:form>
</body>
</html>