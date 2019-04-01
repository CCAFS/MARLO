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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.MarloMessageDAO;
import org.cgiar.ccafs.marlo.data.manager.MarloMessageManager;
import org.cgiar.ccafs.marlo.data.model.MarloMessage;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class MarloMessageManagerImpl implements MarloMessageManager {


  private MarloMessageDAO marloMessageDAO;
  // Managers


  @Inject
  public MarloMessageManagerImpl(MarloMessageDAO marloMessageDAO) {
    this.marloMessageDAO = marloMessageDAO;


  }

  @Override
  public void deleteMarloMessage(long marloMessageId) {

    marloMessageDAO.deleteMarloMessage(marloMessageId);
  }

  @Override
  public boolean existMarloMessage(long marloMessageID) {

    return marloMessageDAO.existMarloMessage(marloMessageID);
  }

  @Override
  public List<MarloMessage> findAll() {

    return marloMessageDAO.findAll();

  }

  @Override
  public MarloMessage getMarloMessageById(long marloMessageID) {

    return marloMessageDAO.find(marloMessageID);
  }

  @Override
  public MarloMessage saveMarloMessage(MarloMessage marloMessage) {

    return marloMessageDAO.save(marloMessage);
  }


}
