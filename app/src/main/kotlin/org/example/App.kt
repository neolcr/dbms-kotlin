package org.example
import java.nio.file.Paths
import java.nio.file.Files
import org.example.lexico.analisisLexicoA
import org.example.lexico.analisisLexicoB
import org.example.sintactico.analisisSintacticoA


fun main() {
    val currentDirFile = Paths.get("src/main/kotlin/org/example/query.sql").toAbsolutePath() 			
    println(currentDirFile)
	var contenido = Files.readAllLines(currentDirFile).joinToString("");
    println("Primera lectura: $contenido")

    contenido = analisisLexicoA(contenido) 
    println("Segunda lectura: $contenido")

    val lista_final_tokens = analisisLexicoB(contenido) 
    println(lista_final_tokens)
    
    analisisSintacticoA(lista_final_tokens) 

}



