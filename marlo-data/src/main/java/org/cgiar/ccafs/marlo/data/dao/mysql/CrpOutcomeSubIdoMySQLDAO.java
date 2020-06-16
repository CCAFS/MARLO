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

import org.cgiar.ccafs.marlo.data.dao.CrpOutcomeSubIdoDAO;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class CrpOutcomeSubIdoMySQLDAO extends AbstractMarloDAO<CrpOutcomeSubIdo, Long> implements CrpOutcomeSubIdoDAO {

  @Inject
  public CrpOutcomeSubIdoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpOutcomeSubIdo(long crpOutcomeSubIdoId) {
    CrpOutcomeSubIdo crpOutcomeSubIdo = this.find(crpOutcomeSubIdoId);
    crpOutcomeSubIdo.setActive(false);
    this.save(crpOutcomeSubIdo);
  }

  @Override
  public boolean existCrpOutcomeSubIdo(long crpOutcomeSubIdoID) {
    CrpOutcomeSubIdo crpOutcomeSubIdo = this.find(crpOutcomeSubIdoID);
    if (crpOutcomeSubIdo == null) {
      return false;
    }

    return true;
  }

  @Override
  public CrpOutcomeSubIdo find(long id) {
    return super.find(CrpOutcomeSubIdo.class, id);
  }

  @Override
  public List<CrpOutcomeSubIdo> findAll() {
    String query = "from " + CrpOutcomeSubIdo.class.getName() + " where is_active=1";
    List<CrpOutcomeSubIdo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }

    return null;
  }

  @Override
  public CrpOutcomeSubIdo getCrpOutcomeSubIdoByOutcomeComposedIdPhaseAndSubIdo(String outcomeComposedId, long phaseId,
    long subIdoId) {
    String query = "select distinct cosi from CrpOutcomeSubIdo cosi " + "join cosi.crpProgramOutcome cpo "
      + "with cpo.composeID = :composeID and cpo.active=true " + "and cpo.phase.id = :phaseID "
      + " where cosi.srfSubIdo.id = :subIdoID";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("composeID", outcomeComposedId);
    createQuery.setParameter("phaseID", phaseId);
    createQuery.setParameter("subIdoID", subIdoId);

    CrpOutcomeSubIdo result = super.findSingleResult(CrpOutcomeSubIdo.class, createQuery);
    return result;
  }

  @Override
  public CrpOutcomeSubIdo save(CrpOutcomeSubIdo crpOutcomeSubIdo) {
    if (crpOutcomeSubIdo.getId() == null) {
      super.saveEntity(crpOutcomeSubIdo);
    } else {
      crpOutcomeSubIdo = super.update(crpOutcomeSubIdo);
    }

    return crpOutcomeSubIdo;
  }
}