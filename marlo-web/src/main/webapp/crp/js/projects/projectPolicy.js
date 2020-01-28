var caseStudiesName;
var $elementsBlock;
var currentSubIdo;

$(document).ready(init);

function init() {

  // Add Events
  attachEvents();

  // Add select2
  addSelect2();

  // Add Geographic Scope
  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function(event,id,name) {
    setGeographicScope(this);
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

  // This function enables launch the pop up window
  popups();

  // Add amount format
  $('input.currencyInput').currencyInput();

  $('.ccRelevanceBlock input:radio').on('change', function() {
    var $commentBox = $(this).parents('.ccRelevanceBlock').find('.ccCommentBox');
    if(this.value != 3) {
      $commentBox.slideDown();
    } else {
      $commentBox.slideUp();
    }
  });
}

function attachEvents() {

  $('select.elementType-repIndPolicyType').on("addElement removeElement", function(event,id,name) {
    // Other Please Specify
    if(id == 4) {
      if(event.type == "addElement") {
        $('.block-pleaseSpecify').slideDown();
      }
      if(event.type == "removeElement") {
        $('.block-pleaseSpecify').slideUp();
      }
    }
  });

  // On change policyInvestimentTypes
  $('select.policyInvestimentTypes').on('change', function() {
    console.log(this.value);
    if(this.value == 3) {
      $('.block-budgetInvestment').slideDown();
    } else {
      $('.block-budgetInvestment').slideUp();
    }
  });

  $('select.maturityLevel').on('change', function() {
    var id = this.value;
    if((id == 4) || (id == 5)) {
      $('.evidences-block .requiredTag').slideDown();
    } else {
      $('.evidences-block .requiredTag').slideUp();
    }
    if(id == 3) {
      $('.block-researchMaturity').slideDown();
    }
    else {
      $('.block-researchMaturity').slideUp();
    }
  });

//On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

  // Add a Sub IDO
  $('.addSubIdo').on('click', addSubIdo);
  // Remove a Sub IDO
  $('.removeSubIdo').on('click', removeSubIdo);

  // Change contribution percentage
  $('input.contribution').on('keyup', function() {
    var $text = $(this).parents('.policy').find('p.contributioRem');
    var $contributions = $(this).parents('.subIdos-list').find('input.contribution');
    updateTotalContribution($contributions, $text);
  });
  $('input.contribution').trigger('keyup');

  // Add an assumption
  $('.addAssumption').on('click', addAssumption);
  // Remove assumption
  $('.removeAssumption').on('click', removeAssumption);

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    // e.target // newly activated tab
    // e.relatedTarget // previous active tab
    var $parent = $(e.target).parents('.policy');
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
    var $list= currentSubIdo.find(".subIdoSelected.list");
    // var v = $(this).text().length > 65 ? $(this).text().substr(0, 65) + ' ... ' : $(this).text();
    var v = $(this).text();

    var $element = currentSubIdo.find(".subIdoSelected").clone(true);
    $element.text(v);
    $element.attr("title", $(this).text()).tooltip();
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

    $element.val(value[value.length - 1]);
    $element.appendTo($list)
    //$("#subIDOs-graphic").dialog("close");
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

}

function addSelect2() {
  $('form select').select2({
    width: '100%'
  });
}

function onChangeRadioButton() {
  var thisValue = this.value === "true";
  var radioType = $(this).classParam('radioType');
  if (thisValue) {
    $('.block-' + radioType).slideDown();
  } else {
    $('.block-' + radioType).slideUp();
  }
}

/**
 * SUB-IDOs Functions
 */

function addSubIdo() {
  var $list = $(this).parents('.policy').find('.subIdos-list');

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
  $(this).parents('.policy').find('.subIdos-list p.message').hide();
}

function removeSubIdo() {
  var $parent = $(this).parents('.policy');
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