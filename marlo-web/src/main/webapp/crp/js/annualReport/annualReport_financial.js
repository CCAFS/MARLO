$(document).ready(init);

function init() {

  // Attaching events
  attachEvents();

  // Setting Currency Inputs
  $('form input.currencyInput').currencyInput();

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