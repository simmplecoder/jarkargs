package syntaxtree_tests;

import evaluator.Lexer;
import evaluator.SyntaxTree;

public class SyntaxTreeTest {

    public static void main(String[] args)
    {
        Lexer lexer = new Lexer("( 1 + 2 - 5 ) * 25");
        SyntaxTree tree = new SyntaxTree(lexer.tokenQueue());
        System.out.println(tree.evaluate());
    }
}
