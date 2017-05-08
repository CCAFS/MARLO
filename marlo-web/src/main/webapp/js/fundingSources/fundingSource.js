$(document).ready(init);

function init() {
  
  
  // Popup
  popups();

  // Add Data Table
  addDataTable();

  // Lead partner
  $(".institution").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addLeadPartner(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });

  $(".removeLeadPartner").on("click", removeLeadPartner);
  
// Country item
  $(".countriesSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addCountry(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });
  $(".removeCountry").on("click", removeCountry);
  
// REGION item
  $(".regionsSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addRegion(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });
  $(".removeRegion").on("click", removeRegion);

  // Setting Currency Inputs
  $('.currencyInput').currencyInput();
  date("form #fundingSource\\.startDate", "form #fundingSource\\.endDate");

  
  // Agreement status & Donor
  $('form select').select2({
    width: "100%"
  });
  
  /* Select2 multiple for country and region select */
  $('.countriesSelect').select2({
      placeholder: "Select a country(ies)...",
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });
  $('.regionSelect').select2({
    placeholder: "Select a region(s)...",
    width: '100%'
});
  
  changeDonorByFundingType($(".type").val(), $(".donor"))
  
  checkAgreementStatus($(".type").val());
  
  // Funding Window / Budget type
  $("select.type").select2({
      templateResult: function(state) {
        var name = state.text;
        var desc = $('li.budgetTypeDescription-' + state.id).text();
        var $state = $("<span><b>" + name + "</b><br><small class='selectDesc'>" + desc + "</small></span>");
        return $state;
      }
  });
  

  // When select center as Funding Window
  var lastDonor = -1;
  $("select.type").on("change", function() {
    
    var option = $(this).find("option:selected");
    var url = baseURL + "/institutionsByBudgetType.do";
    var data = {
      budgetTypeID: option.val()
    };
    // Change Donor list
    ajaxService(url, data);
    
    // Check Agreement status
    checkAgreementStatus(option.val());
     
  });

  // Set file upload (blueimp-tmpl)
  var $uploadBlock = $('.fileUploadContainer');
  var $fileUpload = $uploadBlock.find('.upload')
  $fileUpload.fileupload({
      dataType: 'json',
      start: function(e) {
        $uploadBlock.addClass('blockLoading');
      },
      stop: function(e) {
        $uploadBlock.removeClass('blockLoading');
      },
      done: function(e,data) {
        var r = data.result;
        console.log(r);
        if(r.saved) {
          $uploadBlock.find('.textMessage .contentResult').html(r.fileFileName);
          $uploadBlock.find('.textMessage').show();
          $uploadBlock.find('.fileUpload').hide();
          // Set file ID
          $('input#fileID').val(r.fileID);
        }
      },
      progressall: function(e,data) {
        var progress = parseInt(data.loaded / data.total * 100, 10);
      }
  });

  $uploadBlock.find('.removeIcon').on('click', function() {
    $uploadBlock.find('.textMessage .contentResult').html("");
    $uploadBlock.find('.textMessage').hide();
    $uploadBlock.find('.fileUpload').show();
    $('input#fileID').val('');
  });

  // Principal investigator auto-complete
  addContactAutoComplete();
  
  
  // Disabled Auto save AJAX if click Save
  $('[name=save]').on('click', function(e) {
    // Cancel Auto Save
    autoSaveActive = false;
  });
  
  $(".button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
  });
  
// Is this deliverable Open Access
  $(".isRegional .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    if(!valueSelected) {
      $(".regionsBox").hide("slow");
    } else {
      $(".regionsBox").show("slow");
    }
  });
}

/**
 * Check Agreement status
 * 
 * @param {number} typeID - Funding budget type
 */
