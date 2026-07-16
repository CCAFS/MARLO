$(document).ready(init);

function init() {

  $(".parameterValue.type-2").datepicker({
      dateFormat: "yy-mm-dd",
      minDate: '2012-01-01',
      maxDate: '2031-12-31',
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
  $('.parameter-search').on('input', filterParametersBySearch);

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

function filterParametersBySearch() {
  var searchTerm = ($(this).val() || '').toLowerCase();
  var tableId = $(this).attr('data-target');
  var $table = $('#' + tableId);

  if(!$table.length) {
    return;
  }

  $table.find('tbody tr.parameter').each(function() {
    var keyText = ($(this).find('strong').text() || '').toLowerCase();
    var descText = ($(this).find('small i').text() || '').toLowerCase();
    var fullText = (keyText + ' ' + descText).trim();

    if(fullText.indexOf(searchTerm) !== -1) {
      $(this).show();
    } else {
      $(this).hide();
    }
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