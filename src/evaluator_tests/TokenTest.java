package evaluator_tests;
import evaluator.Lexer;
import evaluator.LexicalErrorException;
import evaluator.Token;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TokenTest {
    static private final Map<String, Token.TokenType> answeredInput = new HashMap<>();
    static {
        answeredInput.put("+", Token.TokenType.Plus);
        answeredInput.put("-", Token.TokenType.Minus);
        answeredInput.put("*", Token.TokenType.Product);
        answeredInput.put("/", Token.TokenType.Division);
        answeredInput.put("(", Token.TokenType.OpeningBracket);
        answeredInput.put(")", Token.TokenType.ClosingBracket);
        answeredInput.put("123", Token.TokenType.Number);
        answeredInput.put(".123", Token.TokenType.Number);
        answeredInput.put("123.123", Token.TokenType.Number);
    }

    @Test
    public void correctInputTests()
    {
        for (Map.Entry<String, Token.TokenType> test: answeredInput.entrySet())
        {
            Lexer lexer = new Lexer(test.getKey());
            Token tk = lexer.tokenQueue().peek();
            Assert.assertTrue("Token was not recognized correctly",
                                test.getValue() == tk.type && test.getKey().equals(tk.value));
        }
    }

    @Test
    public void wrongInputTests()
    {
        String[] wrongInputs = new String[]{
                "123asd",
                "123laskhdlajkshd",
                "123.123sssd",
                "123.asd123",
                ".asdasd",
                "asd.12",
                "+-123"
        };

        for (String input: wrongInputs)
        {
            try {
                new Lexer(input);
            }
            catch (LexicalErrorException ex)
            {
                return;
            }

            Assert.fail("Incorrect sequence was recognized as a token");
        }
    }

    @Test
    public void variableInputTests()
    {
        String input = "(abc + d) + 123 - 9";
        Lexer lexer = new Lexer(input);
        Assert.assertEquals("[(, abc, +, d, ), +, 123, -, 9]", Arrays.toString(lexer.tokenQueue().toArray()));
    }
}
