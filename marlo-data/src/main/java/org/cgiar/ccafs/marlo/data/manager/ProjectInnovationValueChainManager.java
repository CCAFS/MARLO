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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationValueChain;

import java.util.List;


public interface ProjectInnovationValueChainManager {

  /**
   * This method removes a value chain from an innovation in its phase and the phases it replicates to.
   *
   * @param projectInnovationValueChainId is the projectInnovationValueChain identifier.
   */
  public void deleteProjectInnovationValueChain(long projectInnovationValueChainId);

  /**
   * This method gets the active value chains of an innovation in a phase.
   *
   * @param innovationID is the innovation identifier.
   * @param phaseID is the phase identifier.
   * @return the value chains, an empty list when there are none.
   */
  public List<ProjectInnovationValueChain> getByInnovationAndPhase(long innovationID, long phaseID);

  /**
   * This method saves a value chain of an innovation and replicates it to the next phases.
   *
   * @param projectInnovationValueChain is the object with the new information to be added/updated.
   * @return the saved object.
   */
  public ProjectInnovationValueChain saveProjectInnovationValueChain(
    ProjectInnovationValueChain projectInnovationValueChain);
}
