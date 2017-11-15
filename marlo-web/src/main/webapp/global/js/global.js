// Global Vars
var yesnoEvent;
var notyDefaultOptions = {
    text: '',
    layout: 'bottomRight',
    type: 'error',
    theme: 'relax',
    timeout: 6000,
    progressBar: true,
    animation: {
        open: 'animated bounceInRight',
        close: 'animated bounceOutRight'
    },
    closeWith: [
      'click'
    ]
};

/**
 * Global javascript must be here.
 */
$(document).ready(function() {
  showNotificationMessages();
  showHelpText();


  // Changes detected
  $('p.changesDetected strong').text($('.changedField').length);

  // Help text in each section
  $('.scrollup').click(function() {
    $("html, body").animate({
      scrollTop: 0
    }, 600);
    return false;
  });
  setViewMore();
  $('.viewMore').on("click", expandViewMoreBlock);

  $(".removeHelp").on("click", function() {
    $(this).parent().parent().fadeOut(function() {
      console.log(this);
      $(this).remove();
    });
  });

  // Tawkto Widget
  var $dragButton = $("#draggable-button");
  // hideDragButton();
  $dragButton.animateCss('flipInY');
  $dragButton.draggable();
  $dragButton.find('p').on('click', function() {
    Tawk_API.toggle();
  });

  Tawk_API.onChatMaximized = function() {
    $dragButton.fadeOut();
  };

  Tawk_API.onChatMinimized = function() {
    hideDragButton();
  };
  Tawk_API.onChatHidden = function() {
    hideDragButton();
  };

  function hideDragButton() {
    if(Tawk_API.isVisitorEngaged()) {
      $dragButton.find('.status').text('On-Going Chat');
    } else {
      $dragButton.find('.status').text('');
    }
    $dragButton.fadeIn();
  }

  // Save Button click (Loading state)
  $('.button-save, .button-save span').on('click', function(e) {
    // Validate if there ia a justification
    var justification = $("form:first").find("textarea.justification");
    if(justification.exists() && justification.val().trim().length == 0) {
      e.preventDefault();
      return

    }
    // Turn save button in saving button
    $(this).addClass('disabled animated flipInY');
    $(this).find('.glyphicon').hide();
    $(this).find('.saveText').html('Saving ... <img src="' + baseURL + '/images/global/loading_3.gif" />');
  });

  // Yes / No Event
  $('input.onoffswitch-radio').on('change', function(e) {
    yesnoEvent($(this));
  });

  // hash url animation
  if(window.location.hash) {
    if($(window.location.hash).exists && hashScroll) {
      $('html, body').animate({
        scrollTop: $(window.location.hash).offset().top - 110
      }, 1500);
    }
  }

  // Main Menu always visible
  if($('#mainMenu').exists()) {
    var mainMenuPosition = $('#mainMenu').position().top + 20;
    $(window).scroll(function() {
      if($(window).scrollTop() >= mainMenuPosition) {
        $('#mainMenu .menuContent').addClass('positionFixedTop');
      } else {
        $('#mainMenu .menuContent').removeClass('positionFixedTop');
      }
    });
  }
  
  // Phase tag visible
  if($('#phaseTag').exists()) {
    var phaseTagPos = $('#timelineScroll').parent().position().top + 20;
    
    $('#phaseTag').find('span').css({
      right: $(document).width() - ($('.phaseTag').offset().left + $('.phaseTag').width())
    });
    
    $(window).scroll(function() {
      if($(window).scrollTop() >= phaseTagPos) {
        $('#phaseTag span').fadeIn();
      } else {
        $('#phaseTag span').hide();
      }
    });
  }

  // Function that set the interface buttons always visible
  var $buttons = $('.buttons');
  if($buttons.exists()) {
    var menuOffset = function() {
      return $(document).height() - ($buttons.offset().top + $buttons.height());
    }
    $buttons.find('.buttons-content').css({
      right: $(document).width() - ($buttons.offset().left + $buttons.width())
    });

    setTimeout(function() {
      setFixedElement($(window).scrollBottom() >= menuOffset());
    }, 500);

    $(document).on('updateComponent', function() {
      setFixedElement($(window).scrollBottom() >= menuOffset());
    });

    $(window).scroll(function() {
      setFixedElement($(window).scrollBottom() >= menuOffset());
    });
  }

  function setFixedElement(isFixed) {
    if(isFixed) {
      $buttons.find('.buttons-content').addClass('positionFixedBot animated flipInX');
    } else {
      $buttons.find('.buttons-content').removeClass('positionFixedBot animated flipInX');
    }
  }

  // Animate help text
  function showHelpText() {
    $('.helpMessage').addClass('animated flipInX');
  }

  function showNotificationMessages() {
    var messageSelector = $('#generalMessages').find("#message");
    // VALIDATE IF IS ERROR O SUCCES CLASS
    if($(messageSelector).hasClass("success")) {
      // SUCCESS MESSAGE
      if(messageSelector.length == 1 && messageSelector.html().split(":")[0] === "message") {
        var message = $(messageSelector).html().split(":")[1];
        var messageType = "success";
        notifyErrorMessage(messageType, message);
      } else if(messageSelector.length == 1 && messageSelector.html().split(":")[0] === "draft") {
        // DRAFT MESSAGE
        var message = $(messageSelector).html().split(":")[1];
        var messageType = "success";
        notifyErrorMessage(messageType, message);
      } else if(messageSelector.length >= 1 && messageSelector.html().split(":")[0] != "message") {
        // WARNING MESSAGE
        var message = ""
        message += "The Information was correctly saved. <br> ";
        message += "Please keep in mind that the fields highlighted below are missing or incorrect.";
        var messageType = "warning";
        notifyErrorMessage(messageType, message);
      }
    } else if($(messageSelector).hasClass("error")) {
      // ERROR MESSAGE
      var message = $(messageSelector).html();
      var messageType = "error";
      notifyErrorMessage(messageType, message);
    }

  }

  function notifyErrorMessage(messageType,message) {
    $('#generalMessages').noty({
        theme: 'relax',
        layout: 'top',
        theme: 'relax', // or 'relax'
        type: messageType,
        text: message, // can be html or string
        dismissQueue: true, // If you want to use
        // queue feature set
        // this true
        animation: {
            open: 'animated flipInX', // Animate.css
            // class names
            close: 'animated flipInX' // Animate.css
        // class names
        },
        timeout: 10000, // delay for closing event.
        // Set false for sticky
        // notifications
        force: false, // adds notification to the
        // beginning of queue when set
        // to true
        modal: false,
        maxVisible: 5, // you can set max visible
        // notification for dismissQueue
        // true option,
        killer: false, // for close all notifications
        // before show
        closeWith: [
          'click'
        ]

    });
  }

  /* Tooltips with JQuery UI */
  $(this).tooltip({
      track: true,
      content: function() {
        return $(this).attr('title');
      }
  });

  yesnoEvent = function(target) {
    // var isChecked = $(this).is(':checked');
    var $t = $(target);
    var isChecked = ($t.val() === "true");
    $t.siblings().removeClass('radio-checked');
    $t.next().addClass('radio-checked');
    var array = $t.attr('name').split('.');
    var $aditional = $('#aditional-' + array[array.length - 1]);
    if($t.hasClass('inverse')) {
      isChecked = !isChecked;
    }
    if(isChecked) {
      $aditional.slideDown("slow");
    } else {
      $aditional.slideUp("slow");
      $aditional.find('input:text,textarea').val('');
    }
  }

  /**
   * Tick Box functions
   */
  var $tickBoxWp = $('.tickBox-wrapper input[type=checkbox]');

  $tickBoxWp.on('change', toggleInputs);
  $tickBoxWp.each(function() {
    var $toggle = $(this).closest('.tickBox-wrapper').find('.tickBox-toggle');
    if($(this).is(':checked')) {
      $toggle.show();
    } else {
      $toggle.hide();
    }
  });

  function toggleInputs(e) {
    $(this).parent().parent().parent().find('.tickBox-toggle').slideToggle($(e.target).is(':checked'));
  }

  // History log pop up
  $('.button-history').on('click', function() {

    $('#log-history').dialog({
        modal: true,
        maxWidth: 500,
        closeText: "",
        width: '70%',
        buttons: {
          Cancel: function() {
            $(this).dialog("close");
          }
        }
    });

  });

  // Datatables language
  if($.fn.dataTable) {
    $.extend(true, $.fn.dataTable.defaults, {
      "language": {
        "infoFiltered": "(filtered from a total of _MAX_ entries)"
      }
    });
  }

  // Cancel button
  $('#cancelButton').on('click', function() {
    $('button[name="cancel"]').trigger('click');
  });

  // Set autogrow
  $("textarea[id!='justification']").autoGrow();

});

