package expression.generic;

public class MyDouble extends MyType<Double> {

    public MyDouble(Double value){
        super(value);
    }

    @Override
    MyType<Double> wrap(Double smt) {
        return new MyDouble(smt);
    }

    @Override
    MyType<Double> wrap(int smt) {
        return wrap((double)smt);
    }

    @Override
    Double getParseConst(String s) {
        return Double.parseDouble(s);
    }

    @Override
    public Double getAdd(Double two) {
        return getValue() +  two;
    }

    @Override
    public Double getDiv(Double two) {
        return getValue() /  two;
    }

    @Override
    public Double getMul(Double two) {
        return getValue() * two;
    }

    @Override
    public Double getSub(Double two) {
        return getValue() - two;
    }

    @Override
    Double getNegate() {
        return getValue()*(-1);
    }
}
