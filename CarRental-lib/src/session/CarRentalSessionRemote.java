package session;

import java.util.Date;
import java.util.Set;
import javax.ejb.Remote;
import rental.Quote;
import rental.ReservationConstraints;
import rental.ReservationException;

@Remote
public interface CarRentalSessionRemote {

    Set<String> getAllRentalCompanies();

    Set<String> getAvailableCarTypes(String carRentalName, Date start, Date end);

    Quote createQuote(ReservationConstraints constraints, String client, String carRentalName) throws ReservationException;

    Set<Quote> getCurrentQuotes();

    void confirmQuotes() throws ReservationException;
}
