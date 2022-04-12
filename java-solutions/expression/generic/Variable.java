package expression.generic;

public class Variable<T extends Number> extends NewCommonExpression<T> {
    private String str;

    public Variable(String str) {
        this.str = str;
    }

    @Override
    public MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z) {
        if (str.equals("x")) {
            return x;
        } else if (str.equals("y")) {
            return y;
        } else {
            return z;
        }

    }

    @Override
    public String toString() {
        return str;
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
        Variable new_obj = (Variable) obj;
        return str.equals(new_obj.str);

    }


    @Override
    public void fillString(StringBuilder sb) {
        sb.append(toString());
    }
}
