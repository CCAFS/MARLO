$(document).ready(init);
var currentSubIdo;
var saveObj;
var expandAllOutcomesbol = false;
var expandAllMilesetonesbol = false;

function init() {

  /* Declaring Events */
  attachEvents();
 
  /* Init Select2 plugin */
  // .opi-plain / .opi-select keep the native control: the design's selects are
  // plain boxes with a chevron, not select2's rendered markup.
  $('.outcomes-list select').not('.opi-plain, .opi-select').select2();

  /* Numeric Inputs */
  $('input.targetValue , input.targetYear').not('.opi-cell__value').numericInput();
  opiBindCellNumeric($('input.opi-cell__value'));

  // Baseline value is optional and nullable, so it stays out of numericInput():
  // that helper rewrites an empty field to 0, which would store a real 0 for an
  // indicator whose baseline was simply never captured.
  $('.opi-page input.opi-baselineValue').on('keydown', function(e) { isNumber(e); });

  /* Percentage Inputs */
  $('.outcomes-list input.contribution').percentageInput();

  $(document).ready(function(){
		$('[data-toggle="popover"]').popover();

		var $tables = $('table.deliverableList, table.innovationList, table.evidencieList, table.dt-outcomes');
		$tables.each(function() {
		  initializeDataTable($(this));
		});
});



setFormatInput();

$('.targetUnit-block select').on('change', function(){
  const selector = $(this).parents('.targetUnit-block').next('.targetValue-block').find('input').attr('name');
  setFormatInput(`input[name="${selector}"]`, {isRecallMethod: true});
})

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
  //click event expand 
  $('.blockTitle.opened').on('click', function() {
    if($(this).hasClass('closed')) {
      // $('.blockContent').slideUp();
      // $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
    });
  });
  // Expand alls Outcomes
  $('.btn-expand-all-outcomes').on('click', expandAllOutcomes);
  // Expand alls Milestones
  $('.btn-expand-all').on('click', expandAllMilestones);
  // Expand an outcome
  $('.btn-expand-Outcome').on('click', expandOutcome);
  // Expand a Milestone
  $('.btn-expand').on('click', expandMilestone);
  // Add an Outcome
  $('.addOutcome').on('click', addOutcome);
  // Remove an Outcome
  $('.removeOutcome').on('click', removeOutcome);

  // Add a Milestone
  $('.addMilestone').on('click', addMilestone);
  // Remove a Milestone
  $('.removeMilestone').on('click', removeMilestone);

  // Change Outcomes/Milestones Year
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
  }).trigger('keyup');

  // Change Milestone Status
  $('select.milestoneStatus').on('change', function() {
    var $parent = $(this).parents('div.milestone');
    // var extendedYear = $parent.find('.milestoneExtendedYear').val();
    // var hasExtendedYear = (extendedYear && (extendedYear != -1));
    var showExtendedYear = (this.value == 4)
    if(showExtendedYear) {
      $parent.find('.extendedYearBlock').slideDown();
    } else {
      $parent.find('.extendedYearBlock').slideUp();
    }
  });

  $('select.milestoneStatus').each(function(i,statusSelect) {
    var $parent = $(this).parents('div.milestone');
    var extendedYear = $parent.find('.milestoneExtendedYear').val();
    var year = $parent.find('.milestoneYear').val() || extendedYear;
    var isNew = ($parent.classParam('isNew') == "true");
    // Planning/POWB
    if(!reportingActive) {
      if(year >= currentCycleYear) {
        $(statusSelect).find('option[value="3"]').prop('disabled', true); // Complete
        $(statusSelect).find('option[value="4"]').prop('disabled', true); // Extended
      } else {
        if(!isNew) {
          $(statusSelect).find('option[value="1"]').prop('disabled', true); // New
        }
      }
    }
  });

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
    // var v = $(this).text().length > 65 ? $(this).text().substr(0, 65) + ' ... ' : $(this).text();
    var v = $(this).text();

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

  // Set Primary Sub-IDO
  $('.setPrimaryRadio').on('click', function() {
    var $parent = $(this).parents('.subIdo');
    var $siblings = $parent.siblings()
    console.log(this.value);
    $siblings.find('.setPrimaryRadio').prop('checked', false);
  });

  // Event when the assessment of risk to achievement is changed
  $('input.assesmentLevels').on('change', function() {
    var $milestoneRiskBlocks = $(this).parents('.milestone').find('.milestoneRisk');

    if(this.value >= 2) {
      $milestoneRiskBlocks.slideDown();
    } else {
      // Trigger Risks Options
      $milestoneRiskBlocks.find('select.risksOptions').val('-1');
      $milestoneRiskBlocks.slideUp();
    }

    // Trigger Risks Options
    $milestoneRiskBlocks.find('select.risksOptions').trigger('change');
  });

  $('select.risksOptions').on('change', function() {
    var $elementBlocks = $(this).parents('.milestone').find('.milestoneOtherRiskField');
    if(this.value == 7) {
      $elementBlocks.slideDown();
    } else {
      $elementBlocks.find('input').val('');
      $elementBlocks.slideUp();
    }
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
  $item.show('slow', function() {
    // A new indicator's gaps are known the moment it exists -- statement, closing year, target
    // unit and the still-empty period-target matrix -- so it opens with the missing-fields tag
    // rather than no status at all. Painted from the animation's callback because
    // opiMissingFields only counts required markers that are already visible.
    if ($('.opi-page').exists()) { opiRefreshCardStatus($item); }
  });
}

function removeOutcome() {
  var $trigger = $(this);
  var $item = $trigger.parents('.outcome');
  var drop = function() {
    $item.hide(function() {
      $item.remove();
      updateAllIndexes();
      // The removed card was counting towards the section's tally.
      if ($('.opi-page').exists()) { opiRefreshSummary(); }
    });
  };

  // Removing the card takes its disaggregation rows and every period target with it, and
  // the deletion lands on the next save. It used to happen on a single click with nothing
  // asked, so the dialog says what goes before it does.
  if (!$('.opi-page').exists()) {
    drop();
    return;
  }
  var rows = $item.find('.opi-dis__row').not('.is-principal').length;
  var values = 0;
  $item.find('.opi-cell__value').each(function() {
    if ($.trim($(this).val() || '') !== '') { values++; }
  });
  var name = $.trim($item.find('.opi-card__code').first().text() || '');
  opiConfirm({
      title: opiLabel('removeCardTitle').replace('{0}', name || opiLabel('removeCardFallback')),
      detail: opiLabel('removeCardDetail').replace('{0}', rows).replace('{1}', values),
      trigger: $trigger,
      confirmLabel: opiLabel('removeCardConfirm'),
      cancelLabel: opiLabel('dialogCancel'),
      onConfirm: drop
  });
}

/**
 * Milestone Functions
 */

function addMilestone() {
  var $list = $(this).parents('.outcome').find('.milestones-list');
  var $item = $('#srfSlo-template').clone(true).removeAttr("id");

  // Set Status as new
  $item.find('.milestoneStatus').val(1); // New

  // Set Milestone year as the currentCycleYear
  $item.find('.milestoneYear').val(currentCycleYear);

  $item.find('select').select2({
    width: '100%'
  });

  $list.append($item);
  updateAllIndexes();
  $item.show('slow');
  // $item.removeClass()
  $item.find(".milestone").css({"display":"block"});
  // Hide empty message
  $(this).parents('.outcome').find('.milestones-list p.message').hide();
}

function removeMilestone() {
  // var $list = $(this).parents('.outcome').find('.milestones-list');
  // var $item = $(this).parents('.milestone');
  // $item.hide(function() {
  //   $item.remove();
  //   updateAllIndexes();
  // });


  console.log("remove milestone");
  var $list = $(this).parents('.outcome').find('.milestones-list');
  // var $item = $(this).parents('.milestone');
  var $item =  $(this).parents('.srfSlo').find(".milestone");
  var $collapse = $(this).parents('.srfSlo');
  //  $(this).parents('.srfSlo').find(".milestone").css({"color": "red", "border": "2px solid red"});
  console.log($item);

  $collapse.hide(function() {
    $collapse.remove();
    $item.hide(function() {
      $item.remove();
      updateAllIndexes();
    });
    // updateAllIndexes();
  });





}

function expandMilestone(){
  let $milestone = $(this).parents('.milestone');
  let $titlePreview = $milestone.closest('.srfSlo').find('.milestoneTitlePreview');

  if ($milestone.find(".to-minimize").hasClass("minimize")){
    $milestone.find(".to-minimize").removeClass("minimize");
    $(this).html("Collapse");
    $titlePreview.hide();
  } else {
    $milestone.find(".to-minimize").addClass("minimize");
    $(this).html("Expand");
    $titlePreview.show();
  }
}

function expandOutcome(){
  let $outcome = $(this).parents('.outcome');
  let $selector="#"+$outcome[0].id;
  if ($($selector+" .to-minimize-outcome").hasClass("minimizeOutcome")){
    $($selector+" .to-minimize-outcome").removeClass("minimizeOutcome");
    $outcome.removeClass("is-collapsed");
    $($selector+" .btn-expand-Outcome").attr("aria-expanded","true");
  }else{
    $($selector+" .to-minimize-outcome").addClass("minimizeOutcome");
    $outcome.addClass("is-collapsed");
    $($selector+" .btn-expand-Outcome").attr("aria-expanded","false");
  }
}

function expandAll(){
  let $outcome = $(this).parents('.outcome');
  // console.log($outcome[0].id  );
  
    $("#"+$outcome[0].id +" .milestones-list").find('.milestone').each(function(i,milestone) {

      if( $("#"+$outcome[0].id +" .btn-expand-all").text() == "Expand all"){
    
          $(milestone).find('.to-minimize').each(function(i,milestone) {
          $(milestone).removeClass("minimize");

      });
      // console.log("Minimize all");
      }else{
          $(milestone).find('.to-minimize').each(function(i,milestone) {
          $(milestone).addClass("minimize");
          });

        // console.log("Expand all");
      }
    });

  if($("#"+$outcome[0].id +" .btn-expand-all").text() == "Expand all"){
    $("#"+$outcome[0].id +" .btn-expand-all").html("Collapse all");
    $("#"+$outcome[0].id +" .btn-expand").html("Collapse");
  }else{
    $("#"+$outcome[0].id +" .btn-expand").html("Expand");
    $("#"+$outcome[0].id +" .btn-expand-all").html("Expand all");
  }
}
function expandAllMilestones(){
  let $outcome = $(this).parents('.outcome');
  console.log($outcome);
  // console.log($outcome[0].id  );
  
    $("#"+$outcome[0].id +" .milestones-list").find('.blockContent').each(function(i,milestone) {
       if($("#"+$outcome[0].id +" .btn-expand-all").text() == "Expand all"){
        $(milestone).slideDown();
        $("#"+$outcome[0].id +" .milestones-list").find('.blockTitle').switchClass('closed','opened');

       }else{
        $(milestone).slideUp();
        $("#"+$outcome[0].id +" .milestones-list").find('.blockTitle').switchClass('opened','closed');

       }

    });
    expandAllMilesetonesbol = !expandAllMilesetonesbol;

  if($("#"+$outcome[0].id +" .btn-expand-all").text() == "Expand all"){
    $("#"+$outcome[0].id +" .btn-expand-all").html("Collapse all");
    $("#"+$outcome[0].id +" .btn-expand").html("Collapse");
  }else{
    $("#"+$outcome[0].id +" .btn-expand").html("Expand");
    $("#"+$outcome[0].id +" .btn-expand-all").html("Expand all");
  }
}


function expandAllOutcomes(){

 
  
    $(" .outcomes-list").find('.outcome').each(function(i,outcome) {
      if( expandAllOutcomesbol){
    
          $(outcome).find('.to-minimize-outcome').each(function(i,btn) {
          $(btn).removeClass("minimizeOutcome");
          

      });
      // console.log("minimizeOutcome all");
     
      }else{
          $(outcome).find('.to-minimize-outcome').each(function(i,btn) {
          $(btn).addClass("minimizeOutcome");
          });

        // console.log("Expand all");
       
      }
    });

  var opiCA = (typeof opiLabel === 'function' && opiLabel('collapseAll')) || "Collapse all";
  var opiEA = (typeof opiLabel === 'function' && opiLabel('expandAll')) || "Expand all";
  if(expandAllOutcomesbol){
    $(".btn-expand-all-outcomes ").text(opiCA);
    $(".outcomes-list .outcome").removeClass("is-collapsed");
    $(".btn-expand-Outcome").attr("aria-expanded","true");
    expandAllOutcomesbol = false;
  }else{
    $(".btn-expand-all-outcomes ").text(opiEA);
    $(".outcomes-list .outcome").addClass("is-collapsed");
    $(".btn-expand-Outcome").attr("aria-expanded","false");
    expandAllOutcomesbol = true;
  }
  
}
/**
 * SUB-IDOs Functions
 */

function addSubIdo() {
  var $list = $(this).parents('.outcome').find('.subIdos-list');

  if($list.find('.subIdo').length >= 3) {
    $('div.addSubIdo').animateCss('shake');
    return;
  }

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
        // The empty-state note is the panel's only file status, so it follows the upload.
        $ub.find('.opi-fileNone').hide();
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
  $ub.find('.opi-fileNone').show();
  $ub.find('input.fileID').val('');
  $ub.find('input.outcomeID').val('');
});

