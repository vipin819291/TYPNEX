package com.example.data.model

object LanguageData {
    val supportedLanguages = listOf(
        LanguageItem("hi", "Hindi", "हिन्दी", "🇮🇳"),
        LanguageItem("en", "English", "English", "🌐"),
        LanguageItem("hinglish", "Hinglish", "Hinglish", "🇮🇳"),
        LanguageItem("es", "Spanish", "Español", "🇪🇸"),
        LanguageItem("fr", "French", "Français", "🇫🇷"),
        LanguageItem("de", "German", "Deutsch", "🇩🇪"),
        LanguageItem("ar", "Arabic", "العربية", "🇸🇦"),
        LanguageItem("ru", "Russian", "Русский", "🇷🇺"),
        LanguageItem("bn", "Bengali", "বাংলা", "🇮🇳"),
        LanguageItem("mr", "Marathi", "मराठी", "🇮🇳"),
        LanguageItem("te", "Telugu", "తెలుగు", "🇮🇳"),
        LanguageItem("ta", "Tamil", "தமிழ்", "🇮🇳"),
        LanguageItem("gu", "Gujarati", "ગુજરાતી", "🇮🇳"),
        LanguageItem("ur", "Urdu", "اردو", "🇵🇰")
    )

    fun getWelcomeMessages(userName: String, langCode: String): Triple<String, String, String> {
        return when (langCode.lowercase()) {
            "hi" -> Triple(
                "नमस्ते $userName जी! Typnex में आपका स्वागत है।",
                "आप कुछ भी टाइप करा सकते हैं, आप कोई भी फ़ाइल भेज सकते हैं। अपनी ड्राफ़्ट हमें भेजें और मात्र 5 मिनट में अपनी ओरिजिनल टाइपिंग फ़ाइल प्राप्त करें!",
                "भविष्य में आप यहाँ कुछ भी काम करा सकते हैं, अभी केवल टाइपिंग और दस्तावेज़ तैयार करने का काम उपलब्ध है।"
            )
            "en" -> Triple(
                "Hello $userName! Welcome to Typnex.",
                "You can get anything typed, you can send any file. Send your draft to us and receive your original typed file in just 5 minutes!",
                "In the future, you will be able to get all kinds of tasks completed here; currently, ultra-fast typing and document work is active."
            )
            "es" -> Triple(
                "¡Hola $userName! Bienvenido a Typnex.",
                "Puedes solicitar cualquier mecanografiado y enviar cualquier archivo. Envíanos tu borrador y recibe tu archivo original mecanografiado en 5 minutos.",
                "En el futuro podrás encargar cualquier trabajo, actualmente nuestro servicio de mecanografía veloz está disponible."
            )
            "fr" -> Triple(
                "Bonjour $userName ! Bienvenue sur Typnex.",
                "Vous pouvez faire saisir n'importe quel texte et envoyer n'importe quel fichier. Envoyez-nous votre brouillon et recevez votre document original saisi en 5 minutes !",
                "À l'avenir, vous pourrez confier tous types de travaux ; pour l'instant, le service de dactylographie express est opérationnel."
            )
            "de" -> Triple(
                "Hallo $userName! Willkommen bei Typnex.",
                "Sie können jeden Text tippen lassen und jede Datei senden. Senden Sie uns Ihren Entwurf und erhalten Sie Ihre getippte Datei in nur 5 Minuten!",
                "In Zukunft werden Sie viele Aufgaben hier erledigen können; derzeit ist unser Express-Tippservice für Sie da."
            )
            "ar" -> Triple(
                "مرحباً $userName! أهلاً بك في تيبنكس Typnex.",
                "يمكنك كتابة وطباعة أي شيء وإرسال أي ملف. أرسل مسودتك إلينا واحصل على ملفك الأصلي المطبوع في غضون 5 دقائق فقط!",
                "في المستقبل ستتمكن من إنجاز شتى المهام، وحالياً تتوفر خدمة الطباعة والرقن السريع للمستندات."
            )
            "ru" -> Triple(
                "Здравствуйте, $userName! Добро пожаловать в Typnex.",
                "Вы можете заказать набор любого текста и отправить любой файл. Отправьте нам черновик и получите готовый напечатанный файл всего за 5 минут!",
                "В будущем вы сможете заказывать любые работы, сейчас доступен скоростной набор текста и обработка документов."
            )
            "bn" -> Triple(
                "নমস্কার $userName! Typnex-এ আপনাকে স্বাগতম।",
                "আপনি যেকোনো কিছু টাইপ করাতে পারেন, যেকোনো ফাইল পাঠাতে পারেন। আপনার খসড়া আমাদের পাঠান এবং ৫ মিনিটে আপনার মূল টাইপ করা ফাইলটি পান!",
                "ভবিষ্যতে আপনি এখানে যেকোনো কাজ করাতে পারবেন, বর্তমানে অতি দ্রুত টাইপিং কাজ সম্পন্ন করা যাবে।"
            )
            "mr" -> Triple(
                "नमस्कार $userName! Typnex मध्ये आपले सहर्ष स्वागत आहे.",
                "तुम्ही काहीही टाईप करून घेऊ शकता, कोणतीही फाईल पाठवू शकता. तुमचा ड्राफ्ट आम्हाला पाठवा आणि फक्त 5 मिनिटांत तुमची मूळ टायपिंग फाईल मिळवा!",
                "भविष्यात तुम्ही येथे कोणतीही कामे करून घेऊ शकाल, सध्या केवळ सुपरफास्ट टायपिंगचे काम उपलब्ध आहे."
            )
            else -> Triple( // hinglish default
                "Hello $userName! Typnex me aapka swagat hai.",
                "Aap kuch bhi type kara sakte hai, aap kuch bhi file send kar sakte hai. Apni draft hume send karo aur 5 min main apni original typing file lo!",
                "Bhavishya main aap kuch bhi kaam kara sakte hai, abhi keval typing wala kaam ho sakta hai."
            )
        }
    }
}
