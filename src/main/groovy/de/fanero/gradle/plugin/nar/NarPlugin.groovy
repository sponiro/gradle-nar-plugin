package de.fanero.gradle.plugin.nar

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin

/**
 * @author Robert Kühne
 */
class NarPlugin implements Plugin<Project> {

    public static final String NAR_TASK_NAME = 'nar'
    public static final String NAR_CONFIGURATION = 'nar'

    @Override
    void apply(Project project) {
        ensureJavaPlugin(project)
        Configuration conf = createNarConfiguration(project)
        createNarTask(project, conf)
    }

    private void ensureJavaPlugin(Project project) {
        if (!project.plugins.hasPlugin(JavaPlugin)) {
            project.plugins.apply(JavaPlugin)
        }
    }

    private Configuration createNarConfiguration(Project project) {
        Configuration narConfiguration = project.configurations.create(NAR_CONFIGURATION)
        project.configurations.compileOnly.extendsFrom(project.configurations.nar)
        narConfiguration.transitive = false
        narConfiguration
    }

    private void createNarTask(Project project, Configuration conf) {
        Nar nar = project.tasks.create(NAR_TASK_NAME, Nar)
        nar.setDescription("Assembles a nar archive containing the main classes jar and the runtimeClasspath configuration dependencies.")
        nar.setGroup(BasePlugin.BUILD_GROUP)
        nar.inputs.files(conf)

        configureBundledDependencies(project, nar)
        configureParentNarManifestEntry(nar, conf)

        project.tasks[BasePlugin.ASSEMBLE_TASK_NAME].dependsOn(nar)
    }

    private void configureBundledDependencies(Project project, Nar nar) {
        nar.bundledDependencies = [project.configurations.runtimeClasspath, project.tasks[JavaPlugin.JAR_TASK_NAME]]
    }

    private void configureParentNarManifestEntry(Nar nar, Configuration conf) {
        nar.parentNarConfiguration = conf
    }
}
