/**
 * Project / cluster deliverable form (A2-2607 redesign).
 *
 * Presentation only: the deliverable scripts (deliverableInfo.js, deliverableDissemination.js,
 * deliverableQualityCheck.js) keep every behaviour and every selector they rely on. This file adds what
 * the redesign draws on top of them: the save bar's state, and the label and counter text the design
 * writes differently from customForm.
 */
$(document).ready(function() {
  var $root = $('.dlf');
  if(!$root.length) {
    return;
  }

  // ---- Save bar ----
  var $saveBar = $root.find('.dlf-saveBar');

  // buttons-deliverables.ftl prints the last-edit message after the buttons; it reads as the state's caption.
  var $message = $saveBar.find('.dlf-saveBar__actions #lastUpdateMessage');
  if($message.length) {
    $saveBar.find('.dlf-saveBar__info').append($message);
  }

  // The design's outline save icon in place of the shared glyphicon.
  $saveBar.find('.button-save .glyphicon').replaceWith(
      '<svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true">'
          + '<path d="M3 3h7.2L13 5.8V13H3V3Z" stroke="#fff" stroke-width="1.5" stroke-linejoin="round"></path>'
          + '<path d="M5.6 3v3.4h4.2V3M5.6 13v-3.2h4.8V13" stroke="#fff" stroke-width="1.4" stroke-linejoin="round"></path>'
          + '</svg>');

  // The state compares the form with the one the page loaded, so undoing an edit brings "All changes saved"
  // back. The snapshot waits for every ready handler: the deliverable scripts rewrite some values on load
  // (select2, the trainees totals, the remainingPending flag), and loading alone must not read as an edit.
  var $form = $saveBar.closest('form');
  var $state = $saveBar.find('[data-dlf-save-state]');
  var loadedForm = null;

  function refreshSaveState() {
    if(loadedForm === null || !$state.length) {
      return;
    }
    var dirty = $form.serialize() !== loadedForm;
    $saveBar.toggleClass('is-dirty', dirty);
    $state.text($state.attr(dirty ? 'data-unsaved' : 'data-saved'));
  }

  if($state.length && $form.length) {
    setTimeout(function() {
      loadedForm = $form.serialize();
    }, 0);
    $form.on('input change', 'input, textarea, select', refreshSaveState);
    // List components and the yes/no switches change hidden inputs without firing change on them.
    $form.on('click', '.button-label, .removeElement, .removeActivity, .removeFundingSource, .removePartnerItem',
        function() {
          setTimeout(refreshSaveState, 0);
        });
  }

  // ---- Labels ----
  // customForm ends every label with a colon ("Title:*"); the design's labels carry none. Only the text node
  // before the required mark changes, so the mark, the help text and the feedback bubble inside the label stay.
  function dropLabelColons($scope) {
    $scope.find('.dlf-card label, .dlf-card .yesNoLabel').each(function() {
      $(this).contents().filter(function() {
        return this.nodeType === 3;
      }).each(function() {
        this.nodeValue = this.nodeValue.replace(/:\s*$/, ' ').replace(/^\s+/, '');
      });
    });
  }
  dropLabelColons($root);

  // ---- Quality check: FAIR summary and fix buttons ----
  // deliverableQualityCheck.js recomputes each principle by swapping the achieved / not-achieved classes on the
  // .fairCompliant rows; the summary counts the achieved rows whenever one of those classes changes.
  var $fairRows = $root.find('.dlf-fairRow');
  var $fairSummary = $root.find('[data-dlf-fair-summary]');

  function refreshFairSummary() {
    var achieved = $fairRows.filter('.achieved').length;
    $fairSummary.text(String($fairSummary.attr('data-label') || '').replace('{0}', achieved))
      .toggleClass('is-complete', achieved === $fairRows.length);
  }

  if($fairRows.length && $fairSummary.length) {
    refreshFairSummary();
    if(window.MutationObserver) {
      var fairObserver = new MutationObserver(refreshFairSummary);
      $fairRows.each(function() {
        fairObserver.observe(this, {
            attributes: true,
            attributeFilter: ['class']
        });
      });
    }
  }

  // A principle that is not achieved links to the question that decides it, on the Dissemination tab.
  $root.on('click', '[data-dlf-fair-fix]', function() {
    var target = $(this).attr('data-dlf-fair-fix');
    $root.find('a[href="#deliverable-disseminationMetadata"]').tab('show');
    setTimeout(function() {
      var $question = $root.find('#deliverable-disseminationMetadata ' + target).closest('.dlf-question');
      if($question.length) {
        $('html, body').animate({
          scrollTop: $question.offset().top - 90
        }, 250);
      }
    }, 200);
  });

  // ---- Tooltips ----
  // global.js delegates a jQuery UI tooltip from the document that follows the cursor over every [title]. Inside
  // the redesigned form the browser's own title tooltip is enough, so the delegated one skips it.
  try {
    $(document).tooltip('option', 'items', '[title]:not(.dlf [title])');
  } catch(e) {
    // The tooltip widget is not initialised on this page; nothing to narrow.
  }

  // ---- General information: list components drawn as the design's lists ----
  var $strings = $root.find('[data-dlf-strings]');
  function text(key) {
    return String($strings.attr('data-' + key) || '');
  }

  // Watches a container and runs fn now and after every change the deliverable scripts make inside it.
  function watch($el, fn) {
    if(!$el.length) {
      return;
    }
    fn();
    if(window.MutationObserver) {
      var busy = false;
      var observer = new MutationObserver(function() {
        if(busy) {
          return;
        }
        busy = true;
        observer.disconnect();
        fn();
        $el.each(function() {
          observer.observe(this, {
              childList: true,
              subtree: true
          });
        });
        busy = false;
      });
      $el.each(function() {
        observer.observe(this, {
            childList: true,
            subtree: true
        });
      });
    }
  }

  // The "add" select of a list reads as the design's "+ Add ..." control. select2 draws its own configured
  // placeholder ("Select multiple options...") and redraws it on every change and every destroy/re-init, so the
  // label is kept on the select (data-dlf-add-label, also on the empty option for clones) and re-applied to the
  // rendered text whenever the form changes.
  function applyAddLabels() {
    $root.find('select[data-dlf-add-label]').each(function() {
      var value = $(this).val();
      if(value && value !== '-1') {
        return;
      }
      var label = $(this).attr('data-dlf-add-label');
      var $rendered = $(this).next('.select2').find('.select2-selection__rendered');
      if($rendered.length && $rendered.text() !== label) {
        $rendered.text(label).removeAttr('title');
      }
    });
  }

  function setAddLabel($select, label) {
    $select.attr('data-dlf-add-label', label).each(function() {
      $(this).find('option[value="-1"], option[value=""]').first().text(label);
    });
    applyAddLabels();
  }

  if(window.MutationObserver) {
    var labelsBusy = false;
    new MutationObserver(function() {
      if(labelsBusy) {
        return;
      }
      labelsBusy = true;
      applyAddLabels();
      labelsBusy = false;
    }).observe($root[0], {
        childList: true,
        subtree: true,
        characterData: true
    });
  }

  // "IPI 1.1" (or "Sub-action 2", "Priority action 10") pulled out of a label into the design's code pill.
  function splitCode(label, pattern) {
    var match = String(label).match(pattern);
    if(!match) {
      return null;
    }
    return {
        code: match[1].trim(),
        rest: String(label).replace(match[0], '').replace(/^[\s:.\-]+/, '').trim()
    };
  }

  // Performance indicators: code pill, "+ Add indicator (n left)" and no add control once the limit is reached.
  var $indicators = $root.find('.listindicators .elementsListComponent');
  watch($indicators.find('ul.list'), function() {
    var $select = $indicators.find('select');
    var max = parseInt($select.classParam ? $select.classParam('maxLimit') : 3, 10) || 3;
    var count = $indicators.find('ul.list > li.relationElement').length;
    $indicators.find('ul.list > li.relationElement').each(function() {
      var $name = $(this).find('.elementName');
      if(!$(this).find('.dlf-code').length) {
        var parts = splitCode($name.text(), /(IPI\s?\d+(?:\.\d+)*)/);
        if(parts) {
          $name.before($('<span class="dlf-code"></span>').text(parts.code));
        }
      }
    });
    $indicators.toggleClass('is-full', count >= max);
    setAddLabel($select, text('add-indicator').replace('{0}', Math.max(max - count, 0)));
  });

  // Activities and funding sources: a count beside the label and "+ Add ..." controls.
  function listCount($list, $label) {
    watch($list.find('ul.list'), function() {
      var count = $list.find('ul.list > li').length;
      var $count = $label.find('.dlf-count');
      if(!$count.length) {
        $count = $('<span class="dlf-count"></span>').appendTo($label);
      }
      $count.text(count || '');
    });
  }
  var $activityList = $root.find('#activityList');
  listCount($activityList, $activityList.closest('.dlf-list').find('.dlf-list__head label'));
  setAddLabel($activityList.find('select.activity'), text('add-activity'));
  var $fundingList = $root.find('#fundingSourceList');
  listCount($fundingList, $fundingList.closest('.dlf-list').find('.dlf-list__head label'));
  setAddLabel($fundingList.find('select.fundingSource'), text('add-funding-source'));

  // ---- SHFRM: priority action cards with their sub-actions ----
  // The priority actions are an elementsListComponent (its hidden list keeps the posted ids and drives the remove
  // logic of deliverableShfrm.js), and each one has a block in #projectPartnersBlock with its sub-actions. The
  // blocks become the design's cards, shown between the question and the "+ Add priority action" control. Nothing
  // moves: global.js finds a list's select through its .panel-body, so the cards must stay outside it, and the
  // order is a flex order on a wrapper (the answer block itself is shown and hidden by jQuery's slide).
  var $paComponent = $root.find('#actionsListReference .elementsListComponent');
  var $paBlocks = $root.find('#projectPartnersBlock');
  if($paComponent.length && $paBlocks.length) {
    var $shfrmBlock = $paBlocks.closest('.block-yesNo');
    if(!$shfrmBlock.children('.dlf-shfrm').length) {
      $shfrmBlock.children().wrapAll('<div class="dlf-shfrm"></div>');
    }
    $paComponent.closest('.form-group').addClass('dlf-shfrm__pa');
    setAddLabel($paComponent.find('select'), text('add-priority-action'));

    watch($paBlocks, function() {
      $paBlocks.children('.projectPartner').each(function() {
        var $card = $(this);
        var $title = $card.find('.blockTitle');
        if(!$title.find('.dlf-pa__remove').length) {
          var parts = splitCode($title.find('.priorityActionTitle').text(), /^(Priority action\s*\d+)/i);
          if(parts) {
            $title.find('.priorityActionTitle').text(parts.rest)
              .before($('<span class="dlf-code dlf-code--pa"></span>').text(parts.code));
          }
          $('<button type="button" class="dlf-pa__remove"></button>')
            .attr('aria-label', text('remove').replace('{0}', parts ? parts.code : ''))
            .appendTo($title);
        }
        // Sub-actions: a counted heading, numbered rows, and the add control after them.
        var $selector = $card.find('.subSectionsSelector');
        var $wrapper = $selector.find('.deliverableWrapper');
        var $head = $selector.children('.dlf-sub__head');
        if(!$head.length) {
          $head = $('<span class="dlf-sub__head"></span>')
            .append($('<span class="dlf-sub__label"></span>').text(text('sub-actions')))
            .append('<span class="requiredTag">*</span>')
            .append('<span class="dlf-count"></span>')
            .prependTo($selector);
        }
        $head.find('.dlf-count').text($wrapper.find('.deliverableActivity').length || '');
        $wrapper.find('.deliverableActivity').each(function() {
          var $name = $(this).children('span.name');
          if(!$(this).find('.dlf-code').length) {
            // deliverableShfrm.js cuts a new sub-action to 80 characters and keeps the full text in its title.
            var parts = splitCode($name.attr('title') || $name.text(), /^(Sub-action\s*\d+)/i);
            $name.removeAttr('title');
            if(parts) {
              $name.text(parts.rest).before($('<span class="dlf-code dlf-code--sub"></span>').text(parts.code));
            }
          }
        });
        setAddLabel($selector.find('.subActionsSelector select'), text('add-sub-action'));
      });
    });

    // The card's cross removes the priority action through its row in the hidden list, so deliverableShfrm.js
    // sees the same mouseenter + click it always did and removes the card itself.
    $paBlocks.on('click', '.dlf-pa__remove', function(e) {
      e.stopPropagation();
      var id = $(this).closest('.projectPartner').find('.actionidvalue').val();
      var $row = $paComponent.find('ul.list > li.relationElement').filter(function() {
        return $(this).find('.elementRelationID').val() === id;
      });
      $row.find('.removeElement').trigger('mouseenter').trigger('click');
    });
  }
  // The sub-action selects every new card clones carry the same label.
  setAddLabel($('[id^="subactionSelect-"] select'), text('add-sub-action'));

  // ---- Geographic scope: the scopes as toggle chips ----
  // The scope list stays the elementsListComponent the save reads (deliverable.geographicScopes); the chips add a
  // scope through onSelectElement (global.js) and remove it through the row's own remove control, so
  // setGeographicScope() still shows the region and country blocks.
  var $scopeComponent = $root.find('.geographicScopeBlock .elementsListComponent').first();
  var $scopeSelect = $scopeComponent.find('select.elementType-repIndGeographicScope');
  if($scopeSelect.length) {
    var $chips = $('<div class="dlf-scopeChips" role="group"></div>');
    $scopeComponent.addClass('dlf-scope');
    $scopeComponent.find('.panel-head label').append(
        $('<span class="dlf-scope__hint"></span>').text(text('select-all')));
    $scopeSelect.find('option').each(function() {
      if(this.value === '-1' || this.value === '') {
        return;
      }
      $('<button type="button" class="dlf-scopeChip" aria-pressed="false"></button>')
        .attr('data-scope', this.value)
        .append('<span class="dlf-scopeChip__box" aria-hidden="true"></span>')
        .append(document.createTextNode($(this).text()))
        .appendTo($chips);
    });
    $chips.insertAfter($scopeComponent.find('.panel-head'));

    var refreshChips = function() {
      var selected = $scopeComponent.find('ul.list > li.relationElement .elementRelationID').map(function() {
        return this.value;
      }).get();
      $chips.find('.dlf-scopeChip').each(function() {
        var on = $.inArray($(this).attr('data-scope'), selected) !== -1;
        $(this).toggleClass('is-on', on).attr('aria-pressed', on);
      });
    };
    watch($scopeComponent.find('ul.list'), refreshChips);

    $chips.on('click', '.dlf-scopeChip', function() {
      var value = $(this).attr('data-scope');
      var $row = $scopeComponent.find('ul.list > li.relationElement').filter(function() {
        return $(this).find('.elementRelationID').val() === value;
      });
      if($row.length) {
        $row.find('.removeElement').trigger('click');
      } else if(typeof onSelectElement === 'function') {
        $scopeSelect.find('option[value="' + value + '"]').prop('disabled', false);
        $scopeSelect.val(value);
        onSelectElement.call($scopeSelect[0]);
      }
    });
  }
  setAddLabel($root.find('.regionalBlock select'), text('add-region'));

  // ---- Cross-cutting dimensions: the focus levels as a segmented control ----
  // Each marker keeps its select (the posted repIndGenderYouthFocusLevel.id); the buttons set it.
  $root.find('#dlf-crossCutting select').each(function() {
    var $select = $(this);
    var $field = $select.closest('[class*="col-md-"]');
    if($field.find('.dlf-levels').length) {
      return;
    }
    var $levels = $('<span class="dlf-levels" role="radiogroup"></span>');
    $select.find('option').each(function() {
      if(this.value === '-1' || this.value === '') {
        return;
      }
      var parts = String($(this).text()).split(/\s+-\s+/);
      $('<button type="button" class="dlf-level" role="radio" aria-checked="false"></button>')
        .attr('data-level', this.value)
        .append($('<span class="dlf-level__n"></span>').text(parts[0]))
        .append(document.createTextNode(parts.slice(1).join(' - ') || parts[0]))
        .appendTo($levels);
    });
    $field.addClass('dlf-crossField').append($levels);
    var refresh = function() {
      $levels.find('.dlf-level').each(function() {
        var on = $(this).attr('data-level') === String($select.val());
        $(this).toggleClass('is-on', on).attr('aria-checked', on);
      });
    };
    refresh();
    $select.on('change', refresh);
    $levels.on('click', '.dlf-level', function() {
      $select.val($(this).attr('data-level')).trigger('change');
    });
  });

  // ---- Shared clusters: the design's cards (flag, country, code, lead) ----
  var isoByCountry = {};
  $root.find('select.countriesSelect option').each(function() {
    if(this.value) {
      isoByCountry[$(this).text().trim().toLowerCase()] = this.value.toLowerCase();
    }
  });
  var $shared = $root.find('.listClusters .elementsListComponent');
  setAddLabel($shared.find('select'), text('add-shared'));
  watch($shared.find('ul.list'), function() {
    $shared.find('ul.list > li.relationElement').each(function() {
      var $item = $(this);
      if($item.find('.dlf-shared').length) {
        return;
      }
      // "C102076 - Senegal: Activities led by ILRI"
      var label = $item.find('.elementName').text().trim();
      var match = label.match(/^(\S+)\s*-\s*([^:]+):?\s*(.*)$/);
      var code = match ? match[1] : label;
      var country = match ? match[2].trim() : '';
      var lead = match ? match[3].trim() : '';
      var iso = isoByCountry[country.toLowerCase()];
      var $card = $('<span class="dlf-shared"></span>')
        .append($('<span class="dlf-shared__flag" aria-hidden="true"></span>')
          .addClass(iso ? 'flag-icon flag-icon-' + iso : 'is-empty'))
        .append($('<span class="dlf-shared__text"></span>')
          .append($('<span class="dlf-shared__name"></span>').text(country || code)
            .append(country ? $('<span class="dlf-shared__code"></span>').text(' ' + code) : null))
          .append(lead ? $('<span class="dlf-shared__lead"></span>').text(lead) : null));
      if(code === $strings.attr('data-project-code')) {
        $card.append($('<span class="dlf-shared__this"></span>').text(text('this-section')));
      }
      $item.addClass('dlf-shared-item').find('.elementName').after($card);
    });
  });

  // ---- Word counters ----
  // applyWordCounter() (utils.js) prints "(20 words remaining of 20)"; the design drops the brackets. Only the
  // outer text changes, so the count it keeps updating is untouched.
  $root.find('.charCount').each(function() {
    $(this).contents().filter(function() {
      return this.nodeType === 3;
    }).each(function() {
      this.nodeValue = this.nodeValue.replace(/[()]/g, '');
    });
  });
});
