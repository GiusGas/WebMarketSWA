package it.univaq.swa.webmarket.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "NotFoundError", description = "Purchase Request Not Found Error")
public class NotFoundError {
    
    @Schema(description = "Error code", example = "404")
    private int status;
    
    @Schema(description = "Error message", example = "Purchase Request not found")
    private String message;

    public NotFoundError(int status, String message) {
        this.status = status;
        this.message = message;
    }
}