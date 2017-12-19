package org.cgiar.ccafs.marlo.validation;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.ICenterSectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterSectionStatus;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Calendar;

import javax.mail.internet.InternetAddress;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseValidator {

  private static final Logger LOG = LoggerFactory.getLogger(BaseValidator.class);

  @Inject
  protected APConfig config;
  protected StringBuilder validationMessage;
  protected StringBuilder missingFields;

  @Inject
  private SectionStatusManager sectionStatusManager;
  @Inject
  private ICenterSectionStatusManager centerSectionStatusManager;


  @Inject
  public BaseValidator() {
    validationMessage = new StringBuilder();
    missingFields = new StringBuilder();
  }

  protected void addMessage(String message) {
    validationMessage.append("<p> - ");
    validationMessage.append(message);
    validationMessage.append("</p>");

    this.addMissingField(message);
  }

  /**
   * This method add a missing field separated by a semicolon (;).
   * 
   * @param field is the name of the field.
   */
  protected void addMissingField(String field) {
    if (missingFields.length() != 0) {
      missingFields.append(";");
    }
    missingFields.append(field);
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
   * ******************************************************************************************
   * ************************* CENTER METHOD **************************************************
   * *******************************************************************************************
   * This method saves the missing fields into the database for a section at CapDev.
   * 
   * @param program is a CenterProgram.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CapacityDevelopment capacityDevelopment, String sectionName) {

    int year = Calendar.getInstance().get(Calendar.YEAR);

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByCapdev(capacityDevelopment.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setCapacityDevelopment(capacityDevelopment);
      status.setYear(year);
    }
    if (this.missingFields.length() > 0) {
      status.setMissingFields(this.missingFields.toString());
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
    String sectionName) {
    int year = Calendar.getInstance().get(Calendar.YEAR);

    CenterSectionStatus status = centerSectionStatusManager.getSectionStatusBySupDocs(deliverable.getId(),
      capacityDevelopment.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setDeliverable(deliverable);
      status.setCapacityDevelopment(capacityDevelopment);
      status.setYear(year);
    }
    if (this.missingFields.length() > 0) {
      status.setMissingFields(this.missingFields.toString());
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
   * @param program is a CenterProgram.
   * @param project is a CenterProject.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CenterDeliverable deliverable, CenterProject project, String sectionName) {
    int year = Calendar.getInstance().get(Calendar.YEAR);

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(), project.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setDeliverable(deliverable);
      status.setProject(project);
      status.setYear(year);
    }
    if (this.missingFields.length() > 0) {
      status.setMissingFields(this.missingFields.toString());
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
   * @param program is a CenterProgram.
   * @param outcome is a CenterOutcome.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CenterProgram program, CenterOutcome outcome, String sectionName) {
    int year = Calendar.getInstance().get(Calendar.YEAR);

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByOutcome(program.getId(), outcome.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setResearchProgram(program);
      status.setResearchOutcome(outcome);
      status.setYear(year);
    }
    if (this.missingFields.length() > 0) {
      status.setMissingFields(this.missingFields.toString());
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
   * @param program is a CenterProgram.
   * @param output is a CenterOutput.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CenterProgram program, CenterOutput output, String sectionName) {
    int year = Calendar.getInstance().get(Calendar.YEAR);

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByOutput(program.getId(), output.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setResearchProgram(program);
      status.setResearchOutput(output);
      status.setYear(year);
    }
    if (this.missingFields.length() > 0) {
      status.setMissingFields(this.missingFields.toString());
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
   * @param program is a CenterProgram.
   * @param project is a CenterProject.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CenterProgram program, CenterProject project, String sectionName) {
    int year = Calendar.getInstance().get(Calendar.YEAR);

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByProject(program.getId(), project.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setProject(project);
      status.setYear(year);
    }
    if (this.missingFields.length() > 0) {
      status.setMissingFields(this.missingFields.toString());
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
   * @param program is a CenterProgram.
   * @param sectionName is the name of the section (researchImpact, researchTopics, etc.).
   */
  protected void saveMissingFields(CenterProgram program, String sectionName) {

    int year = Calendar.getInstance().get(Calendar.YEAR);

    CenterSectionStatus status =
      centerSectionStatusManager.getSectionStatusByProgram(program.getId(), sectionName, year);
    if (status == null) {

      status = new CenterSectionStatus();
      status.setSectionName(sectionName);
      status.setResearchProgram(program);
      status.setYear(year);
    }
    if (this.missingFields.length() > 0) {
      status.setMissingFields(this.missingFields.toString());
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
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(Deliverable deliverable, String cycle, int year, String sectionName) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(), cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setDeliverable(deliverable);
      status.setSectionName(sectionName);
      status.setProject(deliverable.getProject());

    }
    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);
    this.missingFields.setLength(0);


  }

  /**
   * This method saves the missing fields into the database for a section at project Outcome level.
   * 
   * @param fundingSource is a funding Source
   * @param cycle could be 'Planning' or 'Reporting'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(FundingSource fundingSource, String cycle, Integer year, String sectionName) {
    // Reporting missing fields into the database.
    int a = 0;
    LOG.debug("save MissingField :" + a);
    SectionStatus status =
      sectionStatusManager.getSectionStatusByFundingSource(fundingSource.getId(), cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setFundingSource(fundingSource);
      status.setSectionName(sectionName);
      fundingSource.getSectionStatuses().add(status);
    }
    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);
    this.missingFields.setLength(0);
  }


  /**
   * This method saves the missing fields into the database for a section at deliverable level.
   * 
   * @param ipLiaisonInstitution is a ipLiaisonInstitution.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(IpLiaisonInstitution ipLiaisonInstitution, String cycle, int year,
    String sectionName) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByCrpIndicators(ipLiaisonInstitution.getId(), cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setIpLiaisonInstitution(ipLiaisonInstitution);;
      status.setSectionName(sectionName);

    }
    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);
    this.missingFields.setLength(0);


  }


  /**
   * This method saves the missing fields into the database for a section at deliverable level.
   * 
   * @param deliverable is a deliverable.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(IpProgram program, String cycle, int year, String sectionName) {
    // Reporting missing fields into the database.

    SectionStatus status = sectionStatusManager.getSectionStatusByIpProgram(program.getId(), cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setIpProgram(program);
      status.setSectionName(sectionName);

    }
    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);
    this.missingFields.setLength(0);


  }

  /**
   * This method saves the missing fields into the database for a section at project Case Study level.
   * 
   * @param caseStudy is a Case Study.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(Project project, CaseStudy caseStudy, String cycle, int year, String sectionName) {
    // Reporting missing fields into the database.
    SectionStatus status =
      sectionStatusManager.getSectionStatusByCaseStudy(caseStudy.getId(), cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setCaseStudy(caseStudy);
      status.setSectionName(sectionName);
      status.setProject(project);

    }
    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);
    this.missingFields.setLength(0);
  }

  /**
   * This method saves the missing fields into the database for a section at project Case Study level.
   * 
   * @param highlight is a Project Highlight.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(Project project, ProjectHighlight highlight, String cycle, int year,
    String sectionName) {
    // Reporting missing fields into the database.
    SectionStatus status =
      sectionStatusManager.getSectionStatusByProjectHighlight(highlight.getId(), cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setProjectHighlight(highlight);
      status.setSectionName(sectionName);
      status.setProject(project);

    }
    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);
    this.missingFields.setLength(0);
  }

  /**
   * This method saves the missing fields into the database for a section at project level.
   * 
   * @param project is a project.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(Project project, String cycle, int year, String sectionName) {
    // Reporting missing fields into the database.

    SectionStatus status = sectionStatusManager.getSectionStatusByProject(project.getId(), cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setProject(project);
      status.setSectionName(sectionName);


    }

    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);

  }


  /**
   * This method saves the missing fields into the database for a section at project Outcome level.
   * 
   * @param projectOutcome is a project Outcome.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(ProjectOutcome projectOutcome, String cycle, int year, String sectionName) {
    // Reporting missing fields into the database.

    SectionStatus status =
      sectionStatusManager.getSectionStatusByProjectOutcome(projectOutcome.getId(), cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setProjectOutcome(projectOutcome);
      status.setSectionName(sectionName);
      status.setProject(projectOutcome.getProject());

    }
    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);
    this.missingFields.setLength(0);
  }


  /**
   * This method saves the missing fields into the database for a section at ImpactPathway.
   * 
   * @param crpProgram is a CrpProgram.
   * @param sectionName is the name of the section (description, partners, etc.).
   */
  protected void saveMissingFieldsImpactPathway(CrpProgram crpProgram, String sectionName) {
    // Reporting missing fields into the database.
    int year = 0;

    SectionStatus status = sectionStatusManager.getSectionStatusByCrpProgam(crpProgram.getId(), sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setSectionName(sectionName);
      status.setCrpProgram(crpProgram);
    }
    if (this.missingFields.length() > 0) {
      status.setMissingFields(this.missingFields.toString());
    } else {
      status.setMissingFields("");
    }

    sectionStatusManager.saveSectionStatus(status);
  }

  /**
   * This method saves the missing fields into the database for a section at Project.
   * 
   * @param project is a Project.
   * @param sectionName is the name of the section (description, partners, etc.).
   */
  protected void saveMissingFieldsProject(Project project, String sectionName) {
    // Reporting missing fields into the database.
    int year = 0;

    SectionStatus status = sectionStatusManager.getSectionStatusByCrpProgam(project.getId(), sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setSectionName(sectionName);
      status.setProject(project);
    }
    if (this.missingFields.length() > 0) {
      status.setMissingFields(this.missingFields.toString());
    } else {
      status.setMissingFields("");
    }

    sectionStatusManager.saveSectionStatus(status);
  }

  protected void validateLessonsLearn(BaseAction action, IpProgram program) {
    if (program.getProjectComponentLesson() != null) {
      ProjectComponentLesson lesson = program.getProjectComponentLesson();
      if (!(this.isValidString(lesson.getLessons()) && (this.wordCount(lesson.getLessons()) <= 100))) {
        // Let them save.
        this.addMessage("Lessons");

        this.addMissingField("projectLessons.lessons");

      }
    }

  }

  protected void validateLessonsLearn(BaseAction action, Project project) {
    if (project.getProjectComponentLesson() != null) {
      ProjectComponentLesson lesson = project.getProjectComponentLesson();
      if (!(this.isValidString(lesson.getLessons()) && (this.wordCount(lesson.getLessons()) <= 100))) {
        // Let them save.
        this.addMessage("Lessons");

        this.addMissingField("projectLessons.lessons");

      }
    }

  }

  protected void validateLessonsLearnOutcome(BaseAction action, ProjectOutcome project) {
    if (project.getProjectComponentLesson() != null) {
      ProjectComponentLesson lesson = project.getProjectComponentLesson();
      if (!(this.isValidString(lesson.getLessons()) && (this.wordCount(lesson.getLessons()) <= 100))) {
        // Let them save.
        this.addMessage("Lessons");

        this.addMissingField("projectLessons.lessons");

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
    text = text.trim();
    return text.isEmpty() ? 0 : text.split("\\s+").length;
  }
}
