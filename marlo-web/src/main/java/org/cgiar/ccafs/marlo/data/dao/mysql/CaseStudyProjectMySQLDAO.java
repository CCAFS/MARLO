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

import org.cgiar.ccafs.marlo.data.dao.CaseStudyProjectDAO;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CaseStudyProjectMySQLDAO extends AbstractMarloDAO<CaseStudyProject, Long> implements CaseStudyProjectDAO {


  @Inject
  public CaseStudyProjectMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteCaseStudyProject(long caseStudyProjectId) {
    CaseStudyProject caseStudyProject = this.find(caseStudyProjectId);

    return super.delete(caseStudyProject);
  }

  @Override
  public boolean existCaseStudyProject(long caseStudyProjectID) {
    CaseStudyProject caseStudyProject = this.find(caseStudyProjectID);
    if (caseStudyProject == null) {
      return false;
    }
    return true;

  }

  @Override
  public CaseStudyProject find(long id) {
    return super.find(CaseStudyProject.class, id);

  }

  @Override
  public List<CaseStudyProject> findAll() {
    String query = "from " + CaseStudyProject.class.getName() + " where is_active=1";
    List<CaseStudyProject> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CaseStudyProject caseStudyProject) {
    if (caseStudyProject.getId() == null) {
      super.saveEntity(caseStudyProject);
    } else {
      super.update(caseStudyProject);
    }


    return caseStudyProject.getId();
  }


}