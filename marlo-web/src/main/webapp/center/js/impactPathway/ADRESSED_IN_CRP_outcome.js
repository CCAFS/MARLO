$(document).ready(init);
var currentSubIdo, saveObj, MAX_YEAR, MIN_YEAR;

function init() {

  MIN_YEAR = 2014;
  MAX_YEAR = 2050;

  /* Declaring Events */
  attachEvents();

  /* Init Select2 plugin */
  $('form select').select2();

  /* Numeric Inputs */
  $('input.targetValue , input.targetYear').integerInput();

  /* Percentage Inputs */
  $('.outcomes-list input.contribution').percentageInput();

  $("#researchTopics").on(
      "change",
      function() {
        var programID = $("#programSelected").html();
        var option = $(this).find("option:selected");
        if(option.val() != "-1") {
          var url =
              baseURL + "/centerImpactPathway/" + centerSession + "/outcomesList.do?programID=" + programID
                  + "&edit=" + editable + "&topicID=" + option.val();
          window.location = url;
        }
      });

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

  // Add a Milestone
  $('.addMilestone').on('click', addMilestone);
  // Remove a Milestone
  $('.removeMilestone').on('click', removeMilestone);

  // On Outcome year change
  $('select.outcomeYear').on('change', function() {
    var endYear = ($(this).val()) || MAX_YEAR;

    $('select.milestoneYear').each(function(i,select) {
      var currentValue = $(select).val();
      var startYear = MIN_YEAR;
      console.log(i);
      // Empty
      $(select).empty();
      $(select).addOption(-1, "Select a year...")
      // New Years list
      while(startYear <= endYear) {
        $(select).addOption(startYear, startYear);
        startYear++;
      }
      // Set value
      $(select).val(currentValue);
      // Refresh select
      $(select).trigger("change.select2");
    });

  });
  $('select.outcomeYear').trigger('change');

}

/**
 * Milestone Functions
 */

function addMilestone() {
  var $list = $('.milestones-list');
  var $item = $('#milestone-template').clone(true).removeAttr("id");
  $item.find('select').select2({
    width: '100%'
  });
  $list.append($item);
  updateAllIndexes();
  $item.show('slow');
  // Hide empty message
  $('.milestones-list p.message').hide();
}

function removeMilestone() {
  var $list = $('.milestones-list');
  var $item = $(this).parents('.milestone');
  $item.hide(function() {
    $item.remove();
    updateAllIndexes();
  });
}

/**
 * General Function
 */

function updateAllIndexes() {
  // Update Milestones
  $('form .milestone').each(function(i,milestone) {

    $(milestone).find('span.index').text(i + 1);

    $(milestone).setNameIndexes(1, i);

  });

  // Update component event
  $(document).trigger('updateComponent');

}
