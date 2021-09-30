// $.fn.dataTableExt.sErrMode = 'throw';
$(document).ready(initDashboard);

var barType = 'Bar';
var pieType = 'Pie';
var chartHome1 = 'chartHome1';
var chartHome2 = 'chartHome2';

var homeProjectsLoaded = false;
var homeProjectsData1 = [];
var homeProjectsData2 = [];
var homeProjectsTitle1 = 'Projects by Flagship/Module';
var homeProjectsTitle2 = 'Projects by Status';
var homeProjectsAjaxURL = '/projectGraphs.do';
var homeProjectsArrName1 = 'byProjectProgramType';
var homeProjectsArrName2 = 'byProjectStatus';

var deliverablesLoaded = false;
var deliverablesData1 = [];
var deliverablesData2 = [];
var deliverablesTitle1 = 'Deliverables by Open Access';
var deliverablesTitle2 = 'Deliverables by Status';
var deliverablesAjaxURL = '/deliverableGraphs.do';
var deliverablesArrName1 = 'byDeliverableOpenAccess';
var deliverablesArrName2 = 'byDeliverableStatus';

var oicrsLoaded = false;
var oicrsData = [];
var oicrsTitle = 'OICRs by Level';
var oicrsAjaxURL = '/oicrGraphs.do';
var oicrsArrName = 'byLevel';
var isOicrs = false;

var meliasLoaded = false;
var meliasData = [];
var meliasTitle = 'MELIAs by Type';
var meliasAjaxURL = '/meliaGraphs.do';
var meliasArrName = 'byStudyType';
var isMelias = false;

var innovationsLoaded = false;
var innovationsData1 = [];
var innovationsData2 = [];
var innovationsTitle1 = 'Innovations by Type';
var innovationsTitle2 = 'Innovations by Stage';
var innovationsAjaxURL = '/innovationGraphs.do';
var innovationsArrName1 = 'byInnovationType';
var innovationsArrName2 = 'byInnovationLevel';

var policiesLoaded = false;
var policiesData1 = [];
var policiesData2 = [];
var policiesTitle1 = 'Policies by Type';
var policiesTitle2 = 'Policies by Level';
var policiesAjaxURL = '/policyGraphs.do';
var policiesArrName1 = 'byPolicyType';
var policiesArrName2 = 'byPolicyLevel';

var clickedBtnGraph = false;

