var $tableViewMore;
var tableDatatableViewmore, tableDataProgressTableViewmore, tableDatatableTableGrey, tableInnovations, tablePolicies, tableOICRs;
var pageName;
var googleChartsLoaded = false;
var isActive = false;
var isBtnClose = false;

function getContributionListComponentValue(contributionData) {

  //Variables
  const {
    additionalContribution,
    geographicScope,
    summary
  } = contributionData;

  //strings
  let geographicScopeString = '';
  let regionString = '';
  let countriesString = '';
  let countriesStringV2 = '';

  geographicScope.forEach(geoData => {
    geographicScopeString += `<p> - ${geoData.name}</p>`;

    if (geoData.name == "National") {
      geoData.element.forEach(nationalData => {
        countriesStringV2 += `<p> - ${nationalData.name}</p>`;
      });
    }

    if (geoData.name == "Multi-national") {
      geoData.element.forEach(multiNationalData => {
        countriesString += `<p> - ${multiNationalData.name}</p>`;
      });
    }

    if (geoData.name == "Regional") {
      geoData.element.forEach(regionalData => {
        regionString += `<p> - ${regionalData.name}</p>`;
      });
    }

  });


  geographicScopeString = geographicScopeString == '' ? '<p>  Not available</p>' : geographicScopeString;


  return `
  <br>
  <div style="background-color: rgb(250, 250, 250); border-radius: 10px; padding: 7px; position: relative;">

  <div class="leftHead  sm">
    <!--<span class="index">12</span>-->
    <span class="index indexSloContribution">...</span>
  </div>

  <br>
      <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px; margin-top: 10px;">Geographic scope:</p>
      ${geographicScopeString}
     
      <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px; margin-top: 10px;display: ${regionString == '' ? 'none' : 'block'};">Regions:</p>
      ${regionString} 

      <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px; margin-top: 10px;display: ${(countriesString == '' && countriesStringV2 == '') ? 'none' : 'block'};">Country(ies):</p>
      ${countriesString || countriesStringV2} 
      
      <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px;">Brief summary of new evidence of CGIAR contribution:</p>
      <p>${summary || "Not available"}</p>
   
      <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px;">Expected additional contribution before end of 2022 (if not already fully covered)</p>
      <p>${additionalContribution || "Not available"}</p>
</div>
  
  `
}

function getTargetCasesBySLO() {

  for (let index = 1; index < 11; index++) {

    $.ajax({
      url: baseURL + '/targetCasesBySLO.do',
      data: {
        id: index
      },
      beforeSend: function () {
        // console.log("before");
      },
      success: function (data) {

        contributionListComponentInsertHTML(data, index);
      },
      error: function (e) {
        console.log(e);
      },
      complete: function () {
        // console.log("complete"); 
      }
    });
  }
}

function contributionListComponentInsertHTML(data, id) {
  var count = 0;
  var activeFP = false;

  $(`.flagshipBtn-${id}`).on('click', changeButtonText);
  $(`.btnClose-${id}`).on('click', closePopup);

  data.sources.forEach((item, index) => {
    if (item.contribution.length == 0) {
      count += 1;
      if (count == data.sources.length) {
        $(`.flagshipBtn-${id}`).prop("disabled", true);
        $(`.flagshipBtn-${id}`).text("No Flagships information");
        $(`.flagshipBtn-${id}`).prepend(`<span class="glyphicon glyphicon-info-sign" style="margin-right: 7px; position: relative; top:3px"></span>`);
        // $(`.insertHtmlSlo-tabpanel-${id}`).append(`<p class="tb1-Fp-noData"><span class="glyphicon glyphicon-info-sign" style="margin-right: 7px; position: relative; top:3px"></span>No Flagships information</p>`);
      }
    } else {
      $('.insertHtmlSlo-tabs-' + id).append(`<li role="presentation" class="${!activeFP ? 'active' : ''}" ><a href="#${item.id}-${id}-tab" aria-controls="${item.id}-${id}-tab" role="tab" data-toggle="tab">${item.id}</a></li>`);
      $('.insertHtmlSlo-tabpanel-' + id).append(`<div role="tabpanel" class="tab-pane ${!activeFP ? 'active' : ''}" id="${item.id}-${id}-tab" style="overflow-y: scroll; max-height: 510px;"></div>`);
      activeFP = true;
    }
    item.contribution.forEach(contributionData => {
      $(`#${item.id}-${id}-tab`).append(getContributionListComponentValue(contributionData));
    });
  });
}

