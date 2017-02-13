$(document).ready(init);

function init() {

  // Adding DataTable plugin
  $(".projectContributions, .regionalContributions").dataTable({
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
  $(".projectContributions, .regionalContributions").on('draw.dt', function() {
    // List all blocks and removing buttons that have a height too small
    setViewMore();
  });

  // Attaching events
  attachEvents();
}

function attachEvents() {
  // Validating numeric value
  $('.isNumeric').on("keydown", isNumber);

  $('.viewMoreSyntesis').on('click', expandViewMoreSyntesisBlock);

  // urlify
  $('table tbody td').each(function(i,e) {
    var html = urlify($(e).html());
    $(e).html(html);
  });

}

function expandViewMoreSyntesisBlock() {
  if($(this).hasClass("closed")) {
    $(this).parent().css({
      height: $(this).parent().find('.dataTables_wrapper').height() + $(this).height() + 5
    });
    $(this).html('View less');
    $(this).addClass("opened");
    $(this).removeClass("closed");
  } else if($(this).hasClass("opened")) {
    $(this).parent().css({
      height: 255
    });
    $(this).html('View More');
    $(this).addClass("closed");
    $(this).removeClass("opened");
  }
}

function setViewMore() {
  $('.viewMoreSyntesis-block').each(function(i,element) {
    if($(element).height() < 225) {
      $(element).find('.viewMoreSyntesis').remove();
    } else {
      $(element).css({
        "height": 225
      })
      $(element).find('.viewMoreSyntesis').addClass("closed");
      $(element).find('.viewMoreSyntesis').html('View More');
    }
  });
}
