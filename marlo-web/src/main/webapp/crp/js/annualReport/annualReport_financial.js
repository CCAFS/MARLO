$(document).ready(init);

function init() {

  // Attaching events
  attachEvents();

  // Setting Currency Inputs
  $('form input.currencyInput').currencyInput();

  $('.currencyInput').on('keyup', function() {

    var element = $(this).classParam('element');
    var category = $(this).classParam('category');
    var type = $(this).classParam('type');
    // Total Category
    var queryTotalCategory = '.element-' + element + '.category-' + category;
    var totalCategory = $('input' + queryTotalCategory).getSumCurrencyInputs();
    $('.totalCategory' + queryTotalCategory).find('span').text(setCurrencyFormat(totalCategory)).animateCss('flipInX');

    // Budget Planned
    var queryPlanned = '.element-' + element + '.type-' + type + '.category-planned';
    var planned = $('input' + queryPlanned).getSumCurrencyInputs();

    // Actual Expenditure
    var queryActualExpenditure = '.element-' + element + '.type-' + type + '.category-actualExpenditure';
    var actualExpenditure = $('input' + queryActualExpenditure).getSumCurrencyInputs();

    // Total Difference by type
    var totalDiff = (planned - actualExpenditure);
    var queryDiff = '.element-' + element + '.type-' + type;
    $('.totalDiff' + queryDiff).find('span').text(setCurrencyFormat(totalDiff)).animateCss('flipInX');

    // Grand Total by element
    var queryGrandTotal = '.element-' + element + '.category-difference';
    var grandTotal = $('.totalDiff' + queryGrandTotal + ' span').getSumCurrencyInputs();
    $('.totalCategory' + queryGrandTotal).find('span').text(setCurrencyFormat(grandTotal)).animateCss('flipInX');

  }).trigger('keyup');
}

function attachEvents() {

}

/* Calculated functions */

/**
 * Get total by type
 * 
 * @param params
 * @returns
 */

jQuery.fn.getSumCurrencyInputs = function() {
  var total = 0
  $(this).each(function(i,input) {
    total = total + removeCurrencyFormat($(input).val() || $(input).text() || "0");
  });
  return total;
};