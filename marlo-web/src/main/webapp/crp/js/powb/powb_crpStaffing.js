$(document).ready(function() {

  console.log('Init');

  // Setting Numeric Inputs
  $('form input.currencyInput').numericInput();

  $('.currencyInput').on('keyup', function() {
    var type = $(this).classParam('type');
    var category = $(this).classParam('category');
    var totalCategory = getTotalByCategory(category);
    var totalFemale = getTotalByCategoryAndType(category, "female") + getTotalByCategoryAndType(category, "femaleNon");
    var totalType = getTotalByType(type);
    var granTotal = getGrandTotal();
    var percFemale = ((totalFemale / totalCategory) * 100);
    if(isNaN(percFemale)) {
      percFemale = 0;
    }
    var percFemaleGrandTotal = (((getTotalByType("female") + getTotalByType("femaleNon")) / granTotal) * 100);
    if(isNaN(percFemaleGrandTotal)) {
      percFemaleGrandTotal = 0;
    }

    var $totalCategoryLabel = $('span.label-total.category-' + category);
    var $totalTypeLabel = $('span.totalByType.type-' + type);
    var $percFemaleLabel = $('span.label-percFemale.category-' + category);
    var $grandTotalLabel = $('span.label-total.grandTotal');
    var $percFemaleGrandTotalLabel = $('span.label-percFemale.grandTotal');

    $totalCategoryLabel.text(totalCategory.toFixed(1)).animateCss('flipInX');

    $totalTypeLabel.text(totalType.toFixed(1)).animateCss('flipInX');

    $percFemaleLabel.text(percFemale.toFixed(1)).animateCss('flipInX');

    $grandTotalLabel.text(granTotal.toFixed(1)).animateCss('flipInX');

    $percFemaleGrandTotalLabel.text(percFemaleGrandTotal.toFixed(1)).animateCss('flipInX');

  }).trigger('keyup');
})

/**
 * Get total FTE per category (All types included)
 * 
 * @param {string} category
 * @returns {number} total
 */
function getTotalByCategory(category) {
  var total = 0
  $('input.numericValue.category-' + category).each(function(i,input) {
    total = total + (parseFloat($(input).val()) || 0);
  });
  return total;
}

/**
 * Get total FTE per type (All categories included)
 * 
 * @param {string} type
 * @returns {number} total
 */
function getTotalByType(type) {
  var total = 0
  $('input.numericValue.type-' + type).each(function(i,input) {
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
  $('input.numericValue.category-' + category + '.type-' + type).each(function(i,input) {
    total = total + (parseFloat($(input).val()) || 0);
  });
  return total;
}

/**
 * Get grand total FTE
 * 
 * @returns {number} total
 */
function getGrandTotal() {
  var total = 0
  $('input.numericValue').each(function(i,input) {
    total = total + (parseFloat($(input).val()) || 0);
  });
  return total;
}