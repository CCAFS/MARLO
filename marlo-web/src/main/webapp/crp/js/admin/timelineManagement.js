$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();
  
	datePickerConfig({
	  startDate: ".srfSlo:not(#srfSlo-template) .startDate",
	  endDate:   ".srfSlo:not(#srfSlo-template) .endDate",
	  defaultMinDateValue: $("#minDateValue").val(),
	  defaultMaxDateValue: $("#maxDateValue").val()
	});

}

function attachEvents() {
  $('.addSlo').on('click', addIdo);

  $('.addIndicator').on('click', addIndicator);

  $('.addTargets').on('click', addTargets);

  $('.addCrossCuttingIssue').on('click', addCrossCuttingIssue);

  $('.remove-element').on('click', removeElement);

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

	
	$(document).on('addComponent', function (e) {
	  const $container = $(e.target); 

	  $container.find('.startDate, .endDate').each(function () {
	    try { $(this).datepicker('destroy'); } catch (err) {}
	  });

	  datePickerConfig({
	    startDate: $container.find('.startDate'),
	    endDate:   $container.find('.endDate'),
	    defaultMinDateValue: $("#minDateValue").val(),
	    defaultMaxDateValue: $("#maxDateValue").val()
	  });
	});
}

function addIdo() {
  console.log("add ido");
  var $itemsList = $(this).parent().find('.slos-list');
  var $item = $("#srfSlo-template").clone(true).removeAttr("id");
  $item.find('.blockTitle').trigger('click');
  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function addIndicator() {
  console.log("addIndicator");
  var $itemsList = $(this).parent().parent().find('.srfIndicators-list');
  var $item = $("#srfSloIndicator-template").clone(true).removeAttr("id");

  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function addTargets() {
  console.log("addTargets");
  var $itemsList = $(this).parent().parent().find('.targetsList');
  var $item = $("#targetIndicator-template").clone(true).removeAttr("id");
  $itemsList.append($item);
  $item.show('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function addCrossCuttingIssue() {
  console.log("addCrossCuttingIssue");
  var $itemsList = $(this).parent().find('.issues-list');
  var $item = $("#srfCCIssue-template").clone(true).removeAttr("id");

  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function removeElement() {
  $item = $(this).parent();
  $item.hide('slow', function() {
    $item.remove();
    updateIndexes();
    $(document).trigger('removeComponent');
  });
}

function updateIndexes() {
  $('.slos-list .srfSlo').each(function(i,slo) {
    // Updating indexes
    $(slo).setNameIndexes(1, i);
    $(slo).find('.srfSloIndicator').each(function(subIdoIndex,subIdo) {
      // Updating indexes
      $(subIdo).setNameIndexes(2, subIdoIndex);
    });
  });

  $('.issues-list .srfCCIssue').each(function(i,crossCutting) {
    // Updating indexes
    $(crossCutting).setNameIndexes(1, i);

  });
}

/**
 * Attach to the date fields the datepicker plugin
 */
function datePickerConfig(opts) {
  date($(opts.startDate), $(opts.endDate), opts.defaultMinDateValue, opts.defaultMaxDateValue);
}

function date($start, $end, minDateStr, maxDateStr) {
  var dateFormat = "yy-mm-dd";
  var minDate = minDateStr ? $.datepicker.parseDate(dateFormat, minDateStr) : new Date(2023,0,1);
  var maxDate = maxDateStr ? $.datepicker.parseDate(dateFormat, maxDateStr) : new Date(2031,11,31);

  // START (sin normalizar al 1° del mes)
  $start.datepicker({
    dateFormat: dateFormat,
    minDate: minDate,
    maxDate: maxDate,
    changeMonth: true,
    changeYear: true,
    numberOfMonths: 2,
    // no beforeShow que cambie la fecha
    // no onChangeMonthYear que cambie la fecha
    onSelect: function() {
      var s = $start.datepicker('getDate');
      if (s) {
        $end.datepicker('option', 'minDate', s);
        var e = $end.datepicker('getDate');
        if (e && e < s) $end.datepicker('setDate', s);
      }
    }
  });

  // END (sin normalizar al último día del mes)
  $end.datepicker({
    dateFormat: dateFormat,
    minDate: minDate,
    maxDate: maxDate,
    changeMonth: true,
    changeYear: true,
    numberOfMonths: 2,
    onSelect: function() {
      var e = $end.datepicker('getDate');
      if (e) {
        $start.datepicker('option', 'maxDate', e);
        var s = $start.datepicker('getDate');
        if (s && s > e) $start.datepicker('setDate', e);
      }
    }
  });
}

