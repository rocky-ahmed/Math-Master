package com.example.math

import androidx.compose.ui.graphics.Color

enum class FormulaDifficulty(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val badgeColor: Color
) {
    BASIC("basic", "মৌলিক (Basic)", "Basic", Color(0xFF10B981)),
    INTERMEDIATE("intermediate", "মাধ্যমিক (Intermediate)", "Intermediate", Color(0xFFF59E0B)),
    ADVANCED("advanced", "উচ্চতর (Advanced)", "Advanced", Color(0xFFEF4444))
}

/**
 * 12 Comprehensive Mathematics Formula Categories.
 * Preserves ALGEBRA ("বীজগণিত"), GEOMETRY ("জ্যামিতি"), and CALCULUS ("ক্যালকুলাস") exact titles for backwards compatibility.
 */
enum class FormulaSubject(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String
) {
    ARITHMETIC(
        id = "arithmetic",
        titleBn = "পাটিগণিত",
        titleEn = "Arithmetic",
        descriptionBn = "গড়, ঐকিক নিয়ম, ল.সা.গু, গ.সা.গু, অনুপাত ও সমানুপাত এবং কাজ-সময়-গতিবেগের সূত্র"
    ),
    NUMBER_THEORY(
        id = "number_theory",
        titleBn = "সংখ্যাতত্ত্ব",
        titleEn = "Number Theory",
        descriptionBn = "স্বাভাবিক সংখ্যা, মৌলিক উৎপাদক, ভাজক সংখ্যা, বিভাজ্যতা ও ইউক্লিডীয় ভাগ প্রক্রিয়া"
    ),
    ALGEBRA(
        id = "algebra",
        titleBn = "বীজগণিত",
        titleEn = "Algebra",
        descriptionBn = "বর্গ ও ঘনের অভেদ, সূচক, লগারিদম, দ্বিঘাত সমীকরণ এবং অসমতার পূর্ণাঙ্গ সূত্রাবলী"
    ),
    GEOMETRY(
        id = "geometry",
        titleBn = "জ্যামিতি",
        titleEn = "Geometry",
        descriptionBn = "রেখা, কোণ, ত্রিভুজ, চতুর্ভুজ, বৃত্ত, বহুভুজ এবং পিথাগোরাসের জ্যামিতিক উপপাদ্য ও সূত্র"
    ),
    MENSURATION(
        id = "mensuration",
        titleBn = "পরিমিতি",
        titleEn = "Mensuration",
        descriptionBn = "দ্বিমাত্রিক ও ত্রিমাত্রিক বস্তুর ক্ষেত্রফল, পরিসীমা, সমগ্রতল ও আয়তন নির্ণয়ের সূত্র"
    ),
    TRIGONOMETRY(
        id = "trigonometry",
        titleBn = "ত্রিকোণমিতি",
        titleEn = "Trigonometry",
        descriptionBn = "ত্রিকোণমিতিক অনুপাত, মৌলিক অভেদাবলী, আদর্শ কোণের মান এবং উচ্চতা ও দূরত্বের সূত্র"
    ),
    COORDINATE_GEOMETRY(
        id = "coordinate_geometry",
        titleBn = "স্থানাঙ্ক জ্যামিতি",
        titleEn = "Coordinate Geometry",
        descriptionBn = "বিন্দুদ্বয়ের দূরত্ব, বিভক্তিকরণ, ত্রিভুজের ক্ষেত্রফল এবং সরলরেখার ঢাল ও সমীকরণ"
    ),
    STATISTICS(
        id = "statistics",
        titleBn = "পরিসংখ্যান",
        titleEn = "Statistics",
        descriptionBn = "গাণিতিক গড়, মধ্যক, প্রচুরক, পরিসর, ভেদাঙ্ক ও পরিমিত ব্যবধান নির্ণয়ের সূত্র"
    ),
    PROBABILITY(
        id = "probability",
        titleBn = "সম্ভাব্যতা",
        titleEn = "Probability",
        descriptionBn = "সম্ভাব্যতার মৌলিক সূত্র, পূরক ঘটনা, বর্জনশীল ও স্বাধীন ঘটনা এবং বিন্যাস-সমাবেশ"
    ),
    SEQUENCES_SERIES(
        id = "sequences_series",
        titleBn = "অনুক্রম ও ধারা",
        titleEn = "Sequences and Series",
        descriptionBn = "সমান্তর ধারা, গুণোত্তর ধারা, অসীম ধারা এবং স্বাভাবিক সংখ্যার ঘাতের সমষ্টি"
    ),
    COMMERCIAL_MATH(
        id = "commercial_math",
        titleBn = "বাণিজ্যিক গণিত",
        titleEn = "Commercial Mathematics",
        descriptionBn = "শতকরা, লাভ-ক্ষতি, কমিশন ও বাট্টা, সরল মুনাফা এবং চক্রবৃদ্ধি মুনাফার সূত্র"
    ),
    CALCULUS(
        id = "calculus",
        titleBn = "ক্যালকুলাস",
        titleEn = "Calculus & Advanced Mathematics",
        descriptionBn = "ফাংশনের সীমা (Limits), অন্তরীকরণ (Derivatives), সমাকলন (Integrals) ও উচ্চতর গণিত"
    );

    val primaryColor: Color
        get() = when (this) {
            ARITHMETIC -> Color(0xFF2563EB)
            NUMBER_THEORY -> Color(0xFF0D9488)
            ALGEBRA -> Color(0xFF7C3AED)
            GEOMETRY -> Color(0xFF059669)
            MENSURATION -> Color(0xFFD97706)
            TRIGONOMETRY -> Color(0xFFE11D48)
            COORDINATE_GEOMETRY -> Color(0xFF0891B2)
            STATISTICS -> Color(0xFF4F46E5)
            PROBABILITY -> Color(0xFF9333EA)
            SEQUENCES_SERIES -> Color(0xFFEA580C)
            COMMERCIAL_MATH -> Color(0xFF16A34A)
            CALCULUS -> Color(0xFFDC2626)
        }
}

/**
 * Rich, structured mathematical formula representation with Unicode, LaTeX, plain-text,
 * domain conditions, derivation steps, difficulty level, and related formula links.
 */
data class LibraryFormula(
    val id: String,
    val subject: FormulaSubject,
    val subCategory: String,
    val titleBn: String,
    val titleEn: String,
    val formulaExpression: String,
    val explanationBn: String,
    val variables: List<Pair<String, String>>,
    val exampleProblemBn: String,
    val exampleSolutionBn: String,
    val tipsBn: String = "",
    val tags: List<String> = emptyList(),
    val latexExpression: String = formulaExpression,
    val plainTextExpression: String = formulaExpression,
    val conditionsBn: String = "সকল বাস্তব সংখ্যার ডোমেনে প্রযোজ্য (হর ≠ ০)।",
    val derivationStepsBn: List<String> = emptyList(),
    val difficultyLevel: FormulaDifficulty = FormulaDifficulty.BASIC,
    val relatedFormulaIds: List<String> = emptyList(),
    val curriculumTracks: Set<CurriculumTrack> = FormulaLibraryProvider.inferDefaultTracks(subject, difficultyLevel, id)
)

object FormulaLibraryProvider {

    fun inferDefaultTracks(
        subject: FormulaSubject,
        difficulty: FormulaDifficulty,
        id: String
    ): Set<CurriculumTrack> {
        val tracks = mutableSetOf<CurriculumTrack>()
        if (subject != FormulaSubject.CALCULUS) {
            tracks.add(CurriculumTrack.NCTB)
        }
        if (subject in setOf(
                FormulaSubject.ARITHMETIC,
                FormulaSubject.NUMBER_THEORY,
                FormulaSubject.ALGEBRA,
                FormulaSubject.GEOMETRY,
                FormulaSubject.MENSURATION,
                FormulaSubject.PROBABILITY,
                FormulaSubject.SEQUENCES_SERIES,
                FormulaSubject.COMMERCIAL_MATH,
                FormulaSubject.STATISTICS
            )
        ) {
            tracks.add(CurriculumTrack.BCS)
        }
        if (difficulty != FormulaDifficulty.BASIC ||
            id.contains("bdmo") ||
            subject in setOf(
                FormulaSubject.NUMBER_THEORY,
                FormulaSubject.ALGEBRA,
                FormulaSubject.GEOMETRY,
                FormulaSubject.PROBABILITY,
                FormulaSubject.SEQUENCES_SERIES,
                FormulaSubject.CALCULUS
            )
        ) {
            tracks.add(CurriculumTrack.BDMO)
        }
        if (tracks.isEmpty()) {
            tracks.add(CurriculumTrack.NCTB)
        }
        return tracks
    }

    val allFormulas: List<LibraryFormula> by lazy {
        arithmeticFormulas +
            numberTheoryFormulas +
            algebraFormulas +
            geometryFormulas +
            mensurationFormulas +
            trigonometryFormulas +
            coordinateGeometryFormulas +
            statisticsFormulas +
            probabilityFormulas +
            sequencesSeriesFormulas +
            commercialMathFormulas +
            calculusFormulas
    }

