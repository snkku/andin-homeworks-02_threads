package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.nmedia.dao.AuthorDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.AuthorEntity
import ru.netology.nmedia.entity.PostEntity

@Database(entities = [PostEntity::class, AuthorEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun authorDao(): AuthorDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                //  .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}