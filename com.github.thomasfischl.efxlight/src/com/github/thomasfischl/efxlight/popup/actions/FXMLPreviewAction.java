package com.github.thomasfischl.efxlight.popup.actions;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javafx.stage.Stage;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.github.thomasfischl.efxlight.EclipseUtil;
import com.github.thomasfischl.efxlight.PreviewStage;
import com.sun.javafx.application.PlatformImpl;

public class FXMLPreviewAction implements IObjectActionDelegate {

  private static boolean initFx;

  private ISelection selection;

  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

  public void selectionChanged(IAction action, ISelection selection) {
    this.selection = selection;
  }

  public void run(IAction action) {
    initFX();
    final IFile fxmlFile = EclipseUtil.getSelectedFile(selection);

    PlatformImpl.startup(new Runnable() {
      public void run() {
        Stage s = new PreviewStage(fxmlFile);
        s.show();
      }
    });
  }

  private static void initFX() {
    if (initFx) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
        public Void run() {
          System.setProperty("javafx.macosx.embedded", "true");
          return null;
        }
      });
      initFx = true;
    }
  }

}
