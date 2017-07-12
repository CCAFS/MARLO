$(document).ready(init);

function init() {

  $(".parameterValue.type-2").datepicker({
      dateFormat: "yy-mm-dd",
      minDate: '2012-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true
  });

  $(".parameterValue.type-3").numericInput();

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {

  $('.addParameter').on('click', addParameter);
  $('.removeParameter').on('click', removeParameter);

  $('.blockTitle.closed').on('click', function() {
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
    });
  });

}

function addParameter() {
  var $item = $('#parameter-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.crpParameters').find("table tbody");
  // Adding item to the list
  $list.append($item);
  // Update Indexes
  updateParametersIndexes();
  // Show item
  $item.show('slow');
}

function removeParameter() {
  var $parent = $(this).parents('tr');
  $parent.hide('slow', function() {
    $parent.remove();
    updateParametersIndexes();
  });
}

function updateParametersIndexes() {
  $('.crpParameters').each(function(i,crpParameters) {
    $(crpParameters).find('.parameter').each(function(j,parameter) {
      $(parameter).setNameIndexes(1, i);
      $(parameter).setNameIndexes(2, j);
    });
  });
}