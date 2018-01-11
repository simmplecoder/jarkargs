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
        Space,
        Alphabetic
    }

    private enum PartialTokenType {
        Variable,
        Number,
        NumberWithDot,
        None
    }

    private CharType classify(char c) {
        switch (c) {
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

        if (c >= '0' && c <= '9') {
            return CharType.Digit;
        }

        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') {
            return CharType.Alphabetic;
        }

        throw new LexicalErrorException("could not determine the char type");
    }

    private void flushNumber(StringBuilder numberBuffer) {
        final Pattern numberPattern = Pattern.compile("\\d*(\\.\\d+)*");

        String num = numberBuffer.toString();
        if (!num.isEmpty()) {
            if (!numberPattern.matcher(num).matches()) {
                throw new LexicalErrorException("wrong number input, it should abide pattern [0-9]*.[0-9]+");
            }
            numberBuffer.setLength(0);
            tokens.add(new Token(num, Token.TokenType.Number));
        }
    }

    private void flushVariable(StringBuilder variable) {
        final Pattern variableNamePattern = Pattern.compile("([a-z]|[A-Z])+");

        String variableName = variable.toString();
        if (!variableNamePattern.matcher(variableName).matches())
        {
            throw new LexicalErrorException("wrong variable name format");
        }
        tokens.add(new Token(variableName, Token.TokenType.Variable));
        variable.setLength(0);
    }

    private void flushCurrentLeaf(StringBuilder numberBuffer, StringBuilder variableBuffer,
                                  PartialTokenType currentType)
    {
        switch (currentType)
        {
            case Number:
            case NumberWithDot:
                flushNumber(numberBuffer);
                break;
            case Variable:
                flushVariable(variableBuffer);
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
        StringBuilder variable = new StringBuilder();
        PartialTokenType currentType = PartialTokenType.None;

        for (char c: expression.toCharArray())
        {
            switch (classify(c))
            {
                case Digit:
                    if (currentType == PartialTokenType.Variable)
                    {
                        flushVariable(variable);
                    }

                    if (currentType == PartialTokenType.None)
                    {
                        currentType = PartialTokenType.Number;
                    }
                    number.append(c);
                    break;
                case Dot:
                    if (currentType == PartialTokenType.Variable) {
                        flushVariable(variable);
                    }

                    number.append(c);
                    if (currentType == PartialTokenType.NumberWithDot)
                    {
                        throw new LexicalErrorException("double dots are not allowed");
                    }
                    currentType = PartialTokenType.NumberWithDot;
                    break;
                case Plus:
                    flushCurrentLeaf(number, variable, currentType);
                    currentType = PartialTokenType.None;
                    tokens.add(new Token("+", Token.TokenType.Plus));
                    break;
                case Minus:
                    flushCurrentLeaf(number, variable, currentType);
                    currentType = PartialTokenType.None;
                    tokens.add(new Token("-", Token.TokenType.Minus));
                    break;
                case Product:
                    flushCurrentLeaf(number, variable, currentType);
                    currentType = PartialTokenType.None;
                    tokens.add(new Token("*", Token.TokenType.Product));
                    break;
                case Division:
                    flushCurrentLeaf(number, variable, currentType);
                    currentType = PartialTokenType.None;
                    tokens.add(new Token("/", Token.TokenType.Division));
                    break;
                case OpeningBracket:
                    flushCurrentLeaf(number, variable, currentType);
                    currentType = PartialTokenType.None;
                    tokens.add(new Token("(", Token.TokenType.OpeningBracket));
                    break;
                case ClosingBracket:
                    flushCurrentLeaf(number, variable, currentType);
                    currentType = PartialTokenType.None;
                    tokens.add(new Token(")", Token.TokenType.ClosingBracket));
                    break;
                case Alphabetic:
                    if (!number.toString().isEmpty()) {
                        flushNumber(number);
                    }
                    variable.append(c);
                    currentType = PartialTokenType.Variable;
                    break;
                case Space:
                    flushCurrentLeaf(number, variable, currentType);
                    currentType = PartialTokenType.None;
                    break;
            }
        }

        //gonna cause syntax error anyway if both of them are not flushed
        //so flush in any order
        if (!variable.toString().isEmpty())
        {
            flushVariable(variable);
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

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (Token tk: tokens)
        {
            builder.append(tk.value);
        }

        return builder.toString();
    }
}
