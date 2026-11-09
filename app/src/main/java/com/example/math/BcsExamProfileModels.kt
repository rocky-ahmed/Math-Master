package com.example.math

/**
 * Verified BCS Examination Profiles supported by Math Master.
 *
 * Modeled according to official BPSC notifications, syllabi, and marks distributions:
 * - [BCS_50TH_PRELIM]: Official 50th BCS Preliminary Syllabus (9 Dec 2025; 20 Math + 15 Mental Ability = 35 marks)
 * - [BCS_WRITTEN_REGULAR]: BPSC Written Exam (Subject Code 008: 50 Math + Subject Code 009: 50 Mental Ability = 100 marks)
 * - [BCS_51ST_SPECIAL]: 51st Special BCS 2026 (10 Math + 10 Mental Ability = 20 marks in 200-mark screening test)
 * - [BCS_LEGACY_PRELIM]: Historical 35th–49th BCS Preliminary (15 Math + 15 Mental Ability = 30 marks)
 */
enum class BcsExamProfile(
    val id: String,
    val titleBn: String,
    val shortBadgeBn: String,
    val stageTitleBn: String,
    val mathMarks: Int,
    val mentalAbilityMarks: Int,
    val totalSubjectMarks: Int,
    val durationMinutes: Int,
    val defaultQuestionCount: Int,
    val officialNegativeMarkingPenalty: Double,
    val publishedDateBn: String,
    val accessedDateBn: String = "৬ নভেম্বর ২০২৬",
    val authorityBn: String = "বাংলাদেশ সরকারি কর্ম কমিশন (BPSC)",
    val officialSourceUrl: String = "https://bpsc.gov.bd/pages/psc-exams/",
    val officialPdfUrl: String = "https://objectstorage.ap-dcc-gazipur-1.oraclecloud15.com/n/axvjbnqprylg/b/V2Ministry/o/office-bpsc/2024/12/be22c1ddda60414fad98b0a82080ef9a.pdf",
    val isDefault: Boolean = false,
    val descriptionBn: String
) {
    BCS_50TH_PRELIM(
        id = "BCS_50TH_PRELIM_2025",
        titleBn = "৫০তম বিসিএস প্রিলিমিনারি (বর্তমান সাধারণ)",
        shortBadgeBn = "৫০তম প্রিলিমিনারি • ৩৫ নম্বর",
        stageTitleBn = "প্রিলিমিনারি পরীক্ষা (MCQ)",
        mathMarks = 20,
        mentalAbilityMarks = 15,
        totalSubjectMarks = 35,
        durationMinutes = 35,
        defaultQuestionCount = 35,
        officialNegativeMarkingPenalty = 0.50,
        publishedDateBn = "৯ ডিসেম্বর ২০২৫",
        officialSourceUrl = "https://bpsc.gov.bd/pages/psc-exams/",
        officialPdfUrl = "https://objectstorage.ap-dcc-gazipur-1.oraclecloud15.com/n/axvjbnqprylg/b/V2Ministry/o/office-bpsc/2024/12/be22c1ddda60414fad98b0a82080ef9a.pdf",
        isDefault = true,
        descriptionBn = "পিএসসি কর্তৃক ৯ ডিসেম্বর ২০২৫ তারিখে প্রকাশিত ৫০তম বিসিএস প্রিলিমিনারি সিলেবাস অনুযায়ী গাণিতিক যুক্তিতে ২০ নম্বর (৫টি অফিশিয়াল ইউনিট × ৪ নম্বর) এবং মানসিক দক্ষতায় ১৫ নম্বর (মোট ৩৫ নম্বর) নির্ধারিত।"
    ),
    BCS_WRITTEN_REGULAR(
        id = "BCS_WRITTEN_REGULAR_008_009",
        titleBn = "সাধারণ বিসিএস লিখিত সমন্বিত (কোড ০০৮ ও ০০৯)",
        shortBadgeBn = "লিখিত সমন্বিত • ১০০ নম্বর",
        stageTitleBn = "আবশ্যিক লিখিত পরীক্ষা (সমন্বিত)",
        mathMarks = 50,
        mentalAbilityMarks = 50,
        totalSubjectMarks = 100,
        durationMinutes = 120,
        defaultQuestionCount = 20,
        officialNegativeMarkingPenalty = 0.0,
        publishedDateBn = "বিপিএসসি সাধারণ লিখিত সিলেবাস",
        officialSourceUrl = "https://bpsc.gov.bd/site/view/psc_exam/BCS%20Examination/",
        officialPdfUrl = "https://bpsc.gov.bd/sites/default/files/files/bpsc.portal.gov.bd/psc_exam/3c11cd64_6370_4e04_be9a_60f82b085597/SYLLABUS%20FOR%20BCS%20%28WRITTEN%29%20EXAMINATION.pdf",
        descriptionBn = "বিসিএস আবশ্যিক লিখিত সিলেবাসের বিষয় কোড ০০৮ (গাণিতিক যুক্তি — পূর্ণমান ৫০) এবং বিষয় কোড ০০৯ (মানসিক দক্ষতা — পূর্ণমান ৫০)-এর সমন্বিত ১০০ নম্বরের প্রস্তুতি প্রোফাইল (অ্যাপের ডিফল্ট অনুশীলন সেটিং: ২০টি প্রশ্ন, কাস্টমাইজযোগ্য)।"
    ),
    BCS_WRITTEN_008_MATH(
        id = "BCS_WRITTEN_008_MATH",
        titleBn = "বিসিএস লিখিত কোড ০০৮ (গাণিতিক যুক্তি — ৫০ নম্বর)",
        shortBadgeBn = "লিখিত কোড ০০৮ • ৫০ নম্বর",
        stageTitleBn = "লিখিত পরীক্ষা • বিষয় কোড ০০৮",
        mathMarks = 50,
        mentalAbilityMarks = 0,
        totalSubjectMarks = 50,
        durationMinutes = 120,
        defaultQuestionCount = 10,
        officialNegativeMarkingPenalty = 0.0,
        publishedDateBn = "বিপিএসসি লিখিত সিলেবাস (কোড ০০৮)",
        officialSourceUrl = "https://bpsc.gov.bd/site/view/psc_exam/BCS%20Examination/",
        officialPdfUrl = "https://bpsc.gov.bd/sites/default/files/files/bpsc.portal.gov.bd/psc_exam/3c11cd64_6370_4e04_be9a_60f82b085597/SYLLABUS%20FOR%20BCS%20%28WRITTEN%29%20EXAMINATION.pdf",
        descriptionBn = "বিপিএসসি আবশ্যিক লিখিত পরীক্ষা (বিষয় কোড ০০৮: গাণিতিক যুক্তি) — অফিশিয়াল সিলেবাস অনুযায়ী পূর্ণমান ৫০ (১২টি গাণিতিক অধ্যায় ক্ষেত্র)। সিলেবাস নথিতে প্রশ্নের সংখ্যা নির্দিষ্ট নয় (ঐতিহাসিক প্রশ্নপত্রে সাধারণত ৫০ নম্বরের জন্য ১০টি বা বিকল্পসহ প্রশ্ন থাকে; অ্যাপের ডিফল্ট অনুশীলন সেটিং ১০টি প্রশ্ন, যা কাস্টমাইজযোগ্য)।"
    ),
    BCS_WRITTEN_009_MENTAL(
        id = "BCS_WRITTEN_009_MENTAL",
        titleBn = "বিসিএস লিখিত কোড ০০৯ (মানসিক দক্ষতা — ৫০ নম্বর)",
        shortBadgeBn = "লিখিত কোড ০০৯ • ৫০ নম্বর",
        stageTitleBn = "লিখিত পরীক্ষা • বিষয় কোড ০০৯",
        mathMarks = 0,
        mentalAbilityMarks = 50,
        totalSubjectMarks = 50,
        durationMinutes = 60,
        defaultQuestionCount = 50,
        officialNegativeMarkingPenalty = 0.0,
        publishedDateBn = "বিপিএসসি লিখিত সিলেবাস (কোড ০০৯)",
        officialSourceUrl = "https://bpsc.gov.bd/site/view/psc_exam/BCS%20Examination/",
        officialPdfUrl = "https://bpsc.gov.bd/sites/default/files/files/bpsc.portal.gov.bd/psc_exam/3c11cd64_6370_4e04_be9a_60f82b085597/SYLLABUS%20FOR%20BCS%20%28WRITTEN%29%20EXAMINATION.pdf",
        descriptionBn = "বিপিএসসি আবশ্যিক লিখিত পরীক্ষা (বিষয় কোড ০০৯: মানসিক দক্ষতা) — অফিশিয়াল সিলেবাস অনুযায়ী পূর্ণমান ৫০ (৬টি ক্ষেত্র: ভাষাগত যৌক্তিক বিচার, বিমূর্ত যুক্তি, স্থানিক সম্পর্ক, সংখ্যাগত দক্ষতা, বানান ও ভাষা এবং যান্ত্রিক দক্ষতা)। অ্যাপে ডিফল্ট ৫০টি নৈর্ব্যক্তিক অনুশীলন প্রশ্ন (কাস্টমাইজযোগ্য)।"
    ),
    BCS_51ST_SPECIAL(
        id = "BCS_51ST_SPECIAL_2026",
        titleBn = "৫১তম বিশেষ বিসিএস ২০২৬ (স্বাস্থ্য ক্যাডার স্ক্রিনিং)",
        shortBadgeBn = "৫১তম বিশেষ • ২০ নম্বর",
        stageTitleBn = "লিখিত/স্ক্রিনিং পরীক্ষা (MCQ)",
        mathMarks = 10,
        mentalAbilityMarks = 10,
        totalSubjectMarks = 20,
        durationMinutes = 20,
        defaultQuestionCount = 20,
        officialNegativeMarkingPenalty = 0.50,
        publishedDateBn = "৪ মার্চ ২০২৬ (৫১তম বিশেষ বিসিএস সিলেবাস)",
        officialSourceUrl = "https://bpsc.gov.bd/site/view/psc_exam/BCS%20Examination/",
        officialPdfUrl = "https://bpsc.gov.bd/sites/default/files/files/bpsc.portal.gov.bd/psc_exam/32fb6be3_5ef7_4b67_97e2_6d4d0e677a91/2026-03-04-08-34-e01e6b3f205725a5df437412826c15f4.pdf",
        descriptionBn = "৫১তম বিশেষ বিসিএস (কেবল স্বাস্থ্য ক্যাডার — সহকারী সার্জন ও সহকারী ডেন্টাল সার্জন; সাধারণ ক্যাডার প্রার্থীদের জন্য প্রযোজ্য নয়)-এর মোট ২০০ নম্বরের এমসিকিউ লিখিত/স্ক্রিনিং পরীক্ষায় গাণিতিক যুক্তি ১০ নম্বর এবং মানসিক দক্ষতা ১০ নম্বর (সমন্বিত মোট ২০ নম্বর) নির্ধারিত।"
    ),
    BCS_LEGACY_PRELIM(
        id = "BCS_LEGACY_35_49",
        titleBn = "প্রাক্তন বিসিএস প্রিলিমিনারি (৩৫তম–৪৯তম • আর্কাইভড)",
        shortBadgeBn = "৩৫তম–৪৯তম আর্কাইভ • ৩০ নম্বর",
        stageTitleBn = "আর্কাইভড প্রিলিমিনারি সিলেবাস (২০১৪–২০২৪)",
        mathMarks = 15,
        mentalAbilityMarks = 15,
        totalSubjectMarks = 30,
        durationMinutes = 30,
        defaultQuestionCount = 30,
        officialNegativeMarkingPenalty = 0.50,
        publishedDateBn = "৩৫তম–৪৯তম বিসিএস সিলেবাস (আর্কাইভড)",
        officialSourceUrl = "https://bpsc.gov.bd/pages/psc-exams/",
        officialPdfUrl = "",
        descriptionBn = "ঐতিহাসিক ৩৫তম থেকে ৪৯তম বিসিএস প্রিলিমিনারি সিলেবাস (২০১৪–২০২৪): গাণিতিক যুক্তি ১৫ নম্বর (৫ ইউনিট × ৩ নম্বর) এবং মানসিক দক্ষতা ১৫ নম্বর, মোট ৩০ নম্বর। (দ্রষ্টব্য: ১০ম–৩৪তম বিসিএসের যাচাইকৃত প্রশ্নসমূহ প্রাক-৩৫তম ১০০ নম্বরের সাধারণ প্রিলিমিনারি কাঠামোর অন্তর্ভুক্ত, যা বিগত প্রশ্ন আর্কাইভে পৃথকভাবে চিহ্নিত)।"
    );

    companion object {
        fun fromIdOrDefault(id: String?): BcsExamProfile {
            return entries.firstOrNull { it.id == id } ?: BCS_50TH_PRELIM
        }
    }
}

