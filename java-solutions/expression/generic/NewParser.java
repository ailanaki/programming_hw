package expression.generic;

public interface NewParser <T extends Number> {
    NewCommonExpression<T> parse(String expression,MyType<T> type) throws OperationException;
}

