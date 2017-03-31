var countFundingSources;

$(document).ready(init);

function init() {

  $('a[data-toggle="tab"]').ready(showHelpText(), setViewMore());

  countFundingSources = parseInt($('#budgetIndex').text());
  addProject = addFundingSource;

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

  $('input.currencyInput.fundInput').on('keyup', validateFundingSource);

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
    updateActiveYearCurrency('1', $partner);
    updateActiveYearCurrency('2', $partner);
    updateActiveYearCurrency('3', $partner);
    updateActiveYearCurrency('4', $partner);
    updateActiveYearCurrency('5', $partner);
  });
}

function calculateCurrencyPercentage() {
  var type = getClassParameter($(this), 'type');
  var $partner = $(this).parents('.projectPartner');
  updateActiveYearCurrency(type, $partner)
}

/**
 * General Functions
 */

function validateFundingSource() {
  var value = parseInt($(this).val()) || 0;
  var limit = removeCurrencyFormat($(this).parents('.projectW3bilateralFund').find('.projectAmount').text());

  if(value > limit) {
    $(this).addClass('fieldError');
  } else {
    $(this).removeClass('fieldError');
  }
}

// Add funding source function
function addFundingSource(fs) {
  dialog.dialog("close");
  var $item = $('#projectW3bilateralFund-template').clone(true).removeAttr('id');
  var $list = $elementSelected.parents(".projectPartner").find(".projectW3bilateralFund-list");

  // Setting parameters
  $item.find('.title').text(fs.composedName);
  $item.find('.titleId').text(fs.id);
  $item.find('.projectAmount').text(setCurrencyFormat(fs.budget));
  $item.find('.institutionId').val(fs.institutionSelected);
  $item.find('.selectedYear').val($('.tab-pane.active').attr('id').split('-')[1]);
  $item.find('.projectId').val(fs.id);
  $item.find('.projectId').addClass("institution-" + fs.institutionSelected + " " + "year-" + fs.selectedYear);
  // Set type as default
  $item.find('.budgetTypeName').text(fs.type);
  $item.find('.budgetTypeId').val(fs.typeId);

  // Set type
  $item.find('input:text').removeClass('type-none').addClass('type-' + fs.typeId)

  // Setting Currency Inputs
  $item.find('input.currencyInput').currencyInput();
  // Setting Percentage Inputs
  $item.find('input.percentageInput').percentageInput();
  // Update Index
  $item.setNameIndexes(1, countFundingSources);
  countFundingSources++;
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

  // For each partner
  $('.tab-pane.active .projectPartner').each(function(i,e) {
    var totalPartner = calculateBudgetCurrencyByPartner(type, $(e));
    // Set label no editable amount
    var $targetPartner = $(e).find('.currencyInput.totalByPartner-' + type);
    $targetPartner.text(setCurrencyFormat(totalPartner));
    // Animate CSS
    $targetPartner.parent().animateCss('flipInX');
  });

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

  $(partner).find('.percentageAmount.type-' + type + ' span').text(setCurrencyFormat(genderAmount));
}

function showHelpText() {
  $('.helpMessage').show();
  $('.helpMessage').addClass('animated flipInX');
}