function initDashboard() {
  $('#newProject').on('click', function (e) {
    $('#decisionTree .addProjectButtons').show(0, function () {
      $(this).addClass('animated flipInX');
    });
  });

  var byType = {
    title: homeProjectsTitle1,
    titleTextStyle: {
      color: '#5f5e5e',
      fontName: 'Open Sans',
      fontSize: 16,
      bold: false
    },
    chartArea: {
      top: 60,
      right: 0,
      width: '80%',
      heigth: "100%"
    },
    hAxis: {
      baseline: 'none',
      textPosition: 'none',
      gridlines: {
        count: 0
      }
    },
    vAxis: {
      textStyle: {
        color: '#5f5e5e',
        fontName: 'Open Sans'
      }
    },
    legend: {
      position: "none"
    },
    bars: 'horizontal'
  };

  var byStatus = {
    title: homeProjectsTitle2,
    titleTextStyle: {
      color: '#5f5e5e',
      fontName: 'Open Sans',
      fontSize: 16,
      bold: false
    },
    orientation: 'horizontal',
    hAxis: {
      baseline: 'none',
      textPosition: 'none',
      gridlines: {
        count: 0
      }
    },
    vAxis: {
      baseline: 'none',
      textPosition: 'none',
      gridlines: {
        count: 0
      }
    },
    chartArea: {
      top: 60,
      width: "80%",
      heigth: "100%"
    },
    // colors: [
    //   '#B21691', '#1773b8', '#00a0b0', '#f3bd1e', '#373a3b'
    // ],
    bar: { groupWidth: '100%' },
    legend: {
      // position: "bottom",
      alignment: 'center',
    },
  };

  var byLevel = {
    title: homeProjectsTitle1,
    titleTextStyle: {
      color: '#5f5e5e',
      fontName: 'Open Sans',
      fontSize: 16,
      bold: false
    },
    orientation: 'horizontal',
    hAxis: {
      baseline: 'none',
      textPosition: 'none',
      gridlines: {
        count: 0
      }
    },
    vAxis: {
      baseline: 'none',
      textPosition: 'none',
      gridlines: {
        count: 0
      }
    },
    chartArea: {
      top: 60,
      width: "80%",
      heigth: "100%"
    },
    bar: { groupWidth: '100%' },
    legend: {
      alignment: 'center',
    },
  };

  $('#homeProjects').on('click', function () {
    isOicrs = false;
    isMelias = false;
    byType.title = homeProjectsTitle1;
    byStatus.title = homeProjectsTitle2;
    $('#chartHome2').css('opacity', 1);
    if (homeProjectsLoaded) {
      drawChart(homeProjectsData1, chartHome1, barType, byType);
      drawChart(homeProjectsData2, chartHome2, pieType, byStatus);
    }
  });

  $('#deliverables').on('click', function () {
    isOicrs = false;
    isMelias = false;
    byType.title = deliverablesTitle1;
    byStatus.title = deliverablesTitle2;
    $('#chartHome2').css('opacity', 1);
    if (!deliverablesLoaded && !clickedBtnGraph) {
      setGoogleCharts(deliverablesAjaxURL, deliverablesArrName1, chartHome1, barType, byType);
      setGoogleCharts(deliverablesAjaxURL, deliverablesArrName2, chartHome2, pieType, byStatus);
      deliverablesLoaded = true;
    } else {
      drawChart(deliverablesData1, chartHome1, barType, byType);
      drawChart(deliverablesData2, chartHome2, pieType, byStatus);
    }
  });

  $('#oicrs').on('click', function () {
    isOicrs = true;
    isMelias = false;
    byStatus.title = oicrsTitle;
    $('#chartHome2').css('opacity', 0);
    if (!oicrsLoaded && !clickedBtnGraph) {
      setGoogleCharts(oicrsAjaxURL, oicrsArrName, chartHome1, pieType, byStatus);
      oicrsLoaded = true;
    } else {
      drawChart(oicrsData, chartHome1, pieType, byStatus);
    }
  });

  $('#melias').on('click', function () {
    isMelias = true;
    isOicrs = false;
    byLevel.title = meliasTitle;
    $('#chartHome2').css('opacity', 0);
    if (!meliasLoaded && !clickedBtnGraph) {
      setGoogleCharts(meliasAjaxURL, meliasArrName, chartHome1, pieType, byLevel);
      meliasLoaded = true;
    } else {
      drawChart(meliasData, chartHome1, pieType, byLevel);
    }
  });

  $('#innovations').on('click', function () {
    isOicrs = false;
    isMelias = false;
    byLevel.title = innovationsTitle1;
    byStatus.title = innovationsTitle2;
    $('#chartHome2').css('opacity', 1);
    if (!innovationsLoaded && !clickedBtnGraph) {
      setGoogleCharts(innovationsAjaxURL, innovationsArrName1, chartHome1, pieType, byLevel);
      setGoogleCharts(innovationsAjaxURL, innovationsArrName2, chartHome2, pieType, byStatus);
      innovationsLoaded = true;
    } else {
      drawChart(innovationsData1, chartHome1, pieType, byLevel);
      drawChart(innovationsData2, chartHome2, pieType, byStatus);
    }
  });

  $('#policies').on('click', function () {
    isOicrs = false;
    isMelias = false;
    byType.title = policiesTitle1;
    byStatus.title = policiesTitle2;
    $('#chartHome2').css('opacity', 1);
    if (!policiesLoaded && !clickedBtnGraph) {
      setGoogleCharts(policiesAjaxURL, policiesArrName1, chartHome1, barType, byType);
      setGoogleCharts(policiesAjaxURL, policiesArrName2, chartHome2, pieType, byStatus);
      policiesLoaded = true;
    } else {
      drawChart(policiesData1, chartHome1, barType, byType);
      drawChart(policiesData2, chartHome2, pieType, byStatus);
    }
  });

  $('.loadingBlock').hide().next().fadeIn(500);

  // Set google charts
  if (!homeProjectsLoaded && !clickedBtnGraph) {
    setGoogleCharts(homeProjectsAjaxURL, homeProjectsArrName1, chartHome1, barType, byType);
    setGoogleCharts(homeProjectsAjaxURL, homeProjectsArrName2, chartHome2, pieType, byStatus);
    homeProjectsLoaded = true;
  }

  $('.toggleBtnGraphs').on('click', function () {
    var icon = $('.icon-show');
    var homeGraphs = $('.homeGraphs');
    var currentTab = '#' + $('.nav-tabs').find('li.active').find('a').attr('id');
    var activeTab = $(`${currentTab}`);
    
    if (!clickedBtnGraph) {
      homeGraphs.hide();
      icon.removeClass("glyphicon-eye-open");
      icon.addClass("glyphicon-eye-close");
      clickedBtnGraph = true;
    } else {
      homeGraphs.show();
      icon.removeClass("glyphicon-eye-close");
      icon.addClass("glyphicon-eye-open");
      clickedBtnGraph = false;
      activeTab.click();
    }
  });
}

