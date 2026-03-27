import java.util.*;
import java.util.regex.*;
import java.util.function.Consumer;

public class MindShellCLI {

    static Map<String, Integer> memory = new HashMap<>();
    static List<String> history = new ArrayList<>();
    static Map<String, Consumer<String>> commands = new LinkedHashMap<>();

    static boolean typingEffect = false;

    public static void main(String[] args) {

        registerCommands();
        Scanner sc = new Scanner(System.in);

        printAI("🔥 MindShell 🤖");
        printAI("Powering your terminal with intelligence and precision.");
        printAI("Solve, compute, and automate — all in one place.");
        printAI("Type 'help' to begin your journey.");



        printAI("Type 'help' | 'exit'");

        while (true) {
            System.out.print(color("You: ", BLUE));
            String input = sc.nextLine().toLowerCase().trim();

            history.add(input);

            if (input.matches("exit|quit|bye")) {
                printAI("Goodbye 👋");
                break;
            }

            if (input.equals("help")) {
                showHelp();
                continue;
            }

            if (input.equals("history")) {
                showHistory();
                continue;
            }

            boolean matched = false;

            // ✅ longest command first
            List<String> keys = new ArrayList<>(commands.keySet());
            keys.sort((a, b) -> b.length() - a.length());

            for (String key : keys) {
                if (input.contains(key)) {
                    try {
                        commands.get(key).accept(input);
                    } catch (Exception e) {
                        printAI("⚠️ Invalid input format");
                    }
                    matched = true;
                    break;
                }
            }

            if (!matched) suggest(input);
        }

        sc.close();
    }

    static void registerCommands() {

        commands.put("remember", input -> {
            Matcher m = Pattern.compile("(\\d+)\\s+as\\s+(\\w+)").matcher(input);
            if (m.find()) {
                memory.put(m.group(2), Integer.parseInt(m.group(1)));
                printAI("Stored " + m.group(1) + " as '" + m.group(2) + "'");
            } else {
                int num = extractSingleNumber(input);
                memory.put("last", num);
                printAI("Stored " + num);
            }
        });

        commands.put("use", input -> {
            Matcher m = Pattern.compile("use\\s+(\\w+)").matcher(input);
            if (m.find()) {
                String key = m.group(1);
                if (memory.containsKey(key)) {
                    printAI(key + " = " + memory.get(key));
                } else {
                    printAI("No memory found for '" + key + "'");
                }
            } else {
                printAI("Usage: use x");
            }
        });

        commands.put("fibonacci", i -> run(() -> new Fibonacci().series(getNumber(i))));
        commands.put("tribonacci", i -> run(() -> new TribonacciSeries().triSeries(getNumber(i))));

        commands.put("prime between", i -> {
            int[] n = extractTwoNumbers(i);
            run(() -> new PrimeNumbers().findPrime(n[0], n[1]));
        });

        commands.put("prime", i -> run(() -> new Prime().checkPrime(getNumber(i))));

        commands.put("factorial", i -> printAI("Result: " + new Factorial().fact(getNumber(i))));
        commands.put("table", i -> run(() -> new Table().maketable(getNumber(i))));
        commands.put("sqrt", i -> run(() -> new SquareRoot().calculate(getNumber(i))));
        commands.put("perfect", i -> run(() -> new PerfectNumber().checkNum(getNumber(i))));

        commands.put("reverse", i -> printAI("Result: " + new Reverse().getReverse(getNumber(i))));
        commands.put("sum", i -> printAI("Sum: " + new SumDigits().sum(getNumber(i))));
        commands.put("palindrome", i -> run(() -> new Palindrome().check(getNumber(i))));
        commands.put("armstrong", i -> run(() -> new Armstrong().check(getNumber(i))));
        commands.put("factors", i -> run(() -> new Factor().printFactors(getNumber(i))));

        commands.put("swap", i -> {
            int[] n = extractTwoNumbers(i);
            new Swap().swapWithThird(n[0], n[1]);
        });

        commands.put("divisible", i -> {
            int[] n = extractTwoNumbers(i);
            run(() -> new DivisibilityCheck().check(n[0], n[1]));
        });

        commands.put("leap", i -> run(() -> new LeapYear().yearCheck(getNumber(i))));
        commands.put("day", i -> run(() -> new Weekdays().printDays(getNumber(i))));
        commands.put("month", i -> run(() -> new MonthsDetail().detail(getNumber(i))));

        commands.put("case", i -> {
            String letters = i.replaceAll("[^a-zA-Z]", "");
            if (letters.isEmpty()) {
                printAI("Please provide a character");
                return;
            }
            new ConvertCase().change(letters.charAt(0));
        });

        commands.put("calc", i -> {
            String expr = i.replaceAll("[^0-9+\\-*/^().]", "");
            if (expr.isEmpty()) {
                printAI("Invalid expression");
                return;
            }
            printAI("Result: " + new Calculator().calculate(expr));
        });
    }

