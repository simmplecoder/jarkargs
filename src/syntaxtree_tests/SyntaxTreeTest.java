package syntaxtree_tests;

import evaluator.Evaluator;
import evaluator.Lexer;
import evaluator.SyntaxTree;

public class SyntaxTreeTest {

    public static void main(String[] args)
    {
        String expression = "(1 + 2 -5 ) * 25";
        Lexer lexer = new Lexer(expression);
        SyntaxTree tree = new SyntaxTree(lexer.tokenQueue());
        System.out.println(tree.evaluate());
        System.out.println(Evaluator.evaluate(expression));
    }
}
