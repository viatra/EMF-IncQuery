package org.eclipse.incquery.runtime.patternregistry.sources;


public class PatternSourcePlugin {

    // Rework
    // private static final String QUERY_EXPLORER_ANNOTATION = "QueryExplorer";
    //
    // private static Map<Pattern, IMatcherFactory<?>> collectGeneratedMatcherFactories() {
    // Map<Pattern, IMatcherFactory<?>> factories = new HashMap<Pattern, IMatcherFactory<?>>();
    // for (IMatcherFactory<?> factory : MatcherFactoryRegistry.getContributedMatcherFactories()) {
    // Pattern pattern = factory.getPattern();
    // Boolean annotationValue = getValueOfQueryExplorerAnnotation(pattern);
    // if (annotationValue != null && annotationValue) {
    // factories.put(pattern, factory);
    // }
    // }
    // return factories;
    // }
    //
    // private static Boolean getValueOfQueryExplorerAnnotation(Pattern pattern) {
    // Annotation annotation = CorePatternLanguageHelper.getFirstAnnotationByName(pattern, QUERY_EXPLORER_ANNOTATION);
    // if (annotation == null) {
    // return null;
    // } else {
    // for (AnnotationParameter ap : annotation.getParameters()) {
    // if (ap.getName().equalsIgnoreCase("display")) {
    // return Boolean.valueOf(((BoolValueImpl) ap.getValue()).isValue());
    // }
    // }
    // return Boolean.TRUE;
    // }
    // }

}
