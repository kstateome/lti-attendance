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

    <title>Attendance Summary Page</title>
</head>
<body>
<nav class="navbar navbar-default hidden-print">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="${context}/roster/${selectedSectionId}">K-State Attendance</a>
        </div>
        <ul class="nav navbar-nav">
            <li><a id="classSetupLink" href="${context}/classSetup/${selectedSectionId}">Setup</a></li>
            <li class="active"><a id="attendanceSummaryLink" href="#">Attendance Summary</a></li>
            <li><a id="rosterLink" href="${context}/roster/${selectedSectionId}}">Class Roster</a></li>
        </ul>
    </div>
</nav>
<div class="container">

    <div class="visible-print page-header">${courseName}</div>
    <div class="sectionDropdown">
        <div class="row">
            <div class='col-sm-4'>
                <div class="form-group">
                    <label for="sectionId">Section</label>
                    <form:select class="form-control" id="sectionId" path="sectionId" items="${sectionList}"
                                 itemValue="canvasSectionId" itemLabel="name"
                                 onchange="toggleSection(value, '${context}'); false;"/>
                </div>
                <div class="form-group">
                    <button type="button" class="hidden-print hovering-purple-button" onclick="window.print();">Print Report</button>
                    <a target="_blank" class="hidden-print hovering-purple-button" href="${context}/attendanceSummary/${selectedSectionId}/csv">Export to CSV</a>
                </div>
            </div>
        </div>
    </div>
    <br/>

    <div class="container">
        <c:forEach items="${attendanceSummaryForSections}" var="summaryForSection" varStatus="loop">
            <table class="table table-bordered sectionTable" style="display:none" id="${summaryForSection.sectionId}">
                <tr>
                    <th class="text-center">Name</th>
                    <th class="text-center">Total Classes Absent</th>
                    <th class="text-center">Total Classes Tardy</th>
                </tr>

                <c:forEach items="${summaryForSection.entries}" var="attendancesummaryEntry" varStatus="loop">
                    <tr>
                        <td class="${attendancesummaryEntry.dropped ? 'dropped' : ''}">
                            <span><a href="${context}/studentSummary/${attendancesummaryEntry.sectionId}/${attendancesummaryEntry.studentId}">${attendancesummaryEntry.studentName}</a></span>
                        </td>
                        <td class="text-center">${attendancesummaryEntry.totalClassesMissed}</td>
                        <td class="text-center">${attendancesummaryEntry.totalClassesTardy}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:forEach>
    </div>

</div>
<script type="text/javascript">
    $(function () {

        // Update the selected section information
        val = ${selectedSectionId};
        contextPath = '${context}';
        console.log('value in body - ' + val);
        toggleSection(val, contextPath);

        // Deal with highlighting the danger zones
        const WARNING = 8;
        const DANGER = 10;
        $(".sectionTable > tbody > tr").each(function () {
            const percentMissedCell = $(this).find(".percentMissed");
            const percentMissed = percentMissedCell.text();
            if (percentMissed >= DANGER) {
                percentMissedCell.addClass('bg-danger');
                return;
            }
            if (percentMissed >= WARNING) {
                percentMissedCell.addClass('bg-warning');
                return;
            }
        });
    });
</script>
</body>
</html>
