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

import org.cgiar.ccafs.marlo.data.manager.impl.IpProgramElementManagerImpl;
import org.cgiar.ccafs.marlo.data.model.IpProgramElement;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(IpProgramElementManagerImpl.class)
public interface IpProgramElementManager {


  /**
   * This method removes a specific ipProgramElement value from the database.
   * 
   * @param ipProgramElementId is the ipProgramElement identifier.
   * @return true if the ipProgramElement was successfully deleted, false otherwise.
   */
  public void deleteIpProgramElement(long ipProgramElementId);


  /**
   * This method validate if the ipProgramElement identify with the given id exists in the system.
   * 
   * @param ipProgramElementID is a ipProgramElement identifier.
   * @return true if the ipProgramElement exists, false otherwise.
   */
  public boolean existIpProgramElement(long ipProgramElementID);


  /**
   * This method gets a list of ipProgramElement that are active
   * 
   * @return a list from IpProgramElement null if no exist records
   */
  public List<IpProgramElement> findAll();


  /**
   * This method gets a ipProgramElement object by a given ipProgramElement identifier.
   * 
   * @param ipProgramElementID is the ipProgramElement identifier.
   * @return a IpProgramElement object.
   */
  public IpProgramElement getIpProgramElementById(long ipProgramElementID);

  /**
   * This method saves the information of the given ipProgramElement
   * 
   * @param ipProgramElement - is the ipProgramElement object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ipProgramElement was
   *         updated
   *         or -1 is some error occurred.
   */
  public IpProgramElement saveIpProgramElement(IpProgramElement ipProgramElement);


}
