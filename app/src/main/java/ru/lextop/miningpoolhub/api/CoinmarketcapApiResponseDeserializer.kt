package ru.lextop.miningpoolhub.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object CoinmarketcapApiResponseDeserializer : JsonDeserializer<ApiResponse<*>> {
    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): ApiResponse<*> {
        if (json.isJsonObject) {
            val errorMessage = json.asJsonObject["error"]?.asString
            if (errorMessage != null) {
                return ApiResponse(null, errorMessage)
            }
        }
        val dataType = (type as ParameterizedType).actualTypeArguments[0]
        val data = context.deserialize<Any?>(json, dataType)
        return ApiResponse(data, null)
    }
}
