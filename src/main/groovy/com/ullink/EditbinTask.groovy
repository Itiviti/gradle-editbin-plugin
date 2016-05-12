package com.ullink
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

public class EditbinTask extends DefaultTask {
    final static String EditbinFileName = 'editbin.exe'
    final static String VarSetupBatName = 'vcvars32.bat'

    def targetFileName
    boolean enableLargeAddressAware
    String editbinFolder
    boolean useEnvSetupBat = false

    @TaskAction
    def doIt() {
        if (!enableLargeAddressAware) {
            logger.info 'enableLargeAddressAware is false, skipping.'
            return
        }

        if (targetFileName == null) {
            throw new GradleException("editbin.targetFileName not set")
        }

        def installedEditbinFolder = findInstalledEditbinFolderPath()
        if(editbinFolder == null) {
            editbinFolder = installedEditbinFolder
        }

        def editBinFile = new File(editbinFolder, EditbinFileName)
        if(!editBinFile.exists()) {
            throw new GradleException("'$editBinFile' doesn exist.")
        }
        logger.info "The path of $EditbinFileName is '$editBinFile'."
        if(useEnvSetupBat) {
            def batFile = new File(editbinFolder, VarSetupBatName)
            if (!batFile.exists()) {
                throw new GradleException("'$batFile' doesn't exists.")
            }
            logger.info "The path of $VarSetupBatName is '$batFile'."
        }

        String destination = project.file(targetFileName).getAbsolutePath()
        def processBuilder = new ProcessBuilder('cmd.exe', '/c', getCommandLine(destination))
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(editBinFile.getParentFile())

        def process = processBuilder.start()
        def output = process.text
        if (process.exitValue() != 0)
            throw new GradleException(output)

        logger.info output
    }

    String getCommandLine(String destination) {
        String result = ""
        if(useEnvSetupBat) {
            result += "$VarSetupBatName & "
        }
        result += "$EditbinFileName /LARGEADDRESSAWARE ${destination}"
        result
    }

    String getProgramFiles() {
        System.getenv("programfiles(x86)") ?: System.getenv("programfiles")
    }

    String findInstalledEditbinFolderPath(){
        ['14.0', '12.0', '11.0', '10.0', '9.0', '8.0']
                .collect { it -> new File("${programFiles}/Microsoft Visual Studio $it/VC/bin/" + EditbinFileName) }
                .find { it -> it.exists() }?.getParentFile()?.path
    }
}