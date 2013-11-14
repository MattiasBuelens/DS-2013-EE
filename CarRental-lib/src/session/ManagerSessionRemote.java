package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {

    public Set<CarType> getCarTypes(String company);

    public Set<Integer> getCarIds(String company, String type);

    public int getNumberOfReservations(String company, String type, int carId);

    public int getNumberOfReservations(String company, String type);

    public int getNumberOfReservationsBy(String renter);

    public Set<String> getAllRentalCompanies();

    public void addCompany(String company);

    public void addCar(CarType carType, String ownerCompany);

    public String getMostPopularCarRentalCompany();

    public CarType getMostPopularCarTypeIn(String company);
}