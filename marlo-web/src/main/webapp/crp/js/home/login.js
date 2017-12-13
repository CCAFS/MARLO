$(document).ready(init);
var cookieTime,loginSwitch=false,email;
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

  //Next button click
  $("input#login_next").on('click',function(e){
    email =  $('input[name="login-email"]').val();
    if(!isEmail(email) || email==""){
      e.preventDefault();
      console.log("please enter an email");
    }else{
      if(!loginSwitch){
        e.preventDefault();
        loginSwitch=true;

        loadAvailableItems();
        //change height value to form
        $("#loginFormContainer .loginForm").addClass("max-size");
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
      }
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
    $(".loginForm #login-password").val("");

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

function loadAvailableItems(){
  $.ajax({
    url: baseUrl+"/crpByEmail.do",
    data: {
      userEmail: email
    },
    beforeSend: function() {},
    success: function(data) {
      console.log(data);
      $(".welcome-message-container .username span").text(data.user.name);

      var itemsList = $('.selection-bar-options ul')
      $.each(data.crps, function(i){
          var li = $('<li/>')
              .addClass("option")
              .attr({
                id:'crp-'+data.crps[i].acronym,
                role:'menuitem'
                })
              .appendTo(itemsList);
          var img = $('<img/>')
              .addClass('animated bounceIn')
              .attr({
                src: baseUrl + "/global/images/crps/" + data.crps[i].acronym + ".png",
                alt:"${element.name}",
                tabindex:"1"
                })
              .appendTo(li);
      });
      if(data.crps.length>1){
        //when user has access to multiple crps, show the side bar
        $(".crps-select").removeClass("hidden");

        //move crps select side bar
        var sideBarPosition=-$(".loginForm").position().left-120;
        $(".crps-select").css("left",sideBarPosition);
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