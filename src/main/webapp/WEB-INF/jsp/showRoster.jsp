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
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap-theme.css"/>

    <title>Aviation Reporting Class Roster</title>
</head>
<body>
    <div class="container">
        <div class="row">
            <c:forEach items="${rosterForm.enrollments}" var="enrollment">
                <!-- Will have to implement sections in the future-->
                <c:if test="${enrollment.key.name == 'CIS 200 A'}">
                    <div class="row">
                        <div class="col-md-1">Name</div>
                        <div class="col-md-1">ID</div>
                        <div class="col-md-1">Date</div>
                        <div class="col-md-1">Date</div>
                        <div class="col-md-1">Date</div>
                        <div class="col-md-1">Date</div>
                        <div class="col-md-1">Date</div>
                        <div class="col-md-1">Date</div>
                        <div class="col-md-1">Date</div>
                        <div class="col-md-1">Total</div>
                        <div class="col-md-1">Makeup</div>
                    </div>
                </c:if>
                <c:forEach items="${enrollment.value}" var="student" varStatus="loop">
                    <div class="row">
                        <div class="col-md-1">
                            ${student.user.sortableName}
                        </div>
                        <div class="col-md-1">
                            ${student.user.sisUserId}
                        </div>
                        <div class="col-md-1">
                            <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <div class="dropdown-menu">
                                    <a class="dropdown-item" href="#">Present</a>
                                    <a class="dropdown-item" href="#">Tardy</a>
                                    <a class="dropdown-item" href="#">Absent</a>
                                    <div class="dropdown-divider"></div>
                                    <a class="dropdown-item" href="#">Excused</a>
                                </div>
                            </button>
                        </div>
                        <div class="col-md-1">Present</div>
                        <div class="col-md-1">Present</div>
                        <div class="col-md-1">Present</div>
                        <div class="col-md-1">Present</div>
                        <div class="col-md-1">Present</div>
                        <div class="col-md-1">Present</div>
                        <div class="col-md-1">0</div>
                        <div class="col-md-1">1</div>

                    </div>
                </c:forEach>
            </c:forEach>
        </div>
    </div>
    <!-- Load Bootstrap JS -->
    <script src="${context}/js/jquery.2.1.3.min.js"></script>
    <script src="${context}/bootstrap/js/bootstrap.js"></script>
</body>
</html>