    // -------------------------------------------------------------
    // 1. ARITHMETIC (পাটিগণিত)
    // -------------------------------------------------------------
    val arithmeticFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_arith_01",
            subject = FormulaSubject.ARITHMETIC,
            subCategory = "গড় ও সমষ্টি",
            titleBn = "পাটিগণিতীয় গড় ও সমষ্টি নির্ণয়",
            titleEn = "Arithmetic Mean & Sum",
            formulaExpression = "গড় = রাশিগুলোর যোগফল ÷ রাশিগুলোর সংখ্যা  |  যোগফল = গড় × রাশির সংখ্যা",
            latexExpression = "\\bar{x} = \\frac{\\sum_{i=1}^{n} x_i}{n}",
            plainTextExpression = "Average = Sum of terms / Number of terms",
            explanationBn = "একই জাতীয় একাধিক রাশির যোগফলকে রাশিগুলোর মোট সংখ্যা দ্বারা ভাগ করলে তাদের গড় মান পাওয়া যায়।",
            variables = listOf(
                "∑xᵢ" to "রাশিগুলোর মোট সমষ্টি",
                "n" to "রাশিগুলোর মোট সংখ্যা (n > 0)"
            ),
            conditionsBn = "রাশির সংখ্যা n অবশ্যই ধনাত্মক পূর্ণসংখ্যা হতে হবে (n ≥ 1)।",
            derivationStepsBn = listOf(
                "মনে করি n সংখ্যক রাশির মান x₁, x₂, ..., xₙ",
                "মোট সমষ্টি S = x₁ + x₂ + ... + xₙ",
                "সুষম বণ্টন অনুযায়ী গড় = S / n"
            ),
            exampleProblemBn = "১২, ১৮, ২৪, ৩০ এবং ৩৬ এর গড় কত?",
            exampleSolutionBn = "যোগফল = ১২ + ১৮ + ২৪ + ৩০ + ৩৬ = ১২০\nরাশির সংখ্যা = ৫\nগড় = ১২০ ÷ ৫ = ২৪",
            tipsBn = "সমপার্থক্যবিশিষ্ট ক্রমিক সংখ্যার ক্ষেত্রে: গড় = (প্রথম সংখ্যা + শেষ সংখ্যা) ÷ ২।",
            tags = listOf("গড়", "পাটিগণিত", "average", "mean", "sum"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_stat_01", "lib_arith_02")
        ),
        LibraryFormula(
            id = "lib_arith_02",
            subject = FormulaSubject.ARITHMETIC,
            subCategory = "গতিবেগ, দূরত্ব ও সময়",
            titleBn = "গতিবেগ, দূরত্ব ও সময়ের মৌলিক সম্পর্ক",
            titleEn = "Speed, Distance & Time Formula",
            formulaExpression = "দূরত্ব (D) = গতিবেগ (S) × সময় (T)  |  S = D / T  |  T = D / S",
            latexExpression = "D = S \\times T, \\quad S = \\frac{D}{T}, \\quad T = \\frac{D}{S}",
            plainTextExpression = "Distance = Speed * Time",
            explanationBn = "একক সময়ে কোনো গতিশীল বস্তু যে দূরত্ব অতিক্রম করে তাকে গতিবেগ বলে। একক রূপান্তর: ১ কিমি/ঘণ্টা = ৫/১৮ মিটার/সেকেন্ড।",
            variables = listOf(
                "D" to "অতিক্রান্ত দূরত্ব (Distance)",
                "S" to "গতিবেগ (Speed)",
                "T" to "প্রয়োজনীয় সময় (Time > 0)"
            ),
            conditionsBn = "সময় T > 0 এবং গতিবেগ ও দূরত্বের একক সামঞ্জস্যপূর্ণ হতে হবে।",
            derivationStepsBn = listOf(
                "সংজ্ঞানুসারে ১ একক সময়ে অতিক্রান্ত দূরত্ব = S",
                "অতএব T একক সময়ে অতিক্রান্ত দূরত্ব D = S × T"
            ),
            exampleProblemBn = "একটি ট্রেন ঘণ্টায় ৭২ কিমি বেগে চললে ১০ সেকেন্ডে কত মিটার যাবে?",
            exampleSolutionBn = "গতিবেগ S = ৭২ × (৫/১৮) মি/সে = ২০ মি/সে\nদূরত্ব D = ২০ × ১০ = ২০০ মিটার",
            tipsBn = "যাতায়াতের গড় গতিবেগ (সমদূরত্বে x ও y বেগে): 2xy / (x + y)।",
            tags = listOf("গতিবেগ", "দূরত্ব", "সময়", "speed", "distance", "time"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_arith_03")
        ),
        LibraryFormula(
            id = "lib_arith_03",
            subject = FormulaSubject.ARITHMETIC,
            subCategory = "সময় ও কাজ",
            titleBn = "যৌথ কাজ ও নল-চৌবাচ্চার সূত্র",
            titleEn = "Time & Work Combined Rate Formula",
            formulaExpression = "যৌথ সময় T = (A × B) / (A + B)  |  খালি করার নলসহ: T = (A × B) / (B − A)",
            latexExpression = "T_{together} = \\frac{A \\cdot B}{A + B}",
            plainTextExpression = "T = (A * B) / (A + B)",
            explanationBn = "দুইজন ব্যক্তি পৃথকভাবে একটি কাজ যথাক্রমে A ও B দিনে করতে পারলে তারা একত্রে কাজটি (AB)/(A+B) দিনে সম্পন্ন করবে।",
            variables = listOf(
                "A" to "প্রথম ব্যক্তির একা কাজের সময়",
                "B" to "দ্বিতীয় ব্যক্তির একা কাজের সময়"
            ),
            conditionsBn = "A > 0, B > 0 এবং চৌবাচ্চা পূর্ণ হওয়ার জন্য B > A হতে হবে।",
            derivationStepsBn = listOf(
                "১ দিনে প্রথমজন করে ১/A অংশ এবং দ্বিতীয়জন করে ১/B অংশ",
                "১ দিনে দুজনে একত্রে করে = ১/A + ১/B = (A + B) / (AB) অংশ",
                "সম্পূর্ণ ১ অংশ করতে সময় = (AB) / (A + B) দিন"
            ),
            exampleProblemBn = "ক একটি কাজ ১০ দিনে এবং খ ১৫ দিনে করতে পারে। তারা একত্রে কত দিনে শেষ করবে?",
            exampleSolutionBn = "সময় = (১০ × ১৫) ÷ (১০ + ১৫) = ১৫০ ÷ ২৫ = ৬ দিন",
            tipsBn = "তিনজনের ক্ষেত্রে একত্রে সময় = (ABC) / (AB + BC + CA)।",
            tags = listOf("সময় ও কাজ", "চৌবাচ্চা", "time and work", "pipe cistern"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_arith_02")
        )
    )

    // -------------------------------------------------------------
    // 2. NUMBER THEORY (সংখ্যাতত্ত্ব)
    // -------------------------------------------------------------
    val numberTheoryFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_nt_01",
            subject = FormulaSubject.NUMBER_THEORY,
            subCategory = "গ.সা.গু ও ল.সা.গু",
            titleBn = "গ.সা.গু ও ল.সা.গু-এর মৌলিক সম্পর্ক",
            titleEn = "GCD and LCM Fundamental Identity",
            formulaExpression = "দুটি সংখ্যার গুণফল (a × b) = গ.সা.গু (GCD) × ল.সা.গু (LCM)",
            latexExpression = "a \\times b = \\gcd(a, b) \\times \\text{lcm}(a, b)",
            plainTextExpression = "a * b = GCD(a, b) * LCM(a, b)",
            explanationBn = "যেকোনো দুটি ধনাত্মক পূর্ণসংখ্যার গুণফল তাদের গরিষ্ঠ সাধারণ গুণনীয়ক (গ.সা.গু) এবং লঘিষ্ঠ সাধারণ গুণিতক (ল.সা.গু)-এর গুণফলের সমান।",
            variables = listOf(
                "a, b" to "দুটি ধনাত্মক পূর্ণসংখ্যা",
                "GCD" to "গরিষ্ঠ সাধারণ গুণনীয়ক (HCF)",
                "LCM" to "লঘিষ্ঠ সাধারণ গুণিতক"
            ),
            conditionsBn = "কেবলমাত্র দুটি ধনাত্মক পূর্ণসংখ্যার (a > 0, b > 0) ক্ষেত্রে সরাসরি প্রযোজ্য।",
            derivationStepsBn = listOf(
                "ধরি গ.সা.গু = g, তাহলে a = g·x এবং b = g·y যেখানে gcd(x, y) = 1",
                "অতএব ল.সা.গু = g·x·y",
                "গ.সা.গু × ল.সা.গু = g × (g·x·y) = (g·x) × (g·y) = a × b"
            ),
            exampleProblemBn = "দুটি সংখ্যার গ.সা.গু ১২ এবং ল.সা.গু ৭২। একটি সংখ্যা ২৪ হলে অপরটি কত?",
            exampleSolutionBn = "অপর সংখ্যা = (১২ × ৭২) ÷ ২৪ = ১২ × ৩ = ৩৬",
            tipsBn = "ভগ্নাংশের গ.সা.গু = (লবগুলোর গ.সা.গু) ÷ (হরগুলোর ল.সা.গু)।",
            tags = listOf("গসাগু", "লসাগু", "gcd", "lcm", "hcf", "number theory"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_nt_02", "lib_nt_03")
        ),
        LibraryFormula(
            id = "lib_nt_02",
            subject = FormulaSubject.NUMBER_THEORY,
            subCategory = "ভাজক ও উৎপাদক",
            titleBn = "মোট গুণনীয়ক (ভাজক) সংখ্যা নির্ণয়ের সূত্র",
            titleEn = "Number of Divisors Formula",
            formulaExpression = "N = p₁ᵃ · p₂ᵇ · p₃ᶜ হলে মোট ভাজক সংখ্যা d(N) = (a + 1)(b + 1)(c + 1)",
            latexExpression = "d(N) = \\prod_{i=1}^{k} (a_i + 1)",
            plainTextExpression = "d(N) = (a + 1)(b + 1)(c + 1)",
            explanationBn = "কোনো সংখ্যাকে মৌলিক উৎপাদকের ঘাত আকারে প্রকাশ করার পর প্রত্যেকটি মৌলিক উৎপাদকের ঘাতের সাথে ১ যোগ করে তাদের গুণ করলে মোট ভাজক সংখ্যা পাওয়া যায়।",
            variables = listOf(
                "p₁, p₂, p₃" to "ভিন্ন ভিন্ন মৌলিক উৎপাদক",
                "a, b, c" to "মৌলিক উৎপাদকগুলোর সূচক বা ঘাত"
            ),
            conditionsBn = "N > 1 একটি ধনাত্মক পূর্ণসংখ্যা।",
            derivationStepsBn = listOf(
                "N এর যেকোনো ভাজকে p₁ এর ঘাত হতে পারে 0, 1, ..., a (মোট a+1 টি উপায়)",
                "একইভাবে p₂ এর ঘাত নির্বাচনের উপায় (b+1) টি",
                "গুণন বিধি অনুযায়ী মোট ভাজক = (a+1)(b+1)..."
            ),
            exampleProblemBn = "৭২ সংখ্যাটির মোট ভাজক বা গুণনীয়ক কয়টি?",
            exampleSolutionBn = "৭২ = ৮ × ৯ = ২³ × ৩²\nভাজক সংখ্যা = (৩ + ১) × (২ + ১) = ৪ × ৩ = ১২টি",
            tipsBn = "পূর্ণবর্গ সংখ্যার মোট ভাজক সংখ্যা সর্বদা বিজোড় হয়।",
            tags = listOf("ভাজক", "গুণনীয়ক", "divisors", "factors", "prime factorization"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_nt_01", "lib_nt_03")
        ),
        LibraryFormula(
            id = "lib_nt_03",
            subject = FormulaSubject.NUMBER_THEORY,
            subCategory = "ইউক্লিডীয় ভাগ প্রক্রিয়া",
            titleBn = "ইউক্লিডের ভাগ অ্যালগরিদম (ভাজ্য-ভাজক সূত্র)",
            titleEn = "Euclidean Division Lemma",
            formulaExpression = "ভাজ্য (a) = ভাজক (b) × ভাগফল (q) + ভাগশেষ (r),  যেখানে 0 ≤ r < |b|",
            latexExpression = "a = bq + r, \\quad 0 \\le r < |b|",
            plainTextExpression = "a = b * q + r (0 <= r < |b|)",
            explanationBn = "যেকোনো পূর্ণসংখ্যা a কে অশূন্য পূর্ণসংখ্যা b দ্বারা ভাগ করলে অনন্য ভাগফল q এবং ভাগশেষ r পাওয়া যায় যেখানে ভাগশেষ সর্বদা ভাজকের পরম মান অপেক্ষা ছোট।",
            variables = listOf(
                "a" to "ভাজ্য (Dividend)",
                "b" to "ভাজক (Divisor, b ≠ 0)",
                "q" to "ভাগফল (Quotient)",
                "r" to "ভাগশেষ (Remainder)"
            ),
            conditionsBn = "ভাজক b ≠ 0 এবং 0 ≤ r < |b|।",
            exampleProblemBn = "কোন সংখ্যাকে ১৫ দ্বারা ভাগ করলে ভাগফল ৮ এবং ভাগশেষ ৭ থাকে?",
            exampleSolutionBn = "ভাজ্য = ১৫ × ৮ + ৭ = ১২০ + ৭ = ১২৭",
            tipsBn = "ভাগশেষ r = 0 হলে a সংখ্যাটি b দ্বারা নিঃশেষে বিভাজ্য।",
            tags = listOf("ভাজ্য", "ভাজক", "ভাগশেষ", "division", "remainder", "euclidean"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_nt_01")
        ),
        LibraryFormula(
            id = "nt_bdmo_1",
            subject = FormulaSubject.NUMBER_THEORY,
            subCategory = "ভাজক ও সমষ্টি (BdMO)",
            titleBn = "ভাজকের সংখ্যা d(N) ও ভাজকের সমষ্টি σ(N)",
            titleEn = "Divisor Count d(N) & Divisor Sum σ(N)",
            formulaExpression = "d(N) = ∏(aᵢ + 1),  σ(N) = ∏((pᵢ^(aᵢ+1) − 1) / (pᵢ − 1))",
            latexExpression = "\\sigma(N) = \\prod_{i=1}^{k} \\frac{p_i^{a_i+1}-1}{p_i-1}",
            plainTextExpression = "d(N) = prod(a_i + 1), sigma(N) = prod((p_i^(a_i+1) - 1)/(p_i - 1))",
            explanationBn = "N = p₁^a₁ · p₂^a₂... হলে মোট ভাজক সংখ্যা d(N) এবং সকল ধনাত্মক ভাজকের যোগফল σ(N) গুণোত্তর ধারার সূত্র দিয়ে নির্ণয় করা হয়।",
            variables = listOf(
                "N" to "ধনাত্মক পূর্ণসংখ্যা (N > 1)",
                "pᵢ" to "মৌলিক উৎপাদক",
                "aᵢ" to "মৌলিক উৎপাদকের ঘাত"
            ),
            conditionsBn = "N ≥ 2 পূর্ণসংখ্যা এবং pᵢ ভিন্ন ভিন্ন মৌলিক সংখ্যা।",
            exampleProblemBn = "৩৬ এর মোট ভাজক কয়টি এবং ভাজকগুলোর সমষ্টি কত?",
            exampleSolutionBn = "৩৬ = ২² × ৩²। d(৩৬) = (২+১)(২+১) = ৯টি। σ(৩৬) = ((২³−১)/(২−১)) × ((৩³−১)/(৩−১)) = ৭ × ১৩ = ৯১।",
            tipsBn = "BdMO ও BCS এ ভাজকের সমষ্টি নির্ণয়ে এই গুণোত্তর সূত্রটি সবচেয়ে দ্রুত কাজ করে।",
            tags = listOf("ভাজক", "সমষ্টি", "divisor sum", "sigma", "bdmo"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            curriculumTracks = setOf(CurriculumTrack.BDMO, CurriculumTrack.BCS)
        ),
        LibraryFormula(
            id = "nt_bdmo_2",
            subject = FormulaSubject.NUMBER_THEORY,
            subCategory = "অয়লারের টশেন্ট ও সহমৌলিকতা",
            titleBn = "অয়লারের টশেন্ট ফাংশন φ(N)",
            titleEn = "Euler's Totient Function φ(N)",
            formulaExpression = "φ(N) = N × ∏(1 − 1/pᵢ)",
            latexExpression = "\\phi(N) = N \\prod_{p | N} \\left(1 - \\frac{1}{p}\\right)",
            plainTextExpression = "phi(N) = N * prod(1 - 1/p)",
            explanationBn = "১ থেকে N পর্যন্ত যতগুলো সংখ্যা N এর সাথে সহমৌলিক ( অর্থাৎ যাদের সাথে N এর গ.সা.গু ১), তাদের সংখ্যাকে φ(N) বলা হয়।",
            variables = listOf(
                "N" to "ধনাত্মক পূর্ণসংখ্যা",
                "pᵢ" to "N এর মৌলিক উৎপাদকসমূহ"
            ),
            conditionsBn = "N ≥ 1 একটি ধনাত্মক পূর্ণসংখ্যা। মৌলিক সংখ্যা p এর জন্য φ(p) = p − 1।",
            exampleProblemBn = "৩৬ এর চেয়ে ছোট বা সমান কয়টি ধনাত্মক পূর্ণসংখ্যা ৩৬ এর সাথে সহমৌলিক?",
            exampleSolutionBn = "৩৬ = ২² × ৩²। φ(৩৬) = ৩৬ × (১ − ১/২) × (১ − ১/৩) = ৩৬ × (১/২) × (২/৩) = ১২টি।",
            tipsBn = "বড় ঘাতের ভাগশেষ নির্ণয়ে (অয়লারের উপপাদ্য: a^φ(m) ≡ 1 mod m) এটি অপরিহার্য।",
            tags = listOf("টশেন্ট", "সহমৌলিক", "totient", "euler", "coprime", "bdmo"),
            difficultyLevel = FormulaDifficulty.ADVANCED,
            curriculumTracks = setOf(CurriculumTrack.BDMO)
        ),
        LibraryFormula(
            id = "nt_bdmo_3",
            subject = FormulaSubject.NUMBER_THEORY,
            subCategory = "মডুলার পাটিগণিত (Modular Arithmetic)",
            titleBn = "ফার্মার ক্ষুদ্র উপপাদ্য (Fermat's Little Theorem)",
            titleEn = "Fermat's Little Theorem",
            formulaExpression = "a^(p−1) ≡ 1 (mod p), যখন p মৌলিক ও gcd(a, p) = 1",
            latexExpression = "a^{p-1} \\equiv 1 \\pmod{p}",
            plainTextExpression = "a^(p-1) = 1 (mod p)",
            explanationBn = "p একটি মৌলিক সংখ্যা হলে এবং a সংখ্যাটি p দ্বারা বিভাজ্য না হলে, a^(p−1) কে p দ্বারা ভাগ করলে সর্বদা ভাগশেষ ১ থাকে।",
            variables = listOf(
                "a" to "যেকোনো পূর্ণসংখ্যা (ভিত্তি)",
                "p" to "মৌলিক ভাজক (Prime Modulus)"
            ),
            conditionsBn = "p মৌলিক সংখ্যা এবং p দ্বারা a নিঃশেষে বিভাজ্য নয়।",
            exampleProblemBn = "২^১০০ কে ১০১ দ্বারা ভাগ করলে ভাগশেষ কত থাকবে?",
            exampleSolutionBn = "যেহেতু ১০১ মৌলিক এবং gcd(২, ১০১) = ১, তাই ফার্মার উপপাদ্য মতে ২^(১০১−১) = ২^১০০ ≡ ১ (mod ১০১)। ভাগশেষ = ১।",
            tipsBn = "অলিম্পিয়াডে বিশাল সূচকের ভাগশেষ ১ সেকেন্ডে বের করার প্রধান হাতিয়ার।",
            tags = listOf("ফার্মা", "মডুলার", "ভাগশেষ", "fermat", "modular", "bdmo"),
            difficultyLevel = FormulaDifficulty.ADVANCED,
            curriculumTracks = setOf(CurriculumTrack.BDMO)
        ),
        LibraryFormula(
            id = "nt_bdmo_4",
            subject = FormulaSubject.NUMBER_THEORY,
            subCategory = "ফ্যাক্টরিয়াল ও শূন্যের সংখ্যা",
            titleBn = "লেজেন্ডারের সূত্র ও n! এর শেষে শূন্যের সংখ্যা",
            titleEn = "Legendre's Formula & Trailing Zeros in n!",
            formulaExpression = "E_p(n!) = ⌊n/p⌋ + ⌊n/p²⌋ + ⌊n/p³⌋ + ...",
            latexExpression = "E_p(n!) = \\sum_{k=1}^{\\infty} \\left\\lfloor \\frac{n}{p^k} \\right\\rfloor",
            plainTextExpression = "E_p(n!) = floor(n/p) + floor(n/p^2) + floor(n/p^3) + ...",
            explanationBn = "n! (ফ্যাক্টরিয়াল) এ মৌলিক সংখ্যা p এর সর্বোচ্চ ঘাত নির্ণয়ের সূত্র। p = 5 বসালে n! এর শেষে মোট কয়টি শূন্য (Trailing Zeros) আছে তা পাওয়া যায়।",
            variables = listOf(
                "n" to "ফ্যাক্টরিয়ালের সংখ্যা (n!)",
                "p" to "মৌলিক উৎপাদক (শূন্যের সংখ্যার জন্য p = 5)"
            ),
            conditionsBn = "n ≥ 1 পূর্ণসংখ্যা এবং p মৌলিক সংখ্যা।",
            exampleProblemBn = "১০০! (১০০ ফ্যাক্টরিয়াল) এর শেষে কয়টি শূন্য আছে?",
            exampleSolutionBn = "শূন্যের সংখ্যা = ⌊১০০/৫⌋ + ⌊১০০/২৫⌋ = ২০ + ৪ = ২৪টি।",
            tipsBn = "বিসিএস ও অলিম্পিয়াডে ১০০!, ২০০! বা ১০০০! এর শেষে শূন্যের সংখ্যা নির্ণয়ে p = ৫ ব্যবহার করুন।",
            tags = listOf("ফ্যাক্টরিয়াল", "শূন্য", "লেজেন্ডার", "factorial", "trailing zeros", "bdmo", "bcs"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            curriculumTracks = setOf(CurriculumTrack.BDMO, CurriculumTrack.BCS)
        ),
        LibraryFormula(
            id = "nt_bdmo_5",
            subject = FormulaSubject.NUMBER_THEORY,
            subCategory = "মডুলার পাটিগণিত (Modular Arithmetic)",
            titleBn = "একক স্থানীয় অঙ্ক ও ঘাতের পর্যায়কাল (Cyclicity)",
            titleEn = "Unit Digit Cyclicity & Modular Reduction",
            formulaExpression = "a^b (mod 10) = (a mod 10)^((b − 1 mod 4) + 1) mod 10",
            latexExpression = "a^b \\pmod{10}",
            plainTextExpression = "a^b mod 10 using cycle of 4",
            explanationBn = "যেকোনো সংখ্যার ঘাতের একক স্থানীয় অঙ্ক প্রতি ৪ ঘাত পরপর পুনরাবৃত্তি করে। তাই সূচককে ৪ দিয়ে ভাগ করে একক স্থানীয় অঙ্ক নির্ণয় করা যায়।",
            variables = listOf(
                "a" to "ভিত্তি সংখ্যা (Base)",
                "b" to "ঘাত বা সূচক (Exponent, b ≥ 1)"
            ),
            conditionsBn = "b ≥ 1 ধনাত্মক পূর্ণসংখ্যা।",
            exampleProblemBn = "৭^২২২ সংখ্যাটির একক স্থানীয় অঙ্ক কত?",
            exampleSolutionBn = "২২২ কে ৪ দিয়ে ভাগ করলে ভাগশেষ ২ থাকে। তাই ৭^২২২ এর একক অঙ্ক = ৭² = ৪৯ এর একক অঙ্ক = ৯।",
            tipsBn = "০, ১, ৫, ৬ এর যেকোনো ধনাত্মক ঘাতের একক স্থানীয় অঙ্ক অপরিবর্তিত থাকে।",
            tags = listOf("একক অঙ্ক", "ঘাত", "unit digit", "cyclicity", "bcs", "bdmo"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            curriculumTracks = setOf(CurriculumTrack.NCTB, CurriculumTrack.BDMO, CurriculumTrack.BCS)
        )
    )

    // -------------------------------------------------------------
    // 3. ALGEBRA (বীজগণিত) — 17 Formulas
    // -------------------------------------------------------------
    val algebraFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_alg_01",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "বর্গের সূত্র ও অভেদ",
            titleBn = "দ্বিপদী রাশির যোগফলের বর্গ",
            titleEn = "Square of a Binomial Sum",
            formulaExpression = "(a + b)² = a² + 2ab + b² = (a − b)² + 4ab",
            latexExpression = "(a + b)^2 = a^2 + 2ab + b^2 = (a - b)^2 + 4ab",
            plainTextExpression = "(a + b)^2 = a^2 + 2ab + b^2",
            explanationBn = "দুটি রাশির যোগফলের বর্গ হলো প্রথম রাশির বর্গ, দ্বিতীয় রাশির বর্গ এবং রাশি দুটির গুণফলের দ্বিগুণের সমষ্টি।",
            variables = listOf(
                "a" to "প্রথম বীজগণিতীয় রাশি বা পদ",
                "b" to "দ্বিতীয় বীজগণিতীয় রাশি বা পদ",
                "2ab" to "উভয় পদের গুণফলের দ্বিগুণ"
            ),
            derivationStepsBn = listOf(
                "(a + b)² = (a + b)(a + b)",
                "= a(a + b) + b(a + b) = a² + ab + ba + b²",
                "= a² + 2ab + b²"
            ),
            exampleProblemBn = "যদি a + b = 7 এবং ab = 10 হয়, তবে a² + b² এর মান কত?",
            exampleSolutionBn = "আমরা জানি, a² + b² = (a + b)² − 2ab\n= (7)² − 2(10)\n= 49 − 20 = 29",
            tipsBn = "সংখ্যা বর্গের শর্টকাট: 103² = (100 + 3)² = 100² + 2·100·3 + 3² = 10000 + 600 + 9 = 10609।",
            tags = listOf("বর্গ", "অভেদ", "(a+b)^2", "square", "binomial"),
            relatedFormulaIds = listOf("lib_alg_02", "lib_alg_03", "lib_alg_04")
        ),
        LibraryFormula(
            id = "lib_alg_02",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "বর্গের সূত্র ও অভেদ",
            titleBn = "দ্বিপদী রাশির বিয়োগফলের বর্গ",
            titleEn = "Square of a Binomial Difference",
            formulaExpression = "(a − b)² = a² − 2ab + b² = (a + b)² − 4ab",
            latexExpression = "(a - b)^2 = a^2 - 2ab + b^2 = (a + b)^2 - 4ab",
            plainTextExpression = "(a - b)^2 = a^2 - 2ab + b^2",
            explanationBn = "দুটি রাশির বিয়োগফলের বর্গ নির্ণয়ে মধ্যপদ −2ab বিয়োগ হয়, কিন্তু উভয় প্রান্তীয় বর্গপদ সর্বদা ধনাত্মক (+a² ও +b²) থাকে।",
            variables = listOf(
                "a" to "প্রথম পদ",
                "b" to "দ্বিতীয় পদ"
            ),
            derivationStepsBn = listOf(
                "(a − b)² = (a − b)(a − b)",
                "= a(a − b) − b(a − b) = a² − ab − ba + b² = a² − 2ab + b²"
            ),
            exampleProblemBn = "যদি a + b = 9 এবং ab = 20 হয়, তবে (a − b)² এর মান কত?",
            exampleSolutionBn = "(a − b)² = (a + b)² − 4ab\n= 9² − 4(20)\n= 81 − 80 = 1",
            tipsBn = "লক্ষ্য করুন: (−b)² সবসময় +b² হয়, কখনোই −b² লিখবেন না।",
            tags = listOf("বর্গ", "বিয়োগফল", "(a-b)^2", "difference"),
            relatedFormulaIds = listOf("lib_alg_01", "lib_alg_03", "lib_alg_04")
        ),
        LibraryFormula(
            id = "lib_alg_03",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "বর্গের সূত্র ও অভেদ",
            titleBn = "দুটি বর্গের অন্তর (উৎপাদক সূত্র)",
            titleEn = "Difference of Two Squares",
            formulaExpression = "a² − b² = (a + b)(a − b)",
            latexExpression = "a^2 - b^2 = (a + b)(a - b)",
            plainTextExpression = "a^2 - b^2 = (a + b)(a - b)",
            explanationBn = "দুটি রাশির বর্গের বিয়োগফল রাশি দুটির যোগফল এবং বিয়োগফলের গুণফলের সমান। এটি উৎপাদকে বিশ্লেষণের অন্যতম প্রধান সূত্র।",
            variables = listOf(
                "a²" to "প্রথম পূর্ণবর্গ রাশি",
                "b²" to "দ্বিতীয় পূর্ণবর্গ রাশি"
            ),
            derivationStepsBn = listOf(
                "(a + b)(a − b) = a² − ab + ab − b² = a² − b²"
            ),
            exampleProblemBn = "মান নির্ণয় করো: 98² − 2²",
            exampleSolutionBn = "98² − 2² = (98 + 2)(98 − 2)\n= 100 × 96 = 9600",
            tipsBn = "দ্রুত মৌখিক গুণন: 53 × 47 = (50 + 3)(50 − 3) = 50² − 3² = 2500 − 9 = 2491।",
            tags = listOf("বর্গের অন্তর", "উৎপাদক", "a^2-b^2", "factorization"),
            relatedFormulaIds = listOf("lib_alg_01", "lib_alg_02")
        ),
        LibraryFormula(
            id = "lib_alg_04",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "বর্গের সূত্র ও অভেদ",
            titleBn = "4ab এবং ab নির্ণয়ের অনুসিদ্ধান্ত",
            titleEn = "4ab and Product Identities",
            formulaExpression = "4ab = (a + b)² − (a − b)²   এবং   2(a² + b²) = (a + b)² + (a − b)²",
            latexExpression = "4ab = (a + b)^2 - (a - b)^2",
            plainTextExpression = "4ab = (a + b)^2 - (a - b)^2",
            explanationBn = "(a+b) এবং (a−b) এর মান দেওয়া থাকলে সরাসরি গুণফল ab কিংবা বর্গের সমষ্টি (a²+b²) বের করতে এই অনুসিদ্ধান্ত জোড়া ব্যবহৃত হয়।",
            variables = listOf(
                "a + b" to "রাশিদ্বয়ের সমষ্টি",
                "a − b" to "রাশিদ্বয়ের অন্তর"
            ),
            exampleProblemBn = "a + b = 8 এবং a − b = 4 হলে ab এর মান কত?",
            exampleSolutionBn = "4ab = (a + b)² − (a − b)²\n4ab = 8² − 4² = 64 − 16 = 48\nab = 48 ÷ 4 = 12",
            tipsBn = "সরাসরি মনে রাখুন: ab = [(a+b)/2]² − [(a−b)/2]²।",
            tags = listOf("অনুসিদ্ধান্ত", "4ab", "2(a^2+b^2)", "corollary"),
            relatedFormulaIds = listOf("lib_alg_01", "lib_alg_02")
        ),
        LibraryFormula(
            id = "lib_alg_05",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "বর্গের সূত্র ও অভেদ",
            titleBn = "ত্রিপদী রাশির বর্গের সূত্র",
            titleEn = "Square of a Trinomial",
            formulaExpression = "(a + b + c)² = a² + b² + c² + 2(ab + bc + ca)",
            latexExpression = "(a + b + c)^2 = a^2 + b^2 + c^2 + 2(ab + bc + ca)",
            plainTextExpression = "(a + b + c)^2 = a^2 + b^2 + c^2 + 2(ab + bc + ca)",
            explanationBn = "তিনটি পদের সমষ্টির বর্গ প্রতিটি পদের বর্গের সমষ্টি এবং দুইটি করে পদের গুণফলের দ্বিগুণের সমষ্টির সমান।",
            variables = listOf(
                "a, b, c" to "ত্রিপদী রাশির তিনটি পদ",
                "ab + bc + ca" to "জোড়ায় জোড়ায় গুণফলের সমষ্টি"
            ),
            exampleProblemBn = "a + b + c = 9 এবং ab + bc + ca = 26 হলে a² + b² + c² এর মান কত?",
            exampleSolutionBn = "a² + b² + c² = (a + b + c)² − 2(ab + bc + ca)\n= 9² − 2(26)\n= 81 − 52 = 29",
            tipsBn = "যেকোনো দুটি রাশির মান জানা থাকলে তৃতীয় রাশিটি এই একটি সূত্র থেকেই বের করা যায়।",
            tags = listOf("ত্রিপদী", "(a+b+c)^2", "trinomial"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE
        ),
        LibraryFormula(
            id = "lib_alg_06",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "ঘনের সূত্র ও অভেদ",
            titleBn = "দ্বিপদী রাশির ঘনের সূত্র (যোগ ও বিয়োগ)",
            titleEn = "Cube of a Binomial",
            formulaExpression = "(a + b)³ = a³ + b³ + 3ab(a + b)  |  (a − b)³ = a³ − b³ − 3ab(a − b)",
            latexExpression = "(a \\pm b)^3 = a^3 \\pm b^3 \\pm 3ab(a \\pm b)",
            plainTextExpression = "(a + b)^3 = a^3 + b^3 + 3ab(a + b)",
            explanationBn = "ঘনের মান নির্ণয় এবং অভেদ প্রমাণে সংক্ষিপ্ত রূপ a³ + b³ + 3ab(a + b) সবচেয়ে বেশি কার্যকর।",
            variables = listOf(
                "a, b" to "দ্বিপদী রাশির পদদ্বয়"
            ),
            exampleProblemBn = "যদি a + b = 5 এবং ab = 6 হয়, তবে a³ + b³ এর মান কত?",
            exampleSolutionBn = "অনুসিদ্ধান্ত: a³ + b³ = (a + b)³ − 3ab(a + b)\n= 5³ − 3(6)(5)\n= 125 − 90 = 35",
            tipsBn = "মান নির্ণয়ে সবসময় 3ab(a+b) বিশিষ্ট অনুসিদ্ধান্ত রূপটি ব্যবহার করবেন।",
            tags = listOf("ঘন", "কিউব", "(a+b)^3", "(a-b)^3", "cube"),
            relatedFormulaIds = listOf("lib_alg_07", "lib_alg_08")
        ),
        LibraryFormula(
            id = "lib_alg_07",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "ঘনের সূত্র ও অভেদ",
            titleBn = "দুটি ঘনের সমষ্টি ও অন্তরের উৎপাদক সূত্র",
            titleEn = "Sum & Difference of Two Cubes (Factoring)",
            formulaExpression = "a³ + b³ = (a + b)(a² − ab + b²)  |  a³ − b³ = (a − b)(a² + ab + b²)",
            latexExpression = "a^3 \\pm b^3 = (a \\pm b)(a^2 \\mp ab + b^2)",
            plainTextExpression = "a^3 + b^3 = (a + b)(a^2 - ab + b^2)",
            explanationBn = "বীজগণিতীয় ভগ্নাংশের লঘিষ্ঠকরণ ও উৎপাদক বিশ্লেষণে এই সূত্র দুটি অপরিহার্য।",
            variables = listOf(
                "a³ + b³" to "দ্বিতীয় বন্ধনীতে −ab থাকে",
                "a³ − b³" to "দ্বিতীয় বন্ধনীতে +ab থাকে"
            ),
            exampleProblemBn = "উৎপাদকে বিশ্লেষণ করো: x³ − 27",
            exampleSolutionBn = "x³ − 27 = x³ − 3³\n= (x − 3)(x² + 3x + 3²)\n= (x − 3)(x² + 3x + 9)",
            tipsBn = "চিহ্ন মনে রাখার কৌশল (SOAP): Same sign, Opposite sign, Always Positive।",
            tags = listOf("ঘনের উৎপাদক", "a^3+b^3", "a^3-b^3", "SOAP"),
            relatedFormulaIds = listOf("lib_alg_06", "lib_alg_08")
        ),
        LibraryFormula(
            id = "lib_alg_08",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "ঘনের সূত্র ও অভেদ",
            titleBn = "তিনটি পদের ঘনের বিশেষ অভেদ",
            titleEn = "Gauss's Cubic Identity",
            formulaExpression = "a³ + b³ + c³ − 3abc = (a + b + c)(a² + b² + c² − ab − bc − ca)",
            latexExpression = "a^3 + b^3 + c^3 - 3abc = (a+b+c)(a^2+b^2+c^2-ab-bc-ca)",
            plainTextExpression = "a^3 + b^3 + c^3 - 3abc = (a + b + c)(a^2 + b^2 + c^2 - ab - bc - ca)",
            explanationBn = "যদি a + b + c = 0 হয়, তবে ডানপক্ষ শূন্য হয়ে যায় এবং সরাসরি a³ + b³ + c³ = 3abc পাওয়া যায়।",
            variables = listOf(
                "a, b, c" to "তিনটি বীজগণিতীয় রাশি"
            ),
            conditionsBn = "a + b + c = 0 হলে a³ + b³ + c³ = 3abc।",
            exampleProblemBn = "যদি x + y + z = 0 হয়, তবে x³ + y³ + z³ এর মান কত?",
            exampleSolutionBn = "যেহেতু x + y + z = 0,\nসুতরাং x³ + y³ + z³ − 3xyz = 0\nঅতএব, x³ + y³ + z³ = 3xyz",
            tipsBn = "বিকল্প রূপ: ½(a + b + c)[(a − b)² + (b − c)² + (c − a)²]।",
            tags = listOf("3abc", "a^3+b^3+c^3", "cubic identity"),
            difficultyLevel = FormulaDifficulty.ADVANCED
        ),
        LibraryFormula(
            id = "lib_alg_09",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "সূচক ও ঘাত (Exponents)",
            titleBn = "সূচকের মৌলিক গুণ ও ভাগের নিয়ম",
            titleEn = "Product & Quotient Laws of Exponents",
            formulaExpression = "aᵐ · aⁿ = aᵐ⁺ⁿ   এবং   aᵐ ÷ aⁿ = aᵐ⁻ⁿ  (a ≠ 0)",
            latexExpression = "a^m \\cdot a^n = a^{m+n}, \\quad \\frac{a^m}{a^n} = a^{m-n}",
            plainTextExpression = "a^m * a^n = a^(m+n), a^m / a^n = a^(m-n)",
            explanationBn = "একই ভিত্তিবিশিষ্ট ঘাত রাশির গুণফলে সূচকগুলো যোগ হয় এবং ভাগফলে সূচকগুলো বিয়োগ হয়।",
            variables = listOf(
                "a" to "ভিত্তি (Base, a ≠ 0)",
                "m, n" to "ঘাত বা সূচক (Exponents)"
            ),
            conditionsBn = "ভাগের ক্ষেত্রে ভিত্তি a ≠ 0 হতে হবে।",
            exampleProblemBn = "সরল করো: (2⁵ × 2³) ÷ 2⁶",
            exampleSolutionBn = "(2⁵⁺³) ÷ 2⁶ = 2⁸ ÷ 2⁶ = 2⁸⁻⁶ = 2² = 4",
            tipsBn = "ভিত্তি সমান না হলে সূচক যোগ করা যাবে না (যেমন 2³ × 3² এ সূচক যোগ হয় না)।",
            tags = listOf("সূচক", "ঘাত", "exponents", "indices", "a^m"),
            relatedFormulaIds = listOf("lib_alg_10", "lib_alg_11")
        ),
        LibraryFormula(
            id = "lib_alg_10",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "সূচক ও ঘাত (Exponents)",
            titleBn = "ঘাতের ঘাত, শূন্য ও ঋণাত্মক সূচক",
            titleEn = "Power of a Power, Zero & Negative Exponents",
            formulaExpression = "(aᵐ)ⁿ = aᵐⁿ  |  a⁰ = 1 (a ≠ 0)  |  a⁻ⁿ = 1 / aⁿ",
            latexExpression = "(a^m)^n = a^{mn}, \\quad a^0 = 1, \\quad a^{-n} = \\frac{1}{a^n}",
            plainTextExpression = "(a^m)^n = a^(m*n), a^0 = 1, a^(-n) = 1 / a^n",
            explanationBn = "শূন্য ব্যতীত যেকোনো সংখ্যার ঘাত 0 হলে তার মান 1 হয়। ঋণাত্মক ঘাত মানে ধনাত্মক ঘাতের বিপরীত ভগ্নাংশ।",
            variables = listOf(
                "a⁰" to "মান সর্বদা 1 (যেখানে a ≠ 0)",
                "a⁻ⁿ" to "গুণাত্মক বিপরীত 1/aⁿ"
            ),
            conditionsBn = "0⁰ অসংজ্ঞায়িত; তাই a ≠ 0 আবশ্যক।",
            exampleProblemBn = "মান নির্ণয় করো: (5⁰ + 3⁻²) × 9",
            exampleSolutionBn = "(1 + 1/3²) × 9 = (1 + 1/9) × 9 = (10/9) × 9 = 10",
            tipsBn = "ভগ্নাংশের ঋণাত্মক ঘাত থাকলে ভগ্নাংশটি উল্টে গিয়ে ঘাত ধনাত্মক হয়: (a/b)⁻ⁿ = (b/a)ⁿ।",
            tags = listOf("শূন্য ঘাত", "ঋণাত্মক সূচক", "a^0=1", "negative exponent"),
            relatedFormulaIds = listOf("lib_alg_09", "lib_alg_11")
        ),
        LibraryFormula(
            id = "lib_alg_11",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "লগারিদম (Logarithms)",
            titleBn = "লগারিদমের মৌলিক সূত্রাবলী",
            titleEn = "Fundamental Laws of Logarithms",
            formulaExpression = "logₐ(MN) = logₐM + logₐN  |  logₐ(M/N) = logₐM − logₐN  |  logₐ(Mʳ) = r·logₐM",
            latexExpression = "\\log_a(MN) = \\log_a M + \\log_a N, \\quad \\log_a(M^r) = r\\log_a M",
            plainTextExpression = "log_a(M*N) = log_a(M) + log_a(N)",
            explanationBn = "লগারিদম গুণকে যোগে, ভাগকে বিয়োগে এবং ঘাতকে সহগ গুণে রূপান্তর করে জটিল হিসাব সহজ করে।",
            variables = listOf(
                "a" to "লগারিদমের ভিত্তি (a > 0, a ≠ 1)",
                "M, N" to "ধনাত্মক বাস্তব সংখ্যা (M > 0, N > 0)"
            ),
            conditionsBn = "a > 0, a ≠ 1 এবং M > 0, N > 0। ঋণাত্মক বা শূন্য সংখ্যার বাস্তব লগারিদম নেই।",
            exampleProblemBn = "মান নির্ণয় করো: log₁₀(25) + log₁₀(4)",
            exampleSolutionBn = "log₁₀(25 × 4) = log₁₀(100) = log₁₀(10²) = 2 · log₁₀(10) = 2 × 1 = 2",
            tipsBn = "মনে রাখবেন: logₐ(1) = 0 এবং logₐ(a) = 1।",
            tags = listOf("লগারিদম", "লগ", "logarithm", "log rules"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_alg_12")
        ),
        LibraryFormula(
            id = "lib_alg_12",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "লগারিদম (Logarithms)",
            titleBn = "লগারিদমের ভিত্তি পরিবর্তন সূত্র",
            titleEn = "Change of Base Formula",
            formulaExpression = "logₐ(b) = logₖ(b) / logₖ(a)   এবং   logₐ(b) × log_b(a) = 1",
            latexExpression = "\\log_a b = \\frac{\\log_k b}{\\log_k a}, \\quad \\log_a b \\cdot \\log_b a = 1",
            plainTextExpression = "log_a(b) = log_k(b) / log_k(a)",
            explanationBn = "লগারিদমের ভিত্তি একটি সংখ্যা থেকে অন্য সুবিধাজনক ভিত্তিতে (যেমন 10 বা e) রূপান্তরের সূত্র।",
            variables = listOf(
                "a, b, k" to "ধনাত্মক সংখ্যা যেখানে a ≠ 1, k ≠ 1"
            ),
            conditionsBn = "a, b, k > 0 এবং a ≠ 1, b ≠ 1, k ≠ 1।",
            exampleProblemBn = "log₄(8) এর মান কত?",
            exampleSolutionBn = "2 ভিত্তিক লগে পরিবর্তন করে:\nlog₄(8) = log₂(8) / log₂(4) = log₂(2³) / log₂(2²) = 3 / 2 = 1.5",
            tipsBn = "logₐ(b) = 1 / log_b(a) — ভিত্তি ও সংখ্যা স্থান বিনিময় করলে মান ব্যস্তানুপাতিক হয়।",
            tags = listOf("ভিত্তি পরিবর্তন", "change of base", "logarithm"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_alg_11")
        ),
        LibraryFormula(
            id = "lib_alg_13",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "দ্বিঘাত সমীকরণ (Quadratic Equations)",
            titleBn = "দ্বিঘাত সমীকরণের সাধারণ সমাধান (শ্রীধর আচার্যের সূত্র)",
            titleEn = "Quadratic Formula",
            formulaExpression = "ax² + bx + c = 0 হলে,  x = [−b ± √(b² − 4ac)] / (2a)",
            latexExpression = "x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}",
            plainTextExpression = "x = (-b +- sqrt(b^2 - 4ac)) / (2a)",
            explanationBn = "যেকোনো এক চলকবিশিষ্ট দ্বিঘাত সমীকরণের দুটি মূল নির্ণয়ের সর্বজনীন সূত্র। এখানে D = b² − 4ac কে নিশ্চায়ক (Discriminant) বলা হয়।",
            variables = listOf(
                "a" to "x²-এর সহগ (a ≠ 0)",
                "b" to "x-এর সহগ",
                "c" to "ধ্রুবক পদ",
                "b² − 4ac" to "নিশ্চায়ক (মূলের প্রকৃতি নির্ধারক)"
            ),
            conditionsBn = "a ≠ 0। বাস্তব মূলের জন্য b² − 4ac ≥ 0 হতে হবে।",
            derivationStepsBn = listOf(
                "ax² + bx + c = 0 এর উভয় পক্ষকে a দ্বারা ভাগ করে: x² + (b/a)x + c/a = 0",
                "পূর্ণবর্গ তৈরি করে: (x + b/2a)² = (b² − 4ac) / (4a²)",
                "বর্গমূল নিয়ে: x + b/2a = ±√(b² − 4ac) / (2a) ⇒ x = [−b ± √(b² − 4ac)] / (2a)"
            ),
            exampleProblemBn = "x² − 5x + 6 = 0 সমীকরণটির মূলদ্বয় নির্ণয় করো।",
            exampleSolutionBn = "এখানে a = 1, b = −5, c = 6\nx = [5 ± √((-5)² − 4·1·6)] / 2 = [5 ± √(25 − 24)] / 2 = (5 ± 1)/2\nসুতরাং x = 3 অথবা x = 2",
            tipsBn = "নিশ্চায়ক D > 0 হলে মূলদ্বয় বাস্তব ও অসমান; D = 0 হলে বাস্তব ও সমান; D < 0 হলে জটিল (অবাস্তব)।",
            tags = listOf("দ্বিঘাত সমীকরণ", "নিশ্চায়ক", "quadratic formula", "roots"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_alg_14")
        ),
        LibraryFormula(
            id = "lib_alg_14",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "দ্বিঘাত সমীকরণ (Quadratic Equations)",
            titleBn = "দ্বিঘাত সমীকরণের মূল ও সহগের সম্পর্ক",
            titleEn = "Vieta's Formulas for Quadratic Equations",
            formulaExpression = "মূলদ্বয়ের যোগফল (α + β) = −b / a   এবং   মূলদ্বয়ের গুণফল (αβ) = c / a",
            latexExpression = "\\alpha + \\beta = -\\frac{b}{a}, \\quad \\alpha\\beta = \\frac{c}{a}",
            plainTextExpression = "alpha + beta = -b/a, alpha * beta = c/a",
            explanationBn = "ax² + bx + c = 0 সমীকরণের মূল দুটি α ও β হলে সমীকরণ সমাধান না করেই সহগ থেকে মূলদ্বয়ের যোগফল ও গুণফল বের করা যায়।",
            variables = listOf(
                "α, β" to "সমীকরণের মূলদ্বয়",
                "x² − (α+β)x + αβ = 0" to "মূলদ্বয় দেওয়া থাকলে সমীকরণ গঠনের সূত্র"
            ),
            conditionsBn = "a ≠ 0।",
            exampleProblemBn = "2x² − 7x + 3 = 0 সমীকরণের মূলদ্বয়ের সমষ্টি ও গুণফল কত?",
            exampleSolutionBn = "সমষ্টি α + β = −(−7)/2 = 7/2\nগুণফল αβ = 3/2",
            tipsBn = "দুটি মূল জানা থাকলে সমীকরণটি হবে: x² − (মূলদ্বয়ের যোগফল)x + (মূলদ্বয়ের গুণফল) = 0।",
            tags = listOf("মূল ও সহগ", "যোগফল", "গুণফল", "vieta", "roots"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_alg_13")
        ),
        LibraryFormula(
            id = "lib_alg_15",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "সমান্তর ও গুণোত্তর ধারা",
            titleBn = "সমান্তর ধারার n-তম পদ ও সমষ্টি",
            titleEn = "Arithmetic Progression (AP)",
            formulaExpression = "n-তম পদ Tₙ = a + (n − 1)d   |   সমষ্টি Sₙ = (n/2)[2a + (n − 1)d]",
            latexExpression = "T_n = a + (n-1)d, \\quad S_n = \\frac{n}{2}[2a + (n-1)d]",
            plainTextExpression = "T_n = a + (n - 1)d, S_n = (n/2)[2a + (n - 1)d]",
            explanationBn = "পরপর দুটি পদের পার্থক্য সর্বদা সমান হলে তাকে সমান্তর ধারা বলে।",
            variables = listOf(
                "a" to "প্রথম পদ (First term)",
                "d" to "সাধারণ অন্তর (Common difference)",
                "n" to "পদ সংখ্যা"
            ),
            exampleProblemBn = "2 + 5 + 8 + ... ধারাটির ১০ম পদ এবং প্রথম ১০টি পদের সমষ্টি কত?",
            exampleSolutionBn = "এখানে a = 2, d = 3, n = 10\n১০ম পদ = 2 + (10 − 1)×3 = 2 + 27 = 29\nসমষ্টি S₁₀ = (10/2)[2(2) + 9(3)] = 5 × (4 + 27) = 155",
            tipsBn = "শেষ পদ l জানা থাকলে সমষ্টি Sₙ = (n/2)(a + l)।",
            tags = listOf("সমান্তর ধারা", "AP", "arithmetic progression", "series"),
            relatedFormulaIds = listOf("lib_alg_16", "lib_alg_17", "lib_seq_01")
        ),
        LibraryFormula(
            id = "lib_alg_16",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "সমান্তর ও গুণোত্তর ধারা",
            titleBn = "গুণোত্তর ধারার n-তম পদ ও সমষ্টি",
            titleEn = "Geometric Progression (GP)",
            formulaExpression = "Tₙ = a · rⁿ⁻¹   |   Sₙ = a(rⁿ − 1) / (r − 1)  [যখন r > 1]",
            latexExpression = "T_n = ar^{n-1}, \\quad S_n = \\frac{a(r^n - 1)}{r - 1}",
            plainTextExpression = "T_n = a * r^(n-1), S_n = a(r^n - 1)/(r - 1)",
            explanationBn = "যেকোনো পদ ও তার পূর্ববর্তী পদের অনুপাত সর্বদা ধ্রুবক (r) হলে তাকে গুণোত্তর ধারা বলে।",
            variables = listOf(
                "a" to "প্রথম পদ",
                "r" to "সাধারণ অনুপাত (Common ratio, r ≠ 1)",
                "n" to "পদ সংখ্যা"
            ),
            conditionsBn = "সমষ্টি সূত্রে r ≠ 1। অসীম সমষ্টির জন্য |r| < 1।",
            exampleProblemBn = "3 + 6 + 12 + 24 + ... ধারাটির ৬ষ্ঠ পদ কত?",
            exampleSolutionBn = "এখানে a = 3, r = 6/3 = 2\n৬ষ্ঠ পদ T₆ = 3 × 2⁶⁻¹ = 3 × 2⁵ = 3 × 32 = 96",
            tipsBn = "|r| < 1 হলে অসীম গুণোত্তর ধারার সমষ্টি S∞ = a / (1 − r)।",
            tags = listOf("গুণোত্তর ধারা", "GP", "geometric progression"),
            relatedFormulaIds = listOf("lib_alg_15", "lib_seq_02")
        ),
        LibraryFormula(
            id = "lib_alg_17",
            subject = FormulaSubject.ALGEBRA,
            subCategory = "সমান্তর ও গুণোত্তর ধারা",
            titleBn = "স্বাভাবিক সংখ্যার সমষ্টি, বর্গের সমষ্টি ও ঘনের সমষ্টি",
            titleEn = "Sums of Powers of Natural Numbers",
            formulaExpression = "∑n = n(n+1)/2  |  ∑n² = n(n+1)(2n+1)/6  |  ∑n³ = [n(n+1)/2]²",
            latexExpression = "\\sum_{k=1}^{n} k = \\frac{n(n+1)}{2}, \\quad \\sum_{k=1}^{n} k^2 = \\frac{n(n+1)(2n+1)}{6}",
            plainTextExpression = "Sum(n) = n(n+1)/2, Sum(n^2) = n(n+1)(2n+1)/6, Sum(n^3) = [n(n+1)/2]^2",
            explanationBn = "প্রথম n সংখ্যক স্বাভাবিক সংখ্যা, তাদের বর্গ এবং তাদের ঘনের সমষ্টি নির্ণয়ের সরাসরি সূত্র।",
            variables = listOf(
                "n" to "স্বাভাবিক সংখ্যার শেষ পদ (1, 2, 3, ..., n)"
            ),
            exampleProblemBn = "1 + 2 + 3 + ... + 20 এর মান কত?",
            exampleSolutionBn = "সমষ্টি = n(n + 1)/2 = (20 × 21) / 2 = 210",
            tipsBn = "প্রথম n সংখ্যক বিজোড় স্বাভাবিক সংখ্যার সমষ্টি = n²।",
            tags = listOf("স্বাভাবিক সংখ্যা", "সমষ্টি", "n(n+1)/2", "natural numbers"),
            relatedFormulaIds = listOf("lib_alg_15", "lib_seq_03")
        )
    )

    // -------------------------------------------------------------
    // 4. GEOMETRY (জ্যামিতি) — 16 Formulas
    // -------------------------------------------------------------
    val geometryFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_geom_01",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "ত্রিভুজ (Triangles)",
            titleBn = "ত্রিভুজের সাধারণ ক্ষেত্রফল ও পরিসীমা",
            titleEn = "Area and Perimeter of a Triangle",
            formulaExpression = "ক্ষেত্রফল = ½ × ভূমি × উচ্চতা (½ · b · h)  |  পরিসীমা = a + b + c",
            latexExpression = "A = \\frac{1}{2} b h, \\quad P = a + b + c",
            plainTextExpression = "Area = 1/2 * base * height, Perimeter = a + b + c",
            explanationBn = "যেকোনো ত্রিভুজের ভূমি এবং ওই ভূমির ওপর লম্ব উচ্চতার গুণফলের অর্ধেক হলো ত্রিভুজটির ক্ষেত্রফল।",
            variables = listOf(
                "b" to "ত্রিভুজের ভূমি (Base)",
                "h" to "লম্ব উচ্চতা (Height)",
                "a, b, c" to "ত্রিভুজের তিনটি বাহুর দৈর্ঘ্য"
            ),
            conditionsBn = "b > 0, h > 0 এবং যেকোনো দুই বাহুর সমষ্টি তৃতীয় বাহু অপেক্ষা বৃহত্তর (a + b > c)।",
            exampleProblemBn = "একটি ত্রিভুজের ভূমি 12 সেমি এবং উচ্চতা 8 সেমি হলে ক্ষেত্রফল কত?",
            exampleSolutionBn = "ক্ষেত্রফল = ½ × 12 × 8 = 6 × 8 = 48 বর্গ সেমি",
            tipsBn = "ত্রিভুজের তিন কোণের সমষ্টি সর্বদা ১৮০° (বা দুই সমকোণ)।",
            tags = listOf("ত্রিভুজ", "ক্ষেত্রফল", "ভূমি", "উচ্চতা", "triangle area"),
            relatedFormulaIds = listOf("lib_geom_02", "lib_geom_03", "lib_geom_04")
        ),
        LibraryFormula(
            id = "lib_geom_02",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "ত্রিভুজ (Triangles)",
            titleBn = "সমকোণী ত্রিভুজ ও পিথাগোরাসের উপপাদ্য",
            titleEn = "Right Triangle & Pythagorean Theorem",
            formulaExpression = "অতিভুজ² = লম্ব² + ভূমি²   (c² = a² + b²)",
            latexExpression = "c^2 = a^2 + b^2 \\implies c = \\sqrt{a^2 + b^2}",
            plainTextExpression = "c^2 = a^2 + b^2",
            explanationBn = "সমকোণী ত্রিভুজের সমকোণের বিপরীত বাহু (অতিভুজ)-এর ওপর অঙ্কিত বর্গক্ষেত্র অপর দুই বাহুর ওপর অঙ্কিত বর্গক্ষেত্রদ্বয়ের সমষ্টির সমান।",
            variables = listOf(
                "c" to "অতিভুজ (Hypotenuse — বৃহত্তম বাহু)",
                "a" to "লম্ব (Perpendicular / Altitude)",
                "b" to "ভূমি (Base)"
            ),
            conditionsBn = "ত্রিভুজটি অবশ্যই সমকোণী (একটি কোণ ৯০°) হতে হবে।",
            exampleProblemBn = "সমকোণী ত্রিভুজের লম্ব 6 সেমি ও ভূমি 8 সেমি হলে অতিভুজ কত?",
            exampleSolutionBn = "c = √(6² + 8²) = √(36 + 64) = √100 = 10 সেমি",
            tipsBn = "জনপ্রিয় পিথাগোরাসীয় ত্রয়ী (Triplets): (3, 4, 5), (5, 12, 13), (8, 15, 17), (7, 24, 25)।",
            tags = listOf("পিথাগোরাস", "সমকোণী ত্রিভুজ", "অতিভুজ", "pythagoras", "right triangle"),
            relatedFormulaIds = listOf("lib_geom_01", "lib_trig_01")
        ),
        LibraryFormula(
            id = "lib_geom_03",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "ত্রিভুজ (Triangles)",
            titleBn = "সমবাহু ত্রিভুজের ক্ষেত্রফল ও উচ্চতা",
            titleEn = "Equilateral Triangle Area & Height",
            formulaExpression = "ক্ষেত্রফল = (√3 / 4) a²   |   উচ্চতা h = (√3 / 2) a   |   পরিসীমা = 3a",
            latexExpression = "A = \\frac{\\sqrt{3}}{4}a^2, \\quad h = \\frac{\\sqrt{3}}{2}a",
            plainTextExpression = "Area = (sqrt(3)/4) * a^2, Height = (sqrt(3)/2) * a",
            explanationBn = "যে ত্রিভুজের তিনটি বাহুই পরস্পর সমান এবং প্রতিটি কোণ ৬০° তাকে সমবাহু ত্রিভুজ বলে।",
            variables = listOf(
                "a" to "সমবাহু ত্রিভুজের যেকোনো এক বাহুর দৈর্ঘ্য",
                "√3" to "প্রায় 1.732"
            ),
            exampleProblemBn = "একটি সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য 4 সেমি হলে ক্ষেত্রফল কত?",
            exampleSolutionBn = "ক্ষেত্রফল = (√3 / 4) × 4² = (√3 / 4) × 16 = 4√3 বর্গ সেমি",
            tipsBn = "সমদ্বিবাহু ত্রিভুজের ক্ষেত্রফল = (b/4) × √(4a² − b²), যেখানে a সমান বাহু ও b ভূমি।",
            tags = listOf("সমবাহু ত্রিভুজ", "equilateral triangle", "sqrt(3)/4"),
            relatedFormulaIds = listOf("lib_geom_01", "lib_geom_04")
        ),
        LibraryFormula(
            id = "lib_geom_04",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "ত্রিভুজ (Triangles)",
            titleBn = "হেরনের সূত্র (বিষমবাহু ত্রিভুজের ক্ষেত্রফল)",
            titleEn = "Heron's Formula",
            formulaExpression = "ক্ষেত্রফল = √[s(s − a)(s − b)(s − c)],  যেখানে s = (a + b + c) / 2",
            latexExpression = "A = \\sqrt{s(s-a)(s-b)(s-c)}, \\quad s = \\frac{a+b+c}{2}",
            plainTextExpression = "Area = sqrt(s*(s-a)*(s-b)*(s-c))",
            explanationBn = "ত্রিভুজের উচ্চতা জানা না থাকলেও কেবল তিনটি বাহুর দৈর্ঘ্য জানা থাকলে হেরনের সূত্র দিয়ে নিখুঁত ক্ষেত্রফল বের করা যায়।",
            variables = listOf(
                "a, b, c" to "ত্রিভুজের তিন বাহুর দৈর্ঘ্য",
                "s" to "অর্ধ-পরিসীমা (Semi-perimeter)"
            ),
            conditionsBn = "ত্রিভুজ অসমতা সিদ্ধ হতে হবে: s > a, s > b, s > c।",
            exampleProblemBn = "ত্রিভুজের তিন বাহু যথাক্রমে 5, 12 ও 13 মিটার হলে ক্ষেত্রফল কত?",
            exampleSolutionBn = "অর্ধ-পরিসীমা s = (5 + 12 + 13) / 2 = 15\nক্ষেত্রফল = √[15(15−5)(15−12)(15−13)] = √[15 × 10 × 3 × 2] = √900 = 30 বর্গ মিটার",
            tipsBn = "পরিসীমা 2s হলে অর্ধ-পরিসীমা s = পরিসীমা ÷ 2।",
            tags = listOf("হেরনের সূত্র", "অর্ধপরিসীমা", "heron formula", "scalene"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_geom_01", "lib_geom_03")
        ),
        LibraryFormula(
            id = "lib_geom_05",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "চতুর্ভুজ (Quadrilaterals)",
            titleBn = "আয়তক্ষেত্রের ক্ষেত্রফল, পরিসীমা ও কর্ণ",
            titleEn = "Rectangle Area, Perimeter & Diagonal",
            formulaExpression = "ক্ষেত্রফল = দৈর্ঘ্য × প্রস্থ (l × w)  |  পরিসীমা = 2(l + w)  |  কর্ণ d = √(l² + w²)",
            latexExpression = "A = l \\times w, \\quad P = 2(l + w), \\quad d = \\sqrt{l^2 + w^2}",
            plainTextExpression = "Area = l * w, Perimeter = 2*(l + w), Diagonal = sqrt(l^2 + w^2)",
            explanationBn = "আয়তক্ষেত্রের বিপরীত বাহুগুলো সমান ও সমান্তরাল এবং প্রতিটি কোণ সমকোণ (৯০°)।",
            variables = listOf(
                "l" to "আয়তক্ষেত্রের দৈর্ঘ্য (Length)",
                "w" to "আয়তক্ষেত্রের প্রস্থ (Width)",
                "d" to "কর্ণের দৈর্ঘ্য (Diagonal)"
            ),
            exampleProblemBn = "একটি ঘরের দৈর্ঘ্য 8 মিটার ও প্রস্থ 6 মিটার হলে এর ক্ষেত্রফল, পরিসীমা ও কর্ণ কত?",
            exampleSolutionBn = "ক্ষেত্রফল = 8 × 6 = 48 বর্গ মিটার\nপরিসীমা = 2(8 + 6) = 28 মিটার\nকর্ণ = √(8² + 6²) = √(64 + 36) = √100 = 10 মিটার",
            tipsBn = "বাগানের ভেতরে চারদিকে w চওড়া রাস্তা থাকলে রাস্তার ক্ষেত্রফল = 2w(l + b − 2w)।",
            tags = listOf("আয়তক্ষেত্র", "পরিসীমা", "কর্ণ", "rectangle", "area"),
            relatedFormulaIds = listOf("lib_geom_06", "lib_mens_01")
        ),
        LibraryFormula(
            id = "lib_geom_06",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "চতুর্ভুজ (Quadrilaterals)",
            titleBn = "বর্গক্ষেত্রের ক্ষেত্রফল, পরিসীমা ও কর্ণ",
            titleEn = "Square Area, Perimeter & Diagonal",
            formulaExpression = "ক্ষেত্রফল = a² = ½ d²   |   পরিসীমা = 4a   |   কর্ণ d = a√2",
            latexExpression = "A = a^2 = \\frac{1}{2}d^2, \\quad P = 4a, \\quad d = a\\sqrt{2}",
            plainTextExpression = "Area = a^2 = 0.5 * d^2, Perimeter = 4a, Diagonal = a * sqrt(2)",
            explanationBn = "বর্গক্ষেত্রের চারটি বাহুই সমান এবং প্রতিটি কোণ সমকোণ। কর্ণ দেওয়া থাকলে সরাসরি ½ d² দিয়ে ক্ষেত্রফল পাওয়া যায়।",
            variables = listOf(
                "a" to "বর্গক্ষেত্রের এক বাহুর দৈর্ঘ্য",
                "d" to "বর্গক্ষেত্রের কর্ণ (a√2)"
            ),
            exampleProblemBn = "একটি বর্গক্ষেত্রের কর্ণ 6√2 সেমি হলে এর ক্ষেত্রফল ও পরিসীমা কত?",
            exampleSolutionBn = "কর্ণ a√2 = 6√2 ⇒ বাহু a = 6 সেমি\nক্ষেত্রফল = 6² = 36 বর্গ সেমি\nপরিসীমা = 4 × 6 = 24 সেমি",
            tipsBn = "বর্গক্ষেত্রের বাহু ১০% বৃদ্ধি পেলে ক্ষেত্রফল বৃদ্ধি পায় (10 + 10 + 100/100)% = 21%।",
            tags = listOf("বর্গক্ষেত্র", "কর্ণ", "square", "diagonal"),
            relatedFormulaIds = listOf("lib_geom_05")
        ),
        LibraryFormula(
            id = "lib_geom_07",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "চতুর্ভুজ (Quadrilaterals)",
            titleBn = "সামান্তরিক, রম্বস ও ট্রাপিজিয়ামের ক্ষেত্রফল",
            titleEn = "Parallelogram, Rhombus & Trapezium Area",
            formulaExpression = "সামান্তরিক = ভূমি × উচ্চতা  |  রম্বস = ½(d₁ × d₂)  |  ট্রাপিজিয়াম = ½(a + b)h",
            latexExpression = "A_{\\text{rhombus}} = \\frac{1}{2}d_1 d_2, \\quad A_{\\text{trapezium}} = \\frac{1}{2}(a+b)h",
            plainTextExpression = "Parallelogram = b*h, Rhombus = 0.5*d1*d2, Trapezium = 0.5*(a+b)*h",
            explanationBn = "রম্বসের কর্ণদ্বয় পরস্পরকে সমকোণে সমদ্বিখণ্ডিত করে। ট্রাপিজিয়ামের দুটি বাহু সমান্তরাল।",
            variables = listOf(
                "d₁, d₂" to "রম্বসের কর্ণদ্বয়ের দৈর্ঘ্য",
                "a, b" to "ট্রাপিজিয়ামের সমান্তরাল বাহুদ্বয়ের দৈর্ঘ্য",
                "h" to "সমান্তরাল বাহুদ্বয়ের মধ্যবর্তী লম্ব দূরত্ব"
            ),
            exampleProblemBn = "একটি রম্বসের কর্ণদ্বয় 10 সেমি ও 14 সেমি হলে ক্ষেত্রফল কত?",
            exampleSolutionBn = "ক্ষেত্রফল = ½ × d₁ × d₂ = ½ × 10 × 14 = 70 বর্গ সেমি",
            tipsBn = "রম্বসের পরিসীমা = 4a, যেখানে বাহু a = √[(d₁/2)² + (d₂/2)²]।",
            tags = listOf("সামান্তরিক", "রম্বস", "ট্রাপিজিয়াম", "rhombus", "trapezium"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE
        ),
        LibraryFormula(
            id = "lib_geom_08",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "বৃত্ত (Circles)",
            titleBn = "বৃত্তের পরিধি ও ক্ষেত্রফল",
            titleEn = "Circumference & Area of a Circle",
            formulaExpression = "পরিধি C = 2πr = πd   |   ক্ষেত্রফল A = πr² = (πd²) / 4",
            latexExpression = "C = 2\\pi r, \\quad A = \\pi r^2",
            plainTextExpression = "C = 2*pi*r, A = pi*r^2",
            explanationBn = "বৃত্তের কেন্দ্র থেকে পরিধি পর্যন্ত দূরত্ব ব্যাসার্ধ (r) এবং ব্যাস d = 2r। π (পাই)-এর আসন্ন মান 22/7 বা 3.1416।",
            variables = listOf(
                "r" to "বৃত্তের ব্যাসার্ধ (Radius)",
                "d" to "বৃত্তের ব্যাস (Diameter = 2r)",
                "π" to "ধ্রুবক (≈ 3.14159 বা 22/7)"
            ),
            exampleProblemBn = "একটি বৃত্তের ব্যাসার্ধ 7 সেমি হলে এর পরিধি ও ক্ষেত্রফল কত?",
            exampleSolutionBn = "পরিধি = 2 × (22/7) × 7 = 44 সেমি\nক্ষেত্রফল = (22/7) × 7² = 22 × 7 = 154 বর্গ সেমি",
            tipsBn = "অর্ধবৃত্তের পরিধি = πr + 2r = r(π + 2) এবং ক্ষেত্রফল = ½ πr²।",
            tags = listOf("বৃত্ত", "পরিধি", "ব্যাসার্ধ", "circle", "pi", "circumference"),
            relatedFormulaIds = listOf("lib_geom_09")
        ),
        LibraryFormula(
            id = "lib_geom_09",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "বৃত্ত (Circles)",
            titleBn = "বৃত্তচাপের দৈর্ঘ্য ও বৃত্তকলার ক্ষেত্রফল",
            titleEn = "Arc Length & Sector Area",
            formulaExpression = "বৃত্তচাপ s = (θ / 360°) × 2πr   |   বৃত্তকলার ক্ষেত্রফল = (θ / 360°) × πr²",
            latexExpression = "s = \\frac{\\theta}{360^\\circ} 2\\pi r, \\quad A = \\frac{\\theta}{360^\\circ} \\pi r^2",
            plainTextExpression = "s = (theta/360)*2*pi*r, Area = (theta/360)*pi*r^2",
            explanationBn = "বৃত্তের কেন্দ্রে θ কোণ উৎপন্নকারী চাপের দৈর্ঘ্য এবং বৃত্তকলার (Sector) ক্ষেত্রফল নির্ণয়ের সূত্র।",
            variables = listOf(
                "θ" to "কেন্দ্রে উৎপন্ন কোণ (ডিগ্রিতে)",
                "r" to "বৃত্তের ব্যাসার্ধ"
            ),
            exampleProblemBn = "7 সেমি ব্যাসার্ধের বৃত্তে কেন্দ্রে 90° কোণ উৎপন্নকারী বৃত্তকলার ক্ষেত্রফল কত?",
            exampleSolutionBn = "ক্ষেত্রফল = (90° / 360°) × (22/7) × 7²\n= (1/4) × 154 = 38.5 বর্গ সেমি",
            tipsBn = "কোণ রেডিয়ানে থাকলে: চাপ s = rθ এবং ক্ষেত্রফল A = ½ r²θ।",
            tags = listOf("বৃত্তচাপ", "বৃত্তকলা", "arc length", "sector area"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_geom_08")
        ),
        LibraryFormula(
            id = "lib_geom_10",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "বহুভুজ (Polygons)",
            titleBn = "সুষম বহুভুজের অন্তঃস্থ ও বহিঃস্থ কোণ এবং কর্ণ",
            titleEn = "Regular Polygon Angles & Diagonals",
            formulaExpression = "অন্তঃস্থ কোণের সমষ্টি = (n − 2) × 180°  |  প্রতিটি বহিঃস্থ কোণ = 360° / n  |  মোট কর্ণ = n(n − 3) / 2",
            latexExpression = "\\sum \\theta_{\\text{int}} = (n-2)\\times 180^\\circ, \\quad D = \\frac{n(n-3)}{2}",
            plainTextExpression = "Interior Sum = (n - 2) * 180, Diagonals = n*(n - 3)/2",
            explanationBn = "n সংখ্যক বাহুবিশিষ্ট সুষম বহুভুজের কোণ ও কর্ণের সংখ্যা নির্ণয়ের সর্বজনীন সূত্র।",
            variables = listOf(
                "n" to "বহুভুজের বাহুর সংখ্যা (n ≥ 3)"
            ),
            conditionsBn = "বাহুর সংখ্যা n ≥ 3 পূর্ণসংখ্যা হতে হবে।",
            exampleProblemBn = "একটি সুষম ষড়ভুজের (n = 6) প্রতিটি অন্তঃস্থ কোণ ও মোট কর্ণ সংখ্যা কত?",
            exampleSolutionBn = "অন্তঃস্থ কোণের সমষ্টি = (6 − 2) × 180° = 720°\nপ্রতিটি অন্তঃস্থ কোণ = 720° ÷ 6 = 120°\nমোট কর্ণ = 6(6 − 3) / 2 = 9টি",
            tipsBn = "যেকোনো বহুভুজের সকল বহিঃস্থ কোণের সমষ্টি সর্বদা 360°।",
            tags = listOf("বহুভুজ", "ষড়ভুজ", "কর্ণ", "polygon", "interior angle", "diagonals"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE
        ),
        LibraryFormula(
            id = "lib_geom_11",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "ঘনবস্তু (3D Solids)",
            titleBn = "সমবৃত্তভূমিক বেলন (সিলিন্ডার) এর ক্ষেত্রফল ও আয়তন",
            titleEn = "Cylinder Surface Area & Volume",
            formulaExpression = "বক্রতল = 2πrh  |  সমগ্রতল = 2πr(r + h)  |  আয়তন V = πr²h",
            latexExpression = "V = \\pi r^2 h, \\quad A_{\\text{total}} = 2\\pi r(r + h)",
            plainTextExpression = "Volume = pi * r^2 * h, Total Area = 2*pi*r*(r + h)",
            explanationBn = "সিলিন্ডারের দুটি বৃত্তাকার তল (2πr²) এবং একটি বক্রতল (2πrh) থাকে।",
            variables = listOf(
                "r" to "ভূমির ব্যাসার্ধ",
                "h" to "সিলিন্ডারের উচ্চতা"
            ),
            exampleProblemBn = "একটি বেলনের ব্যাসার্ধ 7 সেমি এবং উচ্চতা 10 সেমি হলে এর আয়তন কত?",
            exampleSolutionBn = "আয়তন V = πr²h = (22/7) × 7² × 10 = 154 × 10 = 1540 ঘন সেমি",
            tipsBn = "আয়তন = ভূমির ক্ষেত্রফল (πr²) × উচ্চতা (h)।",
            tags = listOf("বেলন", "সিলিন্ডার", "আয়তন", "cylinder", "volume"),
            relatedFormulaIds = listOf("lib_geom_12", "lib_geom_13", "lib_mens_02")
        ),
        LibraryFormula(
            id = "lib_geom_12",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "ঘনবস্তু (3D Solids)",
            titleBn = "গোলক ও শঙ্কুর ক্ষেত্রফল এবং আয়তন",
            titleEn = "Sphere & Cone Surface Area and Volume",
            formulaExpression = "গোলক: তল = 4πr², আয়তন = (4/3)πr³  |  শঙ্কু: আয়তন = (1/3)πr²h, বক্রতল = πrl",
            latexExpression = "V_{\\text{sphere}} = \\frac{4}{3}\\pi r^3, \\quad V_{\\text{cone}} = \\frac{1}{3}\\pi r^2 h",
            plainTextExpression = "Sphere V = (4/3)*pi*r^3, Cone V = (1/3)*pi*r^2*h",
            explanationBn = "সমবৃত্তভূমিক শঙ্কুর হেলানো উচ্চতা l = √(r² + h²)। শঙ্কুর আয়তন সমান ব্যাসার্ধ ও উচ্চতার সিলিন্ডারের আয়তনের এক-তৃতীয়াংশ।",
            variables = listOf(
                "r" to "ব্যাসার্ধ",
                "h" to "শঙ্কুর লম্ব উচ্চতা",
                "l" to "শঙ্কুর হেলানো উন্নতি বা তীর্যক উচ্চতা √(r² + h²)"
            ),
            exampleProblemBn = "একটি শঙ্কুর ব্যাসার্ধ 3 সেমি ও উচ্চতা 4 সেমি হলে এর হেলানো উচ্চতা কত?",
            exampleSolutionBn = "হেলানো উচ্চতা l = √(3² + 4²) = √(9 + 16) = √25 = 5 সেমি",
            tipsBn = "অর্ধগোলকের সমগ্রতলের ক্ষেত্রফল = 3πr² এবং আয়তন = (2/3)πr³।",
            tags = listOf("গোলক", "শঙ্কু", "কোণক", "sphere", "cone", "volume"),
            relatedFormulaIds = listOf("lib_geom_11", "lib_mens_03")
        ),
        LibraryFormula(
            id = "lib_geom_13",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "ঘনবস্তু (3D Solids)",
            titleBn = "ঘনক ও আয়তাকার ঘনবস্তু",
            titleEn = "Cube & Cuboid Properties",
            formulaExpression = "ঘনক: V = a³, সমগ্রতল = 6a² | আয়তাকার: V = abc, তল = 2(ab + bc + ca)",
            latexExpression = "V_{\\text{cube}} = a^3, \\quad V_{\\text{cuboid}} = abc",
            plainTextExpression = "Cube: V = a^3, S = 6a^2 | Cuboid: V = a*b*c, S = 2(ab + bc + ca)",
            explanationBn = "ঘনকের প্রতিটি বাহু সমান। আয়তাকার ঘনবস্তুর তিনটি মাত্রা দৈর্ঘ্য a, প্রস্থ b ও উচ্চতা c।",
            variables = listOf(
                "a, b, c" to "দৈর্ঘ্য, প্রস্থ ও উচ্চতা",
                "কর্ণ" to "আয়তাকারের কর্ণ = √(a² + b² + c²), ঘনকের কর্ণ = a√3"
            ),
            exampleProblemBn = "একটি ঘনকের বাহু 4 সেমি হলে এর কর্ণ ও আয়তন কত?",
            exampleSolutionBn = "আয়তন = 4³ = 64 ঘন সেমি\nকর্ণ = 4√3 ≈ 6.93 সেমি",
            tipsBn = "ঘরের মধ্যে রাখা যায় এমন দীর্ঘতম লাঠির দৈর্ঘ্য হলো ঘরের কর্ণের দৈর্ঘ্যের সমান: √(a² + b² + c²)।",
            tags = listOf("ঘনক", "আয়তাকার ঘনবস্তু", "cube", "cuboid", "volume"),
            relatedFormulaIds = listOf("lib_mens_01")
        ),
        LibraryFormula(
            id = "lib_geom_14",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "স্থানাঙ্ক জ্যামিতি",
            titleBn = "দুটি বিন্দুর মধ্যকার দূরত্ব নির্ণয়",
            titleEn = "Distance Formula in Coordinate Geometry",
            formulaExpression = "d = √[(x₂ − x₁)² + (y₂ − y₁)²]",
            latexExpression = "d = \\sqrt{(x_2 - x_1)^2 + (y_2 - y_1)^2}",
            plainTextExpression = "d = sqrt((x2 - x1)^2 + (y2 - y1)^2)",
            explanationBn = "কার্তেসীয় সমতলে P(x₁, y₁) এবং Q(x₂, y₂) দুটি বিন্দুর মধ্যবর্তী সরলরেখিক দূরত্বের পরিমাপ।",
            variables = listOf(
                "(x₁, y₁)" to "প্রথম বিন্দুর স্থানাঙ্ক",
                "(x₂, y₂)" to "দ্বিতীয় বিন্দুর স্থানাঙ্ক"
            ),
            exampleProblemBn = "A(1, 2) এবং B(4, 6) বিন্দুদ্বয়ের মধ্যবর্তী দূরত্ব কত?",
            exampleSolutionBn = "d = √[(4 − 1)² + (6 − 2)²] = √[3² + 4²] = √[9 + 16] = √25 = 5 একক",
            tipsBn = "বর্গ করার কারণে (x₂ − x₁)² এবং (x₁ − x₂)² একই ফলাফল প্রদান করে।",
            tags = listOf("স্থানাঙ্ক জ্যামিতি", "দূরত্ব", "distance formula", "coordinate"),
            relatedFormulaIds = listOf("lib_geom_15", "lib_geom_16", "lib_coord_01")
        ),
        LibraryFormula(
            id = "lib_geom_15",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "স্থানাঙ্ক জ্যামিতি",
            titleBn = "মধ্যবিন্দু ও বিভক্তিকরণ সূত্র",
            titleEn = "Midpoint & Section Formula",
            formulaExpression = "মধ্যবিন্দু M = ((x₁ + x₂) / 2, (y₁ + y₂) / 2)",
            latexExpression = "M = \\left(\\frac{x_1 + x_2}{2}, \\frac{y_1 + y_2}{2}\\right)",
            plainTextExpression = "M = ((x1 + x2)/2, (y1 + y2)/2)",
            explanationBn = "দুটি বিন্দুর সংযোগকারী সরলরেখাংশের ঠিক মধ্যবর্তী বিন্দুর স্থানাঙ্ক নির্ণয়।",
            variables = listOf(
                "(x₁, y₁), (x₂, y₂)" to "প্রান্তবিন্দুদ্বয়"
            ),
            exampleProblemBn = "P(2, 8) এবং Q(6, 4) এর মধ্যবিন্দু কত?",
            exampleSolutionBn = "M = ((2 + 6)/2, (8 + 4)/2) = (8/2, 12/2) = (4, 6)",
            tipsBn = "m:n অনুপাতে অন্তর্বিভক্তকারী বিন্দুর স্থানাঙ্ক: ((mx₂ + nx₁)/(m+n), (my₂ + ny₁)/(m+n))।",
            tags = listOf("মধ্যবিন্দু", "midpoint", "section formula", "স্থানাঙ্ক"),
            relatedFormulaIds = listOf("lib_geom_14", "lib_coord_01")
        ),
        LibraryFormula(
            id = "lib_geom_16",
            subject = FormulaSubject.GEOMETRY,
            subCategory = "স্থানাঙ্ক জ্যামিতি",
            titleBn = "সরলরেখার ঢাল ও সমীকরণ",
            titleEn = "Slope & Equation of a Straight Line",
            formulaExpression = "ঢাল m = (y₂ − y₁) / (x₂ − x₁),  সমীকরণ: y = mx + c  বা  y − y₁ = m(x − x₁)",
            latexExpression = "m = \\frac{y_2 - y_1}{x_2 - x_1}, \\quad y = mx + c",
            plainTextExpression = "m = (y2 - y1)/(x2 - x1), y = mx + c",
            explanationBn = "ঢাল হলো x-অক্ষের সাথে রেখাটির খাড়া হওয়ার মাত্রা (tan θ)। m হলো ঢাল এবং c হলো y-অক্ষের ছেদাংশ।",
            variables = listOf(
                "m" to "সরলরেখার ঢাল (Gradient/Slope)",
                "c" to "y-অক্ষের ছেদাংশ (y-intercept)"
            ),
            conditionsBn = "ঢাল নির্ণয়ে x₁ ≠ x₂ হতে হবে (উল্লম্ব রেখার ঢাল অসংজ্ঞায়িত)।",
            exampleProblemBn = "(1, 3) ও (3, 7) বিন্দুগামী রেখার ঢাল কত?",
            exampleSolutionBn = "m = (7 − 3) / (3 − 1) = 4 / 2 = 2\nরেখাটির সমীকরণ: y − 3 = 2(x − 1) ⇒ y = 2x + 1",
            tipsBn = "দুটি রেখা সমান্তরাল হলে তাদের ঢাল সমান (m₁ = m₂)। লম্ব হলে ঢালদ্বয়ের গুণফল −1 (m₁ · m₂ = −1)।",
            tags = listOf("ঢাল", "সরলরেখা", "slope", "straight line", "equation"),
            relatedFormulaIds = listOf("lib_geom_14", "lib_coord_02")
        )
    )

    // -------------------------------------------------------------
    // 5. MENSURATION (পরিমিতি)
    // -------------------------------------------------------------
    val mensurationFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_mens_01",
            subject = FormulaSubject.MENSURATION,
            subCategory = "চার দেয়াল ও পথের ক্ষেত্রফল",
            titleBn = "ঘরের চার দেয়ালের ক্ষেত্রফল নির্ণয়ের সূত্র",
            titleEn = "Area of Four Walls of a Room",
            formulaExpression = "চার দেয়ালের ক্ষেত্রফল = 2 × (দৈর্ঘ্য + প্রস্থ) × উচ্চতা = পরিসীমা × উচ্চতা",
            latexExpression = "A_{\\text{walls}} = 2(l + b)h",
            plainTextExpression = "Area of 4 walls = 2 * (length + width) * height",
            explanationBn = "একটি আয়তাকার কক্ষের মেঝের পরিসীমাকে কক্ষটির উচ্চতা দ্বারা গুণ করলে চার দেয়ালের মোট ক্ষেত্রফল পাওয়া যায়।",
            variables = listOf(
                "l" to "ঘরের দৈর্ঘ্য",
                "b" to "ঘরের প্রস্থ",
                "h" to "ঘরের উচ্চতা"
            ),
            exampleProblemBn = "একটি ঘরের দৈর্ঘ্য ১০ মি, প্রস্থ ৮ মি এবং উচ্চতা ৪ মি হলে চার দেয়ালের ক্ষেত্রফল কত?",
            exampleSolutionBn = "ক্ষেত্রফল = ২ × (১০ + ৮) × ৪ = ২ × ১৮ × ৪ = ১৪৪ বর্গ মিটার",
            tipsBn = "ছাদ ও মেঝেসহ সমগ্রতলের ক্ষেত্রফল = 2(lb + bh + hl)।",
            tags = listOf("পরিমিতি", "চার দেয়াল", "mensuration", "room walls"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_geom_05", "lib_geom_13")
        ),
        LibraryFormula(
            id = "lib_mens_02",
            subject = FormulaSubject.MENSURATION,
            subCategory = "বৃত্তাকার বলয় ও প্রিজম",
            titleBn = "বৃত্তাকার বলয় (Ring) ও সুষম প্রিজমের আয়তন",
            titleEn = "Circular Ring Area & Prism Volume",
            formulaExpression = "বলয়ের ক্ষেত্রফল = π(R² − r²) = π(R + r)(R − r)  |  প্রিজমের আয়তন = ভূমির ক্ষেত্রফল × উচ্চতা",
            latexExpression = "A_{\\text{ring}} = \\pi(R^2 - r^2), \\quad V_{\\text{prism}} = A_{\\text{base}} \\times h",
            plainTextExpression = "Ring Area = pi*(R^2 - r^2), Prism Volume = Base Area * height",
            explanationBn = "বৃত্তাকার মাঠের চারপাশের রাস্তার ক্ষেত্রফল নির্ণয়ে π(R² − r²) ব্যবহৃত হয় যেখানে R বহিঃব্যাসার্ধ এবং r অন্তঃব্যাসার্ধ।",
            variables = listOf(
                "R" to "বহিঃস্থ ব্যাসার্ধ (Outer Radius)",
                "r" to "অন্তঃস্থ ব্যাসার্ধ (Inner Radius)"
            ),
            conditionsBn = "R > r > 0।",
            exampleProblemBn = "বহিঃব্যাসার্ধ ৮ মি ও অন্তঃব্যাসার্ধ ৬ মি হলে বৃত্তাকার পথের ক্ষেত্রফল কত?",
            exampleSolutionBn = "ক্ষেত্রফল = π(8² − 6²) = (22/7) × (64 − 36) = (22/7) × 28 = 88 বর্গ মিটার",
            tipsBn = "R² − r² কে (R+r)(R−r) আকারে ভেঙে নিলে বর্গ ছাড়াই দ্রুত হিসাব করা যায়।",
            tags = listOf("বলয়", "প্রিজম", "ring area", "prism", "mensuration"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_geom_08", "lib_geom_11")
        )
    )

    // -------------------------------------------------------------
    // 6. TRIGONOMETRY (ত্রিকোণমিতি)
    // -------------------------------------------------------------
    val trigonometryFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_trig_01",
            subject = FormulaSubject.TRIGONOMETRY,
            subCategory = "ত্রিকোণমিতিক অনুপাত ও অভেদ",
            titleBn = "ত্রিকোণমিতির মৌলিক পিথাগোরাসীয় অভেদত্রয়",
            titleEn = "Pythagorean Trigonometric Identities",
            formulaExpression = "sin²θ + cos²θ = 1  |  sec²θ − tan²θ = 1  |  csc²θ − cot²θ = 1",
            latexExpression = "\\sin^2\\theta + \\cos^2\\theta = 1, \\quad 1 + \\tan^2\\theta = \\sec^2\\theta, \\quad 1 + \\cot^2\\theta = \\csc^2\\theta",
            plainTextExpression = "sin^2(theta) + cos^2(theta) = 1, sec^2(theta) - tan^2(theta) = 1",
            explanationBn = "সমকোণী ত্রিভুজের পিথাগোরাসের উপপাদ্য থেকে প্রাপ্ত ত্রিকোণমিতির সবচেয়ে মৌলিক তিনটি অভেদ।",
            variables = listOf(
                "θ" to "সূক্ষ্মকোণ বা যেকোনো সংজ্ঞায়িত কোণ",
                "sin θ" to "লম্ব / অতিভুজ",
                "cos θ" to "ভূমি / অতিভুজ",
                "tan θ" to "লম্ব / ভূমি = sin θ / cos θ"
            ),
            exampleProblemBn = "যদি sec θ + tan θ = 5 হয়, তবে sec θ − tan θ এর মান কত?",
            exampleSolutionBn = "আমরা জানি, sec²θ − tan²θ = 1\n⇒ (sec θ + tan θ)(sec θ − tan θ) = 1\n⇒ 5 × (sec θ − tan θ) = 1 ⇒ sec θ − tan θ = 1/5 = 0.2",
            tipsBn = "sec²θ − tan²θ = 1 থেকে (sec θ + tan θ) এবং (sec θ − tan θ) পরস্পর গুণাত্মক বিপরীত।",
            tags = listOf("ত্রিকোণমিতি", "sin", "cos", "tan", "trigonometric identities"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_geom_02", "lib_trig_02")
        ),
        LibraryFormula(
            id = "lib_trig_02",
            subject = FormulaSubject.TRIGONOMETRY,
            subCategory = "যৌগিক ও গুণিতক কোণ",
            titleBn = "যৌগিক কোণ ও দ্বিগুণ কোণের ত্রিকোণমিতিক সূত্র",
            titleEn = "Compound & Double Angle Formulas",
            formulaExpression = "sin(A±B) = sinA cosB ± cosA sinB  |  sin 2A = 2 sinA cosA  |  cos 2A = cos²A − sin²A",
            latexExpression = "\\sin(A\\pm B) = \\sin A\\cos B \\pm \\cos A\\sin B, \\quad \\sin 2A = 2\\sin A\\cos A",
            plainTextExpression = "sin(A+B) = sinA cosB + cosA sinB, sin(2A) = 2 sinA cosA",
            explanationBn = "দুটি কোণের সমষ্টি বা অন্তর এবং দ্বিগুণ কোণের ত্রিকোণমিতিক অনুপাত নির্ণয়ের সূত্র।",
            variables = listOf(
                "A, B" to "যেকোনো দুটি কোণ",
                "tan(A+B)" to "(tan A + tan B) / (1 − tan A tan B)"
            ),
            exampleProblemBn = "যদি sin A = 3/5 এবং cos A = 4/5 হয়, তবে sin 2A এর মান কত?",
            exampleSolutionBn = "sin 2A = 2 sin A cos A = 2 × (3/5) × (4/5) = 24/25",
            tipsBn = "cos 2A এর তিনটি রূপ: cos²A − sin²A = 2cos²A − 1 = 1 − 2sin²A।",
            tags = listOf("যৌগিক কোণ", "sin 2A", "cos 2A", "double angle"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_trig_01")
        )
    )

    // -------------------------------------------------------------
    // 7. COORDINATE GEOMETRY (স্থানাঙ্ক জ্যামিতি)
    // -------------------------------------------------------------
    val coordinateGeometryFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_coord_01",
            subject = FormulaSubject.COORDINATE_GEOMETRY,
            subCategory = "ত্রিভুজের ক্ষেত্রফল ও ভরকেন্দ্র",
            titleBn = "স্থানাঙ্ক হতে ত্রিভুজের ক্ষেত্রফল ও ভরকেন্দ্র নির্ণয়",
            titleEn = "Triangle Area & Centroid from Coordinates",
            formulaExpression = "ভরকেন্দ্র G = ((x₁+x₂+x₃)/3, (y₁+y₂+y₃)/3)  |  ক্ষেত্রফল = ½ |x₁(y₂−y₃) + x₂(y₃−y₁) + x₃(y₁−y₂)|",
            latexExpression = "G = \\left(\\frac{x_1+x_2+x_3}{3}, \\frac{y_1+y_2+y_3}{3}\\right)",
            plainTextExpression = "G = ((x1+x2+x3)/3, (y1+y2+y3)/3)",
            explanationBn = "ত্রিভুজের তিনটি শীর্ষবিন্দুর স্থানাঙ্ক দেওয়া থাকলে সরাসরি এর ভরকেন্দ্র এবং ক্ষেত্রফল নির্ণয় করা যায়।",
            variables = listOf(
                "(x₁,y₁), (x₂,y₂), (x₃,y₃)" to "ত্রিভুজের তিনটি শীর্ষবিন্দু",
                "G" to "ত্রিভুজের ভরকেন্দ্র (Centroid)"
            ),
            conditionsBn = "ক্ষেত্রফল শূন্য (০) হলে বিন্দু তিনটি সমরেখ (Collinear)।",
            exampleProblemBn = "(0, 0), (4, 0) এবং (0, 3) শীর্ষবিশিষ্ট ত্রিভুজের ক্ষেত্রফল ও ভরকেন্দ্র কত?",
            exampleSolutionBn = "ক্ষেত্রফল = ½ |0 + 4(3 − 0) + 0| = ½ × 12 = 6 বর্গ একক\nভরকেন্দ্র G = ((0+4+0)/3, (0+0+3)/3) = (4/3, 1)",
            tipsBn = "তিনটি বিন্দু সমরেখ হওয়ার শর্ত হলো তাদের দ্বারা গঠিত ত্রিভুজের ক্ষেত্রফল = ০।",
            tags = listOf("ভরকেন্দ্র", "স্থানাঙ্ক", "centroid", "collinear"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_geom_14", "lib_geom_15", "lib_geom_16")
        ),
        LibraryFormula(
            id = "lib_coord_02",
            subject = FormulaSubject.COORDINATE_GEOMETRY,
            subCategory = "সরলরেখা ও ছেদক",
            titleBn = "অক্ষদ্বয়কে ছেদকারী সরলরেখার সমীকরণ ও লম্ব দূরত্ব",
            titleEn = "Intercept Form & Point-to-Line Distance",
            formulaExpression = "ছেদক আকার: x/a + y/b = 1  |  বিন্দু (x₁,y₁) হতে ax+by+c=0 এর দূরত্ব = |ax₁+by₁+c| / √(a²+b²)",
            latexExpression = "\\frac{x}{a} + \\frac{y}{b} = 1, \\quad d = \\frac{|ax_1 + by_1 + c|}{\\sqrt{a^2 + b^2}}",
            plainTextExpression = "x/a + y/b = 1, d = |ax1 + by1 + c| / sqrt(a^2 + b^2)",
            explanationBn = "সরলরেখাটি x-অক্ষকে (a,0) এবং y-অক্ষকে (0,b) বিন্দুতে ছেদ করলে তার সমীকরণ x/a + y/b = 1।",
            variables = listOf(
                "a, b" to "যথাক্রমে x-অক্ষ ও y-অক্ষের খণ্ডিতাংশ",
                "d" to "লম্ব দূরত্ব"
            ),
            conditionsBn = "a ≠ 0, b ≠ 0 এবং দূরত্ব সূত্রে a² + b² > 0।",
            exampleProblemBn = "(3, 4) বিন্দু থেকে 3x + 4y − 10 = 0 রেখার লম্ব দূরত্ব কত?",
            exampleSolutionBn = "d = |3(3) + 4(4) − 10| / √(3² + 4²) = |9 + 16 − 10| / √25 = 15 / 5 = 3 একক",
            tipsBn = "অক্ষদ্বয়ের সাথে উৎপন্ন ত্রিভুজের ক্ষেত্রফল = ½ |ab| বর্গ একক।",
            tags = listOf("ছেদক আকার", "লম্ব দূরত্ব", "intercept form", "perpendicular distance"),
            difficultyLevel = FormulaDifficulty.ADVANCED,
            relatedFormulaIds = listOf("lib_geom_16")
        )
    )

    // -------------------------------------------------------------
    // 8. STATISTICS (পরিসংখ্যান)
    // -------------------------------------------------------------
    val statisticsFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_stat_01",
            subject = FormulaSubject.STATISTICS,
            subCategory = "কেন্দ্রীয় প্রবণতা",
            titleBn = "গাণিতিক গড়, মধ্যক ও প্রচুরক (Central Tendency)",
            titleEn = "Mean, Median & Mode",
            formulaExpression = "গড় x̄ = (∑fᵢxᵢ)/N  |  বিজোড় n-এ মধ্যক = ((n+1)/2)-তম পদ  |  প্রচুরক ≈ 3×মধ্যক − 2×গড়",
            latexExpression = "\\bar{x} = \\frac{\\sum f_i x_i}{N}, \\quad \\text{Mode} \\approx 3\\text{Median} - 2\\text{Mean}",
            plainTextExpression = "Mean = Sum(f_i * x_i) / N, Mode = 3*Median - 2*Mean",
            explanationBn = "উপাত্তসমূহকে মানের ক্রমানুসারে সাজালে ঠিক মাঝের মানটি মধ্যক এবং সর্বাধিকবার পুনরাবৃত্ত মানটি প্রচুরক।",
            variables = listOf(
                "x̄" to "গাণিতিক গড় (Arithmetic Mean)",
                "n" to "উপাত্তের সংখ্যা",
                "fᵢ" to "গণসংখ্যা (Frequency)"
            ),
            conditionsBn = "মধ্যক নির্ণয়ের আগে উপাত্তগুলোকে অবশ্যই মানের ঊর্ধ্বক্রমে বা অধঃক্রমে সাজাতে হবে।",
            exampleProblemBn = "৫, ২, ৯, ৭, ৪ উপাত্তগুলোর মধ্যক কত?",
            exampleSolutionBn = "মানের ঊর্ধ্বক্রমে সাজিয়ে পাই: ২, ৪, ৫, ৭, ৯\nপদসংখ্যা n = ৫ (বিজোড়)\nমধ্যক = (৫+১)/২ = ৩য় পদ = ৫",
            tipsBn = "n জোড় হলে মধ্যক = [ (n/2)-তম পদ + (n/2 + 1)-তম পদ ] ÷ ২।",
            tags = listOf("পরিসংখ্যান", "মধ্যক", "প্রচুরক", "mean", "median", "mode"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_arith_01", "lib_stat_02")
        ),
        LibraryFormula(
            id = "lib_stat_02",
            subject = FormulaSubject.STATISTICS,
            subCategory = "বিস্তার পরিমাপ",
            titleBn = "ভেদাঙ্ক (Variance) ও পরিমিত ব্যবধান (Standard Deviation)",
            titleEn = "Variance & Standard Deviation",
            formulaExpression = "ভেদাঙ্ক σ² = ∑(xᵢ − x̄)² / n   |   পরিমিত ব্যবধান σ = √[ ∑(xᵢ − x̄)² / n ]",
            latexExpression = "\\sigma^2 = \\frac{\\sum (x_i - \\bar{x})^2}{n}, \\quad \\sigma = \\sqrt{\\sigma^2}",
            plainTextExpression = "Variance = Sum((x_i - mean)^2) / n, SD = sqrt(Variance)",
            explanationBn = "গড় মান থেকে উপাত্তগুলোর বিচ্যুতির বর্গের গড়কে ভেদাঙ্ক এবং ভেদাঙ্কের ধনাত্মক বর্গমূলকে পরিমিত ব্যবধান বলে।",
            variables = listOf(
                "σ²" to "ভেদাঙ্ক (Variance)",
                "σ" to "পরিমিত ব্যবধান (Standard Deviation)",
                "x̄" to "উপাত্তের গড় মান"
            ),
            conditionsBn = "ভেদাঙ্ক ও পরিমিত ব্যবধান সর্বদা অঋণাত্মক (σ ≥ 0)।",
            exampleProblemBn = "কোনো নিবেশনের ভেদাঙ্ক 16 হলে এর পরিমিত ব্যবধান কত?",
            exampleSolutionBn = "পরিমিত ব্যবধান σ = √(ভেদাঙ্ক) = √16 = 4",
            tipsBn = "প্রথম n সংখ্যক স্বাভাবিক সংখ্যার ভেদাঙ্ক = (n² − 1) / 12।",
            tags = listOf("ভেদাঙ্ক", "পরিমিত ব্যবধান", "variance", "standard deviation"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_stat_01")
        )
    )

    // -------------------------------------------------------------
    // 9. PROBABILITY (সম্ভাব্যতা)
    // -------------------------------------------------------------
    val probabilityFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_prob_01",
            subject = FormulaSubject.PROBABILITY,
            subCategory = "মৌলিক সম্ভাব্যতা",
            titleBn = "সম্ভাব্যতার মৌলিক সূত্র ও পূরক ঘটনা",
            titleEn = "Classical Probability & Complement Rule",
            formulaExpression = "P(A) = n(A) / n(S),  যেখানে 0 ≤ P(A) ≤ 1  |  P(A') = 1 − P(A)",
            latexExpression = "P(A) = \\frac{n(A)}{n(S)}, \\quad P(A^c) = 1 - P(A)",
            plainTextExpression = "P(A) = n(A) / n(S), P(not A) = 1 - P(A)",
            explanationBn = "কোনো ঘটনার অনুকূল ফলাফল সংখ্যাকে সমগ্র সম্ভাব্য ফলাফল (নমুনাক্ষেত্র) দ্বারা ভাগ করলে ঘটনাটির সম্ভাব্যতা পাওয়া যায়।",
            variables = listOf(
                "n(A)" to "ঘটনা A-এর অনুকূল ফলাফল সংখ্যা",
                "n(S)" to "মোট নমুনাবিন্দু বা সমগ্র সম্ভাব্য ফলাফল",
                "P(A')" to "ঘটনাটি না ঘটার সম্ভাব্যতা"
            ),
            conditionsBn = "সম্ভাব্যতার মান সর্বদা ০ থেকে ১ এর মধ্যে থাকে (0 ≤ P(A) ≤ 1)। নিশ্চিত ঘটনার সম্ভাব্যতা ১ এবং অসম্ভব ঘটনার সম্ভাব্যতা ০।",
            exampleProblemBn = "একটি নিরপেক্ষ ছক্কা নিক্ষেপ করলে মৌলিক সংখ্যা উঠার সম্ভাব্যতা কত?",
            exampleSolutionBn = "নমুনাক্ষেত্র S = {1, 2, 3, 4, 5, 6}, n(S) = 6\nমৌলিক সংখ্যা A = {2, 3, 5}, n(A) = 3\nP(A) = 3 / 6 = 1/2 = 0.5",
            tipsBn = "n টি মুদ্রা একত্রে নিক্ষেপ করলে মোট ফলাফল = 2ⁿ এবং n টি ছক্কা নিক্ষেপ করলে মোট ফলাফল = 6ⁿ।",
            tags = listOf("সম্ভাব্যতা", "ছক্কা", "মুদ্রা", "probability", "sample space"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_prob_02")
        ),
        LibraryFormula(
            id = "lib_prob_02",
            subject = FormulaSubject.PROBABILITY,
            subCategory = "বিন্যাস ও সমাবেশ",
            titleBn = "বিন্যাস (Permutation) ও সমাবেশ (Combination) এবং সংযোগ বিধি",
            titleEn = "Permutations, Combinations & Addition Rule",
            formulaExpression = "ⁿPᵣ = n! / (n − r)!  |  ⁿCᵣ = n! / [r!(n − r)!]  |  P(A∪B) = P(A) + P(B) − P(A∩B)",
            latexExpression = "^nC_r = \\frac{n!}{r!(n-r)!}, \\quad P(A\\cup B) = P(A)+P(B)-P(A\\cap B)",
            plainTextExpression = "nCr = n! / (r! * (n-r)!), P(A U B) = P(A) + P(B) - P(A and B)",
            explanationBn = "ক্রম বিবেচনা করে সাজানোকে বিন্যাস (ⁿPᵣ) এবং ক্রম বিবেচনা না করে দল গঠনকে সমাবেশ (ⁿCᵣ) বলে।",
            variables = listOf(
                "n!" to "ফ্যাক্টরিয়াল: n × (n−1) × ... × 1 (এবং 0! = 1)",
                "ⁿCᵣ" to "n টি বস্তু হতে r টি নিয়ে সমাবেশ সংখ্যা"
            ),
            conditionsBn = "n ≥ r ≥ 0 পূর্ণসংখ্যা। স্বাধীন ঘটনার ক্ষেত্রে P(A∩B) = P(A) · P(B)।",
            exampleProblemBn = "৫ জন খেলোয়াড় থেকে ৩ জনের একটি দল কতভাবে গঠন করা যায়?",
            exampleSolutionBn = "⁵C₃ = 5! / (3! × 2!) = (5 × 4) / (2 × 1) = 10 ভাবে",
            tipsBn = "হ্যান্ডশেক বা করমর্দন সংখ্যা = ⁿC₂ = n(n − 1) / 2।",
            tags = listOf("বিন্যাস", "সমাবেশ", "permutation", "combination", "nCr"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_prob_01")
        )
    )

    // -------------------------------------------------------------
    // 10. SEQUENCES AND SERIES (অনুক্রম ও ধারা)
    // -------------------------------------------------------------
    val sequencesSeriesFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_seq_01",
            subject = FormulaSubject.SEQUENCES_SERIES,
            subCategory = "সমান্তর ও গুণোত্তর মধ্যক",
            titleBn = "সমান্তর মধ্যক (AM) ও গুণোত্তর মধ্যক (GM)",
            titleEn = "Arithmetic Mean & Geometric Mean of Two Numbers",
            formulaExpression = "সমান্তর মধ্যক AM = (a + b) / 2   |   গুণোত্তর মধ্যক GM = √(ab)   |   AM ≥ GM",
            latexExpression = "\\text{AM} = \\frac{a+b}{2}, \\quad \\text{GM} = \\sqrt{ab}, \\quad \\text{AM} \\ge \\text{GM}",
            plainTextExpression = "AM = (a + b)/2, GM = sqrt(a * b)",
            explanationBn = "দুটি ধনাত্মক সংখ্যার সমান্তর মধ্যক সর্বদা তাদের গুণোত্তর মধ্যক অপেক্ষা বৃহত্তর বা সমান হয়।",
            variables = listOf(
                "a, b" to "দুটি ধনাত্মক বাস্তব সংখ্যা",
                "HM" to "বিপরীত মধ্যক = 2ab / (a + b)"
            ),
            conditionsBn = "গুণোত্তর মধ্যকের জন্য a ≥ 0, b ≥ 0 হতে হবে।",
            exampleProblemBn = "4 এবং 16 এর সমান্তর মধ্যক ও গুণোত্তর মধ্যক কত?",
            exampleSolutionBn = "সমান্তর মধ্যক AM = (4 + 16)/2 = 10\nগুণোত্তর মধ্যক GM = √(4 × 16) = √64 = 8",
            tipsBn = "সম্পর্ক: GM² = AM × HM।",
            tags = listOf("অনুক্রম", "ধারা", "সমান্তর মধ্যক", "গুণোত্তর মধ্যক", "AM GM"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_alg_15", "lib_alg_16", "lib_seq_02")
        ),
        LibraryFormula(
            id = "lib_seq_02",
            subject = FormulaSubject.SEQUENCES_SERIES,
            subCategory = "অসীম গুণোত্তর ধারা",
            titleBn = "অসীম গুণোত্তর ধারার সমষ্টি (Infinite GP Sum)",
            titleEn = "Sum to Infinity of a Geometric Series",
            formulaExpression = "S∞ = a / (1 − r),   যখন |r| < 1  (অর্থাৎ −1 < r < 1)",
            latexExpression = "S_\\infty = \\frac{a}{1 - r} \\quad (|r| < 1)",
            plainTextExpression = "S_inf = a / (1 - r) for |r| < 1",
            explanationBn = "গুণোত্তর ধারার সাধারণ অনুপাত r এর পরম মান ১ অপেক্ষা ছোট হলে অসীম সংখ্যক পদের একটি নির্দিষ্ট সসীম সমষ্টি থাকে।",
            variables = listOf(
                "a" to "প্রথম পদ",
                "r" to "সাধারণ অনুপাত (|r| < 1)"
            ),
            conditionsBn = "|r| ≥ 1 হলে অসীম গুণোত্তর ধারাটির কোনো সসীম সমষ্টি থাকে না (অভিসারী নয়)।",
            exampleProblemBn = "1 + 1/2 + 1/4 + 1/8 + ... অসীম ধারাটির সমষ্টি কত?",
            exampleSolutionBn = "এখানে প্রথম পদ a = 1, সাধারণ অনুপাত r = 1/2 (< 1)\nঅসীমতক সমষ্টি S∞ = 1 / (1 − 1/2) = 1 / (1/2) = 2",
            tipsBn = "পৌনঃপুনিক দশমিককে সাধারণ ভগ্নাংশে রূপান্তর করতে এই সূত্র ব্যবহৃত হয়।",
            tags = listOf("অসীম ধারা", "infinite series", "convergent GP"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_alg_16", "lib_seq_01")
        )
    )

    // -------------------------------------------------------------
    // 11. COMMERCIAL MATHEMATICS (বাণিজ্যিক গণিত)
    // -------------------------------------------------------------
    val commercialMathFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_comm_01",
            subject = FormulaSubject.COMMERCIAL_MATH,
            subCategory = "লাভ ও ক্ষতি",
            titleBn = "শতকরা লাভ ও ক্ষতির সূত্র",
            titleEn = "Profit and Loss Percentage Formulas",
            formulaExpression = "লাভ = বিক্রয়মূল্য − ক্রয়মূল্য  |  শতকরা লাভ/ক্ষতি = (লাভ বা ক্ষতি ÷ ক্রয়মূল্য) × 100%",
            latexExpression = "\\text{Profit \\%} = \\frac{\\text{SP} - \\text{CP}}{\\text{CP}} \\times 100\\%",
            plainTextExpression = "Profit % = ((SP - CP) / CP) * 100%",
            explanationBn = "লাভ বা ক্ষতি সর্বদা ক্রয়মূল্যের (Cost Price) ওপর হিসাব করা হয়, যদি না প্রশ্নে অন্য কিছু উল্লেখ থাকে।",
            variables = listOf(
                "CP" to "ক্রয়মূল্য (Cost Price)",
                "SP" to "বিক্রয়মূল্য (Selling Price)",
                "MP" to "ধার্যমূল্য বা তালিকা মূল্য (Marked Price)"
            ),
            conditionsBn = "ক্রয়মূল্য CP > 0 হতে হবে। বাট্টা বা ছাড় সর্বদা ধার্যমূল্যের (MP) ওপর হিসাব হয়।",
            exampleProblemBn = "৫০০ টাকায় একটি বই কিনে ৫৫০ টাকায় বিক্রয় করলে শতকরা কত লাভ হবে?",
            exampleSolutionBn = "লাভ = ৫৫০ − ৫০০ = ৫০ টাকা\nশতকরা লাভ = (৫০ ÷ ৫০০) × ১০০% = ১০%",
            tipsBn = "ক্রমিক x% ও y% পরিবর্তনের নিট শতকরা পরিবর্তন = (x + y + xy/100)%।",
            tags = listOf("লাভ", "ক্ষতি", "শতকরা", "profit", "loss", "discount"),
            difficultyLevel = FormulaDifficulty.BASIC,
            relatedFormulaIds = listOf("lib_comm_02")
        ),
        LibraryFormula(
            id = "lib_comm_02",
            subject = FormulaSubject.COMMERCIAL_MATH,
            subCategory = "সরল ও চক্রবৃদ্ধি মুনাফা",
            titleBn = "সরল মুনাফা ও চক্রবৃদ্ধি মুনাফার সূত্র",
            titleEn = "Simple & Compound Interest Formulas",
            formulaExpression = "সরল মুনাফা I = Pnr / 100   |   চক্রবৃদ্ধি সবৃদ্ধিমূল C = P(1 + r/100)ⁿ",
            latexExpression = "I = \\frac{Pnr}{100}, \\quad C = P\\left(1 + \\frac{r}{100}\\right)^n",
            plainTextExpression = "I = (P * n * r) / 100, C = P * (1 + r/100)^n",
            explanationBn = "প্রারম্ভিক মূলধনের ওপর প্রতি বছর সমান হারে যে মুনাফা গণনা করা হয় তা সরল মুনাফা; আর মুনাফা-আসলের ওপর মুনাফা হলে তা চক্রবৃদ্ধি মুনাফা।",
            variables = listOf(
                "P" to "আসল বা মূলধন (Principal)",
                "n" to "সময় (বছর)",
                "r" to "বার্ষিক শতকরা মুনাফার হার (%)",
                "C − P" to "চক্রবৃদ্ধি মুনাফা"
            ),
            conditionsBn = "P > 0, n ≥ 0, r ≥ 0।",
            exampleProblemBn = "বার্ষিক ১০% মুনাফায় ২০০০ টাকার ২ বছরের সরল ও চক্রবৃদ্ধি মুনাফার পার্থক্য কত?",
            exampleSolutionBn = "২ বছরের পার্থক্য = P(r/100)² = ২০০০ × (১০/১০০)² = ২০০০ × (১/১০০) = ২০ টাকা",
            tipsBn = "২ বছরের চক্রবৃদ্ধি ও সরল মুনাফার পার্থক্যের সরাসরি শর্টকাট: পার্থক্য = P × (r / 100)²।",
            tags = listOf("সরল মুনাফা", "চক্রবৃদ্ধি", "সুদকষা", "simple interest", "compound interest"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_comm_01")
        )
    )

    // -------------------------------------------------------------
    // 12. CALCULUS (ক্যালকুলাস) — 15 Formulas
    // -------------------------------------------------------------
    val calculusFormulas: List<LibraryFormula> = listOf(
        LibraryFormula(
            id = "lib_calc_01",
            subject = FormulaSubject.CALCULUS,
            subCategory = "অন্তরীকরণ (Derivatives)",
            titleBn = "বীজগণিতীয় ঘাত নিয়ম (Power Rule)",
            titleEn = "Power Rule of Differentiation",
            formulaExpression = "d/dx (xⁿ) = n · xⁿ⁻¹",
            latexExpression = "\\frac{d}{dx}(x^n) = n x^{n-1}",
            plainTextExpression = "d/dx(x^n) = n * x^(n-1)",
            explanationBn = "যেকোনো চলকের ঘাতের অন্তরজ নির্ণয় করতে ঘাতটি সামনে সহগ হিসেবে গুণ হয় এবং নতুন ঘাত ১ কমে যায়।",
            variables = listOf(
                "x" to "স্বাধীন চলক",
                "n" to "যেকোনো বাস্তব সংখ্যা (ঘাত)"
            ),
            exampleProblemBn = "অন্তরজ নির্ণয় করো: d/dx (x⁴)",
            exampleSolutionBn = "d/dx (x⁴) = 4 · x⁴⁻¹ = 4x³",
            tipsBn = "যদি রুট থাকে: d/dx (√x) = d/dx (x¹/²) = (1/2)x⁻¹/² = 1 / (2√x)।",
            tags = listOf("অন্তরীকরণ", "ঘাত নিয়ম", "derivative", "power rule", "calculus"),
            relatedFormulaIds = listOf("lib_calc_02", "lib_calc_08")
        ),
        LibraryFormula(
            id = "lib_calc_02",
            subject = FormulaSubject.CALCULUS,
            subCategory = "অন্তরীকরণ (Derivatives)",
            titleBn = "ধ্রুবক ও ধ্রুব গুণিতক নিয়ম",
            titleEn = "Constant & Constant Multiple Rule",
            formulaExpression = "d/dx (c) = 0  এবং  d/dx [c · f(x)] = c · f'(x)",
            latexExpression = "\\frac{d}{dx}(c) = 0, \\quad \\frac{d}{dx}[c f(x)] = c f'(x)",
            plainTextExpression = "d/dx(c) = 0, d/dx[c * f(x)] = c * f'(x)",
            explanationBn = "স্বাধীন চলক ছাড়া যেকোনো একক ধ্রুবকের পরিবর্তনের হার শূন্য। সহগ হিসেবে ধ্রুবক থাকলে তা বাইরে এসে ফাংশনের অন্তরজের সাথে গুণ হয়।",
            variables = listOf(
                "c" to "যেকোনো বাস্তব ধ্রুব সংখ্যা",
                "f(x)" to "অন্তরীকরণযোগ্য ফাংশন"
            ),
            exampleProblemBn = "অন্তরজ নির্ণয় করো: d/dx (7x³ + 15)",
            exampleSolutionBn = "d/dx (7x³) + d/dx (15) = 7 · (3x²) + 0 = 21x²",
            tipsBn = "ধ্রুবক একা থাকলে তার অন্তরজ সর্বদা ০। যেমন d/dx(100) = 0।",
            tags = listOf("ধ্রুবক", "constant rule", "derivative"),
            relatedFormulaIds = listOf("lib_calc_01")
        ),
        LibraryFormula(
            id = "lib_calc_03",
            subject = FormulaSubject.CALCULUS,
            subCategory = "অন্তরীকরণ (Derivatives)",
            titleBn = "গুণফল নিয়ম (Product Rule)",
            titleEn = "Product Rule of Differentiation",
            formulaExpression = "d/dx (u · v) = u(dv/dx) + v(du/dx) = u v' + v u'",
            latexExpression = "\\frac{d}{dx}(uv) = u\\frac{dv}{dx} + v\\frac{du}{dx}",
            plainTextExpression = "d/dx(u*v) = u * dv/dx + v * du/dx",
            explanationBn = "দুটি ফাংশনের গুণফলের অন্তরজ = প্রথম ফাংশন × দ্বিতীয়টির অন্তরজ + দ্বিতীয় ফাংশন × প্রথমটির অন্তরজ।",
            variables = listOf(
                "u, v" to "x-এর দুটি অন্তরীকরণযোগ্য ফাংশন"
            ),
            exampleProblemBn = "d/dx (x² · sin x) এর মান কত?",
            exampleSolutionBn = "u = x², v = sin x\n= x² · d/dx(sin x) + sin x · d/dx(x²)\n= x² · cos x + 2x · sin x",
            tipsBn = "মনে রাখবেন: (uv)' কিন্তু u'v' নয়। অবশ্যই যোগফল আকারে লিখতে হবে।",
            tags = listOf("গুণফল নিয়ম", "product rule", "derivative"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_calc_04")
        ),
        LibraryFormula(
            id = "lib_calc_04",
            subject = FormulaSubject.CALCULUS,
            subCategory = "অন্তরীকরণ (Derivatives)",
            titleBn = "ভাগফল নিয়ম (Quotient Rule)",
            titleEn = "Quotient Rule of Differentiation",
            formulaExpression = "d/dx (u / v) = [v(du/dx) − u(dv/dx)] / v² = [v u' − u v'] / v²",
            latexExpression = "\\frac{d}{dx}\\left(\\frac{u}{v}\\right) = \\frac{v u' - u v'}{v^2}",
            plainTextExpression = "d/dx(u/v) = (v*u' - u*v') / v^2",
            explanationBn = "দুটি ফাংশনের ভাগফলের অন্তরজ: [হর × লবের অন্তরজ − লব × হরের অন্তরজ] ভাগ (হরের বর্গ)।",
            variables = listOf(
                "u" to "লব ফাংশন",
                "v" to "হর ফাংশন (v ≠ 0)"
            ),
            conditionsBn = "হর ফাংশন v(x) ≠ 0 হতে হবে।",
            exampleProblemBn = "d/dx (x / (x + 1)) এর মান কত?",
            exampleSolutionBn = "u = x, v = x + 1\n= [(x + 1)·(1) − x·(1)] / (x + 1)²\n= [x + 1 − x] / (x + 1)² = 1 / (x + 1)²",
            tipsBn = "ক্রমটি খুব গুরুত্বপূর্ণ! লবের অন্তরজ আগে হবে: v u' − u v'। আগে u v' লিখলে চিহ্ন উল্টে ভুল হবে।",
            tags = listOf("ভাগফল নিয়ম", "quotient rule", "derivative"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE,
            relatedFormulaIds = listOf("lib_calc_03")
        ),
        LibraryFormula(
            id = "lib_calc_05",
            subject = FormulaSubject.CALCULUS,
            subCategory = "অন্তরীকরণ (Derivatives)",
            titleBn = "শৃঙ্খল নিয়ম (Chain Rule)",
            titleEn = "Chain Rule for Composite Functions",
            formulaExpression = "dy/dx = (dy/du) · (du/dx)  বা  d/dx [f(g(x))] = f'(g(x)) · g'(x)",
            latexExpression = "\\frac{dy}{dx} = \\frac{dy}{du} \\cdot \\frac{du}{dx}",
            plainTextExpression = "dy/dx = (dy/du) * (du/dx)",
            explanationBn = "ফাংশনের ভেতর অন্য ফাংশন (সংযুক্ত ফাংশন) থাকলে বাইরের ফাংশনটির অন্তরজ করে ভিতরের ফাংশনের অন্তরজ দিয়ে গুণ করতে হয়।",
            variables = listOf(
                "y = f(u)" to "বহিঃস্থ ফাংশন",
                "u = g(x)" to "অন্তঃস্থ ফাংশন"
            ),
            exampleProblemBn = "d/dx [sin(3x²)] এর মান কত?",
            exampleSolutionBn = "বাইরের ফাংশন sin(u) এর অন্তরজ cos(u)\nভিতরের ফাংশন 3x² এর অন্তরজ 6x\n= cos(3x²) · 6x = 6x cos(3x²)",
            tipsBn = "সব সময় বাইরে থেকে ভেতরে স্তরে স্তরে অন্তরীকরণ করতে হয়।",
            tags = listOf("শৃঙ্খল নিয়ম", "chain rule", "composite function"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE
        ),
        LibraryFormula(
            id = "lib_calc_06",
            subject = FormulaSubject.CALCULUS,
            subCategory = "অন্তরীকরণ (Derivatives)",
            titleBn = "সূচকীয় ও লগারিদমিক অপেক্ষকের অন্তরজ",
            titleEn = "Exponential & Logarithmic Derivatives",
            formulaExpression = "d/dx (eˣ) = eˣ,  d/dx (aˣ) = aˣ ln a,  d/dx (ln x) = 1/x",
            latexExpression = "\\frac{d}{dx}(e^x) = e^x, \\quad \\frac{d}{dx}(\\ln x) = \\frac{1}{x}",
            plainTextExpression = "d/dx(e^x) = e^x, d/dx(ln x) = 1/x",
            explanationBn = "স্বাভাবিক সূচক eˣ এর অন্তরজ হুবহু একই থাকে। প্রাকৃতিক লগারিদম ln x এর অন্তরজ হলো 1/x।",
            variables = listOf(
                "e" to "অয়লারের ধ্রুবক (প্রায় 2.718)",
                "ln x" to "e ভিত্তিক লগারিদম (x > 0)"
            ),
            conditionsBn = "ln x এর ক্ষেত্রে ডোমেন x > 0।",
            exampleProblemBn = "d/dx (e⁵ˣ + ln(4x)) এর মান কত?",
            exampleSolutionBn = "d/dx(e⁵ˣ) = 5e⁵ˣ\nd/dx(ln 4x) = 1/x\n= 5e⁵ˣ + 1/x",
            tipsBn = "ভিত্তি a হলে d/dx (logₐ x) = 1 / (x ln a)।",
            tags = listOf("সূচকীয়", "লগারিদমিক", "exponential", "logarithm", "e^x"),
            relatedFormulaIds = listOf("lib_calc_09")
        ),
        LibraryFormula(
            id = "lib_calc_07",
            subject = FormulaSubject.CALCULUS,
            subCategory = "অন্তরীকরণ (Derivatives)",
            titleBn = "ত্রিকোণমিতিক ফাংশনের অন্তরজ",
            titleEn = "Trigonometric Derivatives",
            formulaExpression = "d/dx(sin x) = cos x,  d/dx(cos x) = −sin x,  d/dx(tan x) = sec² x",
            latexExpression = "\\frac{d}{dx}(\\sin x) = \\cos x, \\quad \\frac{d}{dx}(\\cos x) = -\\sin x",
            plainTextExpression = "d/dx(sin x) = cos x, d/dx(cos x) = -sin x, d/dx(tan x) = sec^2(x)",
            explanationBn = "মৌলিক ত্রিকোণমিতিক অনুপাতগুলোর সাধারণ অন্তরজের সেট।",
            variables = listOf(
                "x" to "কোণ (রেডিয়ানে পরিমাপকৃত)"
            ),
            exampleProblemBn = "অন্যান্য ত্রিকোণমিতিক অন্তরজসমূহ:",
            exampleSolutionBn = "d/dx(cot x) = −csc² x\nd/dx(sec x) = sec x tan x\nd/dx(csc x) = −csc x cot x",
            tipsBn = "স্মরণীয় কৌশল: যে ত্রিকোণমিতিক ফাংশনগুলোর নাম 'co' দিয়ে শুরু (cos, cot, csc), তাদের অন্তরজের সামনে ঋণাত্মক (−) চিহ্ন বসে!",
            tags = listOf("ত্রিকোণমিতি", "trigonometry", "sin", "cos", "tan", "derivative"),
            relatedFormulaIds = listOf("lib_calc_10", "lib_trig_01")
        ),
        LibraryFormula(
            id = "lib_calc_08",
            subject = FormulaSubject.CALCULUS,
            subCategory = "সমাকলন (Integration)",
            titleBn = "সমাকলনের মৌলিক ঘাত সূত্র",
            titleEn = "Power Rule of Integration",
            formulaExpression = "∫ xⁿ dx = (xⁿ⁺¹ / (n + 1)) + C  [n ≠ −1]",
            latexExpression = "\\int x^n\\,dx = \\frac{x^{n+1}}{n+1} + C \\quad (n \\neq -1)",
            plainTextExpression = "integral(x^n dx) = x^(n+1)/(n+1) + C",
            explanationBn = "অনির্দিষ্ট সমাকলন হলো অন্তরীকরণের বিপরীত প্রক্রিয়া। ঘাত ১ বৃদ্ধি পায় এবং নতুন ঘাত দিয়ে ভাগ হয়, সাথে সমাকলন ধ্রুবক C যোগ হয়।",
            variables = listOf(
                "n" to "ঘাত (n ≠ −1)",
                "C" to "সমাকলন ধ্রুবক (Constant of Integration)"
            ),
            conditionsBn = "n ≠ −1 (কারণ n = −1 হলে হর ০ হয়ে যায়, তখন সূত্র ln|x| + C)।",
            exampleProblemBn = "সমাকলন করো: ∫ x⁵ dx",
            exampleSolutionBn = "∫ x⁵ dx = (x⁵⁺¹ / (5 + 1)) + C = (x⁶ / 6) + C",
            tipsBn = "অনির্দিষ্ট সমাকলনে সর্বদা +C লিখতে ভুলবেন না।",
            tags = listOf("সমাকলন", "ইন্টিগ্রেশন", "power rule", "integration", "calculus"),
            relatedFormulaIds = listOf("lib_calc_01", "lib_calc_09")
        ),
        LibraryFormula(
            id = "lib_calc_09",
            subject = FormulaSubject.CALCULUS,
            subCategory = "সমাকলন (Integration)",
            titleBn = "লগারিদমিক ও সূচকীয় সমাকলন",
            titleEn = "Logarithmic & Exponential Integrals",
            formulaExpression = "∫ (1 / x) dx = ln|x| + C  এবং  ∫ eˣ dx = eˣ + C",
            latexExpression = "\\int \\frac{1}{x}\\,dx = \\ln|x| + C, \\quad \\int e^x\\,dx = e^x + C",
            plainTextExpression = "integral(1/x dx) = ln|x| + C, integral(e^x dx) = e^x + C",
            explanationBn = "যেহেতু d/dx (ln x) = 1/x, তাই 1/x এর সমাকলন হলো প্রাকৃতিক লগারিদম ln|x|। eˣ এর সমাকলন অপরিবর্তিত থাকে।",
            variables = listOf(
                "|x|" to "পরম মান (যেহেতু লগের ডোমেন ধনাত্মক)",
                "C" to "সমাকলন ধ্রুবক"
            ),
            conditionsBn = "x ≠ 0।",
            exampleProblemBn = "সমাকলন করো: ∫ (3/x + 2eˣ) dx",
            exampleSolutionBn = "3 ∫ (1/x) dx + 2 ∫ eˣ dx\n= 3 ln|x| + 2eˣ + C",
            tipsBn = "যেকোনো অশূন্য a-এর ক্ষেত্রে: ∫ aˣ dx = (aˣ / ln a) + C।",
            tags = listOf("লগারিদম", "সূচকীয়", "1/x", "e^x", "integration"),
            relatedFormulaIds = listOf("lib_calc_06", "lib_calc_08")
        ),
        LibraryFormula(
            id = "lib_calc_10",
            subject = FormulaSubject.CALCULUS,
            subCategory = "সমাকলন (Integration)",
            titleBn = "ত্রিকোণমিতিক ফাংশনের সমাকলন",
            titleEn = "Trigonometric Integrals",
            formulaExpression = "∫ sin x dx = −cos x + C,  ∫ cos x dx = sin x + C,  ∫ sec² x dx = tan x + C",
            latexExpression = "\\int \\sin x\\,dx = -\\cos x + C, \\quad \\int \\cos x\\,dx = \\sin x + C",
            plainTextExpression = "integral(sin x dx) = -cos x + C, integral(cos x dx) = sin x + C",
            explanationBn = "মৌলিক ত্রিকোণমিতিক অপেক্ষকের প্রতি-অন্তরজ (Anti-derivatives)।",
            variables = listOf(
                "C" to "সমাকলন ধ্রুবক"
            ),
            exampleProblemBn = "অন্যান্য আদর্শ ত্রিকোণমিতিক সমাকলন:",
            exampleSolutionBn = "∫ csc² x dx = −cot x + C\n∫ sec x tan x dx = sec x + C\n∫ csc x cot x dx = −csc x + C",
            tipsBn = "সতর্কতা: sin x এর অন্তরজ +cos x, কিন্তু sin x এর সমাকলন −cos x!",
            tags = listOf("ত্রিকোণমিতি", "সমাকলন", "sin", "cos", "tan", "integral"),
            relatedFormulaIds = listOf("lib_calc_07")
        ),
        LibraryFormula(
            id = "lib_calc_11",
            subject = FormulaSubject.CALCULUS,
            subCategory = "সমাকলন (Integration)",
            titleBn = "খণ্ডশ সমাকলন (Integration by Parts)",
            titleEn = "Integration by Parts Formula",
            formulaExpression = "∫ u v dx = u ∫ v dx − ∫ [ (du/dx) · ∫ v dx ] dx",
            latexExpression = "\\int u\\,dv = uv - \\int v\\,du",
            plainTextExpression = "integral(u*v dx) = u*integral(v dx) - integral((du/dx)*integral(v dx) dx)",
            explanationBn = "দুটি ফাংশনের গুণফলের সমাকলন নির্ণয়ের সবচেয়ে শক্তিশালী পদ্ধতি। 'ILATE' নিয়মের সাহায্যে u নির্ধারণ করা হয়।",
            variables = listOf(
                "u, v" to "ফাংশনদ্বয়",
                "ILATE" to "ক্রম: Inverse, Logarithmic, Algebraic, Trigonometric, Exponential"
            ),
            exampleProblemBn = "সমাকলন করো: ∫ x eˣ dx",
            exampleSolutionBn = "ILATE অনুসারে: u = x (বীজগণিতীয়), v = eˣ (সূচকীয়)\n= x ∫ eˣ dx − ∫ [d/dx(x) · ∫ eˣ dx] dx\n= x eˣ − ∫ (1 · eˣ) dx\n= x eˣ − eˣ + C = eˣ(x − 1) + C",
            tipsBn = "ILATE নিয়মে যে ফাংশনটির নাম তালিকায় আগে আসবে, তাকে 'u' ধরতে হয়।",
            tags = listOf("খণ্ডশ সমাকলন", "integration by parts", "ILATE"),
            difficultyLevel = FormulaDifficulty.ADVANCED
        ),
        LibraryFormula(
            id = "lib_calc_12",
            subject = FormulaSubject.CALCULUS,
            subCategory = "সমাকলন (Integration)",
            titleBn = "নির্দিষ্ট সমাকলন ও কলনবিদ্যার মৌলিক উপপাদ্য",
            titleEn = "Definite Integral & FTC",
            formulaExpression = "∫[a to b] f(x) dx = F(b) − F(a)  যেখানে F'(x) = f(x)",
            latexExpression = "\\int_{a}^{b} f(x)\\,dx = F(b) - F(a)",
            plainTextExpression = "integral_a^b f(x) dx = F(b) - F(a)",
            explanationBn = "নির্দিষ্ট সমাকলনের মাধ্যমে বক্ররেখার নিচের আবদ্ধ অঞ্চলের ক্ষেত্রফল নির্ধারিত হয়। এতে কোনো ধ্রুবক C থাকে না।",
            variables = listOf(
                "a" to "নিম্ন সীমা (Lower Limit)",
                "b" to "উচ্চ সীমা (Upper Limit)",
                "F(x)" to "f(x)-এর প্রতি-অন্তরজ (Anti-derivative)"
            ),
            exampleProblemBn = "মান নির্ণয় করো: ∫[1 to 3] 2x dx",
            exampleSolutionBn = "F(x) = x²\nF(3) − F(1) = 3² − 1² = 9 − 1 = 8",
            tipsBn = "সীমা প্রতিস্থাপন করার সময় সবসময় উচ্চ সীমা বসিয়ে মান থেকে নিম্ন সীমা বসানো মান বিয়োগ করবেন।",
            tags = listOf("নির্দিষ্ট সমাকলন", "definite integral", "FTC", "area under curve"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE
        ),
        LibraryFormula(
            id = "lib_calc_13",
            subject = FormulaSubject.CALCULUS,
            subCategory = "সীমা (Limits)",
            titleBn = "ত্রিকোণমিতিক সীমার মৌলিক সূত্র",
            titleEn = "Fundamental Trig Limits",
            formulaExpression = "lim(x→0) [sin x / x] = 1  এবং  lim(x→0) [tan x / x] = 1",
            latexExpression = "\\lim_{x \\to 0} \\frac{\\sin x}{x} = 1, \\quad \\lim_{x \\to 0} \\frac{\\tan x}{x} = 1",
            plainTextExpression = "lim(x->0) (sin x / x) = 1, lim(x->0) (tan x / x) = 1",
            explanationBn = "যখন কোণ x শূন্যের খুব কাছাকাছি যায় (রেডিয়ানে), তখন sin x ও x এর অনুপাত ১ এর দিকে ধাবিত হয়।",
            variables = listOf(
                "x" to "কোণ রেডিয়ানে পরিমাপিত"
            ),
            conditionsBn = "কোণ x অবশ্যই রেডিয়ানে (Radian) হতে হবে, ডিগ্রিতে নয়।",
            exampleProblemBn = "মান নির্ণয় করো: lim(x→0) [sin(5x) / x]",
            exampleSolutionBn = "lim(x→0) [5 · sin(5x) / (5x)] = 5 · lim(5x→0) [sin(5x)/(5x)] = 5 · 1 = 5",
            tipsBn = "লব ও হরের চলক হুবহু একই হতে হবে। যদি sin(kx) থাকে, তবে হরেও kx তৈরি করতে হবে।",
            tags = listOf("সীমা", "limits", "trig limit", "sin x / x"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE
        ),
        LibraryFormula(
            id = "lib_calc_14",
            subject = FormulaSubject.CALCULUS,
            subCategory = "সীমা (Limits)",
            titleBn = "অয়লারের ধ্রুবক ও সূচকীয় সীমা",
            titleEn = "Euler's Constant & Exponential Limits",
            formulaExpression = "lim(x→∞) (1 + 1/x)ˣ = e  এবং  lim(x→0) [(eˣ − 1) / x] = 1",
            latexExpression = "\\lim_{x \\to \\infty} \\left(1 + \\frac{1}{x}\\right)^x = e",
            plainTextExpression = "lim(x->inf) (1 + 1/x)^x = e",
            explanationBn = "চক্রবৃদ্ধি বৃদ্ধি এবং প্রাকৃতিক ঘাতের ভিত্তি ধ্রুবক e (প্রায় ২.৭১৮২৮) এর গাণিতিক সংজ্ঞায়ন ও সীমা।",
            variables = listOf(
                "e" to "অয়লারের ধ্রুবক (Euler's number ≈ 2.71828)"
            ),
            exampleProblemBn = "মান নির্ণয় করো: lim(x→0) [(e³ˣ − 1) / x]",
            exampleSolutionBn = "= lim(x→0) [3 · (e³ˣ − 1) / (3x)] = 3 · 1 = 3",
            tipsBn = "সূচকীয় বৃদ্ধির হারে এই সীমাটি অত্যন্ত মৌলিক।",
            tags = listOf("সীমা", "e", "euler", "exponential limit"),
            difficultyLevel = FormulaDifficulty.INTERMEDIATE
        ),
        LibraryFormula(
            id = "lib_calc_15",
            subject = FormulaSubject.CALCULUS,
            subCategory = "সীমা (Limits)",
            titleBn = "লা'হস্পিটালের নিয়ম (L'Hôpital's Rule)",
            titleEn = "L'Hôpital's Rule for Indeterminate Forms",
            formulaExpression = "lim(x→c) [f(x) / g(x)] = lim(x→c) [f'(x) / g'(x)]  [রূপ: 0/0 বা ∞/∞]",
            latexExpression = "\\lim_{x \\to c} \\frac{f(x)}{g(x)} = \\lim_{x \\to c} \\frac{f'(x)}{g'(x)}",
            plainTextExpression = "lim(x->c) f(x)/g(x) = lim(x->c) f'(x)/g'(x)",
            explanationBn = "সীমা সরাসরি বসিয়ে যদি 0/0 বা ∞/∞ অনির্ণেয় রূপ পাওয়া যায়, তবে লব ও হরকে পৃথকভাবে অন্তরীকরণ করে পুনরায় সীমা বের করা যায়।",
            variables = listOf(
                "f'(x), g'(x)" to "লব ও হরের আলাদা অন্তরজ (ভাগফল নিয়ম নয়)"
            ),
            conditionsBn = "কেবলমাত্র 0/0 অথবা ±∞/±∞ অনির্ণেয় আকারের ক্ষেত্রে প্রযোজ্য এবং g'(x) ≠ 0।",
            exampleProblemBn = "মান নির্ণয় করো: lim(x→0) [(1 − cos x) / x²]",
            exampleSolutionBn = "0 বসালে রূপ 0/0 হয়। লা'হস্পিটাল প্রয়োগ করে:\n= lim(x→0) [sin x / 2x] (পুনরায় 0/0 রূপ)\n= lim(x→0) [cos x / 2] = cos(0) / 2 = 1/2",
            tipsBn = "মনে রাখবেন: এটি ভাগফল নিয়ম (Quotient Rule) নয়! লবকে একা এবং হরকে একা অন্তরীকরণ করবেন।",
            tags = listOf("লা হস্পিটাল", "lhopital", "অনির্ণেয় রূপ", "0/0", "limits"),
            difficultyLevel = FormulaDifficulty.ADVANCED
        )
    )

    fun getFormulasBySubject(subject: FormulaSubject): List<LibraryFormula> {
        return allFormulas.filter { it.subject == subject }
    }

    fun getFormulaById(id: String): LibraryFormula? {
        return allFormulas.find { it.id == id }
    }

    fun getRelatedFormulas(formula: LibraryFormula): List<LibraryFormula> {
        val explicit = formula.relatedFormulaIds.mapNotNull { id -> allFormulas.find { it.id == id } }
        if (explicit.isNotEmpty()) return explicit
        return allFormulas.filter { it.subject == formula.subject && it.id != formula.id }.take(3)
    }

    fun getRelatedFormulas(formulaId: String): List<LibraryFormula> {
        val f = getFormulaById(formulaId) ?: return emptyList()
        return getRelatedFormulas(f)
    }

    fun getSubCategories(subject: FormulaSubject?): List<String> {
        val list = if (subject == null) allFormulas else allFormulas.filter { it.subject == subject }
        return list.map { it.subCategory }.distinct()
    }

    fun searchFormulas(
        query: String = "",
        subject: FormulaSubject? = null,
        subCategory: String? = null,
        difficulty: FormulaDifficulty? = null,
        onlyBookmarked: Boolean = false,
        bookmarkedIds: Set<String> = emptySet(),
        onlyRecent: Boolean = false,
        recentIds: Collection<String> = emptyList(),
        onlyBookmarkedIds: Set<String>? = null,
        onlyRecentIds: Collection<String>? = null,
        curriculumTrack: CurriculumTrack = CurriculumTrack.ALL
    ): List<LibraryFormula> {
        val q = query.trim().lowercase()
        val effectiveOnlyRecent = onlyRecent || (onlyRecentIds != null)
        val effectiveRecentIds = onlyRecentIds ?: recentIds
        val effectiveOnlyBookmarked = onlyBookmarked || (onlyBookmarkedIds != null)
        val effectiveBookmarkedIds = onlyBookmarkedIds ?: bookmarkedIds

        val base = if (effectiveOnlyRecent) {
            effectiveRecentIds.mapNotNull { id -> allFormulas.find { it.id == id } }
        } else {
            allFormulas
        }

        return base.filter { f ->
            val matchesSubject = subject == null || f.subject == subject
            val matchesSubCat = subCategory == null || f.subCategory == subCategory
            val matchesDifficulty = difficulty == null || f.difficultyLevel == difficulty
            val matchesTrack = curriculumTrack == CurriculumTrack.ALL || f.curriculumTracks.contains(curriculumTrack)
            val matchesBookmark = !effectiveOnlyBookmarked || (f.id in effectiveBookmarkedIds)
            val matchesQuery = q.isEmpty() ||
                f.titleBn.lowercase().contains(q) ||
                f.titleEn.lowercase().contains(q) ||
                f.formulaExpression.lowercase().contains(q) ||
                f.plainTextExpression.lowercase().contains(q) ||
                f.subCategory.lowercase().contains(q) ||
                f.explanationBn.lowercase().contains(q) ||
                f.tags.any { it.lowercase().contains(q) }
            matchesSubject && matchesSubCat && matchesDifficulty && matchesTrack && matchesBookmark && matchesQuery
        }
    }

    /**
     * Validates that every formula in the library has non-empty required fields,
     * unique IDs, valid category assignments, and valid related formula references.
     */
    fun validateAllFormulas(): Boolean {
        val ids = mutableSetOf<String>()
        for (f in allFormulas) {
            if (f.id.isBlank() || !ids.add(f.id)) return false
            if (f.titleBn.isBlank() || f.titleEn.isBlank()) return false
            if (f.formulaExpression.isBlank() || f.latexExpression.isBlank() || f.plainTextExpression.isBlank()) return false
            if (f.explanationBn.isBlank() || f.conditionsBn.isBlank()) return false
            if (f.variables.isEmpty()) return false
            if (f.exampleProblemBn.isBlank() || f.exampleSolutionBn.isBlank()) return false
            if (f.tags.isEmpty()) return false
            if (f.curriculumTracks.isEmpty()) return false
        }
        return true
    }
}
