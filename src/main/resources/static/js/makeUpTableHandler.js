"use strict";
$(function() {
    const $date = moment().format('MM/DD/YYYY');

    //hide persisted makeup to be deleted on failed validation
    $('#makeupTableBody > tr').each(function(){
        if($(this).find(".toBeDeletedFlag").val() === "true"){
            $(this).hide();
        }
        //setup datepickers for already persisted rows
        setupDatePickers();
    });

    //for hiding already saved makeup or removing unsaved makeup entry
    $('#makeupTableBody').on('click','.delete-button',function(){
        const $rowEntry = $(this).closest('tr');
        const rowIndex =  $rowEntry.index();
        console.log($rowEntry.find('.makeupId').val());
        console.log("Hiding row " + rowIndex);
        $rowEntry.hide();
        $rowEntry.find('.toBeDeletedFlag').val(true);
        const $classDate = $rowEntry.find('.dateOfClass');
        $classDate.removeAttr("required");
        $classDate.val($date);
        const $dateMadeUp = $rowEntry.find('.dateMadeUp');
        $dateMadeUp.val($date);
        const $minutesMadeup = $rowEntry.find('.minutesMadeUp');
        $minutesMadeup.val(1);
    });

    //creating new row
    $('#addMakeupBtn').click(function() {
        //reusable bits
        const index = $('#makeupTableBody tr').length;
        const namePrefix = "entries[" + index + "].";

        //creating input elements
        const $makeUpId = $("<input>", {type: "hidden", name: namePrefix + "makeupId"});
        const $classDate = $("<input>", {type: "text", name: namePrefix + "dateOfClass",
            class:"form-control dateOfClass", required: "true"});
        const $dateMadeUp = $("<input>", {type: "text", name: namePrefix + "dateMadeUp",
            class:"form-control"});
        const $minutesMadeUp = $("<input>", {type: "text", name: namePrefix + "minutesMadeUp",
            class:"form-control minutesMadeUp", size: "5"});
        const $projectDesc = $("<input>", {type: "text", name: namePrefix + "projectDescription",
            class:"form-control", size: "5"});
        const $toBeDeletedFlag = $("<input>", {type: "hidden", name: namePrefix + "toBeDeletedFlag",
            class:"toBeDeletedFlag", value: "false"});

        //creating delete row link
        const $delete = $("<a>", {class: "delete-button", href: "#"}).text("Delete");

        //creating tds
        const $classDateTD = $("<td>");
        const $makeUpTD = $("<td>");
        const $minutesMadeUpTD = $("<td>");
        const $projectDescTD = $("<td>");
        const $deleteTD = $("<td>");

        //creating row
        const $newRow = $("<tr>", {id: "row-" + index});

        //appending inputs to tds
        $classDateTD
            .append($makeUpId)
            .append(createDatePicker($classDate));
        $makeUpTD.append(createDatePicker($dateMadeUp));
        $minutesMadeUpTD.append($minutesMadeUp);
        $projectDescTD.append($projectDesc);
        $deleteTD
            .append($delete)
            .append($toBeDeletedFlag);

        //appending tds to new row
        $newRow
            .append($classDateTD)
            .append($makeUpTD)
            .append($minutesMadeUpTD)
            .append($projectDescTD)
            .append($deleteTD);

        //appending row to table
        $('#makeupTableBody:last-child').append($newRow);

        //setup datepickers for date picker of the new column
        setupDatePickers();
    });

    //creates the datePicker div
    function createDatePicker(element){
        const $formGroupDiv = $("<div>", {class: "form-group"});
        const $inputGroupDiv = $("<div>", {class: "input-group date"});
        $formGroupDiv.append(
            $inputGroupDiv.append(element)
                .append("<span class='input-group-addon'><span class='glyphicon glyphicon-calendar'></span></span>")
        )
        return $formGroupDiv;
    }
    function setupDatePickers(){
        $('.date').datepicker({
            autoclose: true
        });
    }
});