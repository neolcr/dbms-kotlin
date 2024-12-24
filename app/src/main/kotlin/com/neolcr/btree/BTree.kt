package com.neolcr.btree

import kotlinx.serialization.Serializable

@Serializable
class BTree<K : Comparable<K>, V>(private val degree: Int) {

    var root: BTreeNode<K, V> = BTreeNode()

    // Search for a key in the B-Tree
    fun search(key: K, node: BTreeNode<K, V>? = root): V? {
        val i = node?.keys?.indexOfFirst { it >= key } ?: -1

        return when {
            i >= 0 && i < node!!.keys.size && node.keys[i] == key -> node.values[i]
            node?.isLeaf == true -> null
            else -> search(key, node?.children?.get(i))
        }
    }

    // Split a full child node
    private fun splitChild(parent: BTreeNode<K, V>, index: Int) {
        val fullChild = parent.children[index]
        val medianIndex = degree - 1

        val newNode = BTreeNode<K, V>(
            keys = fullChild.keys.subList(medianIndex + 1, fullChild.keys.size).toMutableList(),
            values = fullChild.values.subList(medianIndex + 1, fullChild.values.size).toMutableList(),
            children = if (fullChild.isLeaf) mutableListOf() else fullChild.children.subList(medianIndex + 1, fullChild.children.size).toMutableList(),
            isLeaf = fullChild.isLeaf
        )

        parent.keys.add(index, fullChild.keys[medianIndex])
        parent.values.add(index, fullChild.values[medianIndex])
        parent.children.add(index + 1, newNode)

        fullChild.keys = fullChild.keys.subList(0, medianIndex).toMutableList()
        fullChild.values = fullChild.values.subList(0, medianIndex).toMutableList()
        if (!fullChild.isLeaf) fullChild.children = fullChild.children.subList(0, medianIndex + 1).toMutableList()
    }

    // Insert a key-value pair
    fun insert(key: K, value: V) {
        val r = root
        if (r.keys.size == 2 * degree - 1) {
            val s = BTreeNode<K, V>(isLeaf = false)
            s.children.add(r)
            root = s
            splitChild(s, 0)
            insertNonFull(s, key, value)
        } else {
            insertNonFull(r, key, value)
        }
    }

    // Insert into a non-full node
    private fun insertNonFull(node: BTreeNode<K, V>, key: K, value: V) {
        var i = node.keys.size - 1
        if (node.isLeaf) {
            while (i >= 0 && key < node.keys[i]) {
                i--
            }
            node.keys.add(i + 1, key)
            node.values.add(i + 1, value)
        } else {
            while (i >= 0 && key < node.keys[i]) {
                i--
            }
            i++
            if (node.children[i].keys.size == 2 * degree - 1) {
                splitChild(node, i)
                if (key > node.keys[i]) i++
            }
            insertNonFull(node.children[i], key, value)
        }
    }
}
