package evaluator;

public class Token {
    public enum TokenType
    {
        Variable,
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

    public Token(String term, TokenType t)
    {
        value = term;
        type = t;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
