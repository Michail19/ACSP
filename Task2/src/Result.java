import java.io.Serializable;

public class Result implements Serializable {
    private final Double x1;
    private final Double x2;
    private final String message;

    public Result(Double x1, Double x2, String message) {
        this.x1 = x1;
        this.x2 = x2;
        this.message = message;
    }

    public Double getX1() { return x1; }
    public Double getX2() { return x2; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        if (x1 == null && x2 == null)
            return message;
        else if (x1 != null && x2 == null)
            return "x = " + x1;
        else
            return "x1 = " + x1 + ", x2 = " + x2;
    }
}
