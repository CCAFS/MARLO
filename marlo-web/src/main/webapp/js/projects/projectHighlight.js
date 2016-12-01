var $isGlobal;

$(document).ready(function() {

  $isGlobal = $('input.isGlobal');
  // Activate the select2 plugin to the existing case studies
  addSelect2();
  // Add JQuery Calendar widget to start dates and end dates
  datePickerConfig($("form input.startDate"), $("form input.endDate"));
  // Set word limits to inputs that contains class limitWords-value, for example : <input class="limitWords-100" />
  setWordCounterToInputs('limitWords');
  // Attach Events
  attachEvents();

  // Validate justification event
  /*
   * validateEvent([ "#justification" ]);
   */

});

function attachEvents() {
  $isGlobal.on('change', isGlobalChange);
  $isGlobal.trigger('change');

  $('.fileUpload .remove').on('click', function(e) {
    var context = $(this).attr('id').split('-')[1];
    var $parent = $(this).parent();
    $parent.parent().parent().find('img').attr('src', baseURL + '/images/global/defaultImage.png');
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
  } else {
    $('div.countriesBlock').show(500);
  }
}

function addSelect2() {
  $('form select').select2();
}

function datePickerConfig($startDate,$endDate) {
  var defaultMinDateValue = $("#minDateValue").val();
  var defaultMaxDateValue = $("#maxDateValue").val();
  var minDateValue = defaultMinDateValue;
  var maxDateValue = defaultMaxDateValue;

  // Start date calendar
  maxDateValue = $endDate.val();

  // Add readonly attribute to prevent inappropriate user input
  $startDate.on('keydown', function(e) {
    e.preventDefault();
  });
  var finalMaxDate = (maxDateValue != 0) ? maxDateValue : defaultMaxDateValue;
  $startDate.datepicker({
      dateFormat: "yy-mm-dd",
      minDate: defaultMinDateValue,
      maxDate: finalMaxDate,
      changeMonth: true,
      changeYear: true,
      defaultDate: null,
      onClose: function(selectedDate) {
        if(selectedDate != "") {
          $endDate.datepicker("option", "minDate", selectedDate);
        }
      }
  });

  // End date calendar
  minDateValue = $startDate.val();

  // Add readonly attribute to prevent inappropriate user input
  $endDate.on('keydown', function(e) {
    e.preventDefault();
  });
  var finalMinDate = (minDateValue != 0) ? minDateValue : defaultMinDateValue;
  $endDate.datepicker({
      dateFormat: "yy-mm-dd",
      minDate: finalMinDate,
      maxDate: defaultMaxDateValue,
      changeMonth: true,
      changeYear: true,
      defaultDate: null,
      onClose: function(selectedDate) {
        if(selectedDate != "") {
          // $startDate.datepicker("option", "maxDate", selectedDate);
        }
      }
  });
}