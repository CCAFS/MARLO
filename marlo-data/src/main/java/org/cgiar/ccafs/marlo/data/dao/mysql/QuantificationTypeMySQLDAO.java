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

import org.cgiar.ccafs.marlo.data.dao.QuantificationTypeDAO;
import org.cgiar.ccafs.marlo.data.model.QuantificationType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class QuantificationTypeMySQLDAO extends AbstractMarloDAO<QuantificationType, Long>
  implements QuantificationTypeDAO {


  @Inject
  public QuantificationTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteQuantificationType(long quantificationTypeId) {
    QuantificationType quantificationType = this.find(quantificationTypeId);
    this.delete(quantificationType);
  }

  @Override
  public boolean existQuantificationType(long quantificationTypeID) {
    QuantificationType quantificationType = this.find(quantificationTypeID);
    if (quantificationType == null) {
      return false;
    }
    return true;

  }

  @Override
  public QuantificationType find(long id) {
    return super.find(QuantificationType.class, id);

  }

  @Override
  public List<QuantificationType> findAll() {
    String query = "from " + QuantificationType.class.getName();
    List<QuantificationType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public QuantificationType save(QuantificationType quantificationType) {
    if (quantificationType.getId() == null) {
      super.saveEntity(quantificationType);
    } else {
      quantificationType = super.update(quantificationType);
    }


    return quantificationType;
  }


}