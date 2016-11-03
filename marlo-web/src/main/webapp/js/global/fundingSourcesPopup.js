var dialog, notyDialog;
var timeoutID;
var $elementSelected, $dialogContent, $searchInput;
var openSearchDialog, addProject, addUserMessage;
var institutionSelected, selectedPartnerTitle, selectedYear;

$(document).ready(function() {

  /** Initialize */
  $dialogContent = $("#dialog-searchProjects");
  $searchInput = $('.search-input .input input');
  var dialogOptions = {
      autoOpen: false,
      height: 650,
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
      $(this).find('span.title').text('Create Funding Source');
      $(this).find('span.glyphicon').removeClass('glyphicon-search').addClass('glyphicon-plus');
    } else {
      $(this).next().slideDown();
      $(this).addClass('active');
      $(this).find('span.title').text('Search Funding Source');
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
    var composedName = $parent.find(".name").text();
    var budget = $parent.find(".budget").text();
    var type = $parent.find(".budgetTypeName").text();
    var typeId = $parent.find(".budgetTypeId").text();

    // Add user
    addProject(composedName, projectId, budget, type, typeId);
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
    project.cofundedMode = $dialogContent.find("input[name='cofundedMode']").val().trim();
    project.description = $dialogContent.find("#description").val().trim();
    project.startDate = $dialogContent.find("#startDate").val().trim();
    project.endDate = $dialogContent.find("#endDate").val().trim();
    project.financeCode = $dialogContent.find("#financeCode").val().trim();
    project.status = $dialogContent.find("#status").val().trim();
    project.budgetType = $dialogContent.find("#budgetType").val().trim();
    project.liaisonInstitution = institutionSelected;
    project.institution = $dialogContent.find("#institution").val().trim();
    project.contactName = $dialogContent.find("#contactName").val().trim();
    project.contactEmail = $dialogContent.find("#contactEmail").val().trim();
    project.selectedYear = selectedYear;
    project.budgets = [];
    $('.budgetByYears .tab-content .tab-pane').each(function(i,e) {
      project.budgets.push({
          year: $(e).attr('id').split('-')[1],
          budget: removeCurrencyFormat($(e).find('input').val())
      });
    });
    project.budgets = JSON.stringify(project.budgets);

    var projectValidate = {};
    projectValidate.description = project.description;
    projectValidate.startDate = project.startDate;
    projectValidate.endDate = project.endDate;
    projectValidate.status = project.status;
    projectValidate.contactName = project.contactName;
    projectValidate.contactEmail = project.contactEmail;
    projectValidate.institution = project.institution;

    // Validate if fields are filled
    $.each(projectValidate, function(key,value) {
      if(value.length < 1) {
        invalidFields.push($('label[for="' + key + '"]').text().trim().replace(':', ''));
      } else if(value == -1) {
        invalidFields.push('Select an option');
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
          'url': baseURL + '/fundingSourceAdd.do',
          method: 'POST',
          data: project,
          beforeSend: function() {
            $dialogContent.find('.loading').show();
          },
          success: function(data) {
            if(data.status == "OK") {
              console.log('create');
              addProject(data.title, data.id, data.ammount, data.type, data.typeID);
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
    selectedYear = $elementSelected.parents('.tab-pane').attr('id').split('-')[1];

    dialog.dialog("open");

    // Hide search loader
    $dialogContent.find(".search-loader").fadeOut("slow");

    // Set dates
    date('#startDate', '#endDate');

    // Search initial projects
    getData('');
  }

  addProject = function(composedName,projectId,budget,type,typeId) {
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
        'url': baseURL + '/FundingSourceList.do',
        'data': {
            q: query,
            institutionID: institutionSelected,
            year: selectedYear
        },
        'dataType': "json",
        beforeSend: function(xhr,opts) {
          $dialogContent.find(".search-loader").show();
          $dialogContent.find(".panel-body ul").empty();
        },
        success: function(data) {
          var usersFound = (data.sources).length;
          if(usersFound > 0) {
            $dialogContent.find(".panel-body .userMessage").hide();
            $.each(data.sources, function(i,source) {
              var $item = $dialogContent.find("li#userTemplate").clone(true).removeAttr("id");

              if(source.amount <= 0) {
                $item.find('.noBudgetMessage').show();
                $item.find('.noBudgetMessage').attr('title', 'Insufficient funds for ' + selectedYear);
                // $item.find('.listButton.select').hide();
              }

              $item.find('.name').html('<strong>' + source.type + '</strong> - ' + source.name);
              $item.find('.contactId').html(source.id);
              $item.find('.budget').html(source.amount);
              $item.find('.budgetTypeName').html(source.type);
              $item.find('.budgetTypeId').html(source.typeId);
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
    getYears();
  }), to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2015-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true
  }).on("change", function() {
    from.datepicker("option", "maxDate", getDate(this));
    getYears();
  });

  function getYears() {
    var endYear = (new Date(to.val())).getFullYear();
    var years = [];
    var startYear = (new Date(from.val())).getFullYear() || 2015;

    $('.budgetByYears .nav-tabs').empty();
    $('.budgetByYears .tab-content').empty();

    while(startYear <= endYear) {
      var state = '';
      if(selectedYear == startYear) {
        state = 'active';
      }
      $('.budgetByYears .nav-tabs').append(
          '<li class="' + state + '"><a href="#fundingYear-' + startYear + '" data-toggle="tab">' + startYear
              + '</a></li>');
      $('.budgetByYears .tab-content').append(
          '<div id="fundingYear-' + startYear + '" class="tab-pane col-md-4 ' + state + '">'
              + '<label for="">Budget for ' + startYear
              + ':</label> <input type="text" class="currencyInput form-control input-sm col-md-4" />' + '</div>');

      years.push(startYear++);
    }

    if(years.indexOf(parseInt(selectedYear)) == -1) {
      $('.budgetByYears .nav-tabs li').last().addClass('active');
      $('.budgetByYears .tab-content .tab-pane').last().addClass('active');
    }

    // Set currency format
    $dialogContent.find('.currencyInput').currencyInput();

    $dialogContent.find('.currencyInput').on('keyup', function() {

    });

  }

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
