$(document).ready(init);
var countID = 0;

function init() {
  checkOutputsToRemove();
  /* Init Select2 plugin */
  $('form select').select2({
    width: "100%"
  });

  $('.amount').currencyInput();

  datePickerConfig({
      "startDate": "#project\\.startDate",
      "endDate": "#project\\.endDate",
      "extensionDate": "#project\\.extensionDate"
  });

  /** Check region option * */
  $("#regionList").find(".region").each(function(i,e) {
    var option = $(".regionSelect").find("option[value='" + $(e).find("input.rId").val() + "']");
    option.prop('disabled', true);
    // option.hide();
  });

  // Events
  $(".addFundingSource").on("click", addFundingSource);
  $(".removeFundingSource").on("click", removeFundingSource);

  $(".outputSelect").on("change", addOutput);
  $(".removeOutput").on("click", removeOutput);

  // Request Outputs popup
  $('#requestModal').on('show.bs.modal', function(event) {
    $.noty.closeAll();

    var $modal = $(this);
    // Show Form & button
    $modal.find('form, .requestButton').show();
    $modal.find('.messageBlock').hide();

    $modal.find('select.countriesRequest').val(null).trigger('select2:change');
    $modal.find('select.countriesRequest').trigger('change');

  });
  $('#requestModal button.requestButton').on('click', function() {
    var $modal = $(this).parents('.modal');
    var outcomeID = $modal.find('select#outcomeID').val();
    
    if(outcomeID == -1){
      return
    }
    
    $.ajax({
        url: baseURL + '/outputRequest.do',
        data: $('#requestModal form').serialize(),
        beforeSend: function(data) {
          $modal.find('.loading').fadeIn();
        },
        success: function(data) {
          console.log(data);
          if(data.messageSent) {
            // Hide Form & button
            $modal.find('form, .requestButton').hide();
            $modal.find('.messageBlock').show();
          }
        },
        complete: function() {
          $modal.find('.loading').fadeOut();
        }
    });
  });

  // Country item
  $(".countriesSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      var countryISO = $(option).val();
      var countryName = $(option).text();
      
      // Add Country
      addCountry(countryISO, countryName);
      
      // Reset select
      option.val("-1");
      option.trigger('change.select2');
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });
  $(".removeCountry").on("click", removeCountry);

  // REGION item
  $(".regionSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addRegion(option);
      option.prop('disabled', true);
      $('.regionSelect').select2();
    }
  });
  $(".removeRegion").on("click", removeRegion);

  /* Select2 multiple for country and region select */
  $('.countriesSelect').select2({
      placeholder: "Select a country(ies)...",
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });

  $(".button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    if($(this).hasClass("radio-checked")) {
      $(this).removeClass("radio-checked")
      $input.val("");
    } else {
      $input.val(valueSelected);
      $(this).parent().find("label").removeClass("radio-checked");
      $(this).addClass("radio-checked");
    }
  });
  
  // Is this project has a global dimension
  $(".isGlobal .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var isChecekd = $(this).hasClass('radio-checked');
    if(!valueSelected || !isChecekd) {
      // $(".countriesBox").show("slow");
    } else {
      // $(".countriesBox").hide("slow");
    }
  });

  // Is this project has a regional dimension
  $(".isRegional .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var isChecekd = $(this).hasClass('radio-checked');
    if(!valueSelected || !isChecekd) {
      $(".regionsBox").hide("slow");
    } else {

      $(".regionsBox").show("slow");
    }
  });

  $('.blockTitle').on('click', function() {
    if($(this).hasClass('closed')) {
      $(this).parent().find('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
      $(this).find(".errorTag").hide();
      $(this).find(".errorTag").css("left", $(this).find(".deliverableWrapper").outerWidth());
      $(this).find(".errorTag").fadeIn(2000);
    });
  });
  
  
  
  // Cross cutting checkbox
  $('.crossCutting input.optionable').on('click', function(){
    var checkedOptions = $('.crossCutting input.optionable:checked').length;
    console.log(checkedOptions);
    if (checkedOptions > 0){
      $('.crossCutting input.na').prop('checked', false);
    }else{
      $('.crossCutting input.na').prop('checked', true);
    }
  })
  
  $('.crossCutting input.na').on('click', function(){
    $('.crossCutting input.optionable').prop('checked', false);
  });
  
}

