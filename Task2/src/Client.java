import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    private Client() {}

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(null);
            Expression stub = (Expression) registry.lookup("Expression");
            stub.printExpr();
        }
        catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }
}
