/**
 * Cluster activities (A2-2742 redesign).
 *
 * The page lists every activity of the phase in one form. The tabs, the search and the collapsed cards only hide
 * activities: a hidden activity is still posted, because the action deletes every activity the form leaves out.
 * An activity is removed by taking its card out of the form; the removal is undone by putting the card back.
 */
$(document).ready(initActivities);

var cacTexts = {};
var cacPool = [];
var cacNewCount = 0;
var cacUndo = null;
var cacToastTimer = null;

function initActivities() {
  var $root = $('[data-cac]');
  if(!$root.length) {
    return;
  }
  cacTexts = $('#cacTexts').data() || {};
  cacPool = $('#cacDeliverablePool li').map(function() {
    return {
        id: String($(this).data('id')),
        url: $(this).data('url'),
        title: $.trim($(this).text())
    };
  }).get();

  var editable = $root.data('editable') === true;

  // Form controls of every activity already in the form; the template is outside it and gets them when cloned
  $root.find('[data-cac-activity]').each(function() {
    initActivityControls($(this), editable);
    reindexDeliverables($(this));
  });

  // ---- Header and list controls ----
  $root.on('click', '[data-cac-note-toggle]', toggleNote);
  $root.on('click', '[data-cac-missing-toggle]', toggleMissing);
  $root.on('click', '[data-cac-tab]', function() {
    selectTab($(this).data('cacTab'));
  });
  $root.on('input', '[data-cac-search]', refreshList);
  $root.on('click', '[data-cac-clear]', function() {
    $('[data-cac-search]').val('').trigger('input').focus();
  });
  $root.on('click', '[data-cac-expand-all]', expandAll);
  $root.on('click', '[data-cac-add]', addActivity);

  // ---- One activity ----
  $root.on('click', '[data-cac-toggle]', function() {
    var $card = $(this).closest('[data-cac-activity]');
    setOpen($card, $card.find('.cac-act__body').prop('hidden'));
  });
  $root.on('click', '[data-cac-remove]', askRemove);
  $root.on('click', '[data-cac-remove-cancel]', function() {
    closeConfirm($(this).closest('[data-cac-activity]'));
  });
  $root.on('click', '[data-cac-remove-confirm]', removeActivity);
  $root.on('click', '[data-cac-undo]', undoRemove);
  $root.on('change', 'input.activityStatus', function() {
    var $card = $(this).closest('[data-cac-activity]');
    syncProgressLabel($card);
    refreshActivity($card);
    refreshList();
  });
  $root.on('click', '[data-cac-mark-complete]', function() {
    $(this).closest('[data-cac-activity]').find('input.activityStatus[value="3"]').prop('checked', true).trigger(
        'change');
  });
  $root.on('change', 'select.activityTitle', function() {
    refreshAll();
  });
  $root.on('input change', 'input.activityTitle, textarea, select.activityLeader, input.startDate, input.endDate',
      function() {
        refreshActivity($(this).closest('[data-cac-activity]'));
        refreshList();
      });

  // ---- Deliverables of an activity ----
  $root.on('focus input', '[data-cac-combo-input]', function() {
    openCombo($(this).closest('[data-cac-combo]'));
  });
  $root.on('keydown', '[data-cac-combo-input]', comboKeys);
  $root.on('mousedown', '[data-cac-combo-list] [role="option"]', function(e) {
    // Before the input loses the focus, which would close the list
    e.preventDefault();
    pickDeliverable($(this));
  });
  $root.on('click', '[data-cac-dl-remove]', removeDeliverable);
  $(document).on('mousedown', function(e) {
    if(!$(e.target).closest('[data-cac-combo]').length) {
      closeCombos();
    }
  });

  // ---- Dates typed by hand ----
  // A format the server cannot read is flagged, and blocks the save while one is left. While typing, the warning is
  // only cleared, so a date half typed is not flagged before the field is left.
  $root.on('change blur', 'input.startDate, input.endDate', function() {
    validateActivityDate($(this));
  });
  $root.on('input', 'input.startDate, input.endDate', function() {
    if(isValidActivityDate($.trim($(this).val() || ''))) {
      validateActivityDate($(this));
    }
  });
  // Capture phase, so it runs before the global Save handler turns the button into its saving state
  document.addEventListener('click', blockSaveOnInvalidDates, true);

  initSaveState($root);
  stripLabelColons($root);

  // The page opens on the On-going set, unless it is empty and the other set is not
  var $cards = $root.find('[data-cac-activity]');
  if(!$cards.filter('[data-set="open"]').length && $cards.filter('[data-set="closed"]').length) {
    selectTab('closed');
  }
  refreshAll();

  // Activities the save sent back with missing fields (fieldsValidation.js marks them first), on the set that
  // holds the first one, and the one in the url
  var $marked = $cards.filter(function() {
    return $(this).find('.fieldError, .missingSelect2, .missingSelect, .errorTag').length > 0;
  });
  if($marked.length) {
    selectTab($marked.first().attr('data-set'));
  }
  $marked.each(function() {
    setOpen($(this), true);
  });
  var hash = window.location.hash.substring(1);
  if(hash) {
    var $target = $root.find('[data-cac-activity]').filter(function() {
      return this.id === hash;
    });
    if($target.length) {
      selectTab($target.attr('data-set'));
      setOpen($target, true);
    }
  }
}

