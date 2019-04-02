[#ftl]
[#import "/WEB-INF/global/pages/bowerComponents.ftl" as components /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#assign globalLibs = ["jquery", "bootstrap", "jquery-ui", "pusher-js", "noty", "countdown", "animate.css", "autogrow-textarea", "jReject", "cookieconsent" ] /]
<!doctype html>
  <html lang="en">
  <head>
  	<meta charset="UTF-8">
  	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
  	[#-- Favicon --]
    <link rel="shortcut icon" href="${baseUrl}/global/images/MARLO_favicon.png" />
    [#-- Keywords --]
    [#if pageKeywords??]<meta name="keywords" content="${pageKeywords}" />[/#if]
    [#-- Description --]
    [#if pageDescription??]<meta name="description" content="${pageDescription}" />[/#if]
    [#-- Google SEO - Refer to http://support.google.com/webmasters/bin/answer.py?hl=en&answer=79812 --]
    [#if robotAccess??]
      <meta name="robots" content="${robotAccess}" />
      <meta name="googlebot" content="${robotAccess}" />
    [/#if]
    [#-- Title --]
    <title>[#include "/WEB-INF/global/pages/titleGenerator.ftl" /]</title>
    [#compress]
  	[#-- Importing Global CSS files --]
  	[#if globalLibs??][#list globalLibs as libraryName][@components.css_imports libraryName=libraryName/][/#list][/#if]
    
    [#-- Importing Page CSS files --]
    [#if pageLibs??][#list pageLibs as libraryName][@components.css_imports libraryName=libraryName/][/#list][/#if]
    
    [#-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries --]
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    [#-- Second, import global javascripts and templates. --]
    <link rel="stylesheet" type="text/css" href="${baseUrl}/global/css/global.css?20190402" />
    <link rel="stylesheet" type="text/css" href="${baseUrl}/global/css/jquery-ui.custom.css" />
    [#if centerGlobalUnit]
      <link rel="stylesheet" type="text/css" href="${baseUrl}/global/css/global-center.css" />
    [/#if]
    
    [#-- Import the custom CSS --]
    [#if customCSS??][#list customCSS as css]<link rel="stylesheet" type="text/css" href="${css}" />[/#list][/#if] 
    [/#compress]
    
    [#-- Cookie Consent --]
    <script>
      window.addEventListener("load", function(){
      window.cookieconsent.initialise({
        "palette": {
          "popup": {
            "background": "#f9fbfc",
            "text": "#3f5259"
          },
          "button": {
            "background": "#1983ab",
            "text": "#ffffff"
          }
        },
        "content": {
            "message": "[@s.text name="global.cookieConsentMessage" /]",
            "href": "${baseUrl}/legalInformation.do#privacyNotice"
        },
        "onInitialise": function(status) {
          if(status == "dismiss"){
            // Enable Google Analytics here
          }
        }
        
      })});
    </script>
    
    [#-- Google Analytics --]
    <script>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
      
      ga('create', 'UA-86349544-1', 'cgiar.org');
      ga('set', 'userId', '${(currentUser.composedID)!"Unknown"}' ); // Set the user ID using signed-in user_id.
      ga('send', 'pageview');   
    </script>
    
  </head>
  <body class="mode-${editable?string('editable', 'readOnly')}">
    [#if !(avoidHeader!false)]
      <header class="clearfix" style="display: ${((includeHeader)??)?string('none','block')}">
        [#-- Board Messages --]
        [#include "/WEB-INF/global/pages/boardMessage.ftl" /]
        
      
        [#-- MARLO Admin Menu --]
        [#include "/WEB-INF/global/pages/superadmin-menu.ftl" /]
        
        <div class="container">
          <div class="marlo-header">
            [#-- MARLO Title --]
            <div id="marlo-logo" class="animated fadeIn">
              <a href="${baseUrl}">
                <div id="title" >MARLO</div>    
                <div id="subTitle" class="visible-md-block visible-lg-block">Managing Agricultural Research for Learning & Outcomes</div>
                <div class="clearfix"></div>
              </a>
            </div>
            [#-- GlobalUnit Image/Logo--]
            [#if namespace?contains('superadmin')]
              <img id="crp-image" src="${baseUrl}/global/images/cgiar.png" alt="" />
            [#else]
              [#if crpSession??]<img id="crp-image" src="${baseUrl}/global/images/crps/${crpSession}.png" alt="${crpSession}" />[/#if]
            [/#if]
            <div class="clearfix"></div>
            [#-- Testing Environment --]
            [#if !config.production]<h4 class="testEnvironment"><span class="label label-danger text-left">Testing Environment</span> </h4>[/#if]
            [#-- Closed --]
            <h4 class="">[#if crpClosed] <span class="label label-default text-left">Closed</span> [/#if]</h4>
          </div>
          
        </div>
      </header>
    [/#if]
    <!-- ${actionName} Content -->
    
    [#-- Timer message --]
    <div class="" style="display:none">
      <div id="timer-content">
        <div class="message">{{message}}</div>
        <div class="finishedMessage" style="display:none">The server is being restarted right now, please refresh this page in 3 minutes approx.</div>
        <div class="countdown"></div>
        <div class="clearfix"></div>
      </div>
    </div>
  