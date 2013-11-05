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

    @PersistenceContext
    EntityManager em;

    @Override
    public Set<CarType> getCarTypes(String companyName) {
        try {
            CarRentalCompany company = getCompany(companyName);
            return new HashSet<CarType>(company.getAllTypes());
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCars(String companyName, String type) {
        try {
            CarRentalCompany company = getCompany(companyName);
            Set<Integer> out = new HashSet<Integer>();
            for (Car car : company.getCars(type)) {
                out.add(car.getId());
            }
            return out;
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Reservation> getReservations(String companyName, String type, int id) {
        try {
            CarRentalCompany company = getCompany(companyName);
            return company.getCar(id).getReservations();
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Reservation> getReservations(String companyName, String type) {
        try {
            CarRentalCompany company = getCompany(companyName);
            Set<Reservation> out = new HashSet<Reservation>();
            for (Car c : company.getCars(type)) {
                out.addAll(c.getReservations());
            }
            return out;
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Reservation> getReservationsBy(String renter) {
        List<CarRentalCompany> companies = em.createQuery(
                "SELECT c FROM CarRentalCompany c", CarRentalCompany.class)
                .getResultList();
        Set<Reservation> out = new HashSet<Reservation>();
        for (CarRentalCompany crc : companies) {
            out.addAll(crc.getReservationsBy(renter));
        }
        return out;
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
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
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