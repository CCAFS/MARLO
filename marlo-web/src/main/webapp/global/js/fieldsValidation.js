$(document).ready(init);
var errorList = $("li#message");

function init() {
  startIntro();
}

// highlights missing fields

// PRUEBA HIGHLIGHTS FIELDS
// var intro = introJs();
function startIntro() {

  missingFields(errorList);
  // intro.addHints();
}

// test3
function missingFields(errorList) {
  if(errorList.length != 0) {
    errorList.each(function(i,e) {
      var list = $(e).html();
      var fieldName = list.split(":")[0].split(/-(.+)/)[1];
      var type = list.split(":")[0].split("-")[0];
      var message = list.split(":")[1];

      // select element by input name, list name or add name
      if(type === "add") {
        getAddElement(fieldName, message);
      } else if(type === "list") {
        getListElement(fieldName, message);
      } else  if(type === "input") {
        // INPUTS
        getInputElement(fieldName, message);
      }
    });
  }
}

function getListElement(fieldName,message) {
// LISTAS
  var elementQuery = $("div[listname='" + fieldName + "']")[0];
  if($(elementQuery).exists()) {
    var offset = $(elementQuery).offset();

    // Find Bootstrap tabs
    var tabPane = $(elementQuery).parents('.tab-pane');
    if(tabPane) {
      var tabID = $(tabPane).attr('id');
      var $tab = $('a[href="#' + tabID + '"]');
      // Add Field error to the text
      $tab.addClass('fieldError');
      // Add Filed error to the badge
      $tab.find('.badge').addClass('fieldError');
    }

    // Tag with message
    var tagElement = $("#test").clone(true).removeAttr("id");
    tagElement.attr("title", message);
    $(elementQuery).append(tagElement);
    var left = $(elementQuery).outerWidth();
    tagElement.offset({
        top: 0,
        left: left
    });
    tagElement.fadeIn(2000);

    var $spanSelector = $(elementQuery).find('span.select2');
    if($spanSelector) {
      $spanSelector.addClass('fieldError');
      $spanSelector.attr("title", message);
    }

    const firstLabel = $(elementQuery).find('label:first');
    if(firstLabel) {
      firstLabel.addClass('fieldError');
      firstLabel.attr("title", message);
    }

    const checkBoxes = $(elementQuery).find('input[type="checkbox"]').next('label');
    if(checkBoxes) {
      checkBoxes.each(function(i,e) {
        $(e).addClass('fieldError');
        $(e).attr("title", message);
      });
    }
  }
}

