package expression.generic;

import java.math.BigInteger;

public class MyBigInteger extends MyType<BigInteger> {

    public MyBigInteger(BigInteger value){
        super(value);
    }

    @Override
    MyType <BigInteger> wrap(BigInteger smt) {
        return new MyBigInteger(smt);
    }

    @Override
    MyType<BigInteger> wrap(int smt) {
        return wrap(new BigInteger(String.valueOf(smt)));
    }

    @Override
    BigInteger getParseConst(String s) {
        return new BigInteger(s);
    }

    @Override
    public BigInteger getAdd(BigInteger two) {
        return getValue().add(two);
    }

    @Override
    public BigInteger getDiv(BigInteger two) {
        return getValue().divide(two);
    }

    @Override
    public BigInteger getMul(BigInteger two) {
        return getValue().multiply(two);
    }

    @Override
    public BigInteger getSub(BigInteger two) {
        return getValue().subtract(two);
    }

    @Override
    BigInteger getNegate() {
        return getValue().negate();
    }
}
