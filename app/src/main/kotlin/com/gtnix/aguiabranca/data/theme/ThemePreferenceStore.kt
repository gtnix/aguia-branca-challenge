package com.gtnix.aguiabranca.data.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gtnix.aguiabranca.presentation.theme.TemaPreferencia
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.temaDataStore: DataStore<Preferences> by preferencesDataStore(name = "inovagab_tema")

@Singleton
class ThemePreferenceStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val temaKey = stringPreferencesKey("tema")

    val tema: Flow<TemaPreferencia> = context.temaDataStore.data.map { prefs ->
        prefs[temaKey]
            ?.let { stored -> runCatching { TemaPreferencia.valueOf(stored) }.getOrNull() }
            ?: TemaPreferencia.SISTEMA
    }

    suspend fun set(tema: TemaPreferencia) {
        context.temaDataStore.edit { prefs ->
            prefs[temaKey] = tema.name
        }
    }
}
