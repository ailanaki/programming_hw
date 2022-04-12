package expression.generic;

public class MyShort extends MyType<Short> {

    public MyShort(short value){
        super(value);
    }

    @Override
    MyType<Short> wrap(Short smt) {
        return new MyShort(smt);
    }

    @Override
    MyType<Short> wrap(int smt) {
        return new MyShort((short)smt);
    }

    @Override
    Short getParseConst(String s) {
        return Short.parseShort(s);
    }

    @Override
    public Short getAdd(Short two) {
        return (short) (getValue() +  two);
    }

    @Override
    public Short getDiv(Short two) {
        return (short) (getValue() /  two);
    }

    @Override
    public Short getMul(Short two) {
        return (short)(getValue() * two);
    }

    @Override
    public Short getSub(Short two) {
        return (short)(getValue() - two);
    }

    @Override
    Short getNegate() {
        return (short)(getValue()*(-1));
    }
}