/**
 * Application-structured 9-domain pedagogical taxonomy mapping every topic to the official
 * BPSC Preliminary Syllabus (5 Official Units, 20 Marks) and BPSC Written Subject Code 008 Syllabus (12 Topics, 50 Marks).
 *
 * Note: The official BPSC Preliminary syllabus defines 5 units ([BcsSyllabusUnit]), and Written Code 008 defines 12 topic areas.
 * These 9 [BcsMathDomain] categories organize study modules pedagogically while preserving exact official syllabus mappings in [officialSyllabusMappingBn].
 */
enum class BcsMathDomain(
    val id: String,
    val domainNumber: Int,
    val titleBn: String,
    val titleEn: String,
    val prelimUnitNumber: Int?,
    val writtenCode008Included: Boolean,
    val isAppPedagogicalGrouping: Boolean = true,
    val officialSyllabusMappingBn: String,
    val summaryBn: String
) {
    ARITHMETIC(
        id = "ARITHMETIC",
        domainNumber = 1,
        titleBn = "১. পাটিগণিত (Arithmetic)",
        titleEn = "Arithmetic",
        prelimUnitNumber = 1,
        writtenCode008Included = true,
        officialSyllabusMappingBn = "অফিশিয়াল প্রিলিমিনারি ইউনিট ১ (৪ নম্বর) + লিখিত কোড ০০৮ অধ্যায় ১–২",
        summaryBn = "বাস্তব সংখ্যা, ল.সা.গু ও গ.সা.গু, ভগ্নাংশ, শতকরা, সরল ও যৌগিক মুনাফা, লাভ-ক্ষতি, অনুপাত-সমানুপাত এবং ঐকিক নিয়ম (সময়-কাজ-নল-নৌকা-ট্রেন)।"
    ),
    ALGEBRA(
        id = "ALGEBRA",
        domainNumber = 2,
        titleBn = "২. বীজগণিত (Algebra)",
        titleEn = "Algebra",
        prelimUnitNumber = 2,
        writtenCode008Included = true,
        officialSyllabusMappingBn = "অফিশিয়াল প্রিলিমিনারি ইউনিট ২ (৪ নম্বর) + লিখিত কোড ০০৮ অধ্যায় ৩–৫",
        summaryBn = "বীজগাণিতিক সূত্রাবলি, বহুপদী উৎপাদক, একঘাত ও দ্বিঘাত সমীকরণ, অসমতা, দুই চলকবিশিষ্ট সমীকরণ জোট, সূচক ও লগারিদম।"
    ),
    SEQUENCES_SERIES(
        id = "SEQUENCES_SERIES",
        domainNumber = 3,
        titleBn = "৩. ধারা ও অনুক্রম (Sequences & Series)",
        titleEn = "Sequences & Series",
        prelimUnitNumber = 3,
        writtenCode008Included = true,
        officialSyllabusMappingBn = "অফিশিয়াল প্রিলিমিনারি ইউনিট ৩ (৪ নম্বর) + লিখিত কোড ০০৮ অধ্যায় ৬",
        summaryBn = "সমান্তর ও গুণোত্তর অনুক্রম ও ধারা, n-তম পদ, প্রথম n সংখ্যক পদের সমষ্টি এবং স্বাভাবিক সংখ্যার বর্গ ও ঘনের সমষ্টি।"
    ),
    GEOMETRY(
        id = "GEOMETRY",
        domainNumber = 4,
        titleBn = "৪. জ্যামিতি (Geometry)",
        titleEn = "Geometry",
        prelimUnitNumber = 4,
        writtenCode008Included = true,
        officialSyllabusMappingBn = "অফিশিয়াল প্রিলিমিনারি ইউনিট ৪ (জ্যামিতি অংশ) + লিখিত কোড ০০৮ অধ্যায় ৭",
        summaryBn = "রেখা, কোণ, ত্রিভুজ ও চতুর্ভুজ সংক্রান্ত উপপাদ্য, পিথাগোরাসের উপপাদ্য, বৃত্ত সংক্রান্ত উপপাদ্য ও সদৃশতা।"
    ),
    MENSURATION(
        id = "MENSURATION",
        domainNumber = 5,
        titleBn = "৫. পরিমিতি (Mensuration)",
        titleEn = "Mensuration",
        prelimUnitNumber = 4,
        writtenCode008Included = true,
        officialSyllabusMappingBn = "অফিশিয়াল প্রিলিমিনারি ইউনিট ৪ (পরিমিতি অংশ) + লিখিত কোড ০০৮ অধ্যায় ৮",
        summaryBn = "ত্রিভুজ, আয়তক্ষেত্র, বর্গক্ষেত্র, রম্বস, ট্রাপিজিয়াম ও বৃত্তের ক্ষেত্রফল এবং ঘনবস্তু, প্রিজম, বেলন (সিলিন্ডার), কোণক ও গোলকের আয়তন ও পৃষ্ঠতলের ক্ষেত্রফল।"
    ),
    CARTESIAN_GEOMETRY(
        id = "CARTESIAN_GEOMETRY",
        domainNumber = 6,
        titleBn = "৬. স্থানাঙ্ক জ্যামিতি (Cartesian Geometry)",
        titleEn = "Cartesian Geometry",
        prelimUnitNumber = 4,
        writtenCode008Included = true,
        officialSyllabusMappingBn = "অফিশিয়াল লিখিত কোড ০০৮ অধ্যায় ৯ (কার্তেসীয় স্থানাঙ্ক ও সরলরেখার সমীকরণ) + প্রিলিমিনারি ইউনিট ৪ পরিপূরক",
        summaryBn = "কার্তেসীয় স্থানাঙ্ক ব্যবস্থা, দুই বিন্দুর মধ্যবর্তী দূরত্ব নির্ণয়, সরলরেখার ঢাল (Slope) এবং সরলরেখার সমীকরণ (লিখিত কোড ০০৮ ও প্রিলিমিনারি পরিপূরক)।"
    ),
    TRIGONOMETRY(
        id = "TRIGONOMETRY",
        domainNumber = 7,
        titleBn = "৭. ত্রিকোণমিতি (Trigonometry)",
        titleEn = "Trigonometry",
        prelimUnitNumber = 4,
        writtenCode008Included = true,
        officialSyllabusMappingBn = "অফিশিয়াল লিখিত কোড ০০৮ অধ্যায় ১০ (ত্রিকোণমিতি ও দূরত্ব-উচ্চতা) + প্রিলিমিনারি ইউনিট ৪ পরিপূরক",
        summaryBn = "ত্রিকোণমিতিক অনুপাত ও অভেদাবলি, আদর্শ কোণের মান (0°, 30°, 45°, 60°, 90°) এবং দূরত্ব ও উচ্চতা (উন্নতি ও অবনতি কোণ)।"
    ),
    SETS_COUNTING_PROBABILITY(
        id = "SETS_COUNTING_PROBABILITY",
        domainNumber = 8,
        titleBn = "৮. সেট, বিন্যাস, সমাবেশ ও সম্ভাব্যতা",
        titleEn = "Sets, Counting & Probability",
        prelimUnitNumber = 5,
        writtenCode008Included = true,
        officialSyllabusMappingBn = "অফিশিয়াল প্রিলিমিনারি ইউনিট ৫ (সেট, বিন্যাস, সমাবেশ ও সম্ভাব্যতা) + লিখিত কোড ০০৮ অধ্যায় ১১–১২",
        summaryBn = "সেট ও ভেনচিত্র, ফাংশন, বিন্যাস (Permutation), সমাবেশ (Combination) এবং সম্ভাব্যতা (Probability)।"
    ),
    STATISTICS(
        id = "STATISTICS",
        domainNumber = 9,
        titleBn = "৯. পরিসংখ্যান (Statistics)",
        titleEn = "Statistics",
        prelimUnitNumber = 5,
        writtenCode008Included = false,
        officialSyllabusMappingBn = "অফিশিয়াল প্রিলিমিনারি ইউনিট ৫ (পরিসংখ্যান অংশ) + এনসিটিবি ৬ষ্ঠ–১০ম শ্রেণি উপাত্ত ও পরিসংখ্যান ভিত্তি",
        summaryBn = "উপাত্ত বিশ্লেষণ, গাণিতিক গড়, মধ্যক (Median), প্রচুরক (Mode), সংক্ষিপ্ত পদ্ধতিতে গড় নির্ণয় এবং পরিমিত ব্যবধান।"
    );

    companion object {
        fun fromIdOrDefault(id: String?): BcsMathDomain {
            return entries.firstOrNull { it.id == id } ?: ARITHMETIC
        }
    }
}

