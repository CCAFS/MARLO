package org.cgiar.ccafs.marlo.validation;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBilateralCofinancing;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;

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


    }
    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);
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
   * This method saves the missing fields into the database for a section at project cofunded level.
   * 
   * @param projectBilateralCofinancing is a project cofunded.
   * @param cycle could be 'Planning' or 'Reporting'
   * @param sectionName is the name of the section inside deliverables.
   */
  protected void saveMissingFields(ProjectBilateralCofinancing projectBilateralCofinancing, String cycle, int year,
    String sectionName) {
    // Reporting missing fields into the database.

    SectionStatus status = sectionStatusManager.getSectionStatusByProjectCofunded(projectBilateralCofinancing.getId(),
      cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setProjectBilateralCofinancing(projectBilateralCofinancing);
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


    }
    status.setMissingFields(this.missingFields.toString());
    sectionStatusManager.saveSectionStatus(status);
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

  protected void validateLessonsLearn(BaseAction action, Project project) {
    if (project.getProjectComponentLesson() != null) {
      ProjectComponentLesson lesson = project.getProjectComponentLesson();
      if (!(this.isValidString(lesson.getLessons()) && this.wordCount(lesson.getLessons()) <= 100)) {
        // Let them save.
        this.addMessage("Lessons");

        this.addMissingField("projectLessons.lessons");

      }
    }

  }


  protected void validateLessonsLearnOutcome(BaseAction action, ProjectOutcome project) {
    if (project.getProjectComponentLesson() != null) {
      ProjectComponentLesson lesson = project.getProjectComponentLesson();
      if (!(this.isValidString(lesson.getLessons()) && this.wordCount(lesson.getLessons()) <= 100)) {
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
