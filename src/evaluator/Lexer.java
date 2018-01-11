package evaluator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.stream.Stream;

public class Lexer {
    private ArrayList<Token> tokens = new ArrayList<>();

    public Lexer(String expression)
    {
        String[] terms = expression.split("\\s+");
        for (String term: terms)
        {
            tokens.add(new Token(term));
        }
    }

    public Queue<Token> tokenQueue()
    {
        return new ArrayDeque<>(tokens);
    }
}
