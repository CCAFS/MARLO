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

//Chart #10 - Number of peer reviewed articles by Open Access status
  var $chart10 = $('#chart10');
  var data10 = google.visualization.arrayToDataTable(getChartDataArray($chart10));
  var chart10 = new google.visualization.PieChart(document.getElementById($chart10[0].id));
  chart10.draw(data10, {
      title: 'Number of peer reviewed articles by Open Access status',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      pieHole: 0.4,
      chartArea: {
        top: 70,
        width: '100%'
      },
      colors: '#e67e22',
      legend: {
        alignment: 'center'
      },
      slices: {
          0: {
            color: '#27ae60'
          },
          1: {
            color: '#f1c40f'
          }
      }
  });

//Chart #11
  var $chart11 = $('#chart11');
  var data11 = google.visualization.arrayToDataTable(getChartDataArray($chart11));
  var chart11 = new google.visualization.PieChart(document.getElementById($chart11[0].id));
  chart11.draw(data11, {
      title: 'Number of peer reviewed articles by ISI status',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      pieHole: 0.4,
      chartArea: {
        top: 70,
        width: 400
      },
      colors: '#e67e22',
      legend: {
        alignment: 'center'
      },
      slices: {
          0: {
            color: '#16a085'
          },
          1: {
            color: '#bdc3c7'
          }
      }
  });

});