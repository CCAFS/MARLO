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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HisotryComparator {


  public List<String> compareHistory(String jsonNew, String jsonOlder) {
    List<String> myDifferences = new ArrayList<>();
    Gson g = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    Map<String, Object> firstMap = g.fromJson(jsonNew.toString(), mapType);
    Map<String, Object> secondMap = g.fromJson(jsonOlder.toString(), mapType);
    MapDifference<String, Object> comparable = Maps.difference(firstMap, secondMap);
    Map<String, ValueDifference<Object>> diferences = comparable.entriesDiffering();
    Map<String, Object> diferencesLeft = comparable.entriesOnlyOnLeft();
    diferences.forEach((k, v) -> myDifferences.add(k));
    diferencesLeft.forEach((k, v) -> myDifferences.add(k));
    return myDifferences;
  }
}
