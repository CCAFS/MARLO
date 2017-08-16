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

import org.cgiar.ccafs.marlo.data.dao.DeliverableQualityAnswerDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityAnswer;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class DeliverableQualityAnswerMySQLDAO extends AbstractMarloDAO<DeliverableQualityAnswer, Long> implements DeliverableQualityAnswerDAO {


  @Inject
  public DeliverableQualityAnswerMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteDeliverableQualityAnswer(long deliverableQualityAnswerId) {
    DeliverableQualityAnswer deliverableQualityAnswer = this.find(deliverableQualityAnswerId);
    deliverableQualityAnswer.setActive(false);
    return this.save(deliverableQualityAnswer) > 0;
  }

  @Override
  public boolean existDeliverableQualityAnswer(long deliverableQualityAnswerID) {
    DeliverableQualityAnswer deliverableQualityAnswer = this.find(deliverableQualityAnswerID);
    if (deliverableQualityAnswer == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableQualityAnswer find(long id) {
    return super.find(DeliverableQualityAnswer.class, id);

  }

  @Override
  public List<DeliverableQualityAnswer> findAll() {
    String query = "from " + DeliverableQualityAnswer.class.getName() + " where is_active=1";
    List<DeliverableQualityAnswer> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverableQualityAnswer deliverableQualityAnswer) {
    if (deliverableQualityAnswer.getId() == null) {
      super.saveEntity(deliverableQualityAnswer);
    } else {
      super.update(deliverableQualityAnswer);
    }


    return deliverableQualityAnswer.getId();
  }


}