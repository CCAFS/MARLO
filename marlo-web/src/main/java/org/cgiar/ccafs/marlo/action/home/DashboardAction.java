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

package org.cgiar.ccafs.marlo.action.home;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableHomeDTO;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.HomeIndicatorsGraphByTotalDTO;
import org.cgiar.ccafs.marlo.data.model.InnovationHomeDTO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PolicyHomeDTO;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.StudyHomeDTO;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.shiro.authz.AuthorizationInfo;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DashboardAction extends BaseAction {

	private static final long serialVersionUID = 6686785556753962379L;

	// Managers
	private PhaseManager phaseManager;
	private ProjectManager projectManager;
	private GlobalUnitManager crpManager;
	private DeliverableManager deliverableManager;
	private ProjectExpectedStudyManager projectExpectedStudyManager;
	private ProjectInnovationManager projectInnovationManager;
	private ProjectPolicyManager projectPolicyManager;

	// Variables
	private GlobalUnit loggedCrp;
	private String[] indicatorNames = new String[] { "Projects", "Deliverables", "Studies", "Innovations", "Policies" };
	private String[] indicatorColors = new String[] { "#4B91D7", "#F39C12", "#71CE48", "#8139B6", "#E43A74" };

	private List<Project> myProjects;
	private List<DeliverableHomeDTO> myDeliverables = new ArrayList<>();
	private List<StudyHomeDTO> myStudies = new ArrayList<>();
	private List<InnovationHomeDTO> myInnovations = new ArrayList<>();
	private List<PolicyHomeDTO> myPolicies = new ArrayList<>();
	private List<HomeIndicatorsGraphByTotalDTO> byTotalDTOs = new ArrayList<>();

	@Inject
	public DashboardAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
			PhaseManager phaseManager, DeliverableManager deliverableManager, ProjectPolicyManager projectPolicyManager,
			ProjectExpectedStudyManager projectExpectedStudyManager,
			ProjectInnovationManager projectInnovationManager) {
		super(config);
		this.projectManager = projectManager;
		this.crpManager = crpManager;
		this.phaseManager = phaseManager;
		this.deliverableManager = deliverableManager;
		this.projectExpectedStudyManager = projectExpectedStudyManager;
		this.projectInnovationManager = projectInnovationManager;
		this.projectPolicyManager = projectPolicyManager;
	}

	public List<HomeIndicatorsGraphByTotalDTO> getByTotalDTOs() {
		return byTotalDTOs;
	}

	public GlobalUnit getLoggedCrp() {
		return loggedCrp;
	}

	/**
	 * Get the value of myDeliverables
	 *
	 * @return the value of myDeliverables
	 */
	public List<DeliverableHomeDTO> getMyDeliverables() {
		return myDeliverables;
	}

	public List<InnovationHomeDTO> getMyInnovations() {
		return myInnovations;
	}

	public List<PolicyHomeDTO> getMyPolicies() {
		return myPolicies;
	}

	public List<Project> getMyProjects() {
		return myProjects;
	}

	public List<StudyHomeDTO> getMyStudies() {
		return myStudies;
	}

	@Override
	public void prepare() throws Exception {
		loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
		loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
		Phase phase = phaseManager.getPhaseById(this.getActualPhase().getId());

		if (this.isSwitchSession()) {
			this.clearPermissionsCache();
		}

		// if (projectManager.findAll() != null) {
		myProjects = new ArrayList<>();
		if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {

			for (ProjectPhase projectPhase : phase.getProjectPhases()) {
				projectPhase.getProject()
						.setProjectInfo(projectPhase.getProject().getProjecInfoPhase(this.getActualPhase()));
				myProjects.add(projectPhase.getProject());
			}

		} else {

			List<Project> allProjects = new ArrayList<>();
			if (phase != null) {
				/*
				 * if (phase.getCrp().getGlobalUnitType().getId().equals(
				 * APConstants.CRP_DASHBOARD_CENTER_IDENTIFICATION)) { for
				 * (ProjectPhase projectPhase : phase.getProjectPhases()) {
				 * Project project =
				 * projectManager.getProjectById(projectPhase.getProject().getId
				 * ()); if (this.isProjectSubmitted(project.getId())) {
				 * allProjects.add(project); } } }
				 */
				for (ProjectPhase projectPhase : phase.getProjectPhases()) {
					allProjects.add(projectManager.getProjectById(projectPhase.getProject().getId()));
				}
			}

			AuthorizationInfo info = ((APCustomRealm) this.securityContext.getRealm())
					.getAuthorizationInfo(this.securityContext.getSubject().getPrincipals());

			for (String permission : info.getStringPermissions()) {
				if (permission.contains("project")) {
					for (int i = 0; i > permission.split(":").length; i++) {
						System.out.println(permission.split(":")[i]);
					}
				}
			}

			myProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym()).stream()
					.filter(p -> p.isActive()).collect(Collectors.toList());

			List<Project> mProjects = new ArrayList<>();
			mProjects.addAll(myProjects);

			for (Project project : mProjects) {
				project.getProjecInfoPhase(this.getActualPhase());

				if (!allProjects.contains(project)) {
					myProjects.remove(project);
				}
			}

		}
		// Skip closed projects for Reporting
		if (this.isPlanningActive()) {
			if (this.getActualPhase() != null && this.getActualPhase().getId() != null) {
				List<Project> closedProjects = projectManager.getCompletedProjects(this.getCrpID(),
						this.getActualPhase().getId());
				if (closedProjects != null) {
					// closedProjects.addAll(projectManager.getNoPhaseProjects(this.getCrpID(),
					// this.getActualPhase()));
					myProjects.removeAll(closedProjects);
				}
				Collections.sort(myProjects, (p1, p2) -> p1.getId().compareTo(p2.getId()));

			}
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("y");

			myProjects = myProjects.stream().filter(mp -> mp.isActive()
					&& mp.getProjecInfoPhase(this.getActualPhase()) != null
					&& (mp.getProjecInfoPhase(this.getActualPhase()).getEndDate() == null || Integer.parseInt(
							dateFormat.format(mp.getProjecInfoPhase(this.getActualPhase()).getEndDate())) >= this
									.getCurrentCycleYear()))
					.collect(Collectors.toList());
		}

		myDeliverables = myProjects.stream().filter(p -> p != null && p.getId() != null)
				.flatMap(p -> deliverableManager
						.getDeliverablesByProjectAndPhaseHome(this.getActualPhase().getId(), p.getId()).stream())
				.collect(Collectors.toList());

		myStudies = myProjects.stream().filter(p -> p != null && p.getId() != null)
				.flatMap(p -> projectExpectedStudyManager
						.getStudiesByProjectAndPhaseHome(this.getActualPhase().getId(), p.getId()).stream())
				.collect(Collectors.toList());

		myInnovations = myProjects.stream().filter(p -> p != null && p.getId() != null)
				.flatMap(p -> projectInnovationManager
						.getInnovationsByProjectAndPhaseHome(this.getActualPhase().getId(), p.getId()).stream())
				.collect(Collectors.toList());

		myPolicies = myProjects.stream().filter(p -> p != null && p.getId() != null)
				.flatMap(p -> projectPolicyManager
						.getPoliciesByProjectAndPhaseHome(this.getActualPhase().getId(), p.getId()).stream())
				.collect(Collectors.toList());

		byTotalDTOs.add(new HomeIndicatorsGraphByTotalDTO(indicatorNames[0], myProjects.size(), indicatorColors[0]));
		byTotalDTOs
				.add(new HomeIndicatorsGraphByTotalDTO(indicatorNames[1], myDeliverables.size(), indicatorColors[1]));
		byTotalDTOs.add(new HomeIndicatorsGraphByTotalDTO(indicatorNames[2], myStudies.size(), indicatorColors[2]));
		byTotalDTOs.add(new HomeIndicatorsGraphByTotalDTO(indicatorNames[3], myInnovations.size(), indicatorColors[3]));
		byTotalDTOs.add(new HomeIndicatorsGraphByTotalDTO(indicatorNames[4], myPolicies.size(), indicatorColors[4]));
	}

	public void setByTotalDTOs(List<HomeIndicatorsGraphByTotalDTO> byTotalDTOs) {
		this.byTotalDTOs = byTotalDTOs;
	}

	public void setLoggedCrp(GlobalUnit loggedCrp) {
		this.loggedCrp = loggedCrp;
	}

	/**
	 * Set the value of myDeliverables
	 *
	 * @param myDeliverables new value of myDeliverables
	 */
	public void setMyDeliverables(List<DeliverableHomeDTO> myDeliverables) {
		this.myDeliverables = myDeliverables;
	}

	public void setMyInnovations(List<InnovationHomeDTO> myInnovations) {
		this.myInnovations = myInnovations;
	}

	public void setMyPolicies(List<PolicyHomeDTO> myPolicies) {
		this.myPolicies = myPolicies;
	}

	public void setMyProjects(List<Project> myProjects) {
		this.myProjects = myProjects;
	}

	public void setMyStudies(List<StudyHomeDTO> myStudies) {
		this.myStudies = myStudies;
	}

}
