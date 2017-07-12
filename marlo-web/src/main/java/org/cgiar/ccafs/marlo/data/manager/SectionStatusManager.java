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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.manager.impl.SectionStatusManagerImpl;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(SectionStatusManagerImpl.class)
public interface SectionStatusManager {


  /**
   * This method removes a specific sectionStatus value from the database.
   * 
   * @param sectionStatusId is the sectionStatus identifier.
   * @return true if the sectionStatus was successfully deleted, false otherwise.
   */
  public boolean deleteSectionStatus(long sectionStatusId);


  /**
   * This method validate if the sectionStatus identify with the given id exists in the system.
   * 
   * @param sectionStatusID is a sectionStatus identifier.
   * @return true if the sectionStatus exists, false otherwise.
   */
  public boolean existSectionStatus(long sectionStatusID);


  /**
   * This method gets a list of sectionStatus that are active
   * 
   * @return a list from SectionStatus null if no exist records
   */
  public List<SectionStatus> findAll();


  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param projectID is the project outcome identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusByCaseStudy(long caseStudyID, String cycle, int year, String sectionName);

  public SectionStatus getSectionStatusByCrpIndicators(long liaisonInstitutionID, String cycle, int year,
    String sectionName);


  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param crpProgramID is the crp program identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusByCrpProgam(long crpProgramID, String sectionName);

  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param deliverableID is the deliverable identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusByDeliverable(long deliverableID, String cycle, int year, String sectionName);

  public SectionStatus getSectionStatusByFundingSource(long fundingSource, String cycle, Integer year,
    String sectionName);

  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param sectionStatusID is the sectionStatus identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusById(long sectionStatusID);

  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param ipProgramID is the IpProgram identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusByIpProgram(long ipProgramID, String cycle, int year, String sectionName);

  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param projectID is the project identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusByProject(long projectID, String cycle, int year, String sectionName);

  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param projectID is the project co - funded identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusByProjectCofunded(long projectID, String cycle, int year, String sectionName);


  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param projectID is the project outcome identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusByProjectHighlight(long projectHighlightID, String cycle, int year,
    String sectionName);


  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param projectID is the project outcome identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusByProjectOutcome(long projectID, String cycle, int year, String sectionName);

  /**
   * This method gets a sectionStatus object by a given sectionStatus identifier.
   * 
   * @param ipProgramID is the IpProgram identifier.
   * @return a SectionStatus object.
   */
  public SectionStatus getSectionStatusBySynthesisMog(long ipProgramID, String cycle, int year, String sectionName);

  /**
   * This method saves the information of the given sectionStatus
   * 
   * @param sectionStatus - is the sectionStatus object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the sectionStatus was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveSectionStatus(SectionStatus sectionStatus);


}
