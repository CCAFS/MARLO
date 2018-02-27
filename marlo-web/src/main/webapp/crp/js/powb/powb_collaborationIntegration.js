$(document).ready(function() {

  attachEvents();
});

function attachEvents() {

  // Add a program collaboration
  $('.addProgramCollaboration').on('click', addProgramCollaboration);

  // Remove a program collaboration
  $('.removeProgramCollaboration').on('click', removeProgramCollaboration);

}

function addProgramCollaboration() {
  var $list = $(this).parents("form").find('.listProgramCollaborations');
  var $item = $('#flagshipCollaboration-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  updateIndexes();
}

function removeProgramCollaboration() {
  var $item = $(this).parents('.flagshipCollaboration');
  $item.hide(function() {
    $item.remove();
    updateIndexes();
  });
}

function updateIndexes() {
  $(".listProgramCollaborations").find(".flagshipCollaboration").each(function(i,element) {
    $(element).setNameIndexes(1, i);
    $(element).find(".index").html(i + 1);

    // Update labels
    $(element).find('.radio-input').each(function(j,input) {
      $(input).next().attr('for', "raioLabel-" + i + "-" + j);
      $(input).attr('id', "raioLabel-" + i + "-" + j);
    });

  });
}