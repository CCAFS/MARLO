$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();
}

function attachEvents() {
  $(".dragProjectList").sortable({
    revert: true
  });

  $('.project').draggable({
      connectToSortable: ".dragProjectList",
      revert: 'invalid',
      stop: function(event,ui) {
        var div = ui.helper[0];
        if($(div).parent().attr("id") === "phasesProjectList") {
          $(div).find("input").attr("name", "phasesProjects[].id")
        } else {
          $(div).find("input").attr("name", "")
        }
        console.log($(div).parent());
        updateLists();
        // calculateHeight();
      }
  });
  // calculateHeight();
}

function calculateHeight() {
  var enabledList = $("#phasesProjectList");
  var disabledList = $("#allProjectList");
  if(enabledList.outerHeight() > disabledList.outerHeight()) {
    console.log("holi");
    console.log(disabledList.outerHeight());
    disabledList.outerHeight(enabledList.outerHeight());
  } else {
    enabledList.outerHeight(disabledList.outerHeight());
    console.log("holi2");
  }
}

function updateLists() {
  $('#phasesProjectList').find(".project").each(function(i,e) {
    // Set indexes
    console.log(i);
    $(e).setNameIndexes(1, i);
  });
}