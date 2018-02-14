$(document).ready(function() {

  console.log('Init');

  // Setting Numeric Inputs
  $('form input.currencyInput').numericInput();

  $('.currencyInput').on('keyup', function() {
    var type = $(this).classParam('type');
    var category = $(this).classParam('category');
    var total = getTotalByCategory(category);
    var totalFemale = getTotalByCategoryAndType(category, "female");
    var percFemale = ((totalFemale / total) * 100);
    if(isNaN(percFemale)) {
      percFemale = 0;
    }

    var $totalLabel = $('span.label-total.category-' + category);
    var $percFemaleLabel = $('span.label-percFemale.category-' + category);

    $totalLabel.text(total.toFixed(1));
    $totalLabel.animateCss('flipInX');

    $percFemaleLabel.text(percFemale.toFixed(1));
    $percFemaleLabel.animateCss('flipInX');

  });
});

/**
 * Get total FTE per category (All types included)
 * 
 * @param {string} category
 * @returns {number} total
 */
function getTotalByCategory(category) {
  var total = 0
  $('input.category-' + category).each(function(i,input) {
    total = total + (parseFloat($(input).val()) || 0);
  });
  return total;
}

/**
 * Get total FTE per category and Type
 * 
 * @param {string} category
 * @param {string} type
 * @returns {number} total
 */
function getTotalByCategoryAndType(category,type) {
  var total = 0
  $('input.category-' + category + '.type-' + type).each(function(i,input) {
    total = total + (parseFloat($(input).val()) || 0);
  });
  return total;
}