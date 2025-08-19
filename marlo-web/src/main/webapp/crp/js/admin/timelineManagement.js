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

  // Usa los límites dinámicos si existen; fallback a tus rangos previos
  var minDate = minDateStr ? $.datepicker.parseDate(dateFormat, minDateStr) : new Date(2023,0,1);
  var maxDate = maxDateStr ? $.datepicker.parseDate(dateFormat, maxDateStr) : new Date(2026,11,31);

  // Helper: primer/último día del mes visible
  function firstDayOf(year, month0) { return new Date(year, month0, 1); }
  function lastDayOf(year, month0)  { return new Date(year, month0 + 1, 0); }

  // START
  $start.datepicker({
    dateFormat: dateFormat,
    minDate: minDate,
    maxDate: maxDate,
    changeMonth: true,
    changeYear: true,
    numberOfMonths: 2,
    beforeShow: function(input, inst) {
      if (!$start.val()) {
        var dp = $(this).datepicker('getDate');
        var today = new Date();
        var base = dp || new Date(today.getFullYear(), today.getMonth(), 1);
        $(this).datepicker('setDate', firstDayOf(base.getFullYear(), base.getMonth()));
      }
    },
    onChangeMonthYear: function(year, month /*1..12*/, inst) {
      var selected = firstDayOf(inst.selectedYear, inst.selectedMonth); 
      $(this).datepicker('setDate', selected);
    },
    onSelect: function(dateText, inst) {
      var selected = $(this).datepicker('getDate');
      if (selected) {
        var normalized = firstDayOf(selected.getFullYear(), selected.getMonth());
        $(this).datepicker('setDate', normalized);
        $end.datepicker('option', 'minDate', normalized);
      }
    }
  });

  // END
  $end.datepicker({
    dateFormat: dateFormat,
    minDate: minDate,
    maxDate: maxDate,
    changeMonth: true,
    changeYear: true,
    numberOfMonths: 2,
    beforeShow: function(input, inst) {
      if (!$end.val()) {
        var dp = $(this).datepicker('getDate');
        var today = new Date();
        var base = dp || new Date(today.getFullYear(), today.getMonth(), 1);
        $(this).datepicker('setDate', lastDayOf(base.getFullYear(), base.getMonth()));
      }
    },
    onChangeMonthYear: function(year, month /*1..12*/, inst) {
      var selected = lastDayOf(inst.selectedYear, inst.selectedMonth);
      $(this).datepicker('setDate', selected);
    },
    onSelect: function(dateText, inst) {
      var selected = $(this).datepicker('getDate');
      if (selected) {
        var normalized = lastDayOf(selected.getFullYear(), selected.getMonth());
        $(this).datepicker('setDate', normalized);
        $start.datepicker('option', 'maxDate', normalized);
      }
    }
  });

  $start.off('click');
  $end.off('click');

  // $start.attr('readonly', true);
  // $end.attr('readonly', true);
}
