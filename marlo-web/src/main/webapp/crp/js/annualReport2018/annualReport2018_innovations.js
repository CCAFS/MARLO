$(document).ready(init);

function init() {
  // Attaching events
  attachEvents();

  // Load Google Charts
  setGoogleCharts();
}

function attachEvents() {
}

function setGoogleCharts() {

  // Chart #8 - Innovations by type
  createGooglePieChart('#chart8', {
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
          width: '80%',
          height: '85%'
      },
      legend: {
        alignment: 'center',
      // position: 'bottom'
      }
  });

  // Chart #9 - Innovations by stage
  createGoogleBarChart('#chart9', {
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
  });

}