$(document).ready(init);
var crpList = [];
function init() {
  if($(".checkEmail").val() != "") {
    ajaxService($(".checkEmail").val());
  }
  // $(".button-save").hide();
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
}

function attachEvents() {

  $(".button-save").on("click", checkAllFields);

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

  $(".checkEmail").on("keyup", function() {
    updateCrpSelect();
    var email = $(this).val();
    if(validateEmail(email)) {
      ajaxService(email);
    } else {
      enableFields(true);
      $(".infoService").css("color", "red");
      $(".infoService").text("Please, write a valid email.");
    }
  });

}

function ajaxService(email) {
  $.ajax({
      url: baseURL + "/searchUserByEmail.do",
      type: 'GET',
      data: {
        userEmail: email
      },
      success: function(m) {
        console.log(m);
        if(m.userFound.newUser == false && m.userFound.cgiarNoExist == true) {
          enableFields(true);
          var user = {
              id: "",
              name: "",
              lastName: "",
              email: m.userFound.email,
              username: "",
              cgiar: "false",
              active: "false",
              autosave: "false"
          };
          updateData(user);
          $(".infoService").css("color", "red");
          $(".infoService").text("This user is not a member of CGIAR, please check the email you entered.");
        } else {
          if(m.userFound.newUser == false) {
            $(".isNewUser").val(false);
            $(".infoService").css("color", "green");
            $(".infoService").text("Found user.");
            enableFields(true);
            updateData(m.userFound);
            updateCrps(m.crpUserFound);
            $(".crpSelect").attr("disabled", false);
          } else {
            $(".isNewUser").val(true);
            $(".infoService").css("color", "rgb(136, 72, 9)");
            $(".infoService").text(
                "This user doesn't exists into the MARLO database, you can to create a new user as guest.");
            $(".crpList").empty();
            // Check if is cgiar user
            if(m.userFound.cgiar == true) {
              updateData(m.userFound);
              enableFields(true);
              $(".crpSelect").attr("disabled", false);
              $(".button-save").show("slow");
            } else {
              enableFields(false);
              var user = {
                  id: "",
                  name: "",
                  lastName: "",
                  email: m.userFound.email,
                  username: "",
                  cgiar: "false",
                  active: "false",
                  autosave: "false"
              };
              updateData(user);
            }
          }
        }
      },
      error: function(e) {
        console.log(e);
      }
  });
}

function updateData(user) {
  // Empty fields
  // User data
  $(".userId").val(user.id);
  $(".userFirstName").val(user.name);
  $(".userLastName").val(user.lastName);
  $(".userEmail").val(user.email);
  $(".userUsername").val(user.username);
  $(".userPassword").val();
  // Configuration
  $(".cgiarUser").val(user.cgiar.toString()).trigger("change");
  $(".isActive").val(user.active.toString()).trigger("change");
  $(".autosave").val(user.autosave.toString()).trigger("change");

  // CRPS

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

function enableFields(state) {
  // User data
  $(".userFirstName").attr("readonly", state);
  $(".userLastName").attr("readonly", state);
  // $(".userUsername").attr("readonly", state);
  $(".userPassword").attr("readonly", state);
  // Configuration
  $(".cgiarUser").attr("disabled", state);
  // $(".isActive").attr("disabled", state);
  // $(".autosave").attr("disabled", state);

  $(".crpSelect").attr("disabled", state);

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
