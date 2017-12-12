var dateFormat;
$(document).ready(init);

function init() {

  // Set date format
  dateFormat = "yyyy-mm-dd";

  // Set date-picker
  setDatePicker();

  /* Declaring Events */
  attachEvents();
}

function attachEvents() {
}

function setDatePicker() {

  $('.crpPhase').each(function(i,e) {
    var $startDate = $(e).find('.startDate');
    var $endDate = $(e).find('.endDate');
    var startDatePicker, endDatePicker;

    // From date a picker
    $startDate.pickadate({
        format: 'mmm d, yyyy',
        formatSubmit: dateFormat,
        hiddenName: true,
        max: new Date($endDate.val()),
        onClose: function() {
          endDatePicker.set('min', startDatePicker.get());
        }
    });

    // Until date a picker
    $endDate.pickadate({
        format: 'mmm d, yyyy',
        formatSubmit: dateFormat,
        hiddenName: true,
        min: new Date($startDate.val()),
        onClose: function(context) {
          startDatePicker.set('max', endDatePicker.get());
        }
    });

    // Instance picker component
    startDatePicker = $startDate.pickadate('picker');
    endDatePicker = $endDate.pickadate('picker');

  });

}