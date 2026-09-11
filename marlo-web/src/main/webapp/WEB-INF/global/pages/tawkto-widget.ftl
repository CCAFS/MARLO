[#ftl]
[#-- The site id is resolved from the state of the session, the same way in every environment. The server
     property is read only while nobody is signed in, which is what puts the chat on the login screen. Once the
     user signs in the global unit owns the chat, so its crp_taw_api parameter becomes the only source: a
     parameter that is missing or empty means that global unit has no chat, not that the server one takes over.
     Pointing an environment at the right chat is therefore a configuration duty: the property belongs to the
     server, the parameter to the global unit. --]
[#if logged]
  [#-- specificityValue only reads the session, where the parameters of the global unit were left at sign-in,
       and answers null when the key is not there, so it is safe before a global unit has been chosen. --]
  [#assign tawktoSiteId = ((action.specificityValue('crp_taw_api'))!"")?trim]
[#else]
  [#assign tawktoSiteId = ((config.tawktoSiteId)!"")?trim]
[/#if]
[#-- Without a site id there is no chat to embed, so neither the script nor the button are rendered. --]
[#if tawktoSiteId?has_content]
[#-- A session outside production now reaches the same chat a real user would, so the visitor is tagged and
     support can tell a test conversation from a real one. --]
[#assign userTag][#if !config.production]([#if config.debug]Develop[#else]Testing[/#if])[/#if][/#assign]
<div id="draggable-button" class="hidden-print" style="display:none">
  <p><img src="${baseUrlCdn}/global/images/chatTawkto.png"></p>
</div>

<script type="text/javascript">
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
[/#if]