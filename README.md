#Gradle plugin for invoking editbin

##Usages

###Set _\<a DLL file path\>_ as large address aware using default installation folders of _Microsoft Visual C++ BuildTools_ and calling _vcvars32.bat_ in order to setup properly _PATH_ with binaries.
```Gradle
editbin {
    targetFileName  = <a DLL file path>
    enableLargeAddressAware = true
    useEnvSetupBat = true
}
```

###Set _\<a DLL file path\>_ as large address aware using _\<editbin folder\>_ where all the necessary binaries to execute _editbin.exe_ are present.
```Gradle
editbin {
    targetFileName  = <a DLL file path>
    enableLargeAddressAware = true
    editbinFolder = <editbin folder>
}
```