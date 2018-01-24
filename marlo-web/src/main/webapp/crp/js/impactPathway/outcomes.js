$(document).ready(init);
var currentSubIdo;
var saveObj;

function init() {

  /* Declaring Events */
  attachEvents();

  /* Init Select2 plugin */
  $('.outcomes-list select').select2();

  /* Numeric Inputs */
  $('input.targetValue , input.targetYear').numericInput();

  /* Percentage Inputs */
  $('.outcomes-list input.contribution').percentageInput();

}

function attachEvents() {
  validateDecimalsContributions();

  // Change a target unit
  $('select.targetUnit').on('change', function() {
    var valueId = $(this).val();
    var $targetValue = $(this).parents('.target-block').find('.targetValue-block');
    if(valueId != "-1") {
      $targetValue.show('slow');
    } else {
      $targetValue.hide('slow');
    }

  });

  // Add an Outcome
  $('.addOutcome').on('click', addOutcome);
  // Remove an Outcome
  $('.removeOutcome').on('click', removeOutcome);

  // Add a Milestone
  $('.addMilestone').on('click', addMilestone);
  // Remove a Milestone
  $('.removeMilestone').on('click', removeMilestone);

  $('input.outcomeYear, input.milestoneYear').on('keyup', function() {
    var $target = $(this);
    var targetVal = parseInt($target.val());
    var $milestonesYearInputs = $(this).parents('.outcome').find('.milestones-list input.targetYear');

    $target.removeClass('fieldError');

    if($target.hasClass('milestoneYear')) {
      var outcomeYearVal = parseInt($(this).parents('.outcome').find('input.outcomeYear').val()) || 0;
      if(targetVal > outcomeYearVal) {
        $target.addClass('fieldError');
      }
    } else {
      $milestonesYearInputs.each(function(i,input) {
        $(input).removeClass('fieldError');
        if(parseInt($(input).val()) > targetVal) {
          $(input).addClass('fieldError');
        }
      });
    }
  });
  $('input.outcomeYear, input.milestoneYear').trigger('keyup');

  // Add a Sub IDO
  $('.addSubIdo').on('click', addSubIdo);
  // Remove a Sub IDO
  $('.removeSubIdo').on('click', removeSubIdo);

  // Change contribution percentage
  $('input.contribution').on('keyup', function() {
    var $text = $(this).parents('.outcome').find('p.contributioRem');
    var $contributions = $(this).parents('.subIdos-list').find('input.contribution');
    updateTotalContribution($contributions, $text);
  });
  $('input.contribution').trigger('keyup');

  // Add an assumption
  $('.addAssumption').on('click', addAssumption);
  // Remove assumption
  $('.removeAssumption').on('click', removeAssumption);

  // Add an baseline indicator
  $('.addBaselineIndicator').on('click', addBaselineIndicator);
  // Remove baseline indicator
  $('.removeBaselineIndicator').on('click', removeBaselineIndicator);

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    // e.target // newly activated tab
    // e.relatedTarget // previous active tab
    var $parent = $(e.target).parents('.outcome');
    var $selects = $parent.find('select');
    var $textAreas = $parent.find('textarea');
    $selects.select2({
      width: '100%'
    });
    $textAreas.autoGrow();
  })

  // PopUp Select SubIdos (Graphic)
  $(".selectSubIDO").on("click", function() {
    currentSubIdo = $(this).parents(".subIdo");
    $("#subIDOs-graphic").dialog({
        autoOpen: false,
        resizable: false,
        closeText: "",
        width: '85%',
        modal: true,
        height: $(window).height() * 0.90,
        show: {
            effect: "blind",
            duration: 500
        },
        hide: {
            effect: "fadeOut",
            duration: 500
        }
    });
    $("#subIDOs-graphic").dialog("open");
  });

  // Filter SubIDOs
  $("#filterForm").on("change", filter);
  // Select a subIdo
  $(".subIDO").on("click", function() {
    var canAdd = true;
    // less text
    var $divSubIdo = currentSubIdo.find(".subIdoSelected");
    var $subIdosList = currentSubIdo.parents(".subIdos-list");
    var v = $(this).text().length > 65 ? $(this).text().substr(0, 65) + ' ... ' : $(this).text();

    $divSubIdo.text(v);
    $divSubIdo.attr("title", $(this).text()).tooltip();
    var $inputSubIdo = currentSubIdo.find("input.subIdoId");
    var value = $(this).attr("id").split('-');

    // Check if the sub ido is already selected
    $subIdosList.find('.subIdo').each(function(i,e) {
      if($(e).find("input.subIdoId").val() == value[value.length - 1]) {
        canAdd = false;
        return;
      }
    });

    if(!canAdd) {
      console.log($(this).animateCss('jello'));
      return;
    }

    $inputSubIdo.val(value[value.length - 1]);
    $("#subIDOs-graphic").dialog("close");
    // Update component
    $(document).trigger('updateComponent');
  });
}

