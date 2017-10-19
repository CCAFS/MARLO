[#ftl]
[#macro partnersList partners={} canEdit=false  namespace="/" defaultAction=""]
  <ul class="list-group">
    [#if partners?has_content]
      [#list partners as partner]
        <li id="partnerRequestItem-${(partner.id)!}" class="list-group-item partnerRequestItem">
          <div class="loading" style="display:none"></div>
          
          [#-- Partner name --]
          <div class="requestInfo">
            <div class="form-group">
               <h4 style="font-family: 'Open Sans';">${partner.partnerInfo}</h4>
               <span class="hiddenTitle" style="display:none">${partner.partnerName}</span>
               [#if partner.webPage??]
                <i>(<a href="${partner.webPage}">${partner.webPage}</a>)</i>
               [/#if]
               <hr />
            </div>
            
            <div class="form-group">
              [#-- Partner type --]
              <p><strong>[@s.text name="Type" /]:</strong> <i>${partner.institutionType.name}</i></p>
              [#-- Country --]
              <p><strong>[@s.text name="Country" /]:</strong> <i class="flag-sm flag-sm-${(partner.countryISO?upper_case)!}"></i> <i>${partner.countryInfo}</i></p>
              [#-- Requested Source --]
              <p><strong>[@s.text name="Requested Source" /]:</strong> <i>${(partner.requestSource)}</i></p>
              [#-- Requested by --]
              <p><strong>[@s.text name="Requested By" /]:</strong> <i>${(partner.createdBy.composedName?html)!'none'}</i></p>
            </div>
            
          </div>
          
          <div class="form-group col-md-8 sameness" style="display:none">
            <br />
            <div class="grayBox">
              <strong>Similar institutions in MARLO:</strong>
              <ul></ul>
            </div>
          </div>
          
          [#-- Action --]
          <div class="btn-group pull-right" role="group" aria-label="..."">
            [#-- Edit --]
            <a class="btn btn-default btn-sm editRequest" href="#">
              <span class="glyphicon glyphicon-pencil"></span> Edit Request
            </a>
            [#-- Accept --]
            <a class="btn btn-success btn-sm" href="[@s.url namespace="" action="superadmin/addPartner"][@s.param name='requestID']${partner.id?c}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-ok"></span> Accept
            </a>
            [#-- Reject --]
            <a class="btn btn-danger btn-sm rejectRequest partnerRequestId-${partner.id}" href="#">
               <span class="glyphicon glyphicon-remove"></span> Reject
            </a>
          </div>
          
          <div class="clearfix"></div>
          
          [#-- Edit Form --]
          <form class="editForm editForm-${(partner.id)!} simpleBox" style="display:none">
            <input type="hidden" name="requestID"  value="${(partner.id)!}"/>
            <div class="form-group row">
              <div class="col-md-3">
                <label for="">Acronym:</label>
                <input type="text" class="form-control input-sm" name="acronym" value="${(partner.acronym)!}" />
              </div>
              <div class="col-md-9">
                <label for="">Name: [@customForm.req required=true /]</label>
                <input type="text" class="form-control input-sm" name="name" value="${(partner.partnerName)!}" />
              </div>
            </div>
            <div class="form-group row">
              <div class="col-md-6">
                [@customForm.select name="type" value=partner.institutionType required=true label="" i18nkey="Institution Type" listName="institutionTypesList" keyFieldName="id"  displayFieldName="name" /]
              </div>
              <div class="col-md-6">
                [@customForm.select name="country" value="'${partner.locElement.isoAlpha2}'" required=true label="" i18nkey="Country ISO Code" listName="countriesList" keyFieldName="isoAlpha2"  displayFieldName="name" /]
              </div>
            </div>
            <div class="form-group">
              <label for="">Web Page: </label>
              <input type="text" class="form-control input-sm" name="webPage" value="${(partner.webPage)!}" />
            </div>
            <hr />
            <div class="form-group">
              <label for="">Justification: [@customForm.req required=true /]</label>
              <textarea class="form-control input-sm" name="modificationJustification" id="" cols="30" rows="3">${(partner.modificationJustification)!}</textarea>
            </div>
            <button class="saveButton">Save</button>
            <button class="cancelButton">Cancel</button>
          </form>
          
          <div class="clearfix"></div>
        </li>
      [/#list]
    [#else]
      <div class="text-center">
        No partner requested yet.
      </div>
    [/#if]
  </ul>
  
[/#macro]

[#macro officesRequest partners={} canEdit=false  namespace="/" defaultAction=""]
  <ul class="list-group">
    [#if partners?has_content]
      [#list partners as partner]
        <li id="officesRequestItem-${(partner.institution.id)!}" class="list-group-item officesRequestItem">
          <form action="">
          <div class="loading" style="display:none"></div>
          
          [#local customName = "countryOfficePOJO"]
          
          [#-- Hidden inputs --]
          <input type="hidden" class="institutionID" name="${customName}.institution.id" value="${partner.institution.id}"/>
          
          [#-- Partner name --]
          <div class="requestInfo">
            <div class="form-group">
               <h4 style="font-family: 'Open Sans';">${partner.institution.composedName}</h4><hr />
            </div>
            
            [#-- Action --]
            <div class="btn-group pull-right" role="group" aria-label="..."">
              [#-- Accept --]
              <a class="btn btn-success btn-sm acceptOfficesRequest institutionOfficeRequestId-${partner.institution.id}" href="#">
                <span class="glyphicon glyphicon-ok"></span> Accept selected
              </a>
              [#-- Reject --]
              <a class="btn btn-danger btn-sm openRejectOfficeRequest institutionOfficeRequestId-${partner.institution.id}" data-toggle="modal" data-target="#rejectOfficeRequest">
                <span class="glyphicon glyphicon-remove"></span>  Reject selected
              </a>
            </div>
            
            <div class="form-group">
              [#-- Country Offices Request --]
              <div class="items-list">
                <ul>
                  [#list partner.partnerRequest as officeRequest]
                    [#local customOfficeName = "${customName}.partnerRequest"]
                    <li class="inputsFlat li-item officeCountryRequest">
                      <input type="checkbox"  name="${customOfficeName}.id" id="officeRequest-${officeRequest.id}" class="officeRequest" value="${officeRequest.id}" />
                      <label class="checkbox-label" for="officeRequest-${officeRequest.id}">${officeRequest.locElement.name}</label>
                      <i class="pull-right flag-sm flag-sm-${(officeRequest.locElement.isoAlpha2?upper_case)!}"></i> 
                      <small>
                        <br />
                        <p class="text-muted" title="Requested in: ${(officeRequest.requestSource?html)!'none'}">
                          <strong>[@s.text name="Requested By" /]:</strong> <i>${(officeRequest.createdBy.composedName?html)!'none'}</i>
                        </p>
                      </small>
                    </li>
                  [/#list]
                </ul>
              </div>
              <div class="clearfix"></div>
            </div>
          </div>
          
          
          
          <div class="clearfix"></div>
          
          </form>
        </li>
      [/#list]
    [#else]
      <div class="text-center">
        No partner requested yet.
      </div>
    [/#if]
  </ul>
[/#macro]