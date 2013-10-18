package org.oyrm.kobo.postproc.ui;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.oyrm.kobo.postproc.constants.Constants;

public class PreferencesDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger("org.oyrm.kobo");  //  @jve:decl-index=0:
	private static Formatter lf;
	private static FileHandler lh;
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
	
	private JPanel jContentPane = null;
	private JButton applyButton = null;
	private JButton srcDirButton = null;
	private JButton xmlStorageButton = null;
	private JButton csvStorageButton = null;
	private JButton cancelButton = null;
	private File xmlDir, csvDir, srcDir;  //  @jve:decl-index=0:
	private JPanel XMLSrcPan = null;
	private JPanel XMLStorPan = null;
	private JPanel CSVStorPan = null;
	
	private JTextField srcDirText = null;
	private JTextField xmlDirText = null;
	private JTextField csvDirText = null;
	
	/**
	 * @param owner
	 */
	public PreferencesDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setName("setDialog");  // Generated
		this.setPreferredSize(new Dimension(325, 250));  // Generated
		this.setLocation(new Point(50, 50));  // Generated
		this.setTitle("Preferences");  // Generated
		// create and load default properties and compensate for changes to directory structure 
		csvDir = new File(KoboPostProcPanel.applicationProps
				.getProperty(Constants.PROPKEY_DIRNAME_CSV));
		
		xmlDir = new File(KoboPostProcPanel.applicationProps
				.getProperty(Constants.PROPKEY_DIRNAME_XML_STORAGE));
		
		srcDir = new File(KoboPostProcPanel.applicationProps
				.getProperty(Constants.PROPKEY_DIRNAME_XML_DEV));
		setContentPane(getJContentPane());
		setVisible(true);  // Generated
		pack();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gbcCSVStorPan = new GridBagConstraints();
			gbcCSVStorPan.gridx = 0;  // Generated
			gbcCSVStorPan.gridwidth = 5;  // Generated
			gbcCSVStorPan.weightx = 1.0D;  // Generated
			gbcCSVStorPan.fill = GridBagConstraints.HORIZONTAL;  // Generated
			gbcCSVStorPan.insets = new Insets(5, 0, 10, 0);  // Generated
			gbcCSVStorPan.gridy = 2;  // Generated
			GridBagConstraints gbcXMLStorPan = new GridBagConstraints();
			gbcXMLStorPan.gridx = 0;  // Generated
			gbcXMLStorPan.gridwidth = 5;  // Generated
			gbcXMLStorPan.weightx = 1.0D;  // Generated
			gbcXMLStorPan.fill = GridBagConstraints.HORIZONTAL;  // Generated
			gbcXMLStorPan.insets = new Insets(5, 0, 0, 0);  // Generated
			gbcXMLStorPan.gridy = 1;  // Generated
			GridBagConstraints gbcXMLSrcPan = new GridBagConstraints();
			gbcXMLSrcPan.gridx = 0;  // Generated
			gbcXMLSrcPan.fill = GridBagConstraints.HORIZONTAL;  // Generated
			gbcXMLSrcPan.gridwidth = 5;  // Generated
			gbcXMLSrcPan.weightx = 1.0D;  // Generated
			gbcXMLSrcPan.gridy = 0;  // Generated
			GridBagConstraints gbcApplyButton = new GridBagConstraints();
			gbcApplyButton.insets = new Insets(0, 0, 0, 0);  // Generated
			gbcApplyButton.gridy = 3;  // Generated
			gbcApplyButton.weightx = 0.0D;  // Generated
			gbcApplyButton.anchor = GridBagConstraints.EAST;  // Generated
			gbcApplyButton.gridx = 4;  // Generated
			GridBagConstraints gbcCancelButton = new GridBagConstraints();
			gbcCancelButton.insets = new Insets(0, 170, 0, 0);  // Generated
			gbcCancelButton.gridy = 3;  // Generated
			gbcCancelButton.weightx = 0.0D;  // Generated
			gbcCancelButton.anchor = GridBagConstraints.EAST;  // Generated
			gbcCancelButton.gridx = 3;  // Generated
			jContentPane = new JPanel();
			jContentPane.setPreferredSize(new Dimension(600, 200));  // Generated
			jContentPane.setLayout(new GridBagLayout());  // Generated
			jContentPane.add(getCancelButton(), gbcCancelButton);  // Generated
			jContentPane.add(getApplyButton(), gbcApplyButton);  // Generated
			jContentPane.add(getXMLSrcPan(), gbcXMLSrcPan);  // Generated
			jContentPane.add(getXMLStorPan(), gbcXMLStorPan);  // Generated
			jContentPane.add(getCSVStorPan(), gbcCSVStorPan);  // Generated
		}
		return jContentPane;
	}

	/**
	 * This method initializes applyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getApplyButton() {
		if (applyButton == null) {
			try {
				applyButton = new JButton();
				applyButton.setText("Apply");  // Generated
				applyButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						KoboPostProcPanel.applicationProps.setProperty(
								Constants.PROPKEY_DIRNAME_CSV, csvDir
								.getAbsolutePath());
						KoboPostProcPanel.applicationProps.setProperty(
								Constants.PROPKEY_DIRNAME_XML_DEV, srcDir
								.getAbsolutePath());
						KoboPostProcPanel.applicationProps.setProperty(
								Constants.PROPKEY_DIRNAME_XML_STORAGE, xmlDir
								.getAbsolutePath());
						KoboPostProcPanel.getInstance(null).update();
						KoboPostProcPanel.getInstance(null).updatePreferences();
						PreferencesDialog.this.setVisible(false);
					}
				});
			} catch (java.lang.Throwable e) {
				logger.log(Level.SEVERE, "could not generate apply button", e);
			}
		}
		return applyButton;
	}

	/**
	 * This method initializes srcDirButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSrcDirButton() {
		if (srcDirButton == null) {
			try {
				srcDirButton = new JButton();
				final JFileChooser xmlSrcChooser = new JFileChooser();
				xmlSrcChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				xmlSrcChooser.setDialogTitle("Change Survey Source Directory");
				xmlSrcChooser.setCurrentDirectory(srcDir);
				
				srcDirButton.setText("...");  // Generated
				srcDirButton.setSize(new Dimension(10, 10));  // Generated
				srcDirButton.setVerticalTextPosition(SwingConstants.TOP);  // Generated
				srcDirButton.setFont(new Font("DejaVu Serif", Font.BOLD, 8));  // Generated
				srcDirButton.setBounds(new Rectangle(0, 0, 58, 25));  // Generated
				srcDirButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						switch (xmlSrcChooser.showSaveDialog(PreferencesDialog.this)) {
							case JFileChooser.APPROVE_OPTION:
								srcDir = xmlSrcChooser.getSelectedFile();
								srcDirText.setText(srcDir.getAbsolutePath());
								pack();
							case JFileChooser.CANCEL_OPTION:
							case JFileChooser.ERROR_OPTION:
								return;
						}
					}
				});
			} catch (java.lang.Throwable e) {
				logger.log(Level.SEVERE, "could not generate source directory button", e);
			}
		}
		return srcDirButton;
	}

	/**
	 * This method initializes xmlStorageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getXmlStorageButton() {
		if (xmlStorageButton == null) {
			try {
				xmlStorageButton = new JButton();
				final JFileChooser xmlStorageChooser = new JFileChooser();
				xmlStorageChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				xmlStorageChooser.setDialogTitle("Change Survey Backup Directory");
				xmlStorageChooser.setCurrentDirectory(xmlDir);
				
				xmlStorageButton.setText("...");  // Generated
				xmlStorageButton.setSize(new Dimension(10, 10));  // Generated
				xmlStorageButton.setFont(new Font("DejaVu Serif", Font.BOLD, 8));  // Generated
				xmlStorageButton.setVerticalTextPosition(SwingConstants.TOP);  // Generated
				xmlStorageButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						switch (xmlStorageChooser.showSaveDialog(PreferencesDialog.this)) {
							case JFileChooser.APPROVE_OPTION:
								xmlDir = xmlStorageChooser.getSelectedFile();
								xmlDirText.setText(xmlDir.getAbsolutePath());
								pack();
							case JFileChooser.CANCEL_OPTION:
							case JFileChooser.ERROR_OPTION:
								return;
						}
					}
				});
			} catch (java.lang.Throwable e) {
				logger.log(Level.SEVERE, "could not generate xml storage directory button", e);
			}
		}
		return xmlStorageButton;
	}

	/**
	 * This method initializes csvStorageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCsvStorageButton() {
		if (csvStorageButton == null) {
			try {
				csvStorageButton = new JButton();
				final JFileChooser csvStorageChooser = new JFileChooser();
				csvStorageChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				csvStorageChooser.setDialogTitle("Change CSV Storage Directory");
				csvStorageChooser.setCurrentDirectory(csvDir);
				
				csvStorageButton.setText("...");  // Generated
				csvStorageButton.setSize(new Dimension(10, 10));  // Generated
				csvStorageButton.setVerticalTextPosition(SwingConstants.TOP);  // Generated
				csvStorageButton.setFont(new Font("DejaVu Serif", Font.BOLD, 8));  // Generated
				csvStorageButton.setBounds(new Rectangle(0, 0, 58, 25));  // Generated
				csvStorageButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						switch (csvStorageChooser.showSaveDialog(PreferencesDialog.this)) {
						case JFileChooser.APPROVE_OPTION:
							csvDir = csvStorageChooser.getSelectedFile();
							csvDirText.setText(csvDir.getAbsolutePath());
							pack();
						case JFileChooser.CANCEL_OPTION:
						case JFileChooser.ERROR_OPTION:
							return;
						}
					}
				});
			} catch (java.lang.Throwable e) {
				logger.log(Level.SEVERE, "could not generate cvs storage directory button", e);
			}
		}
		return csvStorageButton;
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			try {
				cancelButton = new JButton();
				cancelButton.setText("Cancel");  // Generated
				cancelButton.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);  // Generated
				cancelButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						PreferencesDialog.this.setVisible(false);
					}
				});
			} catch (java.lang.Throwable e) {
				logger.log(Level.SEVERE, "could not generate cancel button", e);
			}
		}
		return cancelButton;
	}

	/**
	 * This method initializes XMLSrcPan	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getXMLSrcPan() {
		if (XMLSrcPan == null) {
			try {
				GridBagConstraints gbcSrcDirText = new GridBagConstraints();
				gbcSrcDirText.fill = GridBagConstraints.HORIZONTAL;  // Generated
				gbcSrcDirText.weightx = 1.0D;  // Generated
				gbcSrcDirText.gridx = 0;  // Generated
				gbcSrcDirText.gridy = 0;  // Generated
				srcDirText = new JTextField();
				srcDirText.setText(
						KoboPostProcPanel.applicationProps.getProperty(
								Constants.PROPKEY_DIRNAME_XML_DEV));  // Generated
				srcDirText.setHorizontalAlignment(JTextField.LEADING);  // Generated
				srcDirText.setEditable(false);
				
				GridBagConstraints gbcSrcDirButton = new GridBagConstraints();
				gbcSrcDirButton.insets = new Insets(0, 3, 2, 0);  // Generated
				gbcSrcDirButton.gridy = 0;  // Generated
				gbcSrcDirButton.anchor = GridBagConstraints.EAST;  // Generated
				gbcSrcDirButton.gridx = 1;  // Generated
				
				XMLSrcPan = new JPanel();
				XMLSrcPan.setLayout(new GridBagLayout());  // Generated
				XMLSrcPan.setBorder(BorderFactory.createTitledBorder(null, "XML Source Directory", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));  // Generated
				XMLSrcPan.add(getSrcDirButton(), gbcSrcDirButton);  // Generated
				XMLSrcPan.add(srcDirText, gbcSrcDirText);  // Generated
			} catch (java.lang.Throwable e) {
				logger.log(Level.SEVERE, "could not generate xml source panel", e);
			}
		}
		return XMLSrcPan;
	}

	/**
	 * This method initializes XMLStorPan	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getXMLStorPan() {
		if (XMLStorPan == null) {
			try {
				GridBagConstraints gbcXmlDirText = new GridBagConstraints();
				gbcXmlDirText.fill = GridBagConstraints.HORIZONTAL;  // Generated
				gbcXmlDirText.weightx = 1.0D;  // Generated
				gbcXmlDirText.gridx = 0;  // Generated
				gbcXmlDirText.gridy = 1;  // Generated
				xmlDirText = new JTextField();
				xmlDirText.setText(
						KoboPostProcPanel.applicationProps.getProperty(
								Constants.PROPKEY_DIRNAME_XML_STORAGE));  // Generated
				xmlDirText.setEditable(false);
				
				GridBagConstraints gbcXmlStorDirButton = new GridBagConstraints();
				gbcXmlStorDirButton.insets = new Insets(0, 3, 2, 0);  // Generated
				gbcXmlStorDirButton.gridy = 1;  // Generated
				gbcXmlStorDirButton.anchor = GridBagConstraints.EAST;  // Generated
				gbcXmlStorDirButton.gridx = 1;  // Generated
				
				XMLStorPan = new JPanel();
				XMLStorPan.setLayout(new GridBagLayout());  // Generated
				XMLStorPan.setBorder(BorderFactory.createTitledBorder(null, "XML Storage Directory", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));  // Generated
				XMLStorPan.add(getXmlStorageButton(), gbcXmlStorDirButton);  // Generated
				XMLStorPan.add(xmlDirText, gbcXmlDirText);  // Generated
			} catch (java.lang.Throwable e) {
				logger.log(Level.SEVERE, "could not xml storage panel", e);
			}
		}
		return XMLStorPan;
	}

	/**
	 * This method initializes CSVStorPan	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCSVStorPan() {
		if (CSVStorPan == null) {
			try {
				GridBagConstraints gbcCsvDirText = new GridBagConstraints();
				gbcCsvDirText.fill = GridBagConstraints.HORIZONTAL;  // Generated
				gbcCsvDirText.weightx = 1.0D;  // Generated
				gbcCsvDirText.gridx = 0;  // Generated
				gbcCsvDirText.gridy = 2;  // Generated
				csvDirText = new JTextField();
				csvDirText.setText(
						KoboPostProcPanel.applicationProps.getProperty(
								Constants.PROPKEY_DIRNAME_CSV));  // Generated
				csvDirText.setEditable(false);
				
				GridBagConstraints gbcCsvStorButton = new GridBagConstraints();
				gbcCsvStorButton.insets = new Insets(0, 3, 2, 0);  // Generated
				gbcCsvStorButton.gridy = 2;  // Generated
				gbcCsvStorButton.anchor = GridBagConstraints.EAST;  // Generated
				gbcCsvStorButton.gridx = 1;  // Generated
				
				CSVStorPan = new JPanel();
				CSVStorPan.setLayout(new GridBagLayout());  // Generated
				CSVStorPan.setBorder(BorderFactory.createTitledBorder(null, "CSV Storage Directory", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));  // Generated
				CSVStorPan.add(getCsvStorageButton(), gbcCsvStorButton);  // Generated
				CSVStorPan.add(csvDirText, gbcCsvDirText);  // Generated
			} catch (java.lang.Throwable e) {
				logger.log(Level.SEVERE, "could not generate CSV storage panel", e);
			}
		}
		return CSVStorPan;
	}

}
