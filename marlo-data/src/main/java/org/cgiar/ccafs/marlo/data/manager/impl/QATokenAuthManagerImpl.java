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


import org.cgiar.ccafs.marlo.data.dao.QATokenAuthDAO;
import org.cgiar.ccafs.marlo.data.manager.QATokenAuthManager;
import org.cgiar.ccafs.marlo.data.model.QATokenAuth;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class QATokenAuthManagerImpl implements QATokenAuthManager {


  private QATokenAuthDAO qATokenAuthDAO;
  // Managers


  @Inject
  public QATokenAuthManagerImpl(QATokenAuthDAO qATokenAuthDAO) {
    this.qATokenAuthDAO = qATokenAuthDAO;


  }

  @Override
  public void deleteQATokenAuth(long qATokenAuthId) {

    qATokenAuthDAO.deleteQATokenAuth(qATokenAuthId);
  }

  @Override
  public boolean existQATokenAuth(long qATokenAuthID) {

    return qATokenAuthDAO.existQATokenAuth(qATokenAuthID);
  }

  @Override
  public List<QATokenAuth> findAll() {

    return qATokenAuthDAO.findAll();

  }

  @Override
  public QATokenAuth generateQATokenAuth(String name, String username, String email, String smoCode, String userId) {

    return qATokenAuthDAO.generate(name, username, email, smoCode, userId);
  }

  @Override
  public QATokenAuth getQATokenAuthById(long qATokenAuthID) {

    return qATokenAuthDAO.find(qATokenAuthID);
  }

  @Override
  public QATokenAuth saveQATokenAuth(QATokenAuth qATokenAuth) {

    return qATokenAuthDAO.save(qATokenAuth);
  }


}
