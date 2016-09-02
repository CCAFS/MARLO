$(document).ready(init);

function init() {

  // Setting Numeric Inputs
  $('input.currencyInput, input.percentageInput').numericInput();

  // Setting Currency Inputs
  $('input.currencyInput').currencyInput();

  // Setting Percentage Inputs
  $('input.percentageInput').percentageInput();

  // Attaching events
  attachEvents();
}

function attachEvents() {

  /**
   * General
   */
  $('.blockTitle').on('click', function() {
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow');
  });

}