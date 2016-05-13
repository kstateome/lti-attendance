function toggleSection(val) {
    $(".sectionTable").each(function() {
        $(this).hide();
    });

    $("#"+val).show();

    $("#attendanceSummary").attr("href","attendanceSummary/"+$('#sectionId option:selected').val());
}