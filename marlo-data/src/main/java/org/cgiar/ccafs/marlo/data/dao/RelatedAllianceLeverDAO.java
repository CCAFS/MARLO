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

import org.cgiar.ccafs.marlo.data.model.RelatedAllianceLever;

import java.util.List;


public interface RelatedAllianceLeverDAO {

  /**
   * This method removes a specific relatedAllianceLever value from the database.
   * 
   * @param relatedAllianceLeverId is the relatedAllianceLever identifier.
   * @return true if the relatedAllianceLever was successfully deleted, false otherwise.
   */
  public void deleteRelatedAllianceLever(long relatedAllianceLeverId);

  /**
   * This method validate if the relatedAllianceLever identify with the given id exists in the system.
   * 
   * @param relatedAllianceLeverID is a relatedAllianceLever identifier.
   * @return true if the relatedAllianceLever exists, false otherwise.
   */
  public boolean existRelatedAllianceLever(long relatedAllianceLeverID);

  /**
   * This method gets a relatedAllianceLever object by a given relatedAllianceLever identifier.
   * 
   * @param relatedAllianceLeverID is the relatedAllianceLever identifier.
   * @return a RelatedAllianceLever object.
   */
  public RelatedAllianceLever find(long id);

  /**
   * This method gets a list of relatedAllianceLever that are active
   * 
   * @return a list from RelatedAllianceLever null if no exist records
   */
  public List<RelatedAllianceLever> findAll();


  /**
   * This method gets a list of relatedAllianceLever that are active, by phase
   * 
   * @param phaseId phase ID
   * @return a list from RelatedAllianceLever null if no exist records
   */
  List<RelatedAllianceLever> findAllByPhase(long phaseId);

  /**
   * This method saves the information of the given relatedAllianceLever
   * 
   * @param relatedAllianceLever - is the relatedAllianceLever object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the relatedAllianceLever was
   *         updated
   *         or -1 is some error occurred.
   */
  public RelatedAllianceLever save(RelatedAllianceLever relatedAllianceLever);
}
