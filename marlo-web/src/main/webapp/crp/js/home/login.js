$(document).ready(init);
var cookieTime;
var username = $("input[name='user.email']");
function init() {
  initJreject();

  cookieTime = 100;
  var crpInput = $('input#crp-input').val();
  var typeInput = $('input#type-input').val();

  // Verify "crp"
  if(verifyCookie("CRP") && (getCookie("CRP") != "undefined") && (!crpInput)) {
    var crpSelected = getCookie("CRP");
    setCRP(crpSelected);
  }

  // Verify "type" (CRP, Center, Platform)
  if(verifyCookie("TYPE") && (getCookie("TYPE") != "undefined") && (!typeInput)) {
    var typeSelected = getCookie("TYPE");
    setType(typeSelected);
  } else if(!typeInput) {
    setType('crp');
  }

  // Verify user email session
  if(verifyCookie("username.email")) {
    username.val(getCookie("username.email"));
  } else {
    username.val(getCookie(""));
  }

  // On select a CRP. Center, Platform
  $('.crpGroup ul li.enabled').on('click', function() {
    var crpSelected = $(this).attr('id').split('-')[1];
    setCRP(crpSelected);
  });

  // On select a Type
  $('.nav-tabs> li').on('click', function() {
    var typeSelected = $(this).attr('id');
    // Set Type
    setType(typeSelected);
    console.log(typeSelected);
  });

  $("a.goBackToSelect").on('click', function() {
    // Hide Second Form (Email, password & login button)
    $(this).parents('.loginForm').find('.secondForm').slideUp();

    // Show First Form (CRPs, Centers & Platforms)
    $(this).parents('.loginForm').find('.firstForm').slideDown();
  });

  // Username cookie
  username.on("change", function(e) {
    setCookie("username.email", username.val(), cookieTime);
  });

}

function setCRP(crpSelected) {
  var $li = $("li#crp-" + crpSelected);

  console.log()

  // Removing class selected
  $(".loginOption").removeClass('selected');

  // Add 'selected' class and removing sibling's class if any
  $li.addClass('selected');

  // Setting up the CRP-CENTER-PLATFORM value into a hidden input
  $('#crp-input').val(crpSelected);

  $("#crpSelectedImage").attr("src", baseUrl + "/global/images/crps/" + crpSelected + ".png");

  // Show Second Form (Email, password & login button)
  $li.parents('.loginForm').find('.secondForm').slideDown();

  // Hide First Form (CRPs, Centers & Platforms)
  $li.parents('.loginForm').find('.firstForm').slideUp();

  // Create crp cookie
  setCookie("CRP", crpSelected, cookieTime);
}

function setType(typeSelected) {
  // Enable tabbable tabs
  $('li.type-' + typeSelected + ' a').tab('show');
  // Setting up the type value to log (CRP-CENTER-PALTFORM)
  $('#type-input').val(typeSelected);
  // Create type cookie
  setCookie("TYPE", typeSelected, cookieTime);
}

function initJreject() {
  $.reject({
      reject: {
          msie: false,
          msie5: true,
          msie6: true,
          msie7: true, // Microsoft Internet Explorer
          firefox: false,
          firefox1: true,
          firefox2: true,
          firefox3: true, // Mozilla firefox
          opera: false,
          opera7: true,
          opera8: true,
          opera9: true, // Opera
          safari: false,
          safari2: true,
          safari3: true,
          safari4: true
      // Safari
      }, // Reject all renderers for demo

      closeCookie: true, // Set cookie to remmember close for this session
      display: [
          'firefox', 'chrome', 'opera', 'msie', 'safari'
      ]
  });
}

function verifyCookie(nameCookie) {
  if(getCookie(nameCookie) != "") {
    return true;
  } else {
    return false;
  }
}

function setCookie(cname,cvalue,mins) {
  var d = new Date();
  d.setTime(d.getTime() + (mins * 60 * 1000));
  var expires = "expires=" + d.toUTCString();
  document.cookie = cname + "=" + cvalue + "; " + expires;
}
