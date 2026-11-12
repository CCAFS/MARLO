// Limits for textarea input

var $statuses, $statusDescription;
var flagshipsIds;
var liaisonInstitutionsPrograms, liaisonUserSelected;

$(document).ready(function() {

  $statuses = $('.description_project_status');
  $statusDescription = $('#statusDescription');
  $endDate = $('.endDate');
  var implementationStatus = $statuses.find('option[value="2"]').text();

  liaisonUserSelected = $('#liaisonUserSelected').text();
  liaisonInstitutionsPrograms = jQuery.parseJSON($('#liaisonInstitutionsPrograms').text());

  datePickerConfig({
      "startDate": ".startDate",
      "endDate": ".endDate",
      defaultMinDateValue: $("#minDateValue").val(),
      defaultMaxDateValue: $("#maxDateValue").val()
  });

  $('.previousID').integerInput();

  setDisabledCheckedBoxes();

  addSelect2();

  $('form .CoASelect').select2({
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });

  /**
   * Liaison institution selection
   */

  $('.liaisonInstitutionSelect').on('change', function() {
    var liasonInstitutionID = $(this).val();

    if(liasonInstitutionID == -1) {
      return
    }

    var crpProgramId = liaisonInstitutionsPrograms[liasonInstitutionID];
    console.log('liasonInstitutionID', liasonInstitutionID);
    console.log('crpProgramId', crpProgramId);
    if(crpProgramId != -1) {
      $('input[value="' + crpProgramId + '"]').prop("checked", true);
      $('#projectFlagshipsBlock input').trigger('change');
    }

    // Getting users
    $.ajax({
        url: baseURL + '/liasonUsersByInstitutionsAction.do',
        method: 'POST',
        data: {
          liasonInstitutionID: liasonInstitutionID,
          phaseID: phaseID
        },
        beforeSend: function() {
          $('.loading.liaisonUsersBlock').fadeIn();
          $('.liaisonUserSelect').empty();
          $('.liaisonUserSelect').addOption(-1, 'Select an option');
        },
        success: function(data) {
          $.each(data.liasonsUsers, function(i,e) {
            $('.liaisonUserSelect').addOption(e.id, escapeHtml(e.description));
            if(!e.active) {
              $('.liaisonUserSelect option[value="' + e.id + '"]').attr('disabled', 'disabled');
            }
          });

          // Set current liaison user
          if($('.liaisonUserSelect option[value="' + liaisonUserSelected + '"]').exists()) {
            $('.liaisonUserSelect').val(liaisonUserSelected);
          } else {
            $('.liaisonUserSelect').val('-1');
          }
          $('.liaisonUserSelect').trigger('change.select2');

        },
        complete: function(){
          $('.loading.liaisonUsersBlock').fadeOut();
        }
    });
  });
  if(editable){
    $('.liaisonInstitutionSelect').trigger('change');
  }

  /**
   * Upload files functions
   */

  $('.fileUpload .remove').on('click', function(e) {
    var context = $(this).attr('id').split('-')[1];
    var $parent = $(this).parent().parent();
    var $inputFile = $('[id$=' + context + '-template]').clone(true).removeAttr("id");
    $parent.empty().append($inputFile);
    $inputFile.hide().fadeIn('slow');
    forceChange = true;
  });

  /**
   * Project Flagships
   */

  flagshipsIds = function() {
    var arr = [];
    $('#projectFlagshipsBlock input:checked, input.defaultChecked').each(function(i,e) {
      arr.push($(e).val());
    });
    return arr.join()
  }

  $('#projectFlagshipsBlock input').on('change', function() {
        
    var urlAction = "clusterByFPsAction";
    
    var $inputSelected = $(this);
    if($inputSelected.hasClass('getCenterOutcomes')){
      urlAction = "centerOutcomeByProgramAction";
      $coreSelect = $('select.elementType-centerOutcome')
    }
    
    var selectedPrograms = flagshipsIds();
    if(!selectedPrograms){
      return;
    }
    
    $.ajax({
        url: baseURL + '/'+ urlAction+'.do',
        data: {
          flagshipID: selectedPrograms,
          programID: selectedPrograms,
          phaseID: phaseID
        },
        beforeSend: function() {
          $coreSelect.parents('.panel').find('.listComponentLoading').fadeIn();
          $('.loading.clustersBlock').fadeIn();
          $coreSelect.empty();
          $coreSelect.addOption(-1, 'Select an option');
        },
        success: function(data) {
          var optionsArray = data.clusters;
          if($inputSelected.hasClass('getCenterOutcomes')){
            optionsArray = data.outcomes;
          }
          // console.log(data.clusters);
          $.each(optionsArray, function(i,e) {
            $coreSelect.addOption(e.id, e.description);
          });
        },
        complete: function(){
          $coreSelect.parents('.panel').find('.listComponentLoading').fadeOut();
          $('.loading.clustersBlock').fadeOut();
        }
    });
  });
  
  $('.additionalPrograms input[type="checkbox"], #projectWorking  input[type="checkbox"]').on('click', function(e) {
    
    var programID = ($(this).attr('id')).split("-")[1];
    var liaisonID = getKeyByValue(liaisonInstitutionsPrograms, programID);
    var selectedLiaisonID = $('select.liaisonInstitutionSelect').val();
    if(liaisonID == selectedLiaisonID){
      e.preventDefault();
      $(this).prop('checked', true);
      $(this).parent().animateCss('shake');
    }
  });

  // No regional programmatic focus and the regional programs exclude each other: ticking "no regional focus" clears
  // the programs, and ticking any program clears "no regional focus". Either choice stays available.
  $('#projectNoRegional').on('change', function() {
    if(this.checked) {
      $('input.rpInput:checked').prop('checked', false).trigger('change');
    }
  });
  $('input.rpInput').on('change', function() {
    if(this.checked && $('#projectNoRegional').is(':checked')) {
      $('#projectNoRegional').prop('checked', false).trigger('change');
    }
  });
  // A record saved before this rule could hold both: the exclusive option wins, as it did on load before.
  if($('#projectNoRegional').is(':checked')) {
    $('input.rpInput:checked').prop('checked', false);
  }

  /**
   * Cluster of Activities
   */

  var $coreSelect = $('#projectsList select');
  var $coreProjects = $('#projectsList .list');

  var coaSelectedIds = ($('#coaSelectedIds').text()).split(',');
  $coreSelect.clearOptions(coaSelectedIds);

  // Validate more than one cluster and if has specific rule
  validateClusters();

  /**
   * Scope of project
   */

  var $projectsScopesSelect = $('#projectsScopes select');
  var $projectsScopes = $('#projectsScopes .list');

  var scopesSelectedIds = ($('#scopesSelectedIds').text()).split(',');
  $projectsScopesSelect.clearOptions(scopesSelectedIds);

  /** Gender questions * */

  $('input#gender').on('change', function() {
    if($(this).is(':checked')) {
      $('#gender-question').slideUp();
    } else {
      $('#gender-question').slideDown();
    }
  });

  $('input.ccMarker').on('change', function() {
    $('input#na').prop("checked", false);
  });

  $('input#na').on('change', function() {
    $('input.ccMarker').prop("checked", false);
    $('#gender-question').slideDown();
  });

  /** Events * */

  // Event to add an item to core Project list from select option
  $coreSelect.on('change', function(e) {
    addItemList($(this).find('option:selected'));
  });

  $projectsScopesSelect.on('change', function(e) {
    addItemScopeList($(this).find('option:selected'));
  });

  // Event to remove an element 'li' from core project list
  $('ul li .remove').on('click', function(e) {
    if($(e.target).hasClass('popUpValidation')) {
      $("#removeContribution-dialog").dialog({
          modal: true,
          closeText: "",
          width: 500,
          buttons: {
              "Confirm": function() {
                removeItemList($(e.target).parents('li'));
                $(this).dialog("close");
              },
              "Cancel": function() {
                $(this).dialog("close");
              }
          }
      });
    } else {
      removeItemList($(e.target).parents('li'));
    }

  });

  $statuses.on('change', function(e) {
    var statusID= $(this).val();
    if(isStatusCancelled(statusID) || isStatusExtended(statusID)) {
      $statusDescription.show(400);
    } else {
      $statusDescription.hide(400);
    }
  });

  $endDate.on('change', changeStatus);
  if (editable){
    $endDate.trigger('change');
  }
  

  /** Functions */

  function changeStatus() {
    var d = new Date($(this).val());
    checkImplementationStatus(d.getFullYear());
  }

  function checkImplementationStatus(year) {
    if(year <= currentReportingYear) {
      $statuses.removeOption(2);
    } else {
      if(!reportingActive){
        $statuses.addOption(2, implementationStatus);
      }
    }
    $statuses.select2();
  }

  function addItemScopeList($item) {
    var $listElement = $("#projecScope-template").clone(true).removeAttr("id");
    $listElement.find('.id').val($item.val());
    $listElement.find('.name').html($item.text());

    $listElement.appendTo($projectsScopes).hide().show('slow');
    $projectsScopes.find('.emptyText').hide();
    $item.remove();
    $projectsScopesSelect.trigger("change.select2");
    setProjectsIndexes();
  }

  function addItemList($item) {
    var $listElement = $("#cpListTemplate").clone(true).removeAttr("id");
    $listElement.find('.id').val($item.val());
    $listElement.find('.name').html($item.text());

    // Getting CoA Leaders
    $.ajax({
        url: baseURL + '/ClusterActivitiesLeaders.do',
        data: {
          clusterActivityID: $item.val(),
          phaseID: phaseID
        },
        success: function(data) {
          $.each(data.leaders, function(i,e) {
            $listElement.find('.leaders').append('<li class="leader">' + escapeHtml(e.description) + '</li>');
          });
        }
    });

    $listElement.appendTo($coreProjects).hide().show('slow');
    $coreProjects.find('.emptyText').hide();
    $item.remove();
    $coreSelect.trigger("change.select2");
    setProjectsIndexes();
  }

  function removeItemList($item) {
    // Adding to select list
    var data = {
        id: $item.find('.id').val(),
        'name': $item.find('.name').text()
    };
    var $select = $item.parents('.panel').find('select');
    $select.append(setOption(data.id, data.name));
    $select.trigger("change.select2");
    // Removing from list
    $item.hide("slow", function() {
      $item.remove();
      setProjectsIndexes();
    });
  }

  function validateClusters() {
    var clusterNumber = $coreProjects.find('li.clusterActivity').length;
    if($coreSelect.classParam('multipleCoA') === "false") {
      if(clusterNumber == 0) {
        $coreSelect.prop("disabled", false);
      } else {
        $coreSelect.prop("disabled", true);
      }
    }

  }

  function setProjectsIndexes() {
    // Validate more than one cluster and if has specific rule
    validateClusters();

    $coreProjects.find('li.clusterActivity').each(function(i,item) {
      $(item).setNameIndexes(1, i);
    });

    $projectsScopes.find('li.projecScope').each(function(i,item) {
      $(item).setNameIndexes(1, i);
    });
  }

});

