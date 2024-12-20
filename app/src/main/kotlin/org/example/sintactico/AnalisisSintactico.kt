package org.example.sintactico
import org.example.utils.Tipo
import org.example.utils.getTipo
import org.example.exceptions.AnalisisSintacticoException

fun analisis_sintacticoA(lista: MutableList<String>): Boolean {
    var i: Int = 0

    while (i <= lista.size) {
        val elemento = lista.get(i)
        println("Elemento: $elemento")
        i++
    }
    return false
}
