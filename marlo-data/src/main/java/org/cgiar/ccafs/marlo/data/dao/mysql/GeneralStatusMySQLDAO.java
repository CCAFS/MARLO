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

import org.cgiar.ccafs.marlo.data.dao.GeneralStatusDAO;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class GeneralStatusMySQLDAO extends AbstractMarloDAO<GeneralStatus, Long> implements GeneralStatusDAO {


  @Inject
  public GeneralStatusMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteGeneralStatus(long generalStatusId) {
    GeneralStatus generalStatus = this.find(generalStatusId);
    generalStatus.setActive(false);
    this.update(generalStatus);
  }

  @Override
  public boolean existGeneralStatus(long generalStatusID) {
    GeneralStatus generalStatus = this.find(generalStatusID);
    if (generalStatus == null) {
      return false;
    }
    return true;

  }

  @Override
  public GeneralStatus find(long id) {
    return super.find(GeneralStatus.class, id);

  }

  @Override
  public List<GeneralStatus> findAll() {
    String query = "from " + GeneralStatus.class.getName() + " where is_active=1";
    List<GeneralStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public GeneralStatus save(GeneralStatus generalStatus) {
    if (generalStatus.getId() == null) {
      super.saveEntity(generalStatus);
    } else {
      generalStatus = super.update(generalStatus);
    }


    return generalStatus;
  }


}