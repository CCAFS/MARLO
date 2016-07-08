var tasksLength;
var sections;
var currentCycle;

$(document).ready(function() {
  sections = [
      "outcomes", "clusterActivities"
  ];

  // Progress bar
  tasksLength = sections.length;
  $(".progressbar").progressbar({
    max: tasksLength
  });
  // Event for validate button inside each project
  $('.projectValidateButton, .validateButton').on('click', validateButtonEvent);

  // Refresh event when table is reloaded in project list section
  $('table.projectsList').on('draw.dt', function() {
    $('.projectValidateButton, .validateButton').on('click', validateButtonEvent);
    $(".progressbar").progressbar({
      max: tasksLength
    });
  });

  // Click on submit button
  $('.submitButton, .projectSubmitButton').on('click', submitButtonEvent);
});

function submitButtonEvent(e) {
  e.preventDefault();
  noty({
      text: 'Are you sure you want to submit the program impact pathway now?  Once submitted, you will no longer have editing rights.',
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
              url: baseURL + '/impactPathway/validateImpactPathway.do',
              data: {
                  crpProgramID: id,
                  sectionName: sectionName,
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
                    notyOptions.text = 'The program impact pathway can be submmited now';
                    notyOptions.type = 'success';
                    notyOptions.layout = 'center';
                    noty(notyOptions);
                    $(button).next().fadeOut(function() {
                      $(this).next().fadeIn("slow");
                    });
                  } else {
                    var notyOptions = jQuery.extend({}, notyDefaultOptions);
                    notyOptions.text =
                        "The program impact pathway is still incomplete, please go to the sections without the green check mark and complete the missing fields before submitting your program impact pathway.";
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
                nextTask();
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