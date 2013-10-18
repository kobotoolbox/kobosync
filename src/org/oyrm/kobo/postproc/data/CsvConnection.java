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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.oyrm.kobo.postproc.constants.Constants;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.StrReplace;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * The CsvConnection Class is an implementation of KoboConnection which deals
 * specifically with connections to CSV files. In truth, KoboConnection is an
 * overly simple contract for these interactions.
 * 
 * I've used the SuperCSV package in order to deal with these connections. The
 * class will operate within the directory indicated in the system properties,
 * accessed through System.getProperty(Constants.PROPKEY_DIRNAME_CSV). That
 * directory will be read for .csv files only and the read operation will not be
 * recursive.
 * 
 * The class has the following restrictions A connection has a CSV_DIRECTORY and
 * can read the csv files in that directory only, this is
 * System.getProperty(Constants.PROPKEY_DIRNAME_CSV) A connection can look at
 * that directory without actually opening a connection to an instance A
 * connection cannot interact with a specific instance csv file until a
 * connection to that instance is open using the open() method
 * 
 * The class provides the following functionality work within the CSV directory
 * specified in Constants.CSV_DIRECTORY find and list instance names for all
 * CsvFiles in Constants.CSV_DIRECTORY allow clients to create a connection to a
 * csv file by survey instance name if the file does not already exist, then
 * ensure its creation allow the client to retrieve a File object of the current
 * survey instance generate a listing of all existing survey keys within the
 * survey instance allow clients to close a connection to a csv file allow
 * clients to change from read mode to write mode to read-write mode read mode -
 * retrieve a reader write mode - retrieve a writer read-write mode -
 * unimplemented access an org.supercsv.io.CsvMapReader in read mode access an
 * org.supercsv.io.CsvMapWriter in write mode
 * 
 * 
 * @author Gary Hendrick
 * @see org.oyrm.kobo.postproc.constants.Constants
 */

