$(document).ready(init);
var cookieTime,loginSwitch=false,crpPreselected;
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

    $("input#crp-input").val(selectedImageAcronym);
  });

  // On select a Type
  $('.nav-tabs> li').on('click', function() {
    var typeSelected = $(this).attr('id');
    // Set Type
    setType(typeSelected);
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

  //Set focus on email input on page load
  $(".loginForm #login-email .user-email").focus();

  //Add active class to available options
  $(".selection-bar-options ul li").on('click',function(){
    $(".selection-bar-options ul li").removeClass('active');
    $(this).addClass('active');
  });

  //Next button click
  $("input#login_next").on('click',function(e){
    if($(".loginForm #login-email .user-email").val() == ""){
      e.preventDefault();
      $('input.login-input').addClass("wrongData");
      $('.loginForm p.invalidEmail').removeClass("hidden");
      $('.loginForm p.invalidEmail').text("Please enter a valid email");
      $(".loginForm #login-email .user-email").focus();
    }else if(!isEmail($(".loginForm #login-email .user-email").val())){
      e.preventDefault();
      $('input.login-input').addClass("wrongData");
      $('.loginForm p.invalidEmail').removeClass("hidden");
      $('.loginForm p.invalidEmail').text("Please enter a valid email");
      $(".loginForm #login-email .user-email").focus();
    }else if(!loginSwitch){
      e.preventDefault();
      loadAvailableItems(username.val());
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
    loginSwitch=false;
    $(".loginForm #login-password .user-password").val("");

    //hide the side bar, the crp image, the welcome message and the input password
    $(".crps-select, .loginForm .form-group," +
    		" .loginForm .welcome-message-container, " +
    		".loginForm #login-password").addClass("hidden");
    //change height value to form
    $("#loginFormContainer .loginForm").removeClass("max-size");
    //Hide email input
    $(".loginForm #login-email").removeClass("hidden");
    //Change button value to Next
    $("input#login_next").val("Next");

    //show the back to email button
    $('.login-back-container').addClass('hidden');
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

function loadAvailableItems(email){
  $.ajax({
    url: baseUrl+"/crpByEmail.do",
    data: {
      userEmail: email
    },
    beforeSend: function() {
      //If url has a crp preselected (when is redirected to login '401.ftl'), save that crp in crpPreselected variable
      if($(".loginForm #crp-input").val()){ crpPreselected=$(".loginForm #crp-input").val(); }
    },
    success: function(data) {
      var hasAccess=false;

      if(data.user == null){
        $('input[name="user.email"]').focus();
        $('input.login-input').addClass("wrongData");
        $('.loginForm p.invalidEmail').removeClass("hidden");
        $('.loginForm p.invalidEmail').text("Your email was not found in database");
        $('input[name="user.email"]').on('change',function(){
          $('input.login-input').removeClass("wrongData");
          $('.loginForm p.invalidEmail').addClass("hidden");
        });
      }/*else if(crpPreselected){
        $.each(data.crps, function(i){
          if(data.crps[i].acronym == crpPreselected){
            hasAccess=true;
          }
        });
        if(hasAccess){
          //Change form style
          changeFormStyle(data,true);
        }else{
          $('.loginForm p.invalidEmail').removeClass("hidden");
          $('.loginForm p.invalidEmail').text("You don't have access to this crp");
        }
      }*/else{
        //Change form style
        changeFormStyle(data,false);
      }

    },
    complete: function(data) {},
    error: function(data) {}
  });
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

function changeFormStyle(data,preselected){
  //Hide the invalidEmail field
  $('.loginForm p.invalidEmail').addClass("hidden");
  //Remove wrongData class to input field
  $('input.login-input').removeClass("wrongData");

  $(".welcome-message-container .username span").text(data.user.name);

  $.each(data.crps, function(i){
    if(data.crps.length<7){
      $('.selection-bar-options ul #crp-'+data.crps[i].acronym).removeClass("hidden");
    }else{

    }
  });

  //change height value to form
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

  //when user has access to multiple crps, show the side bar
  if(data.crps.length>1 && !preselected){
    $(".crps-select").removeClass("hidden");

    //move crps select side bar
    var sideBarPosition=-$(".loginForm").position().left-120;
    $(".crps-select").css("left",sideBarPosition);
  }
  //click to the first crps loaded
  $('.selection-bar-options ul #crp-'+data.crps[0].acronym).click();

  //show the back to email button
  $('.login-back-container').removeClass('hidden');

  $('.login-back-container p.loginBack').on('click',function(){
    $(".loginForm .login-input-container.username").click();
  });

  //Button control, just send the form when button value is "login", not "next"
  loginSwitch=true;
}