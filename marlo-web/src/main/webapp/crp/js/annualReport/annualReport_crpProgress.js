var sloAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=8&crpID=';
var sloArrName = 'fullItemsAssessmentStatus';
var container;

$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  console.log('CRP Progress');

  attachEvents();
}

function attachEvents() {
  if ($('#actualPhase').html() == 'true') {
    var additionalContribution = $('.TA_additionalContribution');
    additionalContribution.css('display', 'none');
  }
  
  // Add item
  $('.addSloTarget').on('change', addSloTarget);
  
  // Remove item
  $('.removeSloTarget').on('click', removeSloTarget);
  
  if ($('#actualPhase').html() == 'true' && $('#isSubmitted').html() == 'true') {
    loadQualityAssessmentStatus(sloAjaxURL, sloArrName);
  }

  $('.qaStatus-button').on('click', function(){
    updateQAStatus($(this));
  });
}

function updateQAStatus(element){
  let $stat = element.siblings('.onoffswitch-radio');
  container = element.siblings('.sloContainerTitleStatusMessage');

  if($stat.val() == 'true'){
    element.removeClass('includeARButton');
    element.addClass('removeARButton');
    element.html('Remove from QA');
    $stat.val('false');
    container.css('width', '76.4%');
  } else {
    element.removeClass('removeARButton');
    element.addClass('includeARButton');
    element.html('Include in QA');
    $stat.val('true');
    container.css('width', '79.5%');
  }
}

function loadQualityAssessmentStatus(ajaxURL, arrName) {
  var currentCrpID = $('#actualCrpID').html();

  if (currentCrpID != '-1') {
    var finalAjaxURL = ajaxURL + currentCrpID;

    $.ajax({
      url: baseURL + finalAjaxURL,
      async: false,
      success: function (data) {
        if (data && Object.keys(data).length != 0) {
          var newData = data[arrName].map(function (x) {
            var arr = [];

            arr.push(x.id);
            arr.push(x.assessmentStatus);
            arr.push(x.updatedAt);

            return arr;
          });
          console.log(data)
          updateQualityAssessmentStatusData(newData);
        }
      }
    });
  }
}

function updateQualityAssessmentStatusData(data) {
  data.map(function (x) {
    var isCheckedARComponent = document.getElementById(`isCheckedAR-${x[0]}`);
    if (isCheckedARComponent) {
      var isCheckedAR = isCheckedARComponent.innerHTML;
      var element = document.getElementById(`containerQAStatus-${x[0]}`);
      var containerElements = isCheckedARComponent.parentElement;
      var removeARBtn = isCheckedARComponent.nextElementSibling;
      var date, status, statusClass;
  
      switch (x[1]) {
        case 'pending':
          status = 'Pending assessment';
          statusClass = 'pending-mode';
          break;
        case 'pending_crp':
          status = 'Pending CRP response';
          statusClass = 'pending-mode';
          break;
        case 'in_progress':
          status = 'Quality Assessed (Requires 2nd assessment)';
          statusClass = 'qualityAssessed-mode';
          break;
        case 'quality_assessed':
          date = new Date((x[2].split('T')[0])).toDateString();
          status = 'SLO Target was Quality Assessed on ' + date;
          statusClass = 'qualityAssessed-mode';
          break;
  
        default:
          break;
      }
  
      if (element && isCheckedAR == 'true') {
        var pTag = document.createElement('p');
        var text = document.createTextNode(status);
        
        
        element.innerHTML = '';
        element.classList.remove('pendingForReview-mode');
        element.classList.add(statusClass);
        pTag.appendChild(text);
        element.appendChild(pTag);
        element.style.backgroundPosition = '442px';
        
        if (x[1] == 'quality_assessed' || x[1] == 'pending') {
          var pMessageTag = document.createElement('p');
          container = element.parentElement;
          if (x[1] == 'quality_assessed') {
            var textMessage = document.createTextNode('As this item has already been Quality Assessed, no changes are recommended');
            container.style.marginLeft = '0';
            removeARBtn.style.display = 'none';
          } else {
            var textMessage = document.createTextNode('As this item is being assessed by the SMO, no changes are recommended');
          }

          containerElements.style.marginBottom = '0';
          containerElements.style.justifyContent = 'center';
          element.style.backgroundPosition = '485px';
          pMessageTag.classList.add('messageQAInfo');
          pMessageTag.appendChild(textMessage);
          container.appendChild(pMessageTag);
        } 
      }
    }
  });
}

function addSloTarget() {
  var $select = $(this);
  var $option = $select.find('option:selected');
  var $list = $(this).parents("form").find('.sloTargetsList');
  var $item = $('#sloTarget-template').clone(true).removeAttr("id");

  console.log('Change');

  // Verify repeated selection
  var $repeatedElement = $list.find('.indicatorTargetID[value="' + $option.val() + '"]');
  if ($repeatedElement.length) {
    $select.val('-1').trigger('change.select2');
    $repeatedElement.parent().animateCss('shake');
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'It was already selected';
    noty(notyOptions);
    return;
  }

  var name = $option.text().split(/-(.+)?/);
  $item.find('.name').html("<strong>" + name[0] + "</strong> <br>" + name[1]);
  $item.find('input.indicatorTargetID').val($option.val());

  // Show the element
  $item.appendTo($list).hide().show('slow', function () {
    $select.val('-1').trigger('change.select2');
  });

  updateIndexes();
}

function removeSloTarget() {
  var $item = $(this).parents('.sloTarget');
  $item.hide(function () {
    $item.remove();
    updateIndexes();
  });
}

function updateIndexes() {
  $(".sloTargetsList").find(".sloTarget").each(function (i, element) {
    $(element).setNameIndexes(1, i);
    $(element).find(".index").html(i + 1);
  });
}