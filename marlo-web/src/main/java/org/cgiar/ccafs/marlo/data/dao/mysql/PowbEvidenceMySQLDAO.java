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

import org.cgiar.ccafs.marlo.data.dao.PowbEvidenceDAO;
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbEvidenceMySQLDAO extends AbstractMarloDAO<PowbEvidence, Long> implements PowbEvidenceDAO {


  @Inject
  public PowbEvidenceMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbEvidence(long powbEvidenceId) {
    PowbEvidence powbEvidence = this.find(powbEvidenceId);
    powbEvidence.setActive(false);
    this.save(powbEvidence);
  }

  @Override
  public boolean existPowbEvidence(long powbEvidenceID) {
    PowbEvidence powbEvidence = this.find(powbEvidenceID);
    if (powbEvidence == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbEvidence find(long id) {
    return super.find(PowbEvidence.class, id);

  }

  @Override
  public List<PowbEvidence> findAll() {
    String query = "from " + PowbEvidence.class.getName() + " where is_active=1";
    List<PowbEvidence> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbEvidence save(PowbEvidence powbEvidence) {
    if (powbEvidence.getId() == null) {
      super.saveEntity(powbEvidence);
    } else {
      powbEvidence = super.update(powbEvidence);
    }


    return powbEvidence;
  }


}