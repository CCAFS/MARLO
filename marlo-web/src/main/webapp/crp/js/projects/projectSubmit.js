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

  // Event for validate button inside each project
  $('.projectValidateButton, .validateButton').on('click', validateButtonEvent);
  $('.clusterMenu-status__check').on('keydown', validateButtonKeyEvent);

  // Refresh event when table is reloaded in project list section
  $('table.projectsList').on('draw.dt', function() {
    $('.projectValidateButton, .validateButton').on('click', validateButtonEvent);
    $(".progressbar").progressbar({
      max: tasksLength
    });
  });

  /* Change Pre-setting state */
  $('.projectEditLeader .button-label').on('click', function() {
    var $t = $(this).parent().find('input.onoffswitch-radio');
    var value = ($(this).hasClass('yes-button-label'));
    var $thisLabel = $(this);
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    if(value) {
      notyOptions.text = "Are you sure this project is ready to be completed by the project leader?";
    } else {
      notyOptions.text = "Are you sure you want to make this project to be in pre-set mode? ";
      notyOptions.text += "it won't be able to be edited by the Project Leader and/or Coordinator";
    }
    notyOptions.type = 'confirm';
    notyOptions.layout = 'center';
    notyOptions.modal = true;
    notyOptions.buttons = [
        {
            addClass: 'btn btn-primary',
            text: 'Yes',
            onClick: function($noty) {
              $.ajax({
                  url: baseURL + "/projectLeaderEdit.do",
                  data: {
                      projectID: $('input[name="projectID"]').val(),
                      projectStatus: value,
                      phaseID: phaseID
                  },
                  success: function(data) {
                    window.location.href = window.location.href;
                    if(data.ok) {
                      $thisLabel.siblings().removeClass('radio-checked');
                      $thisLabel.addClass('radio-checked');
                      $t.val(value);
                    }
                  }
              });
            }
        }, {
            addClass: 'btn btn-primary',
            text: 'No',
            onClick: function($noty) {
              $noty.close();
            }
        }
    ];
    noty(notyOptions);

  });

  // Click on submit button
  $('.submitButton, .projectSubmitButton').on('click', submitButtonEvent);

  // Click on submit button
  $('.projectUnSubmitButton').on('click', unSubmitButtonEvent);

  /**
   * Validate justification for old projects
   */
  var $justification = $('#justification');
  var $parent = $justification.parent().parent();
  var errorClass = 'fieldError';
  $parent.prepend('<div class="loading" style="display:none"></div>');
  $('[name=save]').on('click', function(e) {

    // Cancel Auto Save
    autoSaveActive = false;

    $justification.removeClass(errorClass);

    if(!validateField($('#justification'))) {
      // If field is not valid
      e.preventDefault();
      $justification.addClass(errorClass);
      // Go to justification field
      if($justification.exists) {
        $('html, body').animate({
          scrollTop: $justification.offset().top - 110
        }, 1500);
      }
      // Notify justification needs to be filled
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'The justification field needs to be filled';
      noty(notyOptions);

    }

  });

});

function submitButtonEvent(e) {
  e.preventDefault();
  // Read up front: the redesigned link nests an icon, so e.target can be the <svg>.
  var href = $(this).attr('href');
  var message = 'Are you sure you want to submit the cluster now? ';
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
                $('.projectSubmitButton').hide();
                window.location.href = href;
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
  // currentTarget, not target: the redesigned button nests an icon and a label.
  var pID = $(this).attr('id').split('-')[1];
  // Execute Ajax process for each section
  processTasks(sections, pID, $(this));
}

/**
 * Sections can add their own rows to the check results, e.g. the Partners page lists
 * each incomplete partner instead of a single "Partners" row. A provider returns an
 * array of {title, detail, onPick} or nothing to fall back to the default row.
 */
var clusterMenuSectionDetails = {};

function clusterMenuText(template, values) {
  return String(template || '').replace(/\{(\d+)\}/g, function(match, i) {
    return values[i] !== undefined ? values[i] : match;
  });
}

/** Recount the tracked menu rows and refresh the completeness bar and the submit note. */
function updateClusterCompleteness() {
  var $card = $('.clusterMenu-status');
  if(!$card.length) {
    return;
  }
  var $tracked = $('#secondaryMenu li[data-tracked]');
  var total = $tracked.length;
  var done = $tracked.filter('.submitted').length;
  var $count = $card.find('[data-cluster-done]');
  $count.text(clusterMenuText($count.attr('data-template'), [done, total]));
  $card.find('[data-cluster-bar]').css('width', (total ? Math.round(done * 100 / total) : 0) + '%');
  $card.find('.clusterMenu-status__bar').attr('aria-valuenow', done);
  var $note = $card.find('[data-cluster-submit-note]');
  if($note.length) {
    $note.text(done === total ? $note.attr('data-text-ready') : clusterMenuText($note.attr('data-text-pending'), [
      total - done
    ]));
  }
}