/* ---------------------------------------------------------------------------------------------------------------- */
/* Controls */

function initActivityControls($card,editable) {
  if(!editable) {
    return;
  }
  $card.find('select.activityTitle, select.activityLeader').select2({
    width: '100%'
  });
  var uid = $card.attr('id');
  var $start = $card.find('input.startDate');
  var $end = $card.find('input.endDate');
  if($start.length && $end.length) {
    $start.attr('id', uid + '-start');
    $end.attr('id', uid + '-end');
    $start.closest('.input').find('label').attr('for', uid + '-start');
    $end.closest('.input').find('label').attr('for', uid + '-end');
    date('#' + uid + '-start', '#' + uid + '-end');
  }
}

/* customForm ends every label with a colon ("Title:*"); the design's labels carry none */
function stripLabelColons($scope) {
  $scope.find('.cac-act__body label').each(function() {
    $(this).contents().filter(function() {
      return this.nodeType === 3;
    }).each(function() {
      this.nodeValue = this.nodeValue.replace(/\s*:\s*$/, ' ');
    });
  });
}

function format(template) {
  var args = Array.prototype.slice.call(arguments, 1);
  return String(template || '').replace(/\{(\d+)\}/g, function(match,i) {
    return args[i] !== undefined ? args[i] : match;
  });
}

function wordsIn($field) {
  if(!$field.length) {
    return 0;
  }
  if(typeof word_count === 'function') {
    return word_count($field);
  }
  var value = $.trim($field.val() || '');
  return value ? value.split(/\s+/).length : 0;
}

function formatDate(value) {
  if(!isValidActivityDate(value)) {
    return '';
  }
  var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  var parts = value.split('-');
  return parseInt(parts[2], 10) + ' ' + months[parseInt(parts[1], 10) - 1] + ' ' + parts[0];
}

function initials(name) {
  var parts = $.trim(name || '').split(/\s+/).filter(Boolean);
  if(!parts.length) {
    return '–';
  }
  return ((parts[0][0] || '') + (parts.length > 1 ? parts[parts.length - 1][0] : '')).toUpperCase();
}

/* ---------------------------------------------------------------------------------------------------------------- */
/* Reading an activity */

function activityStatus($card) {
  var $checked = $card.find('input.activityStatus:checked');
  return String($checked.length ? $checked.val() : $card.attr('data-saved-status') || '2');
}

function activityTitle($card) {
  var $root = $('[data-cac]');
  if($root.data('freeTitle') === true) {
    var typed = $.trim($card.find('input.activityTitle').val() || '');
    return {
        id: typed ? 'text' : '',
        text: typed
    };
  }
  var $select = $card.find('select.activityTitle');
  if($select.length) {
    var value = $select[0].value;
    var $option = $select.find('option:selected');
    return {
        id: value && value !== '-1' ? value : '',
        text: value && value !== '-1' ? ($option.attr('data-label') || $option.text()) : ''
    };
  }
  // Locked title: its catalog id is posted from a hidden input and its text is printed
  return {
      id: $card.find('input.activityTitle').val() || '',
      text: $.trim($card.find('[data-cac-field="title"] .cac-readValue').text())
  };
}

function splitTitle(text) {
  var match = /^\s*([0-9]+(?:\.[0-9]+)*)\s+([\s\S]*)$/.exec(text || '');
  return match ? {
      code: match[1],
      name: match[2]
  } : {
      code: '',
      name: text || ''
  };
}

function activityLabel($card) {
  var title = splitTitle(activityTitle($card).text);
  return title.code || title.name || cacTexts.newActivity;
}

function activityLeader($card) {
  var $select = $card.find('select.activityLeader');
  if($select.length) {
    var $option = $select.find('option:selected');
    if(!$select[0].value || $select[0].value === '-1') {
      return null;
    }
    return {
        name: $option.attr('data-name') || $option.text(),
        institution: $option.attr('data-institution') || ''
    };
  }
  var text = $.trim($card.find('[data-cac-field="leader"] .cac-readValue').text());
  var $hidden = $card.find('[data-cac-field="leader"] input[type="hidden"]');
  if(!$hidden.val() || $hidden.val() === '-1') {
    return null;
  }
  var parts = text.split(' · ');
  return {
      name: parts[0],
      institution: parts.slice(1).join(' · ')
  };
}

function dateValue($card,cls) {
  var $input = $card.find('input.' + cls);
  return $.trim($input.val() || '');
}

