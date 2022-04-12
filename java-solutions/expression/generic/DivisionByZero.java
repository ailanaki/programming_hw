package expression.generic;

public class DivisionByZero extends RuntimeException {
    public DivisionByZero(String message) {
        super(message);
    }
}

