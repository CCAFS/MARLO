var tasksLength;
var sections;
var currentCycle;
var selectedUrl, selectedAction;

$(document).ready(function() {

  sections = $('#sectionsForChecking').text().split(',');

  // Progress bar
  tasksLength = sections.length;
  $(".progressbar").progressbar({
    max: tasksLength
  });

  // Event for validate button inside each synthesis
  $('.projectValidateButton, .validateButton').on('click', validateButtonEvent);

  // Click on submit button
  $('.submitButton, .projectSubmitButton').on('click', submitButtonEvent);

  // Click on submit button
  $('.projectUnSubmitButton').on('click', unSubmitButtonEvent);

  // Pop up when exists a draft version $('header a, #mainMenu a, .subMainMenu a, #secondaryMenu a')
  $('#secondaryMenu a').on('click', function(e) {
    selectedUrl = $.trim($(this).attr("href"));
    selectedAction = getClassParameter($(this), 'action');

    // Prevent middle click
    if(e.which == 2) {
      return;
    }

    if((isChanged() || forceChange || draft) && editable && draft && selectedUrl && (myTurn == 1)) {
      e.preventDefault();
      $('#discardChanges').modal();
    }
  });

});

function acceptChanges() {
  $('#redirectionUrl').val(selectedAction);
  $('button[name="save"]').trigger('click');
  $('#discardChanges').modal('hide');
}

function cancel() {
  window.location.replace(selectedUrl);
  window.location.href = selectedUrl;
  $('#discardChanges').modal('hide');
}

function submitButtonEvent(e) {
  e.preventDefault();
  var message = 'Are you sure you want to submit the synthesis now?';
  message += 'Once submitted, you will no longer have editing rights.';
  noty({
      text: message,
      type: 'confirm',
      dismissQueue: true,
      layout: 'center',
      theme: 'relax',
      modal: true,
      buttons: [
          {
              addClass: 'btn btn-primary',
              text: 'Ok',
              onClick: function($noty) {
                $noty.close();
                window.location.href = $(e.target).attr('href');
              }
          }, {
              addClass: 'btn btn-danger',
              text: 'Cancel',
              onClick: function($noty) {
                $noty.close();
              }
          }
      ]
  });
}

function validateButtonEvent(e) {
  e.stopImmediatePropagation();
  e.preventDefault();
  var pID = $(e.target).attr('id').split('-')[1];
  // Execute Ajax process for each section
  processTasks(sections, pID, $(this));
}

function processTasks(tasks,id,button) {
  $(button).unbind('click');
  var completed = 0;
  var index = 0;
  $(button).fadeOut(function() {
    $(button).next().fadeIn();
  });
  function nextTask() {
    if(index < tasksLength) {
      var sectionName = tasks[index];
      var $sectionMenu = $('#menu-' + sectionName + '');
      $
          .ajax({
              url: baseURL + '/validatePowbSynthesisSection.do',
              data: {
                  powbSynthesisID: $('#powbSynthesisID').text(),
                  sectionName: sectionName,
                  phaseID: phaseID
              },
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
                    completed++;
                  } else {
                    $sectionMenu.removeClass('submitted').addClass('toSubmit');
                    // Show missingFields
                    console.log(sectionName + ": " + data.section.missingFields);
                  }
                }
                $sectionMenu.removeClass('loadingSection');
              },
              complete: function(data) {
                $sectionMenu.addClass('animated flipInX');
                // Do next Ajax call
                $(button).next().progressbar("value", index + 1);
                index++;
                if(index == tasksLength) {
                  if(completed == tasksLength) {
                    var notyOptions = jQuery.extend({}, notyDefaultOptions);
                    notyOptions.text = 'The synthesis can be submmited now';
                    notyOptions.type = 'success';
                    notyOptions.layout = 'center';
                    noty(notyOptions);
                    $(button).next().fadeOut(function() {
                      $(this).next().fadeIn("slow");
                    });
                  } else {
                    var notyOptions = jQuery.extend({}, notyDefaultOptions);
                    notyOptions.text =
                        "The synthesis is still incomplete, please go to the sections without the green check mark and complete the missing fields before submitting your synthesis.";
                    notyOptions.type = 'confirm';
                    notyOptions.layout = 'center';
                    notyOptions.modal = true;
                    notyOptions.buttons = [
                      {
                          addClass: 'btn btn-primary',
                          text: 'Ok',
                          onClick: function($noty) {
                            $noty.close();
                          }
                      }
                    ];
                    noty(notyOptions);
                    $(button).next().fadeOut(function() {
                      $(button).fadeIn("slow").on('click', validateButtonEvent);
                    });
                  }
                }
                setTimeout(function() {
                  nextTask();
                }, 200);
              },
              error: function(error) {
                console.log(error)
              }
          });
    }
  }
  // Start first Ajax call
  nextTask();
}

function unSubmitButtonEvent(e) {
  e.preventDefault();
  var $dialogContent = $("#unSubmit-justification");
  $dialogContent.dialog({
      width: '30%',
      modal: true,
      closeText: "",
      buttons: {
          Cancel: function() {
            $(this).dialog("close");
          },
          unSubmit: function() {
            var $justification = $dialogContent.find("#justification-unSubmit");
            if($justification.val().length > 0 && $justification.val().trim().length != 0) {
              // TODO: Create and change the un-submit synthesis
              var url = baseURL + "/unsubmitProject.do";
              var liaisonInstitutionID = $(".projectUnSubmitButton").attr("id").split("-")[1];
              var data = {
                  projectID: liaisonInstitutionID,
                  justification: $justification.val()
              }
              console.log(data);
              $justification.removeClass('fieldError');
              $.ajax({
                  url: url,
                  type: 'GET',
                  dataType: "json",
                  data: data
              }).done(
                  function(m) {
                    window.location.href =
                        baseURL + "/powb/" + currentCrpSession + "/adjustmentsChanges.do?liaisonInstitutionID="
                            + liaisonInstitutionID + "&edit=true";
                  });
            } else {
              $justification.addClass('fieldError');
            }
          }
      }
  });
}

function valida(F) {
  if(/^\s+|\s+$/.test(F.val())) {
    return false
  } else {
    return true;
  }
}
