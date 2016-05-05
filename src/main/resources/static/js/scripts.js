function toggleSection(val) {
    $(".hideme").each(function() {
        $(this).hide();
    });

    $("#"+val).show();
}