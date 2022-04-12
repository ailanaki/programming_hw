package expression.generic;

public interface MyExpression <T extends Number> {
    MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z);
}
