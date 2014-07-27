package com.github.thomasfischl.efxlight;

import java.io.FileInputStream;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.eclipse.core.resources.IFile;

public class PreviewStage extends Stage {

  public PreviewStage(IFile file) {
    setTitle("FXML - Preview - " + file.getName());
    init(file);
  }

  private void init(IFile file) {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setStaticLoad(true);

    try {
      Object control = fxmlLoader.load(new FileInputStream(file.getRawLocation().makeAbsolute().toFile()));
      if (control instanceof Parent) {
        setScene(new Scene((Parent) control, 800, 600));
        centerOnScreen();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}