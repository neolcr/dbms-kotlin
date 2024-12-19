package org.example
import java.nio.file.Paths
import java.nio.file.Files
import org.example.analisisLexicoA
import org.example.analisisLexicoB
import org.example.getTipo

enum class Tipo {
    ESPACIO,
    INICIO_KEYWORD,
    INICIO_VARCHAR,
    OPERADOR,
    PUNTOCOMA,
    ASTERISCO,
    INICIO_PARENTESIS,
    FINAL_PARENTESIS,
    PUNTO,
    COMA,
    INICIO_NUMERO,
    SIMBOLO_INCORRECTO
}

class AnalisisLexicoException(mensaje: String) : Exception(mensaje)

fun main() {
    val currentDirFile = Paths.get("src/main/kotlin/org/example/query.sql").toAbsolutePath() 			
    println(currentDirFile)
	var contenido = Files.readAllLines(currentDirFile).joinToString("");
    println("Primera lectura: $contenido")

    contenido = analisisLexicoA(contenido) 
    println("Segunda lectura: $contenido")

    contenido = analisisLexicoB(contenido) 
    println("Tercera lectura: $contenido")
}


fun analisisLexicoA(contenido: String): String {
    var resultado = ""
    val simbolos_validos = "*()<>=.,;"
    val simbolos_incorrectos = "$#"
    
    contenido.forEach { char -> 
       if (simbolos_validos.contains(char)) {
            resultado += ' '
            resultado += char 
            resultado += ' '
       } else {
            resultado += char
       }
       if (simbolos_incorrectos.contains(char)) {
            throw AnalisisLexicoException("Simbolo incorrecto $char")
       }
    }
    return resultado
}


fun Char.getTipo(): Tipo = when {
    this.isWhitespace() -> Tipo.ESPACIO
    this.isLetter() -> Tipo.INICIO_KEYWORD
    this.equals('\'') -> Tipo.INICIO_VARCHAR
    this.equals(',') -> Tipo.COMA
    this.equals('.') -> Tipo.PUNTO
    this.equals('*') -> Tipo.ASTERISCO
    this.equals(';') -> Tipo.PUNTOCOMA
    this.equals('(') -> Tipo.INICIO_PARENTESIS
    this.equals(')') -> Tipo.FINAL_PARENTESIS
    this.isDigit() -> Tipo.INICIO_NUMERO
    "><=".contains(this) -> Tipo.OPERADOR
    else -> Tipo.SIMBOLO_INCORRECTO

}


fun analisisLexicoB(contenido: String): String {
    var resultado = ""
    var i = 0

    while (i < contenido.length) {
        val ch = contenido.get(i)

        println("El index: $i  y el char: $ch y el tipo: ${ch.getTipo()}")
        i++
    }

    return resultado

}
