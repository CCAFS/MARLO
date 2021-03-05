var $tableViewMore;
var tableDatatableViewmore, tableDataProgressTableViewmore, tableDatatableTableGrey, tableInnovations, tablePolicies, tableOICRs;
var pageName;
var googleChartsLoaded = false;
$(document).ready(function() {
  
  // $('.slo-contribution-template').find('textarea').trumbowyg("destroy");

  $('.slo-contribution-template').find('select').select2("destroy");
  pageName = actionName.replace(/[^a-z0-9+]+/gi, '_');

  // Set data tables
  if($.fn.DataTable) {
    $tableViewMore = $('.viewMoreSyntesis-block table');
    tableDatatableViewmore = $tableViewMore.DataTable({
        "paging": false,
        "searching": false,
        "info": false,
        "scrollY": "320px",
        "scrollCollapse": true,
        aoColumnDefs: [
          {
              sType: "natural",
              aTargets: [
                0
              ]
          }
        ]
    });

    $progressTableViewMore = $('.viewMoreSyntesisTable-block table');
    tableDataProgressTableViewmore = $progressTableViewMore.DataTable({
        "paging": true,
        "searching": true,
        "info": true,
        aoColumnDefs: [
          {
              sType: "natural",
              aTargets: [
                0
              ]
          }
        ]
    });

    $tableInnovationsHTML = $('.tableInnovations-block table');
    tableInnovations = $tableInnovationsHTML.DataTable({
      "paging": false,
      "searching": true,
      "info": true,
      aoColumnDefs: [
        {
            sType: "natural",
            aTargets: [
              0
            ]
        }
      ]
    });

    $tablePoliciesHTML = $('.tablePolicies-block table');
    tablePolicies = $tablePoliciesHTML.DataTable({
      "paging": false,
      "searching": true,
      "info": true,
      aoColumnDefs: [
        {
            sType: "natural",
            aTargets: [
              0
            ]
        }
      ]
    });

    $tableOICRsHTML = $('.tableOICRs-block table');
    tableOICRs = $tableOICRsHTML.DataTable({
      "paging": false,
      "searching": true,
      "info": true,
      aoColumnDefs: [
        {
            sType: "natural",
            aTargets: [
              0
            ]
        }
      ]
    });

    $TableGrey = $('.viewMoreSyntesisTableGrey-block table');
    tableDatatableTableGrey = $TableGrey.DataTable({
        "paging": true,
        "searching": true,
        "info": true,
        aoColumnDefs: [
          {
              sType: "natural",
              aTargets: [
                0
              ]
          }
        ]
    });

    tableDataExport = $('.dataTableExport table').DataTable({
        "paging": false,
        "searching": false,
        "info": true,
        dom: 'Bfrtip',
        buttons: [
          {
              text: '<i class="fas fa-download"></i> Export CSV Data',
              extend: 'csv',
              title: 'Data_export_' + currentSectionString + '_' + getDateString(),
              autoFilter: true,
              bom: true, // UTF-8
              className: 'exportCSV'
          }
        ]
    });
  }

  $('.urlify').each(function(i,urlifyText) {
    var text = $(urlifyText).html();

    if($(urlifyText).find('a').length > 0) {
      // Short URLs text
      $(urlifyText).find('a').each(function(iAnchor,anchor) {
        var anchorText = $(anchor).text();
        $(anchor).text(truncate(anchorText, 45));
      });
    } else {
      // URLfy links (Works only for plain text)
      $(urlifyText).html(urlifyComplete(text));
    }

    $(urlifyText).find('a').addClass('dont-break-out');

  });

  // Load indexTab
  loadTab();

  // Save Local storage indexTab
  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    window.localStorage.setItem(pageName, JSON.stringify({
      "indexTab": $(this).attr("index")
    }));
  });

  // checkbox disables field
  console.log("init press");
  
  $('.checkboxDiTeArClick').on('click',setCheckboxValueTohide);
  $('.btn-addEvidence').on('click',addEvidence);
  $('.btn-removeEvidence').on('click',removeEvidence);

  $('.TA_summaryEvidence .trumbowyg-editor').bind('DOMSubtreeModified', function(){
    console.log('%cmovement','background: #222; color: #fd8484');
    // $(this).parents('.slo-contribution-section').find('.TA_summaryEvidence .briefSummaryTAHidden').css("background-color", "yellow");
    $(this).parents('.slo-contribution-section').find('.TA_summaryEvidence .briefSummaryTAHidden').html($(this).html());
  });


  $('.TA_additionalContribution .trumbowyg-editor').bind('DOMSubtreeModified', function(){
    console.log('%cmovement','background: #222; color: #fd8484');

    $(this).parents('.slo-contribution-section').find('.TA_additionalContribution .additionalContributionTAHidden').html($(this).html());
  });

  // $('.slo-contribution-section').on("bind",".TA_summaryEvidence .trumbowyg-editor", function() {
  //   //do whatever
  //   console.log('%cHola mundo','background: #222; color: #fd8484');
  // });

  setStatusByBack();
  // updateAllIndexesContribution();
});

