package expression.generic;

import java.math.BigInteger;


import java.math.BigInteger;

public class GenericTabulator implements Tabulator {

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        switch (mode){
            case "i":
                return tabulate(new MyInt(0), expression, x1, x2, y1, y2, z1, z2);
            case "d":
                return tabulate(new MyDouble(0.),expression,x1,x2,y1,y2,z1,z2);
            case "bi":
                return tabulate(new MyBigInteger(BigInteger.ZERO),expression,x1,x2,y1,y2,z1,z2);
            case "s":
                return tabulate(new MyShort((short)0),expression,x1,x2,y1,y2,z1,z2);
            case "l":
                return tabulate(new MyLong(0),expression,x1,x2,y1,y2,z1,z2);
        }
        return null;
    }

    private <T extends Number> Object[][][] tabulate(MyType<T> mode , String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        ExpressionParser<T> parser = new ExpressionParser<>();
        Object [][][] result = new Object[x2 - x1 + 2][y2 - y1 + 2][z2 - z1 + 2];
        NewCommonExpression<T> res = parser.parse(expression, mode);
        for (int i = 0; i < x2 - x1 + 1; i++) {
            for (int j = 0; j < y2 - y1 + 1; j++) {
                for (int k = 0; k < z2 - z1 + 1; k++) {
                    try {
                        result[i][j][k] = res.evaluate(mode.wrap(x1 + i), mode.wrap(y1 + j), mode.wrap(z1 + k)).getValue();
                    } catch (RuntimeException e) {
                        result[i][j][k] = null;

                    }
                }
            }
        }
        return result;
    }


}
