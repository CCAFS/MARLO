$(document).ready(init);

function init() {
  // List all blocks and removing buttons that have a height too small
  setViewMore()
  // Attaching events
  attachEvents();
  // Set word limits to inputs that contains class limitWords-value, for example : <input class="limitWords-100" />
  setWordCounterToInputs('limitWords');
  // Adding DataTable plugin
  $(".regionalContributions, .projectContributions").dataTable({
      "bPaginate": false, // This option enable the table pagination
      "bLengthChange": false, // This option disables the select table size option
      "bFilter": false, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 100,
      aoColumnDefs: [
        {
            sType: "natural",
            aTargets: [
                0, 1, 2
            ]
        }
      ]
  });
}

function attachEvents() {
  // Validating numeric value
  $('.isNumeric').on("keydown", isNumber);

  $('.viewMore').toggle(expandViewMoreBlock, colapseViewMoreBlock);

  // urlify
  $('table tbody td').each(function(i,e) {
    var html = urlify($(e).html());
    $(e).html(html);
  });

}

function expandViewMoreBlock() {
  $(this).parent().css({
    height: $(this).parent().find('.dataTables_wrapper').height() + $(this).height() + 40
  });
  $(this).html('View less');
}

function colapseViewMoreBlock() {
  $(this).parent().css({
    height: 225
  });
  $(this).html('View More');
}

function setViewMore() {
  $('.viewMore-block').each(function(i,element) {
    if($(element).height() < 225) {
      $(element).find('.viewMore').remove();
    } else {
      $(element).css({
        "height": 225
      })
      $(element).find('.viewMore').html('View More');
    }
  });
}
