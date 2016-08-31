var milestonesCount, outcomeID;
$(document).ready(init);

function init() {
  milestonesCount = $('form .outcomeMilestoneYear').length;
  outcomeID = $('#outcomeId').val();

  // Set Select2 widget to already saved data
  $('form select').select2({
    width: '100%'
  });

  // Numeric inputs
  $('input.targetValue').numericInput();

  // Load Milestones ones
  $('form .milestonesYearSelect').each(loadMilestonesByYear);

  // Attaching events functions
  attachEvents();
}

function attachEvents() {
  // Add a milestone
  $('.milestonesYearSelect select').on('change', addMilestone);

  // Remove a milestone
  $('.removeProjectMilestone').on('click', removeMilestone);

  // Add a next user
  $('.addNextUser').on('click', addNextUser);

  // Remove a next user
  $('.removeNextUser').on('click', removeNextUser);
}

/** FUNCTIONS * */

function loadMilestonesByYear(i,e) {
  var $parent = $(e).parents('.tab-pane');
  var $select = $(e).find('select');
  var selectedIds = ($(e).find('.milestonesSelectedIds').text()).split(',');

  // Getting Milestones list milestonesYear.do?year=2017&outcomeID=33
  $.ajax({
      url: baseURL + '/milestonesYear.do',
      data: {
          year: currentCycleYear,
          outcomeID: outcomeID
      },
      success: function(data) {
        for(var i = 0, len = data.crpMilestones.length; i < len; i++) {
          $select.addOption(data.crpMilestones[i].id, data.crpMilestones[i].description);
        }

        // Clear options
        $select.clearOptions(selectedIds);

        $select.trigger("change.select2");
      }
  });

}

function addMilestone() {
  var $item = $('#milestoneYear-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.milestonesYearBlock').find(".milestonesYearList");
  // var year = ($list.parents('.tab-pane').attr('id')).split('-')[1];
  var title = $(this).find('option:selected').text();
  var milestonId = $(this).find('option:selected').val();

  // Set the milestone parameters
  $item.find('.title').text(title);
  $item.find('.crpMilestoneId').val(milestonId);
  // $item.find('.year').val(year);

  // Set Select2 widget
  $item.find('select').select2({
    width: '100%'
  });

  $list.find('.emptyMessage').hide();

  // Add the milestone to the list
  $list.append($item);

  // Show the milestone
  $item.show('slow');

  // Remove option from select
  $(this).find('option:selected').remove();
  $(this).trigger("change.select2");

  // Get extra information from ajax service milestoneInformation.do?milestoneID=3
  $.ajax({
      url: baseURL + '/milestoneInformation.do',
      data: {
        milestoneID: milestonId
      },
      success: function(data) {
        $item.find('.crpMilestoneYear').text(data.crpMilestone.year);
        $item.find('.crpMilestoneValue').text(data.crpMilestone.value);
        $item.find('select').val(data.crpMilestone.targetUnit).trigger("change.select2");

        // Set year tabs pane indexes
        $item.find('.year-tab a').each(function(i,e) {
          var arr = $(e).attr('href').split('-');
          var year = arr[arr.length - 1];
          $(e).attr('href', '#milestoneYear' + milestonesCount + '-' + year);

          if(year > data.crpMilestone.year) {
            $(e).parent().remove();
          }
        });

        // Set year tabs indexes
        $item.find('.tab-pane').each(function(i,e) {
          var arr = $(e).attr('id').split('-');
          var year = arr[arr.length - 1];
          $(e).attr('id', 'milestoneYear' + milestonesCount + '-' + year);

          if(year > data.crpMilestone.year) {
            $(e).remove();
          }
        });

        // Set indexes
        $item.find('.outcomeMilestoneYear').each(function(i,e) {
          $(e).setNameIndexes(1, milestonesCount);
          milestonesCount++;
        });

        // Update milestone
        $list.find('.milestoneYear').each(function(i,e) {
          $(e).find('.index').text(i + 1);
        });

      }
  });
}

function removeMilestone() {
  var $parent = $(this).parent();
  var $select = $parent.parents('.milestonesYearBlock').find('.milestonesYearSelect select');
  var value = $parent.find('.crpMilestoneId').val();
  var name = $parent.find('.title').text();

  $parent.hide('slow', function() {
    // Remove milestone block
    $parent.remove();

    // Update milestone
    $select.parents('.milestonesYearBlock').find('.milestoneYear').each(function(i,e) {
      $(e).find('.index').text(i + 1);
    });

    // Add milestone option again
    $select.addOption(value, name);
    $select.trigger("change.select2");
  });
}

function addNextUser() {
  var $item = $('#nextUser-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.nextUsersBlock').find(".nextUsersList");

  // Add the milestone to the list
  $list.append($item);

  // Show the milestone
  $item.show('slow');

  // Update Next users list
  updateNextUsers();

}

function removeNextUser() {
  var $parent = $(this).parent();
  $parent.hide('slow', function() {
    // Remove milestone block
    $parent.remove();

    // Update Next users list
    updateNextUsers();
  });
}

function updateNextUsers() {
  $("form .nextUser").each(function(i,e) {
    $(e).find('.index').text(i + 1);
    $(e).setNameIndexes(1, i);
  });
}