$(document).ready(function () {

  getTargetCasesBySLO();
  // $('.slo-contribution-template').find('textarea').trumbowyg("destroy");

  if ($('.slo-contribution-template').length) {
    $('.slo-contribution-template').find('select').select2("destroy");
  }
  pageName = actionName.replace(/[^a-z0-9+]+/gi, '_');

  // Set data tables
  if ($.fn.DataTable) {
    $totalNumber = $('.totalNumber');

    $tableViewMore = $('.viewMoreSyntesis-block table');
    if ($('.totalParticipantsNumber').html() != 0 && $('.totalParticipantFormalTrainingNumber').html() != 0) {
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
    }

    $progressTableViewMore = $('.viewMoreSyntesisTable-block table');
    if ($('.totalViewMoreNumber').html() != 0) {
      tableDataProgressTableViewmore = $progressTableViewMore.DataTable({
        "paging": false,
        "searching": false,
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
    }

    $tableInnovationsHTML = $('.tableNoPaginator-block table');
    if ($totalNumber.html() != 0) {
      tableInnovations = $tableInnovationsHTML.DataTable({
        "paging": false,
        "searching": false,
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
    }

    $tablePoliciesHTML = $('.tablePolicies-block table');
    if ($totalNumber.html() != 0) {
      tablePolicies = $tablePoliciesHTML.DataTable({
        "paging": false,
        "searching": false,
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
    }

    $tableOICRsHTML = $('.tableOICRs-block table');
    if ($totalNumber.html() != 0) {
      tableOICRs = $tableOICRsHTML.DataTable({
        "paging": false,
        "searching": false,
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
    }

    $TablePRP = $('.viewMoreSyntesisTablePRP-block table');
    if ($totalNumber.html() != 0) {
      tableDatatableTablePRP = $TablePRP.DataTable({
        "paging": false,
        "searching": false,
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
    }

    $TableGrey = $('.viewMoreSyntesisTableGrey-block table');
    if ($('.totalGreyNumber').html() != 0) {
      tableDatatableTableGrey = $TableGrey.DataTable({
        "paging": false,
        "searching": false,
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
    }

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

  $('.urlify').each(function (i, urlifyText) {
    var text = $(urlifyText).html();

    if ($(urlifyText).find('a').length > 0) {
      // Short URLs text
      $(urlifyText).find('a').each(function (iAnchor, anchor) {
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
  $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
    window.localStorage.setItem(pageName, JSON.stringify({
      "indexTab": $(this).attr("index")
    }));
  });

  // checkbox disables field
  console.log("init press");

  $('.checkboxDiTeArClick').on('click', setCheckboxValueTohide);
  $('.btn-addEvidence').on('click', addEvidence);
  $('.btn-removeEvidence').on('click', removeEvidence);
  $('#selectAll').on('click', selectDeselectAll);
  $('input[id^="deliverable-"]').on('click', selectIndividual);
  $('#selectAllGrey').on('click', selectDeselectAllGrey);
  $('input[id^="deliverableGrey"]').on('click', selectIndividualGrey);
  $('#selectAllPolicies').on('click', selectDeselectAllPolicies);
  $('input[id^="policy-"]').on('click', selectIndividualPolicies);
  $('#selectAllInnovations').on('click', selectDeselectAllInnovations);
  $('input[id^="innovation-"]').on('click', selectIndividualInnovations);
  $('#selectAllMilestones').on('click', selectDeselectAllMilestones);
  $('input[id^="milestone-"]').on('click', selectIndividualMilestones);
  $('#selectAllStudies').on('click', selectDeselectAllStudies);
  $('input[id^="study-"]').on('click', selectIndividualStudies);
  $('input[id^="disabled-study-"]').on('click', selectIndividualDisabledStudies);
  $('#selectAllMelias').on('click', selectDeselectAllMelias);
  $('input[id^="melia-"]').on('click', selectIndividualMelias);

  // Deliverable Geographic Scope
  $('select.elementType-repIndGeographicScope').on("addElement removeElement", function (event, id, name) {
    console.log('%cevent setGeographicScope', 'background: #222; color: #37ff73');
    setGeographicScope(this);
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);
  // valiate checkbox "No DOI provided" value


  // $('.TA_summaryEvidence .trumbowyg-editor').bind('DOMSubtreeModified', function(){
  //   console.log('%cmovement','background: #222; color: #fd8484');
  //   // $(this).parents('.slo-contribution-section').find('.TA_summaryEvidence .briefSummaryTAHidden').css("background-color", "yellow");
  //   $(this).parents('.slo-contribution-section').find('.TA_summaryEvidence .briefSummaryTAHidden').html($(this).html());
  // });


  // $('.TA_additionalContribution .trumbowyg-editor').bind('DOMSubtreeModified', function(){
  //   console.log('%cmovement','background: #222; color: #fd8484');

  //   $(this).parents('.slo-contribution-section').find('.TA_additionalContribution .additionalContributionTAHidden').html($(this).html());
  // });

  // $('.button-save').on('click',updateALltexareas);
  $(document).keypress(updateALltexareas);
  $(document).click(updateALltexareas);


  // $('.slo-contribution-section').on("bind",".TA_summaryEvidence .trumbowyg-editor", function() {
  //   //do whatever
  //   console.log('%cHola mundo','background: #222; color: #fd8484');
  // });

  setStatusByBack();
  // updateAllIndexesContribution();

  // selectIndividual();
  // selectIndividualGrey();
  // selectIndividualPolicies();
  // selectIndividualInnovations();
  // selectIndividualStudies();
  appearDisappearFlagshipsTable(isActive, 0);
});

function updateALltexareas() {
  $('.sloTargetsList').find('.sloTarget').each(function (i, sloTarget) {
    setTimeout(() => {
      $(sloTarget).find('.evidenceList').find('.slo-contribution-section').each(function (i, evidence) {
        // $(evidence).css("background-color", "yellow");
        $(evidence).find('.TA_summaryEvidence .briefSummaryTAHidden').html($(evidence).find('.TA_summaryEvidence .trumbowyg-editor').html());
        $(evidence).find('.TA_additionalContribution .additionalContributionTAHidden').html($(evidence).find('.TA_additionalContribution .trumbowyg-editor').html());
        // console.log($(evidence).find('.TA_additionalContribution .trumbowyg-editor').val());
      });
    }, 10);
  });
  $(document).trigger('updateComponent');
}

function setStatusByBack() {
  $(".sloTargetsList")
    .find(".sloTarget")
    .each(function (i, field) {
      // console.log($(field).find(".checkboxDiTeArClick").val());

      let checkbox = $(field).find(".checkboxDiTeArClick");
      // console.log($(checkbox).val());



      if ($(checkbox).val() == "true") {
        $(checkbox).val("false");
        // console.log("now is: "+$(this).val());

      } else {
        $(checkbox).val("true");
        // console.log("now is: "+$(this).val());

      }

      if ($(checkbox).val() == "true") {
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

function selectDeselectAll() {
  if ($(this).hasClass('checked')) {
    $('input[id^="deliverable-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', true);
      }
    });
    $(this).removeClass('checked');
  } else {
    $('input[id^="deliverable-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', false);
      }
    });
    $(this).addClass('checked');
  }
}

function selectIndividual() {
  if ($('input[id^="deliverable-"]').length == $('input[id^="deliverable-"]:checked').length) {
    $('#selectAll').prop('checked', true);
  } else {
    $('#selectAll').prop('checked', false);
  }
}

function selectDeselectAllGrey() {
  if ($(this).hasClass('checked')) {
    $('input[id^="deliverableGrey"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', true);
      }
    });
    $(this).removeClass('checked');
  } else {
    $('input[id^="deliverableGrey"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', false);
      }
    });
    $(this).addClass('checked');
  }
}

function selectIndividualGrey() {
  if ($('input[id^="deliverableGrey"]').length == $('input[id^="deliverableGrey"]:checked').length) {
    $('#selectAllGrey').prop('checked', true);
  } else {
    $('#selectAllGrey').prop('checked', false);
  }
}

function selectDeselectAllPolicies() {
  if ($(this).hasClass('checked')) {
    $('input[id^="policy-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', true);
      }
    });
    $(this).removeClass('checked');
  } else {
    $('input[id^="policy-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', false);
      }
    });
    $(this).addClass('checked');
  }
}

function selectIndividualPolicies() {
  if ($('input[id^="policy-"]').length == $('input[id^="policy-"]:checked').length) {
    $('#selectAllPolicies').prop('checked', true);
  } else {
    $('#selectAllPolicies').prop('checked', false);
  }
}

function selectDeselectAllInnovations() {
  if ($(this).hasClass('checked')) {
    $('input[id^="innovation-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', true);
      }
    });
    $(this).removeClass('checked');
  } else {
    $('input[id^="innovation-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', false);
      }
    });
    $(this).addClass('checked');
  }
}

