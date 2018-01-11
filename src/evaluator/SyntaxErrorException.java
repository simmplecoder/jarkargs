package evaluator;

public class SyntaxErrorException extends IllegalArgumentException {
    public SyntaxErrorException(String message)
    {
        super(message);
    }
}
