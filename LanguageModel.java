
import java.util.TreeMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    TreeMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new TreeMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new TreeMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) { 
    String window = "";
    char c;
    In in = new In(fileName);
    for (int i = 0; i < windowLength; i++) {
        if (!in.isEmpty()) {
            window += in.readChar();
        }
    }
    while (!in.isEmpty()) {
        c = in.readChar();
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

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	void calculateProbabilities(List probs) {
    if (probs.getSize() == 0) return;
    int totalCount = 0;
    for (int i = 0; i < probs.getSize(); i++) {
        totalCount += probs.get(i).count;
    }
    double cumulativeProb = 0;
    for (int i = 0; i < probs.getSize(); i++) {
        CharData cd = probs.get(i);
        cd.p = (double) cd.count / totalCount;
        cumulativeProb += cd.p;
        cd.cp = cumulativeProb;
    }
    probs.get(probs.getSize() - 1).cp = 1.0;
}

    // Returns a random character from the given probabilities list.
	char getRandomChar(List probs) {
    double r = randomGenerator.nextDouble();
    for (int i = 0; i < probs.getSize(); i++) {
        if (probs.get(i).cp > r) {
            return probs.get(i).chr;
        }
    }
    return probs.get(probs.getSize() - 1).chr;
  }

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		if (initialText.length() < windowLength) {
        return initialText;
    }
    StringBuilder generatedText = new StringBuilder(initialText);
    while (generatedText.length() < textLength) {
        String currentWindow = generatedText.substring(generatedText.length() - windowLength);
        List probs = CharDataMap.get(currentWindow);
        
        if (probs != null) {
            char nextChar = getRandomChar(probs);
            generatedText.append(nextChar);
        } else {
            break;
        }
    }
    return generatedText.toString();
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
      StringBuilder str = new StringBuilder();
    for (String key : CharDataMap.keySet()) {
        List keyProbs = CharDataMap.get(key);
        str.append(key).append(" : ").append(keyProbs).append("\n");
    }
    return str.toString();
	}

    public static void main(String[] args) {
	int windowLength = 7;
    LanguageModel lm = new LanguageModel(windowLength, 20); 
    lm.train("originofspecies.txt") ;
    System.out.println(lm.generate("Natural", 100));
    }
}
