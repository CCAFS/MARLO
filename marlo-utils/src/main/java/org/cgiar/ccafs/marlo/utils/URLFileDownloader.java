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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to download a file from an URL.
 * 
 * @author Héctor Fabio Tobón R. - CIAT/CCAFS
 */
public class URLFileDownloader {

  private static Logger LOG = LoggerFactory.getLogger(URLFileDownloader.class);

  /**
   * This method gets the file coming from an URL and convert it to a byte array.
   * 
   * @param url where the file is located.
   * @return a Map with the properties of the file. Please use the following keys:
   *         'byte_array' --> Byte, 'filename' and 'mime_type'.
   * @throws IOException if some error occur.
   */
  public static Map<String, Object> getAsByteArray(URL url) throws IOException {
    Map<String, Object> fileProperties = new HashMap();

    URLConnection connection = url.openConnection();
    // Since you get a URLConnection, use it to get the InputStream

    InputStream in = connection.getInputStream();
    // Now that the InputStream is open, get the content length
    int contentLength = connection.getContentLength();

    // Getting the filename
    String raw = connection.getHeaderField("Content-Disposition");
    // raw should be something like: "attachment; filename=abc.jpg"
    if (raw != null && raw.indexOf("=") != -1) {
      String fileName = raw.split("=")[1]; // getting value after '='
      fileProperties.put("filename", fileName);
    } else {
      // fall back to a specific generated file name?
      fileProperties.put("filename", "attached-file");
    }

    fileProperties.put("mime_type", connection.getContentType());


    // To avoid having to resize the array over and over and over as
    // bytes are written to the array, provide an accurate estimate of
    // the ultimate size of the byte array
    ByteArrayOutputStream tmpOut;
    if (contentLength != -1) {
      tmpOut = new ByteArrayOutputStream(contentLength);
    } else {
      tmpOut = new ByteArrayOutputStream(16384); // Pick some appropriate size
    }
    byte[] buf = new byte[512];
    while (true) {
      int len = in.read(buf);
      if (len == -1) {
        break;
      }
      tmpOut.write(buf, 0, len);
    }
    in.close();
    tmpOut.close(); // No effect, but good to do anyway to keep the metaphor alive

    byte[] array = tmpOut.toByteArray();

    // Lines below used to test if file is corrupt
    // FileOutputStream fos = new FileOutputStream("C:\\abc.pdf");
    // fos.write(array);
    // fos.close();

    fileProperties.put("byte_array", ByteBuffer.wrap(array));

    return fileProperties;
  }

}
