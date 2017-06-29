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


import org.cgiar.ccafs.marlo.data.dao.IpProgramDAO;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.model.IpProgram;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class IpProgramManagerImpl implements IpProgramManager {


  private IpProgramDAO ipProgramDAO;
  // Managers


  @Inject
  public IpProgramManagerImpl(IpProgramDAO ipProgramDAO) {
    this.ipProgramDAO = ipProgramDAO;


  }

  @Override
  public boolean deleteIpProgram(long ipProgramId) {

    return ipProgramDAO.deleteIpProgram(ipProgramId);
  }

  @Override
  public boolean existIpProgram(long ipProgramID) {

    return ipProgramDAO.existIpProgram(ipProgramID);
  }

  @Override
  public List<IpProgram> findAll() {

    return ipProgramDAO.findAll();

  }

  @Override
  public IpProgram getIpProgramById(long ipProgramID) {

    return ipProgramDAO.find(ipProgramID);
  }

  @Override
  public long save(IpProgram ipProgram, String section, List<String> relationsName) {
    return ipProgramDAO.save(ipProgram, section, relationsName);
  }

  @Override
  public long saveIpProgram(IpProgram ipProgram) {

    return ipProgramDAO.save(ipProgram);
  }


}
