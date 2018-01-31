$(document).ready(init);
var cookieTime,loginSwitch=false, hasAccess=false;
var username = $("input[name='user.email']");
var crpSession = $(".loginForm #crp-input").val();
function init() {
  initJreject();

  cookieTime = 100;
  var crpInput = $('input#crp-input').val();

  // Verify "crp"
  if(verifyCookie("CRP") && (getCookie("CRP") != "undefined") && (!crpInput)) {
    var crpSelected = getCookie("CRP");
    setCRP(crpSelected);
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

    $("input#crp-input").val(selectedImageAcronym);
  });

  // Username cookie and Hide the wrong data message
  username.on("change", function(e) {
    setCookie("username.email", username.val(), cookieTime);

    $('input.login-input').removeClass("wrongData");
    $('.loginForm p.invalidEmail').addClass("hidden");
  });

  //Set focus on email input on page load
  $(".loginForm #login-email .user-email").focus();

  //Add active class to available options
  $(".selection-bar-options ul li").on('click',function(){
    $(".selection-bar-options ul li").removeClass('active');
    $(this).addClass('active');
  });

  //Next button click
  $("input#login_next").on('click',function(e){
    var email = username.val();

    if(email == "" || !isEmail(email)){
      e.preventDefault();
      wrongData("invalid");
    }else if(!loginSwitch){
      e.preventDefault();
      loadAvailableItems(email);
    }
  });

  //Scroll control
  $(".crps-select").on('mouseover',function(){
    $('html, body').disableScroll();
  });

  $(".crps-select").on('mouseleave',function(){
    $('html, body').enableScroll();
  });

  //Accessible "Enter" (keyCode==13) click to login
  $(".loginForm .login-input").keyup(function(event) {
    if (event.keyCode === 13) {
        $("input#login_next").click();
    }
  });

  //Accessible "Enter" (keyCode==13) click to select crp,center or platform
  $(".selection-bar-options ul li").keyup(function(event) {
    if (event.keyCode === 13) {
        $(this).click();
    }
  });

  //Return to the first form
  $(".loginForm .login-input-container.username").on('click',function(){
    firstForm();
  });

  $('.login-back-container p.loginBack').on('click',function(){
    $(".loginForm .login-input-container.username").click();
  });
}

function firstForm(){
  loginSwitch=false;
  hasAccess=false;

  //Reset input password
  $(".loginForm #login-password .user-password").val("");

  //Hide the center crp image, the welcome message, and the input password (in order)
  $(".crps-select, .loginForm .form-group," +
      " .loginForm .welcome-message-container, " +
      ".loginForm #login-password").addClass("hidden");

  //Hide the "login with different user" button
  $('.login-back-container').addClass('hidden');

  //Hide the labels (CRPs,Centers and Platforms)
  $(".crps-select .name-type-container").addClass("hidden");

  //Hide the crps-centers-platforms images or acronyms in selection bar
  $('.selection-bar-options ul .selection-bar-image,'+
      '.selection-bar-options ul .selection-bar-acronym').addClass("hidden");

  //Change height value according to the first form
  $("#loginFormContainer .loginForm").removeClass("max-size");

  //Show email input
  $(".loginForm #login-email").removeClass("hidden");

  //Change button value to Next
  $("input#login_next").val("Next");

  //Reset the crp-input to init value (to preserve the crpSession in 401.ftl)
  $(".loginForm #crp-input").val(crpSession);
}

function setCRP(crpSelected) {
  // Setting up the CRP-CENTER-PLATFORM value into a hidden input
  $('#crp-input').val(crpSelected);

  // Change central image according to selected CRP-CENTER-PLATFORM
  $("#crpSelectedImage").attr("src", baseUrl + "/global/images/crps/" + crpSelected + ".png");

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

function loadAvailableItems(email){
  $.ajax({
    url: baseUrl+"/crpByEmail.do",
    data: {
      userEmail: email
    },
    beforeSend: function() {},
    success: function(data) {
      $.each(data.crps, function(i){
        if(crpSession == data.crps[i].acronym){
          hasAccess=true;
        }
        $(".crps-select .name-type-container.type-"+data.crps[i].type).removeClass("hidden");
        if(data.crps.length<7){
          $('.selection-bar-options ul #crp-'+data.crps[i].acronym+' .selection-bar-image').removeClass("hidden");
        }else{
          $('.selection-bar-options ul #crp-'+data.crps[i].acronym+' .selection-bar-acronym').removeClass("hidden");
        }
      });
      if(data.user == null){
        wrongData("notFound");
      }else{
        //If user has access to the crpSession or crpSession is void, change form style
        if(crpSession == '' || hasAccess){
          secondForm(data);
        }else{
          wrongData("deniedAccess");
        }
      }
    },
    complete: function(data) {},
    error: function(data) {}
  });
}

function secondForm(data){
  //Button control, just send the form when button value is "login", not "next"
  loginSwitch=true;

  //Hide the invalidEmail field
  $('.loginForm p.invalidEmail').addClass("hidden");
  //Remove wrongData class to input field
  $('input.login-input').removeClass("wrongData");

  //Show username in form
  $(".welcome-message-container .username span").text(data.user.name);

  //Change height value to second Form
  $("#loginFormContainer .loginForm:not(.instructions)").addClass("max-size");

  //Hide email input
  $(".loginForm #login-email").addClass("hidden");

  //show crp image, welcome message and input password
  $(".loginForm .form-group, " +
      ".loginForm .welcome-message-container, " +
      ".loginForm #login-password").removeClass("hidden");

  //Change button value to Login
  $("input#login_next").val("Login");

  //set focus on password input
  $(".loginForm #login-password input").focus();

  //show the back to email button
  $('.login-back-container').removeClass('hidden');

  if(crpSession != ''){

    if(!hasAccess){
      wrongData("deniedAccess");
    }else{
      $('.selection-bar-options ul #crp-'+crpSession).click();
    }

  }else{

    //when user has access to multiple crps, show the side bar
    if(data.crps.length>1){
      $(".crps-select").removeClass("hidden");
      //move crps select side bar
      var sideBarPosition=-$(".loginForm").position().left-120;
      $(".crps-select").css("left",sideBarPosition);
    }

    //click to the first crps loaded
    //$('.selection-bar-options ul #crp-'+data.crps[0].acronym).click();
  }
}

function wrongData(type){
  $('input.login-input').addClass("wrongData");
  $('.loginForm p.invalidEmail.'+type).removeClass("hidden");
  $(".loginForm #login-email .user-email").focus();
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