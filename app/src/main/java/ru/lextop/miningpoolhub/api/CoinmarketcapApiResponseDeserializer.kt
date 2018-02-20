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
        val jsonObject = json.asJsonObject
        val dataType = (type as ParameterizedType).actualTypeArguments[0]
        val errorMessage = jsonObject["error"]?.asString
        val data = jsonObject["data"]?.let {
            context.deserialize<Any?>(it, dataType)
        }
        return ApiResponse(data, errorMessage)
    }
}
