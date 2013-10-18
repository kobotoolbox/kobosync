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

/**
 * 
 */
package org.oyrm.kobo.postproc.data;

/**
 * BaseKoboConnection is an empty implementation
 * of org.oyrm.kobo.postproc.data.KoboConnection
 * note that this is implemented as a skeleton and is not used directly
 * as there is no data type to connect to for the base connection. It would
 * be possible to make this a KoboConnection reading and writing directly
 * to a data structure in memory
 * 
 * @author Gary Hendrick
 * @see org.oyrm.kobo.postproc.data.KoboConnection
 */
public class BaseKoboConnection implements KoboConnection {

	/**
	 * An empty constructor for BaseKoboConnection, nothing to be done. 
	 */
	public BaseKoboConnection() {
	}

	/* (non-Javadoc)
	 * @see org.oyrm.mojo.survey.data.KoboConnection#close()
	 */
	public void close() {
	}

	/* (non-Javadoc)
	 * @see org.oyrm.mojo.survey.data.KoboConnection#open(java.lang.String, int)
	 * @param String the instance name to open to
	 */
	public void open(String instance) {
	}
}
