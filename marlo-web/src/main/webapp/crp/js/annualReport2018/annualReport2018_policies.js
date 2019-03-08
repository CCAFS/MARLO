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
  console.log(getChartDataArray($chart6));
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

  // Chart #7 - Policies Level of maturity
  createGooglePieChart('#chart7', {
      title: 'Policies Level of Maturity',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      pieHole: 0.4,
      chartArea: {
          top: 45,
          width: "100%"
      },
      colors: '#7ed6df',
      legend: {
        alignment: 'center'
      },
      slices: {
          0: {
            color: '#22a6b3'
          },
          1: {
            color: '#7ed6df'
          },
          2: {
            color: '#1ed9d4'
          }
      }
  });

});