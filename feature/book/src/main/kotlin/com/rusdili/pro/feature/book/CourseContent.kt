package com.rusdili.pro.feature.book

data class ExampleSentence(val russian: String, val azerbaijani: String, val transliteration: String)

data class LessonExercise(
    val type: String,
    val question: String,
    val options: List<String>,
    val answer: String,
    val explanation: String
)

data class Chapter(
    val id: Int,
    val title: String,
    val rule: String,
    val examples: List<ExampleSentence>,
    val lessons: List<String>,
    val exercises: List<LessonExercise>
)

private val exerciseTypes = listOf(
    "Tərcüməni seç",
    "Düzgün formanı seç",
    "Halı müəyyən et",
    "Cümləni qur",
    "Dinlə və yaz"
)

private fun exerciseSet(
    question: String,
    options: List<String>,
    answer: String,
    explanation: String,
    otherQuestions: List<String>,
    otherAnswers: List<String>,
    otherOptions: List<List<String>>,
    otherExplanations: List<String>
) = listOf(
    LessonExercise(exerciseTypes[0], question, options, answer, explanation),
    LessonExercise(exerciseTypes[1], otherQuestions[0], otherOptions[0], otherAnswers[0], otherExplanations[0]),
    LessonExercise(exerciseTypes[2], otherQuestions[1], otherOptions[1], otherAnswers[1], otherExplanations[1]),
    LessonExercise(exerciseTypes[3], otherQuestions[2], otherOptions[2], otherAnswers[2], otherExplanations[2]),
    LessonExercise(
        exerciseTypes[4],
        otherQuestions[3],
        listOf(otherAnswers[3], otherAnswers[2], otherAnswers[1]).distinct(),
        otherAnswers[3],
        otherExplanations[3]
    )
)

