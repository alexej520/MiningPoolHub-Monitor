package ru.lextop.miningpoolhub.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object MiningpoolhubApiResponseDeserializer : JsonDeserializer<ApiResponse<*>> {
    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): ApiResponse<*> {
        val jsonObject = json.asJsonObject
        if (jsonObject.size() != 1) {
            throw JsonParseException("")
        }
        val methodResponseJsonObject = jsonObject.entrySet().iterator().next().value.asJsonObject
        val dataType = (type as ParameterizedType).actualTypeArguments[0]
        val data = context.deserialize<Any?>(methodResponseJsonObject["data"], dataType)
        return ApiResponse(data, null)
    }
}
