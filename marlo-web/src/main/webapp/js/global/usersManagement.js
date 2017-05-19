var dialog, notyDialog;
var timeoutID;
var $elementSelected, $dialogContent, $searchInput;
var openSearchDialog, addUser, addUserMessage;

$(document)
    .ready(
        function() {

          /** Initialize */
          $dialogContent = $("#dialog-searchUsers");
          $searchInput = $('.search-input .input input');
          var dialogOptions = {
              autoOpen: false,
              width: 450,
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
          /*
           * $dialogContent.find(".accordion").on('click', function() {
           * $(this).siblings('.accordion-block').slideUp('slow'); $(this).siblings('.accordion').addClass('active');
           * $(this).removeClass('active'); $(this).next().slideToggle(); });
           */

          $('#create-user').on('click', function() {
            $(this).siblings('.accordion-block').slideUp('slow');
            if($(this).next().is(':visible')) {
              $('#search-users').next().slideDown();
              $(this).removeClass('active');
              $(this).find('span.title').text('Create new user');
              $(this).find('span.glyphicon').removeClass('glyphicon-search').addClass('glyphicon-plus');
            } else {
              $(this).next().slideDown();
              $(this).addClass('active');
              $(this).find('span.title').text('Search People');
              $(this).find('span.glyphicon').removeClass('glyphicon-plus').addClass('glyphicon-search');

            }

          });

          $('.close-dialog').on('click', function() {
            dialog.dialog("close");
          });

          // Event to open dialog box and search an contact person
          $(".searchUser, input.userName").on("click", function(e) {
            e.preventDefault();
            openSearchDialog($(this));
          });

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
          $dialogContent
              .find(".create-button")
              .on(
                  "click",
                  function() {
                    $("#errorInfo").remove();
                    $dialogContent.find('.warning-info').empty().hide();
                    var invalidFields = [];
                    var user = {};
                    user.actionName = $('#actionName').val();
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
                      $
                          .ajax({
                              'url': baseURL + '/createUser.do',
                              data: user,
                              beforeSend: function() {
                                $dialogContent.find('.loading').show();
                              },
                              success: function(data) {
                                if(data.message) {
                                  $dialogContent.find('.warning-info').text(data.message).fadeIn('slow');
                                } else {
                                  addUser(data.users[0].composedName, data.users[0].id);
                                  addUserMessage($('#created-message').val());
                                }
                              },
                              complete: function(data) {
                                $dialogContent.find('.loading').fadeOut();
                              },
                              error: function(data) {
                                var errorInfo =
                                    "<p id='errorInfo' class='error-info'>An error has occurred creating this user, please contact with MARLOSupport@cgiar.org</p>"
                                $(".create-user-block").prepend(errorInfo);
                                console.log("Holi error" + data);
                              }
                          });
                    }

                  });

          $dialogContent.find("form").on("submit", function(e) {
            event.preventDefault();
          });

          /** Functions * */

          openSearchDialog = function(target) {
            $("#errorInfo").remove();
            $elementSelected = $(target);
            dialog.dialog("open");
            $dialogContent.find(".search-loader").fadeOut("slow");
          }

          addUser =
              function(composedName,userId) {
                $elementSelected.parents('.userField ').find("input.userName").val(composedName).addClass(
                    'animated flash');
                $elementSelected.parents('.userField ').find("input.userId").val(userId);
                dialog.dialog("close");
              }

          addUserMessage =
              function(message) {
                $elementSelected.parent().find('.username-message').remove();
                $elementSelected.parent().after(
                    "<div style='margin-top:5px;' class='col-md-12'><p class='username-message note animated flipInX col-md-12'>"
                        + message + "</p></div>");
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
                'url': baseURL + '/searchUsers.do',
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
