import java.rmi.RemoteException;

public class ExpressionImpl implements Expression {

    @Override
    public Result solve(double a, double b, double c) throws RemoteException {
        if (a == 0) {
            if (b == 0) {
                return new Result(null, null, "No answers");
            } else {
                double x = -c / b;
                return new Result(x, null, "Success");
            }
        }

        double d = b * b - 4 * a * c;
        if (d < 0) {
            return new Result(null, null, "No roots");
        } else if (d == 0) {
            double x = -b / (2 * a);
            return new Result(x, null, "One root");
        } else {
            double sqrtD = Math.sqrt(d);
            double x1 = (-b + sqrtD) / (2 * a);
            double x2 = (-b - sqrtD) / (2 * a);
            return new Result(x1, x2, "Two roots");
        }
    }

}
