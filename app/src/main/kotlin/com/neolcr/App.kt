package com.neolcr
import com.neolcr.btree.BTree
import com.neolcr.btree.BTreeFiles
import com.neolcr.database.Database
import com.neolcr.database.testCreateDatabase
import java.nio.file.Paths
import java.nio.file.Files
import com.neolcr.lexico.analisisLexicoFaseA
import com.neolcr.lexico.analisisLexicoFaseB
import com.neolcr.sintactico.analisisSintactico


fun main() {
    testCreateDatabase()

    val btree = BTree<Int, String>(degree = 3)

    btree.insert(10, "Value 10")
    btree.insert(70, "Value 20")
    btree.insert(200, "Value 30")
    btree.insert(20, "Value 40")
    btree.insert(140, "Value 50")
    btree.insert(90, "Value 60")
    btree.insert(5, "Value 70")

    val files = BTreeFiles()
    files.saveToFile(btree, "btree.json")
    println("Saved")

    val loadedBTree = files.loadFromFile<Int, String>("btree.json")
    println("Loaded: ${loadedBTree.root} ")
/*    val currentDirFile = Paths.get("src/main/kotlin/com/neolcr/query.sql").toAbsolutePath()
    println(currentDirFile)
	var contenido = Files.readAllLines(currentDirFile).joinToString("");
    println("Primera lectura: $contenido")

    contenido = analisisLexicoFaseA(contenido)
    println("Segunda lectura: $contenido")

    val lista_final_tokens = analisisLexicoFaseB(contenido)
    println(lista_final_tokens)
    
    val estructura = analisisSintactico(lista_final_tokens)
    println("Final structure: $estructura")

 */
}



