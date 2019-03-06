$(document).ready(function() {

  // Set data tables
  var $table = $('.viewMoreSyntesis-block table');

  if($.fn.DataTable.isDataTable('.viewMoreSyntesis-block table')) {

    console.log("$table.DataTable().destroy();");

    $table.DataTable().destroy();
  }

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    e.target // newly activated tab
    e.relatedTarget // previous active tab

    console.log("init datatable");
    if(!$.fn.DataTable.isDataTable('.viewMoreSyntesis-block table')) {
      $table.DataTable({
          "paging": false,
          "searching": false,
          "info": false,
          "scrollY": "320px",
          "scrollCollapse": true,
      });
    }
  })

});
