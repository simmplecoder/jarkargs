package evaluator;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class SyntaxTree {
    private class Node
    {
        Token token;
        Node left;
        Node right;

        public Node(Token tk)
        {
            token = tk;
            left = null;
            right = null;
        }

        public Node(Token tk, Node lhs, Node rhs)
        {
            token = tk;
            left = lhs;
            right = rhs;
        }

        public double evaluate()
        {
            switch (token.type)
            {
                case Number:
                    return Double.parseDouble(token.value);
                case Plus:
                    return left.evaluate() + right.evaluate();
                case Minus:
                    return left.evaluate() - right.evaluate();
                case Product:
                    return left.evaluate() * right.evaluate();
                case Division:
                    return left.evaluate() / right.evaluate();
            }

            throw new RuntimeException("Uncovered token type found");
        }
    }

    private Node root;

    private static final Map<Token.TokenType, Integer> inversePrecedence = new HashMap<>();
    static {
        inversePrecedence.put(Token.TokenType.Plus, 1);
        inversePrecedence.put(Token.TokenType.Minus, 1);
        inversePrecedence.put(Token.TokenType.Product, 2);
        inversePrecedence.put(Token.TokenType.Division, 2);
        inversePrecedence.put(Token.TokenType.OpeningBracket, 0);
    }

    public SyntaxTree(Queue<Token> tokenQueue)
    {
        Stack<Token> prevOps = new Stack<>();
        Stack<Node> prevExpressions = new Stack<>();

        while (!tokenQueue.isEmpty())
        {
            Token currentToken = tokenQueue.remove();
            switch (currentToken.type)
            {
                case Plus:
                case Minus:
                case Product:
                case Division:
                    sweepUntilHigherPrecedence(prevExpressions, prevOps, currentToken);
                    prevOps.push(currentToken);
                    break;
                case Number:
                    prevExpressions.push(new Node(currentToken));
                    break;
                case OpeningBracket:
                    prevOps.push(currentToken);
                    break;
                case ClosingBracket:
                    while (prevOps.peek().type != Token.TokenType.OpeningBracket
                            && !prevOps.isEmpty())
                    {
                        Token operator = prevOps.pop();
                        if (prevExpressions.size() < 2)
                        {
                            throw new SyntaxErrorException("invalid number of numbers and/or operators");
                        }
                        Node rhs = prevExpressions.pop();
                        Node lhs = prevExpressions.pop();

                        prevExpressions.push(new Node(operator, lhs, rhs));
                    }

                    if (prevOps.peek().type != Token.TokenType.OpeningBracket)
                    {
                        throw new IllegalArgumentException("Unequal amount of opening and closing brackets");
                    }

                    prevOps.pop();
                    break;
            }
        }

        sweepAll(prevExpressions, prevOps);
        if (prevExpressions.size() != 1)
        {
            throw new SyntaxErrorException("illegal combination of operators and numbers");
        }

        root = prevExpressions.pop();
    }

    private Node buildExpression(Stack<Node> prevExpressions, Stack<Token> prevOps)
    {
        Token operator = prevOps.pop();

        if (prevExpressions.size() < 2)
        {
            throw new SyntaxErrorException("invalid number of numbers and/or operators");
        }

        Node rhs = prevExpressions.pop();
        Node lhs = prevExpressions.pop();

        return new Node(operator, lhs, rhs);
    }

    private void sweepUntilHigherPrecedence(Stack<Node> prevExpressions, Stack<Token> prevOps, Token currentToken)
    {
        while (!prevOps.isEmpty()
                && inversePrecedence.get(prevOps.peek().type) >= inversePrecedence.get(currentToken.type))
        {
            prevExpressions.push(buildExpression(prevExpressions, prevOps));
        }
    }

    private void sweepAll(Stack<Node> prevExpressions, Stack<Token> prevOps)
    {
        while (!prevOps.isEmpty())
        {
            prevExpressions.push(buildExpression(prevExpressions, prevOps));
        }
    }

    public double evaluate()
    {
        return root.evaluate();
    }
}
