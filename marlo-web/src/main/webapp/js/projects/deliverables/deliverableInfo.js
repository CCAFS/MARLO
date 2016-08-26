$(document).ready(init);

function init() {
  subTypes();
  outcomesAndCoa();
}

function subTypes() {
  var url = baseURL + "/deliverableSubType.do";
  var typeSelect = $(".typeSelect");
  var subTypeSelect = $(".subTypeSelect");
  typeSelect.on("change", function() {

    subTypeSelect.empty();
    var option = $(this).find("option:selected");
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
          console.log(m.deliverableSubTypes[0].id);
          for(var i = 0; i < m.deliverableSubTypes.length; i++) {
            subTypeSelect.append("<option value='" + m.deliverableSubTypes[i].id + "' >"
                + m.deliverableSubTypes[i].name + "</option>");
          }
        });
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
    var option = $(this).find("option:selected");
    console.log(option.val());
    var data = {
      crpProgramID: option.val()
    }
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).success(function(m) {
      console.log(m);
    });
  });
}

function keyOutputs() {
  var url = baseURL + "/??.do";
  var typeSelect = $(".typeSelect");
  var subTypeSelect = $(".subTypeSelect");
  typeSelect.on("change", function() {

    subTypeSelect.empty();
    var option = $(this).find("option:selected");
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
          console.log(m.deliverableSubTypes[0].id);
          for(var i = 0; i < m.deliverableSubTypes.length; i++) {
            subTypeSelect.append("<option value='" + m.deliverableSubTypes[i].id + "' >"
                + m.deliverableSubTypes[i].name + "</option>");
          }
        });
  });
}