public class CsvConnection implements KoboConnection {
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
		logger.setLevel(Level.parse(System
				.getProperty(Constants.PROPKEY_LOGGING_LEVEL)));
		} catch(IllegalArgumentException ex) {
			logger.setLevel(Level.OFF);
			System.out.println("Logging function failed due to exception");
			System.out.println(ex.getMessage());
		}
	}
	private static final CsvConnection INSTANCE = new CsvConnection();
	private boolean isOpen = false;
	private CsvMapReader reader;
	private CsvMapWriter writer;
	private String surveyInstance;
	private File surveyInstanceFile, csvRootDir, tempFile;
	private List<String> surveyInstances, instanceSurveyKeys;
	private String[] CsvHeaders, newCsvHeaders;
	private List<Map<String, String>> extantData, newData;

	/**
	 * Constructor for the class. Responsible for setting the CSV storage
	 * directory. Furthermore, it generates a list of survey instance csv files
	 * in that directory
	 */
	private CsvConnection() {
		super();
		logger.entering(this.getClass().getName(), "CsvConnection()");
		csvRootDir = new File(System.getProperty(Constants.PROPKEY_DIRNAME_CSV));
		if (!csvRootDir.exists()) {
			csvRootDir.mkdir();
		}
		populateCsvInstances();
		logger.exiting(this.getClass().getName(), "CsvConnection()");
	}

	/**
	 * Returns the survey instance to which this connection is currently set
	 * 
	 * @return CSVConnection at use
	 */

	public static final CsvConnection getInstance() {
		return INSTANCE;
	}

	/**
	 * Set the survey instance and open the file this is a gate keeper method.
	 * If you don't end up in an "open" state you can't otherwise interact with
	 * a csv file
	 * 
	 * @param instance
	 *            The instance name of the survey on which the CSVConnection
	 *            should operate
	 * @see org.oyrm.mojo.survey.data.KoboConnection#open(java.lang.String, int)
	 * @throws IOException
	 *             if file operations are fail
	 */
	public void open(String instance) throws IOException {
		logger.entering(getClass().getName(), "open", instance);
		this.setSurveyInstance(instance);
		if (!this.getCsvInstances().contains(instance))
			this.surveyInstanceFile = new File(
					System.getProperty(Constants.PROPKEY_DIRNAME_CSV)
							+ File.pathSeparator + instance
							+ Constants.FILENAME_CSV_EXTENSION);

		surveyInstanceFile = new File(getInstanceFilePath());
		tempFile = File.createTempFile(Constants.STRING_CSV_TEMPFILE_PREFIX,
				Constants.STRING_CSV_TEMPFILE_SUFFIX, csvRootDir);
		tempFile.deleteOnExit();

		if (!surveyInstanceFile.exists()) {
			surveyInstanceFile.createNewFile();
		}
		isOpen = true;
		populateCsvInstances();
		populateSurveys();
		logger.exiting(getClass().getName(), "open");
	}

	/**
	 * close the file connection, by which I mean to dereference the instance
	 * and instanceFile parameters
	 */
	public void close() {
		logger.entering(getClass().getName(), "open");
		try {
			write();
		} catch (IOException ioex) {
			logger.severe("Writer Failed on close()");
		} finally {
			this.setSurveyInstance(null);
			this.surveyInstanceFile = null;
			this.tempFile = null;
			this.CsvHeaders = null;
			this.extantData = null;
			this.newCsvHeaders = null;
			this.newData = null;
			this.reader = null;
			this.writer = null;
			this.surveyInstance = null;
			isOpen = false;
		}
		logger.exiting(this.getClass().getName(), "open");
	}

	/**
	 * Write the new Survey data to the appropriate csv file because the
	 * SuperCSV package does not permit the updating of individual rows or cells
	 * within an existing csv file it is necessary to rewrite the entire file.
	 * 
	 * As a consequence, the write operation behaves differently if the csv file
	 * is new or old.
	 * 
	 * If there is no existing file and the data is being written for the first
	 * time it is simply written through the SuperCSV file writing sequence.
	 * 
	 * If the file already exists, then it is necessary to read in the existing
	 * file and write the entire new file including any new cells to a new file,
	 * which overwrites the existing file as the last operation of the write.
	 * Any new data is maintained in alignment with the existing column headers.
	 * Any new column headers must be added into the header and written first,
	 * then followed with row data written in a manner to compensate for the
	 * addition of these new headers.
	 * 
	 * @throws IOException
	 */
	public void write() throws IOException {
		logger.entering(getClass().getName(), "findSurveyKeys");
		if (!isOpen)
			return;
		writer = getWriter(true);
		String[] headers = getCsvHeaders();
		String[] tmp;

		if (!headers.equals(newCsvHeaders)) {
			for (String columnHead : Arrays.asList(newCsvHeaders)) {
				if (!Arrays.asList(headers).contains(columnHead)) {
					tmp = new String[headers.length + 1];
					System.arraycopy(headers, 0, tmp, 0, headers.length);
					tmp[headers.length] = columnHead;
					headers = tmp;
				}
			}
		}
		// The following Cell Processors will be used last to first as follows
		// Replace newline \n or carriage return \r
		// Replace Null values with an empty string
		// Replace Null values with the value 0
		StrReplace strNewLineProc = new StrReplace("\n", " ");
		StrReplace strReturnProc = new StrReplace("\r", " ", strNewLineProc);
		ConvertNullTo nullToEmptyProc = new ConvertNullTo("", strReturnProc);
		ConvertNullTo nullToZeroProc = new ConvertNullTo("0", nullToEmptyProc);

		CellProcessor[] procs = (CellProcessor[]) Array.newInstance(
				CellProcessor.class, headers.length);
		Arrays.fill(procs, nullToEmptyProc);

		// Set a nullToZeroProc CellProcessor for any columns containing
		// Constants.MULTI_TAG
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].contains(Constants.MULTI_TAG)) {
				procs[i] = nullToZeroProc;
			}
		}

		writer.writeHeader(headers);

		if (extantData != null && !extantData.isEmpty()) {
			for (Map<String, String> data : extantData) {
				writer.write(data, headers, procs);
			}
		}
		if (newData != null) {
			for (Map<String, String> data : newData) {
				writer.write(data, headers, procs);
			}
		}
		writer.close();

		FileUtils.copyFile(tempFile, getInstanceFile());

		tempFile = null;
		logger.exiting(getClass().getName(), "findSurveyKeys");
	}

	/**
	 * generate the list of all survey keys for the instance, the connection
	 * must be in the open state to find survey keys. If an attempt is made to
	 * find the survey keys without an open connection null is returned
	 * 
	 * The survey keys are read from the survey instance CSV file sequentially.
	 * the object instance item instanceSurveyKeys is used to contain the keys
	 */
	public void populateSurveys() {
		logger.entering(this.getClass().getName(), "findSurveyKeys");
		if (!isOpen)
			return;
		this.setInstanceSurveyKeys(new ArrayList<String>());
		newData = new LinkedList<Map<String, String>>();
		newCsvHeaders = new String[0];
		CsvMapReader mReader = null;
		try {
			Map<String, String> record = new HashMap<String, String>();
			mReader = this.getReader();
			this.setCsvHeaders(mReader.getCSVHeader(true));
			extantData = new LinkedList<Map<String, String>>();
			if (getCsvHeaders() == null) {
				setCsvHeaders(new String[0]);
				return;
			}
			while ((record = mReader.read(getCsvHeaders())) != null) {
				instanceSurveyKeys.add(record.get(Constants.KEY_KEY));
				extantData.add(record);
			}
		} catch (IOException ioex) {
			ioex.printStackTrace();
		} finally {
			try {
				mReader.close();
			} catch (IOException ioex) {
				ioex.printStackTrace();
			}
		}
		logger.exiting(this.getClass().getName(), "findSurveyKeys");
		return;
	}

	/**
	 * The survey instance names are simply trimmed from the file names,
	 * removing the csv filename extension from the end found in
	 * Constants.CSV_FILENAME_EXTENSION
	 * 
	 * @see org.oyrm.kobo.postproc.constants.Constants
	 */
	public void populateCsvInstances() {
		logger.entering(this.getClass().getName(), "populateCsvInstances()");

		String[] csvfilenames = csvRootDir.list(new CsvFilenameFilter());
		for (int i = 0; i < csvfilenames.length; i++) {
			csvfilenames[i] = csvfilenames[i].substring(
					0,
					csvfilenames[i].length()
							- Constants.FILENAME_CSV_EXTENSION.length());
		}
		surveyInstances = Arrays.asList(csvfilenames);
		logger.exiting(this.getClass().getName(), "populateCsvInstances()");
	}

	/**
	 * The survey instance names are simply trimmed from the file names,
	 * removing the csv filename extension from the end.
	 * Constants.CSV_FILENAME_EXTENSION
	 * 
	 * @return List<String> The survey instance names recorded in the root
	 *         directory
	 */
	public List<String> getCsvInstances() {
		logger.entering(getClass().getName(), "getCsvInstances");
		logger.exiting(this.getClass().getName(), "getSurveyKeys",
				surveyInstances);
		return surveyInstances;
	}

	/**
	 * If the connection is open a new CsvMapReader is returned which will read
	 * entries in comma separated value file containing the current survey
	 * instance.
	 * 
	 * @return null if there is no open connection, the CsvMapReader otherwise
	 * @throws IOException
	 *             if a new reader cannot be created
	 */
	private CsvMapReader getReader() throws IOException {
		logger.entering(this.getClass().getName(), "getReader");
		if (!isOpen)
			return null;
		this.reader = new CsvMapReader(new InputStreamReader(
				new FileInputStream(this.surveyInstanceFile),
				Constants.ENCODING_CSV),
				CsvPreference.STANDARD_PREFERENCE);
		if (reader == null)
			throw new IOException("Reader is uninstantiated");
		logger.exiting(this.getClass().getName(), "getReader", reader);
		return reader;
	}

	/**
	 * If the connection is open a new CsvMapWriter is returned which will write
	 * entries into the comma separated value file containing the current survey
	 * instance.
	 * 
	 * @return null if there is no open connection, otherwise a valid
	 *         CsvMapWriter
	 * @throws IOException
	 *             if the file is not writable, doesn't exist, etc...
	 */
	private CsvMapWriter getWriter() throws IOException {
		logger.entering(this.getClass().getName(), "getWriter");

		if (!isOpen)
			return null;
			
		// Note: Returned to UTF-8 in order to avoid file writing conflicts
		// if it becomes necessary to support UTF-16, note that BOMs will be
		// written to any file that this OutputStreamWriter writers, more 
		// care should be taken than simply changing the string encoder
		// desination below
		FileOutputStream fos = new FileOutputStream(this.surveyInstanceFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos, Constants.ENCODING_CSV);
		this.writer = new CsvMapWriter(new BufferedWriter(osw),
				CsvPreference.STANDARD_PREFERENCE);

		if (writer == null)
			throw new IOException("Writer is uninstantiated");

		logger.exiting(this.getClass().getName(), "getWriter", writer);
		return writer;
	}

	/**
	 * If the connection is open a new CsvMapWriter is returned which will write
	 * entries into the comma separated value file containing the current survey
	 * instance.
	 * 
	 * @param temp
	 *            true to write to a temporary file
	 * @return null if there is no open connection, otherwise a valid
	 *         CsvMapWriter
	 * @throws IOException
	 *             if the file is not writable, doesn't exist, etc...
	 */
	private CsvMapWriter getWriter(boolean temp) throws IOException {
		logger.entering(this.getClass().getName(), "getWriter", temp);
		if (!temp)
			return getWriter();
		if (!isOpen)
			return null;

		// Note: Returned to UTF-8 in order to avoid file writing conflicts
		// if it becomes necessary to support UTF-16, note that BOMs will be
		// written to any file that this OutputStreamWriter writers, more 
		// care should be taken than simply changing the string encoder
		// desination below
		FileOutputStream fos = new FileOutputStream(tempFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos, Constants.ENCODING_CSV);
		this.writer = new CsvMapWriter(new BufferedWriter(osw),
				CsvPreference.STANDARD_PREFERENCE);

		if (writer == null)
			throw new IOException("Writer is uninstantiated");

		logger.exiting(this.getClass().getName(), "getWriter", writer);
		return writer;
	}

	/**
	 * appendRecord adds data to the newData LinkedList with the recognition
	 * that null data should not be appended, that no attempt to append should
	 * be made if the connection is not open, and that data containing a key
	 * which already exists in the survey instance data record should not be
	 * added. Furthermore, when new data is added through appendRecord a check
	 * is made against the existing headers. If it is necessary to add new
	 * headers, then these new headers are added to the newCsvHeaders instance
	 * member.
	 * 
	 * @param data
	 *            a SurveyRecord instance containing new data to be appended to
	 *            the LinkedList instance member newData.
	 * @return True if the data is appended successfully, otherwise false
	 * @see org.orym.kobo.postproc.data.SurveyRecord
	 */
	public boolean appendRecord(SurveyRecord data) {
		logger.entering(this.getClass().getName(), "appendData", data);
		if (!isOpen) {
			logger.info("attempting to append to closed record");
			return false;
		}
		if (data == null) {
			logger.info("attempting to append a null record");
			return false;
		}

		String[] tmp;
		if (!newCsvHeaders.equals(data.getQuestionIDs().toArray())) {
			for (String qID : data.getQuestionIDs()) {
				if (!Arrays.asList(newCsvHeaders).contains(qID)) {
					tmp = new String[newCsvHeaders.length + 1];
					System.arraycopy(newCsvHeaders, 0, tmp, 0,
							newCsvHeaders.length);
					tmp[newCsvHeaders.length] = qID;
					newCsvHeaders = tmp;
				}
			}
		}
		if (newData == null)
			newData = new LinkedList<Map<String, String>>();
		if (getInstanceSurveyKeys() == null
				|| getInstanceSurveyKeys().contains(data.getKey())) {
			logger.info("instanceSurveyKeys is null or contains this key");
			return false;
		} else if (newData.contains(data.getMap())) { // This is probably a very
														// costly operation
			logger.info("newData contains this key");
			return false;
		} else
			this.newData.add((data.getMap()));

		logger.exiting(getClass().getName(), "appendData", true);
		return true;
	}

	/**
	 * setSurveyInstance simply sets the object member surveyInstance to the
	 * passed instance parameter
	 * 
	 * @param instance
	 *            a String matching the name of the survey instance
	 */
	public void setSurveyInstance(String instance) {
		logger.entering(this.getClass().getName(), "setInstance", instance);
		this.surveyInstance = instance;
		logger.exiting(this.getClass().getName(), "setInstance");
	}

	/**
	 * 
	 * @return survey instance name in String format
	 */
	public String getSurveyInstance() {
		logger.entering(this.getClass().getName(), "getInstance");
		logger.exiting(this.getClass().getName(), "getInstance", surveyInstance);
		return surveyInstance;
	}

	/**
	 * @return the absolute path of the file containing records for this survey
	 *         instance
	 */
	public String getInstanceFilePath() {
		logger.entering(this.getClass().getName(), "getInstanceFilePath");
		logger.exiting(this.getClass().getName(), "getInstanceFilePath",
				System.getProperty(Constants.PROPKEY_DIRNAME_CSV)
						+ File.separator + surveyInstance
						+ Constants.FILENAME_CSV_EXTENSION);
		return System.getProperty(Constants.PROPKEY_DIRNAME_CSV)
				+ File.separator + surveyInstance
				+ Constants.FILENAME_CSV_EXTENSION;
	}

	/**
	 * @return File object of the csv file containing the survey instance
	 */
	public File getInstanceFile() {
		logger.entering(this.getClass().getName(), "getInstanceFile");
		logger.exiting(this.getClass().getName(), "getInstanceFile",
				surveyInstanceFile);
		return this.surveyInstanceFile;
	}

	/**
	 * the instance survey keys contained in List<String> form
	 * 
	 * @return the instanceSurveyKeys List
	 */
	public List<String> getInstanceSurveyKeys() {
		return instanceSurveyKeys;
	}

	/**
	 * @param instanceSurveyKeys
	 *            the instanceSurveyKeys to set
	 */
	public void setInstanceSurveyKeys(List<String> instanceSurveyKeys) {
		this.instanceSurveyKeys = instanceSurveyKeys;
	}

	/**
	 * @return the csvHeaders
	 */
	public String[] getCsvHeaders() {
		return CsvHeaders;
	}

	/**
	 * @param csvHeaders
	 *            the csvHeaders to set
	 */
	public void setCsvHeaders(String[] csvHeaders) {
		CsvHeaders = csvHeaders;
	}

	/**
	 * CsvFilenameFilter filters a directories contents for .csv files
	 * 
	 * @author gary
	 * 
	 */
	private class CsvFilenameFilter implements FilenameFilter {
		/**
		 * Determine if the file ends with the csv extension
		 */
		public boolean accept(File dir, String name) {
			return (name.endsWith(Constants.FILENAME_CSV_EXTENSION));
		}
	}
}