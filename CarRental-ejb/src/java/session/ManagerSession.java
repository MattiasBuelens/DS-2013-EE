package session;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;
import rental.ReservationException;

@Stateless
public class ManagerSession implements ManagerSessionRemote {

    private static final Logger logger = Logger.getLogger(ManagerSession.class.getName());
    @PersistenceContext
    EntityManager em;

    @Override
    public Set<CarType> getCarTypes(String companyName) {
        try {
            CarRentalCompany company = getCompany(companyName);
            return new HashSet<CarType>(company.getAllTypes());
        } catch (ReservationException ex) {
            logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            for (Car c : RentalStore.getRental(company).getCars(type)) {
                out.add(c.getId());
            }
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            return RentalStore.getRental(company).getCar(id).getReservations().size();
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            for (Car c : RentalStore.getRental(company).getCars(type)) {
                out.addAll(c.getReservations());
            }
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        Set<Reservation> out = new HashSet<Reservation>();
        for (CarRentalCompany crc : RentalStore.getRentals().values()) {
            out.addAll(crc.getReservationsBy(renter));
        }
        return out.size();
    }

    @Override
    public Set<String> getAllRentalCompanies() {
        List<String> names = em.createNamedQuery("findAllCompanyNames", String.class)
                .getResultList();
        return new HashSet<String>(names);
    }

    @Override
    public void addCompany(String companyName) {
        CarRentalCompany company = new CarRentalCompany(companyName, Collections.<Car>emptyList());
        em.persist(company);
    }

    @Override
    public void addCarType(CarType carType) {
        em.persist(carType);
    }

    @Override
    public void addCar(String carTypeName, String ownerCompanyName) {
        try {
            CarType carType = em.find(CarType.class, carTypeName);
            CarRentalCompany ownerCompany = getCompany(ownerCompanyName);
            Car car = new Car(0, carType);
            ownerCompany.addCar(car);
            em.persist(ownerCompany);
        } catch (ReservationException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    protected CarRentalCompany getCompany(String companyName) throws ReservationException {
        CarRentalCompany company = em.find(CarRentalCompany.class, companyName);
        if (company == null) {
            throw new ReservationException("Company doesn't exist!: " + company);
        }
        return company;
    }
}