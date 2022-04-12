package expression.generic;

public class CheckedAdd<T extends Number> extends CheckedBinary<T>{

    public CheckedAdd(NewCommonExpression<T> one, NewCommonExpression<T> two) {
        super(one, two);
        sign = "+";
    }

    @Override
    protected  MyType<T> f(MyType<T> one, MyType<T> two) throws RuntimeException {
        return one.add(two);
    }

}