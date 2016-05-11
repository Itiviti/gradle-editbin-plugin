package com.ullink
import org.gradle.api.Plugin
import org.gradle.api.Project

class EditbinPlugin implements Plugin<Project> {
    void apply(Project project){
        project.task('editbin', type: EditbinTask){
            boolean hasMsbuildPlugin = project.plugins.hasPlugin('msbuild')
            if (hasMsbuildPlugin) {
                dependsOn 'msbuild'
            }
        }
    }
}