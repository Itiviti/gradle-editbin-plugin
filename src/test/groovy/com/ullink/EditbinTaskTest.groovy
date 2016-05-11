package com.ullink

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder

import java.nio.file.Files
import java.nio.file.Paths

import static org.junit.Assert.*
import org.junit.Test

class EditbinTaskTest {
    @Test
    void whenFindingInstalledEditbinFolderPath_thenReturnNullWhenExecFileDoesntExistsEvenIfFolderExists() {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def task = project.tasks.editbin

        def programFilesFolder = File.createTempDir();
        Files.createDirectories(Paths.get(new File(programFilesFolder, 'Microsoft Visual Studio 14.0/VC/bin/').path))
        task.metaClass.getProgramFiles = { programFilesFolder }
        assertEquals(null, task.findInstalledEditbinFolderPath())

        programFilesFolder.deleteOnExit()
    }

    @Test
    void whenFindingInstalledEditbinFolderPath_thenReturnProperPath() {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def task = project.tasks.editbin

        def programFilesFolder = File.createTempDir();
        def editBinFolder = new File(programFilesFolder, 'Microsoft Visual Studio 14.0/VC/bin/');
        Files.createDirectories(Paths.get(editBinFolder.path))
        new File(editBinFolder, 'editbin.exe').createNewFile()
        task.metaClass.getProgramFiles = { programFilesFolder }
        assertEquals(editBinFolder.path, task.findInstalledEditbinFolderPath())

        programFilesFolder.deleteOnExit()
    }

    @Test
    void doesntRunBatFileByDefaultBecauseUseEnvSetupBatIsFalse(){
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def task = project.tasks.editbin

        assertEquals(
                task.getCommandLine('dest.dll'),
                'editbin.exe /LARGEADDRESSAWARE dest.dll'
        );
    }

    @Test
    void runBatFile_whenUseEnvSetupBatIsTrue(){
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def task = project.tasks.editbin

        task.useEnvSetupBat = true

        assertEquals(
                task.getCommandLine('dest.dll'),
                'vcvars32.bat & editbin.exe /LARGEADDRESSAWARE dest.dll'
        );
    }

    @Test
    void whenEnableLargeAddressAwareIsFalse_thenNoExceptionAreThrown(){
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def task = project.tasks.editbin

        task.doIt()
    }

    @Test(expected = GradleException.class)
    void whenTargetFileNameIsNotProvided_thenAExceptionIsThrown(){
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def task = project.tasks.editbin

        task.enableLargeAddressAware = true

        task.doIt()
    }

    @Test(expected = GradleException.class)
    void whenEditbinFolderIsNotProvidedNeitherInstalled_thenAExceptionIsThrown(){
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def task = project.tasks.editbin

        task.enableLargeAddressAware = true
        task.targetFileName = 'my.dll'

        task.doIt()
    }

    @Test(expected = GradleException.class)
    void whenEditbinFileDoesntExist_thenAExceptionIsThrown(){
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def task = project.tasks.editbin

        task.editbinFolder = File.createTempDir()
        task.enableLargeAddressAware = true
        task.targetFileName = 'my.dll'

        task.doIt()
    }

    @Test(expected = GradleException.class)
    void whenEditbinFileExistsButNotTheBatFileWhenUsingTheBatFile_thenAExceptionIsThrown(){
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def task = project.tasks.editbin

        task.editbinFolder = File.createTempDir()
        task.useEnvSetupBat = true
        task.enableLargeAddressAware = true
        task.targetFileName = 'my.dll'

        task.doIt()
    }
}
