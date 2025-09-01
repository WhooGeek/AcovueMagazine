package com.AcovueMagazine.Magazine.Service;


import com.AcovueMagazine.Magazine.DTO.Magazine;
import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Repository.MagazineRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MagazineService {

    private final MagazineRepository magazineRepository;

    // 매거진 전체 조회
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

    // 매거진 상세 조회
    @Transactional
    public MagazineResDTO getMagazine(Long magazineId) {
        Magazine magazine = magazineRepository.findById(magazineId).orElseThrow(()-> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        return MagazineResDTO.fromEntity(magazine);
    }
}
