package com.github.thomasfischl.efxlight.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.github.thomasfischl.efxlight.FXMLAnalyser;
import com.github.thomasfischl.efxlight.FXMLControllerUpdater;

public class UpdateControllerAction implements IObjectActionDelegate {

  private Shell shell;

  private ISelection selection;

  public UpdateControllerAction() {
    super();
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    shell = targetPart.getSite().getShell();
  }

  public void selectionChanged(IAction action, ISelection selection) {
    this.selection = selection;
  }

  public void run(IAction action) {
    IProgressMonitor monitor = new NullProgressMonitor();

    try {
      if (selection instanceof TreeSelection) {
        Object element = ((TreeSelection) selection).getFirstElement();
        if (element instanceof IFile) {
          IFile fxmlFile = (IFile) element;

          FXMLAnalyser analyser = new FXMLAnalyser();
          analyser.parseFXML(fxmlFile);

          FXMLControllerUpdater updater = new FXMLControllerUpdater(fxmlFile, monitor);
          updater.updateClass(analyser);
          updater.organizeImports(shell);
          updater.formatCode();
        }
      }
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }

    // MessageDialog.openInformation(shell, "Eclipse Fx Light",
    // "Test was executed.");
  }

}
