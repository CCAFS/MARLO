$(document).ready(init);
function init() {
  // Add Select2 Plugin
  $('select').select2({
    width: '100%'
  });
  
  // Add Tag Editor Plugin
  $('.keywords').tagEditor({ 
    delimiter: ",",
    forceLowercase: false,
    placeholder: "Enter keywords here ..."
  });
  
  // Attach events
  attachEvents();
}

// *************************************************** Events ********************************************************//

function attachEvents() {
  // Select reports type
  $('.summariesSection a, .summariesSection span').on('click', selectSummariesSection);
  
  // Select a report
  $(".summariesFiles").on("click", selectReport);
  
  // Add predefined gender keywords
  $('.addGenderKeys').on('click', addGenderKeys);
  
  // Remove all keywords
  $('.removeAllTags').on('click', removeAllTags);
  
  // Update project List
  // $('[name="cycle"], [name="year"]').on('change', changePhaseParameters);
    $('[name="phaseID"]').on('change', changePhaseParameters);

  // Show or hide select a cluster
  $('#AICCRA_progressReportProcessSummary #1-showAllYears-false').on('click', hideShowClusterSelect);
  $('#AICCRA_progressReportProcessSummary #1-showAllYears-true').on('click', hideShowClusterSelect);
}

// ************************************************ Functions *******************************************************//
function hideShowClusterSelect() {
  if ($(this).attr('value') == 'true') {
    $('#AICCRA_progressReportProcessSummary').children().eq(1).hide('slow');
  } else {
    $('#AICCRA_progressReportProcessSummary').children().eq(1).show('slow');
  }
}

function changePhaseParameters(){
  var $parent = $(this).parents('.summariesFiles');
  if($parent.hasClass('allowProjectID')){
    getProjectsByCycleYear($parent, $parent.find('[name="phaseID"]').val());
  }
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
  $.each(tags, function(i,tag){
    $('.keywords').tagEditor('removeTag', tag); 
  });
}

function selectReport() {
  if($(this).hasClass('selected')){
    return
  }
  // Update the project list if necessary
  var $parent = $(this);
  if($parent.hasClass('allowProjectID')){
    getProjectsByCycleYear($parent, $parent.find('[name="phaseID"]').val());
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

/**
 * Update Projects List
 * 
 * @param {DOM} parent - summariesFiles div
 * @param {String} cycle - Planning/Reporting
 * @param {Number} year
 * @returns
 */
function getProjectsByCycleYear(parent, phaseID) {
  var $parent = $(parent);
  $parent.find(".allProjectsSelect").empty();
  $parent.find('.loading').fadeIn();
  $.ajax({
      url: baseURL + "/projectListByPhase.do?",
      type: 'GET',
      data: {
        // cycle: cycle,
        // year: year,
        phaseID: phaseID
      },
      success: function(m) {
        $.each(m.projects, function(i,e) {
          $parent.find(".allProjectsSelect").addOption(e.id, "C" + e.id + " - " + e.description);
        })
      },
      complete: function() {
        $parent.find('.loading').fadeOut();
      }
  });
}