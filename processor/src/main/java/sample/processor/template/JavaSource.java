package sample.processor.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import lombok.SneakyThrows;

public enum JavaSource {
  SERDE("serde_template"),
  PROCESSOR("processor_template");

  private final String filename;
  private final Handlebars handlebars = new Handlebars();

  JavaSource(String filename) {
    this.filename = filename;
  }

  @SneakyThrows
  public Template getTemplate() {
    return this.handlebars.compile(this.filename);
  }
}
