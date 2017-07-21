function toggleSection(val, contextPath) {
    $(".sectionTable").each(function() {
        $(this).hide();
    });

    $("#"+val).show();
    $("#attendanceSummaryLink").attr("href", contextPath + "/attendanceSummary/" + val);
    $("#rosterLink").attr("href", contextPath + "/roster/" + val);
    $("#classSetupLink").attr("href", contextPath + "/courseConfiguration/" + val);
    $("#sectionSelect").attr("action", contextPath + "/roster/" + val + "/save");
}

function disablePushConfirmation(){
    $('#pushConfirmation')
        .attr("disabled", "disabled")
        .removeClass("hovering-purple-button pull-right buffer-top")
        .addClass("button_disabled pull-right buffer-top")
}