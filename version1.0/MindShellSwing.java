import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.*;

// ════════════════════════════════════════════════════════
//  MindShell GUI  —  Java Swing  (single file)
// ════════════════════════════════════════════════════════
public class MindShellSwing extends JFrame {

    // ── palette ──────────────────────────────────────────
    static final Color BG        = new Color(13,  15,  20);
    static final Color PANEL     = new Color(18,  21,  30);
    static final Color BORDER_C  = new Color(30,  34,  48);
    static final Color GREEN     = new Color(57,  255, 106);
    static final Color GREEN_DIM = new Color(29,  122, 56);
    static final Color CYAN      = new Color(0,   229, 255);
    static final Color AMBER     = new Color(255, 179, 0);
    static final Color RED_C     = new Color(255, 76,  96);
    static final Color WHITE     = new Color(232, 234, 240);
    static final Color MUTED     = new Color(85,  96,  112);

    static final Font MONO_13 = new Font("Monospaced", Font.PLAIN,  13);
    static final Font MONO_12 = new Font("Monospaced", Font.PLAIN,  12);
    static final Font MONO_11 = new Font("Monospaced", Font.PLAIN,  11);
    static final Font MONO_B  = new Font("Monospaced", Font.BOLD,   13);

    // ── engine ────────────────────────────────────────────
    static final Map<String, Integer>          memory   = new LinkedHashMap<>();
    static final List<String>                  history  = new ArrayList<>();
    static final Map<String, Consumer<String>> commands = new LinkedHashMap<>();
    private int histIdx = -1;

    // ── ui nodes ──────────────────────────────────────────
    private JTextPane outputPane;
    private JScrollPane scrollPane;
    private JTextField  inputField;
    private JLabel      statusLabel;
    private JLabel      memLabel;
    private StyledDocument doc;

