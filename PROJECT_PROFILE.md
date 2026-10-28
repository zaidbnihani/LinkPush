# ملف المشروع / Project Profile

## العربية

### الغرض
LinkPush تطبيق Android مكتوب بـ Kotlin يكتشف أجهزةً متاحة على الشبكة المحلية ويرسل إليها رابطًا، كما يشغّل خدمة استقبال أمامية لتلقي الرابط وفتحه على الجهاز. يستند هذا الوصف إلى `app/src/main/java/com/example/network/NetworkScanner.kt` و`LinkSender.kt` و`app/src/main/java/com/example/service/LinkReceiverService.kt` و`app/src/main/AndroidManifest.xml`. ملف `PRD.md` يصف متطلبات صفحة تسويقية للتطبيق، وليس مواصفات تنفيذ موقع موجود. و`README.md` يحتوي على مسودة PRD مختلفة تخص QuranTune؛ لذلك لا يُعد مصدرًا موثوقًا لوصف التطبيق الحالي.

### المكوّنات والتدفقات الأساسية
- واجهة Compose ونقطة الدخول: `app/src/main/java/com/example/MainActivity.kt` و`app/src/main/java/com/example/ui/screens/` و`app/src/main/java/com/example/ui/components/`. ينسّق `app/src/main/java/com/example/ui/MainViewModel.kt` حالة المسح والإرسال والعناصر المحفوظة.
- اكتشاف الأجهزة: `app/src/main/java/com/example/network/NetworkScanner.kt` يفحص الشبكة الفرعية ويتحقق من اتصال TCP بالمنفذ 8888؛ تمثل `DiscoveredDevice.kt` نتيجة الاكتشاف. يرسل `LinkSender.kt` الرابط إلى عنوان الجهاز والمنفذ عبر TCP.
- الاستقبال: `app/src/main/java/com/example/service/LinkReceiverService.kt` يفتح خادم TCP على المنفذ 8888، ويتعامل مع الاتصالات الواردة، ثم يفتح الرابط ويعرض إشعارًا. يسجل `AndroidManifest.xml` الخدمة ومكوّنات التطبيق والأذونات ذات الصلة.
- التخزين المحلي: `data/SavedDevice.kt` و`SavedDeviceDao.kt` و`SavedDeviceRepository.kt` و`AppDatabase.kt` تحفظ العناصر/الأجهزة المسجلة محليًا باستخدام Room.
- الأجزاء المساندة: `privacy/` لإدارة بوابة الموافقة، و`update/` لفحص إصدار GitHub وتنزيله، و`utils/` لأدوات تحسين البطارية، و`service/BootReceiver.kt` لاستقبال حدث الإقلاع. تدعم `app/src/main/java/com/example/UrlLauncherActivity.kt` فتح الرابط.

### شجرة مختصرة
```text
.
├── README.md                         # محتوى PRD غير مطابق للتطبيق الحالي
├── PRD.md                            # متطلبات تسويقية/موقع هبوط؛ ليست تنفيذ الموقع
├── settings.gradle.kts               # إعداد Gradle ووحداته ومستودعات الإضافات
├── build.gradle.kts                  # إعداد إضافات Gradle على مستوى المشروع
├── gradle.properties                 # خصائص بناء Gradle
├── gradle/libs.versions.toml         # كتالوج الإصدارات والتبعيات
└── app/
    ├── build.gradle.kts              # إعداد Android والتبعيات والاختبارات
    └── src/
        ├── main/AndroidManifest.xml  # مكونات Android والأذونات
        ├── main/java/com/example/    # واجهة التطبيق، الشبكة، البيانات والخدمات
        ├── main/res/                 # الموارد والأيقونات والنصوص والسمات
        ├── test/java/                # اختبارات محلية وRobolectric
        └── androidTest/java/         # اختبار instrumented على جهاز/محاكي
```

### الملفات المحورية
- `app/src/main/java/com/example/network/NetworkScanner.kt` و`LinkSender.kt`: اكتشاف الأجهزة والإرسال عبر الشبكة المحلية.
- `app/src/main/java/com/example/service/LinkReceiverService.kt`: استقبال الروابط في الخلفية على TCP/8888.
- `app/src/main/java/com/example/ui/MainViewModel.kt` و`ui/screens/MainAppScreen.kt`: تنسيق حالة التطبيق وواجهة الشاشة الرئيسية.
- `app/src/main/java/com/example/data/AppDatabase.kt` وملفات DAO/repository/entity المجاورة: حفظ البيانات محليًا.
- `app/src/main/AndroidManifest.xml` و`app/build.gradle.kts`: تعريف التطبيق وأذوناته وإعداد بنائه.
- `gradle/libs.versions.toml`: إصدارات الإضافات والمكتبات.

