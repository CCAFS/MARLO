/** * Handle jQuery plugin naming conflict between jQuery UI and Bootstrap ** */
$.widget.bridge('uibutton', $.ui.button);
$.widget.bridge('uitooltip', $.ui.tooltip);

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

  // Set elementsListComponent
  setElementsListComponent();

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

  // View More link button
  $('#helpViewMoreLink').on("click", expandViewMoreLink);

  $('.primarySelect').on("change", function(){
    $($(this).parents('.panel-body')).find(".radio-input").each(function(i,e) {
    if ($(this).parents('.panel-body').find('.list').find('li').length == 1) {
      $(e).attr('checked',true);
    }
    });
  });

  // View More link button
  $('.in-radio-list').on("click", function (){
    $list = $(this).parents('.list');
    $($list ).find(".radio-input").each(function(i,e) {
      // console.log(e.value);
      $(e).val('false');
      $(e).removeAttr('checked')
        // var option =
        //     $("#regionSelect").find(
        //         "option[value='" + $(e).find("input.rId").val() + "-" + $(e).find("input.regionScope").val() + "']");
        // option.prop('disabled', true);
      });

      $(this).parents('.radioFlat').find('.radio-input').val('true');

      $(this).parents('.radioFlat').find('.radio-input').attr('checked');
  });

  $(".removeHelp").on("click", function() {
    $(this).parent().parent().fadeOut(function() {
      console.log(this);
      $(this).remove();
    });
  });

  // Save Button click (Loading state)
  $('button[name=save]').on('click', function(e) {
    if($(this).hasClass('disabled')) {
      e.preventDefault();
      return;
    }
    // Validate if there ia a justification
    var justification = $("form:first").find("textarea.justification");
    if(justification.exists() && justification.val().trim().length == 0) {
      e.preventDefault();
      return;
    }

    // Turn save button in saving button
    turnSavingStateOn(this);

    // Cancel Auto Save
    autoSaveActive = false;

  });

  // Yes / No Event
  $('input.onoffswitch-radio').on('change', function(e) {
    // yesnoEvent($(this));
  });

  // hash url animation
  if(window.location.hash) {
    if($(window.location.hash).exists() && hashScroll) {
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
    $('.helpMessage').show();
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
      },
      position: {
          my: "left+15 center",
          at: "right center"
      }
  });

  /* ADD TITLE TOOLTIP TO ALL REQUIRED SIGN */
  $(".requiredTag").attr('title', 'This is a required field');

  yesnoEvent = function(target) {

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

  $('form .allowTextEditor').setTrumbowyg();

  $('.decodeHTML').each(function(i,e) {
    $(this).html($(this).text());
  });

  // Accessible enter click when button is focus
  $("input[type='submit']").keyup(function(event) {
    if(event.keyCode === 13) {
      $(this).click();
    }
  });

  // Remove concurrency block
  $('.removeConcurrenceBlock').on('click', function(e) {
    e.preventDefault();
    $('#concurrenceBlock').fadeOut();
  });

});

$(document).ajaxError(function(event,jqxhr,settings,exception) {
  if(production && (jqxhr.status == 500)) {
    var slackMessage = {
        "text": "MARLO Ajax Exception",
        "attachments": [
          {
              "color": "#e74c3c",
              "author_name": $('.login-input-container.username span').text(),
              "text": jqxhr.status + " - " + jqxhr.statusText,
              "fields": [
                  {
                      "title": "Section URL",
                      "value": window.location.href,
                      "short": true
                  }, {
                      "title": "User Name",
                      "value": currentUserName,
                      "short": true
                  }
              ],
              "footer": settings.url,
          }
        ]
    };
    postMessageToSlack(JSON.stringify(slackMessage));
  }
});

jQuery.fn.setTrumbowyg = function() {
  var $editor = $(this);
  if($.fn.trumbowyg) {
    $editor.trumbowyg({
        btns: [
          [
            'link', 'strong', 'em', 
          ]
        ],
        plugins: {
          allowTagsFromPaste: {
            allowedTags: ['a', 'br', 'b', 'strong', 'i', 'em']
          }
        },
        urlProtocol: true,
        autogrow: true,
        minimalLinks: true,
        semantic: true
    });

    $editor.trumbowyg().on('tbwpaste', function() {
      console.log('tbwpaste !');
    });
  }
};

function turnSavingStateOn(button) {
  $(button).addClass('disabled animated flipInY');
  $(button).find('.glyphicon').hide();
  $(button).find('.saveText').html('Saving ... <img src="' + baseURL + '/global/images/loading_3.gif" />');
}

