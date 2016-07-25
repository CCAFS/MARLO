$(document).ready(init);
var username = $("input[name='user.email']");
function init() {
  initJreject();

  var cookieTime = 5;

  if(verifyCookie("CRP") == true) {
    var crpCookie = $("li#" + getCookie("CRP"));
    console.log(crpCookie);
    crpCookie.addClass('selected').siblings().removeClass('selected');
    $('#crp').val(crpCookie.attr('id').split("-")[1]);
    crpCookie.parents('.loginForm').find('.secondForm').slideDown();
    if(verifyCookie("username.email") == true) {
      username.val(getCookie("username.email"));
    } else {
      username.val(getCookie(""));
    }
  }

  $('.crpGroup ul li').on('click', function() {
    // Add 'selected' class and removing sibling's class if any
    $(this).addClass('selected').siblings().removeClass('selected');
    // Setting up the CRP value into a hidden input
    $('#crp').val($(this).attr('id').split('-')[1]);
    // Show Second Form (Email, password & login button)
    $(this).parents('.loginForm').find('.secondForm').slideDown();

    // Create crp cookie
    setCookie("CRP", $(this).attr('id'), cookieTime);

  });

  // Username cookie
  username.on("change", function(e) {
    setCookie("username.email", username.val(), cookieTime);
  });

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
