package com.gtnix.aguiabranca.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gtnix.aguiabranca.data.local.dao.IdeiaDao
import com.gtnix.aguiabranca.data.local.dao.OrientacaoDao
import com.gtnix.aguiabranca.data.local.dao.ProjetoDao
import com.gtnix.aguiabranca.data.local.dao.UsuarioDao
import com.gtnix.aguiabranca.data.local.entity.IdeiaEntity
import com.gtnix.aguiabranca.data.local.entity.OrientacaoEntity
import com.gtnix.aguiabranca.data.local.entity.ProjetoEntity
import com.gtnix.aguiabranca.data.local.entity.UsuarioEntity

/**
 * AppDatabase - Banco de Dados Room
 *
 * ## Conceito FIAP - Material 07A
 *
 * Esta classe é o **ponto de acesso principal** ao banco de dados SQLite.
 * O Room gera a implementação automaticamente com base nas anotações.
 *
 * ### Estrutura
 *
 * ```
 * AppDatabase (abstrata)
 *     │
 *     ├── usuarioDao()     → UsuarioDao (interface)
 *     ├── orientacaoDao()  → OrientacaoDao (interface)
 *     ├── ideiaDao()       → IdeiaDao (interface)
 *     └── projetoDao()     → ProjetoDao (interface)
 * ```
 *
 * ### @Database
 *
 * - `entities`: Lista de todas as tabelas do banco
 * - `version`: Versão do schema (incrementar ao fazer mudanças)
 * - `exportSchema`: Exporta schema para migrations (recomendado em produção)
 *
 * ### @TypeConverters
 *
 * Registra conversores para tipos complexos (List, Date, etc.)
 *
 * ### Criação do Database (via Hilt)
 *
 * ```kotlin
 * @Provides
 * @Singleton
 * fun provideDatabase(app: Application): AppDatabase {
 *     return Room.databaseBuilder(
 *         app,
 *         AppDatabase::class.java,
 *         "aguiabranca.db"
 *     )
 *     .fallbackToDestructiveMigration()  // Dev only! Remove em produção
 *     .build()
 * }
 * ```
 */
@Database(
    entities = [
        UsuarioEntity::class,
        OrientacaoEntity::class,
        IdeiaEntity::class,
        ProjetoEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    
    abstract fun orientacaoDao(): OrientacaoDao
    
    abstract fun ideiaDao(): IdeiaDao
    
    abstract fun projetoDao(): ProjetoDao

    companion object {
        const val DATABASE_NAME = "aguiabranca.db"
    }
}
