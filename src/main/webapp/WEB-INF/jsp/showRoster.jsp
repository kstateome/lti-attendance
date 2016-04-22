<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="html" uri="http://www.springframework.org/tags/form" %>
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
    <link rel="stylesheet" href="${context}/stylesheets/style.css"/>

    <script src="${context}/js/jquery.2.1.3.min.js"></script>
    <script src="${context}/js/moment.js"></script>

    <title>Aviation Reporting Class Roster</title>
</head>
<body>
 <form id="dayAttendance" action="${context}/singleDayAttendance" method="post">
    <div class="container">
        <div class="row">
            <c:forEach items="${rosterForm.sectionInfoList}" var="sectionInfo">
                <!-- Will have to implement sections in the future-->
                <%--<c:if test="${enrollment.key.name == 'CIS 200 A'}">--%>
                    <div class="row mainRow">
                        <div class="col-md-2">Name</div>
                        <div class="col-md-1">ID</div>
                        <div class="col-md-2">Date</div>
                        <div class="col-md-2">Minutes Missed</div>
                        <div class="col-md-2">Date Made Up</div>
                        <div class="col-md-2">% of Course Missed</div>
                    </div>
              <%--</c:if>--%>
                <c:forEach items="${sectionInfo.students}" var="student" varStatus="loop">
                    <div class="row">
                        <div class="col-md-2">
                            ${student.name}
                        </div>
                        <div class="col-md-1">
                            ${student.id}
                        </div>
                        <div class="col-md-2">
                            <label>
                                <select class="form-control no-padding no-width">
                                    <option value="Present">Present</option>
                                    <option value="Tardy">Tardy</option>
                                    <option value="Absent">Absent</option>
                                </select>
                            </label>
                        </div>
                        <div class="col-md-2" contenteditable="true">0</div>

                        <div class='col-sm-2'>
                            <label for="datepicker"></label><input type="text" id="datepicker">
                        </div>
                        <script type="text/javascript">
                            $(function() {
                                var $j = jQuery.noConflict();
                                $j("#datepicker4").datetimepicker();
                            });
                        </script>

                        <div class="col-md-2" contenteditable="true">0%</div>
                    </div>
                </c:forEach>
            </c:forEach>
        </div>
        <br>
        <button type="submit" class="btn btn-success">Save</button>
    </div>
 </form>
    <!-- Load Bootstrap JS -->
    <script src="${context}/bootstrap/js/bootstrap.min.js"></script>

</body>
</html>