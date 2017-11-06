[#ftl]
[#assign title = "Insert a partner" /]
[#assign pageLibs = ["jquery", "noty","select2"] /]
[#assign customJS = ["${baseUrl}/global/js/partnersSave.js"] /]
[#assign customCSS = ["${baseUrl}/global/css/partnersSave.css"] /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#assign includeHeader = "false" /]
[#assign currentSection = "projects" /]
[#assign currentStage = "partners" /]

[#include "/WEB-INF/crp/pages/header.ftl" /]

  <section> 
    <article>
    [#-- title --]
    <div class="title col-xs-12">
      <h3 class=" text-center form-group">[@s.text name="Request creation of new institution" /]</h3>
    </div>  
    
    [#if !messageSent]
    <div class="slideshow-container col-xs-12 form-group">
      [#-- SLIDE 1 --]
      <div class="mySlides fades">
        <p  class="helpMessage ">
          <span class=""> </span> Please enter the institution as “Partner,” the legal entity. <br />
          <span class=""> </span> The name of the institution should be in its official language. (e.g. For CIAT: Centro Internacional de Agricultura Tropical).
        </p>
      </div>
    
      [@s.form action="${crpSession}/partnerSave" cssClass="pure-form "]
      <div class="clearfix"></div>
      <hr  />
      <div class="col-md-12"> 
        [#-- Partner Acronym --]
        <div id="partnerAcronym" class="col-xs-3 form-group">
          [@customForm.input name="activityPartner.partner.acronym"  type="text" i18nkey="Acronym" /]
        </div>
        [#-- Partner Name --]
        <div id="partnerName" class="col-xs-9 form-group">
          [@customForm.input name="activityPartner.partner.name" required=true className="col-md-6" type="text" i18nkey="Name" /]
        </div> 
        
        [#-- Partner types list --]
        <div id="partnerTypes" class="col-xs-6 form-group">
          [@customForm.select name="activityPartner.partner.institutionType.id" className="institutionTypes" required=true label="" i18nkey="Type" listName="institutionTypesList" keyFieldName="id"  displayFieldName="name" /]
          <div style="display:none">
            [#list institutionTypesList as type]
              <div id="institutionType-${type.id}">
                <strong>${(type.acronym)!} ${(type.name)!'No Name'}</strong><br />
                <i>${(type.description)!'No description'}</i>
              </div>
            [/#list]
          </div>
        </div>
        
        [#-- Countries list --]
        <div id="partnerCountry" class="col-xs-6 form-group">
          [@customForm.select name="locationId" required=true label="" i18nkey="Country" listName="countriesList" keyFieldName="id"  displayFieldName="name" /]        
        </div>
        
        [#-- Web page link --]
        <div id="partnerPage" class="col-xs-12 form-group">
          [@customForm.input name="activityPartner.partner.websiteLink" type="text"  i18nkey="If you know the partner website please paste the link below" value="http://" /]
        </div>
        
        [#-- Hidden input with message of success --]
        <input type="hidden" id="message.success" value="[@s.text name="partnersSave.successMessage" /]"/>
        <input type="hidden" name="projectID" value="${projectID}"/>
        <input type="hidden" name="fundingSourceID" value="${fundingSourceID}"/>

        <div class="clearfix"></div>
        <br />
        <div class="form-group text-center">
          [@s.submit type="button" name="save"] <span class="glyphicon glyphicon-send"></span>  [@s.text name="form.buttons.savePartner.request" /][/@s.submit]
        </div> 
      </div>
      [/@s.form]
      
      [#else]
      
        <div class="col-md-12">
          [#-- Message --]
          <h1 class="text-center brand-success"><span class="glyphicon glyphicon-ok-sign"></span></h1>
          <p  class="text-center col-md-12"> 
            The new partner request was sent succesfully. <br />
            You will receive a confirmation message as soon as it has been processed.
          </p>
          <br />
          <br />
          [#-- Buttons --]
          <div class="text-center">
            <a href="[@s.url][@s.param name="projectID" value=projectID /][/@s.url]" class="btn btn-primary">Request a new one</a>
            <a href="#" class="btn btn-danger" onClick="window.close()">Close</a>
          </div>
        </div>
        
      [/#if]
      
    </article>
  </section> 
  
  
    [#-- Importing JavaScript files --]
    [#if globalLibs??]
      [#list globalLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
    [/#if]
    
    [#-- Importing JavaScript files --]
    [#if pageLibs??]
      [#list pageLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
    [/#if]
    [#-- import the custom JS and CSS --]
    [#if customJS??][#list customJS as js]<script src="${js}"></script>[/#list][/#if]
</body>