/**
 * 14 Official BCS Practice & Mock Examination Modes.
 */
enum class BcsPracticeMode(
    val id: String,
    val titleBn: String,
    val subtitleBn: String,
    val defaultQuestionCount: Int,
    val durationMinutes: Int,
    val negativeMarking: Double
) {
    TOPIC_PRACTICE("TOPIC_PRACTICE", "১. টপিকভিত্তিক অনুশীলন", "নির্দিষ্ট সিলেবাস টপিকের ধারণা ও প্রশ্ন চর্চা", 10, 12, 0.0),
    DIFFICULTY_PRACTICE("DIFFICULTY_PRACTICE", "২. কঠিনতার স্তরভিত্তিক অনুশীলন", "সহজ, মধ্যম ও কঠিন স্তরের ধাপে ধাপে চর্চা", 15, 18, 0.25),
    PREVIOUS_QUESTION_PRACTICE("PREVIOUS_QUESTION_PRACTICE", "৩. বিগত বিসিএস প্রশ্ন অনুশীলন", "১০ম–৪৬তম বিসিএসের যাচাইকৃত ঐতিহাসিক প্রশ্ন আর্কাইভ (১০ম–৩৪তম প্রাক-৩৫ কাঠামো ও ৩৫তম–৪৬তম প্রিলিমিনারি)", 20, 20, 0.50),
    MISTAKE_PRACTICE("MISTAKE_PRACTICE", "৪. ভুল প্রশ্ন পুনঃঅনুশীলন", "মিসটেক নোটবুকের অমীমাংসিত প্রশ্নগুলোর লক্ষ্যভিত্তিক চর্চা", 10, 12, 0.0),
    WEAK_TOPIC_PRACTICE("WEAK_TOPIC_PRACTICE", "৫. দুর্বল টপিক অনুশীলন", "কম নির্ভুলতা ও ধীর গতির টপিকগুলোর বিশেষ অনুশীলন", 12, 15, 0.25),
    SPEED_DRILL("SPEED_DRILL", "৬. স্পিড ড্রিল (দ্রুত সমাধান)", "প্রতি প্রশ্নে ৪৫ সেকেন্ডে শর্টকাট প্রয়োগের চ্যালেঞ্জ", 15, 11, 0.25),
    TIMED_TOPIC_TEST("TIMED_TOPIC_TEST", "৭. সময়ভিত্তিক টপিক টেস্ট", "নির্দিষ্ট ইউনিটের ওপর নেগেটিভ মার্কিংসহ টাইমড টেস্ট", 15, 15, 0.50),
    SUBJECT_WISE_MOCK_MATH("SUBJECT_WISE_MOCK_MATH", "৮. বিষয়ভিত্তিক মক: গাণিতিক যুক্তি", "৫০তম প্রিলিমিনারি মানদণ্ডে ২০ নম্বরের গণিত মক", 20, 20, 0.50),
    SUBJECT_WISE_MOCK_MENTAL("SUBJECT_WISE_MOCK_MENTAL", "৯. বিষয়ভিত্তিক মক: মানসিক দক্ষতা", "৫০তম প্রিলিমিনারি মানদণ্ডে ১৫ নম্বরের মানসিক দক্ষতা মক", 15, 15, 0.50),
    COMBINED_MOCK("COMBINED_MOCK", "১০. সমন্বিত বিসিএস মক টেস্ট", "গাণিতিক যুক্তি ও মানসিক দক্ষতার সমন্বিত মডেল টেস্ট", 30, 30, 0.50),
    BCS_50TH_PRELIM_35_MOCK("BCS_50TH_PRELIM_35_MOCK", "১১. ৫০তম প্রিলিমিনারি ৩৫ নম্বরের মক", "২০ গণিত + ১৫ মানসিক দক্ষতা = ৩৫ নম্বরের অফিশিয়াল মক", 35, 35, 0.50),
    BCS_WRITTEN_008_PRACTICE("BCS_WRITTEN_008_PRACTICE", "১২. লিখিত কোড ০০৮ (৫০ নম্বর) অনুশীলন", "বিসিএস লিখিত গাণিতিক যুক্তির ধাপভিত্তিক রুব্রিক অনুশীলন (ডিফল্ট ১০ প্রশ্ন, কাস্টমাইজযোগ্য)", 10, 60, 0.0),
    BCS_WRITTEN_009_PRACTICE("BCS_WRITTEN_009_PRACTICE", "১৩. লিখিত কোড ০০৯ (৫০ নম্বর) অনুশীলন", "বিসিএস লিখিত মানসিক দক্ষতার ৫০ নম্বরের অনুশীলন (ডিফল্ট ২৫ প্রশ্ন, কাস্টমাইজযোগ্য)", 25, 30, 0.0),
    BCS_51ST_SPECIAL_20_MOCK("BCS_51ST_SPECIAL_20_MOCK", "১৪. ৫১তম বিশেষ বিসিএস ২০ নম্বরের মক", "১০ গণিত + ১০ মানসিক দক্ষতা = ২০ নম্বরের স্বাস্থ্য ক্যাডার স্ক্রিনিং মক", 20, 20, 0.50);
}

