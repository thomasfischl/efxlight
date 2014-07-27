package com.github.thomasfischl.efxlight;

public class FXMLListener {

  private String name;

  private String methodType;

  public FXMLListener(String name, String methodType) {
    super();
    this.name = name;
    this.methodType = methodType;
  }

  public String getName() {
    return name;
  }

  public String getNormalizedName() {
    if (name.charAt(0) == '#') {
      name = name.substring(1);
    }

    if (Character.isUpperCase(name.charAt(0))) {
      name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
    return name;
  }

  public String getMethodType() {
    return methodType;
  }

}