function isReportingCycle() {
  return false;
}

setWordCounterToInputs('limitWords');
/* Set word counter to inputs list where cssName could be limitWords */
function setWordCounterToInputs(cssName) {
  // Attribute contains certain value somewhere -> [class*="limitWords"]
  var check = cssName + "-";
  $('input[type=text][class*="' + cssName + '"], textarea[class*="' + cssName + '"]').each(function(i,input) {
    var className = $(input).attr('class') || '';
    var cls = $.map(className.split(' '), function(val,i) {
      if(val.indexOf(check) > -1) {
        return val.slice(check.length, val.length);
      }
    });
    applyWordCounter($(input), (cls.join(' ')) || 100);
  });
}

/** secondaryMenu * */

$('.selectedProgram, selectedProject').on('click', function() {
  $(this).parent().next().slideToggle('slow');
});

// event to inputs in login form
$('input[name="user.email"] , input[name="user.password"]').on("keypress", function(event) {
  if(event.keyCode === 10 || event.keyCode === 13) {
    event.submit();
  }
});

/* prevent enter key to inputs */

$('input').on("keypress", function(event) {

  if(event.keyCode === 10 || event.keyCode === 13) {
    event.preventDefault();
  }

});

function setViewMore() {
  var element = $('.helpText');
  if($(element).height() < 100) {
    $(element).find('.viewMore').hide();
  } else {
    $(element).css({
      "height": 65
    })
    $(element).find('.viewMore').html('View More');
    $(element).find('.viewMore').show();
  }
}

