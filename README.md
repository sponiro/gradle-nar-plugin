# gradle-nar-plugin

A gradle plugin to create nar files for [Apache nifi](http://nifi.apache.org).

## Installation
To use the plugin, add the bintray repository to your script and add the plugin dependency:

```groovy
buildscript {
    repositories {
        mavenCentral()
        maven {
            url 'http://dl.bintray.com/sponiro/gradle-plugins'
        }
    }
    dependencies {
        classpath group: 'de.fanero.gradle.plugin.nar', name: 'gradle-nar-plugin', version: '0.4'
    }
}

apply plugin: 'de.fanero.gradle.plugin.nar'
```
## Usage

Run `gradle nar` to execute the nar task and create a nar archive.

## Configuration

This plugin depends on the JavaPlugin.
If it does not exist it will add it to the build.

### Nar Task
The plugin adds a new preconfigured task of type Nar named `nar` to the project.
The Nar class extends Jar and can be modified as such.
The task is configured to add all runtime dependencies and the jar archive itself to the nar archive.

### Nar Parent
Nar archives can have a parent nar.
The parent is optional and there can be at maximum one parent.
The parent relationship is added to the manifest.
To tell the plugin to add a parent you have to add a nar dependency to the nar configuration.
The nar configuration is created by the plugin.
Add the parent nar like this:

```groovy
dependencies {
    nar 'org.apache.nifi:nifi-standard-services-api-nar:0.2.1'
}
```

If you add more than one dependency, it will complain and crash the build.

## Manifest

The manifest of a nar file contains properties to identify the nar file and a parent nar.
This plugin configures the manifest generation to contain the values from the project name, group and version.
The same goes for the nar parent.

### Default manifest values

Manifest Property Key | Value
--- | ---
Nar-Group | project.group
Nar-Id | project.name
Nar-Version | project.version
Nar-Dependency-Group | nar config group
Nar-Dependency-Id | nar config name
Nar-Dependency-Version | nar config version

### Override manifest values
The plugin respects manifest overrides from the user.
For example:

```groovy
nar {
    manifest {
        attributes 'Nar-Group': 'overriden-nar-group-value'
    }
}
```

A full description can be found at https://docs.gradle.org/current/userguide/java_plugin.html#sec:jar.

## Shortcomings

Version 0.1 and 0.2 do not add the parent nar in the nar configuration to the dependencies.
To fix this you can either use version 0.3 or add the following code to your build:

```groovy
configurations {
    compileOnly.extendsFrom(configurations.nar)
}
```


### Service Locator

Apache nifi uses the [ServiceLocator](http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html) to define processors. The [Processor API](https://nifi.apache.org/docs/nifi-docs/html/developer-guide.html#processor_api)
explains in detail how this works. Basically, you have to add a file in `META-INF/services` directory named
`org.apache.nifi.processor.Processor`. This text file contains a fully-qualified class names of your processors.
One per line.

# Contributions

## Version 0.4

Thanks to [Lars Winderling](https://github.com/kaHaleMaKai) for making the plugin ready for Gradle 6.
