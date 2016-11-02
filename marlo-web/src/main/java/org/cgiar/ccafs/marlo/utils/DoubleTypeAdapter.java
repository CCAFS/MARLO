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

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DoubleTypeAdapter extends TypeAdapter<Double> {

  @Override
  public Double read(JsonReader jsonReader) throws IOException {
    if (jsonReader.peek() == JsonToken.NULL) {
      jsonReader.nextNull();
      return null;
    }

    try {
      String value = jsonReader.nextString();
      if (value.contains("%")) {
        value = value.replace("%", "");
      }

      if (value.contains(".00")) {
        value = value.replace(".00", "").replace(",", "");
      }
      if ("".equals(value)) {
        return new Double(0);
      }
      return Double.valueOf(Double.parseDouble(value));
    } catch (NumberFormatException e) {
      return new Double(0);
    }
  }

  @Override
  public void write(JsonWriter out, Double value) throws IOException {


  }

}
