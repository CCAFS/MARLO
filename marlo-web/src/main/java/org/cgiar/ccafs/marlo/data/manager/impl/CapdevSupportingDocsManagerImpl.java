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


import org.cgiar.ccafs.marlo.data.dao.CapdevSupportingDocsDAO;
import org.cgiar.ccafs.marlo.data.manager.CapdevSupportingDocsManager;
import org.cgiar.ccafs.marlo.data.model.CapdevSupportingDocs;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CapdevSupportingDocsManagerImpl implements CapdevSupportingDocsManager {


  private CapdevSupportingDocsDAO capdevSupportingDocsDAO;
  // Managers


  @Inject
  public CapdevSupportingDocsManagerImpl(CapdevSupportingDocsDAO capdevSupportingDocsDAO) {
    this.capdevSupportingDocsDAO = capdevSupportingDocsDAO;


  }

  @Override
  public boolean deleteCapdevSupportingDocs(long capdevSupportingDocsId) {

    return capdevSupportingDocsDAO.deleteCapdevSupportingDocs(capdevSupportingDocsId);
  }

  @Override
  public boolean existCapdevSupportingDocs(long capdevSupportingDocsID) {

    return capdevSupportingDocsDAO.existCapdevSupportingDocs(capdevSupportingDocsID);
  }

  @Override
  public List<CapdevSupportingDocs> findAll() {

    return capdevSupportingDocsDAO.findAll();

  }

  @Override
  public CapdevSupportingDocs getCapdevSupportingDocsById(long capdevSupportingDocsID) {

    return capdevSupportingDocsDAO.find(capdevSupportingDocsID);
  }

  @Override
  public long saveCapdevSupportingDocs(CapdevSupportingDocs capdevSupportingDocs) {

    return capdevSupportingDocsDAO.save(capdevSupportingDocs);
  }


}
