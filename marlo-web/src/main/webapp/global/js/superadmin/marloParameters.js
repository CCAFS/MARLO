$(document).ready(init);

function init() {

  $(".parameterValue.type-2").datepicker({
      dateFormat: "yy-mm-dd",
      minDate: '2012-01-01',
      maxDate: '2031-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true
  });

  $(".parameterValue.type-3").numericInput();

  registerParameterValueOrder();

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {

  $('.addParameter').on('click', addParameter);
  $('.removeParameter').on('click', removeParameter);
  $('.parameter-search').on('input', filterParametersBySearch);
  // The tables live inside collapsed blocks and hidden tabs, so each one is initialized the first time it
  // becomes visible instead of initializing every CRP table on page load.
  $('.crpParameters').on('shown.bs.tab', 'a[data-toggle="tab"]', function() {
    initializeParametersTables($($(this).attr('href')));
  });

  $('.blockTitle.closed').on('click', function() {
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
      initializeParametersTables($(this).find('.tab-pane.active'));
    });
  });

}

/*
 * NOTE (developers): this filter matches the RENDERED text of each row, and the key is rendered with the
 * display-only 'crp_' -> 'system_' relabeling done in marloParameters.ftl. A parameter stored in the
 * `parameters` table as 'crp_has_contact_point' is therefore found here by typing 'system_has_contact_point'.
 * The stored key is unchanged: never reuse the text typed or shown here as a key for SQL queries,
 * hasSpecificities()/specificityValue() calls or APConstants constants.
 */
function filterParametersBySearch() {
  var searchTerm = ($(this).val() || '').toLowerCase();
  var tableId = $(this).attr('data-target');
  var $table = $('#' + tableId);

  if(!$table.length) {
    return;
  }

  $table.find('tbody tr.parameter').each(function() {
    var keyText = ($(this).find('strong').text() || '').toLowerCase();
    var descText = ($(this).find('small i').text() || '').toLowerCase();
    var fullText = (keyText + ' ' + descText).trim();

    if(fullText.indexOf(searchTerm) !== -1) {
      $(this).show();
    } else {
      $(this).hide();
    }
  });
}

/*
 * DataTables setup for the parameters tables, using the same table format as the evidencies popup
 * (relationsPopupMacro.ftl / projectContributionsCrpList.js).
 *
 * IMPORTANT (developers): paging, searching and the length menu are intentionally OFF. DataTables detaches the
 * rows it is not drawing from the DOM, and every row here carries the form inputs of one CustomParameter. A
 * detached row is not submitted, and CrpParametersAction.save() DELETES every DB custom parameter missing from
 * the submitted list, so paging or DataTables' own search would silently wipe parameters on save. Sorting is
 * safe because it only reorders the rows that are already in the tbody. The search box above the table is the
 * custom one in filterParametersBySearch(), which only shows/hides rows and never removes them.
 */
function initializeParametersTables($container) {
  if(!$.fn.dataTable) {
    return;
  }
  $container.find('table.parametersTable').each(function() {
    var $table = $(this);
    if($.fn.dataTable.isDataTable($table)) {
      return;
    }
    $table.DataTable({
      "bPaginate": false, // Keep every row in the DOM: see the note above
      "bLengthChange": false,
      "bFilter": false, // The custom parameter search is used instead
      "bInfo": false,
      "bSort": true, // This option enables the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "order": [
        [0, 'asc']
      ],
      aoColumnDefs: [
        {
          orderDataType: "parameter-value",
          aTargets: [1]
        }
      ]
    });
  });
}

/*
 * The value column holds form controls instead of text, so DataTables is told how to read each cell: the checked
 * radio for booleans, the selected option for the role selects and the typed text for the remaining inputs.
 */
function registerParameterValueOrder() {
  if(!$.fn.dataTable) {
    return;
  }
  $.fn.dataTable.ext.order['parameter-value'] = function(settings, colIndex) {
    return this.api().column(colIndex, {
      order: 'index'
    }).nodes().map(function(cell) {
      var $cell = $(cell);

      var $checkedRadio = $cell.find('input[type="radio"]:checked');
      if($checkedRadio.length) {
        return $checkedRadio.val() === 'true' ? 'Yes' : 'No';
      }

      var $select = $cell.find('select');
      if($select.length) {
        return ($select.find('option:selected').text() || '').trim();
      }

      var $input = $cell.find('input[type="text"], input:not([type]), textarea');
      if($input.length) {
        return ($input.val() || '').trim();
      }

      return ($cell.text() || '').trim();
    });
  };
}

function addParameter() {
  var $item = $('#parameter-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.crpParameters').find("table tbody");
  // Adding item to the list
  $list.append($item);
  // Update Indexes
  updateParametersIndexes();
  // Show item
  $item.show('slow');
}

function removeParameter() {
  var $parent = $(this).parents('tr');
  $parent.hide('slow', function() {
    $parent.remove();
    updateParametersIndexes();
  });
}

function updateParametersIndexes() {
  $('.crpParameters').each(function(i,crpParameters) {
    $(crpParameters).find('.parameter').each(function(j,parameter) {
      $(parameter).setNameIndexes(1, i);
      $(parameter).setNameIndexes(2, j);
    });
  });
}