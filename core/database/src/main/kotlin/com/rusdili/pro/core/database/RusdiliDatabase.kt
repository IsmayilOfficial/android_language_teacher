package com.rusdili.pro.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [
        WordEntity::class,
        ChapterProgressEntity::class,
        NewsEntity::class,
        FlashcardDeckEntity::class,
        FlashcardDeckWordEntity::class,
        SearchHistoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class RusdiliDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun chapterProgressDao(): ChapterProgressDao
    abstract fun newsDao(): NewsDao
    abstract fun flashcardDeckDao(): FlashcardDeckDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `flashcard_decks` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `flashcard_deck_words` (
                    `deckId` INTEGER NOT NULL,
                    `wordRussian` TEXT NOT NULL,
                    `addedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`deckId`, `wordRussian`),
                    FOREIGN KEY(`deckId`) REFERENCES `flashcard_decks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`wordRussian`) REFERENCES `words`(`russian`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_flashcard_deck_words_deckId` ON `flashcard_deck_words` (`deckId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_flashcard_deck_words_wordRussian` ON `flashcard_deck_words` (`wordRussian`)"
            )
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `search_history` (
                    `queryKey` TEXT NOT NULL,
                    `displayQuery` TEXT NOT NULL,
                    `matchedRussian` TEXT,
                    `matchedTranslation` TEXT,
                    `searchedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`queryKey`)
                )
                """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RusdiliDatabase =
        Room.databaseBuilder(context, RusdiliDatabase::class.java, "rusdili.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("DELETE FROM news WHERE id = 'seed-trump-friend-2026-11-21'")
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

    @Provides
    fun provideFlashcardDeckDao(database: RusdiliDatabase): FlashcardDeckDao =
        database.flashcardDeckDao()

    @Provides
    fun provideSearchHistoryDao(database: RusdiliDatabase): SearchHistoryDao =
        database.searchHistoryDao()
}
