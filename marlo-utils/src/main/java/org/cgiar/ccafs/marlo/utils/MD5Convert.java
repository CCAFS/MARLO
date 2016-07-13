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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MD5Convert {

  private static Logger LOG = LoggerFactory.getLogger(MD5Convert.class);

  public static String stringToMD5(String value) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
      byte[] b = md.digest(value.getBytes());
      String md5HashCode = "";
      for (byte element : b) {
        md5HashCode += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
      }
      return md5HashCode;
    } catch (NoSuchAlgorithmException e) {
      LOG.error("There was a problem trying to encript the string. ", e);
    }
    return null;
  }
}
