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
  var view2 = new google.visualization.DataView(data2);
  var chart2 = new google.visualization.BarChart(document.getElementById($chart2[0].id));
  chart2.draw(view2, google.charts.Bar.convertOptions({
      title: "Innovations by Stage",
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      chartArea: {
          right: 0,
          width: '60%'
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
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
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

  // Chart #6
  var $chart6 = $('#chart6');
  var data6 = google.visualization.arrayToDataTable(getChartDataArray($chart6));
  var chart6 = new google.visualization.PieChart(document.getElementById($chart6[0].id));
  chart6.draw(data6, {
      title: 'Number of peer reviewed articles by Open Access status',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      pieHole: 0.4,
      chartArea: {
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

  // Chart #7
  var $chart7 = $('#chart7');
  var data7 = google.visualization.arrayToDataTable(getChartDataArray($chart7));
  var chart7 = new google.visualization.PieChart(document.getElementById($chart7[0].id));
  chart7.draw(data7, {
      title: 'Number of peer reviewed articles by ISI status',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      pieHole: 0.4,
      chartArea: {
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
