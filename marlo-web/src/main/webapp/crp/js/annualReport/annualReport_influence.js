$(document).ready(init);

function init() {
  // Attaching events
  attachEvents();
}

function attachEvents() {
}

google.charts.load('current', {
  packages: [
      'corechart', 'bar'
  ]
});
google.charts.setOnLoadCallback(function() {
  // On load
  $('.chartBox').addClass('loaded');

  // Chart #1
  var $chart1 = $('#chart1');
  var data1 = new google.visualization.arrayToDataTable(getChartDataArray($chart1));
  var view1 = new google.visualization.DataView(data1);
  var chart1 = new google.visualization.BarChart(document.getElementById($chart1[0].id));
  chart1.draw(view1, google.charts.Bar.convertOptions({
      title: "Implementing Organization Type",
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      chartArea: {
          right: 0,
          width: '70%'
      },
      legend: {
        position: "none"
      },
      vAxis: {
        textStyle: {
            color: '#8c8c8c',
            fontName: 'Roboto'
        }
      },
      bars: 'horizontal' // Required for Material Bar Charts.
  }));

});