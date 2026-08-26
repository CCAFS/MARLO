$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  addSelect2();

  initializeUsageTables();

}

function attachEvents() {
  $('.addSlo').on('click', addIdo);

  $('.remove-element').on('click', removeElement);

  $('.blockTitle.closed').on('click', function(e) {
    /*
     * The usage counter sits inside the title, so ignore clicks coming from it: the block must not toggle when the
     * modal is opened. It has to be filtered here and not with stopPropagation() on the counter, because Bootstrap
     * opens modals from a handler delegated on document -- stopping the click would keep the modal from ever
     * opening.
     */
    if ($(e.target).closest('.elementRelations').length) {
      return;
    }
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    /*
     * Not next(): the relations popup of feedbackFieldRelationsMacro sits between the title and .blockContent, so
     * next() would toggle the popup block and leave the field collapsed on every field that has comments.
     */
    $(this).nextAll('.blockContent').first().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
    });
  });

}

/**
 * Turns every field-usage table inside the relations modals into a sortable, searchable DataTable, following the
 * same configuration used by projectContributionsCrpList.
 */
function initializeUsageTables() {
  var $tables = $('table.feedbackUsageList');
  if (!$tables.length || !$.fn.DataTable) return;

  $tables.each(function() {
    var $table = $(this);
    if ($.fn.DataTable.isDataTable($table)) return;

    var api = $table.DataTable({
      "bPaginate": true,
      "bLengthChange": true,
      "bFilter": true,
      "bSort": true,
      "bAutoWidth": false,
      // First entry of the default length menu ([10, 25, 50, 100]), so the dropdown opens on 10.
      "iDisplayLength": 10,
      "language": {
        searchPlaceholder: "Search..."
      },
      // Columns: 0 project, 1 phase, 2 comment count, 3 link.
      // Comment count descending: the heaviest usage is what the administrator is looking for.
      "order": [
        [2, 'desc']
      ],
      aoColumnDefs: [
        {
          // The link column carries no text to sort by.
          bSortable: false,
          aTargets: [-1]
        }
      ]
    });

    /*
     * container() returns the wrapper DataTables builds around the table, which is also where it puts
     * .dataTables_filter. Going through the API instead of walking the DOM keeps this working regardless of how
     * the table ends up nested inside the modal.
     */
    var $filter = $(api.table().container()).find('.dataTables_filter');
    if ($filter.length) {
      var iconSearch = $("<div></div>").addClass("iconSearch");
      iconSearch.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="" style="width: 24px; margin: auto;" >');
      $filter.prepend(iconSearch);
    }
  });

  /*
   * A DataTable built inside a hidden modal measures its columns against a zero-width container, so the header and
   * the body end up misaligned. Recomputing once the modal is visible is the documented workaround.
   */
  $('.modal').on('shown.bs.modal', function() {
    $(this).find('table.feedbackUsageList').each(function() {
      if ($.fn.DataTable.isDataTable(this)) {
        $(this).DataTable().columns.adjust();
      }
    });
  });
}

function addIdo() {
  var $itemsList = $(this).parent().find('.slos-list');
  var $item = $("#srfSlo-template").clone(true).removeAttr("id");
  $item.find('.blockTitle').trigger('click');
  $itemsList.append($item);
  // The clone comes from the untouched template, so its select is still a plain one: give it the search box too.
  initSelect2Within($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

/**
 * Section Name is a slug picked from ProjectSectionsEnum, so it gets the same searchable dropdown used by the
 * dropdowns of Feedback Roles Permissions. Scoped to `form select` because the hidden template row is rendered
 * outside the form: select2 measures a hidden element as zero-width, and clone(true) would then copy a broken
 * widget into every row added afterwards.
 */
function addSelect2() {
  initSelect2Within($('form'));
}

// Initialize select2 only within a given block (used on load and after cloning the template).
function initSelect2Within($ctx) {
  $ctx.find('select.sectionName').each(function() {
    var $select = $(this);
    if ($select.data('select2')) {
      try { $select.select2('destroy'); } catch (e) {}
    }
    $select.select2({ width: '100%' });
  });
}

function removeElement() {
  $item = $(this).parent();
  $item.hide('slow', function() {
    $item.remove();
    updateIndexes();
    $(document).trigger('removeComponent');
  });
}

function updateIndexes() {
  $('.slos-list .srfSlo').each(function(i,slo) {
    // Updating indexes
    $(slo).setNameIndexes(1, i);
    $(slo).find('.srfSloIndicator').each(function(subIdoIndex,subIdo) {
      // Updating indexes
      $(subIdo).setNameIndexes(2, subIdoIndex);
    });
  });

  $('.issues-list .srfCCIssue').each(function(i,crossCutting) {
    // Updating indexes
    $(crossCutting).setNameIndexes(1, i);

  });
}
