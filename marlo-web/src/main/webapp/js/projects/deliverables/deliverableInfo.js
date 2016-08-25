$(document).ready(init);

function init() {
  var url = baseURL + "/deliverableSubType.do";
  var select = $(".selectList select");
  select.on("change", function() {

    var option = $(this).find("option:selected");
    var data = {
      deliverableTypeId: option.val()
    }
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).error(function(m) {

      console.log(m);
    });
  });
}
