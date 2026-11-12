/* ------------------------------------------------------------------------------------------------
 * Project / cluster locations section (A2-2735 redesign), rendered by projectLocations.ftl.
 *
 * Every location row, location level and region carries the hidden inputs that
 * ProjectLocationAction.save() reads. The page posts whatever sits in the form, and the action
 * deletes what it no longer receives, so after every add or removal lcReindex() renames the
 * inputs with contiguous indexes:
 *   project.locationsData[i].{id,name,isList}
 *   project.locationsData[i].locElements[j].{id,name,locElement.isoAlpha2,locGeoposition.*}
 *   project.projectRegions[k].{id,scope,locElement.id | locElementType.id | locElementId | locElementTypeId}
 * A level whose only rows are funding-source suggestions posts nothing: those rows travel in
 * project.countryFS[n].selected instead.
 *
 * The map is Leaflet 1.9.4 with OpenStreetMap tiles, created the first time it scrolls into view.
 * It never geocodes on render; Nominatim is called once per coordinate site, on "Add location", to
 * learn the country the site sits in (saveGeoProjectLocation needs its ISO code).
 * --------------------------------------------------------------------------------------------- */

$(document).ready(lcInit);

var lcText = {};
var $lcRoot = $();
var lcEditable = false;
var lcScopeEditable = false;
var lcHasSuggestions = false;
var lcServerFlagged = false;
var lcInitialState = null;
var lcRowSeq = 0;
var lcAdd = null;
var lcListCache = {};
var lcReverseCache = {};
var lcMap = null;
var lcMarkers = {};
var lcDraftMarker = null;
var lcCountryLevelId = '2';

var LC_PIN = {
    radius: 6.5,
    color: '#fff',
    weight: 2,
    fillColor: '#0277A2',
    fillOpacity: 1
};
var LC_PIN_HOVER = {
    radius: 9,
    fillColor: '#015C7D'
};

function lcInit() {
  $lcRoot = $('[data-lc-root]');
  if(!$lcRoot.exists()) {
    return;
  }
  lcText = lcReadText($('#lc-i18n')[0]);
  lcEditable = $lcRoot.attr('data-editable') === 'true';
  lcScopeEditable = $lcRoot.attr('data-scope-editable') === 'true';
  lcHasSuggestions = $lcRoot.attr('data-has-suggestions') === 'true';

  // The save comes back with "list-project.locationsData: ..." among the action messages when the
  // validator found the list missing; the inline alert takes over from there.
  $('li#message').each(function() {
    if($.trim($(this).text()).indexOf('list-project.locationsData') === 0) {
      lcServerFlagged = true;
    }
  });

  // The design's save bar carries only the save state
  $('.lc-saveBar #lastUpdateMessage').remove();
  // Read-only: the section buttons render nothing, so the bar would be an empty box
  if(!lcEditable && !$.trim($lcRoot.find('.lc-saveBar').text())) {
    $lcRoot.find('.lc-saveBar').prop('hidden', true);
  }

  $lcRoot.find('[data-lc-row]').each(function() {
    lcPrepareRow($(this));
  });
  lcDisableTakenRegions();
  lcRenderSuggestions();
  lcSortGroups();
  lcReindex();
  lcAttachEvents();
  lcRefresh();

  lcInitialState = lcSnapshot();
  lcRefreshSaveState();

  lcWatchMap();
  lcSetFileUploads();
}

/* ------------------------------------------------------------------------------------------------
 * Text
 * --------------------------------------------------------------------------------------------- */

function lcReadText(el) {
  var out = {};
  if(!el) {
    return out;
  }
  $.each(el.attributes, function(i,attr) {
    if(attr.name.indexOf('data-') === 0) {
      var key = attr.name.substring(5).replace(/-([a-z])/g, function(m,c) {
        return c.toUpperCase();
      });
      out[key] = attr.value;
    }
  });
  return out;
}

function lcT(key,values) {
  var template = lcText[key];
  if(template === undefined || template === null) {
    return '';
  }
  return String(template).replace(/\{(\d+)\}/g, function(match,i) {
    return(values && values[i] !== undefined) ? values[i] : match;
  });
}

function lcFmt(value) {
  return Number(value).toFixed(4);
}

/* ------------------------------------------------------------------------------------------------
 * Events
 * --------------------------------------------------------------------------------------------- */

function lcAttachEvents() {
  // Yes / No questions (customForm.yesNoInputDeliverable): the labels drive real radios
  $lcRoot.on('change', '[data-lc-question] .radio-input', function() {
    lcSetAnswer($(this).closest('[data-lc-question]'), this.value);
    lcRefresh();
  });

  // Regions
  $lcRoot.on('change', '[data-lc-region-add]', function() {
    var $option = $(this).find('option:selected');
    if(this.value) {
      lcAddRegion(this.value, $option.text(), $option.attr('data-scope') === 'true');
    }
    this.value = '';
  });
  $lcRoot.on('click', '[data-lc-region-remove]', function() {
    lcRemoveRegion($(this).closest('[data-lc-region]'));
  });

  // Funding-source suggestions
  $lcRoot.on('change', '[data-lc-fs-check]', function() {
    var $item = $(this).closest('[data-lc-fs]');
    $item.toggleClass('is-on', this.checked);
    $item.find('[data-lc-fs-value]').val(this.checked ? 'true' : 'false');
    lcRenderSuggestions();
    lcSortGroups();
    lcReindex();
    lcRefresh();
    lcSyncMarkers();
  });

  // Locations list
  $lcRoot.on('click', '[data-lc-row-remove]', function() {
    lcRemoveRow($(this).closest('[data-lc-row]'));
  });
  $lcRoot.on('click', '[data-lc-row-focus]', function() {
    lcFocusRow($(this).closest('[data-lc-row]'));
  });
  $lcRoot.on('mouseenter', '[data-lc-row]', function() {
    lcHover($(this).attr('data-uid'), true);
  });
  $lcRoot.on('mouseleave', '[data-lc-row]', function() {
    lcHover($(this).attr('data-uid'), false);
  });

  // Add panel
  $lcRoot.on('click', '[data-lc-add-start]', lcStartAdd);
  $lcRoot.on('click', '[data-lc-add-cancel]', lcCancelAdd);
  $lcRoot.on('change', '[data-lc-level]', lcOnLevel);
  $lcRoot.on('click', '[data-lc-pick]', function() {
    lcTogglePick($(this));
  });
  $lcRoot.on('input', '[data-lc-add-name], [data-lc-add-lat], [data-lc-add-lon]', lcRefreshAdd);
  $lcRoot.on('click', '[data-lc-add-confirm]', lcConfirmAdd);
  $lcRoot.on('keydown', '[data-lc-add]', function(e) {
    if(e.key === 'Escape') {
      lcCancelAdd();
    } else if(e.key === 'Enter' && $(e.target).is('input')) {
      // Enter in a field would submit the whole section form
      e.preventDefault();
      $lcRoot.find('[data-lc-add-confirm]:enabled').trigger('click');
    }
  });
}