/** Write what the check found into the results panel under the button. */
function renderClusterCheckResults(missing) {
  var $results = $('[data-cluster-results]');
  if(!$results.length) {
    return;
  }
  $results.empty();
  if(!missing.length) {
    $('<p class="clusterMenu-results__ok"></p>').text($results.attr('data-text-ok')).appendTo($results);
    return;
  }
  $.each(missing, function(i, sectionName) {
    var $link = $('#menu-' + sectionName + ' > a');
    var rows = null;
    if(typeof clusterMenuSectionDetails[sectionName] === 'function') {
      rows = clusterMenuSectionDetails[sectionName]();
    }
    if(!rows || !rows.length) {
      rows = [
        {
            title: $.trim($link.text()) || sectionName,
            detail: $results.attr('data-text-missing'),
            href: $link.attr('href')
        }
      ];
    }
    $.each(rows, function(j, row) {
      var $row = row.href ? $('<a class="clusterMenu-results__row"></a>').attr('href', row.href) : $(
          '<button type="button" class="clusterMenu-results__row"></button>');
      $('<span class="clusterMenu-results__title"></span>').text(row.title).appendTo($row);
      $('<span class="clusterMenu-results__detail"></span>').text(row.detail).appendTo($row);
      if(row.onPick) {
        $row.on('click', function(e) {
          e.preventDefault();
          row.onPick();
        });
      }
      $row.appendTo($results);
    });
  });
}

function processTasks(tasks,id,button) {
  $(button).off('click keydown');
  var completed = 0;
  var index = 0;
  var missing = [];
  // Looked up by id: the results panel now sits between the button and the submit link.
  var $progress = $('#progressbar-' + id);
  if(!$progress.length) {
    $progress = $(button).next();
  }
  $('[data-cluster-results]').empty();
  $(button).fadeOut(function() {
    $progress.fadeIn();
  });
  function nextTask() {
    if(index < tasksLength) {
      var sectionName = tasks[index];
      var $sectionMenu = $('#menu-' + sectionName + '');
      $
          .ajax({
              url: baseURL + '/validateProjectSection.do',
              data: {
                  projectID: id,
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
                    // Only the rows with a status badge are reported as missing.
                    if($sectionMenu.is('[data-tracked]')) {
                      missing.push(sectionName);
                    }
                  }
                }
                $sectionMenu.removeClass('loadingSection');
              },
              complete: function(data) {
                $sectionMenu.addClass('animated flipInX');
                // Do next Ajax call
                $progress.progressbar("value", index + 1);
                index++;
                if(index == tasksLength) {
                  updateClusterCompleteness();
                  renderClusterCheckResults(missing);
                  var againLabel = $(button).attr('data-label-again');
                  if(againLabel) {
                    $(button).find('.clusterMenu-status__checkLabel').text(againLabel);
                  }
                  if(completed == tasksLength) {
                    // The results panel says it in words; the old toast stays for the
                    // menus that do not have one.
                    if(!$('[data-cluster-results]').length) {
                      var notyOptions = jQuery.extend({}, notyDefaultOptions);
                      notyOptions.text = 'The cluster can be submitted now';
                      notyOptions.type = 'success';
                      notyOptions.layout = 'center';
                      noty(notyOptions);
                    }
                    $progress.fadeOut(function() {
                      $('#submitProject-' + id + '.projectSubmitButton').css('display', 'flex').hide().fadeIn("slow");
                    });
                  } else {
                    var notyOptions = jQuery.extend({}, notyDefaultOptions);
                    notyOptions.text =
                        "The cluster is still incomplete, please go to the sections without the green check mark and complete the missing fields before submitting your project.";
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
                    $progress.fadeOut(function() {
                      $(button).fadeIn("slow").on('click', validateButtonEvent).on('keydown', validateButtonKeyEvent);
                    });
                  }
                }
                nextTask();
              },
              error: function(error) {
                // A section the server failed to validate cannot be called complete:
                // stop its spinner and report it with the ones still to fill.
                $sectionMenu.removeClass('loadingSection submitted').addClass('toSubmit');
                if($sectionMenu.is('[data-tracked]')) {
                  missing.push(sectionName);
                }
                console.log(error)
              }
          });
    }
  }
  // Start first Ajax call
  nextTask();
}

/** The check button is a div with role=button: Enter and Space run it like a click. */
function validateButtonKeyEvent(e) {
  if(e.which === 13 || e.which === 32) {
    validateButtonEvent.call(this, e);
  }
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
          "Request changes": function() {
            var $justification = $dialogContent.find("#justification-unSubmit");
            if($justification.val().length > 0 && $justification.val().trim().length != 0) {
              var url = baseURL + "/unsubmitProject.do";
              var projectId = $(".projectUnSubmitButton").attr("id").split("-")[1];
              var data = {
                  projectID: projectId,
                  justification: $justification.val(),
                  phaseID: phaseID
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
                        baseURL + "/projects/" + currentCrpSession + "/description.do?projectID=" + projectId
                            + "&edit=true&phaseID=" + phaseID;
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
