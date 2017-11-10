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

import org.cgiar.ccafs.marlo.data.dao.DeliverableProgramDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class DeliverableProgramMySQLDAO extends AbstractMarloDAO<DeliverableProgram, Long> implements DeliverableProgramDAO {


  @Inject
  public DeliverableProgramMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableProgram(long deliverableProgramId) {
    DeliverableProgram deliverableProgram = this.find(deliverableProgramId);

    super.delete(deliverableProgram);
  }

  @Override
  public boolean existDeliverableProgram(long deliverableProgramID) {
    DeliverableProgram deliverableProgram = this.find(deliverableProgramID);
    if (deliverableProgram == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableProgram find(long id) {
    return super.find(DeliverableProgram.class, id);

  }

  @Override
  public List<DeliverableProgram> findAll() {
    String query = "from " + DeliverableProgram.class.getName() + " where is_active=1";
    List<DeliverableProgram> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableProgram save(DeliverableProgram deliverableProgram) {
    if (deliverableProgram.getId() == null) {
      super.saveEntity(deliverableProgram);
    } else {
      deliverableProgram = super.update(deliverableProgram);
    }


    return deliverableProgram;
  }


}