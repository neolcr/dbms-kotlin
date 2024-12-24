package com.neolcr.btree

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class BTreeFiles {
    inline fun <reified K : SerializableKey, reified V> saveToFile(bTree: BTree<K, V>, filePath: String) {
        val json = Json.encodeToString(bTree)
        File(filePath).writeText(json)
    }
    inline fun <reified K : SerializableKey, reified V> loadFromFile(filePath: String): BTree<K, V> {
        val json = File(filePath).readText()
        return Json.decodeFromString(json)
    }

}