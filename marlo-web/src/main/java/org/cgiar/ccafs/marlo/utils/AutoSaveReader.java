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


package org.cgiar.ccafs.marlo.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia
 * @author Hermes Jimenez
 */
public class AutoSaveReader {

  public static Logger LOG = LoggerFactory.getLogger(AutoSaveReader.class);

  public AutoSaveReader() {
  }

  private HashMap<String, Object> convertJSONFormat(String json) {

    HashMap<String, Object> jsonNew = new HashMap<>();
    Gson gson = new Gson();
    LinkedTreeMap<String, Object> result = gson.fromJson(json, LinkedTreeMap.class);
    HashMap<String, Object> onetoMany = new HashMap<>();
    JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
    for (Map.Entry<String, Object> entry : result.entrySet()) {
      String value = entry.getValue().toString();
      String key = entry.getKey();
      if (!key.contains("[")) {
        String oneToManys[] = key.split("\\.");
        if (oneToManys.length > 1) {
          onetoMany.put(key, entry.getValue());
          jobj.remove(key);
        } else {
          if (!key.contains("__")) {
            jsonNew.put(key, value);
            jobj.remove(key);
          }

        }
      }
    }


    if (!onetoMany.isEmpty()) {
      jsonNew.putAll(this.getOneToMany(gson.toJson(onetoMany)));
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

  private HashMap<String, Object> getListJson(String keyParent, JsonObject jobj, int i) {
    HashMap<String, Object> jsonNew = new HashMap<>();
    Gson gson = new Gson();
    HashMap<String, Object> relations = new HashMap<>();
    HashMap<String, Object> onetoMany = new HashMap<>();

    LinkedTreeMap<String, Object> result = gson.fromJson(jobj, LinkedTreeMap.class);
    for (Map.Entry<String, Object> entry : result.entrySet()) {
      String key = entry.getKey().split("\\.")[0];
      if (key.equals(keyParent + "[" + i + "]")) {
        String newKey = entry.getKey().replace(keyParent + "[" + i + "].", "");


        if (!newKey.contains("[")) {
          String oneToManys[] = newKey.split("\\.");
          if (oneToManys.length > 1) {
            onetoMany.put(newKey, entry.getValue());
          } else {
            jsonNew.put(entry.getKey().replace(keyParent + "[" + i + "].", ""), entry.getValue());
            jobj.remove(key);
          }

        } else {

          relations.put(newKey, entry.getValue());
          jobj.remove(key);
        }
      }
    }
    if (!onetoMany.isEmpty()) {
      jsonNew.putAll(this.getOneToMany(gson.toJson(onetoMany)));
    }
    if (!relations.isEmpty()) {
      jsonNew.putAll(this.convertJSONFormat(gson.toJson(relations)));
    }
    return jsonNew;
  }


  private HashMap<String, Object> getListJsonParent(String keyParent, JsonObject jobj) {
    HashMap<String, Object> jsonNew = new HashMap<>();
    Gson gson = new Gson();
    int index = 0;
    LinkedTreeMap<String, Object> result = gson.fromJson(jobj, LinkedTreeMap.class);

    for (Map.Entry<String, Object> entry : result.entrySet()) {

      String key = entry.getKey().split("\\.")[0];

      String indexStr = key.split("\\[")[1].substring(0);
      String keyName = key.split("\\[")[0].substring(0);
      if (keyName.equals(keyParent)) {
        indexStr = indexStr.substring(0, indexStr.length() - 1);

        int tempIndex = Integer.parseInt(indexStr);
        if (tempIndex > index) {
          index = tempIndex;
        }
      }

    }
    List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    for (int i = 0; i <= index; i++) {
      list.add(this.getListJson(keyParent, jobj, i));
    }
    jsonNew.put(keyParent, list);
    return jsonNew;
  }

  private HashMap<String, Object> getOneToMany(String json) {
    Gson gson = new Gson();
    JsonObject jobj = gson.fromJson(json, JsonObject.class);
    HashMap<String, Object> jsonNew = new HashMap<>();
    LinkedTreeMap<String, Object> result = gson.fromJson(jobj, LinkedTreeMap.class);
    Set<String> listNames = new HashSet<>();
    HashMap<String, Object> onetoMany = new HashMap<>();
    for (Map.Entry<String, Object> entry : result.entrySet()) {
      String key = entry.getKey();

      String keys[] = key.split("\\.");
      String keyList = keys[0];

      listNames.add(keyList);
    }
    for (String name : listNames) {
      if (name.contains("flagshipValue")) {
        System.out.println("a");
      }
      HashMap<String, Object> relation = new HashMap<>();
      for (Map.Entry<String, Object> entry : result.entrySet()) {
        String key = entry.getKey();
        String keys[] = key.split("\\.");
        String keyList = keys[0];
        if (keys.length >= 3) {
          onetoMany = new HashMap<>();
          onetoMany.put(key.replaceAll(keyList + "\\." + keys[1] + "\\.", ""), entry.getValue());
          relation.put(keys[1], this.convertJSONFormat(gson.toJson(onetoMany)));
        } else {
          if (keyList.equals(name)) {

            relation.put(keys[1], entry.getValue());
            jobj.remove(key);


          }
        }
      }

      jsonNew.put(name, relation);

    }
    return jsonNew;
  }


  public Object readFromJson(JsonObject jobj) {

    Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
      .registerTypeAdapter(Long.class, new LongTypeAdapter()).registerTypeAdapter(Double.class, new DoubleTypeAdapter())
      .registerTypeAdapter(Float.class, new FloatTypeAdapter())
      .registerTypeAdapter(Number.class, new IntegerTypeAdapter())
      .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapter())
      .registerTypeAdapter(Date.class, new DateTypeAdapter()).registerTypeAdapter(String.class, new StringTypeAdapter())
      .create();
    HashMap<String, Object> jsonNew = this.convertJSONFormat(gson.toJson(jobj));

    jobj = gson.fromJson(gson.toJson(jsonNew), JsonObject.class);

    String className = jobj.get("className").getAsString();
    jobj.remove("className");

    try {
      Object obj = gson.fromJson(jobj, Class.forName(className));
      return obj;
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(e.getLocalizedMessage());

    }
    return null;


  }
}
