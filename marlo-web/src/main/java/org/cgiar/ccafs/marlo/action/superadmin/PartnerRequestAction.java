/*****************************************************************
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
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.CountryOfficePOJO;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome.Status;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PartnerRequestEmails;
import org.cgiar.ccafs.marlo.utils.PartnerRequestEmails.Email;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PartnerRequestAction:
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author avalencia - CCAFS
 * @date Oct 18, 2017
 * @time 3:45:12 PM
 * @date Nov 10, 2017
 * @time 10:09:18 AM:Inactive parent partner request
 */
public class PartnerRequestAction extends BaseAction {

  private static final long serialVersionUID = -4592281983603538935L;

  private static Logger LOG = LoggerFactory.getLogger(PartnerRequestAction.class);

  /** Status string of a decision that was refused because the request was not an HTTP POST. */
  static final String OUTCOME_NOT_ALLOWED = "notAllowed";
  /** Status string of a decision that ended in an exception. */
  static final String OUTCOME_FAILED = "failed";
  private static final String OUTCOME_TEXT_PREFIX = "marloRequestInstitution.ui.outcome.";

  private final PartnerRequestManager partnerRequestManager;
  private final InstitutionManager institutionManager;
  private final InstitutionTypeManager institutionTypeManager;
  private final InstitutionLocationManager institutionLocationManager;
  private final LocElementManager locElementManager;
  private final GlobalUnitManager globalUnitManager;

  // Variables
  private List<LocElement> countriesList = new ArrayList<>();
  private List<InstitutionType> institutionTypesList = new ArrayList<>();
  private List<CountryOfficePOJO> countryOfficesList = new ArrayList<>();
  private List<PartnerRequest> partners;
  private long requestID;
  private final SendMailS sendMail;
  private boolean success;
  private boolean sendNotification;

  // Overall result of the last review decision, as one status string (design section 4, JSON contract)
  private String outcome;
  // Result of each request of an office decision: request id -> status string (strings only, never entities)
  private Map<String, String> outcomes = new LinkedHashMap<>();

  // Justification for reject office(s)
  private String justification;

  /**
   * CountryOffices selected
   * 
   * @author avalencia - CCAFS
   * @date Oct 19, 2017
   * @time 8:22:32 AM
   */
  private CountryOfficePOJO countryOfficePOJO;

