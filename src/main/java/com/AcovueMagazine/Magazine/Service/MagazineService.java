package com.AcovueMagazine.Magazine.Service;


import com.AcovueMagazine.Magazine.DTO.Magazine;
import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Repository.MagazineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MagazineService {

    private final MagazineRepository magazineRepository;

    @Transactional
    public List<MagazineResDTO> getAllMagazines() {
        List<Magazine> magazines = magazineRepository.findAll();

        return magazines.stream()
                .map(magazine -> new MagazineResDTO(
                        magazine.getUser_seq(),       // 엔티티 필드에 맞춰서
                        magazine.getMagazine_seq(),
                        magazine.getMagazine_title(),
                        magazine.getMagazine_content()
                ))
                .collect(Collectors.toList());
    }
}
