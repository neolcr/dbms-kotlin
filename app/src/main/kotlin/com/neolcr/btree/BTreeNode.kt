package com.neolcr.btree

import kotlinx.serialization.Serializable

@Serializable
data class BTreeNode<K: Comparable<K>, V> (
    var keys: MutableList<K> = mutableListOf(),
    var values: MutableList<V> = mutableListOf(),
    var children: MutableList<BTreeNode<K, V>> = mutableListOf(),
    var isLeaf: Boolean = true
)
