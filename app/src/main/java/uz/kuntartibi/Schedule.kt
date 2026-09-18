package uz.kuntartibi

import java.util.Calendar

/** Signal turi — har biri o'z ovozi va o'z qattiqligiga ega. */
enum class Signal { UYGONISH, YUMSHOQ, DIQQAT }

data class Task(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val title: String,
    val detail: String,
    /** Habarni o'chirish uchun nimani suratga olish kerak. */
    val proofPrompt: String,
    /** true bo'lsa — surat olinmaguncha ekran ochilmaydi. */
    val proofRequired: Boolean,
    val signal: Signal,
    /** Har kun boshqa gap chiqishi uchun 7 ta variant (hafta kunlari bo'yicha aylanadi). */
    val messages: List<String>
) {
    fun timeLabel(): String = String.format("%02d:%02d", hour, minute)

    /** Bugungi kun uchun gap. Kun raqami bo'yicha aylanadi, ya'ni har kuni boshqacha. */
    fun messageForToday(cal: Calendar = Calendar.getInstance()): String {
        val day = cal.get(Calendar.DAY_OF_YEAR)
        return messages[(day + id) % messages.size]
    }
}

object Schedule {

    val tasks: List<Task> = listOf(
        Task(
            id = 1, hour = 6, minute = 30,
            title = "Uyg'onish va gigiyena",
            detail = "Uyg'on, yuvin, kunni tetik boshla.",
            proofPrompt = "Oyoq kiyimingni yoki tish cho'tkangni suratga ol — demak sen turding.",
            proofRequired = true,
            signal = Signal.UYGONISH,
            messages = listOf(
                "Tur. Hozir turgan odam bilan yotib qolgan odam orasidagi farq — bir yildan keyin ko'rinadi.",
                "Bugungi kuning shu daqiqada boshlanadi. Yostiq seni kutmaydi, imtihon esa kutmaydi ham, kechirmaydi ham.",
                "Ko'zingni och. Sen o'zingga bergan va'dani bajaryapsan — bu eng qiyin va eng qimmatli odat.",
                "Uyquning oxirgi 10 daqiqasi hech narsa bermaydi. Turgan 10 daqiqang esa butun kunni belgilaydi.",
                "Bugun ham o'zingni yenga olsang, hech kim seni yenga olmaydi.",
                "Sen orzu qilgan odam har kuni shu vaqtda turadi. Hozir o'sha odam bo'l.",
                "Tur, yuvin, nafas ol. Bugun — o'z ustingda ishlaydigan yana bir kun."
            )
        ),
        Task(
            id = 2, hour = 7, minute = 0,
            title = "Nonushta",
            detail = "Yengil va quvvat beruvchi nonushta.",
            proofPrompt = "Nonushtangni suratga ol.",
            proofRequired = true,
            signal = Signal.YUMSHOQ,
            messages = listOf(
                "Miya ham ovqat yeydi. Yaxshi nonushta — birinchi darsing.",
                "Shoshilma, o'tirib ye. Tanang bugun senga xizmat qiladi, unga yoqilg'i ber.",
                "Yengil nonushta — tiniq fikr. Og'ir nonushta — uyquchan bosh.",
                "Bir stakan suv ham unutma. Kun suv bilan boshlanadi.",
                "Bugungi kuchni hozir yig'asan. Tez va foydali bo'lsin.",
                "Ovqatlanayotganda telefonni qo'y. Bu ham dam olish.",
                "Sog'lom tana — uzoq o'qiy oladigan tana."
            )
        ),
        Task(
            id = 3, hour = 7, minute = 30,
            title = "Yo'lga chiqish",
            detail = "Tayyorlan va yo'lga chiq.",
            proofPrompt = "Sumkangni yoki eshik oldidagi holatingni suratga ol.",
            proofRequired = true,
            signal = Signal.DIQQAT,
            messages = listOf(
                "Kech qolmaslik — bu ham qobiliyat. Bugun o'sha qobiliyatni ko'rsat.",
                "Chiqishdan oldin tekshir: daftar, ruchka, telefon, kalit.",
                "Yo'lda bosh qotirma — bugun nimani o'rganmoqchisan, shuni bir o'yla.",
                "Tayyorgarlik — g'alabaning yarmi. Qolgan yarmi yo'lda boshlanadi.",
                "Bir daqiqa erta chiqqan odam butun kun xotirjam yuradi.",
                "Yo'lda quloqchin bo'lsa — koreyscha so'z tinglab ket.",
                "Qadam tashla. Kun allaqachon senga qarab turibdi."
            )
        ),
        Task(
            id = 4, hour = 8, minute = 0,
            title = "Darslar",
            detail = "Asosiy mashg'ulotlar.",
            proofPrompt = "Ochilgan daftaring yoki stolingni suratga ol.",
            proofRequired = true,
            signal = Signal.DIQQAT,
            messages = listOf(
                "Diqqat bilan o'tirgan 4 soat — chalg'igan 8 soatdan kuchli.",
                "Tushunmagan joyingni yozib qo'y. Kechqurun o'shani yechasan.",
                "Eng old qatorda o'tir. Miya ham shu yerda uyg'oq bo'ladi.",
                "Bugun kamida bitta savol ber. Savol bergan odam o'sadi.",
                "Telefon cho'ntakda qolsin. U hech qayerga qochmaydi.",
                "Bilim to'planadi, bir kunda kelmaydi. Bugungi ulushingni ol.",
                "Diqqating — sening eng qimmat mablag'ing. Bekorga sarflama."
            )
        ),
        Task(
            id = 5, hour = 12, minute = 0,
            title = "Tushlik va dam",
            detail = "Ovqatlan va to'garakka tayyorlan.",
            proofPrompt = "Tushligingni suratga ol.",
            proofRequired = true,
            signal = Signal.YUMSHOQ,
            messages = listOf(
                "Dam olish — dangasalik emas, tayyorgarlikning bir qismi.",
                "Yaxshi ovqatlan: oldinda 4 soatlik matematika turibdi.",
                "15 daqiqa ko'zni yumib dam ol — miya qayta ishga tushadi.",
                "Yarim kun o'tdi. O'zingga ayt: hozirgacha yaxshi bordim.",
                "Og'ir ovqat uyqu keltiradi. Yengil ovqatlan.",
                "Suv ich. Charchoqning yarmi — suvsizlik.",
                "Endi eng muhim qismi boshlanadi. Kuch yig'."
            )
        ),
        Task(
            id = 6, hour = 13, minute = 0,
            title = "Matematika to'garagi",
            detail = "Noyabr imtihoni uchun intensiv tayyorgarlik.",
            proofPrompt = "Misol yechayotgan daftaringni va ruchkangni suratga ol.",
            proofRequired = true,
            signal = Signal.DIQQAT,
            messages = listOf(
                "Noyabr yaqin. Bugungi har bir misol — o'sha kundagi bitta to'g'ri javob.",
                "Qiyin masala — bu sening o'sish nuqtang. Undan qochma, ustiga bor.",
                "Yechilmagan misolni tashlab ketma. 10 daqiqa ko'proq o'ylab ko'r.",
                "Formulani yodlama — nega ishlashini tushun. Imtihonda shu qutqaradi.",
                "Bugun xato qilsang — yaxshi. Imtihonda emas, hozir xato qil.",
                "Har kuni yechgan misollaringni sanab bor. Raqam sendan kuchliroq gapiradi.",
                "Sen matematikani emas, o'z sabringni mashq qilyapsan."
            )
        ),
        Task(
            id = 7, hour = 17, minute = 0,
            title = "Uyga qaytish",
            detail = "Yengil piyoda yurish.",
            proofPrompt = "Ko'chani yoki yo'lni suratga ol.",
            proofRequired = false,
            signal = Signal.YUMSHOQ,
            messages = listOf(
                "Piyoda yur. Bu miyaning dam olish usuli.",
                "Bugun o'rgangan bitta narsani eslab ko'r.",
                "Toza havo — tekin dori. Nafasni chuqur ol.",
                "Yo'l — kunni sarhisob qiladigan joy.",
                "Uyga kirishdan oldin charchoqni ko'chada qoldir.",
                "Qadamlar ham mashq. Shoshilmay, tik yur.",
                "Kun tugagani yo'q — hali eng yaxshi qismlari bor."
            )
        ),
        Task(
            id = 8, hour = 17, minute = 30,
            title = "Uy ishlari",
            detail = "Uy yumushlariga yordam.",
            proofPrompt = "Bajargan yumushingni suratga ol (yig'ilgan xona, yuvilgan idish...).",
            proofRequired = true,
            signal = Signal.YUMSHOQ,
            messages = listOf(
                "Uyingdagi tartib — boshingdagi tartib.",
                "45 daqiqa. Tez boshla, tez tugat.",
                "Yordam bergan odamning duosi orqasidan yuradi.",
                "Kichik ish ham to'liq bajarilsa — katta odat bo'ladi.",
                "Bu ham reja. Rejaning oson qismini tashlab ketish — buzilishning boshi.",
                "Ishlayotganda musiqa qo'y, vaqt tez o'tadi.",
                "Bu 45 daqiqa keyingi mashq uchun isinish."
            )
        ),
        Task(
            id = 9, hour = 18, minute = 15,
            title = "Salomatlik mashqlari",
            detail = "To'liq 1 soat badantarbiya yoki sport.",
            proofPrompt = "Mashq qilayotgan holatingni yoki sport joyingni suratga ol.",
            proofRequired = true,
            signal = Signal.UYGONISH,
            messages = listOf(
                "Tana charchamasa, miya uxlamaydi. Bir soat — faqat harakat.",
                "Boshlash og'ir, tugatish shirin. 10 daqiqadan keyin o'zing xohlaysan.",
                "Kuchli tana — uzoq o'tira oladigan tana. Bu ham imtihon tayyorgarligi.",
                "Bugun bitta ko'proq qil. Kechagi o'zingni yeng.",
                "Ter — bu sening bugungi imzoing.",
                "Telefonni qo'y, taymerni qo'y, ishga tush.",
                "Sport kayfiyatni davolaydi. Bir soatdan keyin boshqa odam bo'lasan."
            )
        ),
        Task(
            id = 10, hour = 19, minute = 15,
            title = "Dush va kechki ovqat",
            detail = "Mashqdan keyingi gigiyena va ovqat.",
            proofPrompt = "Sochiqni yoki kechki ovqatingni suratga ol.",
            proofRequired = false,
            signal = Signal.YUMSHOQ,
            messages = listOf(
                "Sovuq dush — miyani qayta yoqadi. Oldinda 2 soatlik o'qish bor.",
                "Kechki ovqat yengil bo'lsin, aks holda kitob oldida uxlab qolasan.",
                "Tozalik — o'ziga hurmatning birinchi belgisi.",
                "45 daqiqa dam. Keyin yana ishga.",
                "Bugun tanangga rahmat ayt — u seni ko'tarib yurdi.",
                "Ovqatdan keyin darrov yotma. Stol seni kutyapti.",
                "Kun tugamadi — eng foydali ikki soat oldinda."
            )
        ),
        Task(
            id = 11, hour = 20, minute = 0,
            title = "Koreys tili (TOPIK)",
            detail = "Lug'at, grammatika, mashqlar.",
            proofPrompt = "Koreyscha daftaring yoki kitobingni suratga ol.",
            proofRequired = true,
            signal = Signal.DIQQAT,
            messages = listOf(
                "한 걸음 더 — yana bir qadam. TOPIK har kuni yodlangan so'zdan yig'iladi.",
                "Bugun 20 ta yangi so'z. Bir yilda — 7000 ta. Matematikani o'zing bilasan.",
                "Grammatikani gap ichida yodla, ro'yxat ichida emas.",
                "Ovoz chiqarib o'qi. Til quloq orqali o'rnashadi.",
                "화이팅! Bugungi mashq kelajakdagi suhbating.",
                "Kechagi so'zlarni avval takrorla, keyin yangisiga o't.",
                "Sen shunchaki til o'rganmayapsan — boshqa hayotning eshigini ochyapsan."
            )
        ),
        Task(
            id = 12, hour = 21, minute = 0,
            title = "Matematika (mustaqil)",
            detail = "Sertifikat testlari va mavzu takrori.",
            proofPrompt = "Test varaqasi yoki yechayotgan misolingni suratga ol.",
            proofRequired = true,
            signal = Signal.DIQQAT,
            messages = listOf(
                "Bir soat — taymer qo'y, test yech, javobni tekshir. Oddiy va shafqatsiz.",
                "Xato qilgan misolni alohida daftarga ko'chir. O'sha daftar seni imtihonda qutqaradi.",
                "Charchadingmi? Sen tayyorlanayotgan odam charchoqni ham hisobga olgan.",
                "Bugun tushunmagan mavzu — ertaga savol bo'lib qaytadi. Hoziroq yop.",
                "Kunning oxirgi zarbasi eng kuchlisi bo'lsin.",
                "Sekundomer bilan yech. Imtihonda vaqt ham raqib.",
                "Yana bitta variant. Faqat bitta. Keyin dam."
            )
        ),
        Task(
            id = 13, hour = 22, minute = 0,
            title = "Kun yakuni",
            detail = "Ertangi kunga kiyim va kitoblarni hozirlash.",
            proofPrompt = "Tayyorlab qo'ygan kiyim va sumkangni suratga ol.",
            proofRequired = true,
            signal = Signal.YUMSHOQ,
            messages = listOf(
                "Ertangi kunni bugun yut. Kiyim tayyor — ertalab o'ylash shart emas.",
                "Bugun bajarganlaringni bir ko'zdan kechir. Ozmi-ko'pmi — sen harakat qilding.",
                "Telefonni zaryadga qo'y va uzoqroqqa qo'y.",
                "Ertangi 3 ta asosiy ishni yozib qo'y. Faqat 3 ta.",
                "Kun tugadi. O'zingni koyima, xulosa chiqar.",
                "Sumka eshik yonida tursin. Ertalabki o'zingga sovg'a.",
                "Bugun rejaga necha foiz amal qilding? Ertaga undan ko'p bo'lsin."
            )
        ),
        Task(
            id = 14, hour = 22, minute = 30,
            title = "Uyqu",
            detail = "To'liq 8 soat uyqu.",
            proofPrompt = "Yotoq joyingni suratga ol — telefon endi o'chadi.",
            proofRequired = false,
            signal = Signal.YUMSHOQ,
            messages = listOf(
                "Uyqu — dangasalik emas, tiklanish. Miya bugun o'rganganini shunda saqlaydi.",
                "Ekranni yop. Ertaga 06:30 da yana turasan.",
                "8 soat uyqu — ertangi diqqating.",
                "Xayolingni tinchlantir: bugungi ish bugun tugadi.",
                "Ertangi kuning shu daqiqada boshlanadi — yotish bilan.",
                "Telefonsiz 10 daqiqa yot. Uyqu tezroq keladi.",
                "Yaxshi dam ol. Ertaga yana o'sha odam bo'lasan."
            )
        )
    )

    fun byId(id: Int): Task? = tasks.firstOrNull { it.id == id }
}
