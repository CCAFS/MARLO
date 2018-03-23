$(document).ready(init);
var cookieTime,isSecondForm=false, hasAccess=false;
var username = $("input[name='user.email']");
var inputPassword= $("input[name='user.password']");
var crpSession="";


function init() {
  initJreject();

  cookieTime = 100;

  setCrpSession();

  var input = $('.form-control');

  input.on('focus', function (ev) {
    input.parent().addClass('is-focused');
  });

  input.on('blur', function (ev) {
    input.parent().removeClass('is-focused');
  });

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
    setCRPCookie();
  });

  // Username cookie and Hide the wrong data message
  username.on("change", function(e) {
    setCookie("username.email", username.val(), cookieTime);

    $('input.login-input').removeClass("wrongData");
    $('.loginForm p.invalidField').addClass("hidden");
  });

  // Password - Hide the wrong data message
  inputPassword.on("change", function(e) {
    $('input.login-input').removeClass("wrongData");
    $('.loginForm p.invalidField').addClass("hidden");
  });

  //Set focus on email input on page load
  $(".loginForm #login-email .user-email").focus();

  //Add active class to available options
  $(".selection-bar-options ul li").on('click',function(){
    $(".selection-bar-options ul li").removeClass('active');
    $(this).addClass('active');
  });

  //Form button click
  $("input#login_next").on('click',function(e){
    e.preventDefault();
    $('input.login-input').removeClass("wrongData");
    var email = username.val();
    /*|| !isEmail(email) if you want to check if isEmail*/
    if(email == "" ){
      wrongData("invalidEmail");
    }else if(!isSecondForm){
      loadAvailableItems(email);
    }else if(inputPassword.val()==""){
      wrongData("voidPassword");
    }else{
      checkPassword(email,inputPassword.val());
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

  $('input#terms').on('change', function(){
    console.log($('input#terms').is(':checked'));
  });

}

function firstForm(){
  isSecondForm=false;
  hasAccess=false;

  //Hide the invalidEmail field
  $('.loginForm p.invalidField').addClass("hidden");
  //Remove wrongData class to input field
  $('input.login-input').removeClass("wrongData");

  //Reset input password
  $(".loginForm #login-password .user-password").val("");

  //Hide the center crp image, the welcome message, and the input password (in order)
  $(".crps-select, .loginForm .form-group," +
      " .loginForm .welcome-message-container, " +
      ".loginForm #login-password").addClass("hidden");

  //Hide terms and conditions checkbox
  $('.terms-container').addClass("hidden");

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

  //Hide terms and conditions checkbox
  $('.loginForm').removeClass("terms-check");

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

function verifyCrpCookie(){
  var crpInput = $('input#crp-input').val();

  // Verify "crp"
  if(verifyCookie("CRP") && (getCookie("CRP") != "undefined")) {
    var crpSelected = getCookie("CRP");
    return crpSelected;
  }
  return false;
}

function setCrpSession(){
  var availableList=['A4NH','CCAFS','FTA','Livestock','Maize','PIM','WLE','Wheat','CIAT','BigData'];
  var path = window.location.pathname.split("/");
  $.each(path, function(i){
    $.each(availableList,function(j){
      if(path[i]==availableList[j]){
        crpSession = path[i];
      }
    });
  });
}

function setCRPCookie() {
  var crpInput = $('input#crp-input').val();
  // Create crp cookie
  setCookie("CRP", crpInput, cookieTime);
}

function loadAvailableItems(email){
  $.ajax({
    url: baseUrl+"/crpByEmail.do",
    data: {
      userEmail: email
    },
    beforeSend: function() {
      $("input#login_next").addClass("login-loadingBlock");
      $("input#login_next").attr("disabled",true);
      $("input#login_next").val("");
    },
    success: function(data) {
      if(data.user == null){
        wrongData("emailNotFound");
        $("input#login_next").val("Next");
      }else{
        var crpCookie=verifyCrpCookie();
        $('.selection-bar-options ul #crp-'+data.crps[0].acronym).click();
        /*if(crpCookie){
          $('.selection-bar-options ul #crp-'+data.crps[0].acronym).click();
        }*/
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
          if(crpCookie==data.crps[i].acronym){
            $('.selection-bar-options ul #crp-'+data.crps[i].acronym).click();
          }
        });
        if(!data.user.agree){
          showTermsCheckbox();
        }
        //If user has access to the crpSession or crpSession is void, change form style
        if(hasAccess || crpSession==""){
          secondForm(data);
        }else{
          wrongData("deniedAccess");
          $("input#login_next").val("Next");
        }
      }
    },
    complete: function(data) {
      $("input#login_next").removeClass("login-loadingBlock");
      $("input#login_next").attr("disabled",false);
    },
    error: function(data) {}
  });
}

function secondForm(data){
  //Button control, just send the form when is in the second part of the form
  isSecondForm=true;

  //Hide the invalidEmail field
  $('.loginForm p.invalidField').addClass("hidden");
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
    }else{
      //click unique crp loaded
      $('.selection-bar-options ul #crp-'+data.crps[0].acronym).click();
    }
  }
}

function checkPassword(email,password){
  $.ajax({
    url: baseUrl+"/validateUser.do",
    data: {
      userEmail: email,
      userPassword: password,
      agree: $('input#terms').is(':checked')
    },
    beforeSend: function() {
      if($('input#terms').is(':checked')){
        $("input#login_next").addClass("login-loadingBlock");
        $("input#login_next").attr("disabled",true);
        $("input#login_next").val("");
      }
    },
    success: function(data) {
      if(!data.userFound.loginSuccess){
        if(data.messageEror=="Invalid CGIAR email or password, please try again"){
          wrongData("incorrectPassword");
        }/*else if(crpSession!=""){
          wrongData("deniedAccess",data.messageEror);
        }*/else{
          wrongData("incorrectPassword",data.messageEror);
        }
        $("input#login_next").removeClass("login-loadingBlock");
        $("input#login_next").attr("disabled",false);
        $("input#login_next").val("Login");
      }else{
        $("input#login_formSubmit").click();
      }
    },
    complete: function(data) {},
    error: function(data) {}
  });
}

function wrongData(type,customMessage){
  $('input.login-input').addClass("wrongData");
  if(customMessage != null){
    $('.loginForm p.invalidField.'+type).text(customMessage);
    $('.loginForm p.invalidField.'+type).removeClass("hidden");
  }else{
    $('.loginForm p.invalidField.'+type).removeClass("hidden");
  }
  if(type=="voidPassword" || type=="incorrectPassword"){
    inputPassword.focus();
  }else{
    username.focus();
  }
}

function setCookie(cname,cvalue,mins) {
  var d = new Date();
  d.setTime(d.getTime() + (mins * 60 * 1000));
  var expires = "expires=" + d.toUTCString();
  document.cookie = cname + "=" + cvalue + "; " + expires;
}

function showTermsCheckbox(){
  //Show terms and conditions checkbox
  $('.terms-container').removeClass("hidden");

  $('.loginForm').addClass("terms-check");
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