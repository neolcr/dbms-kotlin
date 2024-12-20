package org.example
import java.nio.file.Paths
import java.nio.file.Files
import org.example.lexico.analisisLexicoA
import org.example.lexico.analisisLexicoB


fun main() {
    val currentDirFile = Paths.get("src/main/kotlin/org/example/query.sql").toAbsolutePath() 			
    println(currentDirFile)
	var contenido = Files.readAllLines(currentDirFile).joinToString("");
    println("Primera lectura: $contenido")

    contenido = analisisLexicoA(contenido) 
    println("Segunda lectura: $contenido")

    val resultado = analisisLexicoB(contenido) 
}



