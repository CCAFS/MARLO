/*
 * Review screen of the superadmin Institutions page (CHG-PARTREQ-001).
 * - prqDialog owns every dialog of the page (role, focus in, Tab trap, Escape, focus back to the opener).
 * - Decisions are POSTs; the JSON "outcome" (and the HTTP status of a refused call) selects an inline note.
 * - Every string comes from the data- attributes the FreeMarker view renders on #prqPage (i18n).
 */
var prqDialog = (function($) {
  var FOCUSABLE = 'a[href], button, input, select, textarea, summary, [tabindex]';
  var $active = null;
  var opener = null;
  var busy = false;

  function isVisible(el) {
    return !!(el.offsetWidth || el.offsetHeight || el.getClientRects().length);
  }

  function focusables($panel) {
    return $panel.find(FOCUSABLE).filter(function() {
      return !this.disabled && this.tabIndex >= 0 && isVisible(this);
    });
  }

  // The first field of the dialog, else its primary button, else the panel itself
  function firstFocus($panel) {
    var $field = $panel.find('.prq-dialog__body').find('textarea, select, input:not([type="hidden"])')
        .filter(function() {
          return !this.disabled && isVisible(this);
        }).first();
    if ($field.length) {
      return $field;
    }
    var $primary = $panel.find('.prq-dialog__foot [data-prq-confirm]').first();
    return $primary.length ? $primary : $panel;
  }

  function open(id, from) {
    if ($active) {
      close({focus: false});
    }
    var $dialog = $('#' + id);
    var $panel = $dialog.find('.prq-dialog__panel').first();
    $panel.attr({
      'role': 'dialog',
      'aria-modal': 'true',
      'aria-labelledby': $dialog.find('.prq-dialog__title').first().attr('id')
    });
    opener = from ? $(from)[0] : document.activeElement;
    busy = false;
    $dialog.prop('hidden', false);
    $('html').addClass('prq-dialogOpen');
    $active = $dialog;
    firstFocus($panel).focus();
    return $dialog;
  }

  // options.focus === false: the caller moves the focus itself (the opener is gone or an outcome takes it)
  function close(options) {
    if (!$active) {
      return;
    }
    $active.prop('hidden', true);
    $active.find('.prq-dialog__panel button').prop('disabled', false).removeClass('is-disabled');
    $active.find('.prq-dialog__panel').attr('aria-busy', 'false');
    $('html').removeClass('prq-dialogOpen');
    $active = null;
    busy = false;
    var back = opener;
    opener = null;
    if (!(options && options.focus === false) && back && $.contains(document.documentElement, back)
        && isVisible(back)) {
      back.focus();
    }
  }

  // While a request is in flight the dialog neither closes nor accepts a second click
  function setBusy(on) {
    if (!$active) {
      return;
    }
    var $panel = $active.find('.prq-dialog__panel').first();
    busy = on;
    $panel.find('button').prop('disabled', on).toggleClass('is-disabled', on);
    $panel.attr('aria-busy', on ? 'true' : 'false');
    if (on) {
      $panel.focus();
    }
  }

  $(document).on('keydown', function(e) {
    if (!$active) {
      return;
    }
    if (e.key === 'Escape' || e.which === 27) {
      e.preventDefault();
      if (!busy) {
        close();
      }
      return;
    }
    if (e.key !== 'Tab' && e.which !== 9) {
      return;
    }
    var $panel = $active.find('.prq-dialog__panel').first();
    var $items = focusables($panel);
    if (!$items.length) {
      e.preventDefault();
      $panel.focus();
      return;
    }
    var first = $items[0];
    var last = $items[$items.length - 1];
    var current = document.activeElement;
    var inside = $panel[0].contains(current) && current !== $panel[0];
    if (e.shiftKey) {
      if (!inside || current === first) {
        e.preventDefault();
        last.focus();
      }
    } else if (!inside || current === last) {
      e.preventDefault();
      first.focus();
    }
  });

  $(document).on('click', '[data-prq-dialog] [data-prq-close]', function(e) {
    e.preventDefault();
    if (!busy) {
      close();
    }
  });

  return {open: open, close: close, setBusy: setBusy};
})(jQuery);

