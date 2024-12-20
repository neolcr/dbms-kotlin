package org.example
import java.nio.file.Paths
import java.nio.file.Files
import org.example.analisisLexicoA
import org.example.analisisLexicoB
import org.example.getTipo
import org.example.exceptions.AnalisisLexicoException

enum class Tipo {
    ESPACIO,
    INICIO_IDENTIFICADOR,
    COMILLA_SIMPLE,
    PUNTOCOMA,
    ASTERISCO,
    INICIO_PARENTESIS,
    FINAL_PARENTESIS,
    PUNTO,
    COMA,
    IGUAL,
    MAYOR,
    MENOR,
    INICIO_NUMERO,
    SIMBOLO_INCORRECTO,
    INICIO_DESIGUAL
}


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
    this.isLetter() -> Tipo.INICIO_IDENTIFICADOR
    this.equals('\'') -> Tipo.COMILLA_SIMPLE
    this.equals(',') -> Tipo.COMA
    this.equals('.') -> Tipo.PUNTO
    this.equals('*') -> Tipo.ASTERISCO
    this.equals(';') -> Tipo.PUNTOCOMA
    this.equals('!') -> Tipo.INICIO_DESIGUAL
    this.equals('(') -> Tipo.INICIO_PARENTESIS
    this.equals(')') -> Tipo.FINAL_PARENTESIS
    this.equals('>') -> Tipo.MAYOR
    this.equals('<') -> Tipo.MENOR
    this.equals('=') -> Tipo.IGUAL
    this.isDigit() -> Tipo.INICIO_NUMERO
    else -> Tipo.SIMBOLO_INCORRECTO

}


fun analisisLexicoB(contenido: String): MutableList<String> {
    var i = 0
    var lista_final_tokens = mutableListOf<String>()

    while (i < contenido.length) {
        //println("The len: ${contenido.length} ,  index: $i")
        val ch = contenido.get(i)
        
        when(ch.getTipo()) {
            Tipo.INICIO_IDENTIFICADOR -> {
                val (j, identificador) = extraerIdentificador(i, contenido)
                i = j
                println("Identificador: $identificador")
                lista_final_tokens.add(identificador)
            }
            Tipo.COMILLA_SIMPLE -> {
                val (j, varchar) = extraerVarchar(i, contenido)
                i = j
                println("Varchar: $varchar")
                lista_final_tokens.add(varchar)

            }
            Tipo.ESPACIO -> {
                println("Espacio")
            }
            Tipo.IGUAL -> {

            }
            Tipo.MAYOR -> {

            }
            Tipo.MENOR -> {

            }
            Tipo.PUNTOCOMA -> {

            }
            Tipo.ASTERISCO -> {

            }
            Tipo.INICIO_PARENTESIS -> {

            }
            Tipo.FINAL_PARENTESIS -> {

            }
            Tipo.PUNTO -> {

            }
            Tipo.COMA -> {

            }
            Tipo.INICIO_NUMERO -> {

            }
            Tipo.SIMBOLO_INCORRECTO -> {

            }
            Tipo.INICIO_DESIGUAL -> {

            }
        }

        i++
    }

    println(lista_final_tokens)
    return lista_final_tokens

}

fun extraerIdentificador(i: Int, contenido: String) : Pair<Int, String> {

    var resultado = ""
    var j = i

    while (!contenido.get(j).getTipo().equals(Tipo.ESPACIO)) {
        resultado += contenido.get(j)
        j++
        if (j == contenido.length) {
            throw AnalisisLexicoException("Inicio identificador sin final")
        }
    }

    return Pair(j, resultado)

}

fun extraerVarchar(i: Int, contenido: String) : Pair<Int, String> {

    var resultado = ""
    var j = i

    while (j == i || !contenido.get(j).getTipo().equals(Tipo.COMILLA_SIMPLE)) {
        resultado += contenido.get(j)
        j++
        if (j == contenido.length) {
            throw AnalisisLexicoException("Inicio varchar sin final")
        }
    }
    resultado += '\''
    return Pair(j, resultado)

}
