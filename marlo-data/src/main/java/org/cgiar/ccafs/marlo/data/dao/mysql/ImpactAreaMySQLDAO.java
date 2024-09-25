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

import org.cgiar.ccafs.marlo.data.dao.ImpactAreaDAO;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ImpactAreaMySQLDAO extends AbstractMarloDAO<ImpactArea, Long> implements ImpactAreaDAO {


  @Inject
  public ImpactAreaMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteImpactArea(long impactAreaId) {
    ImpactArea impactArea = this.find(impactAreaId);
    impactArea.setActive(false);
    this.update(impactArea);
  }

  @Override
  public boolean existImpactArea(long impactAreaID) {
    ImpactArea impactArea = this.find(impactAreaID);
    if (impactArea == null) {
      return false;
    }
    return true;

  }

  @Override
  public ImpactArea find(long id) {
    return super.find(ImpactArea.class, id);

  }

  @Override
  public List<ImpactArea> findAll() {
    String query = "from " + ImpactArea.class.getName() + " where is_active=1";
    List<ImpactArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ImpactArea> findAllCustom() {
    String query = "from " + ImpactArea.class.getName();
    List<ImpactArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public ImpactArea save(ImpactArea impactArea) {
    if (impactArea.getId() == null) {
      super.saveEntity(impactArea);
    } else {
      impactArea = super.update(impactArea);
    }


    return impactArea;
  }


}