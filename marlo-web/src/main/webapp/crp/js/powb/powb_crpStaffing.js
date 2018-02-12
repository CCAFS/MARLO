$(document).ready(function() {

  console.log('Init');

  // Setting Numeric Inputs
  $('form input.currencyInput').numericInput();

  $('.currencyInput').on('keyup', function() {
    console.log($(this).attr('class'));
  });
});