val chapters = listOf(
    Chapter(
        1, "Əlifba",
        "Rus əlifbasında 33 hərf var: 10 sait, 21 samit və ь, ъ işarələri. Sait cütləri А-Я, Э-Е, Ы-И, О-Ё, У-Ю-dur. Ь əvvəlki samiti yumşaldır (мать), Ъ isə yumşaltmanı bloklayır (въезд). Ж, Ш, Ц həmişə sərt; Ч, Щ həmişə yumşaqdır.",
        listOf(
            ExampleSentence("Мама читает.", "Ana oxuyur.", "Mama çitayet."),
            ExampleSentence("Это мой дом.", "Bu mənim evimdir.", "Eta moy dom."),
            ExampleSentence("Я учу русский язык.", "Mən rus dilini öyrənirəm.", "Ya uçu russkiy yazık.")
        ),
        listOf("Sait və samitlər", "Yumşaq və sərt səslər", "Sözləri oxu"),
        exerciseSet(
            "Rus əlifbasında neçə hərf var?", listOf("33", "32", "30"), "33",
            "Rus əlifbasında 33 hərf var: 10 sait, 21 samit və 2 işarə.",
            listOf("Ь hansı işi görür?", "въезд sözündə Ъ nə edir?", "Sərt samitləri seç", "Əlifba sözünü yaz"),
            listOf("Əvvəlki samiti yumşaldır", "Yumşaltmanı bloklayır", "Ж, Ш, Ц", "алфавит"),
            listOf(listOf("Əvvəlki samiti yumşaldır", "Saiti uzadır"), listOf("Yumşaltmanı bloklayır", "Sözü yumşaldır"), listOf("Ж, Ш, Ц", "Ч, Щ")),
            listOf("Ь əvvəlki samiti yumşaldır.", "Ъ yumşaltmanı bloklayır.", "Ж, Ш, Ц həmişə sərt səslənir.", "Sözün yazılışına diqqət et.")
        )
    ),
    Chapter(
        2, "Vurğu və reduksiya",
        "Rus dilində vurğu sərbəstdir və sözün müxtəlif hecalarına düşə bilər. Vurğusuz O çox vaxt A kimi, yumşaq samitdən sonra vurğusuz E isə İ kimi səslənir. Eşitdiyin kimi yazma: kökü yoxla, məsələn вода → воды.",
        listOf(
            ExampleSentence("Молоко стоит на столе.", "Süd masanın üstündədir.", "Malako stait na stale."),
            ExampleSentence("Вода холодная.", "Su soyuqdur.", "Vada xalodnaya."),
            ExampleSentence("Воды много.", "Su çoxdur.", "Vodı mnoga.")
        ),
        listOf("Vurğunu tap", "Saitlərin zəifləməsi", "Yazılışı yoxla"),
        exerciseSet(
            "Vurğusuz O necə səslənə bilər?", listOf("A kimi", "U kimi", "Həmişə O kimi"), "A kimi",
            "Vurğusuz O çox vaxt A kimi tələffüz olunur, amma O yazılır.",
            listOf("Vurğu rus dilində necədir?", "вода sözünün yoxlama forması", "Düzgün yazılışı seç", "Vurğunu yadda saxla"),
            listOf("Sərbəst", "воды", "молоко", "молоко"),
            listOf(listOf("Sərbəst", "Həmişə birinci hecada"), listOf("воды", "води"), listOf("молоко", "малако")),
            listOf("Vurğu sözün müxtəlif hecalarına düşə bilər.", "Vurğulu ы kök saitini yoxlayır.", "Tələffüzə əsasən малако yazmaq olmaz.", "Sözün yazılışını eşitdiyin səsə görə dəyişmə.")
        )
    ),
    Chapter(
        3, "İsim və Cins",
        "Samitlə bitən isimlər əsasən kişi cinsindədir: стол, брат. -а/-я ilə bitənlər qadın, -о/-е ilə bitənlər orta cinsdədir. -ь ilə bitən sözlərin cinsi dəyişə bilər: день kişi, ночь və мать qadın cinsidir.",
        listOf(
            ExampleSentence("Большой стол здесь.", "Böyük masa buradadır.", "Bolşoy stol zdes."),
            ExampleSentence("Новая книга интересная.", "Yeni kitab maraqlıdır.", "Novaya kniga interesnaya."),
            ExampleSentence("Окно открыто.", "Pəncərə açıqdır.", "Akno atkrıta.")
        ),
        listOf("Kişi və qadın cinsi", "Orta cins", "İstisnaları yadda saxla"),
        exerciseSet(
            "книга hansı cinsdədir?", listOf("Qadın", "Kişi", "Orta"), "Qadın",
            "книга -а ilə bitir, ona görə qadın cinsindədir.",
            listOf("день hansı cinsdədir?", "окно hansı cinsdədir?", "мать hansı cinsdədir?", "Cinsi bildirən sözü seç"),
            listOf("Kişi", "Orta", "Qadın", "новая книга"),
            listOf(listOf("Kişi", "Qadın"), listOf("Orta", "Kişi"), listOf("Qadın", "Kişi")),
            listOf("день -ь ilə bitsə də kişi cinsindədir.", "окно -о ilə bitir və orta cinsdədir.", "мать istisna olaraq qadın cinsindədir.", "Qadın cinsindəki isimlə новая işlənir.")
        )
    ),
    Chapter(
        4, "6 Hal",
        "Adlıq (кто? что?) mübtəda, yiyəlik (кого? чего?) yoxluq və sahiblik, yönlük (кому? чему?) yönəlmə bildirir. Təsirlik (кого? что?) birbaşa tamamlıq, творительный (кем? чем?) vasitə, предложный (о ком? о чём? где?) isə yalnız önlüklə işlənir. Canlı kişi isimlərində təsirlik yiyəliklə eynidir; cansız kişi və orta cinsdə adlıqla eynidir.\n\nстол: стол, стола, столу, стол, столом, столе / столы, столов, столам, столы, столами, столах\nкнига: книга, книги, книге, книгу, книгой, книге / книги, книг, книгам, книги, книгами, книгах\nокно: окно, окна, окну, окно, окном, окне / окна, окон, окнам, окна, окнами, окнах",
        listOf(
            ExampleSentence("Это новый стол.", "Bu yeni masadır.", "Eta novıy stol."),
            ExampleSentence("Я читаю книгу.", "Mən kitab oxuyuram.", "Ya çitayu knigu."),
            ExampleSentence("Я думаю о доме.", "Mən ev haqqında düşünürəm.", "Ya dumayu a dome.")
        ),
        listOf("Hal sualları", "стол / книга / окно cədvəli", "Önlüklərlə işlənmə"),
        exerciseSet(
            "Я читаю книгу cümləsində книгу hansı haldadır?", listOf("Təsirlik", "Adlıq", "Yönlük"), "Təsirlik",
            "книгу birbaşa tamamlıqdır və что? sualına cavab verən təsirlik haldadır.",
            listOf("книги нет: книги hansı haldadır?", "кому? sualı hansı haldır?", "о доме: hansı haldır?", "книга sözünü cümləyə yerləşdir"),
            listOf("Yiyəlik", "Yönlük", "Предложный", "Я читаю книгу."),
            listOf(listOf("Yiyəlik", "Adlıq"), listOf("Yönlük", "Təsirlik"), listOf("Предложный", "Adlıq")),
            listOf("Yoxluq bildirən нет yiyəlik hal tələb edir.", "кому? sualı yönlük halın sualıdır.", "о önlüyündən sonra предложный işlənir.", "книга təsirlikdə книгу olur.")
        )
    ),
    Chapter(
        5, "Sifət",
        "Sifət isimlə cinsə, saya və hala görə uzlaşır: новый дом, новая книга, новое окно, новые дома. Qısa sifət adətən xəbər kimi işlənir: дом новый. Müqayisə dərəcələri: хороший — лучше — лучший; плохой — хуже — худший.",
        listOf(
            ExampleSentence("Новый дом большой.", "Yeni ev böyükdür.", "Novıy dom bolşoy."),
            ExampleSentence("Новая книга интересная.", "Yeni kitab maraqlıdır.", "Novaya kniga interesnaya."),
            ExampleSentence("Новое окно открыто.", "Yeni pəncərə açıqdır.", "Novoye akno atkrıta.")
        ),
        listOf("Cinsə görə uzlaşma", "Hal və say", "Müqayisə dərəcələri"),
        exerciseSet(
            "___ книга", listOf("новая", "новый", "новое"), "новая",
            "книга qadın cinsindədir, ona görə sifət новая formasında olmalıdır.",
            listOf("___ окно", "___ дома", "хороший sözünün müqayisə forması", "Düzgün cümləni seç"),
            listOf("новое", "новые", "лучше", "Новая книга"),
            listOf(listOf("новое", "новая"), listOf("новые", "новый"), listOf("лучше", "хорошее")),
            listOf("окно orta cinsdədir: новое окно.", "дома cəm formadadır: новые дома.", "хороший sözünün müqayisəsi лучше-dir.", "книга qadın cinsindədir.")
        )
    ),
    Chapter(
        6, "Əvəzliklər",
        "Şəxs əvəzlikləri altı halda dəyişir: я/меня/мне/меня/мной/обо мне, ты/тебя/тебе/тебя/тобой/о тебе, он/его/ему/его/им/о нём, она/её/ей/её/ей/о ней, мы/нас/нам/нас/нами/о нас, вы/вас/вам/вас/вами/о вас, они/их/им/их/ими/о них. мой/mоя/моё/мои isimlə uzlaşır; его, её, их dəyişmir. Вы həm cəm, həm də nəzakətli təkdir.",
        listOf(
            ExampleSentence("Я вижу его.", "Mən onu görürəm.", "Ya víju yevó."),
            ExampleSentence("Она говорит обо мне.", "O mənim haqqımda danışır.", "Ana gavarit aba mnye."),
            ExampleSentence("Это моя книга.", "Bu mənim kitabımdır.", "Eta maya kniga.")
        ),
        listOf("Şəxs əvəzlikləri", "Mənsubiyyət əvəzlikləri", "Вы: cəm və nəzakət"),
        exerciseSet(
            "Я вижу ___ (он).", listOf("его", "ему", "им"), "его",
            "вижу birbaşa tamamlıq tələb edir; он təsirlikdə его olur.",
            listOf("___ книга (мой)", "___ не меняется", "кому? ___ (я)", "Düzgün variantı seç"),
            listOf("моя", "его", "мне", "Я говорю о тебе."),
            listOf(listOf("моя", "мой"), listOf("его", "ему"), listOf("мне", "меня")),
            listOf("книга qadın cinsindədir: моя книга.", "его mənsubiyyət əvəzliyi dəyişmir.", "я yönlük halda мне olur.", "о önlüyündən sonra предложный: о тебе.")
        )
    ),
    Chapter(
        7, "Saylar",
        "1 sayından sonra isim adlıq təkdə olur: один день. 2, 3, 4-dən sonra yiyəlik tək işlənir: два дня, три книги. 5 və yuxarıdan sonra yiyəlik cəm gəlir: пять дней. 21 sayında son söz 1 olduğu üçün один день işlənir.",
        listOf(
            ExampleSentence("Один день.", "Bir gün.", "Adin den."),
            ExampleSentence("Три книги.", "Üç kitab.", "Tri knigi."),
            ExampleSentence("Пять дней.", "Beş gün.", "Pyat dney.")
        ),
        listOf("1-dən sonra isim", "2, 3, 4-dən sonra", "5 və yuxarı"),
        exerciseSet(
            "два ___ (день)", listOf("дня", "дней", "день"), "дня",
            "2-dən sonra isim yiyəlik təkdə olur: два дня.",
            listOf("пять ___ (книга)", "один ___ (день)", "три ___ (книга)", "21 ___ (день)"),
            listOf("книг", "день", "книги", "двадцать один день"),
            listOf(listOf("книг", "книги"), listOf("день", "дня"), listOf("книги", "книг")),
            listOf("5-dən sonra yiyəlik cəm: пять книг.", "1-dən sonra adlıq tək: один день.", "3-dən sonra yiyəlik tək: три книги.", "21 sonundakı один qaydasına tabedir.")
        )
    ),
    Chapter(
        8, "Feilin Növü",
        "Bitməmiş növ proses və təkrarı bildirir: я читаю. Bitmiş növ bir dəfə tamamlanmış nəticədir: я прочитал. Bitmiş növün indiki zamanı yoxdur. каждый день, всегда, часто bitməmiş növə; вчера, уже, вдруг və tamamlanmış nəticə bitmiş növə işarə edir. Feilləri cüt öyrən: делать—сделать, читать—прочитать, писать—написать, говорить—сказать, смотреть—посмотреть, видеть—увидеть, слушать—послушать, брать—взять, давать—дать, помогать—помочь, начинать—начать, заканчивать—закончить, покупать—купить, готовить—приготовить, учить—выучить, думать—подумать.",
        listOf(
            ExampleSentence("Каждый день я читаю.", "Hər gün oxuyuram.", "Kajdıy den ya çitayu."),
            ExampleSentence("Вчера я прочитал книгу.", "Dünən kitabı oxuyub bitirdim.", "Vçera ya praçital knigu."),
            ExampleSentence("Я буду читать вечером.", "Axşam oxuyacağam.", "Ya budu çitat veçeram.")
        ),
        listOf("Proses və nəticə", "Feil cütləri", "Zaman sözləri"),
        exerciseSet(
            "каждый день ilə uyğun növ", listOf("читать", "прочитать", "прочитал"), "читать",
            "каждый день təkrarı bildirir, buna görə bitməmiş növ seçilir.",
            listOf("вчера, tamamlanmış nəticə", "Bitmiş növün indiki zamanı varmı?", "делать sözünün bitmiş növü", "Cümləni tamamla: Каждый день я ___"),
            listOf("прочитал", "Xeyr", "сделать", "читаю"),
            listOf(listOf("прочитал", "читаю"), listOf("Xeyr", "Bəli"), listOf("сделать", "делал")),
            listOf("вчера ilə tamamlanmış nəticədə bitmiş növ işlənir.", "Bitmiş növün indiki zamanı olmur.", "делать—сделать feil cütüdür.", "Hər gün təkrar üçün читаю işlənir.")
        )
    ),
    Chapter(
        9, "Feil Zamanları",
        "I təsrif: -ать/-ять/-еть feilləri: читаю, читаешь, читает, читаем, читаете, читают. II təsrifin nümunəsi: говорю, говоришь, говорит, говорим, говорите, говорят. Keçmiş zamanda şəxs deyil, cins və say dəyişir: читал/читала/читало/читали. Bitməmiş gələcək буду + məsdər, bitmiş gələcəkdə isə прочитаю kimi forma işlənir.",
        listOf(
            ExampleSentence("Я читаю книгу.", "Mən kitab oxuyuram.", "Ya çitayu knigu."),
            ExampleSentence("Вчера она читала.", "Dünən o oxuyurdu.", "Vçera ana çitala."),
            ExampleSentence("Завтра я буду читать.", "Sabah oxuyacağam.", "Zavtra ya budu çitat.")
        ),
        listOf("İndiki zaman", "Keçmiş zaman", "Gələcək zaman"),
        exerciseSet(
            "Keçmiş zamanda qadın cinsi: она ___", listOf("читала", "читал", "читали"), "читала",
            "Keçmiş zamanda feil cinsə görə dəyişir; qadın cinsi üçün -а: читала.",
            listOf("Я ___ (читать, indiki)", "мы ___ (говорить, indiki)", "Bitməmiş gələcək forması", "Sabah mən oxuyacağam"),
            listOf("читаю", "говорим", "буду + məsdər", "Завтра я буду читать."),
            listOf(listOf("читаю", "читаешь"), listOf("говорим", "говорит"), listOf("буду + məsdər", "yalnız keçmiş forma")),
            listOf("Я ilə читаю işlənir.", "мы ilə говорим işlənir.", "Bitməmiş gələcək буду və məsdərdən qurulur.", "буду читать bitməmiş gələcək zamandır.")
        )
    ),
    Chapter(
        10, "Hərəkət Feilləri",
        "İndi bir istiqamətə piyada getmək идти, nəqliyyatla getmək ехать, təyyarə ilə uçmaq лететь-dir. Vərdiş və müxtəlif istiqamətlər üçün ходить, ездить, летать işlənir. Сейчас я иду домой — indi evə gedirəm; Каждый день я хожу на работу — hər gün işə gedirəm.",
        listOf(
            ExampleSentence("Сейчас я иду домой.", "İndi evə gedirəm.", "Seyças ya idu damoy."),
            ExampleSentence("Каждый день я хожу на работу.", "Hər gün işə gedirəm.", "Kajdıy den ya xaju na rabotu."),
            ExampleSentence("Мы едем на автобусе.", "Biz avtobusla gedirik.", "Mı yedem na avtobuse.")
        ),
        listOf("İndi bir istiqamət", "Nəqliyyatla hərəkət", "Vərdiş və təkrarlanma"),
        exerciseSet(
            "Сейчас, piyada bir istiqamətdə: я ___ домой", listOf("иду", "хожу", "ездю"), "иду",
            "Сейчас və bir istiqamət üçün идти: я иду.",
            listOf("Каждый день я ___ на работу", "Nəqliyyatla indi getmək", "Piyada vərdişlə getmək", "Düzgün cümləni seç"),
            listOf("хожу", "ехать", "ходить", "Сейчас я иду домой."),
            listOf(listOf("хожу", "иду"), listOf("ехать", "идти"), listOf("ходить", "идти")),
            listOf("Каждый день vərdişdir: хожу.", "Nəqliyyatla getmək ехать-dir.", "Piyada təkrarlanan hərəkət ходить-dir.", "İndi bir istiqamət üçün иду işlənir.")
        )
    ),
    Chapter(
        11, "Qayıdış Feilləri",
        "Qayıdış feilləri samitdən sonra -ся, saitdən sonra -сь ilə bitir: умываться, учусь. Qayıdış şəkilçisi mənanı dəyişə bilər: учить — öyrətmək, учиться — təhsil almaq; встретить — qarşılamaq, встретиться — görüşmək. нравиться, смеяться, бояться həmişə qayıdış formadadır.",
        listOf(
            ExampleSentence("Я учусь в школе.", "Mən məktəbdə oxuyuram.", "Ya uçus v shkole."),
            ExampleSentence("Мы встретимся завтра.", "Biz sabah görüşəcəyik.", "Mı vstretimsya zavtra."),
            ExampleSentence("Ребёнок смеётся.", "Uşaq gülür.", "Rebyonak smeyotsya.")
        ),
        listOf("-ся və -сь", "Mənanın dəyişməsi", "Həmişə qayıdış olan feillər"),
        exerciseSet(
            "учить → təhsil almaq", listOf("учиться", "учитьсь", "учите"), "учиться",
            "учиться qayıdış feilidir və mənası 'təhsil almaq'dır.",
            listOf("учусь formasının sonluğu", "встретить → qarşılıqlı görüşmək", "Həmişə qayıdış olan feil", "Düzgün formanı seç"),
            listOf("-сь", "встретиться", "бояться", "Я учусь."),
            listOf(listOf("-сь", "-ся"), listOf("встретиться", "встречать"), listOf("бояться", "боять")),
            listOf("учусь saitdən sonra -сь ilə yazılır.", "встретиться qayıdış forması görüşmək deməkdir.", "бояться həmişə qayıdış feili kimi işlənir.", "учусь düzgün formadır.")
        )
    ),
    Chapter(
        12, "Əmr və Şərt",
        "Əmr formaları: читай/читайте, говори/говорите, иди/идите. Yumşaq təklif давай + feil ilə qurulur: Давай пойдём. Şərt şəkli keçmiş zaman forması + бы ilə qurulur: хотел бы. Real şərtdə если + indiki/gələcək, nəticədə gələcək işlənir; qeyri-real şərtdə если бы + keçmiş, nəticədə keçmiş + бы işlənir.",
        listOf(
            ExampleSentence("Читайте эту книгу.", "Bu kitabı oxuyun.", "Çitayte etu knigu."),
            ExampleSentence("Давай пойдём домой.", "Gəl evə gedək.", "Davay paydyom damoy."),
            ExampleSentence("Я хотел бы чай.", "Mən çay istərdim.", "Ya xatel by çay.")
        ),
        listOf("Əmr forması", "Təklif", "Real və qeyri-real şərt"),
        exerciseSet(
            "Nəzakətli cəm əmr: говорить", listOf("говорите", "говори", "говорят"), "говорите",
            "Cəm və ya nəzakətli əmr forması говорите-dir.",
            listOf("Yumşaq təklif", "хотел ___", "давай + идти", "Şərt formasını seç"),
            listOf("давай + feil", "бы", "пойдём", "Я хотел бы чай."),
            listOf(listOf("давай + feil", "если + feil"), listOf("бы", "ли"), listOf("пойдём", "идите")),
            listOf("давай + feil yumşaq təklif bildirir.", "Şərt formasında keçmiş zaman + бы işlənir.", "Давай пойдём yumşaq təklifdir.", "хотел бы keçmiş forma + бы ilə qurulub.")
        )
    ),
    Chapter(
        13, "Feili Sifət və Feili Zərf",
        "Bu mərhələdə formaları tanımaq kifayətdir. İndiki zaman feli sifəti: читающий; keçmiş aktiv: читавший; indiki passiv: читаемый; keçmiş passiv: прочитанный. Bitməmiş növ feili zərfi читая (oxuyarkən), bitmiş növ feili zərfi прочитав (oxuyub bitirdikdən sonra) formasıdır.",
        listOf(
            ExampleSentence("Читающий студент улыбается.", "Oxuyan tələbə gülümsəyir.", "Çitayuşçiy student ulıbayetsya."),
            ExampleSentence("Читая, она пила чай.", "Oxuyarkən o çay içirdi.", "Çitaya ana pila çay."),
            ExampleSentence("Прочитав письмо, он ушёл.", "Məktubu oxuyub qurtarandan sonra getdi.", "Praçitav pismo on ushyol.")
        ),
        listOf("Feli sifətin növləri", "Feili zərf", "Formaları tanı"),
        exerciseSet(
            "читая hansı formadır?", listOf("Bitməmiş feili zərf", "Keçmiş passiv feli sifət", "İndiki aktiv feli sifət"), "Bitməmiş feili zərf",
            "читая -ая ilə bitən bitməmiş növ feili zərfdir.",
            listOf("прочитанный hansı formadır?", "читавший hansı formadır?", "прочитав hansı formadır?", "Oxuyarkən"),
            listOf("Keçmiş passiv feli sifət", "Keçmiş aktiv feli sifət", "Bitmiş feili zərf", "читая"),
            listOf(listOf("Keçmiş passiv feli sifət", "Bitməmiş feili zərf"), listOf("Keçmiş aktiv feli sifət", "İndiki passiv"), listOf("Bitmiş feili zərf", "İndiki aktiv")),
            listOf("прочитанный keçmiş passiv feli sifətdir.", "читавший keçmiş aktiv feli sifətdir.", "прочитав bitmiş feili zərfdir.", "Oxuyarkən mənasında читая işlənir.")
        )
    ),
    Chapter(
        14, "Sintaksis",
        "Əsas söz sırası SVO-dur: Я читаю книгу. Vurğu üçün sıra dəyişə bilər: Книгу читаю я — kitabı oxuyan məhz mənəm. Sual çox vaxt intonasiya ilə qurulur. İkiqat inkar normaldır: Я ничего не знаю. который, что, когда, если, потому что, чтобы ilə qurulan budaq cümlədən əvvəl vergül vacibdir: Я знаю, что ты придёшь.",
        listOf(
            ExampleSentence("Я читаю книгу.", "Mən kitab oxuyuram.", "Ya çitayu knigu."),
            ExampleSentence("Ты идёшь?", "Sən gedirsən?", "Tı idyoş?"),
            ExampleSentence("Я знаю, что ты придёшь.", "Bilirəm ki, sən gələcəksən.", "Ya znayu, şto tı pridyoş.")
        ),
        listOf("Söz sırası və vurğu", "Sual və inkar", "Budaq cümlə və vergül"),
        exerciseSet(
            "Я ___ книгу.", listOf("читаю", "книгу читаю я", "читаю я книгу"), "читаю",
            "Əsas SVO sırası: Я (mübtəda) + читаю (xəbər) + книгу (tamamlıq).",
            listOf("Я знаю ___ ты придёшь.", "İkiqat inkar rus dilində", "Kitabı oxuyan mənəm: ___", "Düzgün yazılışı seç"),
            listOf("что", "Normaldır", "Книгу читаю я", "Я знаю, что ты придёшь."),
            listOf(listOf("что", "потому"), listOf("Normaldır", "Yanlışdır"), listOf("Книгу читаю я", "Я книгу я читаю")),
            listOf("что ilə budaq cümlədən əvvəl vergül qoyulur.", "Rus dilində ikiqat inkar normaldır.", "Söz sırasını dəyişməklə Я vurğulanır.", "Budaq cümlədən əvvəl vergül mütləqdir.")
        )
    ),
    Chapter(
        15, "Orfoqrafiya",
        "Ж, Ш, Ч, Щ-dən sonra Ы yazılmır, həmişə И yazılır: жи, ши, чи, щи. Ж, Ч, Ш, Щ, Ц-dən sonra Я/Ю yox, А/У yazılır: ча, ща, чу, щу. Г, К, Х-dan sonra həmişə И gəlir: ги, ки, хи. Ё həmişə vurğuludur. Vurğusuz sait üçün kökü yoxla: вода → воды.",
        listOf(
            ExampleSentence("Малыши нашли ключи.", "Balacalar açarları tapdılar.", "Malışi našli klyuçi."),
            ExampleSentence("Щука живёт в реке.", "Durnabalığı çayda yaşayır.", "Şçuka jivyot v reke."),
            ExampleSentence("Вода нужна всем.", "Su hamıya lazımdır.", "Vada nujna vsem.")
        ),
        listOf("ЖИ-ШИ, ЧА-ЩА", "ЧУ-ЩУ, ГИ-КИ-ХИ", "Vurğusuz saitləri yoxla"),
        exerciseSet(
            "Düzgün yazılış", listOf("жи", "жы", "жыи"), "жи",
            "Ж-dən sonra И yazılır: жи, heç vaxt жы deyil.",
            listOf("ча yoxsa чя?", "Düzgün yazılış", "вода sözünü yoxla", "Щ-dən sonra seç"),
            listOf("ча", "мыши", "воды", "щи"),
            listOf(listOf("ча", "чя"), listOf("мыши", "мышы"), listOf("воды", "вады")),
            listOf("Ч-dən sonra А yazılır: ча.", "Ш-dən sonra И yazılır: мыши.", "вода sözündə sait воды forması ilə yoxlanır.", "Щ-dən sonra И yazılır: щи.")
        )
    )
)
