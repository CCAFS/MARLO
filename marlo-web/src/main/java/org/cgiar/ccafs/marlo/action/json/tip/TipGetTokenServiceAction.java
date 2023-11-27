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

package org.cgiar.ccafs.marlo.action.json.tip;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.TipParametersManager;
import org.cgiar.ccafs.marlo.data.model.TipParameters;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.ibm.icu.text.SimpleDateFormat;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TipGetTokenServiceAction extends BaseAction {

  private static final long serialVersionUID = 3229622720197560976L;
  private static final Logger LOG = LoggerFactory.getLogger(TipGetTokenServiceAction.class);

  // Managers
  TipParametersManager tipParametersManager;

  // Paramters
  String token;

  // Front-end
  private String url;

  @Inject
  public TipGetTokenServiceAction(APConfig config, TipParametersManager tipParametersManager) {
    super(config);
    this.tipParametersManager = tipParametersManager;
  }

  @Override
  public String execute() throws Exception {

    try {
      TipParameters tipParameters = new TipParameters();

      // URL of the first service
      List<TipParameters> tipParametesList = new ArrayList<>();
      tipParametesList = tipParametersManager.findAll();
      String serviceUrl = null;
      String privateKey = null;
      if (tipParametesList != null && !tipParametesList.isEmpty() && tipParametesList.get(0) != null
        && tipParametesList.get(0).getTipTokenService() != null && tipParametesList.get(0).getPrivateKey() != null) {

        tipParameters = tipParametesList.get(0);
        serviceUrl = tipParameters.getTipTokenService();
        privateKey = tipParametesList.get(0).getPrivateKey();

        URL url = new URL(serviceUrl);

        // Create connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        String requestBody = "{\"private_key\":" + privateKey + "}";

        // Get the output stream to send data
        try (OutputStream os = connection.getOutputStream()) {
          byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
          os.write(input, 0, input.length);
        }

        // Get response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // Process JSON
        JSONObject jsonResponse = new JSONObject(response.toString());
        int status = jsonResponse.getInt("status");
        if (status == 200) {
          token = jsonResponse.getString("token");
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

          Date dueDate = dateFormat.parse(jsonResponse.getString("due_date"));

          // Save token in DB
          if (token != null && !token.isEmpty()) {
            tipParameters.setTokenValue(token);
          }
          if (dueDate != null) {
            tipParameters.setTokenDueDate(dueDate);
          }
          tipParametersManager.saveTipParameters(tipParameters);

          return SUCCESS;
        } else {
          System.out.println("Error: Unexpected status");
          return ERROR;
        }

      } else {
        return ERROR;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR;
    }
  }

  public String getToken() {
    return token;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public void prepare() throws Exception {
  }


  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public void setUrl(String url) {
    this.url = url;
  }
}
