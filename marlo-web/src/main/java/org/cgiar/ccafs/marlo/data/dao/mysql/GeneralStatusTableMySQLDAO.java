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

import org.cgiar.ccafs.marlo.data.dao.GeneralStatusTableDAO;
import org.cgiar.ccafs.marlo.data.model.GeneralStatusTable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class GeneralStatusTableMySQLDAO extends AbstractMarloDAO<GeneralStatusTable, Long> implements GeneralStatusTableDAO {


  @Inject
  public GeneralStatusTableMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteGeneralStatusTable(long generalStatusTableId) {
    GeneralStatusTable generalStatusTable = this.find(generalStatusTableId);
    generalStatusTable.setActive(false);
    this.update(generalStatusTable);
  }

  @Override
  public boolean existGeneralStatusTable(long generalStatusTableID) {
    GeneralStatusTable generalStatusTable = this.find(generalStatusTableID);
    if (generalStatusTable == null) {
      return false;
    }
    return true;

  }

  @Override
  public GeneralStatusTable find(long id) {
    return super.find(GeneralStatusTable.class, id);

  }

  @Override
  public List<GeneralStatusTable> findAll() {
    String query = "from " + GeneralStatusTable.class.getName() + " where is_active=1";
    List<GeneralStatusTable> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public GeneralStatusTable save(GeneralStatusTable generalStatusTable) {
    if (generalStatusTable.getId() == null) {
      super.saveEntity(generalStatusTable);
    } else {
      generalStatusTable = super.update(generalStatusTable);
    }


    return generalStatusTable;
  }


}