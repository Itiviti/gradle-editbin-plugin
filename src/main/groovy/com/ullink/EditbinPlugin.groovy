package com.ullink

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.DefaultTask

class EditbinPlugin implements Plugin<Project> {
    void apply(Project project){

        project.task('editbin', type: EditbinTask){
            boolean hasMsbuildPlugin = project.plugins.hasPlugin('msbuild')
            if (hasMsbuildPlugin) {
                dependsOn 'msbuild'
            }
            doLast {
                if (!enableLargeAddressAware)
                    return;

                println targetFileName
                if (targetFileName == null)
                    throw new GradleException("editbin.targetFileName not set")

                def destination = project.file(targetFileName).getAbsolutePath()
                def editBinFile = findEditBin()
                if (editBinFile == null)
                    throw new GradleException("editbin file not found. Consider Installing Windows SDK or Visual C++ Build Tools")

                println "editbin Path: ${editBinFile}"

                def processBuilder=new ProcessBuilder("cmd.exe","/c", "vcvars32.bat & editbin.exe /LARGEADDRESSAWARE ${destination}")
                processBuilder.redirectErrorStream(true)
                processBuilder.directory(editBinFile.getParentFile())

                def process = processBuilder.start()
                def output = process.text
                if (process.exitValue() != 0)
                    throw new GradleException(output)

                println output
            }
        }
    }



    File findEditBin(){
        def programFiles = System.getenv("programfiles(x86)") ?: System.getenv("programfiles")
        ['14.0', '12.0', '11.0', '10.0', '9.0', '8.0']
        .collect { it -> new File("${programFiles}/Microsoft Visual Studio $it/VC/bin/editbin.exe") }
        .find { it -> it.exists() }
    }
}

public class EditbinTask extends DefaultTask {

    def targetFileName
    boolean enableLargeAddressAware
}