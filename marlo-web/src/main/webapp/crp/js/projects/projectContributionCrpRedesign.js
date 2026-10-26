/*
 * Cluster contribution to performance indicators - A2-2439 redesign.
 * The disaggregated-target accordion, the percentage resolution and the save bar: the
 * period tabs are plain Bootstrap tabs.
 */
$(document).ready(function () {

  function setOpen($head, open) {
    $head.attr('aria-expanded', open ? 'true' : 'false');
  }

  // Open the first disaggregated target of each period so the pane never reads as empty.
  $('.cpi-pane').each(function () {
    var $first = $(this).find('.cpi-dt__head').first();
    if ($first.length) {
      setOpen($first, true);
      $('#' + $first.attr('data-cpi-toggle')).show();
    }
  });

  function toggle($head) {
    var $body = $('#' + $head.attr('data-cpi-toggle'));
    setOpen($head, $head.attr('aria-expanded') !== 'true');
    $body.slideToggle(140);
  }

  $('.cpi-dt__head').on('click', function () {
    toggle($(this));
  });

  // The head is a role="button" div, so it has to answer the keys a button does.
  $('.cpi-dt__head').on('keydown', function (e) {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      toggle($(this));
    }
  });

  // ---- A2-2620 · percentage disaggregations resolve to the figure they represent ----
  // A percentage target's value is a share of the principal target's value for the same
  // field and period: "25% of 100 = 25". Read-only fields are rendered by customForm as a
  // hidden input carrying the stored value, so the same selector reads both kinds.

  var $i18n = $('#cpiI18n');

  function cpiNumber($fields, field) {
    var raw = $.trim($fields.find('input[name$=".' + field + '"]').first().val() || '');
    var n = parseFloat(raw.replace(/[,\s]/g, ''));
    return raw === '' || isNaN(n) ? null : n;
  }

  function cpiFormat(n) {
    return n.toLocaleString('en-US', { maximumFractionDigits: 2 });
  }

  function cpiFill(template, args) {
    return String(template || '').replace(/\{(\d)\}/g, function (match, i) {
      return args[i];
    });
  }

  function refreshResolved($fields) {
    var $principal = $fields.closest('.cpi-pane').find('.cpi-fields[data-cpi-role="principal"]').first();
    $fields.find('[data-cpi-resolve]').each(function () {
      var field = $(this).attr('data-cpi-resolve');
      var pct = cpiNumber($fields, field);
      var base = $principal.length ? cpiNumber($principal, field) : null;
      var text = '';
      if (pct !== null && base !== null) {
        text = cpiFill($i18n.attr('data-pct-of'), [cpiFormat(pct), cpiFormat(base), cpiFormat(base * pct / 100)]);
      } else if (pct !== null) {
        text = cpiFill($i18n.attr('data-pct-no-base'), [cpiFormat(pct)]);
      }
      $(this).text(text).toggle(text !== '');
    });
  }

  function refreshPane($pane) {
    $pane.find('.cpi-fields[data-cpi-pct="true"]').each(function () {
      refreshResolved($(this));
    });
  }

  $('.cpi-pane').each(function () {
    refreshPane($(this));
  });

  // A change to a percentage moves its own figure; a change to the principal moves every
  // percentage of the period.
  $('.cpi-pane').on('input change', 'input.targetValue', function () {
    var $fields = $(this).closest('.cpi-fields');
    if ($fields.attr('data-cpi-role') === 'principal') {
      refreshPane($(this).closest('.cpi-pane'));
    } else if ($fields.attr('data-cpi-pct') === 'true') {
      refreshResolved($fields);
    }
  });

  // ---- A2-2439 · save bar ----
  // The bar reads "state + last edit" on the left and the section buttons on the right, as
  // the Overall Performance Indicators bar does (cpiGroupSaveBar mirrors opiGroupSaveBar).

  var $saveBar = $('.cpi-saveBar');

  // buttons-projectOutcomes.ftl renders the last-edit message after the buttons; it moves
  // next to the save state, and the placeholder holding its slot goes either way.
  function cpiGroupSaveBar() {
    var $info = $saveBar.find('.cpi-saveBar__info');
    var $message = $saveBar.find('.cpi-saveBar__actions #lastUpdateMessage');
    if ($message.length) {
      $info.append($message);
    }
    $info.find('[data-cpi-save-ghost]').remove();
  }

  // The design's outline save icon in place of the shared glyphicon.
  function cpiDecorateSaveButton() {
    var $icon = $saveBar.find('.button-save .glyphicon');
    if ($icon.length) {
      $icon.replaceWith('<svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 3h7.2L13 5.8V13H3V3Z" stroke="#fff" stroke-width="1.5" stroke-linejoin="round"></path><path d="M5.6 3v3.4h4.2V3M5.6 13v-3.2h4.8V13" stroke="#fff" stroke-width="1.4" stroke-linejoin="round"></path></svg>');
    }
  }

  if ($saveBar.length) {
    cpiGroupSaveBar();
    cpiDecorateSaveButton();
  }

  // The state compares the form with the one the page loaded, so undoing an edit brings
  // "All changes saved" back. The snapshot waits for every ready handler (the shared
  // formatters rewrite some values on load) so loading alone never reads as an edit.
  var $form = $saveBar.closest('form');
  var $state = $saveBar.find('[data-cpi-save-state]');
  var loadedForm = null;

  function cpiRefreshSaveState() {
    if (loadedForm === null || !$state.length) {
      return;
    }
    var dirty = $form.serialize() !== loadedForm;
    $saveBar.toggleClass('is-dirty', dirty);
    $state.text($state.attr(dirty ? 'data-unsaved' : 'data-saved'));
  }

  if ($state.length && $form.length) {
    setTimeout(function () {
      loadedForm = $form.serialize();
    }, 0);
    $form.on('input change', 'input, textarea, select', cpiRefreshSaveState);
    $(document).on('cpi:formchange', cpiRefreshSaveState);
  }

  // ---- A2-2439 · word counters ----
  // applyWordCounter() (utils.js) prints "(20 words remaining of 20)"; the design drops the
  // brackets. Only the outer text changes, so the count it keeps updating is untouched. The
  // next-user template sits outside the section and is cloned with its counters.
  $('.cpi-page .charCount, #nextUser-template .charCount').each(function () {
    $(this).contents().filter(function () {
      return this.nodeType === 3;
    }).each(function () {
      this.nodeValue = this.nodeValue.replace(/[()]/g, '');
    });
  });

});
