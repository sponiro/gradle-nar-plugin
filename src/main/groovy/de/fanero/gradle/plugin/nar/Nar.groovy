package de.fanero.gradle.plugin.nar

import org.gradle.api.tasks.bundling.Jar

/**
 * @author Robert Kühne
 */
class Nar extends Jar {

    Nar() {
        extension = 'nar'
    }
}
