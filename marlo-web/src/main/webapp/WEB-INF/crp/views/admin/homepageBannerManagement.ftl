[#ftl]
[#assign title = "Homepage Banner" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/fieldsValidation.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/homepageBanner.css?20260831" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "homepageBannerManagement" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"homepageBannerManagement", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<div class="animated flipInX container viewMore-block containerAlertMargin">
  <div class="containerAlert alert-leftovers alertColorBackgroundInfo" id="containerAlert">
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-exclamation.png" />
      </div>
    </div>
    <div class="containerText col-md-12">
      <p class="alertText">
        [@s.text name="homepageBannerManagement.help" /]
      </p>
    </div>
    <div class="viewMoreCollapse closed"></div>
  </div>
</div>

<section class="marlo-content">
  <div class="container">
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]

        <h4 class="sectionTitle">[@s.text name="homepageBannerManagement.title" /]</h4>

        <div class="borderBox homepageBannerForm">

          [#-- Title and description. Neither is required: an empty banner is how the homepage banner is hidden. --]
          [@customForm.input name="homepageBanner.title" i18nkey="homepageBannerManagement.field.title"
            placeholder="homepageBannerManagement.field.title.placeholder" required=false editable=true /]

          [#-- The label is rendered here instead of by the macro: the shared textArea macro leaves a blank space
               before the colon, which reads as "Description :". --]
          <div class="homepageBannerDescription">
            <label class="editable" for="homepageBanner.description">[@s.text name="homepageBannerManagement.field.description" /]:</label>
            [@customForm.textArea name="homepageBanner.description" showTitle=false
              required=false editable=true className="homepageBannerDescriptionText" /]
          </div>

          [#-- Image. Shown through the very same route the homepage uses, so what the administrator sees here is
               exactly what a visitor sees. --]
          <div class="homepageBannerImage">
            <label class="editable">[@s.text name="homepageBannerManagement.field.image" /]:</label>

            [#if (homepageBanner.imageFileName)?has_content]
              <div class="homepageBannerImage__current">
                <p class="homepageBannerImage__label">[@s.text name="homepageBannerManagement.field.image.current" /]</p>
                <img src="${baseUrl}/data/homepageBannerImage.do?acronym=${(crpSession)!}"
                  alt="[@s.text name="homepageBannerManagement.field.image.current" /]" />
                <div class="homepageBannerImage__remove">
                  [@customForm.checkbox name="removeImage" value="true"
                    i18nkey="homepageBannerManagement.field.image.remove" editable=true /]
                </div>
              </div>
            [#else]
              <p class="homepageBannerImage__label">[@s.text name="homepageBannerManagement.field.image.none" /]</p>
            [/#if]

            <input type="file" id="image" name="image" accept="image/png,image/jpeg,image/svg+xml" />
            <p class="homepageBannerImage__hint">[@s.text name="homepageBannerManagement.field.image.hint" /]</p>
          </div>

        </div>

        [#-- Section Buttons --]
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
