$(document).ready(init);

function init() {
  // Attaching events
  attachEvents();
}

function attachEvents() {
}

google.charts.load('current', {
  'packages': [
    'bar'
  ]
});
google.charts.setOnLoadCallback(function() {

  var $chart1 = $('#chart1');
  var data1 = new google.visualization.arrayToDataTable(getChartData($chart1));

  var chart1 = new google.charts.Bar(document.getElementById($chart1[0].id));
  chart1.draw(data1, google.charts.Bar.convertOptions({
      legend: {
        position: "none"
      },
      bars: 'horizontal' // Required for Material Bar Charts.
  }));

});

/**
 * Get chart data in Array
 * 
 * @param chart
 * @returns
 */
function getChartData(chart) {
  var dataArray = [];
  $(chart).find('.chartData li').each(function(i,e) {
    dataArray.push($(e).find('span').map(function() {
      if($(this).hasClass('number')) {
        return parseFloat($(this).text());
      } else {
        return $(this).text();
      }
    }).toArray());
  });
  return dataArray;
}