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

import org.cgiar.ccafs.marlo.data.model.MetadataElement;

import java.util.List;


public interface MetadataElementDAO {

  /**
   * This method removes a specific metadataElement value from the database.
   * 
   * @param metadataElementId is the metadataElement identifier.
   * @return true if the metadataElement was successfully deleted, false otherwise.
   */
  public void deleteMetadataElement(long metadataElementId);

  /**
   * This method validate if the metadataElement identify with the given id exists in the system.
   * 
   * @param metadataElementID is a metadataElement identifier.
   * @return true if the metadataElement exists, false otherwise.
   */
  public boolean existMetadataElement(long metadataElementID);

  /**
   * This method gets a metadataElement object by a given metadataElement identifier.
   * 
   * @param metadataElementID is the metadataElement identifier.
   * @return a MetadataElement object.
   */
  public MetadataElement find(long id);

  /**
   * This method gets a list of metadataElement that are active
   * 
   * @return a list from MetadataElement null if no exist records
   */
  public List<MetadataElement> findAll();


  /**
   * This method saves the information of the given metadataElement
   * 
   * @param metadataElement - is the metadataElement object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the metadataElement was
   *         updated
   *         or -1 is some error occurred.
   */
  public MetadataElement save(MetadataElement metadataElement);
}
