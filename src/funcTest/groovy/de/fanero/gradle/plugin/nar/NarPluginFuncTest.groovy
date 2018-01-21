package de.fanero.gradle.plugin.nar

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.jar.Manifest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @author Robert Kühne
 */
class NarPluginFuncTest extends Specification {

    private static final TEST_BASE_NAME = 'nar-test'
    private static final TEST_VERSION = '1.0'

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile
    File settingsFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
plugins {
    id 'de.fanero.gradle.plugin.nar'
}
nar {
    baseName '${TEST_BASE_NAME}'
}
group = 'de.fanero.test'
version = '${TEST_VERSION}'
"""
        settingsFile = testProjectDir.newFile('settings.gradle')
        settingsFile << """
rootProject.name = "nar-test"
"""
    }

    def "test simple nar"() {

        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('nar')
                .withPluginClasspath()
                .build()

        Manifest manifest = extractManifest()

        then:
        manifest != null
        manifest.getMainAttributes().getValue('Nar-Id') == 'nar-test'
        manifest.getMainAttributes().getValue('Nar-Dependency-Id') == null
    }

    def "test parent nar entry"() {

        buildFile << """
repositories {
    mavenCentral()
}
dependencies {
    nar 'org.apache.nifi:nifi-standard-services-api-nar:0.2.1'
}
"""
        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('nar')
                .withPluginClasspath()
                .build()

        Manifest manifest = extractManifest()

        then:
        manifest != null
        manifest.getMainAttributes().getValue('Nar-Id') == 'nar-test'
        manifest.getMainAttributes().getValue('Nar-Dependency-Id') == 'nifi-standard-services-api-nar'
    }

    def "test multiple parent nar entries"() {

        buildFile << """
repositories {
    mavenCentral()
}
dependencies {
    nar 'org.apache.nifi:nifi-standard-services-api-nar:0.2.1'
    nar 'org.apache.nifi:nifi-enrich-nar:1.5.0'
}
"""
        expect:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('nar')
                .withPluginClasspath()
                .buildAndFail()
    }

    def "test "() {

        buildFile << """
repositories {
    mavenCentral()
}
dependencies {
    nar 'org.apache.nifi:nifi-standard-services-api-nar:0.2.1'
}
"""
        expect:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('nar')
                .withPluginClasspath()
                .build()
    }

    Manifest extractManifest() {
        Manifest manifest = null
        narFile().withInputStream {
            ZipInputStream zip = new ZipInputStream(it)
            ZipEntry entry = zip.nextEntry
            while (entry != null) {
                if (entry.name == 'META-INF/MANIFEST.MF') {
                    manifest = new Manifest(zip)
                    break
                } else {
                    entry = zip.nextEntry
                }
            }
        }
        manifest
    }

    private File narFile() {
        new File(testProjectDir.root, "build/libs/${TEST_BASE_NAME}-${TEST_VERSION}.nar")
    }
}
