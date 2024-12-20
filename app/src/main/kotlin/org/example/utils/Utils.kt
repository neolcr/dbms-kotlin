package org.example.utils

import org.example.exceptions.AnalisisLexicoException
import org.example.exceptions.AnalisisSintacticoException

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


fun String.getKeywordTipo(): Keyword.Tipo = when {
    this.uppercase().equals("SELECT") -> Keyword.Tipo.SELECT
    this.uppercase().equals("UPDATE") -> Keyword.Tipo.UPDATE
    this.uppercase().equals("DELETE") -> Keyword.Tipo.DELETE
    this.uppercase().equals("INSERT") -> Keyword.Tipo.INSERT
    this.uppercase().equals("CREATE") -> Keyword.Tipo.CREATE
    this.uppercase().equals("DATABASE") -> Keyword.Tipo.DATABASE
    this.uppercase().equals("TABLE") -> Keyword.Tipo.TABLE
    this.uppercase().equals("FROM") -> Keyword.Tipo.FROM
    this.uppercase().equals("INNER") -> Keyword.Tipo.INNER
    this.uppercase().equals("JOIN") -> Keyword.Tipo.JOIN
    this.uppercase().equals("AND") -> Keyword.Tipo.AND
    this.uppercase().equals("OR") -> Keyword.Tipo.OR
    this.uppercase().equals("BETWEEN") -> Keyword.Tipo.BETWEEN
    else -> Keyword.Tipo.NADA 
}


class Raiz {
    val lista_segmentos: MutableList<Segmento> = mutableListOf()
    
    fun validar(): Boolean {
        lista_segmentos.forEach{
            if (!it.validar()) {
                return false 
            }
        }
        return true 
    }
}

class Segmento {
    val lista_acciones: MutableList<Accion> = mutableListOf()

    fun validar(): Boolean {
        lista_acciones.forEach{
            if (!it.validar()) {
                return false 
            }
        }
        return true 
    }
}


abstract class Accion {
    val lista_nodos: MutableList<Nodo> = mutableListOf()
    abstract fun validar(): Boolean
}

class CrearDB: Accion() {
    override fun validar() :Boolean {
        return false;
    }
}
class CrearTabla: Accion() {
    override fun validar() :Boolean {
        return false;
    }
}
class Insert: Accion() {
    override fun validar() :Boolean {
        return false;
    }
}

class Update: Accion() {
    override fun validar() :Boolean {
        return false;
    }
}

class Select: Accion() {
    override fun validar() :Boolean {
        val primerNodo = lista_nodos.get(0)
        if (primerNodo !is Keyword) {
            println("Primer nodo no es keyword")
            return false
        }
        
        if (!primerNodo.tipo.equals(Keyword.Tipo.SELECT)) {
            println("Primer nodo no es select ")
            return false
        }
        val segundoNodo = lista_nodos.get(1)
        if (segundoNodo !is Seleccion) {
            println("Segundo nodo no es seleccion")
            return false 
        }
        if (!segundoNodo.asterisco && segundoNodo.lista_columnas.isEmpty()) {
            println("Segundo nodo tiene problema con asterisco o columnas")
            return false
        }
        // TODO comprobar si las columnas y la tabla existen y sino retornar false 

        val tercerNodo = lista_nodos.get(2)
        if (tercerNodo !is Tabla) {
            println("Tercer nodo no es tabla")
            return false
        }
        if (tercerNodo.identificador.isBlank()){
            println("Tercer nodo no tiene valor")
            return false 
        }
        return true;
    }
}

class Delete: Accion() {
    override fun validar() :Boolean {
        return false;
    }
} 

open class Nodo {
    var identificador: String = ""
    var valor: String = ""
    var anterior: Nodo? = null 
    var siguiente: Nodo? = null 
}

class Keyword(val tipo: Keyword.Tipo): Nodo() {
    public enum class Tipo {
        SELECT,
        UPDATE,
        INSERT,
        DELETE,
        CREATE,
        DATABASE,
        TABLE,
        FROM,
        INNER,
        JOIN,
        AND,
        OR,
        BETWEEN,
        NADA
    }
    override fun toString():String {
        return "Keyword: { tipo: $tipo }"
    }
}
class Seleccion: Nodo() {
    var asterisco: Boolean = false
    val lista_columnas: MutableList<Columna> = mutableListOf()
    override fun toString():String {
        return "Seleccion: { asterisco: $asterisco, listaColumnas: $lista_columnas }"
    }
}
class Operador: Nodo() {
    public enum class Validos(val simbolo: String) {
        IGUAL("="),
        DESIGUAL("!="),
        MAYOR(">"),
        MENOR("<"),

    }
}
class Columna: Nodo() {
}

class Tabla: Nodo() {
    override fun toString():String {
        return "Tabla: { nombre: $identificador }"
    }
}

