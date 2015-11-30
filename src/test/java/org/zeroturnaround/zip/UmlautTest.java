package org.zeroturnaround.zip;
/**
 *    Copyright (C) 2012 ZeroTurnaround LLC <support@zeroturnaround.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

public class UmlautTest extends TestCase {
  private static final File file = new File("src/test/resources/umlauts-o\u0308a\u0308s\u030c.zip");
  // See StackOverFlow post why I'm not using just unicode
  // http://stackoverflow.com/questions/6153345/different-utf8-encoding-in-filenames-os-x/6153713#6153713
  private static final List fileContents = new ArrayList() {
    {
      add("umlauts-öäš/");
      add("umlauts-öäš/Ro\u0308mer.txt"); // Römer - but using the escape code that HFS uses
      add("umlauts-öäš/Raudja\u0308rv.txt"); // Raudjärv - but escape code from HFS
      add("umlauts-öäš/S\u030celajev.txt"); // Šelajev - but escape code from HFS
    }
  };

  public boolean ignoreTestIfJava6() {
    return (System.getProperty("java.version").startsWith("1.6"));
  }

  public void testIterateWithCharset() throws Exception {
    if (ignoreTestIfJava6()) {
      return;
    }

    FileInputStream fis = new FileInputStream(file);

    ZipUtil.iterate(fis, new ZipEntryCallback() {
      public void process(InputStream in, ZipEntry zipEntry) throws IOException {
        assertTrue(zipEntry.getName(), fileContents.contains(zipEntry.getName()));
      }
    }, Charset.forName("UTF8"));
  }

  public void testIterateWithEntryNamesAndCharset() throws Exception {
    if (ignoreTestIfJava6()) {
      return;
    }

    FileInputStream fis = new FileInputStream(file);

    String[] entryNames = (String[]) fileContents.toArray(new String[] {});
    ZipUtil.iterate(fis, entryNames, new ZipEntryCallback() {
      public void process(InputStream in, ZipEntry zipEntry) throws IOException {
        assertTrue(zipEntry.getName(), fileContents.contains(zipEntry.getName()));
      }
    }, Charset.forName("UTF8"));
  }

  public void testZipFileGetEntriesWithCharset() throws Exception {
    if (ignoreTestIfJava6()) {
      return;
    }

    ZipFile zf = ZipFileUtil.getZipFile(file, Charset.forName("UTF8"));
    Enumeration entries = zf.entries();
    while (entries.hasMoreElements()) {
      ZipEntry ze = (ZipEntry) entries.nextElement();
      assertTrue(ze.getName(), fileContents.contains(ze.getName()));
    }
  }
}
