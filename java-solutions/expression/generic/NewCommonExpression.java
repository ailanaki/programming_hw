package expression.generic;

public abstract class NewCommonExpression <T extends Number> implements  MyExpression<T> {

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public abstract void fillString(StringBuilder sb);
}
