package evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    public enum TokenType
    {
        Number,
        OpeningBracket,
        ClosingBracket,
        Plus,
        Minus,
        Product,
        Division
    }

    public TokenType type;
    public String value;

    static private final Map<TokenType, Pattern> patternMap = new HashMap<>();
    static {
        patternMap.put(TokenType.Number, Pattern.compile("\\d*(\\.\\d+)*"));
        patternMap.put(TokenType.Plus, Pattern.compile("\\+"));
        patternMap.put(TokenType.Minus, Pattern.compile("-"));
        patternMap.put(TokenType.Product, Pattern.compile("\\*"));
        patternMap.put(TokenType.Division, Pattern.compile("/"));
        patternMap.put(TokenType.OpeningBracket, Pattern.compile("\\("));
        patternMap.put(TokenType.ClosingBracket, Pattern.compile("\\)"));
    }

    public Token(String term)
    {
        value = term;
        for (Map.Entry<TokenType, Pattern> entry: patternMap.entrySet())
        {
            if (entry.getValue().matcher(term).matches())
            {
                type = entry.getKey();
                return;
            }
        }

        throw new LexicalErrorException("The term doesn't match any of the possible token types");
    }

    public Token(String term, TokenType t)
    {
        value = term;
        type = t;
    }
}
