package com.example.math

object BookCurriculumClass8 {
    val chapters: List<BookChapterSpec> = listOf(
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 1,
            titleBn = "প্যাটার্ন",
            titleEn = "Patterns",
            printedPageStart = 1,
            printedPageEnd = 11,
            summaryBn = "স্বাভাবিক সংখ্যার প্যাটার্ন, মৌলিক সংখ্যা নির্ণয় (ইরাটোস্থিনিসের ছাঁকনি), ফিবোনাক্কি সংখ্যা, বীজগণিতীয় রাশির মাধ্যমে সংখ্যার প্যাটার্ন প্রকাশ এবং ম্যাজিক বর্গ গঠন।",
            objectivesBn = listOf(
                "প্যাটার্ন কী তা ব্যাখ্যা করতে পারবে",
                "রৈখিক ও জ্যামিতিক প্যাটার্ন লিখতে ও বর্ণনা করতে পারবে",
                "ইরাটোস্থিনিসের ছাঁকনির সাহায্যে মৌলিক সংখ্যা নির্ণয় করতে পারবে",
                "কয়েকটি সংখ্যার যোগফল নির্ণয়ের সূত্র প্রয়োগ করতে পারবে এবং ৩ ও ৪ ক্রমের ম্যাজিক বর্গ গঠন করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_1_f1", "প্রথম k-সংখ্যক ক্রমিক বিজোড় স্বাভাবিক সংখ্যার যোগফল", "যোগফল = k²", "যেমন: ১ + ৩ + ৫ + ৭ = ৪² = ১৬"),
                BookFormulaSpec("c8_1_f2", "ক্রমিক স্বাভাবিক সংখ্যার যোগফল নির্ণয়ের সূত্র", "যোগফল = ((প্রথম সংখ্যা + শেষ সংখ্যা) × পদসংখ্যা) ÷ ২", "১ থেকে ১০ পর্যন্ত যোগফল = ((১+১০)×১০)/২ = ৫৫"),
                BookFormulaSpec("c8_1_f3", "k ক্রমের ম্যাজিক বর্গের ম্যাজিক সংখ্যা", "M = k(k² + 1) / 2", "৩ ক্রমের ম্যাজিক সংখ্যা = ৩(৯+১)/২ = ১৫; ৪ ক্রমের ম্যাজিক সংখ্যা = ৩৪")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_1_ex1",
                    titleBn = "উদাহরণ ১: ফিবোনাক্কি ও সংখ্যার প্যাটার্নের পরবর্তী সংখ্যা নির্ণয়",
                    problemStatementBn = "প্রদত্ত সংখ্যাগুলোর পরবর্তী দুইটি সংখ্যা নির্ণয় কর: ৩, ১০, ১৭, ২৪, ৩১, ...",
                    stepsBn = listOf(
                        "পাশাপাশি দুইটি সংখ্যার পার্থক্য নির্ণয় করি: ১০ - ৩ = ৭, ১৭ - ১০ = ৭, ২৪ - ১৭ = ৭, ৩১ - ২৪ = ৭",
                        "প্রতিবার পার্থক্য ৭ করে বাড়ছে (বীজগণিতীয় রাশি: ৭ক - ৪)",
                        "অতএব পরবর্তী দুইটি সংখ্যা হবে: ৩১ + ৭ = ৩৮ এবং ৩৮ + ৭ = ৪৫"
                    ),
                    finalAnswerBn = "৩৮ এবং ৪৫",
                    printedPage = 4
                ),
                BookWorkedExample(
                    id = "c8_1_ex2",
                    titleBn = "উদাহরণ ২: বীজগণিতীয় রাশি থেকে প্যাটার্নের সমষ্টি নির্ণয়",
                    problemStatementBn = "কোনো প্যাটার্নের বীজগণিতীয় রাশি (২ক + ১) হলে এর প্রথম ৫০টি পদের সমষ্টি নির্ণয় কর।",
                    stepsBn = listOf(
                        "১ম পদ (ক = ১): ২(১) + ১ = ৩",
                        "৫০তম পদ (ক = ৫০): ২(৫০) + ১ = ১০১",
                        "সমষ্টি = ((প্রথম পদ + শেষ পদ) × পদসংখ্যা) / ২ = ((৩ + ১০১) × ৫০) / ২ = (১০৪ × ৫০) / ২ = ২৬০০"
                    ),
                    finalAnswerBn = "২৬০০",
                    printedPage = 8
                )
            ),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c8_1_th1",
                    titleBn = "ইরাটোস্থিনিসের ছাঁকনির সাহায্যে মৌলিক সংখ্যা নির্ণয় ও ২টি বর্গের সমষ্টি",
                    typeLabelBn = "পদ্ধতি",
                    statementBn = "১ থেকে ১০০ পর্যন্ত সংখ্যা লিখে ১ কেটে দিয়ে পর্যায়ক্রমে ২, ৩, ৫, ৭ এর গুণিতকগুলো বাদ দিলে মৌলিক সংখ্যাগুলো পাওয়া যায়। এছাড়া কিছু সংখ্যাকে দুইটি বর্গের সমষ্টিরূপে প্রকাশ করা যায় (যেমন: ৫০ = ১² + ৭² = ৫² + ৫²)।",
                    stepsBn = listOf(
                        "১ বাদে ২ থেকে শুরু করে ক্রমান্বয়ে মৌলিক সংখ্যার গুণিতকগুলো কেটে দাও",
                        "৩ ক্রমের ম্যাজিক বর্গে ১ থেকে ৯ পর্যন্ত সংখ্যাগুলো এমনভাবে সাজাও যেন পাশাপাশি, উপর-নিচ ও কোণাকুণি যোগফল ১৫ হয়"
                    ),
                    printedPage = 3
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_1_l1",
                    code = "১.১-১.৩",
                    titleBn = "প্যাটার্ন, মৌলিক সংখ্যা ও সংখ্যাকে দুইটি বর্গের সমষ্টিরূপে প্রকাশ",
                    printedPageStart = 1,
                    printedPageEnd = 6,
                    conceptExplanationBn = "প্রকৃতি ও গণিতে নির্দিষ্ট নিয়মে সাজানো বিন্যাসকে প্যাটার্ন বলে। পরপর দুটি সংখ্যার যোগফল পরবর্তী সংখ্যার সমান হলে তাকে ফিবোনাক্কি সংখ্যা (০, ১, ১, ২, ৩, ৫, ৮, ১৩...) বলে।",
                    keyPointsBn = listOf(
                        "১ থেকে ১০০ পর্যন্ত মোট ২৫টি মৌলিক সংখ্যা আছে",
                        "৫০ = ১² + ৭² = ৫² + ৫² এবং ৬৫ = ১² + ৮² = ৪² + ৭² (দুইভাবে দুইটি বর্গের সমষ্টিরূপে প্রকাশযোগ্য)"
                    ),
                    stepByStepRulesBn = listOf(
                        "প্যাটার্নের পরবর্তী সংখ্যা বের করতে পাশাপাশি দুটি পদের পার্থক্য বা অনুপাত পরীক্ষা কর",
                        "প্রথম k সংখ্যক বিজোড় সংখ্যার সমষ্টি নির্ণয়ে সরাসরি k² সূত্র ব্যবহার কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "১ থেকে ১০০ পর্যন্ত মৌলিক সংখ্যার ফোন নম্বর কোড: ৪৪-২২-৩২২-৩২১ (মোট ২৫টি)!"
                    ),
                    commonMistakesBn = listOf(
                        "১-কে মৌলিক সংখ্যা মনে করা (১ মৌলিক বা যৌগিক কোনোটিই নয়)"
                    ),
                    miniCheckQuestionBn = "ফিবোনাক্কি ধারা ০, ১, ১, ২, ৩, ৫, ৮, ১৩ এর পরবর্তী সংখ্যাটি কত?",
                    miniCheckOptionsBn = listOf("১৮", "২১", "২৪", "২৬"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "৮ + ১৩ = ২১।"
                ),
                BookLessonSection(
                    id = "c8_1_l2",
                    code = "১.৪-১.৬",
                    titleBn = "ম্যাজিক বর্গ, সংখ্যার খেলা ও জ্যামিতিক প্যাটার্ন",
                    printedPageStart = 7,
                    printedPageEnd = 11,
                    conceptExplanationBn = "একটি বর্গক্ষেত্রকে দৈর্ঘ্য ও প্রস্থ বরাবর সমান ভাগে ভাগ করে স্বাভাবিক সংখ্যাগুলো এমনভাবে বসানো হয় যেন উপর-নিচ, পাশাপাশি ও কোণাকুণি যোগফল সমান হয়, তাকে ম্যাজিক বর্গ বলে।",
                    keyPointsBn = listOf(
                        "৩ ক্রমের ম্যাজিক বর্গের ম্যাজিক সংখ্যা ১৫ এবং মাঝের ঘরের সংখ্যা সবসময় ৫ হয়",
                        "৪ ক্রমের ম্যাজিক বর্গের ম্যাজিক সংখ্যা ৩৪",
                        "দিয়াশলাইয়ের কাঠি দিয়ে তৈরি জ্যামিতিক প্যাটার্নকে বীজগণিতীয় রাশি (যেমন ২ক+১ বা ৩ক+১) দ্বারা প্রকাশ করা যায়"
                    ),
                    stepByStepRulesBn = listOf(
                        "বীজগণিতীয় রাশি নির্ণয়: যদি প্রতি ধাপে d করে বাড়ে এবং ১ম পদ a হয়, তবে রাশিটি হবে d×ক + (a - d)"
                    ),
                    quickMemoryTipsBn = listOf(
                        "ম্যাজিক সংখ্যা M = k(k² + 1) / 2 (k = ৩ হলে ১৫, k = ৪ হলে ৩৪, k = ৫ হলে ৬৫)"
                    ),
                    commonMistakesBn = listOf(
                        "বীজগণিতীয় রাশির ১ম পদ ও সাধারণ অন্তরের চিহ্ন উল্টে ফেলা"
                    ),
                    miniCheckQuestionBn = "৪ ক্রমের ম্যাজিক বর্গের ম্যাজিক সংখ্যা কত?",
                    miniCheckOptionsBn = listOf("১৫", "২৪", "৩৪", "৬৫"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "সূত্র k(k²+1)/2 অনুযায়ী ৪(১৬+১)/২ = ২ × ১৭ = ৩৪।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১ (পৃষ্ঠা ৯–১১)"),
            linkedLabTypes = listOf(BookInteractiveLabType.PATTERN_AND_SEQUENCE_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 2,
            titleBn = "মুনাফা",
            titleEn = "Profit and Compound Interest",
            printedPageStart = 12,
            printedPageEnd = 27,
            summaryBn = "লাভ-ক্ষতি, সরল মুনাফা (I = Prn), মুনাফা-আসল (A = P + I) এবং চক্রবৃদ্ধি মুনাফা ও সবৃদ্ধিমূল (C = P(1+r)^n) সংক্রান্ত বাস্তব ও বাণিজ্যিক সমস্যার সমাধান।",
            objectivesBn = listOf(
                "সরল মুনাফা ও চক্রবৃদ্ধি মুনাফার পার্থক্য ব্যাখ্যা করতে পারবে",
                "সরল ও চক্রবৃদ্ধি মুনাফা সংক্রান্ত গাণিতিক সমস্যার সমাধান করতে পারবে",
                "ব্যাংক হিসাব ও জনসংখ্যা বৃদ্ধির ক্ষেত্রে চক্রবৃদ্ধি সূত্র প্রয়োগ করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_2_f1", "সরল মুনাফা ও সবৃদ্ধিমূল", "I = Prn এবং A = P + I = P(1 + rn)", "P = মূলধন, r = মুনাফার হার, n = সময়"),
                BookFormulaSpec("c8_2_f2", "চক্রবৃদ্ধি মূলধন (সবৃদ্ধিমূল)", "C = P(1 + r)ⁿ", "প্রতি বছরান্তে মূলধনের সাথে মুনাফা যুক্ত হলে"),
                BookFormulaSpec("c8_2_f3", "চক্রবৃদ্ধি মুনাফা", "চক্রবৃদ্ধি মুনাফা = C - P = P(1 + r)ⁿ - P", "চক্রবৃদ্ধি মূলধন ও প্রারম্ভিক মূলধনের পার্থক্য")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_2_ex1",
                    titleBn = "উদাহরণ ১: সরল মুনাফা নির্ণয়",
                    problemStatementBn = "বার্ষিক শতকরা ৮.৫০ টাকা মুনাফায় ২৫০০ টাকার ৩ বছরের সরল মুনাফা ও মুনাফা-আসল নির্ণয় কর।",
                    stepsBn = listOf(
                        "এখানে মূলধন P = ২৫০০ টাকা, হার r = ৮.৫০/১০০ = ১৭/২০০, সময় n = ৩ বছর",
                        "সরল মুনাফা I = Prn = ২৫০০ × (৮.৫/১০০) × ৩ = ৬৩৭.৫০ টাকা",
                        "মুনাফা-আসল A = P + I = ২৫০০ + ৬৩৭.৫০ = ৩১৩৭.৫০ টাকা"
                    ),
                    finalAnswerBn = "সরল মুনাফা ৬৩৭.৫০ টাকা এবং মুনাফা-আসল ৩১৩৭.৫০ টাকা",
                    printedPage = 16
                ),
                BookWorkedExample(
                    id = "c8_2_ex2",
                    titleBn = "উদাহরণ ২: সরল মুনাফা ও চক্রবৃদ্ধি মুনাফার পার্থক্য",
                    problemStatementBn = "বার্ষিক ১০% মুনাফায় ৫০০০ টাকার ২ বছরের চক্রবৃদ্ধি মুনাফা ও সরল মুনাফার পার্থক্য কত?",
                    stepsBn = listOf(
                        "সরল মুনাফা I = ৫০০০ × (১০/১০০) × ২ = ১০০০ টাকা",
                        "চক্রবৃদ্ধি মূলধন C = ৫০০০ × (১ + ০.১)² = ৫০০০ × ১.২১ = ৬০৫০ টাকা",
                        "চক্রবৃদ্ধি মুনাফা = ৬০৫০ - ৫০০০ = ১০৫০ টাকা; পার্থক্য = ১০৫০ - ১০০০ = ৫০ টাকা"
                    ),
                    finalAnswerBn = "৫০ টাকা",
                    printedPage = 26
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_2_l1",
                    code = "২.১",
                    titleBn = "লাভ-ক্ষতি ও সরল মুনাফা (I = Prn)",
                    printedPageStart = 12,
                    printedPageEnd = 20,
                    conceptExplanationBn = "প্রতি বছর শুধু প্রারম্ভিক মূলধনের ওপর যে মুনাফা হিসাব করা হয় তাকে সরল মুনাফা বলে।",
                    keyPointsBn = listOf(
                        "মুনাফা-আসল A = P(1 + rn)",
                        "লাভ বা ক্ষতি সর্বদা ক্রয়মূল্যের ওপর শতকরা হারে হিসাব করা হয়"
                    ),
                    stepByStepRulesBn = listOf(
                        "শতকরা হার r%-কে ভগ্নাংশ r/100 রূপে লিখে সূত্রে বসাও"
                    ),
                    quickMemoryTipsBn = listOf(
                        "কোনো আসল n বছরে দ্বিগুণ হলে r × n = ১০০% (অর্থাৎ ১০% হারে ১০ বছরে আসল দ্বিগুণ হয়)"
                    ),
                    commonMistakesBn = listOf(
                        "মুনাফা-আসল থেকে আসল বিয়োগ না করেই সরাসরি মুনাফার সূত্রে বসানো"
                    ),
                    miniCheckQuestionBn = "বার্ষিক ১০% মুনাফায় ২০০০ টাকার ৩ বছরের সরল মুনাফা কত?",
                    miniCheckOptionsBn = listOf("৩০০ টাকা", "৫০০ টাকা", "৬০০ টাকা", "২৬০০ টাকা"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "I = Prn = ২০০০ × (১০/১০০) × ৩ = ৬০০ টাকা।"
                ),
                BookLessonSection(
                    id = "c8_2_l2",
                    code = "২.২",
                    titleBn = "চক্রবৃদ্ধি মুনাফা ও সবৃদ্ধিমূল (C = P(1+r)^n)",
                    printedPageStart = 21,
                    printedPageEnd = 27,
                    conceptExplanationBn = "প্রতি বছরান্তে মূলধনের সাথে মুনাফা যোগ হয়ে নতুন মূলধন গঠিত হলে তাকে চক্রবৃদ্ধি মূলধন বা সবৃদ্ধিমূল বলে।",
                    keyPointsBn = listOf(
                        "১ম বছর শেষে সরল মুনাফা ও চক্রবৃদ্ধি মুনাফা সমান থাকে",
                        "২ বছরের জন্য চক্রবৃদ্ধি ও সরল মুনাফার পার্থক্য = P × r²"
                    ),
                    stepByStepRulesBn = listOf(
                        "জনসংখ্যা বৃদ্ধির অংকেও চক্রবৃদ্ধি মূলধনের সূত্র C = P(1 + r)^n ব্যবহৃত হয়"
                    ),
                    quickMemoryTipsBn = listOf(
                        "২ বছরের সরল ও চক্রবৃদ্ধি মুনাফার পার্থক্য শর্টকাট: Pr²"
                    ),
                    commonMistakesBn = listOf(
                        "চক্রবৃদ্ধি মূলধন C নির্ণয় করার পর প্রারম্ভিক মূলধন P বিয়োগ না করেই চক্রবৃদ্ধি মুনাফা হিসেবে উত্তর লেখা"
                    ),
                    miniCheckQuestionBn = "বার্ষিক ১০% মুনাফায় ১০০০ টাকার ২ বছরের চক্রবৃদ্ধি মূলধন কত?",
                    miniCheckOptionsBn = listOf("১১০০ টাকা", "১২০০ টাকা", "১২১০ টাকা", "১৩৩১ টাকা"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "C = ১০০০ × (১ + ০.১)² = ১০০০ × ১.২১ = ১২১০ টাকা।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ২.১ (পৃষ্ঠা ১৭–২০)", "অনুশীলনী ২.২ (পৃষ্ঠা ২৫–২৭)"),
            linkedLabTypes = listOf(BookInteractiveLabType.PROFIT_INTEREST_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 3,
            titleBn = "পরিমাপ",
            titleEn = "Measurement",
            printedPageStart = 28,
            printedPageEnd = 46,
            summaryBn = "দেশীয়, ব্রিটিশ ও আন্তর্জাতিক পরিমাপ পদ্ধতি, দৈর্ঘ্য, ক্ষেত্রফল, ওজন ও তরল পদার্থের আয়তন পরিমাপ এবং পুকুরের পাড় ও রাস্তাসহ ক্ষেত্রফলের বাস্তব সমস্যা।",
            objectivesBn = listOf(
                "দেশীয়, ব্রিটিশ ও আন্তর্জাতিক পরিমাপ পদ্ধতির পারস্পরিক সম্পর্ক ব্যাখ্যা করতে পারবে",
                "দৈর্ঘ্য, ক্ষেত্রফল, ওজন ও তরল পদার্থের আয়তন সংবলিত গাণিতিক সমস্যার সমাধান করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_3_f1", "আয়তন ও তরল পদার্থের একক সম্পর্ক", "১০০০ ঘন সেন্টিমিটার = ১ লিটার; ১ ঘন মিটার = ১০০০ লিটার", "৪° সেলসিয়াস তাপমাত্রায় ১ লিটার বিশুদ্ধ পানির ওজন ১ কিলোগ্রাম"),
                BookFormulaSpec("c8_3_f2", "আয়তাকার ও বর্গাকার ক্ষেত্রের কর্ণ", "আয়তক্ষেত্রের কর্ণ = √(দৈর্ঘ্য² + প্রস্থ²); বর্গক্ষেত্রের কর্ণ = √২ × এক বাহু", "পিথাগোরাসের উপপাদ্য অনুসারে"),
                BookFormulaSpec("c8_3_f3", "রাস্তাবাদে বা রাস্তাসহ বাগানের ক্ষেত্রফল", "রাস্তার ক্ষেত্রফল = রাস্তাসহ ক্ষেত্রফল - রাস্তাবাদে ক্ষেত্রফল", "চারদিকে সমান চওড়া w রাস্তা থাকলে নতুন দৈর্ঘ্য = L ± 2w")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_3_ex1",
                    titleBn = "উদাহরণ ১: পুকুরের পাড়ের ক্ষেত্রফল নির্ণয়",
                    problemStatementBn = "একটি পুকুরের দৈর্ঘ্য ৬০ মিটার এবং প্রস্থ ৪০ মিটার। পুকুরের পাড়ের বিস্তার ৩ মিটার হলে, পাড়ের ক্ষেত্রফল নির্ণয় কর।",
                    stepsBn = listOf(
                        "পুকুরের ক্ষেত্রফল = ৬০ × ৪০ = ২৪০০ বর্গমিটার",
                        "পাড়সহ পুকুরের দৈর্ঘ্য = ৬০ + (৩ × ২) = ৬৬ মিটার এবং প্রস্থ = ৪০ + (৩ × ২) = ৪৬ মিটার",
                        "পাড়সহ পুকুরের ক্ষেত্রফল = ৬৬ × ৪৬ = ৩০৩৬ বর্গমিটার; অতএব পাড়ের ক্ষেত্রফল = ৩০৩৬ - ২৪০০ = ৬৩৬ বর্গমিটার"
                    ),
                    finalAnswerBn = "৬৩৬ বর্গমিটার",
                    printedPage = 39
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_3_l1",
                    code = "৩.১-৩.৫",
                    titleBn = "মেট্রিক ও ব্রিটিশ একক, ক্ষেত্রফল এবং আয়তন পরিমাপ",
                    printedPageStart = 28,
                    printedPageEnd = 46,
                    conceptExplanationBn = "১ মিটার = উত্তর মেরু থেকে বিষুবরেখা পর্যন্ত দূরত্বের ১ কোটি ভাগের ১ ভাগ। তরল পদার্থের আয়তন লিটারে এবং কঠিন বস্তুর আয়তন ঘনমিটার বা ঘনসেন্টিমিটারে মাপা হয়।",
                    keyPointsBn = listOf(
                        "১ ইঞ্চি = ২.৫৪ সেন্টিমিটার, ১ মাইল ≈ ১.৬১ কিলোমিটার",
                        "১ এয়র = ১০০ বর্গমিটার, ১ হেক্টর = ১০০ এয়র = ১০,০০০ বর্গমিটার, ১ একর = ৪০৪৬.৮৬ বর্গমিটার",
                        "১ মেট্রিক টন = ১০ কুইন্টাল = ১০০০ কিলোগ্রাম"
                    ),
                    stepByStepRulesBn = listOf(
                        "বাগানের বাইরে রাস্তা থাকলে দৈর্ঘ্য ও প্রস্থের সাথে ২ × রাস্তার বিস্তার যোগ কর; ভেতরে থাকলে ২ × বিস্তার বিয়োগ কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "১ ঘন সেমি বিশুদ্ধ পানির ওজন = ১ গ্রাম; ১ লিটার বিশুদ্ধ পানির ওজন = ১ কেজি!"
                    ),
                    commonMistakesBn = listOf(
                        "চারদিকে রাস্তা থাকলে শুধু একবার রাস্তার বিস্তার যোগ/বিয়োগ করা (দুই পাশের জন্য ২ গুণ করতে হবে)"
                    ),
                    miniCheckQuestionBn = "১ হেক্টর সমান কত বর্গমিটার?",
                    miniCheckOptionsBn = listOf("১০০ বর্গমিটার", "১০০০ বর্গমিটার", "১০,০০০ বর্গমিটার", "১,০০,০০০ বর্গমিটার"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "১ হেক্টর = ১০০ মিটার × ১০০ মিটার = ১০,০০০ বর্গমিটার।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৩ (পৃষ্ঠা ৪২–৪৬)"),
            linkedLabTypes = listOf(BookInteractiveLabType.MENSURATION_3D_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 4,
            titleBn = "বীজগাণিতিক সূত্রাবলি ও প্রয়োগ",
            titleEn = "Algebraic Formulae and Applications",
            printedPageStart = 47,
            printedPageEnd = 74,
            summaryBn = "বর্গ ও ঘনের বীজগাণিতিক সূত্রাবলি ও অনুসিদ্ধান্ত, উৎপাদকে বিশ্লেষণ (মিডল টার্ম ব্রেকআপ) এবং বীজগাণিতিক রাশির গ.সা.গু. ও ল.সা.গু. নির্ণয়।",
            objectivesBn = listOf(
                "বীজগণিতীয় বর্গ ও ঘনের সূত্র ও অনুসিদ্ধান্ত প্রয়োগ করে মান নির্ণয় করতে পারবে",
                "ঘনের সূত্র প্রয়োগ করে উৎপাদকে বিশ্লেষণ করতে পারবে",
                "বীজগণিতীয় রাশির গ.সা.গু. ও ল.সা.গু. নির্ণয় করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_4_f1", "ঘনের বিস্তৃতি সূত্র (Cube Expansion)", "(a + b)³ = a³ + 3a²b + 3ab² + b³ = a³ + b³ + 3ab(a + b)", "এবং (a - b)³ = a³ - b³ - 3ab(a - b)"),
                BookFormulaSpec("c8_4_f2", "ঘনের মান নির্ণয়ের অনুসিদ্ধান্ত", "a³ + b³ = (a + b)³ - 3ab(a + b) এবং a³ - b³ = (a - b)³ + 3ab(a - b)", "মান নির্ণয়ের অংকে ব্যবহৃত"),
                BookFormulaSpec("c8_4_f3", "ঘনের উৎপাদক সূত্র", "a³ + b³ = (a + b)(a² - ab + b²) এবং a³ - b³ = (a - b)(a² + ab + b²)", "উৎপাদকে বিশ্লেষণে ব্যবহৃত")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_4_ex1",
                    titleBn = "উদাহরণ ১: ঘনের অনুসিদ্ধান্তের সাহায্যে মান নির্ণয়",
                    problemStatementBn = "a + b = 3 এবং ab = 2 হলে, a³ + b³ এর মান নির্ণয় কর।",
                    stepsBn = listOf(
                        "আমরা জানি, a³ + b³ = (a + b)³ - 3ab(a + b)",
                        "মান বসিয়ে পাই: (3)³ - 3 × 2 × 3 = 27 - 18 = 9"
                    ),
                    finalAnswerBn = "9",
                    printedPage = 59
                ),
                BookWorkedExample(
                    id = "c8_4_ex2",
                    titleBn = "উদাহরণ ২: বীজগণিতীয় রাশির গ.সা.গু. ও ল.সা.গু.",
                    problemStatementBn = "x² - 4 এবং x² + 4x + 4 রাশি দুইটির গ.সা.গু. ও ল.সা.গু. নির্ণয় কর।",
                    stepsBn = listOf(
                        "১ম রাশি = x² - 2² = (x + 2)(x - 2)",
                        "২য় রাশি = x² + 2·x·2 + 2² = (x + 2)² = (x + 2)(x + 2)",
                        "গ.সা.গু. = (x + 2) এবং ল.সা.গু. = (x - 2)(x + 2)²"
                    ),
                    finalAnswerBn = "গ.সা.গু. (x + 2), ল.সা.গু. (x - 2)(x + 2)²",
                    printedPage = 73
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_4_l1",
                    code = "৪.১-৪.২",
                    titleBn = "বর্গ ও ঘনের সূত্রাবলি এবং অনুসিদ্ধান্তের প্রয়োগ",
                    printedPageStart = 47,
                    printedPageEnd = 63,
                    conceptExplanationBn = "৭ম শ্রেণিতে শেখা বর্গের সূত্রের পাশাপাশি ৮ম শ্রেণিতে ঘনের (Cube) সূত্র ও অনুসিদ্ধান্ত যুক্ত হয়েছে যা দিয়ে উচ্চতর ঘাতের মান সহজে নির্ণয় করা যায়।",
                    keyPointsBn = listOf(
                        "x + 1/x = k হলে x³ + 1/x³ = k³ - 3k",
                        "x - 1/x = k হলে x³ - 1/x³ = k³ + 3k"
                    ),
                    stepByStepRulesBn = listOf(
                        "মান নির্ণয়ের অংকে অনুসিদ্ধান্ত এবং উৎপাদকে বিশ্লেষণের অংকে উৎপাদক সূত্র ব্যবহার কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "x + 1/x = 2 হলে x এর যেকোনো ঘাতের যোগফল xⁿ + 1/xⁿ = 2 হয়!"
                    ),
                    commonMistakesBn = listOf(
                        "a³ + b³ এর মান নির্ণয়ের অনুসিদ্ধান্ত ও উৎপাদক সূত্র অদলবদল করে ফেলা"
                    ),
                    miniCheckQuestionBn = "x + 1/x = 3 হলে x³ + 1/x³ এর মান কত?",
                    miniCheckOptionsBn = listOf("9", "18", "27", "36"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "x³ + 1/x³ = (x + 1/x)³ - 3(x + 1/x) = 3³ - 3(3) = 27 - 9 = 18।"
                ),
                BookLessonSection(
                    id = "c8_4_l2",
                    code = "৪.৩-৪.৪",
                    titleBn = "উৎপাদকে বিশ্লেষণ এবং গ.সা.গু. ও ল.সা.গু.",
                    printedPageStart = 64,
                    printedPageEnd = 74,
                    conceptExplanationBn = "দুই বা ততোধিক বীজগণিতীয় রাশির সাধারণ উৎপাদকগুলোর সর্বোচ্চ ঘাতের গুণফল হলো গ.সা.গু. এবং সকল উৎপাদকের সর্বোচ্চ ঘাতের গুণফল হলো ল.সা.গু.।",
                    keyPointsBn = listOf(
                        "সাংখ্যিক সহগগুলোর গ.সা.গু./ল.সা.গু. আলাদাভাবে বের করে বীজগণিতীয় উৎপাদকের সাথে গুণ করতে হয়"
                    ),
                    stepByStepRulesBn = listOf(
                        "প্রথমে প্রতিটি রাশিকে উৎপাদকে বিশ্লেষণ কর",
                        "সাধারণ উৎপাদকগুলো নিয়ে গ.সা.গু. এবং সব উৎপাদক নিয়ে ল.সা.গু. গঠন কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "গ.সা.গু. = শুধু মিল (Common), ল.সা.গু. = মিল ও অমিল সব (Common × Uncommon)"
                    ),
                    commonMistakesBn = listOf(
                        "ল.সা.গু. নির্ণয়ে একই উৎপাদক বারবার গুণ করা"
                    ),
                    miniCheckQuestionBn = "a³ - 8 এর উৎপাদকে বিশ্লেষিত রূপ কোনটি?",
                    miniCheckOptionsBn = listOf("(a - 2)(a² + 2a + 4)", "(a - 2)(a² - 2a + 4)", "(a + 2)(a² - 2a + 4)", "(a - 2)³"),
                    miniCheckCorrectIndex = 0,
                    miniCheckExplanationBn = "a³ - 2³ = (a - 2)(a² + 2a + 4)।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৪.১ (পৃষ্ঠা ৫৩–৫৫)", "অনুশীলনী ৪.২ (পৃষ্ঠা ৬১–৬৩)", "অনুশীলনী ৪.৩ (পৃষ্ঠা ৬৭–৬৮)", "অনুশীলনী ৪.৪ (পৃষ্ঠা ৭২–৭৪)"),
            linkedLabTypes = listOf(BookInteractiveLabType.ALGEBRA_IDENTITY_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 5,
            titleBn = "বীজগাণিতিক ভগ্নাংশ",
            titleEn = "Algebraic Fractions",
            printedPageStart = 75,
            printedPageEnd = 96,
            summaryBn = "বীজগণিতীয় ভগ্নাংশের লঘিষ্ঠকরণ, সাধারণ হরবিশিষ্টকরণ, যোগ, বিয়োগ, গুণ, ভাগ এবং সরলীকরণ।",
            objectivesBn = listOf(
                "বীজগণিতীয় ভগ্নাংশের যোগ, বিয়োগ, গুণ ও ভাগ করতে পারবে",
                "একাধিক প্রক্রিয়া চিহ্ন ও বন্ধনীযুক্ত বীজগণিতীয় ভগ্নাংশের সরলীকরণ করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_5_f1", "বীজগণিতীয় ভগ্নাংশের গুণ ও ভাগ", "(A/B) × (C/D) = AC / BD এবং (A/B) ÷ (C/D) = (A/B) × (D/C)", "লব ও হরের সাধারণ উৎপাদক কাটাকাটি করে লঘিষ্ঠ আকারে প্রকাশ করতে হয়"),
                BookFormulaSpec("c8_5_f2", "চক্রক্রমিক ভগ্নাংশের যোগফল", "1/((a-b)(b-c)) + 1/((b-c)(c-a)) + 1/((c-a)(a-b)) = 0", "হরগুলোর চক্রক্রমিক চিহ্ন পরিবর্তন করে ল.সা.গু. (a-b)(b-c)(c-a) নিলে লব শূন্য হয়")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_5_ex1",
                    titleBn = "উদাহরণ ১: বীজগণিতীয় ভগ্নাংশের গুণ ও ভাগ",
                    problemStatementBn = "সরল কর: ((x² - y²) / (x + y)²) ÷ ((x - y) / (x + y))",
                    stepsBn = listOf(
                        "১ম ভগ্নাংশ = (x + y)(x - y) / ((x + y)(x + y)) = (x - y) / (x + y)",
                        "ভাগ চিহ্নকে গুণ চিহ্নে রূপান্তর করে ২য় ভগ্নাংশ উল্টে পাই: ((x - y) / (x + y)) × ((x + y) / (x - y)) = 1"
                    ),
                    finalAnswerBn = "1",
                    printedPage = 89
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_5_l1",
                    code = "৫.১-৫.২",
                    titleBn = "বীজগাণিতিক ভগ্নাংশের যোগ-বিয়োগ এবং গুণ-ভাগ ও সরলীকরণ",
                    printedPageStart = 75,
                    printedPageEnd = 96,
                    conceptExplanationBn = "বীজগণিতীয় ভগ্নাংশের সরলীকরণে BODMAS নিয়ম অনুযায়ী প্রথমে বন্ধনী, এরপর 'এর', ভাগ, গুণ, যোগ ও বিয়োগের কাজ পর্যায়ক্রমে সম্পন্ন করতে হয়।",
                    keyPointsBn = listOf(
                        "(b - a) = -(a - b) সম্পর্কটি চক্রক্রমিক ভগ্নাংশের যোগ-বিয়োগে অত্যন্ত কার্যকর"
                    ),
                    stepByStepRulesBn = listOf(
                        "গুণ বা ভাগের আগে প্রতিটি ভগ্নাংশের লব ও হরকে উৎপাদকে বিশ্লেষণ করে নাও"
                    ),
                    quickMemoryTipsBn = listOf(
                        "ভাগের পরের ভগ্নাংশটিকে উল্টে গুণ চিহ্ন দাও!"
                    ),
                    commonMistakesBn = listOf(
                        "যোগ বা বিয়োগের মাঝখানে লব ও হর কাটাকাটি করা (শুধু গুণের সম্পর্ক থাকলে কাটাকাটি করা যায়)"
                    ),
                    miniCheckQuestionBn = "((a² - b²) / (a - b)) ÷ (a + b) এর মান কত?",
                    miniCheckOptionsBn = listOf("0", "1", "a - b", "a + b"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "((a+b)(a-b)/(a-b)) ÷ (a+b) = (a+b) ÷ (a+b) = 1।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৫.১ (পৃষ্ঠা ৮৩–৮৫)", "অনুশীলনী ৫.২ (পৃষ্ঠা ৯২–৯৬)"),
            linkedLabTypes = listOf(BookInteractiveLabType.ALGEBRA_IDENTITY_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 6,
            titleBn = "সরল সহসমীকরণ",
            titleEn = "Simple Simultaneous Equations",
            printedPageStart = 97,
            printedPageEnd = 114,
            summaryBn = "দুই চলকবিশিষ্ট সরল সহসমীকরণ, প্রতিস্থাপন ও অপনয়ন পদ্ধতিতে সমাধান, বাস্তব ভিত্তিক সমস্যার সহসমীকরণ গঠন ও লেখচিত্রের সাহায্যে সমাধান।",
            objectivesBn = listOf(
                "প্রতিস্থাপন ও অপনয়ন পদ্ধতিতে দুই চলকবিশিষ্ট সরল সহসমীকরণের সমাধান করতে পারবে",
                "বাস্তব সমস্যার ভিত্তিতে সহসমীকরণ গঠন ও সমাধান করতে পারবে",
                "লেখচিত্রের সাহায্যে সরল সহসমীকরণের সমাধান (ছেদবিন্দু) নির্ণয় করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_6_f1", "দুই চলকবিশিষ্ট সরল সহসমীকরণের আদর্শ রূপ", "a₁x + b₁y = c₁ এবং a₂x + b₂y = c₂", "যুগপৎভাবে x ও y এর যে মান সমীকরণদ্বয়কে সিদ্ধ করে তাই সহসমীকরণের সমাধান (x, y)")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_6_ex1",
                    titleBn = "উদাহরণ ১: অপনয়ন পদ্ধতিতে সমাধান",
                    problemStatementBn = "x + y = 7 এবং x - y = 3 সমীকরণদ্বয় সমাধান কর।",
                    stepsBn = listOf(
                        "সমীকরণ দুইটি যোগ করে পাই: (x + y) + (x - y) = 7 + 3 বা, 2x = 10 বা, x = 5",
                        "১ম সমীকরণে x = 5 বসিয়ে পাই: 5 + y = 7 বা, y = 2",
                        "নির্ণেয় সমাধান: (x, y) = (5, 2)"
                    ),
                    finalAnswerBn = "(x, y) = (5, 2)",
                    printedPage = 102
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_6_l1",
                    code = "৬.১-৬.২",
                    titleBn = "প্রতিস্থাপন ও অপনয়ন পদ্ধতি এবং লেখচিত্রের সাহায্যে সমাধান",
                    printedPageStart = 97,
                    printedPageEnd = 114,
                    conceptExplanationBn = "প্রতিস্থাপন পদ্ধতিতে একটি সমীকরণ থেকে এক চলকের মান অন্য চলকের মাধ্যমে প্রকাশ করে অপর সমীকরণে বসানো হয়। অপনয়ন পদ্ধতিতে সমীকরণদ্বয়কে উপযুক্ত সংখ্যা দ্বারা গুণ করে যোগ বা বিয়োগের মাধ্যমে একটি চলক অপসারণ করা হয়।",
                    keyPointsBn = listOf(
                        "দুইটি সরলরেখার ছেদবিন্দুর ভুজ ও কোটিই প্রদত্ত সহসমীকরণের লেখচিত্রভিত্তিক সমাধান (x, y)"
                    ),
                    stepByStepRulesBn = listOf(
                        "সমাধান পাওয়ার পর (x, y) মান মূল সমীকরণ দুটিতে বসিয়ে শুদ্ধি পরীক্ষা কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "যখন x+y=S এবং x-y=D দেওয়া থাকে, তখন x = (S+D)/2 এবং y = (S-D)/2"
                    ),
                    commonMistakesBn = listOf(
                        "সমীকরণ বিয়োগ করার সময় নিচের সমীকরণের সব পদের চিহ্ন পরিবর্তন না করা"
                    ),
                    miniCheckQuestionBn = "x + y = 10 এবং x - y = 4 হলে (x, y) এর মান কত?",
                    miniCheckOptionsBn = listOf("(6, 4)", "(7, 3)", "(8, 2)", "(5, 5)"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "x = (10+4)/2 = 7 এবং y = (10-4)/2 = 3।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৬.১ (পৃষ্ঠা ১০৩–১০৪)", "অনুশীলনী ৬.২ (পৃষ্ঠা ১১১–১১৪)"),
            linkedLabTypes = listOf(BookInteractiveLabType.EQUATION_BALANCER_LAB, BookInteractiveLabType.COORDINATE_GRAPH_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 7,
            titleBn = "সেট",
            titleEn = "Sets",
            printedPageStart = 115,
            printedPageEnd = 124,
            summaryBn = "সেটের ধারণা (জর্জ ক্যান্টর), তালিকা ও সেট গঠন পদ্ধতি, সসীম, অসীম ও ফাঁকা সেট, উপসেট, সার্বিক সেট, পূরক সেট, সংযোগ সেট, ছেদ সেট এবং ভেনচিত্র।",
            objectivesBn = listOf(
                "সেট ও সেট প্রকাশের পদ্ধতি (তালিকা ও গঠন পদ্ধতি) ব্যাখ্যা করতে পারবে",
                "সংযোগ সেট (∪), ছেদ সেট (∩) ও পূরক সেট (A') নির্ণয় করতে পারবে",
                "ভেনচিত্রের সাহায্যে সেট প্রক্রিয়ার সহজ ধর্মাবলি যাচাই করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_7_f1", "সংযোগ ও ছেদ সেট", "A ∪ B = {x : x ∈ A অথবা x ∈ B}; A ∩ B = {x : x ∈ A এবং x ∈ B}", "সংযোগে সব উপাদান (পুনরাবৃত্তি ছাড়া), ছেদে শুধু সাধারণ উপাদান"),
                BookFormulaSpec("c8_7_f2", "পূরক সেট ও উপসেট সংখ্যা", "A' = U \\ A = {x : x ∈ U এবং x ∉ A}; উপসেট সংখ্যা = 2ⁿ", "n সংখ্যক উপাদানবিশিষ্ট সেটের উপসেট সংখ্যা 2ⁿ")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_7_ex1",
                    titleBn = "উদাহরণ ১: সংযোগ ও ছেদ সেট নির্ণয়",
                    problemStatementBn = "A = {1, 2, 3} এবং B = {2, 4, 6} হলে A ∪ B এবং A ∩ B নির্ণয় কর।",
                    stepsBn = listOf(
                        "A ∪ B = {1, 2, 3} ∪ {2, 4, 6} = {1, 2, 3, 4, 6}",
                        "A ∩ B = {1, 2, 3} ∩ {2, 4, 6} = {2}"
                    ),
                    finalAnswerBn = "A ∪ B = {1, 2, 3, 4, 6}, A ∩ B = {2}",
                    printedPage = 120
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_7_l1",
                    code = "৭.১-৭.৭",
                    titleBn = "সেট প্রকাশের পদ্ধতি, প্রকারভেদ, সেট প্রক্রিয়া ও ভেনচিত্র",
                    printedPageStart = 115,
                    printedPageEnd = 124,
                    conceptExplanationBn = "বাস্তব বা চিন্তা জগতের সু-সংজ্ঞায়িত বস্তুর সমাবেশকে সেট বলে। জার্মান গণিতবিদ জর্জ ক্যান্টর (Georg Cantor) সর্বপ্রথম সেট তত্ত্বের ধারণা দেন এবং জন ভেন চিত্রের সাহায্যে সেট প্রকাশ করেন।",
                    keyPointsBn = listOf(
                        "ফাঁকা সেট ∅ যেকোনো সেটের উপসেট",
                        "দুইটি সেটের কোনো সাধারণ উপাদান না থাকলে (A ∩ B = ∅) তাদেরকে নিশ্ছেদ সেট (Disjoint Sets) বলে"
                    ),
                    stepByStepRulesBn = listOf(
                        "সেট গঠন পদ্ধতি থেকে তালিকা পদ্ধতিতে নেওয়ার সময় প্রদত্ত শর্তগুলো একে একে যাচাই করে উপাদানগুলো দ্বিতীয় বন্ধনী {} এর ভেতর কমা দিয়ে লেখ"
                    ),
                    quickMemoryTipsBn = listOf(
                        "∪ (Cup) মানে সব উপাদান একসাথে কাপের ভেতর নাও; ∩ (Cap) মানে শুধু কমন উপাদান!"
                    ),
                    commonMistakesBn = listOf(
                        "ফাঁকা সেটকে {∅} লেখা ({∅} ফাঁকা সেট নয়, এর উপাদান সংখ্যা ১)"
                    ),
                    miniCheckQuestionBn = "একটি সেটের উপাদান সংখ্যা ৩ হলে এর উপসেট সংখ্যা কতটি?",
                    miniCheckOptionsBn = listOf("৩টি", "৬টি", "৮টি", "৯টি"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "উপসেট সংখ্যা = 2ⁿ = 2³ = 8টি।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৭ (পৃষ্ঠা ১২২–১২৪)"),
            linkedLabTypes = listOf(BookInteractiveLabType.SET_VENN_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 8,
            titleBn = "চতুর্ভুজ",
            titleEn = "Quadrilaterals",
            printedPageStart = 125,
            printedPageEnd = 140,
            summaryBn = "চতুর্ভুজের প্রকারভেদ (সামান্তরিক, আয়ত, রম্বস, বর্গ, ট্রাপিজিয়াম, ঘুড়ি), চতুর্ভুজ সংক্রান্ত উপপাদ্যসমূহের প্রমাণ, ক্ষেত্রফল এবং ৫টি উপাত্তের সাহায্যে চতুর্ভুজ অঙ্কন।",
            objectivesBn = listOf(
                "চতুর্ভুজের চার কোণের সমষ্টি চার সমকোণ (৩৬০°) প্রমাণ করতে পারবে",
                "সামান্তরিক, আয়ত, রম্বস ও বর্গের কর্ণ সংক্রান্ত উপপাদ্য প্রমাণ করতে পারবে",
                "প্রদত্ত উপাত্ত থেকে চতুর্ভুজ অঙ্কন করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_8_f1", "চতুর্ভুজের চার কোণের সমষ্টি", "∠A + ∠B + ∠C + ∠D = 360° (৪ সমকোণ)", "কর্ণ চতুর্ভুজটিকে দুইটি ত্রিভুজে বিভক্ত করে (১৮০° + ১৮০° = ৩৬০°)"),
                BookFormulaSpec("c8_8_f2", "রম্বস ও ট্রাপিজিয়ামের ক্ষেত্রফল", "রম্বসের ক্ষেত্রফল = ½ × d₁ × d₂; ট্রাপিজিয়ামের ক্ষেত্রফল = ½ × (a + b) × h", "d₁, d₂ কর্ণদ্বয়; a, b সমান্তরাল বাহুদ্বয় এবং h উচ্চতা")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_8_ex1",
                    titleBn = "উদাহরণ ১: রম্বসের ক্ষেত্রফল নির্ণয়",
                    problemStatementBn = "একটি রম্বসের দুইটি কর্ণের দৈর্ঘ্য যথাক্রমে ৮ সেমি ও ৬ সেমি হলে এর ক্ষেত্রফল নির্ণয় কর।",
                    stepsBn = listOf(
                        "আমরা জানি, রম্বসের ক্ষেত্রফল = ½ × কর্ণদ্বয়ের গুণফল",
                        "ক্ষেত্রফল = ½ × ৮ × ৬ = ২৪ বর্গ সেন্টিমিটার"
                    ),
                    finalAnswerBn = "২৪ বর্গ সেন্টিমিটার",
                    printedPage = 131
                )
            ),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c8_8_th1",
                    titleBn = "উপপাদ্য ১–৫: চতুর্ভুজ ও সামান্তরিকের ধর্মাবলি",
                    typeLabelBn = "উপপাদ্য",
                    statementBn = "উপপাদ্য ১: চতুর্ভুজের চারটি কোণের সমষ্টি চার সমকোণ। উপপাদ্য ২: সামান্তরিকের বিপরীত বাহু ও কোণগুলো পরস্পর সমান। উপপাদ্য ৩: সামান্তরিকের কর্ণদ্বয় পরস্পরকে সমদ্বিখণ্ডিত করে। উপপাদ্য ৪: আয়তের কর্ণদ্বয় সমান ও পরস্পরকে সমদ্বিখণ্ডিত করে। উপপাদ্য ৫: রম্বসের কর্ণদ্বয় পরস্পরকে সমকোণে সমদ্বিখণ্ডিত করে।",
                    stepsBn = listOf(
                        "কর্ণ অঙ্কন করে দুটি ত্রিভুজ গঠন কর",
                        "একান্তর কোণ ও সর্বসমতার শর্ত প্রয়োগ করে বাহু, কোণ ও কর্ণের খণ্ডাংশের সমতা প্রমাণ কর"
                    ),
                    printedPage = 127
                ),
                BookTheoremOrConstruction(
                    id = "c8_8_con1",
                    titleBn = "সম্পাদ্য ১–৭: চতুর্ভুজ অঙ্কন",
                    typeLabelBn = "সম্পাদ্য",
                    statementBn = "একটি নির্দিষ্ট চতুর্ভুজ আঁকার জন্য ৫টি স্বতন্ত্র উপাত্ত প্রয়োজন (যেমন: ৪টি বাহু ও ১টি কোণ, অথবা ৪টি বাহু ও ১টি কর্ণ, অথবা ৩টি বাহু ও ২টি কর্ণ, অথবা ৩টি বাহু ও ২টি অন্তর্ভুক্ত কোণ)।",
                    stepsBn = listOf(
                        "প্রথমে একটি ত্রিভুজ অংশ অঙ্কন কর এবং এরপর অবশিষ্ট দুইটি উপাত্তের বৃত্তচাপ বা কোণ এঁকে চতুর্থ শীর্ষবিন্দু নির্ণয় কর"
                    ),
                    printedPage = 134
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_8_l1",
                    code = "৮.১-৮.৩",
                    titleBn = "চতুর্ভুজের প্রকারভেদ, উপপাদ্য ও সম্পাদ্য",
                    printedPageStart = 125,
                    printedPageEnd = 140,
                    conceptExplanationBn = "যে চতুর্ভুজের দুই জোড়া সন্নিহিত বাহু সমান তাকে ঘুড়ি বলে। বর্গের কর্ণদ্বয় সমান এবং পরস্পরকে সমকোণে সমদ্বিখণ্ডিত করে।",
                    keyPointsBn = listOf(
                        "নির্দিষ্ট চতুর্ভুজ আঁকতে ৫টি স্বতন্ত্র উপাত্ত প্রয়োজন, কিন্তু বর্গ আঁকতে মাত্র ১টি উপাত্ত (বাহু বা কর্ণ) যথেষ্ট"
                    ),
                    stepByStepRulesBn = listOf(
                        "সামান্তরিকের সন্নিহিত দুটি কোণের সমষ্টি সর্বদা ১৮০°"
                    ),
                    quickMemoryTipsBn = listOf(
                        "রম্বস ও বর্গের কর্ণদ্বয় পরস্পরকে ৯০° কোণে ছেদ করে!"
                    ),
                    commonMistakesBn = listOf(
                        "সামান্তরিকের কর্ণদ্বয়কে সমান মনে করা (আয়ত ও বর্গের কর্ণ সমান, সাধারণ সামান্তরিকের নয়)"
                    ),
                    miniCheckQuestionBn = "একটি নির্দিষ্ট চতুর্ভুজ আঁকার জন্য কয়টি স্বতন্ত্র উপাত্তের প্রয়োজন?",
                    miniCheckOptionsBn = listOf("৩টি", "৪টি", "৫টি", "৬টি"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "একটি সাধারণ চতুর্ভুজ আঁকার জন্য ৫টি স্বতন্ত্র উপাত্ত প্রয়োজন।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৮.১ (পৃষ্ঠা ১৩২–১৩৩)", "অনুশীলনী ৮.২ (পৃষ্ঠা ১৩৯–১৪০)"),
            linkedLabTypes = listOf(BookInteractiveLabType.GEOMETRY_CANVAS_LAB, BookInteractiveLabType.MENSURATION_3D_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 9,
            titleBn = "পিথাগোরাসের উপপাদ্য",
            titleEn = "Pythagoras Theorem",
            printedPageStart = 141,
            printedPageEnd = 147,
            summaryBn = "সমকোণী ত্রিভুজের অতিভুজ, লম্ব ও ভূমি সংক্রান্ত পিথাগোরাসের উপপাদ্য, সদৃশকোণী ত্রিভুজ, ট্রাপিজিয়াম ও বীজগণিতের সাহায্যে প্রমাণ এবং বিপরীত উপপাদ্য।",
            objectivesBn = listOf(
                "পিথাগোরাসের উপপাদ্য বিবৃত ও একাধিক পদ্ধতিতে প্রমাণ করতে পারবে",
                "পিথাগোরাসের উপপাদ্য প্রয়োগ করে জ্যামিতিক ও পরিমাপ সংক্রান্ত সমস্যার সমাধান করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_9_f1", "পিথাগোরাসের উপপাদ্য", "অতিভুজ² = লম্ব² + ভূমি² (c² = a² + b²)", "সমকোণী ত্রিভুজের সমকোণের বিপরীত বাহুই অতিভুজ এবং এটি বৃহত্তম বাহু")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_9_ex1",
                    titleBn = "উদাহরণ ১: সমকোণী ত্রিভুজের অতিভুজ নির্ণয়",
                    problemStatementBn = "একটি সমকোণী ত্রিভুজের সমকোণ সংলগ্ন বাহুদ্বয়ের দৈর্ঘ্য ৬ সেমি ও ৮ সেমি হলে অতিভুজের দৈর্ঘ্য কত?",
                    stepsBn = listOf(
                        "পিথাগোরাসের উপপাদ্য অনুসারে, অতিভুজ² = ৬² + ৮² = ৩৬ + ৬৪ = ১০০",
                        "অতএব, অতিভুজ = √১০০ = ১০ সেমি"
                    ),
                    finalAnswerBn = "১০ সেমি",
                    printedPage = 144
                )
            ),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c8_9_th1",
                    titleBn = "পিথাগোরাসের উপপাদ্য ও বিপরীত উপপাদ্য",
                    typeLabelBn = "উপপাদ্য",
                    statementBn = "একটি সমকোণী ত্রিভুজের অতিভুজের ওপর অঙ্কিত বর্গক্ষেত্র অপর দুই বাহুর ওপর অঙ্কিত বর্গক্ষেত্রদ্বয়ের সমষ্টির সমান। বিপরীতক্রমে, কোনো ত্রিভুজের একটি বাহুর ওপর অঙ্কিত বর্গক্ষেত্র অপর দুই বাহুর ওপর অঙ্কিত বর্গক্ষেত্রদ্বয়ের সমষ্টির সমান হলে শেষোক্ত বাহুদ্বয়ের অন্তর্ভুক্ত কোণটি সমকোণ হবে।",
                    stepsBn = listOf(
                        "দুইটি সমকোণী ত্রিভুজের সাহায্যে ট্রাপিজিয়াম গঠন করে তার ক্ষেত্রফল ½(a+b)(a+b) = ½ab + ½ab + ½c² সমীকরণ থেকে a² + b² = c² প্রমাণ কর"
                    ),
                    printedPage = 142
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_9_l1",
                    code = "৯.১-৯.৪",
                    titleBn = "পিথাগোরাসের উপপাদ্যের প্রমাণ ও প্রয়োগ",
                    printedPageStart = 141,
                    printedPageEnd = 147,
                    conceptExplanationBn = "গ্রিক দার্শনিক ও গণিতবিদ পিথাগোরাস খ্রিস্টপূর্ব ষষ্ঠ শতাব্দীতে সমকোণী ত্রিভুজের তিন বাহুর মধ্যে এই যুগান্তকারী সম্পর্ক আবিষ্কার করেন।",
                    keyPointsBn = listOf(
                        "জনপ্রিয় পিথাগোরাসীয় ত্রয়ী (Pythagorean Triples): (৩, ৪, ৫), (৫, ১২, ১৩), (৬, ৮, ১০), (৮, ১৫, ১৭), (৭, ২৪, ২৫)"
                    ),
                    stepByStepRulesBn = listOf(
                        "কোনো ত্রিভুজ সমকোণী কি না তা যাচাই করতে ছোট দুই বাহুর বর্গের যোগফল বৃহত্তম বাহুর বর্গের সমান হয় কি না পরীক্ষা কর"
                    ),
                    quickMemoryTipsBn = listOf(
                        "(৩, ৪, ৫) এবং (৫, ১২, ১৩) ত্রয়ী দুটি মুখস্থ রাখলে এমসিকিউ ১ সেকেন্ডে সমাধান করা যায়!"
                    ),
                    commonMistakesBn = listOf(
                        "অতিভুজ বাদে অন্য কোনো বাহু নির্ণয়ের সময়ও বর্গ যোগ করে ফেলা (লম্ব² = অতিভুজ² - ভূমি² হবে)"
                    ),
                    miniCheckQuestionBn = "সমকোণী ত্রিভুজের অতিভুজ ১৩ সেমি এবং ভূমি ১২ সেমি হলে লম্ব কত?",
                    miniCheckOptionsBn = listOf("৩ সেমি", "৫ সেমি", "৭ সেমি", "২৫ সেমি"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "লম্ব = √(১৩² - ১২²) = √(১৬৯ - ১৪৪) = √২৫ = ৫ সেমি।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ৯ (পৃষ্ঠা ১৪৫–১৪৭)"),
            linkedLabTypes = listOf(BookInteractiveLabType.PYTHAGORAS_THEOREM_VISUALIZER, BookInteractiveLabType.GEOMETRY_CANVAS_LAB)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 10,
            titleBn = "বৃত্ত",
            titleEn = "Circles",
            printedPageStart = 148,
            printedPageEnd = 158,
            summaryBn = "বৃত্তের কেন্দ্র, ব্যাস, ব্যাসার্ধ, জ্যা, চাপ, পরিধি (2πr), বৃত্তের ক্ষেত্রফল (πr²) এবং বৃত্তের জ্যা সংক্রান্ত উপপাদ্যসমূহ।",
            objectivesBn = listOf(
                "বৃত্তের বিভিন্ন অংশ ও π (পাই) এর ধারণা ব্যাখ্যা করতে পারবে",
                "বৃত্তের জ্যা ও কেন্দ্র থেকে দূরত্ব সংক্রান্ত উপপাদ্য প্রমাণ করতে পারবে",
                "বৃত্তের পরিধি ও ক্ষেত্রফল নির্ণয় করে বাস্তব সমস্যার সমাধান করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_10_f1", "বৃত্তের পরিধি", "পরিধি C = 2πr = πd (যেখানে π ≈ 3.1416)", "r = ব্যাসার্ধ, d = 2r = ব্যাস"),
                BookFormulaSpec("c8_10_f2", "বৃত্তের ক্ষেত্রফল", "ক্ষেত্রফল A = πr²", "বৃত্তাকার ক্ষেত্রকে অসংখ্য ক্ষুদ্র ত্রিভুজাকৃতি খণ্ডে বিভক্ত করে পাওয়া যায়")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_10_ex1",
                    titleBn = "উদাহরণ ১: বৃত্তের পরিধি ও ক্ষেত্রফল নির্ণয়",
                    problemStatementBn = "একটি বৃত্তের ব্যাসার্ধ ১০ সেমি হলে এর পরিধি ও ক্ষেত্রফল নির্ণয় কর (π = ৩.১৪১৬)।",
                    stepsBn = listOf(
                        "পরিধি = 2πr = ২ × ৩.১৪১৬ × ১০ = ৬২.৮৩২ সেমি",
                        "ক্ষেত্রফল = πr² = ৩.১৪১৬ × ১০² = ৩১৪.১৬ বর্গ সেমি"
                    ),
                    finalAnswerBn = "পরিধি ৬২.৮৩২ সেমি এবং ক্ষেত্রফল ৩১৪.১৬ বর্গ সেমি",
                    printedPage = 155
                )
            ),
            theoremsOrConstructions = listOf(
                BookTheoremOrConstruction(
                    id = "c8_10_th1",
                    titleBn = "উপপাদ্য ১–৩: বৃত্তের জ্যা সংক্রান্ত উপপাদ্য",
                    typeLabelBn = "উপপাদ্য",
                    statementBn = "উপপাদ্য ১: বৃত্তের কেন্দ্র ও ব্যাস ভিন্ন কোনো জ্যা-এর মধ্যবিন্দুর সংযোজক রেখাংশ ঐ জ্যা-এর ওপর লম্ব। উপপাদ্য ২: বৃত্তের সকল সমান জ্যা কেন্দ্র থেকে সমদূরবর্তী। উপপাদ্য ৩: বৃত্তের কেন্দ্র থেকে সমদূরবর্তী সকল জ্যা পরস্পর সমান।",
                    stepsBn = listOf(
                        "কেন্দ্র থেকে জ্যা-এর দুই প্রান্ত যোগ করে সমকোণী বা সমদ্বিবাহু ত্রিভুজ গঠন কর",
                        "ত্রিভুজের সর্বসমতা প্রয়োগ করে লম্ব বা সমদূরত্ব প্রমাণ কর"
                    ),
                    printedPage = 149
                )
            ),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_10_l1",
                    code = "১০.১-১০.৬",
                    titleBn = "বৃত্তের জ্যা সংক্রান্ত উপপাদ্য, পরিধি ও ক্ষেত্রফল",
                    printedPageStart = 148,
                    printedPageEnd = 158,
                    conceptExplanationBn = "বৃত্তের পরিধি ও ব্যাসের অনুপাত একটি ধ্রুব সংখ্যা, যাকে গ্রিক অক্ষর π (পাই) দ্বারা প্রকাশ করা হয়। ভারতীয় গণিতবিদ আর্যভট্ট π এর আসন্ন মান ৩.১৪১৬ নির্ণয় করেছিলেন।",
                    keyPointsBn = listOf(
                        "ব্যাসই বৃত্তের বৃহত্তম জ্যা",
                        "কেন্দ্র থেকে যেকোনো জ্যা-এর ওপর অঙ্কিত লম্ব জ্যাটিকে সমদ্বিখণ্ডিত করে"
                    ),
                    stepByStepRulesBn = listOf(
                        "চাকার অংকে: চাকা একবার ঘুরলে পরিধির (2πr) সমান দূরত্ব অতিক্রম করে"
                    ),
                    quickMemoryTipsBn = listOf(
                        "কেন্দ্র, জ্যা-এর মধ্যবিন্দু ও লম্ব দূরত্ব সবসময় একটি সমকোণী ত্রিভুজ গঠন করে যেখানে ব্যাসার্ধ r হলো অতিভুজ!"
                    ),
                    commonMistakesBn = listOf(
                        "ব্যাস দেওয়া থাকলে সেটিকে ২ দিয়ে ভাগ না করে সরাসরি ব্যাসার্ধ r এর স্থানে বসানো"
                    ),
                    miniCheckQuestionBn = "বৃত্তের ব্যাসার্ধ ৫ সেমি এবং কেন্দ্র থেকে একটি জ্যা-এর লম্ব দূরত্ব ৩ সেমি হলে জ্যা-টির দৈর্ঘ্য কত?",
                    miniCheckOptionsBn = listOf("৪ সেমি", "৬ সেমি", "৮ সেমি", "১০ সেমি"),
                    miniCheckCorrectIndex = 2,
                    miniCheckExplanationBn = "জ্যা-এর অর্ধেক = √(৫² - ৩²) = ৪ সেমি; অতএব সম্পূর্ণ জ্যা = ২ × ৪ = ৮ সেমি।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১০.১ (পৃষ্ঠা ১৫১)", "অনুশীলনী ১০.২ (পৃষ্ঠা ১৫৪)", "অনুশীলনী ১০.৩ (পৃষ্ঠা ১৫৭–১৫৮)"),
            linkedLabTypes = listOf(BookInteractiveLabType.GEOMETRY_INTERACTIVE_LAB, BookInteractiveLabType.PYTHAGORAS_THEOREM_VISUALIZER)
        ),
        BookChapterSpec(
            bookId = BookId.CLASS_8,
            chapterNumber = 11,
            titleBn = "তথ্য ও উপাত্ত",
            titleEn = "Information and Data",
            printedPageStart = 159,
            printedPageEnd = 174,
            summaryBn = "কেন্দ্রীয় প্রবণতা, অবিন্যস্ত ও বিন্যস্ত উপাত্তের গাণিতিক গড় (Arithmetic Mean), মধ্যক (Median), প্রচুরক (Mode), আয়তলেখ এবং পাইচিত্র (Pie Chart)।",
            objectivesBn = listOf(
                "কেন্দ্রীয় প্রবণতা ও এর পরিমাপকগুলো (গড়, মধ্যক, প্রচুরক) ব্যাখ্যা করতে পারবে",
                "সারণি থেকে গড় নির্ণয় এবং পাইচিত্র (বৃত্তলেখ) অঙ্কন করতে পারবে"
            ),
            formulas = listOf(
                BookFormulaSpec("c8_11_f1", "সারণি থেকে গাণিতিক গড়", "গড় x̄ = (∑ fᵢxᵢ) / n", "fᵢ = শ্রেণির গণসংখ্যা, xᵢ = শ্রেণির মধ্যমান, n = মোট গণসংখ্যা"),
                BookFormulaSpec("c8_11_f2", "অবিন্যস্ত উপাত্তের মধ্যক", "n বিজোড় হলে মধ্যক = (n+1)/2 তম পদ; n জোড় হলে n/2 ও (n/2 + 1) তম পদের গড়", "উপাত্তগুলোকে অবশ্যই মানের ক্রমানুসারে সাজিয়ে নিতে হবে"),
                BookFormulaSpec("c8_11_f3", "পাইচিত্রের কেন্দ্রীয় কোণ", "কেন্দ্রীয় কোণ = (শ্রেণির উপাত্ত / মোট উপাত্ত) × ৩৬০°", "বৃত্তের কেন্দ্রে মোট উৎপন্ন কোণ ৩৬০°")
            ),
            workedExamples = listOf(
                BookWorkedExample(
                    id = "c8_11_ex1",
                    titleBn = "উদাহরণ ১: পাইচিত্রের কেন্দ্রীয় কোণ নির্ণয়",
                    problemStatementBn = "একটি শ্রেণির ৭২ জন শিক্ষার্থীর মধ্যে ১৮ জন ক্রিকেট পছন্দ করে। পাইচিত্রে ক্রিকেট পছন্দকারী শিক্ষার্থীদের কেন্দ্রীয় কোণ কত হবে?",
                    stepsBn = listOf(
                        "মোট শিক্ষার্থী = ৭২ জন এবং বৃত্তের কেন্দ্রের মোট কোণ = ৩৬০°",
                        "১৮ জনের জন্য কেন্দ্রীয় কোণ = (১৮ / ৭২) × ৩৬০° = (১ / ৪) × ৩৬০° = ৯০°"
                    ),
                    finalAnswerBn = "৯০°",
                    printedPage = 168
                )
            ),
            theoremsOrConstructions = emptyList(),
            lessons = listOf(
                BookLessonSection(
                    id = "c8_11_l1",
                    code = "১১.১-১১.৭",
                    titleBn = "কেন্দ্রীয় প্রবণতা: গড়, মধ্যক, প্রচুরক ও পাইচিত্র",
                    printedPageStart = 159,
                    printedPageEnd = 174,
                    conceptExplanationBn = "অবিন্যস্ত উপাত্তগুলো মানের ক্রমানুসারে সাজালে উপাত্তগুলো মাঝামাঝি কোনো মানের চারপাশে পুঞ্জীভূত হওয়ার প্রবণতা দেখায়, একে কেন্দ্রীয় প্রবণতা বলে। কেন্দ্রীয় প্রবণতার পরিমাপ ৩টি: গাণিতিক গড়, মধ্যক ও প্রচুরক।",
                    keyPointsBn = listOf(
                        "শ্রেণির মধ্যমান = (ঊর্ধ্বসীমা + নিম্নসীমা) / ২",
                        "শ্রেণিসংখ্যা = (পরিসর / শ্রেণিব্যাপ্তি) এর পরবর্তী পূর্ণসংখ্যা, যেখানে পরিসর = (সর্বোচ্চ মান - সর্বনিম্ন মান) + ১"
                    ),
                    stepByStepRulesBn = listOf(
                        "পাইচিত্র আঁকার সময় প্রতিটি খাতের মানকে ৩৬০° এর আনুপাতিক কোণে রূপান্তর করে চাঁদার সাহায্যে বৃত্তকলা আঁক"
                    ),
                    quickMemoryTipsBn = listOf(
                        "পরিসর = (বড় মান - ছোট মান) + ১ (শেষে ১ যোগ করতে ভুলবে না!)"
                    ),
                    commonMistakesBn = listOf(
                        "পরিসর নির্ণয়ে শেষে ১ যোগ না করা"
                    ),
                    miniCheckQuestionBn = "কোনো উপাত্তের সর্বোচ্চ মান ৯০ এবং সর্বনিম্ন মান ৪১ হলে এর পরিসর কত?",
                    miniCheckOptionsBn = listOf("৪৯", "৫০", "৫১", "১৩১"),
                    miniCheckCorrectIndex = 1,
                    miniCheckExplanationBn = "পরিসর = (৯০ - ৪১) + ১ = ৪৯ + ১ = ৫০।"
                )
            ),
            exerciseNamesBn = listOf("অনুশীলনী ১১ (পৃষ্ঠা ১৭০–১৭৪)"),
            linkedLabTypes = listOf(BookInteractiveLabType.STATISTICS_HISTOGRAM_LAB)
        )
    )
}
