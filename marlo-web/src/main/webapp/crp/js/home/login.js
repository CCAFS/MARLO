$(document).ready(init);
var cookieTime,loginSwitch=false;
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
  $('.selection-bar-options ul li.enabled').on('click', function() {
    var selectedImageAcronym = $(this).attr('id').split('-')[1];
    loadSelectedImage(selectedImageAcronym);
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

  //Add active class to available options
  $(".selection-bar-options ul li").on('click',function(){
    $(".selection-bar-options ul li").removeClass('active');
    $(this).addClass('active');
  });

  $("input#login_next").on('click',function(e){
    if(!loginSwitch){
      e.preventDefault();
      loginSwitch=true;
    }
    //change height value to form
    $("#loginFormContainer .loginForm").css("height","400px");
    //Hide email input
    $(".loginForm #login-email").css("display","none");
    //show crp image
    $(".loginForm .form-group").css("display","");
    //show welcome message
    $(".loginForm .welcome-message-container").css("display","");
    //show input password
    $(".loginForm #login-password").css("display","");
    //set focus on password input
    $(".loginForm #login-password input").focus();
    //Change button value to Login
    $(this).val("Login");
    //when user has access to multiple crps, show the side bar
    $(".crps-select").css("display","");

    var sideBarPosition=-$(".loginForm").position().left-50;
    //move crps select side bar
    $(".crps-select").css("left",sideBarPosition);
  });

  //Scroll control
  $(".crps-select").on('mouseover',function(){
    $('html, body').disableScroll();
  });

  $(".crps-select").on('mouseleave',function(){
    $('html, body').enableScroll();
  });

  //Accessible enter click to login
  $(".loginForm .login-input").keyup(function(event) {
    if (event.keyCode === 13) {
        $("input#login_next").click();
    }
  });

  //Accessible enter click to select crp,center or platform
  $(".selection-bar-options ul li").keyup(function(event) {
    if (event.keyCode === 13) {
        $(this).click();
    }
  });

  $(".loginForm .login-input-container.username").on('click',function(){
    loginSwitch=false;
    $(".loginForm #login-password").val("");
    //when user has access to multiple crps, show the side bar
    $(".crps-select").css("display","none");
    //change height value to form
    $("#loginFormContainer .loginForm").css("height","200px");
    //Hide email input
    $(".loginForm #login-email").css("display","");
    //show crp image
    $(".loginForm .form-group").css("display","none");
    //show welcome message
    $(".loginForm .welcome-message-container").css("display","none");
    //show input password
    $(".loginForm #login-password").css("display","none");
    //Change button value to Next
    $("input#login_next").val("Next");
  });
}

function setCRP(crpSelected) {
  var $li = $("li#crp-" + crpSelected);

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

function loadSelectedImage(selectedImageAcronym){
  $("#crpSelectedImage").attr("src", baseUrl + "/global/images/crps/" + selectedImageAcronym + ".png");
}

//Disable and enable scroll on page
$.fn.disableScroll = function() {
  window.oldScrollPos = $(window).scrollTop();

$(window).on('scroll.scrolldisabler',function ( event ) {
  $(window).scrollTop( window.oldScrollPos );
    event.preventDefault();
  });
};

$.fn.enableScroll = function() {
  $(window).off('scroll.scrolldisabler');
};