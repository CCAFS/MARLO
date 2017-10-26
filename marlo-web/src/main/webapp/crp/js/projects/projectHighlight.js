var $isGlobal;

$(document).ready(function() {

  $isGlobal = $('input.isGlobal');
  // Activate the select2 plugin to the existing case studies
  addSelect2();

  // Add JQuery Calendar widget to start dates and end dates
  datePickerConfig({
      "startDate": "form input.startDate",
      "endDate": "form input.endDate",
      defaultMinDateValue: $("#minDateValue").val(),
      defaultMaxDateValue: $("#maxDateValue").val()
  });

  // Set file upload (blueimp-tmpl)
  var $uploadBlock = $('.fileUploadContainer');
  var $fileUpload = $uploadBlock.find('.upload')
  /*
   * $fileUpload.fileupload({ dataType: 'json', start: function(e) { $uploadBlock.addClass('blockLoading'); }, stop:
   * function(e) { $uploadBlock.removeClass('blockLoading'); }, done: function(e,data) { var r = data.result;
   * console.log(r); if(r.saved) { $uploadBlock.find('.textMessage .contentResult').html(r.fileFileName);
   * $uploadBlock.find('.textMessage').show(); $uploadBlock.find('.fileUpload').hide(); // Set file ID
   * $('input#fileID').val(r.fileID); } }, progressall: function(e,data) { var progress = parseInt(data.loaded /
   * data.total * 100, 10); } });
   */

  $uploadBlock.find('.removeIcon').on('click', function() {
    $uploadBlock.find('.textMessage .contentResult').html("");
    $uploadBlock.find('.textMessage').hide();
    $uploadBlock.find('.fileUpload').show();
    $('input#fileID').val('');
    $(".image img").attr("src", baseURL + "/global/images/defaultImage.png")
  });

  // Attach Events
  attachEvents();

});

function attachEvents() {
  $isGlobal.on('change', isGlobalChange);
  $isGlobal.trigger('change');

  $('.fileUpload .remove').on('click', function(e) {
    var context = $(this).attr('id').split('-')[1];
    var $parent = $(this).parent();
    $parent.parent().parent().find('img').attr('src', baseURL + '/global/images/defaultImage.png');
    var $inputFile = $('[id$=' + context + '-template]').clone(true).removeAttr("id");
    $parent.parent().parent().append(' <input type="hidden" name="highlight.photo" value="-1" /> ');
    $parent.empty().append($inputFile);
    $inputFile.hide().fadeIn('slow');
    forceChange = true;
  })
}

function isGlobalChange(e) {
  if($(e.target).is(':checked')) {
    $('div.countriesBlock').hide(500);
    $("div.countriesBlock select").select2("val", "");
    $(this).val(true);
  } else {
    $('div.countriesBlock').show(500);
    $(this).val(false);
  }
}

function addSelect2() {
  $('form select').select2();
}

/**
 * Attach to the date fields the datepicker plugin
 */
function datePickerConfig(element) {
  date($(element.startDate), $(element.endDate));
}

function date(start,end) {
  var dateFormat = "yy-mm-dd";
  var from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2010-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(end).datepicker("option", "minDate", selectedDate);
        }
      }
  });

  var to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2010-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(start).datepicker("option", "maxDate", selectedDate);
        }
      }
  });

  function getDate(element) {
    var date;
    try {
      date = $.datepicker.parseDate(dateFormat, element.value);
    } catch(error) {
      date = null;
    }

    return date;
  }
}