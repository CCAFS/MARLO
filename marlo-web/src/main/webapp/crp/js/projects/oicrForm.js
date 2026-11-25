/**
 * Project / cluster OICR form (A2-2743 redesign).
 *
 * Presentation only: projectStudy.js keeps every behaviour and selector it relies on (the copy link, the shared
 * clusters counter, the Alliance tab toggle, the fields of every tab). This file adds what the redesigned header
 * draws on top: the inline sharing panel, the status chip and segmented control, the current report year note,
 * the Alliance OICR ID chip and help, the tab note while the Alliance tab is hidden, and the save bar's jump to
 * the first field to fix. It runs after projectStudy.js (both on document ready, in load order).
 */
$(document).ready(function() {
  var $root = $('[data-ocf]');
  if(!$root.length) {
    return;
  }

  // The meta row keeps native selects: projectStudy.js turns every form select into select2
  $root.find('.ocf-meta select').each(function() {
    if($(this).data('select2')) {
      $(this).select2('destroy');
    }
  });

  // Each meta field's feedback bubble goes on its own label line, so it never reads as the next field's
  $root.find('.ocf-meta__field').each(function() {
    var $label = $(this).find('.ocf-meta__label, label').first();
    $label.append($(this).find('.commentNumberContainer').first());
  });

  // ---- Sharing panel ----
  var $shareToggle = $root.find('[data-ocf-share-toggle]');
  var $share = $root.find('[data-ocf-share]');
  $shareToggle.on('click', function() {
    var open = $(this).attr('aria-expanded') !== 'true';
    $(this).attr('aria-expanded', open);
    $share.prop('hidden', !open);
  });

  // Owner line: the clusters the OICR is shared with, as the list component adds and removes them
  var $sharedLine = $root.find('[data-ocf-shared-line]');
  var $sharedNames = $root.find('[data-ocf-shared-names]');
  var $shareList = $share.find('ul.list').first();
  function refreshShared() {
    // "C102087 - Theme 4: Activities led by Alliance" reads as "Theme 4", as the server writes it
    var names = $shareList.children('li').map(function() {
      var text = $.trim($(this).find('.elementName').first().text());
      var match = text.match(/^C\d+\s*-\s*([^:]+)/);
      return match ? $.trim(match[1]) : text;
    }).get();
    $sharedNames.text(names.join(', '));
    $sharedLine.prop('hidden', names.length === 0);
    $root.find('#modalCounterShared').text(names.length);
  }
  if($shareList.length && window.MutationObserver) {
    new MutationObserver(refreshShared).observe($shareList[0], {
      childList: true
    });
  }

  // ---- Status: segmented control and the chip next to the OICR ID ----
  $root.on('change', '.ocf-seg__input', function() {
    var $opt = $(this).closest('.ocf-seg__opt');
    $opt.addClass('is-on').siblings().removeClass('is-on');
    var value = this.value;
    $root.find('[data-ocf-status-text]').text($.trim($opt.text()));
    $root.find('[data-ocf-status-chip] .ocf-status__dot').attr('class', 'ocf-status__dot ocf-dot--' + value);
  });

  // ---- Current report year: an earlier year turns amber and opens the note ----
  var $year = $root.find('[data-ocf-year]');
  var $yearNote = $root.find('[data-ocf-year-note]');
  $year.on('change', 'select', function() {
    var cycle = parseInt($year.attr('data-cycle'), 10);
    var year = parseInt(this.value, 10);
    var earlier = !!year && year < cycle;
    $year.toggleClass('is-earlier', earlier);
    $yearNote.prop('hidden', !earlier);
    if(earlier) {
      $yearNote.find('[data-ocf-year-note-text]').text(String($yearNote.attr('data-template'))
        .replace('{0}', year).replace('{1}', cycle).replace('{2}', $yearNote.attr('data-phase')));
    }
  });

  // ---- Alliance OICR ID: "?" on the label line, the help text, and the chip next to the OICR ID ----
  var $aidField = $root.find('#allianceOICRIdContainer');
  var $aidHelpBtn = $aidField.find('[data-ocf-aid-help]');
  $aidField.find('label').first().append($aidHelpBtn);
  $aidHelpBtn.on('click', function(e) {
    e.preventDefault();
    var open = $(this).attr('aria-expanded') !== 'true';
    $(this).attr('aria-expanded', open);
    $root.find('#ocf-aid-help').prop('hidden', !open);
  });
  var $aidChip = $root.find('[data-ocf-aid-chip]');
  var $aidInput = $aidField.find('input[name$="allianceOicr"]');
  function refreshAid() {
    var value = $.trim($aidInput.val() || $aidChip.text());
    var shown = $aidField.css('display') !== 'none';
    $aidChip.text(value).prop('hidden', !shown || !value);
  }
  $aidInput.on('input change', refreshAid);

  // PMU badge on the score label
  var $scoreField = $root.find('.ocf-meta__field--score');
  $scoreField.find('label').first().append($scoreField.find('.ocf-meta__badge'));

  // ---- Alliance tab: projectStudy.js slides it in or out with the contributing centers ----
  var $allianceTab = $root.find('#allianceTab');
  var $allianceNote = $root.find('[data-ocf-alliance-note]');
  function refreshAlliance() {
    var shown = $allianceTab.css('display') !== 'none';
    $allianceNote.prop('hidden', shown);
    refreshAid();
  }
  if($allianceTab.length && window.MutationObserver) {
    new MutationObserver(refreshAlliance).observe($allianceTab[0], {
      attributes: true,
      attributeFilter: ['style']
    });
  }

  // ---- Save bar: jump to the first tab with fields to fix ----
  $root.on('click', '[data-ocf-issues]', function() {
    var $tab = $root.find('[data-ocf-tab]').filter(function() {
      return $(this).css('display') !== 'none' && $(this).find('.ocf-tab__missing').length;
    }).first();
    if(!$tab.length) {
      return;
    }
    $tab.find('a[data-toggle="tab"]').tab('show');
    var $pane = $root.find($tab.find('a').attr('href'));
    var $field = $pane.find('.fieldError, .missingSelect2, .missingInput').filter(':visible').first();
    ($field.length ? $field : $tab)[0].scrollIntoView({
        block: 'center',
        behavior: 'smooth'
    });
  });
});