function getInputElement(fieldName,message) {
  var elementQuery = $("input[name='" + fieldName + "']");

  // validate if it's input
  if(elementQuery.length == 0) {
    // validate if it's textaera
    elementQuery = $("textarea[name='" + fieldName + "']");
    if(elementQuery.length == 0) {
      // validate if it's select
      elementQuery = $("select[name='" + fieldName + "']");
      if($(elementQuery).exists()) {
        $parent = $(elementQuery).parent();
        // find an inner span.selection if exists add other type of class
        var $spanSelector = $parent.find('span.selection');
        if($spanSelector) {
          $spanSelector.find('span.select2-selection').addClass('missingSelect2');
        } else {
          $(elementQuery).parent().addClass("missingSelect"); 
        }

        //Primefaces select 
        let $selectPrime = $(elementQuery).parent('.p-dropdown');

        if($selectPrime.length > 0) {
          $selectPrime.addClass('missingSelect2');
          $selectPrime.attr("title", message);
        }
      }
    }

  } else {
    // VALIDATE IF IT'S CHECKBOX
    if(elementQuery.attr("type") == "checkbox") {
      // Tag with message
      var tagElement = $("#test").clone(true).removeAttr("id");
      tagElement.attr("title", message);
      $(elementQuery).parent().append(tagElement);
      var left = $(elementQuery).parent().outerWidth();
      var top = $(elementQuery).parent().height();
      tagElement.offset({
          top: (top / 2),
          left: left
      });
      tagElement.fadeIn(2000);

    } else if(elementQuery.attr("type") == "radio") {
      // Tag with message
      var tagElement = $("#test").clone(true).removeAttr("id");
      tagElement.attr("title", message);
      $(elementQuery).parents(".radio-block").append(tagElement);
      var left = $(elementQuery).parents(".radio-block").outerWidth();
      var top = $(elementQuery).parents(".radio-block").height();
      tagElement.offset({
          top: (top / 2),
          left: left
      });
      tagElement.fadeIn(2000);
    } else {
      // FIND ASSOCIATE DIV WHEN THE INPUT IS HIDDEN
      var asociateDiv = $("." + fieldName.replace(/\W+/g, ""));
      if(asociateDiv.exists()) {
        $(asociateDiv).attr("title", message);
        $(asociateDiv).addClass("fieldError");
      }
    }
  }

  // Find Associate Label to change the color
  const associateLabel = $("label[for='" + fieldName + "']");
  if(associateLabel.exists()) {
    $(associateLabel).attr("title", message);
    $(associateLabel).addClass("fieldError");
  }

  // Find Bootstrap tabs
  var tabPane = $(elementQuery).parents('.tab-pane');
  if(tabPane) {
    var tabID = $(tabPane).attr('id');
    var $tab = $('a[href="#' + tabID + '"]');
    // Add Field error to the text
    $tab.addClass('fieldError');
    // Add Filed error to the badge
    $tab.find('.badge').addClass('fieldError');
  }

  // Find date label
  var $dateLabel = $(elementQuery).parent().find('.dateLabel');
  if($dateLabel) {
    $dateLabel.addClass('fieldError');
  }

  // Find radio button
  var $radioFlatLabel = $(elementQuery).parents('.radioFlat').find('label');
  if($radioFlatLabel) {
    $radioFlatLabel.addClass('fieldError');
  }

  // Find yesNoInputDeliverable button
  var $yesNoInputDeliverableLabel = $(elementQuery).parents('.yesNoInputDeliverable').find('label.yesNoLabel');
  if($yesNoInputDeliverableLabel) {
    $yesNoInputDeliverableLabel.addClass('fieldError');
    $(elementQuery).removeClass("fieldError");
    $(elementQuery).attr("title", "");
  }

  // Find trumbowyg
  var $trumbowygBox = $(elementQuery).parents('.trumbowyg-box');
  if($trumbowygBox) {
    $trumbowygBox.addClass('fieldError');
  }

  // Find Bootstrap input-group-btn
  var $inputGroupBtn = $(elementQuery).parents('.input-group-btn');
  if($inputGroupBtn) {
    $inputGroupBtn.addClass('fieldError');
  }

  $(elementQuery).addClass("fieldError");
  $(elementQuery).attr("title", message);

}

function getAddElement(fieldName,message) {
  var labelQuery = $("label[for='" + fieldName + "']");
  if(labelQuery.length == 0) {
    labelQuery = $("label[for='" + fieldName + "[]']");
  }

  if(labelQuery.length != 0) {

    // Find Bootstrap tabs
    var tabPane = $(labelQuery).parents('.tab-pane');
    if(tabPane) {
      var tabID = $(tabPane).attr('id');
      var $tab = $('a[href="#' + tabID + '"]');
      // Add Field error to the text
      $tab.addClass('fieldError');
      // Add Filed error to the label
      $(labelQuery).addClass('fieldError');
      $(labelQuery).attr("title", message);
    }
  }
}

// EVENT TO ERROR TAG

$(".errorTag").on("click", function() {
  var $this = $(this).parents(".expandableBlock").find(".blockTitle");
  $($this).next().slideToggle('slow', function() {
    $($this).find('textarea').autoGrow();
  });
  $(this).fadeOut("slow", function() {
    $(this).remove();
  });
});

// VERIFY FIELD ERRORS IN HIDDEN ELEMENTS
function verifyMissingFields(element) {
  if(errorList.length != 0) {
    if($(element).find(".errorTag").exists() || $(element).find(".fieldError").exists()
        || $(element).find(".missingSelect").exists()) {
      // Tag with message
      var tagElement = $("#test").clone(true).removeAttr("id");
      tagElement.attr("title", "Missing fields inside this block!");
      $(element).append(tagElement);
      var left = $(element).outerWidth();
      tagElement.offset({
          top: 0,
          left: left
      });
      tagElement.fadeIn(2000);
    }
  }
}

function showHiddenTags(element) {
  if(errorList.length != 0) {
    if($(element).find(".errorTag").exists()) {
      // Tag with message
      var tagElement = $(element).find("span.errorTag");
      var left = $(element).outerWidth();
      var top = $(element).outerHeight();
      tagElement.css("top", (top / 2));
      tagElement.css("left", left);
    }
  }
}