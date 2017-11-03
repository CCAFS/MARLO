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
import org.hibernate.SessionFactory;

public class IpProgramMySQLDAO extends AbstractMarloDAO<IpProgram, Long> implements IpProgramDAO {


  @Inject
  public IpProgramMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteIpProgram(long ipProgramId) {
    IpProgram ipProgram = this.find(ipProgramId);
    super.delete(ipProgram);
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
    return super.find(IpProgram.class, id);

  }

  @Override
  public List<IpProgram> findAll() {
    String query = "from " + IpProgram.class.getName() + " ";
    List<IpProgram> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public IpProgram save(IpProgram ipProgram) {
    if (ipProgram.getId() == null) {
      super.saveEntity(ipProgram);
    } else {
      ipProgram = super.update(ipProgram);
    }


    return ipProgram;
  }

  @Override
  public IpProgram save(IpProgram ipProgram, String section, List<String> relationsName) {
    if (ipProgram.getId() == null) {
      super.saveEntity(ipProgram, section, relationsName);
    } else {
      ipProgram = super.update(ipProgram, section, relationsName);
    }
    return ipProgram;
  }


}