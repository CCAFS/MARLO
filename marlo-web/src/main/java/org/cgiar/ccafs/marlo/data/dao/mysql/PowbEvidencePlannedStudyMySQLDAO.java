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

import org.cgiar.ccafs.marlo.data.dao.PowbEvidencePlannedStudyDAO;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbEvidencePlannedStudyMySQLDAO extends AbstractMarloDAO<PowbEvidencePlannedStudy, Long> implements PowbEvidencePlannedStudyDAO {


  @Inject
  public PowbEvidencePlannedStudyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbEvidencePlannedStudy(long powbEvidencePlannedStudyId) {
    PowbEvidencePlannedStudy powbEvidencePlannedStudy = this.find(powbEvidencePlannedStudyId);
    powbEvidencePlannedStudy.setActive(false);
    this.save(powbEvidencePlannedStudy);
  }

  @Override
  public boolean existPowbEvidencePlannedStudy(long powbEvidencePlannedStudyID) {
    PowbEvidencePlannedStudy powbEvidencePlannedStudy = this.find(powbEvidencePlannedStudyID);
    if (powbEvidencePlannedStudy == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbEvidencePlannedStudy find(long id) {
    return super.find(PowbEvidencePlannedStudy.class, id);

  }

  @Override
  public List<PowbEvidencePlannedStudy> findAll() {
    String query = "from " + PowbEvidencePlannedStudy.class.getName() + " where is_active=1";
    List<PowbEvidencePlannedStudy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbEvidencePlannedStudy save(PowbEvidencePlannedStudy powbEvidencePlannedStudy) {
    if (powbEvidencePlannedStudy.getId() == null) {
      super.saveEntity(powbEvidencePlannedStudy);
    } else {
      powbEvidencePlannedStudy = super.update(powbEvidencePlannedStudy);
    }


    return powbEvidencePlannedStudy;
  }


}