/**
 * Attach to the date fields the datepicker plugin
 */
function datePickerConfig(element) {
  date($(element.startDate), $(element.endDate));
}

function date(start,end) {
  var dateFormat = "yy-mm-dd";
  var from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2021-01-01',
      maxDate: '2031-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(end).datepicker("option", "minDate", selectedDate);
        }
      }
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
    }
  });

  var to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2021-01-01',
      maxDate: '2031-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(start).datepicker("option", "maxDate", selectedDate);
        }
      }
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
    }
  });

  function getDate(element) {
    var date;
    try {
      date = $.datepicker.parseDate(dateFormat, element.value);
    } catch(error) {
      date = null;
    }

    return date;
  }
}

// Activate the chosen plugin.
function addSelect2() {
  $("form select").select2();
}

function setDisabledCheckedBoxes() {
  // $('#projectWorking input[type=checkbox].fpInput:checked').attr("onclick", "return false").addClass('disabled');
}

function formatState(state) {
  if(state.text.length == 0) {
    return;
  }
  if(state.id != "-1") {
    var text = state.text.split(/:(.+)?/);
    if(typeof text[1] != "undefined") {
      var $state = $("<span><strong>" + text[0] + ":</strong> " + text[1] + "</span>");
      return $state;
    } else {
      var $state = $("<span>" + state.text + "</span>");
      return $state;
    }
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }

};

