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

import org.cgiar.ccafs.marlo.data.dao.PowbSynthesisDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbSynthesisMySQLDAO extends AbstractMarloDAO<PowbSynthesis, Long> implements PowbSynthesisDAO {


  @Inject
  public PowbSynthesisMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbSynthesis(long powbSynthesisId) {
    PowbSynthesis powbSynthesis = this.find(powbSynthesisId);
    powbSynthesis.setActive(false);
    this.save(powbSynthesis);
  }

  @Override
  public boolean existPowbSynthesis(long powbSynthesisID) {
    PowbSynthesis powbSynthesis = this.find(powbSynthesisID);
    if (powbSynthesis == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbSynthesis find(long id) {
    return super.find(PowbSynthesis.class, id);

  }

  @Override
  public List<PowbSynthesis> findAll() {
    String query = "from " + PowbSynthesis.class.getName() + " where is_active=1";
    List<PowbSynthesis> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbSynthesis findSynthesis(long phaseID, long liaisonInstitutionID) {
    String query = "from " + PowbSynthesis.class.getName() + " where is_active=1 and id_phase= " + phaseID
      + " and liaison_institution_id= " + liaisonInstitutionID;
    List<PowbSynthesis> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }

  @Override
  public PowbSynthesis save(PowbSynthesis powbSynthesis) {
    if (powbSynthesis.getId() == null) {
      super.saveEntity(powbSynthesis);
    } else {
      powbSynthesis = super.update(powbSynthesis);
    }


    return powbSynthesis;
  }

  @Override
  public PowbSynthesis save(PowbSynthesis powbSynthesis, String sectionName, List<String> relationsName, Phase phase) {
    if (powbSynthesis.getId() == null) {
      super.saveEntity(powbSynthesis, sectionName, relationsName, phase);
    } else {
      powbSynthesis = super.update(powbSynthesis, sectionName, relationsName, phase);
    }
    return powbSynthesis;
  }


}