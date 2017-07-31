//event add link supporting document

$(".addCapdevsupportDocument").click(function(){
	var $list = $(".documentList");
	var $item = $("#document-template").clone(true).removeAttr("id");
	$list.append($item);
	$item.show('slow');
	checkItems($list);
	updateDocument();
});


//event remove link supporting document
$(".removeCapdevsupportDocument").click(function(){
	console.log("removeCapdevsupportDocument");
	var $list = $('.documentList');
	console.log('lista')
	console.log($list[0])
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
  console.log(items)
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}


//action remove link supporting document
$(".removeCapdevsupportDocument-action").click(function(){
	var documentID = $(".documentID").val();

	$.ajax({
      'url': baseURL + '/delete_document_link.do',
      'data': {
        q: documentID
      },
      beforeSend: function() {
        console.log("antes de enviar el ajax")
      },
      success: function(data) {
      },
      error: function() {
        console.log("algun error")
      },
      complete: function() {
        console.log("terminado todo")
      }
    });
})




//event to remove supporting document
$('#confirm-delete').on('show.bs.modal', function(e) {
     $(this).find('.btn-ok').attr('href', $(e.relatedTarget).data('href'));
            
});