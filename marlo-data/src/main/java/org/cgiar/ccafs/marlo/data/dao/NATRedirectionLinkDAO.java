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

import org.cgiar.ccafs.marlo.data.model.NATRedirectionLink;

import java.util.List;


public interface NATRedirectionLinkDAO {

  /**
   * This method removes a specific NATRedirectionLink value from the database.
   * 
   * @param NATRedirectionLinkId is the NATRedirectionLink identifier.
   * @return true if the NATRedirectionLink was successfully deleted, false otherwise.
   */
  public void deleteNATRedirectionLink(long NATRedirectionLinkId);

  /**
   * This method validate if the NATRedirectionLink identify with the given id exists in the system.
   * 
   * @param NATRedirectionLinkID is a NATRedirectionLink identifier.
   * @return true if the NATRedirectionLink exists, false otherwise.
   */
  public boolean existNATRedirectionLink(long NATRedirectionLinkID);

  /**
   * This method gets a NATRedirectionLink object by a given NATRedirectionLink identifier.
   * 
   * @param NATRedirectionLinkID is the NATRedirectionLink identifier.
   * @return a NATRedirectionLink object.
   */
  public NATRedirectionLink find(long id);

  /**
   * This method gets a list of NATRedirectionLink that are active
   * 
   * @return a list from NATRedirectionLink null if no exist records
   */
  public List<NATRedirectionLink> findAll();

  /**
   * This method gets a NATRedirectionLink object by a given indicator name and id.
   * 
   * @param indicatorName is the indicator name.
   * @param indicatorId is the indicator identifier.
   * @return a NATRedirectionLink object.
   */
  public NATRedirectionLink findByIndicatorAndId(String indicatorName, long indicatorId);


  /**
   * This method saves the information of the given NATRedirectionLink
   * 
   * @param NATRedirectionLink - is the NATRedirectionLink object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the NATRedirectionLink was
   *         updated
   *         or -1 is some error occurred.
   */
  public NATRedirectionLink save(NATRedirectionLink NATRedirectionLink);
}
