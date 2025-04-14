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

package org.cgiar.ccafs.marlo.action.json.global;

import java.util.logging.Logger;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.inject.Inject;

/**
 * @author Kenji Tanaka - CIAT/CCAFS
 * @author Jhon Garcia - CIAT/CCAFS
 */
public class GetInstitutions extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 4342324441648979508L;

  private static Logger LOG = Logger.getLogger(GetInstitutions.class.getName());

    // Managers
    private InstitutionManager institutionManager;

    // Parameters
    private String queryParameter;
    private List<Map<String, Object>> institutions;

    @Inject
    public GetInstitutions(APConfig config, InstitutionManager institutionManager) {
        super(config);
        this.institutionManager = institutionManager;
    }

    @Override
    public String execute() throws Exception {
        List<Institution> institutionList = institutionManager.findAll();
        institutions = new ArrayList<>();
        
        for (Institution institution : institutionList) {
            Map<String, Object> institutionMap = new HashMap<>();
            institutionMap.put("id", institution.getId());
            institutionMap.put("name", institution.getName());
            institutionMap.put("acronym", institution.getAcronym());
            institutionMap.put("composedName", institution.getComposedName());
            institutionMap.put("type", institution.getInstitutionType().getName());
            institutions.add(institutionMap);
        }
        
        return SUCCESS;
    }

    public List<Map<String, Object>> getInstitutions() {
        return institutions;
    }
    public void setInstitutions(List<Map<String, Object>> institutions) {
        this.institutions = institutions;
    }

    

}
