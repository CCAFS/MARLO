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


import org.cgiar.ccafs.marlo.data.dao.NexusDAO;
import org.cgiar.ccafs.marlo.data.manager.NexusManager;
import org.cgiar.ccafs.marlo.data.model.Nexus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class NexusManagerImpl implements NexusManager {


  private NexusDAO nexusDAO;
  // Managers


  @Inject
  public NexusManagerImpl(NexusDAO nexusDAO) {
    this.nexusDAO = nexusDAO;


  }

  @Override
  public void deleteNexus(long nexusId) {

    nexusDAO.deleteNexus(nexusId);
  }

  @Override
  public boolean existNexus(long nexusID) {

    return nexusDAO.existNexus(nexusID);
  }

  @Override
  public List<Nexus> findAll() {

    return nexusDAO.findAll();

  }

  @Override
  public Nexus getNexusById(long nexusID) {

    return nexusDAO.find(nexusID);
  }

  @Override
  public Nexus saveNexus(Nexus nexus) {

    return nexusDAO.save(nexus);
  }


}
