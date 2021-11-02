$(document).ready(init);

var meliasAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=5&crpID=';
var meliasArrName = 'fullItemsAssessmentStatus';

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  attachEvents();
}

function attachEvents() {
  if ($('#actualPhase').html() == 'true') {
    loadQualityAssessmentStatus(meliasAjaxURL, meliasArrName);
  }

  // Add a program collaboration
  $('.addEvaluation').on('click', addEvaluation);

  // Remove a program collaboration
  $('.removeEvaluation').on('click', removeEvaluation);

  // Add Evaluation Action
  $('.addEvaluationAction').on('click', addEvaluationAction);

  // Remove Evaluation Action
  $('.removeEvaluationAction').on('click', removeEvaluationAction);

}

function loadQualityAssessmentStatus(ajaxURL, arrName) {
  var currentCrpID = $('#actualCrpID').html();
  
  if (currentCrpID != '-1') {
    var finalAjaxURL = ajaxURL + currentCrpID;
  
    $.ajax({
      url: baseURL + finalAjaxURL,
      async: false,
      success: function (data) {
        var newData = data[arrName].map(function (x) {
          var arr = [];

          arr.push(x.id);
          arr.push(x.assessmentStatus);
          arr.push(x.updatedAt);

          return arr;
        });
        updateQualityAssessmentStatusData(newData);
      }
    });
  }
}

function updateQualityAssessmentStatusData(data) {
  data.map(function (x) {
    var isCheckedAR = $(`#isCheckedAR-${x[0]}`).html();
    var element = document.getElementById(`QAStatusIcon-${x[0]}`);
    var status, iconSrc;

    switch (x[1]) {
      case 'pending':
        status = 'Pending';
        iconSrc = baseURL + '/global/images/pending-icon.svg';
        break;
      case 'in_progress':
        status = 'Quality Assessed (Requires 2nd assessment)';
        iconSrc = baseURL + '/global/images/quality-assessed-icon.svg';
        break;
      case 'quality_assessed':
        status = 'Quality Assessed';
        iconSrc = baseURL + '/global/images/quality-assessed-icon.svg';
        $(`#melia-${x[0]}`).prop('disabled', true);
        $(`#melia-${x[0]}`).next('span').attr('title', 'This item cannot be unchecked because it has been already Quality Assessed');
        break;
    
      default:
        break;
    }

    if (element && isCheckedAR == '1') {
      var imgTag = document.createElement('img');
      var br = document.createElement('br');
      var spanTag = document.createElement('span');
      var text = document.createTextNode(status);
      
      element.innerHTML = '';
      imgTag.style.width = '25px';
      imgTag.src = iconSrc;
      element.appendChild(imgTag);
      element.appendChild(br);
      spanTag.appendChild(text);
      element.appendChild(spanTag);
    }
  });
}

function addEvaluationAction() {
  var $list = $(this).parents(".evaluationActions").find('.list-block');
  var $item = $('#evaluationAction-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.find('textarea').setTrumbowyg();
  $item.show('slow');
  updateIndexes();
}

function removeEvaluationAction() {
  var $item = $(this).parents('.evaluationAction');
  $item.hide(function() {
    $item.remove();
    updateIndexes();
  });
}

function addEvaluation() {
  var $list = $(this).parents("form").find('.listEvaluations');
  var $item = $('#evaluation-template').clone(true).removeAttr("id");
  $list.append($item);

  // Add select
  $item.find('select').select2({
    width: '100%'
  });

  $item.find('textarea').setTrumbowyg();

  $item.show('slow');
  updateIndexes();
}

function removeEvaluation() {
  var $item = $(this).parents('.evaluation');
  $item.hide(function() {
    $item.remove();
    updateIndexes();
  });
}

function updateIndexes() {
  $(".listEvaluations").find(".evaluation").each(function(i,element) {
    $(element).setNameIndexes(1, i);
    $(element).find(".index").html(i + 1);

    // Update actions
    $(element).find(".evaluationAction").each(function(j,evalAction) {
      $(evalAction).setNameIndexes(2, j);
      $(evalAction).find(".index").html(j + 1);
    });

  });
}