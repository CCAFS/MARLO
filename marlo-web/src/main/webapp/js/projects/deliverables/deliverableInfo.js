$(document).ready(init);

function init() {
  /* Init Select2 plugin */
  $('select').select2();

  /* Events select */
  subTypes();
  outcomesAndCoa();
  keyOutputs();

  var optionSelected = $(".typeSelect").find("option:selected");
  if(optionSelected.val() != "-1") {
    var url = baseURL + "/deliverableSubType.do";
    var data = {
      deliverableTypeId: optionSelected.val()
    }
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).success(
        function(m) {
          for(var i = 0; i < m.deliverableSubTypes.length; i++) {
            $(".subTypeSelect").append(
                "<option value='" + m.deliverableSubTypes[i].id + "' >" + m.deliverableSubTypes[i].name + "</option>");
          }
        });
  }
}

function subTypes() {
  var url = baseURL + "/deliverableSubType.do";
  var typeSelect = $(".typeSelect");
  var subTypeSelect = $(".subTypeSelect");
  typeSelect.on("change", function() {

    subTypeSelect.empty();
    subTypeSelect.append("<option value='-1' >Select an option... </option>");
    subTypeSelect.trigger("change.select2");
    var option = $(this).find("option:selected");

    if(option.val() != "-1") {
      var data = {
        deliverableTypeId: option.val()
      }
      $.ajax({
          url: url,
          type: 'GET',
          dataType: "json",
          data: data
      }).success(
          function(m) {
            for(var i = 0; i < m.deliverableSubTypes.length; i++) {
              subTypeSelect.append("<option value='" + m.deliverableSubTypes[i].id + "' >"
                  + m.deliverableSubTypes[i].name + "</option>");
            }
          });
    }
  });
}

function outcomesAndCoa() {
  var url = baseURL + "/programOutcomeList.do";
  var flagshipSelect = $(".flagship");
  var outcomeSelect = $(".outcome");
  var clusterSelect = $(".cluster");
  var keyOutputSelect = $(".keyOutput");

  flagshipSelect.on("change", function() {
    outcomeSelect.empty();
    clusterSelect.empty();
    keyOutputSelect.empty();
    outcomeSelect.append("<option value='-1' >Select an option... </option>");
    clusterSelect.append("<option value='-1' >Select an option... </option>");
    keyOutputSelect.append("<option value='-1' >Select an option... </option>");
    outcomeSelect.trigger("change.select2");
    clusterSelect.trigger("change.select2");
    keyOutputSelect.trigger("change.select2");
    var option = $(this).find("option:selected");
    console.log(option.val());
    if(option.val() != "-1") {
      var data = {
        crpProgramID: option.val()
      }
      $.ajax({
          url: url,
          type: 'GET',
          dataType: "json",
          data: data
      }).success(
          function(m) {
            for(var i = 0; i < m.programOutcomes.length; i++) {
              outcomeSelect.append("<option value='" + m.programOutcomes[i].id + "' >"
                  + m.programOutcomes[i].description + "</option>");
            }
            for(var i = 0; i < m.clusterOfActivities.length; i++) {
              clusterSelect.append("<option value='" + m.clusterOfActivities[i].id + "' >"
                  + m.clusterOfActivities[i].description + "</option>");
            }
          });
    }
  });
}

function keyOutputs() {
  var url = baseURL + "/keyOutputList.do";
  var clusterSelect = $(".cluster");
  var keyOutputSelect = $(".keyOutput");
  clusterSelect.on("change", function() {

    keyOutputSelect.empty();
    keyOutputSelect.append("<option value='-1' >Select an option... </option>");
    keyOutputSelect.trigger("change.select2");
    var option = $(this).find("option:selected");
    var data = {
      clusterActivityID: option.val()
    }
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).success(
        function(m) {
          console.log(m);
          for(var i = 0; i < m.keyOutputs.length; i++) {
            keyOutputSelect.append("<option value='" + m.keyOutputs[i].id + "' >" + m.keyOutputs[i].description
                + "</option>");
          }
        });
  });
}