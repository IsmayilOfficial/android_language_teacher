package com.rusdili.pro.feature.book.data

import com.rusdili.pro.core.common.LearningLanguage
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
    override suspend fun lookup(token: String, language: LearningLanguage): WordLookup {
        val clean = token
            .trim('.', ',', '!', '?', ':', ';', '"', '«', '»', '(', ')', '¡', '¿')
            .lowercase()
        val inflections = when (language) {
            LearningLanguage.RUSSIAN -> russianInflectedForms
            LearningLanguage.SPANISH -> spanishInflectedForms
            LearningLanguage.FRENCH -> frenchInflectedForms
        }
        val lemma = inflections[clean] ?: clean
        val entity = dao.findByRussian(language.code, clean) ?: dao.findByRussian(language.code, lemma)
        val word = entity?.toDomain()
        val grammaticalForm = when {
            language == LearningLanguage.RUSSIAN && clean in russianGrammaticalForms ->
                russianGrammaticalForms.getValue(clean)
            word == null -> "Qrammatik forma lüğətdə müəyyən edilmədi"
            word.aspectPair != null -> "Feil, məsdər forması; cütü: ${word.aspectPair}"
            word.gender != null -> when (language) {
                LearningLanguage.RUSSIAN -> "İsim, ${word.gender} cinsi; halı kontekstdən asılıdır"
                LearningLanguage.SPANISH -> {
                    val art = if (word.gender == "kişi") "el / un" else "la / una"
                    "İsim, ${word.gender} cinsi (artikl: $art)"
                }
                LearningLanguage.FRENCH -> {
                    val art = if (word.gender == "kişi") "le / un" else "la / une"
                    "İsim, ${word.gender} cinsi (artikl: $art)"
                }
            }
            else -> "Sözlük forması"
        }
        return WordLookup(token = clean, word = word, grammaticalForm = grammaticalForm)
    }
}

private val russianInflectedForms = mapOf(
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

private val spanishInflectedForms = mapOf(
    "soy" to "ser", "eres" to "ser", "es" to "ser", "somos" to "ser", "son" to "ser",
    "estoy" to "estar", "estás" to "estar", "está" to "estar", "estamos" to "estar", "están" to "estar",
    "tengo" to "tener", "tienes" to "tener", "tiene" to "tener", "tenemos" to "tener",
    "hablo" to "hablar", "hablas" to "hablar", "habla" to "hablar",
    "leo" to "leer", "lees" to "leer", "lee" to "leer", "leemos" to "leer", "leí" to "leer",
    "vivo" to "vivir", "vives" to "vivir", "vive" to "vivir", "vivimos" to "vivir",
    "voy" to "ir", "vas" to "ir", "va" to "ir", "vamos" to "ir",
    "trabajo" to "trabajar", "trabajas" to "trabajar", "trabaja" to "trabajar",
    "estudio" to "estudiar", "estudias" to "estudiar", "estudia" to "estudiar",
    "compré" to "comprar", "compro" to "comprar", "libros" to "libro", "casas" to "casa",
    "ventanas" to "ventana", "hermanos" to "hermano", "amigos" to "amigo",
    "blanca" to "blanco", "roja" to "rojo", "nuevos" to "nuevo", "grandes" to "grande",
    "caliente" to "caliente", "fría" to "frío"
)

private val frenchInflectedForms = mapOf(
    "suis" to "être", "es" to "être", "est" to "être", "sommes" to "être", "êtes" to "être", "sont" to "être",
    "ai" to "avoir", "as" to "avoir", "a" to "avoir", "avons" to "avoir", "avez" to "avoir", "ont" to "avoir",
    "parle" to "parler", "parles" to "parler", "parlons" to "parler", "parlez" to "parler", "parlent" to "parler",
    "lis" to "lire", "lit" to "lire", "lisons" to "lire", "lu" to "lire",
    "vais" to "aller", "vas" to "aller", "va" to "aller", "allons" to "aller", "vont" to "aller",
    "travaille" to "travailler", "travaillent" to "travailler",
    "étudie" to "étudier", "étudient" to "étudier",
    "j'ai" to "avoir", "j'aime" to "aimer", "j'étudie" to "étudier", "j'achète" to "acheter",
    "l'école" to "école", "l'hôtel" to "hôtel", "l'eau" to "eau",
    "livres" to "livre", "chats" to "chat", "fleurs" to "fleur",
    "blanche" to "blanc", "noire" to "noir", "rouges" to "rouge", "intéressante" to "intéressant",
    "grande" to "grand", "petite" to "petit", "belles" to "beau"
)

private val russianGrammaticalForms = mapOf(
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
    reviewStage = reviewStage,
    language = language
)

@Module
@InstallIn(SingletonComponent::class)
abstract class BookWordRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindBookWordRepository(implementation: RoomBookWordRepository): BookWordRepository
}
