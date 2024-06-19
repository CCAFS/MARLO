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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

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
  public List<Integer> getCompleteDeliverableListByPhase(long phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT di.deliverable_id as deliverable_id ");
    query.append("FROM deliverables_info di ");
    query.append("JOIN phases p ON p.id = di.id_phase ");
    query.append("JOIN deliverables d ON d.id = di.deliverable_id ");
    query.append(" WHERE di.id_phase =" + phase);
    query.append(" AND d.is_active = 1  ");
    query.append(" AND di.is_active = d.is_active ");
    query.append(" AND EXISTS (  ");
    query.append(" SELECT 1  ");
    query.append(" FROM section_statuses ss  ");
    query.append(" WHERE ss.deliverable_id = di.deliverable_id  ");
    query.append(" AND ss.`year` = p.`year` ");
    query.append(" AND ss.cycle = p.description ");
    query.append(" AND ss.upkeep = p.upkeep ");
    query.append(" AND ss.section_name = 'deliverableList' ");
    query.append(" AND length(ss.missing_fields) = 0 ");
    query.append(" ) ");
    query.append(" AND di.status = 2 ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<Integer> deliverables = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        String tmp = map.get("deliverable_id").toString();
        deliverables.add(Integer.parseInt(tmp));
      }
    }

    return deliverables;
  }

  @Override
  public List<SectionStatus> getSectionsStatusByReportSynthesis(long synthesisID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    List<SectionStatus> sectionStatuses = new ArrayList<>();
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and report_synthesis_id=" + synthesisID;
    sectionStatuses = super.findAll(query);
    if (sectionStatuses.size() > 0) {
      return sectionStatuses;
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByCaseStudy(long caseStudyID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and case_study_id=" + caseStudyID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByCrpIndicators(long ipLiaisonInstitutionID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and ip_liaison_id=" + ipLiaisonInstitutionID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByCrpProgam(long crpProgramID, String sectionName, String cylce, int year,
    Boolean upkeep) {
    String query =
      "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and crp_program_id="
        + crpProgramID + " and cycle='" + cylce + "' and year=" + year + " and upkeep=" + upkeep;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByDeliverable(long deliverableID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and deliverable_id=" + deliverableID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByFundingSource(long fundingSourceId, String cycle, Integer year, Boolean upkeep,
    String sectionName) {
    String query =
      "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and funding_source_id="
        + fundingSourceId + " and year=" + year + " and upkeep=" + upkeep + " and cycle='" + cycle + "'";
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }


  @Override
  public SectionStatus getSectionStatusByIpProgram(long ipProgramID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and ip_program_id=" + ipProgramID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }


  @Override
  public SectionStatus getSectionStatusByPowbSynthesis(long powbSynthesisID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and powb_synthesis_id=" + powbSynthesisID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProject(long projectID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and project_id=" + projectID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }


  @Override
  public SectionStatus getSectionStatusByProjectCofunded(long projectID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and project_cofunded_id=" + projectID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectContributionToLP6(long projectLp6ContributionID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and project_lp6_contribution_id="
      + projectLp6ContributionID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectExpectedStudy(long expectedID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and project_expected_id=" + expectedID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectHighlight(long projectHighlightID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and highlight_id=" + projectHighlightID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectImpacts(Long projectImpactID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and project_impact_id=" + projectImpactID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectInnovation(long projectInnovationID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and project_innovation_id=" + projectInnovationID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectOutcome(long projectID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and project_outcome_id=" + projectID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByProjectPolicy(long projectPolicyID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and project_policy_id=" + projectPolicyID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusByReportSynthesis(long synthesisID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and report_synthesis_id=" + synthesisID;
    List<SectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public SectionStatus getSectionStatusBySynteshisMog(long ipProgramID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    String query = "from " + SectionStatus.class.getName() + " where section_name='" + sectionName + "' and cycle='"
      + cycle + "' and year=" + year + " and upkeep=" + upkeep + " and ip_program_id=" + ipProgramID;
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