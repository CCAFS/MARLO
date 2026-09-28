/*
 * Planning / Reporting cycles (admin -> crpPhases).
 *
 * The page posts the whole phase list back through Struts indexed parameters, so
 * nothing here ever adds or removes a row: rows are only ever hidden. A filtered
 * row keeps its inputs in the form and keeps submitting them.
 *
 * Three things are kept in sync with the radios as they are clicked -- the row's
 * open/landing styling, the counts in the year headers and the subtitle, and the
 * unsaved-changes bar at the foot of the card.
 */
$(document).ready(init);

var $phaseCycles;
var $cycRows;
var cycEditable = false;
var cycBaseline = null;
var cycFilters = {
  query: '',
  type: 'all',
  year: 'all',
  openOnly: false
};

function init() {

  $phaseCycles = $('#phaseCycles');

  if ($phaseCycles.length) {
    $cycRows = $phaseCycles.find('.cycRow');
    cycEditable = $phaseCycles.attr('data-editable') === 'true';
    cycBaseline = cycSnapshot();
    cycRefresh();
  }

  /* Declaring Events */
  attachEvents();

  // Last: the date fields are hidden on this screen, so a picker that fails to
  // initialise must not be able to take the filters and the save bar with it.
  setDatePickers();
}

function attachEvents() {

  $('.button-save').on('click', function(e) {
    var visiblePhases = $('input.visible-yes:checked').length;

    // Validate if there is a valid phase
    if(visiblePhases < 1) {
      e.preventDefault();

      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = "You must switch at least one phase to visible";
      noty(notyOptions);

      // Turn off the saving button state
      turnSavingStateOff(this);

      // global.js opens the full-page loader on every .button-save click; the
      // submit never happens here, so it has to be taken back down.
      closeLoadPage();

      return
    }

  });

  if (!$phaseCycles || !$phaseCycles.length) {
    return;
  }

  // Any switch changes the row, the counters and the unsaved-changes bar.
  $phaseCycles.on('change', '.cycSwitch__input, .cycLanding__input', function() {
    cycRefresh();
  });

  // Search
  $phaseCycles.on('input', '.cycSearch__input', function() {
    cycFilters.query = $.trim(this.value).toLowerCase();
    $phaseCycles.find('.cycSearch__clear').prop('hidden', cycFilters.query.length === 0);
    cycApplyFilters();
  });

  $phaseCycles.on('click', '.cycSearch__clear', function() {
    cycFilters.query = '';
    $phaseCycles.find('.cycSearch__input').val('').focus();
    $(this).prop('hidden', true);
    cycApplyFilters();
  });

  // Phase kind
  $phaseCycles.on('click', '.cycTabs__item', function() {
    cycFilters.type = $(this).attr('data-type');
    cycSelect($phaseCycles.find('.cycTabs__item'), this);
    cycApplyFilters();
  });

  // Year
  $phaseCycles.on('click', '.cycYear', function() {
    cycFilters.year = $(this).attr('data-year');
    cycSelect($phaseCycles.find('.cycYear'), this);
    cycApplyFilters();
  });

  // Open only
  $phaseCycles.on('click', '.cycToggle', function() {
    cycFilters.openOnly = !cycFilters.openOnly;
    $(this).toggleClass('is-selected', cycFilters.openOnly).attr('aria-pressed', cycFilters.openOnly);
    cycApplyFilters();
  });

  $phaseCycles.on('click', '.cycEmpty__reset', function() {
    cycResetFilters();
  });

  $phaseCycles.on('click', '.cycFooter__discard', function() {
    cycDiscard();
  });
}

/* ---------------------------------------------------------------- filtering */

function cycSelect($group, selected) {
  $group.removeClass('is-selected').attr('aria-pressed', 'false');
  $(selected).addClass('is-selected').attr('aria-pressed', 'true');
}

function cycResetFilters() {
  cycFilters.query = '';
  cycFilters.type = 'all';
  cycFilters.year = 'all';
  cycFilters.openOnly = false;

  $phaseCycles.find('.cycSearch__input').val('');
  $phaseCycles.find('.cycSearch__clear').prop('hidden', true);
  cycSelect($phaseCycles.find('.cycTabs__item'), $phaseCycles.find('.cycTabs__item[data-type="all"]'));
  cycSelect($phaseCycles.find('.cycYear'), $phaseCycles.find('.cycYear[data-year="all"]'));
  $phaseCycles.find('.cycToggle').removeClass('is-selected').attr('aria-pressed', 'false');

  cycApplyFilters();
}

function cycMatches($row) {
  if (cycFilters.type !== 'all' && $row.attr('data-name') !== cycFilters.type) {
    return false;
  }
  if (cycFilters.year !== 'all' && $row.attr('data-year') !== cycFilters.year) {
    return false;
  }
  if (cycFilters.openOnly && !cycIsOpen($row)) {
    return false;
  }
  if (cycFilters.query && $row.attr('data-search').indexOf(cycFilters.query) === -1) {
    return false;
  }
  return true;
}

function cycApplyFilters() {
  var shown = 0;

  $cycRows.each(function() {
    var $row = $(this);
    var visible = cycMatches($row);
    $row.prop('hidden', !visible);
    if (visible) {
      shown++;
    }
  });

  // A year header only makes sense while it still has rows under it.
  $phaseCycles.find('.cycGroup').each(function() {
    var $group = $(this);
    var $visibleRows = $group.find('.cycRow').not('[hidden]');
    $group.prop('hidden', $visibleRows.length === 0);
    cycWriteGroupCounts($group, $visibleRows);
  });

  cycWriteResultLabel(shown);

  var $empty = $phaseCycles.find('.cycEmpty');
  $empty.prop('hidden', shown !== 0);
  if (shown === 0) {
    $empty.find('.cycEmpty__text').text(cycText('empty', [cycFilters.query]));
  }
}

