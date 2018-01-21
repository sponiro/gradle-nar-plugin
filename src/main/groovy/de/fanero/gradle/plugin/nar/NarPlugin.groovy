package de.fanero.gradle.plugin.nar

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
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
        narConfiguration.transitive = false
        narConfiguration
    }

    private void createNarTask(Project project, Configuration conf) {
        Nar nar = project.tasks.create(NAR_TASK_NAME, Nar)
        nar.setDescription("Assembles a nar archive containing the main classes jar and the runtime configuration dependencies.")
        nar.setGroup(BasePlugin.BUILD_GROUP)
        nar.inputs.files(conf)
        project.tasks[BasePlugin.ASSEMBLE_TASK_NAME].dependsOn(nar)

        configureBundledDependencies(project, nar)
        configureManifest(project, nar)
        configureParentNarManifestEntry(nar, conf)
    }

    private void configureBundledDependencies(Project project, Nar nar) {
        nar.configure {
            into('META-INF/bundled-dependencies') {
                // todo make hardcoded dependency resolution configurable
                from(project.configurations.runtime, project.tasks[JavaPlugin.JAR_TASK_NAME])
            }
        }
    }

    private void configureManifest(Project project, Nar nar) {
        nar.configure {
            manifest {
                attributes([
                        (NarManifestEntry.NAR_ID.manifestKey): project.name
                ])
            }
        }
    }

    private Task configureParentNarManifestEntry(Nar nar, conf) {
        nar.doFirst {
            if (conf.size() > 1) {
                throw new RuntimeException("Only one parent nar dependency allowed in nar configuration but found ${conf.size()} configurations")
            }

            if (conf.size() == 1) {
                Dependency parentNarDependency = conf.allDependencies.first()
                manifest {
                    attributes([
                            (NarManifestEntry.NAR_DEPENDENCY_ID.manifestKey): parentNarDependency.name
                    ])
                }
            }
        }
    }
}
