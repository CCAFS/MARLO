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
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.ExternalPostUtils;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
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
  // True when the partner request could not be sent to CLARISA, so the page shows the form again with an error
  private boolean requestFailed;
  // True when the form arrived without the institution type or the country, so it is shown again with an error
  private boolean missingRequiredFields;
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
  private ProjectInfo projectInfo;

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
    String sourceMessage = "" + context + " Capdev: (" + capdevID + ") - "
      + (capacityDevelopment != null ? capacityDevelopment.getTitle() : "");
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addFunginMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingSourceID);
    Phase phase = this.getActualPhase();
    FundingSourceInfo fsInfo = (fundingSource != null && phase != null && phase.getId() != null)
      ? fundingSource.getFundingSourceInfo(phase) : null;
    String sourceMessage = "" + context + " Funding Source: (" + fundingSourceID + ") - "
      + (fsInfo != null ? fsInfo.getTitle() : "");
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addPowbSynthesisMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    PowbSynthesis powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
    LiaisonInstitution liaisonInstitution = powbSynthesis != null ? powbSynthesis.getLiaisonInstitution() : null;
    String sourceMessage = "" + context + " PowbSynthesis: (" + powbSynthesisID + ") - "
      + (liaisonInstitution != null ? liaisonInstitution.getComposedName() : "");
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addProjectMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    if (projectInfo == null) {
      Project requestProject = projectManager.getProjectById(projectID);
      projectInfo = requestProject != null ? requestProject.getProjecInfoPhase(this.getActualPhase()) : null;
    }
    String sourceMessage =
      "" + context + " Cluster: (" + projectID + ") - " + (projectInfo != null ? projectInfo.getTitle() : "");
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addReportSynthesisMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    ReportSynthesis reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);
    LiaisonInstitution liaisonInstitution = reportSynthesis != null ? reportSynthesis.getLiaisonInstitution() : null;
    String sourceMessage = "" + context + " Report Synthesis: (" + synthesisID + ") - "
      + (liaisonInstitution != null ? liaisonInstitution.getComposedName() : "");
    message.append(sourceMessage);
    partnerRequest.setRequestSource(sourceMessage);
    partnerRequestModifications.setRequestSource(sourceMessage);
  }

  public void addStudyMessage(StringBuilder message, PartnerRequest partnerRequest,
    PartnerRequest partnerRequestModifications) {
    ProjectExpectedStudy study = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);
    Phase phase = this.getActualPhase();
    ProjectExpectedStudyInfo studyInfo = (study != null && phase != null && phase.getId() != null)
      ? study.getProjectExpectedStudyInfo(phase) : null;
    String sourceMessage =
      "" + context + " Study: (" + expectedID + ") - " + (studyInfo != null ? studyInfo.getTitle() : "");
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

  public boolean isMissingRequiredFields() {
    return missingRequiredFields;
  }

  public boolean isRequestFailed() {
    return requestFailed;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    // Take the project id only the first time the page loads
    int requestProjectID = readRequestId(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID));
    if (requestProjectID != 0) {
      projectID = requestProjectID;
      pageRequestName = "projects";
      LOG.info("The request partner section was loaded for the project {}", projectID);
    }

    // Take the fundingSource id only the first time the page loads
    int requestFundingSourceID = readRequestId(this.getRequest().getParameter(APConstants.FUNDING_SOURCE_REQUEST_ID));
    if (requestFundingSourceID != 0) {
      fundingSourceID = requestFundingSourceID;
      pageRequestName = "fundingSources";
      LOG.info("The request partner section was loaded for the funding source {}", fundingSourceID);
    }

    // Take the Expected Study id only the first time the page loads
    int requestExpectedID = readRequestId(this.getRequest().getParameter(APConstants.EXPECTED_REQUEST_ID));
    if (requestExpectedID != 0) {
      expectedID = requestExpectedID;
      pageRequestName = "studies";
      LOG.info("The request partner section was loaded for the study {}", expectedID);
    }

    // Take the CapDev id only the first time the page loads
    int requestCapdevID = readRequestId(this.getRequest().getParameter(APConstants.CAPDEV_ID));
    if (requestCapdevID != 0) {
      capdevID = requestCapdevID;
      pageRequestName = "capdev";
      LOG.info("The request partner section was loaded for the CapDev {}", capdevID);
    }

    // Take the POWB Synthesis id only the first time the page loads
    int requestPowbSynthesisID = readRequestId(this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID));
    if (requestPowbSynthesisID != 0) {
      powbSynthesisID = requestPowbSynthesisID;
      pageRequestName = "powbSynthesis";
      LOG.info("The request partner section was loaded for the POWB synthesis {}", powbSynthesisID);
    }

    // Take the AR Synthesis id only the first time the page loads
    int requestSynthesisID = readRequestId(this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID));
    if (requestSynthesisID != 0) {
      synthesisID = requestSynthesisID;
      pageRequestName = "reportSynthesis";
      LOG.info("The request partner section was loaded for the report synthesis {}", synthesisID);
    }

    // findAll() returns null on an empty table, and a row can miss its type or its old flag
    List<LocElement> allLocations = locationManager.findAll();
    this.countriesList = allLocations == null ? new ArrayList<>()
      : allLocations.stream().filter(c -> c != null && c.isActive() && c.getLocElementType() != null
        && c.getLocElementType().getId() != null && c.getLocElementType().getId().longValue() == 2)
        .collect(Collectors.toList());
    List<InstitutionType> allInstitutionTypes = institutionManager.findAll();
    this.institutionTypesList = allInstitutionTypes == null ? new ArrayList<>()
      : allInstitutionTypes.stream().filter(c -> c != null && c.isActive() && !Boolean.TRUE.equals(c.getOld()))
        .collect(Collectors.toList());
    List<Institution> allInstitutions = institutionsManager.findAll();
    institutions = allInstitutions == null ? new ArrayList<>()
      : allInstitutions.stream().filter(c -> c != null && c.isActive()).collect(Collectors.toList());

    // Same order as before; a missing name sorts last instead of throwing
    institutions.sort(Comparator.comparing(Institution::getName, Comparator.nullsLast(Comparator.naturalOrder())));
    countriesList.sort(Comparator.comparing(LocElement::getName, Comparator.nullsLast(Comparator.naturalOrder())));
    // Get loggerCrp
    try {
      loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());
    }
  }

  /**
   * Read an id the page was opened with. A missing, blank or non-numeric value means the page was not opened for
   * that kind of item, the same as 0, instead of failing the whole page.
   *
   * @param value the request parameter, or null
   * @return the id, or 0
   */
  static int readRequestId(String value) {
    if (StringUtils.isBlank(value)) {
      return 0;
    }
    try {
      return Integer.parseInt(StringUtils.trim(value));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  @Override
  public String save() {
    String institutionName, institutionAcronym, institutionTypeName, countryName, partnerWebPage;
    String subject;
    StringBuilder message = new StringBuilder();

    // The request needs a partner with a type and a country: without them the form is shown again with an error
    InstitutionType requestedType = (activityPartner != null && activityPartner.getPartner() != null)
      ? activityPartner.getPartner().getInstitutionType() : null;
    LocElement country = locationId > 0 ? locationManager.getLocElementById(locationId) : null;
    if (requestedType == null || requestedType.getId() == null || requestedType.getId() <= 0 || country == null) {
      missingRequiredFields = true;
      return INPUT;
    }

    long partnerTypeId;
    // Take the values to create the message
    institutionName = activityPartner.getPartner().getName();
    institutionAcronym = activityPartner.getPartner().getAcronym();
    partnerTypeId = requestedType.getId();
    partnerWebPage = activityPartner.getPartner().getWebsiteLink();

    // Get the partner type name
    countryName = country.getName();

    institutionTypeName = "";
    if (institutionTypesList != null) {
      for (InstitutionType pt : institutionTypesList) {
        if (pt != null && pt.getId() != null && pt.getId() == partnerTypeId) {
          institutionTypeName = pt.getName();
        }
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

    partnerRequest.setLocElement(country);
    partnerRequestModifications.setLocElement(country);
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

    // A page opened without any id has no request name; switching on null would throw
    switch (pageRequestName != null ? pageRequestName : "") {
      case "projects":
        try {
          projectInfo = projectManager.getProjectById(projectID).getProjecInfoPhase(this.getActualPhase());
        } catch (Exception e) {
          LOG.error("Failed to get projectInfo from projectID ", e);
        }
        this.addProjectMessage(message, partnerRequest, partnerRequestModifications);
        break;

      case "fundingSources":
        this.addFunginMessage(message, partnerRequest, partnerRequestModifications);
        break;

      case "studies":
        try {
          projectInfo = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID).getProject()
            .getProjecInfoPhase(this.getActualPhase());
        } catch (Exception e) {
          LOG.error("Failed to get projectInfo from expectedID ", e);
        }
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

    if (projectInfo == null && projectID != 0) {
      Project requestProject = projectManager.getProjectById(projectID);
      projectInfo = requestProject != null ? requestProject.getProjecInfoPhase(this.getActualPhase()) : null;
    }

    // Get project information
    Project project = projectInfo != null ? projectInfo.getProject() : null;

    boolean postFailed = false;
    if (this.isAiccra()) {

      // setting requestSource CLARISA parameter
      String requestSource;
      if (project != null && project.getAcronym() != null) {
        requestSource = project.getAcronym() + " Cluster";
      } else if (project != null && project.getProjectInfo() != null && project.getProjectInfo().getTitle() != null) {
        requestSource = project.getProjectInfo().getTitle() + " Cluster";
      } else {
        requestSource = "Request made from MARLO";
      }

      postFailed = !this.sendPartnerRequest(partnerRequestModifications, requestSource);

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
       * if (this.validateEmailNotification()) {
       * sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject, message.toString(),
       * null, null, null, true);
       * }
       */
    } catch (Exception e) {
      LOG.error("unable to send mail", e);
      /**
       * Original code swallows the exception and didn't even log it. Now
       * we at least log it, but we need to revisit to see if we should
       * continue processing or re-throw the exception.
       */
    }
    if (postFailed) {
      // The form is shown again, with an error, instead of the "sent" message
      messageSent = false;
      requestFailed = true;
      return INPUT;
    }

    messageSent = true;

    if (projectID != 0) {
      LOG.info("A partner request was sent for the project {}", projectID);
    }
    if (fundingSourceID != 0) {
      LOG.info("A partner request was sent for the funding source {}", fundingSourceID);
    }

    Collection<String> messages = this.getActionMessages();
    this.addActionMessage("message:" + this.getText("saving.saved"));
    messages = this.getActionMessages();
    return SUCCESS;
  }

  /**
   * send request to CLARISA,to create a partner
   *
   * @param PartnerRequest partnerRequest
   * @param String requestSource
   * @return true when CLARISA accepted the request (status 2xx); false when it rejected it or could not be reached
   * @author IBD
   */
  public boolean sendPartnerRequest(PartnerRequest partnerRequest, String requestSource) {

    try {

      ExternalPostUtils epu = new ExternalPostUtils();
      epu.setUsername(config.getClarisaAPIUsername());
      epu.setPassword(config.getClarisaAPIPassword());
      JSONObject jsonObject = new JSONObject();


      jsonObject.put("name", partnerRequest.getPartnerName());
      jsonObject.put("acronym", partnerRequest.getAcronym());
      jsonObject.put("websiteLink", partnerRequest.getWebPage());
      jsonObject.put("institutionTypeCode", partnerRequest.getInstitutionType().getId());
      jsonObject.put("hqCountryIso", partnerRequest.getLocElement().getIsoAlpha2());

      jsonObject.put("requestSource", requestSource);

      jsonObject.put("misAcronym", "aiccra");
      jsonObject.put("userId", this.getCurrentUser().getId());
      jsonObject.put("externalUserMail", this.getCurrentUser().getEmail());
      jsonObject.put("externalUserName", this.getCurrentUser().getUsername());
      jsonObject.put("externalUserComments", "Request made from MARLO");

      HttpPost httpPost = new HttpPost(config.getClarisaAPIHost() + "/api/partner-requests/create");

      // 2024/08/01 cgamboa Format is added to the request made
      httpPost.setEntity(new StringEntity(jsonObject.toString(), "UTF-8"));

      httpPost.setHeader("Accept", "application/json");
      httpPost.setHeader("Content-Type", "application/json");
      httpPost.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(epu.getUsername(), epu.getPassword()),
        "UTF-8", false));

      // Closing the response and the client releases the connection; only the status is logged, never the headers
      try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpPost)) {
        int statusCode = response.getStatusLine() != null ? response.getStatusLine().getStatusCode() : -1;
        if (statusCode >= 200 && statusCode < 300) {
          LOG.info("CLARISA accepted the partner request with status {}", statusCode);
          return true;
        }
        LOG.warn("CLARISA did not accept the partner request, status {}", statusCode);
        return false;
      }
    } catch (Exception e) {
      LOG.error("Could not send the partner request to CLARISA", e);
      return false;
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


}
