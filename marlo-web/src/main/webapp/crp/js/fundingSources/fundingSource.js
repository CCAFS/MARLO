var dateFormat, from, to, extension;
var W1W2, ON_GOING, EXTENDED_STATUS;
var $fundingType, $financeCode, $leadPartner;
$(document).ready(init);

$('#vueApp').show();
var app = new Vue({
    el: '#vueApp',
    data: {
        crpList: [],
        allFundingSources: []
    },
    methods: {

    }
});

function init() {

  // Setting constants
  W1W2 = 1;
  ON_GOING = 2;
  EXTENDED_STATUS = 4;
  $financeCode = $('input.financeCode');
  $leadPartner = $('input.partnerLeadInput');
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
  $('select.elementType-institution').on("addElement removeElement beforeRemoveElement", function(e,id,name) {
    var $divisionBlock = $('.division-' + id);

    if(e.type == "addElement") {
      $divisionBlock.slideDown();

      // Add institution for project mapping
      mappingFundingToProjectModule.addInstitution(id, name);

      // Add finance channel
      var $item = $('<li class="setPartnerLead value-' + id + '"><a href="">' + (name.split('-'))[0] + '</a></li>');
      $('.financeChannel ul.dropdown-menu').append($item);
      $item.on("click", setPartnerLead);
    }

    if(e.type == "beforeRemoveElement") {
      var partnerLeadID = $('input.partnerLeadInput').val();
      if(partnerLeadID == id) {
        e.preventDefault();
        $('.url-field').animateCss('shake');
      }
    }

    if(e.type == "removeElement") {
      $divisionBlock.slideUp();

      // Remove institution for project mapping
      mappingFundingToProjectModule.removeInstitution(id);

      // Remove finance channel
      var $item = $('li.setPartnerLead.value-' + id);
      $item.remove();
    }
  });

  $('.setPartnerLead').on("click", setPartnerLead);

  $financeCode.on("keyup change", findDuplicatedFinanceSource);

  // On Change agreementStatus
  $('.agreementStatus').on('change', onChangeStatus);

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
 // $('.currencyInput').currencyInput();

  /* Select2 multiple for country and region select */
  $('.countriesSelect').select2({
      placeholder: "Select a country(ies)...",
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });

  changeDonorByFundingType($fundingType.val(), $(".donor:eq(0)"));

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
          // Set file URL
          $ub.find('.fileUploaded a').attr('href', r.path + '/' + r.fileFileName)
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
    // Clear URL
    $ub.find('.fileUploaded a').attr('href', '');
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
  $('form .currencyInput').on('keyup', keyupBudgetYear).trigger('keyup');

  // Check duplicated Funding sources along CRPs
  findDuplicatedFinanceSource();

  // Add functionality of mapping funding source to a project
  mappingFundingToProjectModule.init();

  // Agreement status & Donor
  $('form select').select2({
    width: "100%"
  });
  // Funding Window / Budget type
  $("select.type").select2({
      templateResult: budgetTypeTemplate,
      width: "100%"
  });

  // showOnLoading
  $('.showOnLoading').fadeIn();
  $('.hideOnLoading').fadeOut();

}

function setPartnerLead(e) {
  e.preventDefault();
  var institutionID = $(this).classParam("value");
  var CIAT_ID = 46;
  var hasCIATSelected = (institutionID == CIAT_ID);
  var $syncComponents = $('.buttons-field'); // .extensionDateBlock

  // Show CIAT OCS Sync buttons
  if(hasCIATSelected) {
    $syncComponents.slideDown(200);
  } else {
    $syncComponents.slideUp(200);
    if(isSynced) {
      unSyncFundingSource();
    }
  }
  refreshYears();

  $('input.partnerLeadInput').val(institutionID);
  $('.partnerLeadSelectedName').text($(this).text());

  // Find Funding Sources
  findDuplicatedFinanceSource();
}

