$(document).ready(init);

function init() {
// Setting Currency Inputs
  $('.currencyInput').currencyInput();
  date("form #project\\.startDate", "form #project\\.endDate");
  $('form select').select2({
    width: "100%"
  });
}

function date(start,end) {
  var dateFormat = "yy-mm-dd", from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2015-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true
  }).on("change", function() {
    to.datepicker("option", "minDate", getDate(this));
  }), to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2015-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true
  }).on("change", function() {
    from.datepicker("option", "maxDate", getDate(this));
  });

  function getDate(element) {
    console.log(element);
    var date;
    try {
      date = $.datepicker.parseDate(dateFormat, element.value);
    } catch(error) {
      date = null;
    }

    return date;
  }
}