package de.fanero.gradle.plugin.nar

import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.java.archives.Attributes
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.bundling.Jar

/**
 * @author Robert Kühne
 */
class Nar extends Jar {

    @Internal
    List<Object> bundledDependencies

    @Internal
    Configuration parentNarConfiguration

    Nar() {
        archiveExtension.set('nar')
        bundledDependencies = []
        configureBundledDependencies()
        configureManifest()
        configureParentNarManifestEntry()
    }

    private void configureBundledDependencies() {
        configure {
            into('META-INF/bundled-dependencies') {
                from({ -> bundledDependencies })
            }
        }
    }

    private void configureManifest() {
        project.afterEvaluate {
            configure {
                Attributes attr = manifest.attributes
                attr.putIfAbsent(NarManifestEntry.NAR_GROUP.manifestKey, project.group)
                attr.putIfAbsent(NarManifestEntry.NAR_ID.manifestKey, project.name)
                attr.putIfAbsent(NarManifestEntry.NAR_VERSION.manifestKey, project.version)
            }
        }
    }

    private Task configureParentNarManifestEntry() {
        project.afterEvaluate {
            configure {
                if (parentNarConfiguration == null) return

                if (parentNarConfiguration.size() > 1) {
                    throw new RuntimeException("Only one parent nar dependency allowed in nar configuration but found ${parentNarConfiguration.size()} configurations")
                }

                if (parentNarConfiguration.size() == 1) {
                    Dependency parentNarDependency = parentNarConfiguration.allDependencies.first()
                    Attributes attr = manifest.attributes
                    attr.putIfAbsent(NarManifestEntry.NAR_DEPENDENCY_GROUP.manifestKey, parentNarDependency.group)
                    attr.putIfAbsent(NarManifestEntry.NAR_DEPENDENCY_ID.manifestKey, parentNarDependency.name)
                    attr.putIfAbsent(NarManifestEntry.NAR_DEPENDENCY_VERSION.manifestKey, parentNarDependency.version)
                }
            }
        }
    }
}
