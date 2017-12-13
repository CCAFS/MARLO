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

package org.cgiar.ccafs.marlo.action;

import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocGeoposition;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MapGeolocation extends BaseAction {


  private static final long serialVersionUID = -5001936842420166300L;


  private LocElementManager locElementManager;

  @Inject
  public MapGeolocation(APConfig config, LocElementManager locElementManager) {
    super(config);
    this.locElementManager = locElementManager;
  }

  @Override
  public String execute() throws Exception {
    return SUCCESS;
  }

  @Override
  public void prepare() throws Exception {
    List<LocElement> locElements = new ArrayList<>(locElementManager.findAll().stream()
      .filter(le -> le.isActive() && le.getLocElementType().getId() > 2 && le.getLocElement() == null)
      .collect(Collectors.toList()));

    for (LocElement locElement : locElements) {
      LocGeoposition geoposition = locElement.getLocGeoposition();
      String lat = String.valueOf(geoposition.getLatitude());
      String lon = String.valueOf(geoposition.getLongitude());

      String sUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&key="
        + config.getGoogleApiKey();
      try {
        URL url = new URL(sUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
          System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

        StringBuilder jsonNew = new StringBuilder();
        String output;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
          jsonNew.append(output);
        }
        conn.disconnect();
        Gson g = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();


        Map<String, Object> firstMap = g.fromJson(jsonNew.toString(), mapType);
        if (firstMap.get("status").toString().equals("OK")) {
          List<Map<String, Object>> resssss = (List<Map<String, Object>>) firstMap.get("results");
          Map<String, Object> res = resssss.get(0);
          List<Map<String, Object>> adress = (List<Map<String, Object>>) res.get("address_components");;
          for (Map<String, Object> map : adress) {
            if (map.get("types").toString().contains("country")) {
              String iso2 = map.get("short_name").toString();
              LocElement element = locElementManager.getLocElementByISOCode(iso2);
              if (element != null) {
                locElement.setLocElement(element);
                locElementManager.saveLocElement(locElement);
              }
            }
          }
        }

      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


}
