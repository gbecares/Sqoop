/*
 * Copyright (C) 2016 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.test.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility methods to work with compressed files and stream.
 */
public class CompressionUtils {

  private static final Logger LOG = Logger.getLogger(CompressionUtils.class);

  /**
   * Untar given stream (tar.gz archive) to given directory.
   *
   * Directory structure will be preserved.
   *
   * @param inputStream InputStream of tar.gz archive
   * @param targetDirectory Target directory for tarball content
   * @throws IOException
   */
  public static void untarStreamToDirectory(InputStream inputStream, String targetDirectory) throws IOException {
    assert inputStream != null;
    assert targetDirectory != null;

    LOG.info("Untaring archive to directory: " + targetDirectory);

    TarArchiveInputStream in = new TarArchiveInputStream(new GzipCompressorInputStream(inputStream));
    TarArchiveEntry entry = null;

    int BUFFER_SIZE = 2048;

    while ((entry = (TarArchiveEntry) in.getNextEntry()) != null) {
      LOG.info("Untaring file: " + entry.getName());

      if (entry.isDirectory()) {
        File targetFile = new File( HdfsUtils.joinPathFragments(targetDirectory, entry.getName()));
        if (!targetFile.mkdirs()) {
          LOG.warn("Failed to create folder:" + targetFile.getAbsolutePath());
        }
      } else {
        int count;
        byte data[] = new byte[BUFFER_SIZE];

        FileOutputStream fos = new FileOutputStream(HdfsUtils.joinPathFragments(targetDirectory, entry.getName()));
        try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE)) {
          while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            dest.write(data, 0, count);
          }
        }
      }
    }
    in.close();
  }

  private CompressionUtils() {
    // No instantiation
  }
}
