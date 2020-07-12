package sample.processor;

import com.github.jknack.handlebars.Template;
import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import sample.annotation.MyType;
import sample.processor.template.JavaSource;
import sample.processor.template.ProcessorTemplate;

@Slf4j
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes({"sample.annotation.MyType", "sample.annotation.MyField"})
public class AvroSchemaGenerator extends AbstractProcessor {

  @SneakyThrows
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) {
      return false;
    }
    for (TypeElement annotation : annotations) {
      log.info("passed annotation: {}", annotation.toString());
    }

    Set<? extends Element> myTypeElements = roundEnv.getElementsAnnotatedWith(MyType.class);
    for (Element e : myTypeElements) {
      if (e.toString().endsWith("Builder")) {
        continue;
      }
      String className = e.toString();
      writeSerdeFile(className);
      writeProcessorFile(className);
    }
    return true;
  }

  private static final Template PROCESSOR_TEMPLATE = JavaSource.PROCESSOR.getTemplate();

  private void writeProcessorFile(String className) throws IOException {
    log.info("target class: {}", className);
    String packageName = null;
    int lastDot = className.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = className.substring(0, lastDot);
    }

    String simpleClassName = className.substring(lastDot + 1);
    String processorClassName = className + "Processor";
    String processorSimpleClassName = processorClassName.substring(lastDot + 1);

    ProcessorTemplate template = PROCESSOR_TEMPLATE.as(ProcessorTemplate.class);
    template.setPackageName(packageName);
    template.setClassName(processorSimpleClassName);
    template.setKeyType("String");
    template.setValueType(simpleClassName);

    JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(processorClassName);
    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      template.apply(null, out);
    }
  }

  private static final Template SERDE_TEMPLATE = JavaSource.SERDE.getTemplate();

  private void writeSerdeFile(String className) throws IOException {
    log.info("target class: {}", className);
    String packageName = null;
    int lastDot = className.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = className.substring(0, lastDot);
    }

    String simpleClassName = className.substring(lastDot + 1);
    String serdeClassName = className + "Serde";
    String serdeSimpleClassName = serdeClassName.substring(lastDot + 1);

    HashMap<String, String> map = new HashMap<>();
    map.put("packageName", packageName);
    map.put("className", serdeSimpleClassName);
    map.put("typeParameter", simpleClassName);

    JavaFileObject obj = processingEnv.getFiler().createSourceFile(serdeClassName);
    try (PrintWriter out = new PrintWriter(obj.openWriter())) {
      SERDE_TEMPLATE.apply(map, out);
    }
  }
}
