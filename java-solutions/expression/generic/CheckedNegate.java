package expression.generic;

public class CheckedNegate<T extends Number> extends NewCommonExpression<T> {

    protected NewCommonExpression<T> one;

    public CheckedNegate(NewCommonExpression<T> one) {
        this.one = one;
    }

    @Override
    public MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z) {
        return one.evaluate(x, y, z).negate();
    }

    @Override
    public void fillString(StringBuilder sb) {
        sb.append("- (");
        one.fillString(sb);
        sb.append(")");
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        fillString(s);
        return s.toString();
    }


}
