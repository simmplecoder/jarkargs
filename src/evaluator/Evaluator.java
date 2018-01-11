package evaluator;


public class Evaluator {
    public static double evaluate(String expression)
    {
        Lexer lexer = new Lexer(expression);
        SyntaxTree tree = new SyntaxTree(lexer.tokenQueue());
        return tree.evaluate();
    }
}
