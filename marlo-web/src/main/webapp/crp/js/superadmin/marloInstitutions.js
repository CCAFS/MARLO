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
    var $request = $('#partnerRequestItem-' + requesIDSelected);
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
          if(data.success) {
            $request.slideUp();
            $modal.modal('hide');
          }
        },
        complete: function() {
          $modal.find('.loading').fadeOut();
        }
    });
  });

  // Edit Request
  $('a.editRequest').on('click', function(e) {
    e.preventDefault();
    var $request = $(this).parents('.partnerRequestItem');
    $request.find('.editForm').slideDown();
    $request.find('.btn-group').slideUp();
  });

  // Save request edition
  $('button.saveButton').on('click', function(e) {
    e.preventDefault();
    var $request = $(this).parents('.partnerRequestItem');

    // Validate fields
    var name = $('input[name="name"]').val();
    var typeValue = $('select#type').val();
    var countryValue = $('select#country').val();
    
    if(!name || (typeValue == "-1") || (countryValue == "-1")){
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'The required(*) fields needs to be filled';
      noty(notyOptions);
      return
    }

    $.ajax({
        url: baseURL + '/editPartnerRequest.do',
        data: $request.find('.editForm').serializeObject(),
        beforeSend: function() {
          $request.find('.loading').fadeIn();
        },
        success: function(data) {
          if(data.success) {
            location.reload();
            $request.find('.editForm').slideUp();
            $request.find('.btn-group').slideDown();
          }
        },
        complete: function() {
          $request.find('.loading').fadeOut();

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
              // Text Difference
              $li.prettyTextDiff({
                  cleanup: false,
                  originalContent: $(e).find('h4').text(),
                  changedContent: partner.composedName,
                  diffContainer: $li
              });
            });
          }
        },
        complete: function() {
          $(e).find('.loading').fadeOut();
        }
    });
  });
}