/* ------------------------------------------------------------------------------------------------
 * Geographic scope
 * --------------------------------------------------------------------------------------------- */

function lcAnswer(name) {
  return $lcRoot.find('[data-lc-question="' + name + '"] .radio-input:checked').val() || '';
}

/** Checks the radio of an answer and marks its label, as the shared switch is drawn. */
function lcSetAnswer($q,value) {
  $q.find('.radio-input').each(function() {
    this.checked = this.value === value;
  });
  $q.find('.yes-button-label').toggleClass('radio-checked', value === 'true');
  $q.find('.no-button-label').toggleClass('radio-checked', value === 'false');
}

function lcDisableTakenRegions() {
  var taken = {};
  $lcRoot.find('[data-lc-region]').each(function() {
    taken[$(this).attr('data-key')] = true;
  });
  $lcRoot.find('[data-lc-region-add] option').each(function() {
    if(this.value) {
      this.disabled = !!taken[this.value];
    }
  });
}

function lcAddRegion(value,name,isScope) {
  if($lcRoot.find('[data-lc-region][data-key="' + value + '"]').exists()) {
    return;
  }
  var id = value.split('-')[0];
  var $chip = $('#lc-templates [data-lc-region-template]').clone().removeAttr('data-lc-region-template');
  $chip.attr('data-lc-region', '').attr('data-key', value);
  $chip.find('.lc-chip__name').text($.trim(name));
  // saveRegions() reads locElementId for UN regions and locElementTypeId for the global unit's own
  $chip.find('.rId').attr('data-lc-field', isScope ? '.locElementTypeId' : '.locElementId').val(id);
  $chip.find('.regionScope').val(isScope ? 'true' : 'false');
  $chip.find('[data-lc-region-remove]').attr('aria-label', lcT('remove', [
    $.trim(name)
  ]));
  $chip.insertBefore($lcRoot.find('[data-lc-regions-empty]'));
  lcDisableTakenRegions();
  lcReindex();
  lcRefresh();
}

function lcRemoveRegion($chip) {
  $chip.remove();
  lcDisableTakenRegions();
  lcReindex();
  lcRefresh();
  $lcRoot.find('[data-lc-region-add]').trigger('focus');
}

/* ------------------------------------------------------------------------------------------------
 * Funding-source suggestions
 * --------------------------------------------------------------------------------------------- */

/**
 * Shows the regional suggestions only while the regional answer is Yes, puts the checked
 * suggested regions in the regions picker and the checked suggested countries in the Country
 * group, both locked: they leave by being unchecked, never by a remove button.
 */
function lcRenderSuggestions() {
  var regional = lcAnswer('project.projectInfo.locationRegional') === 'true';

  $lcRoot.find('[data-lc-fs]').each(function() {
    var $item = $(this);
    var selected = $item.find('[data-lc-fs-check]').is(':checked');
    var hide = (!lcEditable && !selected) || ($item.attr('data-fs-kind') === 'region' && !regional);
    $item.prop('hidden', hide);
  });
  $lcRoot.find('[data-lc-fs-none]').prop('hidden', $lcRoot.find('[data-lc-fs]:not([hidden])').exists());

  // Suggested regions, as fixed chips
  $lcRoot.find('[data-lc-fs-chip]').remove();
  $lcRoot.find('[data-lc-fs][data-fs-kind="region"]').each(function() {
    if(!$(this).find('[data-lc-fs-check]').is(':checked')) {
      return;
    }
    var $chip = $('<span class="lc-chip lc-chip--fs" data-lc-fs-chip></span>');
    $chip.append($('<span class="lc-chip__name"></span>').text($(this).attr('data-name')));
    $chip.append($('<span class="lc-chip__badge"></span>').text(lcT('fsBadge')));
    $chip.insertBefore($lcRoot.find('[data-lc-regions-empty]'));
  });

  // Suggested countries, as locked rows of the Country group
  var wanted = {};
  $lcRoot.find('[data-lc-fs][data-fs-kind="country"]').each(function() {
    if($(this).find('[data-lc-fs-check]').is(':checked')) {
      wanted[$(this).attr('data-key')] = $(this);
    }
  });
  $lcRoot.find('[data-lc-fs-row]').each(function() {
    if(!wanted[$(this).attr('data-fs-key')]) {
      lcDetachMarker($(this).attr('data-uid'));
      $(this).remove();
    } else {
      delete wanted[$(this).attr('data-fs-key')];
    }
  });
  $.each(wanted, function(key,$item) {
    var $group = lcEnsureGroup(lcCountryLevelId, lcCountryName(), true);
    var $row = lcNewRow();
    $row.attr('data-lc-fs-row', '').attr('data-fs-key', key).addClass('is-fs');
    $row.attr('data-name', $item.attr('data-name')).attr('data-iso', $item.attr('data-iso'));
    $row.find('[data-lc-row-name]').text($item.attr('data-name'));
    // Not posted as a location: the suggestion's own .selected flag carries it
    $row.find('input').remove();
    $row.find('[data-lc-row-remove]').remove();
    $row.append($('<span class="lc-badge"></span>').attr('title', lcT('fsLocked')).append(
        '<svg width="10" height="10" viewBox="0 0 16 16" fill="none" aria-hidden="true">'
            + '<rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.6"></rect>'
            + '<path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.6"></path></svg>').append(
        document.createTextNode(lcT('fsBadge'))));
    $group.find('[data-lc-rows]').append($row);
    lcPrepareRow($row);
  });
  // A Country group left with nothing in it goes away
  $lcRoot.find('[data-lc-group]').each(function() {
    if(!$(this).find('[data-lc-row]').exists() && $(this).attr('data-lc-new') === 'true') {
      $(this).remove();
    }
  });
}

