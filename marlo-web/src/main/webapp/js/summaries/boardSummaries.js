$(document).ready(init);

function init() {
  addSelect2();
  attachEvents();
}

function attachEvents() {
  $('.summariesSection a, .summariesSection span').on('click', selectSummariesSection);
  $('input[name=formOptions]').on('change', selectTypeReport);
  $('select[name=projectID], input[name=q]').on('change', updateUrl);
  $('#generateReport').on('click', generateReport);

  $(".summariesFiles").on("click", function() {
    updateUrl(this);
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
  $('input[name=formOptions]').attr('checked', false);

  // Clean URL
  setUrl('#');
}

function selectTypeReport(e) {
  console.log(e.target);
  var $option = $(e.target).parents(".summariesFiles");
  $option.parent().parent().find('.extraOptions').find('select, input').attr('disabled', true);
  $option.parent().parent().find('.extraOptions').fadeOut();
  $option.find('.extraOptions').find('select, input').attr('disabled', false).trigger("liszt:updated");
  $option.find('.extraOptions').fadeIn();

  // updateUrl();
}

function generateReport(e) {
  var $formOptions = $('input[name=formOptions]:checked');
  var formOption = $formOptions.val() || 0;
  if(formOption == 0) {
    e.preventDefault();
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'You must to select a report option';
    noty(notyOptions);
  } else {
    if($formOptions[0].value == "project") {
      var projectId = $('#projectID').val();
      if(projectId == -1) {
        e.preventDefault();
        var notyOptions = jQuery.extend({}, notyDefaultOptions);
        notyOptions.text = 'You must to select a project';
        noty(notyOptions);
      }
    }
  }

}

function updateUrl(element) {
  var generateUrl = "";
  var $formOptions = $(element).find('input[name=formOptions]');
  var formOption = $formOptions.val() || 0;
  var extraOptions = $('form [name!="formOptions"]').serialize() || 0;
  if(formOption != 0) {
    generateUrl = baseURL + "/summaries/" + formOption + ".do";
    if(extraOptions != 0) {
      generateUrl += '?' + extraOptions;
    }
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
  $("form select#projectID").select2({
      search_contains: true,
      width: '100%'
  });
  $("#genderKeywords").select2({
    tags: [
        "red", "green", "blue"
    ]
  });
}