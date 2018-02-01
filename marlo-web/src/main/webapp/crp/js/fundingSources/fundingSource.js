var dateFormat, from, to, extension;
var W1W2, ON_GOING, EXTENDED_STATUS;
var $fundingType;
$(document).ready(init);

function init() {

  // Setting constants
  W1W2 = 1;
  ON_GOING = 2;
  EXTENDED_STATUS = 4;

  $fundingType = $(".fundingType");

  // Set Dateformat
  dateFormat = "yy-mm-dd";

  // Dropdown
  $('.dropdown-toggle').on('show.bs.dropdown', function() {
    console.log('dropdown-toggle');
  })

  // Check region option
  $("#regionList").find(".region").each(
      function(i,e) {
        var option =
            $("#regionSelect").find(
                "option[value='" + $(e).find("input.rId").val() + "-" + $(e).find("input.regionScope").val() + "']");
        option.prop('disabled', true);
      });

  // Original Donor
  $(".donor").on("change", function() {
    var $option = $(this).find("option:selected");

    var selectedValue = $option.val();
    var count = 0;

    if(selectedValue == "-1") {
      return;
    }

    // Count repeated donors
    $('select.donor').each(function(i,e) {
      if(e.value == selectedValue) {
        count++;
      }
    });

    // Check if the donor is already selected
    if(count > 1) {
      // Reset Select
      $('.donor:eq(1)').val(-1);
      $(this).trigger('select2:change');
      // Noty Message
      var message = "Donors must be different";
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = message;
      noty(notyOptions);
    }
  });

  // Agreement status & Donor
  $('form select').select2({
    width: "100%"
  });

  // Activate Popup
  popups();

  // Add Data Table
  addDataTable();

  // Partner(s) managing the funding source
  $(".institution").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addLeadPartner(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });

  // On Change agreementStatus
  $('.agreementStatus').on('change', onChangeStatus);

  // Remove partner
  $(".removeLeadPartner").on("click", removeLeadPartner);

  // Country item
  $(".countriesSelect").on("change", function() {
    var $option = $(this).find("option:selected");
    if($option.val() != "-1") {
      var countryISO = $option.val();
      var countryName = $option.text();
      // Add Country
      addCountry(countryISO, countryName);
      // Reset select
      $option.val("-1");
      $option.trigger('change.select2');
    }
    // Remove option from select
    $option.remove();
    $(this).trigger("change.select2");
  });

  // Remove country item
  $(".removeCountry").on("click", removeCountry);

  // Region item
  $("#regionSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addRegion(option);
      // Remove option from select
      option.prop('disabled', true);
      $('#regionSelect').select2();
    }
  });

  // Remove region item
  $(".removeRegion").on("click", removeRegion);

  // Setting Currency Inputs
  $('.currencyInput').currencyInput();

  /* Select2 multiple for country and region select */
  $('.countriesSelect').select2({
      placeholder: "Select a country(ies)...",
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });

  changeDonorByFundingType($fundingType.val(), $(".donor:eq(0)"))

  // Check Funding type
  onChangeFundingType($fundingType.val());

  // Funding Window / Budget type
  $("select.type").select2({
    templateResult: budgetTypeTemplate
  });

  // When select center as Funding Window
  var lastDonor = -1;
  $("select.fundingType").on("change", function() {
    var $option = $(this).find("option:selected");
    var optionValue = $option.val();
    // Change Donor list
    getInstitutionsBudgetByType(optionValue);

    // Event on change
    onChangeFundingType(optionValue);
  });

  /**
   * File upload (blueimp-tmpl)
   */

  var $uploadBlock = $('.fileUploadContainer');
  var $fileUpload = $uploadBlock.find('.upload');
  $fileUpload.fileupload({
      dataType: 'json',
      start: function(e) {
        var $ub = $(e.target).parents('.fileUploadContainer');
        $ub.addClass('blockLoading');
      },
      stop: function(e) {
        var $ub = $(e.target).parents('.fileUploadContainer');
        $ub.removeClass('blockLoading');
      },
      done: function(e,data) {
        var r = data.result;
        console.log(r);
        if(r.saved) {
          var $ub = $(e.target).parents('.fileUploadContainer');
          $ub.find('.textMessage .contentResult').html(r.fileFileName);
          $ub.find('.textMessage').show();
          $ub.find('.fileUpload').hide();
          // Set file ID
          $ub.find('input.fileID').val(r.fileID);
        }
      },
      progressall: function(e,data) {
        var progress = parseInt(data.loaded / data.total * 100, 10);
      }
  });

  // Prepare data
  $fileUpload.bind('fileuploadsubmit', function(e,data) {

  });

  // Remove file event
  $uploadBlock.find('.removeIcon').on('click', function() {
    var $ub = $(this).parents('.fileUploadContainer');
    $ub.find('.textMessage .contentResult').html("");
    $ub.find('.textMessage').hide();
    $ub.find('.fileUpload').show();
    $ub.find('input.fileID').val('');
    $ub.find('input.outcomeID').val('');
  });

  /** End File upload* */

  // Add Principal investigator auto-complete
  addContactAutoComplete();

  // Disabled Auto save AJAX if click Save
  $('[name=save]').on('click', function(e) {
    // Cancel Auto Save
    autoSaveActive = false;
  });

  // General YES/NO event
  $(".button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
  });

  // Is this funding source has regional dimension
  $(".isRegional .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    if(!valueSelected) {
      $(".regionsBox").hide("slow");
    } else {
      $(".regionsBox").show("slow");
    }
  });

  // Does this study involve research with human subjects?
  $('.humanSubjectsRadio').on('change', function() {
    var isChecked = (this.value == "true");
    if(isChecked) {
      $('.humanSubjectsBlock').show();
    } else {
      $('.humanSubjectsBlock').hide();
    }
  });

  // Check total grant amount
  $('.currencyInput').on('keyup', keyupBudgetYear).trigger('keyup');
}

