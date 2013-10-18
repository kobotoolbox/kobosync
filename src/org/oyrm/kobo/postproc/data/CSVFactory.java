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
package org.oyrm.kobo.postproc.data;

/**
 * CSVFactory extends PersistenceFactory, returning a copy of the
 * KoboConnection implementation CsvConnection
 * @author gary
 */
public class CSVFactory extends PersistenceFactory {
	
	/**
	 * Returns the singleton instance of the CsvConnection 
	 * @return KoboConnection
	 */
	public static KoboConnection createConnection() {
		return CsvConnection.getInstance();
	}
	
	/**
	 * 
	 * @return SurveyRecordDAO 
	 */
	public SurveyRecordDAO getSurveyRecordDAO() {
		return new CSVSurveyRecordDAO();
	}
}
