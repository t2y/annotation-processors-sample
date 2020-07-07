# annotation-processors-sample

annotation processors sample code

# How to build

```bash
$ ./gradlew :app:clean :app:compileJava
$ tree app/build/generated/sources/annotationProcessor/java/main
app/build/generated/sources/annotationProcessor/java/main
`-- sample
    `-- data
        |-- ContainerProcessor.java
        |-- ContainerSerde.java
        |-- PayloadProcessor.java
        `-- PayloadSerde.java
```