function lcCountryName() {
  var $option = $lcRoot.find('[data-lc-level] option[value="' + lcCountryLevelId + '"]');
  return $option.exists() ? $option.attr('data-name') : 'Country';
}

/* ------------------------------------------------------------------------------------------------
 * Locations list
 * --------------------------------------------------------------------------------------------- */

function lcGroups() {
  return $lcRoot.find('[data-lc-groups] [data-lc-group]');
}

function lcRows() {
  return $lcRoot.find('[data-lc-groups] [data-lc-row]');
}

function lcIsCountryGroup($group) {
  return $group.attr('data-level-id') === lcCountryLevelId;
}

/** Fills a row's icon and second line from what it holds. */
function lcPrepareRow($row) {
  if(!$row.attr('data-uid')) {
    $row.attr('data-uid', 'lc' + (++lcRowSeq));
  }
  var $group = $row.closest('[data-lc-group]');
  var name = $row.attr('data-name') || '';
  // Every row with a stored point gets a marker, countries included (their stored centre), as the
  // previous map did; only sites read their coordinates in the list.
  var hasPoint = $row.attr('data-lat') !== undefined;
  var hasCoords = hasPoint && !lcIsCountryGroup($group);
  $row.toggleClass('has-point', hasPoint);
  var meta;
  if($row.is('[data-lc-fs-row]')) {
    meta = lcT('suggested');
  } else if(hasCoords) {
    meta = lcFmt($row.attr('data-lat')) + ', ' + lcFmt($row.attr('data-lon'));
  } else if(lcIsCountryGroup($group)) {
    meta = lcT('wholeCountry');
  } else {
    meta = lcT('fromList');
  }
  $row.toggleClass('has-coords', hasCoords);
  $row.find('[data-lc-row-icon]').text(hasCoords ? '' : name.substring(0, 2).toUpperCase());
  $row.find('[data-lc-row-meta]').text(meta);
}

function lcNewRow() {
  return $('#lc-templates [data-lc-row-template]').clone().removeAttr('data-lc-row-template');
}

/** The group of a level, created (empty) at the end when the list does not have it yet. */
function lcEnsureGroup(levelId,levelName,isList) {
  var $group = lcGroups().filter('[data-level-id="' + levelId + '"]');
  if($group.exists()) {
    return $group.first();
  }
  $group = $('#lc-templates [data-lc-group-template]').clone().removeAttr('data-lc-group-template');
  $group.attr({
      'data-level-id': levelId,
      'data-level-name': levelName,
      'data-is-list': isList ? 'true' : 'false',
      'data-lc-new': 'true'
  });
  $group.find('[data-lc-group-name]').text(levelName);
  $group.find('.locationLevelId').val(levelId);
  $group.find('.locationLevelName').val(levelName);
  $group.find('.isList').val(isList ? 'true' : 'false');
  $group.insertBefore($lcRoot.find('[data-lc-empty]'));
  lcSortGroups();
  return $group;
}

/** Orders the groups as crp_loc_element_types lists the levels. */
function lcSortGroups() {
  var order = {};
  $lcRoot.find('[data-lc-level] option').each(function(i) {
    order[this.value] = i;
  });
  if($.isEmptyObject(order)) {
    return;
  }
  var $groups = lcGroups().get();
  $groups.sort(function(a,b) {
    var oa = order[$(a).attr('data-level-id')];
    var ob = order[$(b).attr('data-level-id')];
    return(oa === undefined ? 999 : oa) - (ob === undefined ? 999 : ob);
  });
  var $anchor = $lcRoot.find('[data-lc-empty]');
  $.each($groups, function(i,g) {
    $(g).insertBefore($anchor);
  });
}

/** Renames every posted input with contiguous indexes (see the header of this file). */
function lcReindex() {
  var levelIndex = 0;
  lcGroups().each(function() {
    var $group = $(this);
    var $rows = $group.find('[data-lc-row]').not('[data-lc-fs-row]');
    var posted = $rows.exists();
    var prefix = 'project.locationsData[' + levelIndex + ']';
    $group.find('.lc-group__inputs input').each(function() {
      if(posted) {
        this.name = prefix + $(this).attr('data-lc-field');
      } else {
        this.removeAttribute('name');
      }
    });
    $rows.each(function(j) {
      $(this).find('input[data-lc-field]').each(function() {
        this.name = prefix + '.locElements[' + j + ']' + $(this).attr('data-lc-field');
      });
    });
    if(posted) {
      levelIndex++;
    }
  });

  $lcRoot.find('#regionList [data-lc-region]').each(function(k) {
    $(this).find('input[data-lc-field]').each(function() {
      this.name = 'project.projectRegions[' + k + ']' + $(this).attr('data-lc-field');
    });
  });
  $(document).trigger('updateComponent');
}

