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


import org.cgiar.ccafs.marlo.data.dao.DeliverableProgramDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableProgramManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableProgramManagerImpl implements DeliverableProgramManager {


  private DeliverableProgramDAO deliverableProgramDAO;
  // Managers


  @Inject
  public DeliverableProgramManagerImpl(DeliverableProgramDAO deliverableProgramDAO) {
    this.deliverableProgramDAO = deliverableProgramDAO;


  }

  @Override
  public void deleteDeliverableProgram(long deliverableProgramId) {

    deliverableProgramDAO.deleteDeliverableProgram(deliverableProgramId);
  }

  @Override
  public boolean existDeliverableProgram(long deliverableProgramID) {

    return deliverableProgramDAO.existDeliverableProgram(deliverableProgramID);
  }

  @Override
  public List<DeliverableProgram> findAll() {

    return deliverableProgramDAO.findAll();

  }

  @Override
  public DeliverableProgram getDeliverableProgramById(long deliverableProgramID) {

    return deliverableProgramDAO.find(deliverableProgramID);
  }

  @Override
  public DeliverableProgram saveDeliverableProgram(DeliverableProgram deliverableProgram) {

    return deliverableProgramDAO.save(deliverableProgram);
  }


}
