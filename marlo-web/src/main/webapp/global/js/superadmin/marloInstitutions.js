var requestID, institutionOfficeRequestId;
$(document).ready(function() {
  var $modal = $('#myModal');
  var $rejectOfficeRequest = $('#rejectOfficeRequest');

  // Reject Request popup Event
  $('a.rejectRequest').on('click', function(e) {
    e.preventDefault();
    requestID = $(this).classParam('partnerRequestId');

    var $request = $(this).parents('.partnerRequestItem');
    $modal.find('.requestInfo').html($request.find('.requestInfo').clone(true).addClass('grayBox'));
    $modal.modal('show');
  });

  // Reject Request button Event
  $modal.find('.rejectButton').on('click', function() {
    var $request = $('#partnerRequestItem-' + requestID);
    
    $.ajax({
        url: baseURL + '/rejectPartnerRequest.do',
        data: {
            requestID: requestID,
            justification: $modal.find('textarea').val(),
            sendEmail : ($modal.find('sendEmailInput').is(':checked'))? true : false
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
    var modificationJustification = $('textarea[name="modificationJustification"]').val();

    if(!name || !modificationJustification || (typeValue == "-1") || (countryValue == "-1")) {
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

// Open reject request popup Event
  $('a.openRejectOfficeRequest').on('click', function(e) {
    e.preventDefault();
    institutionOfficeRequestId = $(this).classParam('institutionOfficeRequestId');
    console.log(institutionOfficeRequestId);
    var $request = $(this).parents('.officesRequestItem');
    var $countriesSelected = $request.find('.officeRequest:checked').map(function() {
      return $(this).parent().clone(true).find('label').text();
    }).get().join(', ');
    $rejectOfficeRequest.find('.requestInfo').html($countriesSelected);
  });

  // Reject office request
  $('a.rejectOfficesRequest').on('click', function(e) {
    e.preventDefault();
    var $request = $('#officesRequestItem-' + institutionOfficeRequestId)
    var countriesSelected = $request.find('.officeRequest:checked');
    var formData = {
        'countryOfficePOJO.institution.id': $request.find('input.institutionID').val(),
        'justification': $rejectOfficeRequest.find('textarea.modificationJustification').val(),
        'countryOfficePOJO.ids': countriesSelected.map(function() {
          return this.value;
        }).get().join()
    }
    
    // Validate if there are countries selected
    if(!formData.justification) {
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'Please fill out the justification field';
      noty(notyOptions);
      return
    }
    $.ajax({
        url: baseURL + '/rejectOfficesRequest.do',
        data: formData,
        beforeSend: function() {
          $request.find('.loading').fadeIn();
        },
        success: function(data) {
          if(data.success) {
            location.reload();
          }
        },
        complete: function() {
          $request.find('.loading').fadeOut();
          $rejectOfficeRequest.find('textarea.modificationJustification').val('')
        }
    });
  });

  // Accept office request
  $('a.acceptOfficesRequest').on('click', function(e) {
    e.preventDefault();
    institutionOfficeRequestId = $(this).classParam('institutionOfficeRequestId');
    var $request = $('#officesRequestItem-' + institutionOfficeRequestId)
    var countriesSelected = $request.find('.officeRequest:checked');
    var formData = {
        'countryOfficePOJO.institution.id': $request.find('input.institutionID').val(),
        'countryOfficePOJO.ids': countriesSelected.map(function() {
          return this.value;
        }).get().join()
    }
    // Validate if there are countries selected
    if(countriesSelected.length == 0) {
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'Please select at least a country';
      noty(notyOptions);
      return

    }
    $.ajax({
        url: baseURL + '/acceptOfficesRequest.do',
        data: formData,
        beforeSend: function() {
          $request.find('.loading').fadeIn();
        },
        success: function(data) {
          if(data.success) {
            location.reload();
          }
        },
        complete: function() {
          $request.find('.loading').fadeOut();
        }
    });
  });

  // Find Sameness for each partners
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
              /*
               * $li.prettyTextDiff({ cleanup: false, originalContent: $(e).find('h4').text(), changedContent:
               * partner.composedName, diffContainer: $li });
               */
            });
          }
        },
        complete: function() {
          $(e).find('.loading').fadeOut();
        }
    });
  });
}
