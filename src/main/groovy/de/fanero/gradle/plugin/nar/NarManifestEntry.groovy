package de.fanero.gradle.plugin.nar

enum NarManifestEntry {
    NAR_GROUP("Nar-Group"),
    NAR_ID("Nar-Id"),
    NAR_VERSION("Nar-Version"),
    NAR_DEPENDENCY_GROUP("Nar-Dependency-Group"),
    NAR_DEPENDENCY_ID("Nar-Dependency-Id"),
    NAR_DEPENDENCY_VERSION("Nar-Dependency-Version")

    private final String manifestKey

    NarManifestEntry(String manifestKey) {
        this.manifestKey = manifestKey
    }

    String getManifestKey() {
        return manifestKey
    }
}