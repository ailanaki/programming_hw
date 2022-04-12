package expression.generic;

public class MyInt extends MyType<Integer> {


    public MyInt(Integer smt) {
        super(smt);
    }

    @Override
    MyType<Integer> wrap(Integer smt) {
        return new MyInt(smt);
    }

    @Override
    MyType<Integer> wrap(int smt) {
        return new MyInt(smt);
    }

    @Override
    Integer getParseConst(String s) {
        return Integer.parseInt(s);
    }

    @Override
    public Integer getAdd(Integer two) {
        int x = getValue();
        int y = two;
        if (x < 0 && y < 0 && (Integer.MIN_VALUE - x) > y) {
            throw new OperationException("Overflow: too small value (add)");
        } else if (x > 0 && y > 0 && (Integer.MAX_VALUE - x) < y) {
            throw new OperationException("Overflow: too large value (add)");
        }
        return x + y;

    }

    @Override
    public Integer getDiv(Integer two) {
        int x = getValue();
        int y = two;
        if (y == 0) {
            throw new DivisionByZero("Division by zero");
        }
        if (x == -2147483648 && y == -1) {
            throw new OperationException("Overflow: too large number");
        }
        return x / y;
    }

    @Override
    public Integer getMul(Integer two) {
        int x = getValue();
        int y = two;
        if (x != 0 && y != 0) {
            if (y > 0 && x > 0 && y > Integer.MAX_VALUE / x) {
                throw new OperationException("Overflow: too large value (multiply)");
            } else if (y < 0 && x < 0 && y < Integer.MAX_VALUE / x) {
                throw new OperationException("Overflow: too small value (multiply)");
            } else if (x > 0 && y < 0 && y < Integer.MIN_VALUE / x) {
                throw new OperationException("Overflow: too small value (multiply)");
            } else if (x < 0 && y > 0 && x < Integer.MIN_VALUE / y) {
                throw new OperationException("Overflow: too small value (multiply)");
            }
        }
        return x * y;
    }

    @Override
    public Integer getSub(Integer two) {
        int x = getValue();
        int y = two;
        if (x < 0 && y > 0 && (Integer.MIN_VALUE + y) > x) {
            throw new OperationException("Overflow: too small value (subtract)");
        } else if (x > 0 && y < 0 && (Integer.MAX_VALUE + y) < x) {
            throw new OperationException("Overflow: too small value (subtract)");
        }
        return x - y;
    }

    @Override
    Integer getNegate() {
        if (getValue() == -2147483648) {
            throw new OperationException("Overflow~ ");
        }
        return getValue()*(-1);
    }
}
