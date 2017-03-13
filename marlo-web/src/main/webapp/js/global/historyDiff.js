$(document).ready(function() {

  console.log('History Differences')
  // Changes detected
  $('p.changesDetected strong').text($('.changedField').length);

  $('.changedField').each(function(index,field) {
    var diffIndex = $(field).classParam('changedFieldId')
    var $diffObj = $('li.diffContent-' + diffIndex);
    var $p = $(field).find('p');
    var added = ($diffObj.find('.added').text() === "true");

    console.log($diffObj.find('.id').text());
    console.log(added);

    if((!added) && ($(field).is('.textArea, .input, .select'))) {
      $(field).prettyTextDiff({
          cleanup: true,
          originalContent: $diffObj.find('.oldValue').text(),
          changedContent: $diffObj.find('.newValue').text(),
          diffContainer: $p
      });
    }

  });

});