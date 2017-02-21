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

  // Setting Currency Inputs
  $('.currencyInput').currencyInput();
  date("form #fundingSource\\.startDate", "form #fundingSource\\.endDate");
  $('form select').select2({
    width: "100%"
  });

  $(".removeLeadPartner").on("click", removeLeadPartner);

  // When select center as Funding Window----------
  var lastDonor = -1;
  $(".type").on("change", function() {
    var option = $(this).find("option:selected");
    var url = baseURL + "/institutionsByBudgetType.do";
    var data = {
      budgetTypeID: option.val()
    };
    ajaxService(url, data);
    /*
     * var institutionSelect = $(".donor"); var institutionSelected = $(".institution").find("option:selected").val();
     * console.log(institutionSelected); // If the option selected is center if(option.val() == 4) {
     * if(institutionSelect.val() != "-1") { lastDonor = institutionSelect.val(); } institutionSelect.attr("disabled",
     * "disabled"); institutionSelect.val(institutionSelected); institutionSelect.trigger('change.select2');
     * $(".note").hide("slow"); } else { $(".note").show("slow"); if(institutionSelect.attr("disabled") == "disabled") {
     * institutionSelect.removeAttr("disabled"); institutionSelect.val(lastDonor);
     * institutionSelect.trigger('change.select2'); } }
     */
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
  console.log(name + "-" + value);
  var $select = $(".institution");
  $item.hide(1000, function() {
    $item.remove();
    checkLeadPartnerItems($list);
    updateLeadPartner($list);
  });
// Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function updateLeadPartner($list) {
  $($list).find('.leadPartners').each(function(i,e) {
    // Set funding sources indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkLeadPartnerItems(block) {
  console.log(block);
  var items = $(block).find('.leadPartners').length;
  console.log(items);
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
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1)
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
      $(this).datepicker('setDate', $(this).datepicker("option", "minDate"));
    }
  });

  var to = $(end).datepicker({
      dateFormat: dateFormat,
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0)
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
      $(this).datepicker('setDate', $(this).datepicker("option", "maxDate"));
    }
  });

  function getYears() {
    var endYear = (new Date(to.val())).getFullYear();
    var years = [];
    var startYear = (new Date(from.val())).getFullYear() || 2015;

    $('.budgetByYears .nav-tabs').empty();
    $('.budgetByYears .tab-content').empty();

    var index = 0;
    while(startYear <= endYear) {
      var state = '';
      if(currentCycleYear == startYear) {
        state = 'active';
      }

      var tab = '<li class="' + state + '">';
      tab += '<a href="#fundingYear-' + startYear + '" data-toggle="tab">' + startYear + '</a>';
      tab += '</li>';
      $('.budgetByYears .nav-tabs').append(tab);

      var content = '<div class="tab-pane col-md-4 ' + state + '" id="fundingYear-' + startYear + '">';
      content += '<label for="">Budget for ' + startYear + ':</label>';
      content += '<input type="hidden" name="fundingSource.budgets[' + index + '].year" value="' + startYear + '">';
      content +=
          '<input type="text" name="fundingSource.budgets[' + index
              + '].budget" class="currencyInput form-control input-sm col-md-4" />';
      content += '</div>';
      $('.budgetByYears .tab-content').append(content);

      index++;
      years.push(startYear++);
    }

    if(years.indexOf(parseInt(currentCycleYear)) == -1) {
      $('.budgetByYears .nav-tabs li').last().addClass('active');
      $('.budgetByYears .tab-content .tab-pane').last().addClass('active');
    }

    // Set currency format
    $('.currencyInput').currencyInput();

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
        $select.trigger("change.select2");
      },
      error: function(e) {
        console.log(e);
      }
  });
}
