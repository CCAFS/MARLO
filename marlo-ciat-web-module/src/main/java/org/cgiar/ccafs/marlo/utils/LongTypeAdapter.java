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
import java.text.NumberFormat;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class LongTypeAdapter extends TypeAdapter<Long> {

  @Override
  public Long read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }
    String stringValue = reader.nextString();
    // stringValue = stringValue.replaceAll(",", "");
    try {


      Long value = NumberFormat.getNumberInstance(java.util.Locale.US).parse(stringValue).longValue();

      return value;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public void write(JsonWriter writer, Long value) throws IOException {
    if (value == null) {
      writer.nullValue();
      return;
    }
    writer.value(value);
  }
}
