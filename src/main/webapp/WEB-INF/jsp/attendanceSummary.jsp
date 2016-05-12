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

  <title>Aviation Reporting Attendace Summary Page</title>
</head>
<body>
<form:form id="sectionSelect" modelAttribute="rosterForm" class="sectionDropdown" method="POST" action="${context}/save">
  <label>
    <form:select class="form-control" path="sectionId" items="${sectionList}" itemValue="id"  itemLabel="name" onchange="toggleSection(value)"/>
  </label>
  <c:if test="${not empty error}">
    <div class="alert alert-info">
      <p>${error}</p>
    </div>
  </c:if>
  <br/>

  <div class="container">

    <c:forEach items="${rosterForm.sectionInfoList}" var="sectionInfo" varStatus="loop">
      <c:if test="${not empty sectionInfo.students}">
        <%--<c:set var="currentDate" value="${sectionInfo.days[0].date}"/>--%>
        <table class="table table-bordered sectionTable" style="display:none" id="${sectionInfo.sectionId}">
            <%--<div style="visibility:hidden" id="${sectionInfo.sectionId}">--%>
          <tr>
            <th>Name</th>
            <th>Current Minutes Made Up</th>
            <th>Minutes Made Up</th>
            <th>Remaining Minutes Made up</th>
            <th>Total Minutes Missed</th>
            <th>% of Course Missed</th>
          </tr>

          <c:forEach items="${sectionInfo.students}" var="aviationStudent" varStatus="loop">
            <tr >
              <td>
                  ${aviationStudent.name}
              </td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
            </tr>
          </c:forEach>
        </table>
      </c:if>
    </c:forEach>
    <div>
      <input class="hovering-purple-button" type="submit" name="saveAttendance" value="Save Attendance"/>
    </div>
  </div>
</form:form>


</body>
</html>
