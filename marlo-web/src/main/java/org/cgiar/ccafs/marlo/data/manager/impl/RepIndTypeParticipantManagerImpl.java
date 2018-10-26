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


import org.cgiar.ccafs.marlo.data.dao.RepIndTypeParticipantDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndTypeParticipantManager;
import org.cgiar.ccafs.marlo.data.model.RepIndTypeParticipant;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndTypeParticipantManagerImpl implements RepIndTypeParticipantManager {


  private RepIndTypeParticipantDAO repIndTypeParticipantDAO;
  // Managers


  @Inject
  public RepIndTypeParticipantManagerImpl(RepIndTypeParticipantDAO repIndTypeParticipantDAO) {
    this.repIndTypeParticipantDAO = repIndTypeParticipantDAO;


  }

  @Override
  public void deleteRepIndTypeParticipant(long repIndTypeParticipantId) {

    repIndTypeParticipantDAO.deleteRepIndTypeParticipant(repIndTypeParticipantId);
  }

  @Override
  public boolean existRepIndTypeParticipant(long repIndTypeParticipantID) {

    return repIndTypeParticipantDAO.existRepIndTypeParticipant(repIndTypeParticipantID);
  }

  @Override
  public List<RepIndTypeParticipant> findAll() {

    return repIndTypeParticipantDAO.findAll();

  }

  @Override
  public RepIndTypeParticipant getRepIndTypeParticipantById(long repIndTypeParticipantID) {

    return repIndTypeParticipantDAO.find(repIndTypeParticipantID);
  }

  @Override
  public RepIndTypeParticipant saveRepIndTypeParticipant(RepIndTypeParticipant repIndTypeParticipant) {

    return repIndTypeParticipantDAO.save(repIndTypeParticipant);
  }


}
