$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();
}

function attachEvents() {
  $(".dragProjectList").sortable({
      revert: true,
      placeholder: "portlet-placeholder ui-corner-all"
  });

  $('.dragProjectList').bind('sortstart', function(event,ui) {
    var div = ui.helper[0];
    $('.portlet-placeholder').append('<span class="tableTitles" style="line-height:60px;">Drop here</span>');
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
        updateLists();
        calculateHeight();
      }
  });

  calculateHeight();
}

function calculateHeight() {
  var enabledList = $("#phasesProjectList");
  var disabledList = $("#allProjectList");
  var totalHeight;
  var divsHeight = 64;
  var projecstEnabled = $("#phasesProjectList").find(".project").length;
  var projectsDisabled = $("#allProjectList").find(".project").length;
  console.log(projecstEnabled + "-" + projectsDisabled);
  if(projecstEnabled > projectsDisabled) {
    height = divsHeight * projecstEnabled;
    console.log("1");
  } else {
    height = divsHeight * projectsDisabled;
    console.log("2");
  }
  console.log(height);
  enabledList.outerHeight(height)
  disabledList.outerHeight(height)
}

function updateLists() {
  $('#phasesProjectList').find(".project").each(function(i,e) {
    // Set indexes
    $(e).setNameIndexes(1, i);
  });
}