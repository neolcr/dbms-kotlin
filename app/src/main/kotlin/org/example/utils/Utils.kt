package org.example.utils

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
    NUMERO,
    SIMBOLO_INCORRECTO,
    INICIO_DESIGUAL
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
    this.isDigit() -> Tipo.NUMERO
    else -> Tipo.SIMBOLO_INCORRECTO

}

class Raiz {
    val lista_segmentos: MutableList<Segmento> = mutableListOf()
}

class Segmento {
    val lista_nodos: MutableList<Accion> = mutableListOf()
}


open class Accion {
    val lista_nodos: MutableList<Nodo> = mutableListOf()
}

class Insert: Accion() {}
class Update: Accion() {}
class Select: Accion() {}
class Delete: Accion() {} 

open class Nodo {
    val identificador: String = "",
    val valor: String = "",
    val anterior: Nodo = Nodo(), 
    val siguiente: Nodo = Nodo()
}

class Keyword: Nodo() {

}