    public MindShellSwing() {
        super("MindShell 🔥");
        registerCommands();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080, 700);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildSidebar(),   BorderLayout.WEST);
        add(buildCentre(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        // Ctrl+L = clear
        KeyStroke ctrlL = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);
        getRootPane().registerKeyboardAction(e -> {
            clearOutput();
            printAI("Screen cleared.");
        }, ctrlL, JComponent.WHEN_IN_FOCUSED_WINDOW);

        setVisible(true);

        // boot message
        printSystem("🔥  MindShell  🤖");
        printSystem("Powering your terminal with intelligence and precision.");
        printSystem("Type a command below or click a button on the left.");
        printDivider();
        inputField.requestFocus();
    }

    // ═════════════════════════════════════════════════════
    //  BUILD UI SECTIONS
    // ═════════════════════════════════════════════════════

    JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_C),
            new EmptyBorder(12, 18, 12, 18)
        ));

        // traffic lights + title (left)
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);
        for (Color c : new Color[]{new Color(255,95,87), new Color(255,189,46), new Color(40,202,66)}) {
            JLabel dot = new JLabel("●");
            dot.setForeground(c);
            dot.setFont(new Font("Monospaced", Font.PLAIN, 14));
            left.add(dot);
        }
        JLabel title = new JLabel("  MINDSHELL");
        title.setFont(new Font("Monospaced", Font.BOLD, 15));
        title.setForeground(GREEN);
        JLabel sub = new JLabel("  —  AI-Powered Terminal");
        sub.setFont(MONO_11);
        sub.setForeground(MUTED);
        left.add(title);
        left.add(sub);

        // hint (right)
        JLabel hint = new JLabel("Ctrl+L  clear  |  ↑↓  history");
        hint.setFont(MONO_11);
        hint.setForeground(MUTED);

        bar.add(left,  BorderLayout.WEST);
        bar.add(hint,  BorderLayout.EAST);
        return bar;
    }

    JScrollPane buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(PANEL);
        sidebar.setBorder(new EmptyBorder(12, 8, 12, 8));

        addSection(sidebar, "MATH");
        addChip(sidebar, "fibonacci 10");
        addChip(sidebar, "tribonacci 10");
        addChip(sidebar, "factorial 5");
        addChip(sidebar, "sqrt 144");
        addChip(sidebar, "table 7");
        addChip(sidebar, "calc 3+4*2");

        addSection(sidebar, "NUMBERS");
        addChip(sidebar, "prime 17");
        addChip(sidebar, "prime between 10 50");
        addChip(sidebar, "perfect 28");
        addChip(sidebar, "armstrong 153");
        addChip(sidebar, "palindrome 121");
        addChip(sidebar, "factors 36");
        addChip(sidebar, "reverse 12345");
        addChip(sidebar, "sum 9876");

        addSection(sidebar, "TOOLS");
        addChip(sidebar, "swap 8 3");
        addChip(sidebar, "divisible 15 3");
        addChip(sidebar, "leap 2024");
        addChip(sidebar, "day 5");
        addChip(sidebar, "month 3");
        addChip(sidebar, "case A");

        addSection(sidebar, "MEMORY");
        addChip(sidebar, "remember 42 as x");
        addChip(sidebar, "use x");
        addChip(sidebar, "memory");
        addChip(sidebar, "history");
        addChip(sidebar, "help");
        addChip(sidebar, "clear");

        // glue at bottom
        sidebar.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(sidebar);
        sp.setPreferredSize(new Dimension(200, 0));
        sp.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_C));
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getViewport().setBackground(PANEL);
        return sp;
    }

    void addSection(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.BOLD, 9));
        l.setForeground(GREEN_DIM);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(14, 4, 3, 0));
        p.add(l);
    }

    void addChip(JPanel p, String command) {
        JButton b = new JButton(command);
        b.setFont(MONO_11);
        b.setForeground(MUTED);
        b.setBackground(PANEL);
        b.setBorder(new EmptyBorder(4, 8, 4, 8));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(CYAN); b.setBackground(new Color(22,28,44)); }
            public void mouseExited(MouseEvent e)  { b.setForeground(MUTED); b.setBackground(PANEL); }
        });
        b.addActionListener(e -> {
            inputField.setText(command);
            handleInput(command);
            inputField.setText("");
            inputField.requestFocus();
        });
        p.add(b);
    }

    JPanel buildCentre() {
        // output pane
        outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputPane.setBackground(BG);
        outputPane.setFont(MONO_13);
        outputPane.setBorder(new EmptyBorder(12, 16, 12, 16));
        doc = outputPane.getStyledDocument();

        scrollPane = new JScrollPane(outputPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        // slim dark scrollbar
        scrollPane.getVerticalScrollBar().setBackground(PANEL);
        scrollPane.getVerticalScrollBar().setForeground(BORDER_C);

        // input field
        inputField = new JTextField();
        inputField.setFont(MONO_13);
        inputField.setBackground(new Color(10, 12, 16));
        inputField.setForeground(WHITE);
        inputField.setCaretColor(GREEN);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GREEN_DIM, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // placeholder
        inputField.putClientProperty("JTextField.placeholderText", "type a command…");

        // history navigation + enter
        inputField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String txt = inputField.getText().trim();
                    if (!txt.isEmpty()) {
                        history.add(txt);
                        histIdx = -1;
                        handleInput(txt);
                        inputField.setText("");
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (!history.isEmpty()) {
                        histIdx = Math.min(histIdx + 1, history.size() - 1);
                        inputField.setText(history.get(history.size() - 1 - histIdx));
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (histIdx > 0) {
                        histIdx--;
                        inputField.setText(history.get(history.size() - 1 - histIdx));
                    } else {
                        histIdx = -1;
                        inputField.setText("");
                    }
                }
            }
        });

        // RUN button
        JButton runBtn = new JButton("▶  RUN");
        runBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
        runBtn.setForeground(Color.BLACK);
        runBtn.setBackground(GREEN);
        runBtn.setBorder(new EmptyBorder(10, 18, 10, 18));
        runBtn.setFocusPainted(false);
        runBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        runBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { runBtn.setBackground(new Color(80,255,128)); }
            public void mouseExited(MouseEvent e)  { runBtn.setBackground(GREEN); }
        });
        runBtn.addActionListener(e -> {
            String txt = inputField.getText().trim();
            if (!txt.isEmpty()) {
                history.add(txt);
                handleInput(txt);
                inputField.setText("");
            }
            inputField.requestFocus();
        });

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setBackground(PANEL);
        inputRow.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_C),
            new EmptyBorder(10, 14, 10, 14)
        ));
        inputRow.add(inputField, BorderLayout.CENTER);
        inputRow.add(runBtn,     BorderLayout.EAST);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(BG);
        centre.add(scrollPane, BorderLayout.CENTER);
        centre.add(inputRow,   BorderLayout.SOUTH);
        return centre;
    }

    JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(9, 11, 15));
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_C),
            new EmptyBorder(5, 16, 5, 16)
        ));

        statusLabel = new JLabel("● READY");
        statusLabel.setFont(MONO_11);
        statusLabel.setForeground(GREEN);

        JLabel tip = new JLabel("  Ctrl+L = clear  |  ↑↓ = history  |  click sidebar to run commands");
        tip.setFont(MONO_11);
        tip.setForeground(MUTED);

        memLabel = new JLabel("MEM: 0");
        memLabel.setFont(MONO_11);
        memLabel.setForeground(CYAN);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(statusLabel);
        left.add(tip);

        bar.add(left,      BorderLayout.WEST);
        bar.add(memLabel,  BorderLayout.EAST);
        return bar;
    }

    // ═════════════════════════════════════════════════════
    //  COMMAND HANDLING
    // ═════════════════════════════════════════════════════

    void handleInput(String raw) {
        String input = raw.trim();
        if (input.isEmpty()) return;

        printUser(input);

        if (input.matches("(?i)exit|quit|bye")) {
            printAI("Goodbye 👋  (close the window to exit)");
            return;
        }
        if (input.equalsIgnoreCase("help"))    { showHelp();    return; }
        if (input.equalsIgnoreCase("history")) { showHistory(); return; }
        if (input.equalsIgnoreCase("memory"))  { showMemory();  return; }
        if (input.equalsIgnoreCase("clear"))   { clearOutput(); printAI("Screen cleared."); return; }

        List<String> keys = new ArrayList<>(commands.keySet());
        keys.sort((a, b) -> b.length() - a.length());
        String lower = input.toLowerCase();
        boolean matched = false;
        for (String key : keys) {
            if (lower.contains(key)) {
                commands.get(key).accept(input);
                matched = true;
                break;
            }
        }
        if (!matched) suggest(input);

        setStatus("READY");
        updateMemLabel();
    }

    void registerCommands() {

        commands.put("remember", input -> {
            Matcher m = Pattern.compile("(\\d+)\\s+as\\s+(\\w+)").matcher(input);
            if (m.find()) {
                memory.put(m.group(2), Integer.parseInt(m.group(1)));
                printAI("Stored " + m.group(1) + " as '" + m.group(2) + "'");
            } else {
                int num = extractSingleNumber(input);
                memory.put("last", num);
                printAI("Stored " + num + " as 'last'");
            }
        });

        commands.put("use", input -> {
            Matcher m = Pattern.compile("use\\s+(\\w+)").matcher(input);
            if (m.find()) {
                String key = m.group(1);
                if (memory.containsKey(key)) printAI(key + " = " + memory.get(key));
                else printError("No memory found for '" + key + "'");
            } else printError("Usage: use <name>");
        });

        commands.put("fibonacci", i -> {
            int n = getNumber(i);
            StringBuilder sb = new StringBuilder();
            int a=0,b=1,c;
            for (int x=1;x<=n;x++){sb.append(a).append(" ");c=a+b;a=b;b=c;}
            printAI("Fibonacci(" + n + "): " + sb.toString().trim());
        });

        commands.put("tribonacci", i -> {
            int n = getNumber(i);
            StringBuilder sb = new StringBuilder();
            int a=0,b=1,c=2,d;
            for (int x=1;x<=n;x++){sb.append(a).append(" ");d=a+b+c;a=b;b=c;c=d;}
            printAI("Tribonacci(" + n + "): " + sb.toString().trim());
        });

        commands.put("prime between", i -> {
            int[] n = extractTwoNumbers(i);
            StringBuilder sb = new StringBuilder();
            PrimeUtil p = new PrimeUtil();
            for (int x=n[0];x<=n[1];x++) if(p.isPrime(x)) sb.append(x).append(" ");
            printAI("Primes between " + n[0] + " and " + n[1] + ":  " + sb.toString().trim());
        });

        commands.put("prime", i -> {
            int n = getNumber(i);
            printAI(n + " → " + (new PrimeUtil().isPrime(n) ? "✔ Prime" : "✘ Not Prime"));
        });

        commands.put("factorial", i -> {
            int n = getNumber(i);
            if (n < 0) { printError("Factorial not defined for negatives"); return; }
            long p = 1; for (int x=1;x<=n;x++) p*=x;
            printAI("factorial(" + n + ") = " + p);
        });

        commands.put("table", i -> {
            int n = getNumber(i);
            StringBuilder sb = new StringBuilder("Multiplication table of " + n + ":\n");
            for (int x=1;x<=10;x++) sb.append("   ").append(n).append(" × ").append(x).append(" = ").append(n*x).append("\n");
            printAI(sb.toString().trim());
        });

        commands.put("sqrt", i -> {
            int n = getNumber(i);
            printAI("√" + n + " = " + String.format("%.6f", Math.sqrt(n)));
        });

        commands.put("perfect", i -> {
            int n = getNumber(i);
            int s=0; for(int x=1;x<=n/2;x++) if(n%x==0) s+=x;
            printAI(n + " → " + (s==n ? "✔ Perfect number" : "✘ Not a perfect number"));
        });

        commands.put("reverse", i -> {
            int n = getNumber(i), r=0, tmp=n;
            while(tmp!=0){r=r*10+tmp%10;tmp/=10;}
            printAI("reverse(" + n + ") = " + r);
        });

        commands.put("sum", i -> {
            int n = getNumber(i), s=0, tmp=n;
            while(tmp!=0){s+=tmp%10;tmp/=10;}
            printAI("digit-sum(" + n + ") = " + s);
        });

        commands.put("palindrome", i -> {
            int n = getNumber(i), r=0, tmp=n;
            while(tmp!=0){r=r*10+tmp%10;tmp/=10;}
            printAI(n + " → " + (n==r ? "✔ Palindrome" : "✘ Not a palindrome"));
        });

        commands.put("armstrong", i -> {
            int n = getNumber(i), orig=n, s=0, dig=String.valueOf(n).length();
            while(n!=0){int d=n%10;s+=(int)Math.pow(d,dig);n/=10;}
            printAI(orig + " → " + (s==orig ? "✔ Armstrong number" : "✘ Not an Armstrong number"));
        });

        commands.put("factors", i -> {
            int n = getNumber(i);
            StringBuilder sb = new StringBuilder("Factors of " + n + ":  ");
            for (int x=1;x<=n;x++) if(n%x==0) sb.append(x).append(" ");
            printAI(sb.toString().trim());
        });

        commands.put("swap", i -> {
            int[] n = extractTwoNumbers(i);
            int a=n[0], b=n[1], c=a; a=b; b=c;
            printAI("Swap " + n[0] + " ↔ " + n[1] + "  →  a=" + a + ", b=" + b);
        });

        commands.put("divisible", i -> {
            int[] n = extractTwoNumbers(i);
            printAI(n[0] + " ÷ " + n[1] + " → "
                + (n[0]%n[1]==0 ? "✔ Divisible" : "✘ Not divisible"));
        });

        commands.put("leap", i -> {
            int n = getNumber(i);
            boolean leap = (n%400==0)||(n%4==0&&n%100!=0);
            printAI(n + " → " + (leap ? "✔ Leap year" : "✘ Not a leap year"));
        });

        commands.put("day", i -> {
            int n = getNumber(i);
            String[] days={"","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
            printAI("Day " + n + ": " + (n>=1&&n<=7 ? days[n] : "Invalid (1–7)"));
        });

        commands.put("month", i -> {
            int n = getNumber(i);
            String[] months={"","January — 31","February — 28/29","March — 31","April — 30",
                "May — 31","June — 30","July — 31","August — 31","September — 30",
                "October — 31","November — 30","December — 31"};
            printAI("Month " + n + ": " + (n>=1&&n<=12 ? months[n]+" days" : "Invalid (1–12)"));
        });

        commands.put("case", i -> {
            String lower = i.toLowerCase();
            int idx = lower.indexOf("case");
            if (idx==-1){printError("Usage: case <char>");return;}
            int pos = idx+4;
            while(pos<i.length()&&i.charAt(pos)==' ') pos++;
            if(pos>=i.length()){printError("No character provided");return;}
            char ch = i.charAt(pos);
            if(!Character.isLetter(ch)){printError("Invalid character");return;}
            if(Character.isUpperCase(ch))
                printAI("'" + ch + "' → lowercase: '" + Character.toLowerCase(ch) + "'");
            else
                printAI("'" + ch + "' → uppercase: '" + Character.toUpperCase(ch) + "'");
        });

        commands.put("calc", i -> {
            String expr = i.replaceAll("(?i)calc\\s*","").replaceAll("[^0-9+\\-*/^().]","");
            if(expr.isEmpty()){printError("Invalid expression");return;}
            try {
                double result = new CalcEngine().calculate(expr);
                printAI(expr + " = " + (result==(long)result
                        ? String.valueOf((long)result)
                        : String.format("%.6f", result)));
            } catch(Exception ex) { printError("Calc error: " + ex.getMessage()); }
        });
    }

    // ═════════════════════════════════════════════════════
    //  HELP / HISTORY / MEMORY / SUGGEST
    // ═════════════════════════════════════════════════════

    void showHelp() {
        printDivider();
        appendStyled("  AVAILABLE COMMANDS\n", CYAN, MONO_B);
        String[][] cmds = {
            {"fibonacci <n>",          "Fibonacci series"},
            {"tribonacci <n>",         "Tribonacci series"},
            {"prime <n>",              "Check if prime"},
            {"prime between <a> <b>",  "Primes in range"},
            {"factorial <n>",          "Factorial"},
            {"table <n>",              "Multiplication table"},
            {"sqrt <n>",               "Square root"},
            {"perfect <n>",            "Perfect number check"},
            {"reverse <n>",            "Reverse digits"},
            {"sum <n>",                "Sum of digits"},
            {"palindrome <n>",         "Palindrome check"},
            {"armstrong <n>",          "Armstrong check"},
            {"factors <n>",            "All factors"},
            {"swap <a> <b>",           "Swap two numbers"},
            {"divisible <a> <b>",      "Divisibility check"},
            {"leap <year>",            "Leap year check"},
            {"day <1-7>",              "Day name"},
            {"month <1-12>",           "Month details"},
            {"case <char>",            "Toggle case"},
            {"calc <expr>",            "Arithmetic  +-*/^"},
            {"remember <n> as <x>",    "Store to memory"},
            {"use <x>",                "Recall from memory"},
            {"memory",                 "Show all memory"},
            {"history",                "Show input history"},
            {"clear",                  "Clear screen"},
        };
        for (String[] row : cmds) {
            appendStyled(String.format("  %-26s", row[0]), GREEN,  MONO_12);
            appendStyled(row[1] + "\n",                    WHITE,  MONO_12);
        }
        printDivider();
    }

    void showHistory() {
        if (history.isEmpty()) { printAI("No history yet."); return; }
        printDivider();
        appendStyled("  INPUT HISTORY\n", CYAN, MONO_B);
        for (int i=0;i<history.size();i++)
            appendStyled(String.format("  %3d  %s\n", i+1, history.get(i)), MUTED, MONO_12);
        printDivider();
    }

    void showMemory() {
        if (memory.isEmpty()) { printAI("Memory is empty."); return; }
        printDivider();
        appendStyled("  MEMORY\n", CYAN, MONO_B);
        memory.forEach((k,v) -> appendStyled("  " + k + " = " + v + "\n", AMBER, MONO_12));
        printDivider();
    }

    void suggest(String input) {
        String best = null; int min = Integer.MAX_VALUE;
        for (String cmd : commands.keySet()) {
            int d = levenshtein(input.toLowerCase(), cmd);
            if (d<min){min=d;best=cmd;}
        }
        if (min<=5) printError("🤔 Did you mean: '" + best + "'?");
        else        printError("🤔 Unknown command — type 'help' for a list.");
    }

    // ═════════════════════════════════════════════════════
    //  OUTPUT HELPERS
    // ═════════════════════════════════════════════════════

    void printUser(String msg) {
        appendStyled("You › ", CYAN,  MONO_B);
        appendStyled(msg + "\n", WHITE, MONO_13);
    }

    void printAI(String msg) {
        SwingUtilities.invokeLater(() -> {
            appendStyled("AI  › ", GREEN, MONO_B);
            appendStyled(msg + "\n", GREEN, MONO_13);
            scrollToBottom();
        });
    }

    void printSystem(String msg) {
        SwingUtilities.invokeLater(() -> {
            appendStyled("  " + msg + "\n", AMBER, MONO_B);
            scrollToBottom();
        });
    }

    void printError(String msg) {
        SwingUtilities.invokeLater(() -> {
            appendStyled("ERR › ", RED_C, MONO_B);
            appendStyled(msg + "\n", RED_C, MONO_13);
            scrollToBottom();
        });
    }

    void printDivider() {
        SwingUtilities.invokeLater(() -> {
            appendStyled("  " + "─".repeat(74) + "\n", BORDER_C, MONO_11);
            scrollToBottom();
        });
    }

    void clearOutput() {
        SwingUtilities.invokeLater(() -> {
            try { doc.remove(0, doc.getLength()); } catch (Exception ignored) {}
        });
    }

    void appendStyled(String text, Color color, Font font) {
        SwingUtilities.invokeLater(() -> {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            StyleConstants.setFontFamily(attrs, font.getFamily());
            StyleConstants.setFontSize(attrs, font.getSize());
            StyleConstants.setBold(attrs, font.isBold());
            try { doc.insertString(doc.getLength(), text, attrs); }
            catch (Exception ignored) {}
            scrollToBottom();
        });
    }

    void scrollToBottom() {
        SwingUtilities.invokeLater(() ->
            outputPane.setCaretPosition(doc.getLength()));
    }

    void setStatus(String s) {
        SwingUtilities.invokeLater(() -> statusLabel.setText("● " + s));
    }

    void updateMemLabel() {
        SwingUtilities.invokeLater(() -> memLabel.setText("MEM: " + memory.size()));
    }

    // ═════════════════════════════════════════════════════
    //  UTILITIES
    // ═════════════════════════════════════════════════════

    static int getNumber(String input) {
        try { return extractSingleNumber(input); }
        catch (Exception e) {
            if (memory.containsKey("last")) return memory.get("last");
            throw new RuntimeException("No number found");
        }
    }

    static int extractSingleNumber(String input) {
        Matcher m = Pattern.compile("\\d+").matcher(input);
        if (m.find()) return Integer.parseInt(m.group());
        throw new RuntimeException("No number found");
    }

    static int[] extractTwoNumbers(String input) {
        Matcher m = Pattern.compile("\\d+").matcher(input);
        int[] n = new int[2]; int i = 0;
        while (m.find() && i<2) n[i++] = Integer.parseInt(m.group());
        if (i<2) throw new RuntimeException("Two numbers required");
        return n;
    }

    static int levenshtein(String a, String b) {
        int[][] dp = new int[a.length()+1][b.length()+1];
        for (int i=0;i<=a.length();i++)
            for (int j=0;j<=b.length();j++) {
                if(i==0) dp[i][j]=j;
                else if(j==0) dp[i][j]=i;
                else dp[i][j]=Math.min(Math.min(dp[i-1][j]+1,dp[i][j-1]+1),
                        dp[i-1][j-1]+(a.charAt(i-1)==b.charAt(j-1)?0:1));
            }
        return dp[a.length()][b.length()];
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(MindShellSwing::new);
    }
}

