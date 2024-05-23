package am.itspace.photoshootprojectmanagementcommon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "event_category")
public class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Event category name is mandatory")
    @Size(max = 20, message = "Event category name must be at most 20 characters long")
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    private String description;

    @NotBlank(message = "Starting price is mandatory")
    @Min(value = 0, message = "The value must be positive")
    @Max(value = 20000, message = "Max price for the event should not be greater than 10.000")
    @Pattern(regexp = "[\\s]*[0-9]*[1-9]+", message = "msg")
    @Positive
    private double startingPrice;

}
