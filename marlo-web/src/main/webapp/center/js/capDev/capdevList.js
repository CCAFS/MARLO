$(document).ready(function() {
console.log("hola");

  var $buttons = $('.botones');
     console.log($buttons[0])     
  if($buttons.exists()) {
    var menuOffset = function() {
      return $(document).height() - ($buttons.offset().top + $buttons.height());
    }

    console.log('pantalla');
    console.log($(document).width());
    console.log('derecha');
    console.log($buttons.offset().right);
    console.log('izq');
    console.log($buttons.offset().left);
    console.log('botones');
    console.log($buttons.width());

    $buttons.find('.addgroupItem').css({
      right: $(document).width() - ($buttons.offset().left + $buttons.width() + 6) /*360.5px*/
    });
    $buttons.find('.addindividualItem').css({
      right: $(document).width() - ($buttons.offset().left + $buttons.width() - 190)/*555.5px*/
    });

    setTimeout(function() {
      setFixedElement($(window).scrollBottom() >= menuOffset());
    }, 500);

    $(document).on('updateComponent', function() {
      setFixedElement($(window).scrollBottom() >= menuOffset());
    });

    $(window).scroll(function() {
      setFixedElement($(window).scrollBottom() >= menuOffset());
    });
  }

  function setFixedElement(isFixed) {
    if(isFixed) {
      $buttons.find('.buttons-content').addClass('positionFixedBot animated flipInX');
    } else {
      $buttons.find('.buttons-content').removeClass('positionFixedBot animated flipInX');
    }
  }

 

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
  ],
  "order": [[ 0, "desc" ]]
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

