package sample.processor;

import com.github.jknack.handlebars.Handlebars;
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
import lombok.extern.slf4j.Slf4j;
import sample.processor.template.ProcessorTemplate;

@Slf4j
@SupportedAnnotationTypes("org.apache.avro.specific.AvroGenerated")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class AvroSchemaGenerator extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
      for (Element e : annotatedElements) {
        if (e.toString().endsWith("Builder")) {
          continue;
        }
        try {
          String className = e.toString();
          writeSerdeFile(className);
          writeProcessorFile(className);
        } catch (IOException exe) {
          exe.printStackTrace();
        }
      }
    }
    return true;
  }

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

    Handlebars handlebars = new Handlebars();
    ProcessorTemplate template =
        handlebars.compile("processor_template").as(ProcessorTemplate.class);
    template.setPackageName(packageName);
    template.setClassName(processorSimpleClassName);
    template.setKeyType("String");
    template.setValueType(simpleClassName);

    JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(processorClassName);
    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      template.apply(null, out);
    }
  }

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

    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("serde_template");

    JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(serdeClassName);
    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      template.apply(map, out);
    }
  }
}
