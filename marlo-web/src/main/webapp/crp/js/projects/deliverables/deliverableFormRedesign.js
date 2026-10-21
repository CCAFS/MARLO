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
      var first = this.firstChild;
      $(this).contents().filter(function() {
        return this.nodeType === 3;
      }).each(function() {
        // Only the label's own leading text loses its indent: a text node after inline markup ("<b>contributing</b>
        // to ...") keeps the space that separates the words.
        this.nodeValue = this.nodeValue.replace(/:\s*$/, ' ');
        if(this === first) {
          this.nodeValue = this.nodeValue.replace(/^\s+/, '');
        }
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

  // An add control with nothing left to add is hidden: every option is taken (global.js and deliverableShfrm.js
  // disable the chosen ones) or the list reached its maxLimit-N. Each of those changes comes with a list row
  // added or removed, or a select2 re-init, so the same observer re-checks it.
  function hasNothingToAdd($select) {
    var free = $select.find('option').filter(function() {
      return this.value !== '' && this.value !== '-1' && !this.disabled;
    }).length;
    var max = parseInt((($select.attr('class') || '').match(/\bmaxLimit-(\d+)/) || [])[1], 10) || 0;
    var count = $select.closest('.elementsListComponent').find('ul.list > li.relationElement').length;
    return free === 0 || (max > 0 && count >= max);
  }

  function hideExhaustedAdds() {
    $root.find('select[data-dlf-add-label]').each(function() {
      var $container = $(this).next('.select2');
      var exhausted = hasNothingToAdd($(this));
      if($container.hasClass('dlf-isExhausted') !== exhausted) {
        $container.toggleClass('dlf-isExhausted', exhausted);
      }
    });
    // Countries: the inline "+ Add country..." prompt goes once select2's limit (one country for a national
    // scope) is reached or every country is chosen.
    $root.find('.nationalBlock select.countriesSelect').each(function() {
      var select2 = $(this).data('select2');
      var limit = select2 ? parseInt(select2.options.get('maximumSelectionLength'), 10) || 0 : 0;
      var chosen = ($(this).val() || []).length;
      var free = $(this).find('option').filter(function() {
        return this.value !== '' && !this.selected && !this.disabled;
      }).length;
      var exhausted = (limit > 0 && chosen >= limit) || free === 0;
      var $search = $(this).next('.select2').find('.select2-search--inline');
      if($search.hasClass('dlf-isExhausted') !== exhausted) {
        $search.toggleClass('dlf-isExhausted', exhausted);
      }
    });
  }

  if(window.MutationObserver) {
    var labelsBusy = false;
    new MutationObserver(function() {
      if(labelsBusy) {
        return;
      }
      labelsBusy = true;
      applyAddLabels();
      hideExhaustedAdds();
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
    // The select lists the scopes alphabetically; the chips follow their ids, broadest first as in the design
    // (Global, Regional, Multi-national, National, Sub-national, then "yet to be determined").
    $scopeSelect.find('option').sort(function(a, b) {
      return (parseInt(a.value, 10) || 0) - (parseInt(b.value, 10) || 0);
    }).each(function() {
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

  // The countries multi-select reads as the region field: its search box is the "+ Add country..." prompt after
  // the chips. select2 clears the placeholder whenever a country is chosen and redraws the choices on every
  // change, so the prompt is set again after each redraw.
  var addCountry = text('add-country');
  watch($root.find('.nationalBlock .selectList'), function() {
    $root.find('.nationalBlock .select2-selection--multiple .select2-search__field').each(function() {
      if(this.getAttribute('placeholder') !== addCountry) {
        this.setAttribute('placeholder', addCountry);
      }
    });
  });

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

  // ---- Partner people: the whole card selects or clears the person ----
  // Delegated, because changePartnerInstitution() (deliverableInfo.js) redraws the cards when the partner changes.
  // The click is taken over from the label so a chosen responsible person (a radio) can be cleared as well.
  $root.on('click', '#dlf-partners .deliverableUserItem', function(e) {
    if($(e.target).closest('.feedback-flex-items, .commentNumberContainer, a, button').length) {
      return;
    }
    var $input = $(this).find('input[type="radio"], input[type="checkbox"]').first();
    if(!$input.length || $input.prop('disabled') || e.target === $input[0]) {
      return;
    }
    e.preventDefault();
    $input.prop('checked', !$input.prop('checked')).trigger('change');
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

  hideExhaustedAdds();

  // ---- Participants/Trainees: the question's helper says why the answer is Yes ----
  // A capacity development event (known when the page loads) and the trainees indicator (searchIndicator() in
  // deliverableDissemination.js, on every indicator change) each set the answer to Yes. The general helper stays
  // while neither applies.
  $root.find('.dlf-traineesHelper').each(function() {
    var $helper = $(this);
    var general = $.trim($helper.text());
    var $question = $helper.closest('.yesNoTrainees');
    var refresh = function() {
      var capacity = $helper.attr('data-capacity') === 'true';
      var indicator = $question.attr('data-indicator-linked') === 'true';
      var key = capacity && indicator ? 'both' : capacity ? 'capacity' : indicator ? 'indicator' : '';
      $helper.text((key && text('trainees-helper-' + key)) || general);
    };
    $question.on('dlf:traineesIndicator', refresh);
    refresh();
  });

  // ---- Participants/Trainees: a note under each number, as the design ----
  // The total reads "All participants/trainees"; the others give their share of the total, or say a value is higher
  // than the total, or that the number of females is marked as not known. Notes only: the save is not blocked.
  $root.find('.block-participantNumbers').each(function() {
    var $numbers = $(this);
    var $total = $numbers.find('input[name$=".participants"]');
    var $dontKnow = $numbers.find('#dontKnowFemale');
    var number = function($input) {
      var value = String($input.val() || '').trim();
      return value === '' || isNaN(value) ? null : parseFloat(value);
    };
    $numbers.children('[class*="col-"]').each(function() {
      if(!$(this).children('.dlf-numberNote').length) {
        $('<p class="dlf-numberNote" aria-live="polite"></p>').insertAfter($(this).children('.text-area-container'));
      }
    });
    var refresh = function() {
      var total = number($total);
      $numbers.children('[class*="col-"]').each(function() {
        var $card = $(this);
        var $input = $card.find('input.numericInput');
        var $note = $card.children('.dlf-numberNote');
        var value = number($input);
        var over = false;
        var note;
        if($input.is($total)) {
          note = text('trainees-total');
        } else if($dontKnow.length && $card.find($dontKnow).length && $dontKnow.is(':checked')) {
          note = text('trainees-not-known');
        } else if(value !== null && total !== null && value > total) {
          over = true;
          note = text('trainees-over').replace('{0}', total);
        } else if(value !== null && total) {
          note = text('trainees-percent').replace('{0}', Math.round(value / total * 100));
        } else {
          note = '—';
        }
        $card.toggleClass('is-overTotal', over);
        $note.text(note);
      });
    };
    $numbers.on('input change', 'input', refresh);
    refresh();
  });

  // ---- Participants/Trainees: the split across shared clusters as the design's table ----
  // deliverableDissemination.js keeps adding the rows (addcluster), the totals and the remaining values; this draws
  // each row's team cell (flag, code, "this cluster" and "submitted" badges, country), the sum row, the colour of
  // the remaining values and the status line.
  $root.find('.sharedClustersList').each(function() {
    var $split = $(this);
    var $status = $split.find('.dlf-splitStatus');
    if(!$status.length) {
      return;
    }
    var keys = ['participants', 'females', 'african', 'youth'];
    var remainingClass = {
        participants: 'remainingTrainees',
        females: 'remainingFemales',
        african: 'remainingAfrican',
        youth: 'remainingYouth'
    };
    var ownId = String($strings.attr('data-project-code') || '').replace(/^C/, '');
    // "C102076 - Senegal: Activities led by ILRI", by project id: the shared list, its select and the header chip.
    var labels = {};
    $root.find('.listClusters select option').each(function() {
      if(this.value && this.value !== '-1') {
        labels[this.value] = $(this).text();
      }
    });
    $root.find('.listClusters ul.list > li.relationElement').each(function() {
      labels[$(this).find('.elementRelationID').val()] = $(this).find('.elementName').text();
    });
    if(ownId) {
      labels[ownId] = labels[ownId] || $root.find('.dlf-chip--owner').attr('title') || '';
    }
    var countryOf = function(id) {
      var match = String(labels[id] || '').trim().match(/^(\S+)\s*-\s*([^:]+):?/);
      return match ? match[2].trim() : '';
    };
    var number = function(value) {
      var n = parseInt(String(value || '').trim(), 10);
      return isNaN(n) ? 0 : n;
    };

    var decorateRows = function() {
      $split.find('.listClusterDM > .form-group.row').each(function() {
        var $row = $(this);
        if($row.children('.dlf-team').length) {
          return;
        }
        var id = String($row.attr('clusteridparticipant') || '');
        var country = countryOf(id);
        var iso = isoByCountry[country.toLowerCase()];
        var submitted = $row.find('.clusterSubmitted').attr('isSubmit') === 'true';
        var $code = $('<span class="dlf-team__code"></span>').append($('<b></b>').text('C' + id));
        if(id === ownId) {
          $code.append($('<span class="dlf-team__badge"></span>').text($status.attr('data-this-team')));
        }
        if(submitted) {
          $row.addClass('is-submitted');
          $code.append($('<span class="dlf-team__badge dlf-team__badge--lock"></span>')
            .attr('title', $status.attr('data-submitted-help')).text($status.attr('data-submitted')));
        }
        $('<div class="dlf-team"></div>')
          .append($('<span class="dlf-team__flag" aria-hidden="true"></span>')
            .addClass(iso ? 'flag-icon flag-icon-' + iso : 'is-empty'))
          .append($('<span class="dlf-team__text"></span>').append($code)
            .append(country ? $('<span class="dlf-team__country"></span>').text(country) : null))
          .prependTo($row);
      });
    };

    var refresh = function() {
      decorateRows();
      var pending = 0;
      var over = false;
      var allZero = true;
      $.each(keys, function(i, key) {
        var sum = 0;
        $split.find('.listClusterDM input[name$=".' + key + '"]').each(function() {
          sum += number(this.value);
        });
        $split.find('[data-dlf-sum="' + key + '"]').text(sum);
        var $remaining = $split.find('.' + remainingClass[key]);
        var remaining = number($remaining.text());
        $remaining.toggleClass('is-over', remaining < 0).toggleClass('is-pending', remaining > 0);
        if(remaining < 0) {
          over = true;
        }
        if(remaining !== 0) {
          allZero = false;
        }
        if(key === 'participants' && remaining > 0) {
          pending = remaining;
        }
      });
      var state = allZero ? 'ok' : (over ? 'over' : 'pending');
      var message = state === 'ok' ? $status.attr('data-ok') : state === 'over' ? $status.attr('data-over')
        : (pending > 0 ? $status.attr('data-pending').replace('{0}', pending) : $status.attr('data-pending-some'));
      $status.attr('data-state', state).find('.dlf-splitStatus__text').text(message);
    };

    // Values typed here or in the totals, rows added by addcluster and the remaining values it writes.
    $split.on('input change', 'input', refresh);
    $root.on('input change', '.block-participantNumbers input', function() {
      setTimeout(refresh, 0);
    });
    watch($split.find('.listClusterDM, .remaining-container'), refresh);

    // "Manage shared clusters": the shared list sits in General information.
    $split.on('click', '[data-dlf-manage-shared]', function() {
      var $target = $root.find('.listClusters').first();
      var $pane = $target.closest('.tab-pane');
      if($pane.length && !$pane.hasClass('active')) {
        $root.find('[role="tab"][href="#' + $pane.attr('id') + '"]').tab('show');
      }
      setTimeout(function() {
        $target[0].scrollIntoView({
            behavior: 'smooth',
            block: 'center'
        });
      }, 200);
    });
  });

  // ---- Dissemination: the design's sync states, source chips and Web of Science hints ----
  // deliverableDissemination.js keeps driving the sync (fillMetadata buttons), the found / not-found messages, the
  // Web of Science lookup and its Open Access suggestion through ids and inline styles; this only presents them.
  var $findable = $root.find('.block-findable').first();
  if($findable.length) {
    var observe = function(nodes, options, fn) {
      if(!window.MutationObserver) {
        return;
      }
      var busy = false;
      var observer = new MutationObserver(function() {
        if(busy) {
          return;
        }
        busy = true;
        fn();
        observer.takeRecords();
        busy = false;
      });
      $(nodes).each(function() {
        observer.observe(this, options);
      });
    };
    var channelName = function() {
      var $channel = $findable.find('select.disseminationChannel');
      var value = $channel.val();
      return value && value !== '-1' ? $.trim($channel.find('option:selected').text()) : '';
    };

    // Synced or not: the note under the URL and the "From <channel>" chip on its label.
    var $note = $findable.find('.dlf-urlNote');
    var $urlLabel = $findable.find('.url-field label').first();
    var refreshSync = function() {
      // Its own display, not :visible: the tab can be hidden when this runs.
      var $unsync = $findable.find('#fillMetadata .unSyncBlock');
      var synced = $unsync.length > 0 && $unsync.css('display') !== 'none'
        && $findable.find('#fillMetadata').css('display') !== 'none';
      if($note.length) {
        var noteText = $note.attr(synced ? 'data-synced' : 'data-unsynced');
        if($note.text() !== noteText) {
          $note.text(noteText);
        }
      }
      var $chip = $urlLabel.children('.dlf-fromChip');
      var name = channelName();
      if(synced && name) {
        if(!$chip.length) {
          $chip = $('<span class="dlf-fromChip"></span>').appendTo($urlLabel);
        }
        var chipText = text('dissem-from').replace('{0}', name);
        if($chip.text() !== chipText) {
          $chip.text(chipText);
        }
      } else {
        $chip.remove();
      }
    };
    refreshSync();
    observe($findable.find('#fillMetadata')[0], {
        attributes: true,
        subtree: true,
        attributeFilter: ['style']
    }, refreshSync);
    $findable.on('change', 'select.disseminationChannel', refreshSync);

    // DOI: the design's info mark with the explanation as its title.
    $findable.find('.doi-bridge .input > label').first().each(function() {
      if(!$(this).children('.dlf-infoMark').length) {
        $('<span class="dlf-infoMark" aria-hidden="false"></span>').attr('title', text('dissem-doi-help'))
          .attr('aria-label', text('dissem-doi-help')).appendTo(this);
      }
    });

    // What the sync found, in the design's words: "Metadata found in <channel>" / "... in Web of Science".
    var $outputs = $findable.find('#output-dissemination, #output-wos');
    var refreshOutputs = function() {
      $outputs.each(function() {
        var $output = $(this);
        var raw = $.trim($output.text());
        var found = /^Found metadata successfully in/i.test(raw);
        var wanted = raw;
        if(found && this.id === 'output-wos') {
          wanted = text('dissem-wos-found');
        } else if(found) {
          wanted = text('dissem-found').replace('{0}', channelName() || raw.replace(/^Found metadata successfully in\s*/i, ''));
        }
        $output.toggleClass('is-found', found || /^Metadata found in/i.test(raw))
          .toggleClass('is-warn', !!raw && !found && !/^Metadata found in/i.test(raw) && !/^Searching/i.test(raw));
        if(wanted !== raw) {
          $output.text(wanted);
        }
      });
    };
    refreshOutputs();
    observe($outputs, {
        childList: true,
        characterData: true,
        subtree: true
    }, refreshOutputs);

    // "View Web of Science metadata": the modal's fields inline, under the chips, as the design.
    var $wosButton = $findable.find('#WOSModalBtn');
    var $container = $findable.find('#metadata-container');
    if($wosButton.length && $container.length) {
      $wosButton.appendTo($container);
      var $link = $wosButton.find('a').removeAttr('data-toggle').removeAttr('data-target');
      var $panel = $('<div class="dlf-wosPanel" hidden></div>').insertAfter($container);
      $link.empty().append($('<span class="dlf-wosLink__text"></span>').text(text('dissem-wos-view')))
        .append('<svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true">'
          + '<path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" '
          + 'stroke-linejoin="round"></path></svg>')
        .attr({
            role: 'button',
            'aria-expanded': 'false'
        });
      $link.on('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var open = $panel.prop('hidden');
        if(open) {
          $panel.empty();
          $('#WOSModal table tr').each(function() {
            var key = $.trim($(this).children('th').text());
            var value = $.trim($(this).children('td').text());
            $('<span class="dlf-wosPanel__item"></span>')
              .append($('<span class="dlf-wosPanel__key"></span>').text(key))
              .append($('<span class="dlf-wosPanel__value"></span>').text(value || '—'))
              .appendTo($panel);
          });
        }
        $panel.prop('hidden', !open);
        $link.attr('aria-expanded', String(open)).toggleClass('is-open', open)
          .find('.dlf-wosLink__text').text(text(open ? 'dissem-wos-hide' : 'dissem-wos-view'));
      });
      // Unsync, a new lookup or a channel change hides the link: the open panel goes with it.
      observe($wosButton[0], {
          attributes: true,
          attributeFilter: ['style']
      }, function() {
        if($wosButton.css('display') === 'none' && !$panel.prop('hidden')) {
          $panel.prop('hidden', true);
          $link.attr('aria-expanded', 'false').removeClass('is-open')
            .find('.dlf-wosLink__text').text(text('dissem-wos-view'));
        }
      });
    }

    // Open Access: green when the answer matches Web of Science, amber with the suggestion when it does not.
    $root.find('.WOS_tag').has('#WOS_tag_IOA_yes').each(function() {
      var $tag = $(this);
      var $yes = $tag.find('#WOS_tag_IOA_yes');
      var $no = $tag.find('#WOS_tag_IOA_no');
      var $hint = $('<span class="dlf-wosHint"></span>').appendTo($tag);
      var refreshHint = function() {
        var suggestion = $yes.css('display') !== 'none' ? $yes : ($no.css('display') !== 'none' ? $no : null);
        if(!suggestion) {
          $hint.attr('data-state', '').text('');
          return;
        }
        var expected = suggestion.is($yes) ? 'true' : 'false';
        var answer = $root.find('input[name="deliverable.dissemination.isOpenAccess"]:checked').val();
        var word = $.trim(suggestion.text());
        var match = answer === expected;
        var hintText = text(match ? 'dissem-oa-match' : 'dissem-oa-mismatch').replace('{0}', word);
        $hint.attr('data-state', match ? 'match' : 'mismatch');
        if($hint.text() !== hintText) {
          $hint.text(hintText);
        }
      };
      refreshHint();
      observe([$tag[0], $yes[0], $no[0]], {
          attributes: true,
          attributeFilter: ['style']
      }, refreshHint);
      $root.on('change', 'input[name="deliverable.dissemination.isOpenAccess"]', refreshHint);
    });
  }

  // ---- Participants/Trainees: "What do these mean?" as the design's types guide ----
  // The rows come from the typeOfActivityTable modal (name, description); "Use this type" sets the matching option
  // of the type of activity select (deliverableDissemination.js then shows the formal training details).
  $root.find('.block-typeActivity .note.left a[href="#modal-typeOfActivity"]').each(function() {
    var $opener = $(this).removeAttr('data-toggle');
    var $select = $opener.closest('.block-typeActivity').find('select.trainingType');
    var $guide = null;
    var normalise = function(value) {
      return $.trim(String(value || '')).replace(/\s+/g, ' ').toLowerCase();
    };
    var close = function() {
      if(!$guide) {
        return;
      }
      $guide.remove();
      $guide = null;
      $('html').removeClass('dlf-guideOpen');
      $(document).off('keydown.dlfTypesGuide');
      $opener.trigger('focus');
    };
    var open = function() {
      close();
      var current = String($select.val() || '');
      var valueOf = {};
      $select.find('option').each(function() {
        if(this.value && this.value !== '-1') {
          valueOf[normalise($(this).text())] = this.value;
        }
      });
      var $list = $('<div class="dlf-typesGuide__list"></div>');
      $('#modal-typeOfActivity table tbody tr').each(function() {
        var $cells = $(this).children('td');
        var name = $.trim($cells.eq(0).text());
        if(!name) {
          return;
        }
        var value = valueOf[normalise(name)];
        var selected = value !== undefined && value === current;
        var $row = $('<div class="dlf-typesGuide__row"></div>').toggleClass('is-selected', selected)
          .append($('<span class="dlf-typesGuide__name"></span>').text(name))
          .append($('<span class="dlf-typesGuide__desc"></span>').text($.trim($cells.eq(1).text())));
        if(value !== undefined && $select.length && !$select.prop('disabled')) {
          $('<button type="button" class="dlf-typesGuide__use"></button>').attr('data-value', value)
            .text(text(selected ? 'types-selected' : 'types-use')).appendTo($row);
        } else {
          $row.append('<span></span>');
        }
        $list.append($row);
      });
      $guide = $('<div class="dlf-typesGuide" role="presentation"></div>')
        .append($('<div class="dlf-typesGuide__dialog" role="dialog" aria-modal="true" tabindex="-1"></div>')
          .attr('aria-label', text('types-title'))
          .append($('<div class="dlf-typesGuide__head"></div>')
            .append($('<span class="dlf-typesGuide__title"></span>').text(text('types-title')))
            .append($('<button type="button" class="dlf-typesGuide__x" data-dlf-close></button>')
              .attr('aria-label', text('types-close'))))
          .append($list)
          .append($('<div class="dlf-typesGuide__foot"></div>')
            .append($('<button type="button" class="dlf-typesGuide__close" data-dlf-close></button>')
              .text(text('types-close')))))
        .appendTo('body');
      $('html').addClass('dlf-guideOpen');
      $guide.on('click', function(e) {
        if(e.target === this || $(e.target).closest('[data-dlf-close]').length) {
          close();
        }
      });
      $guide.on('click', '.dlf-typesGuide__use', function() {
        $select.val($(this).attr('data-value')).trigger('change');
        close();
      });
      $(document).on('keydown.dlfTypesGuide', function(e) {
        if(e.key === 'Escape') {
          close();
        }
      });
      $guide.find('.dlf-typesGuide__dialog').trigger('focus');
    };
    $opener.on('click', function(e) {
      e.preventDefault();
      open();
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
