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

import org.cgiar.ccafs.marlo.data.dao.PowbExpectedCrpProgressDAO;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class PowbExpectedCrpProgressMySQLDAO extends AbstractMarloDAO<PowbExpectedCrpProgress, Long>
  implements PowbExpectedCrpProgressDAO {


  @Inject
  public PowbExpectedCrpProgressMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbExpectedCrpProgress(long powbExpectedCrpProgressId) {
    PowbExpectedCrpProgress powbExpectedCrpProgress = this.find(powbExpectedCrpProgressId);
    powbExpectedCrpProgress.setActive(false);
    this.save(powbExpectedCrpProgress);
  }

  @Override
  public boolean existPowbExpectedCrpProgress(long powbExpectedCrpProgressID) {
    PowbExpectedCrpProgress powbExpectedCrpProgress = this.find(powbExpectedCrpProgressID);
    if (powbExpectedCrpProgress == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbExpectedCrpProgress find(long id) {
    return super.find(PowbExpectedCrpProgress.class, id);

  }

  @Override
  public List<PowbExpectedCrpProgress> findAll() {
    String query = "from " + PowbExpectedCrpProgress.class.getName() + " where is_active=1";
    List<PowbExpectedCrpProgress> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<PowbExpectedCrpProgress> findByProgram(long crpProgramID) {
    String query = "select distinct pp from PowbExpectedCrpProgress as pp inner join pp.powbSynthesis as powbSynthesis "
      + " inner join powbSynthesis.liaisonInstitution liai" + " where liai.crpProgram.id = :crpProgramID ";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("crpProgramID", crpProgramID);
    List<PowbExpectedCrpProgress> powbExpectedCrpProgresses = createQuery.list();
    return powbExpectedCrpProgresses;
  }

  @Override
  public PowbExpectedCrpProgress save(PowbExpectedCrpProgress powbExpectedCrpProgress) {
    if (powbExpectedCrpProgress.getId() == null) {
      super.saveEntity(powbExpectedCrpProgress);
    } else {
      powbExpectedCrpProgress = super.update(powbExpectedCrpProgress);
    }


    return powbExpectedCrpProgress;
  }


}