function checkAgreementStatus(typeID){
  var W1W2 = 1;
  var ON_GOING = 2;
  // Change Agreement Status when is (W1W2 Type => 1)
  var $agreementStatus = $('select.agreementStatus');
  // 3 => Concept Note/Pipeline
  // 4 => Informally Confirmed
  var $options = $agreementStatus.find("option[value='3'], option[value='4']");
  if(typeID == W1W2){
    $agreementStatus.val(ON_GOING); // On-going
    $options.remove();
  }else{
    if($options.length==0){
      $agreementStatus.addOption("3", "Concept Note/Pipeline");
      $agreementStatus.addOption("4", "Informally Confirmed");
    }
  }
  $agreementStatus.select2("destroy");
  $agreementStatus.select2();
}

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
          q: request.term
        },
        success: function(data) {
          response(data.users);
        }
    });
  }

  function selectUser(event,ui) {
    $("input.contactName").val(ui.item.name);
    $("input.contactEmail").val(ui.item.email);
    return false;
  }

  function renderItem(ul,item) {
    return $("<li>").append("<div>" + escapeHtml(item.composedName) + "</div>").appendTo(ul);
  }

  $("input.contactName").autocomplete(autocompleteOptions).autocomplete("instance")._renderItem = renderItem;
  $("input.contactEmail").autocomplete(autocompleteOptions).autocomplete("instance")._renderItem = renderItem;
}

// Add a new lead partner element
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

