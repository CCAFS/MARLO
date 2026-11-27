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

  // ---- General Information: radio cards (maturity) and segmented controls (markers) over hidden selects ----
  // The select keeps the name, the value the save posts and the change event projectStudy.js listens to (the
  // maturity marks quantification required at Level 3); the buttons only draw and set it.
  $root.on('click', '[data-ocf-cards-for] [data-value], [data-ocf-seg-for] [data-value]', function() {
    var $group = $(this).closest('[data-ocf-cards-for], [data-ocf-seg-for]');
    var name = $group.attr('data-ocf-cards-for') || $group.attr('data-ocf-seg-for');
    var $select = $root.find('select[name="' + name + '"]');
    if(!$select.length || $(this).prop('disabled')) {
      return;
    }
    $group.find('[data-value]').removeClass('is-on').attr('aria-checked', 'false');
    $(this).addClass('is-on').attr('aria-checked', 'true');
    $select.val($(this).attr('data-value')).trigger('change');
  });

  // ---- General Information: the feedback bubble of a field drawn without its own label goes on the label line ----
  $root.find('[data-ocf-bubble-for]').each(function() {
    var $slot = $(this);
    if($slot.children('.commentNumberContainer').length) {
      return;
    }
    var $bubble = $root.find('img.qaComment').filter(function() {
      return this.getAttribute('name') === $slot.attr('data-ocf-bubble-for');
    }).first().closest('.commentNumberContainer');
    $slot.append($bubble);
  });

  // ---- "What to cover" / "More guidance" / "How to score": the label's toggle opens its text ----
  $root.on('click', '[data-ocf-more]', function() {
    var open = $(this).attr('aria-expanded') !== 'true';
    $(this).attr('aria-expanded', open).text($(this).attr(open ? 'data-label-less' : 'data-label-more'));
    $('#' + $(this).attr('aria-controls')).prop('hidden', !open);
  });

  // ---- Geographic scope: checkbox cards over the list component ----
  // The list component keeps posting the scopes and firing addElement / removeElement, which open the regions and
  // countries and lock National against Multi-national (projectStudy.js). The cards read their state from the list,
  // never from the click, so a fast double toggle cannot leave them out of step with what is posted.
  var $scopes = $root.find('[data-ocf-scopes]');
  var $scopeList = $scopes.closest('.dlf-field').find('.elementsListComponent').first();
  var $scopeSelect = $scopeList.find('select[class*="elementType-repIndGeographicScope"]');
  var $scopeNote = $root.find('[data-ocf-scope-note]');
  // Read-only: the cards stay as drawn (disabled, no lock). The list component still renders its select there,
  // because inside customForm `editable` is the action's, true in a closed phase too.
  var scopesReadOnly = $scopes.find('.ocf-scope').first().prop('disabled');
  function scopeIds() {
    return $scopeList.find('ul.list li.relationElement .elementRelationID').map(function() {
      return this.value;
    }).get();
  }
  function refreshScopes() {
    var ids = scopeIds();
    $scopes.find('.ocf-scope').each(function() {
      var value = $(this).attr('data-value');
      var on = ids.indexOf(value) > -1;
      var locked = !on && $scopeSelect.find('option[value="' + value + '"]').prop('disabled');
      $(this).toggleClass('is-on', on).attr('aria-pressed', on).toggleClass('is-locked', !!locked);
      if($scopeSelect.length) {
        $(this).prop('disabled', !!locked);
      }
    });
    // "This is yet to be determined" (6) asks for no countries
    $scopeNote.text($scopeNote.attr(ids.indexOf('6') > -1 ? 'data-note-tbd' : 'data-note'));
    $scopes.removeAttr('data-busy');
  }
  if($scopes.length && !scopesReadOnly) {
    $scopeSelect.on('addElement removeElement', function() {
      // removeElement fires before the row leaves the list: read it once it has
      setTimeout(refreshScopes, 0);
    });
    $scopes.on('click', '.ocf-scope', function() {
      if(!$scopeSelect.length || $(this).prop('disabled') || $scopes.attr('data-busy')) {
        return;
      }
      var value = $(this).attr('data-value');
      $scopes.attr('data-busy', 'true');
      if(scopeIds().indexOf(value) > -1) {
        $scopeList.find('ul.list li.relationElement').filter(function() {
          return $(this).find('.elementRelationID').val() === value;
        }).find('.removeElement').first().trigger('click');
      } else {
        $scopeSelect.find('option[value="' + value + '"]').prop('disabled', false);
        $scopeSelect.val(value).trigger('change');
      }
      // Whatever happens, the cards come back to the list's state
      setTimeout(refreshScopes, 800);
    });
    refreshScopes();
  }

  // ---- Link to Performance Indicators: the component as a tag, the code in bold ----
  // "PDO Outcome: (2024-2025) PDO Indicator 3: Beneficiaries ..." reads as [PDO] **PDO Indicator 3** Beneficiaries ...
  var $indList = $root.find('.ocf-indicators ul.list');
  function decorateIndicators() {
    $indList.children('li.relationElement').not('.is-decorated').each(function() {
      var $name = $(this).find('.elementName');
      var text = $.trim($name.text());
      var match = text.match(/^(\S+)\s+Outcome:\s*(?:\([^)]*\)\s*)?((?:PDO Indicator|IPI)\s*[\d.]+)\s*:\s*([\s\S]*)$/);
      $(this).addClass('is-decorated');
      if(!match) {
        return;
      }
      var tag = match[1].toUpperCase();
      $('<span class="ocf-indTag" aria-hidden="true"></span>').text(tag).addClass('ocf-indTag--' + tag.toLowerCase())
        .insertBefore($name);
      $name.empty().append($('<b></b>').text(match[2])).append(document.createTextNode(' ' + match[3]));
      $name.attr('title', text);
    });
  }
  if($indList.length) {
    decorateIndicators();
    if(window.MutationObserver) {
      new MutationObserver(decorateIndicators).observe($indList[0], {
        childList: true
      });
    }
  }

  // ---- References and quantifications: the count beside the card title ----
  function refreshCount(kind, $list, selector) {
    var n = $list.children(selector).length;
    var $meta = $root.find('[data-ocf-count="' + kind + '"]');
    $meta.text(n ? (n === 1 ? $meta.attr('data-one') : String($meta.attr('data-many')).replace('{0}', n)) : '');
  }
  [['refs', '.referenceList', '.multiInput.references'], ['quant', '.quantificationsList', '.quantification']]
    .forEach(function(def) {
      var $list = $root.find('.ocf-general ' + def[1]).first();
      if(!$list.length) {
        return;
      }
      var update = function() {
        refreshCount(def[0], $list, def[2]);
      };
      update();
      if(window.MutationObserver) {
        new MutationObserver(update).observe($list[0], {
          childList: true
        });
      }
    });

  // ---- Quantification: "Required at Level 3" follows the level of maturity ----
  $root.find('select[name$="projectExpectedStudyInfo.repIndStageStudy.id"]').on('change', function() {
    var three = String(this.value) === '3';
    $root.find('[data-ocf-quant-req]').prop('hidden', !three);
    $root.find('[data-ocf-quant-opt]').prop('hidden', three);
  });

  // ---- List components: the design's "Add …" prompt and empty text, in place of the generic ones ----
  // setOneToManyComponent (global.js) writes "Select multiple options..." on document ready; this runs after it.
  // select2 redraws its rendered text on later re-inits (the region list is drawn while hidden), so the prompt is
  // also re-applied to the rendered text on every change and once the page has loaded.
  function applyListPrompts() {
    $root.find('[data-ocf-placeholder]').each(function() {
      var $select = $(this).find('.elementsListComponent select').first();
      var value = $select.val();
      var label = $(this).attr('data-ocf-placeholder');
      var $rendered = $select.next('.select2').find('.select2-selection__rendered');
      // deliverableFormRedesign.js (also loaded here) labels the region select from its own texts, which this
      // page does not print, and keeps writing that empty label back: give it ours.
      if($select.is('[data-dlf-add-label]')) {
        $select.attr('data-dlf-add-label', label);
      }
      if((!value || value === '-1') && $rendered.length && $rendered.text() !== label) {
        $rendered.text(label).removeAttr('title');
      }
    });
  }
  $root.on('change', '[data-ocf-placeholder] .elementsListComponent select', function() {
    setTimeout(applyListPrompts, 0);
  });
  $(window).on('load', function() {
    setTimeout(applyListPrompts, 0);
  });
  setTimeout(function() {
    $root.find('[data-ocf-placeholder]').each(function() {
      var $select = $(this).find('.elementsListComponent select').first();
      // select2 keeps each option's text in its data cache: drop it so the new text is read
      $select.find('option[value="-1"]').text($(this).attr('data-ocf-placeholder')).removeData('data');
      $select.trigger('change.select2');
    });
    applyListPrompts();
    $root.find('[data-ocf-empty]').each(function() {
      $(this).find('.dlf-listEmpty__edit').text($(this).attr('data-ocf-empty'));
    });
  }, 0);

  // ---- Missing fields: a redrawn control shows the error its hidden field carries ----
  // fieldsValidation.js marks the hidden select or list (fieldError, missingSelect2, ...), possibly after load.
  $root.find('.ocf-general .ocf-hiddenField').each(function() {
    var hidden = this;
    var $proxy = $(hidden).siblings('.ocf-cards, .ocf-scopes, .ocf-seg').first();
    if(!$proxy.length) {
      return;
    }
    var sync = function() {
      var missing = $(hidden).find('.fieldError, .missingSelect2, .missingSelect, .missingInput').length > 0;
      $proxy.toggleClass('is-missing', missing);
    };
    sync();
    if(window.MutationObserver) {
      new MutationObserver(sync).observe(hidden, {
        attributes: true,
        attributeFilter: ['class'],
        subtree: true
      });
    }
  });

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
    // A redrawn control (maturity, scopes, markers) hides the field that carries the error: its card stands for it
    var $field = $pane.find('.fieldError, .missingSelect2, .missingInput').not('[id*="template"] *').first();
    if($field.length && !$field.is(':visible')) {
      $field = $field.parents('.dlf-field, .dlf-card').filter(':visible').first();
    }
    ($field.length ? $field : $tab)[0].scrollIntoView({
        block: 'center',
        behavior: 'smooth'
    });
  });
});
