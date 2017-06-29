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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.IpProgramDAO;
import org.cgiar.ccafs.marlo.data.model.IpProgram;

import java.util.List;

import com.google.inject.Inject;

public class IpProgramMySQLDAO implements IpProgramDAO {

  private StandardDAO dao;

  @Inject
  public IpProgramMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteIpProgram(long ipProgramId) {
    IpProgram ipProgram = this.find(ipProgramId);
    return dao.delete(ipProgram);
  }

  @Override
  public boolean existIpProgram(long ipProgramID) {
    IpProgram ipProgram = this.find(ipProgramID);
    if (ipProgram == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpProgram find(long id) {
    return dao.find(IpProgram.class, id);

  }

  @Override
  public List<IpProgram> findAll() {
    String query = "from " + IpProgram.class.getName() + " ";
    List<IpProgram> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(IpProgram ipProgram) {
    if (ipProgram.getId() == null) {
      dao.save(ipProgram);
    } else {
      dao.update(ipProgram);
    }


    return ipProgram.getId();
  }

  @Override
  public long save(IpProgram ipProgram, String section, List<String> relationsName) {
    if (ipProgram.getId() == null) {
      dao.save(ipProgram, section, relationsName);
    } else {
      dao.update(ipProgram, section, relationsName);
    }
    return ipProgram.getId();
  }


}