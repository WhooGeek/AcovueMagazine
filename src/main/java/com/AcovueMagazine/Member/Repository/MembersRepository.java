package com.AcovueMagazine.Member.Repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.AcovueMagazine.Member.Entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members,Long> {

    Optional<Members> findByMemberEmail(String username);
}
