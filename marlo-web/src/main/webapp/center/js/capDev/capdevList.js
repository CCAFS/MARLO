$(document).ready(function() {
console.log("hola");

 

});


var $capdevList = $('table.capDevList');

var table = $capdevList.DataTable({
  "bPaginate": true, // This option enable the table pagination
  "bLengthChange": true, // This option disables the select table size option
  "bFilter": true, // This option enable the search
  "bSort": true, // this option enable the sort of contents by columns
  "bAutoWidth": false, // This option enables the auto adjust columns width
  "iDisplayLength": 15, // Number of rows to show on the table,
  aoColumnDefs: [
      {
          bSortable: false,
          aTargets: [
              -1, -2
          ]
      }, {
          sType: "natural",
          aTargets: [
            0
          ]
      }
  ]
});


$("#capdevSearchInput").keyup(function () {
    //split the current value of searchInput
    var data = this.value.split(" ");
    console.log(data);
    //create a jquery object of the rows
    var jo = $("#capdevTbody").find("tr");
    if (this.value == "") {
        jo.show();
        return;
    }
    //hide all the rows
    jo.hide();

    //Recusively filter the jquery object to get results.
    jo.filter(function (i, v) {
        var $t = $(this);
        for (var d = 0; d < data.length; ++d) {
            if ($t.is(":contains('" + data[d] + "')")) {
                return true;
            }
        }
        return false;
    })
    //show the rows that match.
    .show();
}).focus(function () {
    this.value = "";
    $(this).css({
        "color": "black"
    });
    $(this).unbind('focus');
}).css({
    "color": "#C0C0C0"
});



//event to remove capdev
$('#confirm-delete-capdev').on('show.bs.modal', function(e) {
    $(this).find('.btn-ok').attr('href', $(e.relatedTarget).data('href'));
            
});

