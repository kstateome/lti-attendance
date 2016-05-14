function toggleSection(val, contextPath) {
    $(".sectionTable").each(function() {
        $(this).hide();
    });

    $("#"+val).show();
    $("#attendanceSummaryLink").attr("href", contextPath + "/attendanceSummary/" + val);
    $("#rosterLink").attr("href", contextPath + "/showRoster/" + val);
    $("#classSetupLink").attr("href", contextPath + "/classSetup/" + val);
}