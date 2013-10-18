package org.oyrm.kobo.postproc.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.oyrm.kobo.postproc.constants.Constants;
import org.oyrm.kobo.postproc.ui.KoboPostProcFrame;
import org.oyrm.kobo.postproc.ui.PreferencesDialog;

public class ShowSettingsAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8149887900456909754L;

	public ShowSettingsAction() {
		super(Constants.TEXT_MENUITEM_OPTIONACTION);
	}
	
	public void actionPerformed(ActionEvent settingsEvent) {
		new PreferencesDialog(KoboPostProcFrame.getInstance());
	}
}
