$(document).ready(init);
function init() {
  // Add Select2 Plugin
  addSelect2();
  
  // Add Tag Editor Plugin
  $('.keywords').tagEditor({ 
    delimiter: ",",
    forceLowercase: false,
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
  $('.addGenderKeys').on('click', addGenderKeys);
  
  // Remove all keywords
  $('.removeAllTags').on('click', removeAllTags);
  
  // Update project List
  $('[name="cycle"], [name="year"]').on('change', function(){
    var $parent = $(this).parents('.summariesFiles');
    getProjectsByCycleYear($parent, $parent.find('[name="cycle"]').val(), $parent.find('[name="year"]').val());
  })
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
  
  // Update the project list if necessary
  if($(this).hasClass('allowProjectID')){
    var $parent = $(this);
    getProjectsByCycleYear($parent, $parent.find('[name="cycle"]').val(), $parent.find('[name="year"]').val());
  }
    
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

  $(".summariesFiles").removeClass("selected");
  $(".extraOptions").slideUp();
}

function generateReport(e) {
}

// Activate the select plugin.
function addSelect2() {
  $('select').select2({
    width: '100%'
  });
}

function getProjectsByCycleYear(parent, cycle, year) {

  var $parent = $(parent);
  
  $parent.find(".allProjectsSelect").empty();
  $parent.find('.loading').fadeIn();
  $.ajax({
      url: baseURL + "/projectList.do?",
      type: 'GET',
      data: {
        cycle: cycle,
        year: year
      },
      success: function(m) {
        console.log(m);
        $.each(m.projects, function(i,e) {
          $parent.find(".allProjectsSelect").addOption(e.id, "P" + e.id + " - " + e.description);
        })
      },
      complete: function(e) {
        $parent.find('.loading').fadeOut();
      }
  });
}
