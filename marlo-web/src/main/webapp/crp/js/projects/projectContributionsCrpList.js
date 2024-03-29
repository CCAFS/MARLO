$(document).ready(function() {
  
  var $addOutcome = $('.addOutcomeBlock a');
  var $addOutcomeSelect = $('.outcomesListBlock select');
  var OUTCOME_ID_PARAMETER = 'outcomeId';
  
  $('select').select2({
    templateResult: formatState,
    templateSelection: formatStateSelection,
    width: '100%'
  });

  var outcomesSelectedIds = ($('#outcomesSelectedIds').text()).split(',');
  $addOutcomeSelect.clearOptions(outcomesSelectedIds);

  $addOutcomeSelect.on('change', function(e) {
    var $this = $(this);
    var url = $addOutcome.attr('href');
    var uri = new Uri(url);
    var projectOutcomeID = $this.val();
    uri.replaceQueryParam(OUTCOME_ID_PARAMETER, projectOutcomeID);
    $addOutcome.attr('href', uri);
  });

  $addOutcome.on('click', function(e) {
    e.preventDefault();
    var $this = $(this);
    var url = $this.attr('href');
    var projectOutcomeID = new Uri(url).getQueryParamValue(OUTCOME_ID_PARAMETER);

    if (projectOutcomeID == "-1") {
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'You must select an indicator for adding.';
      noty(notyOptions);
    } else {
      window.location.href = url;
      addNewOutcome();
    }
  });

  /* Reporting events */
  attachReportingEvents();

  lp6menu();

  var $tables = $('table.deliverableList, table.innovationList, table.evidencieList, table#projectOutcomesList');
  $tables.each(function() {
    var $table = $(this);
    initializeDataTable($table);
  });

  //Add styles to the table
  var iconSearch = $("<div></div>").addClass("iconSearch");
  var divDataTables_filter = document.querySelectorAll('.dataTables_filter');
  iconSearch.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="Imagen"  style="width: 24px; margin: auto;" >');
  iconSearch.prependTo(divDataTables_filter);

});

function addNewOutcome() {
  var value = $addOutcomeSelect.find('option:selected').val();
  var name = $addOutcomeSelect.find('option:selected').text();
}

function formatStateSelection(state) {
  if (state.id != "-1") {
    var text = $("span.outcomeID-" + state.id).html();
    var $state = $(text);
    $state.find('.indicatorText').remove();
    return $state;
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }
}

function formatState(state) {
  if (state.id != "-1") {
    var text = $("span.outcomeID-" + state.id).html();
    return $(text);
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }
}

/** Reporting functions */
function attachReportingEvents() {
  $('.addOtherContribution').on('click', addOtherContribution);
  $('.removeElement').on('click', removeOtherContribution);
  $('input[name="lp6Contribution"]').on('change', contributionToLP6);
}

function addOtherContribution() {
  var $item = $('#otherContribution-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.tab-pane').find('.otherContributionsBlock');
  $list.append($item);
  $item.show('slow');
}

function removeOtherContribution() {
  var $parent = $(this).parent();
  $parent.hide('slow', function() {
    $parent.remove();
  });
}

function contributionToLP6() {
  var projectID = $(this).parents('.borderBox').classParam('project');
  var phaseID = $(this).parents('.borderBox').classParam('phase');
  var contributionValue = this.value;

  $.ajax({
    url: baseURL + '/projectCollaborationValue.do',
    data: {
      projectID: projectID,
      phaseID: phaseID,
      "crp_lp6_contribution_value": contributionValue
    },
    success: function(data) {
      window.location.href = window.location.href;
    }
  });
}

function lp6menu() {
  var $menu = $("#menu-contributionsLP6");
  if ($menu.hasClass('toSubmit')) {
    // $menu.animateCss('rubberBand');
  }
}

function initializeDataTable($table) {
  var table = $table.DataTable({
    "bPaginate": true, // This option enable the table pagination
    "bLengthChange": true, // This option disables the select table size option
    "bFilter": true, // This option enable the search
    "bSort": true, // this option enable the sort of contents by columns
    "bAutoWidth": false, // This option enables the auto adjust columns width
    "iDisplayLength": 25, // Number of rows to show on the table
    "language": {
      searchPlaceholder: "Search..."
    },
    "fnDrawCallback": function() {
      // This function locates the add activity button at left to the filter box
      var table = $(this).parent().find("table");
      if ($(table).attr("id") == "currentActivities") {
        $("#currentActivities_filter").prepend($("#addActivity"));
      }
    },
    "order": [
      [3, 'asc']
    ],
    aoColumnDefs: [
      {
        bSortable: true,
        aTargets: [-1]
      },
      {
        sType: "natural",
        aTargets: [0]
      }
    ]
  });

  //Add styles to the table
  var iconSearch = $("<div></div>").addClass("iconSearch");
  var divDataTables_filter = $table.find('.dataTables_filter');
  iconSearch.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="Imagen"  style="width: 24px; margin: auto;" >');
  iconSearch.prependTo(divDataTables_filter);
}
