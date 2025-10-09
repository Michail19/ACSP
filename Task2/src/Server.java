import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public Server() {}

    public static void main(String[] args) {
        try {
            ExpressionImpl obj = new ExpressionImpl();
            Expression stub = (Expression) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();

            registry.bind("Expression", stub);
            System.out.println("Server ready!");
        }
        catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }
}
