$(document).ready(function() {

  console.log('Init');

  // Setting Numeric Inputs
  $('form input.currencyInput').numericInput();

  $('.currencyInput').on('keyup', function() {
    var type = $(this).classParam('type');
    var category = $(this).classParam('category');

    var $total = $('span.label-total.category-' + category);
    $total.text(getTotalByCategory(category));
    $total.animateCss('flipInX');

  });
});

/**
 * Get total FTE per category (All budget types included)
 * 
 * @param {string} category
 * @returns {number} total
 */
function getTotalByCategory(category) {
  var total = 0
  $('input.category-' + category).each(function(i,input) {
    total = total + ($(input).val() || 0);
  });
  return total;
}