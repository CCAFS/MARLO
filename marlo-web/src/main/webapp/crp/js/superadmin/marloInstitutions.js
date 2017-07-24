$(document).ready(function() {
  var $modal = $('#myModal');

  // Reject Request Event
  $('a.rejectRequest').on('click', function(e) {
    e.preventDefault();
    var requestID = $(this).classParam('partnerRequestId');
    var $request = $(this).parents('.partnerRequestItem');

    $modal.find('.requestInfo').html($request.find('.requestInfo').clone(true).addClass('grayBox'));

    $modal.modal('show');
  });

});
