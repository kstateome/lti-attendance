function toggleSection(val, contextPath) {
    $(".sectionTable").each(function() {
        $(this).hide();
    });

    $("#"+val).show();
    $("#attendanceSummaryLink").attr("href", contextPath + "/attendanceSummary/" + val);
    $("#rosterLink").attr("href", contextPath + "/roster/" + val);
    $("#classSetupLink").attr("href", contextPath + "/courseConfiguration/" + val);
}