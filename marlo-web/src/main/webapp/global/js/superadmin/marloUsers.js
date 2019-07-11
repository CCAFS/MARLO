var crpList = [];
$(document).ready(function() {

  // Add Select2 plugin
  $('form select').select2({
    width: "100%"
  });

  // Add users datatable
  addUsersDatatable();

  // Add guest user module
  guestUsersModule.init();

});

var guestUsersModule =
    (function() {
      var $userEmail = $('input.userEmail');
      var $firstName = $('input.userFirstName');
      var $lastName = $('input.userLastName');
      var $selectedGlobalUnitID = $('input.selectedGlobalUnitID');
      var $message = $('#guestUserMessage');
      var $userNameBlock = $('.firstLastName');
      var $submitButton = $('button[name="save"]');
      var timer = null;
      var userHasAccess = false;

      function init() {
        events();
      }

      function events() {
        $userEmail.on("keyup", function() {
          $userEmail.addClass('input-loading');
          disabledSubmitButton(true);
          if(timer) {
            clearTimeout(timer); // cancel the previous timer.
            timer = null;
          }
          timer = setTimeout(findUserEmail, 1500);
        });

        $firstName.on("change keyup", validateForm);
        $lastName.on("change keyup", validateForm);
      }

      function getUserEmail() {
        return $.trim($userEmail.val());
      }

      function getCRPAcronym() {
        return $.trim($selectedGlobalUnitID.val());
      }

      function findUserEmail() {
        var email = getUserEmail();
        $.ajax({
            url: baseUrl + "/crpByEmail.do",
            data: {
              userEmail: email
            },
            beforeSend: function() {
              $message.hide();
              userHasAccess = false;
            },
            success: function(data) {
              console.log(data);
              if(data.user == null) {
              } else {
                $.each(data.crps, function(i,crp) {
                  if(crp.acronym == getCRPAcronym()) {
                    $message.text(data.user.name + " has already access to " + getCRPAcronym() + "").fadeIn();
                    userHasAccess = true;
                  }
                });
              }
            },
            complete: function(data) {
              validateCGIAR();
              validateForm();
            },
            error: function(data) {
            }
        });
      }

      function validateForm() {
        var email = getUserEmail();
        var firstName = $.trim($firstName.val());
        var lastName = $.trim($lastName.val());
        var isValid = false;

        if(!userHasAccess) {
          if(validateCGIAR()) {
            isValid = true;
          } else {
            if(validateEmail(email) && firstName && lastName) {
              isValid = true;
            }
          }
        }

        disabledSubmitButton(!isValid);
      }

      function disabledSubmitButton(state) {
        $submitButton.prop("disabled", state);
        if(state) {
          $submitButton.addClass("disabled");
        } else {
          $submitButton.removeClass("disabled");
        }
      }

      function validateCGIAR() {
        var email = getUserEmail();
        $userEmail.removeClass('input-loading');
        if(validateEmail(email) && email.indexOf("@cgiar.org") !== -1) {
          $userNameBlock.slideUp();
          return true;
        } else {
          $userNameBlock.slideDown();
          return false;
        }
      }

      function validateEmail(email) {
        var re =
            /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
      }

      return {
        init: init
      }
    })();

/**
 * Users Datatable
 */
function addUsersDatatable() {
  var $marloUsersTable = $('#marloUsersTable');

  $marloUsersTable.DataTable({
      iDisplayLength: 20, // Number of rows to show on the table
      ajax: {
          "url": baseURL + '/searchUsers.do?q=',
          "dataSrc": "users"
      },
      columns: [
          {
            data: "id"
          },
          {
            data: "name"
          },
          {
              data: "username",
              render: function(data,type,full,meta) {
                return data || '<i>No Username<i>';
              }
          },
          {
              data: "email",
              render: function(data,type,full,meta) {
                return '<span class="email">' + data + '</span>';
              }
          },
          {
              data: "isActive",
              render: function(data,type,full,meta) {
                return '<div class="text-center"><img src="' + baseURL + '/global/images/checked-' + data
                    + '.png" /></div>';
              }
          },
          {
              data: "autoSaveActive",
              render: function(data,type,full,meta) {
                return '<div class="text-center"><img src="' + baseURL + '/global/images/checked-' + data
                    + '.png" /></div>';
              }
          }, {
              data: "lastLogin",
              render: function(data,type,full,meta) {
                return data || '<i>No Date<i>';
              }
          }
      ],
      aoColumnDefs: [
        {
            sType: "natural",
            aTargets: [
              0
            ],
            asSorting: [
              "desc"
            ]
        }
      ]

  });
  $marloUsersTable.on('draw.dt', function() {
    $marloUsersTable.find('tbody tr').on("click", function() {
      var userSelectedEmail = $(this).find('span.email').text();
      var $inputEmail = $('input.checkEmail');
      $inputEmail.val(userSelectedEmail);
      // Go to field
      /*
       * $('html, body').animate({ scrollTop: $inputEmail.offset().top - 110 }, 1500);
       */
      // Find user details
      // ajaxService(userSelectedEmail);
      // Modal
      /*
       * $('#myModal').on('shown.bs.modal', function () { console.log('Modla open'); }) $('#myModal').modal();
       */
    })
  });
}

