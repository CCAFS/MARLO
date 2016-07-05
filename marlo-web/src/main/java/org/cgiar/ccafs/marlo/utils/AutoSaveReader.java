/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

/**
 * @author Christian Garcia
 * @author Hermes Jimenez
 */
public class AutoSaveReader {

  public HashMap<String, Object> convertJSONFormat(String json) {

    HashMap<String, Object> jsonNew = new HashMap<>();
    Gson gson = new Gson();
    LinkedTreeMap<String, Object> result = gson.fromJson(json, LinkedTreeMap.class);

    JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
    for (Map.Entry<String, Object> entry : result.entrySet()) {
      String value = entry.getValue().toString();
      String key = entry.getKey();
      if (!key.contains("[")) {
        jsonNew.put(key, value);
        jobj.remove(key);
      }
    }

    result = gson.fromJson(jobj, LinkedTreeMap.class);

    Set<String> listNames = new HashSet<>();
    for (Map.Entry<String, Object> entry : result.entrySet()) {

      String key = entry.getKey();
      String keys[] = key.split("\\.");
      String keyList = keys[0];
      keyList = keyList.split("\\[")[0];

      listNames.add(keyList);

    }

    for (String keyList : listNames) {

      HashMap<String, Object> list = this.getListJsonParent(keyList, jobj);
      if (list.size() > 0) {
        jsonNew.putAll(list);
      }
    }


    return jsonNew;
  }


  public HashMap<String, Object> getListJson(String keyParent, JsonObject jobj, int i) {
    HashMap<String, Object> jsonNew = new HashMap<>();
    Gson gson = new Gson();
    HashMap<String, Object> relations = new HashMap<>();
    LinkedTreeMap<String, Object> result = gson.fromJson(jobj, LinkedTreeMap.class);
    for (Map.Entry<String, Object> entry : result.entrySet()) {
      String key = entry.getKey().split("\\.")[0];
      if (key.equals(keyParent + "[" + i + "]")) {
        String newKey = entry.getKey().replace(keyParent + "[" + i + "].", "");
        if (!newKey.contains("[")) {
          jsonNew.put(entry.getKey().replace(keyParent + "[" + i + "].", ""), entry.getValue());
          jobj.remove(key);
        } else {
          relations.put(newKey, entry.getValue());
          jobj.remove(key);
        }


      }
    }

    if (!relations.isEmpty()) {
      jsonNew.putAll(this.convertJSONFormat(gson.toJson(relations)));
    }
    return jsonNew;
  }

  public HashMap<String, Object> getListJsonParent(String keyParent, JsonObject jobj) {

    HashMap<String, Object> jsonNew = new HashMap<>();
    Gson gson = new Gson();
    int index = 0;
    LinkedTreeMap<String, Object> result = gson.fromJson(jobj, LinkedTreeMap.class);
    for (Map.Entry<String, Object> entry : result.entrySet()) {
      String key = entry.getKey().split("\\.")[0];
      String indexStr = key.split("\\[")[1].substring(0);
      indexStr = indexStr.substring(0, indexStr.length() - 1);
      int tempIndex = Integer.parseInt(indexStr);
      if (tempIndex > index) {
        index = tempIndex;
      }
    }
    List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    for (int i = 0; i <= index; i++) {
      list.add(this.getListJson(keyParent, jobj, i));
    }


    jsonNew.put(keyParent, list);
    return jsonNew;
  }


  public Object readFromJson(String json) {
    return null;
  }


}