function setGoogleCharts(ajaxURL, arrName, chartID, type, options) {
  google.charts.load("visualization", "1", { packages: ["corechart"], callback: () => (loadPageData(ajaxURL, arrName, chartID, type, options)) });
  // google.charts.setOnLoadCallback(loadPageData(chartID));
}

function loadPageData(ajaxURL, arrName, chartID, type, options) {
  $.ajax({
    url: baseURL + ajaxURL,
    async: false,
    success: function (data) {
      if (data) {
        var newData = data[arrName].map(function (x) {
          var arr = [];
          arr.push(x.key);
          arr.push(x.count);
          arr.push((x.count).toString());
          if (arrName == homeProjectsArrName1) {
            arr.push(data['fpColors'][x.key]);
          }
          return arr;
        });
        if (arrName == homeProjectsArrName1) {
          newData.unshift(["Type", "Count", { role: "annotation" }, { role: "style" }]);
        } else {
          newData.unshift(["Type", "Count", { role: "annotation" }]);
        }
        switch (arrName) {
          case homeProjectsArrName1:
            homeProjectsData1 = newData;
            break;

          case homeProjectsArrName2:
            homeProjectsData2 = newData;
            break;

          case deliverablesArrName1:
            deliverablesData1 = newData;
            break;

          case deliverablesArrName2:
            deliverablesData2 = newData;
            break;

          case oicrsArrName:
            oicrsData = newData;
            break;

          case meliasArrName:
            meliasData = newData;
            break;

          case innovationsArrName1:
            innovationsData1 = newData;
            break;

          case innovationsArrName2:
            innovationsData2 = newData;
            break;

          case policiesArrName1:
            policiesData1 = newData;
            break;

          case policiesArrName2:
            policiesData2 = newData;
            break;

          default:
            break;
        }
        drawChart(newData, chartID, type, options);
      }
    },
  });
}

function drawChart(chart_data, chartID, type, options) {
  if (chart_data) {
    var chart1_data = new google.visualization.arrayToDataTable(chart_data);
    var chart1_options = options;
    if (chart1_data.Wf == 0 || chart1_data.Wf == 1) {
      if (isOicrs || isMelias) {
        $('#'+chartHome1).css('display', 'none');
        $('#'+chartHome2).css('display', 'none');
      } else {
        $('#'+chartID).css('display', 'none');
      }
    } else {
      if (isOicrs || isMelias) {
        $('#'+chartHome1).css('display', 'block');
        $('#'+chartHome2).css('display', 'block');
      } else {
        $('#'+chartID).css('display', 'block');
      }
      var view = new google.visualization.DataView(chart1_data);

      if (type == barType) {
        var chart1_chart = new google.visualization.BarChart(document.getElementById(chartID));
      } else {
        var chart1_chart = new google.visualization.PieChart(document.getElementById(chartID));
      }
      chart1_chart.draw(view, chart1_options);
    }
  }
}

function setCompletionDates() {
  var today = new Date();
  $('#timeline li.li').each(function (i, element) {
    var timelineDate = new Date($(element).find('.dateText').text());
    timelineDate.setTime(timelineDate.getTime() + (timelineDate.getTimezoneOffset() / 60) * 3600000);
    $(element).find('.date').text(timelineDate.toDateString()).addClass('animated flipInX');
    var isOpen = $(element).find('.isOpen').text() === "true";
    if (!isOpen) {
      timelineDate.setTime(timelineDate.getTime() + (24 * 3600000));
    }
    if (today >= timelineDate) {
      $(element).addClass('complete');
    }
  });
}

function workflowModal() {
  $("#showPandRWorkflowDialog").dialog({
    modal: true,
    closeText: "",
    width: 700,
    height: 770,
    buttons: {
      Ok: function () {
        $(this).dialog("close");
      }
    }
  });
  return false;
}

var graphStarted = false;
function initTabs() {
  $("#dashboard-tabs").tabs({
    activate: function (event, ui) {
      if (ui.newTab.index() == 1) {
        if (!graphStarted) {
          callCytos(baseURL + "/json/prePlanningIpGraph.do", "ipGraph-content");
          graphStarted = true;
        }
      }
    }
  });
}

function initDatatable() {
  $('#projects-table').dataTable({
    "aLengthMenu": [
      [
        5, 10
      ], [
        5, 10
      ]
    ],
    "iDisplayLength": 5
  });

  $("#deadlineDates table").dataTable();
}

function initSlidr() {
  slidr.create('slider', {
    breadcrumbs: true,
    keyboard: true,
    overflow: true,
    pause: false,
    theme: '#444',
    touch: true
  }).start();
}

