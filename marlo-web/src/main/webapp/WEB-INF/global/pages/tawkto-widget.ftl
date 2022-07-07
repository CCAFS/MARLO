[#ftl]
<div id="draggable-button" class="hidden-print" style="display:none">
  <p><span class="glyphicon glyphicon-comment"></span> Chat </p> <span class="status"></span>
</div>

<script type="text/javascript">
  [#-- MARLO Develop ID as default --]
  [#assign tawktoSiteId = "62c753ba7b967b1179988f9b"]
  [#if config.production]
    [#-- MARLO Production Public Key --]
    [#assign tawktoSiteId = "62c753ba7b967b1179988f9b"]
    [#if crpSession?? && logged]
      [#-- MARLO Production CRP Key --]
      [#assign tawktoSiteId = (action.specificityValue('crp_taw_api'))!tawktoSiteId]
    [/#if]
  [/#if]
  [#-- User tag --]
  [#assign userTag][#if !config.production]([#if config.debug]Develop[#else]Testing[/#if])[/#if][/#assign]
  [#-- Tawk.to Widget --]
  var $dragButton = $("#draggable-button");
  
  var Tawk_API=Tawk_API||{}, Tawk_LoadStart=new Date();
  Tawk_LoadStart = new Date();
  Tawk_API.visitor = {
    'name': '${(userTag)!} ${(currentUser.composedCompleteName)!}',
  };
  Tawk_API.onLoad = function() {
    $dragButton.show();
    $dragButton.animateCss('flipInY');
    $dragButton.draggable();
    $dragButton.find('p').on('click', function() {
      Tawk_API.toggle();
    });
    
    Tawk_API.setAttributes({
        'fullName': '${(userTag)!} ${(currentUser.composedCompleteName)!"No Name"}',
        'userName' : '${(currentUser.username)!"No User name"}',
        'userId': '${(currentUser.id)!"No ID"}',
        'email': '${(currentUser.email)!"No User email"}',
        'composedId': '${(currentUser.composedID)!"No Composed ID"}',
        'userTags': '[${(roles)!}${(roles?has_content && liasons?has_content)?string(',','')}${(liasons)!}]'
        
        }, function(error) {
    });
  };
  
  Tawk_API.onChatStarted = function() {
    setCustomEvent('Tawto_CGIAR_Entity', 'onChatStarted', currentCrpSession);
  };

  Tawk_API.onChatEnded = function() {
    setCustomEvent('Tawto_CGIAR_Entity', 'onChatEnded', currentCrpSession);
  };
  
  (function() {
    var s1 = document.createElement("script"), s0 = document.getElementsByTagName("script")[0];
    s1.async = true;
    s1.src = 'https://embed.tawk.to/${tawktoSiteId}/default';
    s1.charset = 'UTF-8';
    s1.setAttribute('crossorigin', '*');
    s0.parentNode.insertBefore(s1, s0);
  })();
 
</script>