package com.fmspcoding.notesapp.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.fmspcoding.notesapp.data.util.JsonParser
import com.fmspcoding.notesapp.domain.model.CheckItem
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters(
    private val jsonParser: JsonParser
) {

    @TypeConverter
    fun fromCheckItemsJson(json: String): List<CheckItem> {
        return jsonParser.fromJson<ArrayList<CheckItem>>(
            json,
            object : TypeToken<ArrayList<CheckItem>>(){}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toCheckItemsJson(meanings: List<CheckItem>): String {
        return jsonParser.toJson(
            meanings,
            object : TypeToken<ArrayList<CheckItem>>(){}.type
        ) ?: "[]"
    }
}