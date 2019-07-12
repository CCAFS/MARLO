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

import org.cgiar.ccafs.marlo.data.model.MarloMessage;

import java.util.List;


/**
 * @author CCAFS
 */

public interface MarloMessageManager {


  /**
   * This method removes a specific marloMessage value from the database.
   * 
   * @param marloMessageId is the marloMessage identifier.
   * @return true if the marloMessage was successfully deleted, false otherwise.
   */
  public void deleteMarloMessage(long marloMessageId);


  /**
   * This method validate if the marloMessage identify with the given id exists in the system.
   * 
   * @param marloMessageID is a marloMessage identifier.
   * @return true if the marloMessage exists, false otherwise.
   */
  public boolean existMarloMessage(long marloMessageID);


  /**
   * This method gets a list of marloMessage that are active
   * 
   * @return a list from MarloMessage null if no exist records
   */
  public List<MarloMessage> findAll();


  /**
   * This method gets a marloMessage object by a given marloMessage identifier.
   * 
   * @param marloMessageID is the marloMessage identifier.
   * @return a MarloMessage object.
   */
  public MarloMessage getMarloMessageById(long marloMessageID);

  /**
   * This method saves the information of the given marloMessage
   * 
   * @param marloMessage - is the marloMessage object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the marloMessage was
   *         updated
   *         or -1 is some error occurred.
   */
  public MarloMessage saveMarloMessage(MarloMessage marloMessage);


}
