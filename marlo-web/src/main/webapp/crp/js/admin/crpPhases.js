$(document).ready(init);

function init() {

  // Set date-picker
  setDatePickers();

  /* Declaring Events */
  attachEvents();
}

function attachEvents() {
}

function setDatePickers() {
  var datePickerOptions = {
      format: "mmm d, yyyy",
      formatSubmit: "yyyy-mm-dd",
      hiddenName: true
  }

  $('.crpPhase').each(function(i,e) {
    var $startDate = $(e).find('.startDate');
    var $endDate = $(e).find('.endDate');
    var startDatePicker, endDatePicker;

    // Set date pickers
    $startDate.pickadate(datePickerOptions);
    $endDate.pickadate(datePickerOptions);

    // Instance picker component
    startDatePicker = $startDate.pickadate('picker');
    endDatePicker = $endDate.pickadate('picker');

    // Set parameters an events
    startDatePicker.set('max', endDatePicker.get());
    startDatePicker.on('close', function() {
      endDatePicker.set('min', startDatePicker.get());
    });

    endDatePicker.set('min', startDatePicker.get());
    endDatePicker.on('close', function() {
      startDatePicker.set('max', endDatePicker.get());
    })

  });
}