$(document).ready(init);

function init(){
  block();
}

var $supportDocsList = $('table.supportDocsList');

var table = $supportDocsList.DataTable({
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
              -1, 
          ]
      }, {
          sType: "natural",
          aTargets: [
            0
          ]
      }
  ]
});

//event add link supporting document
$(".addCapdevsupportDocument").click(function(){
	var $list = $(".documentList");
	var $item = $("#document-template").clone(true).removeAttr("id");
	$list.append($item);
	$item.show('slow');
	checkItems($list);
	updateDocument();
});

function block(){
  
  var type = $(".capdevDeliverableType").val();
  
  if(type ==+ -1){
    $(".capdevDeliverableSubtype").attr('disabled', 'disabled');
  }

}


//event remove link supporting document
$(".removeCapdevsupportDocument").click(function(){
	
	var $list = $('.documentList');
	
	var $item = $(this).parents('.documents');
	$item.hide(1000, function() {
	$item.remove();
	checkItems($list);
	updateDocument();
  });



})


function updateDocument() {
  $(".documentList").find('.documents').each(function(i,e) {
    // Set indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkItems(block) {
  var items = $(block).find('.documents').length;
  
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}


//action remove link supporting document
$(".removeCapdevsupportDocument-action").click(function(){
  console.log("removeCapdevsupportDocument");
	var documentID = $(".documentID").val();

	$.ajax({
      'url': baseURL + '/delete_document_link.do',
      'data': {
        q: documentID
      },
      beforeSend: function() {
      },
      success: function(data) {
      },
      error: function() {
      },
      complete: function() {
      }
    });
})




//event to remove supporting document
$('#confirm-delete').on('show.bs.modal', function(e) {
     $(this).find('.btn-ok').attr('href', $(e.relatedTarget).data('href'));
            
});


//event to filter the deliverables subtypes
$(".capdevDeliverableType").on("change", function(){
   var deliverableID = $(".capdevDeliverableType").val();
      if(deliverableID > 0){
         $.ajax({
        'url': baseURL + '/filterDeliverablesSubtypes.do',
        'data': {
          q: deliverableID
        },
        'dataType': "json",
        beforeSend: function() {
        },
        success: function(data) {
          $(".capdevDeliverableSubtype").removeAttr('disabled');
          var length = data.length;
          $('.capdevDeliverableSubtype').empty();
          $('.capdevDeliverableSubtype').append('<option value= -1>select option... </option>');
          for (var i = 0; i < length; i++) {
            $('.capdevDeliverableSubtype').append('<option value=' + data[i]['deliberableID'] + '>' + data[i]['deliberableName'] + '</option>');
          }
        },
        error: function() {
        },
        complete: function() {
         
        }
      })
    }
    else{
       $(".capdevDeliverableSubtype").attr('disabled', 'disabled');
       $('.capdevDeliverableSubtype').empty();
       $('.capdevDeliverableSubtype').append('<option value= -1>select option... </option>');

    }
})