function turnSavingStateOff(button) {
  $(button).removeClass('disabled animated flipInY');
  $(button).find('.glyphicon').show();
  $(button).find('.saveText').html('Save');
}

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
/*
 * $('input[name="user.email"] , input[name="user.password"]').on("keypress", function(event) { if(event.keyCode === 10 ||
 * event.keyCode === 13) { event.submit(); } });
 */

/* prevent enter key to inputs */

$('input').on("keypress", function(event) {

  if(event.keyCode === 10 || event.keyCode === 13) {
    event.preventDefault();
  }

});

function setViewMore() {
  var element = $('.helpText');
  if($('.helpMessage').height() < 100) {
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

function expandViewMoreLink() {
  if($(this).hasClass("viewMoreLinkclosed")) {
    $(this).html('View Less');
    $(this).addClass("viewMoreLinkopened");
    $(this).removeClass("viewMoreLinkclosed");
  } else if($(this).hasClass("viewMoreLinkopened")) {
    $(this).html('View More');
    $(this).addClass("viewMoreLinkclosed");
    $(this).removeClass("viewMoreLinkopened");
  }
}

/**
 * Justification Status Functions
 */

function isStatusCancelled(statusId) {
  return(statusId == "5");
}

function isStatusComplete(statusId) {
  return(statusId == "3");
}

function isStatusExtended(statusId) {
  return(statusId == "4");
}

function isStatusOnGoing(statusId) {
  return(statusId == "2");
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
                onlyPPA: optionsData.projectPreSetting,
                phaseID: phaseID
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

/**
 * One to Many component
 * 
 * @description elementsListComponent function to the functioning of the customForm macro
 */
function setElementsListComponent() {
  // Settings
  $('select[class*="elementType-"]').each(function(i,e) {
    $(this).setOneToManyComponent();
  });
  $('select[class*="primarySelectorField"]').each(function(i,e) {
    $(this).setPrimarySelectorFunction();
  });
}

jQuery.fn.setOneToManyComponent = function() {
  var $parent = $(this).parents('.elementsListComponent');
  var $select = $parent.find('select');
  var elementType = $select.classParam('elementType');
  var maxLimit = $select.classParam('maxLimit');
  var $list = $parent.find('ul.list');
  var counted = $list.find('li').length;
  var updateIndex = 0;

  console.log("init", elementType);

  // Disabled elements already selected
  $list.find("li").each(function(index,domElement) {
    var id = $(domElement).find('.elementRelationID').val();
    $select.find('option[value="' + id + '"]').prop("disabled", true);

    // Verify if already exist and remove
    if(($list.find('.elementRelationID[value="' + id + '"]').length) > 1) {
      // $(domElement).remove();
      updateIndex++;
    }
  });

  // Update indexes
  if(updateIndex) {
    $list.find('li.relationElement').each(function(i,element) {
      var indexLevel = $(element).classParam('indexLevel');
      $(element).setNameIndexes(indexLevel, i);
    });
  }

  // Validate limit reached
  if((maxLimit > 0) && (counted >= maxLimit)) {
    $select.prop('disabled', true).trigger('change.select2');
  }

  // Set placeholder
  if((maxLimit == 0)) {
    $select.find('option[value="-1"]').text("Select multiple options...");
  } else if((maxLimit > 1)) {
    $select.find('option[value="-1"]').text("Select multiple options (Max " + maxLimit + ")...");
  } else if((maxLimit == 1)) {
    $select.find('option[value="-1"]').text("Select a single option...");
  }

  // On select element
  $select.on('change', onSelectElement);

  // On click remove button
  $parent.find('[class*="removeElementType-"]').on('click', onClickRemoveElement);
};

//Function for Primary selector fields
jQuery.fn.setPrimarySelectorFunction = function() {
  var $parent = $(this).parents('.elementsListComponent');
  var $select = $parent.find('select');

  $select.on('change', onSelectElementPrimary);

  // On click remove button
  $parent.find('[class*="removeElementType-"]').on('click', onClickRemoveElementPrimary);

}

function onSelectElement() {
  var $select = $(this);
  var $parent = $(this).parents('.elementsListComponent');
  var $option = $select.find('option:selected');
  var elementType = $select.classParam('elementType');
  var maxLimit = $select.classParam('maxLimit');
  var $list = $parent.find('ul.list');
  var counted = $list.find('li').length;
  var className = $list.attr('class');

  // Select an option
  if($option.val() == "-1") {
    return;
  }

  // Clone the new element
  var $element = $parent.find('.relationElement-template').clone(true);
  $element.removeClass('relationElement-template');
  $element.attr('class',' '+$element.attr('class'));

  // Remove template tag
  $element.find('input').each(function(i,e) {
    e.name = (e.name).replace("_TEMPLATE_", "");
    e.id = (e.id).replace("_TEMPLATE_", "");
  });
  // Set attributes
  var id = $option.val();
  var name = $option.text();
  $element.find('.elementRelationID').val(id);
  $element.find('.elementName').html(name);

  // Add Item
  console.log("Add item: " + id);
  console.log($element);

  // Show the element
  $element.appendTo($list).hide().show(350, function() {
    $select.val('-1').trigger('change.select2');

    // Disabled option in select component
    $select.find('option[value="' + id + '"]').prop("disabled", true);
    $select.select2();

    // Create event addElement
    $select.trigger("addElement", [
        id, name
    ]);
  });

  // Validate limit reached
  if((maxLimit > 0) && ((counted + 1) >= maxLimit)) {
    $select.prop('disabled', true).trigger('change.select2');
  }

  // Update indexes
  $list.find('li.relationElement').each(function(i,element) {
    var indexLevel = $(element).classParam('indexLevel');
    $(element).setNameIndexes(indexLevel, i);
  });
  $element.find('label.radio-label').attr('for', $element.find('label.radio-label').parents('.radioFlat').find('input').attr("id"));
  //Validate if is a primary radioButton group
  if(className.indexOf("primary") >= 0){
    if(($list.children().length < 3 && $list.children().first().is('label')) || $list.children().length < 2){
      $element.find('input.radio-input').attr('checked', true);
    }else{
      $element.find('input.radio-input').attr('checked', false);
    }

    $element.find('label.radio-label').attr('for', $element.find('label.radio-label').parents('.radioFlat').find('input').attr("id"));
    $element.find('input.radio-input').on('change', onSelectElementPrimary);
  }
}

function onClickRemoveElement() {
  console.log("remove element");
  var removeElementType = $(this).classParam('removeElementType');
  var $parent = $(this).parent();
  var $select = $(this).parents(".panel-body").find('select');
  var $primaryRadioElement = $(this).parents('.elementsListComponent').find('ul.primaryRadio');
  var $list = $(this).parents('.elementsListComponent').find('ul.list');
  var className = $list.attr('class');
  var counted = $list.find('li').length;
  var maxLimit = $select.classParam('maxLimit');
  var id = $parent.find(".elementRelationID").val();
  var name = $parent.find(".elementName").text();
  var $primaryDisplay = $(this).parents(".elementsListComponent").find('div.primarySelectorDisplayBox');
  var eventData = [
      id, name
  ];

  var eventRemove = jQuery.Event("beforeRemoveElement");
  $select.trigger(eventRemove, eventData);
  if(eventRemove.isDefaultPrevented()) {
    return;
  }

  $parent.slideUp(300, function() {
    $parent.remove();

    // Enabled option in select component
    $select.find('option[value="' + id + '"]').prop("disabled", false);
    $select.select2();

    // Create event removeElement
    $select.trigger("removeElement", [
        id, name
    ]);

    // Update indexes
    $list.find('li.relationElement').each(function(i,element) {
      var indexLevel = $(element).classParam('indexLevel');
      $(element).setNameIndexes(indexLevel, i);
      $(element).find('label.radio-label').attr('for', $(element).find('label.radio-label').parents('.radioFlat').find('input').attr("id"));
 
      if(className.indexOf("primary") >= 0){
        $(element).find('label.radio-label').attr('for', $(element).find('label.radio-label').parents('.radioFlat').find('input').attr("id"));
        
        //$(element).find('input.radio-input').attr('checked', true);
        /*
        if ($list.children().length < 3){
          $(element).find('input.radio-input').attr('checked', true);
        }
        */
      }
    });

    // Enabled select component if needed
    if((maxLimit > 0) && (counted >= maxLimit)) {
      $select.prop('disabled', false).trigger('change.select2');
    }
  });
}

function onSelectElementPrimary() {
  var $select = $(this);
  var $parent = $(this).parents('.elementsListComponent');
  var $option = $select.find('option:selected');
  var elementType = $select.classParam('elementType');
  var maxLimit = $select.classParam('maxLimit');
  var $list = $parent.find('ul.list');
  var counted = $list.find('li').length;
  var id = $option.val();
  var name = $option.text();

  var $primaryList = $parent.find('ul.primaryRadio');
  var $primaryDisplay = $parent.find('div.primarySelectorDisplayBox');
  var className = $primaryDisplay.attr('class');
  var idenfitierClassName = className.split(' ');

  $primaryDisplay.css("display","block");

  var $contentDiv =$("<div class='radioFlat selectPrimary radioContentBox ID-"+ id +"'></div>");
  var $radiobutton = $("<input id='primaryRadioButtonID"+ idenfitierClassName[1] +"-"+ id +"' class='radio-input assesmentLevels primaryRadioButton option-"+ id +"' type='radio' name='"+ idenfitierClassName[1] +"' value='"+ id +"'/>");
  var $radioLabel = $("<label for='primaryRadioButtonID"+ idenfitierClassName[1] +"-"+ id +"' class='radio-label'>"+ name +"</label>");

  if($primaryList.children().length < 1){
    $radiobutton.attr('checked', true);
  }

  $radiobutton.appendTo($contentDiv);
  $radioLabel.appendTo($contentDiv);
  $contentDiv.appendTo($primaryList);
}

function onClickRemoveElementPrimary() {
  var removeElementType = $(this).classParam('removeElementType');
  var $parent = $(this).parent();
  var $select = $(this).parents(".panel-body").find('select');
  var $list = $(this).parents('.elementsListComponent').find('ul.list');
  var counted = $list.find('li').length;
  var maxLimit = $select.classParam('maxLimit');
  var id = $parent.find(".elementRelationID").val();
  var name = $parent.find(".elementName").text();
  var $primaryDisplay = $(this).parents(".elementsListComponent").find('div.primarySelectorDisplayBox');
  var $primaryRadioElement = $(this).parents('.elementsListComponent').find('ul.primaryRadio');

  var $tempo = $primaryRadioElement.find(".radioFlat.selectPrimary.radioContentBox.ID-"+ id +"");
  var $input = $tempo.find("input");

  if($input.is(':checked')){
    $tempo.remove();

    if($primaryRadioElement.children().length > 0){
      $primaryRadioElement.children(":first").find("input").attr('checked', true);
    }
  }else{
    $tempo.remove();
  }

  //$tempo.remove();

  if($primaryRadioElement.children().length < 1){
    $primaryDisplay.css("display", "none");
  }
}

/**
 * Geographic Scope Component
 */
function formatStateCountries(state) {
  if(!state.id) {
    return state.text;
  }
  var flag = '<i class="flag-icon flag-icon-'+ state.element.value.toLowerCase() +'"></i> ';
  var $state;
  if(state.id != -1) {
    $state = $('<span>' + flag + state.text + '</span>');
  } else {
    $state = $('<span>' + state.text + '</span>');
  }
  return $state;
};

function setGeographicScope(component) {

  var $partner = $(component).parents('.geographicScopeBlock');
  var $scopes = $(component).parents('.elementsListComponent');

  var $regionalBlock = $partner.find('.regionalBlock');
  var $nationalBlock = $partner.find('.nationalBlock');

  var isRegional = $scopes.find('.elementRelationID[value="2"]').exists();
  var isMultiNational = $scopes.find('.elementRelationID[value="3"]').exists();
  var isNational = $scopes.find('.elementRelationID[value="4"]').exists();
  var isSubNational = $scopes.find('.elementRelationID[value="5"]').exists();

  // Regions
  if(isRegional) {
    $regionalBlock.slideDown();
  } else {
    $regionalBlock.slideUp();
    // Clean selected region
    $regionalBlock.find("select").val("-1").trigger('change');
  }

  // Countries
  if(isMultiNational || isNational || isSubNational) {
    if(isMultiNational) {
      $nationalBlock.find("select").select2({
          maximumSelectionLength: 0,
          placeholder: "Select a country(ies)",
          templateResult: formatStateCountries,
          templateSelection: formatStateCountries,
          width: '100%'
      });
    } else {
      var countries = ($nationalBlock.find("select").val()) || [];
      if(countries.length > 1) {
        $nationalBlock.find("select").val(null).trigger('change');
      }
      $nationalBlock.find("select").select2({
          maximumSelectionLength: 1,
          placeholder: "Select a country",
          templateResult: formatStateCountries,
          templateSelection: formatStateCountries,
          width: '100%'
      });
    }
    $nationalBlock.slideDown();
  } else {
    $nationalBlock.slideUp();
  }
}
