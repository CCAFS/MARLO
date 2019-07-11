var crpList = [];
$(document).ready(function() {

  // Add Select2 plugin
  $('form select').select2({
    width: "100%"
  });

  // Add users datatable
  addUsersDatatable();

});

/**
 * Users Datatable
 */
function addUsersDatatable() {
  var $marloUsersTable = $('#marloUsersTable');

  $marloUsersTable.DataTable({
      iDisplayLength: 20, // Number of rows to show on the table
      ajax: {
          "url": baseURL + '/searchUsers.do?q=',
          "dataSrc": "users"
      },
      columns: [
          {
            data: "id"
          },
          {
            data: "name"
          },
          {
              data: "username",
              render: function(data,type,full,meta) {
                return data || '<i>No Username<i>';
              }
          },
          {
              data: "email",
              render: function(data,type,full,meta) {
                return '<span class="email">' + data + '</span>';
              }
          },
          {
              data: "isActive",
              render: function(data,type,full,meta) {
                return '<div class="text-center"><img src="' + baseURL + '/global/images/checked-' + data
                    + '.png" /></div>';
              }
          },
          {
              data: "autoSaveActive",
              render: function(data,type,full,meta) {
                return '<div class="text-center"><img src="' + baseURL + '/global/images/checked-' + data
                    + '.png" /></div>';
              }
          }, {
              data: "lastLogin",
              render: function(data,type,full,meta) {
                return data || '<i>No Date<i>';
              }
          }
      ],
      aoColumnDefs: [
        {
            sType: "natural",
            aTargets: [
              0
            ],
            asSorting: [
              "desc"
            ]
        }
      ]

  });
  $marloUsersTable.on('draw.dt', function() {
    // Do something here if needed
  });
}