function cycWriteGroupCounts($group, $visibleRows) {
  var total = $visibleRows.length;
  var open = $visibleRows.filter('.is-open').length;

  $group.find('.cycGroup__count').text(cycText(total === 1 ? 'phase-one' : 'phase-many', [total]));
  $group.find('.cycGroup__openWrap').prop('hidden', open === 0);
  $group.find('.cycGroup__open').text(cycText('open', [open]));
}

function cycWriteResultLabel(shown) {
  var total = $cycRows.length;
  $phaseCycles.find('.cycResult')
    .text(shown === total ? cycText('showing-all', [total]) : cycText('showing', [shown, total]));
}

/* ------------------------------------------------------------- live  state */

function cycIsOpen($row) {
  var $open = $row.find('.editable-yes');
  return $open.length ? $open.prop('checked') : $row.hasClass('is-open');
}

function cycIsLanding($row) {
  var $landing = $row.find('.cycLanding__input');
  return $landing.length ? $landing.prop('checked') : $row.hasClass('is-landing');
}

function cycRefresh() {
  var open = 0;

  $cycRows.each(function() {
    var $row = $(this);
    var isOpen = cycIsOpen($row);
    $row.toggleClass('is-open', isOpen);
    $row.toggleClass('is-landing', cycIsLanding($row));
    if (isOpen) {
      open++;
    }
  });

  $phaseCycles.find('.phaseCycles__openCount').text(cycText('open', [open]));

  cycApplyFilters();
  cycWriteFooter();
}

/* ------------------------------------------------------------ unsaved state */

function cycRowValue($row) {
  return ($row.find('.visible-yes').prop('checked') ? '1' : '0')
    + '|' + ($row.find('.editable-yes').prop('checked') ? '1' : '0');
}

function cycSnapshot() {
  var snapshot = {
    rows: [],
    landing: $phaseCycles.find('.cycLanding__input:checked').val() || ''
  };
  $cycRows.each(function() {
    snapshot.rows.push(cycRowValue($(this)));
  });
  return snapshot;
}

function cycCountChanges() {
  if (!cycEditable || !cycBaseline) {
    return 0;
  }

  var changes = 0;
  $cycRows.each(function(index) {
    if (cycRowValue($(this)) !== cycBaseline.rows[index]) {
      changes++;
    }
  });

  var landing = $phaseCycles.find('.cycLanding__input:checked').val() || '';
  if (landing !== cycBaseline.landing) {
    changes++;
  }

  return changes;
}

function cycWriteFooter() {
  if (!cycEditable) {
    return;
  }

  var changes = cycCountChanges();
  var $footer = $phaseCycles.find('.cycFooter');

  $footer.toggleClass('is-dirty', changes > 0);
  $footer.find('.cycFooter__text').text(changes === 0
    ? $phaseCycles.attr('data-label-saved')
    : cycText(changes === 1 ? 'changed-one' : 'changed-many', [changes]));
  $footer.find('.cycFooter__discard').prop('disabled', changes === 0);
}

function cycDiscard() {
  if (!cycBaseline) {
    return;
  }

  $cycRows.each(function(index) {
    var $row = $(this);
    var values = cycBaseline.rows[index].split('|');
    $row.find(values[0] === '1' ? '.visible-yes' : '.visible-no').prop('checked', true);
    $row.find(values[1] === '1' ? '.editable-yes' : '.editable-no').prop('checked', true);
  });

  var $landing = $phaseCycles.find('.cycLanding__input');
  $landing.prop('checked', false);
  if (cycBaseline.landing) {
    $landing.filter('[value="' + cycBaseline.landing + '"]').prop('checked', true);
  }

  cycRefresh();
}

/* ------------------------------------------------------------------- labels */

/* The bundle owns every string; the templates arrive as data-tpl-* attributes
   with {0}/{1} placeholders, exactly as the dashboard schedule card does it. */
function cycText(key, values) {
  var template = $phaseCycles.attr('data-tpl-' + key) || '';
  $.each(values, function(index, value) {
    template = template.split('{' + index + '}').join(value);
  });
  return template;
}

/* ------------------------------------------------------------- date pickers */

function setDatePickers() {
  var datePickerOptions = {
      format: "mmm d, yyyy",
      formatSubmit: "yyyy-mm-dd",
      hiddenName: true,
      selectYears: true,
      selectMonths: true
  }

  $('.cycRow').each(function(i,e) {
    var $startDate = $(e).find('.startDate');
    var $endDate = $(e).find('.endDate');
    var startDatePicker, endDatePicker;

    if(!$startDate.length || !$endDate.length) {
      return;
    }

    // Set date pickers
    $startDate.pickadate(datePickerOptions);
    $endDate.pickadate(datePickerOptions);

    // Instance picker component
    startDatePicker = $startDate.pickadate('picker');
    endDatePicker = $endDate.pickadate('picker');

    // Set parameters an events
    startDatePicker.set('max', endDatePicker.get());
    startDatePicker.on('close', function() {
      endDatePicker.set('min', startDatePicker.get());
    });

    endDatePicker.set('min', startDatePicker.get());
    endDatePicker.on('close', function() {
      startDatePicker.set('max', endDatePicker.get());
    })

  });
}