function validateDecimalsContributions() {
  $('form input.contribution').each(function(i,e) {
    if(($(e).val() % 1) == 0) {
      $(e).val(parseInt($(e).val() || 0));
    }
  });
}

/**
 * Outcome Functions
 */

function addOutcome() {
  var $list = $('.outcomes-list');
  var $item = $('#outcome-template').clone(true).removeAttr("id");
  // $item.find('select').select2({
  // width: '100%'
  // });
  $list.append($item);
  updateAllIndexes();
  $item.show('slow');
}

function removeOutcome() {
  var $list = $(this).parents('.outcomes-list');
  var $item = $(this).parents('.outcome');
  $item.hide(function() {
    $item.remove();
    updateAllIndexes();
  });
}

/**
 * Milestone Functions
 */

function addMilestone() {
  var $list = $(this).parents('.outcome').find('.milestones-list');
  var $item = $('#milestone-template').clone(true).removeAttr("id");
  // $item.find('select').select2({
  // width: '100%'
  // });
  $list.append($item);
  updateAllIndexes();
  $item.show('slow');
  // Hide empty message
  $(this).parents('.outcome').find('.milestones-list p.message').hide();
}

function removeMilestone() {
  var $list = $(this).parents('.outcome').find('.milestones-list');
  var $item = $(this).parents('.milestone');
  $item.hide(function() {
    $item.remove();
    updateAllIndexes();
  });
}

/**
 * SUB-IDOs Functions
 */

function addSubIdo() {
  var $list = $(this).parents('.outcome').find('.subIdos-list');
  var $item = $('#subIdo-template').clone(true).removeAttr("id");
  // $item.find('select').select2({
  // width: '100%'
  // });
  $item.find('input.contribution').percentageInput();
  $list.append($item);
  updateAllIndexes();
  $item.show('slow');
  // Hide empty message
  $(this).parents('.outcome').find('.subIdos-list p.message').hide();
}

function removeSubIdo() {
  var $parent = $(this).parents('.outcome');
  var $list = $parent.find('.subIdos-list');
  var $item = $(this).parents('.subIdo');
  $item.hide(function() {
    $item.remove();
    updateAllIndexes();
    $parent.find('p.contributioRem span.value').text('0%');
    $('input.contribution').trigger('keyup');
  });
}

function updateTotalContribution(list,text) {
  // calculated total
  var total = 0;
  $(list).each(function(i,item) {
    var itemVal = parseFloat(removePercentageFormat(($(item).val()) || '0'));
    total += (itemVal > 100) ? 100 : itemVal;
  });

  // Removing classes
  $(text).removeClass('fieldError fieldChecked');
  $(list).removeClass('fieldError');

  // Set percentage and classes
  $(text).find('.value').text(setPercentageFormat(total));
  if(total > 100) {
    $(text).addClass('fieldError');
    $(list).addClass('fieldError');
  } else if(total == 100) {
    $(text).addClass('fieldChecked');
  }
}

