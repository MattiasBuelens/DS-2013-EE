package session;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateful;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    private Set<Quote> currentQuotes = new HashSet<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }

    @Override
    public Set<String> getAvailableCarTypes(String carRentalName, Date start, Date end) {
        CarRentalCompany carRental = RentalStore.getRentals().get(carRentalName);
        Set<String> carTypeNames = new HashSet<String>();
        for (CarType carType : carRental.getAvailableCarTypes(start, end)) {
            carTypeNames.add(carType.getName());
        }
        return carTypeNames;
    }

    @Override
    public Quote createQuote(ReservationConstraints constraints, String client, String carRentalName) throws ReservationException {
        CarRentalCompany carRental = RentalStore.getRentals().get(carRentalName);
        Quote quote = carRental.createQuote(constraints, client);
        currentQuotes.add(quote);
        return quote;
    }

    @Override
    public Set<Quote> getCurrentQuotes() {
        return Collections.unmodifiableSet(currentQuotes);
    }

    @Override
    public void confirmQuotes() throws ReservationException {
        Set<Reservation> reservations = new HashSet<Reservation>();
        try {
            for (Quote quote : getCurrentQuotes()) {
                CarRentalCompany carRental = RentalStore.getRentals().get(quote.getRentalCompany());
                Reservation reservation = carRental.confirmQuote(quote);
                reservations.add(reservation);
            }
            currentQuotes.clear();
        } catch (ReservationException e) {
            // Roll back
            for (Reservation reservation : reservations) {
                CarRentalCompany carRental = RentalStore.getRentals().get(reservation.getRentalCompany());
                carRental.cancelReservation(reservation);
            }
            // Rethrow
            throw e;
        }
    }
}
