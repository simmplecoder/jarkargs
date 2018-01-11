package evaluator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.regex.Pattern;

public class Lexer {
    private ArrayList<Token> tokens = new ArrayList<>();

    private enum CharType {
        Plus,
        Minus,
        Product,
        Division,
        Digit,
        Dot,
        OpeningBracket,
        ClosingBracket,
        Space
    }

    private CharType classify(char c)
    {
        switch (c){
            case '+':
                return CharType.Plus;
            case '-':
                return CharType.Minus;
            case '*':
                return CharType.Product;
            case '/':
                return CharType.Division;
            case '.':
                return CharType.Dot;
            case '(':
                return CharType.OpeningBracket;
            case ')':
                return CharType.ClosingBracket;
            case ' ':
                return CharType.Space;
        }

        if (c >= '0' && c <= '9')
        {
            return CharType.Digit;
        }

        throw new LexicalErrorException("could not determine the char type");
    }

    private void flushNumber(StringBuilder numberBuffer)
    {
        final Pattern numberPattern = Pattern.compile("\\d*(\\.\\d+)*");

        String num = numberBuffer.toString();
        if (!num.isEmpty())
        {
            if (!numberPattern.matcher(num).matches())
            {
                throw new LexicalErrorException("wrong number input, it should abide pattern [0-9]*.[0-9]+");
            }
            numberBuffer.setLength(0);
            tokens.add(new Token(num, Token.TokenType.Number));
        }
    }

    public Lexer(String expression)
    {
//        String[] terms = expression.split("\\s+");
//        for (String term: terms)
//        {
//            tokens.add(new Token(term));
//        }
        StringBuilder number = new StringBuilder();

        for (char c: expression.toCharArray())
        {
            switch (classify(c))
            {
                case Digit:
                    number.append(c);
                    break;
                case Dot:
                    number.append(c);
                    break;
                case Plus:
                    flushNumber(number);
                    tokens.add(new Token("+", Token.TokenType.Plus));
                    break;
                case Minus:
                    flushNumber(number);
                    tokens.add(new Token("-", Token.TokenType.Minus));
                    break;
                case Product:
                    flushNumber(number);
                    tokens.add(new Token("*", Token.TokenType.Product));
                    break;
                case Division:
                    flushNumber(number);
                    tokens.add(new Token("/", Token.TokenType.Division));
                    break;
                case OpeningBracket:
                    flushNumber(number);
                    tokens.add(new Token("(", Token.TokenType.OpeningBracket));
                    break;
                case ClosingBracket:
                    flushNumber(number);
                    tokens.add(new Token(")", Token.TokenType.ClosingBracket));
                    break;
                case Space:
                    break;
            }
        }

        if (!number.toString().isEmpty())
        {
            flushNumber(number);
        }
    }

    public Queue<Token> tokenQueue()
    {
        return new ArrayDeque<>(tokens);
    }
}
