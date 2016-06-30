[#ftl]
[#import "bowerComponents.ftl" as components /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#assign globalLibs = ["jquery", "bootstrap", "jquery-ui", "noty", "animate.css", "autogrow-textarea", "jReject" ] /]
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
    <title>${(title)!"MARLO"}</title> 
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
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/global/jquery-ui.custom.css" />
    
    [#-- Import the custom CSS --]
    [#if customCSS??][#list customCSS as css]<link rel="stylesheet" type="text/css" href="${css}" />[/#list][/#if] 
    [/#compress]
  </head>
  <body>
    [#include "/WEB-INF/global/pages/boardMessage.ftl" /]
    [#if !(avoidHeader!false)]
      <header class="clearfix">
        [#if action.canAccessSuperAdmin()]
        [#assign superAdminMenu =[
           { 'slug': 'superadmin',     'name': 'menu.superadmin',    'namespace': '/superadmin',     'action': 'marloBoard', 'visible': action.canAccessSuperAdmin(), 'active': true }
        ]/]
        <div id="superadminBlock">
          <div class="container">
            <ul>
              [#list superAdminMenu as item]
                [#if item.visible]
                <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
                  <a href="[@s.url namespace=item.namespace action=item.action ][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
                    [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if][@s.text name=item.name ][@s.param]${(crpSession?upper_case)!'CRP'}[/@s.param] [/@s.text]
                  </a>
                </li>
                [/#if]
              [/#list]
              <li><a href="" onclick="return false"><span class="glyphicon glyphicon-chevron-down"></span>  CRP (${(crpSession?upper_case)!})</a>
                <ul class="subMenu">
                  [#list crpList as crp]
                    <li class="[#if crpSession?? && crpSession == crp.name?lower_case ]currentSection[/#if]">
                      <a href="[@s.url namespace="/" action="${crp.name?lower_case}/dashboard" ][@s.param name="edit" value="true"/][/@s.url]">${crp.name}</a>
                    </li>
                  [/#list]
                </ul>
               </li>
               <li class="pull-left"> <span class="glyphicon glyphicon-th-list"></span> MARLO Admin Menu</li>
            </ul>
          </div>
        </div>
        [/#if]
        <div class="container">
          <div id="marlo-logo" class="animated fadeIn">
            <div id="title" >MARLO</div>    
            <div id="subTitle" class="visible-md-block visible-lg-block">Managing Agricultural Research for Learning & Outcomes</div>
            <div class="clearfix"></div>
            [#if !config.production] <h4 class="testEnvironment"><span class="label label-danger text-left">Testing Environment</span></h4> [/#if]
          </div>
          [#if crpSession??]
            <img id="crp-image" src="${baseUrl}/images/global/crps/${crpSession}.png" alt="${crpSession}" />
          [/#if]
        </div>
      </header>
    [/#if]
    <!-- ${actionName} Content -->