package expression.generic;

public class MyLong extends MyType<Long> {

    public MyLong(long value){
        super(value);
    }

    @Override
    MyType<Long> wrap(Long smt) {
        return new MyLong(smt);
    }

    @Override
    MyType<Long> wrap(int smt) {
        return wrap((long) smt);
    }

    @Override
    Long getParseConst(String s) {
        return Long.parseLong(s);
    }

    @Override
    public Long getAdd(Long two) {
        return getValue() +  two;
    }

    @Override
    public Long getDiv(Long two) {
        return getValue() /  two;
    }

    @Override
    public Long getMul(Long two) {
        return getValue() * two;
    }

    @Override
    public Long getSub(Long two) {
        return getValue() - two;
    }

    @Override
    Long getNegate() {
        return getValue()*(-1);
    }
}
