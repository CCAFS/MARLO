$(document).ready(init);

function init() {
  // Attaching events
  attachEvents();
}

function attachEvents() {
}

google.charts.load('current', {
  'packages': [
      'bar', 'corechart'
  ]
});

google.charts.setOnLoadCallback(function() {
  // On load
  $('.chartBox').addClass('loaded');

  // Chart #2
  var $chart2 = $('#chart2');
  var data2 = new google.visualization.arrayToDataTable(getChartDataArray($chart2));
  var chart2 = new google.charts.Bar(document.getElementById($chart2[0].id));
  chart2.draw(data2, google.charts.Bar.convertOptions({
      chart: {
        title: "Innovations by Stage"
      },
      legend: {
        position: "none"
      },
      colors: '#27ae60',
      bars: 'horizontal' // Required for Material Bar Charts.
  }));

  // Chart #3
  var $chart3 = $('#chart3');
  var data3 = new google.visualization.arrayToDataTable(getChartDataArray($chart3));
  var chart3 = new google.charts.Bar(document.getElementById($chart3[0].id));
  chart3.draw(data3, google.charts.Bar.convertOptions({
      chart: {
        title: "Partnerships by Partner type"
      },
      legend: {
        position: "none"
      },
      colors: '#f39c12',
      bars: 'horizontal' // Required for Material Bar Charts.
  }));

  // Chart #4
  var $chart4 = $('#chart4');
  var data4 = google.visualization.arrayToDataTable(getChartDataArray($chart4));
  var chart4 = new google.visualization.PieChart(document.getElementById($chart4[0].id));
  chart4.draw(data4, {
      title: 'Partnerships by Geographic Scope',
      pieHole: 0.3,
      chartArea: {
        width: '100%'
      },
      legend: {
        alignment: 'center'
      }
  });

  // Chart #5
  var $chart5 = $('#chart5');
  var data5 = new google.visualization.arrayToDataTable(getChartDataArray($chart5));
  var chart5 = new google.charts.Bar(document.getElementById($chart5[0].id));
  chart5.draw(data5, google.charts.Bar.convertOptions({
      chart: {
        title: "Partnerships by phase"
      },
      legend: {
        position: "none"
      },
      colors: '#e67e22',
      bars: 'horizontal' // Required for Material Bar Charts.
  }));

});