/** Whether the activity sits in the On-going set when the page is saved */
function isOpenStatus(status) {
  return status === '2' || status === '4';
}

/**
 * The fields the save would flag, as ProjectActivitiesValidator checks them: On-going and Complete activities owe
 * every field, a Cancelled one only the reason, asked in the reporting cycle; an Extended one owes nothing.
 */
function activityIssues($card) {
  var $root = $('[data-cac]');
  var reporting = $root.data('reporting') === true;
  var status = activityStatus($card);
  var issues = {};
  var full = status === '2' || status === '3';

  if(full) {
    var title = activityTitle($card);
    if($root.data('freeTitle') === true) {
      var $titleInput = $card.find('input.activityTitle');
      if(!title.text) {
        issues.title = cacTexts.errorRequired;
      } else if(wordsIn($titleInput) > 30) {
        issues.title = format(cacTexts.errorWords, 30);
      }
    } else if(!title.id) {
      issues.title = cacTexts.errorRequired;
    } else if(titleUsedElsewhere($card, title.id)) {
      issues.title = cacTexts.errorTitleUsed;
    }

    var descriptionWords = wordsIn($card.find('textarea.activityDescription'));
    if(!descriptionWords) {
      issues.description = cacTexts.errorRequired;
    } else if(descriptionWords > 150) {
      issues.description = format(cacTexts.errorWords, 150);
    }
    if(!dateValue($card, 'startDate')) {
      issues.startDate = cacTexts.errorRequired;
    }
    if(!dateValue($card, 'endDate')) {
      issues.endDate = cacTexts.errorRequired;
    }
    if(!activityLeader($card)) {
      issues.leader = cacTexts.errorLeader;
    }
    if(reporting && isOverdue($card)) {
      issues.status = true;
    }
  }

  if(reporting && (full || status === '5')) {
    var progressWords = wordsIn($card.find('textarea.progressDescription'));
    if(!progressWords) {
      issues.progress = cacTexts.errorRequired;
    } else if(progressWords > 150) {
      issues.progress = format(cacTexts.errorWords, 150);
    }
  }
  return issues;
}

/** An On-going activity that ended on or before the end of the reporting year */
function isOverdue($card) {
  if(activityStatus($card) !== '2') {
    return false;
  }
  var end = dateValue($card, 'endDate');
  var year = $('[data-cac]').data('cycleYear');
  return isValidActivityDate(end) && end <= (year + '-12-31');
}

function titleUsedElsewhere($card,titleId) {
  var used = false;
  $('[data-cac] [data-cac-activity]').not($card).each(function() {
    if(activityTitle($(this)).id === titleId) {
      used = true;
      return false;
    }
  });
  return used;
}

/* ---------------------------------------------------------------------------------------------------------------- */
/* Drawing */

function refreshAll() {
  refreshTitleOptions();
  $('[data-cac] [data-cac-activity]').each(function() {
    refreshActivity($(this));
  });
  refreshList();
}

/** Catalog entries taken by another activity cannot be picked again */
function refreshTitleOptions() {
  var $cards = $('[data-cac] [data-cac-activity]');
  var usedBy = {};
  $cards.each(function() {
    var id = activityTitle($(this)).id;
    if(id) {
      usedBy[id] = (usedBy[id] || 0) + 1;
    }
  });
  $cards.find('select.activityTitle').each(function() {
    var own = this.value;
    $(this).find('option').each(function() {
      if(this.value === '-1') {
        return;
      }
      var used = !!usedBy[this.value] && this.value !== own;
      this.disabled = used;
      var label = $(this).attr('data-label') || $(this).text();
      $(this).text(used ? label + ' — ' + cacTexts.titleUsed : label);
    });
  });
}

