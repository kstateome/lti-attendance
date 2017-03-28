<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="selectedSectionId" required="true" %>
<%@ attribute name="context" required="true" %>
<%@ attribute name="activeLink" required="true" %>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="${context}/roster/${selectedSectionId}"><spring:message code="institution.name" /> Attendance</a>
        </div>
        <ul class="nav navbar-nav">
            <li <c:if test="${activeLink eq 'setup'}">class="active"</c:if>>
                <a id="classSetupLink" href="${context}/courseConfiguration/${selectedSectionId}">Setup</a>
            </li>
            <li <c:if test="${activeLink eq 'summary'}">class="active"</c:if>>
                <a id="attendanceSummaryLink" href="${context}/attendanceSummary/${selectedSectionId}">Attendance Summary</a>
            </li>
            <li <c:if test="${activeLink eq 'roster'}">class="active"</c:if>>
                <a id="rosterLink" href="${context}/roster/${selectedSectionId}">Class Roster</a>
            </li>
        </ul>
    </div>
</nav>