var capdevTabLoaded;
$(document).ready(function() {
  capdevTabLoaded = ($('input[name="indexTab"]').val() == 3);

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    if(!capdevTabLoaded && e.target.hash == "#tab-capdev") {
      loadCapdev();
    }
  });

  if(capdevTabLoaded) {
    loadCapdev();
  }
});

function loadCapdev() {
  // Redraw table
  tableDatatableViewmore.draw();
  // Numeric fields
  $('.numericInput').numericInput();
  // Set charts
  setGoogleCharts();

  capdevTabLoaded = true;
}

function setGoogleCharts() {
  // Chart #12 - Trainees in short-term
  createGoogleBarChart('#chart12', {
      title: 'Trainees in short-term',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      //pieHole: 0.4,
      /*
      chartArea: {
          top: 70,
          width: '100%'
      },
      */
      hAxis: {
        baseline:'none',
        textPosition: 'none',
        gridlines: {
          count: 0
        }
      },
      vAxis: {
        //baseline:'none',
        gridlines: {
          count: 0
        }
      },
      //colors: '#e67e22',
      series: {
        0:{color:'#F3BD29'},
        1:{color:'#F3BD27'}
      },
      legend: { position: 'bottom',
        alignment: 'center',
        maxLines: 3
      },
      isStacked: true
      /*
      slices: {
          0: {
            color: '#22a6b3'
          },
          1: {
            color: '#7ed6df'
          }
      },*/

  });

  // Chart #13 - Trainees in long-term
  createGoogleBarChart('#chart13', {
      title: 'Trainees in long-term',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      //pieHole: 0.4,
      /*
      chartArea: {
          top: 70,
          width: 400
      },*/
      //colors: '#7ed6df',

      hAxis: {
        baseline:'none',
        textPosition: 'none',
        gridlines: {
          count: 0
        }
      },
      vAxis: {

        gridlines: {
          count: 0
        }
      },
      series: {
        0:{
          color:'#00A0B0',
          annotations: {
            stem: {
              length: 5
            },
          }
        },
        1:{
          color:'#AB2E56',
          annotations: {
            stem: {
              length: 22
            },
          }
        }
      },
      legend: {
        position: 'bottom',
        alignment: 'center',
        maxLines: 3
      },
      isStacked: true
      /*
      slices: {
          0: {
            color: '#22a6b3'
          },
          1: {
            color: '#7ed6df'
          }
      }*/
  });

}