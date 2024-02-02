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

import org.cgiar.ccafs.marlo.data.dao.SoilIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.SoilIndicator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class SoilIndicatorMySQLDAO extends AbstractMarloDAO<SoilIndicator, Long> implements SoilIndicatorDAO {


  @Inject
  public SoilIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSoilIndicator(long soilIndicatorId) {
    SoilIndicator soilIndicator = this.find(soilIndicatorId);
    this.delete(soilIndicator);
  }

  @Override
  public boolean existSoilIndicator(long soilIndicatorID) {
    SoilIndicator soilIndicator = this.find(soilIndicatorID);
    if (soilIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public SoilIndicator find(long id) {
    return super.find(SoilIndicator.class, id);

  }

  @Override
  public List<SoilIndicator> findAll() {
    String query = "from " + SoilIndicator.class.getName();
    List<SoilIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public SoilIndicator save(SoilIndicator soilIndicator) {
    if (soilIndicator.getId() == null) {
      super.saveEntity(soilIndicator);
    } else {
      soilIndicator = super.update(soilIndicator);
    }


    return soilIndicator;
  }


}