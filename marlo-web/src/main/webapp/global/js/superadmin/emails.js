$(document).ready(init);

function init() {

  // setDatatables();

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
 */
function setDatatables() {

  $('#marloEmailsTable').DataTable({
      iDisplayLength: 20, // Number of rows to show on the table
      ajax: {
          "url": baseURL + '/sendFailEmail.do?type=0',
          "dataSrc": "emails"
      },
      columns: [
          {
            data: "id"
          }, {
            data: "subject"
          }, {
            data: "error"
          }, {
            data: "tried"
          }
      ]
  });
}