$(document).ready(init);

function init() {
  addSelect2();
  attachEvents();
}

function attachEvents() {
  $(".notAvailable").attr("title", "Not available at the moment");
  $('.summariesSection a, .summariesSection span').on('click', selectSummariesSection);
  $('#generateReport').on('click', generateReport);

  $(".title-file , .pdfIcon , .excelIcon").on("click", function() {
    $("input[name='projectID']").val("");
    $("#selectProject").html("Click over me");
    var $this = $(this).parents(".summariesFiles");
    console.log("holi");
    $(".summariesFiles").removeClass("selected");
    $(".extraOptions").fadeOut();
    $('.extraOptions').find('select, input').attr('disabled', true);
    if($($this).find("#projectPortfolio").val() == "reportingSummary") {
      $($this).find('.extraOptions').fadeIn();
      $($this).find('.extraOptions').find('select, input').attr('disabled', false).trigger("liszt:updated");
    }
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

function selectSummariesSection(e) {
  e.preventDefault();
  var $section = $(e.target).parents('.summariesSection');
  var $content = $('#' + $section.attr('id') + '-contentOptions');
  $section.siblings().removeClass('current');
  $section.addClass('current');
  $content.siblings().hide();
  $content.fadeIn();

  // Uncheck from formOptions the option selected
  $("input[name='projectID']").val("");
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
    if($selected.find(".extraOptions").find("select").val() == "-1") {
      e.preventDefault();
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'You must to select a project';
      noty(notyOptions);
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