package com.neolcr.database

import com.neolcr.btree.BTree
import com.neolcr.btree.SerializableKey
import com.neolcr.database.Database.Value
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


@Serializable
data class Column(
    val name: String,
    val type: DataType,
    val isPrimaryKey: Boolean = false,
    val isUnique: Boolean = false
)

enum class DataType {
    INT, TEXT, REAL, BLOB
}

@Serializable
data class Table(
    val name: String,
    val columns: List<Column>
)

@Serializable
class Database(val name: String) {
    val tables = mutableMapOf<String, Table>()
    val data = mutableMapOf<String, MutableList<Row>>()

    @Serializable
    data class Row(val values: Map<String, Value>)

    @Serializable
    sealed class Value {

        @Serializable
        data class StringValue(val value: String): Value()

        @Serializable
        data class IntValue(val value: Int): Value()

        @Serializable
        data class BooleanValue(val value: Boolean): Value()

        @Serializable
        data class DoubleValue(val value: Double): Value()
    }

    fun createTable(table: Table) {
        if (tables.containsKey(table.name)) {
            throw IllegalArgumentException("Table ${table.name} already exists.")
        }
        tables[table.name] = table
        data[table.name] = mutableListOf() // Initialize empty storage for rows
    }

    fun insert(tableName: String, row: Map<String, Value>) {
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
    // add indexing with b-trees
    val indexes = mutableMapOf<String, BTree<SerializableKey, Row>>()


    fun createIndex(tableName: String, columnName: String) {
        val table = tables[tableName]
            ?: throw IllegalArgumentException("Table $tableName does not exist.")
        val column = table.columns.find { it.name == columnName }
            ?: throw IllegalArgumentException("Column $columnName does not exist in table $tableName.")

        val bTree = BTree<SerializableKey, Row>(degree = 3)
        data[tableName]?.forEach { row ->
            val value = row.values[columnName]
                ?: throw IllegalStateException("Row missing value for column $columnName")
            val key = valueToSerializableKey(value) // Map value to SerializableKey
            bTree.insert(key, row)
        }
        indexes["$tableName.$columnName"] = bTree
    }

    fun queryByIndex(tableName: String, columnName: String, value: Database.Value): Row? {
        val key = valueToSerializableKey(value) // Map value to SerializableKey
        return indexes["$tableName.$columnName"]?.search(key)
    }

    fun valueToSerializableKey(value: Database.Value): SerializableKey {
        return when (value) {
            is Database.Value.IntValue -> SerializableKey.IntKey(value.value)
            is Database.Value.StringValue -> SerializableKey.StringKey(value.value)
            is Database.Value.DoubleValue -> SerializableKey.DoubleKey(value.value)
            is Database.Value.BooleanValue -> throw IllegalArgumentException("BooleanValue cannot be used as an index key")
        }
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
    db.insert("Users", mapOf("id" to Value.IntValue(1), "name" to Value.StringValue("Alice"), "email" to Value.StringValue("alice@example.com")))
    db.insert("Users", mapOf("id" to Value.IntValue(22), "name" to Value.StringValue("Bob"), "email" to Value.StringValue("bob@example.com")))

    // Query data
    val results = db.query("Users") { row -> row.values["id"] == Value.IntValue(1) }
    println(results)

    // Save to file
    db.saveToFile("database.json")
    // Create an index on the "id" column
    db.createIndex("Users", "id")

// Query using the index
    val user = db.queryByIndex("Users", "id", Value.IntValue(1))
    println("User->>>>>>> $user") // Row with id = 1
}

