function toggleSection(val, contextPath) {
    $(".sectionTable").each(function() {
        $(this).hide();
    });

    $("#"+val).show();

    $("#attendanceSummaryLink").attr("href", contextPath + "/attendanceSummary/"+$('#sectionId option:selected').val());
    $("#rosterLink").attr("href", contextPath + "/showRoster/"+$('#sectionId option:selected').val());
    $("#classSetupLink").attr("href", contextPath + "/classSetup/"+$('#sectionId option:selected').val());
}