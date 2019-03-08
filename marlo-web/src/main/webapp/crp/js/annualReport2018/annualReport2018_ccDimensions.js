$(document).ready(function() {
  capdevTabLoaded = false;

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    if(!capdevTabLoaded && e.target.hash == "#tab-capdev") {
      // Redraw table
      tableDatatableViewmore.draw();

      // Set charts
      setGoogleCharts();

      // Numeric fields
      $('.numericInput').numericInput();

      capdevTabLoaded = true;
    }
  });

});

google.charts.load('current', {
  packages: [
      'corechart', 'bar'
  ]
});

// google.charts.setOnLoadCallback(setGoogleCharts);

function setGoogleCharts() {

  // On load
  $('.chartBox').addClass('loaded');

  // Chart #12 - Trainees in short-term
  createGooglePieChart('#chart12', {
      title: 'Trainees in short-term',
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
            color: '#22a6b3'
          },
          1: {
            color: '#7ed6df'
          }
      }
  });

  // Chart #13 - Trainees in long-term
  createGooglePieChart('#chart13', {
      title: 'Trainees in long-term',
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
          }
      }
  });

}