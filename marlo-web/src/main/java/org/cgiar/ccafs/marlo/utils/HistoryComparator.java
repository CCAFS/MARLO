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

import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.model.Auditlog;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.ibm.icu.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryComparator {


  private AuditLogManager auditlogManager;

  private Class<?> c;

  @Inject
  public HistoryComparator(AuditLogManager auditlogManager) {
    this.auditlogManager = auditlogManager;

  }


  private void addRelationField(Set<HistoryDifference> differencesUniques, Auditlog actual, Auditlog before,
    Auditlog principal, Map<String, String> specialList) throws ClassNotFoundException {
    Class<?> classRelation = Class.forName(actual.getEntityName().replace("class ", ""));
    String listName = this.getListName(classRelation);
    if (listName != null) {
      differencesUniques.add(new HistoryDifference(UUID.randomUUID().toString(), listName, true, "", ""));
    } else {
      if (actual != null && actual.getRelationName() != null) {
        String relationName = actual.getRelationName().replace(":" + principal.getEntityId(), "");
        if (specialList.containsKey(relationName)) {
          differencesUniques
            .add(new HistoryDifference(UUID.randomUUID().toString(), specialList.get(relationName), true, "", ""));
        }
      }


    }
  }

  private List<HistoryDifference> compareHistory(String jsonNew, String jsonOlder, String subFix) {
    List<HistoryDifference> myDifferences = new ArrayList<>();
    Gson g = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();


    JSONObject jsonObjNew = new JSONObject(jsonNew);

    JSONObject jsonObjOld = new JSONObject(jsonOlder);

    this.removeJSONField(jsonObjNew, "activeSince");
    this.removeJSONField(jsonObjOld, "activeSince");
    this.removeJSONField(jsonObjNew, "modifiedBy");
    this.removeJSONField(jsonObjOld, "modifiedBy");
    this.removeJSONField(jsonObjNew, "modificationJustification");
    this.removeJSONField(jsonObjOld, "modificationJustification");
    String subFixStr[] = subFix.split("\\.");
    for (String string : subFixStr) {
      this.removeJSONField(jsonObjNew, string);
      this.removeJSONField(jsonObjOld, string);
    }


    Map<String, Object> firstMap = g.fromJson(jsonObjNew.toString(), mapType);
    Map<String, Object> secondMap = g.fromJson(jsonObjOld.toString(), mapType);
    MapDifference<String, Object> comparable = Maps.difference(firstMap, secondMap);
    Map<String, ValueDifference<Object>> diferences = comparable.entriesDiffering();
    Map<String, Object> diferencesLeft = comparable.entriesOnlyOnLeft();
    diferences.forEach((k, v) -> myDifferences.add(new HistoryDifference(UUID.randomUUID().toString(), k, false,
      v.rightValue().toString(), v.leftValue().toString())));
    diferencesLeft.forEach((k, v) -> new HistoryDifference(UUID.randomUUID().toString(), k, true, "", ""));
    return myDifferences;
  }

  public List<HistoryDifference> getDifferences(String transactionID, Map<String, String> specialList, String subFix)
    throws ClassNotFoundException, NoSuchFieldException, SecurityException {

    Auditlog principal = auditlogManager.getAuditlog(transactionID);

    c = Class.forName(principal.getEntityName().replace("class ", ""));
    Set<HistoryDifference> differencesUniques = new HashSet<>();
    List<HistoryDifference> differences = new ArrayList<>();
    List<Auditlog> actualHistory = auditlogManager.getCompleteHistory(transactionID);
    List<Auditlog> beforeHistory = auditlogManager.getHistoryBefore(transactionID);

    if (!beforeHistory.isEmpty()) {
      for (Auditlog actual : actualHistory) {
        Auditlog before = this.getSimiliar(actual, beforeHistory);
        if (before == null) {
          this.addRelationField(differencesUniques, actual, before, principal, specialList);
        } else {
          switch (actual.getMain().intValue()) {
            case 1:
              differencesUniques.addAll(this.compareHistory(actual.getEntityJson(), before.getEntityJson(), subFix));
              break;

            case 3:

              List<HistoryDifference> diList =
                this.compareHistory(actual.getEntityJson(), before.getEntityJson(), subFix);
              if (!diList.isEmpty()) {
                this.addRelationField(differencesUniques, actual, before, principal, specialList);
              }


              break;

          }
        }
      }

    }


    for (HistoryDifference str : differencesUniques) {

      try {

        Field field = this.getField(str.getDifference());
        if (IAuditLog.class.isAssignableFrom(field.getType())) {
          str.setDifference(subFix + "." + str.getDifference() + ".id");
          str.setAdded(true);

          differences.add(str);
        } else {


          if (Date.class.isAssignableFrom(field.getType())) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
            SimpleDateFormat formatterSystem = new SimpleDateFormat("yyyy-MM-dd");
            str.setNewValue(formatterSystem.format(formatter.parse(str.getNewValue())));
            str.setOldValue(formatterSystem.format(formatter.parse(str.getOldValue())));

            str.setDifference(subFix + "." + str.getDifference());

          } else {
            str.setDifference(subFix + "." + str.getDifference());
          }


          differences.add(str);

        }
      } catch (Exception e) {
        str.setDifference(subFix + "." + str.getDifference());
        differences.add(str);
      }

    }
    return differences;

  }

  public List<HistoryDifference> getDifferencesList(IAuditLog iaAuditLog, String transactionID,
    Map<String, String> specialList, String subFix, String subFixDelete, int levels)
    throws ClassNotFoundException, NoSuchFieldException, SecurityException {
    List<HistoryDifference> differences = new ArrayList<>();
    Auditlog principal = auditlogManager.getAuditlog(transactionID, iaAuditLog);
    if (principal != null) {
      c = Class.forName(principal.getEntityName().replace("class ", ""));
      Set<HistoryDifference> differencesUniques = new HashSet<>();


      List<Auditlog> beforeHistory = auditlogManager.getHistoryBeforeList(transactionID,
        iaAuditLog.getClass().toString(), iaAuditLog.getId().toString());

      if (!beforeHistory.isEmpty()) {
        Auditlog actual = principal;
        {
          Auditlog before = this.getSimiliar(actual, beforeHistory);
          if (before == null) {
            differencesUniques.add(new HistoryDifference(UUID.randomUUID().toString(), "id", true, "", ""));
          } else {

            List<HistoryDifference> diffrencesFields =
              this.compareHistory(actual.getEntityJson(), before.getEntityJson(), subFixDelete);

            if (!diffrencesFields.isEmpty()) {
              differencesUniques.addAll(diffrencesFields);
              differencesUniques.add(new HistoryDifference(UUID.randomUUID().toString(), "id", true, "", ""));
            }


          }
        }

      } else {
        differencesUniques.add(new HistoryDifference(UUID.randomUUID().toString(), "id", true, "", ""));
      }

      String parent = "";
      try {
        String subFixSplit[] = subFix.split("\\.");

        for (int i = 0; i < levels && levels > 1; i++) {
          if (parent.isEmpty()) {
            parent = subFixSplit[i];
          } else {
            parent = parent + "." + subFixSplit[i];
          }
        }
      } catch (Exception e) {

      }
      for (HistoryDifference str : differencesUniques) {
        try {
          Field field = this.getField(str.getDifference());
          if (str.getDifference().equals("id")) {
            differences.add(new HistoryDifference(UUID.randomUUID().toString(), parent + ".id", true, "", ""));
          }
          if (IAuditLog.class.isAssignableFrom(field.getType())) {
            str.setDifference(subFix + "." + str.getDifference() + ".id");
            str.setAdded(true);

          } else {
            if (Date.class.isAssignableFrom(field.getType())) {
              SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
              SimpleDateFormat formatterSystem = new SimpleDateFormat("yyyy-MM-dd");
              str.setNewValue(formatterSystem.format(formatter.parse(str.getNewValue())));
              str.setOldValue(formatterSystem.format(formatter.parse(str.getOldValue())));

              str.setDifference(subFix + "." + str.getDifference());

            } else {
              str.setDifference(subFix + "." + str.getDifference());
            }

          }
        } catch (Exception e) {
          str.setDifference(subFix + "." + str.getDifference());
        }
        differences.add(str);

      }

    }

    return differences;
  }

  public Field getField(String str) {
    Field[] fields = c.getDeclaredFields();

    for (Field field : fields) {
      if (field.getName().equals(str)) {
        return field;
      }
    }
    return null;
  }

  private String getListName(Class<?> objectClass) {

    Field[] fields = c.getDeclaredFields();

    for (Field field : fields) {


      try {
        ParameterizedType integerListType = (ParameterizedType) field.getGenericType();
        Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
        if (integerListClass.equals(objectClass) && field.getType().getSimpleName().equals("List")) {
          return field.getName();
        }

      } catch (Exception e) {

      }
    }
    return null;
  }

  private Auditlog getSimiliar(Auditlog actual, List<Auditlog> beforeHistory) {
    for (Auditlog before : beforeHistory) {
      switch (actual.getMain().intValue()) {
        case 1:
          if (before.getMain().intValue() == 1) {
            return before;
          }
          break;

        case 3:
          if (before.getMain().intValue() == 3) {
            if (before.getRelationName().equals(actual.getRelationName())
              && before.getEntityId().equals(actual.getEntityId())) {
              return before;
            }
          }
          break;
      }

    }
    return null;

  }

  private void removeJSONField(JSONObject obj, String attribute) throws JSONException {
    obj.remove(attribute);

    Iterator<String> it = obj.keys();
    while (it.hasNext()) {
      String key = it.next();
      Object childObj = obj.get(key);
      if (childObj instanceof JSONArray) {
        JSONArray arrayChildObjs = ((JSONArray) childObj);
        int size = arrayChildObjs.length();
        for (int i = 0; i < size; i++) {
          this.removeJSONField(arrayChildObjs.getJSONObject(i), attribute);
        }
      }
      if (childObj instanceof JSONObject) {
        this.removeJSONField(((JSONObject) childObj), attribute);

      }

    }
  }
}
