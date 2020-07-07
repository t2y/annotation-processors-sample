package sample.processor.template;

import com.github.jknack.handlebars.TypeSafeTemplate;
import org.apache.kafka.streams.processor.Processor;

@SuppressWarnings("rawtypes")
public interface ProcessorTemplate extends TypeSafeTemplate<Processor> {
  public ProcessorTemplate setPackageName(String name);

  public ProcessorTemplate setClassName(String name);

  public ProcessorTemplate setKeyType(String keyType);

  public ProcessorTemplate setValueType(String valueType);
}
