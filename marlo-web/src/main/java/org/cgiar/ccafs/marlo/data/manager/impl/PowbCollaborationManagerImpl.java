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


import org.cgiar.ccafs.marlo.data.dao.PowbCollaborationDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbCollaborationManager;
import org.cgiar.ccafs.marlo.data.model.PowbCollaboration;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbCollaborationManagerImpl implements PowbCollaborationManager {


  private PowbCollaborationDAO powbCollaborationDAO;
  // Managers


  @Inject
  public PowbCollaborationManagerImpl(PowbCollaborationDAO powbCollaborationDAO) {
    this.powbCollaborationDAO = powbCollaborationDAO;


  }

  @Override
  public void deletePowbCollaboration(long powbCollaborationId) {

    powbCollaborationDAO.deletePowbCollaboration(powbCollaborationId);
  }

  @Override
  public boolean existPowbCollaboration(long powbCollaborationID) {

    return powbCollaborationDAO.existPowbCollaboration(powbCollaborationID);
  }

  @Override
  public List<PowbCollaboration> findAll() {

    return powbCollaborationDAO.findAll();

  }

  @Override
  public PowbCollaboration getPowbCollaborationById(long powbCollaborationID) {

    return powbCollaborationDAO.find(powbCollaborationID);
  }

  @Override
  public PowbCollaboration savePowbCollaboration(PowbCollaboration powbCollaboration) {

    return powbCollaborationDAO.save(powbCollaboration);
  }


}