/**
 * Authority Badge distinguishing Official BPSC Syllabus topics from NCTB Foundation Bridges and Complementary topics.
 */
enum class CurriculumAuthorityBadge(val labelBn: String) {
    OFFICIAL_BPSC_PRELIM_SYLLABUS("অফিশিয়াল ৫০তম প্রিলিমিনারি সিলেবাস"),
    OFFICIAL_BPSC_WRITTEN_008("অফিশিয়াল লিখিত সিলেবাস (কোড ০০৮)"),
    OFFICIAL_BPSC_WRITTEN_009("অফিশিয়াল লিখিত সিলেবাস (কোড ০০৯)"),
    NCTB_FOUNDATION_BRIDGE("এনসিটিবি পাঠ্যবই ভিত্তি সংযোগ"),
    COMPLEMENTARY_ADVANCED_PRACTICE("পরিপূরক উচ্চতর অনুশীলন")
}

/**
 * Concept Guide for each of the 6 Official BPSC Mental Ability Categories (plus Written Code 009 Abstract Reasoning).
 */
data class BcsMentalAbilityCategoryGuide(
    val category: BcsMentalAbilityCategory,
    val corePrinciplesBn: List<String>,
    val solvingRulesBn: List<String>,
    val commonTrapsBn: List<String>,
    val writtenCode009NotesBn: String
)