// Filter by CrossCutting
function filter() {
  var $checkBox = $(this).find(":checked");
  if($checkBox.length == 2) {
    $checkBox.each(function(i,item) {
      $(".ido").css("display", "inline-block");
      $(".crossCutting").css("display", "inline-block");
      $(".graphic-container").css("width", "2000px");
      $(".crossCutting").css("margin", "5px 8px");
    });
  } else {
    if($checkBox.val() == "IDO") {
      $(".ido").css("display", "inline-block");
      $(".crossCutting").css("display", "none");
      $(".graphic-container").css("width", "1420px");
    } else if($checkBox.val() == "CCIDO") {
      $(".ido").css("display", "none");
      $(".crossCutting").css("display", "inline-block");
      $(".crossCutting").css("margin", "5px 0 5px 12%");
      $(".graphic-container").css("width", "1000px");
    } else {
      $(".ido").css("display", "none");
      $(".crossCutting").css("display", "none");
    }
  }
}

/**
 * Assumptions Functions
 */

function addAssumption() {
  var $assumptionsList = $(this).parents('.subIdo').find('.assumptions-list');
  var $item = $('#assumption-template').clone(true).removeAttr("id");
  $assumptionsList.append($item);
  updateAllIndexes();
  // Hide empty message
  $(this).parents('.subIdo').find('.assumptions-list p.message').hide();
  $item.show('slow');

}

function removeAssumption() {
  var $assumptionsList = $(this).parents('.subIdo').find('.assumptions-list');
  var $item = $(this).parents('.assumption');
  $item.hide(function() {
    $item.remove();
    updateAllIndexes();
  });
}

/**
 * Baseline Indicator Functions
 */

function addBaselineIndicator() {
  var $list = $(this).parents('.outcome').find('.baselineIndicators-list');
  var $item = $('#baselineIndicator-template').clone(true).removeAttr("id");
  $list.append($item);
  updateAllIndexes();
  // Hide empty message
  $(this).parents('.outcome').find('.baselineIndicators-list p.message').hide();
  $item.show('slow');

}

function removeBaselineIndicator() {
  var $item = $(this).parents('.baselineIndicator');
  $item.hide(function() {
    $item.remove();
    updateAllIndexes();
  });
}

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
        $ub.find('input.outcomeID').val(r.outcomeID);
      }
    },
    progressall: function(e,data) {
      var progress = parseInt(data.loaded / data.total * 100, 10);
    }
});

// Prepare data
$fileUpload.bind('fileuploadsubmit', function(e,data) {
  var outcomeID = $(e.target).parents('.outcome').find('.outcomeId').val();
  data.formData = {
    outcomeID: outcomeID
  };
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

/**
 * General Function
 */

function updateAllIndexes() {
  // All Outcomes List
  $('.outcomes-list').find('.outcome').each(function(i,outcome) {
    $(outcome).find('span.index').html(i + 1);
    $(outcome).setNameIndexes(1, i);

    // Update Milestones
    $(outcome).find('.milestone').each(function(i,milestone) {
      $(milestone).find('span.index').text(i + 1);
      $(milestone).setNameIndexes(2, i);
    });

    // Update SubIdos
    $(outcome).find('.subIdo').each(function(i,subIdo) {
      $(subIdo).find('span.index').text(i + 1);
      $(subIdo).setNameIndexes(2, i);

      // Update Assumptions
      $(subIdo).find('.assumption').each(function(i,assumption) {
        $(assumption).find('.statement').attr('placeholder', 'Assumption statement #' + (i + 1));
        $(assumption).setNameIndexes(3, i);
      });
    });

    // Update Baseline Indicators
    $(outcome).find('.baselineIndicator').each(function(i,indicator) {
      $(indicator).find('span.index').text(i + 1);
      $(indicator).setNameIndexes(2, i);
    });
  });

  // Update component event
  $(document).trigger('updateComponent');

}