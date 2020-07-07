package sample;

import lombok.extern.slf4j.Slf4j;
import sample.data.ContainerSerde;
import sample.data.PayloadProcessor;

@Slf4j
public class Main {
  public static void main(String[] args) {
    log.info("main");
    var serde = new ContainerSerde();
    var processor = new PayloadProcessor();
    log.info(serde.getClass().getSimpleName());
    log.info(processor.getClass().getSimpleName());
    serde.close();
  }
}
