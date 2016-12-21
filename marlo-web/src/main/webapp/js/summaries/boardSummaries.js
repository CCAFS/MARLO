$(document).ready(init);
var termsArray = [];
function init() {

  addSelect2();
  attachEvents();
}

function attachEvents() {
  // ADD TERM
  $('.addTerm').on('click', addTerm);
  $('.removeTerm').on('click', removeTerm);

  $(".notAvailable").attr("title", "Not available at the moment");
  $('.summariesSection a, .summariesSection span').on('click', selectSummariesSection);
  $('#generateReport').on('click', generateReport);

  // Clicking other report
  $(".title-file , .pdfIcon , .excelIcon").on("click", function() {
    $('.wordContent').empty();
    termsArray = [];
    $("input[name='projectID']").val("-1");
    $("#selectProject").html("Click over me");
    var $this = $(this).parents(".summariesFiles");
    console.log("holi");
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
        height: '210',
        title: 'terms',
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

function addTerm() {
  var input = $("#termsPopUp").find("input");
  var $list = $('.wordContent');
  var $item = $('#term').clone(true).removeAttr("id");
  if(validateInputTerms() == true) {
    $item.css("display", "inline-block");
    $item.find(".text").html(input.val());
    input.removeClass("fieldError");
    $list.append($item);
    $item.show('slow');
    termsArray.push(input.val());
    input.val("");
  } else {
    input.addClass("fieldError");
  }

}

function removeTerm() {
  var $list = $(this).parents('.wordContent');
  var $item = $(this).parents('.terms');
  var index = termsArray.indexOf($item.html());
  if(index > -1) {
    termsArray.splice(index, 1);
  }
  $item.hide(1000, function() {
    $item.remove();
  });
}

function validateInputTerms() {
  var input = $("#termsPopUp").find("input");
  if(input.val().length > 0) {
    if(/^\s+|\s+$/.test(input.val())) {
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
    if($selected.find(".extraOptions").find("#includeTerms")) {
      var termsString = JSON.stringify(termsArray);
      console.log(termsArray);
      console.log(termsString);
      var $formOptions = $($selected).find('input[name=formOptions]');
      var formOption = $formOptions.val() || 0;
      var url =
          baseURL + "/projects/" + currentCrpSession + "/" + formOption + ".do" + "?keys=" + termsArray.join("~/");
      console.log(url);
      setUrl(url);
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
    generateUrl += '&year=' + currentCycleYear;
    setUrl(generateUrl);
  } else {
    setUrl('#');
  }
}

function setUrl(url) {
  if(url == '#') {
    $('#generateReport').hide();
  } else {
    $('#generateReport').attr('href', url).fadeIn();
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