function selectIndividualInnovations() {
  if ($('input[id^="innovation-"]').length == $('input[id^="innovation-"]:checked').length) {
    $('#selectAllInnovations').prop('checked', true);
  } else {
    $('#selectAllInnovations').prop('checked', false);
  }
}

function selectDeselectAllMilestones() {
  if ($(this).hasClass('checked')) {
    $('input[id^="milestone-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', true);
      }
    });
    $(this).removeClass('checked');
  } else {
    $('input[id^="milestone-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', false);
      }
    });
    $(this).addClass('checked');
  }
}

function selectIndividualMilestones() {
  if ($('input[id^="milestone-"]').length == $('input[id^="milestone-"]:checked').length) {
    $('#selectAllMilestones').prop('checked', true);
  } else {
    $('#selectAllMilestones').prop('checked', false);
  }
}

function selectDeselectAllStudies() {
  if ($(this).hasClass('checked')) {
    $('input[id^="study-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', true);
      }
    });
    $(this).removeClass('checked');
  } else {
    $('input[id^="study-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', false);
      }
    });
    $(this).addClass('checked');
  }
}

function selectIndividualStudies() {
  if ($('input[id^="study-"]').length == $('input[id^="study-"]:checked').length) {
    $('#selectAllStudies').prop('checked', true);
  } else {
    $('#selectAllStudies').prop('checked', false);
  }
}

