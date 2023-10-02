[#ftl]
[#assign title = "Message Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["trumbowyg"] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/marloSLOs.js",  "${baseUrlCdn}/global/js/fieldsValidation.js"
 ] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "marloMessages" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"messageManagement", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]

        <h4 class="sectionTitle">[@s.text name="message.title" /]</h4>
        <div id="systemReset" class="borderBox ">
                                           
                [#--             
                <div class="form-group">
                  [@customForm.textArea name="lastMessage" i18nkey="message.lastMessage" className="description" required=false disabled=true/]
                </div>   
                --] 
                <div class="clearfix"></div>
                <div class="form-group">
                  [@customForm.textArea name="message.messageValue" i18nkey="message.messageValue" className="description" required=true allowTextEditor=true/]
                </div>
                <div class="clearfix"></div>                             
                <br>
                <hr> 
                
                <a class="addButton" href="[@s.url action='deleteMarloMessage'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">[@s.text name="message.deleteMarloMessage" /]</a>
                <div class="clearfix"></div>        
                <br>                                                                              
        </div>
     
                 
        [#-- Cross-Cutting Issues --]
        <h4 class="sectionTitle">[@s.text name="message.messageHistory" /]</h4>
        <div class="issues-list">
        [#if messageHistory?has_content]
          [#list messageHistory as messagePrev]
            [@messageHistoryMacro element=messagePrev name="messageHistory[${messagePrev_index}]" index=messagePrev_index /]
          [/#list]
        [/#if]
        </div>    
       
        [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          </div>
        </div>
        [/@s.form]

        
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro messageHistoryMacro element name index isTemplate=false]
  <div id="messageHistory-${isTemplate?string('template',index)}" class="messageHistory borderBox" style="display:${isTemplate?string('none','block')}">
    
    [#-- Message history --]
    <div class="blockTitle closed">
      [@customForm.textArea name="${name}.messageValue" value="${(element.messageValue)!}" i18nkey="${index}. Message - ${element.activeSince}" className="name" disabled=true /]
    </div>
  </div>
[/#macro]

