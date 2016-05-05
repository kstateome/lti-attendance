function toggleSection(val) {
    $(".sectionTable").each(function() {
        $(this).hide();
    });

    $("#"+val).show();
}