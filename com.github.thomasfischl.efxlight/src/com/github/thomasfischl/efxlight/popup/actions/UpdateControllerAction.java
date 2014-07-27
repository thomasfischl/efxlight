package com.github.thomasfischl.efxlight.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.github.thomasfischl.efxlight.EclipseUtil;
import com.github.thomasfischl.efxlight.FXMLAnalyser;
import com.github.thomasfischl.efxlight.FXMLControllerUpdater;
import com.github.thomasfischl.efxlight.LogFacade;

public class UpdateControllerAction implements IObjectActionDelegate {

  private Shell shell;

  private ISelection selection;

  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    shell = targetPart.getSite().getShell();
  }

  public void selectionChanged(IAction action, ISelection selection) {
    this.selection = selection;
  }

  public void run(IAction action) {
    IProgressMonitor monitor = new NullProgressMonitor();

    try {
      IFile fxmlFile = EclipseUtil.getSelectedFile(selection);

      if (fxmlFile == null) {
        LogFacade.logInfo("No file was seleced");
        return;
      }

      FXMLAnalyser analyser = new FXMLAnalyser();
      analyser.parseFXML(fxmlFile);

      FXMLControllerUpdater updater = new FXMLControllerUpdater(fxmlFile, monitor);
      updater.updateClass(analyser);
      updater.organizeImports(shell);
      updater.formatCode();
      updater.save();
    } catch (Exception e) {
      LogFacade.logError("An error occurs during updating FXML controller.", e);
      MessageDialog.openError(shell, "Error", e.getMessage());
    }
  }

}
