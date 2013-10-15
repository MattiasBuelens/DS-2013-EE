/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.Collection;
import javax.ejb.Stateless;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;

/**
 *
 * @author r0256483
 */
@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @Override
    public Collection<CarType> getCompanyTypes(String carRentalName) {
        CarRentalCompany carRental = RentalStore.getRentals().get(carRentalName);
        return carRental.getAllTypes();
    }

    @Override
    public int getNbCompanyCarTypeReservations(String carRentalName, String carTypeName) {
        CarRentalCompany carRental = RentalStore.getRentals().get(carRentalName);
        return carRental.getNbCarTypeReservations(carTypeName);
    }

    @Override
    public int getNbClientReservations(String clientName) {
        int nbReservations = 0;
        for (CarRentalCompany carRental : RentalStore.getRentals().values()) {
            nbReservations += carRental.getNbClientReservations(clientName);
        }
        return nbReservations;
    }
}
