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

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    $('input[name="indexTab"]').val($(this).attr("index"));
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

function createGooglePieChart(chartID,options) {
  google.charts.load('current', {
    packages: [
        'corechart', 'bar'
    ]
  });

  google.charts.setOnLoadCallback(function() {
    $(this).addClass('loaded');

    var $chart = $(chartID);
    var data = google.visualization.arrayToDataTable(getChartDataArray($chart));
    var chart = new google.visualization.PieChart(document.getElementById($chart[0].id));
    chart.draw(data, options);
  });

}