/** **************************** CODE BELOW IS NOT USED ********************************** */

function init() {

  if($(".checkEmail").val() != "") {
    ajaxService($(".checkEmail").val());
  }

  /* Declaring Events */
  attachEvents();
  $('form select').select2({
    width: "100%"
  });

  // Add options
  $(".cgiarUser").addOption("false", "No");
  $(".cgiarUser").addOption("true", "Yes");
  $(".isActive").addOption("false", "No");
  $(".isActive").addOption("true", "Yes");
  $(".autosave").addOption("false", "No");
  $(".autosave").addOption("true", "Yes");
  $(".crpSelect").find("option").each(function(i,e) {
    var option = {
        "id": $(e).val(),
        "name": $(e).html()
    };
    crpList.push(option);
  });

  /**
   * Add New User
   */
  $('#addNewUser').on('click', function() {
    enableFields(false);
    updateData({
        "lastName": "",
        "autosave": true,
        "cgiar": false,
        "newUser": true,
        "name": "",
        "active": true,
        "id": "",
        "email": "",
        "username": ""
    });
    updateCrps({

    });

    $('#myModal').modal();
  });

}

function attachEvents() {

  // $(".button-save").on("click", checkAllFields);

  $('.blockTitle.closed').on('click', function() {
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
    });
  });

  $(".crpSelect").on("change", function() {
    var option = $(this).find("option:selected");
    addCrp(option);
  });

}

function updateData(user) {

  // User data
  $(".isNewUser").val(user.newUser);
  $(".userId").val(user.id);
  $(".userFirstName").val(user.name);
  $(".userLastName").val(user.lastName);
  $(".userEmail").val(user.email);
  $(".userUsername").val(user.username);
  $(".userPassword").val("");
  // Configuration
  $(".cgiarUser").val(user.cgiar.toString()).trigger("change");
  $(".isActive").val(user.active.toString()).trigger("change");
  $(".autosave").val(user.autosave.toString()).trigger("change");

}

function enableFields(state) {
  // User data
  // $(".userFirstName").attr("readonly", state);
  // $(".userLastName").attr("readonly", state);
  $(".userEmail").attr("readonly", state);
  // $(".userUsername").attr("readonly", state);
  // $(".userPassword").attr("readonly", state);

  // Configuration
  $(".cgiarUser").attr("disabled", state);
  // $(".isActive").attr("disabled", state);
  // $(".autosave").attr("disabled", state);

  $(".crpSelect").attr("disabled", state);

}

function updateCrpSelect() {
  var select = $(".crpSelect");
  select.empty();
  $.each(crpList, function(i,e) {
    select.addOption(e.id, e.name);
  });
}

function updateCrps(crps) {
  var item, list = $(".crpList");
  list.empty();
  $.each(crps, function(i,e) {
    item = $("#crp-template").clone(true).removeAttr("id");
    item.find(".crpTitle").html(e.crpAcronym)
    item.find(".crpUserId").val(e.crpUserId)
    item.find(".crpUserCrpId").val(e.crpId);
    // Remove crps from crpSelect
    $(".crpSelect").find("option[value='" + e.crpId + "']").remove();
    // Role list
    var rolesList = $(item).find(".rolesList");
    // Roles
    $.each(e.role, function(iRole,eRole) {
      var infoList = "<br><ul>";
      $.each(eRole.roleInfo, function(index,element) {
        infoList = infoList + "<li>" + element + "</li>";
      });
      infoList = infoList + "</ul>";
      var span = "<span class='roleSpan'>" + eRole.role + infoList + "</span>";
      // Roles info

      rolesList.append(span);
    });
    list.append(item);
    item.show("slow");
  });
  updateCrpIndex();
}

function addCrp(option) {
  var list = $(".crpList");
  var item = $("#crp-template").clone(true).removeAttr("id");
  item.find(".crpTitle").html($(option).html());
  item.find(".crpUserId").val("-1");
  item.find(".crpUserCrpId").val($(option).val());
  var rolesList = item.find(".rolesList");
  var span = "<span class='roleSpan'>Guest</span>";
  rolesList.append(span);
  list.append(item);
  item.show("slow");
  $(".crpSelect").find("option[value='" + $(option).val() + "']").remove();
  updateCrpIndex();
}

function validateEmail(email) {
  var re =
      /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}

function checkAllFields(e) {
  var count = 0;
  if($(".crpList").find(".crpItem").length > 0) {
    count++;
  }
  if($(".isNewUser").val() == "true") {
    if($(".userFirstName").val().length != 0) {
      count++;
    }
    if($(".userLastName").val().trim() != "") {
      count++;
    }
    if(count < 3) {
      e.preventDefault();
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'Please complete the fields to create the user guest';
      noty(notyOptions);
    } else {
      $(".button-save").trigger('submit');
    }
  } else {
    if(count < 1) {
      e.preventDefault();
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'Please complete the fields to create the user guest';
      noty(notyOptions);
    } else {
      $(".button-save").trigger('submit');
    }
  }

}

function updateCrpIndex() {
  $(".crpList").find('.crpItem').each(function(i,e) {
    // Set crp indexes
    $(e).setNameIndexes(1, i);
  });
}
