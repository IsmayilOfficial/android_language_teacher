package com.rusdili.pro.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [WordEntity::class, ChapterProgressEntity::class, NewsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RusdiliDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun chapterProgressDao(): ChapterProgressDao
    abstract fun newsDao(): NewsDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RusdiliDatabase =
        Room.databaseBuilder(context, RusdiliDatabase::class.java, "rusdili.db")
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    SeedData.words.forEach { word ->
                        db.execSQL(
                            "INSERT OR IGNORE INTO words " +
                                "(russian, translation, gender, example, aspectPair, isFavorite, nextReviewDate) " +
                                "VALUES (?, ?, ?, ?, ?, 0, 0)",
                            arrayOf(word.russian, word.translation, word.gender, word.example, word.aspectPair)
                        )
                    }
                    SeedData.news.forEach { item ->
                        db.execSQL(
                            "INSERT OR IGNORE INTO news " +
                                "(id, titleAz, summaryAz, original, source, publishedAt, imageUrl, sourceUrl, unconfirmed) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            arrayOf(
                                item.id, item.titleAz, item.summaryAz, item.original, item.source,
                                item.publishedAt, item.imageUrl, item.sourceUrl,
                                if (item.unconfirmed) 1 else 0
                            )
                        )
                    }
                }
            })
            .build()

    @Provides
    fun provideWordDao(database: RusdiliDatabase): WordDao = database.wordDao()

    @Provides
    fun provideNewsDao(database: RusdiliDatabase): NewsDao = database.newsDao()

    @Provides
    fun provideChapterProgressDao(database: RusdiliDatabase): ChapterProgressDao =
        database.chapterProgressDao()
}
