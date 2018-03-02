$(document).ready(function() {

  console.log('Init');

  // Setting Numeric Inputs
  $('form input.currencyInput, input.percentageInput').numericInput();

  // Setting Currency Inputs
  $('form input.currencyInput').currencyInput();

  // Setting Percentage Inputs
  $('form input.percentageInput').percentageInput();

  $('.percentageInput').on('keyup', function() {
    // Expenditure Grand Total
    var $expenditureGrandTotalLabel = $('span.label-expenditureTotal');
    var expenditureTotal = getTotalExpenditureArea();
    $expenditureGrandTotalLabel.text(setCurrencyFormat(expenditureTotal)).animateCss('flipInX');
  });

  $('.currencyInput').on('keyup', function() {
    var type = $(this).classParam('type');
    var category = $(this).classParam('category');

    // Total by category (F1, F2, Strategic Comp...)
    var $totalByCategoryLabel = $('span.label-total.category-' + category);
    var totalByCategory = getTotalByCategory(category);
    $totalByCategoryLabel.text(setCurrencyFormat(totalByCategory));
    $totalByCategoryLabel.animateCss('flipInX');

    // Total by type (W1W2, W3Bilateral)
    var $totalByTypeLabel = $('span.label-totalByType.type-' + type);
    var totalByType = getTotalByType(type);
    $totalByTypeLabel.text(setCurrencyFormat(totalByType));
    $totalByTypeLabel.animateCss('flipInX');

    // Grand Total
    var $grandTotalLabel = $('span.label-grandTotal');
    var grandTotal = getGrandTotal();
    $grandTotalLabel.text(setCurrencyFormat(grandTotal));
    $grandTotalLabel.animateCss('flipInX');

    // Trigger Expenditure percentages
    $('.percentageInput').trigger('keyup');

  }).trigger('keyup');

});

/**
 * Get total amount of expenditure area (W1W2)
 * 
 * @returns {number} total
 */
function getTotalExpenditureArea() {
  var grandTotal = getTotalByType('w1w2');
  var total = 0;
  $('.percentageInput').each(function(i,e) {
    var percentage = removePercentageFormat($(e).val() || "0");
    total = total + ((grandTotal / 100) * percentage);
  });
  return total;
}

/**
 * Get total budget per category (All types included)
 * 
 * @param {string} category
 * @returns {number} total
 */
function getTotalByCategory(category) {
  var total = 0
  $('input.currencyInput.category-' + category).each(function(i,input) {
    total = total + removeCurrencyFormat($(input).val() || "0");
  });
  return total;
}

/**
 * Get total budget per type (All categories included)
 * 
 * @param {string} type
 * @returns {number} total
 */
function getTotalByType(type) {
  var total = 0
  $('input.currencyInput.type-' + type).each(function(i,input) {
    total = total + removeCurrencyFormat($(input).val() || "0");
  });
  return total;
}

/**
 * Get grand total budget
 * 
 * @returns {number} total
 */
function getGrandTotal() {
  var total = 0
  $('input.currencyInput').each(function(i,input) {
    total = total + removeCurrencyFormat($(input).val() || "0");
  });
  return total;
}
