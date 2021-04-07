plugins {
    id("org.jetbrains.intellij") version "0.7.2"
}

group = "com.xunfos.tour"
version = "1.0.0"

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2021.1"
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
}
