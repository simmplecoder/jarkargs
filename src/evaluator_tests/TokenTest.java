package evaluator_tests;
import evaluator.Token;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
            Token tk = new Token(test.getKey());
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
                Token tk = new Token(input);
            }
            catch (IllegalArgumentException ex)
            {
                return;
            }

            Assert.fail("Incorrect sequence was recognized as a token");
        }
    }
}
