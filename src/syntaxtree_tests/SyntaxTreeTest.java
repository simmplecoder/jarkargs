package syntaxtree_tests;

import evaluator.Evaluator;
import evaluator.Lexer;
import evaluator.SyntaxErrorException;
import evaluator.SyntaxTree;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SyntaxTreeTest {

    @Test
    public void simpleTest()
    {
        Map<String, Double> testCases = new HashMap<>();
        testCases.put("1 + 2 - 7 * 0", 3.);
        testCases.put("1", 1.);
        testCases.put("(1 + 3) * 4", 16.);
        testCases.put("(24 / 6) + 6", 10.);

        for (Map.Entry<String, Double> test: testCases.entrySet())
        {
            Double result = Evaluator.evaluate(test.getKey());
            Assert.assertEquals("Incorrect evaluation result", result, test.getValue());
        }
    }

    @Test
    public void throwingTests()
    {
        String[] throwingTests = new String[]{
                "1 + 2 2 3 ",
                "(())) + 4",
                "123.123 ()",
                "123"
        };

        for (String test: throwingTests)
        {
            try {
                Evaluator.evaluate(test);
            }
            catch (SyntaxErrorException e)
            {
                continue;
            }
            Assert.fail("Parser didn't complain about syntax error");
        }
    }

    @Test
    public void variableTests()
    {
        String expression = "abc+cde-2*myvar+urvar";
        Lexer lexer = new Lexer(expression);
        Assert.assertEquals(lexer.toString(), expression);
        SyntaxTree tree = new SyntaxTree(lexer.tokenQueue());
        Assert.assertEquals(tree.toString(), expression);
        Assert.assertFalse(tree.isComputable());
        System.out.println(tree.variables().toString());

        Map<String, Double> values = new HashMap<>();
        values.put("abc", 12.);
        values.put("cde", 1.);
        values.put("myvar", 1.);
        //values.put("urvar", 1.);

        tree.substitute(values);
        System.out.println(tree.toString());

//        Assert.assertTrue(tree.isComputable());
//
//        System.out.println(tree.evaluate());
    }

    public static void main(String[] args)
    {
        String expression = "(1 + 2 -5 ) * 25";
        Lexer lexer = new Lexer(expression);
        SyntaxTree tree = new SyntaxTree(lexer.tokenQueue());
        System.out.println(tree.evaluate());
        System.out.println(Evaluator.evaluate(expression));
    }
}
