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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileManager {

  private static Logger LOG = LoggerFactory.getLogger(FileManager.class);

  /**
   * Copy one file to another location
   *
   * @param source specifies the file to be copied
   * @param destination specifies the directory and filename for the
   *        new file
   * @return true if the process of copy was successful. False otherwise
   */
  public static boolean copyFile(File source, String destination) {
    File destinationFile = new File(destination);
    try {
      FileUtils.copyFile(source, destinationFile);
    } catch (IOException e) {
      String msg = "There was an error copying file from " + source + " to " + destination;
      LOG.error(msg, e);
      return false;
    }
    return true;
  }

  /**
   * Delete one file from the hard disk
   *
   * @param fileName specifies the file to be deleted.
   * @return true if the delete process was successful. False otherwise
   */
  public static boolean deleteFile(String fileName) {
    // First, load the image as object
    File deleteFile = new File(fileName);
    // Make sure the file or directory exists and isn't write protected
    if (!deleteFile.exists()) {
      LOG.warn("Delete: no such file or directory: " + fileName);
      // Return true because the file doesn't exists
      return true;
    }
    if (!deleteFile.canWrite()) {
      LOG.error("Delete: write protected: " + fileName);
      return false;
    }
    // Attempt to delete it
    return deleteFile.delete();
  }

  public static byte[] readURL(URL url) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    InputStream is = null;
    try {
      HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
      is = con.getInputStream();
      byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
      int n;

      while ((n = is.read(byteChunk)) > 0) {
        baos.write(byteChunk, 0, n);
      }
      return byteChunk;
    } catch (IOException e) {
      System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
      e.printStackTrace();
      // Perform any other exception handling that's appropriate.
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {

          e.printStackTrace();
        }
      }
    }
    return null;

  }
}
