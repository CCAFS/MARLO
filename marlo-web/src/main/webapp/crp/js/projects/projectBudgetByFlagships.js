$(document).ready(init);

function init() {

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

  // Calculate currency and percentage
  $('input.percentageInput').on('keyup', calculateRemaining);
  $('input.percentageInput').trigger('keyup').removeClass('fieldError');
}

/**
 * Events Functions
 */

function calculateRemaining() {
  var type = getClassParameter($(this), 'type');
  var context = getClassParameter($(this), 'context');
  updateActiveYearCurrency(context, type)
}

/**
 * General Functions
 */

function updateActiveYearCurrency(context,type) {
  var percentageRemaining = 100 - calculateBudgetRemaining(context, type);
  var $target = $('.tab-pane.active .context-' + context + '.totalByYear-' + type);
  var $inputs = $('.tab-pane.active input.percentageInput.context-' + context + '.type-' + type + ':enabled');
  // Set total budget amount of the active year
  $target.parent().parent().removeClass('fieldError fieldChecked');
  $inputs.removeClass('fieldError fieldChecked');
  if(percentageRemaining == 0) {
    $target.parent().parent().addClass('fieldChecked');
    $inputs.addClass('fieldChecked');
  } else {
    $target.parent().parent().addClass('fieldError');
    $inputs.addClass('fieldError');
  }

  $target.text(setPercentageFormat(percentageRemaining));
  // Animate CSS
  $target.parent().animateCss('flipInX');

}

function calculateBudgetRemaining(context,type) {
  var total = 0
  $('.tab-pane.active input.percentageInput.context-' + context + '.type-' + type + ':enabled').each(function(i,e) {
    total = total + parseFloat(removePercentageFormat($(e).val() || "0"));
    total = Number((total).toFixed(2));
  });
  return total;
}
