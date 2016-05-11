package com.ullink

import org.gradle.testfixtures.ProjectBuilder

import java.nio.file.Files
import java.nio.file.Paths

import static org.junit.Assert.*
import org.junit.Test

class EditbinTaskTest {
    @Test
    void whenEditbinFolderIsProvided_thenFindEditBinReturnsIt() {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def editBinFolder = File.createTempDir();

        def task = project.tasks.editbin
        task.editbinFolder = editBinFolder.path
        assertEquals(editBinFolder.path, task.findEditBin().path)

        editBinFolder.deleteOnExit()
    }

    @Test
    void whenEditBinIsPresentInProgramFiles_thenFindEditBinReturnsThePath() {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def programFilesFolder = File.createTempDir();
        def editBinPath = new File(programFilesFolder, 'Microsoft Visual Studio 14.0/VC/bin/editbin.exe');
        Files.createDirectories(Paths.get(editBinPath.path))
        editBinPath.createNewFile()

        def task = project.tasks.editbin
        task.metaClass.getProgramFiles = { programFilesFolder }
        assertEquals(editBinPath.path, task.findEditBin().path)

        programFilesFolder.deleteOnExit()
    }

    @Test
    void whenEditbinDoesntExist_thenFindEditBinReturnsNull() {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: EditbinPlugin
        def programFilesFolder = File.createTempDir();

        def task = project.tasks.editbin
        task.metaClass.getProgramFiles = { programFilesFolder }
        assertNull(task.findEditBin())

        programFilesFolder.deleteOnExit()
    }
}
