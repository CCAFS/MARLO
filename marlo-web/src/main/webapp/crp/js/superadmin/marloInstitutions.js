var requesIDSelected;
$(document).ready(function() {
  var $modal = $('#myModal');

  // Reject Request popup Event
  $('a.rejectRequest').on('click', function(e) {
    e.preventDefault();
    var requestID = $(this).classParam('partnerRequestId');
    requesIDSelected = requestID;
    var $request = $(this).parents('.partnerRequestItem');
    $modal.find('.requestInfo').html($request.find('.requestInfo').clone(true).addClass('grayBox'));
    $modal.modal('show');
  });

  // Reject Request button Event
  $modal.find('.rejectButton').on('click', function() {
    $.ajax({
        url: baseURL + '/rejectPartnerRequest.do',
        data: {
            requestID: requesIDSelected,
            justification: $modal.find('textarea').val(),
        },
        beforeSend: function() {
          $modal.find('.loading').fadeIn();
        },
        success: function(data) {
          console.log(data);
          if(data.success) {
            $modal.modal('hide');
          }
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
          $request.find('.loading').fadeIn();
        },
        success: function(data) {
          console.log(data);
          if(data.success) {
            $request.find('.editForm').slideUp();
            $request.find('.btn-group').slideDown();
          }
        },
        complete: function(data) {
          $request.find('.loading').fadeOut();

        },
        error: function(error) {
          console.log(error)
        }
    });

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
              var $li = $('<li title="' + partner.composedName + '">' + partner.composedName + '</li>');
              $(e).find('.sameness ul').append($li);

              /*
               * $li.prettyTextDiff({ cleanup: true, originalContent: $(e).find('h4').text(), changedContent:
               * partner.composedName, diffContainer: $li });
               */
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
