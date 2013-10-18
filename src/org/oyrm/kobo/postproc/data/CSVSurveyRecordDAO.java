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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.oyrm.kobo.postproc.constants.Constants;

/**
 * The CSVSurveyRecordDAO is a fairly basic data access object which
 * is currently holding mostly non-functional methods. These methods would
 * generally be more useful with a different storage medium. However
 * in a CSV structure in which the objective is to maintain data without
 * altering it only the insert methods are necessary. With it in mind that
 * other SurveyRecordDAO implementation methods would use these methods, they are
 * included here but implemented to return only false
 * 
 * The insertSurveyRecord and unsertSurveyRecordList methods are implemented
 * to function as means of pushing new records into the CSV file.
 *  	 
 * @author gary
 */
public class CSVSurveyRecordDAO implements SurveyRecordDAO {
	private static Logger logger = Logger.getLogger("org.oyrm.kobo");
	private static FileHandler lh;
	private static Formatter lf;
	static {
		try {
			lh = new FileHandler(System.getProperty("user.home")
					+ File.separator + Constants.CONFIG_STORAGEDIR
					+ File.separator + "kobo.log", true);
			lf = new SimpleFormatter();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lh.setFormatter(lf);
		logger.addHandler(lh);
		try {
			logger.setLevel(Level.parse(System.getProperty(Constants.PROPKEY_LOGGING_LEVEL)));
		} catch(IllegalArgumentException ex) {
			logger.setLevel(Level.OFF);
			System.out.println("Logging function failed due to exception");
			System.out.println(ex.getMessage());
		}
	}
	private CsvConnection conn;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
    private Integer progress = 0;
    private Integer nCompleted = 0;

	/**
	 * @return the nTranscribed
	 */
	protected Integer getnCompleted() {
		return nCompleted;
	}

	/**
	 * @param nTranscribed the nTranscribed to set
	 */
	protected void setnCompleted(Integer nCompleted) {
		Integer oldnCompleted = this.nCompleted;
		this.nCompleted = nCompleted;
		pcs.firePropertyChange(Constants.CHANGEPROP_NAME_NCOMPLETED, oldnCompleted, this.nCompleted);
	}
	
	protected void setProgress(Integer progress) {
		Integer oldProgress = this.progress;
		this.progress = progress;
		pcs.firePropertyChange(Constants.CHANGEPROP_NAME_PROGRESS, oldProgress, this.progress);
	}

	protected Integer getProgress() {
		return progress;
	}

	/**
	 * Constructor method which gets the Singleton instance of KoboConnection 
	 * through the CSVFactory
	 */
	public CSVSurveyRecordDAO() {
        logger.entering(this.getClass().getName(), "CSVSurveyRecordDAO()");
		conn = (CsvConnection)CSVFactory.createConnection();
		logger.exiting(this.getClass().getName(), "CSVSurveyRecordDAO()");
	}
	
	/**
	 * This method is somewhat out of place as the iteration of instances
	 * for the CSV implementation is best done elsewhere.
	 * @param instance name to set
	 * @return false
	 * @throws IOException
	 */
	public boolean setInstance(String instance) throws IOException {
		logger.entering(
				this.getClass().getName(),
				"setInstance(String instance)",
				instance);
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.oyrm.mojo.survey.SurveyRecordDAO#deleteSurveyRecord(int)
	 */
	public boolean deleteSurveyRecord(String id) {
		logger.entering(
				this.getClass().getName(), 
				"deleteSurveyRecord",
				id);
		return false;
	}

	/* (non-Javadoc)
	 * @see org.oyrm.kobo.postproc.data.SurveyRecordDAO#findSurveyRecord(int)
	 */
	public SurveyRecord findSurveyRecord(String id) {
		logger.entering(
				this.getClass().getName(), 
				"findSurveyRecord",
				id);
		return null;
	}

	/**
	 * Because the CSV connection won't support insertion at arbitrary locations
	 * this method appends a SurveyRecord to the connection
	 * @param record a SurveyRecord containing data to be added to the persistent data collection
	 * @return The key of the record which has been successfully added, 
	 * 	otherwise Constants.RETURN_FAILED
	 * @see org.oyrm.kobo.postproc.constants.Constants
	 * @see org.oyrm.kobo.postproc.data.SurveyRecordDAO#insertSurveyRecord(java.util.Map)
	 */
	public String insertSurveyRecord(SurveyRecord record) {
		logger.entering(this.getClass().getName(), 
				"insertSurveyRecord", record);
		try {
			conn.open(record.getInstance());
			if(!conn.appendRecord(record)){
				return Constants.RETURN_FAILED;
			}
			conn.close();
		} catch (IOException ioex) {
			logger.warning(ioex.getMessage());
			return Constants.RETURN_FAILED;
		}
		return record.getKey();
	}
	
	/**
	 * Because the CSV connection won't support insertion at arbitrary locations
	 * this method iterates through a list and appends the SurveyRecord to the connection
	 * @param recodList a List<RecordList> containing records to be added to the persistent data collection
	 * @return True if successfully added, false otherwise
	 * @see org.oyrm.kobo.postproc.constants.Constants
	 * @see org.oyrm.kobo.postproc.data.SurveyRecordDAO#insertSurveyRecord(java.util.Map)
	 */
	public boolean insertSurveyRecordList(String instance, List<SurveyRecord> recordList) {
		logger.entering(this.getClass().getName(), 
				"insertSurveyRecordList", recordList);
		try {
			conn.open(instance);
			int totalSteps = recordList.size();
			float progressStep = (float)100/(float)totalSteps;
			float currentProgress = (float)getProgress();
			int nAppended = 0;
			for(SurveyRecord record : recordList){
				if(conn.appendRecord(record)) {
					nAppended ++;
				}
				currentProgress = currentProgress + progressStep;
				this.setProgress((int)currentProgress);
			}
			conn.close();
			this.setnCompleted(nAppended);
		} catch (IOException ioex) {
			logger.warning(ioex.getMessage());
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.oyrm.mojo.survey.SurveyRecordDAO#selectSurveyRecordList()
	 */
	public List<SurveyRecord> selectSurveyRecordList() {
		return null;
	}
	
	/**	The current set up does not indicate the use of updates
	 * 	We don't actually want to change data that exists in the CSV file
	 * 	we only want to insert new records
	 */
	public boolean updateSurveyRecord(SurveyRecord record) {
		return false;
	}

	public void addPropertyChangeListener(PropertyChangeListener pclistener) {
		this.pcs.addPropertyChangeListener(pclistener);
	}

	public void removePropertyChangeListener(PropertyChangeListener pclistener) {
		this.pcs.removePropertyChangeListener(pclistener);
	}
}