function selectIndividualDisabledStudies() {
  let name = $(this).attr('name');
  
  $(this).prop('disabled', true);
  $(this).next().next().prop('checked', true)
  $(this).next().next().prop('name', name);
  $(this).parent().parent().attr('title', 'In order to take out this OICR from the Annual Report, please make sure there are no related policies or innovations mapped to it that are also part of the Annual Report.');
  $(this).closest('.inputContainer').find('.checkmark').css('border', '1px solid #9f9d9d');
}

function selectDeselectAllMelias() {
  if ($(this).hasClass('checked')) {
    $('input[id^="melia-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', true);
      }
    });
    $(this).removeClass('checked');
  } else {
    $('input[id^="melia-"]').map((index, item) => {
      if($(item).prop('disabled') == false) {
        $(item).prop('checked', false);
      }
    });
    $(this).addClass('checked');
  }
}

function selectIndividualMelias() {
  if ($('input[id^="melia-"]').length == $('input[id^="melia-"]:checked').length) {
    $('#selectAllMelias').prop('checked', true);
  } else {
    $('#selectAllMelias').prop('checked', false);
  }
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

  if ($(this).val() == "true") {
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
  if ((ls != null)) {
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
  $(chart).find('.chartData li').each(function (i, e) {
    dataArray.push($(e).find('span').map(function () {
      var text = $(this).text();
      if ($(this).hasClass('number')) {
        return parseFloat(text);
      } else if ($(this).hasClass('json')) {
        return JSON.parse(text);
      } else {
        return text;
      }
    }).toArray());
  });
  return dataArray;
}

function createGooglePieChart(chartID, options) {
  createGoogleChart(chartID, "Pie", options);
}

function createGoogleBarChart(chartID, options) {
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
      console.log(data, data.bf.length);
      if (!data.bf.length || $totalNumber.html() == 0 || $('.totalParticipantsNumber').html() == 0 && $('.totalParticipantFormalTrainingNumber').html() == 0) {
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


  console.log('%cupdateAllIndexesContribution', 'background: #222; color: #84c3fd');
  //All sloTargetsList

  $('.sloTargetsList').find('.sloTarget').each(function (i, sloTarget) {

    $(sloTarget).attr('id', "outcome-" + (i + 1));
    $(sloTarget).setNameIndexes(1, i);

    //  Update slo-contribution
    $(sloTarget).find('.evidenceList').find('.slo-contribution-section').each(function (i, evidence) {
      $(evidence).find('.indexSloContribution').html(i + 1);
      $(evidence).attr('id', "milestone-" + (i + 1));
      $(evidence).setNameIndexes(2, i);
    });

  });

  $(document).trigger('updateComponent');

}

function changeButtonText() {
  var className = $(this).attr('class');
  var theNum = className.match(/\d/g).join('');

  if ($(this).text() == 'Show flagships information') {
    $(this).text('Hide flagships information');
    $(`.highlightedTitle-${theNum}`).css('background', '#71b2ff');
    $(`.highlightedTitle-${theNum}`).css('color', 'white');
    isActive = true;
    isBtnClose = false;
  } else {
    $(this).text('Show flagships information');
    $(`.highlightedTitle-${theNum}`).css('background', 'none');
    $(`.highlightedTitle-${theNum}`).css('color', '#5f5e5e');
    isActive = false;
  }

  if (isActive) {
    $('button[class*="flagshipBtn"]').not(this).prop('ariaExpanded', "false");
    $('button[class*="flagshipBtn"]').not(this).addClass('collapsed');
    $('div[class*="crpProgressflagships"]').removeClass('in');
    $('button[class*="flagshipBtn"]').not(this).text('Show flagships information');
    $('span[class*="highlightedTitle"]').not($(`.highlightedTitle-${theNum}`)).css('background', 'none');
    $(`span[class*="highlightedTitle"]`).not($(`.highlightedTitle-${theNum}`)).css('color', '#5f5e5e');
  }

  appearDisappearFlagshipsTable(isActive, theNum);
}

function closePopup() {
  var className = $(this).attr('class');
  var theNum = className.match(/\d/g).join('');

  if (isBtnClose) {
    $(`.highlightedTitle-${theNum}`).css('background', '#71b2ff');
    $(`.highlightedTitle-${theNum}`).css('color', 'white');
    isActive = true;
    isBtnClose = false;
  } else {
    $(`.highlightedTitle-${theNum}`).css('background', 'none');
    $(`.highlightedTitle-${theNum}`).css('color', '#5f5e5e');
    isActive = false;
    isBtnClose = true;
  }

  if (isBtnClose) {
    $('button[class*="flagshipBtn"]').not($(`flagshipBtn-${theNum}`)).prop('ariaExpanded', "false");
    $('button[class*="flagshipBtn"]').not($(`flagshipBtn-${theNum}`)).addClass('collapsed');
    $('div[class*="crpProgressflagships"]').removeClass('in');
    $('button[class*="flagshipBtn"]').not($(`flagshipBtn-${theNum}`)).text('Show flagships information');
    $('span[class*="highlightedTitle"]').not($(`.highlightedTitle-${theNum}`)).css('background', 'none');
    $(`span[class*="highlightedTitle"]`).not($(`.highlightedTitle-${theNum}`)).css('color', '#5f5e5e');
  }

  appearDisappearFlagshipsTable(isActive, theNum);
}

function appearDisappearFlagshipsTable(isActive, theNum) {
  window.onscroll = function () {
    let yScroll = window.scrollY;

    if (isActive) {
      // if (yScroll <= 1444) {
      //   $(`#collapseExample-${theNum - 1}`).css('opacity', 0);
      //   $(`#collapseExample-${theNum - 1}`).removeClass('in');
      // } else {
      //   $(`#collapseExample-${theNum - 1}`).css('opacity', 1);
      //   $(`#collapseExample-${theNum - 1}`).addClass('in');
      // }
      $(`#collapseExample-${theNum - 1}`).draggable();
    }
  }
}

function addEvidence() {

  console.log('addEvidence');

  var $list = $(this).parents(".simpleBox").find(".evidenceList");
  var $item = $('.slo-contribution-template');
  $item = $item.clone(true);
  $($item).removeClass('slo-contribution-template');
  $list.append($item);


  // $($item).find('.TA_summaryEvidence .trumbowyg-editor').bind('DOMSubtreeModified', function(){
  //  console.log('%csome','background: #222; color: #84c3fd');
  // });


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
    plugins: {
      allowTagsFromPaste: {
        allowedTags: ['a', 'p', 'br', 'b', 'strong', 'i', 'em']
      }
    },
    urlProtocol: true,
    autogrow: true,
    minimalLinks: true,
    semantic: true
  });

}

function removeEvidence() {
  console.log('Remove Evidence');
  var $item = $(this).parents('.slo-contribution-section');
  $item.hide(function () {
    $item.remove();
    updateAllIndexesContribution();
  });

}

