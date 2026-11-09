# MATHEMATICS_ACCURACY_REPORT.md
# Math Master Android v3.2 — Mathematical Accuracy & Engine Verification

Date: 2026-11-02
Scope: `MathProblemSolverEngine.kt`, `NumberSystemModels.kt`, `MentalMathModels.kt`, `VisualMathModels.kt`, `PrimeNumberModels.kt`, `QuizModels.kt`

---

## 1. Verified Mathematical Flaws & Repairs

### 1.1 Mental Math Mixed Arithmetic Negative Subtraction
- **Defect:** `ans = if (isAdd) prod1 + prod2 else (prod1 - prod2).coerceAtLeast(1)` in `MentalMathModels.kt:501` forcefully set negative subtraction results to 1.
- **Example:** $(6 \times 5) - (8 \times 7) = 30 - 56 = -26$. The engine generated this question with answer `1`.
- **Correction:** When generating subtraction problems, swap terms if `prod1 < prod2` so that the result is always strictly positive ($p_1 - p_2 > 0$).

### 1.2 Divisibility Rules on Negative Integers
- **Defect:** `it.digitToInt()` in `MathProblemSolverEngine.kt:1685` threw `IllegalArgumentException` on `'-'`.
- **Correction:** Use `abs(num)` prior to string representation and digit sum calculation ($d \mid n \iff d \mid |n|$).

### 1.3 BODMAS Sign Normalization
- **Defect:** Negated bracket results like `-(2 - 7)` produced `"--5"`, crashing `toDouble()`.
- **Correction:** Normalize consecutive signs (`--` -> `+`, `+-` -> `-`, `-+` -> `-`, `++` -> `+`) prior to arithmetic evaluation.

### 1.4 Visual Fraction Division by Zero
- **Defect:** `VisualMathEngine.analyzeFraction(5, 0)` substituted `den = 1` and returned `5.0`.
- **Correction:** Mark fraction with `isDefined = false`, set `decimalValue = Double.NaN`, and display explicit Bengali explanation that division by zero is undefined in mathematics.

### 1.5 GCD & LCM on Zero and Negative Inputs
- **Defect:** `NumberSystemProvider.gcd` and `calculateGcdLcm` reassigned inputs $\le 0$ to $1$, yielding $\gcd(0, 12) = 1$ instead of $12$, and $\gcd(-12, 18) = 1$ instead of $6$.
- **Correction:** Standard mathematical formulation $\gcd(a, b) = \gcd(|a|, |b|)$. When both inputs are 0, return 0. $\text{lcm}(a, b) = (|a \times b|) / \gcd(a, b)$, returning 0 when either input is 0.

### 1.6 Ratio Simplification with Decimals
- **Defect:** `RatioType.SIMPLIFY` cast `Double` directly to `Long` (`r1.toLong()`), truncating $2.5 : 5.0$ to $2 : 5$.
- **Correction:** Detect fractional digits, multiply by $10^k$ to convert to whole integers, then divide by their GCD.

### 1.7 Linear Equation Coefficient 1 Step Formatting
- **Defect:** In `MathProblemSolverEngine.kt:2172`, `formatLinearCoeff(1.0)` returned `""`, rendering steps as `x = 5 ÷  = 5` with a missing divisor.
- **Correction:** Use `formatNum(netCoeff)` so the divisor `1` is explicitly printed: `x = 5 ÷ 1 = 5`.

### 1.8 BCS Prime Magic Code 10-Decade Formatting
- **Defect:** `PrimeNumberModels.kt:139` rendered 9 digits (`411 - 312 - 214`, sum 19) for 10 decades containing 21 primes.
- **Correction:** Correct string to 10 digits (`411 - 312 - 2214`, sum 21).

### 1.9 Impossible Practice Question in Number Systems
- **Defect:** Question `ns_h_3` asked for smallest digit in $8764*212$ to be divisible by 8. Last 3 digits $212$ are not divisible by 8, so no digit $*$ could make it divisible.
- **Correction:** Updated problem to $876421*$, where $* = 6$ yields $216 \div 8 = 27$ (divisible).

---

## 2. Test Plan & Regression Gates
All 9 mathematical repairs are accompanied by unit tests in `MathProblemSolverUnitTest.kt` and `VisualMathExplorerUnitTest.kt`.