### التقنية والإعداد
هذا مشروع Android أصلي بلغة Kotlin مع Jetpack Compose، وإعداد Gradle Kotlin DSL، وكتالوج إصدارات Gradle. وفق `gradle/libs.versions.toml` و`app/build.gradle.kts`، الإصدارات المعلنة تشمل Android Gradle Plugin 9.1.1، Kotlin 2.2.10، KSP 2.3.5، Compose BOM 2024.09.00، Room 2.7.0، Navigation Compose 2.8.9، Retrofit 2.12.0، OkHttp 4.10.0، Gson 2.11.0، Coroutines 1.10.2، Robolectric 4.16.1، وRoborazzi 1.59.0. يحدد إعداد التطبيق `compileSdk` الإصدار 36، المستوى الفرعي 1، و`minSdk` 24 و`targetSdk` 36؛ إصدار التطبيق المعلن 2.1.1 (versionCode 5)، وتوافق مصدر/هدف Java مضبوط على 11.

### الإعداد والتشغيل والاختبار والبناء
المستودع مهيأ كمشروع Gradle/Android، لكن لا يقدم `README.md` تعليمات إعداد أو تشغيل أو اختبار صالحة للتطبيق. لا توجد ملفات Gradle Wrapper متعقبة (`gradlew` أو `gradle/wrapper/`) ولا تعليمات CI ظاهرة في المستودع؛ لذلك لا نثبت هنا أوامر بناء/اختبار قابلة لإعادة الإنتاج. إعداد Gradle يصرح بأن Secrets Gradle Plugin يقرأ ملفي `.env` و`.env.example`؛ لم تُقرأ محتوياتهما. أسماء متغيرات البيئة التي يستخدمها إعداد توقيع الإصدار هي `KEYSTORE_PATH` و`STORE_PASSWORD` و`KEY_PASSWORD` فقط؛ لا تُدرج أي قيم أو بيانات سرية.

### ملاحظات عملية ونقاط غير محسومة
- ابدأ من إعداد Android في `app/build.gradle.kts` وكتالوج الإصدارات في `gradle/libs.versions.toml`، واتبع تدفق الشبكة بين `NetworkScanner.kt` و`MainViewModel.kt` و`LinkSender.kt` وخدمة الاستقبال.
- توجد اختبارات محلية في `app/src/test/java/com/example/` واختبار أجهزة في `app/src/androidTest/java/com/example/`؛ بعضها أمثلة قالبية (مثل اختبار الجمع والتحقق من اسم الحزمة)، فلا ينبغي اعتبارها تغطية وظيفية شاملة.
- لا تظهر في الملفات المتعقبة إعدادات CI أو وصف موثوق لآلية نشر التطبيق.
- يتباين `README.md` (مسودة QuranTune) مع `PRD.md` (متطلبات موقع Link Push) ومع وجود تطبيق Android فعلي في الشيفرة. متطلبات PRD، مثل موقع التسويق أو قيم الميزات الترويجية، لا تثبت وجود تنفيذ لها. يلزم تحديث README من مالك المشروع وتأكيد نطاق المشروع، وكذلك توضيح متطلبات إعداد Firebase/ملفات الأسرار ومسار تشغيل البناء محليًا.

## English

### Purpose
LinkPush is a Kotlin Android app that discovers reachable devices on the local network and sends a link to them; it also runs a foreground receiver service that accepts a link and opens it on the device. This description is supported by `app/src/main/java/com/example/network/NetworkScanner.kt`, `LinkSender.kt`, `app/src/main/java/com/example/service/LinkReceiverService.kt`, and `app/src/main/AndroidManifest.xml`. `PRD.md` describes requirements for a marketing page for the app, not an implemented website. `README.md` contains a different QuranTune PRD draft, so it is not a reliable description of the current app.

