package com.rusdili.pro.feature.book

private val frenchExerciseTypes = listOf(
    "Tərcüməni seç",
    "Düzgün formanı seç",
    "Səhvi tap",
    "Cümləni qur",
    "Dinlə və yaz"
)

private fun frenchExerciseSet(
    q1: String, o1: List<String>, a1: String, e1: String,
    q2: String, o2: List<String>, a2: String, e2: String,
    q3: String, o3: List<String>, a3: String, e3: String,
    q4: String, o4: List<String>, a4: String, e4: String,
    q5: String, a5: String, e5: String
) = listOf(
    LessonExercise(frenchExerciseTypes[0], q1, o1, a1, e1),
    LessonExercise(frenchExerciseTypes[1], q2, o2, a2, e2),
    LessonExercise(frenchExerciseTypes[2], q3, o3, a3, e3),
    LessonExercise(frenchExerciseTypes[3], q4, o4, a4, e4),
    LessonExercise(frenchExerciseTypes[4], q5, listOf(a5), a5, e5)
)

val frenchChapters: List<Chapter> = listOf(
    Chapter(
        1, "Əlifba və tələffüz",
        "Fransız əlifbasında 26 hərf var. Çox vaxt söz sonundakı samitlər (s, t, d, x, z) oxunmur (Paris → Pari). H hərfi səssizdir. 'ou' [u], 'au/eau' [o], 'oi' [ua], 'ch' [ş] kimi oxunur; 'ç' (cédille) isə 's' səsi verir (français).",
        listOf(
            ExampleSentence("Bonjour ! Je m'appelle Aysel.", "Salam! Mənim adım Ayseldir.", "Bonjur! Jö mapel Aysel."),
            ExampleSentence("J'aime le français et la musique.", "Mən fransız dilini və musiqini sevirəm.", "Jem lö franse e la müzik."),
            ExampleSentence("Merci beaucoup pour l'eau.", "Su üçün çox sağ olun.", "Mersi boku pur lo.")
        ),
        listOf("Səssiz sonluqlar və H", "Sait birləşmələri (ou, eau, oi)", "Aksentlər və ç hərfi"),
        frenchExerciseSet(
            "«Bonjour» nə deməkdir?", listOf("Salam", "Gecəniz xeyrə", "Bəli"), "Salam",
            "Bonjour gündüz salamlama ifadəsidir.",
            "«eau» hərf birləşməsi necə oxunur?", listOf("o kimi", "e-a-u kimi", "i kimi"), "o kimi",
            "'eau' fransız dilində qısa 'o' səsi verir (beaucoup → boku).",
            "Səhvi tap:", listOf("Paris sözündə s güclü oxunur", "ç hərfi s kimi oxunur", "oi [ua] kimi səslənir"), "Paris sözündə s güclü oxunur",
            "Söz sonundakı 's' adətən oxunmur: Paris [Pari].",
            "Cümləni düz: Salam, mənim adım Ayseldir", listOf("Bonjour ! Je m'appelle Aysel."), "Bonjour ! Je m'appelle Aysel.",
            "Özünü təqdim etmək: Je m'appelle ...",
            "Dinlə və yaz (fransızca 'təşəkkür'):", "merci", "'Təşəkkür edirəm' fransızca 'merci' yazılır."
        )
    ),
    Chapter(
        2, "Artikl və cins",
        "Fransız dilində isimlər kişi və qadın cinsində olur. Müəyyən artikllar: le (kişi), la (qadın), l' (sait/h qarşısında), les (cəm). Qeyri-müəyyən artikllar: un (kişi), une (qadın), des (cəm). Məsələn: le livre, la table, l'école.",
        listOf(
            ExampleSentence("Le livre est sur la table.", "Kitab masanın üstündədir.", "Lö livr e sür la tabl."),
            ExampleSentence("J'ai une belle maison.", "Mənim gözəl evim var.", "Je ün bel mezon."),
            ExampleSentence("L'école est près du parc.", "Məktəb parkın yaxınlığındadır.", "Lekol e pre dü park.")
        ),
        listOf("le / la / l' / les", "un / une / des", "Söz cinslərini tanımaq"),
        frenchExerciseSet(
            "«la fenêtre» tərcüməsi:", listOf("pəncərə", "masa", "ev"), "pəncərə",
            "la fenêtre qadın cinsindədir və 'pəncərə' deməkdir.",
            "___ hôtel est grand. (səssiz h qarşısında)", listOf("L'", "Le", "La"), "L'",
            "Sait və ya səssiz 'h' ilə başlayan tək isimlərdə 'l'' işlənir.",
            "Səhvi tap:", listOf("la livre", "une pomme", "les chats"), "la livre",
            "'livre' (kitab) kişi cinsindədir: le livre.",
            "Cümləni düz: Kitab masanın üstündədir", listOf("Le livre est sur la table."), "Le livre est sur la table.",
            "le livre (kişi) + la table (qadın).",
            "Dinlə və yaz:", "un livre", "Qeyri-müəyyən artikl ilə: un livre."
        )
    ),
    Chapter(
        3, "Être və Avoir",
        "Ən vacib iki feil: ÊTRE (olmaq) → je suis, tu es, il/elle est, nous sommes, vous êtes, ils/elles sont. AVOIR (malik olmaq) → j'ai, tu as, il/elle a, nous avons, vous avez, ils/elles ont. Yaş da 'avoir' ilə deyilir: J'ai vingt ans.",
        listOf(
            ExampleSentence("Je suis étudiant à Bakou.", "Mən Bakıda tələbəyəm.", "Jö süi etüdyan a Baku."),
            ExampleSentence("Nous avons deux frères.", "Bizim iki qardaşımız var.", "Nuzavon dö frer."),
            ExampleSentence("Vous êtes très gentil.", "Siz çox mehribansınız.", "Vuzēt tre janti.")
        ),
        listOf("Être təsrifi", "Avoir təsrifi", "Yaş və vəziyyət ifadələri"),
        frenchExerciseSet(
            "«J'ai un chat» nə deməkdir?", listOf("Mənim pişiyim var", "Mən pişiyəm", "Pişik yatır"), "Mənim pişiyim var",
            "J'ai = avoir feilinin I şəxs tək formasıdır.",
            "Nous ___ à la maison. (être)", listOf("sommes", "avons", "êtes"), "sommes",
            "être feilinin 'nous' forması 'sommes'-dur.",
            "Səhvi tap:", listOf("Je suis vingt ans.", "J'ai vingt ans.", "Ils sont heureux."), "Je suis vingt ans.",
            "Fransız dilində yaş 'avoir' ilə deyilir: J'ai vingt ans.",
            "Cümləni düz: Biz məktəbdəyik", listOf("Nous sommes à l'école."), "Nous sommes à l'école.",
            "nous sommes + à l'école.",
            "Dinlə və yaz:", "je suis prêt", "'Mən hazıram' = je suis prêt."
        )
    ),
    Chapter(
        4, "İndiki zaman",
        "-ER ilə bitən qaydalı feillər (parler, aimer, étudier) ən böyük qrupdur: je parle, tu parles, il parle, nous parlons, vous parlez, ils parlent. İnkar quruluşu feili əhatə edir: ne ... pas (Je ne parle pas).",
        listOf(
            ExampleSentence("Je parle un peu français.", "Mən bir az fransızca danışıram.", "Jö parl ön pö franse."),
            ExampleSentence("Tu aimes cette chanson ?", "Bu mahnını xoşlayırsan?", "Tü em set şanson?"),
            ExampleSentence("Ils n'habitent pas ici.", "Onlar burada yaşamırlar.", "İl nabit pa isi.")
        ),
        listOf("-ER feillərinin təsrifi", "Tez-tez işlənən feillər", "İnkar: ne ... pas"),
        frenchExerciseSet(
            "«Vous parlez français» tərcüməsi:", listOf("Siz fransızca danışırsınız", "Biz fransızca oxuyuruq", "Mən evdəyəm"), "Siz fransızca danışırsınız",
            "-ER qrupunda 'vous' şəxsi -ez sonluğu alır.",
            "Ils ___ la musique. (écouter)", listOf("écoutent", "écoutes", "écoutons"), "écoutent",
            "'ils/elles' şəxsində sonluq -ent olur (tələffüz olunmur).",
            "Səhvi tap:", listOf("Je parle pas français.", "Je ne parle pas français.", "Nous aimons lire."), "Je parle pas français.",
            "Standart inkarda 'ne' və 'pas' birlikdə işlənir.",
            "Cümləni düz: Mən fransızca danışıram", listOf("Je parle français."), "Je parle français.",
            "parler → je parle.",
            "Dinlə və yaz:", "elle travaille ici", "'O burada işləyir' = elle travaille ici."
        )
    ),
    Chapter(
        5, "Rəqəmlər və saat",
        "1-10: un, deux, trois, quatre, cinq, six, sept, huit, neuf, dix. Saat soruşmaq: 'Quelle heure est-il ?' Cavab: 'Il est huit heures' (Saat səkkizdir), 'Il est midi' (Günortadır).",
        listOf(
            ExampleSentence("Quelle heure est-il maintenant ?", "İndi saat neçədir?", "Kel ör e-til mentnan?"),
            ExampleSentence("Il est neuf heures du matin.", "Səhər saat doqquzdur.", "İl e nöf ör dü maten."),
            ExampleSentence("J'attends dix minutes.", "Mən on dəqiqə gözləyirəm.", "Jatan di minüt.")
        ),
        listOf("1-dən 100-ə rəqəmlər", "Saatı soruşmaq və demək", "Vaxt ifadələri"),
        frenchExerciseSet(
            "«huit» hansı rəqəmdir?", listOf("8", "7", "9"), "8",
            "sept = 7, huit = 8, neuf = 9.",
            "Il ___ trois heures.", listOf("est", "a", "sont"), "est",
            "Fransız dilində saat həmişə 'Il est ...' ilə deyilir.",
            "Səhvi tap:", listOf("Il a cinq heures.", "Il est cinq heures.", "Il est midi."), "Il a cinq heures.",
            "Saat bildirəndə 'avoir' deyil, 'être' (Il est) işlənir.",
            "Cümləni düz: Saat neçədir?", listOf("Quelle heure est-il ?"), "Quelle heure est-il ?",
            "Rəsmi saat sualı: Quelle heure est-il ?",
            "Dinlə və yaz:", "il est huit heures", "Saat 8 = il est huit heures."
        )
    ),
    Chapter(
        6, "Ailə və qohumlar",
        "Ailə: la mère (ana), le père (ata), le frère (qardaş), la sœur (bacı), les parents (valideynlər). Mənsubiyyət: mon/ma/mes (mənim), ton/ta/tes (sənin), son/sa/ses (onun), notre/nos (bizim). Saitlə başlayan qadın isimdə 'mon' işlənir (mon amie).",
        listOf(
            ExampleSentence("Ma famille habite à Gandja.", "Mənim ailəm Gəncədə yaşayır.", "Ma famiy abit a Gəncə."),
            ExampleSentence("Mon frère et ma sœur étudient.", "Qardaşım və bacım oxuyurlar.", "Mon frer e ma sör etüdi."),
            ExampleSentence("Mes parents travaillent ensemble.", "Valideynlərim birlikdə işləyirlər.", "Me paran travay ansambl.")
        ),
        listOf("Ailə sözləri", "Mon / Ma / Mes", "Sait qarşısında mənsubiyyət"),
        frenchExerciseSet(
            "«sœur» sözünün mənası:", listOf("bacı", "ana", "qardaş"), "bacı",
            "frère = qardaş, sœur = bacı.",
            "___ amis sont gentils. (mənim)", listOf("Mes", "Mon", "Ma"), "Mes",
            "Cəm isimlərlə 'mes' işlənir.",
            "Səhvi tap:", listOf("Ma amie parle.", "Mon amie parle.", "Ta maison est grande."), "Ma amie parle.",
            "amie saitlə başladığı üçün qadın cinsi olsa da 'Mon amie' işlənir.",
            "Cümləni düz: Mənim ailəm böyükdür", listOf("Ma famille est grande."), "Ma famille est grande.",
            "famille qadın cinsindədir: ma famille.",
            "Dinlə və yaz:", "mon père travaille", "'Atam işləyir' = mon père travaille."
        )
    ),
    Chapter(
        7, "Gündəlik həyat",
        "Qayıdış feilləri 'se' ilə işlənir: se lever (je me lève), se préparer, se coucher. Tezlik zərfləri: toujours (həmişə), souvent (tez-tez), parfois (bəzən), jamais (heç vaxt).",
        listOf(
            ExampleSentence("Je me lève à sept heures.", "Saat yeddidə oyanıram.", "Jö mö lev a set ör."),
            ExampleSentence("Je prends le petit-déjeuner vite.", "Səhər yeməyini tez yeyirəm.", "Jö pran lö pöti-dejöne vit."),
            ExampleSentence("Le soir, nous lisons ensemble.", "Axşamlar birlikdə oxuyuruq.", "Lö suar, nu lizon ansambl.")
        ),
        listOf("Qayıdış feilləri (se)", "Günlük fəaliyyətlər", "Zərflərin yeri"),
        frenchExerciseSet(
            "«le petit-déjeuner» nədir?", listOf("səhər yeməyi", "nahar", "şam yeməyi"), "səhər yeməyi",
            "petit-déjeuner = səhər yeməyi, déjeuner = nahar, dîner = şam yeməyi.",
            "Tu ___ lèves tôt.", listOf("te", "me", "se"), "te",
            "'tu' şəxsi ilə 'te' qayıdış əvəzliyi işlənir.",
            "Səhvi tap:", listOf("Je se lève tôt.", "Elle se repose.", "Nous nous levons."), "Je se lève tôt.",
            "'je' ilə 'se' yox, 'me' işlənir: Je me lève.",
            "Cümləni düz: Hər gün fransızca öyrənirəm", listOf("J'étudie le français chaque jour."), "J'étudie le français chaque jour.",
            "chaque jour = hər gün.",
            "Dinlə və yaz:", "je lis toujours", "'Mən həmişə oxuyuram' = je lis toujours."
        )
    ),
    Chapter(
        8, "Passé Composé · Keçmiş zaman",
        "Passé Composé keçmişdə bitmiş hadisələri bildirir: köməkçi feil (avoir və ya être) + feili sifət (participe passé). -ER → -é (parlé), -IR → -i (fini), lire → lu, faire → fait. Hərəkət feilləri (aller, venir, partir) 'être' alır.",
        listOf(
            ExampleSentence("Hier, j'ai lu un roman.", "Dünən bir roman oxudum.", "Yer, je lü ön roman."),
            ExampleSentence("Nous avons mangé au café.", "Biz kafedə yemək yedik.", "Nuzavon manje o kafe."),
            ExampleSentence("Elle est partie ce matin.", "O bu səhər yola düşdü.", "El e parti sö maten.")
        ),
        listOf("Avoir ilə Passé Composé", "Être alan hərəkət feilləri", "Participe passé formaları"),
        frenchExerciseSet(
            "«J'ai fini la leçon» tərcüməsi:", listOf("Dərsi bitirdim", "Dərsə başlayıram", "Dərsi oxuyacağam"), "Dərsi bitirdim",
            "j'ai fini 'finir' feilinin Passé Composé formasıdır.",
            "Hier, tu ___ parlé avec lui.", listOf("as", "es", "a"), "as",
            "parler feili 'avoir' köməkçi feili ilə qurulur: tu as parlé.",
            "Səhvi tap:", listOf("J'ai allé à Paris.", "Je suis allé à Paris.", "Il a écrit une lettre."), "J'ai allé à Paris.",
            "aller hərəkət feili olduğu üçün 'être' ilə işlənir: Je suis allé.",
            "Cümləni düz: Dünən bir kitab aldım", listOf("Hier, j'ai acheté un livre."), "Hier, j'ai acheté un livre.",
            "acheter → j'ai acheté.",
            "Dinlə və yaz:", "j'ai lu ce livre", "Keçmiş zaman: j'ai lu ce livre."
        )
    ),
    Chapter(
        9, "Gələcək zaman",
        "Yaxın gələcək (Futur Proche) 'aller + məsdər' ilə qurulur: Je vais étudier (Öyrənəcəyəm). Sadə gələcək (Futur Simple) məsdərə -ai, -as, -a, -ons, -ez, -ont artırmaqla yaranır: Je parlerai, Nous voyagerons.",
        listOf(
            ExampleSentence("Demain, je vais visiter le musée.", "Sabah muzeyə gedəcəyəm.", "Dömen, jö ve vizite lö müze."),
            ExampleSentence("Nous parlerons français bientôt.", "Tezliklə fransızca danışacağıq.", "Nu parlrön franse byento."),
            ExampleSentence("Ils vont partir à midi.", "Onlar günorta yola düşəcəklər.", "İl von partir a midi.")
        ),
        listOf("Futur Proche (aller + məsdər)", "Futur Simple sonluqları", "Gələcək zaman sözləri"),
        frenchExerciseSet(
            "«Je vais voyager» mənası:", listOf("Səyahət edəcəyəm", "Səyahət etdim", "Səyahət edirəm"), "Səyahət edəcəyəm",
            "aller + məsdər yaxın gələcək planını bildirir.",
            "Demain, nous ___ un film. (regarder)", listOf("allons regarder", "avons regardé", "regardiez"), "allons regarder",
            "nous ilə 'aller' feili 'allons' olur.",
            "Səhvi tap:", listOf("Je vais à manger.", "Je vais manger.", "Tu vas réussir."), "Je vais à manger.",
            "Fransız dilində 'aller + məsdər' arasında 'à' işlənmir.",
            "Cümləni düz: Sabah kitab oxuyacağam", listOf("Demain, je vais lire."), "Demain, je vais lire.",
            "Futur proche: je vais lire.",
            "Dinlə və yaz:", "nous allons partir", "'Biz yola düşəcəyik' = nous allons partir."
        )
    ),
    Chapter(
        10, "Sifətlər",
        "Sifətlər isimlə cinsə və saya görə uzlaşır: un livre intéressant, une histoire intéressante, des livres intéressants. Qadın cinsində adətən -e artırılır. Bəzi qısa sifətlər isimdən əvvəl gəlir: petit, grand, bon, beau, nouveau.",
        listOf(
            ExampleSentence("C'est une grande maison blanche.", "Bu böyük ağ evdir.", "Se ün grand mezon blanş."),
            ExampleSentence("J'ai un nouveau dictionnaire.", "Mənim yeni lüğətim var.", "Je ön nuvo diksyoner."),
            ExampleSentence("Les fleurs rouges sont belles.", "Qırmızı güllər gözəldir.", "Le flör ruj son bel.")
        ),
        listOf("Cins və say uzlaşması", "İsimdən əvvəl gələn sifətlər", "Qaydasız sifətlər (beau/nouveau)"),
        frenchExerciseSet(
            "«une voiture ___» (qara)", listOf("noire", "noir", "noirs"), "noire",
            "voiture qadın cinsində olduğu üçün sifət 'noire' olur.",
            "«un ___ jardin» (gözəl)", listOf("beau", "belle", "belles"), "beau",
            "jardin kişi cinsindədir: un beau jardin.",
            "Səhvi tap:", listOf("une maison grand", "une grande maison", "un livre bleu"), "une maison grand",
            "grand qadın cinsində 'grande' olur və isimdən əvvəl işlənir.",
            "Cümləni düz: Bu maraqlı kitabdır", listOf("C'est un livre intéressant."), "C'est un livre intéressant.",
            "Rəng və uzun sifətlər isimdən sonra gəlir.",
            "Dinlə və yaz:", "une petite table", "'Kiçik masa' = une petite table."
        )
    ),
    Chapter(
        11, "Əvəzliklər",
        "Şəxs əvəzlikləri: je, tu, il, elle, on, nous, vous, ils, elles. Tamamlıq əvəzlikləri: me, te, le/la/l', nous, vous, les (Je le vois = Onu görürəm). Yönlük: lui, leur (Je lui parle = Onunla danışıram). 'On' gündəlik dildə 'biz' mənasında çox işlənir.",
        listOf(
            ExampleSentence("Je la rencontre chaque matin.", "Onunla hər səhər qarşılaşıram.", "Jö la rankontr şak maten."),
            ExampleSentence("Tu lui écris un message.", "Sən ona mesaj yazırsan.", "Tü lüi ekri ön mesaj."),
            ExampleSentence("On va au cinéma ce soir.", "Bu axşam kinoya gedirik.", "On va o sinema sö suar.")
        ),
        listOf("Le / La / Les əvəzlikləri", "Lui və Leur", "On əvəzliyinin işlənməsi"),
        frenchExerciseSet(
            "«Je le connais» nə deməkdir?", listOf("Mən onu tanıyıram", "Mən ona deyirəm", "O məni görür"), "Mən onu tanıyıram",
            "'le' tək kişi cinsli birbaşa tamamlıqdır.",
            "Gündəlik dildə 'biz' mənasında işlənən əvəzlik:", listOf("on", "vous", "ils"), "on",
            "Danışıq fransızcasında 'on' III şəxs tək feili ilə 'biz' bildirir.",
            "Səhvi tap:", listOf("Je vois le.", "Je le vois.", "Elle nous écoute."), "Je vois le.",
            "Tamamlıq əvəzliyi təsriflənmiş feildən əvvəl gəlir: Je le vois.",
            "Cümləni düz: Mən ona kömək edirəm", listOf("Je l'aide."), "Je l'aide.",
            "aider feilindən əvvəl le/la qısalaraq l' olur.",
            "Dinlə və yaz:", "je vous écoute", "'Sizi dinləyirəm' = je vous écoute."
        )
    ),
    Chapter(
        12, "Sual sözləri",
        "Sual qurmaq üçün 'Est-ce que...' işlənir və ya intonasiya qalxır. Sual sözləri: Qui ? (Kim?), Que / Qu'est-ce que ? (Nə?), Où ? (Harada?), Quand ? (Nə vaxt?), Comment ? (Necə?), Pourquoi ? (Niyə?), Combien ? (Neçə?).",
        listOf(
            ExampleSentence("Où est la gare centrale ?", "Mərkəzi vağzal haradadır?", "U e la gar santral?"),
            ExampleSentence("Combien coûte ce billet ?", "Bu bilet neçəyədir?", "Konbyen kut sö biye?"),
            ExampleSentence("Pourquoi apprenez-vous le français ?", "Niyə fransız dilini öyrənirsiniz?", "Purkua aprene-vu lö franse?")
        ),
        listOf("Est-ce que sual forması", "Sual sözləri", "İnversiya ilə nəzakətli sual"),
        frenchExerciseSet(
            "«Où ?» hansı sual sözüdür?", listOf("Harada?", "Niyə?", "Kim?"), "Harada?",
            "Où = hara/harada; Quand = nə vaxt; Pourquoi = niyə.",
            "___ coûte cette tasse ?", listOf("Combien", "Qui", "Quand"), "Combien",
            "Qiymət və miqdar soruşanda 'Combien' işlənir.",
            "Səhvi tap:", listOf("Où tu veux combien ?", "Où habitez-vous ?", "Comment allez-vous ?"), "Où tu veux combien ?",
            "İki sual sözü eyni sualda belə birləşdirilmir.",
            "Cümləni düz: Vağzal haradadır?", listOf("Où est la gare ?"), "Où est la gare ?",
            "Où + est + la gare.",
            "Dinlə və yaz:", "comment allez-vous", "Nəzakətli 'Necəsiniz?' sualı."
        )
    ),
    Chapter(
        13, "Kafe və sifariş",
        "Kafedə nəzakətli ifadələr: 'Je voudrais...' (İstərdim), 'Un café, s'il vous plaît' (Zəhmət olmasa bir qəhvə), 'L'addition, s'il vous plaît' (Hesabı gətirin). Hissə artiklları (du/de la/de l'): Je prends du thé (Çay içirəm).",
        listOf(
            ExampleSentence("Je voudrais une tasse de thé chaud.", "Bir fincan isti çay istərdim.", "Jö vudre ün tas dö te şo."),
            ExampleSentence("Je prends du pain et du fromage.", "Mən çörək və pendir götürürəm.", "Jö pran dü pen e dü fromaj."),
            ExampleSentence("L'addition, s'il vous plaît.", "Zəhmət olmasa, hesabı verin.", "Ladisyon, sil vu ple.")
        ),
        listOf("Je voudrais... nəzakəti", "Hissə artiklları (du/de la)", "Hesab və menyu sözləri"),
        frenchExerciseSet(
            "«L'addition, s'il vous plaît» nə deməkdir?", listOf("Hesabı gətirin, zəhmət olmasa", "Menyu verin", "Su istəyirəm"), "Hesabı gətirin, zəhmət olmasa",
            "l'addition = hesab.",
            "Je bois ___ lait. (kişi cinsi hissə artiklı)", listOf("du", "de la", "des"), "du",
            "Süd (le lait) kişi cinsindədir: du lait.",
            "Səhvi tap:", listOf("Je voudrais de la pain.", "Je voudrais du pain.", "Une tasse de café."), "Je voudrais de la pain.",
            "pain kişi cinsindədir, düzgün hissə artiklı 'du pain'-dir.",
            "Cümləni düz: Bir fincan qəhvə istərdim", listOf("Je voudrais un café."), "Je voudrais un café.",
            "'Je voudrais...' kafedə ən nəzakətli sifarişdir.",
            "Dinlə və yaz:", "merci beaucoup", "'Çox sağ olun' = merci beaucoup."
        )
    ),
    Chapter(
        14, "Səyahət və istiqamət",
        "Nəqliyyat və yol: l'aéroport, le train, le billet, le passeport, l'hôtel, la valise. İstiqamət: à droite (sağa), à gauche (sola), tout droit (düz irəli). 'Où se trouve...' (... harada yerləşir?).",
        listOf(
            ExampleSentence("Allez tout droit puis tournez à gauche.", "Düz gedin, sonra sola dönün.", "Ale tu drua püi turne a goş."),
            ExampleSentence("Le train pour Lyon part à neuf heures.", "Liona gedən qatar saat doqquzda yola düşür.", "Lö tren pur Liyon par a nöf ör."),
            ExampleSentence("Voici mon passeport et ma carte.", "Buyurun, pasportum və xəritəm.", "Vuasi mon paspor e ma kart.")
        ),
        listOf("Vağzal və aeroport", "Yol soruşmaq", "İstiqamət vermək"),
        frenchExerciseSet(
            "«à droite» hansı tərəfdir?", listOf("sağa", "sola", "arxaya"), "sağa",
            "à droite = sağa, à gauche = sola, tout droit = düz.",
            "J'achète un ___ pour le train.", listOf("billet", "verre", "sucre"), "billet",
            "Qatar üçün 'billet' (bilet) alınır.",
            "Səhvi tap:", listOf("Nous allons par le gare.", "Nous allons à la gare.", "L'hôtel est au centre."), "Nous allons par le gare.",
            "'gare' qadın cinsindədir və 'à la gare' işlənir.",
            "Cümləni düz: Otel mərkəzdə yerləşir", listOf("L'hôtel est au centre."), "L'hôtel est au centre.",
            "au = à + le (le centre → au centre).",
            "Dinlə və yaz:", "tout droit", "'Düz irəli' = tout droit."
        )
    ),
    Chapter(
        15, "B1 Ümumi təkrar",
        "B1 səviyyəsində mürəkkəb cümlə bağlayıcıları (parce que, mais, bien que, quand, si) və nəzakətli şərt forması (conditionnel: je voudrais, j'aimerais, il faudrait) geniş işlənir.",
        listOf(
            ExampleSentence("J'étudie le français parce que j'aime voyager.", "Fransız dilini öyrənirəm, çünki səyahət etməyi sevirəm.", "Jetüdi lö franse pars kö jem vuayaje."),
            ExampleSentence("S'il fait beau demain, nous irons au parc.", "Sabah hava yaxşı olsa, parka gedəcəyik.", "Sil fe bo dömen, nuziron o park."),
            ExampleSentence("J'aimerais parler couramment cette langue.", "Bu dildə sərbəst danışmaq istərdim.", "Jemre parle kuraman set lang.")
        ),
        listOf("Səbəb və nəticə bağlayıcıları", "Şərt cümlələri (Si)", "B1 yekun yoxlama"),
        frenchExerciseSet(
            "«parce que» bağlayıcısının mənası:", listOf("çünki", "amma", "əgər"), "çünki",
            "parce que səbəb bildirir; mais = amma; si = əgər.",
            "S'il ___ temps, il lira plus. (avoir)", listOf("a", "aura", "avait eu"), "a",
            "Real şərt cümləsində 'si + indiki zaman', nəticədə gələcək zaman işlənir.",
            "Səhvi tap:", listOf("Si j'aurai le temps, je viendrai.", "Si j'ai le temps, je viendrai.", "Il faut étudier."), "Si j'aurai le temps, je viendrai.",
            "'si' budaq cümləsindən sonra gələcək (futur) deyil, indiki zaman işlənir.",
            "Cümləni düz: Hər gün məşq etmək lazımdır", listOf("Il faut pratiquer chaque jour."), "Il faut pratiquer chaque jour.",
            "'Il faut + məsdər' vaciblik bildirir.",
            "Dinlə və yaz:", "parce que j'aime", "Səbəb ifadəsi: parce que j'aime."
        )
    )
)
