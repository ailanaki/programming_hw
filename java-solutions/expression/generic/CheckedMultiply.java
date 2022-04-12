package expression.generic;

public class CheckedMultiply<T extends Number> extends CheckedBinary<T> {

    public CheckedMultiply(NewCommonExpression<T> one,NewCommonExpression<T> two) {
        super(one, two);
        sign = "*";
    }

    @Override
    protected  MyType<T> f(MyType<T> one, MyType<T> two) throws RuntimeException {
        return one.mul(two);
    }

}