    static void run(Runnable r) {
        System.out.print(color("AI: ", GREEN));
        r.run();
    }

    static int getNumber(String input) {
        try {
            return extractSingleNumber(input);
        } catch (Exception e) {
            if (memory.containsKey("last")) return memory.get("last");
            throw new RuntimeException();
        }
    }

    static int extractSingleNumber(String input) {
        Matcher m = Pattern.compile("\\d+").matcher(input);
        if (m.find()) return Integer.parseInt(m.group());
        throw new RuntimeException();
    }

    static int[] extractTwoNumbers(String input) {
        Matcher m = Pattern.compile("\\d+").matcher(input);
        int[] nums = new int[2];
        int i = 0;
        while (m.find() && i < 2)
            nums[i++] = Integer.parseInt(m.group());
        if (i < 2) throw new RuntimeException();
        return nums;
    }

    static final String RESET = "\u001B[0m";
    static final String GREEN = "\u001B[32m";
    static final String BLUE = "\u001B[34m";

    static String color(String text, String color) {
        return color + text + RESET;
    }

    static void printAI(String msg) {
        System.out.print(color("AI: ", GREEN));
        if (typingEffect) {
            for (char c : msg.toCharArray()) {
                System.out.print(c);
                try { Thread.sleep(8); } catch (Exception ignored) {}
            }
            System.out.println();
        } else {
            System.out.println(msg);
        }
    }

    static void showHelp() {
        System.out.println(color("\n🔥 COMMANDS:", GREEN));
        System.out.println("fibonacci 10");
        System.out.println("prime between 10 and 50");
        System.out.println("factorial 5");
        System.out.println("calc 2+3*4");
        System.out.println("remember 25 as x");
        System.out.println("use x");
        System.out.println("history");
    }

    static void showHistory() {
        System.out.println(color("\n📜 HISTORY:", BLUE));
        for (String h : history) System.out.println(h);
    }

    static void suggest(String input) {
        String best = null;
        int min = Integer.MAX_VALUE;

        for (String cmd : commands.keySet()) {
            int d = levenshtein(input, cmd);
            if (d < min) {
                min = d;
                best = cmd;
            }
        }

        if (min <= 5)
            printAI("🤔 Did you mean: '" + best + "'?");
        else
            printAI("🤔 Try 'help'");
    }

    static int levenshtein(String a, String b) {
        int[][] dp = new int[a.length()+1][b.length()+1];

        for (int i = 0; i <= a.length(); i++)
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else {
                    dp[i][j] = Math.min(
                        Math.min(dp[i-1][j]+1, dp[i][j-1]+1),
                        dp[i-1][j-1] + (a.charAt(i-1)==b.charAt(j-1)?0:1)
                    );
                }
            }
        return dp[a.length()][b.length()];
    }
}



// FIBONACCI SERIES
class Fibonacci {
    void series(int term) {
        int a = 0, b = 1, c;

        for (int i = 1; i <= term; i++) {
            System.out.print(a + " ");
            c = a + b;
            a = b;
            b = c;
        }
    }
}

// TRIBONACCI SERIES
class TribonacciSeries {
    void triSeries(int term) {
        int a = 0, b = 1, c = 2, d;

        for (int i = 1; i <= term; i++) {
            System.out.print(a + " ");
            d = a + b + c;
            a = b;
            b = c;
            c = d;
        }
    }
}

// CALCULATOR
class Calculator {

    double calculate(String expression) {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty");
        }
        return evaluate(expression.replaceAll("\\s+", ""));
    }

    private double evaluate(String expr) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();

                while (i < expr.length() &&
                        (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    sb.append(expr.charAt(i));
                    i++;
                }

                numbers.push(Double.parseDouble(sb.toString()));
                i--;
            }

            else if (ch == '(') {
                operators.push(ch);
            }

            else if (ch == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            }

            else if (isOperator(ch)) {
                while (!operators.isEmpty() &&
                        precedence(operators.peek()) >= precedence(ch)) {
                    numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
    }

    private int precedence(char op) {
        switch (op) {
            case '+':
            case '-': return 1;
            case '*':
            case '/': return 2;
            case '^': return 3;
        }
        return 0;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Divide by zero");
                return a / b;
            case '^': return Math.pow(a, b);
        }
        throw new RuntimeException("Invalid operator");
    }
}

// MULTIPLICATION TABLE
class Table {
    void maketable(int n) {
        for (int i = 1; i <= 10; i++) {
            System.out.println(n + " * " + i + " = " + (n * i));
        }
    }
}

// FACTORIAL
class Factorial {
    long fact(int n) {
        if (n < 0) {
            System.out.println("Factorial not defined for negative numbers");
            return -1;
        }

        long prod = 1;
        for (int i = 1; i <= n; i++) {
            prod *= i;
        }
        return prod;
    }
}

