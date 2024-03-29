package com.migu.android.network.model

/**
 * 基类，用于组装请求url中的where部分，如下
 * where={"likes":2, "author":{"__type":"Pointer","className":"UserInfo","objectId":"65377c75117ff355ed264265"}}
 * 该类用于生成下面这段内容
 * "author":{"__type":"Pointer","className":"UserInfo","objectId":"65377c75117ff355ed264265"}
 */
data class LeanCloudPointerBaseModel(
    var objectId: String,
    var className: String = "",
    var type: String? = "Pointer",
    var pointerName: String? = ""
) {
    override fun toString(): String {
        return "{\"__type\":\"${type}\",\"className\":\"${className}\",\"objectId\":\"${objectId}\"}"
    }
    fun toStringSeparateParameters():String {
        return "{\"${pointerName}\":{\"__type\":\"${type}\",\"className\":\"${className}\",\"objectId\":\"${objectId}\"}}"
    }
}