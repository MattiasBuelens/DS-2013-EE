package client;

import java.util.Date;
import java.util.Set;
import javax.naming.InitialContext;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractScriptedSimpleTripTest<CarRentalSessionRemote, ManagerSessionRemote> {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String scriptFile = args.length > 0 ? args[0] : "simpleTrips";
        new Main(scriptFile).run();
    }
    
    public Main(String scriptFile) {
        super(scriptFile);
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        InitialContext context = new InitialContext();
        return (CarRentalSessionRemote) context.lookup(CarRentalSessionRemote.class.getName());
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        InitialContext context = new InitialContext();
        return (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        System.out.println("Available car types from " + start + " to " + end + ":");
        for (String carRentalName : session.getAllRentalCompanies()) {
            Set<String> availableCarTypes = session.getAvailableCarTypes(carRentalName, start, end);
            System.out.println(" * " + carRentalName + ": " + availableCarTypes);
        }
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String carRentalName) throws Exception {
        ReservationConstraints constraints = new ReservationConstraints(start, end, carType);
        session.createQuote(constraints, name, carRentalName);
    }

    @Override
    protected void confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getNbClientReservations(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNbCompanyCarTypeReservations(carRentalName, carType);
    }
}
