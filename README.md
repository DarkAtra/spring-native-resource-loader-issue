Demonstrates an issue with the DefaultResourceLoader producting differents results when the app was compiled using GraalVM vs. when it runs on the JVM.
The Application tries to load a resource from an external jar `/data/app.jar` via:

```java
resourceLoader.getResource("jar:file:/data/app.jar!/");
```

When running on the JVM the above code yields an instance of `UrlResource`, just as expected.
When running natively the above code throws an exception:

```
java.lang.IllegalStateException: Failed to execute ApplicationRunner
 	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:761) ~[de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication:3.0.1]
 	at org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:748) ~[de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication:3.0.1]
 	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315) ~[de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication:3.0.1]
 	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1302) ~[de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication:3.0.1]
 	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1291) ~[de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication:3.0.1]
 	at de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication.main(ResourceLoaderIssueApplication.java:21) ~[de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication:na]
 Caused by: java.io.FileNotFoundException: class path resource [jar:file:/data/app.jar!/] cannot be resolved to URL because it does not exist
 	at org.springframework.core.io.ClassPathResource.getURL(ClassPathResource.java:226) ~[de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication:6.0.3]
 	at de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication.run(ResourceLoaderIssueApplication.java:32) ~[de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication:na]
 	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:758) ~[de.darkatra.resourceloaderissue.ResourceLoaderIssueApplication:3.0.1]
 	... 5 common frames omitted
```

It seems like the `DefaultResourceLoader` mistakenly thinks that the resource is a `ClassPathResource`.

## How to reproduce the issue locally?

Build the JVM docker image via:

```
mvn clean spring-boot:build-image
```

and the native docker image via:

```
mvn -Pnative clean spring-boot:build-image
```

The resulting images are named `spring-native-resource-loader-issue:0.0.1` and `spring-native-resource-loader-issue:0.0.1-native`.

Place any jar inside a folder of your choice and alter the following docker command accordingly:

```
docker run <image> -v <folder-that-contains-the-jar>:/data
```

In my case, the following command produces the exception:

```
docker run spring-native-resource-loader-issue:0.0.1-native -v /home/test:/data
```

For the fix, see: https://github.com/spring-projects/spring-framework/issues/29808