/**
 * Validate the grand total amount doesn't exceed
 */
function keyupBudgetYear() {
  var grantAmount = $('#grantTotalAmount input').val();
  var total = 0
  $('.currencyInput').each(function(i,e) {
    total = total + removeCurrencyFormat(e.value || "0");
  });
  $('#grantTotalAmount .remaining').text(setCurrencyFormat(grantAmount - total));

  // Validate total of agreement and budget type
  if(grantAmount < total) {
    $('#grantTotalAmount').addClass('fieldError').animateCss('shake');
  } else {
    $('#grantTotalAmount').removeClass('fieldError');
  }
}

/**
 * Event on Change the funding type (W1/W2, W3, Bilateral, CenterFunds)
 * 
 * @param {number} typeID - Funding budget type
 */
function onChangeFundingType(typeID) {
  // Check W1/W2 - Tag
  if(typeID == W1W2) {
    $('.w1w2-tag').show();
  } else {
    $('.w1w2-tag').hide();
  }
}

/**
 * Event on change Agreement status
 */
function onChangeStatus() {
  if(this.value == EXTENDED_STATUS) {
    $('.extensionDateBlock').show();
    $('.endDateBlock .dateLabel').addClass('disabled');
  } else {
    $('.extensionDateBlock').hide();
    $('.endDateBlock .dateLabel').removeClass('disabled');
  }
}

/**
 * This function initialize the contact person auto complete
 * 
 * @returns
 */
function addContactAutoComplete() {
  var autocompleteOptions = {
      source: searchSource,
      minLength: 2,
      select: selectUser
  }

  function searchSource(request,response) {
    $.ajax({
        url: baseURL + '/searchUsers.do',
        data: {
            q: request.term,
            phaseID: phaseID
        },
        success: function(data) {
          response(data.users);
        }
    });
  }

  function selectUser(event,ui) {
    $("input.contactName:not([readonly])").val(ui.item.name);
    $("input.contactEmail:not([readonly])").val(ui.item.email);
    return false;
  }

  function renderItem(ul,item) {
    return $("<li>").append("<div>" + escapeHtml(item.composedName) + "</div>").appendTo(ul);
  }

  $("input.contactName").autocomplete(autocompleteOptions).autocomplete("instance")._renderItem = renderItem;
  $("input.contactEmail").autocomplete(autocompleteOptions).autocomplete("instance")._renderItem = renderItem;
}

