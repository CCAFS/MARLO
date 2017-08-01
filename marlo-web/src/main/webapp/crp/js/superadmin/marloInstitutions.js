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

  // Edit Request
  $('a.editRequest').on('click', function(e) {
    e.preventDefault();
    var $request = $(this).parents('.partnerRequestItem');
    console.log($request);
    $request.find('.editForm').slideDown();
    $request.find('.btn-group').slideUp();
  });

  // Save request edition
  $('button.saveButton').on('click', function(e) {
    e.preventDefault();
    var $request = $(this).parents('.partnerRequestItem');

    $.ajax({
        url: baseURL + '/editPartnerRequest.do',
        data: $request.find('.editForm').serializeObject(),
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

    $request.find('.editForm').slideUp();
    $request.find('.btn-group').slideDown();
  });

  // Cancel request edition
  $('button.cancelButton').on('click', function(e) {
    e.preventDefault();
    var $request = $(this).parents('.partnerRequestItem');
    $request.find('.editForm').slideUp();
    $request.find('.btn-group').slideDown();
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
