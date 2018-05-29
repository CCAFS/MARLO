$(document).ready(init);

function init() {

  // Attaching events
  attachEvents();

  // Setting Currency Inputs
  $('form input.currencyInput').currencyInput();

  $('.currencyInput').on('keyup', function() {

    var p = {
        element: $(this).classParam('element'),
        category: $(this).classParam('category'),
        type: $(this).classParam('type')
    }

    var totalCategory = $('input.element-' + p.element + '.category-' + p.category).getSumCurrencyInputs();
    var totalType = $('input.element-' + p.element + '.type-' + p.type).getSumCurrencyInputs();

    console.log(total);

    // totalType <-- RECUERDA CALCULAR ESTO

  }).trigger('keyup');
}

function attachEvents() {
  // Collapsible content
  $('.blockTitle').on('click', function() {
    if($(this).hasClass('closed')) {
      $(this).parent().find('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow');
  });

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
    total = total + removeCurrencyFormat($(input).val() || "0");
  });
  return total;
};