package com.syncura360.dto.Service;
import lombok.Data;
import java.util.List;

/**
 * DTO for representing a request to delete services, containing a list of service names.
 *
 * @author Benjamin Leiby
 */
@Data
public class DeleteServicesDTO {
    private List<String> names;
}
