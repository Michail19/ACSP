import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Expression extends Remote {
    void printExpr() throws RemoteException;
}
