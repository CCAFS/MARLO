$(init);
    
function init(){
  updateTipToken();
}

function updateTipToken() {
  $("#updateTokenBtn").on("click", function(event) {   
    console.log(baseURL);
       $.ajax({
          url: baseURL + '/tipTokenService.do',
          type: 'POST',
          dataType: 'json',
          success: function(response) {       
            console.log(response);       
              var token = response.token;
              $("#tipParameter.tokenValue").val(token);
              $('input[name="tipParameter.tokenValue"]').val(token);              
          },
          error: function(error) {
              console.error('Error getting embedded URL:', error);
          }
       });
  });
    
}