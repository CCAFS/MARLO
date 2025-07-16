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

package org.cgiar.ccafs.marlo.action.json.summary;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.action.summaries.ai.service.AIIndicatorReport;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class AIReportSummaryAction extends BaseAction {


  private static final long serialVersionUID = -5595055892247130791L;

  private String indicatorName;
  private int year;
  private String jsonResponse;

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Inject
  public AIReportSummaryAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    Map<String, Object> response = new HashMap<>();

    if (indicatorName == null || indicatorName.isEmpty()) {
      response.put("status", "error");
      response.put("message", "Indicator name is required.");
      jsonResponse = gson.toJson(response);
      return SUCCESS;
    }

    AIIndicatorReport report = this.getAIIndicatorReportObject(indicatorName, year);

    if (report != null) {
      response.put("status", "success");
      response.put("indicator", report.getIndicator());
      response.put("year", report.getYear());
      response.put("content", report.getContent());
    } else {
      response.put("status", "error");
      response.put("message", "Could not generate AI report.");
    }

    jsonResponse = gson.toJson(response);
    return SUCCESS;
  }

  public String getJsonResponse() {
    return jsonResponse;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, String[]> parameters = this.getRequest().getParameterMap();

    if (parameters.containsKey("indicatorName")) {
      this.indicatorName = parameters.get("indicatorName")[0];
    }

    if (parameters.containsKey("year")) {
      try {
        this.year = Integer.parseInt(parameters.get("year")[0]);
      } catch (NumberFormatException e) {
        this.year = 2025; // fallback or default year
      }
    }
  }

  public void setIndicatorName(String indicatorName) {
    this.indicatorName = indicatorName;
  }

  public void setYear(int year) {
    this.year = year;
  }
}