$(document).ready(function() {
  var $page = $('#prqPage');
  var $notes = $('#prqNotes');
  var PENDING_NOTES = 'prqPendingNotes';
  var PENDING_FILTER = 'prqPendingFilter';
  var MAX_PAGE_NOTES = 5;
  // A request in one of these statuses is no longer pending: its card (or office row) leaves the list
  var CLOSED = {done: true, alreadyHandled: true, notFound: true, skippedExisting: true};
  var pending = null; // what the open dialog will decide
  var KINDS = ['partners', 'offices'];
  var SEARCH_DELAY = 150;
  var filter = {q: '', seg: 'all'}; // q is the normalised query; the field keeps what was typed
  var searchTimer = null;
  var $search = $('#prqSearch');

  /* ---------- text, notes and counts ---------- */

  function text(key) {
    return $page.attr('data-' + key) || '';
  }

  function outcomeText(status) {
    return text('outcome-' + String(status).toLowerCase()) || text('outcome-failed');
  }

  function noteKind(status) {
    if (status === 'done') {
      return 'success';
    }
    return status === 'failed' ? 'danger' : 'warning';
  }

  function statusOf(data) {
    if (data && data.outcome) {
      return data.outcome;
    }
    return data && data.success ? 'done' : 'failed';
  }

  // A refused call (403) is "not allowed"; an expired session (401) asks the reviewer to sign in again
  function statusOfError(xhr) {
    if (xhr && xhr.status === 401) {
      return 'sessionExpired';
    }
    return (xhr && xhr.status === 403) ? 'notAuthorized' : 'failed';
  }

  function buildNote(status, label) {
    var $note = $('<p class="prq-note" tabindex="-1" data-prq-js></p>').addClass('prq-note--' + noteKind(status));
    var $text = $('<span class="prq-note__text"></span>');
    if (label) {
      $text.append($('<strong></strong>').text(label)).append(document.createTextNode(' '));
    }
    $text.append(document.createTextNode(outcomeText(status)));
    return $note.append($text);
  }

  // Page note: the live region #prqNotes announces it; the newest MAX_PAGE_NOTES stay
  function pageNote(status, label) {
    var $note = buildNote(status, label);
    $notes.append($note);
    $notes.children('[data-prq-js]').slice(0, -MAX_PAGE_NOTES).remove();
    return $note;
  }

  // Card note: for a card that stays (failed, not authorized...), shown at the end of the card body, above the
  // buttons the reviewer used
  function cardNote($card, status, label) {
    var $note = buildNote(status, label).attr('role', status === 'done' ? 'status' : 'alert');
    $card.find('.prq-card__body').first().append($note);
    return $note;
  }

  function clearCardNotes($card) {
    $card.find('[data-prq-js]').remove();
  }

  function queueNote(status, label) {
    try {
      var queue = JSON.parse(window.sessionStorage.getItem(PENDING_NOTES) || '[]');
      queue.push({status: status, label: label});
      window.sessionStorage.setItem(PENDING_NOTES, JSON.stringify(queue));
      return true;
    } catch (err) {
      return false;
    }
  }

  // A note queued before a reload (an edit re-renders the card with its changes)
  function showQueuedNotes() {
    var queue = [];
    try {
      queue = JSON.parse(window.sessionStorage.getItem(PENDING_NOTES) || '[]');
      window.sessionStorage.removeItem(PENDING_NOTES);
    } catch (err) {
      queue = [];
    }
    $.each(queue, function(i, item) {
      pageNote(item.status, item.label);
    });
  }

  // The pending total of each kind; a list that ran out of cards swaps to its "nothing pending" state
  function refreshCounts() {
    $.each(KINDS, function(i, kind) {
      var $list = $('[data-prq-list="' + kind + '"]');
      if ($list.children('li').length === 0) {
        $list.prop('hidden', true);
        $list.siblings('[data-prq-empty]').prop('hidden', false);
      }
    });
    applyFilter();
  }

  /* ---------- search and kind filter (client-side: the cards are all in the page) ---------- */

  // Lower case, no accents, single spaces: the same form for the query and for the text of a card
  function normalize(value) {
    var out = String(value || '').toLowerCase();
    if (out.normalize) {
      out = out.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
    }
    return $.trim(out.replace(/\s+/g, ' '));
  }

  // What a card answers to: its own data-prq-search plus the one of each office row still in it
  function searchTextOf($card) {
    var parts = [$card.attr('data-prq-search') || ''];
    $card.find('.officeCountryRequest').each(function() {
      parts.push($(this).attr('data-prq-search') || '');
    });
    return normalize(parts.join(' '));
  }

  function fill(template, first, second) {
    return template.replace('{0}', first).replace('{1}', second);
  }

  function filterActive() {
    return filter.q !== '' || filter.seg !== 'all';
  }

  // Shows the cards that match the query and the kind, and keeps every count consistent with them.
  // Counts say "x of y" only for a section the filter narrowed; the rest show the plain total.
  function applyFilter() {
    var tokens = filter.q ? filter.q.split(' ') : [];
    var matched = {};
    var totals = {};

    $.each(KINDS, function(i, kind) {
      var kindOn = filter.seg === 'all' || filter.seg === kind;
      var total = 0;
      var found = 0;
      $('[data-prq-list="' + kind + '"]').children('li').each(function() {
        var $card = $(this);
        var hay = tokens.length ? searchTextOf($card) : '';
        var ok = true;
        $.each(tokens, function(j, token) {
          ok = hay.indexOf(token) > -1;
          return ok;
        });
        total++;
        found += ok ? 1 : 0;
        $card.prop('hidden', !(ok && kindOn));
      });
      matched[kind] = found;
      totals[kind] = total;

      var narrowed = found < total;
      var $section = $('[data-prq-section="' + kind + '"]');
      $section.prop('hidden', !kindOn);
      $section.find('[data-prq-nomatch]').prop('hidden', !(kindOn && total > 0 && found === 0));
      $('[data-prq-count="' + kind + '"]').text(narrowed ? fill(text('text-of'), found, total) : total);

      var $summary = $('[data-prq-summary="' + kind + '"]');
      $summary.prop('hidden', !kindOn);
      if (narrowed) {
        $summary.text($.trim(fill(text('text-filtered-' + kind), found, total)));
      } else {
        $summary.text($.trim(text('text-' + kind + (total === 1 ? '-one' : '-many')).replace('{0}', total)));
      }
    });

    $('.prq-head__sep').prop('hidden', filter.seg !== 'all');
    $('[data-prq-seg-count="all"]').text(matched.partners + matched.offices);
    $('[data-prq-seg-count="partners"]').text(matched.partners);
    $('[data-prq-seg-count="offices"]').text(matched.offices);
    $('[data-prq-seg]').each(function() {
      var on = $(this).attr('data-prq-seg') === filter.seg;
      $(this).toggleClass('is-on', on).attr('aria-pressed', on ? 'true' : 'false');
    });
    $('[data-prq-toolbar] [data-prq-clear]').prop('hidden', !filterActive());
  }

  // The filter survives the reload an edit does, through the URL hash
  function syncHash() {
    var parts = [];
    var typed = $.trim($search.val());
    if (typed !== '') {
      parts.push('q=' + encodeURIComponent(typed));
    }
    if (filter.seg !== 'all') {
      parts.push('seg=' + filter.seg);
    }
    try {
      window.history.replaceState(null, '', window.location.pathname + window.location.search
          + (parts.length ? '#' + parts.join('&') : ''));
    } catch (err) {
      // The filter still works without the hash
    }
  }

  // The accept is a full-page POST that ends in a redirect to a URL without the hash: keep the filter for the next page
  function saveFilterForReload() {
    try {
      window.sessionStorage.setItem(PENDING_FILTER, window.location.hash);
    } catch (err) {
      // Without storage the filter resets, as after any navigation
    }
  }

  // Parses a hash value ("#q=...&seg=..."): q goes in through .val(), seg only if it is one of the two kinds
  function parseFilter(hash) {
    var typed = '';
    $.each(String(hash).replace(/^#/, '').split('&'), function(i, part) {
      var pair = part.split('=');
      try {
        if (pair[0] === 'q') {
          typed = decodeURIComponent(pair.slice(1).join('='));
        } else if (pair[0] === 'seg' && (pair[1] === 'partners' || pair[1] === 'offices')) {
          filter.seg = pair[1];
        }
      } catch (err) {
        typed = '';
      }
    });
    $search.val(typed);
    filter.q = normalize(typed);
  }

  // The URL hash wins; without one, the filter saved before a redirect is applied once and discarded
  function readHash() {
    var hash = window.location.hash;
    var saved = '';
    try {
      saved = window.sessionStorage.getItem(PENDING_FILTER) || '';
      window.sessionStorage.removeItem(PENDING_FILTER);
    } catch (err) {
      saved = '';
    }
    var restored = !hash && saved !== '';
    parseFilter(restored ? saved : hash);
    if (restored) {
      syncHash();
    }
  }

  // Applies what is in the field right now (a pending debounce included)
  function commitFilter() {
    window.clearTimeout(searchTimer);
    filter.q = normalize($search.val());
    applyFilter();
    syncHash();
  }

  $search.on('input', function() {
    window.clearTimeout(searchTimer);
    searchTimer = window.setTimeout(commitFilter, SEARCH_DELAY);
  });

  $search.on('keydown', function(e) {
    if (e.key === 'Escape' && $search.val() !== '') {
      e.preventDefault();
      $search.val('');
      commitFilter();
    } else if (e.key === 'Enter') {
      e.preventDefault();
      commitFilter();
    }
  });

  $page.on('click', '[data-prq-seg]', function() {
    filter.seg = $(this).attr('data-prq-seg');
    commitFilter();
  });

  $page.on('click', '[data-prq-clear]', function() {
    filter.seg = 'all';
    $search.val('');
    commitFilter();
    $search.focus();
  });

  function removeCard($card) {
    $card.remove();
    refreshCounts();
  }

  // The loading overlay of a card or dialog panel; a counter lets several requests share it
  function spin($scope, on) {
    var $loading = $scope.children('.loading').first();
    var count = Math.max(0, ($loading.data('prqBusy') || 0) + (on ? 1 : -1));
    $loading.data('prqBusy', count).toggle(count > 0);
  }

  function lock($scope, on) {
    $scope.find('button').prop('disabled', on).toggleClass('is-disabled', on);
    $scope.attr('aria-busy', on ? 'true' : 'false');
  }

  /* ---------- field errors ---------- */

  function showError($field, $error) {
    $field.addClass('is-invalid').attr('aria-invalid', 'true');
    $error.prop('hidden', false);
  }

  function clearError($field, $error) {
    $field.removeClass('is-invalid').removeAttr('aria-invalid');
    $error.prop('hidden', true);
  }

  $page.on('input change', '.prq-input, .prq-select, .prq-textarea', function() {
    var $field = $(this);
    if ($field.hasClass('is-invalid') && $.trim($field.val()) !== '' && $field.val() !== '-1') {
      clearError($field, $('#' + $field.attr('id') + '-error'));
    }
  });

  $('#prqRejectJustification, #prqRejectOfficesJustification').on('input', function() {
    if ($.trim($(this).val()) !== '') {
      clearError($(this), $('#' + this.id + 'Error'));
    }
  });

  /* ---------- partner request: edit inline ---------- */

  $page.on('click', '[data-prq-action="edit"]', function() {
    var $card = $(this).closest('.partnerRequestItem');
    var $form = $card.find('[data-prq-form="edit"]');
    clearCardNotes($card);
    $form.prop('hidden', false);
    $(this).attr('aria-expanded', 'true');
    $card.find('[data-prq-actions]').prop('hidden', true);
    $form.find('input[name="institutionName"]').focus();
  });

  $page.on('click', '[data-prq-action="cancel-edit"]', function() {
    var $card = $(this).closest('.partnerRequestItem');
    var $form = $card.find('[data-prq-form="edit"]');
    $form[0].reset();
    $form.find('.is-invalid').each(function() {
      clearError($(this), $('#' + this.id + '-error'));
    });
    $form.prop('hidden', true);
    $card.find('[data-prq-actions]').prop('hidden', false);
    $card.find('[data-prq-action="edit"]').attr('aria-expanded', 'false').focus();
  });

  $page.on('submit', '[data-prq-form="edit"]', function(e) {
    e.preventDefault();
    var $form = $(this);
    var $card = $form.closest('.partnerRequestItem');
    var $save = $form.find('[data-prq-action="save-edit"]');
    var $fields = {
      name: $form.find('[name="institutionName"]'),
      type: $form.find('[name="institutionTypeID"]'),
      country: $form.find('[name="countryID"]'),
      why: $form.find('[name="justification"]')
    };
    var invalid = [];
    $.each($fields, function(key, $field) {
      var value = $.trim($field.val());
      var missing = value === '' || value === '-1';
      if (missing) {
        invalid.push($field);
        showError($field, $('#' + $field.attr('id') + '-error'));
      } else {
        clearError($field, $('#' + $field.attr('id') + '-error'));
      }
    });
    if (invalid.length) {
      invalid[0].focus();
      return;
    }

    var result = {status: 'failed'};
    $.ajax({
        url: baseURL + '/editPartnerRequest.do',
        type: 'POST',
        dataType: 'json',
        data: {
          requestID: $form.find('[name="requestID"]').val(),
          institutionName: $fields.name.val(),
          institutionAcronym: $form.find('[name="institutionAcronym"]').val(),
          institutionWebPage: $form.find('[name="institutionWebPage"]').val(),
          institutionTypeID: $fields.type.val(),
          countryID: $fields.country.val(),
          justification: $fields.why.val()
        },
        beforeSend: function() {
          clearCardNotes($card);
          spin($card, true);
          lock($card, true);
        },
        success: function(data) {
          result = {status: statusOf(data)};
        },
        error: function(xhr) {
          result = {status: statusOfError(xhr)};
        },
        complete: function() {
          spin($card, false);
          lock($card, false);
          finishEdit($card, result.status, $save);
        }
    });
  });

  function finishEdit($card, status, $save) {
    var name = $card.attr('data-prq-name');
    if (status === 'done' && queueNote(status, name)) {
      // The card is rendered again by the server with the changed fields and the "edited" badge
      window.location.reload();
      return;
    }
    if (CLOSED[status]) {
      var $note = pageNote(status, name);
      removeCard($card);
      $note.focus();
      return;
    }
    cardNote($card, status, name);
    $save.focus();
  }

  /* ---------- partner request: reject ---------- */

  $page.on('click', '[data-prq-action="reject"]', function() {
    var $card = $(this).closest('.partnerRequestItem');
    var $dialog = $('#prqRejectDialog');
    clearCardNotes($card);
    pending = {kind: 'reject', $card: $card, id: $card.attr('data-request-id')};
    $dialog.find('[data-prq-target]').text($card.attr('data-prq-name'));
    $('#prqRejectJustification').val('');
    clearError($('#prqRejectJustification'), $('#prqRejectJustificationError'));
    $('#prqRejectNotify').prop('checked', true);
    prqDialog.open('prqRejectDialog', this);
  });

  function rejectPartner() {
    var $justification = $('#prqRejectJustification');
    var justification = $.trim($justification.val());
    if (!justification) {
      showError($justification, $('#prqRejectJustificationError'));
      $justification.focus();
      return;
    }
    var $card = pending.$card;
    var $dialog = $('#prqRejectDialog');
    var $panel = $dialog.find('.prq-dialog__panel');
    var result = {status: 'failed'};

    $.ajax({
        url: baseURL + '/rejectPartnerRequest.do',
        type: 'POST',
        dataType: 'json',
        data: {
          requestID: pending.id,
          justification: justification,
          sendNotification: $('#prqRejectNotify').is(':checked'),
          phaseID: window.phaseID
        },
        beforeSend: function() {
          spin($panel, true);
          lock($card, true);
          prqDialog.setBusy(true);
        },
        success: function(data) {
          result = {status: statusOf(data)};
        },
        error: function(xhr) {
          result = {status: statusOfError(xhr)};
        },
        complete: function() {
          spin($panel, false);
          lock($card, false);
          finishDecision($card, result.status);
        }
    });
  }

  /* ---------- partner request: accept (the card's POST form, gated by a dialog) ---------- */

  $page.on('submit', 'form[data-prq-confirm]', function(e) {
    e.preventDefault();
    var $form = $(this);
    var $card = $form.closest('.partnerRequestItem');
    clearCardNotes($card);
    pending = {kind: 'accept', $card: $card, form: this};
    $('#prqAcceptDialog').find('[data-prq-target]').text($card.attr('data-prq-name'));
    prqDialog.open($form.attr('data-prq-confirm'), $form.find('[data-prq-action="accept"]')[0]);
  });

  function acceptPartner() {
    var form = pending.form;
    prqDialog.setBusy(true);
    lock(pending.$card, true);
    spin(pending.$card, true);
    // A full-page POST and redirect: addPartner answers with an action message that the page shows as a note
    saveFilterForReload();
    form.submit();
  }

  // A decision on a card that is not an office batch
  function finishDecision($card, status) {
    var name = $card.attr('data-prq-name');
    if (CLOSED[status]) {
      var $note = pageNote(status, name);
      prqDialog.close({focus: false});
      removeCard($card);
      $note.focus();
      return;
    }
    cardNote($card, status, name);
    prqDialog.close();
  }

  /* ---------- office requests: accept and reject the selected countries ---------- */

  function selectedOffices($card) {
    var offices = [];
    $card.find('.officeRequest:checked').each(function() {
      var $row = $(this).closest('.officeCountryRequest');
      offices.push({
        id: this.value,
        $row: $row,
        label: $.trim($row.find('.prq-office__country').text().replace(/\s+/g, ' '))
      });
    });
    return offices;
  }

  function officeError($card) {
    return $card.find('[data-prq-offices-error]');
  }

  $page.on('change', '.officeRequest', function() {
    var $card = $(this).closest('.officesRequestItem');
    officeError($card).prop('hidden', true);
    $card.find('.prq-fieldset').removeAttr('aria-describedby');
  });

  function openOfficeDialog(button, kind, dialogId) {
    var $card = $(button).closest('.officesRequestItem');
    var offices = selectedOffices($card);
    clearCardNotes($card);
    if (!offices.length) {
      var $error = officeError($card);
      $error.prop('hidden', false);
      $card.find('.prq-fieldset').attr('aria-describedby', $error.attr('id'));
      $card.find('.officeRequest').first().focus();
      return;
    }
    pending = {
      kind: kind,
      $card: $card,
      institutionId: $card.attr('data-institution-id'),
      name: $card.attr('data-prq-name'),
      offices: offices
    };
    var labels = $.map(offices, function(office) {
      return office.label;
    });
    $('#' + dialogId).find('[data-prq-target]').text(pending.name + ': ' + labels.join(', '));
    if (kind === 'rejectOffices') {
      $('#prqRejectOfficesJustification').val('');
      clearError($('#prqRejectOfficesJustification'), $('#prqRejectOfficesJustificationError'));
      $('#prqRejectOfficesNotify').prop('checked', true);
    }
    prqDialog.open(dialogId, button);
  }

  $page.on('click', '[data-prq-action="accept-offices"]', function() {
    openOfficeDialog(this, 'acceptOffices', 'prqAcceptOfficesDialog');
  });

  $page.on('click', '[data-prq-action="reject-offices"]', function() {
    openOfficeDialog(this, 'rejectOffices', 'prqRejectOfficesDialog');
  });

  function officeIds(offices) {
    return $.map(offices, function(office) {
      return office.id;
    }).join();
  }

  function acceptOffices() {
    var $card = pending.$card;
    var offices = pending.offices;
    var $panel = $('#prqAcceptOfficesDialog').find('.prq-dialog__panel');
    var decision = pending;
    var result = {status: 'failed', data: null};

    $.ajax({
        url: baseURL + '/acceptOfficesRequest.do',
        type: 'POST',
        dataType: 'json',
        data: {
          'countryOfficePOJO.institution.id': $card.find('input.institutionID').val(),
          'countryOfficePOJO.ids': officeIds(offices)
        },
        beforeSend: function() {
          spin($panel, true);
          lock($card, true);
          prqDialog.setBusy(true);
        },
        success: function(data) {
          result = {status: statusOf(data), data: data};
        },
        error: function(xhr) {
          result = {status: statusOfError(xhr), data: null};
        },
        complete: function() {
          spin($panel, false);
          lock($card, false);
          finishOffices(decision, result.data, result.status, true);
        }
    });
  }

  function rejectOffices() {
    var $justification = $('#prqRejectOfficesJustification');
    var justification = $.trim($justification.val());
    if (!justification) {
      showError($justification, $('#prqRejectOfficesJustificationError'));
      $justification.focus();
      return;
    }
    var $card = pending.$card;
    var offices = pending.offices;
    var $panel = $('#prqRejectOfficesDialog').find('.prq-dialog__panel');
    var decision = pending;
    var result = {status: 'failed', data: null};

    $.ajax({
        url: baseURL + '/rejectOfficesRequest.do',
        type: 'POST',
        dataType: 'json',
        data: {
          'countryOfficePOJO.institution.id': $card.find('input.institutionID').val(),
          'justification': justification,
          'sendNotification': $('#prqRejectOfficesNotify').is(':checked'),
          'countryOfficePOJO.ids': officeIds(offices)
        },
        beforeSend: function() {
          spin($panel, true);
          lock($card, true);
          prqDialog.setBusy(true);
        },
        success: function(data) {
          result = {status: statusOf(data), data: data};
        },
        error: function(xhr) {
          result = {status: statusOfError(xhr), data: null};
        },
        complete: function() {
          spin($panel, false);
          lock($card, false);
          finishOffices(decision, result.data, result.status, false);
        }
    });
  }

  // One note per distinct status of the batch: "Institution: countries" and what happened to them
  function finishOffices(decision, data, overall, accepted) {
    var $card = decision.$card;
    var outcomes = (data && data.outcomes) || {};
    var groups = [];
    var byStatus = {};

    $.each(decision.offices, function(i, office) {
      var status = outcomes[office.id] || overall;
      if (!byStatus[status]) {
        byStatus[status] = [];
        groups.push(status);
      }
      byStatus[status].push(office.label);
      if (!CLOSED[status]) {
        return;
      }
      office.$row.remove();
      var $chips = $card.find('.prq-chips');
      if (accepted && status === 'done' && $chips.length) {
        $chips.append($('<li class="prq-chip"></li>').text(office.label));
      }
    });

    var left = $card.find('.officeCountryRequest').length;
    var $badge = $card.find('.prq-badge--count');
    $badge.text($badge.text().replace(/\d+/, left));

    // The card may no longer match the query (its rows changed): counts and the filter are reapplied
    applyFilter();
    var $first = null;
    var cardLeaves = left === 0;
    var cardHidden = $card.prop('hidden');
    $.each(groups, function(i, status) {
      var label = decision.name + ': ' + byStatus[status].join(', ');
      var $note = (cardLeaves || cardHidden) ? pageNote(status, label) : cardNote($card, status, label);
      $first = $first || $note;
    });

    if (cardLeaves || cardHidden) {
      prqDialog.close({focus: false});
      if (cardLeaves) {
        removeCard($card);
      }
      $first.focus();
      return;
    }
    // The card stays: the alert is announced where the reviewer was working, and the focus goes back to the button
    prqDialog.close();
  }

  /* ---------- dialog buttons ---------- */

  $(document).on('click', '.prq-dialog [data-prq-confirm]', function(e) {
    e.preventDefault();
    if (!pending) {
      return;
    }
    var kind = $(this).closest('.prq-dialog').attr('data-prq-dialog');
    if (kind === 'reject' && pending.kind === 'reject') {
      rejectPartner();
    } else if (kind === 'accept' && pending.kind === 'accept') {
      acceptPartner();
    } else if (kind === 'acceptOffices' && pending.kind === 'acceptOffices') {
      acceptOffices();
    } else if (kind === 'rejectOffices' && pending.kind === 'rejectOffices') {
      rejectOffices();
    }
  });

  // All institutions: the table is a DataTable created while the dialog was hidden
  $page.on('click', '[data-prq-open="allInstitutions"]', function() {
    prqDialog.open('allInstitutions', this);
    var $table = $('#allInstitutionsTable');
    if ($.fn.dataTable && $.fn.dataTable.isDataTable($table)) {
      $table.DataTable().columns.adjust();
    }
  });

  /* ---------- similar institutions ---------- */

  function findSameness() {
    $('.partnerRequestItem').each(function(i, e) {
      var $card = $(e);
      $.ajax({
          url: baseURL + '/searchInstitutionsName.do',
          data: {
            institutionName: $card.attr('data-prq-name'),
            requestID: $card.attr('data-request-id')
          },
          beforeSend: function() {
            spin($card, true);
          },
          success: function(data) {
            var institutions = (data && data.institutions) || [];
            if (!institutions.length) {
              return;
            }
            var $list = $card.find('.sameness ul');
            $.each(institutions, function(j, partner) {
              var $li = $('<li></li>').attr('title', partner.composedName).text(partner.composedName);
              if (partner.isPPA) {
                $li.append(document.createTextNode(' '))
                    .append($('<span class="prq-badge prq-badge--count"></span>').text(text('text-managing')));
              }
              $list.append($li);
            });
            $card.find('.sameness').prop('hidden', false);
          },
          complete: function() {
            spin($card, false);
          }
      });
    });
  }

  // Back from the full-page accept (bcache) must not show a locked, half-decided screen
  $(window).on('pageshow', function(e) {
    if (e.originalEvent && e.originalEvent.persisted) {
      window.location.reload();
    }
  });

  readHash();
  applyFilter();
  showQueuedNotes();
  findSameness();
});
