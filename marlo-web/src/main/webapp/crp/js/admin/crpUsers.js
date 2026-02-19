$(document).ready(init);

function init() {
  // Initialize DataTables for all tables
  initializeDataTables();

  // Add guest user module
  guestUsersModule.init();

  // Attaching events
  attachEvents();
}

function initializeDataTables() {
  // DataTables configuration
  var dataTablesConfig = {
      dom: 'Bfrtip',
      buttons: [
          {
              extend: 'copy',
              text: '<span class="glyphicon glyphicon-copy"></span> Copy',
              title: 'MARLO Users Export',
              exportOptions: {
                  columns: ':visible'
              }
          },
          {
              extend: 'excel',
              text: '<span class="glyphicon glyphicon-download-alt"></span> Excel',
              title: 'MARLO_Users_Export_' + getDateString(),
              filename: 'MARLO_Users_' + getDateString(),
              exportOptions: {
                  columns: ':visible',
                  format: {
                      body: function(data, row, column, node) {
                          // Remove HTML tags but keep text content
                          return data.replace(/<[^>]*>/g, '').trim();
                      }
                  }
              },
              customize: function(xlsx) {
                  var sheet = xlsx.xl.worksheets['sheet1.xml'];
                  
                  // Style header row - blue background, white text, bold
                  $('row:first c', sheet).attr('s', '42');
                  
                  // Add custom styles to workbook
                  var styles = xlsx.xl['styles.xml'];
                  var lastXfIndex = $('cellXfs xf', styles).length - 1;
                  
                  // Header style
                  var headerStyle = '<xf numFmtId="0" fontId="2" fillId="5" borderId="1" applyFont="1" applyFill="1" applyBorder="1">' +
                                   '<alignment horizontal="center" vertical="center"/>' +
                                   '</xf>';
                  
                  $('cellXfs', styles).append(headerStyle);
                  
                  // Make ID column narrower, email wider
                  $('col', sheet).each(function(index) {
                      if (index === 0) $(this).attr('width', 8);  // ID column
                      if (index === 1) $(this).attr('width', 25); // Name column
                      if (index === 2) $(this).attr('width', 20); // Roles column
                      if (index === 3) $(this).attr('width', 30); // Email column
                      if (index === 4) $(this).attr('width', 20); // Last Login column
                  });
              }
          },
          {
              extend: 'csv',
              text: '<span class="glyphicon glyphicon-export"></span> CSV',
              title: 'MARLO_Users_Export_' + getDateString(),
              filename: 'MARLO_Users_' + getDateString(),
              exportOptions: {
                  columns: ':visible'
              }
          },
          {
              extend: 'print',
              text: '<span class="glyphicon glyphicon-print"></span> Print',
              title: 'MARLO Users',
              exportOptions: {
                  columns: ':visible'
              },
              customize: function(win) {
                  $(win.document.body).prepend('<h2>MARLO Users - ' + getDateString('display') + '</h2>');
                  $(win.document.body).find('table').addClass('display').css('font-size', '12px');
                  $(win.document.body).find('tr:nth-child(odd)').css('background-color', '#f9f9f9');
                  $(win.document.body).find('th').css({
                      'background-color': '#0478a3',
                      'color': '#fff',
                      'padding': '10px'
                  });
              }
          }
      ],
      pageLength: 25,
      lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, 'All']],
      order: [[1, 'asc']], // Sort by name by default
      columnDefs: [
          { width: '5%', targets: 0 },   // ID column
          { width: '18%', targets: 1 },  // Name column
          { width: '40%', targets: 2 },  // Roles column (increased more)
          { width: '15%', targets: 3 },  // Email column
          { width: '22%', targets: 4 }   // Last Login column (reduced)
      ],
      autoWidth: false,
      retrieve: true, // Allow reinitializing already initialized tables
      deferRender: true, // Improve performance for large datasets
      language: {
          search: 'Search users:',
          lengthMenu: 'Show _MENU_ users per page',
          info: 'Showing _START_ to _END_ of _TOTAL_ users',
          infoFiltered: '(filtered from _MAX_ total users)'
      }
  };

  // Initialize DataTables for visible tables first
  $('.tab-pane.active .usersTable').each(function() {
      if (!$.fn.DataTable.isDataTable(this)) {
          $(this).DataTable(dataTablesConfig);
      }
  });

  // Initialize DataTables when a tab is shown
  $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var targetPane = $(e.target).attr('href');
      $(targetPane + ' .usersTable').each(function() {
          if (!$.fn.DataTable.isDataTable(this)) {
              $(this).DataTable(dataTablesConfig);
          } else {
              // Adjust column sizing if already initialized
              $(this).DataTable().columns.adjust().draw();
          }
      });
  });
}

function attachEvents() {

}

var guestUsersModule =
    (function() {
      var $userEmail = $('input.userEmail');
      var $firstName = $('input.userFirstName');
      var $lastName = $('input.userLastName');
      var $selectedGlobalUnitID = $('input.selectedGlobalUnitID');
      var $message = $('#guestUserMessage');
      var $userNameBlock = $('.firstLastName');
      var $submitButton = $('button[name="save"]');
      var timer = null;
      var userHasAccess = false;

      function init() {
        events();

        validateForm();
      }

      function events() {
        $userEmail.on("keyup", function() {
          $userEmail.addClass('input-loading');
          disabledSubmitButton(true);
          if(timer) {
            clearTimeout(timer); // cancel the previous timer.
            timer = null;
          }
          timer = setTimeout(findUserEmail, 1500);
        });

        $firstName.on("change keyup", validateForm);
        $lastName.on("change keyup", validateForm);
      }

      function getUserEmail() {
        return $.trim($userEmail.val());
      }

      function getCRPAcronym() {
        return $.trim($selectedGlobalUnitID.val());
      }

      function findUserEmail() {
        var email = getUserEmail();
        if(validateEmail(email)) {
          $.ajax({
              url: baseUrl + "/crpByEmail.do",
              data: {
                userEmail: email
              },
              beforeSend: function() {
                $message.hide();
                userHasAccess = false;
              },
              success: function(data) {
                console.log(data);
                if(data.user == null) {
                } else {
                  $.each(data.crps, function(i,crp) {
                    if(crp.acronym == getCRPAcronym()) {
                      $message.text(data.user.name + " has already access to " + getCRPAcronym() + "").fadeIn();
                      userHasAccess = true;
                    }
                  });
                }
              },
              complete: function(data) {
                validateForm();
              },
              error: function(data) {
              }
          });
        } else {
          validateForm();
        }

      }

      function validateForm() {
        var email = getUserEmail();
        var firstName = $.trim($firstName.val());
        var lastName = $.trim($lastName.val());
        var isValid = false;

        $userEmail.removeClass('input-loading');

        if(!userHasAccess) {
          if(validateCGIAR()) {
            isValid = true;
          } else {
            if(validateEmail(email) && firstName && lastName) {
              isValid = true;
            }
          }
        }

        console.log("Validate Form", isValid);

        disabledSubmitButton(!isValid);
      }

      function disabledSubmitButton(state) {
        $submitButton.prop("disabled", state);
        if(state) {
          $submitButton.addClass("disabled");
        } else {
          $submitButton.removeClass("disabled");
        }
      }

      function validateCGIAR() {
        var email = getUserEmail();
        if(validateEmail(email) && email.indexOf("@cgiar.org") !== -1) {
          $userNameBlock.slideUp();
          return true;
        } else {
          $userNameBlock.slideDown();
          return false;
        }
      }

      function validateEmail(email) {
        var re =
            /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
      }

      return {
        init: init
      }
    })();