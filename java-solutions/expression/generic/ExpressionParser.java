package expression.generic;

public class ExpressionParser<T extends Number> implements NewParser<T> {
    private String expression;
    private int i = 0;
    private MyType<T> type;

    @Override
    public NewCommonExpression<T> parse(String expression, MyType<T> type) throws OperationException {
        i = 0;
        this.type = type;
        this.expression = expression;
        whiteSpace();
        if (i >= expression.length()) {
            return null;
        }
        NewCommonExpression<T> exp = addSub();
        if (i < expression.length()) {
            if (check(')')) {
                StringBuilder m = new StringBuilder();
                if (expression.length() <= 50) {
                    m.append(expression);

                } else {
                    if (i - 25 > 0) {
                        m.append(expression.substring(i - 25, i + 25));
                    } else {
                        m.append(expression.substring(i, i + 50));
                    }
                }
                throw new OperationException("No opening parenthesis: " + m);
            }
            StringBuilder m = new StringBuilder();
            expWriter(m);
            throw new OperationException("Unexpected symbol. " + m);
        }
        return exp;
    }


    private NewCommonExpression<T> Unary() {
        NewCommonExpression exp;
        if (check('(')) {
            i++;
            whiteSpace();
            exp = addSub();
            whiteSpace();
            if (!check(')')) {
                StringBuilder m = new StringBuilder();
                if (expression.length() <= 50) {
                    m.append(expression);

                } else {
                    if (i - 25 > 0) {
                        m.append(expression.substring(i - 25, i + 25));
                    } else {
                        m.append(expression.substring(i, i + 50));
                    }
                }
                throw new OperationException("No closing parenthesis: " + m);
            }
            i++;
            whiteSpace();
            return exp;
        }
        if (check('-')) {
            i++;
            whiteSpace();
            if (checkDigit()) {
                StringBuilder sb = new StringBuilder();
                sb.append('-');
                while (checkDigit()) {
                    sb.append(expression.charAt(i++));
                }
                checkExceptionUnary();
                try {
                    return new Const<>(type.parseConst(sb.toString()));
                } catch (NumberFormatException e) {
                    throw new OperationException("Constant overflow: " + sb);
                }
            } else {
                return minus();
            }
        }
        if (checkDigit()) {
            StringBuilder sb = new StringBuilder();
            while (checkDigit()) {
                sb.append(expression.charAt(i++));
            }
            checkExceptionUnary();
            try {
                return new Const<>(type.parseConst(sb.toString()));
            } catch (NumberFormatException e) {
                throw new OperationException("Constant overflow: " + sb);
            }
        }
        if (check('x') || check('y') || check('z')) {
            StringBuilder sb = new StringBuilder();
            while (check('x') || check('y') || check('z')) {
                sb.append(expression.charAt(i++));
            }
            checkExceptionUnary();
            return new Variable<>(sb.toString());
        }
        checkExceptionUnary();
        return null;
    }


    private NewCommonExpression<T> mulDiv() {
        NewCommonExpression<T> exp;
        exp = Unary();
        checkExceptionOperation(exp);
        whiteSpace();
        while (check('*') || check('/')) {
            if (expression.charAt(i) == '*') {
                i++;
                whiteSpace();
                NewCommonExpression<T> x = Unary();
                checkExceptionSymbol(x);
                exp = new CheckedMultiply<>(exp, x);
            } else if (expression.charAt(i) == '/') {
                i++;
                whiteSpace();
                NewCommonExpression<T> x = Unary();
                checkExceptionSymbol(x);
                exp = new CheckedDivide<>(exp, x);
            }
            whiteSpace();
        }
        return exp;
    }

    private NewCommonExpression<T> addSub() {
        NewCommonExpression<T> exp;
        exp = mulDiv();
        checkExceptionOperation(exp);
        whiteSpace();
        while (check('+') || check('-')) {
            if (expression.charAt(i) == '+') {
                i++;
                whiteSpace();
                NewCommonExpression<T> x = mulDiv();
                checkExceptionSymbol(x);
                exp = new CheckedAdd<>(exp, x);
            } else if (expression.charAt(i) == '-') {
                i++;
                whiteSpace();
                NewCommonExpression<T> x = mulDiv();
                checkExceptionSymbol(x);
                exp = new CheckedSubtract<>(exp, x);
            }
            whiteSpace();

        }
        return exp;
    }


    private NewCommonExpression<T> minus() {
        whiteSpace();
        NewCommonExpression<T> x = Unary();
        i--;
        checkExceptionOperation(x);
        i++;
        return new CheckedNegate<>(x);
    }

    private void whiteSpace() {
        while (i < expression.length() && Character.isWhitespace(expression.charAt(i))) {
            i++;
        }
    }

    private boolean check(char c) {
        return i < expression.length() && expression.charAt(i) == c;
    }

    private boolean checkDigit() {
        return i < expression.length() && expression.charAt(i) <= '9' && expression.charAt(i) >= '0';
    }

    private boolean checkPowLog() {
        return i < expression.length() && (Character.isWhitespace(expression.charAt(i)) || expression.charAt(i) == '-' || (expression.charAt(i) <= 9
                && expression.charAt(i) >= '0') || expression.charAt(i) == '(');
    }

    private void checkExceptionUnary() {
        whiteSpace();
        if (i < expression.length()) {
            if (expression.charAt(i) != '+' && expression.charAt(i) != '-'
                    && expression.charAt(i) != '*' && expression.charAt(i) != '/' && expression.charAt(i) != ')') {
                StringBuilder m = new StringBuilder();
                expWriter(m);
                throw new OperationException("Unexpected symbol. " + m);
            }
        }
    }

    private void checkExceptionOperation(NewCommonExpression exp) {
        if (exp == null) {
            StringBuilder m = new StringBuilder();
            expWriter(m);
            throw new OperationException("Unexpected operator. " + m);
        }
    }

    private void checkExceptionSymbol(NewCommonExpression x) {
        if (x == null) {
            StringBuilder m = new StringBuilder();
            if (expression.length() <= 50) {
                m.append(expression);

            } else {
                if (i - 25 > 0) {
                    m.append(expression.substring(i - 25, i + 25));
                } else {
                    m.append(expression.substring(i, i + 50));
                }
            }
            throw new OperationException("No argument.  " + m);
        }
    }

    private void exp(StringBuilder m) {
        m.append("--------> ");
        while (i < expression.length() && !Character.isWhitespace(expression.charAt(i))) {
            m.append(expression.charAt(i));
            i++;
        }
        m.append(" <-------- ");

    }

    private void expWriter(StringBuilder m) {
        if (expression.length() <= 50) {
            m.append(expression.substring(0, i));
            exp(m);
            if (i < expression.length()) {
                m.append((expression.substring(i)));
            }

        } else {
            if (i - 25 > 0) {
                m.append(expression.substring(i - 25, i));
                exp(m);
                if (i < expression.length()) {
                    m.append(expression.substring(i, i + 25));
                }

            } else {
                m.append(expression.substring(0, i));
                exp(m);
                if (i < expression.length()) {
                    m.append(expression.substring(i, i + 50));
                }
            }
        }
    }
}

