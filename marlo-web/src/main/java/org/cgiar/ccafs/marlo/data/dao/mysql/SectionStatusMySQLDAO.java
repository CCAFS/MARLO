/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.SectionStatusDAO;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;

import java.util.List;

import com.google.inject.Inject;

public class SectionStatusMySQLDAO implements SectionStatusDAO {

  private StandardDAO dao;

  @Inject
  public SectionStatusMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteSectionStatus(long sectionStatusId) {
    SectionStatus sectionStatus = this.find(sectionStatusId);

    return dao.delete(sectionStatus);
  }

  @Override
  public boolean existSectionStatus(long sectionStatusID) {
    SectionStatus sectionStatus = this.find(sectionStatusID);
    if (sectionStatus == null) {
      return false;
    }
    return true;

  }

  @Override
  public SectionStatus find(long id) {
    return dao.find(SectionStatus.class, id);

  }

  @Override
  public List<SectionStatus> findAll() {
    String query = "from " + SectionStatus.class.getName() + " where is_active=1";
    List<SectionStatus> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(SectionStatus sectionStatus) {
    if (sectionStatus.getId() == null) {
      dao.save(sectionStatus);
    } else {
      dao.update(sectionStatus);
    }


    return sectionStatus.getId();
  }


}