/**
 * General Function
 */

function updateAllIndexes() {
  // All Outcomes List
  $('.outcomes-list').find('.outcome').each(function(i,outcome) {
    $(outcome).attr('id', "outcome-"+(i+1));
    // $(outcome).find('span.index').html(i + 1);
    $(outcome).setNameIndexes(1, i);

    // Update Milestones
    $(outcome).find('.milestone').each(function(i,milestone) {
      $(milestone).attr('id', "milestone-"+(i+1));
      // $(milestone).find('span.index').text(i + 1);
      $(milestone).setNameIndexes(2, i);

      // Update radios for Assesment Risk
      $(milestone).find('.radioFlat').each(function(i,radioBlock) {
        var radioFlatID = ($(radioBlock).find('input').attr('id') + i).replace(/\W/g, '');
        $(radioBlock).find('input').attr('id', radioFlatID);
        $(radioBlock).find('label').attr('for', radioFlatID);
      });

    });

    // Update SubIdos
    $(outcome).find('.subIdo').each(function(i,subIdo) {
      $(subIdo).find('span.index').text(i + 1);
      $(subIdo).setNameIndexes(2, i);

      // Update radios for primary option
      var radioFlatID = $(subIdo).find('.radioFlat input').attr('id');
      $(subIdo).find('.radioFlat label').attr('for', radioFlatID);

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

function initializeDataTable($table) {
  var dt = $table.DataTable({
    paging: true,
    bLengthChange: true,
    searching: true,
    ordering: true,
    autoWidth: false,
    iDisplayLength: 25,
    language: {
      searchPlaceholder: "Search..."
    },
    order: [[1, 'asc']],
    columnDefs: [
      { targets: 2, orderable: false },
      { targets: -1, orderable: false }
    ],
    drawCallback: function () {
      var $wrapper = $table.closest('.dataTables_wrapper');
      if ($wrapper.length && !$wrapper.data('search-icon-added')) {
        var $filter = $wrapper.find('.dataTables_filter');
        if ($filter.length) {
          var $icon = $('<div class="iconSearch"></div>');
          $icon.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="Search" style="width:24px;margin:auto;">');
          $icon.prependTo($filter);
          $wrapper.data('search-icon-added', true);
        }
      }
    }
  });
}
/* ============================================================================
 * OPI redesign (A2-2437) — Overall Performance Indicators
 *
 * Renders the design's matrix over the flat milestone list: distinct milestone
 * statements are the disaggregation rows, distinct years are the period-target
 * columns. Every visible control here syncs into the real Struts inputs
 * (outcomesForm[i].milestones[j].*), so OutcomeValidator and the save chain
 * stay untouched. All wording comes from the #opiI18n carrier.
 * ========================================================================== */

var opiRowSeq = 0;

$(document).ready(function() {
  if (!$('.opi-page').exists()) {
    return;
  }
  opiAttachHelpToggle();
  opiDecorateCheckButton();
  // Registered before the editable guard below: the check button only renders on a page
  // the user can edit, but the registration must stand whatever that guard decides.
  window.impactPathwayValidationReport = opiReportValidation;
  opiGroupSaveBar();
  opiDecorateSaveButton();
  // The unit affix, the cell hint and the row's code / unit caption are read-only
  // information, so they are painted before the editable-only wiring: a locked
  // page renders the same cells and must not lose them.
  opiPaintMatrix();
  opiRelabelLockedErrors();
  if (!opiIsEditable()) {
    return;
  }
  opiAttachDirtyTracking();
  opiAttachValidationClearing();
  // Before the first status count, so the cells it opens are counted with the rest.
  var opened = false;
  $('.outcomes-list > .outcome').each(function() { opened = opiOpenGaps($(this)) || opened; });
  if (opened) { updateAllIndexes(); }
  opiRefreshAllStatuses();
  opiDecorateSidebar();
  $('.opi-q').each(function() { opiRefreshQuestions($(this).closest('.outcome')); });
  $('.outcomes-list > .outcome').each(function() { opiRecodeRows($(this)); });

  var $page = $('.opi-page');

  // ---- live recount ----
  $page.on('change keyup', 'input, textarea, select', function() {
    opiRefreshCardStatus($(this).closest('.opi-card'));
  });

  // Every handler below ends on opiRefreshCardStatus because the live recount above
  // is bound first and therefore runs before opiSyncRow has touched the cells --
  // without this the pill is computed from the state the row had a moment ago.

  // ---- indicator statement mirrors the principal row ----
  $page.on('input keyup', '.outcome-statement', function() {
    var $card = $(this).closest('.outcome');
    var v = $(this).val() || '';
    $card.find('[data-opi-cardname]').text(v);
    var $pDis = $card.find('.opi-dis__row.is-principal');
    $pDis.find('.opi-dis__stmtInput').val(v);
    opiSyncRow($card, $pDis.attr('data-opi-row'));
    opiRefreshCardStatus($card);
  });

  // ---- outcome unit mirrors the principal row unit ----
  $page.on('change', 'select.targetUnit', function() {
    var $card = $(this).closest('.outcome');
    if (!$card.exists()) { return; }
    var $pDis = $card.find('.opi-dis__row.is-principal');
    $pDis.find('.opi-dis__unitSelect').val($(this).val());
    opiSyncRow($card, $pDis.attr('data-opi-row'));
    opiRefreshCardStatus($card);
  });

  // ---- disaggregation row edits sync into the hidden milestone inputs ----
  $page.on('input keyup', '.opi-dis__stmtInput, .opi-dis__codeInput', function() {
    var $card = $(this).closest('.outcome');
    opiSyncRow($card, $(this).closest('.opi-dis__row').attr('data-opi-row'));
    opiRefreshCardStatus($card);
  });
  $page.on('change', '.opi-dis__unitSelect', function() {
    var $card = $(this).closest('.outcome');
    opiSyncRow($card, $(this).closest('.opi-dis__row').attr('data-opi-row'));
    opiRefreshCardStatus($card);
  });

  // ---- Yes / No disaggregations toggle ----
  $page.on('click', '.opi-dis__yes, .opi-dis__no', function() {
    var $card = $(this).closest('.outcome');
    var yes = $(this).hasClass('opi-dis__yes');
    if (yes) {
      opiSetDisAnswer($card, true);
      // The answer has no column: outcomes.ftl derives it from the rows. "Yes" with no
      // row would come back as "No" on reload, so open the first one for it.
      if (!$card.find('.opi-dis__row').not('.is-principal').exists()) {
        opiAddDisRow($card);
      }
    } else {
      // Only paint "No" once the rows are gone. Hiding them would keep them submitting,
      // and the answer would flip back to "Yes" on the next reload -- and it also means
      // cancelling leaves the toggle exactly where it was, with nothing to bounce back.
      opiClearDisRows($card, $(this), function() { opiSetDisAnswer($card, false); });
    }
  });

  // ---- drag & drop reorder ----
  $page.on('dragstart', '.opi-dis__row[draggable=true]', function(e) {
    e.originalEvent.dataTransfer.effectAllowed = 'move';
    try { e.originalEvent.dataTransfer.setData('text/plain', $(this).attr('data-opi-row')); } catch (err) {}
    $(this).addClass('is-dragging');
  });
  $page.on('dragend', '.opi-dis__row', function() {
    $(this).removeClass('is-dragging');
    $('.opi-dis__row').removeClass('is-dropTarget');
  });
  $page.on('dragover', '.opi-dis__row:not(.is-principal)', function(e) {
    if (!$(this).closest('.outcome').find('.opi-dis__row.is-dragging').exists()) { return; }
    e.preventDefault();
    e.originalEvent.dataTransfer.dropEffect = 'move';
    $(this).addClass('is-dropTarget');
  });
  $page.on('dragleave', '.opi-dis__row', function() { $(this).removeClass('is-dropTarget'); });
  $page.on('drop', '.opi-dis__row:not(.is-principal)', function(e) {
    e.preventDefault();
    var $card = $(this).closest('.outcome');
    var $from = $card.find('.opi-dis__row.is-dragging');
    $(this).removeClass('is-dropTarget');
    if (!$from.exists() || $from.is(this)) { return; }
    var fromKey = $from.attr('data-opi-row');
    var toKey = $(this).attr('data-opi-row');
    $from.insertBefore(this);
    var $mFrom = opiMatrixRow($card, fromKey);
    $mFrom.insertBefore(opiMatrixRow($card, toKey));
    opiAfterDisChange($card);
  });

  // ---- delete a disaggregation row (all its milestones) ----
  $page.on('click', '.opi-dis__delete', function() {
    var $card = $(this).closest('.outcome');
    opiDropDisRow($card, $(this).closest('.opi-dis__row'));
    opiAfterDisChange($card);
  });

  // ---- business rule: caption only, so it does not mark the form dirty ----
  $page.on('change', '.opi-dis__ruleSelect', function() {
    var $dis = $(this).closest('.opi-dis__row');
    var $mRow = opiMatrixRow($dis.closest('.outcome'), $dis.attr('data-opi-row'));
    $mRow.find('[data-opi-rowsub]').text(opiRowSubtitle($dis));
  });

  // ---- add a disaggregation row ----
  $page.on('click', '.opi-addDis', function() {
    var $card = $(this).closest('.outcome');
    opiAddDisRow($card);
  });

  // ---- add a year column: the button opens the year menu ----
  $page.on('click', '.opi-addYear', function(e) {
    e.stopPropagation();
    var $btn = $(this);
    var wasOpen = $btn.attr('aria-expanded') === 'true';
    opiCloseYearMenu();
    if (!wasOpen) { opiOpenYearMenu($btn); }
  });

  $page.on('click', '.opi-yearMenu__option:not([disabled])', function() {
    var $card = $(this).closest('.outcome');
    var year = parseInt($(this).attr('data-opi-year'), 10);
    opiCloseYearMenu();
    if (!isNaN(year)) { opiAddYear($card, year); }
  });

  $page.on('input keyup', '.opi-yearMenu__custom', function() {
    var digits = String($(this).val() || '').replace(/\D/g, '').slice(0, 4);
    if (digits !== $(this).val()) { $(this).val(digits); }
    opiRefreshYearMenuCustom($(this).closest('.opi-yearMenu'));
  });

  $page.on('keydown', '.opi-yearMenu__custom', function(e) {
    if (e.which === 13) { e.preventDefault(); $(this).closest('.opi-yearMenu').find('.opi-yearMenu__add').trigger('click'); }
  });

  $page.on('click', '.opi-yearMenu__add:not([disabled])', function() {
    var $menu = $(this).closest('.opi-yearMenu');
    var $card = $menu.closest('.outcome');
    var year = parseInt($menu.find('.opi-yearMenu__custom').val(), 10);
    opiCloseYearMenu();
    if (!isNaN(year)) { opiAddYear($card, year); }
  });

  // The menu is a popover: anything outside it, or Escape, closes it.
  $(document).on('mousedown.opiYearMenu', function(e) {
    if (!$(e.target).closest('.opi-addYearWrap').exists()) { opiCloseYearMenu(); }
    if (!$(e.target).closest('.opi-rmYear__pop, .opi-rmYear').exists()) { opiCloseRemoveYear(); }
  });
  $(document).on('keydown.opiYearMenu', function(e) {
    if (e.which !== 27) { return; }
    if ($('.opi-yearMenu').exists()) {
      var $btn = $('.opi-addYear[aria-expanded="true"]');
      opiCloseYearMenu();
      $btn.trigger('focus');
    }
    if ($('.opi-rmYear__pop').exists()) { opiCloseRemoveYear(true); }
  });

  // ---- remove a year column ----
  $page.on('click', '.opi-rmYear:not([disabled])', function(e) {
    e.stopPropagation();
    opiCloseYearMenu();
    var $col = $(this).closest('[data-opi-yearcol]');
    var $card = $col.closest('.outcome');
    var year = parseInt($col.attr('data-opi-yearcol'), 10);
    if (isNaN(year)) { return; }
    // An empty column costs nothing to rebuild, so it goes without asking; the
    // undo bar is the safety net either way.
    if (opiYearValueCount($card, year) === 0) {
      opiCloseRemoveYear();
      opiRemoveYear($card, year);
    } else {
      opiOpenRemoveYear($(this), $card, year);
    }
  });

  $page.on('click', '.opi-rmYear__cancel', function() { opiCloseRemoveYear(); });

  $page.on('click', '.opi-rmYear__confirm', function() {
    var $pop = $(this).closest('.opi-rmYear__pop');
    var $card = $pop.closest('.outcome');
    var year = parseInt($pop.attr('data-opi-rmyear'), 10);
    opiCloseRemoveYear();
    if (!isNaN(year)) { opiRemoveYear($card, year); }
  });

  // The popover is pinned to a column of a horizontally scrollable table, so it
  // cannot follow it: close it instead of letting it drift.
  $page.on('scroll', '.opi-matrix', function() { opiCloseRemoveYear(); });

  // ---- create the missing milestone behind an empty cell ----
  $page.on('click', '.opi-cell__create', function() {
    var $ph = $(this).closest('.opi-cell');
    var $mRow = $ph.closest('.opi-matrix__row');
    var $card = $ph.closest('.outcome');
    var $cell = opiNewCell($card, $mRow.attr('data-opi-row'), $ph.attr('data-opi-year'));
    $ph.replaceWith($cell);
    updateAllIndexes();
    opiRefreshCardStatus($card);
    opiMarkDirty();
    $cell.find('.opi-cell__value').trigger('focus');
  });

  // ---- status: reveal the extended-year select on "Extended" ----
  $page.on('change', '.opi-cell__status', function() {
    var $cell = $(this).closest('.opi-cell');
    $cell.find('.opi-cell__extYear').toggle($(this).val() === '4');
  });

  // ---- cell values: amber when missing + percentage hint ----
  $page.on('input keyup change', '.opi-cell__value', function() {
    opiRefreshCell($(this));
  });

  // ---- questions: renumber + gate the add button while one is empty ----
  $page.on('input keyup', '.opi-q__input', function() {
    opiRefreshQuestions($(this).closest('.outcome'));
  });
  $page.on('click', '.addBaselineIndicator, .removeBaselineIndicator', function() {
    var $card = $(this).closest('.outcome');
    setTimeout(function() { opiRefreshQuestions($card); }, 0);
  });
});

/**
 * Reads one localized string from the carrier rendered by outcomes.ftl.
 * @param {string} key data-attribute name on #opiI18n
 * @return {string} the localized text, or '' when the key is absent
 */
function opiLabel(key) {
  var text = $('#opiI18n').data(key);
  return (text === undefined || text === null) ? '' : String(text);
}

/**
 * Whether the section renders editable controls in this phase.
 * @return {boolean} true when the form is editable
 */
function opiIsEditable() {
  return String($('#opiI18n').data('editable')) === 'true';
}

/**
 * The caption under a matrix row's statement: its unit, plus the business rule
 * when one is picked. The rule has no table behind it yet, so it only ever comes
 * from the select on screen.
 * @param {jQuery} $dis the .opi-dis__row element
 * @return {string} the caption, e.g. "% \u00b7 Actors (innovations)"
 */
function opiRowSubtitle($dis) {
  var unit = $.trim($dis.find('.opi-dis__unitSelect option:selected').text() || '');
  var $rule = $dis.find('.opi-dis__ruleSelect');
  var rule = $rule.val() === 'none' ? '' : $.trim($rule.find('option:selected').text() || '');
  return unit && rule ? unit + ' \u00b7 ' + rule : (unit || rule);
}

/**
 * Paints the purely descriptive part of every matrix: each cell's unit affix and
 * hint, and each row's code and caption. Writes nothing that is submitted, so it
 * also runs on a page the user cannot edit.
 */
function opiPaintMatrix() {
  $('.opi-matrix__row .opi-cell__value').each(function() { opiRefreshCell($(this)); });
  $('.opi-dis__row').each(function() {
    var $card = $(this).closest('.outcome');
    var $mRow = opiMatrixRow($card, $(this).attr('data-opi-row'));
    $mRow.find('[data-opi-rowsub]').text(opiRowSubtitle($(this)));
    $mRow.find('[data-opi-rowcode]').text($(this).find('.opi-dis__codeInput').val() || ' ');
  });
}

/**
 * Collapses / expands the "How this section works" panel.
 */
function opiAttachHelpToggle() {
  $('.opi-help__toggle').on('click', function() {
    var $button = $(this);
    var $body = $('#' + $button.attr('aria-controls'));
    var isOpen = $button.attr('aria-expanded') === 'true';
    $body.slideToggle(150);
    $button.attr('aria-expanded', isOpen ? 'false' : 'true');
    $button.text(isOpen ? opiLabel('buttonShow') : opiLabel('buttonHide'));
  });
}

/**
 * Fills the live missing-fields badge on the active component. Only the active
 * component's data is on the page, so the other entries carry no badge.
 */
function opiDecorateSidebar() {
  var $badge = $('[data-opi-menu-badge]');
  if (!$badge.exists()) { return; }
  var total = opiSectionMissing();
  $badge.text(total > 0 ? String(total) : '\u2713').toggleClass('is-ok', total === 0);
}

/**
 * Prepends the design's check icon to the sidebar validate button.
 */
function opiDecorateCheckButton() {
  var $btn = $('.opi-sidebar .projectValidateButton');
  if ($btn.exists() && !$btn.find('svg').exists()) {
    $btn.prepend('<svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" stroke="#fff" stroke-width="1.5"></circle><path d="M5.2 8.2 7.1 10l3.7-4" stroke="#fff" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg> ');
  }
}

/**
 * Moves the shared last-edit message into the save bar's left group, next to
 * the save state, so the bar reads "state + last edit" on the left and the
 * action buttons on the right. The message is rendered by
 * buttons-impactPathway.ftl, which every impact pathway section reuses, so it
 * is relocated here instead of being re-rendered for this section alone.
 */
function opiGroupSaveBar() {
  var $info = $('.opi-saveBar__info');
  if (!$info.exists()) { return; }
  var $message = $('.opi-saveBar__actions #lastUpdateMessage');
  if ($message.exists()) {
    $info.append($message);
  }
  // Always drops the placeholder: a section with no history log never gets a
  // message, and the slot must not keep shimmering for it.
  $info.find('[data-opi-save-ghost]').remove();
}

/**
 * Replaces the shared glyphicon on the save button with the design's outline
 * save icon, the way opiDecorateCheckButton() does for the validate button.
 */
function opiDecorateSaveButton() {
  var $icon = $('.opi-saveBar .button-save .glyphicon');
  if (!$icon.exists()) { return; }
  $icon.replaceWith('<svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 3h7.2L13 5.8V13H3V3Z" stroke="#fff" stroke-width="1.5" stroke-linejoin="round"></path><path d="M5.6 3v3.4h4.2V3M5.6 13v-3.2h4.8V13" stroke="#fff" stroke-width="1.4" stroke-linejoin="round"></path></svg>');
}

/**
 * Flips the save bar to its "unsaved changes" state.
 * Typing raises it through the listener below, but adding or removing rows, years and
 * cells changes the form without any field firing an event, so those call this directly.
 */
function opiMarkDirty() {
  var $bar = $('.opi-saveBar');
  if ($bar.hasClass('is-dirty')) { return; }
  $bar.addClass('is-dirty');
  $bar.find('[data-opi-save-state]').text(opiLabel('saveUnsaved'));
  $bar.find('[data-opi-save-detail]').text(opiLabel('saveUnsavedDetail'));
}

/**
 * Flips the save bar to its "unsaved changes" state on the first edit.
 */
function opiAttachDirtyTracking() {
  $('.opi-page').on('change keyup', 'input, textarea, select', opiMarkDirty);
}

/**
 * Finds the matrix row that pairs with a disaggregations row.
 * @param {jQuery} $card the .outcome card
 * @param {string} key the shared data-opi-row key
 * @return {jQuery} the .opi-matrix__row element
 */
function opiMatrixRow($card, key) {
  return $card.find('.opi-matrix__row[data-opi-row="' + key + '"]');
}

/**
 * Copies one disaggregation row's statement / code / unit into the hidden
 * inputs of every milestone cell on its matrix row.
 * @param {jQuery} $card the .outcome card
 * @param {string} key the shared data-opi-row key
 */
function opiSyncRow($card, key) {
  if (!key) { return; }
  var $dis = $card.find('.opi-dis__row[data-opi-row="' + key + '"]');
  var $mRow = opiMatrixRow($card, key);
  if (!$dis.exists() || !$mRow.exists()) { return; }
  var stmt = $dis.find('.opi-dis__stmtInput').val() || '';
  var code = $dis.find('.opi-dis__codeInput').val() || '';
  var unit = $dis.find('.opi-dis__unitSelect').val() || '-1';
  $mRow.find('[data-opi-rowstmt]').text(stmt);
  $mRow.find('[data-opi-rowcode]').text(code || ' ');
  $mRow.find('[data-opi-rowsub]').text(opiRowSubtitle($dis));
  var naUnit = String(unit) === '-1';
  $mRow.find('.opi-cell').each(function() {
    $(this).find('.opi-cell__title').val(stmt);
    $(this).find('.opi-cell__code').val(code);
    $(this).find('.opi-cell__unit').val(unit);
    opiApplyNotApplicable($(this), naUnit);
    var $value = $(this).find('.opi-cell__value');
    if ($value.exists()) { opiRefreshCell($value); }
  });
  opiTagRowForValidation($dis, $mRow);
}

/**
 * Re-labels the errors the shared highlighter left on a closed year's cells.
 *
 * OutcomeValidator flags every milestone with no value, the ones this phase can no
 * longer edit included, and its message is a bare "Required Field". On a box the user
 * cannot type into that reads like a bug, so those cells say where the gap actually
 * lives instead. The highlight itself stays: the milestone really is incomplete, and
 * it is already counting towards the section being reported as incomplete.
 *
 * Deferred because fieldsValidation.js paints from its own ready handler, registered
 * after this one -- the classes are not on the page yet when this runs.
 */
function opiRelabelLockedErrors() {
  window.setTimeout(function() {
    var text = opiLabel('lockedValue');
    if (!text) { return; }
    $('.opi-cell.is-readonly .opi-cell__valueWrap.fieldError').attr('title', text);
  }, 0);
}

/**
 * Swaps the field-name classes an element carries for the highlighter.
 *
 * fieldsValidation.js decorates a field it cannot reach by its name -- a hidden one,
 * or one with no label -- by looking for an element whose class is that field name
 * with the non-word characters stripped. Milestone indexes move with
 * updateAllIndexes(), so the previous set is recorded and dropped rather than left
 * to pile up.
 * @param {jQuery} $el the element standing in for the field
 * @param {Array<string>} names the field names it should answer to
 */
function opiTagValidationKeys($el, names) {
  if (!$el.exists()) { return; }
  var previous = $el.attr('data-opi-keys');
  if (previous) { $el.removeClass(previous); }

  var joined = $.map(names, function(name) {
    return name ? name.replace(/\W+/g, '') : null;
  }).join(' ');

  if (joined) {
    $el.attr('data-opi-keys', joined).addClass(joined);
  } else {
    $el.removeAttr('data-opi-keys');
  }
}

/**
 * Makes one matrix row's validation errors land on something the user can see.
 *
 * OutcomeValidator reports against the milestone behind each cell, and neither of the
 * two fields it flags can be decorated on its own: the statement is a hidden input in
 * the matrix, and the value has no label. So the row's statement box answers for every
 * "...milestones[j].title" of the row, and each cell's value box for its own
 * "...milestones[j].value".
 * @param {jQuery} $dis the .opi-dis__row element
 * @param {jQuery} $mRow the matching .opi-matrix__row element
 */
function opiTagRowForValidation($dis, $mRow) {
  var titles = [];
  $mRow.children('.opi-cell').each(function() {
    var $cell = $(this);
    titles.push($cell.find('input[name$=".title"]').attr('name'));
    // The year travels with the value: it is a hidden input with no control of its own, and
    // when the validator rejects it -- a period target dated past the closing year -- the cell
    // is the only thing on screen that stands for it.
    opiTagValidationKeys($cell.find('.opi-cell__valueWrap'), [
        $cell.find('input[name$=".value"]').attr('name'),
        $cell.find('input[name$=".year"]').attr('name')
    ]);
  });
  opiTagValidationKeys($dis.find('.opi-dis__stmtInput'), titles);
}

/**
 * Renumbers the # column of the disaggregations table.
 * @param {jQuery} $card the .outcome card
 */
function opiRenumberDis($card) {
  $card.find('.opi-dis__row').each(function(i) {
    $(this).find('.opi-dis__n').text(i + 1);
  });
}

/**
 * Rewrites every code from the row order: the principal becomes <prefix>.0 and the
 * rest <prefix>.1..n, mirroring the design's "codes renumber automatically" rule.
 * Also refreshes the # column, so one call covers both after a reorder.
 * @param {jQuery} $card the .outcome card
 */
function opiRecodeRows($card) {
  var $principal = $card.find('.opi-dis__row.is-principal');
  var pCode = $principal.find('.opi-dis__codeInput').val() || '';
  // Keep whatever numbering the indicator already uses; default to 1 for a fresh one.
  var match = pCode.match(/^\s*(\d+)/);
  var prefix = match ? match[1] : '1';

  $principal.find('.opi-dis__codeInput').val(prefix + '.0');
  opiSyncRow($card, $principal.attr('data-opi-row'));

  var n = 1;
  $card.find('.opi-dis__row').not('.is-principal').each(function() {
    $(this).find('.opi-dis__codeInput').val(prefix + '.' + n);
    n++;
    opiSyncRow($card, $(this).attr('data-opi-row'));
  });
  opiRenumberDis($card);
}

/**
 * Opens every empty cell of a card as a real, editable period target.
 *
 * A (row, year) with no milestone behind it renders as a placeholder carrying the "+"
 * button, so the year shows nothing to type into until the button is found and clicked.
 * The cells are opened here instead, on load, through the very path that button uses --
 * the reader gets the empty box the design asks for rather than a gap to discover.
 *
 * Nothing is written by this: the cells are new milestones with no value, exactly like
 * the ones "+ Add year" creates, and they reach the database only if the user saves.
 * The form is deliberately not marked dirty for them, and a page the user cannot edit
 * never gets here -- a locked cell keeps its dash, because inventing a value for a
 * target nobody set would read as a target of zero.
 * @param {jQuery} $card the .outcome card
 * @return {boolean} true when the card had a gap to open
 */
function opiOpenGaps($card) {
  // Only gaps the user may actually fill are marked .is-empty: outcomes.ftl renders a
  // gap in a year this phase can no longer edit as a locked cell instead, never as one
  // of these.
  var $gaps = $card.find('.opi-matrix__row > .opi-cell.is-empty');
  if (!$gaps.exists()) { return false; }
  $gaps.each(function() {
    var $gap = $(this);
    var key = $gap.closest('.opi-matrix__row').attr('data-opi-row');
    $gap.replaceWith(opiNewCell($card, key, $gap.attr('data-opi-year')));
  });
  // opiNewCell leaves the unit affix and the hint blank; the row owns both.
  $card.find('.opi-matrix__row').each(function() { opiSyncRow($card, $(this).attr('data-opi-row')); });
  return true;
}

/**
 * Clones the hidden matrix-cell template as a new milestone for (row, year).
 * @param {jQuery} $card the .outcome card
 * @param {string} key the row's data-opi-row key
 * @param {string|number} year the reporting year
 * @return {jQuery} the new .opi-cell element (not yet inserted)
 */
function opiNewCell($card, key, year) {
  var $dis = $card.find('.opi-dis__row[data-opi-row="' + key + '"]');
  var $cell = $('#opiCell-template').clone(true).removeAttr('id').removeAttr('style');
  $cell.attr('data-opi-year', year);
  $cell.find('.opi-cell__year').val(year);
  $cell.find('.opi-cell__title').val($dis.find('.opi-dis__stmtInput').val() || '');
  $cell.find('.opi-cell__code').val($dis.find('.opi-dis__codeInput').val() || '');
  $cell.find('.opi-cell__unit').val($dis.find('.opi-dis__unitSelect').val() || '-1');
  $cell.find('.opi-cell__status').val('1'); // New
  if ($.fn.numericInput) {
    opiBindCellNumeric($cell.find('input.opi-cell__value'));
  }
  return $cell;
}

/**
 * Binds MARLO's numeric keydown filter to period-target cells without the side
 * effect that comes with it: numericInput() (global/js/utils.js) rewrites an
 * empty field to 0, exactly like it would for Baseline value above. On the
 * matrix that is not cosmetic -- an unfilled target would read as a real 0, so
 * the amber "Missing value" flag and the "required" hint could never fire, and
 * the next save would store targets nobody set.
 * @param {jQuery} $inputs the .opi-cell__value fields to bind
 */
function opiBindCellNumeric($inputs) {
  var $empty = $inputs.filter(function() { return $.trim($(this).val() || '') === ''; });
  $inputs.numericInput();
  $empty.val('');
}

/**
 * Reads the year columns a card already shows, in ascending order.
 * @param {jQuery} $card the .outcome card
 * @return {Array<number>} the years, ascending
 */
function opiYears($card) {
  return $card.find('.opi-matrix__head [data-opi-yearcol]').map(function() {
    return parseInt($(this).attr('data-opi-yearcol'), 10);
  }).get().filter(function(y) { return !isNaN(y); }).sort(function(a, b) { return a - b; });
}

/**
 * The reporting year of the phase being edited.
 * @return {number} the year, or the calendar year when the carrier has none
 */
function opiNowYear() {
  var now = parseInt($('#opiI18n').data('nowYear'), 10);
  return isNaN(now) ? new Date().getFullYear() : now;
}

/**
 * Adds a year column: one header cell plus one new milestone per row. The column
 * is inserted in chronological order, so any year can be added, not only the next
 * one after the last.
 * @param {jQuery} $card the .outcome card
 * @param {number} [year] the year to open; defaults to the one after the last column
 */
function opiAddYear($card, year) {
  var years = opiYears($card);
  var newYear = parseInt(year, 10);
  if (isNaN(newYear)) {
    newYear = years.length ? years[years.length - 1] + 1 : opiNowYear();
  }
  if (years.indexOf(newYear) !== -1) { return; }

  // Where the new column lands among the existing ones; the cells of every row
  // follow the header, so one index drives both.
  var pos = 0;
  while (pos < years.length && years[pos] < newYear) { pos++; }

  var $head = $card.find('.opi-matrix__head');
  var $headYears = $head.find('[data-opi-yearcol]');
  var $col = $('<span class="opi-matrix__year" />').attr('data-opi-yearcol', newYear)
    .append($('<span class="opi-matrix__yearLabel" />').text(newYear));
  if (newYear === opiNowYear()) {
    $col.addClass('is-now').append($('<span class="opi-matrix__now" />').text(opiLabel('nowLabel')));
  }
  // A column that was just opened has nothing stored behind it, so it is always
  // removable -- no canBeDeleted() question to ask.
  $col.append($('<button type="button" class="opi-rmYear" />')
    .attr({ 'aria-label': opiLabel('rmyearLabel'), title: opiLabel('rmyearLabel') })
    .html('&#10005;'));
  if (pos < $headYears.length) { $col.insertBefore($headYears.eq(pos)); } else { $head.append($col); }

  $card.find('.opi-matrix__row').each(function() {
    var $row = $(this);
    var $cell = opiNewCell($card, $row.attr('data-opi-row'), newYear);
    var $cells = $row.children('.opi-cell');
    if (pos < $cells.length) { $cell.insertBefore($cells.eq(pos)); } else { $row.append($cell); }
    // The cell is a clone of the hidden template, so its unit affix and hint are
    // still blank until the row's unit is copied onto it.
    opiSyncRow($card, $row.attr('data-opi-row'));
  });

  opiApplyGrid($card);
  updateAllIndexes();
  opiRefreshCardStatus($card);
  opiMarkDirty();
}

/**
 * Reads one of the card's own year selects. Restricted to .outcomeYear because a
 * milestone select in the leftovers block is also named "...[i].year".
 * @param {jQuery} $card the .outcome card
 * @param {RegExp} nameEnd pattern the select's name must end with
 * @return {number} the selected year, or NaN when unset
 */
function opiCardYear($card, nameEnd) {
  var value = $card.find('select.outcomeYear').filter(function() {
    return nameEnd.test($(this).attr('name') || '');
  }).first().val();
  var year = parseInt(value, 10);
  return year > 0 ? year : NaN;
}

/**
 * How many cells of one year column carry a value. Read-only cells count: they
 * are stored targets and would be deleted with the column just the same.
 * @param {jQuery} $card the .outcome card
 * @param {number} year the column's year
 * @return {number} how many values would be lost
 */
function opiYearValueCount($card, year) {
  var count = 0;
  $card.find('.opi-matrix__row').children('.opi-cell[data-opi-year="' + year + '"]').each(function() {
    if ($.trim($(this).find('.opi-cell__value').val() || '') !== '') { count++; }
  });
  return count;
}

/**
 * Drops one year column: its header plus one cell per row. The cells carry the
 * milestones' hidden inputs, so taking them out of the form is what deletes those
 * milestones on the next save. Nothing is kept -- the removal stands until the
 * page is left without saving.
 * @param {jQuery} $card the .outcome card
 * @param {number} year the column's year
 */
function opiRemoveYear($card, year) {
  var $col = $card.find('.opi-matrix__head [data-opi-yearcol="' + year + '"]');
  if (!$col.exists()) { return; }

  $col.remove();
  $card.find('.opi-matrix__row').children('.opi-cell[data-opi-year="' + year + '"]').remove();

  opiApplyGrid($card);
  updateAllIndexes();
  opiRefreshCardStatus($card);
  opiMarkDirty();
}

/**
 * Opens the "Remove <year>?" confirmation under its column.
 *
 * The popover lives on .opi-matrix-block rather than inside the header cell:
 * .opi-matrix scrolls horizontally, which would clip it.
 * @param {jQuery} $btn the clicked .opi-rmYear button
 * @param {jQuery} $card the .outcome card
 * @param {number} year the column's year
 */
function opiOpenRemoveYear($btn, $card, year) {
  opiCloseRemoveYear();
  var count = opiYearValueCount($card, year);
  var detail = count === 1
    ? opiLabel('rmyearDetailOne')
    : opiLabel('rmyearDetailMany').replace('{0}', count);

  var $pop = $('<div class="opi-rmYear__pop" role="dialog" />')
    .attr('data-opi-rmyear', year)
    .attr('aria-label', opiLabel('rmyearTitle').replace('{0}', year))
    .append($('<span class="opi-rmYear__title" />').text(opiLabel('rmyearTitle').replace('{0}', year)))
    .append($('<span class="opi-rmYear__detail" />').text(detail))
    .append($('<span class="opi-rmYear__actions" />')
      .append($('<button type="button" class="opi-rmYear__cancel" />').text(opiLabel('rmyearCancel')))
      .append($('<button type="button" class="opi-rmYear__confirm" />').text(opiLabel('rmyearConfirm'))));

  var $block = $card.find('.opi-matrix-block');
  $block.append($pop);

  var blockRect = $block[0].getBoundingClientRect();
  var btnRect = $btn[0].getBoundingClientRect();
  var width = $pop.outerWidth();
  var left = (btnRect.left - blockRect.left) + (btnRect.width / 2) - (width / 2);
  left = Math.max(8, Math.min(left, blockRect.width - width - 8));
  $pop.css({ left: Math.round(left), top: Math.round(btnRect.bottom - blockRect.top + 8) });

  $btn.addClass('is-open');
  $pop.find('.opi-rmYear__cancel').trigger('focus');
}

/**
 * Closes the remove-year confirmation.
 * @param {boolean} [restoreFocus] whether to put the focus back on its button
 */
function opiCloseRemoveYear(restoreFocus) {
  var $btn = $('.opi-rmYear.is-open');
  $('.opi-rmYear__pop').remove();
  $btn.removeClass('is-open');
  if (restoreFocus) { $btn.trigger('focus'); }
}

/**
 * Builds the option list of the add-year menu: the next five reporting years from
 * the later of the baseline and the reporting year, each with the reason it is in
 * the list. A year the card already has a column for is listed too, disabled, so
 * the menu explains why it is not on offer instead of silently leaving a gap.
 * @param {jQuery} $card the .outcome card
 * @return {Array<Object>} five {year, note, kind, taken} entries, ascending
 */
function opiYearChoices($card) {
  var years = opiYears($card);
  var now = opiNowYear();
  var baseline = opiCardYear($card, /\.startYear$/);
  var closing = opiCardYear($card, /\]\.year$/);
  // Nothing before the reporting year can still be planned, and nothing before the
  // baseline belongs to the programme.
  var first = isNaN(baseline) ? now : Math.max(baseline, now);

  var choices = [];
  for (var y = first; choices.length < 5; y++) {
    var taken = years.indexOf(y) !== -1;
    var kind = taken ? 'taken' : ((!isNaN(closing) && y > closing) ? 'after' : (y === now ? 'now' : 'within'));
    choices.push({
      year: y,
      kind: kind,
      taken: taken,
      note: opiLabel('year' + kind.charAt(0).toUpperCase() + kind.slice(1))
    });
  }
  return choices;
}

/**
 * Opens the add-year menu under its button.
 * @param {jQuery} $btn the .opi-addYear button
 */
function opiOpenYearMenu($btn) {
  var $card = $btn.closest('.outcome');
  var baseline = opiCardYear($card, /\.startYear$/);
  var closing = opiCardYear($card, /\]\.year$/);
  var range = (isNaN(baseline) || isNaN(closing))
    ? opiLabel('yearRangeUnset')
    : opiLabel('yearRange').replace('{0}', baseline).replace('{1}', closing);

  var $menu = $('<div class="opi-yearMenu" role="dialog" />').attr('aria-label', opiLabel('yearTitle'));
  $menu.append($('<div class="opi-yearMenu__head" />')
    .append($('<span class="opi-yearMenu__title" />').text(opiLabel('yearTitle')))
    .append($('<span class="opi-yearMenu__range" />').text(range)));

  $.each(opiYearChoices($card), function(i, choice) {
    var $option = $('<button type="button" class="opi-yearMenu__option" />')
      .attr('data-opi-year', choice.year)
      .prop('disabled', choice.taken)
      .append($('<span class="opi-yearMenu__year" />').text(choice.year))
      .append($('<span class="opi-yearMenu__note" />').addClass('is-' + choice.kind).text(choice.note));
    if (choice.taken) {
      $option.append('<span class="opi-yearMenu__check" aria-hidden="true">'
        + '<svg width="13" height="13" viewBox="0 0 16 16" fill="none">'
        + '<path d="M3 8.4 6.2 11.6 13 4.8" stroke="currentColor" stroke-width="1.9" '
        + 'stroke-linecap="round" stroke-linejoin="round"></path></svg></span>');
    }
    $menu.append($option);
  });

  $menu.append('<span class="opi-yearMenu__sep"></span>');
  $menu.append($('<div class="opi-yearMenu__custom-row" />')
    .append($('<input type="text" class="opi-yearMenu__custom" inputmode="numeric" maxlength="4" />')
      .attr('placeholder', opiLabel('yearOther')).attr('aria-label', opiLabel('yearOther')))
    .append($('<button type="button" class="opi-yearMenu__add" disabled />').text(opiLabel('yearAdd')))
    .append($('<span class="opi-yearMenu__customNote" />')));

  $btn.attr('aria-expanded', 'true').addClass('is-open');
  $btn.closest('.opi-addYearWrap').append($menu);
  opiRefreshYearMenuCustom($menu);
}

/**
 * Closes whichever add-year menu is open.
 */
function opiCloseYearMenu() {
  $('.opi-yearMenu').remove();
  $('.opi-addYear').attr('aria-expanded', 'false').removeClass('is-open');
}

/**
 * Gates the "Other year" Add button: four digits, and not a year the card already
 * shows a column for.
 * @param {jQuery} $menu the .opi-yearMenu element
 */
function opiRefreshYearMenuCustom($menu) {
  var raw = String($menu.find('.opi-yearMenu__custom').val() || '');
  var year = parseInt(raw, 10);
  var taken = /^\d{4}$/.test(raw) && opiYears($menu.closest('.outcome')).indexOf(year) !== -1;
  $menu.find('.opi-yearMenu__add').prop('disabled', !/^\d{4}$/.test(raw) || taken);
  $menu.find('.opi-yearMenu__customNote').text(taken ? opiLabel('yearTaken') : '');
}

/**
 * Paints the Yes / No answer of "Does this indicator have disaggregations?".
 * The answer has no column of its own -- outcomes.ftl recomputes it from the number of
 * distinct milestone statements -- so callers must make the rows match the answer before
 * painting it, or it will not survive a reload.
 * @param {jQuery} $card the .outcome card
 * @param {boolean} yes whether the indicator has disaggregations
 */
function opiSetDisAnswer($card, yes) {
  $card.find('.opi-dis__yes').toggleClass('is-on', yes).attr('aria-pressed', String(yes));
  $card.find('.opi-dis__no').toggleClass('is-on', !yes).attr('aria-pressed', String(!yes));
  $card.find('.opi-dis').toggle(yes);
  $card.find('.opi-matrix__row').not('.is-principal').toggle(yes);
  var $note = $card.find('[data-opi-disnote]');
  $note.text($note.data(yes ? 'yes' : 'no'));

  // The answer is a stored field now, so record it. Adding or clearing rows already marks the
  // form dirty, but toggling between two states that need no row change does not.
  var $answer = $card.find('.opi-disAnswer');
  var value = yes ? 'true' : 'false';
  // Enable it on the first answer: until then it is deliberately left out of the submit.
  if ($answer.val() !== value || $answer.prop('disabled')) {
    $answer.val(value).prop('disabled', false);
    opiMarkDirty();
  }

  // The rows only count while they are on screen, so the tally has to follow the
  // answer. Toggling Yes/No without adding or clearing a row reaches no other
  // recount path.
  opiRefreshCardStatus($card);
}

/**
 * Drops one disaggregation row and the matrix row holding its milestones. The milestones
 * stop being submitted, which is what makes OutcomesAction delete them on save.
 * Callers refresh once with opiAfterDisChange when they are done removing.
 * @param {jQuery} $card the .outcome card
 * @param {jQuery} $row the .opi-dis__row to drop
 */
function opiDropDisRow($card, $row) {
  opiMatrixRow($card, $row.attr('data-opi-row')).remove();
  $row.remove();
}

/**
 * Refreshes everything that depends on the set of disaggregation rows. opiRecodeRows
 * renumbers the # column on its way out, so it covers the row numbering too.
 * @param {jQuery} $card the .outcome card
 */
function opiAfterDisChange($card) {
  opiRecodeRows($card);
  updateAllIndexes();
  opiRefreshCardStatus($card);
  opiMarkDirty();
}

/**
 * Removes every disaggregation, so answering "No" becomes true in the data too.
 * Saving deletes the milestones behind the removed rows, so it asks first. A row whose
 * milestones are already in use cannot be deleted; nothing is removed in that case and
 * the answer stays on "Yes".
 * @param {jQuery} $card the .outcome card
 * @return {boolean} true when the card is left with no disaggregations
 */
function opiClearDisRows($card, $trigger, onCleared) {
  var $rows = $card.find('.opi-dis__row').not('.is-principal');
  if (!$rows.exists()) {
    onCleared();
    return;
  }
  if ($rows.find('.opi-dis__delete:disabled').exists()) {
    opiConfirm({
        title: opiLabel('disClearBlockedTitle'),
        detail: opiLabel('disClearBlocked'),
        trigger: $trigger,
        cancelLabel: opiLabel('dialogClose')
    });
    return;
  }
  opiConfirm({
      title: opiLabel('disClearTitle'),
      // Names what goes: the rows, and the values already captured against them.
      detail: opiLabel($rows.length === 1 ? 'disClearDetailOne' : 'disClearDetailMany')
        .replace('{0}', $rows.length),
      trigger: $trigger,
      confirmLabel: opiLabel('disClearConfirmButton'),
      cancelLabel: opiLabel('dialogCancel'),
      onConfirm: function() {
        $rows.each(function() { opiDropDisRow($card, $(this)); });
        opiAfterDisChange($card);
        onCleared();
      }
  });
}

/**
 * Adds a disaggregation row plus one new milestone per existing year column.
 * @param {jQuery} $card the .outcome card
 */
function opiAddDisRow($card) {
  // Disaggregations are stored on the milestones of each year column, so a row added
  // before any column exists would have nowhere to save to. Open the first one for it.
  if (!$card.find('.opi-matrix__head [data-opi-yearcol]').exists()) {
    opiAddYear($card);
  }
  var key = 'jr' + (++opiRowSeq);
  var $pDis = $card.find('.opi-dis__row.is-principal');

  var $row = $pDis.clone(false).removeClass('is-principal').attr('data-opi-row', key).attr('draggable', 'true');
  $row.find('.opi-dis__pBadge').remove();
  // The code is generated by opiRecodeRows, so it stays read-only on new rows too.
  $row.find('.opi-dis__codeInput').val('');
  $row.find('.opi-dis__stmtInput').val('').prop('readonly', false).removeAttr('title');
  // Carry over the principal row's unit: "Not applicable" blanks and locks every cell
  // (opiApplyNotApplicable), which would leave the new row impossible to fill in.
  // .clone() copies the markup, not a value assigned through .val(), and on a brand-new
  // indicator the principal's unit is only ever set that way, so read it explicitly.
  $row.find('.opi-dis__unitSelect')
    .val($pDis.find('.opi-dis__unitSelect').val() || '-1')
    .prop('disabled', false);
  // The business rule is per row, so the clone must not inherit the principal's.
  $row.find('.opi-dis__ruleSelect').val('none').prop('disabled', false);
  // The clone carries the principal row's delete control, which outcomes.ftl renders
  // disabled ("cannot be deleted"). A row the user just added is always removable, so
  // replace it outright instead of only filling in a missing one.
  $row.find('.opi-dis__actions').empty()
    .append('<button type="button" class="opi-dis__delete" aria-label="Delete disaggregation">✕</button>');
  $card.find('.opi-dis__rows').append($row);

  var $mPrincipal = $card.find('.opi-matrix__row.is-principal');
  var $mRow = $('<div class="opi-matrix__row" />').attr('data-opi-row', key).attr('style', $mPrincipal.attr('style') || '');
  var $label = $('<span class="opi-matrix__label" />')
    .append('<span class="opi-matrix__rowcode" data-opi-rowcode>&nbsp;</span>')
    .append($('<span class="opi-matrix__stmtWrap" />')
      .append('<span class="opi-matrix__stmt" data-opi-rowstmt></span>')
      .append('<span class="opi-matrix__sub" data-opi-rowsub></span>'));
  $mRow.append($label);
  $card.find('.opi-matrix__head [data-opi-yearcol]').each(function() {
    $mRow.append(opiNewCell($card, key, $(this).attr('data-opi-yearcol')));
  });
  $card.find('.opi-matrix__rows').append($mRow);

  opiAfterDisChange($card);
  $row.find('.opi-dis__stmtInput').trigger('focus');
}

/**
 * Recomputes the shared grid-template-columns after a column change.
 * @param {jQuery} $card the .outcome card
 */
function opiApplyGrid($card) {
  var n = $card.find('.opi-matrix__head [data-opi-yearcol]').length;
  var cols = 'minmax(260px,1fr)' + (n > 0 ? ' repeat(' + n + ',132px)' : '');
  $card.find('.opi-matrix__head, .opi-matrix__row').css('grid-template-columns', cols);
}

/**
 * Puts one cell into (or out of) the "Not applicable" state.
 *
 * A row that is Not applicable captures nothing, so the value is dropped rather
 * than kept aside: switching back to # of / % leaves the cell empty and asking to
 * be filled in again. What is on screen is therefore what a save will store.
 *
 * @param {jQuery} $cell the .opi-cell element
 * @param {boolean} na whether the row's unit is Not applicable
 */
function opiApplyNotApplicable($cell, na) {
  var $value = $cell.find('.opi-cell__value');
  if (!$value.exists()) { return; }
  // A locked cell only mirrors what was stored; its value travels in a hidden
  // input, so clearing the box here would just misreport it.
  if ($cell.hasClass('is-readonly')) { return; }

  if (na) {
    $cell.addClass('is-na');
    $value.val('').prop('readonly', true);
  } else if ($cell.hasClass('is-na')) {
    $cell.removeClass('is-na');
    $value.prop('readonly', false);
  }
}

/**
 * Amber-flags an empty cell value and refreshes the percentage hint.
 * A row whose unit label contains "%" resolves against the principal row's
 * value for the same year, like the design's derived hint.
 * @param {jQuery} $value the .opi-cell__value input
 */
function opiRefreshCell($value) {
  var $cell = $value.closest('.opi-cell');
  var raw = $.trim($value.val() || '');
  var notApplicable = $cell.hasClass('is-na');
  // A locked cell is never flagged: amber and "Required" ask for an edit the
  // user is not allowed to make.
  var readOnly = $cell.hasClass('is-readonly');
  $cell.toggleClass('is-missing', raw === '' && !notApplicable && !readOnly);

  var $card = $cell.closest('.outcome');
  var $mRow = $cell.closest('.opi-matrix__row');
  var $dis = $card.find('.opi-dis__row[data-opi-row="' + $mRow.attr('data-opi-row') + '"]');
  var unitText = $dis.find('.opi-dis__unitSelect option:selected').text() || '';
  var isPct = unitText.indexOf('%') !== -1;
  var isNA = /not applicable/i.test(unitText);
  $cell.find('.opi-cell__affix').text(isNA ? '' : (isPct ? '%' : '#'));
  var $hint = $cell.find('[data-opi-hint]');
  var hint = '';
  if (notApplicable || isNA) {
    hint = 'n/a';
  } else if (raw === '') {
    hint = readOnly ? '' : opiLabel('requiredLabel');
  } else if (unitText.indexOf('%') !== -1 && raw !== '' && !$mRow.hasClass('is-principal')) {
    var year = $cell.attr('data-opi-year');
    var baseRaw = $card.find('.opi-matrix__row.is-principal .opi-cell[data-opi-year="' + year + '"] .opi-cell__value').val();
    var base = parseFloat(String(baseRaw || '').replace(/[,\s]/g, ''));
    var pct = parseFloat(raw.replace(/[,\s]/g, ''));
    if (!isNaN(base) && !isNaN(pct)) {
      hint = '≈ ' + Math.round(base * pct / 100).toLocaleString('en-US');
    }
  }
  $hint.text(hint).toggleClass('is-required', raw === '' && !isNA && !notApplicable && !readOnly);
}

/**
 * Renumbers questions, refreshes the count badge, and gates the add button
 * while any question is still empty (design rule).
 * @param {jQuery} $card the .outcome card
 */
function opiRefreshQuestions($card) {
  var $rows = $card.find('.baselineIndicator').filter(function() {
    return $(this).attr('id') !== 'baselineIndicator-template';
  });
  $rows.each(function(i) { $(this).find('.index').text(i + 1); });
  var $qc = $card.find('[data-opi-qcount]');
  $qc.text($rows.length);
  var $noun = $qc.parent();
  if ($noun.exists()) {
    $noun.contents().filter(function() { return this.nodeType === 3; }).remove();
    $noun.append(' ' + opiLabel($rows.length === 1 ? 'qOne' : 'qMany'));
  }
  $card.find('.opi-q__empty').toggle($rows.length === 0);

  var hasEmpty = false;
  $rows.find('.opi-q__input').each(function() {
    if ($.trim($(this).val() || '') === '') { hasEmpty = true; }
  });
  var $add = $card.find('.addBaselineIndicator');
  $add.prop('disabled', hasEmpty);
  $card.find('[data-opi-qnote]').text(hasEmpty ? ($add.data('blockedTitle') || '') : '');
}


/**
 * Counts the words in a value the way BaseValidator.wordCount does server-side: trim, then
 * split on runs of whitespace. Kept identical on purpose -- the two have to agree on whether
 * a statement is over the 100-word limit.
 * @param {string} text the value to measure
 * @return {number} how many words it holds
 */
function opiWordCount(text) {
  var trimmed = $.trim(text || '');
  return trimmed === '' ? 0 : trimmed.split(/\s+/).length;
}

/**
 * The required fields one indicator card is still missing: every shown required marker with an
 * empty control, every disaggregation row missing its statement, every empty matrix cell, and the
 * period targets themselves when the indicator has none.
 *
 * Returns the elements rather than a tally so that the card badge and the check report can be
 * driven by one predicate: the badge counts them, and the check outlines the ones the validator
 * could not see because the indicator is not saved yet.
 * @param {jQuery} $card the .opi-card element
 * @return {jQuery} the controls standing for each gap
 */
function opiMissingFields($card) {
  var $missing = $();
  $card.find('.opi-card__body .requiredTag').each(function() {
    var $tag = $(this);
    if (!$tag.is(':visible')) { return; }
    var $group = $tag.closest('.form-group, .opi-grid5 > div, .opi-fieldRow__acronym, .opi-fieldRow__statement');
    if (!$group.exists()) { $group = $tag.parent(); }
    var $field = $group.find('input:not([type="hidden"]), textarea, select').first();
    if (!$field.exists()) { return; }
    var value = $.trim($field.val() || '');
    // -1 means "nothing chosen" for every select here except the target unit. There, -1 is
    // the header option and it is labelled "Not Applicable" on purpose
    // (outcome.selectTargetUnit.placeholder), which the section's own instructions tell the
    // user to pick when the indicator has no quantifiable target -- so it is an answer, not a
    // gap. OutcomeValidator reads it the same way.
    var blankOnly = $field.is('select.targetUnit');
    if (value === '' || (value === '-1' && !blankOnly)) {
      $missing = $missing.add($field);
    } else if ($field.is('textarea.limitWords-100') && opiWordCount(value) > 100) {
      // OutcomeValidator rejects the statement on the same two grounds -- empty, or over 100
      // words -- so both are one gap here too, counted the way it counts them.
      $missing = $missing.add($field);
    }
  });
  // Disaggregation rows carry their required marker on the column header rather than
  // on every cell, so they are counted here instead of through .requiredTag. The
  // principal row is skipped: its statement and unit only mirror the indicator's own
  // Statement and Target unit, which the loop above already counted.
  // Only the statement can be missing. "Not applicable" is a real answer to the unit,
  // not an empty one -- the row simply captures no value for any year.
  $card.find('.opi-dis:visible .opi-dis__row').not('.is-principal').each(function() {
    var $stmt = $(this).find('.opi-dis__stmtInput');
    if ($.trim($stmt.val() || '') === '') { $missing = $missing.add($stmt); }
  });
  $card.find('.opi-cell__value:visible').each(function() {
    var $cell = $(this).closest('.opi-cell');
    if ($cell.hasClass('is-na') || $cell.hasClass('is-readonly')) { return; }
    if ($.trim($(this).val() || '') === '') { $missing = $missing.add($(this)); }
  });
  // Period Targets is itself required: an indicator with no year column has none at all, which
  // the validator reports as an empty list against the same container. Counted once, as it does.
  var $targets = $card.find('.milestones-list').first();
  if ($targets.exists() && !$targets.find('.opi-cell, .srfSlo').exists()) {
    $missing = $missing.add($targets);
  }
  return $missing;
}

/**
 * How many required fields one indicator card is still missing.
 * @param {jQuery} $card the .opi-card element
 * @return {number} how many required fields are still empty
 */
function opiCountMissing($card) {
  return opiMissingFields($card).length;
}

/**
 * The indicator cards on the page, leaving out the hidden template and the untouched starter.
 * @return {jQuery} the real indicator cards
 */
function opiIndicatorCards() {
  return $('.outcomes-list > .opi-card').filter(function() {
    return $(this).attr('id') !== 'outcome-template' && $(this).is(':visible');
  });
}

/**
 * Everything the section is still missing: the gaps inside each indicator, or -- when the
 * component has no indicator at all -- the single gap the validator reports for that.
 * @return {number} how many gaps the section has
 */
function opiSectionMissing() {
  var $cards = opiIndicatorCards();
  if ($cards.length === 0) { return 1; }
  var total = 0;
  $cards.each(function() { total += opiCountMissing($(this)); });
  return total;
}

/**
 * Repaints the status pill of one indicator card.
 * @param {jQuery} $card the .opi-card element
 */
function opiRefreshCardStatus($card) {
  if (!$card || !$card.exists() || $card.attr('id') === 'outcome-template') { return; }
  var $pill = $card.find('[data-opi-status]').first();
  if (!$pill.exists()) { return; }
  var missing = opiCountMissing($card);
  if (missing === 0) {
    $pill.removeClass('is-missing').text(opiLabel('statusComplete'));
  } else {
    $pill.addClass('is-missing')
      .text(missing + ' ' + opiLabel(missing === 1 ? 'statusMissingOne' : 'statusMissingMany'));
  }
  opiRefreshSummary();
}

/**
 * Repaints every visible indicator card and the section summary.
 */
function opiRefreshAllStatuses() {
  $('.outcomes-list > .opi-card').each(function() {
    opiRefreshCardStatus($(this));
  });
}

/**
 * Updates the "N indicators - M fields still missing" line above the list.
 */
function opiRefreshSummary() {
  var $summary = $('[data-opi-summary]');
  if (!$summary.exists()) { return; }
  var $cards = opiIndicatorCards();
  var total = opiSectionMissing();
  var count = $cards.length;
  var text = count + ' ' + opiLabel(count === 1 ? 'countOne' : 'countMany');
  if (total > 0) {
    text += ' · ' + total + ' ' + opiLabel(total === 1 ? 'summaryMissingOne' : 'summaryMissingMany');
  } else {
    text += ' · ' + opiLabel('summaryComplete');
  }
  $summary.text(text);
  opiDecorateSidebar();
}

/* ==========================================================================
 * "Check for missing fields"
 *
 * The sidebar button runs OutcomeValidator over the saved section and comes back
 * with the fields it flagged. programSubmit.js hands the result here instead of
 * raising its own dialog: the redesign reports in place -- the flagged fields are
 * outlined where they stand and the tally is announced through a live region --
 * and it has no green check marks to send anyone looking for.
 *
 * The validator answers for what is stored, so the report is cleared as soon as
 * the user edits the field it landed on.
 * ========================================================================== */

/**
 * Resolves one invalidFields key onto the element that should carry its error.
 *
 * The keys are "<type>-<field name>", the shape fieldsValidation.js already reads after
 * a save. A field with a control of its own is marked on the control; one the matrix
 * keeps hidden -- a cell value, a row statement -- is marked on the stand-in element
 * opiTagRowForValidation() stamped with the field name, non-word characters stripped.
 *
 * @param {string} key an invalidFields key, e.g. "input-outcomesForm[0].value"
 * @return {jQuery} the element to mark, empty when the field is not on this page
 */
function opiValidationAnchor(key) {
  var split = String(key).indexOf('-');
  if (split < 0) { return $(); }
  var field = String(key).substring(split + 1);
  var $page = $('.opi-page');

  // Deliberately not filtered by :visible. A collapsed card hides its whole body, so
  // filtering here would lose every field inside it -- the report would say those gaps
  // were "not on this page" and the card would never be opened to show them. Whether the
  // element is on screen is the caller's problem, and the caller opens the card.
  var $control = $page.find('[name="' + field + '"]').not('[type="hidden"]').first();
  if ($control.exists()) { return $control; }

  // The stand-in carries the field name as a class, which is also how the shared
  // highlighter reaches a field it cannot select by name.
  var $standIn = $page.find('.' + field.replace(/\W+/g, '')).first();
  if ($standIn.exists()) { return $standIn; }

  return $page.find('div[listname="' + field + '"]').first();
}

/**
 * Drops every mark the last check left, without touching the ones the shared
 * highlighter paints after a save -- only what this reporter added carries
 * .opi-checkFlag.
 */
function opiClearValidationReport() {
  var $page = $('.opi-page');
  $page.find('.opi-checkNote').remove();
  $page.find('.opi-checkFlag').removeClass('opi-checkFlag fieldError').removeAttr('aria-invalid');
  $page.find('[data-opi-check-status]').removeClass('is-missing is-ok').text('');
}

/**
 * Marks one field, and returns whether it took the mark.
 * @param {jQuery} $anchor the element resolved for the field
 * @return {boolean} true when something was marked
 */
function opiMarkInvalidField($anchor, message) {
  if (!$anchor.exists() || $anchor.hasClass('opi-checkFlag')) { return $anchor.exists(); }
  $anchor.addClass('opi-checkFlag fieldError');
  if ($anchor.is('input, textarea, select')) {
    $anchor.attr('aria-invalid', 'true');
  }
  // A matrix cell is one box in a grid of them: an inline note per cell would break the
  // columns apart, so there the outline carries it and the card's own "n fields missing"
  // badge says it in words.
  if ($anchor.closest('.opi-cell').exists()) { return true; }
  // The validator sends a note per field. Most say "Required Field", for which the design's own
  // shorter wording reads better; anything else is specific to that field -- a closing year that
  // is filled in but does not reach the last column, say -- and is shown as it came.
  var note = (message && message !== 'Required Field') ? message : opiLabel('checkRequired');
  if (!note) { return true; }
  // Placed straight after the control it belongs to. Looking for a wrapper to append to was
  // wrong: the card itself carries .form-group, so a field with no closer wrapper resolved to
  // the whole card -- and since only one note per wrapper is added, three of four fields lost
  // their note and the one that survived sat at the foot of the card, away from its field.
  // Every flagged control gets exactly one note because a second pass returns above.
  var id = 'opiCheckNote-' + (opiRowSeq++);
  // The matrix scrolls sideways and lays its rows out on a grid, so a note belonging to the
  // matrix as a whole -- "this indicator has no period targets" -- goes under the block rather
  // than inside it.
  var $after = $anchor.closest('.opi-matrix').exists() ? $anchor.closest('.opi-matrix-block') : $anchor;
  $after.after($('<span class="opi-checkNote" id="' + id + '"></span>').text(note));
  var described = $anchor.attr('aria-describedby');
  $anchor.attr('aria-describedby', described ? described + ' ' + id : id);
  return true;
}

/**
 * Opens one indicator card if it is collapsed, so a field flagged inside it is not marked
 * out of sight.
 *
 * Deliberately not a click on the caret: that toggles, so a card holding two flagged fields
 * would be opened by the first and shut again by the second. The collapsed state lives in two
 * places -- .is-collapsed on the card, which styles it, and .minimizeOutcome on the body,
 * which hides it -- and "Collapse all" writes both, so both are cleared here.
 * @param {jQuery} $card the .outcome card
 */
function opiExpandCard($card) {
  if (!$card.exists() || !$card.hasClass('is-collapsed')) { return; }
  $card.removeClass('is-collapsed');
  $card.find('.to-minimize-outcome').removeClass('minimizeOutcome');
  $card.find('.btn-expand-Outcome').attr('aria-expanded', 'true');
}

/**
 * Presents the result of "Check for missing fields" on the OPI page.
 *
 * Registered as window.impactPathwayValidationReport, which programSubmit.js calls in
 * place of its own dialog.
 *
 * @param {Array<Object>} results one entry per checked section
 * @param {boolean} complete whether every checked section came back clean
 */
function opiReportValidation(results, complete) {
  opiClearValidationReport();

  var keys = [];
  var messages = {};
  $.each(results || [], function(i, result) {
    if (!result || result.sectionName !== 'outcomes') { return; }
    $.each(result.invalidFields || {}, function(key, message) {
      keys.push(key);
      messages[key] = message;
    });
  });

  // An indicator with nothing stored yet is invisible to the validator, which reads the saved
  // record: all it can answer for a component in that state is "there are no indicators", against
  // the whole list. That is true and useless -- the card is right there with its boxes empty. So
  // the gaps of any card that is not saved yet are added here, from the same predicate the badge
  // counts with, and the list-level finding is dropped because those gaps say it better.
  var $unsaved = $('.outcomes-list > .opi-card').filter(function() {
    return $(this).attr('id') !== 'outcome-template' && $(this).is(':visible')
      && $.trim($(this).find('input.outcomeId').first().val() || '') === '';
  });
  var $ownGaps = $();
  $unsaved.each(function() { $ownGaps = $ownGaps.add(opiMissingFields($(this))); });
  if ($ownGaps.length) {
    keys = $.grep(keys, function(key) { return key !== 'list-outcomes'; });
  }

  // Counted by element rather than by key: the validator reports against the stored record,
  // where one gap on screen can be several -- an empty row statement is one box here and one
  // entry per year column there. The number has to be the number of boxes the user can see
  // outlined, or it sends them looking for something that is not there.
  var $first = $();
  var $flagged = $();
  var unreached = 0;
  $.each(keys, function(i, key) {
    var $anchor = opiValidationAnchor(key);
    if (!$anchor.exists()) { unreached++; return; }
    opiExpandCard($anchor.closest('.outcome'));
    if (!$anchor.hasClass('opi-checkFlag')) { $flagged = $flagged.add($anchor); }
    opiMarkInvalidField($anchor, messages[key]);
    if (!$first.exists()) { $first = $anchor; }
  });

  $ownGaps.each(function() {
    var $anchor = $(this);
    opiExpandCard($anchor.closest('.outcome'));
    if (!$anchor.hasClass('opi-checkFlag')) { $flagged = $flagged.add($anchor); }
    opiMarkInvalidField($anchor);
    if (!$first.exists()) { $first = $anchor; }
  });

  var $status = $('.opi-page, #secondaryMenu').find('[data-opi-check-status]');
  var total = $flagged.length + unreached;
  var message;
  if (complete && total === 0) {
    message = opiLabel('checkComplete');
    $status.addClass('is-ok');
  } else {
    message = total + ' ' + opiLabel(total === 1 ? 'checkMissingOne' : 'checkMissingMany');
    if (unreached > 0) {
      message += ' ' + opiLabel('checkElsewhere');
    }
    $status.addClass('is-missing');
  }
  $status.text(message);

  if ($first.exists()) {
    $('html, body').animate({scrollTop: $first.offset().top - 140}, 400);
    if ($first.is('input:visible, textarea:visible, select:visible')) {
      $first.trigger('focus');
    }
  }
}

/**
 * Clears a field's mark once the user answers it, so the report never outlives the gap
 * it described. The count itself is repainted by the live recount already bound above.
 */
function opiAttachValidationClearing() {
  $('.opi-page').on('change keyup', '.opi-checkFlag', function() {
    var $field = $(this);
    if ($.trim($field.val() || '') === '') { return; }
    var described = $field.attr('aria-describedby');
    $field.removeClass('opi-checkFlag fieldError').removeAttr('aria-invalid');
    var $slot = $field.closest('.input, .form-group');
    $slot.find('.opi-checkNote').remove();
    if (described) { $field.removeAttr('aria-describedby'); }
  });
  // A cell value is marked on its wrapper, not on the input, so it is cleared from there.
  $('.opi-page').on('change keyup', '.opi-cell__value', function() {
    if ($.trim($(this).val() || '') === '') { return; }
    $(this).closest('.opi-checkFlag').removeClass('opi-checkFlag fieldError');
  });
}

/* ==========================================================================
 * Confirmation dialog
 *
 * Destructive actions in this section used to go through window.confirm(), which
 * is unstyled, blocks the whole page until a person dismisses it by hand -- so
 * browser automation stalls on it -- and cannot say what is about to be lost in
 * more than one line of plain text.
 *
 * opiConfirm() replaces it: one dialog, callback-based, that both the
 * disaggregations toggle and the indicator's remove button raise. Nothing blocks:
 * the caller's work happens in onConfirm.
 * ========================================================================== */

/** The element that opened the dialog, so focus can go back to it. @type {jQuery} */
var opiDialogTrigger = null;

/**
 * Closes the jQuery UI tooltip a control may have open.
 *
 * The widget is delegated from the document in global.js with track: true, so it closes on
 * the target's own mouseleave. The dialog's backdrop covers the trigger without the pointer
 * having to move, and a covered element gets no mouseleave until it moves again -- so the
 * tooltip would stay on screen with nothing left to dismiss it.
 *
 * @param {jQuery} $el the control whose tooltip should go
 */
function opiDismissTooltip($el) {
  if (!$el || !$el.exists()) { return; }
  // The events jQuery UI itself binds to close it, so the widget keeps its own bookkeeping.
  $el.trigger('mouseleave').trigger('focusout');
}

/**
 * Closes the dialog, if one is open, and puts the focus back where it came from.
 * @param {Function} [callback] what to run once it is gone
 */
function opiCloseDialog(callback) {
  var $dialog = $('.opi-dialog');
  if (!$dialog.exists()) { return; }
  $dialog.remove();
  $(document).off('keydown.opiDialog focusin.opiDialog');
  var $trigger = opiDialogTrigger;
  opiDialogTrigger = null;
  // The trigger may have been removed by the very action that was confirmed.
  if ($trigger && $trigger.exists() && $trigger.is(':visible')) {
    $trigger.trigger('focus');
    // The focus is given back by code, not by a gesture, so the delegated tooltip would
    // open with no pointer over the control and nothing to close it again.
    opiDismissTooltip($trigger);
  }
  if (callback) { callback(); }
}

/**
 * Raises the section's confirmation dialog.
 *
 * @param {Object} options
 * @param {string} options.title the heading, which names the action
 * @param {string} options.detail what will be lost, in words
 * @param {jQuery} options.trigger the control that opened it; focus returns here
 * @param {string} [options.confirmLabel] omit for a notice with only a dismiss button
 * @param {string} options.cancelLabel the dismiss label
 * @param {Function} [options.onConfirm] run after the dialog closes, on confirm only
 */
function opiConfirm(options) {
  opiCloseDialog();
  opiDialogTrigger = options.trigger && options.trigger.exists() ? options.trigger : null;
  opiDismissTooltip(opiDialogTrigger);

  var id = 'opiDialog-' + (opiRowSeq++);
  var $title = $('<h2 class="opi-dialog__title" id="' + id + '-title" />').text(options.title);
  var $detail = $('<p class="opi-dialog__detail" id="' + id + '-detail" />').text(options.detail);
  var $cancel = $('<button type="button" class="opi-dialog__cancel" />').text(options.cancelLabel);
  var $actions = $('<div class="opi-dialog__actions" />').append($cancel);

  var $confirm = null;
  if (options.confirmLabel) {
    $confirm = $('<button type="button" class="opi-dialog__confirm" />').text(options.confirmLabel);
    $actions.append($confirm);
  }

  var $box = $('<div class="opi-dialog__box" role="dialog" aria-modal="true" />')
    .attr('aria-labelledby', id + '-title')
    .attr('aria-describedby', id + '-detail')
    .append($title).append($detail).append($actions);
  var $dialog = $('<div class="opi-dialog" />').append($box);

  $('body').append($dialog);

  $cancel.on('click', function() { opiCloseDialog(); });
  if ($confirm) {
    $confirm.on('click', function() { opiCloseDialog(options.onConfirm); });
  }
  // Clicking the backdrop cancels, the way Esc does. Clicks inside the box must not.
  $dialog.on('click', function(e) {
    if (e.target === this) { opiCloseDialog(); }
  });

  $(document).on('keydown.opiDialog', function(e) {
    if (e.key === 'Escape' || e.keyCode === 27) {
      e.preventDefault();
      opiCloseDialog();
      return;
    }
    if (e.key !== 'Tab' && e.keyCode !== 9) { return; }
    // Keeps Tab inside the dialog.
    var $stops = $box.find('button:visible');
    if (!$stops.exists()) { return; }
    var first = $stops.first()[0];
    var last = $stops.last()[0];
    if (e.shiftKey && document.activeElement === first) {
      e.preventDefault();
      last.focus();
    } else if (!e.shiftKey && document.activeElement === last) {
      e.preventDefault();
      first.focus();
    }
  });
  // A click that lands outside the dialog -- on a control still in the page behind it --
  // would take the focus out of a dialog that is meant to be modal.
  $(document).on('focusin.opiDialog', function(e) {
    if (!$box[0].contains(e.target)) {
      e.stopPropagation();
      $box.find('button:visible').first().trigger('focus');
    }
  });

  // The destructive button is never the one focus lands on.
  $cancel.trigger('focus');
}
