[#ftl]
<script type="text/javascript">
  [#-- MARLO Develop ID as default --]
  [#assign tawktoSiteId = "57864c4b7e9d57372d381198"]
  [#if config.production]
    [#-- MARLO Production Public Key --]
    [#assign tawktoSiteId = "582f0c81f9976a1964b0c240"]
    [#if crpSession?? && logged]
      [#-- MARLO Production CRP Key --]
      [#assign tawktoSiteId = (action.specificityValue('crp_taw_api'))!tawktoSiteId]
    [/#if]
  [/#if]
  [#-- User tag --]
  [#assign userTag][#if !config.production]([#if config.debug]Develop[#else]Testing[/#if])[/#if][/#assign]
  [#-- Tawk.to Widget --]
  var Tawk_API=Tawk_API||{}, Tawk_LoadStart=new Date();
  Tawk_LoadStart = new Date();
  Tawk_API.visitor = {
    'name': '${(userTag)!} ${(currentUser.composedCompleteName)!}',
  };
  Tawk_API.onLoad = function() {
    Tawk_API.setAttributes({
        'fullName': '${(userTag)!} ${(currentUser.composedCompleteName)!"No Name"}',
        'userName' : '${(currentUser.username)!"No User name"}',
        'userId': '${(currentUser.id)!"No ID"}',
        'composedId': '${(currentUser.composedID)!"No Composed ID"}',
        'userTags': '[${(roles)!}${(roles?has_content && liasons?has_content)?string(',','')}${(liasons)!}]'
        
    }, function(error) {
    });
    [#--  Tawk_API.addTags(['MARLO', '${config.production?string('Production','Development')}', '${(crpSession)!}'], function(error){});--]
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