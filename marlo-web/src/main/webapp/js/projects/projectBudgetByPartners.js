$(document).ready(init);

function init() {

  // Setting Numeric Inputs
  $('input.currencyInput, input.percentageInput').numericInput();

  // Setting Currency Inputs
  $('input.currencyInput').currencyInput();

  // Setting Percentage Inputs
  $('input.percentageInput').percentageInput();

  // Update totals year once
  updateAllCurrencies();

  // Attaching events
  attachEvents();
}

function attachEvents() {

  /**
   * General
   */
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
  $('.w3bilateralFund select').on('change', function() {
    var value = $(this).val();
    var $inputs = $(this).parents('.w3bilateralFund').find('input.currencyInput, input.percentageInput');
    $inputs.removeClass('type-w3 type-bilateral');
    if(value != "-1") {
      $inputs.addClass('type-' + value);
    }
    // Update overalls
    updateBudgetCurrency('w3');
    updateBudgetCurrency('bilateral');
  });

  $('.removeW3bilateralFund').on('click', function() {
    var $parent = $(this).parent();
    $parent.slideUp('slow', function() {
      $parent.remove();

      // Update overalls
      updateBudgetCurrency('w3');
      updateBudgetCurrency('bilateral');
    });

  });

  /**
   * Calculate currency
   */
  $('input.currencyInput').on('keyup', function() {
    var type = getBudgetType($(this));
    updateBudgetCurrency(type);
  });

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    updateAllCurrencies();
  })

}

function updateBudgetCurrency(type) {
  var total = calculateBudgetCurrency(type);
  var $target = $('.tab-pane.active .totalByYear-' + type);
  $target.text(setCurrencyFormat(total));
  // Animate CSS
  $target.parent().animateCss('flipInX');

  if((type == "w3") || (type == "bilateral")) {
    console.log($('.tab-pane.active .totalByPartner-' + type));
    $('.tab-pane.active .totalByPartner-' + type).text(setCurrencyFormat(total));
  }
}

function calculateBudgetCurrency(type) {
  var total = 0
  $('.tab-pane.active input.currencyInput.type-' + type + ':enabled').each(function(i,e) {
    total = total + removeCurrencyFormat($(e).val());
  });
  return total
}

function getBudgetType(input) {
  var cssName = "type";
  var check = cssName + "-";
  var className = $(input).attr('class') || '';
  var type = $.map(className.split(' '), function(val,i) {
    if(val.indexOf(check) > -1) {
      return val.slice(check.length, val.length);
    }
  });
  return((type.join(' ')) || 'none')
}

function updateAllCurrencies() {
  updateBudgetCurrency('w1w2');
  updateBudgetCurrency('w3');
  updateBudgetCurrency('bilateral');
  updateBudgetCurrency('centerFunds');
}
