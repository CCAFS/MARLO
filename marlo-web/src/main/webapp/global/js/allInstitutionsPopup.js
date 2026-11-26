var $allInstitutionsTable;
// Escapes a value for HTML text (DataTables writes every cell as HTML)
function escapeCell(value) {
  return $('<div></div>').text(value == null ? '' : value).html();
}

// A website is linked only when it is an http:// or https:// address; any other scheme (javascript:, data:) is not linked
function isWebLink(value) {
  var url = $.trim(String(value == null ? '' : value)).toLowerCase();
  return url.indexOf('http://') === 0 || url.indexOf('https://') === 0;
}

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
            data: "id",
            render: function(data,type,full,meta) {
              return type === 'display' ? escapeCell(data) : data;
            }
          }, {
              data: "acronym",
              render: function(data,type,full,meta) {
                if (!data) {
                  return '<i><i>';
                }
                return type === 'display' ? escapeCell(data) : data;
              }
          }, {
              data: "name",
              render: function(data,type,full,meta) {
                var $cell = $('<div></div>');
                $cell.append(document.createTextNode(data == null ? '' : data)).append(' <br/>  ');
                $.each((full.countries || '').split(','), function(i,flag) {
                  $cell.append($('<i></i>').attr('title', flag).addClass('flag-icon flag-icon-' + $.trim(flag.toLowerCase())));
                });
                if (full.webPage != undefined && isWebLink(full.webPage)) {
                  $cell.append($('<a></a>').css('margin', '20px')
                      .attr({
                        'href': $.trim(full.webPage),
                        'target': '_blank',
                        'rel': 'noopener noreferrer',
                        'data-toggle': 'tooltip',
                        'data-placement': 'top',
                        'title': full.webPage
                      }).text('Web page'));
                }
                return $cell.html();
              }
          }, {
            data: "type",
            render: function(data,type,full,meta) {
              return type === 'display' ? escapeCell(data) : data;
            }
          }
      ]
  });

});
