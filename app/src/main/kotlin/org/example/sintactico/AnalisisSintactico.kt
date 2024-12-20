package org.example.sintactico
import org.example.utils.Tipo
import org.example.utils.getTipo
import org.example.utils.getKeywordTipo
import org.example.utils.*
import org.example.exceptions.AnalisisSintacticoException

fun analisisSintacticoA(lista: MutableList<String>): Boolean {
    var i: Int = 0

    while (i < lista.size) {
        val (j, exito) = buscarAccion(i, lista)
        if (exito) {
            i = j
        }
        
        i++
    }
    return false
}

fun buscarAccion(i: Int, lista: MutableList<String>):Pair<Int,Boolean> {
    val elemento = lista.get(i)
    val tipo = elemento.getKeywordTipo()

    when(tipo) {

        Keyword.Tipo.SELECT -> {
            println("Es una select")
            return Pair(montarSelect(i, lista), true) 
        }
        Keyword.Tipo.UPDATE -> {
            println("Es un update")
            return Pair(i, false) 
        }
        Keyword.Tipo.DELETE -> {
            println("Es un delete")
            return Pair(i, false) 
        }
        Keyword.Tipo.INSERT -> {
            println("Es un insert")
            return Pair(i, false) 
        }
        else -> return Pair(i, false) 
    }


}

fun montarSelect(i:Int, lista: MutableList<String>):Int { 
    val vseleccion = lista.get(i + 1)
    val vtabla = lista.get(i + 2)
    val keyword = Keyword(Keyword.Tipo.SELECT) 
    val seleccion = Seleccion()
    if (vseleccion.equals("*")) {
        seleccion.asterisco = true
    } else {
        // TODO Implementar busqueda de siguientas tabla, tabla, tabla, etc
    }
    val tabla = Tabla()
    tabla.identificador = vtabla
    val select = Select()
    select.lista_nodos.add(keyword)
    select.lista_nodos.add(seleccion)
    select.lista_nodos.add(tabla)
    println("Llega hasta aqui >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    if (select.validar()) {
        println("Lista nodos: ${select.lista_nodos}")
        return i + 2    
    } else {
        throw AnalisisSintacticoException("Error sintactico al crear select")
    }
}