### Main components and flows
- Compose UI and entry point: `app/src/main/java/com/example/MainActivity.kt`, `app/src/main/java/com/example/ui/screens/`, and `app/src/main/java/com/example/ui/components/`. `app/src/main/java/com/example/ui/MainViewModel.kt` coordinates scan/send state and saved items.
- Device discovery: `app/src/main/java/com/example/network/NetworkScanner.kt` scans the subnet and checks TCP reachability on port 8888; `DiscoveredDevice.kt` represents discoveries. `LinkSender.kt` sends the link to a device address and port over TCP.
- Receiving: `app/src/main/java/com/example/service/LinkReceiverService.kt` opens a TCP server on port 8888, handles incoming connections, then opens the link and displays a notification. `AndroidManifest.xml` registers the service, app components, and related permissions.
- Local storage: `data/SavedDevice.kt`, `SavedDeviceDao.kt`, `SavedDeviceRepository.kt`, and `AppDatabase.kt` persist saved items/devices locally using Room.
- Supporting areas: `privacy/` manages the consent gate, `update/` checks for and downloads a GitHub release, `utils/` contains battery-optimization helpers, and `service/BootReceiver.kt` handles the boot event. `app/src/main/java/com/example/UrlLauncherActivity.kt` supports opening the link.

### Short directory tree
```text
.
├── README.md                         # PRD content inconsistent with the current app
├── PRD.md                            # Marketing/landing-page requirements; not the site implementation
├── settings.gradle.kts               # Gradle settings, modules, and plugin repositories
├── build.gradle.kts                  # Project-level Gradle plugin setup
├── gradle.properties                 # Gradle build properties
├── gradle/libs.versions.toml         # Dependency and plugin version catalog
└── app/
    ├── build.gradle.kts              # Android, dependency, and test configuration
    └── src/
        ├── main/AndroidManifest.xml  # Android components and permissions
        ├── main/java/com/example/    # App UI, networking, data, and services
        ├── main/res/                 # Resources, icons, strings, and themes
        ├── test/java/                # Local and Robolectric tests
        └── androidTest/java/         # Instrumented device/emulator test
```

### Key files
- `app/src/main/java/com/example/network/NetworkScanner.kt` and `LinkSender.kt`: local-network discovery and link sending.
- `app/src/main/java/com/example/service/LinkReceiverService.kt`: receives links in the background over TCP/8888.
- `app/src/main/java/com/example/ui/MainViewModel.kt` and `ui/screens/MainAppScreen.kt`: app state coordination and main-screen UI.
- `app/src/main/java/com/example/data/AppDatabase.kt` and adjacent DAO/repository/entity files: local persistence.
- `app/src/main/AndroidManifest.xml` and `app/build.gradle.kts`: app declaration, permissions, and build configuration.
- `gradle/libs.versions.toml`: plugin and library versions.

### Technology and configuration
This is a native Android project in Kotlin using Jetpack Compose, Gradle Kotlin DSL, and a Gradle version catalog. Versions declared in `gradle/libs.versions.toml` and `app/build.gradle.kts` include Android Gradle Plugin 9.1.1, Kotlin 2.2.10, KSP 2.3.5, Compose BOM 2024.09.00, Room 2.7.0, Navigation Compose 2.8.9, Retrofit 2.12.0, OkHttp 4.10.0, Gson 2.11.0, Coroutines 1.10.2, Robolectric 4.16.1, and Roborazzi 1.59.0. The app configuration sets `compileSdk` to API 36, minor level 1, `minSdk` 24, and `targetSdk` 36; the declared app version is 2.1.1 (versionCode 5), and Java source/target compatibility is 11.

### Setup, run, test, and build
The repository is configured as a Gradle/Android project, but `README.md` provides no valid app setup, run, or test instructions. No tracked Gradle Wrapper files (`gradlew` or `gradle/wrapper/`) or CI instructions are present in the repository; therefore no reproducible build/test commands are asserted here. The Gradle configuration says the Secrets Gradle Plugin reads `.env` and `.env.example`; their contents were not read. The environment-variable names used by release signing are `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` only; no values or secret data are included.

### Practical notes and unresolved points
- Start with Android configuration in `app/build.gradle.kts` and the version catalog in `gradle/libs.versions.toml`; follow the network flow across `NetworkScanner.kt`, `MainViewModel.kt`, `LinkSender.kt`, and the receiver service.
- Local tests are under `app/src/test/java/com/example/`, and a device test is under `app/src/androidTest/java/com/example/`; some are template examples (such as the addition test and package-name check), not comprehensive feature coverage.
- No CI configuration or authoritative app-release procedure appears among the tracked files.
- `README.md` (QuranTune draft) conflicts with `PRD.md` (Link Push website requirements) and the Android app in source. PRD requirements, including a marketing site or promotional feature claims, do not prove an implementation exists. The project owner should update the README and confirm project scope, and clarify Firebase/secret-file setup and the local build workflow.
