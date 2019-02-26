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
      title: "Organizations designing/promulgating the policy",
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      chartArea: {
          right: 0,
          width: '80%'
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

//Chart #2
  var $chart2 = $('#chart2');
  var data2 = new google.visualization.arrayToDataTable(getChartDataArray($chart2));
  var view2 = new google.visualization.DataView(data2);
  var chart2 = new google.visualization.ColumnChart(document.getElementById($chart2[0].id));
  chart2.draw(view2, google.charts.Bar.convertOptions({
      title: "CGIAR Cross-cutting markers",
      stacked: true,
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      chartArea: {
          right: 0,
          width: '80%',
      },
      legend: {
        position: "top"
      },
      vAxis: {
        minValue: 0,
        textStyle: {
            color: '#8c8c8c',
            fontName: 'Roboto'
        }
      }
  }));

});