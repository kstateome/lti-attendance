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

function sortByDateDesc(){
    var dateArray = [];
    $('#summaryTableBody tr').has('td').each(function() {
        var arrayItem = {};
        $('td', $(this)).each(function (index, item) {
            arrayItem[index] = $(item).html();
        })
    });

        alert(dateArray.toString);
        dateArray.sort(function(oldDate, newDate){
        return new Date(newDate.date) - new Date(oldDate.date)
    });
}