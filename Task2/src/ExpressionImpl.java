import java.rmi.RemoteException;

public class ExpressionImpl implements Expression {

    @Override
    public void printExpr() throws RemoteException {
        System.out.println("Mathematical expression");
    }
}
