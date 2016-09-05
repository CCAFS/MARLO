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
      // $('.blockContent').slideUp();
      $(this).parent().find('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow');
  });

  /**
   * W3 Bilateral Funds
   */
  $('.w3bilateralFund select').on('change', function() {
    var value = $(this).val();
    var $inputs = $(this).parents('.w3bilateralFund').find('input.currencyInput, input.percentageInput');
    $inputs.removeClass('type-w3 type-bilateral');
    if(value != "-1") {
      $inputs.addClass('type-' + value);
    }
  });

}