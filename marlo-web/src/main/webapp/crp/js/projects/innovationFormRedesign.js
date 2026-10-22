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
});
