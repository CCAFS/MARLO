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

import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface DeliverableManager {


  public Deliverable copyDeliverable(Deliverable deliverable, Phase phase);


  /**
   * This method removes a specific deliverable value from the database.
   * 
   * @param deliverableId is the deliverable identifier.
   * @return true if the deliverable was successfully deleted, false otherwise.
   */
  public void deleteDeliverable(long deliverableId);


  /**
   * This method validate if the deliverable identify with the given id exists in the system.
   * 
   * @param deliverableID is a deliverable identifier.
   * @return true if the deliverable exists, false otherwise.
   */
  public boolean existDeliverable(long deliverableID);

  /**
   * This method gets a list of deliverable that are active
   * 
   * @return a list from Deliverable null if no exist records
   */
  public List<Deliverable> findAll();

  /**
   * This method gets a deliverable object by a given deliverable identifier.
   * 
   * @param deliverableID is the deliverable identifier.
   * @return a Deliverable object.
   */
  public Deliverable getDeliverableById(long deliverableID);

  /**
   * This method gets a list of Deliverable that are active by a given parameters
   * 
   * @param filterPhaseYear: true for specific phase year (excluding cancelled), false for all years and statuses
   * @param filterParticipants: filter deliverables that have participants
   * @param filterPublications: filter publications -null for all deliverables and publications-
   * @return a list from Deliverable null if no exist records
   */
  public List<Deliverable> getDeliverablesByParameters(Phase phase, boolean filterPhaseYear, boolean filterParticipants,
    Boolean filterPublications);

  /**
   * This method gets a list of Deliverable that are active by a given phase
   * 
   * @return a list from Deliverable null if no exist records
   */
  public List<Deliverable> getDeliverablesByPhase(long phase);

  public List<Deliverable> getDeliverablesByProjectAndPhase(long phase, long project);

  public List<Deliverable> getDeliverablesLeadByInstitution(long institutionId, long phaseId);

  public List<Deliverable> getDeliverablesLeadByUser(long userId, long phaseId);

  /**
   * This method gets a list of publications that are active by a given phase
   * 
   * @return a list from Deliverable null if no exist records
   */

  public List<Deliverable> getPublicationsByPhase(long phase);

  /**
   * This method gets a list of publications that are active for an specific liaisonInstitution
   * Flagship: Get the list of projects that have project_focus equal to the liaisonInstitution
   * PMU: Get the list of liaison according to what was selected by the flagships
   * 
   * @param liaisonInstitution
   * @param phase
   * @return a list from Deliverable null if no exist records
   */
  public List<Deliverable> getPublicationsList(LiaisonInstitution liaisonInstitution, Phase phase);

  /**
   * This method saves the information of the given deliverable
   * 
   * @param deliverable - is the deliverable object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverable was
   *         updated
   *         or -1 is some error occurred.
   */
  public Deliverable saveDeliverable(Deliverable deliverable);

  /**
   * This method saves the information of the given deliverable and save the history in the auditlog
   * 
   * @param deliverable - is the deliverable object with the new information to be added/updated.
   * @param section - the action name of the section that execute the save method
   * @param relationsName - the model class relations of deliverables that save in the auditlog.
   * @return
   */
  public Deliverable saveDeliverable(Deliverable deliverable, String section, List<String> relationsName, Phase phase);

}
