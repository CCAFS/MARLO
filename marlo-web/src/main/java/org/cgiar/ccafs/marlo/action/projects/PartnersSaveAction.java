/**
 * ***************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.ActivityPartner;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.ExternalPostUtils;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PartnersSaveAction:
 *
 * @author avalencia - CCAFS
 * @date Oct 30, 2017
 * @time 11:43:29 AM: Add CRP
 * @time 4:25:29 PM: Add clone request to check if there are changes
 */
public class PartnersSaveAction extends BaseAction {

  /**
   *
   */
  private static final long serialVersionUID = -5137162991426442026L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(PartnersSaveAction.class);

  private ActivityPartner activityPartner;

  // Managers
  private LocElementManager locationManager;

  private InstitutionTypeManager institutionManager;
  private InstitutionManager institutionsManager;
  private ProjectManager projectManager;
  private FundingSourceManager fundingSourceManager;
  private PartnerRequestManager partnerRequestManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ICapacityDevelopmentService capacityDevelopmentManager;
  private PowbSynthesisManager powbSynthesisManager;
  private ReportSynthesisManager reportSynthesisManager;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private final SendMailS sendMail;
  // Model
  private List<LocElement> countriesList;
  private List<InstitutionType> institutionTypesList;
  private List<Institution> institutions;
  private long locationId;
  private GlobalUnit loggedCrp;
  private boolean messageSent;
  private String partnerWebPage;
  private String context;
  private int projectID;
  private int fundingSourceID;
  private int powbSynthesisID;
  private int synthesisID;
  private int expectedID;
  private int activityID;
  private int capdevID;
  private String pageRequestName;

