$(document).ready(function() {

  // Generating hash from form information
  setFormHash();

  /**
   * Events
   */

  // Force change when an file input is changed
  $("input:file").on('change', function() {
    forceChange = true;
  });

});

/**
 * Changes functions
 */

function isChanged() {
  var itChange = ((formBefore != getFormHash()) || forceChange);
  /*console.log({
      formBefore: formBefore,
      getFormHash: getFormHash(),
      forceChange: forceChange,
      itChange: itChange
  })*/
  return itChange;
}

function setFormHash() {
  formBefore = getFormHash();
}

function getFormHash() {
  return getHash($('form:first').serialize());
}

function getHash(str) {
  var hash = 0, i, chr, len;
  if(str.length == 0) {
    return hash;
  }
  for(i = 0, len = str.length; i < len; i++) {
    chr = str.charCodeAt(i);
    hash = ((hash << 5) - hash) + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return hash;
}