function lcRemoveRow($row) {
  var $group = $row.closest('[data-lc-group]');

  lcDetachMarker($row.attr('data-uid'));
  $row.detach();
  if(!$group.find('[data-lc-row]').exists()) {
    $group.detach();
  }
  lcReindex();
  lcRefresh();
}

/* ------------------------------------------------------------------------------------------------
 * Summary, validation and empty states
 * --------------------------------------------------------------------------------------------- */

function lcRefresh() {
  var global = lcAnswer('project.projectInfo.locationGlobal');
  var regional = lcAnswer('project.projectInfo.locationRegional');
  var $rows = lcRows();
  var total = $rows.length;

  // Regions picker
  $lcRoot.find('[data-lc-regions]').prop('hidden', regional !== 'true');
  lcRenderSuggestionsVisibility(regional === 'true');
  $lcRoot.find('[data-lc-regions-empty]').prop('hidden',
      $lcRoot.find('#regionList [data-lc-region], #regionList [data-lc-fs-chip]').exists());

  // Groups: count badges, and a group with no rows left is not shown
  lcGroups().each(function() {
    var n = $(this).find('[data-lc-row]').length;
    $(this).find('[data-lc-group-count]').text(n);
    $(this).prop('hidden', n === 0);
  });

  // Count line: "6 locations · 1 country · 5 provinces"
  var parts = [];
  lcGroups().each(function() {
    var n = $(this).find('[data-lc-row]').length;
    if(n) {
      parts.push(n + ' ' + lcPluralLevel($(this).attr('data-level-name'), n));
    }
  });
  var countLine = !total ? lcT('countNone') : (total === 1 ? lcT('countOne') : lcT('countOther', [
    total
  ]));
  if(parts.length) {
    countLine += ' · ' + parts.join(' · ');
  }
  $lcRoot.find('[data-lc-count]').text(countLine);

  // Section summary under the title
  var answer = function(v) {
    return v === 'true' ? lcT('yes') : (v === 'false' ? lcT('no') : lcT('none'));
  };
  var countries = lcGroups().filter('[data-level-id="' + lcCountryLevelId + '"]').find('[data-lc-row]').length;
  var summary = [
      lcT('summaryGlobal', [
        answer(global)
      ]), lcT('summaryRegional', [
        answer(regional)
      ])
  ];
  if(countries) {
    summary.push(countries === 1 ? lcT('countriesOne') : lcT('countriesOther', [
      countries
    ]));
  }
  $lcRoot.find('[data-lc-summary]').text(summary.join(' · '));

  // ProjectLocationValidator: required only when global = No, the list is empty and there are no
  // funding-source suggestions. An unanswered global question requires nothing.
  var missing = global === 'false' && total === 0 && !lcHasSuggestions;
  var flagged = lcServerFlagged && missing && lcEditable;
  $lcRoot.find('[data-lc-error]').prop('hidden', !flagged);
  $lcRoot.find('[data-lc-locations]').toggleClass('is-invalid', flagged);
  $lcRoot.find('[data-lc-global-note]').prop('hidden', !(lcEditable && global === 'true' && total === 0));

  // Empty states
  var $title = $lcRoot.find('[data-lc-empty-title]');
  $title.text(global === 'true' ? $title.attr('data-text-global') : $title.attr('data-text-default'));
  $lcRoot.find('[data-lc-empty]').prop('hidden', !lcEditable || total > 0 || !!lcAdd);
  $lcRoot.find('[data-lc-empty-ro]').prop('hidden', lcEditable || total > 0);

  lcRefreshMapCaption();
  lcRefreshSaveState();
}

function lcRenderSuggestionsVisibility(regional) {
  $lcRoot.find('[data-lc-fs][data-fs-kind="region"]').each(function() {
    var selected = $(this).find('[data-lc-fs-check]').is(':checked');
    $(this).prop('hidden', !regional || (!lcEditable && !selected));
  });
  $lcRoot.find('[data-lc-fs-chip]').prop('hidden', !regional);
  $lcRoot.find('[data-lc-fs-none]').prop('hidden', $lcRoot.find('[data-lc-fs]:not([hidden])').exists());
}

/** English plural of a location level name, for the count line ("2 provinces", "3 AICCRA sites"). */
function lcPluralLevel(level,n) {
  var word = level || '';
  if(word === 'Country') {
    return n === 1 ? 'country' : 'countries';
  }
  if(word !== word.toUpperCase()) {
    word = word.toLowerCase();
  }
  if(n === 1 || /s$/.test(word)) {
    return word;
  }
  return /[^aeiou]y$/.test(word) ? word.replace(/y$/, 'ies') : word + 's';
}

/* ------------------------------------------------------------------------------------------------
 * Change tracking and save lock
 * --------------------------------------------------------------------------------------------- */

/** What the section holds, keyed so that re-indexing does not read as a change. */
function lcSnapshot() {
  var rows = lcRows().not('[data-lc-fs-row]').map(function() {
    var $row = $(this);
    var level = $row.closest('[data-lc-group]').attr('data-level-id');
    var id = $row.find('.locElementId').val();
    return level + ':' + (id && id !== '-1' ? id : $row.attr('data-name') + '@' + $row.attr('data-lat') + ','
        + $row.attr('data-lon'));
  }).get();
  var regions = $lcRoot.find('[data-lc-region]').map(function() {
    return $(this).attr('data-key');
  }).get();
  var fs = $lcRoot.find('[data-lc-fs]').map(function() {
    return $(this).attr('data-key') + ':' + $(this).find('[data-lc-fs-value]').val();
  }).get();
  return {
      global: lcAnswer('project.projectInfo.locationGlobal'),
      regional: lcAnswer('project.projectInfo.locationRegional'),
      rows: rows,
      regions: regions,
      fs: fs
  };
}

