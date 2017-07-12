$(document).ready(init);
var cookieTime;
var username = $("input[name='user.email']");
function init() {
  initJreject();

  cookieTime = 10;
  var crpInput = $('input#crp-input').val();

  if(verifyCookie("CRP") && (getCookie("CRP") != "undefined") && (!crpInput)) {

    var crpSelected = getCookie("CRP");
    setCRP(crpSelected);

    if(verifyCookie("username.email")) {
      username.val(getCookie("username.email"));
    } else {
      username.val(getCookie(""));
    }
  }

  $('.crpGroup ul li.enabled').on('click', function() {
    var crpSelected = $(this).attr('id').split('-')[1];
    setCRP(crpSelected);
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
  var type = $('.nav-tabs li.active').attr('id');
  // Removing class selected
  $(".loginOption").removeClass('selected');

  // Add 'selected' class and removing sibling's class if any
  $li.addClass('selected');

  console.log(crpSelected);
  // Setting up the CRP-CENTER-PLATFORM value into a hidden input
  $('#crp-input').val(crpSelected);
  // Setting up the type value to log (CRP-CENTER-PALTFORM)
  $('#type-input').val(type);

  $("#crpSelectedImage").attr("src", baseUrlMedia + "/images/global/crps/" + crpSelected + ".png");

  // Show Second Form (Email, password & login button)
  $li.parents('.loginForm').find('.secondForm').slideDown();

  // Hide First Form (CRPs, Centers & Platforms)
  $li.parents('.loginForm').find('.firstForm').slideUp();

  // Create crp cookie
  setCookie("CRP", crpSelected, cookieTime);
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
