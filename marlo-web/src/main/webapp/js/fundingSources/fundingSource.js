$(document).ready(init);

function init() {

  // Popup
  popups();

  // Add Data Table
  addDataTable();
  
  
  // Harvest metadata from URL
  $("#fillMetadata .checkButton, #fillMetadata .updateButton").on("click", syncMetadata);

  // Unsync metadata
  $("#fillMetadata .uncheckButton").on("click", unSyncDeliverable);

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

  // Setting Currency Inputs
  $('.currencyInput').currencyInput();
  date("form #fundingSource\\.startDate", "form #fundingSource\\.endDate");

  
  // Agreement status & Donor
  $('form select').select2({
    width: "100%"
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
  
  // $(".donor").select2(searchInstitutionsOptions(true));
  // $(".donor").parent().find("span.select2-selection__placeholder").text(placeholderText);

  $(".removeLeadPartner").on("click", removeLeadPartner);

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
}

/**
 * Harvest metadata functions
 */

function syncMetadata() {
  getOCSMetadata();
}

function setMetadata(data) {
  console.log(data);

  // Text area & Inputs fields
  $.each(data, function(key,value) {
    var $parent = $('.metadataElement-' + key);
    var $input = $parent.find(".metadataValue");
    var $hide = $parent.find('.hide');
    if(value) {
      $input.val(value);
      $parent.find('textarea').autoGrow();
      $input.attr('readOnly', true);
      $hide.val("true");
    } else {
      $input.attr('readOnly', false);
      $hide.val("false");
    }
    
    // Date picker
    if($input.hasClass('hasDatepicker')){
      console.log(key+" in date");
      $input.datepicker( "hide" );
      $input.datepicker( "refresh" );
    }
  });

  syncDeliverable();

}

function syncDeliverable() {
  // Hide Sync Button & dissemination channel
  $('#fillMetadata .checkButton, .disseminationChannelBlock').hide('slow');
  // Show UnSync & Update Button
  $('#fillMetadata .unSyncBlock').show();
  // Set hidden inputs
  $('#fillMetadata input:hidden').val(true);
  // Dissemination URL
  $('.financeCode').attr('readOnly', true);
  // Update component
  $(document).trigger('updateComponent');
}

function unSyncDeliverable() {
  // Show metadata
  $('[class*="metadataElement"]').each(function(i,e) {
    var $parent = $(e);
    var $input = $parent.find('.metadataValue');
    var $hide = $parent.find('.hide');
    $input.attr('readOnly', false);
    $hide.val("false");
    
  });

  // Show Sync Button & dissemination channel
  $('#fillMetadata .checkButton, .disseminationChannelBlock').show('slow');
  // Hide UnSync & Update Button
  $('#fillMetadata .unSyncBlock').hide();
  // Set hidden inputs
  $('#fillMetadata input:hidden').val(false);
  // Dissemination URL
  $('.financeCode').attr('readOnly', false);

  // Update component
  $(document).trigger('updateComponent');
}

function getOCSMetadata(){
  // get data from url
  // Ajax to service
  $.ajax({
      'url': baseURL + '/',
      'data': {},
      beforeSend: function() {
        $(".financeCode").addClass('input-loading');
      },
      success: function(data) {
          
        // Setting Metadata
        setMetadata(agreementData);

      },
      complete: function() {
        $(".financeCode").removeClass('input-loading');
      },
      error: function() {
        console.log("error");
        $('#metadata-output').empty().append("Invalid URL for searching metadata");
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



var agreementData =  {
    "id": "A128",
    "description":"Effecting change in  seed security response: In crisis, chronic stress and developmental contexts",
    "donor": {
      "id": "444426081",
      "name": "USAID-United States Agency for International Development"
    },
    "countries": [
      {"code": "CG", "description": "Congo", "percentage": "25"},
      {"code": "MG", "description": "Madagascar", "percentage": "25"},
      {"code": "TP", "description": "East Timor", "percentage": "25"},
      {"code": "ZM", "description": "Zambia", "percentage": "25"}
    ],
    "crps": [
      {"id": "35", "name": "11 GLDC - GRAIN LEGUMES AND DRY LAND CEREALS", "percentage": "100"}
    ],
    "researcher": {
      "id": "06230",
      "name": "BURUCHARA , ROBIN ARANI"
    },
    "shortTitle": "",
    "objectives": "THIS PROJECT FOCUSES ON &NBSP;TOOL DEVELOPMENT AND CAPACITY-BUILDING IN SEED SYSTEM SECURITY ASSESSMENT (SSSA). &NBSP;SUCH RESEARCH SKILLS ARE CRITICAL FOR DESIGNING&NBSP; IMMEDIATE RESPONSE AND LONGER-TERM PROGRAMS WHICH &NBSP;SUPPORT FARMERS DURING PERIODS OF &NBSP;ACUTE (DISASTER) AND CHRONIC STRESS. THE SSSA IS THE FIRST TOOL IN THE WORLD TO SPECIFICALLY DISTINGUISH BETWEEN SEED SECURITY ISSUES AND FOOD SECURITY ISSUES, AND PUTS AGRICULTURAL THEMES AT THE HEART OF DISASTER RECOVERY.",
    "grantAmount": "831091.00",
    "startDate": "4/1/2012",
    "endDate": "3/31/2014",
    "extensionDate": "6/30/2016",
    "contractStatus": "C",
    "fundingType": "BLR",
    "plas": [
      {
        "id": "C-032-15",
        "description": "AGREEMENT BETWEEN INTERNATIONAL CENTRE FOR TROPICAL AGRICULTURE AND CATHOLIC RELIEF SERVICES,GUINEA",
        "partners": [ {"id": "GN000472U", "name": "CATHOLIC RELIEF SERVICES-GUINEA"} ],
        "countries": [{"code": "GN", "description": "Guinea", "percentage": "100"}]
      }
    ]
  }



