package com.ndp.server.utils

import java.io.FileReader
import java.io.FileWriter
import java.util.*

object Settings {
    val setting = Properties()

    init {
        val inFile = FileReader("settings.properties")
        setting.load(inFile)
        inFile.close()
    }

    fun save() {
        val outFile = FileWriter("settings.properties")
        setting.store(outFile, "The count of ip-test-geo.")
    }

}