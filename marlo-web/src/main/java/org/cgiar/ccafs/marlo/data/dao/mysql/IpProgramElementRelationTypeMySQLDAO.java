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

import org.cgiar.ccafs.marlo.data.dao.IpProgramElementRelationTypeDAO;
import org.cgiar.ccafs.marlo.data.model.IpProgramElementRelationType;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class IpProgramElementRelationTypeMySQLDAO extends AbstractMarloDAO<IpProgramElementRelationType, Long> implements IpProgramElementRelationTypeDAO {


  @Inject
  public IpProgramElementRelationTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteIpProgramElementRelationType(long ipProgramElementRelationTypeId) {
    IpProgramElementRelationType ipProgramElementRelationType = this.find(ipProgramElementRelationTypeId);
    super.delete(ipProgramElementRelationType);
  }

  @Override
  public boolean existIpProgramElementRelationType(long ipProgramElementRelationTypeID) {
    IpProgramElementRelationType ipProgramElementRelationType = this.find(ipProgramElementRelationTypeID);
    if (ipProgramElementRelationType == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpProgramElementRelationType find(long id) {
    return super.find(IpProgramElementRelationType.class, id);

  }

  @Override
  public List<IpProgramElementRelationType> findAll() {
    String query = "from " + IpProgramElementRelationType.class.getName() + " where is_active=1";
    List<IpProgramElementRelationType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public IpProgramElementRelationType save(IpProgramElementRelationType ipProgramElementRelationType) {
    if (ipProgramElementRelationType.getId() == null) {
      super.saveEntity(ipProgramElementRelationType);
    } else {
      ipProgramElementRelationType = super.update(ipProgramElementRelationType);
    }


    return ipProgramElementRelationType;
  }


}