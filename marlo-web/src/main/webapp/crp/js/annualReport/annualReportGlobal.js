$(document).ready(function() {
  // setViewMores();

  // Set datatables
  var $table = $('.viewMoreSyntesis-block table');
  $table.DataTable({
      "paging": false,
      "searching": false,
      "info": false,
      "scrollY": "320px",
      "scrollCollapse": true,
  });

});

/** View More Synthesis functions * */

function expandViewMoreSyntesisBlock() {

  var blockHeight = $(this).parent().find('table').height() + $(this).height();
  var defaultHeigth = 300;

  if($(this).hasClass("closed")) {
    $(this).parent().css({
      height: blockHeight + 20
    });
    $(this).html('View less');
    $(this).addClass("opened").removeClass("closed");
  } else if($(this).hasClass("opened")) {
    $(this).parent().css({
      height: defaultHeigth
    });
    $(this).html('View More');
    $(this).addClass("closed").removeClass("opened");
  }
}

function setViewMores() {
  var defaultHeigth = 300;
  $('.viewMoreSyntesis-block').each(function(i,element) {
    var $viewMoreButton = $(element).find('.viewMoreSyntesis');

    if($(element).height() < defaultHeigth) {
      $viewMoreButton.remove();
    } else {
      $(element).css({
        "height": defaultHeigth
      })
      $viewMoreButton.addClass("closed");
      $viewMoreButton.html('View More');
    }
    // Show the block if is hidden
    $(element).show();
    // Add Event
    $viewMoreButton.on('click', expandViewMoreSyntesisBlock);
  });
}

/**
 * Get chart data in Array
 * 
 * @param chart
 * @returns
 */
function getChartDataArray(chart) {
  var dataArray = [];
  $(chart).find('.chartData li').each(function(i,e) {
    dataArray.push($(e).find('span').map(function() {
      if($(this).hasClass('number')) {
        return parseFloat($(this).text());
      } else if($(this).hasClass('json')) {
        return JSON.parse($(this).text());
      } else {
        return $(this).text();
      }
    }).toArray());
  });
  return dataArray;
}
