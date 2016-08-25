$(document).ready(init);

function init() {
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
