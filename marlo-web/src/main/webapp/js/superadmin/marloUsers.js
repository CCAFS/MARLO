$(document).ready(init);

function init() {
  $(".button-save").hide();
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
}

function attachEvents() {

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
    var email = $(this).val();
    if(validateEmail(email)) {
      $.ajax({
          url: baseURL + "/searchUserByEmail.do",
          type: 'GET',
          data: {
            userEmail: email
          },
          success: function(m) {
            console.log(m);
            if(m.userFound.newUser == false) {
              $(".infoService").css("color", "green");
              $(".infoService").text("Found user.");
              enableFields(true);
              updateData(m.userFound);
              updateCrps(m.crpUserFound);
            } else {
              $(".crpList").empty();
              enableFields(false);
              $(".infoService").css("color", "rgb(136, 72, 9)");
              $(".infoService").text("This user doesn't exists, you can to create a new user as guest.");
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
          },
          error: function(e) {
            console.log(e);
          }
      });
    } else {
      enableFields(true);
      $(".infoService").css("color", "red");
      $(".infoService").text("Please, write a valid email.");
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

function updateCrps(crps) {
  var item, list = $(".crpList");
  list.empty();
  $.each(crps, function(i,e) {
    item = $("#crp-template").clone(true).removeAttr("id");
    item.find(".crpTitle").html(e.crpAcronym)
    item.find(".crpUserId").val(e.crpUserId)
    item.find(".crpUserCrpId").val(e.crpId);
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
}

function enableFields(state) {
  // User data
  $(".userFirstName").attr("disabled", state);
  $(".userLastName").attr("disabled", state);
  $(".userUsername").attr("disabled", state);
  $(".userPassword").attr("disabled", state);
  // Configuration
  $(".cgiarUser").attr("disabled", state);
  // $(".isActive").attr("disabled", state);
  // $(".autosave").attr("disabled", state);

  $(".crpSelect").attr("disabled", state);
  if(state == true) {
    $(".button-save").hide();
  } else {
    $(".button-save").show("slow");
  }
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
}

function validateEmail(email) {
  var re =
      /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}
