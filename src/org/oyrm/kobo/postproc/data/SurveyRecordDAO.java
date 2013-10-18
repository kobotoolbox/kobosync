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
 * The SurveyRecordDAO is patterned from the FactoryMethodPattern (GoF) 
 * 
 * SurveyRecordDAO provides an interface for interacting with implementations
 * providing access to various persistent storage mediums.
 */
package org.oyrm.kobo.postproc.data;

import java.beans.PropertyChangeListener;
import java.util.List;
/**
 * 
 * @author Gary Hendrick
 */
public interface SurveyRecordDAO {
	
	/**
	 * @param record a SurveyRecord
	 * @return the survey record id or -1 on error
	 */
	public String insertSurveyRecord(SurveyRecord record);
	
	/**
	 * @param false if not successful
	 * @return true if successful
	 */	
	public boolean insertSurveyRecordList(String instance, List<SurveyRecord> recordList);

	/**
	 * @param id
	 * @return true on success, false on failure
	 */
	public boolean deleteSurveyRecord(String id);
	
	/**
	 * @param id
	 * @return the requested SurveyRecord or an empty survey record on failure
	 */
	public SurveyRecord findSurveyRecord(String id);
	
	/**
	 * @param SurveyRecord
	 * @return true on success, false on failure
	 */
	public boolean updateSurveyRecord(SurveyRecord record);
	
	/**
	 * @return the full list of records
	 */
	public List<SurveyRecord> selectSurveyRecordList();
	
	public void addPropertyChangeListener(PropertyChangeListener pclistener);
	public void removePropertyChangeListener(PropertyChangeListener pclistener);
}