function expandViewMoreBlock() {
  if($(this).hasClass("closed")) {
    $(this).parent().css({
      height: $(this).parent().find('.helpMessage').height() + $(this).height() + 5
    });
    $(this).css("border-bottom", "none");
    $(this).html('View less');
    $(this).addClass("opened");
    $(this).removeClass("closed");
  } else if($(this).hasClass("opened")) {
    $(this).parent().css({
      height: 70
    });
    $(this).css("border-bottom", "1px solid #f2f2f2");
    $(this).html('View More');
    $(this).addClass("closed");
    $(this).removeClass("opened");
  }

}

/**
 * Justification Status Functions
 */

function isStatusCancelled(statusId) {
  return(statusId == "5")
}

function isStatusComplete(statusId) {
  return(statusId == "3")
}

function isStatusExtended(statusId) {
  return(statusId == "4")
}

function isStatusOnGoing(statusId) {
  return(statusId == "2")
}

/**
 * Search institutions functions
 */

var placeholderText = 'Search the organization here...'
var searchInstitutionsOptions = function(includePPA) {
  return searchInstitutionsOptionsData({
      includePPA: includePPA,
      projectPreSetting: projectPreSetting
  });
}

var searchInstitutionsOptionsData = function(optionsData) {
  return {
      ajax: {
          url: baseURL + '/searchInstitutions.do',
          dataType: 'json',
          delay: 350,
          data: function(params) {
            return {
                q: params.term || '', // search term
                withPPA: optionsData.includePPA ? 1 : 0,
                onlyPPA: optionsData.projectPreSetting
            };
          },
          processResults: function(data,params) {
            return {
              results: data.institutions,
            };
          }
      },
      escapeMarkup: function(markup) {
        return markup;
      }, // let our custom formatter work
      minimumInputLength: 0,
      templateResult: formatRepo,
      templateSelection: formatRepoSelection,
      placeholder: placeholderText,
      width: '100%'
  };
}

function formatRepo(repo) {
  if(repo.loading) {
    return repo.text;
  }
  var markup = "";
  markup += "<div class='select2-result-repository clearfix'>";

  // Is PPA
  markup += "<small class='pull-right'>";
  if(repo.isPPA) {
    markup += "<span class='label label-warning'>Managing / PPA Partner</span>"
  } else {
    markup += "<span class='label label-default'>Partner</span>";
  }
  markup += "</small>";
  // Acronym & Title
  if(repo.acronym) {
    markup += "<strong>" + repo.acronym + "</strong> - " + repo.name;
  } else {
    markup += "<strong>" + repo.name + "</strong>";
  }

  // Partner type
  markup += "<br>";
  markup += "<small> <i>" + repo.type + " </i></small> ";
  markup += "</div>";
  return markup;
}

function formatRepoSelection(repo) {
  return repo.composedName;
}

function notificationError(message) {
  var notyOptions = jQuery.extend({}, notyDefaultOptions);
  notyOptions.text = message;
  noty(notyOptions);
}
