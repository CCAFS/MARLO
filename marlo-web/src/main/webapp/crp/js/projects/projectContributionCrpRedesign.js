/*
 * Cluster contribution to performance indicators - A2-2439 redesign.
 * Only the disaggregated-target accordion: the period tabs are plain Bootstrap tabs.
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

});
