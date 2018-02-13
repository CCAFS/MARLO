$(document).ready(function() {

  console.log('Init');

  // Setting Numeric Inputs
  $('form input.currencyInput, input.percentageInput').numericInput();

  // Setting Currency Inputs
  $('form input.currencyInput').currencyInput();

  // Setting Percentage Inputs
  $('form input.percentageInput').percentageInput();

  $('.currencyInput').on('keyup', function() {
    var type = $(this).classParam('type');
    var category = $(this).classParam('category');
    var total = getTotalByCategory(category);

    var $totalLabel = $('span.label-total.category-' + category);

    $totalLabel.text(setCurrencyFormat(total));
    $totalLabel.animateCss('flipInX');

  });

});

/**
 * Get total budget per category (All types included)
 * 
 * @param {string} category
 * @returns {number} total
 */
function getTotalByCategory(category) {
  var total = 0
  $('input.category-' + category).each(function(i,input) {
    total = total + removeCurrencyFormat($(input).val() || "0");
  });
  return total;
}
