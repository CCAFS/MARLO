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

import org.cgiar.ccafs.marlo.data.dao.IpElementTypeDAO;
import org.cgiar.ccafs.marlo.data.model.IpElementType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class IpElementTypeMySQLDAO extends AbstractMarloDAO<IpElementType, Long> implements IpElementTypeDAO {


  @Inject
  public IpElementTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteIpElementType(long ipElementTypeId) {
    IpElementType ipElementType = this.find(ipElementTypeId);
    super.delete(ipElementType);
  }

  @Override
  public boolean existIpElementType(long ipElementTypeID) {
    IpElementType ipElementType = this.find(ipElementTypeID);
    if (ipElementType == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpElementType find(long id) {
    return super.find(IpElementType.class, id);

  }

  @Override
  public List<IpElementType> findAll() {
    String query = "from " + IpElementType.class.getName() + " where is_active=1";
    List<IpElementType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public IpElementType save(IpElementType ipElementType) {
    if (ipElementType.getId() == null) {
      super.saveEntity(ipElementType);
    } else {
      ipElementType = super.update(ipElementType);
    }


    return ipElementType;
  }


}