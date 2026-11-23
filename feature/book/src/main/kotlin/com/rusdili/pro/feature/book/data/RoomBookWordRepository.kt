package com.rusdili.pro.feature.book.data

import com.rusdili.pro.core.common.Word
import com.rusdili.pro.core.database.WordDao
import com.rusdili.pro.core.database.WordEntity
import com.rusdili.pro.feature.book.domain.BookWordRepository
import com.rusdili.pro.feature.book.domain.WordLookup
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class RoomBookWordRepository @Inject constructor(
    private val dao: WordDao
) : BookWordRepository {
    override suspend fun lookup(token: String): WordLookup {
        val clean = token.trim('.', ',', '!', '?', ':', ';', '"', '«', '»', '(', ')').lowercase()
        val lemma = inflectedForms[clean] ?: clean
        val entity = dao.findByRussian(clean) ?: dao.findByRussian(lemma)
        val word = entity?.toDomain()
        return WordLookup(
            token = clean,
            word = word,
            grammaticalForm = when {
                clean in grammaticalForms -> grammaticalForms.getValue(clean)
                word == null -> "Qrammatik forma lüğətdə müəyyən edilmədi"
                word.aspectPair != null -> "Feil, məsdər forması; cütü: ${word.aspectPair}"
                word.gender != null -> "İsim, ${word.gender} cinsi; bu formanın halı kontekstdən asılıdır"
                else -> "Sözlük forması"
            }
        )
    }
}

private val inflectedForms = mapOf(
    "книгу" to "книга", "книги" to "книга", "книге" to "книга",
    "стола" to "стол", "столу" to "стол", "столе" to "стол", "столом" to "стол",
    "дома" to "дом", "дому" to "дом", "доме" to "дом", "домом" to "дом",
    "окна" to "окно", "окну" to "окно", "окне" to "окно", "окном" to "окно",
    "воды" to "вода", "воде" to "вода", "воду" to "вода", "водой" to "вода",
    "дня" to "день", "днём" to "день", "дне" to "день",
    "молока" to "молоко", "молоком" to "молоко",
    "читаю" to "читать", "читает" to "читать", "читала" to "читать",
    "прочитал" to "прочитать", "говорит" to "говорить", "говорю" to "говорить",
    "вижу" to "видеть", "думаю" to "думать", "идёшь" to "идти",
    "пью" to "пить", "пила" to "пить", "буду" to "быть",
    "знаю" to "знать", "придёшь" to "прийти", "пойдём" to "пойти",
    "хотел" to "хотеть", "стоит" to "стоять", "едет" to "ехать",
    "светит" to "светить", "ушёл" to "уйти"
)

private val grammaticalForms = mapOf(
    "книгу" to "İsim, qadın cinsi, tək təsirlik hal",
    "книги" to "İsim, qadın cinsi; yiyəlik tək və ya adlıq/təsirlik cəm",
    "книге" to "İsim, qadın cinsi, yönlük və ya yerlik hal",
    "столе" to "İsim, kişi cinsi, yerlik hal",
    "доме" to "İsim, kişi cinsi, yerlik hal",
    "дня" to "İsim, kişi cinsi, yiyəlik tək",
    "воды" to "İsim, qadın cinsi; yiyəlik tək və ya adlıq/təsirlik cəm",
    "читаю" to "Feil, indiki zaman, I şəxs tək",
    "читает" to "Feil, indiki zaman, III şəxs tək",
    "говорит" to "Feil, indiki zaman, III şəxs tək",
    "вижу" to "Feil, indiki zaman, I şəxs tək",
    "думаю" to "Feil, indiki zaman, I şəxs tək",
    "идёшь" to "Feil, indiki zaman, II şəxs tək"
)

private fun WordEntity.toDomain() = Word(
    russian = russian,
    translation = translation,
    gender = gender,
    example = example,
    aspectPair = aspectPair,
    isFavorite = isFavorite,
    nextReviewDate = nextReviewDate,
    reviewStage = reviewStage
)

@Module
@InstallIn(SingletonComponent::class)
abstract class BookWordRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindBookWordRepository(implementation: RoomBookWordRepository): BookWordRepository
}
