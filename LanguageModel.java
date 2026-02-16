import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

// The map of this model.
// Maps windows to lists of charachter data objects.
HashMap<String, List> CharDataMap;

// The window length used in this model.
int windowLength;

// The random number generator used by this model.
private Random randomGenerator;

/** Constructs a language model with the given window length and a given
* seed value. Generating texts from this model multiple times with the
* same seed value will produce the same random texts. Good for debugging. */
public LanguageModel(int windowLength, int seed) {
this.windowLength = windowLength;
randomGenerator = new Random(seed);
CharDataMap = new HashMap<String, List>();
}

/** Constructs a language model with the given window length.
* Generating texts from this model multiple times will produce
* different random texts. Good for production. */
public LanguageModel(int windowLength) {
this.windowLength = windowLength;
randomGenerator = new Random();
CharDataMap = new HashMap<String, List>();
}


// Computes and sets the probabilities (p and cp fields) of all the
// characters in the given list. */
void calculateProbabilities(List probs) {
    int totalChars = 0;
    for (int i = 0; i < probs.getSize(); i++) {
        totalChars += probs.get(i).count;
    }
    double cumulativeProb = 0.0;
    for (int i = 0; i < probs.getSize(); i++) {
        CharData current = probs.get(i);
        current.p = (double) current.count / totalChars;
        cumulativeProb += current.p;
        current.cp = cumulativeProb;
    }
    if (probs.getSize() > 0) {
        probs.get(probs.getSize() - 1).cp = 1.0;
    }
}

// Returns a random character from the given probabilities list.
char getRandomChar(List probs) {
double r = randomGenerator.nextDouble();
for (int i = 0; i < probs.getSize(); i++) {
if (r < probs.get(i).cp) {
return probs.get(i).chr;
}
}
return probs.get(probs.getSize() - 1).chr;
}

/**
* Generates a random text, based on the probabilities that were learned during training.
* @param initialText - text to start with. If initialText's last substring of size numberOfLetters
* doesn't appear as a key in Map, we generate no text and return only the initial text.
* @param textLength - the size of text to generate
* @return the generated text
*/
public String generate(String initialText, int textLength) {
if (windowLength > initialText.length()) {
return initialText;
}
String window = initialText.substring(initialText.length() - windowLength);
String generatedText = initialText;
while (generatedText.length() < initialText.length() + textLength) {
List probs = CharDataMap.get(window);
if (probs == null) {
return generatedText;
}
char c = getRandomChar(probs);
generatedText += c;
window = generatedText.substring(generatedText.length() - windowLength);
}
return generatedText;
}

public void train(String fileName) {
    In in = new In(fileName);
    String window = "";
    while (window.length() < windowLength && !in.isEmpty()) {
        char c = in.readChar();
        if (c != '\r') window += c;
    }
    while (!in.isEmpty()) {
        char c = in.readChar();
        if (c == '\r') continue; 
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

/** Returns a string representing the map of this language model. */
public String toString() {
StringBuilder str = new StringBuilder();
for (String key : CharDataMap.keySet()) {
List keyProbs = CharDataMap.get(key);
str.append(key + " : " + keyProbs + "\n");
}
return str.toString();
}
public static void main(String[] args) {
int windowLength = Integer.parseInt(args[0]);
String initialText = args[1];
int generatedTextLength = Integer.parseInt(args[2]);
boolean randomGeneration = args[3].equals("random");
String fileName = args[4];
LanguageModel lm;
if (randomGeneration) {
lm = new LanguageModel(windowLength);
} else {
lm = new LanguageModel(windowLength, 20);
}
lm.train(fileName);
System.out.println(lm.generate(initialText, generatedTextLength));
}
}