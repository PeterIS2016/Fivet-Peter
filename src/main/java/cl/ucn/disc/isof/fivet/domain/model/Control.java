package cl.ucn.disc.isof.fivet.domain.model;

import com.durrutia.ebean.BaseModel;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.DoubleSummaryStatistics;

/**
 * Created by Peter on 20-12-2016.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Control extends BaseModel{
    /**
     * fecha actual del control
     */
    @Getter
    @Setter
    @NotEmpty
    @Column
    private Date fecha;

    /**
     * proximo Control de la mascota
     */
    @Getter
    @Setter
    @NotEmpty
    private Date proximoControl;

    /**
     * temperatura de la mascota
     */
    @Getter
    @Setter
    @Column
    private Integer temperatura;

    /**
     * diagnostico de la mascota
     */
    @Getter
    @Setter
    private String diagnostico;

    /**
     * Peso de la mascota
     */
    @Getter
    @Setter
    private Double peso;

    /**
     * veterinario que trata a la mascota
     */
    @Getter
    @Column
    private Persona veterinario;
}