  @Inject
  public PartnerRequestAction(APConfig config, PartnerRequestManager partnerRequestManager,
    InstitutionManager institutionManager, InstitutionTypeManager institutionTypeManager,
    LocElementManager locElementManager, InstitutionLocationManager institutionLocationManager,
    GlobalUnitManager globalUnitManager, SendMailS sendMail) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.institutionManager = institutionManager;
    this.institutionTypeManager = institutionTypeManager;
    this.locElementManager = locElementManager;
    this.institutionLocationManager = institutionLocationManager;
    this.globalUnitManager = globalUnitManager;
    this.sendMail = sendMail;
  }

  /**
   * Add a list of office request and send an email to the users who requested
   * the Countries (The full list of locations is send to each user). The decision is taken by the manager in one
   * transaction; the emails are sent afterwards, only for the requests it applied.
   * 
   * @author avalencia - CCAFS
   * @date Oct 18, 2017
   * @time 3:55:44 PM
   * @return
   */
  public String addCountryOffices() {
    if (!this.isHttpPost()) {
      return this.refuseNonPost("addCountryOffices");
    }
    try {
      if (this.countryOfficePOJO == null || this.countryOfficePOJO.getInstitution() == null
        || this.countryOfficePOJO.getInstitution().getId() == null) {
        LOG.warn("addCountryOffices: nothing decided, the posted selection has no institution (request ids {})",
          this.postedRequestIds());
        this.recordFailure();
        return SUCCESS;
      }
      long institutionId = this.countryOfficePOJO.getInstitution().getId();
      List<PartnerRequestOutcome> results =
        this.partnerRequestManager.acceptOfficeRequests(this.parseRequestIds(), institutionId);
      this.recordOutcomes("addCountryOffices", institutionId, results);
      this.sendOfficeEmails("addCountryOffices", results, true);
    } catch (Exception e) {
      LOG.error("addCountryOffices: could not accept the office requests {} of institution {}",
        this.postedRequestIds(), this.requestedInstitutionId(), e);
      this.recordFailure();
    }
    return SUCCESS;
  }

  /**
   * Accepts a pending partner request. Only a POST is accepted; the answer is an action message or error on the
   * redirect to the review page, never a rendered page, because after a rolled-back write the entities loaded in
   * prepare() are detached.
   * 
   * @return always SUCCESS (the redirect).
   */
  public String addPartner() {
    if (!this.isHttpPost()) {
      LOG.warn("addPartner refused for request {}: not an HTTP POST", this.requestID);
      this.outcome = OUTCOME_NOT_ALLOWED;
      this.addActionError(this.getText(OUTCOME_TEXT_PREFIX + OUTCOME_NOT_ALLOWED));
      return SUCCESS;
    }
    PartnerRequestOutcome result;
    try {
      result = this.partnerRequestManager.acceptPartnerRequest(this.requestID, this.getCurrentUser());
      if (result == null || result.getStatus() == null) {
        throw new IllegalStateException("The manager returned no outcome");
      }
    } catch (Exception e) {
      LOG.error("Could not accept partner request {}", this.requestID, e);
      this.outcome = OUTCOME_FAILED;
      this.addActionError(this.getText(OUTCOME_TEXT_PREFIX + OUTCOME_FAILED));
      return SUCCESS;
    }
    this.outcome = statusName(result.getStatus());
    if (result.getStatus() == Status.DONE) {
      this.addActionMessage(this.getText(OUTCOME_TEXT_PREFIX + this.outcome));
      this.sendPartnerAcceptedEmail(result);
    } else {
      LOG.warn("Partner request {} was not accepted: {}", this.requestID, result.getStatus());
      this.addActionError(this.getText(OUTCOME_TEXT_PREFIX + this.outcome));
    }
    return SUCCESS;
  }

  public List<LocElement> getCountriesList() {
    return this.countriesList;
  }

  public CountryOfficePOJO getCountryOfficePOJO() {
    return this.countryOfficePOJO;
  }

  public List<CountryOfficePOJO> getCountryOfficesList() {
    return this.countryOfficesList;
  }

  public List<InstitutionType> getInstitutionTypesList() {
    return this.institutionTypesList;
  }

  @Override
  public String getJustification() {
    return this.justification;
  }

  public String getOutcome() {
    return this.outcome;
  }

  public Map<String, String> getOutcomes() {
    return this.outcomes;
  }

  public List<PartnerRequest> getPartners() {
    return this.partners;
  }

  public long getRequestID() {
    return this.requestID;
  }

  public boolean isSuccess() {
    return this.success;
  }

  /**
   * @author avalencia - CCAFS
   * @date Oct 18, 2017
   * @time 3:46:44 PM Added countryOffices to a POJO instead of a HashMap
   * @throws Exception
   */
  @Override
  public void prepare() throws Exception {
    this.success = true;
    HashMap<Institution, List<PartnerRequest>> countryOfficesHashMap = new HashMap<Institution, List<PartnerRequest>>();
    // Verify if exists active partnerRequest
    if (this.partnerRequestManager.findAll() != null) {
      this.partners = new ArrayList<>(this.partnerRequestManager.findAll().stream()
        .filter(pr -> pr.isActive() && !pr.isOffice() && pr.getPartnerRequest() != null).collect(Collectors.toList()));
      for (PartnerRequest officeRequest : this.partnerRequestManager.findAll().stream()
        .filter(pr -> pr.isActive() && pr.isOffice() && pr.getInstitution() != null).collect(Collectors.toList())) {
        if (countryOfficesHashMap.containsKey(officeRequest.getInstitution())) {
          countryOfficesHashMap.get(officeRequest.getInstitution()).add(officeRequest);
        } else {
          List<PartnerRequest> requestList = new ArrayList<>();
          requestList.add(officeRequest);
          countryOfficesHashMap.put(officeRequest.getInstitution(), requestList);
        }
      }
    } else {
      this.partners = new ArrayList<>();
      countryOfficesHashMap = new HashMap<Institution, List<PartnerRequest>>();
    }
    // Fill countryOfficeList with the HashMap
    if (countryOfficesHashMap.size() > 0) {
      for (Institution institution : countryOfficesHashMap.keySet()) {
        CountryOfficePOJO countryOfficePojo =
          new CountryOfficePOJO(institution, countryOfficesHashMap.get(institution));
        this.countryOfficesList.add(countryOfficePojo);
      }
    } else {
      this.countryOfficesList = new ArrayList<>();
    }
    this.countriesList = this.locElementManager.findAll().stream()
      .filter(c -> c.isActive() && c.getLocElementType().getId().longValue() == 2).collect(Collectors.toList());
    this.institutionTypesList = this.institutionTypeManager.findAll().stream()
      .filter(it -> it.isActive() && !it.getOld()).collect(Collectors.toList());
    this.countriesList.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
  }

  /**
   * Reject a list of office requests and send an email to the users who
   * requested the Countries (The full list of locations is send to each user)
   * when the superadmin asked for it. The decision is taken by the manager in one transaction; the emails are sent
   * afterwards.
   * 
   * @author avalencia - CCAFS
   * @date Oct 19, 2017
   * @time 11:11:19 AM
   * @return
   */
  public String rejectCountryOffices() {
    if (!this.isHttpPost()) {
      return this.refuseNonPost("rejectCountryOffices");
    }
    try {
      Map<String, Parameter> parameters = this.getParameters();
      String justificationValue = parameterValue(parameters, APConstants.JUSTIFICATION_REQUEST);
      this.justification = justificationValue == null ? "" : justificationValue;
      this.sendNotification =
        Boolean.valueOf(parameterValue(parameters, APConstants.PARTNER_REQUEST_SEND_NOTIFICATION));

      if (this.countryOfficePOJO == null || this.countryOfficePOJO.getInstitution() == null
        || this.countryOfficePOJO.getInstitution().getId() == null) {
        LOG.warn("rejectCountryOffices: nothing decided, the posted selection has no institution (request ids {})",
          this.postedRequestIds());
        this.recordFailure();
        return SUCCESS;
      }
      long institutionId = this.countryOfficePOJO.getInstitution().getId();
      List<PartnerRequestOutcome> results = this.partnerRequestManager.rejectOfficeRequests(this.parseRequestIds(),
        institutionId, this.justification, this.getCurrentUser());
      this.recordOutcomes("rejectCountryOffices", institutionId, results);
      if (this.sendNotification) {
        this.sendOfficeEmails("rejectCountryOffices", results, false);
      }
    } catch (Exception e) {
      LOG.error("rejectCountryOffices: could not reject the office requests {} of institution {}",
        this.postedRequestIds(), this.requestedInstitutionId(), e);
      this.recordFailure();
    }
    return SUCCESS;
  }

  /**
   * The ids of the office requests the page sent, separated by commas.
   */
  private List<Long> parseRequestIds() {
    List<Long> ids = new ArrayList<>();
    String raw = this.countryOfficePOJO.getIds();
    if (raw == null) {
      return ids;
    }
    for (String part : raw.split(",")) {
      String trimmed = part.trim();
      if (!trimmed.isEmpty()) {
        ids.add(Long.valueOf(trimmed));
      }
    }
    return ids;
  }

  /**
   * The raw ids of the posted office requests, for a log line: null when nothing was posted, line breaks removed
   * because the value is request input.
   */
  private String postedRequestIds() {
    String raw = this.countryOfficePOJO == null ? null : this.countryOfficePOJO.getIds();
    return raw == null ? null : raw.replaceAll("[\\r\\n]", " ");
  }

  /**
   * The institution id of the posted office request, for a log line; 0 when the page sent none.
   */
  private long requestedInstitutionId() {
    if (this.countryOfficePOJO == null || this.countryOfficePOJO.getInstitution() == null
      || this.countryOfficePOJO.getInstitution().getId() == null) {
      return 0;
    }
    return this.countryOfficePOJO.getInstitution().getId();
  }

  /**
   * Answers a decision that did not come as an HTTP POST: nothing is written, nothing is sent.
   */
  private String refuseNonPost(String action) {
    LOG.warn("{} refused: not an HTTP POST", action);
    this.success = false;
    this.outcome = OUTCOME_NOT_ALLOWED;
    this.outcomes = new LinkedHashMap<>();
    return SUCCESS;
  }

  private void recordFailure() {
    this.success = false;
    this.outcome = OUTCOME_FAILED;
    this.outcomes = new LinkedHashMap<>();
  }

  /**
   * Publishes the results of an office decision as strings only and logs every request the manager refused.
   */
  private void recordOutcomes(String action, long institutionId, List<PartnerRequestOutcome> results) {
    Map<String, String> byRequest = new LinkedHashMap<>();
    boolean anyDone = false;
    boolean anySkipped = false;
    String first = null;
    for (PartnerRequestOutcome result : results) {
      String status = statusName(result.getStatus());
      byRequest.put(String.valueOf(result.getRequestId()), status);
      if (first == null) {
        first = status;
      }
      if (result.getStatus() == Status.DONE) {
        anyDone = true;
      } else {
        if (result.getStatus() == Status.SKIPPED_EXISTING) {
          anySkipped = true;
        }
        LOG.warn("{}: request {} of institution {} was {}", action, result.getRequestId(), institutionId,
          result.getStatus());
      }
    }
    this.outcomes = byRequest;
    if (anyDone) {
      this.outcome = statusName(Status.DONE);
    } else if (anySkipped) {
      this.outcome = statusName(Status.SKIPPED_EXISTING);
    } else {
      this.outcome = first == null ? statusName(Status.NOT_FOUND) : first;
    }
    this.success = anyDone || anySkipped;
  }

  /**
   * The status as the one string the JSON actions and the page messages use.
   */
  static String statusName(Status status) {
    switch (status) {
      case DONE:
        return "done";
      case ALREADY_HANDLED:
        return "alreadyHandled";
      case NOT_FOUND:
        return "notFound";
      case WRONG_KIND:
        return "wrongKind";
      case SKIPPED_EXISTING:
        return "skippedExisting";
      default:
        return OUTCOME_FAILED;
    }
  }

  /**
   * The first value of a request parameter, trimmed; null when the parameter was not sent.
   */
  private static String parameterValue(Map<String, Parameter> parameters, String name) {
    Parameter parameter = parameters == null ? null : parameters.get(name);
    if (parameter == null || parameter.getMultipleValues() == null || parameter.getMultipleValues().length == 0) {
      return null;
    }
    return StringUtils.trim(parameter.getMultipleValues()[0]);
  }

  /**
   * The unit of the request as the database holds it: the builder reads its lazy parameters, which a detached
   * instance cannot.
   */
  private GlobalUnit managedUnit(PartnerRequest request) {
    if (request == null || request.getCrp() == null) {
      return null;
    }
    return this.globalUnitManager.getGlobalUnitById(request.getCrp().getId());
  }

  private String approverEmail() {
    User approver = this.getCurrentUser();
    return approver == null ? null : approver.getEmail();
  }

  /**
   * Tells the requester that the partner was added. Recipients as they have always been: the requester (or the mail
   * received from CLARISA) with the approver in copy, plus the creator when the request is external, and our
   * notification mailbox in blind copy. A failure is logged and never reaches the superadmin: the decision is
   * already committed.
   */
  private void sendPartnerAcceptedEmail(PartnerRequestOutcome result) {
    try {
      PartnerRequest request = result.getRequest();
      GlobalUnit unit = this.managedUnit(request);
      if (!PartnerRequestEmails.isEnabled(unit)) {
        return;
      }
      User creator = request.getCreatedBy();
      String creatorEmail = creator == null ? null : creator.getEmail();
      String toEmail;
      String ccEmail = this.approverEmail();
      String dearName;
      if (StringUtils.isNotBlank(request.getExternalUserMail())) {
        toEmail = request.getExternalUserMail();
        if (StringUtils.isNotBlank(creatorEmail)) {
          ccEmail = StringUtils.isBlank(ccEmail) ? creatorEmail : ccEmail + ", " + creatorEmail;
        }
        dearName = StringUtils.isNotBlank(request.getExternalUserName()) ? request.getExternalUserName()
          : (creator == null ? null : creator.getFirstName());
      } else {
        toEmail = creatorEmail;
        dearName = creator == null ? null : creator.getFirstName();
      }
      if (StringUtils.isBlank(toEmail)) {
        LOG.warn("Partner request {} was accepted but has no requester to notify", result.getRequestId());
        return;
      }
      Institution institution = result.getInstitution();
      String institutionName = institution == null ? request.getPartnerInfo() : institution.getComposedName();
      Email email = PartnerRequestEmails.partnerAccepted(unit, this.getBaseUrlCdn(), dearName, institutionName,
        request.getPartnerName());
      this.sendMail.send(toEmail, ccEmail, this.config.getEmailNotification(), email.getSubject(), email.getBody(),
        null, null, null, true);
    } catch (Exception e) {
      LOG.error("Could not send the accepted email of partner request {}", result.getRequestId(), e);
    }
  }

  /**
   * Tells each requester of an office decision, once per Global Unit of the requests, with the full list of the
   * countries of that unit's requests. Only the requests the manager applied (or found already in the institution)
   * are notified. Failures are logged per recipient and never reach the superadmin.
   */
  private void sendOfficeEmails(String action, List<PartnerRequestOutcome> results, boolean accepted) {
    Map<Long, List<PartnerRequestOutcome>> byUnit = new LinkedHashMap<>();
    for (PartnerRequestOutcome result : results) {
      if (!result.isNotifiable() || result.getRequest() == null) {
        continue;
      }
      GlobalUnit crp = result.getRequest().getCrp();
      if (crp == null) {
        LOG.warn("{}: request {} has no Global Unit, so its requester is not notified", action,
          result.getRequestId());
        continue;
      }
      byUnit.computeIfAbsent(crp.getId(), id -> new ArrayList<>()).add(result);
    }
    for (List<PartnerRequestOutcome> group : byUnit.values()) {
      this.sendOfficeEmailsOfUnit(action, group, accepted);
    }
  }

  private void sendOfficeEmailsOfUnit(String action, List<PartnerRequestOutcome> group, boolean accepted) {
    String requestIds = group.stream().map(r -> String.valueOf(r.getRequestId())).collect(Collectors.joining(","));
    try {
      GlobalUnit unit = this.managedUnit(group.get(0).getRequest());
      if (!PartnerRequestEmails.isEnabled(unit)) {
        return;
      }
      // One recipient per mailbox, with the name to greet; the full list of countries goes to each of them.
      Map<String, String> recipients = new LinkedHashMap<>();
      // An accept that found the country already in the institution closes the request without adding it, so the
      // accepted email names those countries apart from the ones this decision added.
      Set<String> countries = new LinkedHashSet<>();
      Set<String> existingCountries = new LinkedHashSet<>();
      Institution institution = null;
      for (PartnerRequestOutcome result : group) {
        PartnerRequest request = result.getRequest();
        if (request.getCreatedBy() != null) {
          addRecipient(recipients, request.getCreatedBy().getEmail(), request.getCreatedBy().getFirstName());
        }
        // If the request comes from CLARISA, include external mail
        addRecipient(recipients, request.getExternalUserMail(), request.getExternalUserName());
        if (request.getLocElement() != null && request.getLocElement().getName() != null) {
          if (accepted && result.getStatus() == Status.SKIPPED_EXISTING) {
            existingCountries.add(request.getLocElement().getName());
          } else {
            countries.add(request.getLocElement().getName());
          }
        }
        if (institution == null) {
          institution = result.getInstitution() != null ? result.getInstitution() : request.getInstitution();
        }
      }
      String institutionName = institution == null ? "" : institution.getComposedName();
      String countryList = String.join(", ", countries);
      String existingList = String.join(", ", existingCountries);
      for (Map.Entry<String, String> recipient : recipients.entrySet()) {
        Email email;
        if (accepted) {
          email = PartnerRequestEmails.officeAccepted(unit, this.getBaseUrlCdn(), recipient.getValue(),
            institutionName, countryList, existingList);
        } else {
          email = PartnerRequestEmails.officeRejected(unit, this.getBaseUrlCdn(), recipient.getValue(),
            institutionName, countryList, this.justification);
        }
        this.sendMail.send(recipient.getKey(), this.approverEmail(), this.config.getEmailNotification(),
          email.getSubject(), email.getBody(), null, null, null, true);
      }
    } catch (Exception e) {
      LOG.error("{}: could not send the office emails of requests {}", action, requestIds, e);
    }
  }

  private static void addRecipient(Map<String, String> recipients, String email, String firstName) {
    if (StringUtils.isNotBlank(email)) {
      recipients.putIfAbsent(email.trim(), firstName);
    }
  }

  public void setCountryOfficePOJO(CountryOfficePOJO countryOfficePOJO) {
    this.countryOfficePOJO = countryOfficePOJO;
  }

  public void setCountryOfficesList(List<CountryOfficePOJO> countryOfficesList) {
    this.countryOfficesList = countryOfficesList;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setPartners(List<PartnerRequest> partners) {
    this.partners = partners;
  }

  public void setRequestID(long requestID) {
    this.requestID = requestID;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

}