/**
 * 8 Progressive Preparation Stages in the Aspirant's Journey.
 */
enum class BcsPreparationStage(
    val stageNumber: Int,
    val titleBn: String,
    val subtitleBn: String,
    val badgeBn: String
) {
    FOUNDATION_BUILDING(1, "ভিত্তি গঠন", "মৌলিক সূত্র, সংখ্যা ও মূল পাঠ্যবইয়ের ধারণা", "ধাপ ১"),
    CONCEPT_MASTERY(2, "ধারণা আয়ত্তকরণ", "পাটিগণিত ও বীজগণিতের নিয়ম ও বৈশিষ্ট্য", "ধাপ ২"),
    STANDARD_PRACTICE(3, "মানসম্মত অনুশীলন", "টপিকভিত্তিক স্ট্যান্ডার্ড MCQ ও সমস্যা সমাধান", "ধাপ ৩"),
    ADVANCED_PRACTICE(4, "উন্নত অনুশীলন", "জটিল পাটিগণিত, জ্যামিতি ও মানসিক দক্ষতা", "ধাপ ৪"),
    SPEED_ACCURACY(5, "গতি ও নির্ভুলতা", "শর্টকাট কৌশল প্রয়োগ ও দ্রুত হিসাব", "ধাপ ৫"),
    PREVIOUS_QUESTION_REVISION(6, "বিগত প্রশ্ন রিভিশন", "১০ম–৪৬তম বিসিএস প্রশ্নপত্রের নিখুঁত চর্চা", "ধাপ ৬"),
    MOCK_EXAMINATION(7, "মক পরীক্ষা", "টাইমার ও নেগেটিভ মার্কিংযুক্ত বিষয়ভিত্তিক পরীক্ষা", "ধাপ ৭"),
    FINAL_REVISION(8, "চূড়ান্ত রিভিশন", "ভুল সংশোধন নোটবুক ও দুর্বল টপিকের স্পেসড রিভিশন", "ধাপ ৮");

    companion object {
        fun fromNumber(num: Int): BcsPreparationStage {
            return entries.firstOrNull { it.stageNumber == num } ?: FOUNDATION_BUILDING
        }
    }
}

