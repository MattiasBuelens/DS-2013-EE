/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.Collection;
import javax.ejb.Remote;
import rental.CarType;

/**
 *
 * @author r0256483
 */
@Remote
public interface ManagerSessionRemote {

    Collection<CarType> getCompanyTypes(String carRentalName);

    int getNbCompanyCarTypeReservations(String carRentalName, String carTypeName);

    int getNbClientReservations(String clientName);
}
