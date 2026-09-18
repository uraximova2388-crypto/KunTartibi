# Kun tartibi — majburlovchi eslatma ilovasi (Android)

Sening kunlik rejang asosida yozilgan Android ilova. Har bir ish vaqti kelganda:

1. Telefon uyg'onadi, ovoz va tebranish boshlanadi (qulflangan ekranda ham).
2. **Butun ekranni qoplaydigan** oyna ochiladi: soat, ish nomi va o'sha kunga mos mativatsion gap.
3. Oynada faqat bitta yo'l bor — **Kamerani ochish** → o'sha ishni qilayotganingni suratga olish.
4. Surat olinmaguncha oyna yopilmaydi: "Orqaga" ishlamaydi, ovoz tugmalari ishlamaydi, "Home" bossang oyna qaytib keladi, habar ham surib o'chmaydi.
5. Surat olingandan keyin ovoz o'chadi, ish "bajarildi" deb belgilanadi va asosiy ekranda ✓ paydo bo'ladi.

Mativatsion gaplar har bir ish uchun 7 xil yozilgan va kun raqamiga qarab aylanadi — ya'ni bugungi gap ertangi gapdan boshqa bo'ladi.

## Qaysi ish uchun nima suratga olinadi

| Vaqt | Ish | Surat |
|---|---|---|
| 06:30 | Uyg'onish | oyoq kiyim yoki tish cho'tka |
| 07:00 | Nonushta | nonushta |
| 07:30 | Yo'lga chiqish | sumka / eshik oldi |
| 08:00 | Darslar | ochilgan daftar |
| 12:00 | Tushlik | tushlik |
| 13:00 | Matematika to'garagi | misol yechayotgan daftar |
| 17:00 | Uyga qaytish | ko'cha *(surat majburiy emas)* |
| 17:30 | Uy ishlari | bajarilgan yumush |
| 18:15 | Sport | mashq holati |
| 19:15 | Dush va kechki ovqat | sochiq / ovqat *(majburiy emas)* |
| 20:00 | Koreys tili | koreyscha daftar |
| 21:00 | Matematika (mustaqil) | test varaqasi |
| 22:00 | Kun yakuni | tayyorlangan kiyim va sumka |
| 22:30 | Uyqu | yotoq *(majburiy emas)* |

Har bir ishni asosiy ekrandan o'chirib-yoqib qo'yish mumkin.

## Ovozlar

`app/src/main/res/raw/` papkasiga uchta fayl tashlaysan:

- `signal_uygonish.mp3` — uyg'onish va sport uchun. Bu yerga o'sha "g'alati", asabga tegadigan ovozni qo'y. Ilova uni **sekin boshlab, har 8 soniyada balandlatib boradi** — quloqni yormaydi, lekin yotib qolishga ham qo'ymaydi.
- `signal_diqqat.mp3` — darslar, matematika, koreys tili uchun.
- `signal_yumshoq.mp3` — ovqat, uy ishlari, uyqu uchun.

Fayl qo'shmasang, telefondagi standart budilnik ovozi ishlatiladi. Ovoz "alarm" kanalida chiqadi, ya'ni telefon jimlik rejimida bo'lsa ham eshitiladi.

## Qanday yig'iladi (build)

1. Kompyuterga **Android Studio** ni o'rnat (bepul).
2. `File → Open` → shu `KunTartibi` papkasini tanla. Gradle o'zi yuklab oladi (internet kerak, birinchi marta 5–15 daqiqa).
3. Telefonda *Sozlamalar → Telefon haqida → Build number* ni 7 marta bosib **Developer options** ni yoq, keyin **USB debugging** ni yoq.
4. Telefonni USB bilan ula va Android Studio'da yashil ▶ tugmasini bos. Ilova telefonga o'rnatiladi.
5. APK kerak bo'lsa: `Build → Build Bundle(s)/APK(s) → Build APK(s)`.

Minimal Android versiyasi: **8.0 (API 26)**.

## Birinchi ochilishda beriladigan ruxsatlar

Ilova o'zi so'raydi, hammasiga **ruxsat ber**, aks holda eslatma ishlamaydi:

- **Bildirishnomalar** (Android 13+)
- **Aniq budilnik qo'yish** (Alarms & reminders)
- **Batareyani tejashdan chiqarish** — busiz telefon ilovani uxlab qolganda o'ldiradi
- **Kamera** — birinchi surat olishda so'raydi

Xiaomi / Redmi / Realme / Oppo / Vivo telefonlarda qo'shimcha: *Sozlamalar → Ilovalar → Kun tartibi* ichidan **Autostart** ni yoq va **"Qulflangan ekranda ko'rsatish" / "Display pop-up windows"** ruxsatini ber. Bu telefonlarda tizim to'liq ekranli oynani shusiz bloklaydi.

## Fayllar

```
app/src/main/java/uz/kuntartibi/
  Schedule.kt            — kun tartibi, mativatsion gaplar, surat topshiriqlari
  AlarmScheduler.kt      — har kungi aniq budilniklar (AlarmManager)
  AlarmReceiver.kt       — vaqt kelganda ishga tushadi
  AlarmService.kt        — ovoz, tebranish, to'liq ekranli bildirishnoma
  AlarmActivity.kt       — to'liq ekranli oyna + kamera (suratsiz yopilmaydi)
  MainActivity.kt        — asosiy ro'yxat, kun progressi
  ProofGalleryActivity.kt— bugungi isbot suratlari
  Prefs.kt               — sozlamalar va bajarilganlar ro'yxati
```

Vaqtni yoki gaplarni o'zgartirmoqchi bo'lsang — faqat `Schedule.kt` faylini tahrirla, boshqa hech narsaga tegish shart emas.

## Muhim ikki eslatma

- Bu **Android** uchun. iPhone'da bunday ilovani yasab bo'lmaydi: iOS ilovaga boshqa ilova ustidan ekranni qoplashga va signalni surat bilan bog'lashga ruxsat bermaydi. iPhone'da eng yaqin variant — odatiy budilnik + Shortcuts.
- Favqulodda holatda telefonni qayta yoqish yoki ilovani "Force stop" qilish signalni to'xtatadi. Buni ataylab qoldirdim — telefon butunlay qulflanib qolishi xavfli.