/**
 * General Information redesign (A2-2734).
 *
 * Presentation only: the handlers above keep every rule (liaison to program, the On-going option against the end
 * year, the exclusive "no regional focus", the locked flagships). This block draws what the design adds on top of
 * them: the save bar's state, the segmented cluster type, the checkbox rows, the challenges guidance, the end-date
 * check and the duration line.
 */
$(document).ready(function() {
  var $root = $('.pdn');
  if(!$root.length) {
    return;
  }
  var $form = $root.closest('form');
  var phaseYear = parseInt($root.attr('data-phase-year'), 10);
  var $i18n = $('#pdn-i18n');

  // ---- Labels ----
  // customForm ends every label with a colon ("Cluster Title:*"); the design's labels carry none. Only the text
  // nodes change, so the required mark and the help text inside the label stay.
  $root.find('.pdn-card label').each(function() {
    var first = this.firstChild;
    $(this).contents().filter(function() {
      return this.nodeType === 3;
    }).each(function() {
      this.nodeValue = this.nodeValue.replace(/\s*:\s*$/, '');
      if(this === first) {
        this.nodeValue = this.nodeValue.replace(/^\s+/, '').replace(/\s+$/, '');
      }
    });
  });

  // ---- Cluster type: the segmented control writes the one hidden value the form posts ----
  var $typeValue = $root.find('[data-pdn-type-value]');
  $root.on('click', '.pdn-seg__btn', function() {
    var $btn = $(this);
    if($btn.prop('disabled') || !$typeValue.length) {
      return;
    }
    $btn.closest('.pdn-seg').removeClass('fieldError').find('.pdn-seg__btn').removeClass('is-on')
        .attr('aria-checked', 'false');
    $btn.addClass('is-on').attr('aria-checked', 'true');
    $typeValue.val($btn.attr('data-pdn-type')).trigger('change');
  });
  // Arrow keys move the choice, as in a radio group.
  $root.on('keydown', '.pdn-seg__btn', function(e) {
    var step = (e.key === 'ArrowRight' || e.key === 'ArrowDown') ? 1
        : ((e.key === 'ArrowLeft' || e.key === 'ArrowUp') ? -1 : 0);
    if(!step) {
      return;
    }
    e.preventDefault();
    var $buttons = $(this).closest('.pdn-seg').find('.pdn-seg__btn:not(:disabled)');
    var next = ($buttons.index(this) + step + $buttons.length) % $buttons.length;
    $buttons.eq(next).trigger('focus').trigger('click');
  });

  // ---- Checkbox rows mirror their input ----
  function syncChecks() {
    $root.find('.pdn-check__input').each(function() {
      $(this).closest('.pdn-check').toggleClass('is-on', this.checked);
    });
  }
  // The prevented click on the liaison's own program leaves it checked: read the state after it settles.
  $root.on('change click', '.pdn-check__input', function() {
    setTimeout(syncChecks, 0);
  });
  // The ready handler above may already have cleared programs a "no regional focus" record still held.
  syncChecks();

  // ---- Challenges guidance: opens under the field's label ----
  $root.find('.pdn-field--guide').each(function() {
    var $field = $(this);
    var $panel = $field.find('.pdn-guide');
    var $label = $field.find('.textArea > label').first();
    if($panel.length && $label.length) {
      $label.after($panel);
    }
  });
  $root.on('click', '.pdn-guide__toggle', function() {
    var $toggle = $(this);
    var $panel = $('#' + $toggle.attr('aria-controls'));
    var open = $toggle.attr('aria-expanded') !== 'true';
    $panel.prop('hidden', !open);
    $toggle.attr('aria-expanded', String(open)).text($toggle.attr(open ? 'data-label-hide' : 'data-label-show'));
  });

  // ---- Status colour ----
  var $statusField = $root.find('.pdn-field--status');
  function syncStatus() {
    var select = $statusField.find('select')[0];
    if(select) {
      // select.value, not $().val(): jQuery reads a disabled selected option as null.
      $statusField.attr('data-status', select.value);
    }
  }
  $statusField.on('change', 'select', syncStatus);
  syncStatus();

  // ---- Dates: the end-year rule of the validator and the duration line ----
  var $start = $root.find('input.startDate');
  var $end = $root.find('input.endDate');
  var $endError = $root.find('[data-pdn-end-error]');
  var $duration = $root.find('[data-pdn-duration]');

  function parseDate(value) {
    if(!value) {
      return null;
    }
    try {
      return $.datepicker.parseDate('yy-mm-dd', String(value).substring(0, 10));
    } catch(e) {
      return null;
    }
  }

  function refreshDates() {
    // Read-only dates are hidden inputs that still carry the value.
    var start = parseDate($start.val());
    var end = parseDate($end.val());
    var endEditable = $end.is('[type="text"]');
    var endTooEarly = !!(end && !isNaN(phaseYear) && end.getFullYear() < phaseYear);
    $endError.prop('hidden', !(endTooEarly && endEditable));
    $end.toggleClass('fieldError', endTooEarly && endEditable);

    if(start && end && end >= start) {
      // Whole months from the first to the last month, both included: Jan 2021 to Dec 2029 is 9 years.
      var months = (end.getFullYear() - start.getFullYear()) * 12 + (end.getMonth() - start.getMonth()) + 1;
      var years = Math.floor(months / 12);
      var rest = months % 12;
      var key = years && rest ? 'data-duration' : (years ? 'data-duration-years' : 'data-duration-months');
      var pattern = String($i18n.attr(key) || '');
      $duration.text(pattern.replace('{0}', years || rest).replace('{1}', rest));
      $duration.prop('hidden', !pattern);
    } else {
      $duration.prop('hidden', true);
    }
  }
  $start.add($end).on('change input', refreshDates);
  refreshDates();

  // ---- Month picker (Start / End date) ----
  // Cluster dates have month precision: the start is stored as the 1st and the end as the last day of the month.
  // date() opened a jQuery UI picker with its days table hidden (jquery-ui.custom.css) while the field still showed a
  // day nobody could choose. One trigger shows the month; its panel is a year header and a grid of the 12 months.
  // The original input stays, visually hidden, as the posted value that autoSave.js and the validator read.
  var monthNames = ($.datepicker.regional[''] || {}).monthNames || [];
  var monthShort = ($.datepicker.regional[''] || {}).monthNamesShort || [];
  var $orderError = $root.find('[data-pdn-order-error]');
  var MIN_YEAR = 2021;
  var MAX_YEAR = 2031;
  var pickers = [];

  function pad(n) {
    return (n < 10 ? '0' : '') + n;
  }

  function monthLabel(date) {
    return (monthNames[date.getMonth()] || pad(date.getMonth() + 1)) + ' ' + date.getFullYear();
  }

  // Month index since year 0, to compare months without days.
  function monthKey(date) {
    return date ? date.getFullYear() * 12 + date.getMonth() : null;
  }

  function closeAll(except) {
    $.each(pickers, function(i, picker) {
      if(picker !== except) {
        picker.close(false);
      }
    });
  }

  function buildMonthPicker($input, isEnd) {
    var $field = $input.closest('.pdn-field');
    var $label = $field.find('label').first();
    var triggerId = $input.attr('id') + '-picker';
    var saved = parseDate($input.val());
    // A saved year outside the old range stays reachable, so loading the page never rewrites it.
    var minYear = Math.min(MIN_YEAR, saved ? saved.getFullYear() : MIN_YEAR);
    var maxYear = Math.max(MAX_YEAR, saved ? saved.getFullYear() : MAX_YEAR);
    var shownYear;

    var $wrap = $('<div class="pdn-monthpick"></div>');
    var $trigger = $('<button type="button" class="pdn-monthpick__trigger" aria-haspopup="dialog" aria-expanded="false"></button>')
      .attr('id', triggerId);
    var $text = $('<span class="pdn-monthpick__text"></span>');
    $trigger.append($text, '<svg class="pdn-monthpick__icon" width="16" height="16" viewBox="0 0 16 16" aria-hidden="true">'
      + '<rect x="2" y="3" width="12" height="11" rx="2" fill="none" stroke="currentColor" stroke-width="1.4"/>'
      + '<path d="M2 6.5h12M5.5 1.5v3M10.5 1.5v3" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/></svg>');

    var $panel = $('<div class="pdn-monthpick__panel" role="dialog" hidden></div>')
      .attr('aria-label', $.trim($label.clone().children().remove().end().text()));
    var $prev = $('<button type="button" class="pdn-monthpick__nav" data-step="-1"></button>')
      .attr('aria-label', $i18n.attr('data-prev-year')).html('<span aria-hidden="true">&#8249;</span>');
    var $next = $('<button type="button" class="pdn-monthpick__nav" data-step="1"></button>')
      .attr('aria-label', $i18n.attr('data-next-year')).html('<span aria-hidden="true">&#8250;</span>');
    var $year = $('<span class="pdn-monthpick__year" aria-live="polite"></span>');
    var $grid = $('<div class="pdn-monthpick__grid"></div>');
    for(var m = 0; m < 12; m++) {
      $grid.append($('<button type="button" class="pdn-monthpick__month"></button>').attr('data-month', m)
        .attr('aria-label', monthNames[m] || '').text(monthShort[m] || pad(m + 1)));
    }
    $panel.append($('<div class="pdn-monthpick__head"></div>').append($prev, $year, $next), $grid);
    $wrap.append($trigger, $panel);

    // The label pointed at the input that is now hidden; it names the trigger instead.
    $label.attr('for', triggerId);
    $input.addClass('pdn-month__value').attr({ 'aria-hidden': 'true', tabindex: '-1' }).after($wrap);

    function current() {
      return parseDate($input.val());
    }

    function refreshTrigger() {
      var date = current();
      $text.text(date ? monthLabel(date) : $i18n.attr('data-placeholder')).toggleClass('is-empty', !date);
    }

    // The other end bounds this one, as the old picker's minDate / maxDate did.
    function bound() {
      return monthKey(parseDate((isEnd ? $start : $end).val()));
    }

    function renderPanel() {
      var selected = monthKey(current());
      var limit = bound();
      $year.text(shownYear);
      $prev.prop('disabled', shownYear <= minYear);
      $next.prop('disabled', shownYear >= maxYear);
      $grid.children().each(function() {
        var key = shownYear * 12 + Number($(this).attr('data-month'));
        var outOfOrder = limit !== null && (isEnd ? key < limit : key > limit);
        $(this).prop('disabled', outOfOrder && key !== selected)
          .attr('aria-pressed', key === selected ? 'true' : 'false')
          .toggleClass('is-selected', key === selected);
      });
    }

    function focusMonth() {
      var $target = $grid.children('.is-selected:not(:disabled)').first();
      if(!$target.length) {
        $target = $grid.children(':not(:disabled)').first();
      }
      ($target.length ? $target : $next).trigger('focus');
    }

    var picker = {
      open: function() {
        closeAll(picker);
        var date = current() || parseDate((isEnd ? $start : $end).val());
        shownYear = date ? date.getFullYear() : Math.min(Math.max(new Date().getFullYear(), minYear), maxYear);
        renderPanel();
        $panel.prop('hidden', false);
        $trigger.attr('aria-expanded', 'true');
        $wrap.addClass('is-open');
        focusMonth();
      },
      close: function(returnFocus) {
        if($panel.prop('hidden')) {
          return;
        }
        $panel.prop('hidden', true);
        $trigger.attr('aria-expanded', 'false');
        $wrap.removeClass('is-open');
        if(returnFocus) {
          $trigger.trigger('focus');
        }
      },
      contains: function(node) {
        return $.contains($wrap[0], node);
      }
    };
    pickers.push(picker);

    $trigger.on('click', function() {
      if($panel.prop('hidden')) {
        picker.open();
      } else {
        picker.close(true);
      }
    });
    $panel.on('click', '.pdn-monthpick__nav', function() {
      shownYear += Number($(this).attr('data-step'));
      renderPanel();
    });
    $grid.on('click', '.pdn-monthpick__month', function() {
      var month = Number($(this).attr('data-month'));
      var day = isEnd ? new Date(shownYear, month + 1, 0).getDate() : 1;
      $input.val(shownYear + '-' + pad(month + 1) + '-' + pad(day)).trigger('change');
      picker.close(true);
    });
    // Arrow keys move through the grid (4 columns); Escape closes.
    $panel.on('keydown', function(e) {
      if(e.key === 'Escape') {
        e.preventDefault();
        picker.close(true);
        return;
      }
      var steps = { ArrowLeft: -1, ArrowRight: 1, ArrowUp: -4, ArrowDown: 4 };
      var $month = $(e.target).closest('.pdn-monthpick__month');
      if(!$month.length || !steps[e.key]) {
        return;
      }
      e.preventDefault();
      var $months = $grid.children();
      var index = $months.index($month) + steps[e.key];
      while(index >= 0 && index < 12 && $months.eq(index).prop('disabled')) {
        index += steps[e.key] > 0 ? 1 : -1;
      }
      if(index >= 0 && index < 12) {
        $months.eq(index).trigger('focus');
      }
    });
    $input.on('change', refreshTrigger);
    refreshTrigger();
  }

  $start.add($end).filter('[type="text"]').each(function() {
    var $input = $(this);
    if($input.hasClass('hasDatepicker')) {
      $input.datepicker('destroy');
    }
    buildMonthPicker($input, $input.hasClass('endDate'));
  });

  $(document).on('mousedown.pdnMonthpick focusin.pdnMonthpick', function(e) {
    $.each(pickers, function(i, picker) {
      if(!picker.contains(e.target)) {
        picker.close(false);
      }
    });
  });

  // Read-only: forms.ftl prints the stored yyyy-mm-dd as text; show the month the value means.
  $start.add($end).filter('[type="hidden"]').each(function() {
    var date = parseDate(this.value);
    var $text = $(this).closest('.pdn-field').find('.input > p').first();
    if(date && $text.length && $.trim($text.text()) === this.value.substring(0, 10)) {
      $text.text(monthLabel(date));
    }
  });

  // Saved data can still hold an end before the start; the panel no longer lets anyone pick one.
  function refreshOrder() {
    var start = parseDate($start.val());
    var end = parseDate($end.val());
    var editable = $end.is('[type="text"]');
    var wrong = !!(editable && start && end && end < start);
    $orderError.prop('hidden', !wrong);
    $end.next('.pdn-monthpick').toggleClass('is-error', wrong || $end.hasClass('fieldError'));
    $start.next('.pdn-monthpick').toggleClass('is-error', $start.hasClass('fieldError'));
  }
  $start.add($end).on('change', refreshOrder);
  refreshOrder();

  // ---- Save bar ----
  var $saveBar = $root.find('.pdn-saveBar');

  // buttons-projects.ftl prints the last-edit message after the buttons; it reads as the state's caption.
  var $message = $saveBar.find('.pdn-saveBar__actions #lastUpdateMessage');
  if($message.length) {
    $saveBar.find('.pdn-saveBar__info').append($message);
  }

  // The design's outline save icon in place of the shared glyphicon.
  $saveBar.find('.button-save .glyphicon').replaceWith(
      '<svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true">'
          + '<path d="M3 3h7.2L13 5.8V13H3V3Z" stroke="#fff" stroke-width="1.5" stroke-linejoin="round"></path>'
          + '<path d="M5.6 3v3.4h4.2V3M5.6 13v-3.2h4.8V13" stroke="#fff" stroke-width="1.4" stroke-linejoin="round"></path>'
          + '</svg>');

  // Read-only, with no last edit to report and no button to show, the bar would be an empty white strip.
  if(!$.trim($saveBar.text()) && !$saveBar.find('button:visible, a:visible').length) {
    $saveBar.hide();
  }

  // The state compares the form with the one the page loaded, so undoing an edit brings "All changes saved" back.
  // The snapshot waits for the page to load: select2, the liaison lookup and the status options rewrite some
  // values on ready, and loading alone must not read as an edit.
  var $state = $saveBar.find('[data-pdn-save-state]');
  var loadedForm = null;

  function formState() {
    return $form.find(':input').not('[type="file"]').serialize();
  }

  function refreshSaveState() {
    if(loadedForm === null || !$state.length) {
      return;
    }
    var dirty = formState() !== loadedForm;
    $saveBar.toggleClass('is-dirty', dirty);
    $state.text($state.attr(dirty ? 'data-unsaved' : 'data-saved'));
  }

  if($state.length && $form.length) {
    $(window).on('load', function() {
      setTimeout(function() {
        loadedForm = formState();
      }, 0);
    });
    $form.on('input change', 'input, textarea, select', refreshSaveState);
    $form.on('click', '.pdn-check, .pdn-seg__btn', function() {
      setTimeout(refreshSaveState, 0);
    });
  }
});
