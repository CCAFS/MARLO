/**
 * Project / cluster innovation form (A2-2613 redesign).
 *
 * Presentation only: projectInnovations.js keeps every behaviour and every selector it relies on, and
 * deliverableFormRedesign.js (A2-2607) draws the save state, the labels and the word counters. This file adds
 * what the innovation form draws on top of them.
 */
$(document).ready(function() {
  var $root = $('.dlf.inf');
  if(!$root.length) {
    return;
  }

  // ---- Header chip and General Information note follow the Innovation/Bundle answer ----
  // The note only tells what this visit changed: it shows once Bundled innovation is chosen here.
  var $bundleChip = $root.find('[data-inf-bundle-chip]');
  var $bundleNote = $root.find('[data-inf-bundle-note]');
  $root.on('change', 'input[name="innovation.projectInnovationInfo.innovationBundle"]', function() {
    $bundleChip.prop('hidden', this.value !== 'true');
    $bundleNote.prop('hidden', this.value !== 'true');
  });

  // ---- Header: the shared clusters count is highlighted once there is one ----
  var counter = document.getElementById('modalCounterShared');
  if(counter) {
    var refreshCounter = function() {
      $(counter).toggleClass('is-on', (parseInt(counter.textContent, 10) || 0) > 0);
    };
    refreshCounter();
    if(window.MutationObserver) {
      new MutationObserver(refreshCounter).observe(counter, {
          childList: true,
          characterData: true,
          subtree: true
      });
    }
  }

  // ---- Annual Report year: where the innovation will be listed after saving ----
  var $yearHint = $root.find('[data-inf-year-hint]');
  var $year = $root.find('select[name="innovation.projectInnovationInfo.year"]');
  function refreshYearHint() {
    var year = parseInt($year.val(), 10);
    var phaseYear = parseInt($yearHint.attr('data-phase-year'), 10);
    if(!year || !phaseYear) {
      $yearHint.text('');
      return;
    }
    $yearHint.text($yearHint.attr(year >= phaseYear ? 'data-current' : 'data-previous'));
  }
  if($yearHint.length && $year.length) {
    refreshYearHint();
    $year.on('change', refreshYearHint);
  }

  // ---- General Information: list components drawn as the design's lists ----
  var $strings = $root.find('#innovationGeneral [data-dlf-strings]');

  function text(key) {
    return String($strings.attr('data-' + key) || '');
  }

  // Text with {0}, {1}... placeholders, as global.properties writes them
  function format(value, args) {
    return String(value || '').replace(/\{(\d+)\}/g, function(match, index) {
      return args[index] !== undefined ? args[index] : match;
    });
  }

  // Runs fn now and after every change the form scripts make inside $el.
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

  // The "add" select of a list reads "+ Add ...". data-dlf-add-label lets deliverableFormRedesign.js keep the label
  // after select2 redraws and hide the control once the list is full.
  function setAddLabel($select, label) {
    if(!$select.length || !label) {
      return;
    }
    $select.attr('data-dlf-add-label', label);
    $select.find('option[value="-1"], option[value=""]').first().text(label);
    var value = $select.val();
    if(!value || value === '-1') {
      $select.next('.select2').find('.select2-selection__rendered').text(label).removeAttr('title');
    }
  }

  setAddLabel($root.find('.inf-leads .elementsListComponent select'), text('add-lead'));
  setAddLabel($root.find('.inf-indicators .elementsListComponent select'), text('add-indicator'));

  // ---- Value chain(s) (A2-2613) ----
  // "Not value-chain specific" is exclusive: choosing it clears and disables every other value chain, and choosing any
  // other value chain disables it. "Other" shows the other value chain text.
  var $valueChains = $root.find('[data-inf-value-chains]');
  function refreshValueChains() {
    var $boxes = $valueChains.find('.inf-vcChip input[type="checkbox"]');
    var $notSpecific = $boxes.filter('[data-not-specific]');
    var $specific = $boxes.not('[data-not-specific]');
    var notSpecificOn = $notSpecific.is(':checked');
    var specificOn = $specific.is(':checked');
    $specific.prop('disabled', notSpecificOn);
    $notSpecific.prop('disabled', specificOn);
    $boxes.each(function() {
      var why = '';
      if(this.disabled) {
        why = $valueChains.attr($(this).is('[data-not-specific]') ? 'data-clear-first' : 'data-unavailable');
      }
      $(this).closest('.inf-vcChip').toggleClass('is-on', this.checked).toggleClass('is-disabled', this.disabled)
        .attr('title', why || null);
    });
    var $note = $valueChains.find('[data-inf-vc-note]');
    $note.text(notSpecificOn ? $valueChains.attr('data-note') : '').prop('hidden', !notSpecificOn);
    $valueChains.find('[data-inf-vc-other]').toggle($boxes.filter('[data-other]').is(':checked'));
    var selected = $boxes.filter(':checked').length;
    $valueChains.find('[data-inf-vc-count]').text(selected ? format($valueChains.attr('data-selected'), [selected]) : '');
    // The missing message stands until a value chain is chosen
    var missing = $valueChains.find('.inf-vc__label').hasClass('fieldError') && !selected;
    $valueChains.find('[data-inf-vc-error]').prop('hidden', !missing);
  }
  if($valueChains.length) {
    $valueChains.on('change', '.inf-vcChip input[type="checkbox"]', function() {
      if(this.checked && $(this).is('[data-not-specific]')) {
        $valueChains.find('.inf-vcChip input[type="checkbox"]').not(this).prop('checked', false);
      }
      refreshValueChains();
    });
    refreshValueChains();
  }

  // Regional countries: deliverableFormRedesign.js labels every select of .regionalBlock "+ Add region..."; the
  // countries field under "specify countries" is a multi-select and reads "+ Add country..." instead.
  var $specifiedCountries = $root.find('.block-hasSpecifiedOutputCountries');
  $specifiedCountries.find('select').removeAttr('data-dlf-add-label');
  watch($specifiedCountries, function() {
    $specifiedCountries.find('.select2-search__field').each(function() {
      if(this.getAttribute('placeholder') !== text('add-country')) {
        this.setAttribute('placeholder', text('add-country'));
      }
    });
  });

  // Performance indicators: "IPI 1.1" / "PDO Indicator 3" pulled out of the label into the design's code pill.
  var $indicators = $root.find('.inf-indicators .elementsListComponent');
  watch($indicators.find('ul.list'), function() {
    $indicators.find('ul.list > li.relationElement').each(function() {
      if($(this).find('.dlf-code').length) {
        return;
      }
      var $name = $(this).find('.elementName');
      var match = $name.text().match(/(IPI\s?\d+(?:\.\d+)*|PDO Indicator\s?\d+)/);
      if(match) {
        $name.before($('<span class="dlf-code"></span>').text(match[1].replace(/^PDO Indicator\s?/, 'PDO ')));
      }
    });
  });

  // Contributing external partners: "n of 5" beside the label, the sort hint in the help, "+ Add ... (max 5)".
  var $externals = $root.find('.inf-externals');
  var $externalsList = $externals.find('.elementsListComponent');
  var $externalsSelect = $externalsList.find('select');
  var externalsMax = parseInt(((($externalsSelect.attr('class') || '').match(/\bmaxLimit-(\d+)/)) || [])[1], 10) || 5;
  var $externalsHelp = $externals.find('.helpLabel, .note').first();
  if($externalsHelp.length && !$externalsHelp.find('.inf-sortHint').length) {
    $externalsHelp.append(' ').append($('<span class="inf-sortHint"></span>').text($externals.attr('data-inf-sort-hint')));
  }
  setAddLabel($externalsSelect, format(text('add-external'), [externalsMax]));
  watch($externalsList.find('ul.list'), function() {
    var $label = $externalsList.find('.panel-head label').first();
    var $count = $label.find('.inf-count');
    if(!$count.length) {
      $count = $('<span class="inf-count"></span>');
      var $required = $label.find('.requiredTag');
      if($required.length) {
        $required.after($count);
      } else {
        $label.append($count);
      }
    }
    $count.text(format(text('of-max'), [$externalsList.find('ul.list > li.relationElement').length, externalsMax]));
  });

  // Responsible institution: "Contact person(s) at this institution  n selected" above the people of each partner.
  // changePartnerInstitution() (projectInnovations.js) redraws the people when the institution changes.
  var $partners = $root.find('.projectInnovationsPartners');
  function refreshContacts() {
    $partners.find('.usersBlock').each(function() {
      var $block = $(this);
      var $head = $block.prev('.inf-contacts');
      if(!$block.children('.deliverableUserItem').length) {
        $head.remove();
        return;
      }
      if(!$head.length) {
        $head = $('<span class="inf-contacts"></span>').text(text('contacts') + ' ')
          .append('<span class="inf-contacts__count"></span>').insertBefore($block);
      }
      var selected = $block.find('.deliverableUserItem input:checked').length;
      $head.find('.inf-contacts__count').text(selected ? format(text('selected'), [selected]) : '');
    });
  }
  watch($partners, refreshContacts);
  $partners.on('change', 'input', refreshContacts);

  // ---- Cross-cutting alignment: SDGs ----
  // Each "Goal n: Name" of the list reads as "GOAL n" over the name, beside the goal's icon (projectInnovations.js
  // draws it); the options are listed in goal order.
  var $sdgs = $root.find('[data-inf-sdgs]');
  var $sdgList = $sdgs.find('.elementsListComponent');
  var $sdgSelect = $sdgList.find('select');
  function goalOf(label) {
    var match = String(label || '').match(/^\s*Goal\s+(\d+)\s*:\s*(.*)$/);
    return match ? {
        n: parseInt(match[1], 10),
        name: match[2]
    } : null;
  }
  $sdgSelect.find('option').filter(function() {
    return goalOf($(this).text());
  }).sort(function(a, b) {
    return goalOf($(a).text()).n - goalOf($(b).text()).n;
  }).appendTo($sdgSelect);
  setAddLabel($sdgSelect, $sdgs.attr('data-add-label'));
  watch($sdgList.find('ul.list'), function() {
    var $items = $sdgList.find('ul.list > li.relationElement');
    $items.each(function() {
      var $name = $(this).find('.elementName');
      if($(this).hasClass('inf-sdgItem')) {
        return;
      }
      var goal = goalOf($name.text());
      if(!goal) {
        return;
      }
      $(this).addClass('inf-sdgItem');
      $name.empty().append($('<span class="inf-sdgItem__goal"></span>').text('Goal ' + goal.n))
        .append($('<span class="inf-sdgItem__name"></span>').text(goal.name));
    });
    var $label = $sdgList.find('.panel-head label').first();
    var $count = $label.find('.inf-count');
    if(!$count.length) {
      $count = $('<span class="inf-count"></span>');
      var $required = $label.find('.requiredTag');
      if($required.length) {
        $required.after($count);
      } else {
        $label.append($count);
      }
    }
    $count.text($items.length ? format($sdgs.attr('data-selected'), [$items.length]) : '');
  });

  // ---- Cross-cutting alignment: Impact Area scores ----
  // Score ids: 1 = (0) Not Targeted, 2 = (1) Significant, 3 = (2) Principal.
  var $ia = $root.find('[data-inf-ia]');
  var $iaRows = $ia.find('[data-inf-ia-row]');
  $ia.on('click', '[data-inf-ia-guide]', function() {
    var open = $(this).attr('aria-expanded') !== 'true';
    $(this).attr('aria-expanded', open).text($(this).attr(open ? 'data-hide' : 'data-show'));
    $ia.find('#' + $(this).attr('aria-controls')).prop('hidden', !open);
  });
  function scoreOf($row) {
    // Read-only forms print the answer only: the principal note tells the score 2 apart
    var $checked = $row.find('input[type="radio"]:checked');
    if($checked.length) {
      return $checked.val();
    }
    return $row.find('.inf-iaRow__principal').css('display') !== 'none' ? '3' : '';
  }
  function refreshImpactAreas() {
    var principal = 0;
    var notTargeted = 0;
    $iaRows.each(function() {
      var $row = $(this);
      var score = scoreOf($row);
      principal += score === '3' ? 1 : 0;
      notTargeted += score === '1' ? 1 : 0;
      var tag = $row.attr('data-evidence');
      var has = !!tag && $('.referenceListReadiness input[type="checkbox"][id$=".' + tag + '"]:checked').length > 0;
      $row.find('[data-inf-evidence]').toggleClass('is-added', has)
        .text($ia.attr(has ? 'data-evidence-added' : 'data-evidence-pending'));
    });
    var warn = '';
    if(principal > 2) {
      warn = $ia.attr('data-warn-many');
    } else if($iaRows.length && notTargeted === $iaRows.length) {
      warn = $ia.attr('data-warn-zero');
    }
    $ia.find('[data-inf-ia-warn]').text(warn).prop('hidden', !warn);
  }
  if($ia.length) {
    $ia.on('change', 'input[type="radio"]', refreshImpactAreas);
    $root.on('change', '.referenceListReadiness input[type="checkbox"]', refreshImpactAreas);
    watch($root.find('.referenceListReadiness'), refreshImpactAreas);
    refreshImpactAreas();
    // "Go to Knowledge & Scaling": open the tab and bring its evidence list into view
    $ia.on('click', '[data-inf-go-tab]', function(event) {
      event.preventDefault();
      var $tab = $('a[data-toggle="tab"][href="' + $(this).attr('href') + '"]');
      $tab.one('shown.bs.tab', function() {
        var $target = $root.find('.referenceBlock').first();
        if($target.length) {
          $('html, body').animate({
            scrollTop: $target.offset().top - 140
          }, 300);
        }
      }).tab('show');
    });
  }
});
