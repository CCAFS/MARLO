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

import org.cgiar.ccafs.marlo.data.dao.ProgressTargetCaseGeographicCountryDAO;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicCountry;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProgressTargetCaseGeographicCountryMySQLDAO extends
  AbstractMarloDAO<ProgressTargetCaseGeographicCountry, Long> implements ProgressTargetCaseGeographicCountryDAO {


  @Inject
  public ProgressTargetCaseGeographicCountryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProgressTargetCaseGeographicCountry(long progressTargetCaseGeographicCountryId) {
    ProgressTargetCaseGeographicCountry progressTargetCaseGeographicCountry =
      this.find(progressTargetCaseGeographicCountryId);
    progressTargetCaseGeographicCountry.setActive(false);
    this.delete(progressTargetCaseGeographicCountry);
  }

  @Override
  public boolean existProgressTargetCaseGeographicCountry(long progressTargetCaseGeographicCountryID) {
    ProgressTargetCaseGeographicCountry progressTargetCaseGeographicCountry =
      this.find(progressTargetCaseGeographicCountryID);
    if (progressTargetCaseGeographicCountry == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProgressTargetCaseGeographicCountry find(long id) {
    return super.find(ProgressTargetCaseGeographicCountry.class, id);

  }

  @Override
  public List<ProgressTargetCaseGeographicCountry> findAll() {
    String query = "from " + ProgressTargetCaseGeographicCountry.class.getName() + " where is_active=1";
    List<ProgressTargetCaseGeographicCountry> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProgressTargetCaseGeographicCountry> findGeographicCountryByTargetCase(long targetCaseID) {
    String query = "from " + ProgressTargetCaseGeographicCountry.class.getName()
      + " where report_synthesis_srf_progress_targets_case_id = " + targetCaseID;
    List<ProgressTargetCaseGeographicCountry> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public ProgressTargetCaseGeographicCountry
    save(ProgressTargetCaseGeographicCountry progressTargetCaseGeographicCountry) {
    if (progressTargetCaseGeographicCountry.getId() == null) {
      super.saveEntity(progressTargetCaseGeographicCountry);
    } else {
      progressTargetCaseGeographicCountry = super.update(progressTargetCaseGeographicCountry);
    }


    return progressTargetCaseGeographicCountry;
  }


}