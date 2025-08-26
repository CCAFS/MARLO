// Initialize when DOM is ready
$(document).ready(init);

function init() {
  /* Declaring Events */
  attachEvents();

  // Initialize Select2 on non-template selects
  addSelect2();

  // Initialize any "filter" Select2 elements with custom options
  applySelect2Filters();

  // Initialize datepickers per block (server-rendered blocks)
  initPerBlockDatepickers($(document));
}

/* -------------------------------------------------------------------------- */
/* Event wiring                                                               */
/* -------------------------------------------------------------------------- */
function attachEvents() {

  // Filter by permission (legacy UI filter)
  $('#feedbackPermissionFilter').on('change', function () {
    const selectedId = $(this).val();
    $('.srfSlo').each(function () {
      const permissionId = $(this).find('.feedbackPermission').val();
      if (!selectedId || selectedId === permissionId) {
        $(this).show();
      } else {
        $(this).hide();
      }
    });
  });

  $('#clearFeedbackPermissionFilter').on('click', function () {
    $('#feedbackPermissionFilter').val(null).trigger('change');
  });

  // Scroll to new entry if present
  const newEntry = document.querySelector(".new-entry");
  if (newEntry) {
    newEntry.scrollIntoView({
      behavior: "smooth",
      block: "center"
    });
  }

  // Add/remove handlers
  $('.addSlo').on('click', addIdo);
  $('.addIndicator').on('click', addIndicator);
  $('.addTargets').on('click', addTargets);
  $('.addCrossCuttingIssue').on('click', addCrossCuttingIssue);
  $('.remove-element').on('click', removeElement);

  // Collapsible block
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

  // Filter by permission via data-permission-id (second legacy filter)
  $('#feedbackPermissionFilter').on('change', function () {
    const selectedPermissionId = $(this).val();
    $('.srfSlo').not('.is-template').each(function () {
      const itemPermissionId = $(this).data('permission-id') + "";
      const shouldShow = !selectedPermissionId || selectedPermissionId === itemPermissionId;
      $(this).toggle(shouldShow);
    });
  });

  // Prevent the hidden template from being posted (important for portfolios[-1] placeholders)
  $('form').on('submit', function () {
    $('#srfSlo-template').find('[name]').prop('disabled', true);
  });
}

