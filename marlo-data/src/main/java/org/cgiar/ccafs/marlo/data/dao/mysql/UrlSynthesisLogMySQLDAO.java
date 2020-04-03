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

import org.cgiar.ccafs.marlo.data.dao.UrlSynthesisLogDAO;
import org.cgiar.ccafs.marlo.data.model.UrlSynthesisLog;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class UrlSynthesisLogMySQLDAO extends AbstractMarloDAO<UrlSynthesisLog, Long> implements UrlSynthesisLogDAO {


  @Inject
  public UrlSynthesisLogMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteUrlSynthesisLog(long urlSynthesisLogId) {
    UrlSynthesisLog urlSynthesisLog = this.find(urlSynthesisLogId);
    this.update(urlSynthesisLog);
  }

  @Override
  public boolean existUrlSynthesisLog(long urlSynthesisLogID) {
    UrlSynthesisLog urlSynthesisLog = this.find(urlSynthesisLogID);
    if (urlSynthesisLog == null) {
      return false;
    }
    return true;

  }

  @Override
  public UrlSynthesisLog find(long id) {
    return super.find(UrlSynthesisLog.class, id);

  }

  @Override
  public List<UrlSynthesisLog> findAll() {
    String query = "from " + UrlSynthesisLog.class.getName();
    List<UrlSynthesisLog> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public UrlSynthesisLog save(UrlSynthesisLog urlSynthesisLog) {
    if (urlSynthesisLog.getId() == null) {
      super.saveEntity(urlSynthesisLog);
    } else {
      urlSynthesisLog = super.update(urlSynthesisLog);
    }


    return urlSynthesisLog;
  }


}