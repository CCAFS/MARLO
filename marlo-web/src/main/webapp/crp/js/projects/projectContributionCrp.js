var milestonesCount, outcomeID;


$(document).ready(init);

function init() {
  milestonesCount = $('form .outcomeMilestoneYear').length;
  outcomeID = $('#outcomeId').val();
  
  // Set Select2 widget to already saved data
  $('form select').select2();
  $('form .milestonesYearSelect select').select2({
    templateResult: formatState,
    templateSelection: formatState,
    width: '100%'
  });
  
  // Capdev popup
  $('.helpMessage3').on("click", openDialog);

  // Numeric inputs
  $('input.targetValue').numericInput();

  // Load Milestones ones
  $('form .milestonesYearSelect').each(loadMilestonesByYear);

  // Attaching events functions
  attachEvents();
  feedbackAutoImplementation();
  //re_animate();
  $('.glyphicon-new-window').on("click", openTab);

  targetBlankPopup();  
  
  
  
  // sort deliverable table
  var $deliverableList = $('table.deliverableList');
  var $innovationList = $('table.innovationList');
  var $evidencieList = $('table.evidencieList');

  $(".fairDiagram").on("click", openDialog);

  var table = $deliverableList.DataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 25, // Number of rows to show on the table
      "fnDrawCallback": function() {
        // This function locates the add activity button at left to the filter box
        var table = $(this).parent().find("table");
        if($(table).attr("id") == "currentActivities") {
          $("#currentActivities_filter").prepend($("#addActivity"));
        }
      },
      "order": [
        [
            3, 'asc'
        ]
      ],
      aoColumnDefs: [
          {
              bSortable: true,
              aTargets: [
                -1
              ]
          }, {
              sType: "natural",
              aTargets: [
                0
              ]
          }
      ]
  });

  var table = $innovationList.DataTable({
    "bPaginate": true, // This option enable the table pagination
    "bLengthChange": true, // This option disables the select table size option
    "bFilter": true, // This option enable the search
    "bSort": true, // this option enable the sort of contents by columns
    "bAutoWidth": false, // This option enables the auto adjust columns width
    "iDisplayLength": 25, // Number of rows to show on the table
    "fnDrawCallback": function() {
      // This function locates the add activity button at left to the filter box
      var table = $(this).parent().find("table");
      if($(table).attr("id") == "currentActivities") {
        $("#currentActivities_filter").prepend($("#addActivity"));
      }
    },
    "order": [
      [
          3, 'asc'
      ]
    ],
    aoColumnDefs: [
        {
            bSortable: true,
            aTargets: [
              -1
            ]
        }, {
            sType: "natural",
            aTargets: [
              0
            ]
        }
    ]
  });

  var table = $evidencieList.DataTable({
    "bPaginate": true, // This option enable the table pagination
    "bLengthChange": true, // This option disables the select table size option
    "bFilter": true, // This option enable the search
    "bSort": true, // this option enable the sort of contents by columns
    "bAutoWidth": false, // This option enables the auto adjust columns width
    "iDisplayLength": 25, // Number of rows to show on the table
    "fnDrawCallback": function() {
      // This function locates the add activity button at left to the filter box
      var table = $(this).parent().find("table");
      if($(table).attr("id") == "currentActivities") {
        $("#currentActivities_filter").prepend($("#addActivity"));
      }
    },
    "order": [
      [
          3, 'asc'
      ]
    ],
    aoColumnDefs: [
        {
            bSortable: true,
            aTargets: [
              -1
            ]
        }, {
            sType: "natural",
            aTargets: [
              0
            ]
        }
    ]
  });

}

//add target="blank" element a in popup
function targetBlankPopup() {
  $(".text-modal-evidences a").attr('target', 'blank');
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

  // open modal evidences
  $('.button-evidences').on('click', openModalEvidences);

  // close modal evidences
  $('.close-modal-evidences').on('click', closeModalEvidences);

}

/** FUNCTIONS * */

function openTab(){
 window.open($(this).parent().attr('href'), 'new tab');
}

function loadMilestonesByYear(i, e) {
  var $parent = $(e).parents('.tab-pane');
  var $select = $(e).find('select');
  var selectedIds = ($(e).find('.milestonesSelectedIds').text()).split(',');

  // Getting Milestones list milestonesYear.do?year=2017&outcomeID=33
  $.ajax({
    url: baseURL + '/milestonesYear.do',
    data: {
      year: currentCycleYear,
      outcomeID: outcomeID,
      phaseID: phaseID,
      ignoreNewer: true,
    },
    success: function (data) {
      for (var i = 0, len = data.crpMilestones.length; i < len; i++) {
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
  $item.find('.crpMilestoneId').val(milestonId);

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
      milestoneID: milestonId,
      phaseID: phaseID
    },
    success: function (data) {

      $item.find('.crpMilestoneYear').text(data.crpMilestone.year);
      $item.find('.crpMilestoneYearInput').val(data.crpMilestone.year);
      $item.find('.crpMilestoneValue').text(data.crpMilestone.value);
      // $item.find('.targetValue').attr('placeholder', data.crpMilestone.value);
      $item.find('.crpMilestoneTargetUnit').text(data.crpMilestone.targetUnitName);
      $item.find('.crpMilestoneTargetUnitInput').val(data.crpMilestone.targetUnit);
      $item.find('.title').text(data.crpMilestone.title);

      if (data.crpMilestone.targetUnit != -1) {
        $item.find('.milestoneTargetValue').show();
      }

      // Set indexes
      $item.find('.outcomeMilestoneYear').each(function (i, e) {
        $(e).setNameIndexes(1, milestonesCount);
        milestonesCount++;
      });

      // Update milestone
      $list.find('.milestoneYear').each(function (i, e) {
        $(e).find('.index').text(i + 1);
      });

    }
  });
}

function removeMilestone() {
  var $parent = $(this).parent();
  var $select = $parent.parents('.milestonesYearBlock').find('.milestonesYearSelect select');
  var value = $parent.find('.crpMilestoneId').val();
  var name = $parent.find('.crpMilestoneYear').text() + " - " + $parent.find('.title').text();

  $parent.hide('slow', function () {
    // Remove milestone block
    $parent.remove();

    // Update milestone
    $select.parents('.milestonesYearBlock').find('.milestoneYear').each(function (i, e) {
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
  $parent.hide('slow', function () {
    // Remove milestone block
    $parent.remove();

    // Update Next users list
    updateNextUsers();
  });
}

function updateNextUsers() {
  $("form .nextUser").each(function (i, e) {
    $(e).find('.index').text(i + 1);
    $(e).setNameIndexes(1, i);
  });
}

function formatState(state) {
  if (state.id != "-1") {
    var text = state.text.split(/-(.+)?/);
    var $state = $("<span><strong> Milestone for " + text[0] + "-</strong> " + text[1] + "</span>");
    return $state;
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }

}

function openDialog() {
  $("#dialog").dialog({
      width: '980',
      modal: true,
      closeText: ""
  });
}

function openModalEvidences(){
  let modal = $('.modal-evidences');
  modal.show();
}

function closeModalEvidences(){
  let modal = $('.modal-evidences');
  modal.hide();
}