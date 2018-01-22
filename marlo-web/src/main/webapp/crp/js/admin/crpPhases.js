$(document).ready(init);

function init() {

  // Set date-picker
  setDatePickers();

  /* Declaring Events */
  attachEvents();
}

function attachEvents() {
  
  $('.button-save').on('click', function(e) {
    var visiblePhases = $('input.visible-yes:checked').length;
    
    // Validate if there is a valid phase
    if(visiblePhases < 1) {
      e.preventDefault();
      
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = "Should be at least one valid phase visible";
      noty(notyOptions);
      
      // Turn off the saving button state
      turnSavingStateOff(this);
      
      return
    }

  });
}

function setDatePickers() {
  var datePickerOptions = {
      format: "mmm d, yyyy",
      formatSubmit: "yyyy-mm-dd",
      hiddenName: true,
      selectYears: true,
      selectMonths: true
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