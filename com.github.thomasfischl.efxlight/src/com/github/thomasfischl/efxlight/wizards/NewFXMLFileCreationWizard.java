package com.github.thomasfischl.efxlight.wizards;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.github.thomasfischl.efxlight.DialogUtil;



public class NewFXMLFileCreationWizard extends BasicNewResourceWizard {

  private WizardNewFileCreationPage mainPage;

  public NewFXMLFileCreationWizard() {
    super();
  }

  public void addPages() {
    super.addPages();
    mainPage = new WizardNewFileCreationPage("New FXML File", getSelection());
    mainPage.setTitle("FXML File");
    mainPage.setDescription("Create a new FXML file resource.");
    mainPage.setFileExtension("fxml");
    addPage(mainPage);
  }

  public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
    super.init(workbench, currentSelection);
    setWindowTitle("New FXML File");
    setNeedsProgressMonitor(true);
  }

  // protected void initializeDefaultPageImageDescriptor() {
  //    ImageDescriptor desc = IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/newfile_wiz.png");//$NON-NLS-1$
  // setDefaultPageImageDescriptor(desc);
  // }

  public boolean performFinish() {
    IFile file = mainPage.createNewFile();
    if (file == null) {
      return false;
    }

    try {

      StringBuffer sb = new StringBuffer();

      sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      sb.append("<?import java.lang.*?>");
      sb.append("<?import java.util.*?>");
      sb.append("<?import javafx.geometry.*?>");
      sb.append("<?import javafx.scene.control.*?>");
      sb.append("<?import javafx.scene.layout.*?>");
      sb.append("<?import javafx.scene.paint.*?>");
      sb.append("<fx:root type=\"javafx.scene.layout.AnchorPane\" xmlns:fx=\"http://javafx.com/fxml\">");
      sb.append("</fx:root>");

      file.setContents(new ByteArrayInputStream(sb.toString().getBytes()), true, true, null);
    } catch (CoreException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    selectAndReveal(file);

    // Open editor on new file.
    IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
    try {
      if (dw != null) {
        IWorkbenchPage page = dw.getActivePage();
        if (page != null) {
          IDE.openEditor(page, file, true);
        }
      }
    } catch (PartInitException e) {
      DialogUtil.openError(dw.getShell(), "Problems Opening Editor", e.getMessage(), e);
    }

    return true;
  }
}