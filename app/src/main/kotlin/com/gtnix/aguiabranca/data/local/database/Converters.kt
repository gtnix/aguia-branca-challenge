package com.gtnix.aguiabranca.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TypeConverters para Room Database
 *
 * ## Conceito FIAP - Material 07A
 *
 * O Room só suporta tipos primitivos e algumas classes básicas.
 * Para tipos complexos como `List<String>`, precisamos definir
 * como converter para um tipo que o SQLite entende (geralmente String).
 *
 * ### Como funciona?
 *
 * 1. Ao **salvar**, Room chama `listToJson()` → converte List para String JSON
 * 2. Ao **ler**, Room chama `jsonToList()` → converte String JSON para List
 *
 * ### Exemplo
 *
 * ```kotlin
 * // Projeto com membros: ["id1", "id2", "id3"]
 * // No banco fica: membrosIds = '["id1","id2","id3"]'
 * ```
 */
class Converters {

    private val gson = Gson()

    /**
     * Converte List<String> para JSON String.
     * Usado ao salvar no banco.
     */
    @TypeConverter
    fun listToJson(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>())
    }

    /**
     * Converte JSON String para List<String>.
     * Usado ao ler do banco.
     */
    @TypeConverter
    fun jsonToList(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
