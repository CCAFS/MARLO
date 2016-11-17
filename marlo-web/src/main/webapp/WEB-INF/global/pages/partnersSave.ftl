[#ftl]
[#assign title = "Insert a partner" /]
[#assign globalLibs = ["jquery", "noty","select2"] /]
[#assign customJS = ["${baseUrl}/js/global/partnersSave.js"] /]
[#assign customCSS = ["${baseUrl}/css/global/partnersSave.css"] /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#assign includeHeader = "false" /]
[#assign currentSection = "projects" /]
[#assign currentStage = "partners" /]

[#include "/WEB-INF/global/pages/header.ftl" /]

  <section>
    <article >
    [#-- title --]
    <div class="title col-xs-12">
      <h3 class=" text-center form-group">[@s.text name="Request a new institution or branch" /]</h3>
    </div>
    [#include "/WEB-INF/global/pages/generalMessages.ftl" /]
    
    <div class="slideshow-container col-xs-12 form-group">
    [#-- SLIDE 1 --]
    <div class="mySlides fades">
      <p  class="helpMessage ">
        <span class="glyphicon glyphicon-hand-right"> </span> Please enter the institution as “Partner,” the legal entity. <br />
      </p>
    </div>
    [#-- SLIDE 2 --]
    <div class="mySlides fades">
      <p  class="helpMessage">
        <span class="glyphicon glyphicon-hand-right"> </span> The name of the institution should be in its official language. (e.g. For CIAT: Centro Internacional de Agricultura Tropical).
      </p>
    </div>
    [#-- DOTS --]
    <div class="col-xs-12" style="text-align:center">
      <span class="dot" onclick="currentSlide(1)"></span> 
      <span class="dot" onclick="currentSlide(2)"></span> 
    </div>
    </div>
      [@s.form action="${crpSession}/partnerSave" cssClass="pure-form "]
      <div class="clearfix"></div>
      <hr  />
      <div class="col-xs-12 form-group">
        [@customForm.yesNoInput name="isBranch" label="Is this institution a branch?"  inverse=false value="" cssClass="text-left " value="false" /]
      </div>
      <div class="selectHeadquater panel tertiary col-xs-12"  style="display:none">
          <div class="panel-body">
            [@customForm.select name="activityPartner.partner.headqueater.id" label="" required=true  i18nkey="Select institution headquarter" listName="institutions" keyFieldName="id"  displayFieldName="composedName" className="" value="" /]
          </div>
        </div>
        [#-- Partner Acronym --]
      <div id="partnerAcronym" class="col-xs-6 form-group">
        [@customForm.input name="activityPartner.partner.acronym"  type="text" i18nkey="Acronym" /]
      </div>
      [#-- Partner Name --]
      <div id="partnerName" class="col-xs-6 form-group">
        [@customForm.input name="activityPartner.partner.name" required=true className="col-md-6" type="text" i18nkey="Name" /]
      </div>
      
      
      
      [#-- Partner types list --]
      <div id="partnerTypes" class="col-xs-6 form-group">
        [@customForm.select name="activityPartner.partner.type.id" required=true label="" i18nkey="Type" listName="institutionTypesList" keyFieldName="id"  displayFieldName="name" /]
      </div>
      
      [#-- Countries list --]
      <div id="partnerCountry" class="col-xs-6 form-group">
        [@customForm.select name="activityPartner.partner.country.id" required=true label="" i18nkey="Country" listName="countriesList" keyFieldName="id"  displayFieldName="name" /]        
      </div>
      
      [#-- City of location --]
      <div id="partnerCity" class="col-xs-6 form-group">
        [@customForm.input name="activityPartner.partner.city" required=true type="text" i18nkey="City" /]
      </div>
      
      [#-- Web page link --]
      <div id="partnerPage" class="col-xs-12 form-group">
        [@customForm.input name="activityPartner.partner.websiteLink" type="text"  i18nkey="If you know the partner website please paste the link below" value="http://" /]
      </div>
      
      [#-- Hidden input with message of success --]
      <input type="hidden" id="message.success" value="[@s.text name="partnersSave.successMessage" /]"/>
      <input type="hidden" name="projectID" value="${projectID}"/>
      
      <div class="form-group pull-right">
        [@s.submit type="button" name="save"][@s.text name="form.buttons.savePartner.request" /][/@s.submit]
      </div>

      [/@s.form]
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