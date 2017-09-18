
var termsArray = [];
var reportYear = "2017";

$(document).ready(init);
function init() {
  // Add Select2 Plugin
  addSelect2();
  
  // Add Tag Editor Plugin
  $('.keywords').tagEditor({ 
    delimiter: ",",
    placeholder: "Enter keywords here ..."
  });
  
  // Attach events
  attachEvents();
}

function attachEvents() {

  // Select reports type
  $('.summariesSection a, .summariesSection span').on('click', selectSummariesSection);
  // Select a report
  $(".summariesFiles").on("click", selectReport);
  // Download
  $('.generateReport').on('click', generateReport);
  
  // Add predefined gender keywords
  $('.addGenderKeys').on('click', addGenderKeys)
  
  // Remove all keywords
  $('.removeAllTags').on('click', removeAllTags)
}

function addGenderKeys(){
  // Gender Tags
  var genderArray =
    [
      "Gender", "female", "male", "men", "elderly", "caste", "women", "equitable", "inequality", "equity",
      "social differentiation", "social inclusion", "youth", "social class", "children", "child"
      ];
  $.each(genderArray, function(i,tag){
    $('.keywords').tagEditor('addTag', tag);
  });
}

function removeAllTags(){
  var tags = $('.keywords').tagEditor('getTags')[0].tags;
  for (i = 0; i < tags.length; i++) { 
    $('.keywords').tagEditor('removeTag', tags[i]); 
  }
}

function selectReport() {
  if($(this).hasClass('selected')){
    return
  }
  console.log(this);
  // Hide all reports
  $('.summariesFiles').removeClass("selected");
  $('.extraOptions').slideUp();
  // Show selected report
  $(this).find('.extraOptions').slideDown();
  $(this).find('.extraOptions').find('select, input').attr('disabled', false).trigger("liszt:updated");
  $(this).addClass("selected");

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
  $(".extraOptions").slideUp();
  $('.extraOptions').find('select, input').attr('disabled', true);
  // Clean URL
  $("#optionsPopUp").find(".projectSelectWrapper").hide("slow");
  $("#projectID").val("-1");
}

function generateReport(e) {
}

// Activate the select plugin.
function addSelect2() {
  $('select').select2({
    width: '100%'
  });
}

function getProjectsByCycleYear() {
  $(".allProjects").empty();
  $(".allProjects").addOption("-1", "Select an option...");
  $.ajax({
      url: baseURL + "/projectList.do?",
      type: 'GET',
      data: {
          cycle: $("input[name='cycle']:checked").val(),
          year: $("select#reportYears").find("option:selected").val()
      },
      success: function(m) {
        console.log(m);
        $.each(m.projects, function(i,e) {
          $(".allProjects").addOption(e.id, "P" + e.id + "-" + e.description);
        })
      },
      error: function(e) {
        console.log(e);
      }
  });
}
