package com.dyes.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AddressBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AddressBookOption addressBookOption;

    private String receiver;

    private String contactNumber;

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateAddressBookOption(AddressBookOption addressBookOption) {
        this.addressBookOption = addressBookOption;
    }
}
