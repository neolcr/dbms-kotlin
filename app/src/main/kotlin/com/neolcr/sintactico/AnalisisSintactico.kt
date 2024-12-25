package com.neolcr.sintactico

import com.neolcr.exceptions.AnalisisSintacticoException
import java.util.logging.Logger
import java.util.stream.Collectors
import kotlinx.serialization.json.*
import java.io.File

var logger: Logger = Logger.getLogger("Analisis Sintactico: ")

fun getSelectTree(): MutableList<SqlNodeTree>  {
    val list: MutableList<SqlNodeTree> = ArrayList()
    list.add(SqlNodeTree("SELECT", Kind.SELECT, null, Kind.AST))
    list.add(SqlNodeTree("SELECT", Kind.SELECT, null, Kind.TABLENAME))
    list.add(SqlNodeTree("*", Kind.AST, Kind.SELECT, Kind.FROM))
    list.add(SqlNodeTree("FROM", Kind.FROM, Kind.SELECT, Kind.TABLENAME))
    list.add(SqlNodeTree("", Kind.TABLENAME, Kind.FROM, Kind.SEMICOLON))
    list.add(SqlNodeTree(";", Kind.SEMICOLON, Kind.TABLENAME, Kind.END))
    val encodeToJsonElement: JsonElement = Json.encodeToJsonElement(list)
    File("structure.json").writeText(encodeToJsonElement.toString())
    return list
}

fun getCreateDBTree(): MutableList<SqlNodeTree>  {
    val list: MutableList<SqlNodeTree> = ArrayList()
    list.add(SqlNodeTree("CREATE", Kind.CREATE, null, Kind.DATABASE))
    list.add(SqlNodeTree("DATABASE", Kind.DATABASE, Kind.CREATE, Kind.DATABASENAME))
    list.add(SqlNodeTree("", Kind.DATABASENAME, Kind.DATABASE, Kind.SEMICOLON))
    list.add(SqlNodeTree(";", Kind.SEMICOLON, Kind.DATABASENAME, Kind.END))
    val encodeToJsonElement: JsonElement = Json.encodeToJsonElement(list)
    File("db-structure.json").writeText(encodeToJsonElement.toString())
    return list
}
fun getSelectStructure(): MutableList<SqlNodeTree> {
    val json = File("structure.json").readText()
    return Json.decodeFromString(json)
}

fun analisisSintactico(elements: MutableList<String> ): SqlNodeTree {
    val list = when (elements.first().uppercase()) {
        "SELECT" -> getSelectTree()
        "CREATE" -> when (elements[1].uppercase()) {
            "TABLE" -> getSelectStructure()
            "DATABASE" ->getCreateDBTree()
            else -> throw AnalisisSintacticoException("Valor incorrecto")
        }
        else -> throw AnalisisSintacticoException("Valor incorrecto")
    }
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
                .filter { it: SqlNodeTree -> listOf(Kind.TABLENAME, Kind.DATABASENAME).contains(it.kind)}
                .toList()
        }
        val nextTypes = matchCurrentValue.stream().map { it: SqlNodeTree -> it.nextKind }.toList()
        val nextValues = matchCurrentValue.stream().map { it: SqlNodeTree -> it.nextValue }.toList()

        if (listOf(Kind.TABLENAME, Kind.DATABASENAME).contains(matchCurrentValue.first().kind)){
            matchCurrentValue.first().value = element
        }
        pushNode(root, matchCurrentValue.first())

        futuresMatchingValueOrType = list.stream()
            .filter { it: SqlNodeTree -> nextTypes.contains(it.kind) || nextValues.contains(it.value) }
            .toList()

        if (futuresMatchingValueOrType.isEmpty()) {
            println("Collected está vacio")
        } else if (futuresMatchingValueOrType.size > 1) {
            println("Collected tiene mas de un elemento: $futuresMatchingValueOrType")
        }
    }
    val finalStructure = Json.encodeToJsonElement(root)
    File("final-structure.json").writeText(finalStructure.toString())
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