/* -------------------------------------------------------------------------- */
/* Add/Remove blocks                                                          */
/* -------------------------------------------------------------------------- */
function addIdo() {
  console.log("add ido");
  var $itemsList = $(this).parent().find('.slos-list');
  var $tpl = $("#srfSlo-template");
  var $item = $tpl.clone(true).removeAttr("id"); // clone template and remove duplicate id

  // Make sure the content is visible so widgets compute layout properly
  $item.find('.blockContent').show();

  // Clean field values in the cloned block
  $item.find('input[type="hidden"][name$=".id"]').val('');
  $item.find('input[type="text"]').val('');
  $item.find('select').each(function () {
    try { $(this).val(null).trigger('change'); } catch (e) {}
  });
  $item.find('.startDate, .endDate').prop('readonly', false);

  // Append and animate
  $itemsList.append($item);
  $item.slideDown('slow');

  // Update indexes with your existing utility
  updateIndexes();

  // Re-init widgets ONLY inside the cloned block
  initSelect2Within($item);

  // Re-init datepickers per block (Timeline-style: destroy -> re-init, local pair linkage)
  wireDateRangeForBlock($item);  // ensures selecting a date actually sets input value
  initDatepickers($item);        // optional: if Bootstrap Datepicker is also present
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

/* -------------------------------------------------------------------------- */
/* Indexing                                                                   */
/* -------------------------------------------------------------------------- */
function updateIndexes() {
  $('.slos-list .srfSlo').each(function(i,slo) {
    // Updating indexes (uses your custom setNameIndexes util)
    $(slo).setNameIndexes(1, i);
    $(slo).find('.srfSloIndicator').each(function(subIdoIndex,subIdo) {
      $(subIdo).setNameIndexes(2, subIdoIndex);
    });
  });

  $('.issues-list .srfCCIssue').each(function(i,crossCutting) {
    $(crossCutting).setNameIndexes(1, i);
  });
}

/* -------------------------------------------------------------------------- */
/* Select2 helpers                                                            */
/* -------------------------------------------------------------------------- */

// Global Select2 init for non-template selects
function addSelect2() {
  // Avoid initializing Select2 on the hidden template
  $("form select").not("#srfSlo-template select").select2();
}

// Initialize Select2 only within a given block (used after cloning)
function initSelect2Within($ctx) {
  $ctx.find('select.countriesSelect').each(function () {
    var $sel = $(this);
    // Destroy previous Select2 instance if the template was already initialized
    if ($sel.data('select2')) {
      try { $sel.select2('destroy'); } catch (e) {}
    }
    $sel.prop('disabled', false);
    $sel.select2({
      width: '100%',
      placeholder: (window.i18n_phases_placeholder || 'Select phases'),
      dropdownParent: $sel.closest('.blockContent') // avoids clipping inside containers
    });
  });
}

// Special Select2 for "filters" (kept as you had it)
function applySelect2Filters() {
  $('.select2-filter').select2({
    theme: 'bootstrap',
    width: '100%',
    allowClear: true,
    placeholder: function () {
      return $(this).data('placeholder') || 'Type to search...';
    },
    language: {
      searching: function () {
        return "Searching...";
      },
      noResults: function () {
        return "No results found";
      }
    }
  });
}

/* -------------------------------------------------------------------------- */
/* Datepickers                                                                */
/* -------------------------------------------------------------------------- */

/**
 * Initialize datepickers per block for already-rendered items.
 * This ensures each cloned block has its own start/end linkage.
 */
function initPerBlockDatepickers($ctx) {
  $ctx.find('.srfSlo').not('.is-template').each(function () {
    wireDateRangeForBlock($(this));
  });
  // If Bootstrap Datepicker is also present, run the destroy->reinit pass:
  initDatepickers($ctx);
}

/**
 * Wire one block's start/end date inputs so they update each other locally.
 * - Destroys any previous plugin instances
 * - Initializes jQuery UI datepicker range glue
 * - Adds onSelect to ensure the input shows the chosen date
 */
function wireDateRangeForBlock($block) {
  var $start = $block.find('input.startDate').first();
  var $end   = $block.find('input.endDate').first();

  if ($start.length === 0 && $end.length === 0) return;

  // Destroy any existing datepicker instances (both jQuery UI and Bootstrap DP)
  $start.add($end).each(function () {
    try { if ($(this).hasClass('hasDatepicker')) { $(this).datepicker('destroy'); } } catch (e) {}
    try { if ($(this).data('datepicker')) { $(this).datepicker('remove'); } } catch (e) {}
    try { if ($(this).data('datepicker')) { $(this).datepicker('destroy'); } } catch (e) {}
  });

  // Use your existing "date" glue per block (jQuery UI datepicker)
  date($start, $end);

  // Force onSelect to always set the input value and update pair constraints locally
  try {
    $start.datepicker('option', 'onSelect', function () {
      var sel = $start.datepicker('getDate');
      if (sel) { $end.datepicker('option', 'minDate', sel); }
    });
  } catch (e) {}

  try {
    $end.datepicker('option', 'onSelect', function () {
      var sel = $end.datepicker('getDate');
      if (sel) { $start.datepicker('option', 'maxDate', sel); }
    });
  } catch (e) {}
}

// Bootstrap Datepicker / jQuery UI Datepicker re-init (Timeline-style fix)
// Call this if your page uses Bootstrap Datepicker too.
function initDatepickers($ctx) {
  var isBootstrapDP = !!($.fn.datepicker && $.fn.datepicker.Constructor);

  $ctx.find('input.startDate, input.endDate').each(function () {
    var $inp = $(this);
    var $container = $inp.closest('.blockContent'); // anchor dropdown to avoid clipping

    try {
      if (isBootstrapDP) {
        // Bootstrap Datepicker
        if ($inp.data('datepicker')) {
          try { $inp.datepicker('remove'); } catch (e) {}
          try { $inp.datepicker('destroy'); } catch (e) {}
        }
        $inp.datepicker({
          format: 'yyyy-mm-dd',
          autoclose: true,
          todayHighlight: true,
          orientation: 'auto',
          container: $container.length ? $container : 'body'
        });
      } else if ($.fn.datepicker) {
        // jQuery UI Datepicker
        if ($inp.hasClass('hasDatepicker')) {
          $inp.datepicker('destroy');
        }
        $inp.datepicker({
          dateFormat: 'yy-mm-dd',
          changeMonth: true,
          changeYear: true,
          numberOfMonths: 2
        });
      }
    } catch (e) {
      // Silent catch to avoid breaking cloning if a lib is missing
    }
  });
}

/**
 * Your original date range glue, now used per block.
 * Accepts jQuery objects for start and end within the same block.
 */
function date(start,end) {
  var dateFormat = "yy-mm-dd";
  var from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2023-01-01',
      maxDate: '2029-12-31',
      changeMonth: true,
      numberOfMonths: 2,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(end).datepicker("option", "minDate", selectedDate);
        }
      }
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
    }
  });

  var to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2023-01-01',
      maxDate: '2029-12-31',
      changeMonth: true,
      numberOfMonths: 2,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(start).datepicker("option", "maxDate", selectedDate);
        }
      }
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
    }
  });

  function getDate(element) {
    var date;
    try {
      date = $.datepicker.parseDate(dateFormat, element.value);
    } catch(error) {
      date = null;
    }
    return date;
  }
}
