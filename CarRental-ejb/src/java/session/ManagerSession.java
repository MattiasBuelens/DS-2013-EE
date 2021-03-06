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
import rental.ReservationException;

@Stateless
public class ManagerSession implements ManagerSessionRemote {

    private static final Logger logger = Logger.getLogger(ManagerSession.class.getName());
    @PersistenceContext
    EntityManager em;

    @Override
    public Set<CarType> getCarTypes(String company) {
        List<CarType> carTypes = em.createNamedQuery("CarRentalCompany.carTypes", CarType.class)
                .setParameter("companyName", company)
                .getResultList();
        return new HashSet<CarType>(carTypes);
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        List<Integer> ids = em.createNamedQuery("CarRentalCompany.carIds", Integer.class)
                .setParameter("companyName", company)
                .setParameter("carTypeName", type)
                .getResultList();
        return new HashSet<Integer>(ids);
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        long count = em.createNamedQuery("Reservation.countByCar", Long.class)
                .setParameter("companyName", company)
                .setParameter("carTypeName", type)
                .setParameter("carId", id)
                .getSingleResult();
        return (int) count;
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        long count = em.createNamedQuery("Reservation.countByCarType", Long.class)
                .setParameter("companyName", company)
                .setParameter("carTypeName", type)
                .getSingleResult();
        return (int) count;
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        long count = em.createNamedQuery("Reservation.countByRenter", Long.class)
                .setParameter("renterName", renter)
                .getSingleResult();
        return (int) count;
    }

    @Override
    public Set<String> getAllRentalCompanies() {
        List<String> names = em.createNamedQuery("CarRentalCompany.all", String.class)
                .getResultList();
        return new HashSet<String>(names);
    }

    @Override
    public void addCompany(String companyName) {
        CarRentalCompany company = new CarRentalCompany(companyName, Collections.<Car>emptyList());
        em.persist(company);
    }

    @Override
    public void addCar(CarType carType, String ownerCompanyName) {
        try {
            CarRentalCompany ownerCompany = getCompany(ownerCompanyName);
            if (ownerCompany.hasType(carType.getName())) {
                // Already exists, use managed car type
                carType = ownerCompany.getType(carType.getName());
            } else {
                // New car type
                ownerCompany.addCarType(carType);
            }
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

    @Override
    public String getMostPopularCarRentalCompany() {
        return em.createNamedQuery("Reservation.companyWithMost", String.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    @Override
    public CarType getMostPopularCarTypeIn(String company) {
        return em.createNamedQuery("Reservation.carTypeWithMost", CarType.class)
                .setParameter("companyName", company)
                .setMaxResults(1)
                .getSingleResult();
    }
}