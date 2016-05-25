[#ftl]
[#import "bowerComponents.ftl" as components /]
[#assign globalLibs = ["jquery", "bootstrap", "jquery-ui", "animate.css", "autogrow-textarea", "jReject" ] /]
<!doctype html>
  <html lang="en">
  <head>
  	<meta charset="UTF-8" />
  	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
  	[#-- Favicon --]
    <link rel="shortcut icon" href="${baseUrl}/images/global/favicon.ico" />
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
    <title>${(title)!"CCAFS Activity Planning"}</title> 
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
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/global/global.css" />
    
    [#-- Import the custom CSS --]
    [#if customCSS??][#list customCSS as css]<link rel="stylesheet" type="text/css" href="${css}" />[/#list][/#if] 
    [/#compress]
  </head>
  <body>
  [#include "/WEB-INF/global/pages/boardMessage.ftl" /]
  [#if !(avoidHeader!false)]
    <div class="container">  
      <header class="clearfix">
        <div id="mainLogo">MARLO </div>    
        <div id="autoSavingMessages">
          <p id="saving" style="display:none;" >
            <img src="${baseUrl}/images/global/saving.gif" alt="Saving information" />
            [@s.text name="saving.saving" /]
          </p>
          <p id="saved" style="display:none;" >[@s.text name="saving.saved" /]</p>
          <p id="problemSaving" style="display:none;" >[@s.text name="saving.problem" /] </p>
        </div> 
      </header>
     </div> 
  [/#if]
  <!-- ${actionName} Content -->