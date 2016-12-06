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

function addSelect2() {
  $('form select').select2();
}
