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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.UserRole;

import java.util.Date;
import java.util.List;

/**
 * Manager interface for the transactional creation of a new Global Unit (CRP / Platform / Center).
 * All DB insertions are executed inside a single transaction; the logo is NOT copied here.
 *
 * @author MARLO Dev Team
 */
public interface GlobalUnitCreationManager {

  /**
   * A lightweight DTO that describes a single Phase to create for the new Global Unit.
   */
  public static class PhaseInput {

    private String name;
    private String description;
    private int year;
    private Boolean upkeep;
    private Boolean editable;
    private Boolean visible;
    private Date startDate;
    private Date endDate;

    public String getDescription() {
      return description;
    }

    public Date getEndDate() {
      return endDate;
    }

    public String getName() {
      return name;
    }

    public Date getStartDate() {
      return startDate;
    }

    public int getYear() {
      return year;
    }

    public Boolean isEditable() {
      return editable;
    }

    public Boolean isUpkeep() {
      return upkeep;
    }

    public Boolean isVisible() {
      return visible;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public void setEditable(Boolean editable) {
      this.editable = editable;
    }

    public void setEndDate(Date endDate) {
      this.endDate = endDate;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setStartDate(Date startDate) {
      this.startDate = startDate;
    }

    public void setUpkeep(Boolean upkeep) {
      this.upkeep = upkeep;
    }

    public void setVisible(Boolean visible) {
      this.visible = visible;
    }

    public void setYear(int year) {
      this.year = year;
    }
  }

  /**
   * Input bundle for Global Unit creation.
   */
  public static class CreateRequest {

    private String name;
    private String acronym;
    private long globalUnitTypeId;
    private Long institutionId;
    private boolean isMarlo;
    private boolean login;
    private long templateGlobalUnitId;
    private List<PhaseInput> phasesInput;
    private int currentPhaseIndex;
    private String customFileName;
    private String liaisonName;
    private String liaisonAcronym;
    private long superAdminUserId;
    private List<Long> crpAdminUserIds;

    public String getAcronym() {
      return acronym;
    }

    public String getCustomFileName() {
      return customFileName;
    }

    public List<Long> getCrpAdminUserIds() {
      return crpAdminUserIds;
    }

    public int getCurrentPhaseIndex() {
      return currentPhaseIndex;
    }

    public long getGlobalUnitTypeId() {
      return globalUnitTypeId;
    }

    public Long getInstitutionId() {
      return institutionId;
    }

    public String getLiaisonAcronym() {
      return liaisonAcronym;
    }

    public String getLiaisonName() {
      return liaisonName;
    }

    public String getName() {
      return name;
    }

    public List<PhaseInput> getPhasesInput() {
      return phasesInput;
    }

    public long getSuperAdminUserId() {
      return superAdminUserId;
    }

    public long getTemplateGlobalUnitId() {
      return templateGlobalUnitId;
    }

    public boolean isLogin() {
      return login;
    }

    public boolean isMarlo() {
      return isMarlo;
    }

    public void setAcronym(String acronym) {
      this.acronym = acronym;
    }

    public void setCurrentPhaseIndex(int currentPhaseIndex) {
      this.currentPhaseIndex = currentPhaseIndex;
    }

    public void setCustomFileName(String customFileName) {
      this.customFileName = customFileName;
    }

    public void setCrpAdminUserIds(List<Long> crpAdminUserIds) {
      this.crpAdminUserIds = crpAdminUserIds;
    }

    public void setGlobalUnitTypeId(long globalUnitTypeId) {
      this.globalUnitTypeId = globalUnitTypeId;
    }

    public void setInstitutionId(Long institutionId) {
      this.institutionId = institutionId;
    }

    public void setLiaisonAcronym(String liaisonAcronym) {
      this.liaisonAcronym = liaisonAcronym;
    }

    public void setLiaisonName(String liaisonName) {
      this.liaisonName = liaisonName;
    }

    public void setLogin(boolean login) {
      this.login = login;
    }

    public void setMarlo(boolean isMarlo) {
      this.isMarlo = isMarlo;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setPhasesInput(List<PhaseInput> phasesInput) {
      this.phasesInput = phasesInput;
    }

    public void setSuperAdminUserId(long superAdminUserId) {
      this.superAdminUserId = superAdminUserId;
    }

    public void setTemplateGlobalUnitId(long templateGlobalUnitId) {
      this.templateGlobalUnitId = templateGlobalUnitId;
    }
  }

  /**
   * Creates a new Global Unit with all its required seed data inside a single transaction:
   * <ol>
   * <li>global_units</li>
   * <li>phases (chained via next)</li>
   * <li>crp_users (super-admin access)</li>
   * <li>roles (cloned from template)</li>
   * <li>crp_loc_element_types (cloned from template)</li>
   * <li>liaison_institutions (PMU/Secretariat)</li>
   * <li>custom_parameters (current_phase, crp_custom_file)</li>
   * </ol>
   * Logo copy happens OUTSIDE this transaction, after successful return.
   *
   * @param request bundle containing all creation parameters
   * @return the persisted {@link GlobalUnit} with its generated id
   * @throws Exception if any step fails (full rollback occurs)
   */
  GlobalUnit createGlobalUnit(CreateRequest request);

  /**
   * Gets the current CRP-Admin assignments for a Global Unit using the canonical role resolution.
   *
   * @param globalUnitId target Global Unit id
   * @return CRP-Admin assignments, or an empty list when the role has no users
   */
  List<UserRole> getCrpAdminTeam(long globalUnitId);

  /**
   * Atomically synchronizes the CRP-Admin team of an existing Global Unit inside a single transaction.
   * <ul>
   * <li>Removes CRP-Admin assignments that are no longer submitted. When a removed user keeps no other role in
   * the Global Unit and is not SuperAdmin in any Global Unit, its {@code crp_users} access is deactivated.</li>
   * <li>Adds the newly submitted users to the CRP-Admin role and grants (or reactivates) their {@code crp_users}
   * access to the Global Unit.</li>
   * </ul>
   * If any step fails the whole synchronization for this Global Unit is rolled back.
   *
   * @param globalUnitId target Global Unit id
   * @param submittedUserIds user ids that must remain assigned as CRP-Admin (must contain at least one)
   * @throws IllegalArgumentException when the Global Unit, the role, or the submitted users are invalid
   */
  void syncCrpAdminTeam(long globalUnitId, List<Long> submittedUserIds);

  /**
   * Soft-deletes a Global Unit and removes all CRP-Admin {@code user_roles} for that Global Unit inside a single
   * transaction. {@code crp_users} access is deactivated only when the user keeps no other role in the Global Unit
   * and is not SuperAdmin in any Global Unit.
   *
   * @param globalUnitId target Global Unit id
   * @throws IllegalArgumentException when the Global Unit does not exist
   */
  void softDeleteGlobalUnit(long globalUnitId);
}
