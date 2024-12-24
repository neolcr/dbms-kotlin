package com.neolcr.database

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

data class Column(
    val name: String,
    val type: DataType,
    val isPrimaryKey: Boolean = false,
    val isUnique: Boolean = false
)

enum class DataType {
    INT, TEXT, REAL, BLOB
}

data class Table(
    val name: String,
    val columns: List<Column>
)

class Database(val name: String) {
    val tables = mutableMapOf<String, Table>()
    val data = mutableMapOf<String, MutableList<Row>>()

    data class Row(val values: Map<String, Any?>)

    fun createTable(table: Table) {
        if (tables.containsKey(table.name)) {
            throw IllegalArgumentException("Table ${table.name} already exists.")
        }
        tables[table.name] = table
        data[table.name] = mutableListOf() // Initialize empty storage for rows
    }

    fun insert(tableName: String, row: Map<String, Any?>) {
        val table = tables[tableName] ?: throw IllegalArgumentException("Table $tableName does not exist.")
        val rows = data[tableName] ?: throw IllegalStateException("No storage initialized for $tableName.")

        // Validate input row against schema
        for (column in table.columns) {
            if (!row.containsKey(column.name) && column.isPrimaryKey) {
                throw IllegalArgumentException("Missing value for primary key: ${column.name}")
            }
        }
        rows.add(Row(row))
    }

    fun query(tableName: String, where: (Row) -> Boolean = { true }): List<Row> {
        val rows = data[tableName] ?: throw IllegalArgumentException("Table $tableName does not exist.")
        return rows.filter(where)
    }
}

@Serializable
data class PersistentDatabase(
    val name: String,
    val tables: Map<String, Table>,
    val data: Map<String, List<Database.Row>>
)

fun Database.saveToFile(filePath: String) {
    val persistent = PersistentDatabase(name, tables, data.mapValues { it.value })
    val json = Json.encodeToString(persistent)
    File(filePath).writeText(json)
}

fun loadDatabaseFromFile(filePath: String): Database {
    val json = File(filePath).readText()
    val persistent = Json.decodeFromString<PersistentDatabase>(json)
    val db = Database(persistent.name)
    persistent.tables.forEach { (_, table) -> db.createTable(table) }
    persistent.data.forEach { (tableName, rows) ->
        rows.forEach { db.insert(tableName, it.values) }
    }
    return db
}

fun testCreateDatabase(){
    val db = Database("MyDatabase")

    // Define a table schema
    val userTable = Table(
        name = "Users",
        columns = listOf(
            Column("id", DataType.INT, isPrimaryKey = true),
            Column("name", DataType.TEXT),
            Column("email", DataType.TEXT, isUnique = true)
        )
    )

    // Create the table
    db.createTable(userTable)

    // Insert rows
    db.insert("Users", mapOf("id" to 1, "name" to "Alice", "email" to "alice@example.com"))
    db.insert("Users", mapOf("id" to 2, "name" to "Bob", "email" to "bob@example.com"))

    // Query data
    val results = db.query("Users") { row -> row.values["id"] == 1 }
    println(results)

    // Save to file
    db.saveToFile("database.json")
}

/// add indexing with b-trees
/*private val indexes = mutableMapOf<String, BTree<Any, Database.Row>>()

fun createIndex(tableName: String, columnName: String) {
    val table = tables[tableName] ?: throw IllegalArgumentException("Table $tableName does not exist.")
    val column = table.columns.find { it.name == columnName }
        ?: throw IllegalArgumentException("Column $columnName does not exist in table $tableName.")

    val bTree = BTree<Any, Database.Row>(degree = 3)
    data[tableName]?.forEach { row ->
        val key = row.values[columnName] ?: throw IllegalStateException("Row missing value for column $columnName")
        bTree.insert(key, row)
    }
    indexes["$tableName.$columnName"] = bTree
}

fun queryByIndex(tableName: String, columnName: String, value: Any): Database.Row? {
    return indexes["$tableName.$columnName"]?.search(value)
}

// Create an index on the "id" column
db.createIndex("Users", "id")

// Query using the index
val user = db.queryByIndex("Users", "id", 1)
println(user) // Row with id = 1
*/