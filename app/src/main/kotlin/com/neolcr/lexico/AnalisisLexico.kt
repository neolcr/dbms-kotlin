package com.neolcr.lexico
import com.neolcr.utils.Tipo
import com.neolcr.utils.getTipo
import com.neolcr.exceptions.AnalisisLexicoException


fun analisisLexicoFaseA(contenido: String): String {
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

fun analisisLexicoFaseB(contenido: String): MutableList<String> {
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
                println("Igual: $ch")
                lista_final_tokens.add("=")

            }
            Tipo.MAYOR -> {
                println("Mayor: $ch")
                lista_final_tokens.add(">")

            }
            Tipo.MENOR -> {
                println("Menor: $ch")
                lista_final_tokens.add("<")

            }
            Tipo.PUNTOCOMA -> {
                println("Punto y coma: $ch")
                lista_final_tokens.add(";")

            }
            Tipo.ASTERISCO -> {
                println("Asterisco: $ch")
                lista_final_tokens.add("*")

            }
            Tipo.INICIO_PARENTESIS -> {
                println("Inicio parentesis: $ch")
                lista_final_tokens.add("(")

            }
            Tipo.FINAL_PARENTESIS -> {
                println("Final parentesis: $ch")
                lista_final_tokens.add(")")

            }
            Tipo.PUNTO -> {
                println("Punto: $ch")
                lista_final_tokens.add(".")

            }
            Tipo.COMA -> {
                println("Coma: $ch")
                lista_final_tokens.add(",")

            }
            Tipo.NUMERO -> {
                var (j, numero) = extraerNumero(i, contenido)
                i = j 
                println("Numero: $numero")
                lista_final_tokens.add(numero)
            }
            Tipo.SIMBOLO_INCORRECTO -> {
                throw AnalisisLexicoException("Simbolo incorrecto: $ch")
            }
            Tipo.INICIO_DESIGUAL -> {
                var (j , desigual) = extraerDesigual(i, contenido)
                i = j
                println("Varchar: $desigual")
                lista_final_tokens.add(desigual)
            }
        }

        i++
    }

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

fun extraerNumero(i: Int, contenido: String) : Pair<Int, String> {

    var resultado = ""
    var j = i

    while (contenido.get(j).getTipo().equals(Tipo.NUMERO)) {
        resultado += contenido.get(j)
        j++
        if (j == contenido.length) {
            throw AnalisisLexicoException("Inicio numero sin final")
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

fun extraerDesigual(i: Int, contenido: String) : Pair<Int, String> {
    return if (contenido.get(i + 2).equals('=')) {
       Pair(i + 2, "!=")
    } else {
        throw AnalisisLexicoException("Error al extraer simbolo '!='")
    }

}
