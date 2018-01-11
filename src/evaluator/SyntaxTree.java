package evaluator;

import java.util.*;

public class SyntaxTree {
    enum Computable{
        Yes,
        No
    }

    private class Node
    {
        Token token;
        Node left;
        Node right;
        int depth;
        Computable computable;


        Node(Token tk)
        {
            token = tk;
            depth = 1;
            if (tk.type == Token.TokenType.Variable)
            {
                computable = Computable.No;
            }
            else
            {
                computable = Computable.Yes;
            }
        }

        Node(Token tk, Node lhs, Node rhs)
        {
            token = tk;
            left = lhs;
            right = rhs;
            depth = Math.max(left.depth, right.depth) + 1;

            if (left.computable == Computable.No || right.computable == Computable.No)
            {
                computable = Computable.No;
            }
            else
            {
                computable = Computable.Yes;
            }
        }

        double evaluate()
        {
            if (left != null && right != null
                    && (left.token.type == Token.TokenType.Variable
                        || right.token.type == Token.TokenType.Variable))
            {
                throw new SyntaxErrorException("Cannot evaluate, at least one variable is not substituted yet");
            }

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
    private Set<String> variablesNames = new HashSet<>();

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
                case Variable:
                    variablesNames.add(currentToken.value);
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

                    if (prevOps.isEmpty())
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

    private Node findLeftmost()
    {
        Node current = root;
        while (current.left != null)
        {
            current = current.left;
        }
        return current;
    }

    public Set<String> variables()
    {
        return variablesNames;
    }

    public boolean isComputable()
    {
        return root.computable == Computable.Yes;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        if (root != null) {
            printSubtree(root, result);
        }

        return result.toString();
    }

    private void printSubtree(Node root, StringBuilder builder)
    {
        if (root.left != null)
        {
            printSubtree(root.left, builder);
        }

        builder.append(root.token.value);

        if (root.right != null)
        {
            printSubtree(root.right, builder);
        }
    }

    public void substitute(Map<String, Double> values)
    {
        substituteSubtree(root, values);
        recheckSubtree(root);
    }

    private void substituteSubtree(Node root, Map<String, Double> values)
    {
        Double value = values.get(root.token.value);
        if (value != null)
        {
            root.token.type = Token.TokenType.Number;
            root.token.value = value.toString();
            root.computable = Computable.Yes;
        }

        if (root.left != null)
        {
            substituteSubtree(root.left, values);
        }

        if (root.right != null)
        {
            substituteSubtree(root.right, values);
        }
    }

    private void recheckSubtree(Node root)
    {
        if (root.left == null)
        {
            return;
        }

        recheckSubtree(root.left);
        recheckSubtree(root.right);

        if (root.left.computable == Computable.No || root.right.computable == Computable.No)
        {
            root.computable = Computable.No;
        }
        else
        {
            root.computable = Computable.Yes;
        }
    }
}
