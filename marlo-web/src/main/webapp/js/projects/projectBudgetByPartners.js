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
   * General:
   */
  // Collapsible partners content
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

  // Change W3/Bilateral Funds budget type
  $('.w3bilateralFund select').on('change', function() {
    var value = $(this).val();
    var $inputs = $(this).parents('.w3bilateralFund').find('input.currencyInput, input.percentageInput');
    var $partner = $(this).parents('.projectPartner');
    $inputs.removeClass('type-2 type-3');
    if(value != "-1") {
      $inputs.addClass('type-' + value);
    }
    // Update overalls
    updateActiveYearCurrency('2', $partner);
    updateActiveYearCurrency('3', $partner);
  });

  // Remove a W3/Bilateral Fund
  $('.removeW3bilateralFund').on('click', function() {
    var $parent = $(this).parent();
    var $partner = $(this).parents('.projectPartner');
    $parent.slideUp('slow', function() {
      $parent.remove();

      // Update overalls
      updateActiveYearCurrency('2', $partner);
      updateActiveYearCurrency('3', $partner);
    });

  });

  /**
   * Calculate currency and percentage
   */
  $('input.currencyInput, input.percentageInput').on('keyup', function() {
    var type = getClassParameter($(this), 'type');
    var $partner = $(this).parents('.projectPartner');
    updateActiveYearCurrency(type, $partner)
  });

}

function updateActiveYearCurrency(type,partner) {
  var totalyear = calculateBudgetCurrency(type);
  var $target = $('.tab-pane.active .totalByYear-' + type);
  // Set total budget amount of the active year
  $target.text(setCurrencyFormat(totalyear));
  // Animate CSS
  $target.parent().animateCss('flipInX');

  // If the partner has W3 or Bilateral budgets
  if((type == "2") || (type == "3")) {
    // For each partner
    $('.tab-pane.active .projectPartner').each(function(i,e) {
      var totalPartner = calculateBudgetCurrencyByPartner(type, $(e));
      // Set label no editable amount
      var $targetPartner = $(e).find('.currencyInput.totalByPartner-' + type);
      $targetPartner.text(setCurrencyFormat(totalPartner));
      // Animate CSS
      $targetPartner.parent().animateCss('flipInX');
    });
  }

  // Calculate gender
  calculateGenderBudget(type, $(partner));
}

function calculateBudgetCurrency(type) {
  var total = 0
  $('.tab-pane.active input.currencyInput.type-' + type + ':enabled').each(function(i,e) {
    total = total + removeCurrencyFormat($(e).val());
  });
  return total;
}

function calculateBudgetCurrencyByPartner(type,partner) {
  var total = 0
  $(partner).find('input.currencyInput.type-' + type + ':enabled').each(function(i,e) {
    total = total + removeCurrencyFormat($(e).val());
  });
  return total;
}

function calculateGenderBudget(type,partner) {
  var totalAmount = 0;
  var percentage = 0;
  var genderAmount = 0;

  if(((type == "2") || (type == "3") || (type == "none")) && $('.projectW3bilateralFund').exists()) {

    $(partner).find('.projectW3bilateralFund').each(function(i,e) {
      var amount = removeCurrencyFormat($(e).find('input.currencyInput.type-' + type + ':enabled').val() || "0");
      var pcg = removePercentageFormat($(e).find('input.percentageInput.type-' + type + ':enabled').val() || "0");
      totalAmount = totalAmount + amount;
      genderAmount = genderAmount + ((amount / 100) * pcg);
    });
    percentage = ((genderAmount / totalAmount) * 100).toFixed(2);

    if(isNaN(percentage)) {
      percentage = 0;
    }
    console.log(percentage);
    $(partner).find('.percentageInput.type-' + type).text(setPercentageFormat(percentage));

  } else {
    totalAmount = removeCurrencyFormat($(partner).find('input.currencyInput.type-' + type + ':enabled').val());
    percentage = removePercentageFormat($(partner).find('input.percentageInput.type-' + type + ':enabled').val());
    genderAmount = (totalAmount / 100) * percentage;
  }

  $(partner).find('.percentageAmount.type-' + type + ' span').text(setCurrencyFormat(genderAmount));
}
