package expression.generic;

abstract public class MyType<T extends Number> {
    private T value;

    public MyType (T value){
        this.value = value;
    }

    public T getValue(){
        return value;
    }

    public MyType<T> add(MyType<T> two) {
        return wrap(getAdd(two.getValue()));
    }

    public MyType<T> div(MyType<T> two) {
        return wrap(getDiv(two.getValue()));
    }

    public MyType<T> mul(MyType<T> two) {
        return wrap(getMul(two.getValue()));
    }

    public MyType<T> sub(MyType<T> two) {
        return wrap(getSub(two.getValue()));
    }
    public MyType<T> negate(){
        return wrap(getNegate());
    }

    public MyType<T> parseConst(String s) {
        return wrap(getParseConst(s));
    }


    abstract MyType<T> wrap(T smt);

    abstract MyType<T> wrap(int smt);

    abstract T getParseConst(String s);

    abstract T getAdd(T two);

    abstract T getDiv(T two);

    abstract T getMul(T two);

    abstract T getSub(T two);

    abstract T getNegate();
}
