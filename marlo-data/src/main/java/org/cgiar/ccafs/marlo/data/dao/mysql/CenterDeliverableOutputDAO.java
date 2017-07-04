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

import org.cgiar.ccafs.marlo.data.dao.ICenterDeliverableOutputDAO;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableOutput;

import java.util.List;

import com.google.inject.Inject;

public class CenterDeliverableOutputDAO implements ICenterDeliverableOutputDAO {

  private StandardDAO dao;

  @Inject
  public CenterDeliverableOutputDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverableOutput(long deliverableOutputId) {
    CenterDeliverableOutput deliverableOutput = this.find(deliverableOutputId);
    deliverableOutput.setActive(false);
    return this.save(deliverableOutput) > 0;
  }

  @Override
  public boolean existDeliverableOutput(long deliverableOutputID) {
    CenterDeliverableOutput deliverableOutput = this.find(deliverableOutputID);
    if (deliverableOutput == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterDeliverableOutput find(long id) {
    return dao.find(CenterDeliverableOutput.class, id);

  }

  @Override
  public List<CenterDeliverableOutput> findAll() {
    String query = "from " + CenterDeliverableOutput.class.getName();
    List<CenterDeliverableOutput> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterDeliverableOutput> getDeliverableOutputsByUserId(long userId) {
    String query = "from " + CenterDeliverableOutput.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterDeliverableOutput deliverableOutput) {
    if (deliverableOutput.getId() == null) {
      dao.save(deliverableOutput);
    } else {
      dao.update(deliverableOutput);
    }
    return deliverableOutput.getId();
  }

  @Override
  public long save(CenterDeliverableOutput deliverableOutput, String actionName, List<String> relationsName) {
    if (deliverableOutput.getId() == null) {
      dao.save(deliverableOutput, actionName, relationsName);
    } else {
      dao.update(deliverableOutput, actionName, relationsName);
    }
    return deliverableOutput.getId();
  }


}