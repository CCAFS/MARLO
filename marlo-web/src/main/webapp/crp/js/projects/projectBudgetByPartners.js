var countFundingSources, budgetTypeJson;

$(document).ready(init);

function init() {

  budgetTypeJson = JSON.parse($('#budgetTypeJson').text());

  $('a[data-toggle="tab"]').ready(showHelpText(), setViewMore());

  countFundingSources = parseInt($('#budgetIndex').text());

  /**
   * @override
   */
  addProject = addFundingSource;

  // Setting Numeric Inputs
  $('form input.currencyInput, input.percentageInput').numericInput();

  // Setting Currency Inputs
  $('form input.currencyInput').currencyInput();

  // Setting Percentage Inputs
  $('form input.percentageInput').percentageInput();

  // Update total budget currency per Year
  updateYearsTotalCurrency('planning');
  updateYearsTotalCurrency('reporting');

  // Attaching events
  attachEvents();
}

function attachEvents() {
  /**
   * General
   */
  // Collapsible partners content
  $('.blockTitle').on('click', function() {
    if($(this).hasClass('closed')) {
      $(this).parent().find('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow');
  });

  /**
   * Funding sources
   */

  // Remove a Funding source
  $('.removeW3bilateralFund').on('click', removeBilateralFund);

  // Calculate currency and percentage
  $('input.currencyInput, input.percentageInput').on('keyup', calculateCurrencyPercentage);

  $('input.currencyInput.fundInput').on('keyup', validateFundingSource);

  $('.toggleProjectFundingSource').on('click', toggleProjectFundingSource);
}

/**
 * Events Functions
 */

function toggleProjectFundingSource() {
  var $partner = $(this).parents('.projectPartner');
  $partner.find('.project-fs-expandible-true').slideToggle(300);
  $partner.find('.project-fs-expandible-false').slideToggle(300);
}

function removeBilateralFund() {
  var $parent = $(this).parents('.projectW3bilateralFund');
  var $partner = $(this).parents('.projectPartner');
  $parent.slideUp(300, function() {
    $parent.remove();
    // Update overalls
    $.each(budgetTypeJson, function(i,e) {
      updateActiveYearCurrency(e.id, $partner, 'planning');
    });
    updateYearsTotalCurrency('planning');
  });
}

function calculateCurrencyPercentage() {
  var type = getClassParameter($(this), 'type');
  var cycle = getClassParameter($(this), 'cycle');

  console.log(type, cycle);

  var $partner = $(this).parents('.projectPartner');
  updateActiveYearCurrency(type, $partner, cycle);
  updateYearsTotalCurrency(cycle);
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
  var $partner = $elementSelected.parents(".projectPartner");
  var $list = $partner.find(".projectW3bilateralFund-list");

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

  // $item.setNameIndexes(1, countFundingSources);
  // countFundingSources++;

  // Add the W3bilateralFund to the list
  $list.append($item);
  // Remove emptyMessage
  $list.find('.emptyMessage').remove();
  // Show the W3bilateralFund
  $item.show('slow');

  // Update Index
  $('.projectW3bilateralFund').each(function(iFs,fs) {
    $(fs).setNameIndexes(1, iFs);
  })

  // Update FS counter
  var fsCount = $list.find('.projectW3bilateralFund').length;
  $partner.find('span.fsCounter').text(fsCount);
  $partner.find('span.fsCounter').parent().animateCss('bounceIn');
}

function updateActiveYearCurrency(type,partner,cycle) {
  var totalyear = getCurrencyByType(type, cycle);
  var $target = $('.tab-pane.active .cycle-' + cycle + '.totalByYear-' + type);
  // Set total budget amount of the active year
  $target.text(setCurrencyFormat(totalyear));
  // Animate CSS
  $target.parent().animateCss('flipInX');

  // For each partner
  $('.tab-pane.active .projectPartner').each(function(i,e) {
    var totalPartner = getCurrencyByTypeAndPartner(type, $(e), cycle);
    // Set label no editable amount
    var $targetPartner = $(e).find('.currencyInput.cycle-' + cycle + '.totalByPartner-' + type);
    $targetPartner.text(setCurrencyFormat(totalPartner));
    // Animate CSS
    $targetPartner.parent().animateCss('flipInX');
  });

  // Calculate gender
  calculateGenderBudget(type, $(partner), cycle);
}

function showHelpText() {
  $('.helpMessage').show();
  $('.helpMessage').addClass('animated flipInX');
}

function updateYearsTotalCurrency(cycle) {
  $('.tab-pane').each(function(i,pane) {
    var year = $(pane).attr('id').split('-')[1];
    var totalyear = getTotalByYear(year, cycle);
    var $target = $('.overallAmount.cycle-' + cycle + '.year-' + year);
    // Set total budget amount of the year
    $target.text(setCurrencyFormat(totalyear));
    // Animate CSS
    $target.parent().animateCss('flipInX');
  });
}

/**
 * Get budget Currency filtered by budget type ID
 * 
 * @param {int} Budget Type ID
 * @returns {number} Total
 */
function getCurrencyByType(type,cycle) {
  var total = 0
  $('.tab-pane.active input.currencyInput.cycle-' + cycle + '.type-' + type + ':enabled').each(function(i,e) {
    total = total + removeCurrencyFormat($(e).val() || "0");
  });
  return total;
}

/**
 * Get total budget per year (All budget types included)
 * 
 * @param {int} Year
 * @returns {number} Total
 */
function getTotalByYear(year,cycle) {
  var total = 0
  $('span.cycle-' + cycle + '.totalByYear.year-' + year).each(function(i,e) {
    total = total + removeCurrencyFormat($(e).text() || "0");
  });
  return total;
}

/**
 * Get budget Currency filtered by budget type ID and Partner
 * 
 * @param {int} Budget Type ID
 * @param {DOM} Partner
 */
function getCurrencyByTypeAndPartner(type,partner,cycle) {
  var total = 0
  $(partner).find('input.currencyInput.cycle-' + cycle + '.type-' + type + ':enabled').each(function(i,e) {
    total = total + removeCurrencyFormat($(e).val() || "0");
  });
  return total;
}

/**
 * Calculate gender budget currency and percentage filtered by budget type ID and Partner
 * 
 * @param {int} Budget Type ID
 * @param {DOM} Partner
 */
function calculateGenderBudget(type,partner,cycle) {
  var totalAmount = 0;
  var percentage = 0;
  var genderAmount = 0;

  $(partner).find('.projectW3bilateralFund').each(
      function(i,e) {
        var amount =
            removeCurrencyFormat($(e).find('input.currencyInput.cycle-' + cycle + '.type-' + type + ':enabled').val()
                || "0");
        var pcg =
            removePercentageFormat($(e).find('input.percentageInput.cycle-' + cycle + '.type-' + type + ':enabled')
                .val()
                || "0");
        totalAmount = totalAmount + amount;
        genderAmount = genderAmount + ((amount / 100) * pcg);
      });

  percentage = ((genderAmount / totalAmount) * 100).toFixed(2);

  if(isNaN(percentage)) {
    percentage = 0;
  }

  // Percentage
  $(partner).find('.percentageLabel.type-' + type).text(setPercentageFormat(percentage));

  // Amount (Currency)
  $(partner).find('.percentageAmount.type-' + type + ' span').text(setCurrencyFormat(genderAmount));
}
