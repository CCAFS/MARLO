var timeoutAutoSave;
var $draftTag, $editedBy, $cancelButton;

$(document).ready(function() {

  $draftTag = $('[name="save"]').find('.draft');
  $editedBy = $('#lastUpdateMessage');
  $cancelButton = $('.button-cancel');

  /* Event triggers */
  $(document).on('updateComponent', changeDetected);
  $(':input').on('keyup change', changeDetected);

});

function autoSave() {
  $.ajax({
      dataType: 'json',
      method: 'POST',
      url: baseURL + '/autosaveWriter.do',
      data: {
        autoSave: JSON.stringify($('form').serializeObject())
      },
      beforeSend: function() {
        $draftTag.text('... Saving');
      },
      success: function(data) {
        if(data.status.status) {
          successNotification('Successfully saved a draft version');
          $draftTag.text('(Draft Version)').addClass('animated flipInX');
          $editedBy.find('.datetime').text(data.status.activeSince);
          $editedBy.find('.modifiedBy').text(data.status.modifiedBy);
          $cancelButton.css('display', 'inline-block');

          // Validate section
          validateThisSection();

          // Send push for saving
          pushSave();

        } else {
          errorNotification('Auto save error' + data.status.statusMessage);
        }
      },
      complete: function() {
      },
      error: function(e) {
        errorNotification('Auto save ' + e.statusText);
      }
  });
}

function successNotification(msj) {
  var notyOptions = jQuery.extend({}, notyDefaultOptions);
  notyOptions.text = msj;
  notyOptions.type = 'success';
  notyOptions.layout = 'topCenter';
  notyOptions.animation = {
      open: 'animated fadeInDown',
      close: 'animated fadeOutUp'
  };
  noty(notyOptions);
}

function errorNotification(msj) {
  var notyOptions = jQuery.extend({}, notyDefaultOptions);
  notyOptions.text = msj;
  notyOptions.type = 'error';
  notyOptions.layout = 'topCenter';
  notyOptions.animation = {
      open: 'animated fadeInDown',
      close: 'animated fadeOutUp'
  };
  noty(notyOptions);
}

function changeDetected(e) {
  if(isChanged()) {
    if(autoSaveActive) {
      if(timeoutAutoSave) {
        clearTimeout(timeoutAutoSave);
      }
      // Start a timer that will search when finished
      timeoutAutoSave = setTimeout(function() {
        autoSave();
      }, 15 * 1000);
    }
  }
}

function validateThisSection() {
  var $sectionMenu = $('#secondaryMenu .currentSection');
  var sectionName = ($sectionMenu.attr('id')).split("-")[1];
  console.log(sectionName);

  var validateService = "";
  var sectionData = {};
  sectionData.sectionName = sectionName;

  // Validate projects
  if(isProjectSection()) {
    sectionData.projectID = $('input[name="projectID"]').val();
    validateService = "/validateProjectSection.do";
  }

  // Validate impact pathway
  if(isImpactPathwaySection()) {
    sectionData.crpProgramID = $('input[name="crpProgramID"]').val();
    validateService = "/impactPathway/validateImpactPathway.do";
  }

  $.ajax({
      url: baseURL + validateService,
      data: sectionData,
      beforeSend: function() {
        $sectionMenu.removeClass('animated flipInX').addClass('loadingSection');
      },
      success: function(data) {
        // Process Ajax results here
        if(jQuery.isEmptyObject(data)) {
          $sectionMenu.removeClass('submitted');
        } else {
          if(data.section.missingFields == "") {
            $sectionMenu.addClass('submitted').removeClass('toSubmit');
          } else {
            $sectionMenu.removeClass('submitted').addClass('toSubmit');

          }
        }
        $sectionMenu.removeClass('loadingSection');
      },
      complete: function(data) {
        $sectionMenu.addClass('animated flipInX');
      },
      error: function(error) {
        console.log(error)
      }
  });
}
