$(document).ready(function() {

  var $partnershipsTable = $('table.partnershipsTable');

  var table = $partnershipsTable.DataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": false, // This option disables the select table size option
      "bFilter": false, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 5, // Number of rows to show on the table
      "order": [
        [
            0, 'desc'
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

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

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

  // Add select
  $item.find('select').select2({
    width: '100%'
  });

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