var $tableViewMore;
var tableDatatableViewmore
var googleChartsLoaded = false;
$(document).ready(function() {

  // Set data tables
  $tableViewMore = $('.viewMoreSyntesis-block table');
  tableDatatableViewmore = $tableViewMore.DataTable({
      "paging": false,
      "searching": false,
      "info": false,
      "scrollY": "320px",
      "scrollCollapse": true,
  });

  $('.urlify').each(function(i,e) {
    var text = $(e).html();
    $(e).html(urlify(text));
  });

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    $('input[name="indexTab"]').val($(this).attr("index"));
  });

});

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
    $chart.addClass('loaded');
    google.charts.setOnLoadCallback(function() {
      var data = new google.visualization.arrayToDataTable(getChartDataArray($chart));
      if(data.wg.length === 0) {
        $chart.append('<p  class="text-center"> ' + options.title + ' <br>  No data </p>');
      } else {
        if(type == "Bar") {
          var view = new google.visualization.DataView(data);
          var chart = new google.visualization.BarChart(document.getElementById($chart[0].id));
          chart.draw(view, google.charts.Bar.convertOptions({}));
        } else if(type == "Pie") {
          var chart = new google.visualization.PieChart(document.getElementById($chart[0].id));
          chart.draw(data, options);
        }
      }
      console.log(type + ": " + chartID, options.title);
    });
  }
}
