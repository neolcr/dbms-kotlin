package org.example.sintactico

import org.example.exceptions.AnalisisSintacticoException
import java.util.logging.Logger
import java.util.stream.Collectors
import kotlinx.serialization.json.*
import java.io.File

var logger: Logger = Logger.getLogger("Analisis Sintactico: ")

fun getTree(): MutableList<SqlNodeTree>  {
    val list: MutableList<SqlNodeTree> = ArrayList()
    list.add(SqlNodeTree("SELECT", Kind.SELECT, null, Kind.AST))
    list.add(SqlNodeTree("SELECT", Kind.SELECT, null, Kind.TABLE))
    list.add(SqlNodeTree("*", Kind.AST, Kind.SELECT, Kind.FROM))
    list.add(SqlNodeTree("FROM", Kind.FROM, Kind.SELECT, Kind.TABLE))
    list.add(SqlNodeTree("", Kind.TABLE, Kind.FROM, Kind.SEMICOLON))
    list.add(SqlNodeTree(";", Kind.SEMICOLON, Kind.TABLE, Kind.END))
    val encodeToJsonElement: JsonElement = Json.encodeToJsonElement(list)
    File("localtree.json").writeText(encodeToJsonElement.toString())
    return list
}

fun analisisSintactico(elements: MutableList<String> ): SqlNodeTree {
    val list = getTree()
    var futuresMatchingValueOrType: List<SqlNodeTree>
    var matchCurrentValue = list.stream()
        .filter { it: SqlNodeTree -> it.value.equals(list.first().value, ignoreCase = true) }
        .collect(Collectors.toList())

    val firstNextTypes = matchCurrentValue.stream()
        .map { it: SqlNodeTree -> it.nextKind }
        .toList();

    val firstNextValues = matchCurrentValue.stream()
        .map { it: SqlNodeTree -> it.nextValue }
        .toList()

    futuresMatchingValueOrType = list.stream()
        .filter { it: SqlNodeTree -> firstNextTypes.contains(it.kind) || firstNextValues.contains(it.value) }
        .toList()

    val root = SqlNodeTree(Kind.ROOT)
    pushNode(root, matchCurrentValue.first())
    elements.removeFirst()

    for (element in elements) {
        matchCurrentValue = futuresMatchingValueOrType.stream()
            .filter { it: SqlNodeTree -> it.value.equals(element, ignoreCase = true)}
            .toList()

        if (matchCurrentValue.isEmpty()) {
            matchCurrentValue = futuresMatchingValueOrType.stream()
                .filter { it: SqlNodeTree -> it.kind == Kind.TABLE }
                .toList()
        }
        val nextTypes = matchCurrentValue.stream().map { it: SqlNodeTree -> it.nextKind }.toList()
        val nextValues = matchCurrentValue.stream().map { it: SqlNodeTree -> it.nextValue }.toList()

        pushNode(root, matchCurrentValue.first())

        if (matchCurrentValue.isEmpty()) {
            val tables = futuresMatchingValueOrType.stream()
                .filter { it: SqlNodeTree -> it.kind == Kind.TABLE }
                .toList()

            if (tables.size > 1) {
                throw RuntimeException("No tiene sentido que haya mas de una tabla")
            } else if (tables.isEmpty()) {
                println("Element: $element")
                throw RuntimeException("No tiene sentido que ninguna sea tabla")
            } else {
                val table: SqlNodeTree = tables.first()
                if (table.previousKind == Kind.FROM) {
                    table.value = element
                } else {
                    throw RuntimeException("Encontrada declaracion de tabla sin FROM")
                }
                //throw new RuntimeException(("El element no existe: " + element));
            }
        }

        futuresMatchingValueOrType = list.stream()
            .filter { it: SqlNodeTree -> nextTypes.contains(it.kind) || nextValues.contains(it.value) }
            .toList()

        if (futuresMatchingValueOrType.isEmpty()) {
            println("Collected está vacio")
        } else if (futuresMatchingValueOrType.size > 1) {
            println("Collected tiene mas de un elemento: $futuresMatchingValueOrType")
        }
    }
    return root
}

fun pushNode(root: SqlNodeTree, child: SqlNodeTree?) {
    if (child == null) {
        throw AnalisisSintacticoException("No se puede tratar de asignar un hijo null");
    }
    if (root.child == null) {
        root.child = child
        return
    }
    pushNode(root.child!!, child)
}
