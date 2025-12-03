package com.AcovueMagazine.AboutMe.Repository;

import com.AcovueMagazine.AboutMe.Entity.AboutMe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AboutMeRepository extends JpaRepository <AboutMe,Long> {

    AboutMe findByaboutMeSeq(int i);

}
