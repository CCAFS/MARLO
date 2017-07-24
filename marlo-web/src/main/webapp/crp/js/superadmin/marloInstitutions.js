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

  $modal.find('.rejectButton').on('click', function() {

    $.ajax({
        url: baseURL + '/rejectPartnerRequest.do',
        data: {
            requestID: '',
            justification: '',
        },
        beforeSend: function() {
          $modal.find('.loading').fadeIn();
        },
        success: function(data) {
        },
        complete: function(data) {
          $modal.find('.loading').fadeOut();
        },
        error: function(error) {
          console.log(error)
        }
    });

  });

});
