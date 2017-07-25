$(document).ready(function() {
  var $modal = $('#myModal');

  // Reject Request popup Event
  $('a.rejectRequest').on('click', function(e) {
    e.preventDefault();
    var requestID = $(this).classParam('partnerRequestId');
    var $request = $(this).parents('.partnerRequestItem');
    $modal.find('.requestInfo').html($request.find('.requestInfo').clone(true).addClass('grayBox'));
    $modal.modal('show');
  });

  // Reject Request button Event
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

  findSameness();
});

function findSameness() {
  $('.partnerRequestItem').each(function(i,e) {
    var institutionName = $(e).find('.hiddenTitle').text();
    console.log(baseURL + '/searchInstitutionsName.do');
    $.ajax({
        url: baseURL + '/searchInstitutionsName.do',
        data: {
          institutionName: institutionName
        },
        beforeSend: function() {
          $(e).find('.loading').fadeIn();
        },
        success: function(data) {

          if((data.institutions).length > 0) {
            $(e).find('.sameness').show();

            $.each(data.institutions, function(i,partner) {
              $(e).find('.sameness ul').append('<li>' + partner.composedName + '</li>')
            });

          }

        },
        complete: function(data) {
          $(e).find('.loading').fadeOut();
        },
        error: function(error) {
          console.log(error)
        }
    });
  });
}
