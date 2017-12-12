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

    $startDate.pickadate({
        format: dateFormat,
        max: $endDate.val() || false
    });
    $endDate.pickadate({
        format: dateFormat,
        min: $startDate.val() || false
    });
  });

}