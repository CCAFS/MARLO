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
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
  var data = google.visualization.arrayToDataTable([
      [
          'Year', 'Sales'
      ], [
          'Private sector', 1000
      ], [
          'Government', 1170
      ], [
          'Academic and Research', 660
      ], [
          'Development organizations', 1030
      ]
  ]);

  var options = {
      chart: {
        title: 'Implementing Organization Type'
      },
      bars: 'horizontal' // Required for Material Bar Charts.
  };

  var chart = new google.charts.Bar(document.getElementById('barchart_material'));

  chart.draw(data, google.charts.Bar.convertOptions(options));
}