/**
 * 6 Official BPSC Mental Ability Categories (মানসিক দক্ষতা).
 */
enum class BcsMentalAbilityCategory(
    val id: String,
    val categoryNumber: Int,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String
) {
    VERBAL_REASONING(
        id = "VERBAL_REASONING",
        categoryNumber = 1,
        titleBn = "ভাষাগত যৌক্তিক বিচার",
        titleEn = "Verbal Reasoning",
        descriptionBn = "শব্দজোড়, সাদৃশ্য, বৈসাদৃশ্য, সম্পর্ক নির্ণয় এবং যুক্তিযুক্ত সিদ্ধান্ত গ্রহণ।"
    ),
    PROBLEM_SOLVING(
        id = "PROBLEM_SOLVING",
        categoryNumber = 2,
        titleBn = "সমস্যা সমাধান ও বিশ্লেষণ",
        titleEn = "Problem Solving",
        descriptionBn = "ঘড়ি ও ক্যালেন্ডার সংক্রান্ত হিসাব, বয়স ও ক্রমবিন্যাস, ধাঁধাঁ এবং জটিল তথ্যের যৌক্তিক সমাধান।"
    ),
    SPELLING_LANGUAGE(
        id = "SPELLING_LANGUAGE",
        categoryNumber = 3,
        titleBn = "বানান ও ভাষা দক্ষতা",
        titleEn = "Spelling and Language",
        descriptionBn = "শুদ্ধ বানান নির্ণয়, বর্ণমালা ও শব্দক্রম, বর্ণ সাজানো ও শব্দগঠন।"
    ),
    MECHANICAL_REASONING(
        id = "MECHANICAL_REASONING",
        categoryNumber = 4,
        titleBn = "যান্ত্রিক দক্ষতা ও বলবিজ্ঞান",
        titleEn = "Mechanical Reasoning",
        descriptionBn = "গিয়ার চাকা ও ঘূর্ণন, কপিকল ও পুলি সিস্টেম, লিভার, ভারসাম্য ও পানির প্রবাহ।"
    ),
    SPACE_RELATION(
        id = "SPACE_RELATION",
        categoryNumber = 5,
        titleBn = "স্থানাঙ্ক ও দিক নির্ণয়",
        titleEn = "Space Relation & Spatial Ability",
        descriptionBn = "দিক ও দূরত্ব নির্ণয়, জ্যামিতিক চিত্র গণনা (ত্রিভুজ, চতুর্ভুজ), দর্পণ ও পানির প্রতিবিম্ব এবং বিমূর্ত চিত্র প্যাটার্ন।"
    ),
    NUMERICAL_ABILITY(
        id = "NUMERICAL_ABILITY",
        categoryNumber = 6,
        titleBn = "সংখ্যাগত ক্ষমতা ও প্যাটার্ন",
        titleEn = "Numerical Ability & Series Pattern",
        descriptionBn = "সংখ্যার ধারা ও প্যাটার্ন, মিসিং নাম্বার, ম্যাজিক বর্গ ও কোডিং-ডিকোডিং।"
    );

    companion object {
        fun fromIdOrDefault(id: String?): BcsMentalAbilityCategory {
            return entries.firstOrNull { it.id == id } ?: VERBAL_REASONING
        }
    }
}

