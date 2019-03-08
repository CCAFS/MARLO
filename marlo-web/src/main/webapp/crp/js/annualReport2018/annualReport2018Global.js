var $tableViewMore;
var tableDatatableViewmore
$(document).ready(function() {

  // Set data tables
  $tableViewMore = $('.viewMoreSyntesis-block table');
  tableDatatableViewmore = $tableViewMore.DataTable({
      "paging": false,
      "searching": false,
      "info": false,
      "scrollY": "320px",
      "scrollCollapse": true,
  });

  $('.urlify').each(function(i,e) {
    var text = $(e).html();
    $(e).html(urlify(text));
  });

});

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