// ═════════════════════════════════════════════════════════
//  HELPER CLASSES
// ═════════════════════════════════════════════════════════

class PrimeUtil {
    boolean isPrime(int n) {
        if (n<=1) return false;
        for (int i=2;i<=(int)Math.sqrt(n);i++) if(n%i==0) return false;
        return true;
    }
}

class CalcEngine {
    double calculate(String expression) {
        if (expression==null||expression.isEmpty())
            throw new IllegalArgumentException("Empty expression");
        return evaluate(expression.replaceAll("\\s+",""));
    }
    private double evaluate(String expr) {
        Stack<Double>    numbers = new Stack<>();
        Stack<Character> ops     = new Stack<>();
        for (int i=0;i<expr.length();i++) {
            char ch = expr.charAt(i);
            if (Character.isDigit(ch)||ch=='.') {
                StringBuilder sb = new StringBuilder();
                while(i<expr.length()&&(Character.isDigit(expr.charAt(i))||expr.charAt(i)=='.'))
                    sb.append(expr.charAt(i++));
                numbers.push(Double.parseDouble(sb.toString())); i--;
            } else if(ch=='(') { ops.push(ch);
            } else if(ch==')') {
                while(ops.peek()!='(') numbers.push(applyOp(ops.pop(),numbers.pop(),numbers.pop()));
                ops.pop();
            } else if(isOp(ch)) {
                while(!ops.isEmpty()&&prec(ops.peek())>=prec(ch))
                    numbers.push(applyOp(ops.pop(),numbers.pop(),numbers.pop()));
                ops.push(ch);
            }
        }
        while(!ops.isEmpty()) numbers.push(applyOp(ops.pop(),numbers.pop(),numbers.pop()));
        return numbers.pop();
    }
    private boolean isOp(char c){return c=='+'||c=='-'||c=='*'||c=='/'||c=='^';}
    private int prec(char o){return o=='+'||o=='-'?1:o=='*'||o=='/'?2:o=='^'?3:0;}
    private double applyOp(char op,double b,double a) {
        switch(op){
            case '+':return a+b; case '-':return a-b;
            case '*':return a*b;
            case '/':if(b==0)throw new ArithmeticException("Divide by zero");return a/b;
            case '^':return Math.pow(a,b);
        }
        throw new RuntimeException("Invalid operator");
    }
}