function lcDiffCount(before,now) {
  var count = 0;
  var seen = {};
  $.each(before, function(i,k) {
    seen[k] = (seen[k] || 0) + 1;
  });
  $.each(now, function(i,k) {
    if(seen[k]) {
      seen[k]--;
    } else {
      count++;
    }
  });
  $.each(seen, function(k,left) {
    count += left;
  });
  return count;
}

function lcCountChanges() {
  if(!lcInitialState) {
    return 0;
  }
  var now = lcSnapshot();
  var count = 0;
  if(now.global !== lcInitialState.global) {
    count++;
  }
  if(now.regional !== lcInitialState.regional) {
    count++;
  }
  count += lcDiffCount(lcInitialState.rows, now.rows);
  count += lcDiffCount(lcInitialState.regions, now.regions);
  // Each toggled suggestion is one change
  count += lcDiffCount(lcInitialState.fs, now.fs) / 2;
  return count;
}

function lcRefreshSaveState() {
  if(!lcInitialState) {
    return;
  }
  var count = lcCountChanges();
  var $state = $lcRoot.find('[data-lc-save-state]');
  $state.toggleClass('is-dirty', count > 0);
  $state.find('[data-lc-save-text]').text(!count ? lcT('saveClean') : (count === 1 ? lcT('saveOne') : lcT(
      'saveOther', [
        count
      ])));

  // While a location is being added, Save waits for "Add location" or Cancel
  var drafting = !!lcAdd;
  $lcRoot.find('.lc-saveBar button[name="save"]').prop('disabled', drafting).toggleClass('is-locked', drafting).attr(
      'title', drafting ? lcT('saveLocked') : null);
}

/* ------------------------------------------------------------------------------------------------
 * Add a location
 * --------------------------------------------------------------------------------------------- */

function lcStartAdd() {
  if(lcAdd) {
    return;
  }
  lcAdd = {
      levelId: '',
      levelName: '',
      isList: false,
      picks: {},
      busy: false
  };
  var $panel = $lcRoot.find('[data-lc-add]');
  $panel.find('[data-lc-level]').val('');
  $panel.find('[data-lc-add-name], [data-lc-add-lat], [data-lc-add-lon]').val('');
  $panel.prop('hidden', false);
  $lcRoot.find('[data-lc-add-start]').prop('disabled', true);
  lcOnLevel();
  lcRefresh();
  $panel.find('[data-lc-level]').trigger('focus');
}

function lcCancelAdd() {
  if(!lcAdd) {
    return;
  }
  lcAdd = null;
  $lcRoot.find('[data-lc-add]').prop('hidden', true);
  $lcRoot.find('[data-lc-add-start]').prop('disabled', false);
  lcSetDraft(null);
  lcRefresh();
  $lcRoot.find('.lc-card__bar [data-lc-add-start]').trigger('focus');
}

function lcOnLevel() {
  if(!lcAdd) {
    return;
  }
  var $panel = $lcRoot.find('[data-lc-add]');
  var $option = $panel.find('[data-lc-level] option:selected');
  lcAdd.levelId = $option.val() || '';
  lcAdd.levelName = $option.attr('data-name') || '';
  lcAdd.isList = $option.attr('data-list') === 'true';
  lcAdd.picks = {};

  var none = !lcAdd.levelId;
  $panel.find('[data-lc-add-none]').prop('hidden', !none);
  $panel.find('[data-lc-add-list]').prop('hidden', none || !lcAdd.isList);
  $panel.find('[data-lc-add-coords]').prop('hidden', none || lcAdd.isList);
  $panel.find('[data-lc-level-hint]').text(none ? lcT('levelHint') : lcT(lcAdd.isList ? 'levelHintList'
      : 'levelHintCoord', [
    lcAdd.levelName
  ]));

  if(!none && lcAdd.isList) {
    lcLoadListOptions(lcAdd.levelId);
  }
  lcRefreshAdd();
}

/** The options of a list level, from searchCountryListPL.do, cached per level. */
function lcLoadListOptions(levelId) {
  var $picks = $lcRoot.find('[data-lc-picks]');
  if(lcListCache[levelId]) {
    lcRenderPicks(lcListCache[levelId]);
    return;
  }
  $picks.empty().append($('<span class="lc-help"></span>').text(lcT('listLoading')));
  $.ajax({
      url: baseURL + '/searchCountryListPL.do',
      type: 'GET',
      dataType: 'json',
      data: {
          parentId: levelId,
          phaseID: phaseID
      }
  }).done(function(m) {
    lcListCache[levelId] = (m && m.locElements) || [];
    if(lcAdd && lcAdd.levelId === levelId) {
      lcRenderPicks(lcListCache[levelId]);
    }
  }).fail(function() {
    $picks.empty().append($('<span class="lc-help is-error"></span>').text(lcT('listEmpty')));
  });
}

function lcRenderPicks(options) {
  var $picks = $lcRoot.find('[data-lc-picks]').empty();
  if(!options.length) {
    $picks.append($('<span class="lc-help"></span>').text(lcT('listEmpty')));
    return;
  }
  var taken = lcTakenIds(lcAdd.levelId);
  $.each(options, function(i,o) {
    var id = String(o.id);
    var added = !!taken[id];
    var $btn = $('<button type="button" class="lc-pick" data-lc-pick></button>').text(o.name).attr({
        'data-id': id,
        'data-iso': o.isoAlpha2 || '',
        'data-name': o.name,
        'aria-pressed': 'false'
    });
    if(added) {
      $btn.prop('disabled', true).addClass('is-added').attr('title', lcT('alreadyAdded'));
    }
    $picks.append($btn);
  });
}

