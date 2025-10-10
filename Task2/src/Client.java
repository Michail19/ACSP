import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    private Client() {}

    public static void main(String[] args) {
        try {
            System.out.println();
            
            Registry registry = LocateRegistry.getRegistry(null);
            Expression stub = (Expression) registry.lookup("Expression");
            double a = 1, b = -3, c = 2;

            Result result = stub.solve(a, b, c);
            System.out.println("Solve: " + result);
            System.out.println("Comment: " + result.getMessage());
            System.out.println();

            a = 1;
            b = 2;
            c = 1;
            result = stub.solve(a, b, c);
            System.out.println("Solve: " + result);
            System.out.println("Comment: " + result.getMessage());
            System.out.println();

            a = 3;
            b = 2;
            c = 1;
            result = stub.solve(a, b, c);
            System.out.println("Solve: " + result);
            System.out.println("Comment: " + result.getMessage());
            System.out.println();
        }
        catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }
}
