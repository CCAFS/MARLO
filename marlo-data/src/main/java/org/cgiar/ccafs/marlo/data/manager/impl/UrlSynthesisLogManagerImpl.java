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


import org.cgiar.ccafs.marlo.data.dao.UrlSynthesisLogDAO;
import org.cgiar.ccafs.marlo.data.manager.UrlSynthesisLogManager;
import org.cgiar.ccafs.marlo.data.model.UrlSynthesisLog;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class UrlSynthesisLogManagerImpl implements UrlSynthesisLogManager {


  private UrlSynthesisLogDAO urlSynthesisLogDAO;
  // Managers


  @Inject
  public UrlSynthesisLogManagerImpl(UrlSynthesisLogDAO urlSynthesisLogDAO) {
    this.urlSynthesisLogDAO = urlSynthesisLogDAO;


  }

  @Override
  public void deleteUrlSynthesisLog(long urlSynthesisLogId) {

    urlSynthesisLogDAO.deleteUrlSynthesisLog(urlSynthesisLogId);
  }

  @Override
  public boolean existUrlSynthesisLog(long urlSynthesisLogID) {

    return urlSynthesisLogDAO.existUrlSynthesisLog(urlSynthesisLogID);
  }

  @Override
  public List<UrlSynthesisLog> findAll() {

    return urlSynthesisLogDAO.findAll();

  }

  @Override
  public UrlSynthesisLog getUrlSynthesisLogById(long urlSynthesisLogID) {

    return urlSynthesisLogDAO.find(urlSynthesisLogID);
  }

  @Override
  public UrlSynthesisLog saveUrlSynthesisLog(UrlSynthesisLog urlSynthesisLog) {

    return urlSynthesisLogDAO.save(urlSynthesisLog);
  }


}