function refreshActivity($card) {
  var editable = $('[data-cac]').data('editable') === true;
  var reporting = $('[data-cac]').data('reporting') === true;
  var status = activityStatus($card);

  // Summary row
  var title = splitTitle(activityTitle($card).text);
  var isNew = $card.attr('data-new') === 'true';
  $card.find('[data-cac-code]').text(title.code || (!title.name ? cacTexts.newCode : ''));
  $card.find('[data-cac-name]').text(title.name || cacTexts.selectCatalog).attr('title', title.name).toggleClass(
      'is-empty', !title.name);
  $card.toggleClass('is-new', isNew);

  var leader = activityLeader($card);
  $card.find('[data-cac-leader-initials]').text(leader ? initials(leader.name) : '–');
  $card.find('[data-cac-leader-line]').text(
      leader ? leader.name + (leader.institution ? ' · ' + leader.institution : '') : cacTexts.noLeader);

  var start = dateValue($card, 'startDate');
  var end = dateValue($card, 'endDate');
  if(editable || start || end) {
    $card.find('[data-cac-date-line]').text(
        formatDate(start) && formatDate(end) ? formatDate(start) + ' – ' + formatDate(end) : cacTexts.noDates);
  }

  var links = $card.find('[data-cac-dl]').length;
  $card.find('[data-cac-dl-line]').text(
      links === 1 ? cacTexts.deliverablesOne : format(cacTexts.deliverablesMany, links));
  $card.find('[data-cac-dl-count]').text(links);
  $card.find('[data-cac-dl-empty]').prop('hidden', links > 0);

  var $statusOption = $card.find('input.activityStatus:checked').closest('.cac-seg__opt');
  $card.find('[data-cac-status-dot]').attr('class', 'dlv-dot cac-dot--' + status);
  if($statusOption.length) {
    $card.find('[data-cac-status-label]').text($.trim($statusOption.text()));
  }

  var label = activityLabel($card);
  var open = !$card.find('.cac-act__body').prop('hidden');
  $card.find('.cac-act__caret').attr('aria-label', format(open ? cacTexts.collapse : cacTexts.expand, label));
  $card.find('[data-cac-remove]').attr({
      'title': format(cacTexts.remove, label),
      'aria-label': format(cacTexts.remove, label)
  });

  // Where the activity goes on save
  var savedOpen = $card.attr('data-set') === 'open';
  var moves = !isNew && savedOpen !== isOpenStatus(status);
  $card.find('[data-cac-moves]').prop('hidden', !moves).text(savedOpen ? cacTexts.movesClosed : cacTexts.movesOpen);

  if(!editable) {
    return;
  }

  // Fields to fix
  var issues = activityIssues($card);
  $card.find('[data-cac-error]').each(function() {
    var key = $(this).attr('data-cac-error');
    var message = typeof issues[key] === 'string' ? issues[key] : '';
    $(this).text(message).prop('hidden', !message);
    $(this).closest('[data-cac-field]').toggleClass('has-issue', !!message);
  });
  $card.find('[data-cac-hint="leader"]').prop('hidden', !!issues.leader);

  var overdue = !!issues.status;
  $card.find('[data-cac-status-box]').toggleClass('is-overdue', overdue);
  if(reporting) {
    var year = $('[data-cac]').data('cycleYear');
    $card.find('[data-cac-overdue]').prop('hidden', !overdue);
    $card.find('[data-cac-overdue-text]').text(
        format(cacTexts.overdue, formatDate(end), $('[data-cac]').data('phaseLabel'), year));
  }

  var count = Object.keys(issues).length;
  $card.find('[data-cac-issues]').prop('hidden', count === 0).text(
      count === 1 ? cacTexts.issuesOne : format(cacTexts.issuesMany, count));
}

/** Tabs, search, counts, summary, the unmapped deliverables and the empty state */
function refreshList() {
  var $root = $('[data-cac]');
  var $cards = $root.find('[data-cac-activity]');
  var tab = $root.find('[data-cac-tab].is-on').data('cacTab') || 'open';
  var query = $.trim($root.find('[data-cac-search]').val() || '').toLowerCase();

  var counts = {
      open: 0,
      closed: 0
  };
  var visible = 0;
  $cards.each(function() {
    var $card = $(this);
    var set = $card.attr('data-set');
    counts[set]++;
    var leader = activityLeader($card);
    // Read only, the description is printed instead of held in a textarea
    var text = [activityTitle($card).text, $card.find('textarea.activityDescription').val() || '',
        $card.find('[data-cac-field="description"] .textArea > p').text(),
        leader ? leader.name + ' ' + leader.institution : ''].join(' ').toLowerCase();
    // A new activity stays in sight until it is saved, whatever the search
    var shown = set === tab && (!query || text.indexOf(query) > -1 || $card.attr('data-new') === 'true');
    $card.prop('hidden', !shown);
    if(shown) {
      visible++;
    }
  });
  $root.find('[data-cac-count="open"]').text(counts.open);
  $root.find('[data-cac-count="closed"]').text(counts.closed);

  // Deliverables no activity takes yet
  var mapped = {};
  $root.find('[data-cac-dl]').each(function() {
    mapped[String($(this).attr('data-cac-dl'))] = true;
  });
  var unmapped = 0;
  var $missing = $root.find('[data-cac-missing]');
  $missing.find('[data-cac-missing-row]').each(function() {
    var isMapped = !!mapped[String($(this).attr('data-cac-missing-row'))];
    $(this).prop('hidden', isMapped);
    if(!isMapped) {
      unmapped++;
    }
  });
  $missing.prop('hidden', unmapped === 0);
  $missing.find('[data-cac-missing-title]').text(
      unmapped === 1 ? $missing.attr('data-title-one') : format($missing.attr('data-title-many'), unmapped));

  // Summary
  var $summary = $root.find('[data-cac-summary]');
  var parts = [format($summary.attr('data-total'), counts.open + counts.closed),
      format($summary.attr('data-open'), counts.open), format($summary.attr('data-closed'), counts.closed)];
  if(unmapped) {
    parts.push(format($summary.attr('data-unmapped'), unmapped));
  }
  $summary.text(parts.join(' · '));

  // Empty state
  var $empty = $root.find('[data-cac-empty]');
  var kind = query ? 'search' : tab;
  $empty.prop('hidden', visible > 0);
  $empty.find('[data-cac-empty-title]').text($empty.find('[data-cac-empty-title]').attr('data-' + kind));
  $empty.find('[data-cac-empty-text]').text($empty.find('[data-cac-empty-text]').attr('data-' + kind));
  $empty.find('[data-cac-clear]').prop('hidden', !query);

  // Adding is offered once the last new activity got a title; a new activity always lands in the On-going set
  var waiting = $cards.filter('[data-new="true"]').filter(function() {
    return !activityTitle($(this)).id;
  }).length > 0;
  $root.find('[data-cac-add]').prop('disabled', waiting);

  var $shown = $cards.not('[hidden]');
  var allOpen = $shown.length > 0 && $shown.filter(function() {
    return !$(this).find('.cac-act__body').prop('hidden');
  }).length === $shown.length;
  var $expand = $root.find('[data-cac-expand-all]');
  $expand.text(allOpen ? $expand.attr('data-label-collapse') : $expand.attr('data-label-expand'));
}

