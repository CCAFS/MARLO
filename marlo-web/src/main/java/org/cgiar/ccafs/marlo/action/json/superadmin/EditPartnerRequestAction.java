package org.cgiar.ccafs.marlo.action.json.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

public class EditPartnerRequestAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 821788435993637711L;
  // Managers
  private final PartnerRequestManager partnerRequestManager;
  private final InstitutionTypeManager institutionTypeManager;
  private final LocElementManager locElementManager;
  // Variables
  private String requestID;
  private String name;
  private String acronym;
  private String webPage;
  private String type;
  private String country;
  private String modificationJustification;
  private boolean success;


  @Inject
  public EditPartnerRequestAction(APConfig config, PartnerRequestManager partnerRequestManager,
    InstitutionTypeManager institutionTypeManager, LocElementManager locElementManager) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.institutionTypeManager = institutionTypeManager;
    this.locElementManager = locElementManager;
  }


  @Override
  public String execute() throws Exception {
    success = true;
    try {
      PartnerRequest partnerRequest = partnerRequestManager.getPartnerRequestById(Long.parseLong(requestID));
      boolean isEdited = false;

      if (name != null && !name.isEmpty()) {
        partnerRequest.setPartnerName(name);
        isEdited = true;
      } else {
        partnerRequest.setPartnerName("");
      }
      if (acronym != null && !acronym.isEmpty()) {
        partnerRequest.setAcronym(acronym);
        isEdited = true;
      } else {
        partnerRequest.setAcronym("");
      }
      if (webPage != null && !webPage.isEmpty()) {
        partnerRequest.setWebPage(webPage);
        isEdited = true;
      } else {
        partnerRequest.setWebPage("");
      }
      if (type != null && !type.isEmpty()) {
        Long typeID = Long.parseLong(type);
        if (typeID != null && typeID != 0) {
          InstitutionType institutionType = institutionTypeManager.getInstitutionTypeById(typeID);
          partnerRequest.setInstitutionType(institutionType);
          isEdited = true;
        }
      }
      if (country != null && !country.isEmpty()) {
        LocElement locElement = locElementManager.getLocElementByISOCode(country);
        partnerRequest.setLocElement(locElement);
        isEdited = true;
      }
      if (modificationJustification != null && !modificationJustification.isEmpty()) {
        partnerRequest.setModificationJustification(modificationJustification);
        isEdited = true;
      }
      // If the PartnerRequest is edited, save it
      if (isEdited) {
        partnerRequest.setModifiedBy(this.getCurrentUser());
        partnerRequest.setModified(true);
        partnerRequest.setActiveSince(new Date());
        partnerRequestManager.savePartnerRequest(partnerRequest);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      success = false;
    }
    return SUCCESS;
  }


  public String getAcronym() {
    return acronym;
  }


  public String getCountry() {
    return country;
  }


  public String getModificationJustification() {
    return modificationJustification;
  }


  public String getName() {
    return name;
  }


  public String getRequestID() {
    return requestID;
  }


  public String getType() {
    return type;
  }


  public String getWebPage() {
    return webPage;
  }


  public boolean isSuccess() {
    return success;
  }


  @Override
  public void prepare() throws Exception {
    success = true;
    try {
      Map<String, Parameter> parameters = this.getParameters();
      requestID = StringUtils.trim(parameters.get(APConstants.PARTNER_REQUEST_ID).getMultipleValues()[0]);
      name = StringUtils.trim(parameters.get(APConstants.INSTITUTION_NAME).getMultipleValues()[0]);
      acronym = StringUtils.trim(parameters.get("institutionAcronym").getMultipleValues()[0]);
      webPage = StringUtils.trim(parameters.get("institutionWebPage").getMultipleValues()[0]);
      type = StringUtils.trim(parameters.get(APConstants.INSTITUTION_TYPE_REQUEST_ID).getMultipleValues()[0]);
      country = StringUtils.trim(parameters.get(APConstants.COUNTRY_REQUEST_ID).getMultipleValues()[0]);
      modificationJustification =
        StringUtils.trim(parameters.get(APConstants.JUSTIFICATION_REQUEST).getMultipleValues()[0]);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      success = false;
    }
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRequestID(String requestID) {
    this.requestID = requestID;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }


  public void setType(String type) {
    this.type = type;
  }


  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }


}
