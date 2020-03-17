$(document).ready(init);

function init() {
  // Attaching events
  attachEvents();

  // Set google charts
  setGoogleCharts();
}

function attachEvents() {
}

function setGoogleCharts() {
  // Chart #7 - Policies Level of maturity
  createGoogleBarChart('#chart7', {
      title: 'Policies Level of Maturity',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      orientation: 'horizontal',
      hAxis: {
        baseline:'none',
        textPosition: 'none',
        gridlines: {
          count: 0
        }
      },
      vAxis: {
        //baseline:'none',
        baseline:'none',
        textPosition: 'none',
        gridlines: {
          count: 0
        }
      },
      //pieHole: 0.4,
      chartArea: {
          top: 45,
          width: "80%",
          heigth: "100%"
      },
      colors: [
          '#1773b8', '#e43a74', '#00a0b0', '#f3bd1e', '#373a3b'
      ],
      bar: {groupWidth: '100%'},
      legend: {
        position: "bottom",
        //alignment: 'center',
      },
  });

  // Chart #6 - Policies by Type
  createGoogleBarChart("#chart6", {
      title: "Policies by Type",
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      chartArea: {
          top: 65,
          right: 0,
          width: '70%',
          heigth: "100%"
      },
      hAxis: {
        baseline:'none',
        //viewWindowMode: 'pretty',
        //slantedText: true,
        textPosition: 'none',
        gridlines: {
          count: 0
        }
      },
      vAxis: {
        textStyle: {
            color: '#5f5e5e',
            fontName: 'Roboto'
        }
      },
      legend: {
        position: "none"
      },
      bars: 'horizontal' // Required for Material Bar Charts.
  });

};