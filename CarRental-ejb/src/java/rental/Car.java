package rental;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Car {
    
    private int id;
    private CarType type;
    private Set<Reservation> reservations;

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }
    
    protected Car() {
        // do nothing
    }

    /******
     * ID *
     ******/
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
    	return id;
    }

    protected void setId(int id) {
        this.id = id;
    }
    
    /************
     * CAR TYPE *
     ************/
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    public CarType getType() {
        return type;
    }

    protected void setType(CarType type) {
        this.type = type;
    }

    /****************
     * RESERVATIONS *
     ****************/

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }

    @OneToMany(cascade = CascadeType.ALL)
    public Set<Reservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

}