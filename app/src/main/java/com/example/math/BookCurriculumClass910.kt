package com.example.math

object BookCurriculumClass910 {
    val chapters: List<BookChapterSpec> = listOf(
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 1,
            titleBn = "বাস্তব সংখ্যা",
            titleEn = "Real Numbers",
            printedPageStart = 1,
            printedPageEnd = 20,
            summaryBn = "বাস্তব সংখ্যার শ্রেণিবিন্যাস (স্বাভাবিক, পূর্ণ, মূলদ, অমূলদ), √2 ও √3 অমূলদ সংখ্যার প্রমাণ, আবৃত্ত দশমিক ভগ্নাংশের রূপান্তর ও প্রক্রিয়াকরণ।",
            objectivesBn = listOf(
                "বাস্তব সংখ্যার শ্রেণিবিন্যাস করতে পারবে",
                "মূলদ ও অমূলদ সংখ্যা শনাক্ত এবং অমূলদ সংখ্যা প্রমাণ করতে পারবে",
                "আবৃত্ত দশমিক ভগ্নাংশকে সাধারণ ভগ্নাংশে রূপান্তর এবং যোগ, বিয়োগ, গুণ ও ভাগ করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_1_f1", "মূলদ ও অমূলদ সংখ্যার সংজ্ঞা", "মূলদ সংখ্যা = p/q (p, q পূর্ণসংখ্যা, q ≠ 0); যে সংখ্যাকে p/q আকারে প্রকাশ করা যায় না তা অমূলদ", "পূর্ণবর্গ নয় এমন যেকোনো স্বাভাবিক সংখ্যার বর্গমূল (√2, √3, √5) অমূলদ সংখ্যা"),
                BookFormulaSpec("c910_1_f2", "আবৃত্ত দশমিককে সাধারণ ভগ্নাংশে রূপান্তর", "লব = (দশমিক বিন্দু ও পৌনঃপুনিক বাদে সম্পূর্ণ সংখ্যা) - (অনাবৃত্ত অংশ); হর = (আবৃত্ত অঙ্কের সমান সংখ্যক 9) ও (দশমিকের পর অনাবৃত্ত অঙ্কের সমান সংখ্যক 0)", "যেমন: 0.3̇ = 3/9 = 1/3; 0.24̇ = (24-2)/90 = 22/90 = 11/45")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_1_ex1",
                    titleBn = "উদাহরণ ১: আবৃত্ত দশমিককে সাধারণ ভগ্নাংশে প্রকাশ",
                    problemStatementBn = "0.24̇ (যেখানে 4 এর ওপর পৌনঃপুনিক) কে সাধারণ ভগ্নাংশে প্রকাশ কর।",
                    stepsBn = listOf(
                        "এখানে সম্পূর্ণ সংখ্যা 24 এবং অনাবৃত্ত অংশ 2; সুতরাং লব = 24 - 2 = 22",
                        "দশমিকের পর আবৃত্ত অঙ্ক ১টি (4) এবং অনাবৃত্ত অঙ্ক ১টি (2); সুতরাং হর = 90",
                        "নির্ণেয় ভগ্নাংশ = 22 / 90 = 11 / 45"
                    ),
                    finalAnswerBn = "11/45",
                    printedPage = 12
                )
            ),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c910_1_th1",
                    titleBn = "প্রমাণ কর যে, √2 একটি অমূলদ সংখ্যা",
                    typeLabelBn = "উপপাদ্য",
                    statementBn = "√2 কে দুইটি সহমৌলিক স্বাভাবিক সংখ্যার অনুপাত p/q (q > 1) আকারে প্রকাশ করা সম্ভব নয়; অতএব √2 একটি অমূলদ সংখ্যা।",
                    stepsBn = listOf(
                        "ধরি √2 = p/q যেখানে p, q সহমৌলিক এবং q > 1",
                        "বর্গ করে পাই 2 = p²/q² বা 2q = p²/q",
                        "বামপক্ষ 2q পূর্ণসংখ্যা কিন্তু ডানপক্ষ p²/q পূর্ণসংখ্যা নয় (কারণ p ও q সহমৌলিক), যা অসম্ভব; অতএব √2 অমূলদ"
                    ),
                    printedPage = 4
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_1_l1",
                    code = "১.১-১.৬",
                    titleBn = "বাস্তব সংখ্যার শ্রেণিবিন্যাস, অমূলদ সংখ্যা ও আবৃত্ত দশমিক",
                    printedPageStart = 1,
                    printedPageEnd = 20,
                    conceptExplanationBn = "সকল মূলদ এবং অমূলদ সংখ্যাকে একত্রে বাস্তব সংখ্যা (Real Numbers, ℝ) বলা হয়। সসীম দশমিক ও আবৃত্ত অসীম দশমিক ভগ্নাংশ মূলদ সংখ্যা, আর অনাবৃত্ত অসীম দশমিক ভগ্নাংশ অমূলদ সংখ্যা।",
                    keyPointsBn = listOf(
                        "দুইটি বাস্তব সংখ্যার মধ্যে অসংখ্য মূলদ ও অমূলদ সংখ্যা বিদ্যমান",
                        "আবৃত্ত দশমিকের যোগ-বিয়োগে সদৃশ আবৃত্ত দশমিকে রূপান্তর করে নিতে হয়"
                    ),
                    stepByStepRulesBn = listOf(
                        "সদৃশ আবৃত্ত দশমিকে অনাবৃত্ত অঙ্কের সংখ্যা হবে প্রদত্ত সংখ্যাগুলোর সর্বোচ্চ অনাবৃত্ত অঙ্কের সমান এবং আবৃত্ত অঙ্কের সংখ্যা হবে আবৃত্ত অঙ্কগুলোর ল.সা.গু. এর সমান"
                    ),
                    quickMemoryTipsBn = listOf(
                        "যতগুলো পৌনঃপুনিক বিন্দু ততগুলো 9, দশমিকের পর যতগুলো খালি অঙ্ক ততগুলো 0!"
                    ),
                    commonMistakesBn = listOf(
                        "দশমিক বিন্দুর বামের পূর্ণসংখ্যাকে হরের শূন্য গণনায় অন্তর্ভুক্ত করা"
                    ),
                    miniCheckQuestionBn = "নিচের কোনটি অমূলদ সংখ্যা?",
                    miniCheckOptionsBn = listOf("√4", "√9", "√7", "22/7"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "7 পূর্ণবর্গ সংখ্যা নয়, তাই √7 একটি অমূলদ সংখ্যা।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১ (পৃষ্ঠা ১৭–২০)"),
            linkedLabTypes = listOf(BookInteractiveLabType.NUMBER_LINE_LAB, BookInteractiveLabType.PRIME_FACTOR_SIEVE_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 2,
            titleBn = "সেট ও ফাংশন",
            titleEn = "Sets and Functions",
            printedPageStart = 21,
            printedPageEnd = 42,
            summaryBn = "সেট, শক্তি সেট P(A), ক্রমজোড়, কার্তেসীয় গুণজ (A × B), অন্বয় (Relation), ফাংশন f(x), ডোমেন ও রেঞ্জ এবং ফাংশনের লেখচিত্র।",
            objectivesBn = listOf(
                "শক্তি সেট ও কার্তেসীয় গুণজ নির্ণয় করতে পারবে",
                "অন্বয় ও ফাংশনের পার্থক্য ব্যাখ্যা এবং ডোমেন ও রেঞ্জ নির্ণয় করতে পারবে",
                "ফাংশনের লেখচিত্র অঙ্কন করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_2_f1", "শক্তি সেট P(A) এর উপাদান সংখ্যা", "n(A) = m হলে n(P(A)) = 2ᵐ এবং প্রকৃত উপসেট সংখ্যা = 2ᵐ - 1", "কোনো সেটের সকল উপসেট নিয়ে গঠিত সেটকে শক্তি সেট P(A) বলে"),
                BookFormulaSpec("c910_2_f2", "কার্তেসীয় গুণজ ও ক্রমজোড়", "(x₁, y₁) = (x₂, y₂) হলে x₁ = x₂ এবং y₁ = y₂; A × B = {(x, y) : x ∈ A এবং y ∈ B}", "n(A × B) = n(A) × n(B)"),
                BookFormulaSpec("c910_2_f3", "ডোমেন ও রেঞ্জ", "অন্বয় R ⊆ A × B এর ক্রমজোড়গুলোর প্রথম উপাদানসমূহের সেট ডোমেন (Dom R) এবং দ্বিতীয় উপাদানসমূহের সেট রেঞ্জ (Range R)", "প্রতিটি x এর জন্য কেবল একটি নির্দিষ্ট y পাওয়া গেলে অন্বয়টি ফাংশন")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_2_ex1",
                    titleBn = "উদাহরণ ১: ফাংশনের মান নির্ণয়",
                    problemStatementBn = "f(x) = x² - 4x + 3 হলে f(-1) এর মান নির্ণয় কর।",
                    stepsBn = listOf(
                        "প্রদত্ত ফাংশনে x = -1 বসিয়ে পাই: f(-1) = (-1)² - 4(-1) + 3",
                        "= 1 + 4 + 3 = 8"
                    ),
                    finalAnswerBn = "8",
                    printedPage = 34
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_2_l1",
                    code = "২.১-২.২",
                    titleBn = "শক্তি সেট, কার্তেসীয় গুণজ, অন্বয় ও ফাংশন",
                    printedPageStart = 21,
                    printedPageEnd = 42,
                    conceptExplanationBn = "দুইটি চলক x এবং y এমনভাবে সম্পর্কযুক্ত যেন x এর যেকোনো একটি মানের জন্য y এর একটি মাত্র মান পাওয়া যায়, তবে y কে x এর ফাংশন বলা হয় এবং y = f(x) দ্বারা প্রকাশ করা হয়।",
                    keyPointsBn = listOf(
                        "সকল ফাংশনই অন্বয়, কিন্তু সকল অন্বয় ফাংশন নয়",
                        "A \\ B বা A - B = {x : x ∈ A এবং x ∉ B} (বাদ সেট)"
                    ),
                    stepByStepRulesBn = listOf(
                        "ক্রমজোড়ের সমতার অংকে প্রথম উপাদান = প্রথম উপাদান এবং দ্বিতীয় উপাদান = দ্বিতীয় উপাদান ধরে সহসমীকরণ সমাধান কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "ডোমেন = প্রথম উপাদান (Input x), রেঞ্জ = দ্বিতীয় উপাদান (Output y)!"
                    ),
                    commonMistakesBn = listOf(
                        "P(A) লেখার সময় উপসেটগুলোর বাইরে অতিরিক্ত দ্বিতীয় বন্ধনী {} না দেওয়া"
                    ),
                    miniCheckQuestionBn = "R = {(1, 2), (2, 3), (3, 4)} অন্বয়টির ডোমেন কোনটি?",
                    miniCheckOptionsBn = listOf("{1, 2, 3}", "{2, 3, 4}", "{1, 4}", "{1, 2, 3, 4}"),
                    miniCheckCorrectIndex = 0,
                    miniCheckExplanationBn = "ক্রমজোড়গুলোর প্রথম উপাদানসমূহের সেট ডোমেন = {1, 2, 3}।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ২.১ (পৃষ্ঠা ৩০–৩২)", "অনুশীলনী ২.২ (পৃষ্ঠা ৪০–৪২)"),
            linkedLabTypes = listOf(BookInteractiveLabType.SET_VENN_LAB, BookInteractiveLabType.COORDINATE_GRAPH_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 3,
            titleBn = "বীজগাণিতিক রাশি",
            titleEn = "Algebraic Expressions",
            printedPageStart = 43,
            printedPageEnd = 74,
            summaryBn = "বর্গ ও ঘনের সম্প্রসারিত সূত্র ও অনুসিদ্ধান্ত, ভাগশেষ উপপাদ্য (Remainder Theorem) ও উৎপাদক উপপাদ্য (Factor Theorem) এবং বীজগাণিতিক সূত্রের বাস্তব প্রয়োগ।",
            objectivesBn = listOf(
                "ত্রিপদী রাশির বর্গ ও ঘনের সূত্র ও অনুসিদ্ধান্ত প্রয়োগ করতে পারবে",
                "ভাগশেষ উপপাদ্যের সাহায্যে বহুপদীর উৎপাদকে বিশ্লেষণ করতে পারবে",
                "মান নির্ণয় ও বাস্তব সমস্যার সমাধানে বীজগাণিতিক সূত্র প্রয়োগ করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_3_f1", "ত্রিপদী রাশির বর্গের সূত্র", "(a + b + c)² = a² + b² + c² + 2(ab + bc + ca)", "অনুসিদ্ধান্ত: a² + b² + c² = (a + b + c)² - 2(ab + bc + ca)"),
                BookFormulaSpec("c910_3_f2", "ভাগশেষ ও উৎপাদক উপপাদ্য", "f(x) কে (x - a) দ্বারা ভাগ করলে ভাগশেষ f(a); যদি f(a) = 0 হয় তবে (x - a), f(x) এর একটি উৎপাদক", "বহুপদীর শূন্য (Zero of polynomial) পদ্ধতিতে উৎপাদকে বিশ্লেষণ"),
                BookFormulaSpec("c910_3_f3", "বিশেষ ঘনের অভেদ", "a³ + b³ + c³ - 3abc = (a + b + c)(a² + b² + c² - ab - bc - ca) = ½(a+b+c){(a-b)²+(b-c)²+(c-a)²}", "যদি a + b + c = 0 হয়, তবে a³ + b³ + c³ = 3abc")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_3_ex1",
                    titleBn = "উদাহরণ ১: ত্রিপদী রাশির বর্গের অনুসিদ্ধান্ত",
                    problemStatementBn = "a + b + c = 9 এবং ab + bc + ca = 31 হলে a² + b² + c² এর মান নির্ণয় কর।",
                    stepsBn = listOf(
                        "আমরা জানি, a² + b² + c² = (a + b + c)² - 2(ab + bc + ca)",
                        "= (9)² - 2 × 31 = 81 - 62 = 19"
                    ),
                    finalAnswerBn = "19",
                    printedPage = 47
                ),
                BookWorkedExample(
                    id = "c910_3_ex2",
                    titleBn = "উদাহরণ ২: উৎপাদক উপপাদ্যের প্রয়োগ",
                    problemStatementBn = "x³ - 6x² + 11x - 6 রাশিটিকে উৎপাদকে বিশ্লেষণ কর।",
                    stepsBn = listOf(
                        "ধরি f(x) = x³ - 6x² + 11x - 6; এখানে x = 1 বসালে f(1) = 1 - 6 + 11 - 6 = 0, তাই (x - 1) একটি উৎপাদক",
                        "x³ - x² - 5x² + 5x + 6x - 6 = x²(x - 1) - 5x(x - 1) + 6(x - 1) = (x - 1)(x² - 5x + 6)",
                        "= (x - 1)(x - 2)(x - 3)"
                    ),
                    finalAnswerBn = "(x - 1)(x - 2)(x - 3)",
                    printedPage = 62
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_3_l1",
                    code = "৩.১-৩.৫",
                    titleBn = "বর্গ, ঘন, উৎপাদক উপপাদ্য ও বাস্তব প্রয়োগ",
                    printedPageStart = 43,
                    printedPageEnd = 74,
                    conceptExplanationBn = "উৎপাদক উপপাদ্য অনুসারে f(a) = 0 হলে (x - a) বহুপদী f(x)-এর একটি উৎপাদক। এছাড়া x + 1/x বা x - 1/x এর মান থেকে উচ্চতর ঘাত যেমন x⁵ + 1/x⁵ নির্ণয়ে (x² + 1/x²)(x³ + 1/x³) = (x⁵ + 1/x⁵) + (x + 1/x) সম্পর্কটি ব্যবহৃত হয়।",
                    keyPointsBn = listOf(
                        "x⁵ + 1/x⁵ = (x² + 1/x²)(x³ + 1/x³) - (x + 1/x)",
                        "a + b + c = 0 হলে a³ + b³ + c³ = 3abc"
                    ),
                    stepByStepRulesBn = listOf(
                        "ভাগশেষ উপপাদ্যে ধ্রুব পদের গুণনীয়কগুলো (±1, ±2, ±3...) বসিয়ে শূন্য পরীক্ষা কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "বহুপদীর সব সহগের যোগফল 0 হলে (x - 1) নিশ্চিতভাবে একটি উৎপাদক!"
                    ),
                    commonMistakesBn = listOf(
                        "f(-a) = 0 হলে উৎপাদকটিকে (x - a) লেখা (সঠিক উৎপাদক হবে x + a)"
                    ),
                    miniCheckQuestionBn = "f(x) = x³ - 3x + 2 বহুপদীর একটি উৎপাদক কোনটি?",
                    miniCheckOptionsBn = listOf("x - 1", "x - 2", "x + 1", "x + 3"),
                    miniCheckCorrectIndex = 0,
                    miniCheckExplanationBn = "f(1) = 1 - 3 + 2 = 0; সুতরাং (x - 1) একটি উৎপাদক।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৩.১ (পৃষ্ঠা ৪৯–৫০)", "অনুশীলনী ৩.২ (পৃষ্ঠা ৫৪–৫৫)", "অনুশীলনী ৩.৩ (পৃষ্ঠা ৫৮–৫৯)", "অনুশীলনী ৩.৪ (পৃষ্ঠা ৬৩)", "অনুশীলনী ৩.৫ (পৃষ্ঠা ৭০–৭৪)"),
            linkedLabTypes = listOf(BookInteractiveLabType.ALGEBRA_IDENTITY_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 4,
            titleBn = "সূচক ও লগারিদম",
            titleEn = "Exponents and Logarithms",
            printedPageStart = 75,
            printedPageEnd = 92,
            summaryBn = "মূলদ ও ঋণাত্মক সূচক, n-তম মূল, লগারিদমের সূত্রাবলি (গুণ, ভাগ, ঘাত, ভিত্তি পরিবর্তন), সাধারণ লগারিদমের পূর্ণক ও অংশক এবং বৈজ্ঞানিক আকার (Scientific Notation)।",
            objectivesBn = listOf(
                "মূলদ ও ঋণাত্মক সূচকের নিয়মাবলি প্রয়োগ করে সরলীকরণ করতে পারবে",
                "লগারিদমের সূত্র প্রমাণ ও প্রয়োগ করতে পারবে",
                "সংখ্যার বৈজ্ঞানিক রূপ (a × 10ⁿ) এবং সাধারণ লগারিদমের পূর্ণক ও অংশক নির্ণয় করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_4_f1", "সূচকের মৌলিক সূত্রাবলি", "aᵐ · aⁿ = aᵐ⁺ⁿ, aᵐ / aⁿ = aᵐ⁻ⁿ, (aᵐ)ⁿ = aᵐⁿ, a⁰ = 1, a⁻ⁿ = 1/aⁿ", "a > 0, a ≠ 1 হলে aˣ = aʸ ⇒ x = y"),
                BookFormulaSpec("c910_4_f2", "লগারিদমের সংজ্ঞা ও সূত্রাবলি", "aˣ = N ⇔ x = logₐ N; logₐ(MN) = logₐ M + logₐ N; logₐ(M/N) = logₐ M - logₐ N; logₐ(Mʳ) = r logₐ M", "logₐ a = 1 এবং logₐ 1 = 0"),
                BookFormulaSpec("c910_4_f3", "ভিত্তি পরিবর্তন ও বৈজ্ঞানিক রূপ", "logₐ M = log_b M × logₐ b = (log_b M) / (log_b a); বৈজ্ঞানিক রূপ N = a × 10ⁿ (1 ≤ a < 10)", "সাধারণ লগের পূর্ণক = n")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_4_ex1",
                    titleBn = "উদাহরণ ১: লগারিদমের মান নির্ণয়",
                    problemStatementBn = "log_5 (³√5 · √5) এর মান নির্ণয় কর।",
                    stepsBn = listOf(
                        "³√5 · √5 = 5^(1/3) · 5^(1/2) = 5^(1/3 + 1/2) = 5^(5/6)",
                        "সুতরাং log_5 (5^(5/6)) = (5/6) log_5 5 = (5/6) × 1 = 5/6"
                    ),
                    finalAnswerBn = "5/6",
                    printedPage = 85
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_4_l1",
                    code = "৪.১-৪.৩",
                    titleBn = "সূচক, লগারিদম ও বৈজ্ঞানিক আকার",
                    printedPageStart = 75,
                    printedPageEnd = 92,
                    conceptExplanationBn = "স্কটল্যান্ডের গণিতবিদ জন নেপিয়ার (John Napier) ১৬১৪ সালে লগারিদমের সূচনা করেন এবং হেনরি ব্রিগস ১০ ভিত্তিক সাধারণ লগারিদম প্রচলন করেন।",
                    keyPointsBn = listOf(
                        "লগারিদম কেবল ধনাত্মক সংখ্যারই হয়; শূন্য বা ঋণাত্মক সংখ্যার লগারিদম নেই",
                        "বৈজ্ঞানিক আকার N = a × 10ⁿ (যেখানে 1 ≤ a < 10 এবং n ∈ ℤ)"
                    ),
                    stepByStepRulesBn = listOf(
                        "পূর্ণক নির্ণয়ে সংখ্যাটিকে বৈজ্ঞানিক আকারে a × 10ⁿ এ প্রকাশ করলে 10 এর সূচক n-ই হবে পূর্ণক"
                    ),
                    quickMemoryTipsBn = listOf(
                        "logₐ b × log_b a = 1 (পরস্পর বিপরীত)!"
                    ),
                    commonMistakesBn = listOf(
                        "log(M + N) কে log M + log N লেখা (আসলে log(MN) = log M + log N)"
                    ),
                    miniCheckQuestionBn = "log_2 64 এর মান কত?",
                    miniCheckOptionsBn = listOf("4", "5", "6", "8"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "64 = 2⁶, তাই log_2 (2⁶) = 6 log_2 2 = 6।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৪.১ (পৃষ্ঠা ৮০–৮১)", "অনুশীলনী ৪.২ (পৃষ্ঠা ৮৬–৮৭)", "অনুশীলনী ৪.৩ (পৃষ্ঠা ৯১–৯২)"),
            linkedLabTypes = listOf(BookInteractiveLabType.ALGEBRA_IDENTITY_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 5,
            titleBn = "এক চলকবিশিষ্ট সমীকরণ",
            titleEn = "Equations in One Variable",
            printedPageStart = 93,
            printedPageEnd = 110,
            summaryBn = "সমীকরণ ও অভেদের পার্থক্য, এক ঘাত সমীকরণ, ভগ্নাংশ ও বর্গমূল সংবলিত সমীকরণ এবং এক চলকবিশিষ্ট দ্বিঘাত সমীকরণ (ax² + bx + c = 0) গঠন ও সমাধান।",
            objectivesBn = listOf(
                "এক চলকবিশিষ্ট একঘাত ও দ্বিঘাত সমীকরণ সমাধান করতে পারবে",
                "বাস্তব সমস্যা থেকে সমীকরণ গঠন করে সমাধান নির্ণয় করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_5_f1", "দ্বিঘাত সমীকরণের আদর্শ রূপ", "ax² + bx + c = 0 (যেখানে a ≠ 0)", "উৎপাদকে বিশ্লেষণ করে (x - α)(x - β) = 0 থেকে মূল x = α, β নির্ণয় করা হয়")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_5_ex1",
                    titleBn = "উদাহরণ ১: দুই অঙ্কবিশিষ্ট সংখ্যার সমস্যা",
                    problemStatementBn = "দুই অঙ্কবিশিষ্ট একটি সংখ্যার অঙ্কদ্বয়ের সমষ্টি 9। অঙ্ক দুইটি স্থান বিনিময় করলে যে সংখ্যা পাওয়া যায় তা প্রদত্ত সংখ্যা হতে 27 বেশি। সংখ্যাটি কত?",
                    stepsBn = listOf(
                        "ধরি একক স্থানীয় অঙ্ক x, তবে দশক স্থানীয় অঙ্ক (9 - x); সংখ্যাটি = 10(9 - x) + x = 90 - 9x",
                        "স্থান বিনিময় করলে সংখ্যাটি হয় = 10x + (9 - x) = 9x + 9",
                        "প্রশ্নমতে, (9x + 9) - (90 - 9x) = 27 ⇒ 18x - 81 = 27 ⇒ 18x = 108 ⇒ x = 6; সংখ্যাটি = 90 - 54 = 36"
                    ),
                    finalAnswerBn = "36",
                    printedPage = 104
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_5_l1",
                    code = "৫.১-৫.২",
                    titleBn = "এক চলকবিশিষ্ট একঘাত ও দ্বিঘাত সমীকরণ সমাধান",
                    printedPageStart = 93,
                    printedPageEnd = 110,
                    conceptExplanationBn = "ভগ্নাংশযুক্ত সমীকরণ সমাধানের পর প্রাপ্ত মূল কোনো হরকে শূন্য (0) করে কি না তা পরীক্ষা করতে হয়। যদি হর শূন্য হয় তবে ঐ মান গ্রহণযোগ্য নয় (অবান্তর মূল)।",
                    keyPointsBn = listOf(
                        "দশক স্থানীয় অঙ্ক y এবং একক স্থানীয় অঙ্ক x হলে সংখ্যাটি = 10y + x"
                    ),
                    stepByStepRulesBn = listOf(
                        "দ্বিঘাত সমীকরণ সমাধানের জন্য ডানপক্ষ শূন্য করে বামপক্ষকে উৎপাদকে বিশ্লেষণ কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "দুইটি উৎপাদকের গুণফল PQ = 0 হলে হয় P = 0 অথবা Q = 0!"
                    ),
                    commonMistakesBn = listOf(
                        "সমীকরণের উভয় পক্ষকে চলক সংবলিত উৎপাদক দ্বারা ভাগ করে একটি মূল হারিয়ে ফেলা"
                    ),
                    miniCheckQuestionBn = "x² - 5x + 6 = 0 সমীকরণের সমাধান সেট কোনটি?",
                    miniCheckOptionsBn = listOf("{1, 6}", "{2, 3}", "{-2, -3}", "{-1, 6}"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "(x - 2)(x - 3) = 0 ⇒ x = 2, 3।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৫.১ (পৃষ্ঠা ১০১–১০৩)", "অনুশীলনী ৫.২ (পৃষ্ঠা ১০৮–১১০)"),
            linkedLabTypes = listOf(BookInteractiveLabType.EQUATION_BALANCER_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 6,
            titleBn = "রেখা, কোণ ও ত্রিভুজ",
            titleEn = "Lines, Angles and Triangles",
            printedPageStart = 111,
            printedPageEnd = 135,
            summaryBn = "ইউক্লিডের স্বীকার্য, সমতলীয় জ্যামিতির যুক্তিমূলক প্রমাণ, ত্রিভুজের কোণ ও বাহুর সম্পর্ক, ত্রিভুজের দুই বাহুর মধ্যবিন্দুর সংযোজক রেখাংশ সংক্রান্ত উপপাদ্য (উপপাদ্য ১৫)।",
            objectivesBn = listOf(
                "জ্যামিতিক স্বীকার্য ও উপপাদ্যের পার্থক্য ব্যাখ্যা করতে পারবে",
                "ত্রিভুজের বহিঃস্থ কোণ, বাহু-কোণের অসমতা এবং মধ্যবিন্দু উপপাদ্য প্রমাণ ও প্রয়োগ করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_6_f1", "ত্রিভুজের মধ্যবিন্দু উপপাদ্য (উপপাদ্য ১৫)", "ত্রিভুজের যেকোনো দুই বাহুর মধ্যবিন্দুর সংযোজক রেখাংশ তৃতীয় বাহুর সমান্তরাল এবং দৈর্ঘ্যে তার অর্ধেক (DE ∥ BC এবং DE = ½ BC)", "ΔABC এ D ও E যথাক্রমে AB ও AC এর মধ্যবিন্দু হলে"),
                BookFormulaSpec("c910_6_f2", "সমকোণী ত্রিভুজের সূক্ষ্মকোণদ্বয়ের সম্পর্ক", "সমকোণী ত্রিভুজের সূক্ষ্মকোণদ্বয় পরস্পর পূরক (সমষ্টি 90°)", "যেহেতু একটি কোণ 90° এবং তিন কোণের সমষ্টি 180°")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_6_ex1",
                    titleBn = "উদাহরণ ১: ত্রিভুজের বাহুর মধ্যবিন্দু উপপাদ্যের প্রয়োগ",
                    problemStatementBn = "ΔABC এ AB ও AC বাহুর মধ্যবিন্দু যথাক্রমে D ও E। BC = 12 সেমি হলে DE এর দৈর্ঘ্য কত?",
                    stepsBn = listOf(
                        "উপপাদ্য ১৫ অনুসারে, ত্রিভুজের দুই বাহুর মধ্যবিন্দুর সংযোজক রেখাংশ তৃতীয় বাহুর অর্ধেক",
                        "অতএব, DE = ½ BC = ½ × 12 = 6 সেমি"
                    ),
                    finalAnswerBn = "6 সেমি",
                    printedPage = 132
                )
            ),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c910_6_th15",
                    titleBn = "উপপাদ্য ১৫: ত্রিভুজের যেকোনো দুই বাহুর মধ্যবিন্দুর সংযোজক রেখাংশ তৃতীয় বাহুর সমান্তরাল এবং দৈর্ঘ্যে তার অর্ধেক",
                    typeLabelBn = "উপপাদ্য",
                    statementBn = "ΔABC এ D ও E যথাক্রমে AB ও AC বাহুর মধ্যবিন্দু হলে DE ∥ BC এবং DE = ½ BC।",
                    stepsBn = listOf(
                        "DE কে F পর্যন্ত বর্ধিত করি যেন EF = DE হয় এবং C, F যোগ করি",
                        "ΔADE ≅ ΔCFE প্রমাণ করে CF = AD = BD এবং CF ∥ BD পাই",
                        "সুতরাং BDFC একটি সামান্তরিক; ফলে DF = BC বা 2DE = BC বা DE = ½ BC এবং DE ∥ BC"
                    ),
                    printedPage = 131
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_6_l1",
                    code = "৬.১-৬.৩",
                    titleBn = "রেখা, কোণ ও ত্রিভুজ সংক্রান্ত উপপাদ্য",
                    printedPageStart = 111,
                    printedPageEnd = 135,
                    conceptExplanationBn = "গ্রিক গণিতবিদ ইউক্লিড তাঁর বিখ্যাত গ্রন্থ 'Elements'-এ জ্যামিতিক প্রতিজ্ঞাগুলোকে সুশৃঙ্খলভাবে সাজান। ত্রিভুজের যেকোনো দুই বাহুর সমষ্টি তৃতীয় বাহু অপেক্ষা বৃহত্তর এবং অন্তর তৃতীয় বাহু অপেক্ষা ক্ষুদ্রতর।",
                    keyPointsBn = listOf(
                        "ত্রিভুজের বৃহত্তর বাহুর বিপরীত কোণ ক্ষুদ্রতর বাহুর বিপরীত কোণ অপেক্ষা বৃহত্তর"
                    ),
                    stepByStepRulesBn = listOf(
                        "জ্যামিতিক প্রমাণে বিশেষ নির্বচন, অঙ্কন ও ধাপগুলোর যথার্থতা ক্রমানুসারে উল্লেখ কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "মধ্যবিন্দু সংযোজক রেখাংশ = ভূমি ÷ ২!"
                    ),
                    commonMistakesBn = listOf(
                        "বহিঃস্থ কোণকে সংলগ্ন অন্তঃস্থ কোণের সমান মনে করা"
                    ),
                    miniCheckQuestionBn = "ΔPQR এ PQ ও PR এর মধ্যবিন্দু M ও N এবং MN = 5 সেমি হলে QR কত?",
                    miniCheckOptionsBn = listOf("2.5 সেমি", "5 সেমি", "10 সেমি", "15 সেমি"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "QR = 2 × MN = 2 × 5 = 10 সেমি।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৬.১ (পৃষ্ঠা ১১৭)", "অনুশীলনী ৬.২ (পৃষ্ঠা ১২০)", "অনুশীলনী ৬.৩ (পৃষ্ঠা ১৩৩–১৩৫)"),
            linkedLabTypes = listOf(BookInteractiveLabType.GEOMETRY_CANVAS_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 7,
            titleBn = "ব্যবহারিক জ্যামিতি",
            titleEn = "Practical Geometry",
            printedPageStart = 136,
            printedPageEnd = 151,
            summaryBn = "বিশেষ শর্তাধীনে ত্রিভুজ অঙ্কন (ভূমি, ভূমি সংলগ্ন কোণ ও অপর দুই বাহুর সমষ্টি/অন্তর, অথবা পরিসীমা ও দুইটি কোণ) এবং চতুর্ভুজ, সামান্তরিক ও ট্রাপিজিয়াম অঙ্কন।",
            objectivesBn = listOf(
                "ত্রিভুজের পরিসীমা ও ভূমি সংলগ্ন দুইটি কোণ দেওয়া থাকলে ত্রিভুজ আঁকতে পারবে",
                "প্রদত্ত উপাত্ত থেকে চতুর্ভুজ, সামান্তরিক ও ট্রাপিজিয়াম অঙ্কন করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_7_f1", "পরিসীমা থেকে ত্রিভুজ অঙ্কনের মূলনীতি", "পরিসীমা p = AB + BC + CA এবং ভূমি সংলগ্ন কোণ ∠B, ∠C দেওয়া থাকলে অর্ধকোণ ∠B/2 ও ∠C/2 এবং লম্বদ্বিখণ্ডক এঁকে শীর্ষ A, B, C নির্ণয় করা হয়", "সম্পাদ্য ৩")
            ),
            workedExamples = emptyList(),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c910_7_con1",
                    titleBn = "সম্পাদ্য ১–৩: বিশেষ শর্তাধীনে ত্রিভুজ অঙ্কন",
                    typeLabelBn = "সম্পাদ্য",
                    statementBn = "সম্পাদ্য ১: ত্রিভুজের ভূমি, ভূমি সংলগ্ন একটি কোণ ও অপর দুই বাহুর সমষ্টি দেওয়া আছে, ত্রিভুজটি আঁকতে হবে। সম্পাদ্য ২: ভূমি, ভূমি সংলগ্ন একটি সূক্ষ্মকোণ ও অপর দুই বাহুর অন্তর দেওয়া আছে। সম্পাদ্য ৩: ভূমি সংলগ্ন দুইটি কোণ ও পরিসীমা দেওয়া আছে, ত্রিভুজটি আঁকতে হবে।",
                    stepsBn = listOf(
                        "সমষ্টি s দেওয়া থাকলে রশ্মি থেকে সমান অংশ কেটে শেষ প্রান্ত যোগ করে সমান কোণ আঁক",
                        "পরিসীমা p দেওয়া থাকলে দুই প্রান্তে প্রদত্ত কোণদ্বয়ের অর্ধেক কোণ এঁকে ছেদবিন্দু A নির্ণয় কর এবং লম্ব সমদ্বিখণ্ডকের সাহায্যে B ও C বিন্দু নির্ণয় কর"
                    ),
                    printedPage = 138
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_7_l1",
                    code = "৭.১-৭.২",
                    titleBn = "ত্রিভুজ ও চতুর্ভুজ অঙ্কন (সম্পাদ্য ১–৬)",
                    printedPageStart = 136,
                    printedPageEnd = 151,
                    conceptExplanationBn = "সাধারণ উপাত্তের পরিবর্তে বাহুদ্বয়ের যোগফল, বিয়োগফল বা পরিসীমা দেওয়া থাকলেও সমদ্বিবাহু ত্রিভুজের ধর্ম (সমান সমান কোণের বিপরীত বাহু সমান) কাজে লাগিয়ে কেবল রুলার ও কম্পাসের সাহায্যে ত্রিভুজ আঁকা যায়।",
                    keyPointsBn = listOf(
                        "সামান্তরিকের দুইটি কর্ণ ও তাদের অন্তর্ভুক্ত কোণ দেওয়া থাকলে কর্ণদ্বয়কে সমদ্বিখণ্ডিত করে সামান্তরিক আঁকা যায়"
                    ),
                    stepByStepRulesBn = listOf(
                        "সম্পাদ্যে অঙ্কনের বিবরণ সংক্ষিপ্ত ও স্পষ্ট ধাপে লিখতে হবে"
                    ),
                    quickMemoryTipsBn = listOf(
                        "পরিসীমার সম্পাদ্যে কোণকে প্রথমে অর্ধেক (১/২) করে আঁকতে হয়!"
                    ),
                    commonMistakesBn = listOf(
                        "বাহুদ্বয়ের অন্তর দেওয়া থাকলে কোন বাহুটি বড় সে অনুযায়ী কোণের দিক ঠিক না রাখা"
                    ),
                    miniCheckQuestionBn = "ত্রিভুজের পরিসীমা ও ভূমি সংলগ্ন দুইটি কোণ ∠B = 60° ও ∠C = 40° দেওয়া থাকলে অঙ্কনের শুরুতে রেখাংশের দুই প্রান্তে কত ডিগ্রি কোণ আঁকতে হয়?",
                    miniCheckOptionsBn = listOf("60° ও 40°", "30° ও 20°", "120° ও 80°", "45° ও 45°"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "সম্পাদ্য ৩ অনুসারে প্রদত্ত কোণদ্বয়ের অর্ধেক অর্থাৎ 60°/2 = 30° এবং 40°/2 = 20° কোণ আঁকতে হয়।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৭.১ (পৃষ্ঠা ১৪৩–১৪৪)", "অনুশীলনী ৭.২ (পৃষ্ঠা ১৪৯–১৫১)"),
            linkedLabTypes = listOf(BookInteractiveLabType.GEOMETRY_CANVAS_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 8,
            titleBn = "বৃত্ত",
            titleEn = "Circle",
            printedPageStart = 152,
            printedPageEnd = 173,
            summaryBn = "বৃত্তের চাপ, কেন্দ্রস্থ ও বৃত্তস্থ কোণ (উপপাদ্য ২০), অর্ধবৃত্তস্থ কোণ এক সমকোণ (উপপাদ্য ২২), বৃত্তে অন্তর্লিখিত চতুর্ভুজ (উপপাদ্য ২৩), বৃত্তের স্পর্শক এবং পরিবৃত্ত, অন্তর্বৃত্ত ও বহির্বৃত্ত অঙ্কন।",
            objectivesBn = listOf(
                "কেন্দ্রস্থ কোণ বৃত্তস্থ কোণের দ্বিগুণ — উপপাদ্যটি প্রমাণ ও প্রয়োগ করতে পারবে",
                "বৃত্তে অন্তর্লিখিত চতুর্ভুজ ও বৃত্তের স্পর্শক সংক্রান্ত উপপাদ্য প্রমাণ করতে পারবে",
                "ত্রিভুজের পরিবৃত্ত, অন্তর্বৃত্ত ও বহির্বৃত্ত অঙ্কন করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_8_f1", "কেন্দ্রস্থ ও বৃত্তস্থ কোণের সম্পর্ক (উপপাদ্য ২০)", "কেন্দ্রস্থ কোণ = ২ × বৃত্তস্থ কোণ (∠BOC = 2 ∠BAC)", "বৃত্তের একই চাপের ওপর দণ্ডায়মান কেন্দ্রস্থ কোণ বৃত্তস্থ কোণের দ্বিগুণ"),
                BookFormulaSpec("c910_8_f2", "বৃত্তে অন্তর্লিখিত চতুর্ভুজ (উপপাদ্য ২৩)", "∠A + ∠C = 180° এবং ∠B + ∠D = 180°", "বৃত্তে অন্তর্লিখিত চতুর্ভুজের যেকোনো দুইটি বিপরীত কোণের সমষ্টি দুই সমকোণ (১৮০°)"),
                BookFormulaSpec("c910_8_f3", "বহিঃস্থ বিন্দু থেকে অঙ্কিত স্পর্শক (উপপাদ্য ২৬)", "PA = PB এবং OP ⊥ স্পর্শবিন্দুগামী ব্যাসার্ধ OA", "বৃত্তের বহিঃস্থ কোনো বিন্দু থেকে বৃত্তে দুইটি স্পর্শক টানলে স্পর্শকদ্বয়ের দৈর্ঘ্য সমান")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_8_ex1",
                    titleBn = "উদাহরণ ১: কেন্দ্রস্থ ও বৃত্তস্থ কোণের হিসাব",
                    problemStatementBn = "O কেন্দ্রবিশিষ্ট বৃত্তে একই উপচাপ BC এর ওপর দণ্ডায়মান বৃত্তস্থ কোণ ∠BAC = 55° হলে কেন্দ্রস্থ কোণ ∠BOC এর মান কত?",
                    stepsBn = listOf(
                        "আমরা জানি, বৃত্তের একই চাপের ওপর দণ্ডায়মান কেন্দ্রস্থ কোণ বৃত্তস্থ কোণের দ্বিগুণ",
                        "অতএব, ∠BOC = 2 × ∠BAC = 2 × 55° = 110°"
                    ),
                    finalAnswerBn = "110°",
                    printedPage = 158
                )
            ),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c910_8_th20",
                    titleBn = "উপপাদ্য ২০–২৬: বৃত্তস্থ কোণ, অন্তর্লিখিত চতুর্ভুজ ও স্পর্শক",
                    typeLabelBn = "উপপাদ্য",
                    statementBn = "উপপাদ্য ২০: বৃত্তের একই চাপের ওপর দণ্ডায়মান কেন্দ্রস্থ কোণ বৃত্তস্থ কোণের দ্বিগুণ। উপপাদ্য ২১: একই চাপের ওপর দণ্ডায়মান বৃত্তস্থ কোণগুলো পরস্পর সমান। উপপাদ্য ২২: অর্ধবৃত্তস্থ কোণ এক সমকোণ (৯০°)। উপপাদ্য ২৩: বৃত্তে অন্তর্লিখিত চতুর্ভুজের দুইটি বিপরীত কোণের সমষ্টি ১৮০°।",
                    stepsBn = listOf(
                        "উপপাদ্য ২০ প্রমাণে A বিন্দু দিয়ে কেন্দ্রগামী রেখাংশ AD এঁকে ΔAOB ও ΔAOC সমদ্বিবাহু ত্রিভুজের বহিঃস্থ কোণের উপপাদ্য প্রয়োগ কর"
                    ),
                    printedPage = 156
                ),
                BookTheoremOrConstruction(
                    id = "c910_8_con9",
                    titleBn = "সম্পাদ্য ৯–১১: ত্রিভুজের পরিবৃত্ত, অন্তর্বৃত্ত ও বহির্বৃত্ত অঙ্কন",
                    typeLabelBn = "সম্পাদ্য",
                    statementBn = "পরিবৃত্ত আঁকতে যেকোনো দুই বাহুর লম্ব সমদ্বিখণ্ডক, অন্তর্বৃত্ত আঁকতে যেকোনো দুই কোণের অন্তর্দ্বিখণ্ডক এবং বহির্বৃত্ত আঁকতে দুইটি বহিঃস্থ কোণের সমদ্বিখণ্ডক অঙ্কন করে কেন্দ্র নির্ণয় করতে হয়।",
                    stepsBn = listOf(
                        "পরিবৃত্তে: বাহুদ্বয়ের লম্ব সমদ্বিখণ্ডকের ছেদবিন্দু O থেকে শীর্ষ A পর্যন্ত ব্যাসার্ধ নিয়ে বৃত্ত আঁক",
                        "অন্তর্বৃত্তে: কোণদ্বয়ের সমদ্বিখণ্ডকের ছেদবিন্দু O থেকে ভূমির ওপর লম্ব OD এঁকে OD ব্যাসার্ধ নিয়ে বৃত্ত আঁক"
                    ),
                    printedPage = 171
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_8_l1",
                    code = "৮.১-৮.৫",
                    titleBn = "বৃত্তস্থ কোণ, অন্তর্লিখিত চতুর্ভুজ, স্পর্শক ও বৃত্ত সম্পাদ্য",
                    printedPageStart = 152,
                    printedPageEnd = 173,
                    conceptExplanationBn = "বৃত্তের যেকোনো বিন্দুতে অঙ্কিত স্পর্শক স্পর্শবিন্দুগামী ব্যাসার্ধের ওপর লম্ব। আবার দুইটি বৃত্ত পরস্পরকে বহিঃস্পর্শ করলে কেন্দ্রদ্বয়ের দূরত্ব ব্যাসার্ধদ্বয়ের সমষ্টির সমান এবং অন্তঃস্পর্শ করলে অন্তরের সমান।",
                    keyPointsBn = listOf(
                        "অর্ধবৃত্তস্থ কোণ = ৯০°",
                        "বৃত্তে অন্তর্লিখিত সামান্তরিক একটি আয়তক্ষেত্র"
                    ),
                    stepByStepRulesBn = listOf(
                        "পরিবৃত্তে বাহুর লম্ব সমদ্বিখণ্ডক এবং অন্তর্বৃত্তে কোণের সমদ্বিখণ্ডক আঁকতে হয়"
                    ),
                    quickMemoryTipsBn = listOf(
                        "পরিবৃত্ত = বাহু দ্বিখণ্ডন; অন্তর্বৃত্ত = কোণ দ্বিখণ্ডন!"
                    ),
                    commonMistakesBn = listOf(
                        "অন্তর্বৃত্ত আঁকার সময় কেন্দ্র থেকে বাহুর ওপর লম্ব না এঁকে সরাসরি শীর্ষবিন্দু পর্যন্ত ব্যাসার্ধ নেওয়া"
                    ),
                    miniCheckQuestionBn = "ABCD বৃত্তস্থ চতুর্ভুজে ∠A = 75° হলে বিপরীত কোণ ∠C এর মান কত?",
                    miniCheckOptionsBn = listOf("75°", "95°", "105°", "115°"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "বৃত্তস্থ চতুর্ভুজের বিপরীত কোণদ্বয়ের সমষ্টি 180°, তাই ∠C = 180° - 75° = 105°।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৮.১ (পৃষ্ঠা ১৫৪)", "অনুশীলনী ৮.২ (পৃষ্ঠা ১৬১)", "অনুশীলনী ৮.৩ (পৃষ্ঠা ১৬৫)", "অনুশীলনী ৮.৪ (পৃষ্ঠা ১৬৯)", "অনুশীলনী ৮.৫ (পৃষ্ঠা ১৭১–১৭৩)"),
            linkedLabTypes = listOf(BookInteractiveLabType.CIRCLE_THEOREM_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 9,
            titleBn = "ত্রিকোণমিতিক অনুপাত",
            titleEn = "Trigonometric Ratios",
            printedPageStart = 174,
            printedPageEnd = 196,
            summaryBn = "সমকোণী ত্রিভুজের বাহুগুলোর নামকরণ, ৬টি ত্রিকোণমিতিক অনুপাত (sin, cos, tan, cot, sec, cosec), মৌলিক অভেদাবলি, পূরক কোণ এবং 0°, 30°, 45°, 60°, 90° কোণের মান।",
            objectivesBn = listOf(
                "সমকোণী ত্রিভুজের সূক্ষ্মকোণের ৬টি ত্রিকোণমিতিক অনুপাত ও তাদের পারস্পরিক সম্পর্ক ব্যাখ্যা করতে পারবে",
                "ত্রিকোণমিতিক অভেদাবলি প্রমাণ ও প্রয়োগ করতে পারবে",
                "0°, 30°, 45°, 60°, 90° কোণের ত্রিকোণমিতিক মান প্রয়োগ করে সমীকরণ সমাধান করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_9_f1", "ত্রিকোণমিতিক অনুপাতের সংজ্ঞা", "sin θ = লম্ব/অতিভুজ, cos θ = ভূমি/অতিভুজ, tan θ = লম্ব/ভূমি = sin θ / cos θ", "cosec θ = 1/sin θ, sec θ = 1/cos θ, cot θ = 1/tan θ = cos θ / sin θ"),
                BookFormulaSpec("c910_9_f2", "ত্রিকোণমিতিক মৌলিক অভেদাবলি (Pythagorean Identities)", "sin²θ + cos²θ = 1; sec²θ - tan²θ = 1; cosec²θ - cot²θ = 1", "যেখানে 0° ≤ θ ≤ 90°"),
                BookFormulaSpec("c910_9_f3", "আদর্শ কোণের মান (30°, 45°, 60°)", "sin 30° = ½, sin 45° = 1/√2, sin 60° = √3/2; tan 30° = 1/√3, tan 45° = 1, tan 60° = √3", "cos(90° - θ) = sin θ এবং cot(90° - θ) = tan θ")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_9_ex1",
                    titleBn = "উদাহরণ ১: ত্রিকোণমিতিক অভেদ ও মান নির্ণয়",
                    problemStatementBn = "tan A = 4/3 হলে sin A এবং sec A এর মান নির্ণয় কর।",
                    stepsBn = listOf(
                        "দেওয়া আছে লম্ব = 4 এবং ভূমি = 3; পিথাগোরাসের উপপাদ্য মতে অতিভুজ = √(4² + 3²) = √25 = 5",
                        "অতএব, sin A = লম্ব/অতিভুজ = 4/5 এবং sec A = অতিভুজ/ভূমি = 5/3"
                    ),
                    finalAnswerBn = "sin A = 4/5, sec A = 5/3",
                    printedPage = 184
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_9_l1",
                    code = "৯.১-৯.২",
                    titleBn = "ত্রিকোণমিতিক অনুপাত, অভেদাবলি ও আদর্শ কোণের মান",
                    printedPageStart = 174,
                    printedPageEnd = 196,
                    conceptExplanationBn = "নির্দিষ্ট সূক্ষ্মকোণ θ এর সাপেক্ষে সমকোণের বিপরীত বাহু অতিভুজ, প্রদত্ত কোণের বিপরীত বাহু লম্ব এবং কোণ সৃষ্টিকারী সংলগ্ন বাহু ভূমি।",
                    keyPointsBn = listOf(
                        "(sec θ + tan θ) এবং (sec θ - tan θ) পরস্পরের বিপরীত, কারণ (sec θ + tan θ)(sec θ - tan θ) = 1",
                        "একইভাবে (cosec θ + cot θ)(cosec θ - cot θ) = 1"
                    ),
                    stepByStepRulesBn = listOf(
                        "অভেদ প্রমাণের সময় জটিল পক্ষকে sin θ ও cos θ তে ভেঙে সরল কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "০, ১, ২, ৩, ৪ কে ৪ দিয়ে ভাগ করে বর্গমূল নিলেই যথাক্রমে sin 0°, 30°, 45°, 60°, 90° এর মান পাওয়া যায়!"
                    ),
                    commonMistakesBn = listOf(
                        "সূক্ষ্মকোণের অবস্থান পরিবর্তন হলেও লম্ব ও ভূমি একই ধরে নেওয়া (কোণের বিপরীত বাহুই সবসময় লম্ব)"
                    ),
                    miniCheckQuestionBn = "sin 60° × cos 30° + cos 60° × sin 30° এর মান কত?",
                    miniCheckOptionsBn = listOf("0", "1/2", "√3/2", "1"),
                    miniCheckCorrectIndex = 3,
                    miniCheckExplanationBn = "(√3/2)(√3/2) + (1/2)(1/2) = 3/4 + 1/4 = 1।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৯.১ (পৃষ্ঠা ১৮৩–১৮৫)", "অনুশীলনী ৯.২ (পৃষ্ঠা ১৯৩–১৯৬)"),
            linkedLabTypes = listOf(BookInteractiveLabType.TRIG_UNIT_CIRCLE_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 10,
            titleBn = "দূরত্ব ও উচ্চতা",
            titleEn = "Distance and Elevation",
            printedPageStart = 197,
            printedPageEnd = 204,
            summaryBn = "ভূ-রেখা, ঊর্ধ্বরেখা, উল্লম্বতল, উন্নতি কোণ (Angle of Elevation) ও অবনতি কোণ (Angle of Depression) এবং ত্রিকোণমিতিক অনুপাতের সাহায্যে টাওয়ার, গাছ বা নদীর বিস্তার নির্ণয়।",
            objectivesBn = listOf(
                "উন্নতি কোণ ও অবনতি কোণ চিত্রসহ ব্যাখ্যা করতে পারবে",
                "ত্রিকোণমিতির সাহায্যে দূরত্ব ও উচ্চতা বিষয়ক বাস্তব সমস্যার সমাধান করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_10_f1", "উন্নতি ও অবনতি কোণে উচ্চতা ও দূরত্বের সম্পর্ক", "tan θ = উচ্চতা (h) / ভূমি দূরত্ব (x); ভাঙা গাছের অংশের দৈর্ঘ্য নির্ণয়ে sin θ বা cos θ ব্যবহৃত হয়", "৩০° কোণে ভূমি = √3 × লম্ব; ৬০° কোণে লম্ব = √3 × ভূমি; ৪৫° কোণে লম্ব = ভূমি")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_10_ex1",
                    titleBn = "উদাহরণ ১: মিনারের উচ্চতা নির্ণয়",
                    problemStatementBn = "একটি মিনারের পাদদেশ থেকে 20 মিটার দূরে ভূতলের কোনো বিন্দুতে মিনারের চূড়ার উন্নতি কোণ 60° হলে মিনারটির উচ্চতা কত?",
                    stepsBn = listOf(
                        "মনে করি মিনারের উচ্চতা h মিটার; দেওয়া আছে ভূমি দূরত্ব x = 20 মিটার এবং উন্নতি কোণ θ = 60°",
                        "আমরা জানি, tan 60° = h / 20 ⇒ √3 = h / 20 ⇒ h = 20√3 ≈ 34.64 মিটার"
                    ),
                    finalAnswerBn = "20√3 মিটার (প্রায় 34.64 মিটার)",
                    printedPage = 201
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_10_l1",
                    code = "১০.১",
                    titleBn = "উন্নতি কোণ, অবনতি কোণ এবং উচ্চতা ও দূরত্ব নির্ণয়",
                    printedPageStart = 197,
                    printedPageEnd = 204,
                    conceptExplanationBn = "ভূ-তলের সমান্তরাল রেখার উপরের কোনো বিন্দু ভূমির সাথে যে কোণ তৈরি করে তাকে উন্নতি কোণ এবং নিচের কোনো বিন্দু যে কোণ তৈরি করে তাকে অবনতি কোণ বলে। একান্তর কোণ বলে উন্নতি কোণ ও অবনতি কোণ পরস্পর সমান।",
                    keyPointsBn = listOf(
                        "মিনার বা গাছের পাদদেশের দিকে এগিয়ে গেলে উন্নতি কোণ বৃদ্ধি পায় (যেমন 30° থেকে 60° হয়)"
                    ),
                    stepByStepRulesBn = listOf(
                        "প্রথমে আনুপাতিক সমকোণী ত্রিভুজ এঁকে লম্ব, ভূমি ও অতিভুজ চিহ্নিত কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "উন্নতি কোণ 45° হলে উচ্চতা আর ছায়ার দৈর্ঘ্য সবসময় সমান (h = x)!"
                    ),
                    commonMistakesBn = listOf(
                        "অবনতি কোণকে উল্লম্ব রেখার সাথে চিহ্নিত করা (অবনতি কোণ সর্বদা আনুভূমিক রেখার সাথে তৈরি হয়)"
                    ),
                    miniCheckQuestionBn = "একটি গাছের উচ্চতা ও ছায়ার দৈর্ঘ্য সমান হলে সূর্যের উন্নতি কোণ কত?",
                    miniCheckOptionsBn = listOf("30°", "45°", "60°", "90°"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "tan θ = h / h = 1 = tan 45° ⇒ θ = 45°।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১০ (পৃষ্ঠা ২০১–২০৪)"),
            linkedLabTypes = listOf(BookInteractiveLabType.TRIG_UNIT_CIRCLE_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 11,
            titleBn = "বীজগাণিতিক অনুপাত ও সমানুপাত",
            titleEn = "Algebraic Ratio and Proportion",
            printedPageStart = 205,
            printedPageEnd = 223,
            summaryBn = "বীজগাণিতিক অনুপাত, ক্রমিক সমানুপাত, অনুপাতের রূপান্তর (ব্যস্তকরণ, একান্তরকরণ, যোজন, বিয়োজন, যোজন-বিয়োজন) এবং সমানুপাতের ধর্মাবলি।",
            objectivesBn = listOf(
                "অনুপাতের রূপান্তর বিধি (যোজন-বিয়োজন ইত্যাদি) প্রয়োগ করে বীজগাণিতিক সমতা প্রমাণ করতে পারবে",
                "ক্রমিক সমানুপাত ও ধারাবাহিক অনুপাতের বাস্তব সমস্যার সমাধান করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_11_f1", "যোজন-বিয়োজন বিধি (Componendo and Dividendo)", "a/b = c/d হলে (a + b)/(a - b) = (c + d)/(c - d)", "অনুপাতের রূপান্তরে সবচেয়ে গুরুত্বপূর্ণ বিধি"),
                BookFormulaSpec("c910_11_f2", "ক্রমিক সমানুপাত", "a, b, c ক্রমিক সমানুপাতী হলে a/b = b/c বা b² = ac", "এখানে b কে a ও c এর মধ্যসমানুপাতী বলে"),
                BookFormulaSpec("c910_11_f3", "সমান অনুপাতের সংযোজন বিধি", "a/b = c/d = e/f = k হলে প্রত্যেকটি অনুপাত = (a + c + e)/(b + d + f)", "k-পদ্ধতিতে (a = bk, c = dk, e = fk) প্রমাণ করা সহজ")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_11_ex1",
                    titleBn = "উদাহরণ ১: যোজন-বিয়োজন পদ্ধতির প্রয়োগ",
                    problemStatementBn = "(a + b)/(a - b) = 5/3 হলে a : b এর মান নির্ণয় কর।",
                    stepsBn = listOf(
                        "যোজন-বিয়োজন করে পাই: ((a+b) + (a-b)) / ((a+b) - (a-b)) = (5 + 3) / (5 - 3)",
                        "বা, 2a / 2b = 8 / 2 ⇒ a / b = 4 / 1; অতএব a : b = 4 : 1"
                    ),
                    finalAnswerBn = "4 : 1",
                    printedPage = 213
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_11_l1",
                    code = "১১.১-১১.২",
                    titleBn = "অনুপাতের রূপান্তর, ক্রমিক সমানুপাত ও ধারাবাহিক অনুপাত",
                    printedPageStart = 205,
                    printedPageEnd = 223,
                    conceptExplanationBn = "যখন a, b, c ক্রমিক সমানুপাতী থাকে, তখন a/b = b/c = k ধরলে b = ck এবং a = bk = ck² পাওয়া যায়। এই k-পদ্ধতি ব্যবহার করে জটিল বীজগাণিতিক অভেদগুলো খুব সহজে বামপক্ষ = ডানপক্ষ প্রমাণ করা যায়।",
                    keyPointsBn = listOf(
                        "ব্যস্তকরণ: a/b = c/d ⇒ b/a = d/c",
                        "একান্তরকরণ: a/b = c/d ⇒ a/c = b/d"
                    ),
                    stepByStepRulesBn = listOf(
                        "x = 4ab/(a+b) আকারের অংকে উভয় পক্ষকে 2a ও 2b দ্বারা ভাগ করে যোজন-বিয়োজন কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "(x+y)/(x-y) আকার দেখলেই পুনরায় যোজন-বিয়োজন করলে সরাসরি x/y পাওয়া যায়!"
                    ),
                    commonMistakesBn = listOf(
                        "যোজন-বিয়োজনে হরে (লব - হর) এর পরিবর্তে (হর - লব) লিখে ফেলা"
                    ),
                    miniCheckQuestionBn = "4, x, 9 ক্রমিক সমানুপাতী হলে ধনাত্মক x এর মান কত?",
                    miniCheckOptionsBn = listOf("5", "6", "6.5", "13"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "x² = 4 × 9 = 36 ⇒ x = 6।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১১.১ (পৃষ্ঠা ২১৩–২১৫)", "অনুশীলনী ১১.২ (পৃষ্ঠা ২২০–২২৩)"),
            linkedLabTypes = listOf(BookInteractiveLabType.ALGEBRA_IDENTITY_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 12,
            titleBn = "দুই চলকবিশিষ্ট সরল সহসমীকরণ",
            titleEn = "Simple Simultaneous Equations in Two Variables",
            printedPageStart = 224,
            printedPageEnd = 248,
            summaryBn = "দুই চলকবিশিষ্ট সরল সহসমীকরণের সমাধানযোগ্যতার শর্ত (সমঞ্জস/অসমঞ্জস, নির্ভরশীল/অনির্ভরশীল), প্রতিস্থাপন, অপনয়ন, আড়গুণন (Cross-multiplication) ও লৈখিক পদ্ধতি।",
            objectivesBn = listOf(
                "সহসমীকরণের সহগ তুলনা করে সমাধানের প্রকৃতি ও সংখ্যা নির্ধারণ করতে পারবে",
                "প্রতিস্থাপন, অপনয়ন ও আড়গুণন পদ্ধতিতে সহসমীকরণ সমাধান করতে পারবে",
                "লেখচিত্রের সাহায্যে সরল সহসমীকরণ সমাধান করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_12_f1", "সহসমীকরণের সমাধানযোগ্যতার শর্ত", "(১) a₁/a₂ ≠ b₁/b₂ হলে সমঞ্জস, অনির্ভরশীল ও ১টি মাত্র সমাধান আছে; (২) a₁/a₂ = b₁/b₂ = c₁/c₂ হলে সমঞ্জস, নির্ভরশীল ও অসংখ্য সমাধান; (৩) a₁/a₂ = b₁/b₂ ≠ c₁/c₂ হলে অসমঞ্জস ও কোনো সমাধান নেই", "a₁x + b₁y = c₁ এবং a₂x + b₂y = c₂ এর জন্য"),
                BookFormulaSpec("c910_12_f2", "আড়গুণন (বজ্রগুণন) সূত্র", "x / (b₁c₂ - b₂c₁) = y / (c₁a₂ - c₂a₁) = 1 / (a₁b₂ - a₂b₁)", "যেখানে সমীকরণদ্বয় a₁x + b₁y + c₁ = 0 এবং a₂x + b₂y + c₂ = 0 আকারে সাজানো")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_12_ex1",
                    titleBn = "উদাহরণ ১: সহসমীকরণের প্রকৃতি নির্ণয়",
                    problemStatementBn = "2x + 3y = 7 এবং 6x + 9y = 15 সমীকরণ জোটটি সমঞ্জস কি না এবং এর সমাধান কয়টি?",
                    stepsBn = listOf(
                        "এখানে a₁/a₂ = 2/6 = 1/3, b₁/b₂ = 3/9 = 1/3 এবং c₁/c₂ = 7/15",
                        "দেখা যাচ্ছে a₁/a₂ = b₁/b₂ ≠ c₁/c₂; সুতরাং সমীকরণ জোটটি অসমঞ্জস ও পরস্পর অনির্ভরশীল এবং এর কোনো সমাধান নেই (সমান্তরাল সরলরেখা)"
                    ),
                    finalAnswerBn = "অসমঞ্জস, কোনো সমাধান নেই",
                    printedPage = 228
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_12_l1",
                    code = "১২.১-১২.৪",
                    titleBn = "সমাধানযোগ্যতার শর্ত, আড়গুণন পদ্ধতি ও লেখচিত্র",
                    printedPageStart = 224,
                    printedPageEnd = 248,
                    conceptExplanationBn = "দুই চলকবিশিষ্ট দুইটি সরল সমীকরণ জ্যামিতিকভাবে সমতলে দুইটি সরলরেখা নির্দেশ করে। রেখাদ্বয় পরস্পরকে একটি বিন্দুতে ছেদ করলে একক সমাধান, সমাপতিত হলে অসংখ্য সমাধান এবং সমান্তরাল হলে কোনো সমাধান পাওয়া যায় না।",
                    keyPointsBn = listOf(
                        "আড়গুণন সূত্র প্রয়োগের আগে উভয় সমীকরণের ডানপক্ষ অবশ্যই শূন্য (= 0) করে নিতে হবে"
                    ),
                    stepByStepRulesBn = listOf(
                        "সহগ তুলনা করার সময় উভয় সমীকরণে ধ্রুবপদ একই পাশে আছে কি না তা নিশ্চিত কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "আড়গুণনের চক্রক্রমিক ক্রম: b → c → a → b!"
                    ),
                    commonMistakesBn = listOf(
                        "এক সমীকরণে ধ্রুবপদ বামে এবং অন্যটিতে ডানে রেখে অনুপাত c₁/c₂ বের করা"
                    ),
                    miniCheckQuestionBn = "x + 2y = 5 এবং 2x + 4y = 10 সমীকরণ জোটটির সমাধান কয়টি?",
                    miniCheckOptionsBn = listOf("একটি মাত্র", "দুইটি", "কোনো সমাধান নেই", "অসংখ্য সমাধান"),
                    miniCheckCorrectIndex = 3,
                    miniCheckExplanationBn = "এখানে 1/2 = 2/4 = 5/10, তাই জোটটি সমঞ্জস ও নির্ভরশীল এবং এর অসংখ্য সমাধান আছে।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১২.১ (পৃষ্ঠা ২২৮)", "অনুশীলনী ১২.২ (পৃষ্ঠা ২৩৬–২৩৭)", "অনুশীলনী ১২.৩ (পৃষ্ঠা ২৪০)", "অনুশীলনী ১২.৪ (পৃষ্ঠা ২৪৫–২৪৮)"),
            linkedLabTypes = listOf(BookInteractiveLabType.COORDINATE_GRAPH_LAB, BookInteractiveLabType.EQUATION_BALANCER_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 13,
            titleBn = "সসীম ধারা",
            titleEn = "Finite Series",
            printedPageStart = 249,
            printedPageEnd = 265,
            summaryBn = "অনুক্রম ও ধারা, সমান্তর ধারা (n-তম পদ ও n সংখ্যক পদের সমষ্টি), স্বাভাবিক সংখ্যার বর্গ ও ঘনের সমষ্টি এবং গুণোত্তর ধারা।",
            objectivesBn = listOf(
                "সমান্তর ও গুণোত্তর ধারা শনাক্ত এবং n-তম পদ ও প্রথম n পদের সমষ্টি নির্ণয় করতে পারবে",
                "প্রথম n সংখ্যক স্বাভাবিক সংখ্যার সমষ্টি, বর্গের সমষ্টি ও ঘনের সমষ্টির সূত্র প্রয়োগ করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_13_f1", "সমান্তর ধারার n-তম পদ ও সমষ্টি", "n-তম পদ = a + (n - 1)d; সমষ্টি Sₙ = (n/2){2a + (n - 1)d} = (n/2)(a + p)", "a = ১ম পদ, d = সাধারণ অন্তর, p = শেষ পদ"),
                BookFormulaSpec("c910_13_f2", "স্বাভাবিক সংখ্যার সমষ্টি, বর্গের সমষ্টি ও ঘনের সমষ্টি", "∑n = n(n+1)/2; ∑n² = n(n+1)(2n+1)/6; ∑n³ = {n(n+1)/2}²", "১ম n সংখ্যক স্বাভাবিক সংখ্যার জন্য"),
                BookFormulaSpec("c910_13_f3", "গুণোত্তর ধারার n-তম পদ ও সমষ্টি", "n-তম পদ = arⁿ⁻¹; সমষ্টি Sₙ = a(rⁿ - 1)/(r - 1) [যখন r > 1] অথবা a(1 - rⁿ)/(1 - r) [যখন r < 1]", "a = ১ম পদ, r = সাধারণ অনুপাত (r ≠ 1)")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_13_ex1",
                    titleBn = "উদাহরণ ১: সমান্তর ধারার পদ ও সমষ্টি নির্ণয়",
                    problemStatementBn = "5 + 8 + 11 + 14 + ... ধারাটির কোন পদ 383? এবং প্রথম 20টি পদের সমষ্টি কত?",
                    stepsBn = listOf(
                        "এখানে ১ম পদ a = 5, সাধারণ অন্তর d = 8 - 5 = 3; ধরি n-তম পদ = 383",
                        "5 + (n - 1)3 = 383 ⇒ 3(n - 1) = 378 ⇒ n - 1 = 126 ⇒ n = 127 (১২৭তম পদ)",
                        "প্রথম 20 পদের সমষ্টি S₂₀ = (20/2){2×5 + (20-1)×3} = 10(10 + 57) = 670"
                    ),
                    finalAnswerBn = "১২৭তম পদ এবং ২০ পদের সমষ্টি 670",
                    printedPage = 254
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_13_l1",
                    code = "১৩.১-১৩.২",
                    titleBn = "সমান্তর ধারা, গুণোত্তর ধারা ও বিশেষ ধারার সমষ্টি",
                    printedPageStart = 249,
                    printedPageEnd = 265,
                    conceptExplanationBn = "কোনো ধারার যেকোনো পদ ও তার পূর্ববর্তী পদের পার্থক্য সর্বদা সমান হলে তাকে সমান্তর ধারা (Arithmetic Series) এবং অনুপাত সর্বদা সমান হলে তাকে গুণোত্তর ধারা (Geometric Series) বলে।",
                    keyPointsBn = listOf(
                        "প্রথম n সংখ্যক স্বাভাবিক সংখ্যার ঘনের সমষ্টি তাদের যোগফলের বর্গের সমান: 1³ + 2³ + ... + n³ = (1 + 2 + ... + n)²"
                    ),
                    stepByStepRulesBn = listOf(
                        "ধারাটি সমান্তর নাকি গুণোত্তর তা প্রথমে d = ২য় পদ - ১ম পদ অথবা r = ২য় পদ ÷ ১ম পদ বের করে নিশ্চিত কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "1³ + 2³ + ... + n³ = (∑n)² — ঘনের সমষ্টি মানে সাধারণ যোগফলের বর্গ!"
                    ),
                    commonMistakesBn = listOf(
                        "গুণোত্তর ধারার n-তম পদের সূত্রে arⁿ লেখা (সঠিক সূত্র arⁿ⁻¹)"
                    ),
                    miniCheckQuestionBn = "2 + 4 + 8 + 16 + ... গুণোত্তর ধারাটির ৭ম পদ কত?",
                    miniCheckOptionsBn = listOf("64", "128", "256", "512"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "এখানে a = 2, r = 2; ৭ম পদ = ar⁶ = 2 × 2⁶ = 2⁷ = 128।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১৩.১ (পৃষ্ঠা ২৫৪–২৫৬)", "অনুশীলনী ১৩.২ (পৃষ্ঠা ২৬৩–২৬৫)"),
            linkedLabTypes = listOf(BookInteractiveLabType.SERIES_PROGRESSION_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 14,
            titleBn = "অনুপাত, সদৃশতা ও প্রতিসমতা",
            titleEn = "Ratio, Similarity and Symmetry",
            printedPageStart = 266,
            printedPageEnd = 284,
            summaryBn = "জ্যামিতিক সমানুপাত (থেলিসের উপপাদ্য - উপপাদ্য ২৮), ত্রিভুজের কোণের সমদ্বিখণ্ডক উপপাদ্য, সদৃশ ত্রিভুজ এবং রেখা প্রতিসমতা ও ঘূর্ণন প্রতিসমতা।",
            objectivesBn = listOf(
                "ত্রিভুজের বাহু ও ক্ষেত্রফলের অনুপাত সংক্রান্ত উপপাদ্য প্রমাণ ও প্রয়োগ করতে পারবে",
                "দুইটি ত্রিভুজের সদৃশতার শর্তগুলো ব্যাখ্যা ও প্রয়োগ করতে পারবে",
                "জ্যামিতিক চিত্রের রৈখিক প্রতিসাম্য রেখা এবং ঘূর্ণন প্রতিসমতার ক্রম ও কোণ নির্ণয় করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_14_f1", "থেলিসের উপপাদ্য (উপপাদ্য ২৮)", "ত্রিভুজের যেকোনো বাহুর সমান্তরাল সরলরেখা অপর দুই বাহুকে সমান অনুপাতে বিভক্ত করে (DE ∥ BC হলে AD/DB = AE/EC)", "এবং বিপরীত উপপাদ্য ২৯"),
                BookFormulaSpec("c910_14_f2", "কোণের অন্তর্দ্বিখণ্ডক উপপাদ্য (উপপাদ্য ৩০)", "ΔABC এর ∠A এর সমদ্বিখণ্ডক AD ভূমি BC কে D বিন্দুতে ছেদ করলে BD / DC = AB / AC", "সন্নিহিত বাহুদ্বয়ের অনুপাতে ভূমিকে অন্তর্বিভক্ত করে"),
                BookFormulaSpec("c910_14_f3", "ঘূর্ণন প্রতিসমতার কোণ ও ক্রম", "ঘূর্ণন কোণ θ হলে ঘূর্ণন প্রতিসমতার ক্রম n = 360° / θ", "সুষম n-ভুজের প্রতিসাম্য রেখা nটি এবং ঘূর্ণন ক্রম n")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_14_ex1",
                    titleBn = "উদাহরণ ১: থেলিসের উপপাদ্যের প্রয়োগ",
                    problemStatementBn = "ΔABC এ DE ∥ BC। যদি AD = 4 সেমি, DB = 6 সেমি এবং AE = 6 সেমি হয়, তবে EC এর দৈর্ঘ্য কত?",
                    stepsBn = listOf(
                        "উপপাদ্য ২৮ অনুসারে, AD / DB = AE / EC",
                        "মান বসিয়ে পাই: 4 / 6 = 6 / EC ⇒ 4 × EC = 36 ⇒ EC = 9 সেমি"
                    ),
                    finalAnswerBn = "9 সেমি",
                    printedPage = 271
                )
            ),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c910_14_th28",
                    titleBn = "উপপাদ্য ২৮–৩৫: জ্যামিতিক সমানুপাত ও সদৃশতা",
                    typeLabelBn = "উপপাদ্য",
                    statementBn = "উপপাদ্য ২৮: ত্রিভুজের যেকোনো বাহুর সমান্তরাল সরলরেখা ঐ ত্রিভুজের অপর বাহুদ্বয়কে বা তাদের বর্ধিতাংশদ্বয়কে সমান অনুপাতে বিভক্ত করে। উপপাদ্য ৩২: দুইটি ত্রিভুজ সদৃশকোণী হলে তাদের অনুরূপ বাহুগুলো সমানুপাতিক।",
                    stepsBn = listOf(
                        "একই উচ্চতাবিশিষ্ট ত্রিভুজগুলোর ক্ষেত্রফল ভূমির সমানুপাতিক — এই ধর্ম ব্যবহার করে উপপাদ্য ২৮ প্রমাণ করা হয়"
                    ),
                    printedPage = 269
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_14_l1",
                    code = "১৪.১-১৪.৩",
                    titleBn = "জ্যামিতিক সমানুপাত, সদৃশতা ও প্রতিসমতা",
                    printedPageStart = 266,
                    printedPageEnd = 284,
                    conceptExplanationBn = "আকার এক কিন্তু মাপ ভিন্ন হতে পারে এমন জ্যামিতিক চিত্রকে সদৃশ চিত্র বলে। সকল সর্বসম চিত্রই সদৃশ, কিন্তু সকল সদৃশ চিত্র সর্বসম নয়।",
                    keyPointsBn = listOf(
                        "সমবাহু ত্রিভুজের প্রতিসাম্য রেখা ৩টি, ঘূর্ণন কোণ ১২০° এবং ঘূর্ণন প্রতিসমতার ক্রম ৩",
                        "বর্গের প্রতিসাম্য রেখা ৪টি, ঘূর্ণন কোণ ৯০° এবং ঘূর্ণন ক্রম ৪; বৃত্তের প্রতিসাম্য রেখা অসংখ্য"
                    ),
                    stepByStepRulesBn = listOf(
                        "সুষম বহুভুজের বাহু সংখ্যা n হলে প্রতিসাম্য রেখা = n এবং ঘূর্ণন কোণ = 360°/n"
                    ),
                    quickMemoryTipsBn = listOf(
                        "বৈদ্যুতিক পাখার ৩টি ব্লেডের ঘূর্ণন কোণ = ৩৬০° ÷ ৩ = ১২০° এবং ক্রম ৩!"
                    ),
                    commonMistakesBn = listOf(
                        "সামান্তরিকের প্রতিসাম্য রেখা ২টি মনে করা (সাধারণ সামান্তরিকের কোনো রৈখিক প্রতিসাম্য রেখা নেই, তবে ঘূর্ণন ক্রম ২)"
                    ),
                    miniCheckQuestionBn = "একটি সুষম ষড়ভুজের ঘূর্ণন প্রতিসমতার কোণ ও ক্রম কত?",
                    miniCheckOptionsBn = listOf("60° এবং 6", "90° এবং 4", "120° এবং 3", "45° এবং 8"),
                    miniCheckCorrectIndex = 0,
                    miniCheckExplanationBn = "ষড়ভুজের বাহু n = 6; ঘূর্ণন ক্রম = 6 এবং ঘূর্ণন কোণ = 360° / 6 = 60°।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১৪.১ (পৃষ্ঠা ২৭১)", "অনুশীলনী ১৪.২ (পৃষ্ঠা ২৭৭–২৭৮)", "অনুশীলনী ১৪.৩ (পৃষ্ঠা ২৮৩–২৮৪)"),
            linkedLabTypes = listOf(BookInteractiveLabType.GEOMETRY_CANVAS_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 15,
            titleBn = "ক্ষেত্রফল সম্পর্কিত উপপাদ্য ও সম্পাদ্য",
            titleEn = "Area Related Theorems and Constructions",
            printedPageStart = 285,
            printedPageEnd = 293,
            summaryBn = "একই ভূমি ও একই সমান্তরাল রেখাযুগলের মধ্যে অবস্থিত সামান্তরিক ও ত্রিভুজক্ষেত্রের ক্ষেত্রফলের সম্পর্ক, পিথাগোরাসের উপপাদ্যের ইউক্লিডীয় প্রমাণ এবং ক্ষেত্রফল সমান রেখে সম্পাদ্য অঙ্কন।",
            objectivesBn = listOf(
                "একই ভূমি ও সমান্তরাল যুগলের মধ্যে সামান্তরিক ও ত্রিভুজের ক্ষেত্রফল উপপাদ্য প্রমাণ করতে পারবে",
                "পিথাগোরাসের উপপাদ্য ও এর বিপরীত উপপাদ্য প্রমাণ করতে পারবে",
                "প্রদত্ত ত্রিভুজ বা চতুর্ভুজের সমান ক্ষেত্রফলবিশিষ্ট সামান্তরিক বা বর্গক্ষেত্র আঁকতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_15_f1", "সামান্তরিক ও ত্রিভুজের ক্ষেত্রফল সম্পর্ক (উপপাদ্য ৩৬–৩৮)", "একই ভূমি ও একই সমান্তরাল রেখাযুগলের মধ্যে অবস্থিত ত্রিভুজক্ষেত্রের ক্ষেত্রফল সামান্তরিক ক্ষেত্রের ক্ষেত্রফলের অর্ধেক", "এবং সকল সামান্তরিক ক্ষেত্রের ক্ষেত্রফল পরস্পর সমান")
            ),
            workedExamples = emptyList(),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c910_15_th36",
                    titleBn = "উপপাদ্য ৩৬–৪০: ক্ষেত্রফল উপপাদ্য ও পিথাগোরাসের উপপাদ্য",
                    typeLabelBn = "উপপাদ্য",
                    statementBn = "উপপাদ্য ৩৬: একই ভূমি ও একই সমান্তরাল রেখাযুগলের মধ্যে অবস্থিত সকল সামান্তরিক ক্ষেত্রের ক্ষেত্রফল সমান। উপপাদ্য ৩৭: ত্রিভুজক্ষেত্রের ক্ষেত্রফল সামান্তরিক ক্ষেত্রের অর্ধেক। উপপাদ্য ৩৯: পিথাগোরাসের উপপাদ্য।",
                    stepsBn = listOf(
                        "পিথাগোরাসের ইউক্লিডীয় প্রমাণে অতিভুজ ও অপর দুই বাহুর ওপর তিনটি বর্গক্ষেত্র এঁকে প্রত্যেক বর্গক্ষেত্রকে সংশ্লিষ্ট ত্রিভুজের দ্বিগুণ দেখিয়ে AB² + AC² = BC² প্রমাণ করা হয়"
                    ),
                    printedPage = 287
                ),
                BookTheoremOrConstruction(
                    id = "c910_15_con12",
                    titleBn = "সম্পাদ্য ১২–১৫: সমান ক্ষেত্রফলবিশিষ্ট ক্ষেত্র অঙ্কন",
                    typeLabelBn = "সম্পাদ্য",
                    statementBn = "সম্পাদ্য ১২: প্রদত্ত ত্রিভুজক্ষেত্রের সমান ক্ষেত্রফলবিশিষ্ট একটি সামান্তরিক আঁকতে হবে যার একটি কোণ নির্দিষ্ট। সম্পাদ্য ১৫: একটি নির্দিষ্ট আয়তক্ষেত্রের সমান ক্ষেত্রফলবিশিষ্ট একটি বর্গক্ষেত্র আঁকতে হবে।",
                    stepsBn = listOf(
                        "আয়তের দৈর্ঘ্য ও প্রস্থকে একই সরলরেখায় বর্ধিত করে অর্ধবৃত্ত এঁকে মধ্যসমানুপাতী লম্ব আঁকলে সেটিই বর্গের এক বাহু হবে"
                    ),
                    printedPage = 291
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_15_l1",
                    code = "১৫.১",
                    titleBn = "ক্ষেত্রফল সম্পর্কিত উপপাদ্য ও সম্পাদ্য",
                    printedPageStart = 285,
                    printedPageEnd = 293,
                    conceptExplanationBn = "জ্যামিতিতে দুটি চিত্র সর্বসম না হয়েও তাদের ক্ষেত্রফল সমান হতে পারে। যেমন ৪ সেমি × ৯ সেমি আয়তক্ষেত্র এবং ৬ সেমি বাহুবিশিষ্ট বর্গক্ষেত্র উভয়েরই ক্ষেত্রফল ৩৬ বর্গ সেমি।",
                    keyPointsBn = listOf(
                        "একই ভূমি ও একই সমান্তরাল যুগলের মধ্যে অবস্থিত সকল ত্রিভুজক্ষেত্রের ক্ষেত্রফল সমান"
                    ),
                    stepByStepRulesBn = listOf(
                        "ত্রিভুজের সমান ক্ষেত্রফলবিশিষ্ট সামান্তরিক আঁকতে ভূমিকে সমদ্বিখণ্ডিত করে অর্ধেক ভূমির ওপর সামান্তরিক আঁকতে হয়"
                    ),
                    quickMemoryTipsBn = listOf(
                        "আয়তের বাহু a ও b হলে সমান ক্ষেত্রফলবিশিষ্ট বর্গের বাহু হবে √(ab)!"
                    ),
                    commonMistakesBn = listOf(
                        "সামান্তরিকের ক্ষেত্রফল নির্ণয়ে তির্যক বাহুকে উচ্চতা হিসেবে গুণ করা"
                    ),
                    miniCheckQuestionBn = "9 সেমি দৈর্ঘ্য ও 4 সেমি প্রস্থবিশিষ্ট আয়তক্ষেত্রের সমান ক্ষেত্রফলবিশিষ্ট বর্গের এক বাহুর দৈর্ঘ্য কত?",
                    miniCheckOptionsBn = listOf("5 সেমি", "6 সেমি", "6.5 সেমি", "13 সেমি"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "বর্গের ক্ষেত্রফল = 9 × 4 = 36 বর্গ সেমি; অতএব এক বাহু = √36 = 6 সেমি।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১৫ (পৃষ্ঠা ২৯২–২৯৩)"),
            linkedLabTypes = listOf(BookInteractiveLabType.GEOMETRY_CANVAS_LAB, BookInteractiveLabType.MENSURATION_3D_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 16,
            titleBn = "পরিমিতি",
            titleEn = "Mensuration",
            printedPageStart = 294,
            printedPageEnd = 325,
            summaryBn = "সমকোণী, সমদ্বিবাহু, সমবাহু ও বিষমবাহু ত্রিভুজের ক্ষেত্রফল (হেরনের সূত্র), চতুর্ভুজ ও সুষম বহুভুজের ক্ষেত্রফল, বৃত্তকলার ক্ষেত্রফল ও চাপের দৈর্ঘ্য এবং আয়তাকার ঘনবস্তু, ঘনক ও বেলন (Cylinder)।",
            objectivesBn = listOf(
                "সকল প্রকার ত্রিভুজ, চতুর্ভুজ ও সুষম বহুভুজের ক্ষেত্রফল নির্ণয় করতে পারবে",
                "বৃত্তের চাপের দৈর্ঘ্য ও বৃত্তকলার ক্ষেত্রফল নির্ণয় করতে পারবে",
                "আয়তাকার ঘনবস্তু, ঘনক ও সমবৃত্তভূমিক বেলনের পৃষ্ঠতলের ক্ষেত্রফল, কর্ণ ও আয়তন নির্ণয় করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_16_f1", "ত্রিভুজের ক্ষেত্রফল (সমবাহু, সমদ্বিবাহু ও হেরনের সূত্র)", "সমবাহু = (√3/4)a²; সমদ্বিবাহু = (b/4)√(4a² - b²); বিষমবাহু = √{s(s-a)(s-b)(s-c)} [যেখানে s = (a+b+c)/2]", "দুই বাহু ও অন্তর্ভুক্ত কোণ দেওয়া থাকলে ক্ষেত্রফল = ½ ab sin θ"),
                BookFormulaSpec("c910_16_f2", "বৃত্তচাপের দৈর্ঘ্য ও বৃত্তকলার ক্ষেত্রফল এবং সুষম বহুভুজ", "বৃত্তচাপ s = (πrθ)/180°; বৃত্তকলার ক্ষেত্রফল = (θ/360°) × πr²; সুষম n-ভুজের ক্ষেত্রফল = (na²/4) cot(180°/n)", "θ কেন্দ্রে উৎপন্ন কোণ (ডিগ্রিতে)"),
                BookFormulaSpec("c910_16_f3", "আয়তাকার ঘনবস্তু, ঘনক ও বেলন (Cylinder)", "ঘনবস্তুর সমগ্রতল = 2(ab+bc+ca), কর্ণ = √(a²+b²+c²), আয়তন = abc; বেলনের বক্রতল = 2πrh, সমগ্রতল = 2πr(r+h), আয়তন = πr²h", "ঘনকের সমগ্রতল = 6a², কর্ণ = √3 a, আয়তন = a³")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_16_ex1",
                    titleBn = "উদাহরণ ১: সমবাহু ত্রিভুজের ক্ষেত্রফল নির্ণয়",
                    problemStatementBn = "একটি সমবাহু ত্রিভুজের প্রত্যেক বাহুর দৈর্ঘ্য 4 মিটার হলে এর ক্ষেত্রফল নির্ণয় কর।",
                    stepsBn = listOf(
                        "সমবাহু ত্রিভুজের বাহু a = 4 মিটার",
                        "ক্ষেত্রফল = (√3 / 4) a² = (√3 / 4) × 4² = 4√3 ≈ 6.93 বর্গমিটার"
                    ),
                    finalAnswerBn = "4√3 বর্গমিটার (প্রায় 6.93 বর্গমিটার)",
                    printedPage = 299
                ),
                BookWorkedExample(
                    id = "c910_16_ex2",
                    titleBn = "উদাহরণ ২: সমবৃত্তভূমিক বেলনের আয়তন ও সমগ্রতলের ক্ষেত্রফল",
                    problemStatementBn = "একটি সমবৃত্তভূমিক বেলনের উচ্চতা 10 সেমি এবং ভূমির ব্যাসার্ধ 7 সেমি হলে এর আয়তন ও সমগ্রতলের ক্ষেত্রফল নির্ণয় কর।",
                    stepsBn = listOf(
                        "এখানে r = 7 সেমি, h = 10 সেমি",
                        "আয়তন = πr²h = 3.1416 × 7² × 10 = 1539.38 ঘন সেমি",
                        "সমগ্রতলের ক্ষেত্রফল = 2πr(r + h) = 2 × 3.1416 × 7 × (7 + 10) = 747.70 বর্গ সেমি"
                    ),
                    finalAnswerBn = "আয়তন 1539.38 ঘন সেমি, সমগ্রতল 747.70 বর্গ সেমি",
                    printedPage = 321
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_16_l1",
                    code = "১৬.১-১৬.৪",
                    titleBn = "ত্রিভুজ, চতুর্ভুজ, বৃত্তকলা এবং ঘনবস্তু ও বেলনের পরিমিতি",
                    printedPageStart = 294,
                    printedPageEnd = 325,
                    conceptExplanationBn = "পরিমিতি অধ্যায়ে দ্বিমাত্রিক ক্ষেত্র (ত্রিভুজ, চতুর্ভুজ, সুষম বহুভুজ, বৃত্তকলা) এবং ত্রিমাত্রিক ঘনবস্তু (আয়তাকার ঘনবস্তু, ঘনক ও বেলন)-এর ক্ষেত্রফল, কর্ণ ও আয়তন নির্ণয়ের পূর্ণাঙ্গ গাণিতিক কৌশল আলোচনা করা হয়েছে।",
                    keyPointsBn = listOf(
                        "সমবাহু ত্রিভুজের বাহু a মিটার বাড়ালে ক্ষেত্রফল যদি k√3 বর্গমিটার বাড়ে, তবে সূত্র বসিয়ে সহজে বাহু নির্ণয় করা যায়",
                        "সুষম ষড়ভুজের ক্ষেত্রফল = 6 × (√3/4)a² = (3√3 / 2)a²"
                    ),
                    stepByStepRulesBn = listOf(
                        "ত্রিমাত্রিক ঘনবস্তুর অংকে সব একক একই (যেমন সব সেমি বা সব মিটার) আছে কি না তা প্রথমে মিলিয়ে নাও"
                    ),
                    quickMemoryTipsBn = listOf(
                        "ঘনকের কর্ণ = √3 a এবং বর্গের কর্ণ = √2 a!"
                    ),
                    commonMistakesBn = listOf(
                        "হেরনের সূত্রে পরিসীমা 2s কে s এর জায়গায় বসানো (s হলো অর্ধপরিসীমা = পরিসীমা/2)"
                    ),
                    miniCheckQuestionBn = "একটি ঘনকের এক ধারের দৈর্ঘ্য 5 সেমি হলে এর সমগ্রতলের ক্ষেত্রফল কত?",
                    miniCheckOptionsBn = listOf("100 বর্গ সেমি", "125 বর্গ সেমি", "150 বর্গ সেমি", "75 বর্গ সেমি"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "ঘনকের সমগ্রতলের ক্ষেত্রফল = 6a² = 6 × 5² = 6 × 25 = 150 বর্গ সেমি।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১৬.১ (পৃষ্ঠা ৩০১–৩০২)", "অনুশীলনী ১৬.২ (পৃষ্ঠা ৩১০–৩১১)", "অনুশীলনী ১৬.৩ (পৃষ্ঠা ৩১৬–৩১৭)", "অনুশীলনী ১৬.৪ (পৃষ্ঠা ৩২৩–৩২৫)"),
            linkedLabTypes = listOf(BookInteractiveLabType.MENSURATION_3D_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 17,
            titleBn = "পরিসংখ্যান",
            titleEn = "Statistics",
            printedPageStart = 326,
            printedPageEnd = 344,
            summaryBn = "বিন্যস্ত উপাত্তের সংক্ষিপ্ত পদ্ধতিতে গড় (Short-cut Method Mean), মধ্যক ও প্রচুরক নির্ণয়ের সূত্র এবং আয়তলেখ, গণসংখ্যা বহুভুজ ও অজিভ রেখা অঙ্কন।",
            objectivesBn = listOf(
                "সংক্ষিপ্ত পদ্ধতিতে বিন্যস্ত উপাত্তের গাণিতিক গড় নির্ণয় করতে পারবে",
                "সূত্রের সাহায্যে বিন্যস্ত উপাত্তের মধ্যক ও প্রচুরক নির্ণয় করতে পারবে",
                "অবিচ্ছিন্ন শ্রেণিসীমা ব্যবহার করে আয়তলেখ, গণসংখ্যা বহুভুজ ও অজিভ রেখা ব্যাখ্যা ও অঙ্কন করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c910_17_f1", "সংক্ষিপ্ত পদ্ধতিতে গড় (Assumed Mean Method)", "গড় x̄ = a + (∑ fᵢuᵢ / n) × h, যেখানে ধাপ বিচ্যুতি uᵢ = (xᵢ - a) / h", "a = আনুমানিক গড়, h = শ্রেণিব্যাপ্তি, n = মোট গণসংখ্যা"),
                BookFormulaSpec("c910_17_f2", "বিন্যস্ত উপাত্তের মধ্যক নির্ণয়ের সূত্র", "মধ্যক = L + (n/2 - F_c) × (h / f_m)", "L = মধ্যক শ্রেণির নিম্নসীমা, F_c = মধ্যক শ্রেণির পূর্ববর্তী শ্রেণির ক্রমযোজিত গণসংখ্যা, f_m = মধ্যক শ্রেণির গণসংখ্যা"),
                BookFormulaSpec("c910_17_f3", "বিন্যস্ত উপাত্তের প্রচুরক নির্ণয়ের সূত্র", "প্রচুরক = L + {f₁ / (f₁ + f₂)} × h", "L = প্রচুরক শ্রেণির নিম্নসীমা, f₁ = প্রচুরক শ্রেণি ও পূর্ববর্তী শ্রেণির গণসংখ্যার পার্থক্য, f₂ = প্রচুরক শ্রেণি ও পরবর্তী শ্রেণির গণসংখ্যার পার্থক্য")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c910_17_ex1",
                    titleBn = "উদাহরণ ১: সূত্রের সাহায্যে প্রচুরক নির্ণয়",
                    problemStatementBn = "কোনো গণসংখ্যা নিবেশনে প্রচুরক শ্রেণি (41-50), প্রচুরক শ্রেণির গণসংখ্যা 25, পূর্ববর্তী শ্রেণির গণসংখ্যা 15 এবং পরবর্তী শ্রেণির গণসংখ্যা 20 হলে প্রচুরক নির্ণয় কর।",
                    stepsBn = listOf(
                        "এখানে L = 41, h = 10, f₁ = 25 - 15 = 10 এবং f₂ = 25 - 20 = 5",
                        "প্রচুরক = L + {f₁ / (f₁ + f₂)} × h = 41 + {10 / (10 + 5)} × 10",
                        "= 41 + (100 / 15) = 41 + 6.67 = 47.67"
                    ),
                    finalAnswerBn = "47.67",
                    printedPage = 340
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c910_17_l1",
                    code = "১৭.১-১৭.৬",
                    titleBn = "সংক্ষিপ্ত পদ্ধতিতে গড়, মধ্যক, প্রচুরক ও পরিসংখ্যানিক লেখচিত্র",
                    printedPageStart = 326,
                    printedPageEnd = 344,
                    conceptExplanationBn = "বড় উপাত্তের ক্ষেত্রে সাধারণ গড় নির্ণয় জটিল হওয়ায় সংক্ষিপ্ত পদ্ধতি (ধাপ বিচ্যুতি পদ্ধতি) ব্যবহার করা হয়। লেখচিত্র অঙ্কনে: আয়তলেখে X-অক্ষে অবিচ্ছিন্ন শ্রেণিসীমা, গণসংখ্যা বহুভুজে শ্রেণির মধ্যমান এবং অজিভ রেখায় শ্রেণির উচ্চসীমা ও Y-অক্ষে ক্রমযোজিত গণসংখ্যা বসানো হয়।",
                    keyPointsBn = listOf(
                        "মধ্যক শ্রেণি হলো সেই শ্রেণি যেখানে (n/2)-তম পদ অবস্থিত",
                        "প্রচুরক শ্রেণি হলো সর্বাধিক গণসংখ্যাবিশিষ্ট শ্রেণি"
                    ),
                    stepByStepRulesBn = listOf(
                        "আয়তলেখ আঁকতে শ্রেণিব্যাপ্তি বিচ্ছিন্ন থাকলে নিম্নসীমা থেকে 0.5 বিয়োগ ও উচ্চসীমার সাথে 0.5 যোগ করে অবিচ্ছিন্ন শ্রেণিসীমা তৈরি কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "F_c হলো মধ্যক শ্রেণির ঠিক আগের শ্রেণির ক্রমযোজিত গণসংখ্যা, আর f_m হলো মধ্যক শ্রেণির নিজস্ব গণসংখ্যা!"
                    ),
                    commonMistakesBn = listOf(
                        "F_c এর জায়গায় মধ্যক শ্রেণির নিজস্ব ক্রমযোজিত গণসংখ্যা বসিয়ে দেওয়া"
                    ),
                    miniCheckQuestionBn = "প্রচুরক শ্রেণির নিম্নসীমা L = 30, f₁ = 6, f₂ = 4 এবং শ্রেণিব্যাপ্তি h = 5 হলে প্রচুরক কত?",
                    miniCheckOptionsBn = listOf("32", "33", "34", "35"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "প্রচুরক = 30 + {6 / (6 + 4)} × 5 = 30 + (6/10) × 5 = 30 + 3 = 33।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১৭ (পৃষ্ঠা ৩৪২–৩৪৪, উত্তরমালা পৃষ্ঠা ৩৪৫–৩৫৪)"),
            linkedLabTypes = listOf(BookInteractiveLabType.STATISTICS_HISTOGRAM_LAB)
        )
    )
}
