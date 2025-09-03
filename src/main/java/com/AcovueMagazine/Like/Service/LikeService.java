package com.AcovueMagazine.Like.Service;

import com.AcovueMagazine.Like.DTO.MagazineLikeResDTO;
import com.AcovueMagazine.Like.Entity.MagazineLike;
import com.AcovueMagazine.Like.Respository.MagazineLikeRepository;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.Magazine.Repository.MagazineRepository;
import com.AcovueMagazine.User.Entity.Users;
import com.AcovueMagazine.User.Repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UsersRepository usersRepository;
    private final MagazineLikeRepository magazineLikeRepository;
    private final MagazineRepository magazineRepository;

    // 매거진 좋아요 등록
    @Transactional
    public MagazineLikeResDTO toggleMagazineLike(Long magazineSeq, Long userSeq) {

        Users user = usersRepository.findById(userSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다." + userSeq));

        Magazine magazine = magazineRepository.findById(magazineSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다." + magazineSeq));

        Optional<MagazineLike> existing = magazineLikeRepository.findByUserAndMagazine(user, magazine);

        MagazineLike like;
        if(existing.isPresent()){
            // 좋아요 취소
            magazineLikeRepository.delete(existing.get());
            like = null;
        }else{
            // 좋아요 등록
            like = magazineLikeRepository.save(MagazineLike.create(user, magazine));
        }

        return MagazineLikeResDTO.fromEntity(like);
    }

}
