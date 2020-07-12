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

For debugging, pass debug jvm arguments to gradle.

```bash
$ ./gradlew --info :app:compileJava -Dorg.gradle.jvmargs='-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005'
```
