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

    <c:set var="context" value="${pageContext.request.contextPath}"/>

    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap-theme.css"/>
    <link rel="stylesheet" href="${context}/bootstrap/css/bootstrap-datepicker.min.css"/>
    <link rel="stylesheet" href="${context}/stylesheets/jquery-ui.min.css"/>
    <link rel="stylesheet" href="${context}/stylesheets/style.css"/>
    <link rel="stylesheet" href="${context}/css/buttonOverrides.css"/>

    <script src="${context}/js/jquery.2.1.3.min.js"></script>
    <script src="${context}/js/jquery-ui.min.js"></script>
    <script src="${context}/bootstrap/js/bootstrap.min.js"></script>
    <script src="${context}/js/jquery.confirm.js"></script>
    <script src="${context}/js/scripts.js"></script>
    <title>Help</title>
</head>
<body>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="${context}/roster/${selectedSectionId}">K-State Attendance</a>
        </div>
        <ul class="nav navbar-nav">
            <li><a id="classSetupLink" href="${context}/courseConfiguration/${selectedSectionId}">Setup</a></li>
            <li><a id="attendanceSummaryLink" href="${context}/attendanceSummary/${selectedSectionId}">Attendance
                Summary</a></li>
            <li><a id="rosterLink" href="${context}/roster/${selectedSectionId}">Class Roster</a></li>
            <li class="active"><a id="helpLink" href="${context}/help/${selectedSectionId}">Help</a></li>
        </ul>
    </div>
</nav>

<h1>Start Up Guide for K-State Attendance</h1>
<hr>

<div class="panel-group form-div" id="accordion" role="tablist" aria-multiselectable="true">
    <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingOne">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne"
                   aria-expanded="false" aria-controls="collapseOne">
                Saving a Setup
                </a>
            </h4>
        </div>
        <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
            <div class="panel-body">
                <img src="${context}/img/Setup.png">
                <hr>
                <p>Before you can push a section's attendance to the Canvas gradebook, a Setup must be associated with
                the section. The Setup consists of the three fields that open up when the "Convert Attendance to
                Assignment" box is checked: the <em>Assignment Name </em>, the <em>Total Points</em>, and the
                <em>Attendance Weights</em> (pictured above).</p>
                <p><strong>NOTE:</strong> If you change any of the required fields on the Setup page, you will need to
                save the setup again before pushing to the Canvas Gradebook.</p>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingTwo">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"
                   aria-expanded="false" aria-controls="collapseTwo">
                    Pushing Grades
                </a>
            </h4>
        </div>
        <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
            <div class="panel-body">
                <img src="${context}/img/PushModal.png">
                <p>Once you've saved a Setup, the next step is to push your Attendance records to Canvas and convert
                them into an assignment.</p>
                <p>After you click "OK", you will be redirected back to the Course Setup page. Depending on the size of
                your class, it may take upwards of 30 minutes before Canvas has received all of your information. You
                may close your browser or visit other websites without compromising the grades you're pushing. Please
                keep this delay in mind if information in the "Grades" and "Assignments" tabs in Canvas is not completely
                up to date.</p>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingThree">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseThree"
                   aria-expanded="false" aria-controls="collapseThree">
                    Editing and Deleting Assignments
                </a>
            </h4>
        </div>
        <div id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingThree">
            <div class="panel-body">
                <p><strong>DO NOT EDIT OR DELETE THE ATTENDANCE ASSIGNMENT FROM THE ASSIGNMENTS TAB IN CANVAS.</strong> </p>
                <br>
                <h4>Editing: </h4>
                <p>If you'd like to change the assignment name, the total points, or the weight of any particular
                attendance status, make all of your changes through the K-State Attendance application. Make the desired
                changes by creating a new Setup on the Course Setup page then saving the setup for the appropriate
                section.</p>
                <br>
                <h4>Deleting: </h4>
                <p>Deleting an attendance assignment is done by un-checking the "Convert Attendance to Assignment" checkbox.
                The dialog pictured below will pop up and answering "Yes" will remove all saved Setups and all attendance
                assignments. Please do not delete the assignment through the Canvas Assignments tab, instead using this tool.
                This will <strong>NOT</strong> have any affect on the attendance records you have saved in the course.</p>
                <br>
                <p><strong>NOTE:</strong> If you receive an error after recently editing or deleting an assignment, please send us an email including the
                URL of the error page in the body of the email.</p>

            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingFour">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFour" aria-expanded="false" aria-controls="collapseFour">
                    Understanding <em>Attendance Weights</em>
                </a>
            </h4>
        </div>
        <div id="collapseFour" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingFour">
            <div class="panel-body">
                <img src="${context}/img/WeightsExample.png">
                <hr>
                <p>This portion of the Setup is what determines the worth of each attendance status. It is important to
                understand how this is calculated before grading students.</p>
                <p>Take the Setup pictured above for example. Assume that the attendance assignment is worth 50 points
                total. According to the setup above, if a student is "Present" for 10 days, "Absent" for 5, and "Excused"
                for 3, they will receive a grade of 31.94 out of 50. Let's run through the math:</p>
                <br>
                <p>(10 days "Present") * (<strong>100%</strong> of 50 possible points) = 10 * (<strong>1.00</strong> * 50) = 10 * 50 = 500</p>
                <p>(5 days "Absent") * (<strong>0%</strong> of 50 possible points) = 5 * (<strong>0.00</strong> * 50) = 0 * 50 = 0</p>
                <p>(3 days "Excused") * (<strong>50%</strong> of 50 possible points) = 3 * (<strong>0.50</strong> * 50) = 3 * 25 = 75</p>
                <br>
                <p>500 + 0 + 75 = 575 total points earned over the course of 18 total days </p>
                <p>575/18 = <em><strong>31.94</strong></em>, which is the final grade given to the student.</p>
                <br>
                <p>When you change the <em>Attendance Weights</em> values for each status, know that it is the bolded percentages
                above that you are changing. Visiting a student's summary page will show you how many of each status the
                student has received, so if you feel that there is an error in the gradebook, be sure to use the equations
                provided above to check the validity of the grade.</p>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingFive">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFive" aria-expanded="false" aria-controls="collapseFive">
                What To Expect In "Assignments" and "Grades"
                </a>
            </h4>
        </div>
        <div id="collapseFive" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingFive">
            <div class="panel-body">
                <p>After pushing grades out to Canvas you will have one Attendance Assignment. By default, this assignment
                is muted and assigned to every student.Though it is viewable in the Assignments tab, <strong>DO
                NOT</strong> edit the assignments within the Assignments tab, instead using the Attendance tool.</p>
                <p>The Grades tab in Canvas will display all attendance assignments as regular assignments that have been
                assigned only to their relative sections.
            </div>
        </div>
    </div>




</div>

</body>
</html>
