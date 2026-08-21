$(document).ready(initDashboard);

function initDashboard() {
  initClusterBanner();
  initBrowseByCategory();

  $('#newProject').on('click', function (e) {
    $('#decisionTree .addProjectButtons').show(0, function () {
      $(this).addClass('animated flipInX');
    });
  });

  $('.loadingBlock').hide().next().fadeIn(500);
}

/**
 * "Browse by category" rail.
 */
function initBrowseByCategory() {
  $('.dashboardBrowse__cat').on('click', updateTable);
}

/**
 * Collapse/expand the "What is a Cluster?" banner. The choice is remembered so
 * a user who hides it does not have to hide it again on every page load.
 */
function initClusterBanner() {
  var toggle = document.getElementById('clusterBannerToggle');
  var banner = document.getElementById('clusterBanner');
  if (!toggle || !banner) {
    return;
  }

  var STORAGE_KEY = 'marlo.clusterBanner.collapsed';
  var label = toggle.querySelector('span');

  function render(collapsed) {
    banner.classList.toggle('clusterBanner--collapsed', collapsed);
    toggle.setAttribute('aria-expanded', String(!collapsed));
    label.textContent = collapsed
      ? toggle.getAttribute('data-label-show')
      : toggle.getAttribute('data-label-hide');
  }

  var stored = null;
  try {
    stored = window.localStorage.getItem(STORAGE_KEY);
  } catch (e) {
    // Private mode or blocked storage: fall back to the expanded default.
  }
  render(stored === 'true');

  toggle.addEventListener('click', function () {
    var collapsed = toggle.getAttribute('aria-expanded') === 'true';
    render(collapsed);
    try {
      window.localStorage.setItem(STORAGE_KEY, String(collapsed));
    } catch (e) {
      // Nothing to do; the toggle still works for this page view.
    }
  });
}

/**
 * Category rail: activate the clicked category and reveal its table.
 */
function updateTable() {
  // The pane is named by the button rather than looked up through the
  // "<tableId>_wrapper" element DataTables builds: the table ids collide with
  // the button ids, and the lookup fails outright if DataTables never
  // initialised — for instance on a category whose table came back empty.
  var pane = document.querySelector('.dashboardBrowse__panel #' + this.getAttribute('data-pane'));
  if (!pane) {
    return;
  }

  $('.dashboardBrowse__cat').removeClass('is-active').attr('aria-pressed', 'false');
  $(this).addClass('is-active').attr('aria-pressed', 'true');

  $('.dashboardBrowse__panel .tab-pane').removeClass('in active');
  pane.classList.add('in', 'active');

  // Keep the "Showing <category> for <phase>" chip in sync with the rail.
  var chip = document.getElementById('dashboardScopeChip');
  if (chip) {
    var template = chip.getAttribute('data-scope-template') || '{0}';
    chip.textContent = template.replace('{0}', this.getAttribute('data-scope') || '');
  }
}

$('table.projectsList').dataTable({
  "bPaginate": true, // This option enable the table pagination
  "bLengthChange": true, // This option disables the select table size option
  "bFilter": true, // This option enable the search
  "bSort": true, // this option enable the sort of contents by columns
  "bAutoWidth": false, // This option enables the auto adjust columns width
  "iDisplayLength": 25, // Number of rows to show on the table
  "pagingType": "simple",
  language: {
    searchPlaceholder: "Search..."
  },
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


// Search icon, appended to the column dataTables.net-bs builds for the filter.
// Placement is CSS only: the previous version stripped the .row class off the
// control rows and pulled the page-size column out of flow with an absolute
// position, which dropped it on top of the pagination buttons.
$('.dataTables_filter').parent().each(function () {
  $('<div></div>')
    .addClass('iconSearch')
    .append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="" style="width: 24px; margin: auto;" >')
    .prependTo(this);
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