/* ---------------------------------------------------------------------------------------------------------------- */
/* List actions */

function toggleNote() {
  var $button = $(this);
  var open = $button.attr('aria-expanded') !== 'true';
  $('#' + $button.attr('aria-controls')).prop('hidden', !open);
  $button.attr('aria-expanded', open).text($button.attr(open ? 'data-label-less' : 'data-label-more'));
}

function toggleMissing() {
  var $button = $(this);
  var open = $button.attr('aria-expanded') !== 'true';
  $('#' + $button.attr('aria-controls')).prop('hidden', !open);
  $button.attr('aria-expanded', open);
  $button.find('[data-cac-missing-label]').text($button.attr(open ? 'data-label-hide' : 'data-label-show'));
}

function selectTab(tab) {
  var $root = $('[data-cac]');
  $root.find('[data-cac-tab]').each(function() {
    var on = $(this).data('cacTab') === tab;
    $(this).toggleClass('is-on', on).attr('aria-selected', on);
  });
  $root.find('[data-cac-confirm]').prop('hidden', true);
  $root.find('[data-cac-remove]').attr('aria-expanded', false);
  refreshList();
}

function setOpen($card,open) {
  $card.find('.cac-act__body').prop('hidden', !open);
  $card.toggleClass('is-open', open);
  $card.find('.cac-act__caret, .cac-act__summary').attr('aria-expanded', open);
  if(open) {
    $card.find('textarea').each(function() {
      if($.fn.autoGrow) {
        $(this).autoGrow();
      }
    });
  }
  refreshActivity($card);
  refreshList();
}

function expandAll() {
  var $shown = $('[data-cac] [data-cac-activity]').not('[hidden]');
  var allOpen = $shown.filter(function() {
    return !$(this).find('.cac-act__body').prop('hidden');
  }).length === $shown.length;
  $shown.each(function() {
    setOpen($(this), !allOpen);
  });
}

function nextActivityIndex() {
  var max = -1;
  $('[data-cac] [data-cac-activity] .index').each(function() {
    var value = parseInt($(this).text(), 10);
    if(!isNaN(value) && value > max) {
      max = value;
    }
  });
  return max + 1;
}

function addActivity() {
  var $template = $('#cacActivityTemplate [data-cac-activity]');
  if(!$template.length || $(this).prop('disabled')) {
    return;
  }
  cacNewCount++;
  var index = nextActivityIndex();
  var uid = 'projectActivity-new' + cacNewCount;
  var $card = $template.clone(true);

  // Names, ids and the references to them take the new index and id
  $card.attr('id', uid);
  $card.find('[name]').each(function() {
    this.name = this.name.replace('projectActivities[-1]', 'projectActivities[' + index + ']');
  });
  $card.find('[id], [for], [aria-controls], [aria-labelledby]').each(function() {
    var $el = $(this);
    $.each(['id', 'for', 'aria-controls', 'aria-labelledby'], function(i,attr) {
      var value = $el.attr(attr);
      if(value) {
        $el.attr(attr, value.replace('projectActivity-template', uid).replace('act-template', 'act-new' + cacNewCount)
            .replace('projectActivities[-1]', 'projectActivities[' + index + ']'));
      }
    });
  });
  $card.find('.index').text(index);

  // The toolbar button adds at the top of the list, the one under the list adds at its end, next to the button
  var atEnd = $(this).hasClass('cac-addBottom');
  var $root = $('[data-cac]');
  $root.find('[data-cac-search]').val('');
  selectTab('open');
  var $openCards = $root.find('[data-cac-activity][data-set="open"]');
  if(atEnd && $openCards.length) {
    $openCards.last().after($card);
  } else if($openCards.length) {
    $openCards.first().before($card);
  } else {
    $root.find('[data-cac-list]').prepend($card);
  }
  initActivityControls($card, true);
  syncProgressLabel($card);
  stripLabelColons($card);
  refreshAll();
  setOpen($card, true);
  if(atEnd) {
    $card[0].scrollIntoView({ block: 'start', behavior: 'smooth' });
  }
  $card.find('select.activityTitle').select2('open');
  $card.find('input.activityTitle').focus();
  $(this).closest('form').trigger('cac:changed');
}

