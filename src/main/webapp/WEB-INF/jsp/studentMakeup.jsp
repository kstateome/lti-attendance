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

    <script type="text/javascript">
        function generateRow(currentRow) {
            return '<tr id="row-' + currentRow +'">' +
                    '<td>' +
                    '<input type="hidden" id="id' + currentRow + '" name="entries[' + currentRow + '].makeupId" />' +
                    '<div class="form-group">' +
                    '<div class="input-group date">' +
                    '<input required="true" id="classDate' + currentRow + '" name="entries[' + currentRow + '].dateOfClass" class="form-control dateOfClass" />' +
                    '<span class="input-group-addon"> <span class="glyphicon glyphicon-calendar"></span>' +
                    '</span>' +
                    '</div>' +
                    '</div>' +
                    '</td>' +
                    '<td>' +
                    '<div class="form-group">' +
                    '<div class="input-group date">' +
                    '<input id="dateMadeUp' + currentRow + '" name="entries[' + currentRow + '].dateMadeUp" class="form-control" />' +
                    '<span class="input-group-addon"> <span class="glyphicon glyphicon-calendar"></span>' +
                    '</span>' +
                    '</div>' +
                    '</div>' +
                    '</td>' +
                    '<td><input id="minutesMadeUp' + currentRow + '" name="entries[' + currentRow + '].minutesMadeUp" class="form-control" size="5" /></td>' +
                    '<td><input id="projectDescription' + currentRow + '" name="entries[' + currentRow + '].projectDescription" class="form-control" size="5" /></td>' +
                    '<td><a id="delete-' + currentRow + '" onclick=hideRow(' + currentRow + '); >Delete</a></td>' +
                    '<input id="entries' + currentRow + '.toBeDeletedFlag" name="entries[' + currentRow + '].toBeDeletedFlag" type="hidden" value="false">' +
                    '</tr>';
        }
        function hideRow(index) {
            const $rowEntry = $('#row-' + index);
            if($('#id' + index).val() !== "") {
                console.log("Hidding Row " + index);
                $rowEntry.hide();
                $('#entries' + index +'\\.toBeDeletedFlag').val("true");

                const date = moment().format('MM/DD/YYYY');
                const classDate = $('#classDate' + index);
                classDate.removeAttr("required");
                classDate.val(date);
                const dateMadeUp = $('#dateMadeUp' + index);
                dateMadeUp.val(date);
                const minutesMadeup = $('#minutesMadeUp' + index);
                minutesMadeup.val(1);
            } else {
                console.log("Removing Row " + index);
                $rowEntry.remove();
            }
        }
        $(function() {
            $('#addMakeupBtn').click(function() {
                $('#makeupTableBody')
                        .append(generateRow(largestMakeUpIndex));
                $('#delete-' + largestMakeUpIndex).click(function() {
                    rowId = $(this).attr('id').split('-')[1];
                    $('#row-' + rowId).hide();
                });
                $('#delete-' + largestMakeUpIndex)
                setLatestIndex(largestMakeUpIndex+1);
                setupDatePickers();
            });

            $('#currentDate').on("change", function(){
                var dateChange = $("<input>").attr("type", "hidden").attr("name", "changeDate");
                $(".sectionTable").hide();
                $("#waitLoading").show();
                $("#sectionSelect").append($(dateChange));
                $("#sectionSelect").submit();
            });

            $('#makeupTableBody > tr').each(function(){
                if($(this).find(".toBeDeleted").val() === "true"){
                    $(this).hide();
                }
                setupDatePickers();
            })

            function setupDatePickers(){
                $('.date').datepicker({
                    autoclose: true
                });
            }
        });

        var largestMakeUpIndex = 0;
        function setLatestIndex(latestIndex) {
            largestMakeUpIndex = latestIndex;
        }
    </script>
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
					<th>Class Date</th>
					<th>Date Made Up</th>
					<th>Minutes Made Up</th>
					<th>Project Description</th>
					<th>&nbsp;</th>
				</tr>
			</thead>

			<tbody id="makeupTableBody">
				<c:forEach items="${makeupForm.entries}" var="makeup" varStatus="makeupLoop">
					<tr id="row-${makeupLoop.index}">
						<td>
						    <form:input type="hidden" id="id${makeupLoop.index}" path="entries[${makeupLoop.index}].makeupId" />
                            <div class="form-group">
                                <div class="input-group date">
                                    <form:input id="classDate${makeupLoop.index}" path="entries[${makeupLoop.index}].dateOfClass" cssClass="form-control"/>
                                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                                </div>
                                <form:errors cssClass="error center-block" path="entries[${makeupLoop.index}].dateOfClass" />
                            </div>
						</td>
						<td>
							<div class="form-group">
								<div class="input-group date" id="datePickerMadeup-${makeupLoop.index}">
									<form:input id="dateMadeUp${makeupLoop.index}" path="entries[${makeupLoop.index}].dateMadeUp" cssClass="form-control" />
									<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
								</div>
                                <form:errors cssClass="error center-block" path="entries[${makeupLoop.index}].dateMadeUp" />
							</div>
						</td>
						<td>
						    <form:input id="minutesMadeUp${makeupLoop.index}" path="entries[${makeupLoop.index}].minutesMadeUp" cssClass="form-control" size="5" />
						    <form:errors cssClass="error center-block" path="entries[${makeupLoop.index}].minutesMadeUp" />
						</td>
                        <td>
                            <form:input path="entries[${makeupLoop.index}].projectDescription" cssClass="form-control" size="5" />
                            <form:errors cssClass="error center-block" path="entries[${makeupLoop.index}].projectDescription" />
                        </td>
                        <td>
                            <a id="delete-${makeupLoop.index}" href="#" onclick="hideRow(${makeupLoop.index})">Delete</a>
                            <form:hidden cssClass="toBeDeleted" path="entries[${makeupLoop.index}].toBeDeletedFlag"/>
                        </td>
                    </tr>
                    <c:if test="${makeupLoop.last}">
                        <script type="text/javascript">
                            setLatestIndex(${makeupLoop.index + 1});
                        </script>
                    </c:if>
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
</body>
</html>