function setStatusByBack() {
  $(".sloTargetsList")
    .find(".sloTarget")
    .each(function (i, field) {
      // console.log($(field).find(".checkboxDiTeArClick").val());

      let checkbox = $(field).find(".checkboxDiTeArClick");
      console.log($(checkbox).val());



      if ($(checkbox).val() == "true") {
        $(checkbox).val("false");
        // console.log("now is: "+$(this).val());

      } else {
        $(checkbox).val("true");
        // console.log("now is: "+$(this).val());

      }
    
      if ($(checkbox).val() == "false") {
        $(checkbox).parents(".a-slo").find(".to-disabled-box").hide(400);
        $(checkbox).parents(".a-slo").find(".btn-addEvidence").hide(400);
        // $(checkbox).parents(".a-slo").find(".disabled-box").show();
      } else {
        // $(checkbox).parents(".a-slo").find(".disabled-box").hide();
        $(checkbox).parents(".a-slo").find(".to-disabled-box").show(400);
        $(checkbox).parents(".a-slo").find(".btn-addEvidence").show(400);
      }

    });
}

function setCheckboxValueTohide() {
  // console.log("init value: "+$(this).val());
  if ($(this).val() == "true") {
    $(this).val("false");
    // console.log("now is: "+$(this).val());
  } else {
    $(this).val("true");
    // console.log("now is: "+$(this).val());
  }

  if ($(this).val() == "false") {
    $(this).parents(".a-slo").find(".to-disabled-box").hide(400);
    $(this).parents(".a-slo").find(".btn-addEvidence").hide(400);
    // $(this).parents(".a-slo").find(".disabled-box").show();
  } else {
    // $(this).parents(".a-slo").find(".disabled-box").hide();
    $(this).parents(".a-slo").find(".to-disabled-box").show(400);
    $(this).parents(".a-slo").find(".btn-addEvidence").show(400);
    
  }

}


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

function createGoogleChart(chartID, type, options) {
  if (!googleChartsLoaded) {
    google.charts.load("current", {
      packages: ["corechart", "bar"],
    });
    googleChartsLoaded = true;
  }

  var $chart = $(chartID);
  if ($chart.exists()) {
    google.charts.setOnLoadCallback(function () {
      $chart.addClass("loaded");
      var data = new google.visualization.arrayToDataTable(
        getChartDataArray($chart)
      );
      console.log(data);
      if (!data) {
        $chart.append(
          '<p  class="text-center"> ' + options.title + " <br>  No data </p>"
        );
      } else {
        if (type == "Bar") {
          var view = new google.visualization.DataView(data);
          var chart = new google.visualization.BarChart(
            document.getElementById($chart[0].id)
          );
          chart.draw(view, google.charts.Bar.convertOptions(options));
        } else if (type == "Pie") {
          var chart = new google.visualization.PieChart(
            document.getElementById($chart[0].id)
          );
          chart.draw(data, options);
        }
      }
      console.log(type + ": " + chartID, options.title);
    });
  }
}

function updateAllIndexesContribution() {
  

    console.log('%cupdateAllIndexesContribution','background: #222; color: #84c3fd');
    //All sloTargetsList
 
  $('.sloTargetsList').find('.sloTarget').each(function(i,sloTarget) {

    $(sloTarget).attr('id', "outcome-"+(i+1));
    $(sloTarget).setNameIndexes(1, i);
    
    //  Update slo-contribution
     $(sloTarget).find('.evidenceList').find('.slo-contribution-section').each(function(i,evidence) {
      $(evidence).find('.indexSloContribution').html(i+1);
      $(evidence).attr('id', "milestone-"+(i+1));
      $(evidence).setNameIndexes(2, i);
     });
     
  });

  $(document).trigger('updateComponent');

}

function addEvidence() {
  
console.log('addEvidence');

  var $list =  $(this).parents(".simpleBox").find(".evidenceList");
  var $item = $('.slo-contribution-template');
  $item = $item.clone(true);
  $($item).removeClass('slo-contribution-template');
  $list.append($item);
  updateAllIndexesContribution();
  $item.show('slow');
  $item.find("select").select2({
    // templateResult: formatState,
    width: '100%'
});


$item.find('textarea.tumaco').trumbowyg({
  btns: [
    [
        'link', 'strong', 'em'
    ]
  ],
  allowTagsFromPaste: [
      'a', 'p', 'br', 'b', 'strong', 'i', 'em'
  ],
  urlProtocol: true,
  autogrow: true,
  minimalLinks: true,
  semantic: true
});

}

function removeEvidence(){
  console.log('Remove Evidence');
  var $item =  $(this).parents('.slo-contribution-section');
    $item.hide(function() {
      $item.remove();
      updateAllIndexesContribution();
    });

} 

