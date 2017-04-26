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
      }
  });

}

function updateLists() {
  $('#phasesProjectList').find(".project").each(function(i,e) {
    // Set indexes
    console.log(i);
    $(e).setNameIndexes(1, i);
  });
}