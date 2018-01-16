$(document).ready(init);
var countID = 0;

function init() {
  /* Init Select2 plugin */
  $('form select').select2({
    width: "100%"
  });

  datePickerConfig({
      "startDate": "#deliverable\\.startDate",
      "endDate": "#deliverable\\.endDate"
  });

  // Events
  $(".addDocument").on("click", addDocument);
  $(".removeDocument").on("click", removeDocument);
  $(".link").on("keyup", checkUrl);

  $(".outputSelect").on("change", addOutput);
  $(".removeOutput").on("click", removeOutput);

  // Change deliverable type
  $(".typeSelect").on("change", changeDeliverableType);
}

/** FUNCTIONS documents * */

function checkUrl() {
  var input = $(this);
  var inputData = $.trim(input.val());
  var uri = new Uri(inputData);
  $(input).removeClass("fieldError");
  if(inputData != "") {
    if(uri.protocol() == "http" || uri.protocol() == "https") {
      $(input).removeClass("fieldError");
    } else {
      $(input).addClass("fieldError");
    }
  }
}

// Add a new document element
function addDocument() {
  var $list = $(".documentList");
  var $item = $("#document-template").clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  checkItems($list);
  updateDocument();

}

// Remove document element
function removeDocument() {
  var $list = $(this).parents('.documentList');
  var $item = $(this).parents('.documents');
  $item.hide(1000, function() {
    $item.remove();
    checkItems($list);
    updateDocument();
  });

}

function updateDocument() {
  $(".documentList").find('.documents').each(function(i,e) {
    // Set indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkItems(block) {
  var items = $(block).find('.documents').length;
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
          outputID: option.val(),
          phaseID: phaseID
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
  date($(element.startDate), $(element.endDate));
}

function date(start,end) {
  var dateFormat = "yy-mm-dd";
  var from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2010-01-01',
      maxDate: '2030-12-31',
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
      minDate: '2010-01-01',
      maxDate: '2030-12-31',
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

function changeDeliverableType() {
  var typeID = $(this).val()
  
  if (typeID == -1){
    return
  }
  
  $.ajax({
      url: baseURL + '/centerDeliverableSubType.do',
      data: {
        deliverableTypeId: typeID,
        phaseID: phaseID
      },
      beforeSend: function() {
        $(".loading.subtype").fadeIn();
        $("select.subTypeSelect").empty();
        $("select.subTypeSelect").addOption("-1", "Select a sub type...");
      },
      success: function(data) {
        $.each(data.deliverableSubTypes, function(i,type) {
          $("select.subTypeSelect").addOption(type.id, type.name);
        });
      },
      complete: function() {
        $(".loading.subtype").fadeOut();
       // $("select.subTypeSelect").val("-1");
        $("select.subTypeSelect").select2();
        // $("select.subTypeSelect").trigger("select2.change");
      }
  });

}
