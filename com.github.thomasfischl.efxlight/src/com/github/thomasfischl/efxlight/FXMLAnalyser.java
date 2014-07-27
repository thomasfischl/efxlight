package com.github.thomasfischl.efxlight;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.core.resources.IFile;

public class FXMLAnalyser {

  private String baseClass;
  private Map<String, String> controllerElements;
  private List<FXMLListener> controllerActions;

  public void clearData() {
    baseClass = null;
    controllerElements = new HashMap<String, String>();
    controllerActions = new ArrayList<FXMLListener>();
  }

  public String getBaseClass() {
    return baseClass;
  }

  public Map<String, String> getControllerElements() {
    return controllerElements;
  }

  public List<FXMLListener> getControllerActions() {
    return controllerActions;
  }

  public void parseFXML(IFile fxmlFile) throws IOException, XMLStreamException {
    clearData();

    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(fxmlFile.getRawLocation().makeAbsolute().toFile()));

    while (reader.hasNext()) {
      int event = reader.next();

      switch (event) {
      case XMLStreamConstants.START_ELEMENT:
        // System.out.println(reader.getLocalName());
        String elemName = createXmlName(reader.getPrefix(), reader.getLocalName());

        if ("fx:root".equals(elemName)) {
          for (int idx = 0; idx < reader.getAttributeCount(); idx++) {
            String attrName = createXmlName(reader.getAttributePrefix(idx), reader.getAttributeLocalName(idx));
            String value = reader.getAttributeValue(idx);

            if ("type".equals(attrName)) {
              baseClass = value;
            }
          }
        }

        for (int idx = 0; idx < reader.getAttributeCount(); idx++) {
          String attrName = createXmlName(reader.getAttributePrefix(idx), reader.getAttributeLocalName(idx));
          String value = reader.getAttributeValue(idx);

          if ("fx:id".equals(attrName)) {
            controllerElements.put(value, reader.getLocalName());
          }

          if ("onAction".equals(attrName)) {
            controllerActions.add(new FXMLListener(value, "onAction"));
          }
        }
        break;
      }
    }
  }

  private String createXmlName(String prefix, String localName) {
    if (prefix != null && !prefix.isEmpty()) {
      return prefix + ":" + localName;
    }
    return localName;
  }

}
