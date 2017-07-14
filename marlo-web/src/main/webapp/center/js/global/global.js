$(document).ready(init);
var forceChange;
function init() {
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
}
var yesnoEvent;
var notyDefaultOptions = {
    text: '',
    layout: 'bottomRight',
    type: 'error',
    theme: 'relax',
    timeout: 5000,
    animation: {
        open: 'animated bounceInRight',
        close: 'animated bounceOutRight'
    },
    closeWith: [
      'click'
    ]
};

// Global javascript must be here.
$(document)
    .ready(
        function() {
          showNotificationMessages();
          showHelpText();

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
                var message =
                    "Information was correctly saved. <br> Please keep in mind the highlighted fields below are missing or incorrect.";
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
            $t = $(target);
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
                maxWidth: '500px',
                width: '70%',
                buttons: {
                  Cancel: function() {
                    $(this).dialog("close");
                  }
                }
            });
          });

          // Cancel button
          $('#cancelButton').on('click', function() {
            $('button[name="cancel"]').trigger('click');
          });

          // Set autogrow
          $("textarea[id!='justification']").autoGrow();

          // Generating hash from form information
          setFormHash();

        });

function isReportingCycle() {
  return false;
}

/**
 * Validate fields length when click to any button
 */
function validateEvent(fields) {
  var $justification = $('#justification');
  var $parent = $justification.parent().parent();
  var errorClass = 'fieldError';
  $parent.prepend('<div class="loading" style="display:none"></div>');
  $('[name=save], [name=next]').on('click', function(e) {
    $parent.find('.loading').fadeIn();
    var isNext = (e.target.name == 'next');
    $justification.removeClass(errorClass);
    /*
     * var fieldErrors = $(document).find('input.fieldError, textarea.fieldError').length; if(fieldErrors != 0) {
     * e.preventDefault(); $parent.find('.loading').fadeOut(500); var notyOptions = jQuery.extend({},
     * notyDefaultOptions); $('html, body').animate({ scrollTop: $('.fieldError').offset().top - 80 }, 700);
     * notyOptions.text = 'Something is wrong in this section, please fix it then save'; noty(notyOptions); } else {
     */
    if(!isChanged() && !forceChange && !isNext) {
      // If there isn't any changes
      e.preventDefault();
      $parent.find('.loading').fadeOut(500);
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'Nothing has changed';
      notyOptions.type = 'alert';
      noty(notyOptions);
    } else {
      if(errorMessages.length != 0) {
        // If there is an error message
        e.preventDefault();
        $parent.find('.loading').fadeOut(500);
        var notyOptions = jQuery.extend({}, notyDefaultOptions);
        notyOptions.text = errorMessages.join();
        noty(notyOptions);
      } else if(!validateField($('#justification')) && (isChanged() || forceChange)) {
        // If field is not valid
        e.preventDefault();
        $parent.find('.loading').fadeOut(500);
        $justification.addClass(errorClass);
        var notyOptions = jQuery.extend({}, notyDefaultOptions);
        notyOptions.text = 'The justification field needs to be filled';
        noty(notyOptions);
      }
    }
    // }

  });

  // Force change when an file input is changed
  $("input:file").on('change', function() {
    forceChange = true;
  });
}

function isChanged() {
  return (formBefore != getFormHash()) || forceChange;
}

function setFormHash() {
  formBefore = getFormHash();
}

function getFormHash() {
  return getHash($('form [id!="justification"]').serialize());
}

function getHash(str) {
  var hash = 0, i, chr, len;
  if(str.length == 0) {
    return hash;
  }
  for(i = 0, len = str.length; i < len; i++) {
    chr = str.charCodeAt(i);
    hash = ((hash << 5) - hash) + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return hash;
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

setCharCounterToInputs('limitChar');
/* Set character counter to inputs list where cssName could be limitChar */
function setCharCounterToInputs(cssName) {
  // Attribute contains certain value somewhere -> [class*="limitChar"]
  var check = cssName + "-";
  $('input[type=text][class*="' + cssName + '"], textarea[class*="' + cssName + '"]').each(function(i,input) {
    var className = $(input).attr('class') || '';
    var cls = $.map(className.split(' '), function(val,i) {
      if(val.indexOf(check) > -1) {
        return val.slice(check.length, val.length);
      }
    });
    applyCharCounter($(input), (cls.join(' ')) || 100);
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

/* Justification Status Functions */

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