function findDuplicatedFinanceSource() {
  var financeCode = $.trim($financeCode.val());
  var leadPartnerID = $.trim($leadPartner.val());
  var $resultList = $('ul.resultList');
  var fundingSourceID = $('input[name="fundingSourceID"]').val();

  if(!financeCode || !leadPartnerID) {
    return;
  }

  $.ajax({
      url: baseURL + '/FundingSourceByCenterFinanceCode.do',
      data: {
          phaseID: phaseID,
          financeCode: financeCode,
          institutionLead: leadPartnerID
      },
      beforeSend: function() {
        app.crpList = [];
        app.allFundingSources = [];
        $financeCode.addClass('input-loading')
      },
      success: function(r) {
        // Get funding sources by Global Unit
        var crpList = [];
        var allFundingSources = [];
        $.each(r.sources, function(i,fs) {
          var includeFs = fs.id != fundingSourceID;
          var obj = $.grep(crpList, function(obj) {
            return obj.name === fs.crpName;
          })[0];
          if(obj == null) {
            obj = {
                name: fs.crpName,
                fundingSources: []
            };
            if(includeFs) {
              obj.fundingSources.push(fs);
            }
            crpList.push(obj);
          } else {
            var fsList = obj.fundingSources;
            if(includeFs) {
              fsList.push(fs);
            }
            obj.fundingSources = fsList;
          }
          if(includeFs) {
            allFundingSources.push(fs);
          }
        });

        app.allFundingSources = allFundingSources;
        app.crpList = crpList;

      },
      error: function(e) {
      },
      complete: function() {
        $financeCode.removeClass('input-loading')
      }
  });
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

  // Calculate Year Remaining
  var yearAmount = removeCurrencyFormat(this.value || "0");
  var year = $(this).classParam('year');
  var $fundingTab = $('#fundingYear-' + year);
  var remainingAmount = yearAmount;
  $fundingTab.find('span.pbAmount').each(function(i,span) {
    remainingAmount -= removeCurrencyFormat($(span).text() || "0");
  });
  var $remainingAmount = $fundingTab.find('span.remainingAmount');
  $remainingAmount.text(setCurrencyFormat(remainingAmount));
  if(remainingAmount < 0) {
    $remainingAmount.parent().addClass('fieldError');
  } else {
    $remainingAmount.parent().removeClass('fieldError');
  }
  $remainingAmount.animateCss('flipInX');

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
  if((this.value == EXTENDED_STATUS) || $('input.extensionDateInput').val()) {
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

  if($("input.contactName").exists()) {
    $("input.contactName").autocomplete(autocompleteOptions).autocomplete("instance")._renderItem = renderItem;
    $("input.contactEmail").autocomplete(autocompleteOptions).autocomplete("instance")._renderItem = renderItem;
  }
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
  var $state = $('<span> <i class="flag-icon flag-icon-' + countryISO.toLowerCase() + '"></i>  ' + v + '</span>');
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
        if((selectedDate != "") && editable) {
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
    $('.budgetByYears .nav-tabs li').first().addClass('active');
    $('.budgetByYears .tab-content .tab-pane').first().addClass('active');
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
  var $donorSelectLists = $(".donor");
  var arrayKeyValues = [];
  $('.loading.contentBlok').fadeIn(800);
  $donorSelectLists.empty();
  $donorSelectLists.addOption("-1", "Select an option...");
  $.ajax({
      url: baseURL + "/institutionsByBudgetType.do",
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
          arrayKeyValues.push([
              e.id, e.name
          ]);
        });
      },
      error: function(e) {
        console.log(e);
      },
      complete: function() {
        // Append new list of institutions
        $donorSelectLists.addArrayOptions(arrayKeyValues);
        // Set CGIAR Consortium Office if applicable to the direct donor
        changeDonorByFundingType(budgetTypeID, $(".donor:eq(0)"));
        // Trigger donors selects
        $donorSelectLists.trigger("change.select2");
        // Stop Loader
        $('.loading.contentBlok').fadeOut(800);
      }
  });
}

function changeDonorByFundingType(budgetType,$donorSelect) {
  var currentDonorId = $donorSelect.find("option:selected").val();
  var currentDonorName = $donorSelect.attr('name');
  var cgConsortiumId = $(".cgiarConsortium").text();
  var $donorParent = $donorSelect.parents('.select').parent();

  // If budget type is W1W2 and the donor is not selected
  if(!centerGlobalUnit) {
    if(((currentDonorId == "-1") || (currentDonorId == cgConsortiumId)) && (budgetType == W1W2)) {
      // Set CGIAR System Organization
      $donorSelect.val(cgConsortiumId).attr("disabled", true).trigger("change");
      $donorSelect.parents('.select').parent().append(
          '<input type="hidden" id="donorHiddenInput" name="' + currentDonorName + '" value="' + cgConsortiumId
              + '" />');
    } else if(budgetType != W1W2) {
      $donorSelect.attr("disabled", false).trigger("change");
      $('#donorHiddenInput').remove();
    }
  }
}

