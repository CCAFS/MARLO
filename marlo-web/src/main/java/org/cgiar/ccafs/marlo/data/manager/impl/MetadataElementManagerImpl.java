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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.MetadataElementDAO;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.model.MetadataElement;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class MetadataElementManagerImpl implements MetadataElementManager {


  private MetadataElementDAO metadataElementDAO;
  // Managers


  @Inject
  public MetadataElementManagerImpl(MetadataElementDAO metadataElementDAO) {
    this.metadataElementDAO = metadataElementDAO;


  }

  @Override
  public void deleteMetadataElement(long metadataElementId) {

    metadataElementDAO.deleteMetadataElement(metadataElementId);
  }

  @Override
  public boolean existMetadataElement(long metadataElementID) {

    return metadataElementDAO.existMetadataElement(metadataElementID);
  }

  @Override
  public List<MetadataElement> findAll() {

    return metadataElementDAO.findAll();

  }

  @Override
  public MetadataElement getMetadataElementById(long metadataElementID) {

    return metadataElementDAO.find(metadataElementID);
  }

  @Override
  public MetadataElement saveMetadataElement(MetadataElement metadataElement) {

    return metadataElementDAO.save(metadataElement);
  }


}
