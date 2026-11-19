package org.cgiar.ccafs.marlo.action.json.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome.Status;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditPartnerRequestAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 821788435993637711L;

  private static final Logger LOG = LoggerFactory.getLogger(EditPartnerRequestAction.class);

  /** Status string of an edit that was refused because the request was not an HTTP POST. */
  static final String OUTCOME_NOT_ALLOWED = "notAllowed";
  /** Status string of an edit that ended in an exception. */
  static final String OUTCOME_FAILED = "failed";

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
  // Result of the edit as one status string (design section 4, JSON contract)
  private String outcome;


  @Inject
  public EditPartnerRequestAction(APConfig config, PartnerRequestManager partnerRequestManager,
    InstitutionTypeManager institutionTypeManager, LocElementManager locElementManager) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.institutionTypeManager = institutionTypeManager;
    this.locElementManager = locElementManager;
  }


  /**
   * Edits the editable copy of a pending request. Only a POST is accepted; the manager changes the copy and reports
   * whether the request was still pending, in one transaction.
   */
  @Override
  public String execute() throws Exception {
    if (!this.isHttpPost()) {
      LOG.warn("editPartnerRequest refused for request {}: not an HTTP POST", this.loggableRequestId());
      this.success = false;
      this.outcome = OUTCOME_NOT_ALLOWED;
      return SUCCESS;
    }
    if (OUTCOME_FAILED.equals(this.outcome)) {
      // prepare() could not read the posted fields and already logged it: nothing is written.
      this.success = false;
      return SUCCESS;
    }
    try {
      Long typeID = StringUtils.isBlank(this.type) ? null : Long.valueOf(this.type.trim());
      PartnerRequestOutcome result = this.partnerRequestManager.editPartnerRequest(Long.parseLong(this.requestID),
        this.name, this.acronym, this.webPage, typeID, this.country, this.modificationJustification);
      if (result == null || result.getStatus() == null) {
        throw new IllegalStateException("The manager returned no outcome");
      }
      this.outcome = statusName(result.getStatus());
      this.success = result.getStatus() == Status.DONE;
      if (!this.success) {
        LOG.warn("editPartnerRequest: request {} was not edited: {}", this.loggableRequestId(), result.getStatus());
      }
    } catch (Exception e) {
      LOG.error("editPartnerRequest: could not edit partner request {}", this.loggableRequestId(), e);
      this.success = false;
      this.outcome = OUTCOME_FAILED;
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


  public String getOutcome() {
    return this.outcome;
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
    outcome = null;
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
      LOG.error("editPartnerRequest: could not read the posted fields of request {}", this.loggableRequestId(), e);
      success = false;
      outcome = OUTCOME_FAILED;
    }
  }

  /**
   * The posted request id for a log line: line breaks removed because the value is request input.
   */
  private String loggableRequestId() {
    return this.requestID == null ? null : this.requestID.replaceAll("[\\r\\n]", " ");
  }

  /**
   * The status as the one string the JSON contract uses.
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
      default:
        return OUTCOME_FAILED;
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
