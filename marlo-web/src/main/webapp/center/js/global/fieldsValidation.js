$(document).ready(init);

function init() {
  startIntro();
}

// highlights missing fields

// PRUEBA HIGHLIGHTS FIELDS
// var intro = introJs();
function startIntro() {

  var errorList = $("li#message");
  missingFields(errorList);
  // intro.addHints();

}

// test3
function missingFields(errorList) {
  if(errorList.length != 0) {
    errorList.each(function(i,e) {
      var list = $(e).html();
      var fieldName = list.split(":")[0].split("-")[1];
      var type = list.split(":")[0].split("-")[0];
      var message = list.split(":")[1];

      // select element by input name or list name
      if(type === "list") {
        getListElement(fieldName, message);
      } else {
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
        $(elementQuery).parent().addClass("missingSelect");
      }
    }
  } else {
    // VALIDATE IF IT'S CHECKBOX
    console.log(elementQuery);
    if(elementQuery.attr("type") == "checkbox") {
      console.log("holi");
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

    } else {
      // FIND ASSOCIATE DIV WHEN THE INPUT IS HIDDEN
      var asociateDiv = $("." + fieldName.replace(/\W+/g, ""));
      if(asociateDiv.exists()) {
        $(asociateDiv).attr("title", message);
        $(asociateDiv).addClass("fieldError");
      }
    }
  }
  // For yesNo inputs
  if(elementQuery.hasClass("onoffswitch-radio")) {
    // Tag with message
    var tagElement = $("#test").clone(true).removeAttr("id");
    tagElement.attr("title", message);
    $(elementQuery).parent().append(tagElement);
    var left = $(elementQuery).parent().outerWidth();
    var top = 0;
    tagElement.offset({
        top: (top / 2),
        left: left
    });
    tagElement.fadeIn(2000);
  }
  $(elementQuery).addClass("fieldError");
  $(elementQuery).attr("title", message);

}

// EVENT TO ERROR TAG

$(".errorTag").on("click", function() {
  $(this).fadeOut("slow", function() {
    $(this).remove();
  });
});

// VERIFY FIELD ERRORS IN HIDDEN ELEMENTS
function verifyMissingFields(element) {
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