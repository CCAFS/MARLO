var countW3BilateralFunds;

$(document).ready(init);

function init() {

  countW3BilateralFunds = $('form .projectW3bilateralFund').length;

  addProject = addBilateralFundProject;

  // Setting Numeric Inputs
  $('form input.currencyInput, input.percentageInput').numericInput();

  // Setting Currency Inputs
  $('form input.currencyInput').currencyInput();

  // Setting Percentage Inputs
  $('form input.percentageInput').percentageInput();

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
  $('.w3bilateralFund select').on('change', changeBilateralFund);

  // Remove a W3/Bilateral Fund
  $('.removeW3bilateralFund').on('click', removeBilateralFund);

  // Calculate currency and percentage
  $('input.currencyInput, input.percentageInput').on('keyup', calculateCurrencyPercentage);

}

/**
 * Events Functions
 */

function changeBilateralFund() {
  var value = $(this).val();
  var $inputs = $(this).parents('.w3bilateralFund').find('input.currencyInput, input.percentageInput');
  var $partner = $(this).parents('.projectPartner');
  $inputs.removeClass('type-2 type-3 type-none');
  if(value != "-1") {
    $inputs.addClass('type-' + value);
  }
  // Update overalls; Type-2: w3, type-3: bilateral
  updateActiveYearCurrency('3', $partner);
  updateActiveYearCurrency('2', $partner);
}

function removeBilateralFund() {
  var $parent = $(this).parent();
  var $partner = $(this).parents('.projectPartner');
  $parent.slideUp('slow', function() {
    $parent.remove();
    // Update overalls; Type-2: w3, type-3: bilateral
    updateActiveYearCurrency('3', $partner);
    updateActiveYearCurrency('2', $partner);
  });
}

function calculateCurrencyPercentage() {
  var type = getClassParameter($(this), 'type');
  console.log(type);
  var $partner = $(this).parents('.projectPartner');
  updateActiveYearCurrency(type, $partner)
}

/**
 * General Functions
 */

// Add bilateral project function
function addBilateralFundProject(composedName,projectId) {
  dialog.dialog("close");
  var $item = $('#projectW3bilateralFund-template').clone(true).removeAttr('id');
  var $list = $elementSelected.parents(".projectPartner").find(".projectW3bilateralFund-list");
  // Setting parameters
  $item.find('.title').text(composedName);
  $item.find('.titleId').text(projectId);
  $item.find('.institutionId').val(institutionSelected);
  $item.find('.selectedYear').val($('.tab-pane.active').attr('id').split('-')[1]);
  $item.find('.projectId').val(projectId);
  // Setting Currency Inputs
  $item.find('input.currencyInput').currencyInput();
  // Setting Percentage Inputs
  $item.find('input.percentageInput').percentageInput();
  // Update Index
  $item.setNameIndexes(1, countW3BilateralFunds);
  countW3BilateralFunds++;
  // Add the W3bilateralFund to the list
  $list.append($item);
  // Remove emptyMessage
  $list.find('.emptyMessage').remove();
  // Show the W3bilateralFund
  $item.show('slow');
}

function updateActiveYearCurrency(type,partner) {
  var totalyear = calculateBudgetCurrency(type);
  var $target = $('.tab-pane.active .totalByYear-' + type);
  // Set total budget amount of the active year
  $target.text(setCurrencyFormat(totalyear));
  // Animate CSS
  $target.parent().animateCss('flipInX');

  // If the partner has W3 or Bilateral budgets; Type-2: w3, type-3: bilateral
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
    total = total + removeCurrencyFormat($(e).val() || "0");
  });
  return total;
}

function calculateBudgetCurrencyByPartner(type,partner) {
  var total = 0
  $(partner).find('input.currencyInput.type-' + type + ':enabled').each(function(i,e) {
    total = total + removeCurrencyFormat($(e).val() || "0");
  });
  return total;
}

function calculateGenderBudget(type,partner) {
  var totalAmount = 0;
  var percentage = 0;
  var genderAmount = 0;

  // Type-2: w3, type-3: bilateral
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
    $(partner).find('.percentageLabel.type-' + type).text(setPercentageFormat(percentage));
  } else {
    totalAmount = removeCurrencyFormat($(partner).find('input.currencyInput.type-' + type + ':enabled').val() || "0");
    percentage =
        removePercentageFormat($(partner).find('input.percentageInput.type-' + type + ':enabled').val() || "0");
    genderAmount = (totalAmount / 100) * percentage;
  }
  $(partner).find('.percentageAmount.type-' + type + ' span').text(setCurrencyFormat(genderAmount));
}
