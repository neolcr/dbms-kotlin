package com.neolcr.sintactico

import kotlinx.serialization.Serializable
//import java.util.regex.Matcher
//import java.util.regex.Pattern


@Serializable
class SqlNodeTree {
    var value: String? = null
    var kind: Kind
    var previousValue: String? = null
    var nextValue: String? = null
    var previousKind: Kind? = null
    var nextKind: Kind? = null
    //var pattern: Pattern = Pattern.compile("a-zA-Z0-9", Pattern.CASE_INSENSITIVE)
    //var matcher: Matcher? = null
    var child: SqlNodeTree? = null

    override fun toString(): String {
        if (kind.equals(Kind.UNKNOWN)) return ""
        return "{\n" +
                "\tvalue='" + value + '\n' +
                "\tkind='" + kind + '\n' +
                "\tchild='" + child + '\n' +
                "}"
    }

    constructor(kind: Kind) {
        this.kind = kind
    }

    constructor(value: String?, kind: Kind, previousValue: String?, nextValue: String?) {
        checkNotNull(value)
        checkNotNull(kind)
        this.value = value
        this.kind = kind
        this.previousValue = previousValue
        this.nextValue = nextValue
    }

    constructor(value: String?, kind: Kind, previousKind: Kind?, nextKind: Kind?) {
        checkNotNull(value)
        checkNotNull(kind)
        this.value = value
        this.kind = kind
        this.previousKind = previousKind
        this.nextKind = nextKind
    }
}