/**
 * Add a new lead partner element function
 * 
 * @param option means an option tag from the select
 * @returns
 */
function addLeadPartner(option) {
  var canAdd = true;
  console.log(option.val());
  if(option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(option).parents(".select").parents("#leadPartnerList").find(".list");
  var $item = $("#leadPartnerTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 80) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.leadPartners').each(function(i,e) {
    if($(e).find('input.fId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set funding source parameters
  $item.find(".name").attr("title", $(option).text());
  $item.find(".name").html(v);
  $item.find(".fId").val(option.val());
  $item.find(".id").val(-1);
  $list.append($item);
  $item.show('slow');
  updateLeadPartner($list);
  checkLeadPartnerItems($list);

  // Reset select
  $(option).val("-1");
  $(option).trigger('change.select2');

}

/**
 * Remove lead partner function
 * 
 * @returns
 */
function removeLeadPartner() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.leadPartners');
  var value = $item.find(".fId").val();
  var name = $item.find(".name").attr("title");

  var $select = $(".institution");
  $item.hide(200, function() {
    $item.remove();
    checkLeadPartnerItems($list);
    updateLeadPartner($list);
  });
  // Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

/**
 * Update indexes for "Managing partners" of funding source
 * 
 * @param $list List of lead partners
 * @returns
 */
function updateLeadPartner($list) {
  // Hide All divisions block
  $('.divisionBlock').hide();

  $($list).find('.leadPartners').each(function(i,e) {
    // Show division block
    var institutionID = $(e).find('.fId').val();
    $('.division-' + institutionID).show();
    // Set funding sources indexes
    $(e).setNameIndexes(1, i);
  });
}

/**
 * Check if there is any lead partners and show a text message
 * 
 * @param block Container with lead partners elements
 * @returns
 */
function checkLeadPartnerItems(block) {

  // Check if CIAT is in the partners list
  var CIAT_ID = 46;
  if($('input.fId[value="' + CIAT_ID + '"]').exists()) {
    $('.buttons-field, .financeChannel, .extensionDateBlock').show();
  } else {
    $('.buttons-field, .financeChannel, .extensionDateBlock').hide();
    if(isSynced) {
      unSyncFundingSource();
    }
  }

  refreshYears();

  var items = $(block).find('.leadPartners').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

/**
 * Add a new country to the Funding source locations
 * 
 * @param countryISO e.g CO
 * @param countryName e.g Colombia
 * @returns
 */
function addCountry(countryISO,countryName,percentage) {
  var canAdd = true;

  if(countryISO == "-1") {
    canAdd = false;
  }

  var $list = $("#countryList").find(".list");
  var $item = $("#countryTemplate").clone(true).removeAttr("id");
  var v = countryName.length > 12 ? countryName.substr(0, 12) + ' ... ' : countryName;

  // Check if is already selected
  $list.find('.country').each(function(i,e) {
    if($(e).find('input.cId').val() == countryISO) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set country parameters
  $item.find(".name").attr("title", countryName);
  var $state = $('<span> <i class="flag-sm flag-sm-' + countryISO + '"></i>  ' + v + '</span>');
  $item.find(".name").html($state);
  $item.find(".cId").val(countryISO);
  $item.find(".cPercentage").val(percentage);
  $item.find(".id").val(-1);
  $list.append($item);
  $item.show('slow');
  updateCountryList($list);
  checkCountryList($list);
}

function removeCountry() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.country');
  var value = $item.find(".cId").val();
  var name = $item.find(".name").attr("title");

  var $select = $(".countriesSelect");
  $item.hide(300, function() {
    $item.remove();
    checkCountryList($list);
    updateCountryList($list);
  });
  // Add country option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function updateCountryList($list) {

  $($list).find('.country').each(function(i,e) {
    // Set country indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkCountryList(block) {
  var items = $(block).find('.country').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

/** REGIONS SELECT FUNCTIONS * */
// Add a new region element
function addRegion(option) {
  var canAdd = true;
  if(option.val() == "-1") {
    canAdd = false;
  }
  var optionValue = option.val().split("-")[0];
  var optionScope = option.val().split("-")[1];

  var $list = $(option).parents("#regionList").find(".list");
  var $item = $("#regionTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 20 ? $(option).text().substr(0, 20) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.region').each(function(i,e) {
    if($(e).find('input.rId').val() == optionValue) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set region parameters
  $item.find(".name").attr("title", $(option).text());
  $item.find(".name").html($(option).text());
  $item.find(".rId").val(optionValue);
  $item.find(".regionScope").val(optionScope);
  $item.find(".id").val(-1);
  $list.append($item);
  $item.show('slow');
  updateRegionList($list);
  checkRegionList($list);

  // Reset select
  // $(option).val("-1");
  // $(option).trigger('change.select2');

}

function removeRegion() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.region');
  var value = $item.find(".rId").val();
  var scope = $item.find(".regionScope").val();
  var name = $item.find(".name").attr("title");

  var $select = $(".regionsSelect");
  $item.hide(300, function() {
    $item.remove();
    checkRegionList($list);
    updateRegionList($list);
  });
  var option = $select.find("option[value='" + value + "-" + scope + "']");
  console.log(option);
  option.prop('disabled', false);
  $('#regionSelect').select2();
  // Add region option again
  // $select.addOption(value, name);
  // $select.trigger("change.select2");
}

function updateRegionList($list) {
  $($list).find('.region').each(function(i,e) {
    // Set regions indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkRegionList(block) {
  var items = $(block).find('.region').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

/**
 * Set the JQuery UI Datepicker plugin for start, end and extension dates
 * 
 * @param start
 * @param end
 * @param extensionDate
 * @returns
 */
function settingDate(start,end,extensionDate) {

  from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: new Date(MIN_DATE),
      maxDate: new Date($(end).val()) || new Date(MAX_DATE),
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1);
        if(budgetsConflicts(from.val().split('-')[0], inst.selectedYear - 1)) {
          $(this).datepicker("hide");
          return;
        }
        $(this).datepicker('setDate', selectedDate);
        $(this).next().html(getDateLabel(this));
        $(this).datepicker("hide");
        if(selectedDate != "") {
          $(end).datepicker("option", "minDate", selectedDate);
        }
        refreshYears();
      }
  }).on("change", function() {
    // The change event is used for Sync
    $(this).parent().find('.dateLabel').html(getDateLabel(this));
    refreshYears();
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
      refreshYears();
    }
  });

  to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: new Date($(start).val()) || new Date(MIN_DATE),
      maxDate: new Date($(extensionDate).val()) || new Date(MAX_DATE),
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        if(budgetsConflicts(inst.selectedYear + 1, to.val().split('-')[0])) {
          $(this).datepicker("hide");
          return;
        }
        $(this).datepicker('setDate', selectedDate);
        $(this).next().html(getDateLabel(this));
        $(this).datepicker("hide");
        if(selectedDate != "") {
          $(start).datepicker("option", "maxDate", selectedDate);
        }
        refreshYears();
      }
  }).on("change", function() {
    // The change event is used for Sync
    $(this).parent().find('.dateLabel').html(getDateLabel(this));
    refreshYears();
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
      refreshYears();
    }
  });

  extension = $(extensionDate).datepicker({
      dateFormat: dateFormat,
      minDate: new Date($(to).val()) || new Date(MIN_DATE),
      maxDate: new Date(MAX_DATE),
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        console.log(selectedDate);
        if(budgetsConflicts(inst.selectedYear + 1, extension.val().split('-')[0])) {
          $(this).datepicker("hide");
          return;
        }
        $(this).datepicker('setDate', selectedDate);
        $(this).next().html(getDateLabel(this));
        $(this).datepicker("hide");
        if(selectedDate != "") {
          $(to).datepicker("option", "maxDate", selectedDate);
        }
        refreshYears();
      }
  }).on("change", function() {
    // The change event is used for Sync
    if(this.value) {
      $(this).parent().find('.dateLabel').html(getDateLabel(this));
    }
    refreshYears();
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
      refreshYears();
    }
  });

  // Event when a datelabel is clicked
  $('.dateLabel').on('click', function() {
    var $dateInput = $(this).parent().find('input');
    var $dateLabel = $(this);
    var isEnable = !($dateLabel.hasClass('disabled'));
    if(isEnable && !isSynced) {
      $dateInput.datepicker("show");

      // Set a Date if the input is empty
      if(!$dateInput.val()) {
        $dateInput.datepicker("setDate", new Date());
        $dateLabel.html(getDateLabel($dateInput));
        refreshYears();
      }
    }
  });

  // Clear Date
  $('.clearDate').on('click', function() {
    if(!isSynced) {
      $(this).parent().find('input').val('');
      $(this).parent().find('.dateLabel').text('');

      // Clear endDate maxlimit
      $(to).datepicker("option", "maxDate", "");
      refreshYears();
    }
  });

  // Activate tab default
  if(!$('.budgetByYears .nav-tabs li.active').exists()) {
    $('.budgetByYears .nav-tabs li').last().addClass('active');
    $('.budgetByYears .tab-content .tab-pane').last().addClass('active');
  }
}

/**
 * Check for budget conflicts, date cannot be changed as this funding source has at least one budget allocation
 * 
 * @param lowEnd
 * @param highEnd
 * @returns
 */
function budgetsConflicts(lowEnd,highEnd) {
  var yearConflicts = [];
  // Getting conflicts
  for(var i = parseInt(lowEnd); i <= parseInt(highEnd); i++) {
    var projectsBudgets = $('#fundingYear-' + i).find('tr.projectBudgetItem').length;
    if(projectsBudgets > 0) {
      yearConflicts.push(i);
    }
  }

  if(yearConflicts.length > 0) {
    // Noty Message
    var message =
        "Date cannot be changed as this funding source has at least one budget allocation in <b>"
            + yearConflicts.join(', ') + "</b>";
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = message;
    notyOptions.animation = {
        open: 'animated bounceInLeft', // Animate.css class names
        close: 'animated bounceOutLeft', // Animate.css class names
        easing: 'swing', // unavailable - no need
        speed: 500
    // unavailable - no need
    };
    $('.dateErrorBox').noty(notyOptions);

    return true;
  }
  return false;
}

/**
 * @returns
 */
function refreshYears() {
  var startYear, endYear, years;

  console.log(from.val());
  startYear = (from.val().split('-')[0]) || currentCycleYear;

  if($('.agreementStatus').val() == EXTENDED_STATUS) {
    endYear = (extension.val().split('-')[0]) || (to.val().split('-')[0]) || startYear;
  } else {
    endYear = (to.val().split('-')[0]) || startYear;
  }

  var years = [];

  // Clear tabs & content
  $('.budgetByYears .nav-tabs').empty();
  $('.budgetByYears .tab-content .tab-pane').removeClass('active going');

  var index = 0;
  while(startYear <= endYear) {

    // Build Tab
    var tab = '<li>';
    tab += '<a href="#fundingYear-' + startYear + '" data-toggle="tab">' + startYear + '</a>';
    tab += '</li>';
    // Append Tab
    $('.budgetByYears .nav-tabs').append(tab);

    if(!$('#fundingYear-' + startYear).exists()) {
      // CustomName
      var customName = 'fundingSource.budgets[-1]';
      // Build Content
      var content = '<div class="tab-pane going" id="fundingYear-' + startYear + '">';
      content += '<div class="form-group row">';
      content += '<div class="col-md-4">';
      content += '<label for="">Budget for ' + startYear + ':</label>';
      content += '<input type="hidden" name="' + customName + '.year" value="' + startYear + '">';
      content += '<input type="text" name="' + customName + '.budget" class="currencyInput form-control input-sm" />';
      content += '</div>';
      content += '</div>';
      content += '</div>';

      var $content = $(content);
      // Set indexes
      $content.setNameIndexes(1, index);
      // Append Content
      $('.budgetByYears .tab-content').append($content);

      // Set currency format
      $content.find('input.currencyInput').currencyInput();

      // Set Budget currency event that check the total grant amount
      $content.find('.currencyInput').on('keyup', keyupBudgetYear);

    } else {
      // Set indexes
      $('#fundingYear-' + startYear).setNameIndexes(1, index);
      $('#fundingYear-' + startYear).addClass('going')
    }

    index++;
    years.push(startYear++);
  }

  // Clear unused content names
  $('.budgetByYears .tab-content .tab-pane').not('.going').each(function(i,content) {
    $(content).setNameIndexes(1, index + i);
  });

  // Set active tab & content
  if(years.indexOf(parseInt(currentCycleYear)) == -1) {
    $('.budgetByYears .nav-tabs li').last().addClass('active');
    $('.budgetByYears .tab-content .tab-pane').last().addClass('active');
  } else {
    $('a[href="#fundingYear-' + currentCycleYear + '"]').parent().addClass('active');
    $('#fundingYear-' + currentCycleYear).addClass('active');
  }

}

/**
 * Get date in format
 * 
 * @param element
 * @returns
 */
function getDate(element) {
  var date;
  try {
    date = $.datepicker.parseDate(dateFormat, element.value);
  } catch(error) {
    date = null;
  }
  return date;
}

/**
 * Get date in MM yy format
 * 
 * @param element - An input with a Date value
 * @returns String e.g. May 2017
 */
function getDateLabel(element) {
  var dateValue = $(element).val();
  var year = dateValue.split('-')[0];
  var month = dateValue.split('-')[1];
  var day = dateValue.split('-')[2];
  console.log($(element).val());
  return $.datepicker.formatDate("MM yy", new Date(year, month - 1, day));
}

/**
 * Add Datatable plugin
 */
function addDataTable() {
  $('table').dataTable({
      "bPaginate": false, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": true, // This option enables the auto adjust columns width
      "iDisplayLength": 100,// Number of rows to show on the table
      "language": {
        "emptyTable": "This funding source has not been assigned to any project."
      },
      aoColumnDefs: [
        {
            bSortable: false,
            aTargets: [

            ]
        }
      ]
  });

}

/**
 * Get from the back-end a list of institutions
 * 
 * @param budgetTypeID
 * @returns
 */
function getInstitutionsBudgetByType(budgetTypeID) {
  // var $select = $(".donor");
  var $donorSelectLists = $(".donor");
  var url = baseURL + "/institutionsByBudgetType.do";
  $.ajax({
      url: url,
      type: 'GET',
      data: {
        budgetTypeID: budgetTypeID
      },
      beforeSend: function() {
        $('.loading').show();
      },
      success: function(m) {
        $donorSelectLists.empty();
        $donorSelectLists.addOption("-1", "Select an option...");

        $.each(m.institutions, function(i,e) {
          $donorSelectLists.addOptionFast(e.id, e.name);
        });

        // Set CGIAR Consortium Office if applicable to the direct donor
        changeDonorByFundingType(budgetTypeID, $(".donor:eq(0)"));
      },
      error: function(e) {
        console.log(e);
      },
      complete: function() {
        $('.loading').hide();
        $donorSelectLists.trigger("change.select2");
      }
  });
}

function changeDonorByFundingType(budgetType,$donorSelect) {
  var currentDonorId = $donorSelect.find("option:selected").val();
  var currentDonorName = $donorSelect.attr('name');
  var cgConsortiumId = $(".cgiarConsortium").text();

  // If budget type is W1W2 and the donor is not selected
  if(((currentDonorId == "-1") || (currentDonorId == cgConsortiumId)) && (budgetType == W1W2)) {
    // Set CGIAR System Organization
    $donorSelect.val(cgConsortiumId).attr("disabled", true).trigger("change");
    $donorSelect.parents('.select').parent().append(
        '<input type="hidden" id="donorHiddenInput" name="' + currentDonorName + '" value="' + cgConsortiumId + '" />');
  } else if(budgetType != W1W2) {
    $donorSelect.attr("disabled", false).trigger("change");
    $('#donorHiddenInput').remove();
  }
}

function formatState(state) {
  if(!state.id) {
    return state.text;
  }
  var $state = "";
  if(state.element.value != "-1") {
    $state =
        $('<span> <i class="flag-sm flag-sm-' + state.element.value.toUpperCase() + '"></i>  ' + state.text + '</span>');
  } else {
    $state = $('<span>' + state.text + '</span>');
  }
  return $state;
};

function budgetTypeTemplate(state) {
  var name = state.text;
  var desc = $('li.budgetTypeDescription-' + state.id).text();
  var $state = $("<span><b>" + name + "</b><br><small class='selectDesc'>" + desc + "</small></span>");
  return $state;
}
