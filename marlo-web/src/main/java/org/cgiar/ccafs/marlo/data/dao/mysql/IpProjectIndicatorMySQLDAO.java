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

import org.cgiar.ccafs.marlo.data.dao.IpProjectIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class IpProjectIndicatorMySQLDAO extends AbstractMarloDAO implements IpProjectIndicatorDAO {


  @Inject
  public IpProjectIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteIpProjectIndicator(long ipProjectIndicatorId) {
    IpProjectIndicator ipProjectIndicator = this.find(ipProjectIndicatorId);
    ipProjectIndicator.setActive(false);
    return this.save(ipProjectIndicator) > 0;
  }

  @Override
  public boolean existIpProjectIndicator(long ipProjectIndicatorID) {
    IpProjectIndicator ipProjectIndicator = this.find(ipProjectIndicatorID);
    if (ipProjectIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpProjectIndicator find(long id) {
    return super.find(IpProjectIndicator.class, id);

  }

  @Override
  public List<IpProjectIndicator> findAll() {
    String query = "from " + IpProjectIndicator.class.getName() + " where is_active=1";
    List<IpProjectIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(IpProjectIndicator ipProjectIndicator) {
    if (ipProjectIndicator.getId() == null) {
      super.save(ipProjectIndicator);
    } else {
      super.update(ipProjectIndicator);
    }


    return ipProjectIndicator.getId();
  }


}