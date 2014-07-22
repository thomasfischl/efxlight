package com.github.thomasfischl.efxlight;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.statushandlers.StatusManager;

public class DialogUtil {

  public static void openError(Shell parent, String title, String message, PartInitException exception) {
    // Check for a nested CoreException
    CoreException nestedException = null;
    IStatus status = exception.getStatus();
    if (status != null && status.getException() instanceof CoreException) {
      nestedException = (CoreException) status.getException();
    }

    IStatus errorStatus = null;

    if (nestedException != null) {
      IStatus stat = nestedException.getStatus();
      errorStatus = new Status(stat.getSeverity(), stat.getPlugin(), stat.getCode(), message, stat.getException());

    } else {
      // Open a regular error dialog since there is no
      // extra information to displa
      errorStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
    }

    StatusManager.getManager().handle(errorStatus, StatusManager.SHOW);
  }
}
