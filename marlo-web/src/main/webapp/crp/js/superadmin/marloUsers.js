$(document).ready(init);
var crpList = [];
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
   * Users Datatable
   */
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
          }, {
            data: "name"
          }, {
              data: "username",
              render: function(data,type,full,meta) {
                return data || '<i>No Username<i>';
              }
          }, {
              data: "email",
              render: function(data,type,full,meta) {
                return '<span class="email">' + data + '</span>';
              }
          }, {
              data: "isActive",
              render: function(data,type,full,meta) {
                return '<div class="text-center"><img src="' + baseURL + '/images/global/checked-' + data + '.png" /></div>';
              }
          }, {
              data: "autoSaveActive",
              render: function(data,type,full,meta) {
                return '<div class="text-center"><img src="' + baseURL + '/images/global/checked-' + data + '.png" /></div>';
              }
          }, {
              data: "lastLogin",
              render: function(data,type,full,meta) {
                return data || '<i>No Date<i>';
              }
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
      ajaxService(userSelectedEmail);
      
      // Modal
      $('#myModal').on('shown.bs.modal', function () {
        console.log('Modla open');
      })
      $('#myModal').modal();
    })
  });
  
  /**
   * Add New User
   */
  $('#addNewUser').on('click', function(){
    enableFields(false);
    updateData({
      "lastName":"",
      "autosave":true,
      "cgiar":false,
      "newUser":true,
      "name":"",
      "active":true,
      "id":"",
      "email":"",
      "username":""
    });
    updateCrps({
      
    });
    
    $('#myModal').modal();
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

}

function ajaxService(email) {
  if(!validateEmail(email)) {
    return
  } 
  $.ajax({
      url: baseURL + "/searchUserByEmail.do",
      type: 'GET',
      data: {
        userEmail: email
      },
      success: function(m) {
        console.log(m);
        // Existing user
        enableFields(true);
        updateData(m.userFound);
        updateCrps(m.crpUserFound);
        $(".crpSelect").attr("disabled", false);
         
        
      }
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