function removeLeadPartner() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.leadPartners');
  var value = $item.find(".fId").val();
  var name = $item.find(".name").attr("title");

  var $select = $(".institution");
  $item.hide(300, function() {
    $item.remove();
    checkLeadPartnerItems($list);
    updateLeadPartner($list);
  });
  // Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function updateLeadPartner($list) {
  // Hide All divisions block
  $('.divisionBlock').hide();

  $($list).find('.leadPartners').each(function(i,e) {
    // Show division block
    var institutionID = $(e).find('.fId').val();
    $('.division-'+institutionID).show();
    // Set funding sources indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkLeadPartnerItems(block) {
  var items = $(block).find('.leadPartners').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

/** COUNTRIES SELECT FUNCTIONS * */
// Add a new country element
function addCountry(option) {
  var canAdd = true;
  console.log(option.val());
  if(option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(option).parents(".select").parents("#countryList").find(".list");
  var $item = $("#countryTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 12 ? $(option).text().substr(0, 12) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.country').each(function(i,e) {
    if($(e).find('input.cId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set country parameters
  $item.find(".name").attr("title", $(option).text());
  var $state =
    $('<span> <i class="flag-sm flag-sm-' + option.val() + '"></i>  ' + v + '</span>');
  $item.find(".name").html($state);
  $item.find(".cId").val(option.val());
  $item.find(".id").val(-1);
  $list.append($item);
  $item.show('slow');
  updateCountryList($list);
  checkCountryList($list);

  // Reset select
  $(option).val("-1");
  $(option).trigger('change.select2');

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
console.log(option.val());
if(option.val() == "-1") {
 canAdd = false;
}

var $list = $(option).parents(".select").parents("#regionList").find(".list");
var $item = $("#regionTemplate").clone(true).removeAttr("id");
var v = $(option).text().length > 20 ? $(option).text().substr(0, 20) + ' ... ' : $(option).text();

// Check if is already selected
$list.find('.region').each(function(i,e) {
 if($(e).find('input.rId').val() == option.val()) {
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
$item.find(".rId").val(option.val());
$item.find(".id").val(-1);
$list.append($item);
$item.show('slow');
updateRegionList($list);
checkRegionList($list);

// Reset select
$(option).val("-1");
$(option).trigger('change.select2');

}

function removeRegion() {
var $list = $(this).parents('.list');
var $item = $(this).parents('.region');
var value = $item.find(".rId").val();
var name = $item.find(".name").attr("title");

var $select = $(".regionsSelect");
$item.hide(300, function() {
 $item.remove();
 checkRegionList($list);
 updateRegionList($list);
});
// Add region option again
$select.addOption(value, name);
$select.trigger("change.select2");
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

function date(start,end) {
  var dateFormat = "yy-mm-dd";
  var from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: MIN_DATE,
      maxDate: $(end).val() || MAX_DATE,
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1);
        if (budgetsConflicts(from.val().split('-')[0], inst.selectedYear - 1)){
          $(this).datepicker("hide");
          return
        }
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(end).datepicker("option", "minDate", selectedDate);
        }
        getYears();
      }
  }).on("change", function() {
    getYears();
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
      getYears();
    }
  });

  var to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: $(start).val() || MIN_DATE,
      maxDate: MAX_DATE,
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        if (budgetsConflicts(inst.selectedYear + 1, to.val().split('-')[0])){
          $(this).datepicker("hide");
          return
        }
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(start).datepicker("option", "maxDate", selectedDate);
        }
        getYears();
      }
  }).on("change", function() {
    getYears();
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
      getYears();
    }
  });

  // Activate tab default
  if(!$('.budgetByYears .nav-tabs li.active').exists()) {
    $('.budgetByYears .nav-tabs li').last().addClass('active');
    $('.budgetByYears .tab-content .tab-pane').last().addClass('active');
  }

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
      var message = "Date cannot be changed as this funding source has at least one budget allocation in <b>" + yearConflicts.join(', ') +"</b>";
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = message;
      notyOptions.animation = {
        open: 'animated bounceInLeft', // Animate.css class names
        close: 'animated bounceOutLeft', // Animate.css class names
        easing: 'swing', // unavailable - no need
        speed: 500 // unavailable - no need
      };
      $('.dateErrorBox').noty(notyOptions);
      
      return true;
    }
    return false;
  }

  function getYears() {
    var startYear = (from.val().split('-')[0]) || currentCycleYear;
    var endYear = (to.val().split('-')[0]) || startYear;
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
        // Build Content
        var content = '<div class="tab-pane col-md-4 going" id="fundingYear-' + startYear + '">';
        content += '<label for="">Budget for ' + startYear + ':</label>';
        content += '<input type="hidden" name="fundingSource.budgets[-1].year" value="' + startYear + '">';
        content +=
            '<input type="text" name="fundingSource.budgets[-1].budget" class="currencyInput form-control input-sm col-md-4" />';
        content += '</div>';

        var $content = $(content);
        // Set indexes
        $content.setNameIndexes(1, index);
        // Append Content
        $('.budgetByYears .tab-content').append($content);

        // Set currency format
        $content.find('input.currencyInput').currencyInput();
      }else{
        // Set indexes
        $('#fundingYear-' + startYear).setNameIndexes(1, index);
        $('#fundingYear-' + startYear).addClass('going')
      }

      index++;
      years.push(startYear++);
    }
    
    // Clear unused content names
    $('.budgetByYears .tab-content .tab-pane').not('.going').each(function(i,content){
      $(content).setNameIndexes(1, index+i);
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

function addDataTable() {
  $('table').dataTable({
      "bPaginate": false, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": true, // This option enables the auto adjust columns width
      "iDisplayLength": 100,// Number of rows to show on the table
      "language": {
        "emptyTable": "No projects adopting this funding source."
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

function ajaxService(url,data) {
  var $select = $(".donor");
  $.ajax({
      url: url,
      type: 'GET',
      data: data,
      success: function(m) {
        $select.empty();
        $select.addOption("-1", "Select an option...");
        $.each(m.institutions, function(i,e) {
          $select.addOption(e.id, e.name);
        });
        changeDonorByFundingType(data.budgetTypeID, $select)
        
      },
      error: function(e) {
        console.log(e);
      },
      complete: function() {
        $select.trigger("change.select2");
        console.log(data);
      }
  });
}

function changeDonorByFundingType(budgetType,$select) {
  var donorId = $select.find("option:selected").val();
  if(donorId == "-1" && budgetType == "1") {
    $select.val($(".cgiarConsortium").text()).trigger("change");
  }
}

function formatState(state) {
  if(!state.id) {
    return state.text;
  }
  var $state="";
  if(state.element.value!="-1"){
   $state =
      $('<span> <i class="flag-sm flag-sm-' + state.element.value.toUpperCase() + '"></i>  ' + state.text + '</span>');
  }else{
    $state =
      $('<span>' + state.text + '</span>');
  }
  return $state;
};