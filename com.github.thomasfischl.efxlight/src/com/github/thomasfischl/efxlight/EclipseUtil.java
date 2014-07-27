package com.github.thomasfischl.efxlight;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;

public class EclipseUtil {

  public static IFile getSelectedFile(ISelection selection) {
    if (selection instanceof TreeSelection) {
      Object element = ((TreeSelection) selection).getFirstElement();
      if (element instanceof IFile) {
        return (IFile) element;
      }
    }
    return null;
  }

}
