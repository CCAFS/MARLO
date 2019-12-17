package org.cgiar.ccafs.marlo.validation;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.ICenterSectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterSectionStatus;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.InternetAddress;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class BaseValidator {

  private static final Logger LOG = LoggerFactory.getLogger(BaseValidator.class);

  /**
   * Setter injection is ok here as this class is extended. TODO look into making this class abstract.
   */

  @Inject
  protected APConfig config;
  @Inject
  private SectionStatusManager sectionStatusManager;
  @Inject
  private ICenterSectionStatusManager centerSectionStatusManager;

  // now the missingFileds is a variable because the validator will be called by Clarisa or other services
  private StringBuilder missingFields = new StringBuilder();


  public BaseValidator() {
  }


  /**
   * This method add a missing field separated by a semicolon (;).
   * 
   * @param field is the name of the field.
   */
  public void addMissingField(String field) {
    if (this.missingFields.length() != 0) {
      this.missingFields.append(";");
    }
    this.missingFields.append(field);
  }

  public StringBuilder getMissingFields() {
    return missingFields;
  }


  protected boolean isValidEmail(String email) {
    if (email != null) {
      try {
        InternetAddress internetAddress = new InternetAddress(email);
        internetAddress.validate();
        return true;
      } catch (javax.mail.internet.AddressException e) {
        email = (email == null) ? "" : email;
        LOG.debug("Email address was invalid: " + email);
      }
    }
    return false;
  }


  protected boolean isValidNumber(String number) {
    if (this.isValidString(number)) {
      try {
        Double.parseDouble(number);
        // If is a number the code comes to here.
        return true;
      } catch (NumberFormatException e) {
        // if not a number.
        return false;
      }
    }
    return false;
  }

  /**
   * This method validates that the string received is not null and is not empty.
   * 
   * @param string
   * @return true if the string is valid. False otherwise.
   */
  protected boolean isValidString(String string) {
    if (string != null) {
      return !string.trim().isEmpty();
    }
    return false;
  }

  /**
   * Validate a Basic Url Structure (http://, https:// or ftp://)
   * 
   * @param url - The Url to Validate
   * @return true if is a valid Url
   */
  protected boolean isValidUrl(String url) {
    UrlValidator urlValidator = new UrlValidator();
    boolean bReturn = urlValidator.isValid(url);
    return bReturn;
  }

  /**
   * Remove HTML tags
   * 
   * @param html
   * @return
   */
  public String removeHtmlTags(String html) {
    if (html != null) {
      String noTags = Jsoup.parse(html).text();
      return noTags;
    } else {
      return "";
    }

  }

  /**
   * ******************************************************************************************
   * ************************* CENTER METHOD **************************************************
   * *******************************************************************************************
   * This method saves the missing fields into the database for a section at CapDev.
   * 
   * @param program is a CrpProgram.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CapacityDevelopment capacityDevelopment, String sectionName, BaseAction action) {

    int year = action.getActualPhase().getYear();

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByCapdev(capacityDevelopment.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setCapacityDevelopment(capacityDevelopment);
      status.setYear(year);
    }
    if (action.getMissingFields().length() > 0) {
      status.setMissingFields(action.getMissingFields().toString());
    } else {
      status.setMissingFields("");
    }

    centerSectionStatusManager.saveSectionStatus(status);
  }

  /**
   * * ******************************************************************************************
   * ************************* CENTER METHOD **************************************************
   * *******************************************************************************************
   * This method saves the missing fields into the database for a section at CapDev - Supporting Documents.
   * 
   * @param deliverable is a CenterDeliverable.
   * @param capacityDevelopment is a CapacityDevelopment.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CenterDeliverable deliverable, CapacityDevelopment capacityDevelopment,
    String sectionName, BaseAction action) {
    int year = action.getActualPhase().getYear();

    CenterSectionStatus status = centerSectionStatusManager.getSectionStatusBySupDocs(deliverable.getId(),
      capacityDevelopment.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setDeliverable(deliverable);
      status.setCapacityDevelopment(capacityDevelopment);
      status.setYear(year);
    }
    if (action.getMissingFields().length() > 0) {
      status.setMissingFields(action.getMissingFields().toString());
    } else {
      status.setMissingFields("");
    }


    centerSectionStatusManager.saveSectionStatus(status);
  }

  /**
   * * ******************************************************************************************
   * ************************* CENTER METHOD **************************************************
   * *******************************************************************************************
   * This method saves the missing fields into the database for a section at Project - Deliverable.
   * 
   * @param program is a CrpProgram.
   * @param project is a CenterProject.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CenterDeliverable deliverable, CenterProject project, String sectionName,
    BaseAction action) {
    int year = action.getActualPhase().getYear();

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(), project.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setDeliverable(deliverable);
      status.setProject(project);
      status.setYear(year);
    }
    if (action.getMissingFields().length() > 0) {
      status.setMissingFields(action.getMissingFields().toString());
    } else {
      status.setMissingFields("");
    }

    centerSectionStatusManager.saveSectionStatus(status);
  }

  /**
   * * ******************************************************************************************
   * ************************* CENTER METHOD **************************************************
   * *******************************************************************************************
   * This method saves the missing fields into the database for a section at ImpactPathway - Outcome.
   * 
   * @param program is a CrpProgram.
   * @param outcome is a CenterOutcome.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CrpProgram program, CenterOutcome outcome, String sectionName, BaseAction action) {
    int year = action.getActualPhase().getYear();

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByOutcome(program.getId(), outcome.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setResearchProgram(program);
      status.setResearchOutcome(outcome);
      status.setYear(year);
    }
    if (action.getMissingFields().length() > 0) {
      status.setMissingFields(action.getMissingFields().toString());
    } else {
      status.setMissingFields("");
    }

    centerSectionStatusManager.saveSectionStatus(status);
  }

  /**
   * * ******************************************************************************************
   * ************************* CENTER METHOD **************************************************
   * *******************************************************************************************
   * This method saves the missing fields into the database for a section at ImpactPathway - Output.
   * 
   * @param program is a CrpProgram.
   * @param output is a CenterOutput.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CrpProgram program, CenterOutput output, String sectionName, BaseAction action) {
    int year = action.getActualPhase().getYear();

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByOutput(program.getId(), output.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setResearchProgram(program);
      status.setResearchOutput(output);
      status.setYear(year);
    }
    if (action.getMissingFields().length() > 0) {
      status.setMissingFields(action.getMissingFields().toString());
    } else {
      status.setMissingFields("");
    }

    centerSectionStatusManager.saveSectionStatus(status);
  }

  /**
   * * ******************************************************************************************
   * ************************* CENTER METHOD **************************************************
   * *******************************************************************************************
   * This method saves the missing fields into the database for a section at ImpactPathway - project.
   * 
   * @param program is a CrpProgram.
   * @param project is a CenterProject.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CrpProgram program, CenterProject project, String sectionName, BaseAction action) {
    int year = action.getActualPhase().getYear();

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByProject(program.getId(), project.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setProject(project);
      status.setYear(year);
    }
    if (action.getMissingFields().length() > 0) {
      status.setMissingFields(action.getMissingFields().toString());
    } else {
      status.setMissingFields("");
    }

    centerSectionStatusManager.saveSectionStatus(status);
  }

  /**
   * ******************************************************************************************
   * ************************* CENTER METHOD **************************************************
   * *******************************************************************************************
   * This method saves the missing fields into the database for a section at ImpactPathway.
   * 
   * @param program is a CrpProgram.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CrpProgram program, String sectionName, BaseAction action) {

    int year = action.getActualPhase().getYear();

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByProgram(program.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setResearchProgram(program);
      status.setYear(year);
    }
    if (action.getMissingFields().length() > 0) {
      status.setMissingFields(action.getMissingFields().toString());
    } else {
      status.setMissingFields("");
    }

    centerSectionStatusManager.saveSectionStatus(status);
  }


  /**
   * This method saves the missing fields into the database for a section at deliverable level.
   * 
   * @param deliverable is a deliverable.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(Deliverable deliverable, String cycle, int year, Boolean upkeep, String sectionName,
    BaseAction action) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setDeliverable(deliverable);
      status.setSectionName(sectionName);
      status.setProject(deliverable.getProject());

    }
    status.setMissingFields(action.getMissingFields().toString());
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);


  }

  /**
   * This method saves the missing fields into the database for a section at project Outcome level.
   * 
   * @param fundingSource is a funding Source
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(FundingSource fundingSource, String cycle, Integer year, Boolean upkeep,
    String sectionName, BaseAction action) {
    // Reporting missing fields into the database.

    int a = 0;
    LOG.debug("save MissingField :" + a);
    SectionStatus status =
      sectionStatusManager.getSectionStatusByFundingSource(fundingSource.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setFundingSource(fundingSource);
      status.setSectionName(sectionName);
      fundingSource.getSectionStatuses().add(status);
    }
    status.setMissingFields(action.getMissingFields().toString());
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);
  }


  /**
   * This method saves the missing fields into the database for a section at deliverable level.
   * 
   * @param ipLiaisonInstitution is a ipLiaisonInstitution.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(IpLiaisonInstitution ipLiaisonInstitution, String cycle, int year, Boolean upkeep,
    String sectionName, BaseAction action) {
    // Reporting missing fields into the database.

    SectionStatus status = sectionStatusManager.getSectionStatusByCrpIndicators(ipLiaisonInstitution.getId(), cycle,
      year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setIpLiaisonInstitution(ipLiaisonInstitution);;
      status.setSectionName(sectionName);

    }
    status.setMissingFields(action.getMissingFields().toString());
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);


  }


  /**
   * This method saves the missing fields into the database for a section at deliverable level.
   * 
   * @param deliverable is a deliverable.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(IpProgram program, String cycle, int year, Boolean upkeep, String sectionName,
    BaseAction action) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByIpProgram(program.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setIpProgram(program);
      status.setSectionName(sectionName);

    }
    status.setMissingFields(action.getMissingFields().toString());
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);


  }

  /**
   * This method saves the missing fields into the database for a section at powb synthesis level.
   * 
   * @param powbSynthesis is a powbSynthesis.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(PowbSynthesis powbSynthesis, String cycle, int year, Boolean upkeep,
    String sectionName, BaseAction action) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByPowbSynthesis(powbSynthesis.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setPowbSynthesis(powbSynthesis);
      status.setSectionName(sectionName);


    }

    // Validate if the form have missing fileds in project sections issue #1209
    String sMissingField = action.getMissingFields().toString();
    if (sMissingField.length() > 0) {
      status.setMissingFields(sMissingField);
    } else {
      status.setMissingFields("");
    }

    sectionStatusManager.saveSectionStatus(status);

  }

  /**
   * This method saves the missing fields into the database for a section at project Case Study level.
   * 
   * @param caseStudy is a Case Study.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(Project project, CaseStudy caseStudy, String cycle, int year, Boolean upkeep,
    String sectionName, BaseAction action) {
    // Reporting missing fields into the database.
    SectionStatus status =
      sectionStatusManager.getSectionStatusByCaseStudy(caseStudy.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setCaseStudy(caseStudy);
      status.setSectionName(sectionName);
      status.setProject(project);

    }
    status.setMissingFields(action.getMissingFields().toString());
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);
  }


  /**
   * This method saves the missing fields into the database for a section at project expected study.
   * 
   * @param expectedStudy is a Project Expected Study.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section.
   */
  protected void saveMissingFields(Project project, ProjectExpectedStudy expectedStudy, String cycle, int year,
    Boolean upkeep, String sectionName, BaseAction action) {
    // Reporting missing fields into the database.
    SectionStatus status = sectionStatusManager.getSectionStatusByProjectExpectedStudy(expectedStudy.getId(), cycle,
      year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setProjectExpectedStudy(expectedStudy);
      status.setSectionName(sectionName);
      status.setProject(project);

    }
    status.setMissingFields(action.getMissingFields().toString());
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);
  }

  /**
   * This method saves the missing fields into the database for a section at project Case Study level.
   * 
   * @param highlight is a Project Highlight.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(Project project, ProjectHighlight highlight, String cycle, int year, Boolean upkeep,
    String sectionName, BaseAction action) {
    // Reporting missing fields into the database.
    SectionStatus status =
      sectionStatusManager.getSectionStatusByProjectHighlight(highlight.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setProjectHighlight(highlight);
      status.setSectionName(sectionName);
      status.setProject(project);

    }
    status.setMissingFields(action.getMissingFields().toString());
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);
  }


  /**
   * This method saves the missing fields into the database for a section at project Innovation.
   * 
   * @param innovation is a Project Innovation.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section.
   */
  protected void saveMissingFields(Project project, ProjectInnovation innovation, String cycle, int year,
    Boolean upkeep, String sectionName, String missiginFields) {
    // Reporting missing fields into the database.
    SectionStatus status =
      sectionStatusManager.getSectionStatusByProjectInnovation(innovation.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setProjectInnovation(innovation);
      status.setSectionName(sectionName);
      status.setProject(project);

    }
    status.setMissingFields(missiginFields);
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    // action.getMissingFields().setLength(0);
  }


  /**
   * This method saves the missing fields into the database for a section at project Contribution to LP6 level.
   * 
   * @param projectLp6Contribution is a Project Lp6 Contribution
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section.
   */
  protected void saveMissingFields(Project project, ProjectLp6Contribution projectLp6Contribution, String cycle,
    int year, Boolean upkeep, String sectionName, BaseAction action) {
    // Reporting missing fields into the database.
    SectionStatus status = sectionStatusManager
      .getSectionStatusByProjectContributionToLP6(projectLp6Contribution.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setProjectLp6Contribution(projectLp6Contribution);
      status.setSectionName(sectionName);
      status.setProject(project);

    }
    status.setMissingFields(action.getMissingFields().toString());
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);
  }


  /**
   * This method saves the missing fields into the database for a section at project Policy.
   * 
   * @param policy is a Project Policy.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section.
   */
  protected void saveMissingFields(Project project, ProjectPolicy policy, String cycle, int year, Boolean upkeep,
    String sectionName, BaseAction action) {
    // Reporting missing fields into the database.
    SectionStatus status =
      sectionStatusManager.getSectionStatusByProjectPolicy(policy.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {
      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setProjectPolicy(policy);
      status.setSectionName(sectionName);
      status.setProject(project);
    }
    status.setMissingFields(action.getMissingFields().toString());

    if (status.getMissingFields().length() > 0) {
      if (status.getMissingFields().equals("null")) {
        status.setMissingFields("");
      }
      status.setMissingFields(status.getMissingFields());
    } else {
      status.setMissingFields("");
    }

    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);
  }

  /**
   * This method saves the missing fields into the database for a section at project level.
   * 
   * @param project is a project.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(Project project, String cycle, int year, Boolean upkeep, String sectionName,
    BaseAction action) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByProject(project.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {
      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setProject(project);
      status.setSectionName(sectionName);
    }

    // Validate if the form have missing fileds in project sections issue #1209
    String sMissingField = action.getMissingFields().toString();
    if (sMissingField.length() > 0) {
      if (sMissingField.equals("null") && sectionName.equals("policies")) {
        status.setMissingFields("");
      }
      status.setMissingFields(sMissingField);
    } else {
      status.setMissingFields("");
    }
    sectionStatusManager.saveSectionStatus(status);
  }

  /**
   * This method saves the missing fields into the database for a section at project Outcome level.
   * 
   * @param projectOutcome is a project Outcome.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(ProjectOutcome projectOutcome, String cycle, int year, Boolean upkeep,
    String sectionName, BaseAction action) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByProjectOutcome(projectOutcome.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setProjectOutcome(projectOutcome);
      status.setSectionName(sectionName);
      status.setProject(projectOutcome.getProject());

    }
    status.setMissingFields(action.getMissingFields().toString());
    sectionStatusManager.saveSectionStatus(status);
    // Not sure if this is still required to set the missingFields to length zero???
    action.getMissingFields().setLength(0);
  }


  /**
   * This method saves the missing fields into the database for a section at annual report synthesis level.
   * 
   * @param reportSynthesis is a reportSynthesis.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(ReportSynthesis reportSynthesis, String cycle, int year, Boolean upkeep,
    String sectionName, BaseAction action) {
    // Reporting missing fields into the database.
    SectionStatus status =
      sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(), cycle, year, upkeep, sectionName);
    if (status == null) {
      status = new SectionStatus();
      status.setCycle(cycle);
      status.setUpkeep(upkeep);
      status.setYear(year);
      status.setReportSynthesis(reportSynthesis);
      status.setSectionName(sectionName);
    }

    // Validate if the form have missing fileds in project sections issue #1209
    String sMissingField = action.getMissingFields().toString();
    if (sMissingField.length() > 0) {
      status.setMissingFields(sMissingField);
    } else {
      status.setMissingFields("");
    }
    sectionStatusManager.saveSectionStatus(status);
  }


  /**
   * This method saves the missing fields into the database for a section at ImpactPathway.
   * 
   * @param crpProgram is a CrpProgram.
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section (description, partners, etc.).
   */
  protected void saveMissingFieldsImpactPathway(CrpProgram crpProgram, String sectionName, int year, String cyle,
    Boolean upkeep, BaseAction action) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByCrpProgam(crpProgram.getId(), sectionName, cyle, year, upkeep);
    if (status == null) {

      status = new SectionStatus();
      status.setSectionName(sectionName);
      status.setCycle(cyle);
      status.setYear(year);
      status.setUpkeep(upkeep);
      status.setCrpProgram(crpProgram);

    }
    if (action.getMissingFields().length() > 0) {
      status.setMissingFields(action.getMissingFields().toString());
    } else {
      status.setMissingFields("");
    }

    sectionStatusManager.saveSectionStatus(status);
  }

  /**
   * This method saves the missing fields into the database for a section at Project.
   * 
   * @param project is a Project.
   * @param upkeep could be '0' or '1'
   * @param sectionName is the name of the section (description, partners, etc.).
   */
  protected void saveMissingFieldsProject(Project project, String sectionName, String cycle, int year, Boolean upkeep,
    BaseAction action) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByCrpProgam(project.getId(), sectionName, cycle, year, upkeep);
    if (status == null) {

      status = new SectionStatus();
      status.setSectionName(sectionName);
      status.setProject(project);
    }
    if (action.getMissingFields().length() > 0) {
      status.setMissingFields(action.getMissingFields().toString());
    } else {
      status.setMissingFields("");
    }

    sectionStatusManager.saveSectionStatus(status);
  }

  public void setMissingFields(StringBuilder missingFields) {
    this.missingFields = missingFields;
  }

  protected void validateLessonsLearn(BaseAction action, IpProgram program) {
    if (program.getProjectComponentLesson() != null) {
      ProjectComponentLesson lesson = program.getProjectComponentLesson();
      if (!(this.isValidString(lesson.getLessons()) && (this.wordCount(lesson.getLessons()) <= 100))) {
        // Let them save.
        action.addMessage("Lessons");

        action.addMissingField("projectLessons.lessons");

      }
    }
  }


  /**
   * This method counts the number of words in a given text.
   * 
   * @param text is some text to be validated.
   * @return the number of words.
   */
  protected int wordCount(String text) {
    if (text != null) {
      text = text.trim();
      return text.isEmpty() ? 0 : text.split("\\s+").length;
    } else {
      return 0;
    }
  }
}
