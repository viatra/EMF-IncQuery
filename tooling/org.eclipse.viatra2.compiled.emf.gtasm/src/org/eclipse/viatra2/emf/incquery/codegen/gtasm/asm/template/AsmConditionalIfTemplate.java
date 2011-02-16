package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm.template;


public class AsmConditionalIfTemplate
{
  protected static String nl;
  public static synchronized AsmConditionalIfTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    AsmConditionalIfTemplate result = new AsmConditionalIfTemplate();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "if(";
  protected final String TEXT_2 = "){";
  protected final String TEXT_3 = NL;
  protected final String TEXT_4 = NL + "}";
  protected final String TEXT_5 = " " + NL + "else {";
  protected final String TEXT_6 = NL;
  protected final String TEXT_7 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    AsmConditionalIfData data = (AsmConditionalIfData) argument;
    stringBuffer.append(TEXT_1);
    stringBuffer.append(data.getCondition().getTerm().toString());
    stringBuffer.append(TEXT_2);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(data.getTrueB());
    stringBuffer.append(TEXT_4);
    if(data.getFalseB() != null){
    stringBuffer.append(TEXT_5);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(data.getFalseB());
    stringBuffer.append(TEXT_7);
    }
    return stringBuffer.toString();
  }
}