function askRemove() {
  var $card = $(this).closest('[data-cac-activity]');
  var $confirm = $card.find('[data-cac-confirm]');
  var opening = $confirm.prop('hidden');
  $('[data-cac] [data-cac-confirm]').prop('hidden', true);
  $('[data-cac] [data-cac-remove]').attr('aria-expanded', false);
  if(!opening) {
    return;
  }
  var title = activityTitle($card).text;
  var links = $card.find('[data-cac-dl]').length;
  var text = format(cacTexts.removeConfirm, title || cacTexts.newActivity);
  if(links) {
    text += ' ' + format(cacTexts.removeLinks, links);
  }
  text += ' ' + cacTexts.removeNote;
  $card.find('[data-cac-confirm-text]').text(text);
  $confirm.prop('hidden', false);
  $(this).attr('aria-expanded', true);
  $confirm.find('[data-cac-remove-cancel]').focus();
}

function closeConfirm($card) {
  $card.find('[data-cac-confirm]').prop('hidden', true);
  $card.find('[data-cac-remove]').attr('aria-expanded', false).focus();
}

function removeActivity() {
  var $card = $(this).closest('[data-cac-activity]');
  var $form = $card.closest('form');
  var label = activityLabel($card);
  var $after = $card.prev();
  var $list = $card.parent();
  $card.find('[data-cac-confirm]').prop('hidden', true);
  $card.find('[data-cac-remove]').attr('aria-expanded', false);
  // Detached, the card keeps its controls and data, so the undo puts it back as it was
  $card.detach();
  cacUndo = function() {
    if($after.length && $.contains(document, $after[0])) {
      $after.after($card);
    } else {
      $list.prepend($card);
    }
    refreshAll();
    $form.trigger('cac:changed');
  };
  refreshAll();
  showToast(format(cacTexts.removed, label), true);
  $form.trigger('cac:changed');
}

function undoRemove() {
  if(cacUndo) {
    cacUndo();
    cacUndo = null;
  }
  hideToast();
}

function showToast(text,canUndo) {
  var $toast = $('[data-cac-toast]');
  clearTimeout(cacToastTimer);
  $toast.find('[data-cac-toast-text]').text(text);
  $toast.find('[data-cac-undo]').prop('hidden', !canUndo);
  $toast.prop('hidden', false);
  cacToastTimer = setTimeout(hideToast, 7000);
}

function hideToast() {
  clearTimeout(cacToastTimer);
  $('[data-cac-toast]').prop('hidden', true);
  cacUndo = null;
}

/* ---------------------------------------------------------------------------------------------------------------- */
/* Status */

/** The progress field asks what the status needs. The text the user wrote is kept when the status changes. */
function syncProgressLabel($card) {
  var status = activityStatus($card);
  var $label = $card.find('textarea.progressDescription').closest('.textArea').find('label').first();
  var text = $('#cacProgressLabels [data-status="' + status + '"]').text();
  if(!$label.length || !text) {
    return;
  }
  var $textNode = $label.contents().filter(function() {
    return this.nodeType === 3 && $.trim(this.nodeValue);
  }).first();
  if($textNode.length) {
    $textNode[0].nodeValue = $.trim(text) + ' ';
  } else {
    $label.prepend(document.createTextNode($.trim(text) + ' '));
  }
}

/* ---------------------------------------------------------------------------------------------------------------- */
/* Deliverables of an activity */

/** The action binds deliverables[0..n] and stops at the first gap, so the rows are numbered without one */
function reindexDeliverables($card) {
  var index = $card.find('.index').text();
  $card.find('[data-cac-dl]').each(function(i) {
    $(this).find('input').each(function() {
      this.name = this.name.replace(/projectActivities\[-?\d+\]\.deliverables\[-?\d+\]/,
          'projectActivities[' + index + '].deliverables[' + i + ']');
    });
  });
}

function mappedDeliverables() {
  var mapped = {};
  $('[data-cac] [data-cac-dl]').each(function() {
    mapped[String($(this).attr('data-cac-dl'))] = true;
  });
  return mapped;
}

