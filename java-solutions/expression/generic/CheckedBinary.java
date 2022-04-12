package expression.generic;

abstract public class CheckedBinary<T extends Number> extends NewCommonExpression<T>{
    protected NewCommonExpression<T> one, two;
    protected String sign;

    @Override
    public MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z) {
        return f(one.evaluate(x,y,z),two.evaluate(x,y,z));
    }

    public CheckedBinary(NewCommonExpression<T> one, NewCommonExpression<T> two) {
        this.one = one;
        this.two = two;
    }

    protected abstract  MyType<T> f(MyType<T> one, MyType<T> two) throws RuntimeException;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        fillString(s);
        return s.toString();
    }

    public void fillString(StringBuilder sb) {
        sb.append("(");
        one.fillString(sb);
        sb.append(" ").append(sign).append(" ");
        two.fillString(sb);
        sb.append(")");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CheckedBinary new_obj = (CheckedBinary) obj;
        return one.equals(new_obj.one) && two.equals(new_obj.two);
    }
}
