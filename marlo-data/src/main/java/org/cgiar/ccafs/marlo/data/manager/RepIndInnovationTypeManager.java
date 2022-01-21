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

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisInnovationsByTypeDTO;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface RepIndInnovationTypeManager {


  /**
   * This method removes a specific repIndInnovationType value from the database.
   * 
   * @param repIndInnovationTypeId is the repIndInnovationType identifier.
   * @return true if the repIndInnovationType was successfully deleted, false otherwise.
   */
  public void deleteRepIndInnovationType(long repIndInnovationTypeId);


  /**
   * This method validate if the repIndInnovationType identify with the given id exists in the system.
   * 
   * @param repIndInnovationTypeID is a repIndInnovationType identifier.
   * @return true if the repIndInnovationType exists, false otherwise.
   */
  public boolean existRepIndInnovationType(long repIndInnovationTypeID);


  /**
   * This method gets a list of repIndInnovationType that are active
   * 
   * @return a list from RepIndInnovationType null if no exist records
   */
  public List<RepIndInnovationType> findAll();

  /**
   * This method gets a list of ReportSynthesisInnovationsByTypeDTO from a given innovation list:
   * List of Innovations grouped by Type
   * 
   * @return a list from ReportSynthesisInnovationsByTypeDTO null if no exist records
   */
  public List<ReportSynthesisInnovationsByTypeDTO>
    getInnovationsByTypeDTO(List<ProjectInnovation> selectedProjectInnovations, Phase phase);


  /**
   * This method gets a repIndInnovationType object by a given repIndInnovationType identifier.
   * 
   * @param repIndInnovationTypeID is the repIndInnovationType identifier.
   * @return a RepIndInnovationType object.
   */
  public RepIndInnovationType getRepIndInnovationTypeById(long repIndInnovationTypeID);

  public List<RepIndInnovationType> oneCGIARFindAll();


  /**
   * This method saves the information of the given repIndInnovationType
   * 
   * @param repIndInnovationType - is the repIndInnovationType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndInnovationType was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndInnovationType saveRepIndInnovationType(RepIndInnovationType repIndInnovationType);


}
