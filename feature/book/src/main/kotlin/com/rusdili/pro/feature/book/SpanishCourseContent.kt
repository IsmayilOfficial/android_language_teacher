package com.rusdili.pro.feature.book

private val spanishExerciseTypes = listOf(
    "Tərcüməni seç",
    "Düzgün formanı seç",
    "Səhvi tap",
    "Cümləni qur",
    "Dinlə və yaz"
)

private fun spanishExerciseSet(
    q1: String, o1: List<String>, a1: String, e1: String,
    q2: String, o2: List<String>, a2: String, e2: String,
    q3: String, o3: List<String>, a3: String, e3: String,
    q4: String, o4: List<String>, a4: String, e4: String,
    q5: String, a5: String, e5: String
) = listOf(
    LessonExercise(spanishExerciseTypes[0], q1, o1, a1, e1),
    LessonExercise(spanishExerciseTypes[1], q2, o2, a2, e2),
    LessonExercise(spanishExerciseTypes[2], q3, o3, a3, e3),
    LessonExercise(spanishExerciseTypes[3], q4, o4, a4, e4),
    LessonExercise(spanishExerciseTypes[4], q5, listOf(a5), a5, e5)
)

val spanishChapters: List<Chapter> = listOf(
    Chapter(
        1, "Əlifba və tələffüz",
        "İspan əlifbasında 27 hərf var. H hərfi həmişə səssizdir (hola → ola). Ñ hərfi 'ny' kimi oxunur (España → Espanya). C və G hərfləri e/i saitlərindən əvvəl yumşalır; J isə Azərbaycan 'x' səsinə yaxın tələffüz olunur (trabajo).",
        listOf(
            ExampleSentence("¡Hola! Me llamo Leyla.", "Salam! Mənim adım Leyladır.", "Ola! Me yamo Leyla."),
            ExampleSentence("España es un país bonito.", "İspaniya gözəl ölkədir.", "Espanya es un pais bonito."),
            ExampleSentence("Yo trabajo mucho.", "Mən çox işləyirəm.", "Yo trabaxo muço.")
        ),
        listOf("Saitlər və H hərfi", "Ñ, LL və RR səsləri", "Sözləri düzgün oxu"),
        spanishExerciseSet(
            "«¡Hola!» sözü nə deməkdir?", listOf("Salam", "Sağ ol", "Bəli"), "Salam",
            "İspan dilində 'hola' salam deməkdir və başdakı 'h' oxunmur.",
            "«España» sözündə «ñ» səsi necə oxunur?", listOf("ny kimi", "ş kimi", "n kimi"), "ny kimi",
            "Ñ hərfi yumşaq 'ny' səsi verir.",
            "Səhvi tap: hansı fikir yanlışdır?", listOf("H hərfi güclü səslənir", "H hərfi səssizdir", "5 təmiz sait var"), "H hərfi güclü səslənir",
            "İspan dilində H hərfi tələffüz olunmur.",
            "Cümləni düz: Salam, mənim adım Leyladır", listOf("¡Hola! Me llamo Leyla."), "¡Hola! Me llamo Leyla.",
            "Salamlaşmaq üçün '¡Hola! Me llamo ...' işlənir.",
            "Dinlə və yaz (ispanca 'salam'):", "hola", "İspan dilində salam 'hola' yazılır."
        )
    ),
    Chapter(
        2, "Artikl və cins",
        "İspan dilində isimlər kişi və qadın cinsində olur. -o ilə bitən isimlər əsasən kişi (el libro), -a ilə bitənlər qadın cinsidir (la mesa). Müəyyən artikllar: el / la / los / las. Qeyri-müəyyən artikllar: un / una / unos / unas. İstisnalar: el día, la mano.",
        listOf(
            ExampleSentence("El libro está en la mesa.", "Kitab masanın üstündədir.", "El libro esta en la mesa."),
            ExampleSentence("Tengo una casa blanca.", "Mənim ağ evim var.", "Tengo una kasa blanka."),
            ExampleSentence("Las ventanas son grandes.", "Pəncərələr böyükdür.", "Las ventanas son grandes.")
        ),
        listOf("Müəyyən artikllar", "Qadın və kişi cinsi", "Cəm formalar və istisnalar"),
        spanishExerciseSet(
            "«la ventana» ifadəsinin tərcüməsi:", listOf("pəncərə", "masa", "ev"), "pəncərə",
            "Ventana 'pəncərə' deməkdir və -a ilə bitdiyi üçün 'la' artiklı alır.",
            "___ día (gün)", listOf("el", "la", "las"), "el",
            "día sözü -a ilə bitsə də istisna olaraq kişi cinsindədir: el día.",
            "Səhvi tap:", listOf("la libro", "el coche", "una casa"), "la libro",
            "libro kişi cinsindədir, düzgün forma 'el libro' olmalıdır.",
            "Cümləni düz: Kitab masanın üstündədir", listOf("El libro está en la mesa."), "El libro está en la mesa.",
            "el libro (kişi) + la mesa (qadın).",
            "Dinlə və yaz (ispanca 'kitab' artiklla):", "el libro", "'kitab' ispanca 'el libro' kimi işlənir."
        )
    ),
    Chapter(
        3, "Ser və Estar",
        "Hər iki feil 'olmaq' deməkdir, amma fərqli işlənir. SER daimi xüsusiyyət, peşə, mənşə və saat üçün işlənir (Soy estudiante). ESTAR isə yer, müvəqqəti hal və əhval üçün işlənir (Estoy en casa, El café está caliente).",
        listOf(
            ExampleSentence("Yo soy estudiante de medicina.", "Mən tibb tələbəsiyəm.", "Yo soy estudiante de medisina."),
            ExampleSentence("Ahora estoy en la escuela.", "İndi mən məktəbdəyəm.", "Aora estoy en la eskuela."),
            ExampleSentence("El té está muy caliente.", "Çay çox istidir.", "El te esta muy kaliente.")
        ),
        listOf("SER: kimlik və peşə", "ESTAR: yer və hal", "SER vs ESTAR fərqləri"),
        spanishExerciseSet(
            "«Estoy aquí» cümləsinin mənası:", listOf("Mən buradayam", "Mən tələbəyəm", "Mən gedirəm"), "Mən buradayam",
            "Yer bildirdiyi üçün 'estar' işlənir: estoy aquí.",
            "Yo ___ profesor. (peşə)", listOf("soy", "estoy", "es"), "soy",
            "Peşə və daimi kimlik üçün 'ser' işlənir: Yo soy profesor.",
            "Səhvi tap:", listOf("Estoy médico en Bakú.", "Estoy cansado hoy.", "Madrid está en España."), "Estoy médico en Bakú.",
            "Peşə bildirəndə 'estar' yox, 'soy médico' işlənir.",
            "Cümləni düz: İndi mən evdəyəm", listOf("Ahora estoy en casa."), "Ahora estoy en casa.",
            "Yer bildirən cümlələrdə 'estar' işlənir.",
            "Dinlə və yaz:", "estoy en casa", "Evdə olmaq yer bildirdiyi üçün 'estoy en casa' deyilir."
        )
    ),
    Chapter(
        4, "İndiki zaman",
        "Qaydalı feillər üç qrupa bölünür: -AR (hablar → hablo, hablas, habla, hablamos, habláis, hablan), -ER (leer → leo, lees, lee, leemos, leéis, leen), -IR (vivir → vivo, vives, vive, vivimos, vivís, viven).",
        listOf(
            ExampleSentence("Yo hablo español todos los días.", "Mən hər gün ispanca danışıram.", "Yo ablo espanyol todos los dias."),
            ExampleSentence("Ella lee un libro interesante.", "O maraqlı kitab oxuyur.", "Eya lee un libro interesante."),
            ExampleSentence("Nosotros vivimos en Bakú.", "Biz Bakıda yaşayırıq.", "Nosotros bivimos en Baku.")
        ),
        listOf("-AR qrupu feilləri", "-ER və -IR qrupları", "Qaydasız tez-tez işlənən feillər"),
        spanishExerciseSet(
            "«Nosotros leemos» nə deməkdir?", listOf("Biz oxuyuruq", "Onlar oxuyurlar", "Mən oxuyuram"), "Biz oxuyuruq",
            "leer feilinin 'nosotros' forması 'leemos'-dur.",
            "Tú ___ agua. (beber)", listOf("bebes", "bebo", "beben"), "bebes",
            "-ER qrupunda 'tú' şəxsi -es sonluğu alır: bebes.",
            "Səhvi tap:", listOf("Yo hablas español.", "Ella vive aquí.", "Ellos comen pan."), "Yo hablas español.",
            "'Yo' şəxsi ilə -o sonluğu işlənir: Yo hablo español.",
            "Cümləni düz: Mən ispanca danışıram", listOf("Yo hablo español."), "Yo hablo español.",
            "hablar → Yo hablo.",
            "Dinlə və yaz:", "yo leo un libro", "İndiki zamanda: yo leo un libro."
        )
    ),
    Chapter(
        5, "Rəqəmlər və saat",
        "1-10: uno, dos, tres, cuatro, cinco, seis, siete, ocho, nueve, diez. Saat soruşmaq: ¿Qué hora es? Saat 1 üçün 'Es la una', qalan saatlar üçün 'Son las dos / tres / ocho' işlənir.",
        listOf(
            ExampleSentence("¿Qué hora es ahora?", "İndi saat neçədir?", "Ke ora es aora?"),
            ExampleSentence("Son las ocho de la mañana.", "Səhər saat səkkizdir.", "Son las oço de la manyana."),
            ExampleSentence("Quiero dos cafés, por favor.", "Zəhmət olmasa iki qəhvə verin.", "Kiero dos kafes, por fabor.")
        ),
        listOf("1-dən 100-ə rəqəmlər", "Saatı demək", "Gün vaxtları"),
        spanishExerciseSet(
            "«siete» hansı rəqəmdir?", listOf("7", "6", "8"), "7",
            "siete = 7, seis = 6, ocho = 8.",
            "Saat 5-dir: ___ las cinco.", listOf("Son", "Es", "Están"), "Son",
            "Saat 2-dən 12-yə qədər 'Son las ...' işlənir.",
            "Səhvi tap:", listOf("Son la una.", "Es la una.", "Son las cuatro."), "Son la una.",
            "Saat 1 tək olduğu üçün 'Es la una' olmalıdır.",
            "Cümləni düz: İndi saat neçədir?", listOf("¿Qué hora es?"), "¿Qué hora es?",
            "Saat soruşmaq üçün '¿Qué hora es?' deyilir.",
            "Dinlə və yaz (saat üçdür):", "son las tres", "Saat 3 üçün 'son las tres' deyilir."
        )
    ),
    Chapter(
        6, "Ailə və qohumlar",
        "Ailə üzvləri: la madre (ana), el padre (ata), el hermano (qardaş), la hermana (bacı), los padres (valideynlər). Mənsubiyyət sifətləri: mi/mis (mənim), tu/tus (sənin), su/sus (onun), nuestro/a (bizim).",
        listOf(
            ExampleSentence("Mi familia es muy grande.", "Mənim ailəm çox böyükdür.", "Mi familia es muy grande."),
            ExampleSentence("Mis padres viven cerca.", "Valideynlərim yaxında yaşayırlar.", "Mis padres biven serka."),
            ExampleSentence("Tengo dos hermanos y una hermana.", "İki qardaşım və bir bacım var.", "Tengo dos ermanos i una ermana.")
        ),
        listOf("Ailə üzvləri", "Mənsubiyyət sifətləri", "Tener feili ilə danışmaq"),
        spanishExerciseSet(
            "«hermana» sözünün tərcüməsi:", listOf("bacı", "ana", "qardaş"), "bacı",
            "hermano = qardaş, hermana = bacı.",
            "___ amigos están aquí. (mənim, cəm)", listOf("Mis", "Mi", "Tu"), "Mis",
            "Cəm isimlərlə 'mi' forması 'mis' olur.",
            "Səhvi tap:", listOf("Mi hermanos trabajan.", "Mi madre lee.", "Sus hijos juegan."), "Mi hermanos trabajan.",
            "hermanos cəmdə olduğu üçün 'Mis hermanos' işlənməlidir.",
            "Cümləni düz: Mənim ailəm böyükdür", listOf("Mi familia es grande."), "Mi familia es grande.",
            "Daimi xüsusiyyət üçün 'es' işlənir.",
            "Dinlə və yaz:", "tengo dos hermanos", "'İki qardaşım var' = tengo dos hermanos."
        )
    ),
    Chapter(
        7, "Gündəlik həyat",
        "Gündəlik işlər üçün qayıdış feilləri işlənir: levantarse (me levanto), ducharse, acostarse. Zaman zərfləri: por la mañana (səhər), todos los días (hər gün), siempre (həmişə), nunca (heç vaxt).",
        listOf(
            ExampleSentence("Me levanto a las siete cada día.", "Hər gün saat yeddidə oyanıram.", "Me lebanto a las siete kada dia."),
            ExampleSentence("Desayuno pan con leche.", "Səhər yeməyində südlə çörək yeyirəm.", "Desayuno pan kon leçe."),
            ExampleSentence("Por la noche leo un libro.", "Axşamlar kitab oxuyuram.", "Por la noçe leo un libro.")
        ),
        listOf("Qayıdış feilləri", "Gündəlik rejim sözləri", "Tezlik zərfləri"),
        spanishExerciseSet(
            "«desayuno» nə deməkdir?", listOf("səhər yeməyi", "şam yeməyi", "iş"), "səhər yeməyi",
            "desayuno = səhər yeməyi, almuerzo = nahar, cena = şam yeməyi.",
            "Yo ___ levanto temprano.", listOf("me", "te", "se"), "me",
            "Qayıdış feilində 'yo' ilə 'me' əvəzliyi işlənir.",
            "Səhvi tap:", listOf("Nosotros me levantamos.", "Ella se ducha.", "Siempre estudio."), "Nosotros me levantamos.",
            "'Nosotros' ilə 'nos' işlənməlidir: Nosotros nos levantamos.",
            "Cümləni düz: Hər gün ispanca öyrənirəm", listOf("Estudio español todos los días."), "Estudio español todos los días.",
            "Zaman ifadəsi cümlənin əvvəlində və ya sonunda gəlir.",
            "Dinlə və yaz:", "siempre practico", "'Həmişə məşq edirəm' = siempre practico."
        )
    ),
    Chapter(
        8, "Keçmiş zaman",
        "Tamamlanmış keçmiş hərəkətlər üçün Pretérito Indefinido işlənir: -AR → -é, -aste, -ó, -amos, -aron (hablé); -ER/-IR → -í, -iste, -ió, -imos, -ieron (comí, viví). İşarə sözləri: ayer (dünən), la semana pasada.",
        listOf(
            ExampleSentence("Ayer leí un libro nuevo.", "Dünən yeni bir kitab oxudum.", "Ayer lei un libro nuebo."),
            ExampleSentence("Mi amigo llegó a las cinco.", "Dostum saat beşdə çatdı.", "Mi amigo yego a las sinko."),
            ExampleSentence("Ellos viajaron a España el año pasado.", "Onlar keçən il İspaniyaya səyahət etdilər.", "Eyos biajaron a Espanya el anyo pasado.")
        ),
        listOf("-AR keçmiş zaman", "-ER/-IR keçmiş zaman", "Tez-tez işlənən qaydasızlar"),
        spanishExerciseSet(
            "«Ayer compré pan» tərcüməsi:", listOf("Dünən çörək aldım", "Sabah çörək alacağam", "İndi çörək yeyirəm"), "Dünən çörək aldım",
            "compré 'comprar' feilinin I şəxs tək keçmiş zaman formasıdır.",
            "Ayer tú ___ en casa. (comer)", listOf("comiste", "comí", "comes"), "comiste",
            "-ER feilinin 'tú' keçmiş zaman sonluğu -iste-dir: comiste.",
            "Səhvi tap:", listOf("Ayer yo hablo con ella.", "Ayer hablé con ella.", "Comimos arroz."), "Ayer yo hablo con ella.",
            "'Ayer' (dünən) keçmiş zaman tələb edir: hablé.",
            "Cümləni düz: Dünən kitab oxudum", listOf("Ayer leí un libro."), "Ayer leí un libro.",
            "leer feilinin I şəxs keçmiş zamanı 'leí' olur.",
            "Dinlə və yaz:", "ayer viajé mucho", "Keçmiş zaman cümləsi: ayer viajé mucho."
        )
    ),
    Chapter(
        9, "Gələcək zaman",
        "Yaxın planlar üçün 'ir + a + məsdər' işlənir: Voy a estudiar (öyrənəcəyəm). Sadə gələcək zaman məsdərə -é, -ás, -á, -emos, -éis, -án artırmaqla qurulur: hablaré, comerás, viajarán.",
        listOf(
            ExampleSentence("Mañana voy a estudiar mucho.", "Sabah çox oxuyacağam.", "Manyana boy a estudiar muço."),
            ExampleSentence("El próximo mes viajaremos a Madrid.", "Gələn ay Madridə səyahət edəcəyik.", "El proksimo mes biajaremos a Madrid."),
            ExampleSentence("Te ayudaré con la tarea.", "Tapşırıqda sənə kömək edəcəyəm.", "Te ayudare kon la tarea.")
        ),
        listOf("Yaxın gələcək: ir + a", "Sadə gələcək sonluqları", "Gələcək zaman sözləri"),
        spanishExerciseSet(
            "«Voy a comprar agua» nə deməkdir?", listOf("Su alacağam", "Su içdim", "Su alıram"), "Su alacağam",
            "ir + a + məsdər yaxın gələcək zaman bildirir.",
            "Mañana nosotros ___ temprano. (salir)", listOf("vamos a salir", "salimos ayer", "fui a salir"), "vamos a salir",
            "Nosotros ilə 'ir' feilinin forması 'vamos' olur.",
            "Səhvi tap:", listOf("Voy estudiar español.", "Voy a estudiar español.", "Hablaré mañana."), "Voy estudiar español.",
            "'ir' feilindən sonra 'a' bağlayıcısı mütləqdir: Voy a estudiar.",
            "Cümləni düz: Sabah səyahət edəcəyəm", listOf("Mañana voy a viajar."), "Mañana voy a viajar.",
            "ir + a + viajar = voy a viajar.",
            "Dinlə və yaz:", "voy a leer", "Gələcək plan: voy a leer."
        )
    ),
    Chapter(
        10, "Sifətlər",
        "İspan dilində sifət adətən isimdən sonra gəlir və cinsə/saya görə uzlaşır: el coche rojo (kişi tək), la casa roja (qadın tək), los coches rojos (kişi cəm), las casas rojas (qadın cəm). -e və ya samitlə bitənlər cinsə görə dəyişmir (grande, azul).",
        listOf(
            ExampleSentence("Tengo una bicicleta roja.", "Mənim qırmızı velosipedim var.", "Tengo una bisikleta roja."),
            ExampleSentence("Es una ciudad muy grande.", "Bu çox böyük şəhərdir.", "Es una siudad muy grande."),
            ExampleSentence("Los libros nuevos están aquí.", "Yeni kitablar buradadır.", "Los libros nuebos estan aki.")
        ),
        listOf("Cinsə görə uzlaşma", "Cəm sifətlər", "Dəyişməyən -e/samit sifətlər"),
        spanishExerciseSet(
            "«una flor ___» (qırmızı)", listOf("roja", "rojo", "rojos"), "roja",
            "flor qadın cinsindədir, ona görə sifət 'roja' olur.",
            "«los zapatos ___» (qara)", listOf("negros", "negro", "negras"), "negros",
            "los zapatos kişi cəmidir: negros.",
            "Səhvi tap:", listOf("una casa blanco", "una casa blanca", "un coche azul"), "una casa blanco",
            "casa qadın cinsindədir, düzgün forma 'blanca' olmalıdır.",
            "Cümləni düz: Bu yeni kitabdır", listOf("Es un libro nuevo."), "Es un libro nuevo.",
            "Sifət isimdən sonra gəlir: libro nuevo.",
            "Dinlə və yaz:", "la mesa grande", "'grande' sifəti cinsə görə dəyişmir."
        )
    ),
    Chapter(
        11, "Əvəzliklər",
        "Şəxs əvəzlikləri: yo, tú, él/ella/usted, nosotros, vosotros, ellos/ellas/ustedes. Vasitəsiz tamamlıq əvəzlikləri: me, te, lo/la, nos, os, los/las (Lo veo = Onu görürəm). Yönlük: me, te, le, nos, os, les (Me gusta = Xoşuma gəlir).",
        listOf(
            ExampleSentence("Ellos nos ayudan siempre.", "Onlar bizə həmişə kömək edirlər.", "Eyos nos ayudan siempre."),
            ExampleSentence("Me gusta mucho el español.", "İspan dili mənim çox xoşuma gəlir.", "Me gusta muço el espanyol."),
            ExampleSentence("¿Puedes darme el mapa?", "Mənə xəritəni verə bilərsən?", "Puedes darme el mapa?")
        ),
        listOf("Şəxs əvəzlikləri", "Lo/La və Le tamamlıqları", "Gustar konstruksiyası"),
        spanishExerciseSet(
            "«Me gusta el té» tərcüməsi:", listOf("Çay xoşuma gəlir", "Çay içirəm", "Çay soyuqdur"), "Çay xoşuma gəlir",
            "gustar feili ilə yönlük əvəzlik (me, te, le...) işlənir.",
            "Nəzakətli 'Siz' forması (tək):", listOf("usted", "tú", "nosotros"), "usted",
            "İspan dilində rəsmi müraciət 'usted'-dir.",
            "Səhvi tap:", listOf("Yo gusta el café.", "Me gusta el café.", "Nos gusta viajar."), "Yo gusta el café.",
            "'gustar' ilə 'yo' deyil, 'me' işlənir: Me gusta el café.",
            "Cümləni düz: Onu hər gün görürəm", listOf("Lo veo todos los días."), "Lo veo todos los días.",
            "Tamamlıq əvəzliyi təsriflənmiş feildən əvvəl gəlir.",
            "Dinlə və yaz:", "me gusta mucho", "'Çox xoşuma gəlir' = me gusta mucho."
        )
    ),
    Chapter(
        12, "Sual sözləri",
        "Əsas sual sözləri həmişə vurğu işarəsi daşıyır: ¿Qué? (Nə?), ¿Quién? (Kim?), ¿Dónde? (Harada?), ¿Cuándo? (Nə vaxt?), ¿Cómo? (Necə?), ¿Por qué? (Niyə?), ¿Cuánto? (Neçə/Nə qədər?). Sual cümləsinin əvvəlində '¿' qoyulur.",
        listOf(
            ExampleSentence("¿Dónde está la estación de tren?", "Qatar stansiyası haradadır?", "Donde esta la estasion de tren?"),
            ExampleSentence("¿Cuánto cuesta este billete?", "Bu bilet neçəyədir?", "Kuanto kuesta este biyete?"),
            ExampleSentence("¿Por qué estudias español?", "Niyə ispan dilini öyrənirsən?", "Por ke estudias espanyol?")
        ),
        listOf("Sual sözləri və vurğu", "¿Qué? vs ¿Cuál?", "¿Por qué? vs Porque"),
        spanishExerciseSet(
            "«¿Dónde?» hansı sualdır?", listOf("Harada?", "Nə vaxt?", "Kim?"), "Harada?",
            "Dónde = hara/harada; Cuándo = nə vaxt; Quién = kim.",
            "___ cuesta el café? (Neçəyə?)", listOf("Cuánto", "Quién", "Cuándo"), "Cuánto",
            "Qiymət soruşanda '¿Cuánto cuesta?' işlənir.",
            "Səhvi tap:", listOf("¿Dónde eres tú?", "¿De dónde eres?", "¿Cómo estás?"), "¿Dónde eres tú?",
            "Haralı olduğunu soruşmaq üçün '¿De dónde eres?' deyilir.",
            "Cümləni düz: Məktəb haradadır?", listOf("¿Dónde está la escuela?"), "¿Dónde está la escuela?",
            "Yer soruşulduğu üçün 'está' işlənir.",
            "Dinlə və yaz:", "cómo te llamas", "'Adın nədir?' = cómo te llamas."
        )
    ),
    Chapter(
        13, "Kafe və sifariş",
        "Kafedə nəzakətli sifariş üçün 'Quisiera...' (İstərdim), 'Me gustaría...' və ya 'Para mí, un café, por favor' ifadələri işlənir. Hesabı istəmək: 'La cuenta, por favor'. Şəkərsiz: 'sin azúcar'.",
        listOf(
            ExampleSentence("Quisiera una taza de café sin azúcar.", "Şəkərsiz bir fincan qəhvə istərdim.", "Kisiera una tasa de kafe sin asukar."),
            ExampleSentence("¿Tienen menú en español?", "İspanca menyunuz varmı?", "Tienen menu en espanyol?"),
            ExampleSentence("La cuenta, por favor.", "Zəhmət olmasa, hesabı gətirin.", "La kuenta, por fabor.")
        ),
        listOf("Nəzakətli sifariş", "İçki və yeməklər", "Hesab ödəmək"),
        spanishExerciseSet(
            "«La cuenta, por favor» nə deməkdir?", listOf("Hesabı gətirin, zəhmət olmasa", "Çay verin", "Masa boşdurmu?"), "Hesabı gətirin, zəhmət olmasa",
            "la cuenta restoran/kafedə 'hesab' deməkdir.",
            "Şəkərsiz çay: té ___ azúcar", listOf("sin", "con", "para"), "sin",
            "sin = -sız/-siz (olmadan), con = ilə.",
            "Səhvi tap:", listOf("Un café, gracias favor.", "Un café, por favor.", "La comida está buena."), "Un café, gracias favor.",
            "Nəzakətli xahiş 'por favor' ilə edilir.",
            "Cümləni düz: Zəhmət olmasa bir stəkan su", listOf("Un vaso de agua, por favor."), "Un vaso de agua, por favor.",
            "un vaso de agua = bir stəkan su.",
            "Dinlə və yaz:", "la cuenta por favor", "Restoranda hesab istəmək üçün."
        )
    ),
    Chapter(
        14, "Səyahət və istiqamət",
        "Səyahət sözləri: el aeropuerto, el pasaporte, el billete, la maleta, el hotel. İstiqamətlər: a la derecha (sağa), a la izquierda (sola), todo recto (düz irəli). '¿Cómo llego a...?' (Necə gedə bilərəm?).",
        listOf(
            ExampleSentence("Siga todo recto y gire a la derecha.", "Düz gedin və sağa dönün.", "Siga todo rekto i xire a la dereça."),
            ExampleSentence("El tren sale a las nueve.", "Qatar saat doqquzda yola düşür.", "El tren sale a las nuebe."),
            ExampleSentence("Aquí tiene mi pasaporte y mi billete.", "Buyurun, pasportum və biletim.", "Aki tiene mi pasaporte i mi biyete.")
        ),
        listOf("Aeroport və qatar sözləri", "İstiqamət göstəriciləri", "Otel qeydiyyatı"),
        spanishExerciseSet(
            "«a la izquierda» hansı tərəfdir?", listOf("sola", "sağa", "düz"), "sola",
            "izquierda = sol, derecha = sağ, todo recto = düz.",
            "Necesito comprar un ___ para el tren.", listOf("billete", "vaso", "ojo"), "billete",
            "Qatar üçün 'billete' (bilet) alınır.",
            "Səhvi tap:", listOf("El avión sale en pie.", "El hotel está en el centro.", "Busco el aeropuerto."), "El avión sale en pie.",
            "'en pie' (ayaq üstə) təyyarə ilə uyğun gəlmir.",
            "Cümləni düz: Otel mərkəzdə yerləşir", listOf("El hotel está en el centro."), "El hotel está en el centro.",
            "Yer bildirmək üçün 'está' işlənir.",
            "Dinlə və yaz:", "todo recto", "'Düz irəli' = todo recto."
        )
    ),
    Chapter(
        15, "B1 Ümumi təkrar",
        "B1 səviyyəsində cümlə bağlayıcıları (porque, pero, aunque, cuando) və şərt/məsləhət ifadələri vacibdir: 'Si tengo tiempo, leeré más' (Vaxtım olsa, daha çox oxuyacağam), 'Tienes que practicar' (Məşq etməlisən).",
        listOf(
            ExampleSentence("Estudio español porque me ayuda en mis viajes.", "İspan dilini öyrənirəm, çünki səyahətlərimdə kömək edir.", "Estudio espanyol porke me ayuda en mis biaxes."),
            ExampleSentence("Aunque es difícil, sigo practicando.", "Çətin olsa da, məşq etməyə davam edirəm.", "Aunke es difisil, sigo praktikando."),
            ExampleSentence("Si tengo tiempo mañana, iré al museo.", "Sabah vaxtım olsa, muzeyə gedəcəyəm.", "Si tengo tiempo manyana, ire al museo.")
        ),
        listOf("Bağlayıcı sözlər", "Zamanların uzlaşması", "B1 kompleks yoxlama"),
        spanishExerciseSet(
            "«aunque» bağlayıcısının mənası:", listOf("baxmayaraq ki / -sa da", "çünki", "və"), "baxmayaraq ki / -sa da",
            "aunque ziddiyyət bildirir: '-sa da / baxmayaraq ki'.",
            "Estudio ___ quiero viajar. (çünki)", listOf("porque", "pero", "nunca"), "porque",
            "Səbəb izah etmək üçün 'porque' işlənir.",
            "Səhvi tap:", listOf("Si tengo tiempo, fui ayer.", "Si tengo tiempo, leeré.", "Me gusta aunque sea largo."), "Si tengo tiempo, fui ayer.",
            "Real şərt cümləsində indiki + gələcək zaman uzlaşır.",
            "Cümləni düz: Hər gün məşq etmək lazımdır", listOf("Hay que practicar todos los días."), "Hay que practicar todos los días.",
            "'Hay que + məsdər' ümumi vaciblik bildirir.",
            "Dinlə və yaz:", "porque me gusta", "Səbəb bağlayıcısı: porque me gusta."
        )
    )
)
