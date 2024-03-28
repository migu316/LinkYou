package com.migu.android.network.model

/**
 * 基类，用于组装请求url中的where部分，如下
 * where={"likes":2, "author":{"__type":"Pointer","className":"UserInfo","objectId":"65377c75117ff355ed264265"}}
 * 该类用于生成下面这段内容
 * "author":{"__type":"Pointer","className":"UserInfo","objectId":"65377c75117ff355ed264265"}
 */
data class LeanCloudPointerBaseModel(
    val pointerName: String,
    val type: String? = "Pointer",
    val className: String,
    val objectId: String
) {
    override fun toString(): String {
        return "\"${pointerName}\":{\"__type\":\"${type}\",\"className\":\"${className}\",\"objectId\":\"${objectId}\"}"
    }
}


fun main() {
    val model =
        LeanCloudPointerBaseModel("author", "Pointer", "UserInfo", "65377c75117ff355ed264265")
    println(model.toString())
}