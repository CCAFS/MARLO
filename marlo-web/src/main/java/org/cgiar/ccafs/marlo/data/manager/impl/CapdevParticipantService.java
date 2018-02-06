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


import org.cgiar.ccafs.marlo.data.dao.ICapdevParticipantDAO;
import org.cgiar.ccafs.marlo.data.manager.ICapdevParticipantService;
import org.cgiar.ccafs.marlo.data.model.CapdevParticipant;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author Christian Garcia
 */
@Named
public class CapdevParticipantService implements ICapdevParticipantService {


  private ICapdevParticipantDAO capdevParticipantDAO;

  // Managers


  @Inject
  public CapdevParticipantService(ICapdevParticipantDAO capdevParticipantDAO) {
    this.capdevParticipantDAO = capdevParticipantDAO;


  }

  @Override
  public void deleteCapdevParticipant(long capdevParticipantId) {

    capdevParticipantDAO.deleteCapdevParticipant(capdevParticipantId);
  }

  @Override
  public boolean existCapdevParticipant(long capdevParticipantID) {

    return capdevParticipantDAO.existCapdevParticipant(capdevParticipantID);
  }

  @Override
  public List<CapdevParticipant> findAll() {

    return capdevParticipantDAO.findAll();

  }

  @Override
  public CapdevParticipant getCapdevParticipantById(long capdevParticipantID) {

    return capdevParticipantDAO.find(capdevParticipantID);
  }

  @Override
  public List<CapdevParticipant> getCapdevParticipantsByUserId(Long userId) {
    return capdevParticipantDAO.getCapdevParticipantsByUserId(userId);
  }

  @Override
  public CapdevParticipant saveCapdevParticipant(CapdevParticipant capdevParticipant) {

    return capdevParticipantDAO.save(capdevParticipant);
  }

  @Override
  public CapdevParticipant saveCapdevParticipant(CapdevParticipant capdevParticipant, String actionName,
    List<String> relationsName) {
    return capdevParticipantDAO.save(capdevParticipant, actionName, relationsName);
  }


}
