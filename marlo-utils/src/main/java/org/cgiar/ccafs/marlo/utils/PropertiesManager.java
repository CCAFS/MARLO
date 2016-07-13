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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Héctor Fabio Tobón R.
 * @author Hernán David Carvajal
 */
@Singleton
public class PropertiesManager {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(PropertiesManager.class);
  private static String PROPERTIES_FILE = "marlo.properties";
  private Properties properties;

  public PropertiesManager() {
    properties = new Properties();
    try {
      properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE));
    } catch (IOException e) {
      LOG.error(
        "The indicated file has not been found, file needed: \"" + new File(PROPERTIES_FILE).getAbsolutePath() + "\"",
        e);
      System.exit(-1);
    }
  }

  public boolean existProperty(String name) {
    return properties.get(name) != null;
  }

  public float getPropertiesAsFloat(String name) {
    return Float.parseFloat(this.getPropertiesAsString(name));
  }

  public float[] getPropertiesAsFloatArray(String name) {
    String[] str = this.getPropertiesAsString(name).split(";");
    float[] array = new float[str.length];
    for (int i = 0; i < str.length; i++) {
      array[i] = Float.parseFloat(str[i]);
    }

    return array;
  }

  public int getPropertiesAsInt(String name) {
    return Integer.parseInt(this.getPropertiesAsString(name));
  }

  public int[] getPropertiesAsIntArray(String name) {
    String[] str = this.getPropertiesAsString(name).split(";");
    int[] array = new int[str.length];
    for (int i = 0; i < str.length; i++) {
      array[i] = Integer.parseInt(str[i]);
    }

    return array;
  }

  public String getPropertiesAsString(String name) {
    return properties.getProperty(name);
  }

  public String[] getPropertiesAsStringArray(String name) {
    return this.getPropertiesAsString(name).split(";");
  }


}
