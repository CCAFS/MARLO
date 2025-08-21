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

import org.cgiar.ccafs.marlo.data.dao.PRMSInnovationDAO;
import org.cgiar.ccafs.marlo.data.model.PRMSInnovation;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PRMSInnovationMySQLDAO extends AbstractMarloDAO<PRMSInnovation, Long> implements PRMSInnovationDAO {


  @Inject
  public PRMSInnovationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePRMSInnovation(long pRMSInnovationId) {
    PRMSInnovation pRMSInnovation = this.find(pRMSInnovationId);
    this.delete(pRMSInnovation);
  }

  @Override
  public boolean existPRMSInnovation(long pRMSInnovationID) {
    PRMSInnovation pRMSInnovation = this.find(pRMSInnovationID);
    if (pRMSInnovation == null) {
      return false;
    }
    return true;

  }

  @Override
  public PRMSInnovation find(long id) {
    return super.find(PRMSInnovation.class, id);

  }

  @Override
  public List<PRMSInnovation> findAll() {
    String query = "from " + PRMSInnovation.class.getName();
    List<PRMSInnovation> list = super.findAll(query);
    if (list == null || list.isEmpty()) {
      return Collections.emptyList();
    }
    return list;
  }

  @Override
  public PRMSInnovation save(PRMSInnovation pRMSInnovation) {
    if (pRMSInnovation.getId() == null) {
      super.saveEntity(pRMSInnovation);
    } else {
      pRMSInnovation = super.update(pRMSInnovation);
    }


    return pRMSInnovation;
  }


}