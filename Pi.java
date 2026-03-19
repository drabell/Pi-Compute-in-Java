/******************************************************************************
 Module         : Pi.java |Class Lib (JDK 25)
 Description    : Method to Compute π to specified number of decimal digits
                : using the Chudnovsky formula (fast converging series for π).
  Version       : 25.1.001
 ------------------------------------------------------------------------------
 Copyright      :  2026 Alexander Bell
 -------------------------------------------------------------------------------
 DISCLAIMER
 : This Module is provided on AS IS basis without any warranty.
 : The user assumes the entire risk as to the accuracy and the use of this module.
 : In no event shall the author be liable for any damages arising out of the use
 : or inability to use of this module.
 TERMS OF USE
 : This module is copyrighted. Please keep the Copyright notice intact.
 ****************************************************************************/

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Pi {
    /*
        Compute π to specified number of decimal digits using Chudnovsky formula,
        which converges rapidly and is efficient for high-precision calculations.
        * @param digits the number of decimal digits of π to compute
        * @return π accurate to 'digits' decimal places
        * @throws IllegalArgumentException if digits is not positive
    */
    public static BigDecimal computePi(int digits) {
        if (digits <= 0)
            throw new IllegalArgumentException("digits must be positive");

        // Internal precision with 20 guard digits
        int precision = digits + 20;
        MathContext mc = new MathContext(precision, RoundingMode.HALF_UP);

        // Constants used in Chudnovsky formula
        BigDecimal C13591409   = new BigDecimal("13591409");
        BigDecimal C545140134  = new BigDecimal("545140134");
        BigDecimal C640320     = new BigDecimal("640320");
        BigDecimal C10005      = new BigDecimal("10005");
        BigDecimal C426880     = new BigDecimal("426880");

        // Each Chudnovsky term gives ~14.18 digits
        int terms = (int) (digits / 14.18) + 2;

        // Series sum
        BigDecimal sum = BigDecimal.ZERO;

        // term_0 corresponds to k = 0:
        // (6k)! / ((3k)! (k!)^3 640320^(3k)) = 1
        BigDecimal term = BigDecimal.ONE;

        for (int k = 0; k < terms; k++) {
            BigDecimal kBD = BigDecimal.valueOf(k);

            // A_k = 13591409 + 545140134*k
            BigDecimal Ak = C13591409.add(C545140134.multiply(kBD, mc), mc);

            // Add current contribution (term * A_k) to the sum
            sum = sum.add(term.multiply(Ak, mc), mc);

            // Calculate next term (k -> k+1)
            if (k + 1 < terms) {

                // numerator: (6k+1)(6k+2)(6k+3)(6k+4)(6k+5)(6k+6)
                BigDecimal n3 = BigDecimal.valueOf(6L * k + 3); // mid term
                BigDecimal num = n3
                    .multiply(n3.subtract(BigDecimal.ONE, mc))
                    .multiply(n3.subtract(BigDecimal.TWO, mc))
                    .multiply(n3.add(BigDecimal.ONE, mc))
                    .multiply(n3.add(BigDecimal.TWO, mc))
                    .multiply(n3.add(BigDecimal.valueOf(3), mc));

                // denominator: (3k+1)(3k+2)(3k+3)(k+1)^3 * 640320^3
                BigDecimal d1 = BigDecimal.valueOf(3L * k + 1);
                BigDecimal kp1 = BigDecimal.valueOf(k + 1);
                BigDecimal den = d1
                    .multiply(d1.add(BigDecimal.ONE, mc))
                    .multiply(d1.add(BigDecimal.TWO, mc))
                    .multiply(kp1, mc)
                    .multiply(kp1, mc)
                    .multiply(kp1, mc)
                    .multiply(C640320, mc)
                    .multiply(C640320, mc)
                    .multiply(C640320, mc);

                // term_{k+1} = term_k * (-num / den)
                term = term.multiply(num, mc).divide(den, mc).negate();
            }
        }

        // Calculate: π = (426880 * sqrt(10005)) / sum
        BigDecimal pi = C426880
            .multiply(C10005.sqrt(mc), mc)
            .divide(sum, mc);

        // Return value: π accurate to 'digits' decimal places
        return pi.setScale(digits, RoundingMode.HALF_UP);
    }

    // Test and Benchmark for the computePi() method
    public static void main(String[] args) {
        // decimal digits to compute
        int digits = 100;
        // a reference value of pi with 100 decimal places
        final BigDecimal piRef100 =
                new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679");

        // Performance Benchmarks
        // Approximate Time to compute π on local machine:
        // 2 ms to calculate 100 digits
        // 30 ms to calculate 1000 digits
        // 1 sec to calculate 10,000 digits
        long timeStart = System.nanoTime();
        BigDecimal Pi = computePi(digits);
        long timeEnd = System.nanoTime();
        System.out.println("Calculated vs Reference Pi");
        System.out.println(Pi);
        System.out.println(piRef100);
        System.out.println("Time, ms: " + (double) (timeEnd - timeStart) / 1000000);
    }
}
