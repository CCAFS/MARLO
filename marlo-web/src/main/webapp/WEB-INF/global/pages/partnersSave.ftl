[#ftl]
[#assign title = "Insert a partner" /]
[#assign globalLibs = ["jquery", "noty"] /]
[#assign customJS = ["${baseUrl}/js/global/partnerSave.js"] /]

[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]

[#include "/WEB-INF/global/pages/popup-header.ftl" /]
  <section>
    <article class="content container_9">
      <h1>[@s.text name="partnersSave.addPartner" /]</h1>
      [@s.form action="partnerSave" cssClass="pure-form"]

      [#-- Partner Name --]
      <div id="partnerName" class="halfPartBlock ">
        [@customForm.input name="activityPartner.partner.name" type="text" i18nkey="partnersSave.name" /]
      </div>
      
      [#-- Partner Acronym --]
      <div id="partnerAcronym" class="halfPartBlock ">
        [@customForm.input name="activityPartner.partner.acronym" type="text" i18nkey="partnersSave.acronym" /]
      </div>
      
      [#-- Partner types list --]
      <div id="partnerTypes" class="halfPartBlock ">
        [@customForm.select name="activityPartner.partner.type.id" label="" i18nkey="partnersSave.partnerType" listName="institutionTypesList" keyFieldName="id"  displayFieldName="name" /]
      </div>
      
      [#-- Countries list --]
      <div id="partnerCountry" class="halfPartBlock ">
        [@customForm.select name="activityPartner.partner.country.id" label="" i18nkey="partnersSave.country" listName="countriesList" keyFieldName="id"  displayFieldName="name" /]        
      </div>
      
      [#-- City of location --]
      <div id="partnerCity" class="halfPartBlock ">
        [@customForm.input name="activityPartner.partner.city" type="text" i18nkey="partnersSave.city" /]
      </div>
      
     
      
      [#-- Contact point name --]
      <!-- div id="partnerContactName" class="halfPartBlock ">
        [@customForm.input name="activityPartner.contactName" type="text" i18nkey="partnersSave.contactName" /]
      </div -->
      
      [#-- Contact point email --]
      <!-- div id="partnerContactEmail" class="halfPartBlock ">
        [@customForm.input name="activityPartner.contactEmail" type="text" i18nkey="partnersSave.contactEmail" /]
      </div -->
      
      [#-- Web page link --]
      <div id="partnerPage" class="fullPartBlock ">
        [@customForm.input name="partnerWebPage" type="text" i18nkey="partnersSave.webPage" /]
      </div>
      
      [#-- Hidden input with message of success --]
      <input type="hidden" id="message.success" value="[@s.text name="partnersSave.successMessage" /]"/>
            
      <!-- internal parameter -->
      [#if activityID?has_content]<input name="activityID" type="hidden" value="${activityID?c}" />[/#if]
      [#if projectID?has_content]<input name="projectID" type="hidden" value="${projectID?c}" />[/#if]
      <input id="messageSent" type="hidden" value="${messageSent?string}" />
      <div class="grid_9 center">
        [@s.submit type="button" name="save"][@s.text name="form.buttons.savePartner.request" /][/@s.submit]
      </div>
      [/@s.form]
    </article>
  </section> 
  [#include "/WEB-INF/global/pages/js-imports.ftl"]
</body>