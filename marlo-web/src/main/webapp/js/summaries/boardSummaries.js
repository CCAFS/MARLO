$(document).ready(init);
var genderArray =
    [
        "Gender", "female", "male", "men", "elderly", "caste", "women", "equitable", "inequality", "equity",
        "social differentiation", "social inclusion", "youth", "social class", "children", "child"
    ];
var termsArray = [];

var reportYear = $("#reportYear").find("option:selected").val();

function init() {
  addGenderTerms();
  addSelect2();
  attachEvents();
}

function attachEvents() {

  $("#reportYear").on("change", function() {
    reportYear = $(this).find("option:selected").val();
    if($(".summariesOptions").find(".selected").exists()) {
      updateUrl($(".summariesOptions").find(".selected"));
    }
  });

  $("#gender").on("change", function() {

    if($(this).hasClass("view")) {
      $(this).addClass("notview");
      $(this).removeClass("view");
      $(".gender").hide();
      removeGenderTerms();
    } else {
      $(this).removeClass("notview");
      $(this).addClass("view");
      addGenderTerms();
    }
  });
  // ADD TERM
  $("#termsPopUp").find("input").on("keypress", function(event) {
    if(event.keyCode === 10 || event.keyCode === 13) {
      addTerm();
    }
  });
  $('.removeTerm').on('click', removeTerm);

  $(".notAvailable").attr("title", "Not available at the moment");
  $('.summariesSection a, .summariesSection span').on('click', selectSummariesSection);
  $('.generateReport').on('click', generateReport);

  // Clicking other report
  $(".title-file , .pdfIcon , .excelIcon").on("click", function() {
    // $('.wordContent').empty();
    $("input[name='projectID']").val("-1");
    $("#selectProject").html("Click over me");
    var $this = $(this).parents(".summariesFiles");
    $(".summariesFiles").removeClass("selected");
    $(".extraOptions").fadeOut();
    $('.extraOptions').find('select, input').attr('disabled', true);
    $($this).find('.extraOptions').fadeIn();
    $($this).find('.extraOptions').find('select, input').attr('disabled', false).trigger("liszt:updated");
    $($this).addClass("selected");
    updateUrl($this);
  });

  /** Select a project * */
  $('#selectProject').on('click', function() {
    var $this = $(this).parents(".summariesFiles");
    $("#projectsPopUp").dialog({
        resizable: false,
        closeText: "",
        width: '40%',
        title: 'Projects',
        modal: true,
        height: $(window).height() * 0.50,
        show: {
            effect: "blind",
            duration: 500
        },
        hide: {
            effect: "fadeOut",
            duration: 500
        },
        open: function(event,ui) {
        }
    });
  });

  /** include terms * */
  $('#includeTerms').on('click', function() {
    var $this = $(this).parents(".summariesFiles");
    $("#termsPopUp").dialog({
        resizable: false,
        closeText: "",
        width: '30%',
        height: '280',
        title: 'Terms',
        modal: true,
        show: {
            effect: "blind",
            duration: 500
        },
        hide: {
            effect: "fadeOut",
            duration: 500
        },
        open: function(event,ui) {
        }
    });
  });

  $(".project").on("click", function() {
    var report = $("#selectProject").parents(".summariesFiles");
    console.log(report);
    $("input[name='projectID']").val($(this).attr("id"));
    var v = $(this).text().length > 16 ? $(this).text().substr(0, 16) + ' ... ' : $(this).text();
    $("#selectProject").attr("title", $(this).html());
    $("#selectProject").html(v);
    $('#projectsPopUp').dialog('close');
    updateUrl(report);
  });
}

function addGenderTerms() {
  for(var i = 0; i < genderArray.length; i++) {
    var $item = $('#term').clone(true).removeAttr("id");
    $item.addClass("gender");
    $item.css("display", "inline-block");
    $item.find(".text").html(genderArray[i]);
    $(".wordContent").append($item);
    $item.show('slow');
  }
}

function removeGenderTerms() {
  $(".wordContent").find(".gender").each(function(i,e) {
    $(e).remove();
  });
}

