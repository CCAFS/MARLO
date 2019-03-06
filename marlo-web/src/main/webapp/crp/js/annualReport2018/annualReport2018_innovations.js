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

  // Chart #8 - Innovations by type
  var $chart8 = $('#chart8');
  var data8 = google.visualization.arrayToDataTable(getChartDataArray($chart8));
  var chart8 = new google.visualization.PieChart(document.getElementById($chart8[0].id));
  chart8.draw(data8, {
      title: 'Innovations by type',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      pieHole: 0.3,
      chartArea: {
          right: 0,
          bottom: 0,
          width: '100%',
          height: '85%'
      },
      legend: {
        alignment: 'center',
      // position: 'bottom'
      }
  });

  // Chart #9 - Innovations by stage
  var $chart9 = $('#chart9');
  var data9 = new google.visualization.arrayToDataTable(getChartDataArray($chart9));
  var view9 = new google.visualization.DataView(data9);
  var chart9 = new google.visualization.BarChart(document.getElementById($chart9[0].id));
  chart9.draw(view9, google.charts.Bar.convertOptions({
      title: "Innovations by stage",
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      chartArea: {
        right: 0,
        bottom: 0,
        width: '80%',
        height: '90%'
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
      // colors: '#27ae60',
      bars: 'horizontal' // Required for Material Bar Charts.
  }));

});