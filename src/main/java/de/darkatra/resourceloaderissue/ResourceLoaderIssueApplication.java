package de.darkatra.resourceloaderissue;

import java.io.IOException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class ResourceLoaderIssueApplication implements ApplicationRunner {

    public static void main(final String[] args) {
        SpringApplication.run(ResourceLoaderIssueApplication.class, args);
    }

    @Override
    public void run(final ApplicationArguments args) throws IOException {

        final ResourceLoader resourceLoader = new DefaultResourceLoader();

        final Resource jarResource = resourceLoader.getResource("jar:file:/data/app.jar!/");

        // expecting UrlResource for the given path
        log.info(jarResource.getClass().getSimpleName() + ": " + jarResource.getURL() + " exists: " + jarResource.exists());
    }
}
