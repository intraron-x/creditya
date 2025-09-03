package com.intraron.r2dbc.entity;

import com.intraron.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import java.util.Collection;
import java.util.stream.Collectors;

// intraron: Esta clase es la entidad de persistencia. Se usa para interactuar
// con la base de datos y, como está en la capa de infraestructura,
// puede implementar la interfaz de Spring Security.

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("userdata")
public class UserEntity implements UserDetails {

    @Id
    private String id;
    @Column("NOMBRES") // Mapea 'nombres' a 'NOMBRES'
    private String nombres;
    @Column("APELLIDOS") // Mapea 'apellidos' a 'APELLIDOS'
    private String apellidos;
    @Column("FECHA_NACIMIENTO")
    private LocalDate fechaNacimiento; // Se usa LocalDate para la fecha
    @Column("DIRECCION") // Mapea 'direccion' a 'DIRECCION'
    private String direccion;
    @Column("TELEFONO") // Mapea 'telefono' a 'TELEFONO'
    private String telefono;
    @Column("CORREO_ELECTRONICO") // Mapea 'correoElectronico' a 'CORREO_ELECTRONICO'
    private String correoElectronico;
    @Column("SALARIO_BASE") // Mapea 'salarioBase' a 'SALARIO_BASE'
    private Double salarioBase;
    @Column("PASSWORD")
    private String password; // intraron: Nuevo campo para la contraseña
    @Column("ROLES")
    private List<String> roles;

    // intraron: Mapeo de la entidad de persistencia al modelo de dominio.
    // Ahora convertimos List<String> a Set<String>.
    public User toUser() {
        return User.builder()
                .id(this.id)
                .nombres(this.nombres)
                .apellidos(this.apellidos)
                .telefono(this.telefono)
                .direccion(this.direccion)
                .correoElectronico(this.correoElectronico)
                .password(this.password)
                .salarioBase(this.salarioBase)
                .roles(this.roles != null ?
                        Set.copyOf(this.roles) :
                        Set.of())
                .build();
    }

    // intraron: La implementación de UserDetails ahora usa el List de roles.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.roles == null || this.roles.isEmpty()) {
            return Set.of();
        }
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.correoElectronico;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
