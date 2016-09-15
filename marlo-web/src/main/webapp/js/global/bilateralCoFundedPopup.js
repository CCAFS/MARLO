var dialog, notyDialog;
var timeoutID;
var $elementSelected, $dialogContent, $searchInput;
var openSearchDialog, addProject, addUserMessage;
var institutionSelected, selectedPartnerTitle;

$(document).ready(function() {

  /** Initialize */
  $dialogContent = $("#dialog-searchProjects");
  $searchInput = $('.search-input .input input');
  var dialogOptions = {
      autoOpen: false,
      height: 600,
      width: 550,
      modal: true,
      dialogClass: 'dialog-searchUsers',
      open: function(event,ui) {
        $dialogContent.find("form")[0].reset();
        // $dialogContent.find("#search-users").trigger('click');
        $dialogContent.find(".tickBox-toggle").hide();
        $dialogContent.find('.warning-info').hide();

        $dialogContent.find(".panel-body ul").empty();
        $dialogContent.find(".panel-body .userMessage").show();
        // getData('');
      }
  };

  // Initializing Manage users dialog
  dialog = $dialogContent.dialog(dialogOptions);

  // Loading initial data with all users
  // getData('');

  /** Events */

  // Event for manage the accordion function
  $('#create-user').on('click', function() {
    $(this).siblings('.accordion-block').slideUp('slow');
    if($(this).next().is(':visible')) {
      $('#search-users').next().slideDown();
      $(this).removeClass('active');
      $(this).find('span.title').text('Create a bilateral co-funded project');
      $(this).find('span.glyphicon').removeClass('glyphicon-search').addClass('glyphicon-plus');
    } else {
      $(this).next().slideDown();
      $(this).addClass('active');
      $(this).find('span.title').text('Search bilateral project');
      $(this).find('span.glyphicon').removeClass('glyphicon-plus').addClass('glyphicon-search');
    }

  });

  $('.close-dialog').on('click', function() {
    dialog.dialog("close");
  });

  // Event to open dialog box and search an contact person
  $(".searchProject").on("click", function(e) {
    openSearchDialog(e);
  });

  // Event when the user select the contact person
  $dialogContent.find("span.select, span.name").on("click", function() {
    var $parent = $(this).parent().parent();
    var projectId = $parent.find(".contactId").text();
    var composedName = "P" + projectId + " - " + $parent.find(".name").text();
    // Add user
    addProject(composedName, projectId);
  });

  // Event to find an user according to search field
  $dialogContent.find(".search-content input").on("keyup", searchUsersEvent);

  // Event to search users clicking in "Search" button
  $dialogContent.find(".search-button").on("click", function() {
    getData($searchInput.val());
  });

  // Trigger to open create user section
  $dialogContent.find("span.link").on("click", function() {
    $dialogContent.find("#create-user").trigger('click');
  });

  // Event to Create an user clicking in the button
  $dialogContent.find(".create-button").on("click", function() {
    $dialogContent.find('.warning-info').empty().hide();
    var invalidFields = [];
    var project = {};
    project.title = $dialogContent.find("#title").val().trim();
    project.startDate = $dialogContent.find("#startDate").val().trim();
    project.endDate = $dialogContent.find("#endDate").val().trim();
    project.financeCode = $dialogContent.find("#financeCode").val().trim();
    project.status = $dialogContent.find("#status").val().trim();
    project.budget = $dialogContent.find("#budget").val().trim();
    project.liaisonInstitution = institutionSelected;
    project.institution = $dialogContent.find("#institution").val().trim();
    project.contactName = $dialogContent.find("#contactName").val().trim();
    project.contactEmail = $dialogContent.find("#contactEmail").val().trim();

    // Validate if fields are filled
    $.each(project, function(key,value) {
      if(value.length < 1) {
        invalidFields.push($('label[for="' + key + '"]').text().trim().replace(':', ''));
      }
    });
    // Validate Email
    var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
    if(!emailReg.test(project.contactEmail)) {
      invalidFields.push('valid contact email');
    }

    if(invalidFields.length > 0) {
      var msj = "You must fill " + invalidFields.join(', ');
      $dialogContent.find('.warning-info').text(msj).fadeIn('slow');
    } else {
      $.ajax({
          'url': baseURL + '/projectsBilateralAdd.do',
          method: 'POST',
          data: project,
          beforeSend: function() {
            $dialogContent.find('.loading').show();
          },
          success: function(data) {
            var data = data[0];
            if(data.status == "OK") {
              console.log('create');
              addProject(data.title, data.id);
            } else {
              $dialogContent.find('.warning-info').text(data.message).fadeIn('slow');
            }
          },
          complete: function(data) {
            $dialogContent.find('.loading').fadeOut();
          }
      });
    }

  });

  $dialogContent.find("form").on("submit", function(e) {
    event.preventDefault();
  });

  /** Functions * */

  openSearchDialog = function(e) {
    e.preventDefault();
    $elementSelected = $(e.target);
    selectedPartnerTitle = $elementSelected.parents('.projectPartner').find('.partnerTitle').text();
    $dialogContent.find('.cgiarCenter').text(selectedPartnerTitle);
    institutionSelected = $elementSelected.parents('.projectPartner').find('.partnerInstitutionId').text();

    dialog.dialog("open");

    // Hide search loader
    $dialogContent.find(".search-loader").fadeOut("slow");

    // Set currency format
    $dialogContent.find('#budgetAgreementPeriod').currencyInput();

    // Set dates
    date('#startDate', '#endDate');
  }

  addProject = function(composedName,projectId) {
    dialog.dialog("close");
  }

  addUserMessage = function(message) {
    $elementSelected.parent().find('.username-message').remove();
    $elementSelected.after("<p class='username-message note animated flipInX'>" + message + "</p>");
  }

  function searchUsersEvent(e) {
    var query = $(this).val();
    if(query.length > 1) {
      if(timeoutID) {
        clearTimeout(timeoutID);
      }
      // Start a timer that will search when finished
      timeoutID = setTimeout(function() {
        getData(query);
      }, 400);
    } else {
      // getData('');
    }

  }

  function getData(query) {
    $.ajax({
        'url': baseURL + '/projectsBilateralList.do',
        'data': {
            q: query,
            institutionID: institutionSelected,
            year: new Date().getFullYear()
        },
        'dataType': "json",
        beforeSend: function(xhr,opts) {
          $dialogContent.find(".search-loader").show();
          $dialogContent.find(".panel-body ul").empty();
        },
        success: function(data) {
          var usersFound = (data.projects).length;
          if(usersFound > 0) {
            $dialogContent.find(".panel-body .userMessage").hide();
            $.each(data.projects, function(i,project) {
              var $item = $dialogContent.find("li#userTemplate").clone(true).removeAttr("id");
              $item.find('.name').html(project.title);
              $item.find('.contactId').html(project.id);
              if(i == usersFound - 1) {
                $item.addClass('last');
              }
              $dialogContent.find(".panel-body ul").append($item);
            });
          } else {
            $dialogContent.find(".panel-body .userMessage").show();
          }

        },
        complete: function() {
          $dialogContent.find(".search-loader").fadeOut("slow");
        }
    });

  }

});

function date(start,end) {
  var dateFormat = "yy-mm-dd", from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2015-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true
  }).on("change", function() {
    to.datepicker("option", "minDate", getDate(this));
  }), to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2015-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true
  }).on("change", function() {
    from.datepicker("option", "maxDate", getDate(this));
  });

  function getDate(element) {
    var date;
    try {
      date = $.datepicker.parseDate(dateFormat, element.value);
    } catch(error) {
      date = null;
    }
    return date;
  }
}
