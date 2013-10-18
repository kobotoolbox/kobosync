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

package org.oyrm.kobo.postproc.constants;

import java.util.regex.Pattern;

/**
 * Constants contains Strings and Patterns used globally throughout
 * the package org.oyrm.kobo.postproc
 * @author Gary Hendrick
 */
public final class Constants {
	public static final String KEY_KEY ="key";
	public static final String KEY_INSTANCE="instance";
	public static final String KEY_DEVICEID="deviceid";
	public static final String KEY_START="start";
	
	public static final String FILENAME_ODK_DELIM = "_";
	public static final String FILENAME_CSV_EXTENSION = ".csv";
	public static final String ENCODING_CSV = "UTF-8";
	public static final String STRING_CSV_TEMPFILE_PREFIX = "kobo_";
	public static final String STRING_CSV_TEMPFILE_SUFFIX = ".tmp";
	
	public static final String FILENAME_KOBO_ICON = "launcher-kobo-48.png";

	public static final String RETURN_FAILED = new Integer(-1).toString();
	public static final Pattern REGEX_FILENAME_DATEFORMAT = Pattern.compile("20\\d\\d-[0-1]\\d-[0-3]\\d_[0-1]\\d-[0-6]\\d-[0-6]\\d");
	
	//Property Keys & Configuration
	public static final String CONFIG_PROPSRESOURCE=new String("org/oyrm/kobo/postproc/resources/KoboPostProcFrame.properties");
	public static final String CONFIG_STORAGEDIR=new String(".kobo");
	public static final String CONFIG_PROPSFILE=new String("kobo.properties");
	public static final String PROPKEY_DIRNAME_CSV="PROPKEY_DIRNAME_CSV";
	public static final String PROPKEY_DIRNAME_XML_STORAGE="PROPKEY_DIRNAME_XML_STORAGE";
	public static final String PROPKEY_DIRNAME_XML_DEV="PROPKEY_DIRNAME_XML_DEV";
	public static final String PROPKEY_LOGGING_LEVEL = "PROPKEY_LOGGING_LEVEL";
	
	public static final String CHANGEPROP_NAME_PROGRESS	= "progress";
	public static final String CHANGEPROP_NAME_STATE	= "state";
	public static final String CHANGEPROP_NAME_NCOMPLETED	= "ncompleted";
	
	public static final String TEXT_MENUITEM_OPTION = "Options";
	public static final char MNEMONIC_MENUITEM_OPTION = 'O';
	public static final String TEXT_MENUITEM_OPTIONACTION = "Set Options";
	public static final char MNEMONIC_MENUITEM_OPTIONACTION = 'S';
	public static final String TEXT_MENUITEM_EXITACTION = "Exit";
	public static final char MNEMONIC_MENUITEM_EXITACTION = 'x';
	public static final String STRING_NODIR_MESSAGE_SOURCE = "The %1$s does not exist. \n"+
																"This may occur if you have disconnected the device \n" +
																"from your computer. The application can recheck the \n" +
																"directory now if you select \"Retry\". \n" +
																"Otherwise, select \"Set\" to change the \n" +
																"%1$s location \n"+
																"Current Directory %2$s";
	public static final String STRING_NODIR_XML = "Survey XML storage directory";
	public static final String STRING_NODIR_SRC = "Survey source directory";
	public static final String MULTI_TAG = "_MULTI_";
	public static final String STRING_DELIM_GROUPFROMCHILD = "::";
	
	public final static int XML_AGGREGATE_COMMAND = 0; //new String("Aggregate");
	public final static int CSV_CONVERT_COMMAND = 1; //new String("Convert to CSV");
	public final static int STATUS_INIT = 2; //new String("Ready");
	public final static int COUNTER_SYNC_TEXT = 3; //new String("Surveys Aggregated : %d");  //  @jve:decl-index=0:
	public final static int COUNTER_TRANS_TEXT = 4; //new String("Surveys Converted to CSV : %d");  //  @jve:decl-index=0:
	public final static int BROWSE_TEXT = 5; 
	public final static int CHANGE_CSV_DIR_TEXT = 6;
	public final static int CHANGE_SRC_DIR_TEXT = 7;
	public final static int CHANGE_XML_DIR_TEXT = 8;
	public final static int CSV_CONVERT_PROC_COMPLETE_TEXT = 9;
	public final static int TASK_COMPLETED_TEXT = 10;
	public final static int COMPLETED_PERCENT_TEXT = 11;
	public final static int XML_AGGREGATE_COMPLETE_TEXT = 12;
	public final static int RETRY_TEXT = 13;
	public final static int SET_TEXT = 14;
	public final static int STRING_NODIR_MESSAGE = 15;
	public final static int STRING_NODIR_CSV = 16;
	public final static int STRING_NODIR_TITLE = 17;
	public final static int STARTING_TEXT = 18;
	public final static int WRITING_XML_TO_STORAGE = 19;
	public final static int CONVERT_TO_CSV_TASK_TEXT = 20;
	public final static int AGGREGATE_XML_TASK_TEXT = 21;
	public final static int DIR_PREF_SET_TEXT = 22;
	public final static int STATUS_TEXT = 23;
	public final static int CONVERT_TO_CSV_TEXT = 24;
	public final static int AGGREGATE_XML_TEXT = 25;
	public final static int COUNTER_TEXT = 26;
	public final static int SURVEY_INSTANCES_TEXT = 27;
	public final static int SAVE_TO_CSV_TEXT = 28;
	public final static int AGGREGATE_TO_TEXT = 29;
}
