$(document).ready(init);

function init() {

  // Select2
  $('select').select2({
    width: '100%'
  });

  // Set visible publications questions
  $('.publicationMetadataBlock').show();

  // Attaching events
  attachEvents();
}

function attachEvents() {

}