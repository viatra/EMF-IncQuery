package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm.template;


public class AsmConditionalTryTemplate
{
  protected static String nl;
  public static synchronized AsmConditionalTryTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    AsmConditionalTryTemplate result = new AsmConditionalTryTemplate();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "try{";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "} " + NL + "catch(RuleFailedException e) {}" + NL + "finally{";
  protected final String TEXT_4 = NL;
  protected final String TEXT_5 = NL + "}";
  protected final String TEXT_6 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    AsmConditionalTryData data = (AsmConditionalTryData) argument;
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(data.getTrueB());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(data.getFalseB());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