function timeline() {
  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth();
  var yyyy = today.getFullYear();

  if (dd < 10) {
    dd = '0' + dd
  }
  if (mm < 10) {
    mm = '0' + mm
  }

  today = new Date(yyyy, mm, dd);
  // today = new Date("12/11/2016");

  var timelineStart = 1;
  var current = [];
  var state = 0;

  $(".infoActions").each(function (i, e) {
    var startDate = new Date($(e).find(".startDate").html());
    if ($(e).find(".endDate").html().length == 0) {
      var endDate = startDate;
    } else {
      var endDate = new Date($(e).find(".endDate").html());
    }

    if (today >= startDate && today <= endDate) {
      current[i] = 1;
      state = 1;
    } else {
      current[i] = startDate + "/" + endDate;
    }
  });

  if (state != 1) {
    for (var i = 0; i < current.length; i++) {
      var resta = new Date(current[i].split("/")[1]) - today;
      var dias = resta / (1000 * 60 * 60 * 24);
      if (dias < 0) {
        current[i] = 0;
      } else {
        current[i] = 1;
        break;
      }
    }
  }
  timelineStart = current.indexOf(1) + 1;

  $().timelinr({
    orientation: 'horizontal',
    // value: horizontal | vertical, default to horizontal
    containerDiv: '#timeline',
    // value: any HTML tag or #id, default to #timeline
    datesDiv: '#dates',
    // value: any HTML tag or #id, default to #dates
    datesSelectedClass: 'selected',
    // value: any class, default to selected
    datesSpeed: 'normal',
    // value: integer between 100 and 1000 (recommended) or 'slow', 'normal' or 'fast'; default to normal
    issuesDiv: '#issues',
    // value: any HTML tag or #id, default to #issues
    issuesSelectedClass: 'selected',
    // value: any class, default to selected
    issuesSpeed: 'fast',
    // value: integer between 100 and 1000 (recommended) or 'slow', 'normal' or 'fast'; default to fast
    issuesTransparency: 0.1,
    // value: integer between 0 and 1 (recommended), default to 0.2
    issuesTransparencySpeed: 500,
    // value: integer between 100 and 1000 (recommended), default to 500 (normal)
    prevButton: '.leftControl',
    // value: any HTML tag or #id, default to #prev
    nextButton: '.rigthControl',
    // value: any HTML tag or #id, default to #next
    arrowKeys: 'false',
    // value: true/false, default to false
    startAt: timelineStart,
    // value: integer, default to 1 (first)
    autoPlay: 'false',
    // value: true | false, default to false
    autoPlayDirection: 'forward',
    // value: forward | backward, default to forward
    autoPlayPause: 2000
  });
}

$('table.projectsList').dataTable({
  "bPaginate": true, // This option enable the table pagination
  "bLengthChange": true, // This option disables the select table size option
  "bFilter": true, // This option enable the search
  "bSort": true, // this option enable the sort of contents by columns
  "bAutoWidth": false, // This option enables the auto adjust columns width
  "iDisplayLength": 5, // Number of rows to show on the table
  "pagingType": "simple",
  "fnDrawCallback": function () {
    // This function locates the add activity button at left to the filter box
    var table = $(this).parent().find("table");
    if ($(table).attr("id") == "currentActivities") {
      $("#currentActivities_filter").prepend($("#addActivity"));
    }
  },
  aoColumnDefs: [
    {
      bSortable: false,
      aTargets: [

      ]
    }, {
      sType: "natural",
      aTargets: [
        0
      ]
    }
  ]
});

$('a#impact[data-toggle="tab"]').on('shown.bs.tab', function (e) {
  e.target // newly activated tab
  e.relatedTarget // previous active tab
  var url = baseURL + "/impactPathway/impactPathwayFullGraph.do";
  var data = {
    crpID: currentCrpID
  }
  ajaxService(url, data, "impactGraphic", true, true, 'concentric', true);
})

// Impact pathway full screen

$("#fullscreen").on("click", function () {
  $("#impactGraphic-content").dialog({
    resizable: false,
    closeText: "",
    width: '90%',
    modal: true,
    height: $(window).height() * 0.80,
    show: {
      effect: "blind",
      duration: 500
    },
    hide: {
      effect: "fadeOut",
      duration: 500
    },
    open: function (event, ui) {
      var dataFull = {
        crpID: currentCrpID
      }
      var url = baseURL + "/impactPathway/impactPathwayFullGraph.do";
      ajaxService(url, dataFull, "impactGraphic-fullscreen", true, true, 'breadthfirst', false);
    }
  });

});