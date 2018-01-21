package de.fanero.gradle.plugin.nar

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Robert Kühne
 */
class NarPluginTest extends Specification {

    public static final String PLUGIN = 'de.fanero.gradle.plugin.nar'
    public static final String NAR_TASK = 'nar'

    private Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "create empty project"() {

        when:
        project.apply plugin: PLUGIN

        then:
        project.plugins.hasPlugin(NarPlugin)
        project.plugins.hasPlugin(JavaPlugin)
    }

    def "project has a nar task"() {

        when:
        project.apply plugin: PLUGIN

        then:
        project.tasks[NAR_TASK]
    }

    def "has nar file extension"() {

        when:
        project.apply plugin: PLUGIN

        then:
        Nar nar = project.tasks[NAR_TASK]
        nar.extension == NAR_TASK
    }

    def "assemble dependsOn nar"() {

        when:
        project.apply plugin: PLUGIN

        then:
        project.tasks['assemble'].dependsOn.find { it instanceof Task && it.name == 'nar' } != null
    }

    def "nar has no Nar-Dependency-Id when no nar dependency is set"() {

        when:
        project.apply plugin: PLUGIN

        then:
        Nar nar = project.tasks[NAR_TASK]
        !nar.manifest.attributes.containsKey('Nar-Dependency-Id')
    }
}
