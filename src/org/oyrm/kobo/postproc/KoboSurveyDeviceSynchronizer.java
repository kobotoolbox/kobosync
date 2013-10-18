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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.oyrm.kobo.postproc.constants.Constants;
import org.oyrm.kobo.postproc.utils.SourceSyncWalker;
import org.oyrm.kobo.postproc.utils.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The KoboSurveyDeviceSynchronizer reads data from a specified directory and
 * copies it onto a specified storage directory in order to maintain copies of
 * the XML locally.
 * 
 * The application of this class is intended to permit data to be harvested from
 * devices mounted to the local file system and stored in a local file storage
 * directory.
 * 
 * @author Gary Hendrick
 * 
 */
public class KoboSurveyDeviceSynchronizer extends SwingWorker<Void, Void> {
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
			logger.setLevel(Level.parse(System.getProperty(Constants.PROPKEY_LOGGING_LEVEL)));
		} catch(IllegalArgumentException ex) {
			logger.setLevel(Level.OFF);
			System.out.println("Logging function failed due to exception");
			System.out.println(ex.getMessage());
		}
		// TODO: Investigate NullPointerException for launch without config
		// NullPointerException
		// when
		// launched
		// by itself
	}

	static final String[] typeName = { "none", "Element", "Attr", "Text",
			"CDATA", "EntityRef", "Entity", "ProcInstr", "Comment", "Document",
			"DocType", "DocFragment", "Notation", };

	private Document documentSource;
	private Document documentExisting;
	private File readdir, storedir;
	private Integer nSynced;

	public KoboSurveyDeviceSynchronizer(File source, File storage) {
		super();

		if (!source.exists() || !source.isDirectory()) {
			throw new IllegalArgumentException(
					"Usage: SimpleSAXSurveyReader requires a valid java.io.File() argument representing a directory");
		}
		readdir = source;
		storedir = storage;
	}

	@Override
	public Void doInBackground() throws Exception {
		logger.entering(getClass().getName(), "doInBackground()");
		setProgress(1);
		try {
			processDirectory();
		} catch (Exception ex) {
			logger.warning(ex.toString());
			throw ex;
		} finally {
			setProgress(100);
		}
		logger.exiting(getClass().getName(), null);
		return null;
	}

	@Override
	public void done() {
		logger.entering(getClass().getName(), "done()");
		logger.exiting(getClass().getName(), "done()");
	}

	/**
	 * Read the storage directory and source directory and sync new files
	 * 
	 * @throws Exception
	 */
	private void processDirectory() throws Exception {
		logger.entering(getClass().getName(), "processDirectory");
		try {
			if (!storedir.exists())
			{
				throw new IOException("Storage Directory, "
						+ storedir.getAbsolutePath() + ", Does Not Exist");
			}
			if (!readdir.exists())
			{
				throw new IOException("Source Directory, "
						+ readdir.getAbsolutePath() + ", Does Not Exist");
			}
			

			IOFileFilter xmlFileFilter = null;
			try
			{
			xmlFileFilter = FileFilterUtils.andFileFilter(
					FileFilterUtils.fileFileFilter(), FileFilterUtils
							.suffixFileFilter(".xml"));
			}
			catch(Exception exp)
			{
				System.out.println(exp.getMessage());
			}
			IOFileFilter anyDirFilter = FileFilterUtils.andFileFilter(
					FileFilterUtils.directoryFileFilter(),
					DirectoryFileFilter.INSTANCE);
			FileFilter myFilter = FileFilterUtils.orFileFilter(anyDirFilter,
					xmlFileFilter);
			SourceSyncWalker walker = new SourceSyncWalker(myFilter);			

			List<File> sourceXmlList = walker.sync(readdir);
			logger.finer("sourceXmlList contains " + sourceXmlList.size()
					+ " source files");
			
			
			List<File> destXmlList = walker.sync(storedir);
			logger.finer("destXmlList: contains " + destXmlList.size()
					+ " possible matches");
			
			List<File> filesToSync = compareFileLists(sourceXmlList,
					destXmlList);

			logger.fine("Syncing " + filesToSync.size() + " files");

			copyFiles(filesToSync);
		} catch (IOException ioex) {
			logger.warning(ioex.toString());
			throw ioex;
		} catch (Exception e) {
			logger.warning(e.toString());
			throw e;
		}
		logger.exiting(getClass().getName(), "processDirectory");
	}

	/**
	 * @param filesToSync
	 *            a List<File> to move to the new storage directory
	 * @throws Exception
	 */
	private void copyFiles(List<File> filesToSync) throws Exception {
		logger.entering(getClass().getName(), "copyFiles", filesToSync);
		if (filesToSync == null || filesToSync.isEmpty()) {
			logger.finer("No files to Sync");
			setnSynced(0);
			logger.exiting(getClass().getName(), "copyFiles");
			return;
		}

		String instanceName;
		String storageFilename;
		File instanceStorageDirectory;
		int totalFiles = filesToSync.size();
		int fileCount = 0;
		int syncedFiles = 0;

		for (File sync : filesToSync) {
			fileCount++;

			if ((100 - getProgress()) * fileCount / totalFiles > 0)
				setProgress(getProgress() + (100 - getProgress()) * fileCount
						/ totalFiles);
			try {
				documentSource = DomUtils.createDocument(sync);
			} catch (Exception e) {
				logger.warning(sync.getName() + " could not be parsed");
				throw e;
			}
			instanceName = documentSource.getDocumentElement().getNodeName()
					.trim();
			Node deviceIdNode = DomUtils.findSubNode(Constants.KEY_DEVICEID,
					documentSource.getDocumentElement());

			if (deviceIdNode == null) {
				continue;
			} else {
				storageFilename = deviceIdNode.getTextContent()
						+ Constants.FILENAME_ODK_DELIM + sync.getName();
			}

			instanceStorageDirectory = new File(storedir, instanceName);
			if (!instanceStorageDirectory.exists()) {
				instanceStorageDirectory.mkdir();
			}
			File storageFile = new File(instanceStorageDirectory,
					storageFilename);
			try {
				FileUtils.copyFile(sync, storageFile);
				syncedFiles++;
			} catch (IOException ioex) {
				logger.warning("Error copying file from " + sync.getName()
						+ " to " + storageFile.getName());
				throw ioex;
			}
		}
		setProgress(100);
		setnSynced(syncedFiles);
		logger.exiting(getClass().getName(), "copyFiles");
	}

	/**
	 * A List<File> of new files to be moved from the newly introduced source
	 * directory to the destination directory.
	 * 
	 * @param source
	 * @param dest
	 * @return
	 */
	private List<File> compareFileLists(List<File> source, List<File> dest) {
		logger.entering(getClass().getName(), "compareFileLists", new Object[] {
				source, dest });
		if (dest.size() == 0 || source.size() == 0)
			return source;
		String time;
		Matcher timematch;
		boolean addFile = true;
		List<File> syncFiles = new ArrayList<File>();
		Map<File, File> destToSourceMatch = new HashMap<File, File>();

		int fileProg = 0;
		int totalFiles = source.size() * dest.size();

		for (File newFile : source) {
			fileProg++;
			setProgress(40 * fileProg / totalFiles);

			addFile = true;
			timematch = Constants.REGEX_FILENAME_DATEFORMAT.matcher(newFile
					.getName());
			if (!timematch.find()) {
				continue;
			}

			time = newFile.getName().substring(timematch.start(),
					timematch.end() + 1);

			for (File existingFile : dest) {
				fileProg++;
				setProgress(40 * fileProg / totalFiles);

				if (existingFile.getName().contains(time)) {
					addFile = false;
					destToSourceMatch.put(existingFile, newFile);
				}
			}
			if (addFile)
				syncFiles.add(newFile);
		}

		if (!destToSourceMatch.isEmpty()) {
			for (File existingFile : destToSourceMatch.keySet()) {
				try {
					if (!checkContentMatches(existingFile, destToSourceMatch
							.get(existingFile))) {
						syncFiles.add(destToSourceMatch.get(existingFile));
					}
				} catch (Exception e) {
					logger.warning("Attempt to check file match failes");
					e.printStackTrace();
				}
			}
		}

		logger.exiting(getClass().getName(), "compareFileLists");
		return syncFiles;
	}

	/**
	 * Does the content of the source match the content of a destination file ?
	 * This method is only used to attempt to match XML files which are near
	 * matches based upon naming conventions. Perhaps this could be better
	 * generalized, but because this method is only invoked with near matches
	 * the performance should be better than attempting to read and match
	 * contents for every existing and source file.
	 * 
	 * The contents are checked based on the device id and start time for the
	 * survey data recorded in the specified files.
	 * 
	 * @param dest
	 *            A file from within the XML storage directory which needs to be
	 *            checked against a newly introduced file
	 * @param source
	 *            A newly introduced XML file whose content may be a match for
	 *            an existing stored file
	 * @return True if the files contain the same survey, otherwise False
	 * @throws Exception
	 */
	private boolean checkContentMatches(File dest, File source)
			throws Exception {
		logger.entering(getClass().getName(), "checkContentMatches",
				new Object[] { dest, source });
		boolean match = false;
		try {
			documentExisting = DomUtils.createDocument(dest);
			documentSource = DomUtils.createDocument(source);
		} catch (Exception e) {
			throw e;
		}

		Node existingDeviceIDNode = DomUtils.findSubNode(Constants.KEY_DEVICEID,
				documentExisting.getDocumentElement()); 
		if(existingDeviceIDNode == null) return false;
		String existingDeviceID = existingDeviceIDNode.getTextContent();
		
		Node existingStartNode = DomUtils.findSubNode(Constants.KEY_START,
				documentExisting.getDocumentElement());
		if (existingStartNode == null) return false;
		String existingStart = existingStartNode.getTextContent();

		Node sourceDeviceIDNode = DomUtils.findSubNode(Constants.KEY_DEVICEID,
				documentSource.getDocumentElement()); 
		if (sourceDeviceIDNode == null) return false;
		String sourceDeviceID = sourceDeviceIDNode.getTextContent();

		Node sourceStartNode = DomUtils.findSubNode(Constants.KEY_START,
				documentSource.getDocumentElement());
		if (sourceStartNode == null) return false;
		String sourceStart = sourceStartNode.getTextContent();
		
		if (existingDeviceID.equals(sourceDeviceID)
				&& existingStart.equals(sourceStart))
			match = true;
		documentExisting = null;
		documentSource = null;
		logger.exiting(getClass().getName(), "checkContentMatches", match);
		return match;
	}

	public int getLengthOfTask() {
		return 100;
	}

	/**
	 * @return the nSynced
	 */
	public Integer getnSynced() {
		return nSynced;
	}

	/**
	 * @param nSynced
	 *            the nSynced to set
	 */
	public void setnSynced(Integer nSynced) {
		Integer oldnSynced = this.nSynced;
		this.nSynced = nSynced;
		getPropertyChangeSupport().firePropertyChange(
				Constants.CHANGEPROP_NAME_NCOMPLETED, oldnSynced, nSynced);
	}

	/**
	 * Have gone to GUI execution, this may need updating
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length != 2) {
				throw new IllegalArgumentException(
						"Usage: "
								+ KoboSurveyDeviceSynchronizer.class.getName()
								+ " <Source Directory> <Destination Directory>"
								+ "\n\tSimpleDOMSurveyReader requires a source directory"
								+ " name argument along with a destination File directory");
			}
			KoboSurveyDeviceSynchronizer handler = new KoboSurveyDeviceSynchronizer(
					new File(args[0]), new File(args[1]));
			handler.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}