/** Element ids a level already holds, its funding-source rows included. */
function lcTakenIds(levelId) {
  var taken = {};
  lcGroups().filter('[data-level-id="' + levelId + '"]').find('[data-lc-row]').each(function() {
    var id = $(this).find('.locElementId').val();
    if(id) {
      taken[id] = true;
    }
    var fsKey = $(this).attr('data-fs-key');
    if(fsKey && fsKey.charAt(0) === 'e') {
      taken[fsKey.substring(1)] = true;
    }
  });
  return taken;
}

function lcTogglePick($btn) {
  if(!lcAdd || $btn.prop('disabled')) {
    return;
  }
  var id = $btn.attr('data-id');
  if(lcAdd.picks[id]) {
    delete lcAdd.picks[id];
  } else {
    lcAdd.picks[id] = {
        id: id,
        iso: $btn.attr('data-iso'),
        name: $btn.attr('data-name')
    };
  }
  $btn.toggleClass('is-on', !!lcAdd.picks[id]).attr('aria-pressed', lcAdd.picks[id] ? 'true' : 'false');
  lcRefreshAdd();
}

function lcCoordOk(value,limit) {
  var v = $.trim(value);
  var n = Number(v);
  return v !== '' && isFinite(n) && Math.abs(n) <= limit;
}

function lcAddValues() {
  var $panel = $lcRoot.find('[data-lc-add]');
  return {
      name: $.trim($panel.find('[data-lc-add-name]').val()),
      lat: $.trim($panel.find('[data-lc-add-lat]').val()),
      lon: $.trim($panel.find('[data-lc-add-lon]').val())
  };
}

function lcRefreshAdd() {
  if(!lcAdd) {
    return;
  }
  var $panel = $lcRoot.find('[data-lc-add]');
  var v = lcAddValues();
  var latOk = lcCoordOk(v.lat, 90);
  var lonOk = lcCoordOk(v.lon, 180);
  var badLat = v.lat !== '' && !latOk;
  var badLon = v.lon !== '' && !lonOk;
  var picks = Object.keys(lcAdd.picks).length;
  var valid;
  var summary = '';

  if(lcAdd.isList) {
    valid = picks > 0;
    summary = picks ? lcT('selected', [
      picks
    ]) : '';
  } else {
    valid = !!lcAdd.levelId && v.name !== '' && latOk && lonOk;
    summary = valid ? v.name + ' · ' + lcFmt(v.lat) + ', ' + lcFmt(v.lon) : '';
  }

  $panel.find('[data-lc-add-lat]').toggleClass('is-invalid', badLat).attr('aria-invalid', badLat ? 'true' : 'false');
  $panel.find('[data-lc-add-lon]').toggleClass('is-invalid', badLon).attr('aria-invalid', badLon ? 'true' : 'false');
  $panel.find('[data-lc-coord-note]').toggleClass('is-error', badLat || badLon);
  $panel.find('[data-lc-coord-note-text]').text(badLat || badLon ? lcT('coordError') : lcT('coordHint'));
  $panel.find('[data-lc-add-summary]').text(summary);
  $panel.find('[data-lc-add-confirm]').prop('disabled', !valid || lcAdd.busy).text(lcAdd.isList && picks > 1 ? lcT(
      'confirmMany', [
        picks
      ]) : lcT('confirmOne'));

  var placing = !!lcAdd.levelId && !lcAdd.isList;
  $lcRoot.find('[data-lc-map]').toggleClass('is-placing', placing);
  lcSetDraft(placing && latOk && lonOk ? [
      Number(v.lat), Number(v.lon)
  ] : null);
  lcRefreshMapCaption();
}

function lcConfirmAdd() {
  if(!lcAdd || lcAdd.busy) {
    return;
  }
  var add = lcAdd;
  add.busy = true;
  lcRefreshAdd();

  var done = function() {
    add.busy = false;
    lcCancelAdd();
    lcSortGroups();
    lcReindex();
    lcRefresh();
    lcSyncMarkers(true);
  };

  if(add.isList) {
    var picks = $.map(add.picks, function(p) {
      return p;
    });
    // The stored coordinates of each picked element, as the previous page posted them
    var requests = $.map(picks, function(p) {
      return $.ajax({
          url: baseURL + '/geopositionByElement.do',
          type: 'GET',
          dataType: 'json',
          data: {
              locElementID: p.id,
              phaseID: phaseID
          }
      }).then(function(m) {
        if(m && m.geopositions && m.geopositions.length) {
          p.lat = m.geopositions[0].latitude;
          p.lon = m.geopositions[0].longitude;
        }
      }, function() {
        return $.Deferred().resolve();
      });
    });
    $.when.apply($, requests).always(function() {
      var $group = lcEnsureGroup(add.levelId, add.levelName, true);
      $.each(picks, function(i,p) {
        lcAppendRow($group, {
            id: p.id,
            name: p.name,
            iso: add.levelId === lcCountryLevelId ? p.iso : '',
            lat: p.lat,
            lon: p.lon
        });
      });
      done();
    });
  } else {
    var v = lcAddValues();
    lcReverseCountry(v.lat, v.lon, function(iso) {
      var $group = lcEnsureGroup(add.levelId, add.levelName, false);
      lcAppendRow($group, {
          id: '-1',
          name: v.name,
          iso: iso,
          lat: v.lat,
          lon: v.lon
      });
      done();
    });
  }
}

