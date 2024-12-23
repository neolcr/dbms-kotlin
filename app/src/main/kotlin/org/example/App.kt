package org.example
import java.nio.file.Paths
import java.nio.file.Files
import org.example.lexico.analisisLexicoFaseA
import org.example.lexico.analisisLexicoFaseB
import org.example.sintactico.analisisSintactico


fun main() {
    val currentDirFile = Paths.get("src/main/kotlin/org/example/query.sql").toAbsolutePath() 			
    println(currentDirFile)
	var contenido = Files.readAllLines(currentDirFile).joinToString("");
    println("Primera lectura: $contenido")

    contenido = analisisLexicoFaseA(contenido)
    println("Segunda lectura: $contenido")

    val lista_final_tokens = analisisLexicoFaseB(contenido)
    println(lista_final_tokens)
    
    val estructura = analisisSintactico(lista_final_tokens)
    println("Final structure: $estructura")
}



