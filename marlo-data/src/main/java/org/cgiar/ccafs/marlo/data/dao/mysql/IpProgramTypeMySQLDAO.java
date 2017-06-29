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

import org.cgiar.ccafs.marlo.data.dao.IpProgramTypeDAO;
import org.cgiar.ccafs.marlo.data.model.IpProgramType;

import java.util.List;

import com.google.inject.Inject;

public class IpProgramTypeMySQLDAO implements IpProgramTypeDAO {

  private StandardDAO dao;

  @Inject
  public IpProgramTypeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteIpProgramType(long ipProgramTypeId) {
    IpProgramType ipProgramType = this.find(ipProgramTypeId);
    return dao.delete(ipProgramType);
  }

  @Override
  public boolean existIpProgramType(long ipProgramTypeID) {
    IpProgramType ipProgramType = this.find(ipProgramTypeID);
    if (ipProgramType == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpProgramType find(long id) {
    return dao.find(IpProgramType.class, id);

  }

  @Override
  public List<IpProgramType> findAll() {
    String query = "from " + IpProgramType.class.getName() + " where is_active=1";
    List<IpProgramType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(IpProgramType ipProgramType) {
    if (ipProgramType.getId() == null) {
      dao.save(ipProgramType);
    } else {
      dao.update(ipProgramType);
    }


    return ipProgramType.getId();
  }


}