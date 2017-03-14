$(document).ready(function() {

  console.log('History Differences')
  // Changes detected
  $('p.changesDetected strong').text($('.changedField').length);

  $('.changedField').each(function(index,field) {
    var diffIndex = $(field).classParam('changedFieldId')
    var $diffObj = $('li.diffContent-' + diffIndex);
    var $p = $(field).find('p');
    var added = ($diffObj.find('.added').text() === "true");
    var oldValue = $diffObj.find('.oldValue').text();
    var newValue = $diffObj.find('.newValue').text();

    console.log($diffObj.find('.id').text());
    console.log(added);

    if((!added) && (oldValue) && ($(field).is('.textArea, .input, .select'))) {
      $(field).prettyTextDiff({
          cleanup: true,
          originalContent: oldValue,
          changedContent: newValue,
          diffContainer: $p
      });
    }

  });

});