function openCombo($combo) {
  var $card = $combo.closest('[data-cac-activity]');
  var $input = $combo.find('[data-cac-combo-input]');
  var query = $.trim($input.val() || '').toLowerCase();
  var own = {};
  $card.find('[data-cac-dl]').each(function() {
    own[String($(this).attr('data-cac-dl'))] = true;
  });
  var mapped = mappedDeliverables();
  var rows = $.grep(cacPool, function(d) {
    return !own[d.id] && (!query || ('d' + d.id + ' ' + d.title).toLowerCase().indexOf(query) > -1);
  });
  // The deliverables no activity takes yet come first
  rows.sort(function(a,b) {
    return (mapped[a.id] ? 1 : 0) - (mapped[b.id] ? 1 : 0);
  });

  var $list = $combo.find('[data-cac-combo-list]').empty();
  $.each(rows, function(i,d) {
    var $option = $('<li role="option" class="cac-combo__option" aria-selected="false"></li>').attr({
        'id': $list.attr('id') + '-' + d.id,
        'data-id': d.id
    });
    $option.append($('<span class="cac-dlId"></span>').text('D' + d.id));
    $option.append($('<span class="cac-combo__title"></span>').text(d.title));
    if(!mapped[d.id]) {
      $option.append($('<span class="cac-tagWarn"></span>').text(cacTexts.notMapped));
    }
    $list.append($option);
  });
  $list.prop('hidden', rows.length === 0);
  $combo.find('[data-cac-combo-none]').prop('hidden', rows.length > 0);
  $input.attr('aria-expanded', true).removeAttr('aria-activedescendant');
}

function closeCombos() {
  $('[data-cac] [data-cac-combo]').each(function() {
    $(this).find('[data-cac-combo-list]').prop('hidden', true).empty();
    $(this).find('[data-cac-combo-none]').prop('hidden', true);
    $(this).find('[data-cac-combo-input]').attr('aria-expanded', false).removeAttr('aria-activedescendant');
  });
}

function comboKeys(e) {
  var $combo = $(this).closest('[data-cac-combo]');
  var $options = $combo.find('[role="option"]');
  var $active = $options.filter('[aria-selected="true"]');
  var index = $options.index($active);
  if(e.key === 'ArrowDown' || e.key === 'ArrowUp') {
    e.preventDefault();
    if($combo.find('[data-cac-combo-list]').prop('hidden')) {
      openCombo($combo);
      $options = $combo.find('[role="option"]');
    }
    index = e.key === 'ArrowDown' ? Math.min(index + 1, $options.length - 1) : Math.max(index - 1, 0);
    $options.attr('aria-selected', false);
    var $next = $options.eq(index).attr('aria-selected', true);
    if($next.length) {
      $(this).attr('aria-activedescendant', $next.attr('id'));
      $next[0].scrollIntoView({
        block: 'nearest'
      });
    }
  } else if(e.key === 'Enter') {
    // Never submits the form
    e.preventDefault();
    if($active.length) {
      pickDeliverable($active);
    }
  } else if(e.key === 'Escape') {
    closeCombos();
  }
}

function pickDeliverable($option) {
  var id = String($option.attr('data-id'));
  var $card = $option.closest('[data-cac-activity]');
  var deliverable = $.grep(cacPool, function(d) {
    return d.id === id;
  })[0];
  if(!deliverable || $card.find('[data-cac-dl="' + id + '"]').length) {
    return;
  }
  var $row = $('<li class="cac-dl"></li>').attr('data-cac-dl', id);
  $row.append($('<input type="hidden" class="cac-dl__deliverable">').attr('name',
      'project.projectActivities[-1].deliverables[-1].deliverable.id').val(id));
  $row.append($('<input type="hidden" class="cac-dl__link">').attr('name',
      'project.projectActivities[-1].deliverables[-1].id').val('-1'));
  $row.append($('<span class="cac-dlId"></span>').text('D' + id));
  $row.append($('<a class="cac-dlTitle"></a>').attr('href', deliverable.url).text(deliverable.title));
  var removeLabel = format(cacTexts.removeDeliverable, id);
  $row.append($('<button type="button" class="dlv-iconBtn dlv-iconBtn--danger cac-dl__remove" data-cac-dl-remove>')
      .attr({
          'title': removeLabel,
          'aria-label': removeLabel
      })
      .html('<svg width="10" height="10" viewBox="0 0 14 14" fill="none" aria-hidden="true">'
          + '<path d="M3 3l8 8M11 3l-8 8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>'));
  $card.find('[data-cac-dl-list]').append($row);
  reindexDeliverables($card);

  var $input = $card.find('[data-cac-combo-input]');
  $input.val('');
  closeCombos();
  refreshActivity($card);
  refreshList();
  $input.trigger('change');
}