function formatState(state) {
  if(!state.id) {
    return state.text;
  }
  var $state = "";
  if(state.element.value != "-1") {
    $state =
        $('<span> <i class="flag-icon flag-icon-' + state.element.value.toLowerCase() + '"></i>  ' + state.text + '</span>');
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

var mappingFundingToProjectModule =
    (function() {

      function vueAppinitialState() {
        return {
            institutionID: -1,
            institutions: [],
            projects: [],
            projectID: -1,
            fundingSourceID: $('input[name="fundingSourceID"]').val(),
            budgetTypeID: $('.fundingType').val(),
            amount: 0,
            gender: 0,
            rationale: "",
            remainingBudget: "0",
            year: undefined,
            modalLoading: false,
            message: "",
            isValidForm: false
        }
      }

      var vueApp = new Vue({
          el: '#mapFundingToProject',
          data: function() {
            return vueAppinitialState();
          },
          methods: {
              projectTitle: function() {
                var titleArr = ($projectSelect.find('option[value="' + this.projectID + '"]').text()).split('-');
                titleArr.shift();
                return titleArr.join('-');
              },
              projectComposedID: function() {
                return "P" + this.projectID;
              },
              institutionAcronym: function() {
                return ($institutionSelect.find('option[value="' + this.institutionID + '"]').text()).split('-')[0];
              },
              reset: function() {
                Object.assign(this.$data, vueAppinitialState());
              }
          }
      });

      var $modal = $('#mapFundingToProject');
      var $institutionSelect = $modal.find('select[name="institutionID"]');
      var $projectSelect = $modal.find('select[name="projectID"]');
      var $amountInput = $modal.find('input.currencyInput');
      var $genderInput = $modal.find('input.percentageInput');
      var $justificationInput = $modal.find('textarea[name="rationale"]');
      var $saveButton = $modal.find('button.saveBudgetMapping');
      var $step2Block = $modal.find('.step2');

      function init() {
        $institutionSelect.select2({
          width: '100%'
        });
        $projectSelect.select2({
          width: '100%'
        });

        // Setting Numeric Inputs
        $modal.find('.currencyInput, input.percentageInput').numericInput();

        // Setting Currency Inputs
        $amountInput.currencyInput();

        // Setting Percentage Inputs
        $genderInput.percentageInput();

        addEvents();
      }

      function addEvents() {
        // On change institution Partner
        $institutionSelect.on("change", findProjects);

        // On change project
        $projectSelect.on("change", validateForm);

        // On change amount
        $amountInput.on("change keyup", validateForm);

        // On change percentage
        $genderInput.on("change  keyup", validateForm);

        // On change justification
        $justificationInput.on("change  keyup", validateForm);

        // On click
        $saveButton.on('click', saveBudgetMapping);

        // On open the modal
        $modal.on('show.bs.modal', openModal);

        // On open the modal
        $modal.on('hide.bs.modal', closeModal);

        // On remove a project budget
        $('.removeProjectBudget').on('click', removeProjectBudgetEvent);
      }

      function removeProjectBudgetEvent(e) {
        e.preventDefault();
        var $button = $(this);
        var $tr = $(this).parents('tr');
        var $table = $(this).parents('table');
        var projectID = $($tr.find('td')[0]).text();
        var projectBudgetID = $tr.classParam('projectBudget');
        var amount = $tr.find('span.pbAmount').text();
        var year = $table.classParam('tableProjectBudgets');

        var removeNotification = noty({
            layout: 'center',
            text: 'Do you want to remove this mapping of <small>US$ ' + amount + '</small> with ' + projectID + '?',
            buttons: [
                {
                    addClass: 'btn btn-primary',
                    text: 'Yes',
                    onClick: function($noty) {
                      $noty.close();
                      removeProjectBudget({
                          "project_budget_id": projectBudgetID,
                          "year": year,
                          phaseID: phaseID
                      });
                    }
                }, {
                    addClass: 'btn btn-danger',
                    text: 'Cancel',
                    onClick: function($noty) {
                      $noty.close();
                    }
                }
            ]
        });

        function removeProjectBudget(data) {
          $.ajax({
              url: baseUrl + "/removeFundingProjectBudget.do",
              data: data,
              beforeSend: function() {
                $button.addClass('icon-loading');
              },
              success: function(data) {
                if(data.status.save) {

                  $tr.hide(600, function() {
                    $tr.remove();
                    var $inputAmount = $('#fundingYear-' + year).find('.currencyInput');
                    $inputAmount.trigger('keyup');
                  });
                }
              },
              complete: function(data) {
                $button.removeClass('icon-loading');
              },
              error: function(data) {
              }
          });
        }

      }

      function retrivePopupValues() {
        vueApp.institutionID = $institutionSelect.val();
        vueApp.projectID = $projectSelect.val();
        vueApp.amount = $amountInput.val();
        // vueApp.gender = removePercentageFormat($genderInput.val());
        vueApp.rationale = $justificationInput.val();
         vueApp.remainingBudget =
          $('#fundingYear-' + vueApp.year + ' span.remainingAmount').text();
      }

      function openModal(event) {
        var $button = $(event.relatedTarget);
        vueApp.year = $button.data('year');

       // retrivePopupValues();
      }

      function closeModal(event) {
        vueApp.reset();
        $institutionSelect.val(vueApp.institutionID).select2({
          width: '100%'
        });
        $amountInput.val(vueApp.amount);
        $genderInput.val(vueApp.gender);
        $justificationInput.val(vueApp.rationale);
        $step2Block.slideUp();
      }

      function addInstitution(value,text) {
        console.log("add");
        $institutionSelect.addOption(value, text);
      }

      function removeInstitution(value) {
        console.log("remove");
        $institutionSelect.removeOption(value);
      }

      function validateForm() {
        vueApp.isValidForm = false;
        var missingFields = 0;
        retrivePopupValues();

        // Validate institution
        if((!vueApp.institutionID) || (vueApp.institutionID == '-1')) {
          missingFields += 1;
        }
        // Validate project
        if((!vueApp.projectID) || (vueApp.projectID == '-1')) {
          missingFields += 1;
        }
        // Validate Amount
        if(vueApp.amount <= 0) {
          missingFields += 1;
        }
        // Validate justification
        if(!vueApp.rationale) {
          missingFields += 1;
        }

        vueApp.remainingBudget -= vueApp.amount;

        vueApp.isValidForm = (missingFields == 0);
        return vueApp.isValidForm;
      }

      function saveBudgetMapping() {
        var $table = $('table.tableProjectBudgets-' + vueApp.year);

        if(validateForm()) {

          var dataBudget = {
              institutionID: vueApp.institutionID,
              fundingSourceID: vueApp.fundingSourceID,
              projectID: vueApp.projectID,
              budgetTypeID: vueApp.budgetTypeID,
              amount: vueApp.amount,
              gender: vueApp.gender,
              justification: vueApp.rationale,
              year: vueApp.year,
              phaseID: phaseID
          };

          $.ajax({
              url: baseUrl + "/SaveFundingMapProject.do",
              data: dataBudget,
              beforeSend: function() {
                vueApp.modalLoading = true;
              },
              success: function(data) {
                if(data.status.save) {

                  var projectBudgetID = data.status.id;
                  var url =
                      baseUrl + '/projects/' + currentCrpSession + '/budgetByPartners.do?projectID=' + vueApp.projectID
                          + '&edit=true&phaseID=' + phaseID;
                  var tr = '<tr class="projectBudget-' + projectBudgetID + '">';
                  tr += '<td><a href="' + url + '">' + vueApp.projectComposedID() + '</a></td>';
                  tr += '<td><a href="' + url + '">' + vueApp.projectTitle() + '</a></td>';
                  tr += '<td>' + vueApp.rationale + ' </td>';
                  tr += '<td> ' + vueApp.institutionAcronym() + ' </td>';
                  tr += '<td>US$ <span class="pbAmount">' + setCurrencyFormat(vueApp.amount) + '</span> </td>';
                  tr += '<td><span class="removeProjectBudget trashIcon"></span></td>';
                  tr += '</tr>';

                  var addedRow = $table.DataTable().row.add($(tr)).draw(false);

                  $modal.modal('hide'); // Hide and clean data
                  console.log(addedRow.node());
                  $(addedRow.node()).find('.removeProjectBudget').on('click', removeProjectBudgetEvent);

                  var $inputAmount = $('#fundingYear-' + dataBudget.year).find('.currencyInput')
                  $inputAmount.trigger('keyup');
                }
              },
              complete: function(data) {
                vueApp.modalLoading = false;
              },
              error: function(data) {
              }
          });
        }

      }

      function findProjects() {
        var institutionID = $institutionSelect.val();

        $.ajax({
            url: baseUrl + "/FundingMapProjectList.do",
            data: {
                institutionID: institutionID,
                fundingSourceID: vueApp.fundingSourceID,
                year: vueApp.year,
                phaseID: phaseID
            },
            beforeSend: function() {
              $projectSelect.empty();
              vueApp.message = "";
              vueApp.modalLoading = true;
            },
            success: function(data) {
              if(!data.projects.length) {
                vueApp.message = "No project(s) to map found";
                $step2Block.slideUp();
              } else {
                $step2Block.slideDown();
              }
              $projectSelect.addOption(-1, "Select an option...");
              $.each(data.projects, function(i,p) {
                $projectSelect.addOption(p.id, p.description);
              });

            },
            complete: function(data) {
              vueApp.modalLoading = false;
              validateForm();
            },
            error: function(data) {
            }
        });

      }

      return {
          init: init,
          addInstitution: addInstitution,
          removeInstitution: removeInstitution
      }
    })();
