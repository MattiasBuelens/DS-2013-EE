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
        List<CarType> carTypes = em.createNamedQuery("findCarIdsInCompany", CarType.class)
                .setParameter("companyName", company)
                .getResultList();
        return new HashSet<CarType>(carTypes);
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        List<Integer> ids = em.createNamedQuery("findCarIdsInCompany", Integer.class)
                .setParameter("companyName", company)
                .setParameter("carTypeName", type)
                .getResultList();
        return new HashSet<Integer>(ids);
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        return em.createNamedQuery("findNbReservationsForCar", Integer.class)
                .setParameter("companyName", company)
                .setParameter("carTypeName", type)
                .setParameter("carId", id)
                .getSingleResult();
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        return em.createNamedQuery("findNbReservationsForCarType", Integer.class)
                .setParameter("companyName", company)
                .setParameter("carTypeName", type)
                .getSingleResult();
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        return em.createNamedQuery("findNbReservationsByRenter", Integer.class)
                .setParameter("renterName", renter)
                .getSingleResult();
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