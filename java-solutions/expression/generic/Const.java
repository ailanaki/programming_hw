package expression.generic;

public class Const<T extends Number> extends NewCommonExpression<T> {
    private MyType<T> value;
    private boolean ui = false;


    public Const(MyType<T> value) {
        this.value = value;
    }

    @Override
    public MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z) {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
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
        Const new_obj = (Const) obj;
        return value.equals(new_obj.value);

    }

    @Override
    public void fillString(StringBuilder sb) {
        sb.append(toString());
    }
}