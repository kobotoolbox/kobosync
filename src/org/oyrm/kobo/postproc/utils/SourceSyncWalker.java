/* 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.oyrm.kobo.postproc.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

/**
 * @author Gary Hendrick
 * @see org.apache.commons.io.DirectoryWalker
 */
public class SourceSyncWalker extends DirectoryWalker {

    public SourceSyncWalker() {
      super();
    }

    public SourceSyncWalker(FileFilter filter) {
        super(filter, -1);
    }
    
    public List<File> sync(File startDirectory) throws IOException {
      List<File> results = new ArrayList<File>();
      walk(startDirectory, results);
      /**START DEBUG BLOCK
      System.out.println("syncing directory : "+startDirectory.getAbsolutePath());
      for( File f : results) {
    	  System.out.println("\t\t"+f.getName());
      }
      **///END DEBUG BLOCK
      return results;
    }

    
	@SuppressWarnings("rawtypes")
	protected boolean handleDirectory(File file, int depth, Collection results) {
      	return true;
    }
    

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void handleFile(File file, int depth, Collection results) {
    	if (file == null || !file.exists() || file.length() <= 0
    			||!file.getName().toLowerCase().endsWith(".xml")) return;
    	results.add(file);
    }
  }
