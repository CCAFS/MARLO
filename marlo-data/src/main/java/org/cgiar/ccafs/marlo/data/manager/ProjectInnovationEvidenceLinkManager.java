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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationEvidenceLink;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationEvidenceLinkManager {

  /**
   * This method removes a specific projectInnovationEvidenceLink value from the database.
   * 
   * @param projectInnovationEvidenceLinkId is the projectInnovationEvidenceLink identifier.
   * @return true if the projectInnovationEvidenceLink was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationEvidenceLink(long projectInnovationEvidenceLinkId);

  /**
   * This method validate if the projectInnovationEvidenceLink identify with the given id exists in the system.
   * 
   * @param projectInnovationEvidenceLinkID is a projectInnovationEvidenceLink identifier.
   * @return true if the projectInnovationEvidenceLink exists, false otherwise.
   */
  public boolean existProjectInnovationEvidenceLink(long projectInnovationEvidenceLinkID);

  /**
   * This method gets a list of projectInnovationEvidenceLink that are active
   * 
   * @return a list from ProjectInnovationEvidenceLink null if no exist records
   */
  public List<ProjectInnovationEvidenceLink> findAll();

  /**
   * This method gets a list of projectInnovationEvidenceLink by a given projectExpectedStudy identifier.
   * 
   * @param innovationId is the projectExpectedStudy identifier.
   * @return a list of projectInnovationEvidenceLink objects.
   */
  public List<ProjectInnovationEvidenceLink> getAllInnovationLinksByStudy(Long innovationId);

  /**
   * This method gets a projectInnovationEvidenceLink object by a given projectInnovationEvidenceLink identifier.
   * 
   * @param projectInnovationEvidenceLinkID is the projectInnovationEvidenceLink identifier.
   * @return a ProjectInnovationEvidenceLink object.
   */
  public ProjectInnovationEvidenceLink getProjectInnovationEvidenceLinkById(long projectInnovationEvidenceLinkID);

  /**
   * This method gets a projectInnovationEvidenceLink List by a given projectExpectedStudy and phase identifier
   * 
   * @param innovationID is the projectExpectedStudy identifier.
   * @param phaseID is the phase identifier
   * @return a ProjectInnovationEvidenceLink object.
   */
  public List<ProjectInnovationEvidenceLink> getProjectInnovationEvidenceLinkByPhase(Long innovationID, Long phaseID);

  /**
   * This method gets a projectInnovationEvidenceLink object by a given projectExpectedStudy, phase identifier and link
   * 
   * @param innovationID is the projectExpectedStudy identifier.
   * @param link is the url link
   * @param phaseID is the phase identifier
   * @return a ProjectInnovationEvidenceLink object.
   */
  public ProjectInnovationEvidenceLink getProjectInnovationEvidenceLinkByPhase(Long innovationID, String link,
    Long phaseID);

  /**
   * This method saves the information of the given projectInnovationEvidenceLink
   * 
   * @param projectInnovationEvidenceLink - is the projectInnovationEvidenceLink object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationEvidenceLink
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationEvidenceLink
    saveProjectInnovationEvidenceLink(ProjectInnovationEvidenceLink projectInnovationEvidenceLink);
}