// SWAP
class Swap {
    void swapWithThird(int a, int b) {
        System.out.println("original a: " + a + "\noriginal b: " + b);
        int c = a;
        a = b;
        b = c;
        System.out.println("swapped a: " + a + "\nswapped b: " + b);
    }

    void swapWithoutThird(int a, int b) {
        System.out.println("original a: " + a + "\noriginal b: " + b);
        a = a + b;
        b = a - b;
        a = a - b;
        System.out.println("swapped a: " + a + "\nswapped b: " + b);
    }
}

// CONVERT CASE
class ConvertCase {
    void change(char ch) {
        if (Character.isUpperCase(ch)) {
            System.out.println("Lowercase: " + Character.toLowerCase(ch));
        } else {
            System.out.println("Uppercase: " + Character.toUpperCase(ch));
        }
    }
}

// NUMBER CHECK
class NumCheck {
    void check(int n) {
        if (n > 0) System.out.println("Positive number");
        else if (n < 0) System.out.println("Negative number");
        else System.out.println("Equal to zero");
    }
}

// LEAP YEAR
class LeapYear {
    void yearCheck(int year) {
        if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)) {
            System.out.println("Leap year");
        } else {
            System.out.println("Not a leap year");
        }
    }
}

// DIVISIBILITY
class DivisibilityCheck {
    void check(int num, int x) {
        if (num % x == 0)
            System.out.println(num + " is divisible by " + x);
        else
            System.out.println(num + " is not divisible by " + x);
    }
}

// WEEKDAYS
class Weekdays {
    void printDays(int day) {
        switch (day) {
            case 1: System.out.println("Monday"); break;
            case 2: System.out.println("Tuesday"); break;
            case 3: System.out.println("Wednesday"); break;
            case 4: System.out.println("Thursday"); break;
            case 5: System.out.println("Friday"); break;
            case 6: System.out.println("Saturday"); break;
            case 7: System.out.println("Sunday"); break;
            default: System.out.println("Invalid number");
        }
    }
}

// FACTORS
class Factor {
    void printFactors(int number) {
        System.out.print("Factors of " + number + " are: ");
        for (int i = 1; i <= number; i++) {
            if (number % i == 0) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }
}

// MONTH DETAILS
class MonthsDetail {
    void detail(int month) {
        switch (month) {
            case 1: System.out.println("January - 31 days"); break;
            case 2: System.out.println("February - 28/29 days"); break;
            case 3: System.out.println("March - 31 days"); break;
            case 4: System.out.println("April - 30 days"); break;
            case 5: System.out.println("May - 31 days"); break;
            case 6: System.out.println("June - 30 days"); break;
            case 7: System.out.println("July - 31 days"); break;
            case 8: System.out.println("August - 31 days"); break;
            case 9: System.out.println("September - 30 days"); break;
            case 10: System.out.println("October - 31 days"); break;
            case 11: System.out.println("November - 30 days"); break;
            case 12: System.out.println("December - 31 days"); break;
            default: System.out.println("Invalid month");
        }
    }
}

// REVERSE
class Reverse {
    int getReverse(int num) {
        int reverse = 0;
        while (num != 0) {
            reverse = reverse * 10 + num % 10;
            num /= 10;
        }
        return reverse;
    }
}

// SUM DIGITS
class SumDigits {
    int sum(int number) {
        int sum = 0;
        while (number != 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }
}

// PALINDROME
class Palindrome {
    void check(int num) {
        if (num == new Reverse().getReverse(num))
            System.out.println("Palindrome");
        else
            System.out.println("Not Palindrome");
    }
}

// ARMSTRONG (improved)
class Armstrong {
    void check(int num) {
        int original = num;
        int sum = 0;
        int digits = String.valueOf(num).length();

        while (num != 0) {
            int digit = num % 10;
            sum += Math.pow(digit, digits);
            num /= 10;
        }

        if (sum == original)
            System.out.println("Armstrong number");
        else
            System.out.println("Not an Armstrong number");
    }
}

// SQUARE ROOT
class SquareRoot {
    void calculate(int a) {
        System.out.println("Square root of " + a + " is " + Math.sqrt(a));
    }
}

// PRIME
class Prime {
    boolean isPrime(int number) {
        if (number <= 1) return false;

        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    void checkPrime(int number) {
        if (isPrime(number))
            System.out.println("Prime number");
        else
            System.out.println("Not a prime number");
    }
}

// RANGE PRIME
class PrimeNumbers {
    void findPrime(int n1, int n2) {
        Prime p = new Prime();
        System.out.print("Prime numbers: ");
        for (int i = n1; i <= n2; i++) {
            if (p.isPrime(i)) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }
}

// PERFECT NUMBER
class PerfectNumber {
    void checkNum(int num) {
        int sum = 0;

        for (int i = 1; i <= num / 2; i++) {
            if (num % i == 0) sum += i;
        }

        if (sum == num)
            System.out.println("Perfect number");
        else
            System.out.println("Not a Perfect Number");
    }
}