/**
 * Visual Diagram Types for Diagram-based Mental Ability questions.
 */
enum class BcsDiagramType {
    NONE,
    GEAR_ROTATION,
    PULLEY_SYSTEM,
    COMPASS_DIRECTION,
    TRIANGLE_COUNT,
    MIRROR_REFLECTION,
    NUMBER_MATRIX
}

/**
 * Strict Provenance Tier to guarantee authenticity of historical questions.
 */
enum class BcsProvenanceTier(val badgeBn: String) {
    VERIFIED_OFFICIAL_PAPER("যাচাইকৃত মূল বিসিএস প্রশ্ন"),
    RECONSTRUCTED_EVIDENCE("যাচাইকৃত প্রমাণসহ পুনর্গঠিত"),
    SECONDARY_ARCHIVE("সেকেন্ডারি প্রশ্ন আর্কাইভ"),
    CONCEPT_DERIVED_PRACTICE("সিলেবাস-ভিত্তিক কনসেপ্ট মডেল"),
    GENERATED_PRACTICE("অনুশীলন মডেল প্রশ্ন")
}

data class BcsMentalAbilityQuestion(
    val id: String,
    val category: BcsMentalAbilityCategory,
    val topicBn: String,
    val questionBn: String,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val explanationBn: String,
    val shortcutTipBn: String = "",
    val diagramType: BcsDiagramType = BcsDiagramType.NONE,
    val diagramData: String = "",
    val examReferenceBn: String = "বিসিএস মানসিক দক্ষতা মডেল",
    val provenanceTier: BcsProvenanceTier = BcsProvenanceTier.CONCEPT_DERIVED_PRACTICE,
    val bcsExamNumber: Int? = null,
    val examYear: Int? = null,
    val difficulty: PracticeDifficulty = PracticeDifficulty.MEDIUM
) {
    val correctAnswerBn: String
        get() = optionsBn.getOrElse(correctIndex) { optionsBn.firstOrNull().orEmpty() }
}
