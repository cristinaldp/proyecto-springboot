package com.musica.musica_api.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "cancion_usuario")
@Getter
@Setter
@NoArgsConstructor

public class CancionUsuario {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_cancion")
    private Integer idCancion;

    @Column(name = "fecha_vista")
    private LocalDateTime fechaVista;
    
    public CancionUsuario(Integer idUsuario, Integer idCancion) {
        this.idUsuario = idUsuario;
        this.idCancion = idCancion;
        this.fechaVista = LocalDateTime.now();
    }

}
