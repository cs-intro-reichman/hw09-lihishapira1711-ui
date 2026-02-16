import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class LanguageModel {

    private int windowLength;
    private HashMap<String, List> CharDataMap; 
    private Random randomGenerator;

    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        this.randomGenerator = new Random(seed);
        this.CharDataMap = new HashMap<String, List>();
    }

    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        this.randomGenerator = new Random();
        this.CharDataMap = new HashMap<String, List>();
    }

    public void train(String fileName) {
        In in = new In(fileName);
        String window = "";
        for (int i = 0; i < windowLength; i++) {
            if (!in.isEmpty()) window += in.readChar();
        }
        while (!in.isEmpty()) {
            char c = in.readChar();
            List probs = CharDataMap.get(window);
            if (probs == null) {
                probs = new List();
                CharDataMap.put(window, probs);
            }
            probs.update(c);
            window = window.substring(1) + c;
        }
        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
    }

    public void calculateProbabilities(List probs) {
        int total = 0;
        for (int i = 0; i < probs.getSize(); i++) {
            total += probs.get(i).count;
        }
        double cumulative = 0.0;
        for (int i = 0; i < probs.getSize(); i++) {
            CharData cd = probs.get(i);
            cd.p = (double) cd.count / total;
            cumulative += cd.p;
            cd.cp = cumulative;
        }
        if (probs.getSize() > 0) {
            probs.get(probs.getSize() - 1).cp = 1.0;
        }
    }

    public char getRandomChar(List probs) {
        double r = randomGenerator.nextDouble();
        for (int i = 0; i < probs.getSize(); i++) {
            if (probs.get(i).cp > r) return probs.get(i).chr;
        }
        return probs.get(probs.getSize() - 1).chr;
    }

    public String generate(String initialText, int textLength) {
        if (initialText.length() < windowLength) return initialText;
        StringBuilder output = new StringBuilder(initialText);
        
        int totalTargetLength = textLength + initialText.length();
        while (output.length() < totalTargetLength) {
            String window = output.substring(output.length() - windowLength);
            List probs = CharDataMap.get(window);
            if (probs == null) break;
            output.append(getRandomChar(probs));
        }
        return output.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // מיון המפתחות של ה-HashMap כדי לעבור את טסט ההשוואה (Train)
        ArrayList<String> keys = new ArrayList<>(CharDataMap.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            sb.append(key).append(": ").append(CharDataMap.get(key).toString()).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int generatedLength = Integer.parseInt(args[2]);
        boolean isRandom = args[3].equals("random");
        String fileName = args[4];

        LanguageModel lm = isRandom ? new LanguageModel(windowLength) : new LanguageModel(windowLength, 20);
        lm.train(fileName);
        System.out.println(lm.generate(initialText, generatedLength));
    }
}