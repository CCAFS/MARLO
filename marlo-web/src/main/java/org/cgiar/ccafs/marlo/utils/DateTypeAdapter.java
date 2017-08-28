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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class DateTypeAdapter extends TypeAdapter<Date> {

  private final DateFormat enUsFormat =
    DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
  private final DateFormat localFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);
  private final String pattern = "MMM-dd-yyyy";

  private final SimpleDateFormat foDateFormat = new SimpleDateFormat(pattern, Locale.US);

  private synchronized Date deserializeToDate(String json) {
    try {
      return localFormat.parse(json);
    } catch (ParseException ignored) {
    }
    try {
      String dateAux = json.replaceAll(", ", " ").replaceAll(" ", "-");
      Date date = foDateFormat.parse(dateAux);

      return date;
    } catch (Exception ignored) {

    }
    try {
      return enUsFormat.parse(json);
    } catch (ParseException ignored) {
    }
    try {
      return ISO8601Utils.parse(json, new ParsePosition(0));
    } catch (ParseException e) {
      return new Date();
    }
  }

  @Override
  public Date read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    return this.deserializeToDate(in.nextString());
  }

  @Override
  public synchronized void write(JsonWriter out, Date value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }
    String dateFormatAsString = enUsFormat.format(value);
    out.value(dateFormatAsString);
  }

}
