[#ftl]

<div id="guide-button" class="guide-button" >
  <img src="${baseUrlCdn}/global/images/guideButton.png" />
</div>


  <div class="popup-guide animate__animated animate__bounce" id="guide">
    <div class="button-exit close-modal-evidences">
      <div class="x-close-modal" id="x-close-modal-guide" ></div>
    </div>

    <p class="title-modal-evidences">Guide to this section</p>
    <div class="line-modal" ></div>

    <div class="text-modal-evidences">
    
    [#if actionName == 'AICCRA/crpDashboard']
      <h3 style="margin-top: 10px;">[@s.text name="home.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="home.popup.description" /]
    [/#if]
    [#if actionName == 'AICCRA/description']
      <h3 style="margin-top: 10px;">[@s.text name="description.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="description.popup.description" /]
    [/#if]
    [#if actionName == 'AICCRA/partners']
      <h3 style="margin-top: 10px;">[@s.text name="partner.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="partner.popup.description" /]
    [/#if]      
    [#if actionName == 'AICCRA/locations']
      <h3 style="margin-top: 10px;">[@s.text name="location.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="location.popup.description" /]
    [/#if]
    [#if actionName == 'AICCRA/contributionsCrpList' || actionName =='AICCRA/contributionCrp']
      <h3 style="margin-top: 10px;">[@s.text name="contribution.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="contribution.popup.description" /]
    [/#if]
    [#if actionName == 'AICCRA/studies' || actionName =='AICCRA/study']
      <h3 style="margin-top: 10px;">[@s.text name="oicrs.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="oicrs.popup.description" /]
    [/#if]
    [#if actionName == 'AICCRA/deliverable' || actionName =='AICCRA/deliverableList']
      <h3 style="margin-top: 10px;">[@s.text name="deliverable.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="deliverable.popup.description" /]
    [/#if] 
    [#if actionName == 'AICCRA/innovationsList' || actionName =='AICCRA/innovation']
      <h3 style="margin-top: 10px;">[@s.text name="innovation.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="innovation.popup.description" /]
    [/#if] 
    [#if actionName == "AICCRA/activities" ]
      <h3 style="margin-top: 10px;">[@s.text name="activity.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="activity.popup.description" /]
    [/#if] 
    [#if actionName == "AICCRA/budgetByPartners" ]
      <h3 style="margin-top: 10px;">[@s.text name="budget.popup.title" /]</h3>   
      <div class="text-inter">
        [@s.text name="budget.popup.description" /]
    [/#if] 
    </div> 
    </div>

    <div class="container-buttons-evidences">
      
      <a  target="_blank">
        <div class="button-pdf-modal" >
          <p>Read full guidance</p>
          <img src="${baseUrlCdn}/global/images/pdf.png" alt="Download document" />
        </div>
      </a>

    </div>
    
  </div>

  <script>
   
    $('#guide-button').click(function() {
      if ($('.popup-guide').is(':visible')) {
        $('.popup-guide').slideUp();
      } else {
        
        $('.popup-guide').slideDown();
        
      }
    });
    
    $('#x-close-modal-guide').click(function() {     
        $('.popup-guide').slideUp();
    });
    
    
  </script>