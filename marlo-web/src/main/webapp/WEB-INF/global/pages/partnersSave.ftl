[#ftl]
[#assign title = "Insert a partner" /]
[#assign globalLibs = ["jquery", "noty","select2"] /]
[#assign customJS = ["${baseUrl}/js/global/partnersSave.js"] /]
[#assign customCSS = ["${baseUrl}/css/global/partnersSave.css"] /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]


[#include "/WEB-INF/global/pages/header.ftl" /]

  <section>
    <article >
    <div class="title col-xs-12">
      <h3 class=" text-center form-group">[@s.text name="Request a new institution or branch" /]</h3>
    </div>
      [@s.form action="partnerSave" cssClass="pure-form "]
      
      <div class="col-xs-12 form-group">
        [@customForm.yesNoInput name="isBranch" label="Is this institution a branch?"  inverse=false value="" cssClass="text-left " value="false" /]
      </div>
      <div class="selectHeadquater panel tertiary col-xs-12"  style="display:none">
          <div class="panel-body">
            [@customForm.select name="" label="" required=true  i18nkey="Select institution headquarter" listName="" keyFieldName="id"  displayFieldName="composedName" className="" value="" /]
          </div>
        </div>
      [#-- Partner Name --]
      <div id="partnerName" class="col-xs-6 form-group">
        [@customForm.input name="activityPartner.partner.name" required=true className="col-md-6" type="text" i18nkey="Name" /]
      </div>
      
      [#-- Partner Acronym --]
      <div id="partnerAcronym" class="col-xs-6 form-group">
        [@customForm.input name="activityPartner.partner.acronym" required=true type="text" i18nkey="Acronym" /]
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
        [@customForm.input name="partnerWebPage" type="text"  i18nkey="If you know the partner web page please paste the link below" value="https://" /]
      </div>
      
      [#-- Hidden input with message of success --]
      <input type="hidden" id="message.success" value="[@s.text name="partnersSave.successMessage" /]"/>
      
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