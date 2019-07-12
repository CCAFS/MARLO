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

import org.cgiar.ccafs.marlo.data.dao.MarloMessageDAO;
import org.cgiar.ccafs.marlo.data.model.MarloMessage;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class MarloMessageMySQLDAO extends AbstractMarloDAO<MarloMessage, Long> implements MarloMessageDAO {


  @Inject
  public MarloMessageMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteMarloMessage(long marloMessageId) {
    MarloMessage marloMessage = this.find(marloMessageId);
    marloMessage.setActive(false);
    this.update(marloMessage);
  }

  @Override
  public boolean existMarloMessage(long marloMessageID) {
    MarloMessage marloMessage = this.find(marloMessageID);
    if (marloMessage == null) {
      return false;
    }
    return true;

  }

  @Override
  public MarloMessage find(long id) {
    return super.find(MarloMessage.class, id);

  }

  @Override
  public List<MarloMessage> findAll() {
    String query = "from " + MarloMessage.class.getName() + " where is_active=1";
    List<MarloMessage> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public MarloMessage save(MarloMessage marloMessage) {
    if (marloMessage.getId() == null) {
      super.saveEntity(marloMessage);
    } else {
      marloMessage = super.update(marloMessage);
    }


    return marloMessage;
  }


}