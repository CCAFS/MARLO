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

import org.cgiar.ccafs.marlo.data.dao.SectionStatusDAO;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class SectionStatusMySQLDAO extends AbstractMarloDAO<SectionStatus, Long> implements SectionStatusDAO {


  @Inject
  public SectionStatusMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSectionStatus(long sectionStatusId) {
    SectionStatus sectionStatus = this.find(sectionStatusId);

    super.delete(sectionStatus);
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
    return super.find(SectionStatus.class, id);

  }

  @Override
  public List<SectionStatus> findAll() {
    String query = "from " + SectionStatus.class.getName() + " ";
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return new ArrayList<SectionStatus>();

  }

  @Override
  public SectionStatus getSectionStatusByCaseStudy(long caseStudyID, String cycle, int year, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and case_study_id=" + caseStudyID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByCrpIndicators(long ipLiaisonInstitutionID, String cycle, int year,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and ip_liaison_id=" + ipLiaisonInstitutionID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByCrpProgam(long crpProgramID, String sectionName, String cylce, int year) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and crp_program_id=" + crpProgramID + " and cycle='" + cylce + "' and year=" + year;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByDeliverable(long deliverableID, String cycle, int year, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and deliverable_id=" + deliverableID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByFundingSource(long fundingSourceId, String cycle, Integer year,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and funding_source_id=" + fundingSourceId + " and year=" + year + " and cycle='" + cycle + "'";
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByIpProgram(long ipProgramID, String cycle, int year, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and ip_program_id=" + ipProgramID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProject(long projectID, String cycle, int year, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and project_id=" + projectID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectCofunded(long projectID, String cycle, int year, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and project_cofunded_id=" + projectID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectHighlight(long projectHighlightID, String cycle, int year,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and highlight_id=" + projectHighlightID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectOutcome(long projectID, String cycle, int year, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and project_outcome_id=" + projectID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusBySynteshisMog(long ipProgramID, String cycle, int year, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and ip_program_id=" + ipProgramID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus save(SectionStatus sectionStatus) {
    if (sectionStatus.getId() == null) {
      super.saveEntity(sectionStatus);
    } else {
      sectionStatus = super.update(sectionStatus);
    }


    return sectionStatus;
  }
}