package com.example.math

/**
 * Beginner-first Pedagogical Enricher for all 16 SolverTopics.
 * Supplies:
 * - Section 1 & 2: ProblemUnderstanding (Given, Required, Prerequisite Concept, Real-life Analogy)
 * - Section 3: FormulaBreakdown & Variable Dictionary
 * - Section 4: 3-Level Step Explanations, Common Mistakes & Progressive Hints (Hint 1 & Hint 2)
 * - Section 6: ShortcutDetails (Shortcut + Why It Works + When NOT to Use + Common Trap)
 * - Official BPSC Syllabus Mapping (Preliminary 15+15 marks, Written 50+50 marks, 5-Category Provenance)
 * - Inline VisualModelSpec with screen-reader accessibility descriptions
 */
object SolverPedagogyEnricher {

    fun enrich(result: SolverResult): SolverResult {
        if (!result.isSuccess) return result

        val topic = result.topic
        val enrichedSteps = result.steps.mapIndexed { idx, step ->
            val defaultRule = step.whyThisStepBn ?: getDefaultWhyRuleForTopic(topic, idx)
            val defaultAnalogy = step.conceptualAnalogyBn ?: getConceptualAnalogyForTopic(topic)
            val defaultMistake = step.commonMistakeBn ?: getCommonMistakeForTopic(topic)
            val defaultHint1 = step.hint1Bn ?: "ইঙ্গিত ১: '${step.titleBn}' ধাপে কোন সূত্র বা নিয়মটি প্রযোজ্য তা চিন্তা করুন।"
            val defaultHint2 = step.hint2Bn ?: "ইঙ্গিত ২: ${step.explanationBn}"
            val expectedVal = step.expectedStepValueBn ?: extractExpectedValueFromMath(step.expressionOrMathBn, result.finalAnswerBn)

            step.copy(
                whyThisStepBn = defaultRule,
                simpleExplanationBn = step.explanationBn,
                detailedRuleBn = defaultRule,
                conceptualAnalogyBn = defaultAnalogy,
                commonMistakeBn = defaultMistake,
                hint1Bn = defaultHint1,
                hint2Bn = defaultHint2,
                expectedStepValueBn = expectedVal
            )
        }

        val understanding = result.problemUnderstanding ?: buildProblemUnderstanding(result)
        val formulaBreakdown = result.formulaBreakdown ?: buildFormulaBreakdown(result)
        val shortcutDetails = result.shortcutDetails ?: buildShortcutDetails(topic, result.bcsShortcutBn)
        val bcsMapping = result.bcsSyllabusMapping ?: buildBcsSyllabusMapping(topic)
        val visualSpec = result.visualModelSpec ?: buildDefaultVisualSpec(result)

        return result.copy(
            steps = enrichedSteps,
            problemUnderstanding = understanding,
            formulaBreakdown = formulaBreakdown,
            shortcutDetails = shortcutDetails,
            bcsSyllabusMapping = bcsMapping,
            visualModelSpec = visualSpec,
            bcsShortcutBn = result.bcsShortcutBn ?: shortcutDetails.shortcutFormulaOrRuleBn,
            bcsExamTipBn = result.bcsExamTipBn ?: shortcutDetails.commonTrapBn
        )
    }

    private fun extractExpectedValueFromMath(mathText: String, fallbackFinal: String): String {
        val afterArrow = mathText.substringAfterLast("➜", "").trim().removeSuffix("]")
        if (afterArrow.isNotEmpty() && afterArrow.length <= 24) return afterArrow
        val afterEquals = mathText.substringAfterLast("=", "").trim()
        if (afterEquals.isNotEmpty() && afterEquals.length <= 24) return afterEquals
        return fallbackFinal
    }

