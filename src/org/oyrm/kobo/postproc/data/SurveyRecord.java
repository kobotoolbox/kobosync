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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.oyrm.kobo.postproc.constants.Constants;

/**
 * The SurveyRecord class encapsulates a map of the survey content and handles 
 * a few functional concerns
 * @author Gary Hendrick
 */

public class SurveyRecord {
	private Map<String, String> content;
	private Map<String, Integer> basekeynames;

	public SurveyRecord() {
		super();
		content = new LinkedHashMap<String, String>();
		basekeynames = new LinkedHashMap<String, Integer>();
	}

	public void clear() {
		content.clear();
		return;
	}

	/**
	 * needs implementation in ODK-Collect to record a key value
	 * 
	 * @return the key
	 */
	public String getKey() {
		if (content.containsKey(Constants.KEY_KEY)) {
			return content.get(Constants.KEY_KEY);
		} else if (content.containsKey(Constants.KEY_DEVICEID)
				&& content.containsKey(Constants.KEY_START)) {
			return content.get(Constants.KEY_DEVICEID) + "_"
					+ content.get(Constants.KEY_START);
		} else {
			return "";
		}
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.content.put(Constants.KEY_KEY, key);
	}

	/**
	 * @return the key
	 */
	public String getInstance() {
		if (content.containsKey(Constants.KEY_INSTANCE)) {
			return content.get(Constants.KEY_INSTANCE);
		} else {
			return null;
		}
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setInstance(String instance) {
		this.content.put(Constants.KEY_INSTANCE, instance);
	}

	/**
	 * @return the content
	 */
	public Map<String, String> getMap() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setMap(Map<String, String> content) {
		this.content = content;
	}

	/**
	 * @return the questionIDs
	 */
	public Collection<String> getQuestionIDs() {
		return content.keySet();
	}

	/**
	 * @return the answers
	 */
	public Collection<String> getAnswers() {
		return content.values();
	}

	/**
	 * @return the answers
	 */
	public String getAnswer(String questionID) {
		return content.get(questionID);
	}

	/**
	 * The set answer function will set the answer into the SurveyRecord
	 * @param questionId
	 *            is the questionID to use for this addition
	 * @param answer
	 *            answer is the value to save with this questionId
	 * @return the answers
	 */
	public String setAnswer(String questionID, String answer) {
		if (questionID.equals(Constants.KEY_DEVICEID)
				&& content.containsKey(Constants.KEY_START)) {
			String value = answer + "_" + content.get(Constants.KEY_START);
			content.put(Constants.KEY_KEY, value);
		} else if (questionID.equals(Constants.KEY_START)
				&& content.containsKey(Constants.KEY_DEVICEID)) {
			String value = content.get(Constants.KEY_DEVICEID) + "_" + answer;
			content.put(Constants.KEY_KEY, value);
		}
		return content.put(questionID, answer);
	}

	public String toString() {
		return "\n" + getClass().getName() + "\n@{Instance=" + getInstance()
				+ "  Key =" + getKey() + "\n" + content.toString() + "}\n";
	}
}