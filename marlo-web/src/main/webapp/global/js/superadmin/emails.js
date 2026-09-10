$(document).ready(init);

function init() {

  setDatatables();

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {
  $('button.sendEmails').on('click', function() {
    console.log("Send Emails");
    $.ajax({
        'url': baseURL + '/sendFailEmail.do',
        'data': {
          type: 1,
        },
        beforeSend: function() {
          $('.loading').show();
        },
        success: function(data) {
          console.log(data)
        },
        complete: function() {
          $('.loading').hide();
        },
        error: function() {

        }
    });
  });

}

/**
 * Emails Datatable
 *
 * The rows are rendered by marloEmails.ftl, so no ajax source and no column definitions are declared here:
 * DataTables reads the table that is already in the DOM.
 *
 * IMPORTANT (developers): DataTables' own search box is switched off through the dom option and replaced by the
 * MARLO search input above the table, which is the format the parameters and evidencies tables use. The subject
 * and error cells hold a link, and DataTables does not strip markup when it searches or sorts, so both carry
 * data-search and data-order with the plain text; without them a search for "modal" would match every row.
 */
function setDatatables() {

  var $emailsTable = $('#marloEmailsTable');

  if(!$.fn.dataTable || $emailsTable.length === 0 || $.fn.dataTable.isDataTable($emailsTable)) {
    return;
  }

  var emailsTable = $emailsTable.DataTable({
      "dom": 'rtip', // No length menu and no default search box: the MARLO one above the table is used instead
      "pageLength": 20, // Number of rows to show on the table
      "order": [
        [3, 'desc']
      ], // Most recent failure first
      "autoWidth": false,
      "language": {
        "emptyTable": "There are no emails on track",
        "zeroRecords": "No email matches the search",
        "info": "Showing _START_ to _END_ of _TOTAL_ emails",
        "infoEmpty": "No emails to show",
        "infoFiltered": "(filtered from _MAX_)"
      }
  });

  $('#marloEmailsSearch').on('keyup change search', function() {
    emailsTable.search($(this).val()).draw();
  });

}
