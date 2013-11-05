package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractScriptedTripTest<CarRentalSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        //TODO: use updated manager interface to load cars into companies
        new Main("trips").run();
    }
    
    private void addCarTypes(String datafile) throws Exception {
        String companyName = getCompanyName(datafile);
        List<CarType> carTypes = loadCarTypes(datafile);
        Set<CarType> uniqueCarTypes = new HashSet<CarType>(carTypes);
        
        ManagerSessionRemote session = getNewManagerSession("CarAdder", companyName);
        for(CarType type : uniqueCarTypes) {
            session.addCarType(type);
        }
        for(CarType type : carTypes) {
            session.addCar(type.getName(), companyName);
        }
    }
    
    private String getCompanyName(String datafile) {
        String fileName = FileSystems.getDefault().getPath(datafile).getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
    
    private static List<CarType> loadCarTypes(String datafile)
            throws NumberFormatException, IOException {
        List<CarType> types = new LinkedList<CarType>();

        InputStreamReader streamReader = new InputStreamReader(Main.class.getClassLoader().getResourceAsStream(datafile));
        //open file from jar
        BufferedReader in = new BufferedReader(streamReader);
        //while next line exists
        while (in.ready()) {
            //read line
            String line = in.readLine();
            //if comment: skip
            if (line.startsWith("#")) {
                continue;
            }
            //tokenize on ,
            StringTokenizer csvReader = new StringTokenizer(line, ",");
            //create new car type from first 5 fields
            CarType type = new CarType(csvReader.nextToken(),
                    Integer.parseInt(csvReader.nextToken()),
                    Float.parseFloat(csvReader.nextToken()),
                    Double.parseDouble(csvReader.nextToken()),
                    Boolean.parseBoolean(csvReader.nextToken()));
            //create N new cars with given type, where N is the 5th field
            for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                types.add(type);
            }
        }

        return types;
    }
    
    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        CarRentalSessionRemote out = (CarRentalSessionRemote) new InitialContext().lookup(CarRentalSessionRemote.class.getName());
        out.setRenterName(name);
        return out;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        ManagerSessionRemote out = (ManagerSessionRemote) new InitialContext().lookup(ManagerSessionRemote.class.getName());
        return out;
    }
    
    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        System.out.println("Available car types between "+start+" and "+end+":");
        for(CarType ct : session.getAvailableCarTypes(start, end))
            System.out.println("\t"+ct.toString());
        System.out.println();
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String carRentalName) throws Exception {
        session.createQuote(carRentalName, new ReservationConstraints(start, end, carType));
    }

    @Override
    protected void confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        session.confirmQuotes();
    }
    
    @Override
    protected Set<Reservation> getReservationsBy(ManagerSessionRemote ms, String renterName) throws Exception {
        return ms.getReservationsBy(renterName);
    }

    @Override
    protected int getReservationsForCarType(ManagerSessionRemote ms, String name, String carType) throws Exception {
        return ms.getReservations(name, carType).size();
    }
}