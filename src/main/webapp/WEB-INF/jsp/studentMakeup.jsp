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

  <a id="backToAttendanceSummary" href="${context}/attendanceSummary/${sectionId}">Back to Attendance Summary</a>
  <c:if test="${updateSuccessful}">
    <br/><br/>
    <div class="alert alert-success">
        <p>Makeups Successfully Saved.</p>
    </div>
    </c:if>
  <c:if test="${nullEntry}">
      <br/><br/>
      <div class="alert alert-warning">
          <p>There was no makeup time entry to be saved.</p>
      </div>
  </c:if>
    <c:if test="${empty updateSuccessful}">
      <br/><br/>
    </c:if>


  <style>
    th, td {
      padding: 10px;
    }
    table {
      table-layout:fixed;
    }
  </style>
  <table>
    <tr><td align="right">Name:</td><td>${student.name}</td></tr>
    <tr><td align="right">WID:</td><td>${student.sisUserId}</td></tr>
  </table>

  <br/>
  <form:form id="makeupForm" modelAttribute="makeupForm" method="POST" action="${context}/studentMakeup/save">
    <c:if test="${not empty error}">
    <div class="alert alert-info">
        <p>${error}</p>
    </div>
    <br/>
    </c:if>
  
        <form:input type="hidden" id="sectionId" path="sectionId" />
        <form:input type="hidden" id="studentId" path="studentId" />

		<table class="table table-bordered">
			<thead>
				<tr>
					<th class="col-md-4">Class Date</th>
					<th class="col-md-4">Date Made Up</th>
					<th class="col-md-2">Minutes Made Up</th>
					<th class="col-md-3">Project Description</th>
					<th class="col-md-1"> &nbsp;</th>
				</tr>
			</thead>

			<tbody id="makeupTableBody">
				<c:forEach items="${makeupForm.entries}" var="makeup" varStatus="makeupLoop">
					<tr id="row-${makeupLoop.index}">
						<td>
						    <form:input type="hidden" id="id${makeupLoop.index}" path="entries[${makeupLoop.index}].makeupId" cssClass="makeupId" />
                            <div class="form-group">
                                <div class="input-group date">
                                    <form:input id="classDate${makeupLoop.index}" path="entries[${makeupLoop.index}].dateOfClass" cssClass="form-control dateOfClass"/>
                                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                                </div>
                                <form:errors cssClass="error center-block" path="entries[${makeupLoop.index}].dateOfClass" />
                            </div>
						</td>
						<td>
							<div class="form-group">
								<div class="input-group date" id="datePickerMadeup-${makeupLoop.index}">
									<form:input id="dateMadeUp${makeupLoop.index}" path="entries[${makeupLoop.index}].dateMadeUp" cssClass="form-control dateMadeUp" />
									<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
								</div>
                                <form:errors cssClass="error center-block" path="entries[${makeupLoop.index}].dateMadeUp" />
							</div>
						</td>
						<td>
						    <form:input id="minutesMadeUp${makeupLoop.index}" path="entries[${makeupLoop.index}].minutesMadeUp" cssClass="form-control minutesMadeUp" size="5" />
						    <form:errors cssClass="error center-block" path="entries[${makeupLoop.index}].minutesMadeUp" />
						</td>
                        <td>
                            <form:input path="entries[${makeupLoop.index}].projectDescription" cssClass="form-control projectDescription" size="5" />
                            <form:errors cssClass="error center-block" path="entries[${makeupLoop.index}].projectDescription" />
                        </td>
                        <td>
                            <a class="delete-button" href="#">Delete</a>
                            <form:hidden cssClass="toBeDeletedFlag" path="entries[${makeupLoop.index}].toBeDeletedFlag"/>
                        </td>
                    </tr>
				</c:forEach>
			</tbody>
		</table>
		<div>
			<input class="hovering-purple-button" type="submit" name="saveMakeup" value="Save Makeups" />
			<input id="addMakeupBtn" class="hovering-purple-button" name="addMakeup" value="Add Makeup" />
		</div>

	</form:form>
  <script src="${context}/js/moment.js"></script>
  <script src="${context}/bootstrap/js/bootstrap-datepicker.min.js"></script>
  <!-- Load Bootstrap JS -->
  <script src="${context}/bootstrap/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="${context}/js/makeUpTableHandler.js"></script>
</body>
</html>