function addTerm() {
  var input = $("#termsPopUp").find(".inputTerm");
  var $list = $('.wordContent');
  var $item = $('#term').clone(true).removeAttr("id");
  if(validateInputTerms() == true) {
    $item.css("display", "inline-block");
    $item.find(".text").html(input.val());
    input.removeClass("fieldError");
    $list.prepend($item);
    $item.show('slow');
    input.val("");
  } else {
    input.addClass("fieldError");
  }

}

function removeTerm() {
  var $list = $(this).parents('.wordContent');
  var $item = $(this).parents('.terms');
  $item.hide(1000, function() {
    $item.remove();
  });
}

function validateInputTerms() {
  var input = $("#termsPopUp").find(".inputTerm");
  if(input.val().length > 0) {
    if(/^\s+|\s+$/.test(input.val()) || /~#/g.test(input.val())) {
      return false
    } else {
      return true;
    }
  } else {
    return false;
  }
}

function selectSummariesSection(e) {
  e.preventDefault();
  var $section = $(e.target).parents('.summariesSection');

  var $content = $('#' + $section.attr('id') + '-contentOptions');
  $section.siblings().removeClass('current');
  $section.addClass('current');
  $content.siblings().hide();
  $content.fadeIn();

  // Uncheck from formOptions the option selected
  $("input[name='projectID']").val("-1");
  $("#selectProject").html("Click over me");
  $('input[name=formOptions]').attr('checked', false);
  $(".summariesFiles").removeClass("selected");
  $(".extraOptions").fadeOut();
  $('.extraOptions').find('select, input').attr('disabled', true);
  // Clean URL
  setUrl('#');
}

function generateReport(e) {
  $("#termsPopUp").dialog("close");
  var $selected = $('.selected');
  if($selected.length == "0") {
    e.preventDefault();
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'You must to select a report option';
    noty(notyOptions);
  } else {
    // FULL REPORT
    if($selected.find(".extraOptions").find("input[name='projectID']").val() == "-1") {
      e.preventDefault();
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'You must to select a project';
      noty(notyOptions);
    }
    // TERMS
    if($selected.find(".extraOptions").find("#includeTerms").length > 0) {
      var url = "";
      var $formOptions = $($selected).find('input[name=formOptions]');
      var formOption = $formOptions.val() || 0;
      if($(".wordContent").find(".terms").length > 0) {
        $(".wordContent").find(".terms").each(function(i,e) {
          termsArray.push($(e).find(".text").html());
        });
        url = baseURL + "/projects/" + currentCrpSession + "/" + formOption + ".do" + "?keys=" + termsArray.join("~/");
        var replace = url.replace(/ /g, "%20");
        setUrl(replace);
      } else {
        url = baseURL + "/projects/" + currentCrpSession + "/" + formOption + ".do";
        setUrl(url);
      }
      $('.wordContent').empty();
      addGenderTerms();
      termsArray = [];
      $("#gender").prop('checked', true);
      $("#gender").removeClass("view");
      $("#gender").removeClass("notview");
      $("#gender").addClass("view");

      // termsArray = genderArray;
    }
  }

}

function updateUrl(element) {
  var generateUrl = "";
  var $formOptions = $(element).find('input[name=formOptions]');
  var formOption = $formOptions.val() || 0;
  var extraOptions = $('form [name!="formOptions"]').serialize() || 0;
  if(formOption != 0) {
    generateUrl = baseURL + "/projects/" + currentCrpSession + "/" + formOption + ".do";
    if(extraOptions != 0) {
      generateUrl += '?' + extraOptions;
    }
    console.log(reportYear);
    if(reportYear == "-1") {
      reportYear = currentCycleYear;
    }
    generateUrl += '&year=' + reportYear;
    setUrl(generateUrl);
  } else {
    setUrl('#');
  }
}

function setUrl(url) {
  if(url == '#') {
    $('#generateReport').hide();
  } else {
    $('.generateReport').attr('href', url).fadeIn();
  }
}

// Activate the select plugin.
function addSelect2() {
  $('form select').select2({
    width: '100%'
  });
  $("#genderKeywords").select2({
    tags: [
        "red", "green", "blue"
    ]
  });
}