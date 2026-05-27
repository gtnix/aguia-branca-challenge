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

@Database(
    entities = [
        UsuarioEntity::class,
        OrientacaoEntity::class,
        IdeiaEntity::class,
        ProjetoEntity::class
    ],
    version = 6,
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
