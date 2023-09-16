package com.dyes.backend.domain.user.repository;

import com.dyes.backend.domain.user.entity.AddressBook;
import com.dyes.backend.domain.user.entity.AddressBookOption;
import com.dyes.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressBookRepository extends JpaRepository<AddressBook, String> {

    List<AddressBook> findAllByUser(User user);

    Optional<AddressBook> findByAddressBookOption(AddressBookOption defaultOption);
}
