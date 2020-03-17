$(document).ready(init);

function init() {
  // Attaching events
  attachEvents();

  // Init Google Charts
  setGoogleCharts();

}

function attachEvents() {
}

function setGoogleCharts() {

  // Chart #10 - Number of peer reviewed articles by Open Access status
  createGooglePieChart('#chart10', {
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
            color: '#f68212'
          },
          1: {
            color: '#999fa3'
          }
      }
  });

  // Chart #11 - Number of peer reviewed articles by ISI status
  createGooglePieChart('#chart11', {
      title: 'Number of peer reviewed articles by ISI status',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false,
          alignment: 'center'
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
            color: '#16a085'
          },
          1: {
            color: '#999fa3'
          }
      }
  });

};