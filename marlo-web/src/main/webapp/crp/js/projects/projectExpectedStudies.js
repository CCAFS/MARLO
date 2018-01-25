$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  attachEvents();
}

function attachEvents() {

  // Add a Expected Study
  $('.addExpectedStudy').on('click', addExpectedStudy);

  // Remove a Expected Study
  $('.removeExpectedStudy').on('click', removeExpectedStudy);

  // Add Expected study shared projects
  $(".addSharedProject").on("change", addSharedProject);

  // Remove Expected study shared projects
  $(".removeProject").on("click", removeProject);

}

function addExpectedStudy() {
  var $list = $(this).parents("form").find('.expectedStudies-list');
  var $item = $('#expectedStudy-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  updateIndexes();
}

function removeExpectedStudy() {
  var $item = $(this).parents('.expectedStudy');
  $item.hide(function() {
    $item.remove();
    updateIndexes();
  });
}

function addSharedProject() {
  var $option = $(this).find("option:selected");
  if($option.val() == "-1") {
    return;
  }
  var $list = $option.parents(".expectedStudyProjectsList").find(".list");
  var $item = $("#sharedProject-template").clone(true).removeAttr("id");
  var text = ($option.text().length > 80) ? ($option.text().substr(0, 80) + ' ... ') : $option.text();
  // Set funding source parameters
  $item.find(".name").attr("title", $option.text()).html(text);
  $item.find(".projectId").val($option.val());
  $list.append($item);
  $item.show('slow');
  updateIndexes();
  // Reset select
  $option.val("-1");
  $option.trigger('change.select2');
}

function removeProject() {
  var $item = $(this).parents('.sharedProject');
  $item.hide('slow', function() {
    $item.remove();
    updateIndexes();
  });

}

function updateIndexes() {
  $(".expectedStudies-list").find(".expectedStudy").each(function(i,expectedStudy) {
    $(expectedStudy).setNameIndexes(1, i);
    $(expectedStudy).find(".index").html(i + 1);

    // Shared projects
    var $sharedProjects = $(expectedStudy).find(".sharedProject");
    if($sharedProjects.length > 0) {
      $(expectedStudy).find('.emptyText').hide();
      // Update expected study projects
      $sharedProjects.each(function(j,sharedProject) {
        $(sharedProject).setNameIndexes(2, j);
      });
    } else {
      $(expectedStudy).find('.emptyText').show();
    }
  });
}