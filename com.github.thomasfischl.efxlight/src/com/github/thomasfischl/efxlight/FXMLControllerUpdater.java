package com.github.thomasfischl.efxlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

public class FXMLControllerUpdater {

  private IProgressMonitor monitor;
  private IPackageFragment pack;
  private ICompilationUnit cu;
  private String className;

  public FXMLControllerUpdater(IFile fxmlFile, IProgressMonitor monitor) throws JavaModelException {
    this.monitor = monitor;

    className = fxmlFile.getName().substring(0, fxmlFile.getName().indexOf("."));

    IJavaProject javaProject = JavaCore.create(fxmlFile.getProject());
    IJavaElement packageFolder = JavaCore.create(fxmlFile.getParent());

    IPackageFragmentRoot root = null;
    for (IPackageFragmentRoot fragment : javaProject.getPackageFragmentRoots()) {
      if ("src".equals(fragment.getElementName())) {
        root = fragment;
      }
    }

    if (root == null) {
      throw new IllegalStateException("No source folder found for project '" + javaProject.getElementName() + "'.");
    }

    root.open(monitor);

    pack = root.getPackageFragment(packageFolder.getElementName());
    pack.open(monitor);
  }

  public void updateClass(FXMLAnalyser analyser) throws JavaModelException {
    cu = pack.getCompilationUnit(className + ".java");

    if (!cu.exists()) {
      cu = createControllerClass();
    }

    addImports();

    IType type = cu.getType(className);
    if (!type.exists()) {
      String baseClass = analyser.getBaseClass();
      if (baseClass.contains(".")) {
        baseClass = baseClass.substring(baseClass.lastIndexOf(".") + 1);
      }
      type = cu.createType("public class " + className + " extends " + baseClass + " {}", null, true, monitor);
    }

    Map<String, String> controlFields = new HashMap<>(analyser.getControllerElements());
    updateFields(controlFields, type);

    List<FXMLListener> controlActions = new ArrayList<>(analyser.getControllerActions());
    updateActions(controlActions, type);

    updateConstructor(type);
  }

  private void updateConstructor(IType type) throws JavaModelException {
    boolean hasConstructor = false;

    for (IMethod method : type.getMethods()) {
      if (method.isConstructor()) {
        hasConstructor = true;
      }
    }

    if (!hasConstructor) {

      StringBuffer sb = new StringBuffer();

      sb.append("public " + className + "() {");
      sb.append("  FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(\"" + className + ".fxml\"));");
      sb.append("  fxmlLoader.setRoot(this);");
      sb.append("  fxmlLoader.setController(this);");
      sb.append("  try {");
      sb.append("    fxmlLoader.load();");
      sb.append("  } catch (IOException e) {");
      sb.append("    throw new IllegalStateException(e);");
      sb.append("  }");
      sb.append("}");

      type.createMethod(sb.toString(), null, true, monitor);
    }
  }

  private void updateFields(Map<String, String> controlFields, IType type) throws JavaModelException {
    Iterator<Entry<String, String>> it = controlFields.entrySet().iterator();

    IField[] fields = type.getFields();
    while (it.hasNext()) {
      Entry<String, String> entry = it.next();

      for (IField field : fields) {
        if (entry.getKey().equals(field.getElementName())) {
          it.remove();
          break;
        }
      }
    }

    IField lastField = null;
    if (fields != null && fields.length > 0) {
      lastField = fields[fields.length - 1];
    }
    for (Entry<String, String> entry : controlFields.entrySet()) {
      lastField = type.createField("@FXML private " + entry.getValue() + " " + entry.getKey() + ";", lastField, true, monitor);
    }
  }

  private void updateActions(List<FXMLListener> controlActions, IType type) throws JavaModelException {
    Iterator<FXMLListener> it = controlActions.iterator();

    IMethod[] methods = type.getMethods();
    while (it.hasNext()) {
      FXMLListener entry = it.next();

      for (IMethod method : methods) {
        if (entry.getNormalizedName().equals(method.getElementName())) {
          it.remove();
          break;
        }
      }
    }

    IMethod lastMethod = null;
    if (methods != null && methods.length > 0) {
      lastMethod = methods[methods.length - 1];
    }
    for (FXMLListener entry : controlActions) {
      String methodCode = null;
      String methodName = entry.getNormalizedName();
      if ("onAction".equals(entry.getMethodType())) {
        methodCode = "@FXML private void " + methodName + "(ActionEvent e) { \n //TODO implement me \n}";
      }

      if (methodCode != null) {
        lastMethod = type.createMethod(methodCode, lastMethod, true, monitor);
      } else {
        System.out.println("No method template for type '" + entry.getMethodType() + "' available.");
      }
    }
  }

  private void addImports() throws JavaModelException {
    String[] imports = { "javafx.fxml.FXML", "java.lang.*", "java.util.*", "javafx.geometry.*", "javafx.scene.control.*",
        "javafx.scene.layout.*", "javafx.scene.paint.*", "javafx.event.*" };
    for (String importPackage : imports) {
      cu.createImport(importPackage, null, monitor);
    }
  }

  public void formatCode() throws JavaModelException {
    CodeFormatter formatter = ToolFactory.createCodeFormatter(null);
    TextEdit formatEdit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, cu.getSource(), 0, cu.getSource().length(), 0, null);
    cu.applyTextEdit(formatEdit, monitor);
  }

  public void organizeImports(Shell shell) {
    Runnable job = new Runnable() {
      @Override
      public void run() {

        IEditorPart editor;
        try {
          editor = JavaUI.openInEditor(cu);

          OrganizeImportsAction org = new OrganizeImportsAction(editor.getSite());
          IStructuredSelection selection = new StructuredSelection(cu);
          org.run(selection);

        } catch (PartInitException | JavaModelException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    };
    shell.getDisplay().syncExec(job);
  }

  private ICompilationUnit createControllerClass() throws JavaModelException {
    StringBuffer buffer = new StringBuffer();
    buffer.append("package " + pack.getElementName() + ";\n");
    buffer.append("\n");
    return pack.createCompilationUnit(className + ".java", buffer.toString(), false, null);
  }

}
