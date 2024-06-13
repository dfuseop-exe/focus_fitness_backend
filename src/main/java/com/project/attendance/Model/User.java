package com.project.attendance.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;

    private String firstName ;
    private String lastName ;
    private String mobile_no ;
    private String email ;
    private String password ;
    private Integer age ;
    private LocalDate joining_LocalDate ;
    private Integer duration ;
    private LocalDate end_LocalDate ;
    private String height ;
    private Float weight ;
    private String shift ;

    @ManyToOne
    private Batch enrolledBatch ;


    /*    Security    */
    @Getter
    @ManyToMany(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    @JoinTable(name = "user_role")
    private List<Role> roles = new ArrayList<>() ;

    // Setter for trainees
    @Setter
    @ManyToMany
    @JoinTable(
            name = "user_personal_training",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    @JsonManagedReference // Use this annotation to indicate the owning side of the relationship
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // Include only if non-empty
    private List<User> trainees = new ArrayList<>();

    @ManyToMany(mappedBy = "trainees")
    @JsonBackReference // Use this annotation to indicate the inverse side of the relationship
    private List<User> trainers = new ArrayList<>();

    // Other fields, getters, setters...

    // Custom method to check if the user is a staff user
    public boolean isStaffUser() {
        // Assuming you have a method to get roles or authorities
        return roles.stream().anyMatch(role -> role.getRoleName().equals("ROLE_STAFF"));
    }

    // Getter for trainees, annotated with @JsonInclude to conditionally serialize
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<User> getTrainees() {
        // Only include trainees if the user is a staff user
        if (isStaffUser()) {
            return trainees;
        } else {
            return Collections.emptyList(); // Return empty list for non-staff users
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = roles.stream().map((role -> new SimpleGrantedAuthority(role.getRoleName()))).collect(Collectors.toList()) ;
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
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