    private fun buildProblemUnderstanding(res: SolverResult): ProblemUnderstanding {
        return when (res.topic) {
            SolverTopic.BASIC_ARITHMETIC -> ProblemUnderstanding(
                summaryBn = "এই অংকে একাধিক গাণিতিক চিহ্ন ও বন্ধনী রয়েছে। কোন কাজটি আগে এবং কোনটি পরে করতে হবে তা BODMAS ক্রম অনুসারে ধাপে ধাপে নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত রাশি" to res.originalProblemBn.removePrefix("সরল করুন:").trim()),
                requiredTargetBn = "সকল বন্ধনী ও চিহ্নের সঠিক অগ্রাধিকার মেনে সরলকৃত একক মান নির্ণয়",
                prerequisiteConceptBn = "BODMAS ক্রম: ১. বন্ধনী (), {}, [] ➜ ২. সূচক/বর্গমূল/শতকরা ➜ ৩. এর (Of) ➜ ৪. ভাগ (÷) ও গুণ (×) [বাম থেকে ডানে] ➜ ৫. যোগ (+) ও বিয়োগ (−) [বাম থেকে ডানে]",
                realLifeContextBn = "বাজারের হিসাব বা ব্যাংকের কিস্তি গণনায় আগে গুণ-ভাগের কাজ শেষ করে পরে মোট যোগ-বিয়োগ করতে হয়।"
            )
            SolverTopic.FRACTIONS_DECIMALS -> ProblemUnderstanding(
                summaryBn = "দুটি ভগ্নাংশের মধ্যে গাণিতিক প্রক্রিয়া সম্পন্ন করে ফলাফলকে লঘিষ্ঠ সাধারণ ভগ্নাংশ, মিশ্র ভগ্নাংশ ও দশমিকে প্রকাশ করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত ভগ্নাংশ সমস্যা" to res.originalProblemBn.removePrefix("নির্ণয় করুন:").trim()),
                requiredTargetBn = "লঘিষ্ঠ ভগ্নাংশ (লব/হর), মিশ্র ভগ্নাংশ ও দশমিক মান",
                prerequisiteConceptBn = "ভগ্নাংশের যোগ-বিয়োগে হরগুলোর ল.সা.গু করে সমহর করতে হয়; গুণনে লবে-লবে ও হরে-হরে গুণ হয়; ভাগে দ্বিতীয় ভগ্নাংশ উল্টে গুণ হয়।",
                realLifeContextBn = "একটি রুটির ৩/৪ অংশ ও ২/৫ অংশ একত্রে কতটুকু হয় তা মাপতে দুটি ভগ্নাংশকে সমান আকারের টুকরায় (সমহর) ভাগ করে নিতে হয়।"
            )
            SolverTopic.PERCENTAGE -> ProblemUnderstanding(
                summaryBn = "শতকরা অর্থ প্রতি ১০০-তে কত। এখানে ভিত্তি মান (Base Value) ১০০ ধরে আনুপাতিক পরিবর্তন বা মান নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত তথ্য" to res.originalProblemBn),
                requiredTargetBn = "শতকরা মান বা শতকরা পরিবর্তনের হার (%)",
                prerequisiteConceptBn = "x% = x/১০০। শতকরা বৃদ্ধি বা হ্রাস সর্বদা প্রারম্ভিক (আদি) মানের সাপেক্ষে হিসাব করা হয়।",
                realLifeContextBn = "দোকানে ছাড়, পরীক্ষার নম্বর বা বেতন বৃদ্ধির হিসাব শতকরার মাধ্যমে প্রকাশ করা হয়।"
            )
            SolverTopic.RATIO_PROPORTION -> ProblemUnderstanding(
                summaryBn = "দুটি বা ততোধিক সমজাতীয় রাশির তুলনা বা আনুপাতিক বণ্টন নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত অনুপাত সমস্যা" to res.originalProblemBn),
                requiredTargetBn = "আনুপাতিক অংশ বা অজ্ঞাত সমানুপাতী রাশি",
                prerequisiteConceptBn = "অনুপাতের কোনো একক নেই। আনুপাতিক ভাগের সূত্র = মোট পরিমাণ × (নির্দিষ্ট অনুপাত ÷ অনুপাতগুলোর যোগফল)।",
                realLifeContextBn = "ব্যবসায় অংশীদারদের মূলধনের অনুপাতে মুনাফা বণ্টন।"
            )
            SolverTopic.PROFIT_LOSS -> ProblemUnderstanding(
                summaryBn = "পণ্যের ক্রয়মূল্য (CP), বিক্রয়মূল্য (SP) অথবা ধার্যমূল্য (MP) থেকে লাভ/ক্ষতি ও শতকরা হার নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত লেনদেন" to res.originalProblemBn),
                requiredTargetBn = "লাভ বা ক্ষতির পরিমাণ এবং শতকরা হার",
                prerequisiteConceptBn = "বিক্রয়মূল্য > ক্রয়মূল্য হলে লাভ; ক্রয়মূল্য > বিক্রয়মূল্য হলে ক্ষতি। শতকরা লাভ/ক্ষতি সর্বদা ক্রয়মূল্যের (CP) ওপর গণনা করা হয়, তবে ছাড় (Discount) ধার্যমূল্যের (MP) ওপর গণনা করা হয়।",
                realLifeContextBn = "দোকানদারের কেনা দাম ও বিক্রির দামের তুলনা।"
            )
            SolverTopic.AVERAGE -> ProblemUnderstanding(
                summaryBn = "একাধিক সংখ্যার সমষ্টিকে মোট পদসংখ্যা দিয়ে ভাগ করে তাদের প্রতিনিধিত্বকারী মধ্যমান (গড়) বের করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত সংখ্যা তালিকা" to res.originalProblemBn),
                requiredTargetBn = "সংখ্যাগুলোর গাণিতিক গড় (Arithmetic Mean)",
                prerequisiteConceptBn = "গড় = রাশিগুলোর যোগফল ÷ রাশিগুলোর সংখ্যা; অতএব যোগফল = গড় × পদসংখ্যা।",
                realLifeContextBn = "ক্রিকেটে একজন ব্যাটসম্যানের প্রতি ম্যাচের গড় রান বা পরিবারের গড় মাসিক খরচ।"
            )
            SolverTopic.SIMPLE_INTEREST -> ProblemUnderstanding(
                summaryBn = "নির্দিষ্ট আসল (P), বার্ষিক সুদের হার (r%) এবং সময় (t বছর) থেকে সরল বা চক্রবৃদ্ধি মুনাফা ও সবৃদ্ধিমূল নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত আর্থিক তথ্য" to res.originalProblemBn),
                requiredTargetBn = "মুনাফা (I বা CI) এবং মুনাফা-আসল বা সবৃদ্ধিমূল (A)",
                prerequisiteConceptBn = "সরল মুনাফায় কেবল আসলের ওপর মুনাফা হয় (I = Prt/১০০)। চক্রবৃদ্ধি মুনাফায় প্রতি পর্বের মুনাফা আসলের সাথে যুক্ত হয়ে নতুন আসল তৈরি করে (A = P(১ + r/১০০m)^mt, CI = A − P)।",
                realLifeContextBn = "ব্যাংকে সঞ্চয়পত্র বা মেয়াদী আমানতের (FDR) মুনাফা হিসাব।"
            )
            SolverTopic.TIME_AND_WORK -> ProblemUnderstanding(
                summaryBn = "একাধিক ব্যক্তি বা নলের পৃথক কাজের সময় থেকে তাদের দৈনিক বা ঘণ্টাপ্রতি কাজের হার (১/সময়) বের করে একত্রে কাজের সময় নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত কাজের তথ্য" to res.originalProblemBn),
                requiredTargetBn = "একত্রে সম্পূর্ণ কাজ (১ অংশ) শেষ করতে প্রয়োজনীয় মোট সময়",
                prerequisiteConceptBn = "কোনো ব্যক্তি x দিনে পুরো কাজ করলে ১ দিনে করে ১/x অংশ। কাজের সময় সরাসরি যোগ করা যায় না; ১ দিনের কাজের ভগ্নাংশ যোগ করতে হয়।",
                realLifeContextBn = "দুইজন রাজমিস্ত্রি একসাথে কাজ করলে দেয়াল তৈরির সময় কমে যায়।"
            )
            SolverTopic.SPEED_DISTANCE_TIME -> ProblemUnderstanding(
                summaryBn = "গতিবেগ (S), দূরত্ব (D) ও সময় (T)-এর পারস্পরিক সম্পর্ক এবং একক রূপান্তর (km/h ↔ m/s) প্রয়োগ করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত গতির তথ্য" to res.originalProblemBn),
                requiredTargetBn = "অজ্ঞাত গতিবেগ, দূরত্ব বা সময় (ঘণ্টা-মিনিট-সেকেন্ডে)",
                prerequisiteConceptBn = "দূরত্ব D = গতিবেগ S × সময় T। ১ km/h = ৫/১৮ m/s এবং ১ m/s = ১৮/৫ km/h।",
                realLifeContextBn = "ট্রেন বা বাসে ভ্রমণের সময় নির্ধারণ।"
            )
            SolverTopic.NUMBER_SYSTEM_DIVISIBILITY -> ProblemUnderstanding(
                summaryBn = "পূর্ণসংখ্যাটিকে সরাসরি দীর্ঘ ভাগ না করেও অঙ্কগুলোর বৈশিষ্ট্য থেকে ভাজক দ্বারা নিঃশেষে বিভাজ্য কিনা এবং ভাগশেষ কত তা নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত সংখ্যা ও ভাজক" to res.originalProblemBn),
                requiredTargetBn = "বিভাজ্যতা যাচাই, ভাগফল ও ভাগশেষ",
                prerequisiteConceptBn = "ইউক্লিডের ভাগ প্রক্রিয়া: ভাজ্য = ভাজক × ভাগফল + ভাগশেষ (যেখানে ০ ≤ ভাগশেষ < ভাজক)।",
                realLifeContextBn = "সমান সংখ্যক দলে মানুষ বা বস্তু নিঃশেষে ভাগ করা যাবে কিনা তা দ্রুত যাচাই।"
            )
            SolverTopic.HCF_LCM -> ProblemUnderstanding(
                summaryBn = "প্রদত্ত সংখ্যাগুলোর গরিষ্ঠ সাধারণ গুণনীয়ক (গ.সা.গু) এবং লঘিষ্ঠ সাধারণ গুণিতক (ল.সা.গু) নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত সংখ্যাসমূহ" to res.originalProblemBn),
                requiredTargetBn = "গ.সা.গু (HCF/GCD) এবং ল.সা.গু (LCM)",
                prerequisiteConceptBn = "গ.সা.গু হলো সবচেয়ে বড় সংখ্যা যা দিয়ে প্রদত্ত সব সংখ্যাকে ভাগ করা যায়; ল.সা.গু হলো সবচেয়ে ছোট সংখ্যা যা প্রদত্ত সব সংখ্যা দ্বারা বিভাজ্য।",
                realLifeContextBn = "ঘণ্টা বাজার সময়ের ব্যবধান (ল.সা.গু) বা সর্বাধিক আকারের বর্গাকার টাইলস (গ.সা.গু) নির্ধারণ।"
            )
            SolverTopic.SQUARES_CUBES_ROOTS -> ProblemUnderstanding(
                summaryBn = "প্রদত্ত সংখ্যার বর্গ, বর্গমূল, ঘন অথবা ঘনমূল নির্ণয় করতে হবে এবং সংখ্যাটি পূর্ণবর্গ/পূর্ণঘন কিনা যাচাই করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত সংখ্যা ও প্রক্রিয়া" to res.originalProblemBn),
                requiredTargetBn = "বর্গ (N²), বর্গমূল (√N), ঘন (N³) বা ঘনমূল (³√N)",
                prerequisiteConceptBn = "মৌলিক উৎপাদকে বিশ্লেষণ করে বর্গমূলের জন্য প্রতি জোড়া (২টি) থেকে ১টি এবং ঘনমূলের জন্য প্রতি ত্রয়ী (৩টি) থেকে ১টি উৎপাদক নিতে হয়।",
                realLifeContextBn = "বর্গাকার জমির ক্ষেত্রফল থেকে এক বাহুর দৈর্ঘ্য (বর্গমূল) বা ঘনকের আয়তন থেকে বাহু (ঘনমূল) নির্ণয়।"
            )
            SolverTopic.LINEAR_EQUATIONS -> ProblemUnderstanding(
                summaryBn = "সমীকরণ বা অসমতার বামপক্ষ ও ডানপক্ষের ভারসাম্য রক্ষা করে অজ্ঞাত চলক (x)-এর মান বা ব্যবধি নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত সমীকরণ/অসমতা" to res.originalProblemBn),
                requiredTargetBn = "চলক x এর মান বা সমাধান সেট",
                prerequisiteConceptBn = "পক্ষান্তর বিধি: সমান চিহ্নের এক পাশ থেকে অন্য পাশে গেলে + হয় − এবং × হয় ÷। অসমতায় ঋণাত্মক সংখ্যা দিয়ে ভাগ করলে অসমতার চিহ্ন উল্টে যায়।",
                realLifeContextBn = "দাঁড়িপাল্লার দুই পাল্লা সমান রেখে অজানা বাটখারার ওজন বের করা।"
            )
            SolverTopic.QUADRATIC_EQUATIONS -> ProblemUnderstanding(
                summaryBn = "দ্বিঘাত সমীকরণ বা অসমতায় নিশ্চায়ক (Δ = b² − 4ac) বিশ্লেষণ করে মূলের প্রকৃতি, মূলদ্বয় ও শীর্ষবিন্দু নির্ণয় করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত দ্বিঘাত রাশি" to res.originalProblemBn),
                requiredTargetBn = "নিশ্চায়ক Δ, মূলের প্রকৃতি এবং মূলদ্বয় (x₁, x₂) বা ব্যবধি",
                prerequisiteConceptBn = "ax² + bx + c = 0 সমীকরণে a ≠ 0। Δ > 0 হলে দুটি ভিন্ন বাস্তব মূল, Δ = 0 হলে দুটি সমান বাস্তব মূল, এবং Δ < 0 হলে অনুবন্ধী জটিল মূল থাকে।",
                realLifeContextBn = "নিক্ষিপ্ত বস্তুর গতিপথ (প্যারাবোলা) এবং সর্বোচ্চ উচ্চতা নির্ণয়।"
            )
            SolverTopic.FACTORIZATION -> ProblemUnderstanding(
                summaryBn = "প্রদত্ত সংখ্যা বা বীজগণিতীয় বহুপদী রাশিকে ক্ষুদ্রতম মৌলিক বা রৈখিক উৎপাদকের গুণফল আকারে প্রকাশ করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত রাশি" to res.originalProblemBn),
                requiredTargetBn = "উৎপাদকে বিশ্লেষিত রূপ",
                prerequisiteConceptBn = "মিডল-টার্ম ব্রেকআপে মধ্যপদ b কে এমন দুটি অংশে (p ও q) ভাঙতে হয় যেন p + q = b এবং p × q = a × c হয়।",
                realLifeContextBn = "আয়তক্ষেত্রের ক্ষেত্রফল থেকে তার দৈর্ঘ্য ও প্রস্থের রাশি দুটি আলাদা করা।"
            )
            SolverTopic.SIMPLIFICATION -> ProblemUnderstanding(
                summaryBn = "বর্গমূল করণী (√n), ভগ্নাংশ অথবা সদৃশ বীজগণিতীয় পদগুলোকে কাটাকাটি ও একত্রীকরণের মাধ্যমে সরলতম আকারে প্রকাশ করতে হবে।",
                givenItemsBn = listOf("প্রদত্ত রাশি" to res.originalProblemBn),
                requiredTargetBn = "সরলতম নিখুঁত রূপ (Exact Form) ও আসন্ন দশমিক মান",
                prerequisiteConceptBn = "করণী সরলীকরণে ভেতরের সংখ্যাটিকে বৃহত্তম পূর্ণবর্গ সংখ্যার গুণিতক আকারে ভেঙে বর্গমূল চিহ্নের বাইরে আনতে হয়: √(k²m) = k√m।",
                realLifeContextBn = "জটিল পরিমাপকে সহজ ও প্রমিত এককে প্রকাশ।"
            )
        }
    }

    private fun buildFormulaBreakdown(res: SolverResult): FormulaBreakdown {
        val rawFormula = res.formulaUsedBn ?: "প্রমিত গাণিতিক সূত্র"
        return when (res.topic) {
            SolverTopic.BASIC_ARITHMETIC -> FormulaBreakdown(
                formulaTitleBn = "BODMAS অগ্রাধিকার বিধি",
                formulaExpressionBn = rawFormula,
                plainMeaningBn = "বন্ধনীর কাজ সবার আগে, তারপর ঘাত/বর্গমূল, তারপর 'এর', তারপর বাম থেকে ডানে ভাগ ও গুণ, এবং সবশেষে যোগ ও বিয়োগ।",
                variables = listOf(
                    VariableExplanation("B", "Brackets — বন্ধনী (), {}, [] (ভেতরেরটি আগে)"),
                    VariableExplanation("O", "Orders / Of — ঘাত (xⁿ), বর্গমূল (√), শতকরা (%) এবং 'এর'"),
                    VariableExplanation("D & M", "Division (÷) ও Multiplication (×) — সমান অগ্রাধিকার, বাম থেকে ডানে"),
                    VariableExplanation("A & S", "Addition (+) ও Subtraction (−) — সমান অগ্রাধিকার, বাম থেকে ডানে")
                ),
                whyApplicableBn = "একাধিক প্রক্রিয়া চিহ্ন থাকলে সর্বজনীনভাবে একই উত্তর পেতে BODMAS ক্রম অপরিহার্য।"
            )
            SolverTopic.SIMPLE_INTEREST -> FormulaBreakdown(
                formulaTitleBn = "সরল ও চক্রবৃদ্ধি মুনাফার সূত্র",
                formulaExpressionBn = rawFormula,
                plainMeaningBn = "আসল, হার ও সময়ের গুণফলকে ১০০ দ্বারা ভাগ করলে সরল মুনাফা পাওয়া যায়; আর চক্রবৃদ্ধিতে প্রতি পর্বে আসল বৃদ্ধি পায়।",
                variables = listOf(
                    VariableExplanation("P", "Principal — প্রারম্ভিক আসল বা মূলধন (টাকা)"),
                    VariableExplanation("r", "Rate — বার্ষিক শতকরা মুনাফার হার (%)"),
                    VariableExplanation("t / n", "Time — সময় বা মেয়াদ (বছর)"),
                    VariableExplanation("m", "Compounding Frequency — বছরে চক্রবৃদ্ধি পর্ব সংখ্যা (বার্ষিক = ১, অর্ধবার্ষিক = ২)"),
                    VariableExplanation("I / CI", "Interest — অর্জিত সরল বা চক্রবৃদ্ধি মুনাফা"),
                    VariableExplanation("A", "Amount — মুনাফা-আসল বা সবৃদ্ধিমূল (P + মুনাফা)")
                ),
                whyApplicableBn = "আর্থিক লেনদেনে সময় ও হারের সাপেক্ষে মুনাফা ও সবৃদ্ধিমূল নির্ণয়ে ব্যবহৃত হয়।"
            )
            SolverTopic.LINEAR_EQUATIONS -> FormulaBreakdown(
                formulaTitleBn = "রৈখিক সমীকরণ ও পরমমান অসমতা বিধি",
                formulaExpressionBn = rawFormula,
                plainMeaningBn = "চলক পদগুলোকে এক পাশে ও সংখ্যাগুলোকে অন্য পাশে নিয়ে সহগ দ্বারা ভাগ করলে চলকের মান পাওয়া যায়।",
                variables = listOf(
                    VariableExplanation("x", "অজ্ঞাত চলক (Unknown Variable) যার মান নির্ণয় করতে হবে"),
                    VariableExplanation("a, c", "চলক x এর সহগ (Coefficients)"),
                    VariableExplanation("b, d", "ধ্রুবক পদ (Constants)"),
                    VariableExplanation("|u|", "পরমমান (Absolute Value) যা সংখ্যারেখায় শূন্য থেকে দূরত্ব নির্দেশ করে (|u| ≥ ০)")
                ),
                whyApplicableBn = "এক চলকবিশিষ্ট সমীকরণ ও অসমতার সমাধান সেট নির্ণয়ে প্রযোজ্য।"
            )
            SolverTopic.QUADRATIC_EQUATIONS -> FormulaBreakdown(
                formulaTitleBn = "দ্বিঘাত সমীকরণ ও নিশ্চায়ক সূত্র",
                formulaExpressionBn = rawFormula,
                plainMeaningBn = "ax² + bx + c = 0 সমীকরণের মূলদ্বয় সরাসরি সহগ a, b, c থেকে নির্ণয়ের সর্বজনীন সূত্র।",
                variables = listOf(
                    VariableExplanation("a", "x² এর সহগ (প্রধান সহগ, a ≠ ০)"),
                    VariableExplanation("b", "x এর সহগ"),
                    VariableExplanation("c", "ধ্রুবক পদ"),
                    VariableExplanation("Δ (D)", "নিশ্চায়ক = b² − 4ac (মূলের প্রকৃতি নির্ধারক)")
                ),
                whyApplicableBn = "যেসব দ্বিঘাত রাশি সহজে উৎপাদকে বিশ্লেষণ করা যায় না, সেগুলোর মূল ও অসমতা নির্ণয়েও এটি শতভাগ কার্যকর।"
            )
            else -> FormulaBreakdown(
                formulaTitleBn = "${res.topic.titleBn} এর মূল সূত্র",
                formulaExpressionBn = rawFormula,
                plainMeaningBn = "প্রদত্ত মানগুলোকে গাণিতিক সম্পর্কে প্রতিস্থাপন করে ধাপে ধাপে অজ্ঞাত রাশির মান নির্ণয়।",
                variables = listOf(
                    VariableExplanation("প্রদত্ত মান", res.originalProblemBn),
                    VariableExplanation("নির্ণেয় উত্তর", res.finalAnswerBn)
                ),
                whyApplicableBn = "সমস্যাটির প্রদত্ত উপাত্ত ও নির্ণেয় লক্ষ্যের সাথে এই সূত্রটি সরাসরি সম্পর্কযুক্ত।"
            )
        }
    }

    private fun buildShortcutDetails(topic: SolverTopic, existingShortcut: String?): ShortcutDetails {
        return when (topic) {
            SolverTopic.BASIC_ARITHMETIC -> ShortcutDetails(
                shortcutTitleBn = "BODMAS দ্রুত স্তর বিভাজন কৌশল",
                shortcutFormulaOrRuleBn = existingShortcut ?: "বন্ধনী ➜ ঘাত/√/% ➜ এর ➜ (÷ ও × বাম থেকে ডানে) ➜ (+ ও − বাম থেকে ডানে)",
                whyShortcutWorksBn = "গুণ বা ভাগের আগে যোগ-বিয়োগ না করে প্রতিটি বন্ধনী ও গুণ/ভাগ ব্লককে আলাদা একক সংখ্যা হিসেবে কল্পনা করলে মানসিক হিসাব দ্রুত হয়।",
                whenToUseBn = "বিসিএস প্রিলিমিনারির ভগ্নাংশ, 'এর' ও বন্ধনীযুক্ত সরল অংকে।",
                whenToAvoidBn = "যখন ঋণাত্মক ঘাত বা নেস্টেড বন্ধনীর বাইরে বিয়োগ চিহ্ন থাকে—তখন তাড়াহুড়ো না করে চিহ্ন লিখে করুন।",
                commonTrapBn = "'১৬ ÷ ২ এর ৪' অংকে আগে ১৬ ÷ ২ = ৮ করে ৮ × ৪ = ৩২ করলে ভুল হবে! আগে '২ এর ৪ = ৮' করে ১৬ ÷ ৮ = ২ করতে হবে।"
            )
            SolverTopic.PERCENTAGE -> ShortcutDetails(
                shortcutTitleBn = "পারস্পরিক শতকরা বিনিময় ও ক্রমাগত পরিবর্তন সূত্র",
                shortcutFormulaOrRuleBn = existingShortcut ?: "x% এর y = y% এর x এবং ক্রমাগত পরিবর্তন = A + B + (AB/১০০)%",
                whyShortcutWorksBn = "(x/১০০) × y = (xy)/১০০ = (y/১০০) × x (গুণের বিনিময় বিধি) এবং (১ + A/১০০)(১ + B/১০০) = ১ + (A + B + AB/১০০)/১০০।",
                whenToUseBn = "আয়তক্ষেত্রের বাহু বা পণ্যের দাম পরপর দুইবার শতকরা বৃদ্ধি/হ্রাস পেলে।",
                whenToAvoidBn = "যখন শতকরা পরিবর্তন মূল মানের ওপর না হয়ে ভিন্ন ভিন্ন ভিত্তির ওপর প্রযোজ্য হয়।",
                commonTrapBn = "দাম ২০% বাড়ার পর আবার ২০% কমলে নিট পরিবর্তন ০% নয়—বরং ২০ − ২০ − (৪০০/১০০) = ৪% হ্রাস পায়!"
            )
            SolverTopic.PROFIT_LOSS -> ShortcutDetails(
                shortcutTitleBn = "১০০ ভিত্তি ও ভগ্নাংশ গুণক কৌশল",
                shortcutFormulaOrRuleBn = existingShortcut ?: "SP = CP × (১০০ ± r)/১০০  এবং  CP = SP × ১০০/(১০০ ± r)",
                whyShortcutWorksBn = "ক্রয়মূল্যকে ১০০% ধরলে r% লাভে বিক্রয়মূল্য হয় (১০০ + r)% এবং ক্ষতিতে (১০০ − r)%।",
                whenToUseBn = "বিক্রয়মূল্য ও লাভ/ক্ষতির হার থেকে দ্রুত ক্রয়মূল্য বের করতে।",
                whenToAvoidBn = "যখন প্রশ্নে বিশেষভাবে 'বিক্রয়মূল্যের ওপর লাভ' উল্লেখ থাকে—তখন হর হিসেবে বিক্রয়মূল্য ব্যবহৃত হয়।",
                commonTrapBn = "শতকরা লাভ বা ক্ষতি বের করার সময় ভুলবশত বিক্রয়মূল্য (SP) দিয়ে ভাগ করা—মনে রাখবেন সর্বদা ক্রয়মূল্য (CP) দিয়ে ভাগ করতে হয়।"
            )
            SolverTopic.SIMPLE_INTEREST -> ShortcutDetails(
                shortcutTitleBn = "সরল ও চক্রবৃদ্ধি মুনাফার ২-বছরের পার্থক্য সূত্র",
                shortcutFormulaOrRuleBn = existingShortcut ?: "২ বছরে (CI − SI) = P × (r/১০০)²  এবং  আসল n গুণ হলে r×t = (n − ১)×১০০",
                whyShortcutWorksBn = "P(১ + r/১০০)² − P − P(২r/১০০) = P(১ + ২r/১০০ + (r/১০০)² − ১ − ২r/১০০) = P(r/১০০)²।",
                whenToUseBn = "বার্ষিক চক্রবৃদ্ধিতে ২ বছরের সরল ও চক্রবৃদ্ধি মুনাফার পার্থক্য দেওয়া থাকলে আসল বা হার বের করতে।",
                whenToAvoidBn = "যখন সময় ২ বছর নয় অথবা অর্ধবার্ষিক/ত্রৈমাসিক পর্ব (m > ১) দেওয়া থাকে।",
                commonTrapBn = "চক্রবৃদ্ধি সবৃদ্ধিমূল A = P(১+r/১০০)ⁿ কে সরাসরি মুনাফা মনে করা—মুনাফা CI পেতে অবশ্যই A থেকে P বিয়োগ করতে হবে!"
            )
            SolverTopic.TIME_AND_WORK -> ShortcutDetails(
                shortcutTitleBn = "ল.সা.গু (LCM Total Work Unit) ও গুণফল/যোগফল কৌশল",
                shortcutFormulaOrRuleBn = existingShortcut ?: "দুইজনের একত্রে সময় = (A × B) ÷ (A + B); নল-চৌবাচ্চায় = (A × B) ÷ (B − A)",
                whyShortcutWorksBn = "১/A + ১/B = (A + B)/(AB) অংশ কাজ হয় ১ দিনে, তাই ১ অংশ কাজ হতে সময় লাগে এর বিপরীত ভগ্নাংশ (AB)/(A + B) দিন।",
                whenToUseBn = "ভগ্নাংশের যোগ এড়িয়ে পূর্ণসংখ্যায় দ্রুত সময় ও কাজের হিসাব করতে।",
                whenToAvoidBn = "চৌবাচ্চার ক্ষেত্রে যদি খালি করার নলের সময় পূর্ণ করার নলের চেয়ে কম বা সমান হয় (তখন চৌবাচ্চা কখনো পূর্ণ হবে না)।",
                commonTrapBn = "A ১০ দিনে ও B ১৫ দিনে কাজ করলে উত্তর ১০ + ১৫ = ২৫ দিন লেখা—একত্রে কাজ করলে সময় অবশ্যই একক সময়ের চেয়ে কম হবে!"
            )
            else -> ShortcutDetails(
                shortcutTitleBn = "${topic.titleBn} দ্রুত সমাধান কৌশল",
                shortcutFormulaOrRuleBn = existingShortcut ?: "প্রমিত সূত্র ও অপশন যাচাই (Back-Solving) পদ্ধতি",
                whyShortcutWorksBn = "বীজগণিতীয় অভেদ ও একক সমতা (Unit Consistency) ব্যবহার করে মধ্যবর্তী ধাপ সংক্ষেপণ।",
                whenToUseBn = "বিসিএস প্রিলিমিনারি ও ব্যাংক জব এমসিকিউ পরীক্ষায় সময় বাঁচাতে।",
                whenToAvoidBn = "বিসিএস লিখিত পরীক্ষায় (কোড 008)—লিখিত পরীক্ষায় শর্টকাট বর্জন করে পূর্ণাঙ্গ ধাপ প্রদর্শন করা বাধ্যতামূলক।",
                commonTrapBn = "চিহ্ন (+/−) ও একক (Unit) যাচাই না করে সরাসরি সংখ্যা বসানো।"
            )
        }
    }

    private fun buildBcsSyllabusMapping(topic: SolverTopic): BcsSyllabusMapping {
        return when (topic) {
            SolverTopic.BASIC_ARITHMETIC,
            SolverTopic.FRACTIONS_DECIMALS,
            SolverTopic.SIMPLIFICATION -> BcsSyllabusMapping(
                preliminarySectionBn = "গাণিতিক যুক্তি (১৫ নম্বর): বাস্তব সংখ্যা, ভগ্নাংশ ও সরলীকরণ + মানসিক দক্ষতা (১৫ নম্বর): সংখ্যাগত ক্ষমতা",
                writtenSectionBn = "গাণিতিক যুক্তি (কোড 008, ৫০ নম্বর): পাটিগণিত ও বীজগণিতীয় সরলীকরণ",
                provenanceCategory = BcsProvenanceCategory.OFFICIAL_BPSC_SYLLABUS,
                examPatternNoteBn = "প্রিলিমিনারিতে ১–২টি সরল বা করণী প্রশ্ন এবং মানসিক দক্ষতায় দ্রুত সংখ্যাগত হিসাব নিয়মিত আসে।"
            )
            SolverTopic.PERCENTAGE,
            SolverTopic.PROFIT_LOSS,
            SolverTopic.SIMPLE_INTEREST -> BcsSyllabusMapping(
                preliminarySectionBn = "গাণিতিক যুক্তি (১৫ নম্বর): শতকরা, সরল ও যৌগিক মুনাফা, লাভ-ক্ষতি (গড়ে ২–৩ নম্বর)",
                writtenSectionBn = "গাণিতিক যুক্তি (কোড 008, ৫০ নম্বর): শতকরা, সরল ও যৌগিক মুনাফা এবং লাভ-ক্ষতির কথার অংক",
                provenanceCategory = BcsProvenanceCategory.OFFICIAL_BPSC_SYLLABUS,
                examPatternNoteBn = "বিসিএস প্রিলিমিনারি ও লিখিত উভয় পরীক্ষায় এই তিনটি অধ্যায় থেকে প্রতি বছর আবশ্যিকভাবে প্রশ্ন থাকে।"
            )
            SolverTopic.RATIO_PROPORTION,
            SolverTopic.AVERAGE,
            SolverTopic.TIME_AND_WORK,
            SolverTopic.SPEED_DISTANCE_TIME -> BcsSyllabusMapping(
                preliminarySectionBn = "গাণিতিক যুক্তি (১৫ নম্বর): অনুপাত ও সমানুপাত, ঐকিক নিয়ম ও গড় + মানসিক দক্ষতা (১৫ নম্বর): দূরত্ব, সময় ও গতি",
                writtenSectionBn = "গাণিতিক যুক্তি (কোড 008, ৫০ নম্বর): অনুপাত-সমানুপাত, গড়, ঐকিক নিয়ম, সময়-কাজ ও গতিবেগ",
                provenanceCategory = BcsProvenanceCategory.OFFICIAL_BPSC_SYLLABUS,
                examPatternNoteBn = "লিখিত পরীক্ষায় (৫০ নম্বর) ঐকিক নিয়ম, কাজ-সময় ও অনুপাত থেকে বর্ণনামূলক সমস্যা আসে।"
            )
            SolverTopic.NUMBER_SYSTEM_DIVISIBILITY,
            SolverTopic.HCF_LCM,
            SolverTopic.SQUARES_CUBES_ROOTS -> BcsSyllabusMapping(
                preliminarySectionBn = "গাণিতিক যুক্তি (১৫ নম্বর): বাস্তব সংখ্যা, মৌলিক ও যৌগিক সংখ্যা, গ.সা.গু ও ল.সা.গু",
                writtenSectionBn = "গাণিতিক যুক্তি (কোড 008, ৫০ নম্বর) ও মানসিক দক্ষতা (কোড 009, ৫০ নম্বর): সংখ্যাতত্ত্ব ও ধারা",
                provenanceCategory = BcsProvenanceCategory.OFFICIAL_BPSC_SYLLABUS,
                examPatternNoteBn = "ল.সা.গু ও গ.সা.গু-এর গুণফল সূত্র এবং বিভাজ্যতা যাচাই প্রিলিমিনারির অত্যন্ত জনপ্রিয় টপিক।"
            )
            SolverTopic.LINEAR_EQUATIONS,
            SolverTopic.QUADRATIC_EQUATIONS,
            SolverTopic.FACTORIZATION -> BcsSyllabusMapping(
                preliminarySectionBn = "গাণিতিক যুক্তি (১৫ নম্বর): বীজগণিতীয় সূত্রাবলি, উৎপাদক, একঘাত ও দ্বিঘাত সমীকরণ এবং অসমতা (৩–৪ নম্বর)",
                writtenSectionBn = "গাণিতিক যুক্তি (কোড 008, ৫০ নম্বর): উৎপাদকে বিশ্লেষণ, একঘাত ও দ্বিঘাত সমীকরণ, অসমতা এবং ২/৩ চলকবিশিষ্ট সহসমীকরণ",
                provenanceCategory = BcsProvenanceCategory.OFFICIAL_BPSC_SYLLABUS,
                examPatternNoteBn = "পরমমান অসমতা (|ax+b| ≤ c), দ্বিঘাত অসমতা এবং সহসমীকরণ প্রিলিমিনারি ও লিখিত উভয় পরীক্ষায় সর্বোচ্চ গুরুত্ব বহন করে।"
            )
        }
    }

    private fun buildDefaultVisualSpec(res: SolverResult): VisualModelSpec {
        return when (res.topic) {
            SolverTopic.BASIC_ARITHMETIC -> VisualModelSpec(
                diagramType = VisualDiagramType.NUMBER_LINE,
                titleBn = "সংখ্যারেখায় চূড়ান্ত মান ও ধাপ প্রবাহ",
                subtitleBn = "চূড়ান্ত ফলাফল: ${res.finalAnswerBn}",
                primaryValues = listOf(0.0, extractFirstDouble(res.finalAnswerBn) ?: 10.0),
                labelsBn = listOf("০", res.finalAnswerBn),
                accessibilitySummaryBn = "সংখ্যারেখায় সরল অংকের চূড়ান্ত মান ${res.finalAnswerBn} চিহ্নিত করা হয়েছে।"
            )
            SolverTopic.FRACTIONS_DECIMALS -> VisualModelSpec(
                diagramType = VisualDiagramType.FRACTION_BAR,
                titleBn = "ভগ্নাংশের সমহর ও অংশের তুলনামূলক বার চিত্র",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(0.75, 0.4, 1.15),
                labelsBn = listOf("১ম ভগ্নাংশ", "২য় ভগ্নাংশ", "ফলাফল"),
                accessibilitySummaryBn = "ভগ্নাংশ বার চিত্রে প্রদত্ত ভগ্নাংশদ্বয় ও ফলাফল ${res.finalAnswerBn} প্রদর্শিত।"
            )
            SolverTopic.PERCENTAGE -> VisualModelSpec(
                diagramType = VisualDiagramType.PERCENTAGE_BAR,
                titleBn = "১০০% স্কেলে শতকরা তুলনামূলক বার",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(100.0, extractFirstDouble(res.finalAnswerBn) ?: 25.0),
                labelsBn = listOf("ভিত্তি (১০০%)", res.finalAnswerBn),
                accessibilitySummaryBn = "১০০% ভিত্তি স্কেলের সাপেক্ষে ${res.finalAnswerBn} প্রদর্শিত।"
            )
            SolverTopic.RATIO_PROPORTION -> VisualModelSpec(
                diagramType = VisualDiagramType.RATIO_SEGMENTS,
                titleBn = "আনুপাতিক অংশ বণ্টন বার চিত্র",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(3.0, 5.0),
                labelsBn = listOf("১ম অংশ", "২য় অংশ"),
                accessibilitySummaryBn = "মোট পরিমাণকে আনুপাতিক খণ্ডে ভাগ করে ${res.finalAnswerBn} দেখানো হয়েছে।"
            )
            SolverTopic.PROFIT_LOSS -> VisualModelSpec(
                diagramType = VisualDiagramType.PROFIT_LOSS_BAR,
                titleBn = "ক্রয়মূল্য (CP) ও বিক্রয়মূল্য (SP) তুলনা বার",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(100.0, 120.0),
                labelsBn = listOf("ক্রয়মূল্য (CP)", "বিক্রয়মূল্য (SP)"),
                accessibilitySummaryBn = "ক্রয়মূল্য ও বিক্রয়মূল্যের পার্থক্য থেকে ${res.finalAnswerBn} চিত্রায়িত।"
            )
            SolverTopic.AVERAGE -> VisualModelSpec(
                diagramType = VisualDiagramType.NUMBER_LINE,
                titleBn = "সুষম বণ্টন ও গড় ভারসাম্য রেখা",
                subtitleBn = "গড় মান = ${res.finalAnswerBn}",
                primaryValues = listOf(extractFirstDouble(res.finalAnswerBn) ?: 25.0),
                labelsBn = listOf("গড় (${res.finalAnswerBn})"),
                accessibilitySummaryBn = "উপাত্তগুলোর ভারসাম্য বিন্দু বা গড় মান ${res.finalAnswerBn}।"
            )
            SolverTopic.SIMPLE_INTEREST -> VisualModelSpec(
                diagramType = VisualDiagramType.INTEREST_GROWTH_BAR,
                titleBn = "আসল (P) ও মুনাফা-আসল (A) বৃদ্ধি চিত্র",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(100.0, 130.0),
                labelsBn = listOf("প্রারম্ভিক আসল (P)", "সবৃদ্ধিমূল (A)"),
                accessibilitySummaryBn = "আসল ও অর্জিত মুনাফার সমন্বয়ে ${res.finalAnswerBn} প্রদর্শিত।"
            )
            SolverTopic.TIME_AND_WORK -> VisualModelSpec(
                diagramType = VisualDiagramType.WORK_RATE_BAR,
                titleBn = "দৈনিক কাজের হার ও যৌথ সম্পন্নকরণ অগ্রগতি বার",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(0.4, 0.6, 1.0),
                labelsBn = listOf("১ম অংশীদারের হার", "২য় অংশীদারের হার", "সম্পূর্ণ কাজ (১ অংশ)"),
                accessibilitySummaryBn = "দৈনিক কাজের অংশ একত্রিত হয়ে মোট সময় ${res.finalAnswerBn} প্রদর্শিত।"
            )
            SolverTopic.SPEED_DISTANCE_TIME -> VisualModelSpec(
                diagramType = VisualDiagramType.MOTION_TRACK,
                titleBn = "গতিপথ (Distance = Speed × Time) স্কেল",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(0.0, extractFirstDouble(res.finalAnswerBn) ?: 60.0),
                labelsBn = listOf("যাত্রা শুরু (০)", res.finalAnswerBn),
                accessibilitySummaryBn = "গতিপথ চিত্রে ${res.finalAnswerBn} নির্দেশিত।"
            )
            SolverTopic.NUMBER_SYSTEM_DIVISIBILITY -> VisualModelSpec(
                diagramType = VisualDiagramType.EUCLIDEAN_STRIP,
                titleBn = "ইউক্লিডীয় ভাগ প্রক্রিয়া: ভাজ্য = ভাজক × ভাগফল + ভাগশেষ",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(1.0),
                labelsBn = listOf(res.finalAnswerBn),
                accessibilitySummaryBn = "ভাজ্যকে সমান ভাজক গুচ্ছে ভাগ করে ফলাফল ${res.finalAnswerBn} দেখানো হয়েছে।"
            )
            SolverTopic.HCF_LCM -> VisualModelSpec(
                diagramType = VisualDiagramType.FACTOR_VENN,
                titleBn = "মৌলিক উৎপাদক ভেনচিত্র (গ.সা.গু = ছেদ, ল.সা.গু = সংযোগ)",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(12.0, 18.0),
                labelsBn = listOf("সাধারণ উৎপাদক (গ.সা.গু)", "সর্বোচ্চ ঘাতের গুণফল (ল.সা.গু)"),
                accessibilitySummaryBn = "মৌলিক উৎপাদকের ভেনচিত্রে ${res.finalAnswerBn} দেখানো হয়েছে।"
            )
            SolverTopic.SQUARES_CUBES_ROOTS -> VisualModelSpec(
                diagramType = VisualDiagramType.SQUARE_GRID,
                titleBn = "জ্যামিতিক বর্গক্ষেত্র ও উৎপাদক জোড়া মডেল",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(extractFirstDouble(res.finalAnswerBn) ?: 12.0),
                labelsBn = listOf("বাহুর মান / ফলাফল: ${res.finalAnswerBn}"),
                accessibilitySummaryBn = "বর্গ ও মূলের জ্যামিতিক মডেলে ${res.finalAnswerBn} প্রদর্শিত।"
            )
            SolverTopic.LINEAR_EQUATIONS -> VisualModelSpec(
                diagramType = VisualDiagramType.ALGEBRA_BALANCE,
                titleBn = "বীজগণিতীয় সমতার দাঁড়িপাল্লা (বামপক্ষ = ডানপক্ষ)",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(extractFirstDouble(res.finalAnswerBn) ?: 4.0),
                labelsBn = listOf("বামপক্ষ (LHS)", "ডানপক্ষ (RHS)"),
                equationLeftBn = res.originalProblemBn.substringBefore("=").trim(),
                equationRightBn = res.originalProblemBn.substringAfter("=", "").trim(),
                accessibilitySummaryBn = "সমতার দাঁড়িপাল্লা ও সংখ্যারেখায় সমাধান ${res.finalAnswerBn} প্রদর্শিত।"
            )
            SolverTopic.QUADRATIC_EQUATIONS -> VisualModelSpec(
                diagramType = VisualDiagramType.PARABOLA_PLOT,
                titleBn = "দ্বিঘাত প্যারাবোলা, শীর্ষবিন্দু ও মূল বিন্দু",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(extractFirstDouble(res.finalAnswerBn) ?: 2.0),
                labelsBn = listOf("মূল / সমাধান: ${res.finalAnswerBn}"),
                accessibilitySummaryBn = "দ্বিঘাত প্যারাবোলা লেখচিত্রে ${res.finalAnswerBn} প্রদর্শিত।"
            )
            SolverTopic.FACTORIZATION,
            SolverTopic.SIMPLIFICATION -> VisualModelSpec(
                diagramType = VisualDiagramType.FACTOR_AREA_GRID,
                titleBn = "উৎপাদক ক্ষেত্রফল গ্রিড ও পদ বিভাজন",
                subtitleBn = res.finalAnswerBn,
                primaryValues = listOf(1.0, 2.0, 3.0),
                labelsBn = listOf("মূল রাশি", "বিশ্লেষিত/সরলীকৃত রূপ: ${res.finalAnswerBn}"),
                accessibilitySummaryBn = "উৎপাদক ও সরলীকরণ গ্রিডে ${res.finalAnswerBn} প্রদর্শিত।"
            )
        }
    }

    private fun extractFirstDouble(bnStr: String): Double? {
        val ascii = SharedMathEngine.normalizeInputAscii(bnStr)
        val m = Regex("""[+-]?\d+(?:\.\d+)?""").find(ascii) ?: return null
        return m.value.toDoubleOrNull()
    }

    private fun getDefaultWhyRuleForTopic(topic: SolverTopic, stepIndex: Int): String = when (topic) {
        SolverTopic.BASIC_ARITHMETIC -> "BODMAS অগ্রাধিকার বিধি অনুযায়ী উচ্চতর অগ্রাধিকারের চিহ্নটি আগে সমাধান করলে হিসাবের নির্ভুলতা বজায় থাকে।"
        SolverTopic.FRACTIONS_DECIMALS -> "ভগ্নাংশের হর সমান (সমহর) বা গ.সা.গু দ্বারা কাটাকাটি করলে ভগ্নাংশটি প্রমিত লঘিষ্ঠ রূপ পায়।"
        SolverTopic.PERCENTAGE -> "শতকরা মানে ১০০ ভাগের অংশ; তাই ১০০ ভিত্তির সাপেক্ষে অনুপাত করলে তুলনা সহজ হয়।"
        SolverTopic.RATIO_PROPORTION -> "মোট পরিমাণকে মোট অনুপাত-সমষ্টি দিয়ে ভাগ করলে ১ একক অনুপাতের প্রকৃত মান পাওয়া যায়।"
        SolverTopic.PROFIT_LOSS -> "ক্রয়মূল্যকে ভিত্তি (১০০%) ধরে লাভ বা ক্ষতির অনুপাত নির্ণয় করাই বাণিজ্যিক গণিতের মূল নিয়ম।"
        SolverTopic.AVERAGE -> "সকল উপাত্তের যোগফলকে উপাত্ত সংখ্যা দিয়ে ভাগ করলে প্রতিটি উপাত্তের সুষম গড় মান পাওয়া যায়।"
        SolverTopic.SIMPLE_INTEREST -> "মূলধন, হার ও সময়ের গুণফল থেকে অর্জিত মুনাফা এবং আসল যোগ করে সবৃদ্ধিমূল নির্ধারিত হয়।"
        SolverTopic.TIME_AND_WORK -> "মোট কাজকে ১ অংশ ধরে একক সময়ের কাজের হার যোগ করাই যৌথ কাজের মৌলিক গাণিতিক নিয়ম।"
        SolverTopic.SPEED_DISTANCE_TIME -> "একক সময়ে অতিক্রান্ত দূরত্বই গতিবেগ; তাই একক (km/h বা m/s) সামঞ্জস্যপূর্ণ রাখা আবশ্যক।"
        SolverTopic.NUMBER_SYSTEM_DIVISIBILITY -> "স্থানীয় মানের বৈশিষ্ট্যের কারণে পুরো সংখ্যা ভাগ না করেই অঙ্ক দেখে বিভাজ্যতা নিশ্চিত করা যায়।"
        SolverTopic.HCF_LCM -> "সাধারণ মৌলিক উৎপাদকগুলো গ.সা.গু এবং সর্বোচ্চ ঘাতগুলো ল.সা.গু গঠন করে।"
        SolverTopic.SQUARES_CUBES_ROOTS -> "মৌলিক উৎপাদকের জোড়া (২টি) বর্গমূল এবং ত্রয়ী (৩টি) ঘনমূল নির্দেশ করে।"
        SolverTopic.LINEAR_EQUATIONS -> "সমীকরণের উভয় পক্ষে একই গাণিতিক প্রক্রিয়া চালালে সমতার ভারসাম্য অক্ষুণ্ণ থাকে।"
        SolverTopic.QUADRATIC_EQUATIONS -> "পূর্ণবর্গীকরণ থেকে প্রাপ্ত দ্বিঘাত সূত্র যেকোনো দ্বিঘাত সমীকরণের মূল নির্ণয়ে সর্বজনীন।"
        SolverTopic.FACTORIZATION -> "যোগফল আকারে থাকা বহুপদীকে গুণফল আকারে নিলে সমীকরণ সমাধান ও ভগ্নাংশ কাটাকাটি সহজ হয়।"
        SolverTopic.SIMPLIFICATION -> "পূর্ণবর্গ উৎপাদক বর্গমূলের বাইরে আনলে এবং সদৃশ পদ একত্রিত করলে রাশিটি ক্ষুদ্রতম আদর্শ রূপ পায়।"
    }

    private fun getConceptualAnalogyForTopic(topic: SolverTopic): String = when (topic) {
        SolverTopic.BASIC_ARITHMETIC -> "উদাহরণ: আগে প্যাকেটের ভেতরের বিস্কুট গুনে (বন্ধনী), তারপর কয়েকটি প্যাকেট গুণ করে, শেষে খোলা বিস্কুট যোগ করা।"
        SolverTopic.FRACTIONS_DECIMALS -> "উদাহরণ: ১/৪ ও ১/২ যোগ করার আগে দুটোকেই সমান ১/৪ আকারের স্লাইসে (১/৪ ও ২/৪) পরিণত করে নেওয়া।"
        SolverTopic.LINEAR_EQUATIONS -> "উদাহরণ: দাঁড়িপাল্লার দুই পাশ থেকে সমান ওজন কমালে বা বাড়ালে পাল্লা দুটি সমানই থাকে।"
        SolverTopic.TIME_AND_WORK -> "উদাহরণ: একটি পানির ট্যাংকে দুটি পাইপ একসাথে পানি ঢাললে প্রতি ঘণ্টায় দুটি পাইপের পানি যোগ হয়।"
        else -> "বাস্তব জীবনের পরিমাপকে ছোট ছোট যৌক্তিক ধাপে ভাগ করে সমাধান।"
    }

    private fun getCommonMistakeForTopic(topic: SolverTopic): String = when (topic) {
        SolverTopic.BASIC_ARITHMETIC -> "সাধারণ ভুল: 'এর' বা ভাগের কাজ করার আগে গুণের কাজ করে ফেলা কিংবা বন্ধনীর বাইরের ঋণাত্মক চিহ্ন ভেতরে গুণ না করা।"
        SolverTopic.FRACTIONS_DECIMALS -> "সাধারণ ভুল: হর সমান না করেই সরাসরি লবের সাথে লব এবং হরের সাথে হর যোগ করে ফেলা (যেমন ১/২ + ১/৩ = ২/৫ লেখা)।"
        SolverTopic.PROFIT_LOSS -> "সাধারণ ভুল: শতকরা লাভ/ক্ষতি বের করার সময় ক্রয়মূল্যের বদলে বিক্রয়মূল্য দিয়ে ভাগ করা।"
        SolverTopic.SIMPLE_INTEREST -> "সাধারণ ভুল: চক্রবৃদ্ধির সূত্র থেকে পাওয়া সবৃদ্ধিমূল (A)-কেই সরাসরি চক্রবৃদ্ধি মুনাফা (CI) মনে করা।"
        SolverTopic.LINEAR_EQUATIONS -> "সাধারণ ভুল: পক্ষান্তরের সময় চিহ্ন না বদলানো এবং ঋণাত্মক সংখ্যা দিয়ে ভাগ করার সময় অসমতার চিহ্ন না উল্টানো।"
        else -> "সাধারণ ভুল: একক বা চিহ্ন (+/−) যাচাই না করে সরাসরি সূত্র প্রয়োগ করা।"
    }

    /**
     * Companion modules covering official BPSC Preliminary & Written syllabus areas outside the 16 arithmetic/algebra topics.
     */
    fun getBcsCompanionModules(): List<BcsCompanionModuleItem> = listOf(
        BcsCompanionModuleItem(
            syllabusAreaBn = "জ্যামিতি ও পিথাগোরাস (রেখা, কোণ, ত্রিভুজ, বৃত্ত)",
            prelimAndWrittenWeightBn = "প্রিলি: ৩ নম্বর | লিখিত: ১০–১৫ নম্বর",
            topicsCoveredBn = "ত্রিভুজের সর্বসমতা, পিথাগোরাসের উপপাদ্য, বৃত্তের জ্যা ও স্পর্শক এবং চতুর্ভুজ সংক্রান্ত উপপাদ্য",
            targetModuleNameBn = "ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার ও ফর্মুলা লাইব্রেরি",
            targetRouteType = "visual_explorer"
        ),
        BcsCompanionModuleItem(
            syllabusAreaBn = "পরিমিতি ও ঘনজ্যামিতি (ক্ষেত্রফল ও আয়তন)",
            prelimAndWrittenWeightBn = "প্রিলি: ১–২ নম্বর | লিখিত: ৫–১০ নম্বর",
            topicsCoveredBn = "আয়তক্ষেত্র, বর্গ, ত্রিভুজ, বৃত্ত, সিলিন্ডার, গোলক ও শঙ্কুর ক্ষেত্রফল ও আয়তন",
            targetModuleNameBn = "ফর্মুলা লাইব্রেরি (পরিমিতি বিভাগ)",
            targetRouteType = "formula_library"
        ),
        BcsCompanionModuleItem(
            syllabusAreaBn = "ত্রিকোণমিতি ও স্থানাঙ্ক জ্যামিতি (দূরত্ব ও ঢাল)",
            prelimAndWrittenWeightBn = "লিখিত (কোড 008): ১০–১৫ নম্বর",
            topicsCoveredBn = "ত্রিকোণমিতিক অনুপাত, উচ্চতা ও দূরত্ব (উন্নতি/অবনতি কোণ), দুই বিন্দুর দূরত্ব ও সরলরেখার সমীকরণ",
            targetModuleNameBn = "ফর্মুলা লাইব্রেরি ও প্র্যাকটিস ল্যাব",
            targetRouteType = "formula_library"
        ),
        BcsCompanionModuleItem(
            syllabusAreaBn = "সেট তত্ত্ব, ভেনচিত্র, বিন্যাস-সমাবেশ ও সম্ভাব্যতা",
            prelimAndWrittenWeightBn = "প্রিলি: ৩–৪ নম্বর | লিখিত: ১০ নম্বর",
            topicsCoveredBn = "সেটের সংযোগ-ছেদ, ভেনচিত্র, nPr ও nCr, এবং সম্ভাব্যতার মৌলিক সূত্র",
            targetModuleNameBn = "ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার ও ফর্মুলা লাইব্রেরি",
            targetRouteType = "visual_explorer"
        ),
        BcsCompanionModuleItem(
            syllabusAreaBn = "সূচক, লগারিদম এবং সমান্তর ও গুণোত্তর ধারা",
            prelimAndWrittenWeightBn = "প্রিলি: ২–৩ নম্বর | লিখিত: ৫–১০ নম্বর",
            topicsCoveredBn = "সূচক ও লগারিদমের বিধি, সমান্তর ও গুণোত্তর ধারার n-তম পদ এবং সমষ্টি নির্ণয়",
            targetModuleNameBn = "ফর্মুলা লাইব্রেরি (বীজগণিত ও ধারা)",
            targetRouteType = "formula_library"
        )
    )
}
