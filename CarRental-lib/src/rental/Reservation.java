package rental;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@IdClass(Reservation.Key.class)
@NamedQueries({
    @NamedQuery(
            name = "Reservation.countByCar",
            query = "SELECT COUNT(r) FROM Reservation r "
            + "WHERE r.rentalCompany = :companyName "
            + "AND r.carType = :carTypeName "
            + "AND r.carId = :carId"),
    @NamedQuery(
            name = "Reservation.countByCarType",
            query = "SELECT COUNT(r) FROM Reservation r "
            + "WHERE r.rentalCompany = :companyName "
            + "AND r.carType = :carTypeName"),
    @NamedQuery(
            name = "Reservation.countByRenter",
            query = "SELECT COUNT(r) FROM Reservation r "
            + "WHERE r.carRenter = :renterName"),
    @NamedQuery(
            name = "Reservation.companyWithMost",
            query = "SELECT r.rentalCompany FROM Reservation r "
            + "GROUP BY r.rentalCompany "
            + "ORDER BY COUNT(r) DESC"),
    @NamedQuery(
            name = "Reservation.carTypeWithMost",
            query = "SELECT c.type FROM Car c, IN(c.reservations) AS r "
            + "WHERE r.rentalCompany = :companyName "
            + "GROUP BY c.type "
            + "ORDER BY COUNT(r) DESC")
})
public class Reservation extends Quote {

    private int carId;

    /*
     * CONSTRUCTOR
     */
    protected Reservation() {
        // do nothing
    }

    public Reservation(Quote quote, int carId) {
        super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(),
                quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.carId = carId;
    }

    /*
     * ID
     */
    @Id
    public int getCarId() {
        return carId;
    }

    protected void setCarId(int carId) {
        this.carId = carId;
    }

    @Id
    @Temporal(TemporalType.DATE)
    @Override
    public Date getStartDate() {
        return super.getStartDate();
    }

    /*
     * TO STRING
     */
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f",
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }

    /*
     * KEY
     */
    public static class Key implements Serializable {

        private int carId;
        private Date startDate;

        public Key(int carId, Date startDate) {
            this.carId = carId;
            this.startDate = startDate;
        }

        protected Key() {
        }

        public int getCarId() {
            return carId;
        }

        protected void setCarId(int carId) {
            this.carId = carId;
        }

        public Date getStartDate() {
            return startDate;
        }

        protected void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + this.carId;
            hash = 89 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Key other = (Key) obj;
            if (this.carId != other.carId) {
                return false;
            }
            if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
                return false;
            }
            return true;
        }
    }
}