var $allInstitutionsTable;
$(document).ready(function() {

  $allInstitutionsTable = $('#allInstitutionsTable');

  // https://172.22.45.84:8443/marlo-web/searchInstitutions.do?q=&withPPA=1&onlyPPA=0
  $allInstitutionsTable.DataTable({
      iDisplayLength: 15, // Number of rows to show on the table
      ajax: {
          "url": baseURL + '/searchInstitutions.do?q=&withPPA=1&onlyPPA=0',
          "dataSrc": "institutions"
      },
      columns: [
          {
            data: "id"
          }, {
              data: "acronym",
              render: function(data,type,full,meta) {
                return data || '<i><i>';
              }
          }, {
              data: "name",
              render: function(data,type,full,meta) {
                var flags = '';
                $.each(full.countries.split(','), function(i,flag) {
                  flags += '<i title="' + flag + '" class="flag-sm flag-sm-' + $.trim(flag) + '"></i>';
                });

                return data + " <br/>  " + flags;
              }
          }, {
            data: "type"
          }
      ]
  });

});

