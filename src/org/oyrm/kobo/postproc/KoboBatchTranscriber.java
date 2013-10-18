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

package org.oyrm.kobo.postproc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.SwingWorker;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.oyrm.kobo.postproc.constants.Constants;
import org.oyrm.kobo.postproc.data.PersistenceFactory;
import org.oyrm.kobo.postproc.data.SurveyRecord;
import org.oyrm.kobo.postproc.data.SurveyRecordDAO;
import org.oyrm.kobo.postproc.utils.SourceSyncWalker;
import org.oyrm.kobo.postproc.utils.DomUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The KoboBatchTranscriber reads XML files from the xml directory and
 * transcribes the survey records contained there into csv files within the csv
 * storage directory.
 * 
 * The xml directory contains xml organized by survey instance. The csv storage
 * directory contains one csv file for each survey instance, that file contains
 * one row for each record. This directory is created using the
 * KoboSurveyDeviceSynchronizer
 * 
 * The KoboBatchTranscriber will check the existing csv storage directory to see
 * if there is a csv record for each survey instance in the xml directory. If
 * the file does not exist then a new file is created, if the file does exist
 * then the individual files are checked against the keys in the csv file to
 * determine which, if any, of the xml files need to be transcribed. This
 * implementation does not support updating or deleting existing records. New
 * records are transcribed, old records are ignored. Existing data within the
 * csv file is preserved, and csv headers are updated if necessary.
 * 
 * @author Gary Hendrick
 */
