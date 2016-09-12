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

  // PopUp Select SubIdos (Graphic)
  $(".selectSubIDO").on("click", function() {
    currentSubIdo = $(this).parents(".subIdo");
    $("#subIDOs-graphic").dialog({
        autoOpen: false,
        resizable: false,
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

  // New Target unit
  var $targetUnit = $("#dialog-targetUnit");
  $('.addOtherTargetUnit').on('click', function(e) {
    e.preventDefault();
    var $select = $(this).parent().find('select');
    $targetUnit.dialog({
        modal: true,
        buttons: [
          {
              text: "Add Target",
              click: function() {
                var targetUnitName = $.trim($('#targetUnitName').val());
                if(targetUnitName != "") {
                  $.ajax({
                      'url': baseURL + '/newTargetUnit.do',
                      data: {
                        targetUnitName: targetUnitName
                      },
                      success: function(data) {
                        if(data.newTargetUnit.status) {
                          $('select.targetUnit').each(function(i,select) {
                            $(select).addOption(data.newTargetUnit.id, data.newTargetUnit.name);
                          });
                        }
                        $select.val(data.newTargetUnit.id);
                      },
                      complete: function(data) {
                        $('#targetUnitName').val("");
                        $targetUnit.dialog("close");
                      }
                  });
                }
              }
          }
        ]
    });

  });

  // Filter SubIDOs
  $("#filterForm").on("change", filter);
  // Select a subIdo
  $(".subIDO").on("click", function() {
    // less text
    var $divSubIdo = currentSubIdo.find(".subIdoSelected");
    var v = $(this).text().length > 65 ? $(this).text().substr(0, 65) + ' ... ' : $(this).text();
    $divSubIdo.text(v);
    $divSubIdo.attr("title", $(this).text()).tooltip();
    var $inputSubIdo = currentSubIdo.find("input.subIdoId");
    var value = $(this).attr("id").split('-');
    $inputSubIdo.val(value[value.length - 1]);
    $("#subIDOs-graphic").dialog("close");
    // Update component
    $(document).trigger('updateComponent');
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
 * General Function
 */

function updateAllIndexes() {
  // All Outcomes List
  $('.outcomes-list').find('.outcome').each(function(i,outcome) {
    var outcomesName = 'outcomes' + '[' + i + '].';
    $(outcome).find('span.index').html(i + 1);
    $(outcome).find('.outcome-statement').attr('name', outcomesName + 'description');
    $(outcome).find('.targetValue').attr('name', outcomesName + 'value');
    $(outcome).find('.targetYear').attr('name', outcomesName + 'year');
    $(outcome).find('.targetUnit').attr('name', outcomesName + 'srfTargetUnit.id');
    $(outcome).find('.outcomeId').attr('name', outcomesName + 'id');

    // Update Milestones
    $(outcome).find('.milestone').each(function(i,milestone) {
      var milestoneName = outcomesName + 'milestones' + '[' + i + '].';
      $(milestone).find('span.index').text(i + 1);
      $(milestone).find('.milestone-statement').attr('name', milestoneName + 'title');
      $(milestone).find('.targetValue').attr('name', milestoneName + 'value');
      $(milestone).find('.targetYear').attr('name', milestoneName + 'year');
      $(milestone).find('.targetUnit').attr('name', milestoneName + 'srfTargetUnit.id');
      $(milestone).find('.mileStoneId').attr('name', milestoneName + 'id');
    });

    // Update SubIdos
    $(outcome).find('.subIdo').each(function(i,subIdo) {
      var subIdoName = outcomesName + 'subIdos' + '[' + i + '].';
      $(subIdo).find('span.index').text(i + 1);
      $(subIdo).find('.subIdoId').attr('name', subIdoName + 'srfSubIdo.id');
      $(subIdo).find('.idoId').attr('name', subIdoName + 'srfSubIdo.srfIdo.id');
      $(subIdo).find('.contribution').attr('name', subIdoName + 'contribution');
      $(subIdo).find('.programSubIDOId').attr('name', subIdoName + 'id');

      // Update Assumptions
      $(subIdo).find('.assumption').each(function(i,assumption) {
        var assumptionName = subIdoName + 'assumptions' + '[' + i + '].';
        $(assumption).find('.assumptionId').attr('name', assumptionName + 'id');
        $(assumption).find('.statement').attr('placeholder', 'Assumption statement #' + (i + 1));
        $(assumption).find('.statement').attr('name', assumptionName + 'description');
      });
    });
  });

  // Update component event
  $(document).trigger('updateComponent');

  // JUST FOR TESTING
  // setCurrentObject();
}

function setCurrentObject() {
  saveObj = {}

  // Outcomes
  var outcomesArray = []
  $('.outcomes-list').find('.outcome').each(function(i,outcome) {
    var outcomeObj = {
        description: $(outcome).find('.outcome-statement').val(),
        value: $(outcome).find('.targetValue').val(),
        year: $(outcome).find('.targetYear').val(),
        srfTargetUnit: {
          id: $(outcome).find('.targetUnit').val()
        },
        id: $(outcome).find('.outcomeId').val()
    }

    // Milestones
    var milestonesArray = [];
    $(outcome).find('.milestone').each(function(i,milestone) {
      var milestonObj = {
          title: $(milestone).find('.milestone-statement').val(),
          value: $(milestone).find('.targetValue').val(),
          year: $(milestone).find('.targetYear').val(),
          srfTargetUnit: {
            id: $(milestone).find('.targetUnit').val()
          },
          id: $(milestone).find('.mileStoneId')
      }
      milestonesArray.push(milestonObj);
    });

    // SubIdos
    var subIdosArray = []
    $(outcome).find('.subIdo').each(function(i,subIdo) {
      var subIdoObj = {
          srfSubIdo: {
              id: $(subIdo).find('.subIdoId').val(),
              srfIdo: {
                id: $(subIdo).find('.idoId').val()
              }
          },
          contribution: $(subIdo).find('.contribution').val(),
          id: $(subIdo).find('.programSubIDOId').val()
      }

      // Assumptions
      var assumptionsArray = [];
      $(subIdo).find('.assumption').each(function(i,assumption) {
        var assumptionObj = {
            id: $(assumption).find('.assumptionId').val(),
            name: $(assumption).find('.statement').val()
        }
        assumptionsArray.push(assumptionObj);
      });

      subIdoObj.assumptions = assumptionsArray;
      subIdosArray.push(subIdoObj);
    });

    outcomeObj.subIdos = subIdosArray;
    outcomeObj.milestones = milestonesArray;
    outcomesArray.push(outcomeObj);
  });

  saveObj.outcomes = outcomesArray;
  saveObj.crpProgramID = $('#crpProgramID').val();

  console.log(saveObj);
  console.log(JSON.stringify(saveObj));
}