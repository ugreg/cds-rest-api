import common.MicrosoftDynamicsDao;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args)  {

        MicrosoftDynamicsDao microsoftDynamicsDao = MicrosoftDynamicsDao.getInstance();

        try {
            microsoftDynamicsDao.getEntityAuditHistory();
        }
        catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
        }
        catch (InterruptedException e) {
            System.out.println("InterruptedException");
        }
        catch (ExecutionException e) {
            System.out.println("ExecutionException, have you provided the correct client id and secret pair?");
        }
    }
}