public class KoboBatchTranscriber extends SwingWorker<Void, Void> implements
		PropertyChangeListener {
	private static Logger logger = Logger.getLogger("org.oyrm.kobo.postproc");
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
		} catch (IllegalArgumentException ex) {
			logger.setLevel(Level.OFF);
			System.out.println("Logging function failed due to exception");
			System.out.println(ex.getMessage());
		}
	}

	private File xmlDir, csvDir;
	private Map<String, List<SurveyRecord>> records; // Map containing all
														// survey results in
														// xmlDir
	private Integer nTranscribed = 0;
	private Integer subProgress = 0;
	private float writeProgressFraction = 0;
	private SurveyRecordDAO srd = null;

	/**
	 * Constructor, requires a directory be passed
	 * 
	 * @input dir Directory to be processed
	 */
	public KoboBatchTranscriber(File source, File dest)
			throws IllegalArgumentException {
		super();
		if (!source.exists() || !source.isDirectory()) {
			throw new IllegalArgumentException(
					"Usage: SimpleSAXSurveyReader "
							+ "<Source Directory> <Destination Directory>"
							+ "/n/trequires a valid java.io.File() argument representing a directory");
		}
		this.xmlDir = source;
		this.csvDir = dest;
	}

	/**
	 * Records are read from the xmlDir, only xml files are read in
	 * 
	 * @throws Exception
	 */
	private void processDirectory() throws Exception {
		List<String> instances = new ArrayList<String>();
		try {
			if (records == null) {
				records = new HashMap<String, List<SurveyRecord>>();
			}

			IOFileFilter xmlFileFilter = FileFilterUtils.andFileFilter(
					FileFilterUtils.fileFileFilter(),
					FileFilterUtils.suffixFileFilter(".xml"));
			IOFileFilter anyDirFilter = FileFilterUtils.andFileFilter(
					FileFilterUtils.directoryFileFilter(),
					DirectoryFileFilter.INSTANCE);
			FileFilter myFilter = FileFilterUtils.orFileFilter(anyDirFilter,
					xmlFileFilter);
			SourceSyncWalker walker = new SourceSyncWalker(myFilter);

			List<File> sourceXmlList = walker.sync(xmlDir);

			Document doc;
			SurveyRecord record;

			float progressFraction = 0;
			float tprogress = 0;
			if (!sourceXmlList.isEmpty()) {
				progressFraction = (float) ((float) 50 / (float) sourceXmlList
						.size());
			}

			for (File xml : sourceXmlList) {
				doc = DomUtils.createDocument(xml);
				logger.finer(doc.toString());
				if (doc.getDocumentElement() == null)
					continue;
				logger.finer("doc.getDocumentElement()="
						+ doc.getDocumentElement().toString());
				if (doc.getDocumentElement().getNodeName() == null)
					continue;
				logger.finer("doc.getDocumentElement().getNodeName()="
						+ doc.getDocumentElement().getNodeName().toString());
				record = DomUtils.documentToSurveyRecord(doc);
				if (record == null)
					continue;

				logger.finer("DomUtils.documentToSurveyRecord(doc)="
						+ record.toString());
				if (!records.containsKey(record.getInstance()))
					records.put(record.getInstance(),
							new ArrayList<SurveyRecord>());
				records.get(record.getInstance()).add(record);
				logger.fine("instances = " + instances.toString());
				tprogress = tprogress + progressFraction;
				setProgress(new Float(tprogress).intValue());
			}
		} catch (FileNotFoundException feex) {
			logger.warning("" + feex.getMessage());
			throw feex;
		} catch (IOException ioex) {
			logger.warning("" + ioex.getMessage());
			throw ioex;
		} catch (SAXException saxex) {
			logger.warning("" + saxex.getMessage());
			throw saxex;
		}
		logger.fine(records.toString());
	}

	/**
	 * writeRecords writes the records
	 */
	private void writeRecords() {
		logger.entering(getClass().getName(), "writeRecords()");
		PersistenceFactory pf = PersistenceFactory
				.getPersistenceFactory(PersistenceFactory.CSV);
		srd = pf.getSurveyRecordDAO();
		srd.addPropertyChangeListener(this);

		Set<String> formKeys = records.keySet();
		String instanceKey;
		List<SurveyRecord> surveyList;

		if (formKeys.isEmpty())
			return;

		writeProgressFraction = (float) 50 / (float) formKeys.size();
		for (Iterator<String> instanceIt = formKeys.iterator(); instanceIt
				.hasNext();) {
			instanceKey = (String) instanceIt.next();
			surveyList = records.get(instanceKey);
			srd.insertSurveyRecordList(instanceKey, surveyList);
		}
	}

	/**
	 * Housekeeping
	 */
	private void end() {
		logger.entering(getClass().getName(), "end()");
		logger.fine("end()");
		setProgress(100);
		logger.exiting(getClass().getName(), "end()");
	}

	@Override
	protected Void doInBackground() throws Exception {
		System.setProperty(Constants.PROPKEY_DIRNAME_CSV,
				csvDir.getAbsolutePath());
		try {
			setProgress(0);
			processDirectory();
			setProgress(50);
			writeRecords();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			this.end();
		}
		return null;
	}

	public void done() {
		logger.entering(getClass().getName(), "end");
		logger.fine("done()");
		csvDir = null;
		xmlDir = null;
		records.clear();
		logger.exiting(getClass().getName(), "end");
	}

	public int getLengthOfTask() {
		return 100;
	}

	/**
	 * @return the nTranscribed
	 */
	protected Integer getnTranscribed() {
		return nTranscribed;
	}

	/**
	 * @param nTranscribed
	 *            the nTranscribed to set
	 */
	protected void setnTranscribed(Integer nTranscribed) {
		Integer oldnTranscribed = this.nTranscribed;
		this.nTranscribed = nTranscribed;
		getPropertyChangeSupport().firePropertyChange(
				Constants.CHANGEPROP_NAME_NCOMPLETED, oldnTranscribed,
				nTranscribed);
	}

	/**
	 * @return the subProgress
	 */
	protected Integer getSubProgress() {
		return subProgress;
	}

	/**
	 * @param subProgress
	 *            the subProgress to set
	 */
	protected void setSubProgress(Integer subProgress) {
		this.subProgress = subProgress;
		if (((int) (getProgress() + writeProgressFraction
				* ((float) subProgress / (float) 100))) > 100) {
			setProgress(100);
			return;
		}
		setProgress((int) (getProgress() + writeProgressFraction
				* ((float) subProgress / (float) 100)));
	}

	/**
	 * Entry point method for command line execution
	 * 
	 * @param args
	 *            should be a single directory name
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException(
					"Usage: SimpleSAXSurveyReader "
							+ "<Source Directory> <Destination Directory>"
							+ "/n/trequires a valid java.io.File() argument representing a directory");
		}
		KoboBatchTranscriber handler = new KoboBatchTranscriber(new File(
				args[0]), new File(args[1]));
		handler.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource().equals(srd)) {
			if (evt.getPropertyName() == Constants.CHANGEPROP_NAME_NCOMPLETED) {
				this.setnTranscribed((Integer) evt.getNewValue());
			} else if (evt.getPropertyName() == Constants.CHANGEPROP_NAME_PROGRESS) {
				this.setSubProgress((Integer) evt.getNewValue());
			} else if (evt.getPropertyName() == Constants.CHANGEPROP_NAME_STATE) {
				this.getPropertyChangeSupport().firePropertyChange(evt);
			}
		}
	}
}