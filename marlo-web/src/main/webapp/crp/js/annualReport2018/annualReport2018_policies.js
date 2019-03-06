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

  // Chart #6 - Organizations designing/promulgating the policy
  var $chart6 = $('#chart6');
  var data6 = new google.visualization.arrayToDataTable(getChartDataArray($chart6));
  var view6 = new google.visualization.DataView(data6);
  var chart6 = new google.visualization.BarChart(document.getElementById($chart6[0].id));
  chart6.draw(view6, google.charts.Bar.convertOptions({
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

//Chart #7
  var $chart7 = $('#chart7');
  var data7 = new google.visualization.arrayToDataTable(getChartDataArray($chart7));
  var view7 = new google.visualization.DataView(data7);
  var chart7 = new google.visualization.ColumnChart(document.getElementById($chart7[0].id));
  chart7.draw(view7, google.charts.Bar.convertOptions({
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