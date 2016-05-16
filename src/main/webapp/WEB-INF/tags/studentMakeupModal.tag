<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="sectionId" required="true"
             type="java.lang.String" %>
<%@ attribute name="studentId" required="true" type="java.lang.String"%>
<div id="modal-${studentId}" class="container-fluid">
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
                <th>Date Made-up</th>
                <th>Minutes Made-up</th>
                <th>Project Description</th>
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
                    <td><form:input path="entries[${makeupTrackerLoop.index}].projectDescription" cssClass="form-control" size="5" /></td>
                    <td><a href="${context}/deleteMakeup/${makeupTrackerForm.sectionId}/${makeupTrackerForm.studentId}/${makeupTrackerForm.entries[makeupTrackerLoop.index].makeupTrackerId}">Delete</a></td>
                </tr>
            </c:forEach>
            </tbody>

        </table>

        <div>
            <input class="hovering-purple-button" type="submit" name="saveMakeup" value="Save Makeups" />
        </div>

    </form:form>
</div>