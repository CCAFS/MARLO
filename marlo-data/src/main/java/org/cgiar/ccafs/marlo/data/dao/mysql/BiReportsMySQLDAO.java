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

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.BiReportsDAO;
import org.cgiar.ccafs.marlo.data.model.BiReports;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class BiReportsMySQLDAO extends AbstractMarloDAO<BiReports, Long> implements BiReportsDAO {


  @Inject
  public BiReportsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteBiReports(long biReportsId) {
    BiReports biReports = this.find(biReportsId);
    biReports.setIsActive(false);
    this.update(biReports);
  }

  @Override
  public boolean existBiReports(long biReportsID) {
    BiReports biReports = this.find(biReportsID);
    if (biReports == null) {
      return false;
    }
    return true;

  }

  @Override
  public BiReports find(long id) {
    return super.find(BiReports.class, id);

  }

  @Override
  public List<BiReports> findAll() {
    String query = "from " + BiReports.class.getName() + " where is_active=1";
    List<BiReports> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public BiReports save(BiReports biReports) {
    if (biReports.getId() == null) {
      super.saveEntity(biReports);
    } else {
      biReports = super.update(biReports);
    }


    return biReports;
  }


}