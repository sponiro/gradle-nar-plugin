package de.fanero.gradle.plugin.nar

import org.gradle.api.Plugin
import org.gradle.api.Project
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

        if (!project.plugins.hasPlugin(JavaPlugin)) {
            project.plugins.apply(JavaPlugin)
        }

        Configuration conf = project.configurations.create(NAR_CONFIGURATION)
        conf.transitive = false

        configureArchive(project, conf)
    }

    private void configureArchive(Project project, Configuration conf) {
        Nar nar = project.tasks.create(NAR_TASK_NAME, Nar)
        nar.setDescription("Assembles a nar archive containing the main classes jar and the runtime configuration dependencies.")
        nar.setGroup(BasePlugin.BUILD_GROUP)
        nar.inputs.files(conf)
        nar.configure {
            into('META-INF/bundled-dependencies') {
                from(project.configurations.runtime, project.tasks.jar)
            }
            manifest {
                attributes 'Nar-Id': project.name
            }
        }

        nar.doFirst {
            if (conf.size() > 1) {
                throw new RuntimeException("Only one dependency allowed in nar configuration but found " + conf.size())
            }

            if (conf.size() == 1) {
                Dependency parent = conf.allDependencies.first()
                manifest {
                    attributes 'Nar-Dependency-Id': parent.name
                }
            }
        }
    }
}
