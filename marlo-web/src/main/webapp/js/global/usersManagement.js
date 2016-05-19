var dialog;
var timeoutID;
var $elementSelected, $dialogContent, $searchInput;
$(document).ready(function() {

  /** Initialize */

  $dialogContent = $("#dialog-searchUsers");
  $searchInput = $('.search-input .input input');
  var dialogOptions = {
      autoOpen: false,
      height: 400,
      width: 600,
      modal: true,

      buttons: {
        Cancel: function() {
          $(this).dialog("close");
        }
      },
      open: function(event,ui) {
        $dialogContent.find("form")[0].reset();
        // $dialogContent.find("#search-users").trigger('click');
        $dialogContent.find(".tickBox-toggle").hide();
        $dialogContent.find('.warning-info').hide();
        getData('');
      }
  };

  // Initializing Manage users dialog
  dialog = $dialogContent.dialog(dialogOptions);

  // Loading initial data with all users
  getData('');

  /** Events */

  // Event for manage the accordion function
  $dialogContent.find(".accordion").on('click', function() {
    $(this).parent().find('.accordion span').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
    $(this).siblings('.accordion-block').slideUp('slow');
    $(this).siblings('.accordion').addClass('active');
    $(this).removeClass('active');
    $(this).next().slideToggle();
    $(this).find('span').addClass('ui-icon-triangle-1-s');
  });

  // Event to open dialog box and search an contact person
  $(".searchUser, input.userName").on("click", openSearchDialog);

  // Event when the user select the contact person
  $dialogContent.find("span.select, span.name").on("click", function() {
    var userId = $(this).parent().find(".contactId").text();
    var composedName = $(this).parent().find(".name").text();
    // Add user
    addUser(composedName, userId);
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
    var user = {};
    user.email = $dialogContent.find("#email").val().trim();
    var isCGIAREmail = ((user.email).indexOf("cgiar") > -1);
    if(!isCGIAREmail) {
      $('#isCCAFS').prop('checked', true);
      $dialogContent.find(".tickBox-toggle").show();
    } else {
      $('#isCCAFS').prop('checked', false);
      $dialogContent.find(".tickBox-toggle").hide();
    }

    if($dialogContent.find("#isCCAFS").is(':checked') && !isCGIAREmail) {
      user.firstName = $dialogContent.find("#firstName").val();
      user.lastName = $dialogContent.find("#lastName").val();
    }

    user.isActive = $dialogContent.find("#isActive").val();

    // Validate if fields are filled
    $.each(user, function(key,value) {
      if(value.length < 1) {
        invalidFields.push($('label[for="' + key + '"]').text().trim().replace(':', ''));
      }
    });
    // Validate Email
    var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
    if(!emailReg.test(user.email)) {
      invalidFields.push('valid email');
    }

    if(invalidFields.length > 0) {
      var msj = "You must fill " + invalidFields.join(', ');
      $dialogContent.find('.warning-info').text(msj).fadeIn('slow');
    } else {
      $.ajax({
          'url': baseURL + '/createUser.do',
          data: user,
          beforeSend: function() {
            $dialogContent.find('.loading').show();
          },
          success: function(data) {
            if(data.message) {
              $dialogContent.find('.warning-info').text(data.message).fadeIn('slow');
            } else {
              addUser(data.newUser.composedName, data.newUser.id);
              addUserMessage($('#created-message').val());
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

  function openSearchDialog(e) {
    e.preventDefault();
    $elementSelected = $(this).parents('.userField').find('.searchUser').parent();
    dialog.dialog("open");
    $dialogContent.find(".search-loader").fadeOut("slow");
  }

  function addUser(composedName,userId) {
    $elementSelected.find("input.userName").val(composedName).addClass('animated flash');
    $elementSelected.find("input.userId").val(userId);
    dialog.dialog("close");
  }

  function addUserMessage(message) {
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
      }, 500);
    } else {
      getData('');
    }

  }

  function getData(query) {
    $.ajax({
        'url': '../../searchUsers.do',
        'data': {
          q: query
        },
        'dataType': "json",
        beforeSend: function() {
          $dialogContent.find(".search-loader").show();
          $dialogContent.find(".panel-body ul").empty();
        },
        success: function(data) {
          var usersFound = (data.users).length;
          if(usersFound > 0) {
            $dialogContent.find(".panel-body .userMessage").hide();
            $.each(data.users, function(i,user) {
              var $item = $dialogContent.find("li#userTemplate").clone(true).removeAttr("id");
              $item.find('.name').html(escapeHtml(user.composedName));
              $item.find('.contactId').html(user.id);
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
