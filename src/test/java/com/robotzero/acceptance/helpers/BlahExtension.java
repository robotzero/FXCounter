package com.robotzero.acceptance.helpers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;

public class BlahExtension extends ApplicationExtension {

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    if (parameterContext.getParameter().getType().isAssignableFrom(FxRobot.class)) {
      return true;
    }
    return true;
    //    return super.supportsParameter(parameterContext, extensionContext);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    if (parameterContext.getParameter().getType().isAssignableFrom(FxRobot.class)) {
      return super.resolveParameter(parameterContext, extensionContext);
    }
    return null;
  }
}
