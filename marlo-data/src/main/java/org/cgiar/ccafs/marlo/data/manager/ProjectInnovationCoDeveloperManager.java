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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCoDeveloper;

import java.util.List;


public interface ProjectInnovationCoDeveloperManager {

  /**
   * This method removes a co-developing institution from an innovation in its phase and the phases it replicates to.
   *
   * @param projectInnovationCoDeveloperId is the projectInnovationCoDeveloper identifier.
   */
  public void deleteProjectInnovationCoDeveloper(long projectInnovationCoDeveloperId);

  /**
   * This method gets the active co-developing institutions of an innovation in a phase.
   *
   * @param innovationID is the innovation identifier.
   * @param phaseID is the phase identifier.
   * @return the institutions, an empty list when there are none.
   */
  public List<ProjectInnovationCoDeveloper> getByInnovationAndPhase(long innovationID, long phaseID);

  /**
   * This method saves a co-developing institution of an innovation and replicates it to the next phases.
   *
   * @param projectInnovationCoDeveloper is the object with the new information to be added/updated.
   * @return the saved object.
   */
  public ProjectInnovationCoDeveloper saveProjectInnovationCoDeveloper(
    ProjectInnovationCoDeveloper projectInnovationCoDeveloper);
}
