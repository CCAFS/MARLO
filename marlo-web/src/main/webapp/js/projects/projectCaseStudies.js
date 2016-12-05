var caseStudiesName;
var $elementsBlock;

$(document).ready(init);

function init() {
  // Set initial variables
  $elementsBlock = $('#caseStudiesBlock');
  caseStudiesName = $('#caseStudiesName').val();

  // Add events for project next users section
  attachEvents();

  // Add select2
  addSelect2();
}

function attachEvents() {
  // Remove a next user event
  $('.removeElement').on('click', removeElement);
  // Add new next user event
  $('#addCaseStudy .addButton').on('click', addElement);

  /**
   * Upload files functions
   */

  $('.fileUpload .remove').on('click', function(e) {
    var context = $(this).attr('id').split('-')[1];
    var $parent = $(this).parent();
    var $inputFile = $('[id$=' + context + '-template]').clone(true).removeAttr("id");
    console.log($inputFile);
    $parent.empty().append($inputFile);
    $inputFile.hide().fadeIn('slow');
    setElementsIndexes();
  });
}

function removeElement(e) {
  e.preventDefault();
  $(e.target).parent().hide('slow', function() {
    $(this).remove();
    setElementsIndexes();
  });
}

function addElement(e) {
  e.preventDefault();
  var $newElement = $('#caseStudy-template').clone(true).removeAttr("id");
  $elementsBlock.append($newElement.fadeIn("slow"));
  $elementsBlock.find('.message').remove();
  setElementsIndexes();
}

function setElementsIndexes() {
  $elementsBlock.find('.caseStudy').each(setElementIndex);
}

function setElementIndex(i,element) {
  var name = caseStudiesName + "[" + i + "].";
  $(element).find("span.index").html(i + 1);

  $(element).find(".caseStudyID").attr("name", name + "id");
  $(element).find(".caseStudyIndicators").attr("name", name + "caseStudyIndicatorsIds");
  $(element).find(".caseStudyExplainIndicatorRelation").attr("name", name + "explainIndicatorRelation");
  $(element).find(".caseStudyTitle").attr("name", name + "title");
  $(element).find(".caseStudyOutcomeStatement").attr("name", name + "outcomeStatement");
  $(element).find(".caseStudyResearchOutput").attr("name", name + "researchOutputs");
  $(element).find(".caseStudyResearchPartners").attr("name", name + "researchPartners");
  $(element).find(".caseStudyActivitiesContributed").attr("name", name + "activities");
  $(element).find(".caseStudyNonResearchPartners").attr("name", name + "nonResearchPartneres");
  $(element).find(".caseStudyOutputUsers").attr("name", name + "outputUsers");
  $(element).find(".caseStudyOutputUsed").attr("name", name + "outputUsed");
  $(element).find(".caseStudyEvidence").attr("name", name + "evidenceOutcome");
  $(element).find(".caseStudyReferences").attr("name", name + "referencesCase");
  $(element).find(".annexesFile").attr("name", name + "myFile");

  $(element).find(".caseStudyYear").attr("name", name + "year");
}

function addSelect2() {
  $('form select').select2();
}