function lcAppendRow($group,data) {
  var $row = lcNewRow();
  $row.attr('data-name', data.name).attr('data-key', data.id);
  if(data.lat !== undefined && data.lat !== null && data.lat !== '' && Number(data.lat) !== 0) {
    $row.attr('data-lat', data.lat).attr('data-lon', data.lon);
    $row.find('.geoLatitude').val(data.lat);
    $row.find('.geoLongitude').val(data.lon);
  }
  $row.find('[data-lc-row-name]').text(data.name);
  $row.find('.locElementId').val(data.id);
  $row.find('.locElementName').val(data.name);
  $row.find('.locElementCountry').val(data.iso || '');
  $row.find('[data-lc-row-remove]').attr('aria-label', lcT('remove', [
    data.name
  ]));
  $group.find('[data-lc-rows]').append($row);
  lcPrepareRow($row);
  return $row;
}

/** The ISO code of the country a picked point sits in (Nominatim, once per point). */
function lcReverseCountry(lat,lon,callback) {
  var key = lcFmt(lat) + ',' + lcFmt(lon);
  if(lcReverseCache[key] !== undefined) {
    callback(lcReverseCache[key]);
    return;
  }
  $.ajax({
      url: 'https://nominatim.openstreetmap.org/reverse',
      dataType: 'json',
      data: {
          format: 'jsonv2',
          lat: lat,
          lon: lon,
          zoom: 3,
          addressdetails: 1
      }
  }).done(function(data) {
    lcReverseCache[key] = (data && data.address && data.address.country_code) ? data.address.country_code
        .toUpperCase() : '';
  }).always(function() {
    callback(lcReverseCache[key] || '');
  });
}

/* ------------------------------------------------------------------------------------------------
 * Map (Leaflet + OpenStreetMap)
 * --------------------------------------------------------------------------------------------- */

function lcWatchMap() {
  var el = $lcRoot.find('[data-lc-map]')[0];
  if(!el || typeof L === 'undefined') {
    return;
  }
  if(!('IntersectionObserver' in window)) {
    lcInitMap(el);
    return;
  }
  var observer = new IntersectionObserver(function(entries) {
    if(entries[0].isIntersecting) {
      observer.disconnect();
      lcInitMap(el);
    }
  }, {
    rootMargin: '200px'
  });
  observer.observe(el);
}

function lcInitMap(el) {
  if(lcMap) {
    return;
  }
  lcMap = L.map(el, {
      scrollWheelZoom: false,
      worldCopyJump: true
  });
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(lcMap);
  lcMap.on('click', lcOnMapClick);
  lcSyncMarkers(true);
  if(lcAdd) {
    lcRefreshAdd();
  }
}

/** Adds and removes markers on the existing map so it matches the rows with coordinates. */
function lcSyncMarkers(fit) {
  if(!lcMap) {
    return;
  }
  var live = {};
  lcRows().filter('.has-point').each(function() {
    var $row = $(this);
    var uid = $row.attr('data-uid');
    live[uid] = true;
    if(lcMarkers[uid]) {
      return;
    }
    var marker = L.circleMarker([
        Number($row.attr('data-lat')), Number($row.attr('data-lon'))
    ], LC_PIN).addTo(lcMap);
    marker.bindTooltip($row.attr('data-name'), {
        direction: 'top',
        offset: [
            0, -8
        ],
        className: 'lc-mapTip'
    });
    marker.on('mouseover', function() {
      lcHover(uid, true);
    });
    marker.on('mouseout', function() {
      lcHover(uid, false);
    });
    marker.on('click', function(e) {
      L.DomEvent.stopPropagation(e);
      var $hit = lcRows().filter('[data-uid="' + uid + '"]');
      if($hit[0] && $hit[0].scrollIntoView) {
        $hit[0].scrollIntoView({
            block: 'nearest',
            behavior: 'smooth'
        });
      }
      lcFocusRow($hit);
    });
    lcMarkers[uid] = marker;
  });
  $.each(lcMarkers, function(uid) {
    if(!live[uid]) {
      lcDetachMarker(uid);
    }
  });
  if(fit) {
    lcFitMap();
  }
  lcRefreshMapCaption();
}

function lcDetachMarker(uid) {
  if(lcMarkers[uid]) {
    lcMarkers[uid].remove();
    delete lcMarkers[uid];
  }
}

/** Fits the sites; without any, the stored points of the countries; without those, Africa. */
function lcFitMap() {
  if(!lcMap) {
    return;
  }
  var points = $.map(lcMarkers, function(m) {
    return [
      m.getLatLng()
    ];
  });
  if(!points.length) {
    lcRows().each(function() {
      var lat = $(this).attr('data-lat');
      if(lat !== undefined) {
        points.push(L.latLng(Number(lat), Number($(this).attr('data-lon'))));
      }
    });
  }
  if(!points.length) {
    lcMap.setView([
        4, 20
    ], 3);
  } else if(points.length === 1) {
    lcMap.setView(points[0], 7);
  } else {
    lcMap.fitBounds(L.latLngBounds(points), {
        padding: [
            28, 28
        ],
        maxZoom: 9
    });
  }
}

/**
 * Shows a location on the map, as clicking it in the previous page's list did: a country fits the
 * map to the country's extent, a site centres on its point. The extent comes from Nominatim on this
 * explicit click only (cached per country); without it the map centres on the country's stored point.
 */
function lcFocusRow($row) {
  if(!$row || !$row.exists()) {
    return;
  }
  var el = $lcRoot.find('[data-lc-map]')[0];
  if(!lcMap && el && typeof L !== 'undefined') {
    lcInitMap(el);
  }
  if(!lcMap) {
    return;
  }
  lcRows().removeClass('is-focused');
  $row.addClass('is-focused');

  var uid = $row.attr('data-uid');
  var lat = $row.attr('data-lat');
  var point = lat !== undefined ? L.latLng(Number(lat), Number($row.attr('data-lon'))) : null;
  var isCountry = lcIsCountryGroup($row.closest('[data-lc-group]'));

  if(isCountry) {
    var iso = $row.find('.locElementCountry').val() || $row.attr('data-iso') || '';
    lcCountryBounds($row.attr('data-name'), iso, function(bounds) {
      if(bounds) {
        lcMap.flyToBounds(bounds, {
            padding: [
                16, 16
            ],
            duration: 0.6
        });
      } else if(point) {
        lcMap.flyTo(point, 6, {
          duration: 0.6
        });
      }
    });
  } else if(point) {
    lcMap.flyTo(point, Math.max(lcMap.getZoom(), 10), {
      duration: 0.6
    });
  }
  if(lcMarkers[uid]) {
    lcMarkers[uid].openTooltip();
  }
}

