package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();
    @PersistenceContext
    EntityManager em;
    @Resource
    private EJBContext context;

    @Override
    public Set<String> getAllRentalCompanies() {
        List<String> names = em.createNamedQuery("CarRentalCompany.allNames", String.class)
                .getResultList();
        return new HashSet<String>(names);
    }

    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List<CarRentalCompany> companies = em.createNamedQuery("CarRentalCompany.all", CarRentalCompany.class)
                .getResultList();

        List<CarType> availableCarTypes = new LinkedList<CarType>();
        for (CarRentalCompany company : companies) {
            for (CarType ct : company.getAvailableCarTypes(start, end)) {
                if (!availableCarTypes.contains(ct)) {
                    availableCarTypes.add(ct);
                }
            }
        }
        return availableCarTypes;
    }

    @Override
    public Quote createQuote(String companyName, ReservationConstraints constraints) throws ReservationException {
        CarRentalCompany company = getCompany(companyName);
        Quote out = company.createQuote(constraints, renter);
        quotes.add(out);
        return out;
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @TransactionAttribute(REQUIRED)
    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
                CarRentalCompany company = getCompany(quote.getRentalCompany());
                done.add(company.confirmQuote(quote));
                em.persist(company);
            }
        } catch (ReservationException e) {
            context.setRollbackOnly();
            throw e;
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    protected CarRentalCompany getCompany(String companyName) throws ReservationException {
        CarRentalCompany company = em.find(CarRentalCompany.class, companyName);
        if (company == null) {
            throw new ReservationException("Company doesn't exist!: " + company);
        }
        return company;
    }
}