function removeDeliverable() {
  var $card = $(this).closest('[data-cac-activity]');
  var $row = $(this).closest('[data-cac-dl]');
  var $next = $row.next().find('[data-cac-dl-remove]');
  $row.remove();
  reindexDeliverables($card);
  refreshActivity($card);
  refreshList();
  ($next.length ? $next : $card.find('[data-cac-combo-input]')).focus();
  $card.closest('form').trigger('cac:changed');
}

/* ---------------------------------------------------------------------------------------------------------------- */
/* Save state */

/** The state compares the form with the one the page loaded, so undoing an edit brings "All changes saved" back */
function initSaveState($root) {
  var $saveBar = $root.find('.cac-saveBar');
  var $state = $saveBar.find('[data-cac-save-state]');
  var $form = $saveBar.closest('form');
  if(!$.trim($saveBar.text()) && !$saveBar.find('button:visible, a.button-save:visible').length) {
    $saveBar.hide();
  }
  if(!$state.length || !$form.length) {
    return;
  }
  var loaded = null;
  function formState() {
    return $form.find(':input').not('[data-cac-search], [data-cac-combo-input]').serialize();
  }
  function refresh() {
    if(loaded === null) {
      return;
    }
    var dirty = formState() !== loaded;
    $saveBar.toggleClass('is-dirty', dirty);
    $state.text($state.attr(dirty ? 'data-unsaved' : 'data-saved'));
  }
  setTimeout(function() {
    loaded = formState();
  }, 0);
  $form.on('input change', 'input, textarea, select', refresh);
  $form.on('cac:changed', refresh);
}

/* ---------------------------------------------------------------------------------------------------------------- */
/* Dates */

function date(start,end) {
  var dateFormat = 'yy-mm-dd';
  var maxYear = window.globalMaxYear || 2031;
  var maxDate = maxYear + '-12-31';

  $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2015-01-01',
      maxDate: maxDate,
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1);
        $(this).datepicker('setDate', selectedDate);
        $(end).datepicker('option', 'minDate', selectedDate);
        // setDate fires no change event, and the minDate option clears an unreadable end date
        validateActivityDate($(start));
        validateActivityDate($(end));
        $(start).trigger('change');
      },
      onSelect: function() {
        $(this).trigger('change');
      }
  });

  $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2015-01-01',
      maxDate: maxDate,
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        $(this).datepicker('setDate', selectedDate);
        $(start).datepicker('option', 'maxDate', selectedDate);
        // setDate fires no change event, and the maxDate option clears an unreadable start date
        validateActivityDate($(start));
        validateActivityDate($(end));
        $(end).trigger('change');
      },
      onSelect: function() {
        $(this).trigger('change');
      }
  });
}

/**
 * Flags a start or end date whose text is not a real yyyy-mm-dd date. The server cannot convert such a value, and
 * then refuses the whole form: nothing is saved and the page comes back read only, without saying why.
 * An empty date is left to the missing-fields check, which does not block the save.
 * @param $input the start or end date input
 * @returns true when the date is empty or valid
 */
function validateActivityDate($input) {
  var value = $.trim($input.val() || '');
  var isValid = value == '' || isValidActivityDate(value);
  var $field = $input.closest('.input');
  $field.toggleClass('has-error', !isValid);
  $field.find('.activityDateError').remove();
  if(!isValid) {
    $("<span class='help-block activityDateError'></span>").text(cacTexts.dateInvalid).insertAfter($input);
  }
  return isValid;
}

function isValidActivityDate(value) {
  if(!/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return false;
  }
  var date;
  try {
    date = $.datepicker.parseDate('yy-mm-dd', value);
  } catch(error) {
    return false;
  }
  // A day that does not exist, such as 2026-02-30, must not pass as the next valid one
  return $.datepicker.formatDate('yy-mm-dd', date) == value;
}

/**
 * Stops the save while a date the server cannot read is left, and opens the activity that holds the first one.
 * @param e the click event, seen in the capture phase
 */
function blockSaveOnInvalidDates(e) {
  var $button = $(e.target).closest('button[name="save"]');
  if(!$button.length) {
    return;
  }
  var $invalid = $('[data-cac] [data-cac-activity]').find('input.startDate, input.endDate').not('[type=hidden]')
      .filter(function() {
        return !validateActivityDate($(this));
      });
  if($invalid.length == 0) {
    return;
  }
  e.preventDefault();
  e.stopImmediatePropagation();
  // The unsaved-changes dialog (changes.js) clicks Save through jQuery, so the global Save handler has already
  // run: undo its saving state, or the disabled button would ignore every later click, and drop the pending
  // redirection so the next save stays on this page
  if(typeof turnSavingStateOff === 'function') {
    turnSavingStateOff($button);
  }
  $('.container_page_load').hide();
  $('#redirectionUrl').val('');
  var $activity = $invalid.first().closest('[data-cac-activity]');
  selectTab($activity.attr('data-set'));
  setOpen($activity, true);
  $('html, body').animate({
    scrollTop: $activity.offset().top - 110
  }, 500);
}