/** FUNCTIONS Funding Sources * */

/** COUNTRIES SELECT FUNCTIONS * */
// Add a new country element
function addCountry(countryISO, countryName) {
  var canAdd = true; 
  
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
  var v = $(option).text().length > 16 ? $(option).text().substr(0, 16) + ' ... ' : $(option).text();

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
  $item.find(".name").html(v);
  $item.find(".rId").val(optionValue);
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
  var name = $item.find(".name").attr("title");

  var $select = $(".regionSelect");
  $item.hide(300, function() {
    $item.remove();
    checkRegionList($list);
    updateRegionList($list);
  });
  var option = $select.find("option[value='" + value + "']");
  console.log(option);
  option.prop('disabled', false);
  $('.regionSelect').select2();
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

// Add a new funding source element
function addFundingSource() {
  var $list = $(".fundingSourceList");
  var $item = $("#fundingSource-template").clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow', function() {
    $item.find("select").select2({
      width: "100%"
    });
  });
  checkItems($list);
  updateFundingSource();

}

// Remove Funding source element
function removeFundingSource() {
  var $list = $(this).parents('.fundingSourceList');
  var $item = $(this).parents('.fundingSources');
  $item.hide(1000, function() {
    $item.remove();
    checkItems($list);
    updateFundingSource();
  });

}

function updateFundingSource() {
  $(".fundingSourceList").find('.fundingSources').each(function(i,e) {
    // Set indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkItems(block) {
  var items = $(block).find('.fundingSources').length;
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}

/** FUNCTIONS Outputs* */

// Add a new funding source element
function addOutput() {
  var $select = $(this);
  var option = $select.find("option:selected");
  if(option.val() != "-1") {
    $.ajax({
        url: baseURL + "/outputInfo.do",
        type: 'GET',
        data: {
          outputID: option.val()
        },
        success: function(m) {
          console.log(m);
          var $list = $(".outputList");
          var $item = $("#output-template").clone(true).removeAttr("id");
          $item.find("span.index").text("O" + m.outputInfo.id);
          $item.find("div.oStatement").text(option.text());
          $item.find("div.rTopic").text(m.outputInfo.topicName);
          $item.find("div.outcome").text(m.outputInfo.outcomeName);
          $item.find(".outputId").val(m.outputInfo.id);
          $list.append($item);
          $item.show('slow');
          updateOutputs();
          checkOutputItems($list);
          $select.find(option).remove();
          $select.val("-1").trigger("change");
        },
        error: function(e) {
          console.log(e);
        }
    });
  }

}

// Remove Funding source element
function removeOutput() {
  var $list = $(this).parents('.outputList');
  var $item = $(this).parents('.outputs');
  var $select = $(".outputSelect");
  $select.addOption($item.find("input.outputId").val(), $item.find("div.oStatement").text());
  $item.hide(1000, function() {
    $item.remove();
    checkOutputItems($list);
    updateOutputs();
  });

}

function updateOutputs() {
  $(".outputList").find('.outputs').each(function(i,e) {
    // Set indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkOutputItems(block) {
  var items = $(block).find('.outputs').length;
  if(items == 0) {
    $(block).find('p.outputInf').fadeIn();
  } else {
    $(block).find('p.outputInf').fadeOut();
  }
}

function checkOutputsToRemove() {
  $(".outputList").find(".outputs").each(function(i,e) {
    var option = $(".outputSelect").find("option[value='" + $(e).find(".outputId").val() + "']");
    option.remove();
  });
}

/**
 * Attach to the date fields the datepicker plugin
 */
function datePickerConfig(element) {
  date($(element.startDate), $(element.endDate), $(element.extensionDate));
}

function date(start,end,extension) {
  var dateFormat = "yy-mm-dd";
  var from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2008-01-01',
      maxDate: '2019-12-31',
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
  });

  var to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2008-01-01',
      maxDate: '2019-12-31',
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
  });

  var to = $(extension).datepicker({
      dateFormat: dateFormat,
      minDate: '2008-01-01',
      maxDate: '2019-12-31',
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