  @Inject
  public PartnersSaveAction(APConfig config, LocElementManager locationManager,
    InstitutionTypeManager institutionManager, InstitutionManager institutionsManager, ProjectManager projectManager,
    PartnerRequestManager partnerRequestManager, FundingSourceManager fundingSourceManager,
    GlobalUnitManager crpManager, SendMailS sendMail, ProjectExpectedStudyManager projectExpectedStudyManager,
    ICapacityDevelopmentService capacityDevelopmentManager, PowbSynthesisManager powbSynthesisManager,
    ReportSynthesisManager reportSynthesisManager) {
    super(config);
    this.locationManager = locationManager;
    this.institutionManager = institutionManager;
    this.projectManager = projectManager;
    this.institutionsManager = institutionsManager;
    this.partnerRequestManager = partnerRequestManager;
    this.fundingSourceManager = fundingSourceManager;
    this.crpManager = crpManager;
    this.sendMail = sendMail;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.capacityDevelopmentManager = capacityDevelopmentManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  public void addCapDevMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    CapacityDevelopment capacityDevelopment = capacityDevelopmentManager.getCapacityDevelopmentById(capdevID);
    String sourceMessage = "" + context + " Capdev: (" + capdevID + ") - " + capacityDevelopment.getTitle();
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addFunginMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    FundingSourceInfo fsInfo =
      fundingSourceManager.getFundingSourceById(fundingSourceID).getFundingSourceInfo(this.getActualPhase());
    String sourceMessage = "" + context + " Funding Source: (" + fundingSourceID + ") - " + fsInfo.getTitle();
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addPowbSynthesisMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    PowbSynthesis powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
    String sourceMessage = "" + context + " PowbSynthesis: (" + powbSynthesisID + ") - "
      + powbSynthesis.getLiaisonInstitution().getComposedName();
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addProjectMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    ProjectInfo projectInfo = projectManager.getProjectById(projectID).getProjecInfoPhase(this.getActualPhase());
    String sourceMessage = "";
    if (this.isAiccra()) {
      sourceMessage = "" + context + " Cluster: (" + projectID + ") - " + projectInfo.getTitle();
    } else {
      sourceMessage = "" + context + " Project: (" + projectID + ") - " + projectInfo.getTitle();
    }
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addReportSynthesisMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    ReportSynthesis reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);
    String sourceMessage = "" + context + " Report Synthesis: (" + synthesisID + ") - "
      + reportSynthesis.getLiaisonInstitution().getComposedName();
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addStudyMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    ProjectExpectedStudyInfo studyInfo = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID)
      .getProjectExpectedStudyInfo(this.getActualPhase());
    String sourceMessage = "" + context + " Study: (" + expectedID + ") - " + studyInfo.getTitle();
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public int getActivityID() {
    return activityID;
  }

  public ActivityPartner getActivityPartner() {
    return activityPartner;
  }

  public int getCapdevID() {
    return capdevID;
  }

  public String getContext() {
    return context;
  }

  public List<LocElement> getCountriesList() {
    return countriesList;
  }

  public int getExpectedID() {
    return expectedID;
  }

  public int getFundingSourceID() {
    return fundingSourceID;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public List<InstitutionType> getInstitutionTypesList() {
    return institutionTypesList;
  }

  public long getLocationId() {
    return locationId;
  }

  public int getPowbSynthesisID() {
    return powbSynthesisID;
  }

  public int getProjectID() {
    return projectID;
  }

  public int getSynthesisID() {
    return synthesisID;
  }

  public boolean isMessageSent() {
    return messageSent;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    // Take the project id only the first time the page loads
    if (this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID) != null
      && Integer.parseInt(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)) != 0) {
      projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      pageRequestName = "projects";
      LOG.info("The user {} load the request partner section related to the project {}.",
        this.getCurrentUser().getEmail(), projectID);
    }
    // Take the fundingSource id only the first time the page loads
    if (this.getRequest().getParameter(APConstants.FUNDING_SOURCE_REQUEST_ID) != null
      && Integer.parseInt(this.getRequest().getParameter(APConstants.FUNDING_SOURCE_REQUEST_ID)) != 0) {
      fundingSourceID =
        Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.FUNDING_SOURCE_REQUEST_ID)));
      pageRequestName = "fundingSources";
      LOG.info("The user {} load the request partner section related to the funding source {}.",
        this.getCurrentUser().getEmail(), fundingSourceID);
    }

    // Take the Expected Study id only the first time the page loads
    if (this.getRequest().getParameter(APConstants.EXPECTED_REQUEST_ID) != null
      && Integer.parseInt(this.getRequest().getParameter(APConstants.EXPECTED_REQUEST_ID)) != 0) {
      expectedID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.EXPECTED_REQUEST_ID)));
      pageRequestName = "studies";
      LOG.info("The user {} load the request partner section related to the Study {}.",
        this.getCurrentUser().getEmail(), expectedID);
    }

    // Take the CapDev id only the first time the page loads
    if (this.getRequest().getParameter(APConstants.CAPDEV_ID) != null
      && Integer.parseInt(this.getRequest().getParameter(APConstants.CAPDEV_ID)) != 0) {
      capdevID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
      pageRequestName = "capdev";
      LOG.info("The user {} load the request partner section related to the Capdev {}.",
        this.getCurrentUser().getEmail(), capdevID);
    }

    // Take the POWB Synthesis id only the first time the page loads
    if (this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID) != null
      && Integer.parseInt(this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID)) != 0) {
      powbSynthesisID =
        Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID)));
      pageRequestName = "powbSynthesis";
      LOG.info("The user {} load the request partner section related to the powbSynthesis {}.",
        this.getCurrentUser().getEmail(), powbSynthesisID);
    }

    // Take the AR Synthesis id only the first time the page loads
    if (this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID) != null
      && Integer.parseInt(this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID)) != 0) {
      synthesisID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID)));
      pageRequestName = "reportSynthesis";
      LOG.info("The user {} load the request partner section related to the reportSynthesis {}.",
        this.getCurrentUser().getEmail(), synthesisID);
    }

    this.countriesList = locationManager.findAll().stream()
      .filter(c -> c.isActive() && c.getLocElementType().getId().longValue() == 2).collect(Collectors.toList());
    this.institutionTypesList =
      institutionManager.findAll().stream().filter(c -> c.isActive() && !c.getOld()).collect(Collectors.toList());;
    institutions = institutionsManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    institutions.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
    countriesList.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
    // Get loggerCrp
    try {
      loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());
    }
  }

  @Override
  public String save() {
    String institutionName, institutionAcronym, institutionTypeName, countryId, countryName, partnerWebPage;
    String subject;
    StringBuilder message = new StringBuilder();

    long partnerTypeId;
    // Take the values to create the message
    institutionName = activityPartner.getPartner().getName();
    institutionAcronym = activityPartner.getPartner().getAcronym();
    partnerTypeId = activityPartner.getPartner().getInstitutionType().getId();
    countryId = String.valueOf(locationId);
    partnerWebPage = activityPartner.getPartner().getWebsiteLink();

    // Get the partner type name
    countryName = locationManager.getLocElementById(Long.parseLong(countryId)).getName();

    institutionTypeName = "";
    for (InstitutionType pt : institutionTypesList) {
      if (pt.getId() == partnerTypeId) {
        institutionTypeName = pt.getName();
      }
    }

    // Add Partner Request information.
    PartnerRequest partnerRequest = new PartnerRequest();
    // Add a clone request to check if there are changes later
    PartnerRequest partnerRequestModifications = new PartnerRequest();

    partnerRequest.setPartnerName(institutionName);
    partnerRequestModifications.setPartnerName(institutionName);
    partnerRequest.setAcronym(institutionAcronym);
    partnerRequestModifications.setAcronym(institutionAcronym);
    partnerRequest.setCrp(loggedCrp);
    partnerRequestModifications.setCrp(loggedCrp);

    partnerRequest.setLocElement(locationManager.getLocElementById(Long.parseLong(countryId)));
    partnerRequestModifications.setLocElement(locationManager.getLocElementById(Long.parseLong(countryId)));
    partnerRequest.setInstitutionType(institutionManager.getInstitutionTypeById(partnerTypeId));
    partnerRequestModifications.setInstitutionType(institutionManager.getInstitutionTypeById(partnerTypeId));
    partnerRequest.setOffice(false);
    partnerRequestModifications.setOffice(false);
    partnerRequest.setPhase(this.getActualPhase());
    partnerRequestModifications.setPhase(this.getActualPhase());

    if (partnerWebPage != null && !partnerWebPage.isEmpty()) {
      partnerRequest.setWebPage(partnerWebPage);
      partnerRequestModifications.setWebPage(partnerWebPage);
    }

    // message subject
    subject = this.getText("marloRequestInstitution.email.subject",
      new String[] {this.getCrpSession().toUpperCase(), institutionName});
    // Message Content
    message.append(this.getCurrentUser().getFirstName() + " " + this.getCurrentUser().getLastName() + " ");
    message.append("(" + this.getCurrentUser().getEmail() + ") ");
    message.append("is requesting to add the following partner information:");
    message.append("</br></br>");
    message.append("Partner Name: ");
    message.append(institutionName);
    message.append("</br>");
    message.append("Acronym: ");
    message.append(institutionAcronym);
    message.append(" </br>");
    message.append("Partner type: ");
    message.append(institutionTypeName);
    message.append(" </br>");

    message.append("Headquarter country location: ");
    message.append(countryName);
    message.append(" </br>");

    // Is there a web page?
    if (partnerWebPage != null && !partnerWebPage.isEmpty()) {
      message.append("Web Page: ");
      message.append(partnerWebPage);
      message.append(" </br>");
    }
    message.append(" </br>");

    switch (pageRequestName) {
      case "projects":
        this.addProjectMessage(message, partnerRequest, partnerRequestModifications);
        break;

      case "fundingSources":
        this.addFunginMessage(message, partnerRequest, partnerRequestModifications);
        break;

      case "studies":
        this.addStudyMessage(message, partnerRequest, partnerRequestModifications);
        break;

      case "capdev":
        this.addCapDevMessage(message, partnerRequest, partnerRequestModifications);
        break;

      case "powbSynthesis":
        this.addPowbSynthesisMessage(message, partnerRequest, partnerRequestModifications);
        break;

      case "reportSynthesis":
        this.addReportSynthesisMessage(message, partnerRequest, partnerRequestModifications);
        break;

    }

    boolean postFailed = false;
    if (this.isAiccra()) {

      Map<String, Object> request = new HashMap<>();
      request.put("name", partnerRequest.getPartnerName());
      request.put("acronym", partnerRequest.getAcronym());
      request.put("websiteLink", partnerRequest.getWebPage());
      request.put("institutionTypeCode", partnerRequest.getInstitutionType().getId());
      request.put("hqCountryIso", partnerRequest.getLocElement().getIsoAlpha2());
      request.put("externalUserMail", this.getCurrentUser().getEmail());
      request.put("externalUserName", this.getCurrentUser().getUsername());
      request.put("externalUserComments", "Request made from AICCRA");

      ObjectMapper mapper = new ObjectMapper();

      try {
        String requestStr = mapper.writeValueAsString(request);

        ExternalPostUtils epu = new ExternalPostUtils();
        epu.setUsername(config.getClarisaAPIUsername());
        epu.setPassword(config.getClarisaAPIPassword());
        String responseStr =
          epu.postJson(config.getClarisaAPIHost() + "/api/CCAFS/institutions/institution-requests", requestStr);

        if (responseStr.isEmpty()) {
          postFailed = true;
        }
      } catch (JsonProcessingException ex) {
        java.util.logging.Logger.getLogger(PartnersSaveAction.class.getName()).log(Level.SEVERE, null, ex);
      }

    } else {
      partnerRequest = partnerRequestManager.savePartnerRequest(partnerRequest);
      partnerRequestModifications.setPartnerRequest(partnerRequest);
      partnerRequestModifications.setModified(false);
      partnerRequestModifications = partnerRequestManager.savePartnerRequest(partnerRequestModifications);
    }

    message.append(".</br>");
    message.append("</br>");

    try {
      /*
      if (this.validateEmailNotification()) {
        sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject, message.toString(),
          null, null, null, true);
      }
      */
    } catch (Exception e) {
      LOG.error("unable to send mail", e);
      /**
       * Original code swallows the exception and didn't even log it. Now
       * we at least log it, but we need to revisit to see if we should
       * continue processing or re-throw the exception.
       */
    }
    messageSent = true;

    if (projectID != 0) {
      LOG.info("The user {} send a message requesting add partners to the project {}", this.getCurrentUser().getEmail(),
        projectID);
    }
    if (fundingSourceID != 0) {
      LOG.info("The user {} send a message requesting add partners to the funding source {}",
        this.getCurrentUser().getEmail(), fundingSourceID);
    }

    if (this.isAiccra() && postFailed) {
      return INPUT;
    } else {
      Collection<String> messages = this.getActionMessages();
      this.addActionMessage("message:" + this.getText("saving.saved"));
      messages = this.getActionMessages();
      return SUCCESS;
    }
  }

  public void setActivityID(int activityID) {
    this.activityID = activityID;
  }

  public void setActivityPartner(ActivityPartner activityPartner) {
    this.activityPartner = activityPartner;
  }

  public void setCapdevID(int capdevID) {
    this.capdevID = capdevID;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public void setExpectedID(int expectedID) {
    this.expectedID = expectedID;
  }

  public void setFundingSourceID(int fundingSourceID) {
    this.fundingSourceID = fundingSourceID;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLocationId(long locationId) {
    this.locationId = locationId;
  }

  public void setMessageSent(boolean messageSent) {
    this.messageSent = messageSent;
  }

  public void setPowbSynthesisID(int powbSynthesisID) {
    this.powbSynthesisID = powbSynthesisID;
  }

  public void setProjectID(int projectID) {
    this.projectID = projectID;
  }

  public void setSynthesisID(int synthesisID) {
    this.synthesisID = synthesisID;
  }

  @Override
  public void validate() {
    boolean anyError = false;

    // If the page is loading don't validate
    if (this.getRequest().getMethod().equalsIgnoreCase("post")) {

      // Check the partner name
      if (activityPartner.getPartner().getName().isEmpty()) {
        this.addFieldError("activityPartner.partner.name", this.getText("validation.field.required"));
        anyError = true;
      }
      // Check the institution type
      if (activityPartner.getPartner().getInstitutionType().getId() == -1) {
        this.addFieldError("activityPartner.partner.institutionType.id", this.getText("validation.field.required"));
        anyError = true;
      }

      // Check the location
      if (locationId == -1 || locationId == 0) {
        this.addFieldError("locationId", this.getText("validation.field.required"));
        anyError = true;
      }

      if (anyError) {
        this.addActionError(this.getText("saving.fields.required"));
      }
    }
    super.validate();
  }

  private boolean validateEmailNotification() {
    GlobalUnit globalUnit = loggedCrp;
    Boolean crpNotification = globalUnit.getCustomParameters().stream()
      .filter(c -> c.getParameter().getKey().equalsIgnoreCase(APConstants.CRP_EMAIL_NOTIFICATIONS))
      .allMatch(t -> (t.getValue() == null) ? true : t.getValue().equalsIgnoreCase("true"));
    return crpNotification;
  }

}
