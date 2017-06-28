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

package org.cgiar.ccafs.marlo.converter;


import org.cgiar.ccafs.marlo.utils.APConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import org.apache.struts2.util.StrutsTypeConverter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DateConverter extends StrutsTypeConverter {

  @SuppressWarnings("rawtypes")
  @Override
  public Object convertFromString(Map context, String[] values, Class toClass) {
    if (values != null && values.length > 0 && values[0] != null && values[0].length() > 0) {
      SimpleDateFormat sdf = new SimpleDateFormat(APConstants.DATE_FORMAT);
      try {
        return sdf.parse(values[0]);
      } catch (ParseException e) {
        throw new TypeConversionException(e);
      }
    }
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public String convertToString(Map context, Object o) {
    Date date = (Date) o;
    if (date == null) {
      // If there isn't an object return an empty string
      return "";
    }
    return new SimpleDateFormat(APConstants.DATE_FORMAT).format(date);
  }

}
