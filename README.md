# gradle-nar-plugin

A gradle plugin to create nar files for [Apache nifi](http://nifi.apache.org).

## Installation
To use the plugin, add the bintray repository to your script:

```groovy
buildscript {
    repositories {
        mavenCentral()
        maven {
            url 'http://dl.bintray.com/sponiro/gradle-plugins'
        }
    }
    dependencies {
                classpath group: 'de.fanero.gradle.plugin.nar', name: 'gradle-nar-plugin', version: '0.1'
    }
}

apply plugin: 'de.fanero.gradle.plugin.nar'
```
## Usage

Run `gradle nar` to execute the nar task and create a nar archive.

## Configuration

This plugin depends on the JavaPlugin. If it does not exist it will add it to the build.

### Nar Task
It adds a new preconfigured task of type Nar named `nar` to the project. The Nar task extends the Jar class and can be modified
as such. The task is configured to add all runtime dependencies to the archive. Also the jar, from the output of the `jar` task,
is added to the archive.

### Nar Parent
Nar archives can have a parent nar. The parent is optional and there can be at maximum one parent. The parent relationship
is added to the manifest. To tell the plugin to add a parent you have to add a nar dependency to the nar configuration.
The nar configuration is added by the plugin. Add the parent nar like this:

```groovy
dependencies {
    nar 'org.apache.nifi:nifi-standard-services-api-nar:0.2.1'
}
```

If you add more than one dependency, it will complain and crash the build.

### Service Locator

Apache nifi uses the [ServiceLocator](http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html) to define processors. The [Processor API](https://nifi.apache.org/docs/nifi-docs/html/developer-guide.html#processor_api)
explains in detail how this works. Basically, you have to add a file in `META-INF/services` directory named
`org.apache.nifi.processor.Processor`. This text file contains a fully-qualified class names of your processors.
One per line.

