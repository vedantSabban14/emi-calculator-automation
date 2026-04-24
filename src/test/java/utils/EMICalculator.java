package utils;

public class EMICalculator {

    // Standard EMI formula: EMI = [P × R × (1+R)^N] / [(1+R)^N - 1]
    public static int calculateEMI(double principal, double annualRate, int tenureYears) {
        double monthlyRate = annualRate / 12 / 100;
        int months = tenureYears * 12;

        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, months))
                / (Math.pow(1 + monthlyRate, months) - 1);

        return (int) Math.round(emi);
    }
}