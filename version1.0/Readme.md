# 🔥 MindShell — AI-Powered Terminal

> Solve, compute, and automate — all in one place.

MindShell is a feature-rich terminal application built in Java that brings together a wide range of mathematical utilities, number theory tools, and memory-based computations — all through a clean, conversational interface. Available in both a **CLI** and a **Swing GUI** version.

---

## 📸 Screenshots & Demo

> **GUI Version Preview




&nbsp;

> **Demo Video**





---

## ✨ Features

- 🧮 **Math Operations** — Fibonacci, Tribonacci, Factorial, Square Root, Multiplication Tables
- 🔢 **Number Theory** — Prime checking, Prime ranges, Perfect numbers, Armstrong, Palindrome, Factors
- 🔁 **Number Utilities** — Reverse digits, Sum of digits, Swap, Divisibility check
- 📅 **Calendar Tools** — Leap year, Day name, Month details
- 🔤 **Character Tools** — Toggle uppercase/lowercase
- ➕ **Expression Calculator** — Supports `+`, `-`, `*`, `/`, `^`, and parentheses
- 🧠 **Memory System** — Store and recall named values across commands
- 📜 **Command History** — Navigate previous inputs with ↑ / ↓ arrow keys
- 🤖 **Smart Suggestions** — Levenshtein-based fuzzy matching for typos

---

## 🖥️ Versions

### 1. CLI Version (`MindShellCLI.java`)
A terminal-based interface with color-coded output using ANSI escape codes.

```
🔥 MindShell 🤖
Powering your terminal with intelligence and precision.
Type 'help' to begin your journey.

You: fibonacci 10
AI: 0 1 1 2 3 5 8 13 21 34

You: remember 42 as x
AI: Stored 42 as 'x'

You: use x
AI: x = 42
```

### 2. GUI Version (`MindShellSwing.java`)
A full-featured Swing desktop app with a dark theme, sidebar quick-launch panel, and styled output.

<!-- Add GUI screenshots here -->
<!-- ![Sidebar Panel](screenshots/sidebar.png) -->
<!-- ![Output Panel](screenshots/output.png) -->

---

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code with Java extension) or terminal with `javac`

### Running the CLI Version

```bash
javac MindShellCLI.java
java MindShellCLI
```

### Running the GUI Version

```bash
javac MindShellSwing.java
java MindShellSwing
```

---

## 📖 Commands Reference

| Command | Description | Example |
|---|---|---|
| `fibonacci <n>` | Print Fibonacci series | `fibonacci 10` |
| `tribonacci <n>` | Print Tribonacci series | `tribonacci 8` |
| `prime <n>` | Check if a number is prime | `prime 17` |
| `prime between <a> <b>` | List primes in a range | `prime between 10 50` |
| `factorial <n>` | Compute factorial | `factorial 6` |
| `table <n>` | Multiplication table (1–10) | `table 7` |
| `sqrt <n>` | Square root | `sqrt 144` |
| `perfect <n>` | Perfect number check | `perfect 28` |
| `reverse <n>` | Reverse the digits | `reverse 12345` |
| `sum <n>` | Sum of digits | `sum 9876` |
| `palindrome <n>` | Palindrome check | `palindrome 121` |
| `armstrong <n>` | Armstrong number check | `armstrong 153` |
| `factors <n>` | Print all factors | `factors 36` |
| `swap <a> <b>` | Swap two numbers | `swap 8 3` |
| `divisible <a> <b>` | Divisibility check | `divisible 15 3` |
| `leap <year>` | Leap year check | `leap 2024` |
| `day <1–7>` | Day name from number | `day 5` |
| `month <1–12>` | Month name and days | `month 3` |
| `case <char>` | Toggle character case | `case A` |
| `calc <expr>` | Evaluate arithmetic expression | `calc 3+4*2^2` |
| `remember <n> as <x>` | Store a value in memory | `remember 42 as x` |
| `use <x>` | Recall a stored value | `use x` |
| `memory` | Show all stored values | `memory` |
| `history` | Show input history | `history` |
| `clear` | Clear the screen | `clear` |
| `help` | Show command list | `help` |
| `exit` / `quit` / `bye` | Exit the application | `exit` |

---

## 🧠 Memory System

MindShell includes a session-scoped memory store. You can save values by name and reuse them in subsequent commands.

```
You: remember 100 as limit
AI: Stored 100 as 'limit'

You: use limit
AI: limit = 100
```

If a command expects a number but none is provided, it automatically falls back to the last stored value under the key `"last"`.

---

## 🔍 Smart Suggestions

If you mistype a command, MindShell uses **Levenshtein distance** to find the closest match and suggest a correction:

```
You: fibonaci 5
AI: 🤔 Did you mean: 'fibonacci'?
```

---

## 🏗️ Project Structure

```
MindShellCLI.java          ← CLI version (all-in-one file)
MindShellSwing.java        ← GUI version (all-in-one file)
```

All supporting logic (Fibonacci, Prime, Calculator, etc.) is implemented as inner/companion classes within each file for easy portability.

---

## 🛠️ Built With

- **Java** — Core language
- **Java Swing** — GUI framework (GUI version)
- **ANSI Escape Codes** — Terminal coloring (CLI version)
- **Regex + Pattern Matching** — Natural command parsing
- **Levenshtein Algorithm** — Fuzzy command suggestions

---

## 📄 License

This project is open source. Feel free to use, modify, and distribute.

---

## 🙌 Contributing

Pull requests are welcome! If you'd like to add new commands, improve the UI, or fix a bug, feel free to open an issue or submit a PR.

---

*Made with ❤️ in Java*