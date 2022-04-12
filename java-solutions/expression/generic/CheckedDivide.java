package expression.generic;

public class CheckedDivide<T extends Number> extends CheckedBinary<T> {

    public CheckedDivide(NewCommonExpression<T> one, NewCommonExpression<T> two) {
        super(one, two);
        sign = "/";
    }

    @Override
    protected MyType<T> f(MyType<T> one, MyType<T> two) throws RuntimeException {
        return one.div(two);
    }
}

