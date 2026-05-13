package cl.duoc.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Este modelo permite manejar las excepciones")
@NoArgsConstructor
@Data
public class ExceptionResponse {
    @Schema(description = "Es el tipo de excepción", name = "type", requiredMode = Schema.RequiredMode.REQUIRED, example = "Técnico")
    private String type;
    @Schema(description = "Es el tipo de excepción", name = "title", requiredMode = Schema.RequiredMode.REQUIRED, example = "Error inesperado")
    private String title;
    @Schema(description = "Es el tipo de excepción", name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "500")
    private String code;
    @Schema(description = "Es el tipo de excepción", name = "detail", requiredMode = Schema.RequiredMode.REQUIRED, example = "Error inesperado")
    private String detail;
    @Schema(description = "Es el tipo de excepción", name = "instance", requiredMode = Schema.RequiredMode.REQUIRED, example = "Error inesperado")
    private String instance;

    public ExceptionResponse(String type, String title, String code, String detail, String instance) {
        this.type = type;
        this.title = title;
        this.code = code;
        this.detail = detail;
        this.instance = instance;
    }

}
