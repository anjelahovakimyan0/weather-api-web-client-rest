package am.itspace.photoshootprojectmanagementcommon.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Nullable
    @Size(max = 500, message = "Description must be at most 500 characters long")
    private String description;

    @NotBlank(message = "Address is mandatory")
    @Pattern(regexp = "^[a-zA-Z0-9\\s,.'-]+$", message = "Invalid address format")
    private String address;

    @NotNull(message = "This field is mandatory")
    @Range(min = 1, max = 1000, message = "Participants must be between 1 and 1000")
    private int participants;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date bookingDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "Event start date and time should be in present or in the future")
    private Date eventStartTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "Event end date and time should be in present or in the future")
    private Date eventEndTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @NotNull(message = "Event category must be specified")
    private EventCategory eventCategory;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name="booking_discount",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id"))
    private List<Discount> discounts;

    @ManyToMany
    @JoinTable(name="booking_specialty",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id"))
    private List<Specialty> specialties;

}
