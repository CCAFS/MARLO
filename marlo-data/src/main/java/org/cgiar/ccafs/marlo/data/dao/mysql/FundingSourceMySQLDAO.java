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

import org.cgiar.ccafs.marlo.action.funding.dto.FundingSourceSearchSummary;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceDAO;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceInfoDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;

@Named
public class FundingSourceMySQLDAO extends AbstractMarloDAO<FundingSource, Long> implements FundingSourceDAO {

  public FundingSourceInfoDAO fundingSourceInfoDAO;

  @Inject
  public FundingSourceMySQLDAO(SessionFactory sessionFactory, FundingSourceInfoDAO fundingSourceInfoDAO) {
    super(sessionFactory);
    this.fundingSourceInfoDAO = fundingSourceInfoDAO;
  }

  @Override
  public void deleteFundingSource(long fundingSourceId) {
    FundingSource fundingSource = this.find(fundingSourceId);
    fundingSource.setActive(false);
    this.save(fundingSource);
  }

  @Override
  public boolean existFundingSource(long fundingSourceID) {
    FundingSource fundingSource = this.find(fundingSourceID);
    if (fundingSource == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSource find(long id) {
    return super.find(FundingSource.class, id);

  }


  @Override
  public List<FundingSource> findAll() {
    String query = "from " + FundingSource.class.getName() + " where is_active=1";
    List<FundingSource> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Map<String, Object>> getFundingSource(long userId, String crp) {
    List<Map<String, Object>> list = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    builder.append("select DISTINCT project_id from user_permission where  crp_acronym='" + crp
      + "' and permission_id = 438 and project_id is not null");
    if (super.getTemTableUserId() == userId) {
      list = super.findCustomQuery(builder.toString());
    } else {
      list = super.excuteStoreProcedure(" call getPermissions(" + userId + ")", builder.toString());
    }
    return list;
  }

  @Override
  public List<FundingSource> getFundingSourceSummaries(GlobalUnit globalUnit, Phase phase, Set<Integer> statusTypes) {

    /**
     * Ideally it would be great if we could just fetch the required properties rather than the entity graph,
     * but because we need to show multiple fundingSourceInstitutions we would need to detect and then
     * remove duplicate fundingSources from the resultSet in the cases where there is more than one leadPartner.
     * Therefore I have opted to do a HQL query instead, which will handle this scenario well.
     */
    String queryString = "SELECT DISTINCT f FROM FundingSource f " + "inner join fetch f.fundingSourceInfos fsi "
      + "left join fetch fsi.budgetType " + "left join fetch f.fundingSourceInstitutions fsint "
      + "left join fetch fsint.institution " + "left join fetch fsi.directDonor " + "left join fetch fsi.originalDonor "
      + "left join fetch f.sectionStatuses ss " + "left join fetch f.fundingSourceBudgets fsb "
      + "WHERE f.active = TRUE " + "AND f.crp = :globalUnit " + "AND fsi.phase = :phase "
      + "AND ( fsint IS NULL OR fsint.phase = :phase ) " /** I think SectionStatus should be updated to use a phase **/
      // + "AND ( ss IS NULL OR (ss.cycle = :phaseDescription AND ss.year = :phaseYear ) ) "
      // Above: I don't know why this piece of code is here
      // + "AND ( fsb IS NULL OR (fsb.year = :phaseYear AND fsb.phase = :phase ) ) "
      // The above line should be what we use, but it appears we have some data corruption, excluding the year works for
      // now, so using the below statement.
      + "AND ( fsb IS NULL OR fsb.phase = :phase ) " + "AND ( fsi.status IS NULL OR ( fsi.status IN ( "
      + statusTypes.stream().map(i -> i.toString()).collect(Collectors.joining(",")) + " ) ) ) "
      + "ORDER BY fsi.endDate NULLS FIRST";

    /**
     * We are using PhaseDescription.PLANNING because if REPORTING is entered the section status results will
     * filter out the results.
     */
    @SuppressWarnings("unchecked")
    List<FundingSource> fundingSources = this.getSessionFactory().getCurrentSession().createQuery(queryString)
      .setParameter("globalUnit", globalUnit).setParameter("phase", phase)
      // .setParameter("phaseDescription", PhaseDescription.PLANNING.toString())
      // .setParameter("phaseYear", phase.getYear())
      .list();

    return fundingSources;

  }

  @Override
  public List<FundingSource> getGlobalUnitFundingSourcesByPhaseAndTypes(GlobalUnit globalUnit, Phase phase,
    Set<Integer> statusTypes) {

    /**
     * Ideally it would be great if we could just fetch the required properties rather than the entity graph,
     * but because we need to show multiple fundingSourceInstitutions we would need to detect and then
     * remove duplicate fundingSources from the resultSet in the cases where there is more than one leadPartner.
     * Therefore I have opted to do a HQL query instead, which will handle this scenario well.
     */
    String queryString = "SELECT DISTINCT f FROM FundingSource f " + "inner join fetch f.fundingSourceInfos fsi "
      + "left join fetch fsi.budgetType " + "left join fetch f.fundingSourceInstitutions fsint "
      + "left join fetch fsint.institution " + "left join fetch fsi.directDonor " + "left join fetch fsi.originalDonor "
      + "WHERE f.active = TRUE " + "AND f.crp = :globalUnit " + "AND fsi.phase = :phase "
      /** I think SectionStatus should be updated to use a phase **/
      // + "AND ( ss IS NULL OR (ss.cycle = :phaseDescription AND ss.year = :phaseYear ) ) "
      // Above: I don't know why this piece of code is here
      // + "AND ( fsb IS NULL OR (fsb.year = :phaseYear AND fsb.phase = :phase ) ) "
      // The above line should be what we use, but it appears we have some data corruption, excluding the year works for
      // now, so using the below statement.
      + "AND ( fsi.status IS NULL OR ( fsi.status IN ( "
      + statusTypes.stream().map(i -> i.toString()).collect(Collectors.joining(",")) + " ) ) ) "
      + "ORDER BY fsi.endDate NULLS FIRST";

    /**
     * We are using PhaseDescription.PLANNING because if REPORTING is entered the section status results will
     * filter out the results.
     */
    @SuppressWarnings("unchecked")
    List<FundingSource> fundingSources = this.getSessionFactory().getCurrentSession().createQuery(queryString)
      .setParameter("globalUnit", globalUnit).setParameter("phase", phase)
      // .setParameter("phaseDescription", PhaseDescription.PLANNING.toString())
      // .setParameter("phaseYear", phase.getYear())
      .list();

    return fundingSources;

  }

  @Override
  public List<FundingSource> getGlobalUnitFundingSourcesByPhaseAndTypesWithoutInstitutions(GlobalUnit globalUnit,
    Phase phase, Set<Integer> statusTypes) {

    /**
     * Ideally it would be great if we could just fetch the required properties rather than the entity graph,
     * but because we need to show multiple fundingSourceInstitutions we would need to detect and then
     * remove duplicate fundingSources from the resultSet in the cases where there is more than one leadPartner.
     * Therefore I have opted to do a HQL query instead, which will handle this scenario well.
     */
    String queryString = "SELECT DISTINCT f FROM FundingSource f " + "inner join fetch f.fundingSourceInfos fsi "
      + "left join fetch fsi.budgetType " + "left join fetch f.fundingSourceInstitutions fsint "
      + "left join fetch fsint.institution " + "left join fetch fsi.directDonor " + "left join fetch fsi.originalDonor "
      + "left join fetch f.sectionStatuses ss " + "left join fetch f.fundingSourceBudgets fsb "
      + "WHERE f.active = TRUE " + "AND f.crp = :globalUnit " + "AND fsi.phase = :phase "

      + "AND ( fsi.status IS NULL OR ( fsi.status IN ( "
      + statusTypes.stream().map(i -> i.toString()).collect(Collectors.joining(",")) + " ) ) ) "
      + "ORDER BY fsi.endDate NULLS FIRST";

    /**
     * We are using PhaseDescription.PLANNING because if REPORTING is entered the section status results will
     * filter out the results.
     */
    @SuppressWarnings("unchecked")
    List<FundingSource> fundingSources = this.getSessionFactory().getCurrentSession().createQuery(queryString)
      .setParameter("globalUnit", globalUnit).setParameter("phase", phase)
      // .setParameter("phaseDescription", PhaseDescription.PLANNING.toString())
      // .setParameter("phaseYear", phase.getYear())
      .list();

    return fundingSources;

  }

  @Override
  public FundingSource save(FundingSource fundingSource) {
    if (fundingSource.getId() == null) {
      super.saveEntity(fundingSource);
    } else {
      fundingSource = super.update(fundingSource);
    }


    return fundingSource;
  }

  @Override
  public FundingSource save(FundingSource fundingSource, String sectionName, List<String> relationsName, Phase phase) {
    if (fundingSource.getId() == null) {
      super.saveEntity(fundingSource, sectionName, relationsName, phase);
    } else {
      fundingSource = super.update(fundingSource, sectionName, relationsName, phase);
    }


    return fundingSource;
  }

  @Override
  public List<FundingSourceSearchSummary> searchFundingSources(String query, int year, long crpID, long phaseID) {

    return this.searchFundingSourcesByInstitution(query, null, year, crpID, phaseID);
  }


  @Override
  public List<FundingSource> searchFundingSourcesByFinanceCode(String ocsCode) {
    StringBuilder q = new StringBuilder();
    q.append("from " + FundingSource.class.getName());
    q.append(" where finance_code=" + ocsCode);


    List<FundingSource> fundingSources = super.findAll(q.toString());
    return fundingSources;
  }

  @Override
  public List<FundingSourceSearchSummary> searchFundingSourcesByInstitution(String userInput, Long institutionID,
    int year, long crpID, long phaseID) {
    StringBuilder q = new StringBuilder();

    q.append(
      "SELECT sub.id AS id, sub.name AS name, sub.type AS type, sub.typeId AS typeId, sub.financeCode AS financeCode, sub.w1w2 AS w1w2, sub.budget AS budget, SUM(pb.amount) AS usedAmount ");
    q.append("FROM ");

    q.append(
      "(SELECT DISTINCT fs.id AS `id`, fsi.title AS `name`, bt.name AS `type`, bt.id AS `typeId`, fsi.finance_code AS `financeCode`, fsi.w1w2 AS `w1w2`, fsb.budget AS `budget` ");

    q.append("FROM funding_sources_info fsi ");
    q.append("INNER JOIN funding_sources fs ON fs.id = fsi.funding_source_id AND fs.is_active AND fs.global_unit_id = "
      + crpID + " ");

    // Only add the INNER JOIN if an institutionId was provided.
    if (institutionID != null) {
      q.append(
        "INNER JOIN funding_source_institutions fsin ON fs.id = fsin.funding_source_id AND fsin.institution_id = "
          + institutionID + " AND fsin.id_phase = " + phaseID + " ");
    }
    q.append("LEFT JOIN budget_types bt ON bt.id = fsi.type ");
    q.append("LEFT JOIN funding_source_budgets fsb ON fs.id = fsb.funding_source_id ");
    q.append("WHERE 1=1 ");
    q.append("AND fsi.title IS NOT NULL ");
    q.append("AND (fsi.status IS NULL OR fsi.status IN (1,2,4,7) ) ");
    q.append("AND (fsi.title LIKE '%" + userInput + "%' ");
    q.append("OR fsi.funding_source_id LIKE '%" + userInput + "%' ");
    q.append("OR CONCAT('FS', fsi.funding_source_id) LIKE '%" + userInput + "%' ");
    q.append("OR fsi.finance_code LIKE '%" + userInput + "%' ");
    q.append("OR (SELECT NAME FROM budget_types bt WHERE bt.id = fsi.type) LIKE '%" + userInput + "%' ) ");
    q.append("AND fsi.id_phase = " + phaseID);
    q.append(" AND fsi.end_date IS NOT NULL ");
    q.append("AND ( ( fsb.id IS NULL OR ( fsb.year = " + year + " AND fsb.id_phase = " + phaseID + " ) ) ");
    // q.append("AND ( ( fsb.id IS NULL OR ( fsb.id_phase = " + phaseID + " ) ) ");
    q.append(" AND (" + year + " <= YEAR(fsi.end_date) OR " + year + " <= YEAR(fsi.extended_date) ) ) ");
    q.append(") AS sub ");
    q.append("LEFT JOIN project_budgets pb ON pb.funding_source_id = sub.id AND pb.is_active=1 " + "AND pb.YEAR ="
      + year + " AND pb.id_phase =" + phaseID + " ");
    q.append("GROUP BY sub.id, sub.name, sub.type, sub.typeId, sub.financeCode, sub.w1w2, sub.budget ");
    q.append("ORDER BY sub.id, sub.name");


    Query query = this.getSessionFactory().getCurrentSession().createSQLQuery(q.toString());
    query.setResultTransformer(new AliasToBeanResultTransformer(FundingSourceSearchSummary.class));
    List<FundingSourceSearchSummary> result = query.list();

    return result;

  }

  @Override
  public List<FundingSource> searchFundingSourcesByLocElement(long projectId, long locElementId, int year, long crpID,
    long phaseID) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("funding_sources.id as id ");
    query.append("FROM ");
    query.append("funding_source_locations ");
    query.append("INNER JOIN funding_sources ON funding_source_locations.funding_source_id = funding_sources.id ");
    query.append("INNER JOIN project_budgets ON project_budgets.funding_source_id = funding_sources.id ");
    query.append("WHERE ");
    query.append("funding_source_locations.loc_element_id =" + locElementId + "   AND funding_sources.global_unit_id="
      + crpID + " AND project_budgets.project_id=" + projectId + "  AND  funding_source_locations.is_active=1 and ");
    query.append("project_budgets.is_active = 1 AND ");
    query.append("funding_source_locations.`id_phase` =" + phaseID);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

    List<FundingSource> fundingSources = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        FundingSource fundingSource = this.find(Long.parseLong(map.get("id").toString()));


        fundingSources.add(fundingSource);
      }
    }

    return fundingSources;

  }

  @Override
  public List<FundingSource> searchFundingSourcesByLocElementType(long projectId, long locElementTypeId, int year,
    long crpID) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("funding_sources.id as id ");
    query.append("FROM ");
    query.append("funding_source_locations ");
    query.append("INNER JOIN funding_sources ON funding_source_locations.funding_source_id = funding_sources.id ");
    query.append("INNER JOIN project_budgets ON project_budgets.funding_source_id = funding_sources.id ");
    query.append("WHERE ");
    query.append(
      "funding_source_locations.loc_element_type_id =" + locElementTypeId + " AND funding_sources.global_unit_id="
        + crpID + " AND project_budgets.project_id=" + projectId + "  AND  funding_source_locations.is_active=1 and ");
    query.append("project_budgets.is_active = 1   ");
    // query.append(" AND project_budgets.`year` =" + year);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

    List<FundingSource> fundingSources = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        FundingSource fundingSource = this.find(Long.parseLong(map.get("id").toString()));
        fundingSources.add(fundingSource);
      }
    }

    return fundingSources;

  }


}