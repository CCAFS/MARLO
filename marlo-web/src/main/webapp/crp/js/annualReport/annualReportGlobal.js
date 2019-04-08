var $tableViewMore;
var tableDatatableViewmore;
var pageName;
var googleChartsLoaded = false;
$(document).ready(function() {
  pageName = actionName.replace(/[^a-z0-9+]+/gi, '_');

  // Set data tables
  $tableViewMore = $('.viewMoreSyntesis-block table');
  tableDatatableViewmore = $tableViewMore.DataTable({
      "paging": false,
      "searching": false,
      "info": false,
      "scrollY": "320px",
      "scrollCollapse": true,
  });

  $progressTableViewMore = $('.viewMoreSyntesisTable-block table');
  tableDataProgressTableViewmore = $progressTableViewMore.DataTable({
      "paging": false,
      "searching": false,
      "info": true
  });

  $('.urlify').each(function(i,e) {
    var text = $(e).html();
    $(e).html(urlify(text));
  });

  // Load indexTab
  loadTab();

  // Save Local storage indexTab
  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    window.localStorage.setItem(pageName, JSON.stringify({
      "indexTab": $(this).attr("index")
    }));
  });

});

function loadTab() {
  var ls = JSON.parse((window.localStorage.getItem(pageName)));
  if((ls != null)) {
    $('.bootstrapTabs li:eq(' + ls.indexTab + ') a').tab('show');
  }

}

/**
 * Get chart data in Array
 * 
 * @param chart
 * @returns
 */
function getChartDataArray(chart) {
  var dataArray = [];
  $(chart).find('.chartData li').each(function(i,e) {
    dataArray.push($(e).find('span').map(function() {
      var text = $(this).text();
      if($(this).hasClass('number')) {
        return parseFloat(text);
      } else if($(this).hasClass('json')) {
        return JSON.parse(text);
      } else {
        return text;
      }
    }).toArray());
  });
  return dataArray;
}

function createGooglePieChart(chartID,options) {
  createGoogleChart(chartID, "Pie", options);
}

function createGoogleBarChart(chartID,options) {
  createGoogleChart(chartID, "Bar", options);
}

function createGoogleChart(chartID,type,options) {
  if(!googleChartsLoaded) {
    google.charts.load('current', {
      packages: [
          'corechart', 'bar'
      ]
    });
    googleChartsLoaded = true;
  }

  var $chart = $(chartID);
  if($chart.exists()) {
    google.charts.setOnLoadCallback(function() {
      $chart.addClass('loaded');
      var data = new google.visualization.arrayToDataTable(getChartDataArray($chart));
      if(data.wg.length === 0) {
        $chart.append('<p  class="text-center"> ' + options.title + ' <br>  No data </p>');
      } else {
        if(type == "Bar") {
          var view = new google.visualization.DataView(data);
          var chart = new google.visualization.BarChart(document.getElementById($chart[0].id));
          chart.draw(view, google.charts.Bar.convertOptions(options));
        } else if(type == "Pie") {
          console.log($chart[0].id);
          var chart = new google.visualization.PieChart(document.getElementById($chart[0].id));
          chart.draw(data, options);
        }
      }
      console.log(type + ": " + chartID, options.title);
    });
  }
}
