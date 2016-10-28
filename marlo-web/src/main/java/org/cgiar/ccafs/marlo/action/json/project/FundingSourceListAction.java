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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class FundingSourceListAction extends BaseAction {


  private static final long serialVersionUID = 6304226585314276677L;


  List<Map<String, Object>> sources;

  private long institutionID;

  private int year;

  private String queryParameter;
  private FundingSourceManager fundingSourceManager;
  private InstitutionManager institutionManager;

  @Inject
  public FundingSourceListAction(APConfig config, FundingSourceManager fundingSourceManager,
    InstitutionManager institutionManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.institutionManager = institutionManager;
  }

  @Override
  public String execute() throws Exception {
    sources = new ArrayList<>();
    List<FundingSource> fundingSources;

    Institution institution = institutionManager.getInstitutionById(institutionID);

    Map<String, Object> source;
    if (institution == null) {
      fundingSources = fundingSourceManager.searchFundingSources(queryParameter, year);
    } else {
      fundingSources =
        fundingSourceManager.searchFundingSourcesByInstitution(queryParameter, institution.getId(), year);
    }

    for (FundingSource fundingSource : fundingSources) {
      source = new HashMap<>();
      source.put("id", fundingSource.getId());
      source.put("name", fundingSource.getDescription());
      sources.add(source);
    }
    return SUCCESS;
  }

  public List<Map<String, Object>> getSources() {
    return sources;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    if (parameters.get(APConstants.INSTITUTION_REQUEST_ID) != null) {
      institutionID =
        Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.INSTITUTION_REQUEST_ID))[0]));
    }
    queryParameter = StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]);
    year = Integer.parseInt(StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0]));
  }

  public void setSources(List<Map<String, Object>> sources) {
    this.sources = sources;
  }

}
