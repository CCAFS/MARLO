[#ftl]
[#import "bowerComponents.ftl" as components /]
[#assign globalLibs = ["jquery", "jquery-ui", "animate.css", "autogrow-textarea", "jReject" ] /]
<!doctype html>
  <html lang="en">
  <head>
  	<meta charset="UTF-8" />
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
    <title>${(title)!"CCAFS Activity Planning"}</title> 
    [#-- This file must be called before close the body tag in order to allow first the page load --]
    <!-- Support for lower versions of IE 9 -->
    <!--[if lt IE 9]>
      <script src="${baseUrl}/js/libs/html5shiv/html5shiv.js"></script>
    <![endif]--> 
    
    [#compress]
  	[#-- Importing Global CSS files --]
  	[#if globalLibs??]
      [#list globalLibs as libraryName][@components.css_imports libraryName=libraryName/][/#list]
    [/#if]
    
    [#-- Importing Page CSS files --]
    [#if pageLibs??]
      [#list pageLibs as libraryName][@components.css_imports libraryName=libraryName/][/#list]
    [/#if]
    [/#compress]
    [#-- Second, import global javascripts and templates. --]
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/global/reset.css" />
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/global/custom-forms-min.css" />
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/global/global.css" /> 
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/global/onoffswitch.css" />
    <!--[if lte IE 8]>
      <link rel="stylesheet" type="text/css" href="${baseUrl}/css/global/ie8.css"/> 
    <![endif]-->
    [#-- Import the custom CSS --]
    [#if customCSS??]
      [#list customCSS as css]<link rel="stylesheet" type="text/css" href="${css}" />[/#list]
    [/#if]
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