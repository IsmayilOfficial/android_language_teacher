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
        SearchHistoryEntity::class,
        ReviewSessionEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class RusdiliDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun chapterProgressDao(): ChapterProgressDao
    abstract fun newsDao(): NewsDao
    abstract fun flashcardDeckDao(): FlashcardDeckDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun reviewSessionDao(): ReviewSessionDao
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

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `review_sessions` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `sessionType` TEXT NOT NULL,
                    `totalItems` INTEGER NOT NULL,
                    `successfulItems` INTEGER NOT NULL,
                    `xpEarned` INTEGER NOT NULL,
                    `completedAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `words_v5` (
                    `id` TEXT NOT NULL,
                    `language` TEXT NOT NULL,
                    `russian` TEXT NOT NULL,
                    `translation` TEXT NOT NULL,
                    `gender` TEXT,
                    `example` TEXT NOT NULL,
                    `aspectPair` TEXT,
                    `isFavorite` INTEGER NOT NULL,
                    `nextReviewDate` INTEGER NOT NULL,
                    `reviewStage` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT OR IGNORE INTO `words_v5`
                (`id`, `language`, `russian`, `translation`, `gender`, `example`, `aspectPair`, `isFavorite`, `nextReviewDate`, `reviewStage`)
                SELECT 'ru:' || `russian`, 'ru', `russian`, `translation`, `gender`, `example`, `aspectPair`, `isFavorite`, `nextReviewDate`, `reviewStage`
                FROM `words`
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `words`")
            db.execSQL("ALTER TABLE `words_v5` RENAME TO `words`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_words_language` ON `words` (`language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_words_russian` ON `words` (`russian`)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `chapter_progress_v5` (
                    `language` TEXT NOT NULL,
                    `chapterId` INTEGER NOT NULL,
                    `scorePercent` INTEGER NOT NULL,
                    `completedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`language`, `chapterId`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT OR IGNORE INTO `chapter_progress_v5` (`language`, `chapterId`, `scorePercent`, `completedAt`)
                SELECT 'ru', `chapterId`, `scorePercent`, `completedAt` FROM `chapter_progress`
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `chapter_progress`")
            db.execSQL("ALTER TABLE `chapter_progress_v5` RENAME TO `chapter_progress`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_chapter_progress_language` ON `chapter_progress` (`language`)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `flashcard_decks_v5` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `language` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT OR IGNORE INTO `flashcard_decks_v5` (`id`, `language`, `name`, `description`, `createdAt`)
                SELECT `id`, 'ru', `name`, `description`, `createdAt` FROM `flashcard_decks`
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `flashcard_deck_words_tmp` (
                    `deckId` INTEGER NOT NULL,
                    `wordId` TEXT NOT NULL,
                    `addedAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO `flashcard_deck_words_tmp` (`deckId`, `wordId`, `addedAt`)
                SELECT `deckId`, 'ru:' || `wordRussian`, `addedAt` FROM `flashcard_deck_words`
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `flashcard_deck_words`")
            db.execSQL("DROP TABLE `flashcard_decks`")
            db.execSQL("ALTER TABLE `flashcard_decks_v5` RENAME TO `flashcard_decks`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcard_decks_language` ON `flashcard_decks` (`language`)")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `flashcard_deck_words` (
                    `deckId` INTEGER NOT NULL,
                    `wordId` TEXT NOT NULL,
                    `addedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`deckId`, `wordId`),
                    FOREIGN KEY(`deckId`) REFERENCES `flashcard_decks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`wordId`) REFERENCES `words`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT OR IGNORE INTO `flashcard_deck_words` (`deckId`, `wordId`, `addedAt`)
                SELECT `deckId`, `wordId`, `addedAt` FROM `flashcard_deck_words_tmp`
                WHERE EXISTS (SELECT 1 FROM `words` WHERE `words`.`id` = `flashcard_deck_words_tmp`.`wordId`)
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `flashcard_deck_words_tmp`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcard_deck_words_deckId` ON `flashcard_deck_words` (`deckId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcard_deck_words_wordId` ON `flashcard_deck_words` (`wordId`)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `search_history_v5` (
                    `language` TEXT NOT NULL,
                    `queryKey` TEXT NOT NULL,
                    `displayQuery` TEXT NOT NULL,
                    `matchedRussian` TEXT,
                    `matchedTranslation` TEXT,
                    `searchedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`language`, `queryKey`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT OR IGNORE INTO `search_history_v5`
                (`language`, `queryKey`, `displayQuery`, `matchedRussian`, `matchedTranslation`, `searchedAt`)
                SELECT 'ru', `queryKey`, `displayQuery`, `matchedRussian`, `matchedTranslation`, `searchedAt`
                FROM `search_history`
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `search_history`")
            db.execSQL("ALTER TABLE `search_history_v5` RENAME TO `search_history`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_search_history_language` ON `search_history` (`language`)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `review_sessions_v5` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `language` TEXT NOT NULL,
                    `sessionType` TEXT NOT NULL,
                    `totalItems` INTEGER NOT NULL,
                    `successfulItems` INTEGER NOT NULL,
                    `xpEarned` INTEGER NOT NULL,
                    `completedAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT OR IGNORE INTO `review_sessions_v5`
                (`id`, `language`, `sessionType`, `totalItems`, `successfulItems`, `xpEarned`, `completedAt`)
                SELECT `id`, 'ru', `sessionType`, `totalItems`, `successfulItems`, `xpEarned`, `completedAt`
                FROM `review_sessions`
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `review_sessions`")
            db.execSQL("ALTER TABLE `review_sessions_v5` RENAME TO `review_sessions`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_review_sessions_language` ON `review_sessions` (`language`)")
        }
    }

    private fun seedWordsIfNeeded(db: SupportSQLiteDatabase) {
        val existingCount = db.query("SELECT COUNT(*) FROM words").use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
        if (existingCount >= SeedData.words.size) return

        db.beginTransaction()
        try {
            SeedData.words.forEach { word ->
                db.execSQL(
                    "INSERT OR IGNORE INTO words " +
                        "(id, language, russian, translation, gender, example, aspectPair, isFavorite, nextReviewDate, reviewStage) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 0, 0, 0)",
                    arrayOf(word.id, word.language, word.russian, word.translation, word.gender, word.example, word.aspectPair)
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RusdiliDatabase =
        Room.databaseBuilder(context, RusdiliDatabase::class.java, "rusdili.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    seedWordsIfNeeded(db)
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
                    seedWordsIfNeeded(db)
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

    @Provides
    fun provideReviewSessionDao(database: RusdiliDatabase): ReviewSessionDao =
        database.reviewSessionDao()
}
