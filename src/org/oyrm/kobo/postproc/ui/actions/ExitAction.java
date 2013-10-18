package org.oyrm.kobo.postproc.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.oyrm.kobo.postproc.ui.KoboPostProcFrame;

public class ExitAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5426591681841055809L;
	public ExitAction() {
		super("Exit");
	}

	public void actionPerformed(ActionEvent e) {
		KoboPostProcFrame.getInstance().exit();
	}

}
