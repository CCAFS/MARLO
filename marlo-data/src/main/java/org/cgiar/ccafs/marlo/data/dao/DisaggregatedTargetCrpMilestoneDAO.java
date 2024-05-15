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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.DisaggregatedTargetCrpMilestone;

import java.util.List;


public interface DisaggregatedTargetCrpMilestoneDAO {

  /**
   * This method removes a specific disaggregatedTargetCrpMilestone value from the database.
   * 
   * @param disaggregatedTargetCrpMilestoneId is the disaggregatedTargetCrpMilestone identifier.
   * @return true if the disaggregatedTargetCrpMilestone was successfully deleted, false otherwise.
   */
  public void deleteDisaggregatedTargetCrpMilestone(long disaggregatedTargetCrpMilestoneId);

  /**
   * This method validate if the disaggregatedTargetCrpMilestone identify with the given id exists in the system.
   * 
   * @param disaggregatedTargetCrpMilestoneID is a disaggregatedTargetCrpMilestone identifier.
   * @return true if the disaggregatedTargetCrpMilestone exists, false otherwise.
   */
  public boolean existDisaggregatedTargetCrpMilestone(long disaggregatedTargetCrpMilestoneID);

  /**
   * This method gets a disaggregatedTargetCrpMilestone object by a given disaggregatedTargetCrpMilestone identifier.
   * 
   * @param disaggregatedTargetCrpMilestoneID is the disaggregatedTargetCrpMilestone identifier.
   * @return a DisaggregatedTargetCrpMilestone object.
   */
  public DisaggregatedTargetCrpMilestone find(long id);

  /**
   * This method gets a list of disaggregatedTargetCrpMilestone that are active
   * 
   * @return a list from DisaggregatedTargetCrpMilestone null if no exist records
   */
  public List<DisaggregatedTargetCrpMilestone> findAll();


  /**
   * This method saves the information of the given disaggregatedTargetCrpMilestone
   * 
   * @param disaggregatedTargetCrpMilestone - is the disaggregatedTargetCrpMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the disaggregatedTargetCrpMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public DisaggregatedTargetCrpMilestone save(DisaggregatedTargetCrpMilestone disaggregatedTargetCrpMilestone);
}
