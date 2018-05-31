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
  // On load
  $('.chartBox').addClass('loaded');

  // Chart #2
  var $chart1 = $('#chart2');
  var data1 = new google.visualization.arrayToDataTable(getChartDataArray($chart1));

  var chart1 = new google.charts.Bar(document.getElementById($chart1[0].id));
  chart1.draw(data1, google.charts.Bar.convertOptions({
      chart: {
        title: "Innovations by Stage"
      },
      legend: {
        position: "none"
      },
      colors: '#27ae60',
      bars: 'horizontal' // Required for Material Bar Charts.
  }));

});
