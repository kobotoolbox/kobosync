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
package org.oyrm.kobo.postproc.fileutils;

import java.io.File;

/**
 * FUtil provides some support for file system operations
 * @author Gary Hendrick
 */

public class FUtil {
	
	/**
	 * check for a parent directory
	 * @param file instance to check
	 * @return True if the parent directory's File instance exists, otherwise false
	 */
	public static boolean hasParent(File file) {
		if(file.getParentFile() == null)
			return false;
		return file.getParentFile().exists();
	}
	
	/**
	 * A little help in finding an existing parent directory which works even if the 
	 * file and direct parent directory do not exist.
	 * 
	 * The null test against the file prevents errors on windows for unmounted
	 * directories.
	 * @param file the File object to work out
	 * @return File instance of the existing parent
	 */
	public static File getRealParent(File file) {
		while(file != null && !hasParent(file)) {
			file = file.getParentFile();
		}
		if(file==null) {
			return new File(System.getProperty("user.home"));
		}
		return file.getParentFile();
	}

}
