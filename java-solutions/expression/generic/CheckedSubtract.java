package expression.generic;

public class CheckedSubtract<T extends Number> extends CheckedBinary<T> {

    public CheckedSubtract(NewCommonExpression<T> one, NewCommonExpression<T> two) {
        super(one, two);
        sign = "-";
    }

    @Override
    protected MyType<T> f(MyType<T> one, MyType<T> two) throws RuntimeException {
        return one.sub(two);
    }


}