var lcCountryCache = {};

function lcCountryBounds(name,iso,callback) {
  var key = (iso || name || '').toUpperCase();
  if(!key) {
    callback(null);
    return;
  }
  if(lcCountryCache[key] !== undefined) {
    callback(lcCountryCache[key]);
    return;
  }
  var data = {
      format: 'jsonv2',
      q: name,
      featureType: 'country',
      limit: 1
  };
  if(iso) {
    data.countrycodes = iso.toLowerCase();
  }
  $.ajax({
      url: 'https://nominatim.openstreetmap.org/search',
      dataType: 'json',
      data: data
  }).done(function(results) {
    var box = results && results[0] && results[0].boundingbox;
    // boundingbox is [south, north, west, east]
    lcCountryCache[key] = box ? L.latLngBounds([
        Number(box[0]), Number(box[2])
    ], [
        Number(box[1]), Number(box[3])
    ]) : null;
  }).always(function() {
    callback(lcCountryCache[key] || null);
  });
}

function lcHover(uid,on) {
  lcRows().filter('[data-uid="' + uid + '"]').toggleClass('is-hover', on);
  var marker = lcMarkers[uid];
  if(marker) {
    marker.setStyle(on ? LC_PIN_HOVER : LC_PIN);
    if(on) {
      marker.bringToFront();
      marker.openTooltip();
    } else {
      marker.closeTooltip();
    }
  }
}

function lcOnMapClick(e) {
  if(!lcAdd || !lcAdd.levelId || lcAdd.isList) {
    return;
  }
  var $panel = $lcRoot.find('[data-lc-add]');
  var latlng = e.latlng.wrap();
  $panel.find('[data-lc-add-lat]').val(latlng.lat.toFixed(4));
  $panel.find('[data-lc-add-lon]').val(latlng.lng.toFixed(4));
  lcRefreshAdd();
}

function lcSetDraft(latlng) {
  if(!lcMap) {
    return;
  }
  if(!latlng) {
    if(lcDraftMarker) {
      lcDraftMarker.remove();
      lcDraftMarker = null;
    }
    return;
  }
  if(!lcDraftMarker) {
    lcDraftMarker = L.circleMarker(latlng, {
        radius: 10,
        color: '#0277A2',
        weight: 2,
        dashArray: '3 3',
        fillColor: '#0277A2',
        fillOpacity: 0.18,
        interactive: false
    }).addTo(lcMap);
  } else {
    lcDraftMarker.setLatLng(latlng);
  }
}

function lcRefreshMapCaption() {
  var sites = lcRows().filter('.has-point').length;
  var placing = !!(lcAdd && lcAdd.levelId && !lcAdd.isList);
  var caption = placing ? lcT('captionPlacing') : (!sites ? lcT('captionNone') : (sites === 1 ? lcT('captionOne')
      : lcT('captionOther', [
        sites
      ])));
  $lcRoot.find('[data-lc-map-caption]').text(caption);
}

/* ------------------------------------------------------------------------------------------------
 * CSV of AR4D activities (crp_location_csv_activities), unchanged from the previous page
 * --------------------------------------------------------------------------------------------- */

function lcSetFileUploads() {
  var containerClass = '.fileUploadContainer';
  var $uploadBlock = $lcRoot.find(containerClass);
  if(!$uploadBlock.exists() || !$.fn.fileupload) {
    return;
  }
  var $fileUpload = $uploadBlock.find('.upload');

  $fileUpload.fileupload({
      dataType: 'json',
      start: function(e) {
        $(e.target).parents(containerClass).addClass('blockLoading');
      },
      stop: function(e) {
        $(e.target).parents(containerClass).removeClass('blockLoading');
      },
      done: function(e,data) {
        var r = data.result;
        if(r.saved) {
          var $ub = $(e.target).parents(containerClass);
          $ub.find('.textMessage .contentResult').html(r.fileFileName);
          $ub.find('.textMessage').show();
          $ub.find('.fileUpload').hide();
          $ub.find('input.fileID').val(r.fileID);
          $ub.find('.fileUploaded a').attr('href', r.path + '/' + r.fileFileName);
        }
      },
      fail: function(e) {
        $(e.target).parents(containerClass).animateCss('shake');
      },
      progressall: function(e,data) {
        var $ub = $(e.target).parents(containerClass);
        var progress = parseInt(data.loaded / data.total * 100, 10);
        $ub.find('.progress').fadeIn(100);
        $ub.find('.progress .progress-bar').width(progress + '%');
        if(progress == 100) {
          $ub.find('.progress').fadeOut(1000, function() {
            $ub.find('.progress .progress-bar').width(0);
          });
        }
      }
  });

  $uploadBlock.find('.removeIcon').on('click', function() {
    var $ub = $(this).parents(containerClass);
    $ub.find('.textMessage .contentResult').html('');
    $ub.find('.textMessage').hide();
    $ub.find('.fileUpload').show();
    $ub.find('input.fileID').val('');
    $ub.find('.fileUploaded a').attr('href', '');
  });

  // Show the CSV box with its Yes / No answer
  $lcRoot.on('click', '.isCSV .button-label', function() {
    $lcRoot.find('.csvActivitiesBox').toggle($(this).hasClass('yes-button-label'));
  });
}
