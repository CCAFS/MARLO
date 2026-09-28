/**
 * System Admin -> Emails
 *
 * The table reads its rows a page at a time from emailLogs.do (DataTables server-side mode), so neither the sent
 * emails nor the messages are loaded with the page. The two tabs switch between the emails not sent and the sent
 * ones; the filters apply to both, and the re-send works on exactly the rows the filters select.
 *
 * Every text comes from the data-text-* attributes of #emailsOnTrack, which marloEmails.ftl fills from the i18n
 * properties.
 */
$(document).ready(init);

var emailsTable;
var emailsStatus = 'notSent';
var emailsNotSentFiltered = 0;
var emailsResendArmed = false;

// Position of each column, and the name emailLogs.do orders it by.
var EMAILS_COLUMNS = [ 'DATE', 'SUBJECT', 'TO', 'GLOBAL_UNIT', 'SOURCE', 'TRIED', null ];
var EMAILS_ERROR_COLUMN = 6;

function init() {
  setDatatables();
  attachEvents();
}

function emailsText(name) {
  return $('#emailsOnTrack').data('text-' + name) || '';
}

function formatText(text, values) {
  return text.replace(/\{(\d+)\}/g, function(match, index) {
    return values[index] !== undefined ? values[index] : match;
  });
}

function escapeHtml(value) {
  return $('<div>').text(value === undefined || value === null ? '' : String(value)).html();
}

/**
 * "/projects/partners" reads as "Projects › Partners", and "/crp/admin/manageUsers" as "Admin › Manage users": the
 * crp namespace prefix says nothing to the reader. The raw value stays in the title, for the rows whose action name
 * says little on its own. NULL is a row logged before the source was recorded; "background" is a send outside any
 * request.
 */
function sourceLabel(source) {
  if(!source) {
    return emailsText('not-recorded');
  }
  if(source === 'background') {
    return emailsText('background');
  }
  return source.split('/').filter(function(part) {
    return part.length > 0 && part.toLowerCase() !== 'crp';
  }).map(function(part) {
    var words = part.replace(/([a-z])([A-Z])/g, '$1 $2').replace(/[-_.]/g, ' ').toLowerCase();
    return words.charAt(0).toUpperCase() + words.slice(1);
  }).join(' › ');
}

/** The filters of the page, sent both to the table and to the re-send. */
function emailsFilters() {
  return {
      globalUnit: $('#emailsFilterGlobalUnit').val(),
      source: $('#emailsFilterSource').val(),
      from: $('#emailsFilterFrom').val(),
      to: $('#emailsFilterTo').val(),
      search: $.trim($('#marloEmailsSearch').val())
  };
}

function updateResendButton() {
  var $button = $('button.sendEmails');
  emailsResendArmed = false;
  $button.toggle(emailsStatus === 'notSent');
  $button.prop('disabled', emailsNotSentFiltered === 0);
  $button.text(formatText(emailsText('resend-filtered'), [ emailsNotSentFiltered ]));
}

function setDatatables() {
  var $emailsTable = $('#marloEmailsTable');
  if(!$.fn.dataTable || $emailsTable.length === 0 || $.fn.dataTable.isDataTable($emailsTable)) {
    return;
  }

  // Source labels on the filter, from the same rule as the table.
  $('#emailsFilterSource option.emailsSourceOption').each(function() {
    $(this).attr('title', $(this).val()).text(sourceLabel($(this).val()));
  });

  var textColumn = function(data) {
    return escapeHtml(data);
  };

  emailsTable = $emailsTable.DataTable({
      "dom": 'rtip', // The filters above the table replace the length menu and the default search box
      "serverSide": true,
      "processing": true,
      "pageLength": 20,
      "order": [
        [ 0, 'desc' ]
      ], // Most recent first
      "autoWidth": false,
      "ajax": function(data, callback) {
        var order = (data.order && data.order[0]) || {
            column: 0,
            dir: 'desc'
        };
        $.ajax({
            url: baseURL + '/emailLogs.do',
            data: $.extend({
                status: emailsStatus,
                draw: data.draw,
                start: data.start,
                length: data.length,
                orderColumn: EMAILS_COLUMNS[order.column] || 'DATE',
                orderDir: order.dir
            }, emailsFilters()),
            success: function(response) {
              var counts = response.counts || {};
              $('.emailsCount-notSent').text(counts.notSent || 0);
              $('.emailsCount-sent').text(counts.sent || 0);
              if(emailsStatus === 'notSent') {
                emailsNotSentFiltered = response.recordsFiltered || 0;
              }
              updateResendButton();
              callback({
                  draw: response.draw,
                  recordsTotal: response.recordsTotal || 0,
                  recordsFiltered: response.recordsFiltered || 0,
                  data: response.data || []
              });
            },
            error: function() {
              $('.emailsResendStatus').first().text(emailsText('load-error'));
              callback({
                  draw: data.draw,
                  recordsTotal: 0,
                  recordsFiltered: 0,
                  data: []
              });
            }
        });
      },
      "columns": [
          {
              data: 'date',
              defaultContent: '',
              className: 'emailDate',
              render: textColumn
          }, {
              data: 'subject',
              defaultContent: '',
              render: function(data, type, row) {
                return '<a href="#" class="emailDetailLink" data-id="' + escapeHtml(row.id) + '">' + escapeHtml(data)
                    + '</a>';
              }
          }, {
              data: 'to',
              defaultContent: '',
              className: 'emailTo',
              render: textColumn
          }, {
              data: 'globalUnit',
              defaultContent: '',
              render: function(data) {
                return data ? escapeHtml(data) : '<span class="emailsMuted">' + escapeHtml(emailsText('not-recorded'))
                    + '</span>';
              }
          }, {
              data: 'source',
              defaultContent: '',
              render: function(data) {
                return '<span title="' + escapeHtml(data || '') + '">' + escapeHtml(sourceLabel(data)) + '</span>';
              }
          }, {
              data: 'tried',
              defaultContent: '',
              className: 'text-center',
              render: textColumn
          }, {
              data: 'error',
              defaultContent: '',
              orderable: false,
              className: 'emailError',
              render: textColumn
          }
      ],
      "language": {
          "emptyTable": emailsText('empty'),
          "zeroRecords": emailsText('empty'),
          "info": emailsText('info'),
          "infoEmpty": emailsText('info-empty'),
          "infoFiltered": emailsText('info-filtered'),
          "processing": emailsText('loading')
      }
  });
}

