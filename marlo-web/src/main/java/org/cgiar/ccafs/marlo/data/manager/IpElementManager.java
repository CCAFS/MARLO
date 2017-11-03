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

import org.cgiar.ccafs.marlo.data.manager.impl.IpElementManagerImpl;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpProgram;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(IpElementManagerImpl.class)
public interface IpElementManager {


  /**
   * This method removes a specific ipElement value from the database.
   * 
   * @param ipElementId is the ipElement identifier.
   * @return true if the ipElement was successfully deleted, false otherwise.
   */
  public void deleteIpElement(long ipElementId);


  /**
   * This method validate if the ipElement identify with the given id exists in the system.
   * 
   * @param ipElementID is a ipElement identifier.
   * @return true if the ipElement exists, false otherwise.
   */
  public boolean existIpElement(long ipElementID);

  /**
   * This method gets a list of ipElement that are active
   * 
   * @return a list from IpElement null if no exist records
   */
  public List<IpElement> findAll();

  /**
   * This method gets a ipElement object by a given ipElement identifier.
   * 
   * @param ipElementID is the ipElement identifier.
   * @return a IpElement object.
   */
  public IpElement getIpElementById(long ipElementID);


  public List<IpElement> getIPElementListForOutcomeSynthesis(IpProgram programm, long type);

  /**
   * This method return all the impact pathways elements
   * setted with the basic information id, description,
   * translatedOf and contributesTo
   * 
   * @param program - Object with the program information
   * @return a list of ipElements present in the database.
   *         If the program has id -1 the full list of
   *         ipElements is returned. Otherwise, the list is
   *         filtered by the program given.
   */
  public List<IpElement> getIPElementListForSynthesis(IpProgram program);

  /**
   * This method gets all the elements that are children of the element
   * passed as parameter.
   * 
   * @param parent
   * @return a list of IPElements objects with the information
   */
  public List<IpElement> getIPElementsByParent(IpElement parent, int relationTypeID);

  public List<IpElement> getIPElementsRelated(int ipElementID, int relationTypeID);

  /**
   * This method saves the information of the given ipElement
   * 
   * @param ipElement - is the ipElement object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ipElement was
   *         updated
   *         or -1 is some error occurred.
   */
  public IpElement saveIpElement(IpElement ipElement);


}