function attachEvents() {
  // Without DataTables there is no table for the tabs, filters and re-send to act on
  if(!emailsTable) {
    return;
  }
  // Tabs: the same table, for the other status
  $('.emailsTabs a[data-status]').on('click', function(e) {
    e.preventDefault();
    var $tab = $(this);
    if($tab.parent().hasClass('active')) {
      return;
    }
    $('.emailsTabs li').removeClass('active');
    $tab.parent().addClass('active');
    emailsStatus = $tab.data('status');
    emailsTable.column(EMAILS_ERROR_COLUMN).visible(emailsStatus === 'notSent');
    $('.emailsResendStatus').text('');
    updateResendButton();
    emailsTable.page(0).draw('page');
  });

  // Filters
  $('#emailsFilterGlobalUnit, #emailsFilterSource, #emailsFilterFrom, #emailsFilterTo').on('change', function() {
    emailsTable.draw();
  });
  var searchTimer;
  $('#marloEmailsSearch').on('keyup search', function() {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(function() {
      emailsTable.draw();
    }, 300);
  });
  $('.emailsClearFilters').on('click', function() {
    $('#emailsFilterGlobalUnit, #emailsFilterSource, #emailsFilterFrom, #emailsFilterTo, #marloEmailsSearch').val('');
    emailsTable.draw();
  });

  // Detail popup
  $('#marloEmailsTable').on('click', 'a.emailDetailLink', function(e) {
    e.preventDefault();
    openEmailDetail($(this).data('id'));
  });

  // Re-send of the emails not sent that match the filters: a second click confirms
  $('button.sendEmails').on('click', function() {
    var $button = $(this);
    if(!emailsResendArmed) {
      emailsResendArmed = true;
      $button.text(formatText(emailsText('resend-confirm'), [ emailsNotSentFiltered ]));
      return;
    }
    resendEmails($.extend({
      type: 1
    }, emailsFilters()), $button, $('.emailsResend .emailsResendStatus'));
  });

  $('.emailDetailResend').on('click', function() {
    resendEmails({
        type: 1,
        id: $(this).data('id')
    }, $(this), $('.emailDetailResendStatus'));
  });
}

function resendEmails(data, $button, $status) {
  $button.prop('disabled', true);
  $status.text(emailsText('resend-running'));
  $.ajax({
      url: baseURL + '/sendFailEmail.do',
      method: 'POST',
      data: data,
      success: function(response) {
        var results = response.results || [];
        var sent = results.filter(function(result) {
          return result.result === 'true';
        }).length;
        $status.text(formatText(emailsText('resend-result'), [ sent, results.length - sent ]));
      },
      error: function() {
        $status.text(emailsText('resend-error'));
      },
      complete: function() {
        $button.prop('disabled', false);
        emailsTable.draw(false);
      }
  });
}

function openEmailDetail(id) {
  var $popup = $('#emailDetailPopup');
  $popup.find('.modal-title').text('');
  $popup.find('.emailDetailBody').hide();
  $popup.find('.emailDetailResend').hide();
  $popup.find('.emailDetailResendStatus').text('');
  $popup.find('.emailDetailLoading').text(emailsText('detail-loading')).show();
  $popup.modal('show');

  $.ajax({
      url: baseURL + '/emailLogDetail.do',
      data: {
        id: id
      },
      success: function(email) {
        $popup.find('.modal-title').text(email.subject || '');
        var fields = {
            to: email.to,
            cc: email.cc,
            bcc: email.bcc,
            date: email.date,
            globalUnit: email.globalUnit || emailsText('not-recorded'),
            source: sourceLabel(email.source),
            status: email.sent ? emailsText('sent') : emailsText('not-sent'),
            tried: email.tried,
            error: email.error,
            fileName: email.fileName,
            messageId: email.messageId
        };
        $.each(fields, function(name, value) {
          $popup.find('[data-field="' + name + '"]').text(value === undefined || value === null ? '' : value);
        });
        $popup.find('[data-field="source"]').attr('title', email.source || '');
        // srcdoc with an empty sandbox: the stored HTML is shown, and no script in it can run
        $popup.find('.emailDetailMessage').attr('srcdoc', email.message || '');
        $popup.find('.emailDetailResend').data('id', email.id).toggle(!email.sent);
        $popup.find('.emailDetailLoading').hide();
        $popup.find('.emailDetailBody').show();
      },
      error: function() {
        $popup.find('.emailDetailLoading').text(emailsText('detail-error'));
      }
  });
}
